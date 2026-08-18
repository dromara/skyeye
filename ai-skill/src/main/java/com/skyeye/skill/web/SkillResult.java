/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.skill.web;

import java.util.List;

/**
 * @ClassName: SkillResult
 * @Description: 对齐 Skyeye OutputObject 的返回结构
 */
public class SkillResult {

    private Integer returnCode = 0;
    private String returnMessage = "成功";
    private Object bean;
    private List<?> beans;
    private long total;

    public static SkillResult ok() {
        return new SkillResult();
    }

    public static SkillResult ok(Object bean) {
        SkillResult result = new SkillResult();
        result.setBean(bean);
        result.setTotal(bean == null ? 0 : 1);
        return result;
    }

    public static SkillResult list(List<?> beans, long total) {
        SkillResult result = new SkillResult();
        result.setBeans(beans);
        result.setTotal(total);
        return result;
    }

    public static SkillResult fail(String message) {
        SkillResult result = new SkillResult();
        result.setReturnCode(-1);
        result.setReturnMessage(message);
        return result;
    }

    public Integer getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(Integer returnCode) {
        this.returnCode = returnCode;
    }

    public String getReturnMessage() {
        return returnMessage;
    }

    public void setReturnMessage(String returnMessage) {
        this.returnMessage = returnMessage;
    }

    public Object getBean() {
        return bean;
    }

    public void setBean(Object bean) {
        this.bean = bean;
    }

    public List<?> getBeans() {
        return beans;
    }

    public void setBeans(List<?> beans) {
        this.beans = beans;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }
}
