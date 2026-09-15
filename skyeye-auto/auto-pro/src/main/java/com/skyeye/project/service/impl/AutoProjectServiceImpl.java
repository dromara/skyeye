/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.project.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.bug.service.AutoBugService;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.object.ResultEntity;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.demand.service.AutoDemandService;
import com.skyeye.exception.CustomException;
import com.skyeye.product.service.AutoProductService;
import com.skyeye.project.dao.AutoProjectDao;
import com.skyeye.project.entity.AutoProject;
import com.skyeye.project.service.AutoProjectService;
import com.skyeye.sdk.catalog.service.CatalogSdkService;
import com.skyeye.team.service.ITeamBusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: AutoProjectServiceImpl
 * @Description: 项目管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/20 19:28
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "项目管理", groupName = "项目管理")
public class AutoProjectServiceImpl extends SkyeyeBusinessServiceImpl<AutoProjectDao, AutoProject> implements AutoProjectService, CatalogSdkService {

    @Autowired
    private ITeamBusinessService iTeamBusinessService;

    @Autowired
    private AutoProductService autoProductService;

    @Autowired
    private AutoDemandService autoDemandService;

    @Autowired
    private AutoBugService autoBugService;

    @Override
    public void queryAutoProjectList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        if (StrUtil.equals(commonPageInfo.getType(), "myCharge")) {
            // 我负责的
            ResultEntity resultEnt = iTeamBusinessService.queryMyBusinessTeamIdsLinkObjectId(commonPageInfo.getPage(),
                commonPageInfo.getLimit(), getServiceClassName(), true);
            if (CollectionUtil.isEmpty(resultEnt.getRows())) {
                return;
            }
            List<String> ids = resultEnt.getRows().stream().map(row -> row.get("objectId").toString()).distinct().collect(Collectors.toList());
            commonPageInfo.setIds(ids);
            if (tenantEnable) {
                commonPageInfo.setTenantId(TenantContext.getTenantId());
            }
            List<Map<String, Object>> autoProjectList = skyeyeBaseMapper.queryAutoProjectList(commonPageInfo);
            iAuthUserService.setNameForMap(autoProjectList, "createId", "createName");
            iAuthUserService.setNameForMap(autoProjectList, "lastUpdateId", "lastUpdateName");
            String serviceClassName = getServiceClassName();
            autoProjectList.forEach(autoProject -> {
                autoProject.put("serviceClassName", serviceClassName);
            });
            outputObject.setBeans(autoProjectList);
            outputObject.settotal(resultEnt.getTotal());
        } else {
            // 全部
            queryPageList(inputObject, outputObject);
        }
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        CommonPageInfo pageInfo = inputObject.getParams(CommonPageInfo.class);
        String userId = inputObject.getLogParams().get("id").toString();
        pageInfo.setCreateId(userId);
        if (tenantEnable) {
            pageInfo.setTenantId(TenantContext.getTenantId());
        }
        List<Map<String, Object>> beans = skyeyeBaseMapper.queryAutoProjectList(pageInfo);
        return beans;
    }

    @Override
    public void createPostpose(AutoProject entity, String userId) {
        // 创建团队信息
        iTeamBusinessService.createTeamBusiness(entity.getTeamTemplateId(), entity.getId(), getServiceClassName());
    }

    @Override
    public void deletePostpose(String id) {
        iTeamBusinessService.deleteTeamBusiness(id, getServiceClassName());
    }

    /**
     * 移出团队成员后：将该成员负责的需求/Bug 默认转给项目经理
     */
    @Override
    public void teamMemberRemovePostpose(String objectId, List<String> removeUserIds, String chargeUserId) {
        if (StrUtil.isBlank(objectId) || CollectionUtil.isEmpty(removeUserIds) || StrUtil.isBlank(chargeUserId)) {
            return;
        }
        autoDemandService.reassignHandlerToChargeUser(objectId, removeUserIds, chargeUserId);
        autoBugService.reassignHandlerToChargeUser(objectId, removeUserIds, chargeUserId);
    }

    @Override
    public AutoProject selectById(String id) {
        AutoProject autoProject = super.selectById(id);
        autoProductService.setDataMation(autoProject, AutoProject::getProductId);
        return autoProject;
    }

    @Override
    public void queryAllAutoProjectList(InputObject inputObject, OutputObject outputObject) {
        String type = inputObject.getParams().get("type").toString();
        CommonPageInfo pageInfo = new CommonPageInfo();
        pageInfo.setType(type);
        String userId = inputObject.getLogParams().get("id").toString();
        pageInfo.setCreateId(userId);
        if (StrUtil.equals("myCharge", pageInfo.getType())) {
            // 我负责的
            ResultEntity resultEnt = iTeamBusinessService.queryMyBusinessTeamIdsLinkObjectId(null,
                null, getServiceClassName(), false);
            if (CollectionUtil.isEmpty(resultEnt.getRows())) {
                return;
            }
            List<String> ids = resultEnt.getRows().stream().map(row -> row.get("objectId").toString()).distinct().collect(Collectors.toList());
            pageInfo.setIds(ids);
        }
        if (tenantEnable) {
            pageInfo.setTenantId(TenantContext.getTenantId());
        }
        List<Map<String, Object>> beans = skyeyeBaseMapper.queryAutoProjectList(pageInfo);
        String serviceClassName = getServiceClassName();
        beans.forEach(bean -> {
            bean.put("serviceClassName", serviceClassName);
        });
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }
}
