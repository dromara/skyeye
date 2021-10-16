/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.solr;

import java.io.Serializable;

import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.solr.core.mapping.SolrDocument;

@SolrDocument(solrCoreName = "custom_core")
public class Forum implements Serializable {

	private static final long serialVersionUID = 1L;

	@Field
	private String id;
	@Field
	private String forumContent;
	@Field
	private String type;
	@Field
	private String state;
	@Field
	private String reportState;
	@Field
	private String tagId;
	@Field
	private String forumTitle;
	@Field
	private String forumDesc;
	@Field
	private String anonymous;
	@Field
	private String createId;
	@Field
	private String createTime;

	public Forum() {
	}

	public Forum(String id, String forumContent, String type, String state, String reportState, String tagId,
			String forumTitle, String forumDesc, String anonymous, String createId, String createTime) {
		this.id = id;
		this.forumContent = forumContent;
		this.type = type;
		this.state = state;
		this.reportState = reportState;
		this.tagId = tagId;
		this.forumTitle = forumTitle;
		this.forumDesc = forumDesc;
		this.anonymous = anonymous;
		this.createId = createId;
		this.createTime = createTime;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getForumContent() {
		return forumContent;
	}

	public void setForumContent(String forumContent) {
		this.forumContent = forumContent;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getReportState() {
		return reportState;
	}

	public void setReportState(String reportState) {
		this.reportState = reportState;
	}

	public String getTagId() {
		return tagId;
	}

	public void setTagId(String tagId) {
		this.tagId = tagId;
	}

	public String getForumTitle() {
		return forumTitle;
	}

	public void setForumTitle(String forumTitle) {
		this.forumTitle = forumTitle;
	}

	public String getForumDesc() {
		return forumDesc;
	}

	public void setForumDesc(String forumDesc) {
		this.forumDesc = forumDesc;
	}

	public String getAnonymous() {
		return anonymous;
	}

	public void setAnonymous(String anonymous) {
		this.anonymous = anonymous;
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

}
