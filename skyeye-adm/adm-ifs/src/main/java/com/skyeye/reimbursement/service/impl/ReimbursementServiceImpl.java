/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.reimbursement.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.FlowableChildStateEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.organization.service.IDepmentService;
import com.skyeye.reimbursement.dao.ReimbursementDao;
import com.skyeye.reimbursement.entity.Reimbursement;
import com.skyeye.reimbursement.entity.ReimbursementChild;
import com.skyeye.reimbursement.service.ReimbursementChildService;
import com.skyeye.reimbursement.service.ReimbursementService;
import com.skyeye.rest.project.service.IProProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: ReimbursementServiceImpl
 * @Description: 报销订单服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/4 16:27
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "报销订单", groupName = "报销订单", flowable = true)
public class ReimbursementServiceImpl extends SkyeyeBusinessServiceImpl<ReimbursementDao, Reimbursement> implements ReimbursementService {

    @Autowired
    private ReimbursementChildService reimbursementChildService;

    @Autowired
    private IDepmentService iDepmentService;

    @Autowired
    private IProProjectService iProProjectService;

    @Override
    public QueryWrapper<Reimbursement> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<Reimbursement> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isNotEmpty(commonPageInfo.getObjectId())) {
            QueryWrapper<ReimbursementChild> childWrapper = new QueryWrapper<>();
            childWrapper.eq(MybatisPlusUtil.toColumns(ReimbursementChild::getProjectId), commonPageInfo.getObjectId());
            List<String> ids = reimbursementChildService.list(childWrapper).stream()
                .map(ReimbursementChild::getParentId)
                .filter(StrUtil::isNotEmpty)
                .distinct()
                .collect(Collectors.toList());
            if (CollectionUtil.isEmpty(ids)) {
                queryWrapper.eq(CommonConstants.ID, StrUtil.DASHED);
            } else {
                queryWrapper.in(CommonConstants.ID, ids);
            }
        }
        if (StrUtil.equals(commonPageInfo.getType(), "myCreate")) {
            // 我创建的
            queryWrapper.eq(MybatisPlusUtil.toColumns(Reimbursement::getCreateId), InputObject.getLogParamsStatic().get("id").toString());
        }
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        iDepmentService.setMationForMap(beans, "departmentId", "departmentMation");
        return beans;
    }

    @Override
    public void validatorEntity(Reimbursement entity) {
        super.validatorEntity(entity);
        entity.setPrice(reimbursementChildService.calcOrderAllTotalPrice(entity.getReimbursementChildList()));
    }

    @Override
    public void writePostpose(Reimbursement entity, String userId) {
        reimbursementChildService.saveLinkList(entity.getId(), entity.getReimbursementChildList());
        super.writePostpose(entity, userId);
    }

    @Override
    public void submitToApprovalPostpose(String id, String processInstanceId) {
        super.submitToApprovalPostpose(id, processInstanceId);
        reimbursementChildService.editStateByPId(id, FlowableChildStateEnum.IN_EXAMINE.getKey());
    }

    @Override
    public Reimbursement getDataFromDb(String id) {
        Reimbursement reimbursement = super.getDataFromDb(id);
        List<ReimbursementChild> reimbursementChildList = reimbursementChildService.selectByPId(reimbursement.getId());
        reimbursement.setReimbursementChildList(reimbursementChildList);
        return reimbursement;
    }

    @Override
    public Reimbursement selectById(String id) {
        Reimbursement reimbursement = super.selectById(id);
        iSysDictDataService.setDataMation(reimbursement, Reimbursement::getPayTypeId);
        iSysDictDataService.setDataMation(reimbursement.getReimbursementChildList(), ReimbursementChild::getReimburseProId);
        iProProjectService.setDataMation(reimbursement.getReimbursementChildList(), ReimbursementChild::getProjectId);
        return reimbursement;
    }

    @Override
    public void deletePostpose(String id) {
        reimbursementChildService.deleteByPId(id);
    }

    @Override
    public void revokePostpose(Reimbursement entity) {
        super.revokePostpose(entity);
        reimbursementChildService.editStateByPId(entity.getId(), FlowableChildStateEnum.DRAFT.getKey());
    }

    @Override
    protected void approvalEndIsSuccess(Reimbursement entity) {
        reimbursementChildService.editStateByPId(entity.getId(), FlowableChildStateEnum.ADEQUATE.getKey());
    }

    @Override
    protected void approvalEndIsFailed(Reimbursement entity) {
        reimbursementChildService.editStateByPId(entity.getId(), FlowableChildStateEnum.REJECT.getKey());
    }

    @Override
    public void queryCostAnalysis(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String year = params.get("year").toString();
        String month = (String) params.get("month");
        if (StrUtil.isNotEmpty(month)){
            int yearInt = Integer.parseInt(year);
            int monthInt = Integer.parseInt(month);
            String startPeriod=year + StrUtil.DASHED + month;
            String endPeriod = year + StrUtil.DASHED + month;
            if (monthInt == CommonNumConstants.NUM_ONE) {
                // 如果是1月，则上期是去年12月
                startPeriod = (yearInt - CommonNumConstants.NUM_ONE) + StrUtil.DASHED + CommonNumConstants.NUM_TWELVE;
                endPeriod = (yearInt - CommonNumConstants.NUM_ONE) + StrUtil.DASHED + CommonNumConstants.NUM_TWELVE;
            }
            List<Map<String, Object>> result =reimbursementChildService.queryReimbursementAnalysis(startPeriod, endPeriod);
            outputObject.setBeans(result);
        }else {
            String startPeriod = year + StrUtil.DASHED + CommonNumConstants.NUM_ZERO + CommonNumConstants.NUM_ONE; // 本期开始时间
            String endPeriod = year + StrUtil.DASHED + CommonNumConstants.NUM_TWELVE;  // 本期结束时间
            List<Map<String, Object>> result =reimbursementChildService.queryReimbursementAnalysis(startPeriod, endPeriod);
            outputObject.setBeans(result);
        }
    }
}
