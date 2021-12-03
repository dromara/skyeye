/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.solr;

import lombok.Data;
import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.solr.core.mapping.SolrDocument;

import java.io.Serializable;

@Data
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

}
