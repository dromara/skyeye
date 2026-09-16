/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.invoice.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;

import com.skyeye.contract.service.SupplierContractService;
import com.skyeye.eve.service.IAreaService;

import com.skyeye.exception.CustomException;
import com.skyeye.invoice.classenum.ErpInvoiceAuthEnum;
import com.skyeye.invoice.dao.SupplierInvoiceDao;

import com.skyeye.invoice.entity.SupplierInvoice;


import com.skyeye.invoice.service.SupplierInvoiceHeaderService;
import com.skyeye.invoice.service.SupplierInvoiceService;

import com.skyeye.payment.service.PaymentService;
import com.skyeye.rest.ifs.receivepayment.service.IfsReceivePaymentService;
import com.skyeye.supplier.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: SupplierInvoiceServiceImpl
 * @Description: 发票服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/3 19:53
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "发票管理", groupName = "发票管理", teamAuth = true, flowable = true, orderApproval = true)
public class SupplierInvoiceServiceImpl extends SkyeyeBusinessServiceImpl<SupplierInvoiceDao, SupplierInvoice> implements SupplierInvoiceService {

    @Autowired
    private SupplierContractService supplierContractService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private SupplierInvoiceHeaderService supplierInvoiceHeaderService;

    @Autowired
    private IAreaService iAreaService;

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private IfsReceivePaymentService ifsReceivePaymentService;

    @Override
    public Class getAuthEnumClass() {
        return ErpInvoiceAuthEnum.class;
    }

    @Override
    public List<String> getAuthPermissionKeyList() {
        return Arrays.asList(ErpInvoiceAuthEnum.ADD.getKey(), ErpInvoiceAuthEnum.EDIT.getKey(), ErpInvoiceAuthEnum.DELETE.getKey(),
                ErpInvoiceAuthEnum.REVOKE.getKey(), ErpInvoiceAuthEnum.INVALID.getKey(), ErpInvoiceAuthEnum.SUBMIT_TO_APPROVAL.getKey(),
                ErpInvoiceAuthEnum.LIST.getKey());
    }

