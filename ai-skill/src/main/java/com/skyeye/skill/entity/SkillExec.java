/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.skill.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * @ClassName: SkillExec
 * @Description: AI技能执行记录
 * @author: skyeye云系列
 * @date: 2026/08/18
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@TableName(value = "skyeye_ai_skill_exec")
public class SkillExec {

    @TableId("id")
    private String id;

    @TableField("skill_id")
    private String skillId;

    @TableField("skill_code")
    private String skillCode;

    @TableField("skill_name")
    private String skillName;

    @TableField("user_input")
    private String userInput;

    @TableField("screen_json")
    private String screenJson;

    @TableField("report_page_id")
    private String reportPageId;

    @TableField("report_content")
    private String reportContent;

    @TableField("status")
    private Integer status;

    @TableField("create_id")
    private String createId;

    @TableField("create_time")
    private String createTime;

    /** 接口返回时把 JSON 解析成对象，不落库 */
    @TableField(exist = false)
    private Object screen;

    /** 接口返回时把报表 content 解析成对象，不落库 */
    @TableField(exist = false)
    private Object reportPage;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSkillId() {
        return skillId;
    }

    public void setSkillId(String skillId) {
        this.skillId = skillId;
    }

    public String getSkillCode() {
        return skillCode;
    }

    public void setSkillCode(String skillCode) {
        this.skillCode = skillCode;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public String getUserInput() {
        return userInput;
    }

    public void setUserInput(String userInput) {
        this.userInput = userInput;
    }

    public String getScreenJson() {
        return screenJson;
    }

    public void setScreenJson(String screenJson) {
        this.screenJson = screenJson;
    }

    public String getReportPageId() {
        return reportPageId;
    }

    public void setReportPageId(String reportPageId) {
        this.reportPageId = reportPageId;
    }

    public String getReportContent() {
        return reportContent;
    }

    public void setReportContent(String reportContent) {
        this.reportContent = reportContent;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCreateId() {
        return createId;
    }

    public void setCreateId(String createId) {
        this.createId = createId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Object getScreen() {
        return screen;
    }

    public void setScreen(Object screen) {
        this.screen = screen;
    }

    public Object getReportPage() {
        return reportPage;
    }

    public void setReportPage(Object reportPage) {
        this.reportPage = reportPage;
    }
}
