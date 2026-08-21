package com.skyeye.knowledge.web;

import java.util.List;

/**
 * 对齐 Skyeye OutputObject 的返回结构
 */
public class KnowledgeResult {

    private Integer returnCode = 0;
    private String returnMessage = "成功";
    private Object bean;
    private List<?> beans;
    private long total;

    public static KnowledgeResult ok() {
        return new KnowledgeResult();
    }

    public static KnowledgeResult ok(Object bean) {
        KnowledgeResult result = new KnowledgeResult();
        result.setBean(bean);
        result.setTotal(bean == null ? 0 : 1);
        return result;
    }

    public static KnowledgeResult list(List<?> beans, long total) {
        KnowledgeResult result = new KnowledgeResult();
        result.setBeans(beans);
        result.setTotal(total);
        return result;
    }

    public static KnowledgeResult fail(String message) {
        KnowledgeResult result = new KnowledgeResult();
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
