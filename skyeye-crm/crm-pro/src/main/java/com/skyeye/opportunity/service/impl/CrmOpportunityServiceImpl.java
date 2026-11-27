/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.opportunity.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.common.base.Joiner;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.contacts.service.IContactsService;
import com.skyeye.opportunity.classenum.CrmOpportunityAuthEnum;
import com.skyeye.opportunity.classenum.CrmOpportunityStateEnum;
import com.skyeye.opportunity.dao.CrmOpportunityDao;
import com.skyeye.opportunity.entity.CrmOpportunity;
import com.skyeye.opportunity.service.CrmOpportunityService;
import com.skyeye.organization.service.IDepmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: CrmOpportunityServiceImpl
 * @Description: 客户商机管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/4/5 13:03
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
@SkyeyeService(name = "商机管理", groupName = "商机管理", flowable = true, teamAuth = true)
public class CrmOpportunityServiceImpl extends SkyeyeBusinessServiceImpl<CrmOpportunityDao, CrmOpportunity> implements CrmOpportunityService {

    @Autowired
    private IDepmentService iDepmentService;

    @Autowired
    private IContactsService iContactsService;

    @Override
    public Class getAuthEnumClass() {
        return CrmOpportunityAuthEnum.class;
    }

    @Override
    public List<String> getAuthPermissionKeyList() {
        return Arrays.asList(CrmOpportunityAuthEnum.ADD.getKey(), CrmOpportunityAuthEnum.EDIT.getKey(), CrmOpportunityAuthEnum.DELETE.getKey(),
            CrmOpportunityAuthEnum.REVOKE.getKey(), CrmOpportunityAuthEnum.INVALID.getKey(), CrmOpportunityAuthEnum.SUBMIT_TO_APPROVAL.getKey(), CrmOpportunityAuthEnum.LIST.getKey(),
            CrmOpportunityAuthEnum.CONMUNICATE.getKey(), CrmOpportunityAuthEnum.QUOTED_PRICE.getKey(), CrmOpportunityAuthEnum.TENDER.getKey(), CrmOpportunityAuthEnum.NEGOTIATE.getKey(),
            CrmOpportunityAuthEnum.TURNOVER.getKey(), CrmOpportunityAuthEnum.LOSING_TABLE.getKey(), CrmOpportunityAuthEnum.LAY_ASIDE.getKey());
    }

