/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface ForumContentService {

	public void queryMyForumContentList(InputObject inputObject, OutputObject outputObject) throws Exception;

	public void insertForumContentMation(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void deleteForumContentById(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void queryForumContentMationById(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void editForumContentMationById(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void queryForumContentMationToDetails(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void queryNewForumContentList(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void insertForumCommentMation(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void queryForumCommentList(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void insertForumReplyMation(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void queryForumReplyList(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void queryForumMyBrowerList(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void queryNewCommentList(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void queryForumListByTagId(InputObject inputObject, OutputObject outputObject) throws Exception;
	
	public void queryHotTagList(InputObject inputObject, OutputObject outputObject) throws Exception;
	
    public void queryActiveUsersList(InputObject inputObject, OutputObject outputObject) throws Exception;
	
    public void queryHotForumList(InputObject inputObject, OutputObject outputObject) throws Exception;
	
    public void querySearchForumList(InputObject inputObject, OutputObject outputObject) throws Exception;
    
    public void querySolrSynchronousTime(InputObject inputObject, OutputObject outputObject) throws Exception;
	
    public void updateSolrSynchronousData(InputObject inputObject, OutputObject outputObject) throws Exception;
    
    public void queryMyCommentList(InputObject inputObject, OutputObject outputObject) throws Exception;
	
    public void deleteCommentById(InputObject inputObject, OutputObject outputObject) throws Exception;
	
    public void queryMyNoticeList(InputObject inputObject, OutputObject outputObject) throws Exception;
	
    public void deleteNoticeById(InputObject inputObject, OutputObject outputObject) throws Exception;

}
