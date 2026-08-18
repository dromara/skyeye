/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.skill.controller;

import com.skyeye.skill.entity.Skill;
import com.skyeye.skill.entity.SkillExec;
import com.skyeye.skill.service.SkillService;
import com.skyeye.skill.web.SkillResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: SkillController
 * @Description: AI技能控制类（路径对齐 Skyeye /post/XxxController/方法名）
 * @author: skyeye云系列
 * @date: 2026/08/16
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
public class SkillController {

    @Autowired
    private SkillService skillService;

    @RequestMapping("/post/SkillController/writeSkill")
    public SkillResult writeSkill(@RequestBody Skill skill) {
        return SkillResult.ok(skillService.saveOrUpdate(skill));
    }

    @RequestMapping("/post/SkillController/querySkillPageList")
    public SkillResult querySkillPageList(@RequestBody(required = false) Map<String, Object> params) {
        int page = intValue(params, "page", 1);
        int limit = intValue(params, "limit", 10);
        String keyword = stringValue(params, "keyword");
        List<Skill> list = skillService.queryPageList(page, limit, keyword);
        return SkillResult.list(list, skillService.count(keyword));
    }

    @RequestMapping("/post/SkillController/querySkillList")
    public SkillResult querySkillList() {
        List<Skill> list = skillService.queryList();
        return SkillResult.list(list, list.size());
    }

    @RequestMapping(value = "/post/SkillController/selectSkillById", method = RequestMethod.GET)
    public SkillResult selectSkillById(@RequestParam("id") String id) {
        return SkillResult.ok(skillService.selectById(id));
    }

    @RequestMapping(value = "/post/SkillController/deleteSkillById", method = RequestMethod.DELETE)
    public SkillResult deleteSkillById(@RequestParam("id") String id) {
        skillService.deleteById(id);
        return SkillResult.ok();
    }

    @RequestMapping("/post/SkillController/executeSkill")
    public SkillResult executeSkill(@RequestBody Map<String, Object> params) {
        return SkillResult.ok(skillService.executeSkill(stringValue(params, "skillCode"), stringValue(params, "userInput")));
    }

    @RequestMapping("/post/SkillController/querySkillExecPageList")
    public SkillResult querySkillExecPageList(@RequestBody(required = false) Map<String, Object> params) {
        int page = intValue(params, "page", 1);
        int limit = intValue(params, "limit", 10);
        String skillCode = stringValue(params, "skillCode");
        List<SkillExec> list = skillService.queryExecPageList(page, limit, skillCode);
        return SkillResult.list(list, skillService.countExec(skillCode));
    }

    @RequestMapping(value = "/post/SkillController/selectSkillExecById", method = RequestMethod.GET)
    public SkillResult selectSkillExecById(@RequestParam("id") String id) {
        return SkillResult.ok(skillService.selectExecById(id));
    }

    private int intValue(Map<String, Object> params, String key, int defaultValue) {
        if (params == null || params.get(key) == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(String.valueOf(params.get(key)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private String stringValue(Map<String, Object> params, String key) {
        if (params == null || params.get(key) == null) {
            return null;
        }
        return String.valueOf(params.get(key));
    }
}
