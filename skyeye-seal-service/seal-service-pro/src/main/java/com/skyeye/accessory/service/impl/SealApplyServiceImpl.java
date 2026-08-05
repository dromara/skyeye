/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.accessory.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.common.base.Joiner;
import com.skyeye.accessory.classenum.UserStockPutOutType;
import com.skyeye.accessory.dao.SealApplyDao;
import com.skyeye.accessory.entity.ApplyLink;
import com.skyeye.accessory.entity.SealApply;
import com.skyeye.accessory.entity.SealApplyChangeStock;
import com.skyeye.accessory.entity.SealApplyCode;
import com.skyeye.accessory.service.ApplyLinkService;
import com.skyeye.accessory.service.SealApplyCodeService;
import com.skyeye.accessory.service.SealApplyService;
import com.skyeye.accessory.service.ServiceUserStockService;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.client.ExecuteFeignClient;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.FlowableChildStateEnum;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.erp.service.IMaterialNormsService;
import com.skyeye.erp.service.IMaterialService;
import com.skyeye.exception.CustomException;
import com.skyeye.rest.depot.rest.IERPOrderRest;
import com.skyeye.rest.depot.service.IDepotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: SealApplyServiceImpl
 * @Description: 配件申领单管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2022/1/11 22:42
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "配件申领单", groupName = "配件申领单管理", flowable = true)
public class SealApplyServiceImpl extends SkyeyeBusinessServiceImpl<SealApplyDao, SealApply> implements SealApplyService {

    @Autowired
    private ApplyLinkService applyLinkService;

    @Autowired
    private ServiceUserStockService sealSeServiceMyPartsService;

    @Autowired
    private IMaterialService iMaterialService;

    @Autowired
    private IMaterialNormsService iMaterialNormsService;

    @Autowired
    private IDepotService iDepotService;

    @Autowired
    private IERPOrderRest ierpOrderRest;

    @Autowired
    private SealApplyCodeService sealApplyCodeService;

