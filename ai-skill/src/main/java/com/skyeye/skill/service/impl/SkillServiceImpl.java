/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.skill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skyeye.skill.dao.SkillDao;
import com.skyeye.skill.dao.SkillExecDao;
import com.skyeye.skill.entity.Skill;
import com.skyeye.skill.entity.SkillExec;
import com.skyeye.skill.exception.CustomException;
import com.skyeye.skill.generator.BigScreenLlmGenerator;
import com.skyeye.skill.generator.BigScreenTemplateGenerator;
import com.skyeye.skill.llm.LlmChatService;
import com.skyeye.skill.service.SkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @ClassName: SkillServiceImpl
 * @Description: AI技能服务类
 * @author: skyeye云系列
 * @date: 2026/08/16
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class SkillServiceImpl implements SkillService {

    private static final String LOCAL_USER = "local";
    private static final int ENABLED = 1;
    private static final String CODE_BIGSCREEN = "skyeye-bigscreen";

    @Autowired
    private SkillDao skillDao;

    @Autowired
    private SkillExecDao skillExecDao;

    @Autowired
    private BigScreenTemplateGenerator bigScreenTemplateGenerator;

    @Autowired
    private BigScreenLlmGenerator bigScreenLlmGenerator;

    @Autowired
    private LlmChatService llmChatService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Skill saveOrUpdate(Skill skill) {
        if (!StringUtils.hasText(skill.getCode())) {
            throw new CustomException("技能编码 code 不能为空");
        }
        if (!StringUtils.hasText(skill.getName())) {
            throw new CustomException("技能名称 name 不能为空");
        }
        if (skill.getEnabled() == null) {
            skill.setEnabled(ENABLED);
        }
        checkCodeUnique(skill);
        String now = now();
        if (!StringUtils.hasText(skill.getId())) {
            skill.setId(UUID.randomUUID().toString().replace("-", ""));
            skill.setCreateId(LOCAL_USER);
            skill.setCreateTime(now);
            skill.setLastUpdateId(LOCAL_USER);
            skill.setLastUpdateTime(now);
            skillDao.insert(skill);
        } else {
            Skill old = skillDao.selectById(skill.getId());
            if (old == null) {
                throw new CustomException("技能不存在: " + skill.getId());
            }
            skill.setCreateId(old.getCreateId());
            skill.setCreateTime(old.getCreateTime());
            skill.setLastUpdateId(LOCAL_USER);
            skill.setLastUpdateTime(now);
            skillDao.updateById(skill);
        }
        return skillDao.selectById(skill.getId());
    }

    @Override
    public Skill selectById(String id) {
        Skill skill = skillDao.selectById(id);
        if (skill == null) {
            throw new CustomException("技能不存在: " + id);
        }
        return skill;
    }

    @Override
    public List<Skill> queryList() {
        return skillDao.selectList(new LambdaQueryWrapper<Skill>().orderByDesc(Skill::getCreateTime));
    }

    @Override
    public List<Skill> queryPageList(int page, int limit, String keyword) {
        Page<Skill> mpPage = new Page<>(Math.max(page, 1), Math.max(limit, 1));
        return skillDao.selectPage(mpPage, buildQuery(keyword)).getRecords();
    }

    @Override
    public long count(String keyword) {
        return skillDao.selectCount(buildQuery(keyword));
    }

    @Override
    public void deleteById(String id) {
        if (skillDao.deleteById(id) <= 0) {
            throw new CustomException("技能不存在: " + id);
        }
    }

    @Override
    public SkillExec executeSkill(String skillCode, String userInput) {
        if (!StringUtils.hasText(skillCode)) {
            throw new CustomException("skillCode不能为空");
        }
        if (!StringUtils.hasText(userInput)) {
            throw new CustomException("userInput不能为空，请用一句话描述大屏需求");
        }
        Skill skill = skillDao.selectOne(new LambdaQueryWrapper<Skill>().eq(Skill::getCode, skillCode));
        if (skill == null) {
            throw new CustomException("未找到技能: " + skillCode);
        }
        if (skill.getEnabled() == null || skill.getEnabled() != ENABLED) {
            throw new CustomException("技能未启用: " + skillCode);
        }
        if (!CODE_BIGSCREEN.equals(skill.getCode())) {
            throw new CustomException("暂只支持执行 skyeye-bigscreen，当前技能: " + skillCode);
        }

        Map<String, Object> screen = generateScreen(skill, userInput);
        String screenJson;
        try {
            screenJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(screen);
        } catch (JsonProcessingException e) {
            throw new CustomException("生成大屏JSON失败");
        }
        SkillExec exec = new SkillExec();
        exec.setId(UUID.randomUUID().toString().replace("-", ""));
        exec.setSkillId(skill.getId());
        exec.setSkillCode(skill.getCode());
        exec.setSkillName(skill.getName());
        exec.setUserInput(userInput);
        exec.setScreenJson(screenJson);
        exec.setStatus(1);
        exec.setCreateId(LOCAL_USER);
        exec.setCreateTime(now());
        skillExecDao.insert(exec);
        exec.setScreen(screen);
        return exec;
    }

    private Map<String, Object> generateScreen(Skill skill, String userInput) {
        if (!llmChatService.isEnabled()) {
            return markTemplate(bigScreenTemplateGenerator.generate(userInput), "skill.llm.enabled=false，使用模板");
        }
        if (!llmChatService.hasApiKey()) {
            if (!llmChatService.isFallbackToTemplate()) {
                throw new CustomException(llmChatService.missingKeyHint());
            }
            return markTemplate(bigScreenTemplateGenerator.generate(userInput), llmChatService.missingKeyHint() + "，已回退模板");
        }
        try {
            return bigScreenLlmGenerator.generate(userInput, skill.getInstruction());
        } catch (Exception e) {
            if (!llmChatService.isFallbackToTemplate()) {
                throw e instanceof CustomException ? (CustomException) e : new CustomException(e.getMessage());
            }
            return markTemplate(bigScreenTemplateGenerator.generate(userInput),
                "大模型失败已回退模板: " + e.getMessage());
        }
    }

    private Map<String, Object> markTemplate(Map<String, Object> screen, String remark) {
        screen.put("source", "template");
        screen.put("remark", remark);
        return screen;
    }

    @Override
    public SkillExec selectExecById(String id) {
        SkillExec exec = skillExecDao.selectById(id);
        if (exec == null) {
            throw new CustomException("执行记录不存在: " + id);
        }
        fillScreen(exec);
        return exec;
    }

    @Override
    public List<SkillExec> queryExecPageList(int page, int limit, String skillCode) {
        Page<SkillExec> mpPage = new Page<>(Math.max(page, 1), Math.max(limit, 1));
        List<SkillExec> list = skillExecDao.selectPage(mpPage, buildExecQuery(skillCode)).getRecords();
        for (SkillExec exec : list) {
            fillScreen(exec);
        }
        return list;
    }

    @Override
    public long countExec(String skillCode) {
        return skillExecDao.selectCount(buildExecQuery(skillCode));
    }

    private LambdaQueryWrapper<SkillExec> buildExecQuery(String skillCode) {
        LambdaQueryWrapper<SkillExec> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(skillCode)) {
            queryWrapper.eq(SkillExec::getSkillCode, skillCode);
        }
        queryWrapper.orderByDesc(SkillExec::getCreateTime);
        return queryWrapper;
    }

    private void fillScreen(SkillExec exec) {
        if (exec == null || !StringUtils.hasText(exec.getScreenJson())) {
            return;
        }
        try {
            exec.setScreen(objectMapper.readValue(exec.getScreenJson(), Object.class));
        } catch (Exception e) {
            exec.setScreen(exec.getScreenJson());
        }
    }

    private void checkCodeUnique(Skill skill) {
        LambdaQueryWrapper<Skill> queryWrapper = new LambdaQueryWrapper<Skill>().eq(Skill::getCode, skill.getCode());
        if (StringUtils.hasText(skill.getId())) {
            queryWrapper.ne(Skill::getId, skill.getId());
        }
        if (skillDao.selectCount(queryWrapper) > 0) {
            throw new CustomException("技能编码已存在: " + skill.getCode());
        }
    }

    private LambdaQueryWrapper<Skill> buildQuery(String keyword) {
        LambdaQueryWrapper<Skill> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(wrapper -> wrapper
                .like(Skill::getCode, keyword)
                .or()
                .like(Skill::getName, keyword)
                .or()
                .like(Skill::getDescription, keyword));
        }
        queryWrapper.orderByDesc(Skill::getCreateTime);
        return queryWrapper;
    }

    private String now() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }
}
