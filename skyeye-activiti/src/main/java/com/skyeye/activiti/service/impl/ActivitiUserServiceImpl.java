/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.activiti.service.impl;

import cn.hutool.json.JSONUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.activiti.service.ActivitiUserService;
import com.skyeye.common.constans.ActivitiConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.dao.SysEveUserDao;
import net.sf.json.JSONObject;
import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.impl.persistence.entity.GroupEntity;
import org.activiti.engine.impl.persistence.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: ActivitiUserServiceImpl
 * @Description: 工作流用户相关内容
 * @author: skyeye云系列--卫志强
 * @date: 2021/12/2 20:45
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class ActivitiUserServiceImpl implements ActivitiUserService {

    @Autowired
    private SysEveUserDao sysEveUserDao;

    @Autowired
    private IdentityService identityService;

    /**
     * @Title: queryUserListToActiviti
     * @Description: 获取人员选择
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    public void queryUserListToActiviti(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String reqObjStr = map.get("reqObj").toString();
        Map<String, Object> reqObj = JSONUtil.toBean(reqObjStr, null);

        // 获取参数信息
        List<Map<String, Object>> conditions = JSONUtil.toList(reqObj.get("conditions").toString(), null);

        // 查询参数
        Map<String, Object> parmter = new HashMap<>();
        if(conditions.size() > 0){//参数信息
            parmter.put(conditions.get(0).get("key").toString(), conditions.get(0).get("value"));
        }
        initPagingMation(reqObj, parmter);

        Page pages = PageHelper.startPage(Integer.parseInt(parmter.get("page").toString()), Integer.parseInt(parmter.get("limit").toString()));
        List<Map<String, Object>> beans = sysEveUserDao.queryUserListToActiviti(parmter);
        long total = pages.getTotal();

        // 表信息
        Map<String, Object> query = new HashMap<>();
        setCommonUserTableElement(query, reqObj.get("queryId").toString(), parmter.get("limit").toString());
        query.put("columnList", ActivitiConstants.getActivitiUserColumnList());
        query.put("columnMap", ActivitiConstants.getActivitiUserColumnMap());

        // 分页信息
        Map<String, Object> pageInfo = getTablePageMation(total, parmter);

        outputObject.setCustomBean("query", query);
        outputObject.setCustomBean("pageInfo", pageInfo);
        outputObject.setBeans(beans);
        outputObject.settotal(total);
    }

    /**
     * 人员选择的表格公共部分
     *
     * @param query table信息
     * @param queryId 表格id
     * @param limit 每页多少条数据
     */
    private void setCommonUserTableElement(Map<String, Object> query, String queryId, String limit){
        query.put("id", queryId);
        query.put("key", "id");
        query.put("tableName", "流程用户列表");
        query.put("pagesize", limit);
        query.put("pagesInGrp", "5");
        query.put("widthType", "px");
        query.put("allowPaging", true);
        query.put("enableMultiline", true);
        query.put("isServerFilter", false);
        query.put("enableMultiHeader", false);
        query.put("simpleSearch", false);
        query.put("startRow", 1);
    }

    /**
     * 初始化分页信息
     *
     * @param reqObj 请求参数信息
     * @param parmter 查询参数
     */
    private void initPagingMation(Map<String, Object> reqObj, Map<String, Object> parmter){
        Map<String, Object> page = JSONObject.fromObject(reqObj.get("pageInfo").toString());
        if(page == null || page.isEmpty()){
            parmter.put("page", 1);
            parmter.put("limit", 10);
        }else{
            parmter.put("page", page.get("pageNum"));
            parmter.put("limit", page.get("pageSize"));
        }
    }

    /**
     * 获取表格的分页信息
     *
     * @param total 数据总条数
     * @param parmter 分页信息
     * @return
     */
    private Map<String, Object> getTablePageMation(long total, Map<String, Object> parmter){
        Map<String, Object> pageInfo = new HashMap<>();
        String limit = parmter.get("limit").toString();
        pageInfo.put("pageNum", parmter.get("page"));
        pageInfo.put("pageSize", parmter.get("limit"));
        pageInfo.put("count", total);
        long pageCount = total / Integer.parseInt(limit);
        if (total % Integer.parseInt(limit) != 0){
            pageCount++;
        }
        pageInfo.put("pageCount", pageCount);
        return pageInfo;
    }

    /**
     * @Title: queryUserGroupListToActiviti
     * @Description: 获取组人员选择
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    public void queryUserGroupListToActiviti(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String reqObjStr = map.get("reqObj").toString();
        Map<String, Object> reqObj = JSONUtil.toBean(reqObjStr, null);
        String queryId = reqObj.get("queryId").toString();
        //获取参数信息
        List<Map<String, Object>> conditions = JSONUtil.toList(reqObj.get("conditions").toString(), null);

        //查询参数
        Map<String, Object> parmter = new HashMap<>();
        for(Map<String, Object> condition : conditions){//参数信息
            parmter.put(condition.get("key").toString(), condition.get("value"));
        }
        initPagingMation(reqObj, parmter);

        Page pages = PageHelper.startPage(Integer.parseInt(parmter.get("page").toString()), Integer.parseInt(parmter.get("limit").toString()));
        List<Map<String, Object>> beans = null;
        if("id_group_list".equals(queryId)){//分组
            beans = sysEveUserDao.queryGroupListToActiviti(parmter);//人员
        }else{
            beans = sysEveUserDao.queryUserListToActivitiByGroup(parmter);//人员
        }
        long total = pages.getTotal();

        // 表信息
        Map<String, Object> query = new HashMap<>();
        setCommonUserTableElement(query, reqObj.get("queryId").toString(), parmter.get("limit").toString());
        if("id_group_list".equals(queryId)){//分组
            query.put("columnList", ActivitiConstants.getActivitiGroupColumnList());
            query.put("columnMap", ActivitiConstants.getActivitiGroupColumnMap());
        }else{//人员
            query.put("columnList", ActivitiConstants.getActivitiUserColumnListByGroupId());
            query.put("columnMap", ActivitiConstants.getActivitiUserColumnMapByGroupId());
        }

        // 分页信息
        Map<String, Object> pageInfo = getTablePageMation(total, parmter);

        outputObject.setCustomBean("query", query);
        outputObject.setCustomBean("pageInfo", pageInfo);
        outputObject.setBeans(beans);
        outputObject.settotal(total);
    }

    /**
     * @Title: insertSyncUserListMationToAct
     * @Description: 用户以及用户组信息同步到act表中
     * @param inputObject
     * @param outputObject
     * @throws Exception    参数
     * @return void    返回类型
     * @throws
     */
    @Override
    public void insertSyncUserListMationToAct(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        // 同步用户组信息
        List<Map<String, Object>> groupList = sysEveUserDao.queryActGroupList(map);
        for(Map<String, Object> bean : groupList){
            Group group = new GroupEntity();
            group.setId(bean.get("id").toString());
            group.setName(bean.get("name").toString());
            identityService.deleteGroup(group.getId());
            identityService.saveGroup(group);
        }
        // 同步用户信息
        List<Map<String, Object>> userList = sysEveUserDao.queryActUserList(map);
        for(Map<String, Object> bean : userList){
            User user = new UserEntity();
            user.setId(bean.get("id").toString());
            user.setFirstName(bean.get("firstName").toString());
            user.setLastName(bean.get("lastName").toString());
            user.setEmail(bean.get("email").toString());
            identityService.deleteUser(user.getId());
            identityService.saveUser(user);
        }
        // 同步用户和用户组的关系信息
        List<Map<String, Object>> userGroupList = sysEveUserDao.queryActUserGroupList(map);
        for(Map<String, Object> bean : userGroupList){
            identityService.deleteMembership(bean.get("userId").toString(), bean.get("groupId").toString());
            identityService.createMembership(bean.get("userId").toString(), bean.get("groupId").toString());
        }
    }

}
