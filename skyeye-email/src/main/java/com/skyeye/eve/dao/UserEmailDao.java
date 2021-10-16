/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: UserEmailDao
 * @Description: 邮箱模块数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 22:51
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface UserEmailDao {

	public List<Map<String, Object>> queryEmailListByUserId(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryEmailISInByEmailAddressAndUserId(Map<String, Object> map) throws Exception;

	public int insertEmailListByUserId(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryEmailCheckByUserId(Map<String, Object> map) throws Exception;

	public Map<String, Object> queryEmailMationById(Map<String, Object> map) throws Exception;

	public int insertEmailEnclosureListToServer(List<Map<String, Object>> beans) throws Exception;

	public List<Map<String, Object>> queryInboxEmailListByEmailId(Map<String, Object> map) throws Exception;
	
	public Map<String, Object> queryEmailMationByEmailId(Map<String, Object> map) throws Exception;
	
	public List<Map<String, Object>> queryEnclosureBeansMationByEmailId(Map<String, Object> map) throws Exception;
	
	public List<Map<String, Object>> querySendedEmailListByEmailId(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryDeleteEmailListByEmailId(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> queryDraftsEmailListByEmailId(Map<String, Object> map) throws Exception;
	
	public List<Map<String, Object>> queryMyEnclusureListByUserIdAndIds(Map<String, Object> map) throws Exception;
	
	public int insertToSendEmailMationByUserId(Map<String, Object> sendEmail) throws Exception;
	
    public Map<String, Object> queryDraftsEmailMationToEditByUserId(Map<String, Object> map) throws Exception;
    
    public List<Map<String, Object>> queryDraftsEmailToPeopleMationToEditByUserId(Map<String, Object> bean) throws Exception;
    
    public List<Map<String, Object>> queryDraftsEmailToCcMationToEditByUserId(Map<String, Object> bean) throws Exception;
    
    public List<Map<String, Object>> queryDraftsEmailToBccMationToEditByUserId(Map<String, Object> bean) throws Exception;
    
    public List<Map<String, Object>> queryDraftsEmailEnclosureMationToEditByUserId(Map<String, Object> bean) throws Exception;
    
    public int deleteEmailEnclosureListByEmailId(Map<String, Object> map) throws Exception;
    
    public int editToSendEmailMationByUserId(Map<String, Object> sendEmail) throws Exception;
    
    public Map<String, Object> queryForwardEmailMationToEditByUserId(Map<String, Object> map) throws Exception;
    
    public List<Map<String, Object>> queryForwardEmailFromPeopleMationToEditByUserId(Map<String, Object> bean) throws Exception;
    
    public List<Map<String, Object>> queryForwardEmailToCcMationToEditByUserId(Map<String, Object> bean) throws Exception;
    
    public List<Map<String, Object>> queryForwardEmailToBccMationToEditByUserId(Map<String, Object> bean) throws Exception;
    
    public List<Map<String, Object>> queryForwardEmailEnclosureMationToEditByUserId(Map<String, Object> bean) throws Exception;
	
}