    @Override
    public QueryWrapper<SupplierInvoice> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<SupplierInvoice> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(SupplierInvoice::getObjectId), commonPageInfo.getObjectId());
        return queryWrapper;
    }

    @Override
    public void validatorEntity(SupplierInvoice entity) {
        super.validatorEntity(entity);
        // 校验开票日期不能大于当前日期但是包含今天
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, CommonNumConstants.NUM_ONE); // 加一天
        Date tomorrow = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.YYYY_MM_DD);
        String tomorrowStr = sdf.format(tomorrow);
        if (DateUtil.compareTime(tomorrowStr, entity.getInvoicTime(), DateUtil.YYYY_MM_DD)) {
            throw new CustomException("开票日期不能大于当前日期");
        }
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        supplierService.setMationForMap(beans, "objectId", "objectMation");
        supplierContractService.setMationForMap(beans, "contractId", "contractMation");
        paymentService.setMationForMap(beans, "paymentCollectionId", "paymentCollectionMation");
        supplierInvoiceHeaderService.setMationForMap(beans, "invoiceHeaderId", "invoiceHeaderMation");
        return beans;
    }

    @Override
    public SupplierInvoice selectById(String id) {
        SupplierInvoice invoice = super.selectById(id);
        // 合同信息
        supplierContractService.setDataMation(invoice, SupplierInvoice::getContractId);
        // 付款信息
        paymentService.setDataMation(invoice, SupplierInvoice::getPaymentCollectionId);
        // 发票抬头
        supplierInvoiceHeaderService.setDataMation(invoice, SupplierInvoice::getInvoiceHeaderId);
        supplierService.setDataMation(invoice, SupplierInvoice::getObjectId);
        iAreaService.setDataMation(invoice, SupplierInvoice::getProvinceId);
        iAreaService.setDataMation(invoice, SupplierInvoice::getCityId);
        iAreaService.setDataMation(invoice, SupplierInvoice::getAreaId);
        iAreaService.setDataMation(invoice, SupplierInvoice::getTownshipId);
        return invoice;
    }

    @Override
    public void approvalEndIsSuccess(SupplierInvoice entity) {
        // 修改付款的开票金额
        paymentService.updateInvoicePrice(entity.getPaymentCollectionId(), entity.getPrice());
        // 远程调用修改——ifs管理--收付款管理 已开发票金额
        Map<String, Object> map = new HashMap<>();
        map.put("fromId", entity.getPaymentCollectionId());
        map.put("invoicePrice", entity.getPrice());
        ifsReceivePaymentService.updateReceivePayment(map);
    }

    @Override
    public void queryAllInvoiceList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<SupplierInvoice> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SupplierInvoice::getState), FlowableStateEnum.PASS.getKey());
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(SupplierInvoice::getCreateTime));
        List<SupplierInvoice> bean = list(queryWrapper);
        // 合同信息
        supplierContractService.setDataMation(bean, SupplierInvoice::getContractId);
        // 回款信息
        paymentService.setDataMation(bean, SupplierInvoice::getPaymentCollectionId);
        // 发票抬头
        supplierInvoiceHeaderService.setDataMation(bean, SupplierInvoice::getInvoiceHeaderId);
        bean.forEach(item -> {
            item.setServiceClassName(getServiceClassName());
        });
        outputObject.setBeans(bean);
        outputObject.settotal(page.getTotal());
    }

    @Override
    public void queryInvoiceStatistics(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String year = (String) params.get("year");
        String month = (String) params.get("month");
        if (StrUtil.isNotEmpty(month)) {
            int yearInt = Integer.parseInt(year);
            int monthInt = Integer.parseInt(month);
            String startPeriod = year + StrUtil.DASHED + month;
            String endPeriod = year + StrUtil.DASHED + month;
            if (monthInt == CommonNumConstants.NUM_ONE) {
                // 如果是1月，则上期是去年12月
                startPeriod = (yearInt - CommonNumConstants.NUM_ONE) + StrUtil.DASHED + CommonNumConstants.NUM_TWELVE;
                endPeriod = (yearInt - CommonNumConstants.NUM_ONE) + StrUtil.DASHED + CommonNumConstants.NUM_TWELVE;
            }
            List<Map<String, Object>> beans = getBeans(startPeriod, endPeriod);
            outputObject.setBeans(beans);

        } else {
            String startPeriod = year + StrUtil.DASHED + CommonNumConstants.NUM_ZERO + CommonNumConstants.NUM_ONE; // 本期开始时间
            String endPeriod = year + StrUtil.DASHED + CommonNumConstants.NUM_TWELVE;  // 本期结束时间
            List<Map<String, Object>> beans = getBeans(startPeriod, endPeriod);
            outputObject.setBeans(beans);
        }
    }

    private List<Map<String, Object>> getBeans(String startPeriod, String endPeriod) {
        QueryWrapper<SupplierInvoice> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SupplierInvoice::getState), FlowableStateEnum.PASS.getKey());
        queryWrapper.apply("date_format(" + MybatisPlusUtil.toColumns(SupplierInvoice::getInvoicTime) + ", '%Y-%m') >= {0}", startPeriod)
                .apply("date_format(" + MybatisPlusUtil.toColumns(SupplierInvoice::getInvoicTime) + ", '%Y-%m') <= {0}", endPeriod);
        List<SupplierInvoice> bean = list(queryWrapper);
        List<Map<String, Object>> beans = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(bean)) {
            Map<String, List<SupplierInvoice>> map = bean.stream().collect(Collectors.groupingBy(SupplierInvoice::getTypeId));
            for (Map.Entry<String, List<SupplierInvoice>> entry : map.entrySet()) {
                Map<String, Object> result = new HashMap<>();
                result.put("typeId", entry.getKey());
                result.put("count", entry.getValue().size());
                String price = String.valueOf(CommonNumConstants.NUM_ZERO);
                for (SupplierInvoice invoice : entry.getValue()) {
                    price = CalculationUtil.add(CommonNumConstants.NUM_TWO,
                            StrUtil.isEmpty(invoice.getPrice()) ? "0" : invoice.getPrice(),
                            price);
                }
                result.put("price", price);
                beans.add(result);
            }
        }
        return beans;
    }

    @Override
    public void queryAllInvoicesLists(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<SupplierInvoice> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(SupplierInvoice::getCreateTime));
        if (StrUtil.isNotEmpty(commonPageInfo.getKeyword())) {
            queryWrapper.like(MybatisPlusUtil.toColumns(SupplierInvoice::getOddNumber), commonPageInfo.getKeyword());
        }
        List<SupplierInvoice> bean = list(queryWrapper);
        // 合同信息
        supplierContractService.setDataMation(bean, SupplierInvoice::getContractId);
        // 回款信息
        paymentService.setDataMation(bean, SupplierInvoice::getPaymentCollectionId);
        // 发票抬头
        supplierInvoiceHeaderService.setDataMation(bean, SupplierInvoice::getInvoiceHeaderId);
        iAuthUserService.setName(bean, "lastUpdateId", "lastUpdateName");
        iAuthUserService.setName(bean, "createId", "createName");
        bean.forEach(item -> {
            item.setServiceClassName(getServiceClassName());
        });
        outputObject.setBeans(bean);
        outputObject.settotal(page.getTotal());
    }
}
