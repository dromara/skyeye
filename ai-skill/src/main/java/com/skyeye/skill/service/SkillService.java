/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.skill.service;

import com.skyeye.skill.entity.Skill;
import com.skyeye.skill.entity.SkillExec;

import java.util.List;

/**
 * @ClassName: SkillService
 * @Description: AI技能服务接口层
 * @author: skyeye云系列
 * @date: 2026/08/16
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface SkillService {

    /**
     * 新增或编辑技能
     *
     * @param skill 技能实体
     * @return 保存后的技能
     */
    Skill saveOrUpdate(Skill skill);

    /**
     * 根据id查询技能
     *
     * @param id 主键id
     * @return 技能
     */
    Skill selectById(String id);

    /**
     * 查询全部技能
     *
     * @return 技能列表
     */
    List<Skill> queryList();

    /**
     * 分页查询技能
     *
     * @param page    页码
     * @param limit   每页条数
     * @param keyword 关键字
     * @return 技能列表
     */
    List<Skill> queryPageList(int page, int limit, String keyword);

    /**
     * 统计数量
     *
     * @param keyword 关键字
     * @return 总数
     */
    long count(String keyword);

    /**
     * 根据id删除技能
     *
     * @param id 主键id
     */
    void deleteById(String id);

    /**
     * 执行技能。skyeye-bigscreen 会生成大屏 JSON 并落执行记录。
     *
     * @param skillCode 技能编码
     * @param userInput 用户自然语言需求
     * @return 执行记录（含解析后的 screen）
     */
    SkillExec executeSkill(String skillCode, String userInput);

    /**
     * 根据id查询执行记录
     *
     * @param id 执行记录id
     * @return 执行记录
     */
    SkillExec selectExecById(String id);

    /**
     * 分页查询执行记录
     *
     * @param page      页码
     * @param limit     每页条数
     * @param skillCode 技能编码，可空
     * @return 执行记录
     */
    List<SkillExec> queryExecPageList(int page, int limit, String skillCode);

    /**
     * 执行记录数量
     *
     * @param skillCode 技能编码，可空
     * @return 总数
     */
    long countExec(String skillCode);
}
