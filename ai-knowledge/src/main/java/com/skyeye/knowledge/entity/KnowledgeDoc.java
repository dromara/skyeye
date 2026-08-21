package com.skyeye.knowledge.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * AI知识库文档
 */
@TableName(value = "skyeye_ai_knowledge_doc")
public class KnowledgeDoc {

    @TableId("id")
    private String id;

    @TableField("knowledge_id")
    private String knowledgeId;

    @TableField(exist = false)
    private Knowledge knowledgeMation;

    @TableField("title")
    private String title;

    @TableField("`type`")
    private String type;

    @TableField("content")
    private String content;

    @TableField("metadata")
    private String metadata;

    @TableField("`status`")
    private String status;

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

    public String getKnowledgeId() {
        return knowledgeId;
    }

    public void setKnowledgeId(String knowledgeId) {
        this.knowledgeId = knowledgeId;
    }

    public Knowledge getKnowledgeMation() {
        return knowledgeMation;
    }

    public void setKnowledgeMation(Knowledge knowledgeMation) {
        this.knowledgeMation = knowledgeMation;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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
