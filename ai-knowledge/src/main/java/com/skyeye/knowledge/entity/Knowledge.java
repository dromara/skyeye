package com.skyeye.knowledge.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * AI知识库
 */
@TableName(value = "skyeye_ai_knowledge")
public class Knowledge {

    @TableId("id")
    private String id;

    @TableField("`name`")
    private String name;

    @TableField("descr")
    private String descr;

    @TableField("embed_id")
    private String embedId;

    @TableField(exist = false)
    private EmbedModel embedMation;

    @TableField("`status`")
    private String status;

    @TableField("`type`")
    private String type;

    @TableField("metadata")
    private String metadata;

    @TableField("create_id")
    private String createId;

    @TableField("create_time")
    private String createTime;

    @TableField("last_update_id")
    private String lastUpdateId;

    @TableField("last_update_time")
    private String lastUpdateTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public String getEmbedId() {
        return embedId;
    }

    public void setEmbedId(String embedId) {
        this.embedId = embedId;
    }

    public EmbedModel getEmbedMation() {
        return embedMation;
    }

    public void setEmbedMation(EmbedModel embedMation) {
        this.embedMation = embedMation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
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

    public String getLastUpdateId() {
        return lastUpdateId;
    }

    public void setLastUpdateId(String lastUpdateId) {
        this.lastUpdateId = lastUpdateId;
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
}
