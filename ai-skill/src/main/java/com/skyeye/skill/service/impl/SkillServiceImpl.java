/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.skill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.skyeye.skill.dao.SkillDao;
import com.skyeye.skill.entity.Skill;
import com.skyeye.skill.exception.CustomException;
import com.skyeye.skill.service.SkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
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

    @Autowired
    private SkillDao skillDao;

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
    public String executeSkill(String skillCode, String userInput) {
        if (!StringUtils.hasText(skillCode)) {
            throw new CustomException("skillCode不能为空");
        }
        Skill skill = skillDao.selectOne(new LambdaQueryWrapper<Skill>().eq(Skill::getCode, skillCode));
        if (skill == null) {
            throw new CustomException("未找到技能: " + skillCode);
        }
        if (skill.getEnabled() == null || skill.getEnabled() != ENABLED) {
            throw new CustomException("技能未启用: " + skillCode);
        }
        return "技能已识别：" + skill.getName() + "（" + skill.getCode()
            + "）。当前是空壳，尚未执行。用户输入：" + (userInput == null ? "" : userInput);
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
