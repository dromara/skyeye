/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.documentary.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeTeamAuthServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.customer.service.CustomerService;
import com.skyeye.documentary.classenum.CrmDocumentaryAuthEnum;
import com.skyeye.documentary.dao.CrmDocumentaryDao;
import com.skyeye.documentary.entity.Documentary;
import com.skyeye.documentary.service.CrmDocumentaryService;
import com.skyeye.eve.contacts.service.IContactsService;
import com.skyeye.exception.CustomException;
import com.skyeye.opportunity.classenum.CrmOpportunityStateEnum;
import com.skyeye.opportunity.entity.CrmOpportunity;
import com.skyeye.opportunity.service.CrmOpportunityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: CrmDocumentaryServiceImpl
 * @Description: 服务跟单管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:20
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "跟单管理", groupName = "跟单管理", teamAuth = true)
public class CrmDocumentaryServiceImpl extends SkyeyeTeamAuthServiceImpl<CrmDocumentaryDao, Documentary> implements CrmDocumentaryService {

    @Autowired
    private IContactsService iContactsService;

    @Autowired
    private CrmOpportunityService crmOpportunityService;

    @Autowired
    private CustomerService customerService;

    @Override
    public Class getAuthEnumClass() {
        return CrmDocumentaryAuthEnum.class;
    }

    @Override
    public List<String> getAuthPermissionKeyList() {
        return Arrays.asList(CrmDocumentaryAuthEnum.ADD.getKey(), CrmDocumentaryAuthEnum.EDIT.getKey(), CrmDocumentaryAuthEnum.DELETE.getKey());
    }

    @Override
    public void createPrepose(Documentary entity) {
        CrmOpportunity crmOpportunity = crmOpportunityService.selectById(entity.getOpportunityId());
        if (ObjectUtil.isEmpty(crmOpportunity) || StrUtil.isEmpty(crmOpportunity.getId())) {
            throw new CustomException("关联的商机不存在。");
        }
        // 只有以下状态的商机才可以进行跟单
        List<String> preposeState = Arrays.asList(new String[]{FlowableStateEnum.PASS.getKey(), CrmOpportunityStateEnum.INITIAL_COMMUNICATION.getKey(),
            CrmOpportunityStateEnum.SCHEME_AND_QUOTATION.getKey(), CrmOpportunityStateEnum.COMPETITION_AND_BIDDING.getKey(), CrmOpportunityStateEnum.BUSINESS_NEGOTIATION.getKey(),
            CrmOpportunityStateEnum.STRIKE_BARGAIN.getKey(), CrmOpportunityStateEnum.LAY_ASIDE.getKey()});
        if (preposeState.indexOf(crmOpportunity.getState()) == -1) {
            throw new CustomException("关联的商机状态不可进行跟单。");
        }
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        CommonPageInfo pageInfo = inputObject.getParams(CommonPageInfo.class);
        // type=myCreate：仅查询当前登录人创建的跟单（仪表盘「我的跟进记录」）
        if (StrUtil.equals(pageInfo.getType(), "myCreate")) {
            pageInfo.setCreateId(inputObject.getLogParams().get("id").toString());
        }
        List<Map<String, Object>> beans = skyeyeBaseMapper.queryDocumentaryList(pageInfo);
        // 填充客户名称
        customerService.setMationForMap(beans, "objectId", "objectMation");
        return beans;
    }

    @Override
    public Documentary selectById(String id) {
        Documentary documentary = super.selectById(id);
        // 联系人信息
        iContactsService.setDataMation(documentary, Documentary::getContacts);
        // 商机信息
        crmOpportunityService.setDataMation(documentary, Documentary::getOpportunityId);
        return documentary;
    }
}