    @Override
    protected QueryWrapper<CrmOpportunity> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<CrmOpportunity> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isNotEmpty(commonPageInfo.getObjectId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(CrmOpportunity::getObjectId), commonPageInfo.getObjectId());
        }
        return queryWrapper;
    }

    @Override
    public CrmOpportunity selectById(String id) {
        CrmOpportunity crmOpportunity = super.selectById(id);
        Map<String, Object> department = iDepmentService.queryDataMationById(crmOpportunity.getDepartmentId());
        crmOpportunity.setDepartmentMation(department);

        // 联系人信息
        iContactsService.setDataMation(crmOpportunity, CrmOpportunity::getContacts);

        crmOpportunity.setFollowMation(iAuthUserService.queryDataMationByIds(Joiner.on(CommonCharConstants.COMMA_MARK).join(crmOpportunity.getFollowId())));
        crmOpportunity.setPartMation(iAuthUserService.queryDataMationByIds(Joiner.on(CommonCharConstants.COMMA_MARK).join(crmOpportunity.getPartId())));
        crmOpportunity.setResponsMation(iAuthUserService.queryDataMationById(crmOpportunity.getResponsId()));

        return crmOpportunity;
    }

    /**
     * 根据商机Id初期沟通
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void conmunicateOpportunity(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        CrmOpportunity crmOpportunity = selectById(id);
        String userId = inputObject.getLogParams().get(CommonConstants.ID).toString();
        checkAuthPermission(crmOpportunity, userId, CommonNumConstants.NUM_SEVEN);

        List<String> preposeState = Arrays.asList(new String[]{FlowableStateEnum.PASS.getKey(), CrmOpportunityStateEnum.SCHEME_AND_QUOTATION.getKey(),
            CrmOpportunityStateEnum.COMPETITION_AND_BIDDING.getKey(), CrmOpportunityStateEnum.BUSINESS_NEGOTIATION.getKey(), CrmOpportunityStateEnum.LAY_ASIDE.getKey()});
        if (preposeState.contains(crmOpportunity.getState())) {
            // 审核通过、方案与报价、竞争与投标、商务谈判、搁置状态下可以初期沟通
            UpdateWrapper<CrmOpportunity> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, id);
            updateWrapper.set(MybatisPlusUtil.toColumns(CrmOpportunity::getState), CrmOpportunityStateEnum.INITIAL_COMMUNICATION.getKey());
            update(updateWrapper);
            refreshCache(id);
        } else {
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
        }
    }

    /**
     * 根据商机Id方案与报价
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void quotedPriceOpportunity(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        CrmOpportunity crmOpportunity = selectById(id);
        String userId = inputObject.getLogParams().get(CommonConstants.ID).toString();
        checkAuthPermission(crmOpportunity, userId, CommonNumConstants.NUM_EIGHT);

        List<String> preposeState = Arrays.asList(new String[]{FlowableStateEnum.PASS.getKey(), CrmOpportunityStateEnum.INITIAL_COMMUNICATION.getKey(),
            CrmOpportunityStateEnum.COMPETITION_AND_BIDDING.getKey(), CrmOpportunityStateEnum.BUSINESS_NEGOTIATION.getKey()});
        if (preposeState.contains(crmOpportunity.getState())) {
            // 审核通过、初期沟通、竞争与投标、商务谈判状态下可以方案与报价
            UpdateWrapper<CrmOpportunity> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, id);
            updateWrapper.set(MybatisPlusUtil.toColumns(CrmOpportunity::getState), CrmOpportunityStateEnum.SCHEME_AND_QUOTATION.getKey());
            update(updateWrapper);
            refreshCache(id);
        } else {
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
        }
    }

    /**
     * 根据商机Id竞争与投标
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void tenderOpportunity(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        CrmOpportunity crmOpportunity = selectById(id);
        String userId = inputObject.getLogParams().get(CommonConstants.ID).toString();
        checkAuthPermission(crmOpportunity, userId, CommonNumConstants.NUM_NINE);

        List<String> preposeState = Arrays.asList(new String[]{FlowableStateEnum.PASS.getKey(), CrmOpportunityStateEnum.INITIAL_COMMUNICATION.getKey(),
            CrmOpportunityStateEnum.SCHEME_AND_QUOTATION.getKey(), CrmOpportunityStateEnum.BUSINESS_NEGOTIATION.getKey()});
        if (preposeState.contains(crmOpportunity.getState())) {
            // 审核通过、初期沟通、方案与报价、商务谈判状态下可以竞争与投标
            UpdateWrapper<CrmOpportunity> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, id);
            updateWrapper.set(MybatisPlusUtil.toColumns(CrmOpportunity::getState), CrmOpportunityStateEnum.COMPETITION_AND_BIDDING.getKey());
            update(updateWrapper);
            refreshCache(id);
        } else {
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
        }
    }

    /**
     * 根据商机Id商务谈判
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void negotiateOpportunity(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        CrmOpportunity crmOpportunity = selectById(id);
        String userId = inputObject.getLogParams().get(CommonConstants.ID).toString();
        checkAuthPermission(crmOpportunity, userId, CommonNumConstants.NUM_TEN);

        List<String> preposeState = Arrays.asList(new String[]{FlowableStateEnum.PASS.getKey(), CrmOpportunityStateEnum.INITIAL_COMMUNICATION.getKey(),
            CrmOpportunityStateEnum.SCHEME_AND_QUOTATION.getKey(), CrmOpportunityStateEnum.COMPETITION_AND_BIDDING.getKey()});
        if (preposeState.contains(crmOpportunity.getState())) {
            // 审核通过、初期沟通、方案与报价、竞争与投标状态下可以商务谈判
            UpdateWrapper<CrmOpportunity> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, id);
            updateWrapper.set(MybatisPlusUtil.toColumns(CrmOpportunity::getState), CrmOpportunityStateEnum.BUSINESS_NEGOTIATION.getKey());
            update(updateWrapper);
            refreshCache(id);
        } else {
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
        }
    }

    /**
     * 根据商机Id成交
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void turnoverOpportunity(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        CrmOpportunity crmOpportunity = selectById(id);
        String userId = inputObject.getLogParams().get(CommonConstants.ID).toString();
        checkAuthPermission(crmOpportunity, userId, CommonNumConstants.NUM_ELEVEN);

        List<String> preposeState = Arrays.asList(new String[]{FlowableStateEnum.PASS.getKey(), CrmOpportunityStateEnum.INITIAL_COMMUNICATION.getKey(),
            CrmOpportunityStateEnum.SCHEME_AND_QUOTATION.getKey(), CrmOpportunityStateEnum.COMPETITION_AND_BIDDING.getKey(), CrmOpportunityStateEnum.BUSINESS_NEGOTIATION.getKey()});
        if (preposeState.contains(crmOpportunity.getState())) {
            // 审核通过、初期沟通、方案与报价、竞争与投标、商务谈判状态下可以成交
            UpdateWrapper<CrmOpportunity> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, id);
            updateWrapper.set(MybatisPlusUtil.toColumns(CrmOpportunity::getState), CrmOpportunityStateEnum.STRIKE_BARGAIN.getKey());
            update(updateWrapper);
            refreshCache(id);
        } else {
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
        }
    }

    /**
     * 根据商机Id丢单
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void losingTableOpportunity(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        CrmOpportunity crmOpportunity = selectById(id);
        String userId = inputObject.getLogParams().get(CommonConstants.ID).toString();
        checkAuthPermission(crmOpportunity, userId, CommonNumConstants.NUM_TWELVE);

        List<String> preposeState = Arrays.asList(new String[]{FlowableStateEnum.PASS.getKey(), CrmOpportunityStateEnum.INITIAL_COMMUNICATION.getKey(),
            CrmOpportunityStateEnum.SCHEME_AND_QUOTATION.getKey(), CrmOpportunityStateEnum.COMPETITION_AND_BIDDING.getKey(), CrmOpportunityStateEnum.BUSINESS_NEGOTIATION.getKey(),
            CrmOpportunityStateEnum.LAY_ASIDE.getKey()});
        if (preposeState.contains(crmOpportunity.getState())) {
            // 审核通过、初期沟通、方案与报价、竞争与投标、商务谈判、搁置状态下可以丢单
            UpdateWrapper<CrmOpportunity> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, id);
            updateWrapper.set(MybatisPlusUtil.toColumns(CrmOpportunity::getState), CrmOpportunityStateEnum.LOST_ORDER.getKey());
            update(updateWrapper);
            refreshCache(id);
        } else {
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
        }
    }

    /**
     * 根据商机Id搁置
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void layAsideOpportunity(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        CrmOpportunity crmOpportunity = selectById(id);
        String userId = inputObject.getLogParams().get(CommonConstants.ID).toString();
        checkAuthPermission(crmOpportunity, userId, CommonNumConstants.NUM_THIRTEEN);

        List<String> preposeState = Arrays.asList(new String[]{FlowableStateEnum.PASS.getKey(), CrmOpportunityStateEnum.INITIAL_COMMUNICATION.getKey(),
            CrmOpportunityStateEnum.SCHEME_AND_QUOTATION.getKey(), CrmOpportunityStateEnum.COMPETITION_AND_BIDDING.getKey(), CrmOpportunityStateEnum.BUSINESS_NEGOTIATION.getKey()});
        if (preposeState.contains(crmOpportunity.getState())) {
            // 审核通过、初期沟通、方案与报价、竞争与投标、商务谈判状态下可以搁置
            UpdateWrapper<CrmOpportunity> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, id);
            updateWrapper.set(MybatisPlusUtil.toColumns(CrmOpportunity::getState), CrmOpportunityStateEnum.LAY_ASIDE.getKey());
            update(updateWrapper);
            refreshCache(id);
        } else {
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
        }
    }

    /**
     * 根据所属第三方业务数据id查询商机
     *
     * @param objectId 所属第三方业务数据id
     * @return
     */
    @Override
    public List<CrmOpportunity> queryCrmOpportunityListByObjectId(String objectId) {
        QueryWrapper<CrmOpportunity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(CrmOpportunity::getObjectId), objectId);
        return list(queryWrapper);
    }

    /**
     * 根据客户id获取指定状态的商机列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryCrmOpportunityListByObjectId(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String objectId = map.get("objectId").toString();
        if (StrUtil.isEmpty(objectId)) {
            return;
        }
        List<CrmOpportunity> crmOpportunityList = queryCrmOpportunityListByObjectId(objectId);
        // 过滤状态
        List<String> preposeState = Arrays.asList(new String[]{FlowableStateEnum.PASS.getKey(), CrmOpportunityStateEnum.INITIAL_COMMUNICATION.getKey(),
            CrmOpportunityStateEnum.SCHEME_AND_QUOTATION.getKey(), CrmOpportunityStateEnum.COMPETITION_AND_BIDDING.getKey(), CrmOpportunityStateEnum.BUSINESS_NEGOTIATION.getKey(),
            CrmOpportunityStateEnum.STRIKE_BARGAIN.getKey(), CrmOpportunityStateEnum.LAY_ASIDE.getKey()});

        List<Map<String, Object>> results = crmOpportunityList.stream()
            .filter(crmOpportunity -> preposeState.indexOf(crmOpportunity.getState()) >= 0)
            .map(crmOpportunity -> BeanUtil.beanToMap(crmOpportunity)).collect(Collectors.toList());
        results.forEach(bean -> {
            bean.put("name", bean.get("title"));
        });
        outputObject.setBeans(results);
        outputObject.settotal(results.size());
    }

}
