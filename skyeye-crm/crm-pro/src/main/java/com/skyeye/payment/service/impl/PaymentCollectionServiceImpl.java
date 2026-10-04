/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.payment.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.contract.service.CrmContractService;
import com.skyeye.eve.flowable.classenum.FormSubType;
import com.skyeye.exception.CustomException;
import com.skyeye.payment.classenum.CrmPaymentCollectionAuthEnum;
import com.skyeye.payment.dao.PaymentCollectionDao;
import com.skyeye.payment.entity.PaymentCollection;
import com.skyeye.payment.service.PaymentCollectionService;
import com.skyeye.receivable.entity.Receivable;
import com.skyeye.receivable.service.ReceivableService;
import com.skyeye.rest.ifs.bizacct.service.IfsBizAcctEventService;
import com.skyeye.rest.ifs.receivepayment.service.IfsReceivePaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: PaymentCollectionServiceImpl
 * @Description: 回款服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/2 20:34
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "回款管理", groupName = "回款管理", flowable = true, teamAuth = true)
public class PaymentCollectionServiceImpl extends SkyeyeBusinessServiceImpl<PaymentCollectionDao, PaymentCollection> implements PaymentCollectionService {

    @Autowired
    private CrmContractService crmContractService;

    @Autowired
    private ReceivableService receivableService;

    @Autowired
    private IfsReceivePaymentService ifsReceivePaymentService;

    @Autowired
    private IfsBizAcctEventService ifsBizAcctEventService;

    @Override
    public Class getAuthEnumClass() {
        return CrmPaymentCollectionAuthEnum.class;
    }

    @Override
    public List<String> getAuthPermissionKeyList() {
        return Arrays.asList(CrmPaymentCollectionAuthEnum.ADD.getKey(), CrmPaymentCollectionAuthEnum.EDIT.getKey(), CrmPaymentCollectionAuthEnum.DELETE.getKey(),
                CrmPaymentCollectionAuthEnum.REVOKE.getKey(), CrmPaymentCollectionAuthEnum.INVALID.getKey(), CrmPaymentCollectionAuthEnum.SUBMIT_TO_APPROVAL.getKey(),
                CrmPaymentCollectionAuthEnum.LIST.getKey());
    }

    @Override
    public QueryWrapper<PaymentCollection> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<PaymentCollection> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(PaymentCollection::getObjectId), commonPageInfo.getObjectId());
        return queryWrapper;
    }

    @Override
    protected void validatorEntity(PaymentCollection entity) {
        super.validatorEntity(entity);
        if(StrUtil.isNotEmpty(entity.getReceivableId())){
            Receivable receivable = receivableService.selectById(entity.getReceivableId());
            double price = Double.parseDouble(receivable.getAmountPrice()) - Double.parseDouble(entity.getPrice()) - Double.parseDouble(receivable.getPaidPrice());
            if (price < CommonNumConstants.NUM_ZERO) {
                throw new CustomException("收款金额不能大于需收金额");
            }
        }
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        crmContractService.setMationForMap(beans, "contractId", "contractMation");
        receivableService.setMationForMap(beans, "receivableId", "receivableMation");
        return beans;
    }

    @Override
    public PaymentCollection selectById(String id) {
        PaymentCollection paymentCollection = super.selectById(id);
        paymentCollection.setName(paymentCollection.getOddNumber());
        // 合同信息
        crmContractService.setDataMation(paymentCollection, PaymentCollection::getContractId);
        receivableService.setDataMation(paymentCollection, PaymentCollection::getReceivableId);
        return paymentCollection;
    }

    @Override
    public List<PaymentCollection> selectByIds(String... ids) {
        List<PaymentCollection> paymentCollections = super.selectByIds(ids);
        List<PaymentCollection> bean = paymentCollections.stream().map(item -> {
            item.setName(item.getOddNumber());
            return item;
        }).collect(Collectors.toList());
        // 合同信息
        crmContractService.setDataMation(bean, PaymentCollection::getContractId);
        return bean;
    }

    @Override
    public void queryPaymentCollectionByContractId(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String contractId = map.get("contractId").toString();
        if (StrUtil.isEmpty(contractId)) {
            return;
        }
        QueryWrapper<PaymentCollection> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(PaymentCollection::getContractId), contractId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(PaymentCollection::getState), FlowableStateEnum.PASS.getKey());
        List<PaymentCollection> paymentCollections = list(queryWrapper);
        paymentCollections.forEach(paymentCollection -> {
            paymentCollection.setName(paymentCollection.getOddNumber());
        });
        outputObject.setBeans(paymentCollections);
        outputObject.settotal(paymentCollections.size());
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void approvalEndIsSuccess(PaymentCollection entity) {
        // 修改合同的回款金额
        crmContractService.updatePaymentPrice(entity.getContractId(), entity.getPrice());
        // 修改应收事项的已支付金额
        if(StrUtil.isNotEmpty(entity.getReceivableId())){
            receivableService.updateReceivablePaidPrice(entity.getReceivableId(), entity.getPrice());
        }
        // 远程调用新增收付款信息
        Map<String, Object> map = BeanUtil.beanToMap(entity);
        map.put("fromId",entity.getId());
        map.put("fromChildId",entity.getReceivableId());
        ifsReceivePaymentService.addIFsReceivePayment(map);
        Map<String, Object> acctEvent = new HashMap<>();
        acctEvent.put("eventType", "receipt");
        acctEvent.put("sourceType", "CRM_RECEIPT");
        acctEvent.put("sourceId", entity.getId());
        acctEvent.put("sourceNo", entity.getOddNumber());
        acctEvent.put("amount", entity.getPrice());
        acctEvent.put("customerId", entity.getObjectId());
        acctEvent.put("voucherDate", entity.getCollectionTime());
        acctEvent.put("summary", "客户回款-" + entity.getOddNumber());
        ifsBizAcctEventService.pushIfsBizAcctEvent(acctEvent);
    }

    @Override
    public void updateInvoicePrice(String id, String invoicePrice) {
        PaymentCollection paymentCollection = selectById(id);
        String newInvoicePrice = CalculationUtil.add(CommonNumConstants.NUM_TWO,
                StrUtil.isEmpty(paymentCollection.getInvoicePrice()) ? "0" : paymentCollection.getInvoicePrice(),
                invoicePrice);
        UpdateWrapper<PaymentCollection> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(PaymentCollection::getInvoicePrice), newInvoicePrice);
        update(updateWrapper);
        refreshCache(id);
        // 修改合同的开票金额
        crmContractService.updateInvoicePrice(paymentCollection.getContractId(), invoicePrice);
    }

}