    @Override
    public QueryWrapper<SealApply> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<SealApply> queryWrapper = super.getQueryWrapper(commonPageInfo);
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SealApply::getCreateId), userId);
        return queryWrapper;
    }

    @Override
    public void validatorEntity(SealApply entity) {
        String allPrice = applyLinkService.calcOrderAllTotalPrice(entity.getApplyLinkList());
        entity.setAllPrice(allPrice);
        entity.setOtherState(CommonNumConstants.NUM_TWO);
    }

    @Override
    public void writePostpose(SealApply entity, String userId) {
        applyLinkService.saveLinkList(entity.getId(), entity.getApplyLinkList());
        super.writePostpose(entity, userId);
    }

    @Override
    protected void deletePreExecution(SealApply entity) {
        Boolean subType = checkState(entity);
        if (!subType) {
            throw new CustomException("该数据状态已改变，删除失败.");
        }
    }

    @Override
    public void submitToApprovalPostpose(String id, String processInstanceId) {
        super.submitToApprovalPostpose(id, processInstanceId);
        applyLinkService.editStateByPId(id, FlowableChildStateEnum.IN_EXAMINE.getKey());
    }

    @Override
    public SealApply getDataFromDb(String id) {
        SealApply sealApply = super.getDataFromDb(id);
        List<ApplyLink> applyLinkList = applyLinkService.selectByPId(sealApply.getId());
        sealApply.setApplyLinkList(applyLinkList);
        return sealApply;
    }

    @Override
    public SealApply selectById(String id) {
        SealApply sealApply = super.selectById(id);
        // 产品信息
        List<String> materialIds = sealApply.getApplyLinkList().stream().map(ApplyLink::getMaterialId).collect(Collectors.toList());
        Map<String, Map<String, Object>> materialMap = iMaterialService.queryDataMationForMapByIds(Joiner.on(CommonCharConstants.COMMA_MARK).join(materialIds));
        // 产品规格信息
        List<String> normsIds = sealApply.getApplyLinkList().stream().map(ApplyLink::getNormsId).collect(Collectors.toList());
        Map<String, Map<String, Object>> normsMap = iMaterialNormsService.queryDataMationForMapByIds(Joiner.on(CommonCharConstants.COMMA_MARK).join(normsIds));
        // 仓库信息
        List<String> depotIds = sealApply.getApplyLinkList().stream().map(ApplyLink::getDepotId).collect(Collectors.toList());
        Map<String, Map<String, Object>> depotMap = iDepotService.queryDataMationForMapByIds(Joiner.on(CommonCharConstants.COMMA_MARK).join(depotIds));
        sealApply.getApplyLinkList().forEach(bean -> {
            bean.setMaterialMation(materialMap.get(bean.getMaterialId()));
            bean.setNormsMation(normsMap.get(bean.getNormsId()));
            bean.setDepotMation(depotMap.get(bean.getDepotId()));
        });
        return sealApply;
    }

    @Override
    public void revokePostpose(SealApply entity) {
        super.revokePostpose(entity);
        applyLinkService.editStateByPId(entity.getId(), FlowableChildStateEnum.DRAFT.getKey());
    }

    @Override
    public void approvalEndIsSuccess(SealApply entity) {
        SealApply sealApply = selectById(entity.getId());
        // 修改子单据状态
        applyLinkService.editStateByPId(entity.getId(), FlowableChildStateEnum.ADEQUATE.getKey());
        // 保存订单到erp
        saveOrderToErp(sealApply);
    }

    private void saveOrderToErp(SealApply sealApply) {
        Map<String, Object> params = BeanUtil.beanToMap(sealApply);
        params.put("state", FlowableStateEnum.PASS.getKey());
        params.put("type", CommonNumConstants.NUM_TWO);
        params.put("idKey", getServiceClassName());
        params.put("operTime", sealApply.getApplyTime());
        params.put("discount", CommonNumConstants.NUM_ZERO);
        params.put("discountMoney", CommonNumConstants.NUM_ZERO);
        params.put("totalPrice", sealApply.getAllPrice());
        List<Map<String, Object>> erpOrderItemList = new ArrayList<>();
        sealApply.getApplyLinkList().forEach(applyLink -> {
            Map<String, Object> erpOrderItem = BeanUtil.beanToMap(applyLink);
            erpOrderItem.put("taxMoney", CommonNumConstants.NUM_ZERO);
            erpOrderItem.put("taxUnitPrice", CommonNumConstants.NUM_ZERO);
            erpOrderItem.put("taxLastMoney", CommonNumConstants.NUM_ZERO);    // 规格单位
            erpOrderItem.put("mType", CommonNumConstants.NUM_ZERO);
            erpOrderItem.put("state", FlowableChildStateEnum.ADEQUATE.getKey());
            erpOrderItemList.add(erpOrderItem);
        });
        params.put("erpOrderItemList", JSONUtil.toJsonStr(erpOrderItemList));
        ExecuteFeignClient.get(() -> ierpOrderRest.createApprovelSuccessOrder(params));
    }

    @Override
    public void approvalEndIsFailed(SealApply entity) {
        applyLinkService.editStateByPId(entity.getId(), FlowableChildStateEnum.REJECT.getKey());
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void editSealApplyOtherState(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        String otherState = params.get("otherState").toString();
        UpdateWrapper<SealApply> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(SealApply::getOtherState), otherState);
        update(updateWrapper);
        refreshCache(id);
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void editSealApplyOutNum(InputObject inputObject, OutputObject outputObject) {
        SealApplyChangeStock sealApplyChangeStock = inputObject.getParams(SealApplyChangeStock.class);
        List<SealApplyCode> sealApplyCodeList = new ArrayList<>();
        sealApplyChangeStock.getApplyLinkList().forEach(applyLink -> {
            // 修改我的库存数量
            sealSeServiceMyPartsService.editMaterialNormsUserStock(sealApplyChangeStock.getCreateId(), applyLink.getMaterialId(), applyLink.getNormsId(),
                applyLink.getOperNumber(), UserStockPutOutType.PUT.getKey());
            // 构建条形码信息
            if (CollectionUtil.isNotEmpty(applyLink.getNormsCodeList())) {
                applyLink.getNormsCodeList().forEach(normsCode -> {
                    SealApplyCode sealApplyCode = new SealApplyCode();
                    sealApplyCode.setNormsCode(normsCode);
                    sealApplyCode.setMaterialId(applyLink.getMaterialId());
                    sealApplyCode.setNormsId(applyLink.getNormsId());
                    sealApplyCodeList.add(sealApplyCode);
                });
            }
        });
        // 保存配件码信息
        sealApplyCodeService.saveList(sealApplyChangeStock.getId(), sealApplyCodeList);
    }
}
