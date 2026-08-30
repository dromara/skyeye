/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.service.impl;

import cn.hutool.core.util.StrUtil;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.classenum.ErpOrderStateEnum;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.dao.ErpPageDao;
import com.skyeye.depot.classenum.DepotOutFromType;
import com.skyeye.depot.classenum.DepotOutState;
import com.skyeye.depot.classenum.DepotPutFromType;
import com.skyeye.depot.classenum.DepotPutState;
import com.skyeye.depot.service.impl.DepotOutServiceImpl;
import com.skyeye.depot.service.impl.DepotPutServiceImpl;
import com.skyeye.pick.service.impl.PatchMaterialServiceImpl;
import com.skyeye.pick.service.impl.PatchOutLetServiceImpl;
import com.skyeye.pick.service.impl.RequisitionMaterialServiceImpl;
import com.skyeye.pick.service.impl.RequisitionOutLetServiceImpl;
import com.skyeye.pick.service.impl.ReturnMaterialServiceImpl;
import com.skyeye.pick.service.impl.ReturnPutServiceImpl;
import com.skyeye.product.service.impl.ProductLeadOutStockServiceImpl;
import com.skyeye.product.service.impl.ProductReturnInStockServiceImpl;
import com.skyeye.purchase.service.impl.PurchaseDeliveryServiceImpl;
import com.skyeye.purchase.service.impl.PurchaseExchangesServiceImpl;
import com.skyeye.purchase.service.impl.PurchaseOrderServiceImpl;
import com.skyeye.purchase.service.impl.PurchasePutServiceImpl;
import com.skyeye.purchase.service.impl.PurchaseReturnsServiceImpl;
import com.skyeye.retail.service.impl.RetailOutLetServiceImpl;
import com.skyeye.retail.service.impl.RetailReturnsServiceImpl;
import com.skyeye.seal.service.impl.SalesExchangesServiceImpl;
import com.skyeye.seal.service.impl.SalesOrderServiceImpl;
import com.skyeye.seal.service.impl.SalesOutLetServiceImpl;
import com.skyeye.seal.service.impl.SalesReturnsServiceImpl;
import com.skyeye.service.ErpPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: ErpPageServiceImpl
 * @Description: ERP统计模块服务层--强隔离
 * @author: skyeye云系列--卫志强
 * @date: 2023/5/2 11:31
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "AI角色", groupName = "AI角色", manageShow = false)
public class ErpPageServiceImpl implements ErpPageService {

    @Autowired
    private ErpPageDao erpPageDao;

    @Value("${skyeye.tenant.enable}")
    private boolean tenantEnable;

    @Override
    @IgnoreTenant
    public void queryFourTypeMoneyList(InputObject inputObject, OutputObject outputObject) {
        List<String> states = Arrays.asList(FlowableStateEnum.PASS.getKey(), ErpOrderStateEnum.COMPLETED.getKey());
        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
        // 1.获取本月累计销售，当前月已审核通过的销售订单金额
        String salesMoney = erpPageDao.queryThisMonthErpOrder(SalesOrderServiceImpl.class.getName(), states, tenantId);
        // 2.获取本月累计零售，当前月已审核通过的零售订单金额
        String retailMoney = erpPageDao.queryThisMonthErpOrder(RetailOutLetServiceImpl.class.getName(), states, tenantId);
        // 3.获取本月累计采购，当前月已审核通过的采购订单金额
        String purchaseMoney = erpPageDao.queryThisMonthErpOrder(PurchaseOrderServiceImpl.class.getName(), states, tenantId);
        // 4.本月利润（已审核通过），零售订单金额 + 销售订单金额 - 采购订单金额
        String profitMoney = CalculationUtil.subtract(CalculationUtil.add(salesMoney, retailMoney), purchaseMoney);
        Map<String, Object> map = new HashMap<>();
        map.put("salesMoney", salesMoney);
        map.put("retailMoney", retailMoney);
        map.put("purchaseMoney", purchaseMoney);
        map.put("profitMoney", profitMoney);
        outputObject.setBean(map);
    }

    @Override
    @IgnoreTenant
    public void querySixMonthPurchaseMoneyList(InputObject inputObject, OutputObject outputObject) {
        List<String> states = Arrays.asList(FlowableStateEnum.PASS.getKey(), ErpOrderStateEnum.COMPLETED.getKey());
        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
        List<Map<String, Object>> beans = erpPageDao.querySixMonthOrderMoneyList(PurchaseOrderServiceImpl.class.getName(), states, tenantId);
        outputObject.setBeans(beans);
    }

    @Override
    @IgnoreTenant
    public void querySixMonthSealsMoneyList(InputObject inputObject, OutputObject outputObject) {
        List<String> states = Arrays.asList(FlowableStateEnum.PASS.getKey(), ErpOrderStateEnum.COMPLETED.getKey());
        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
        List<Map<String, Object>> beans = erpPageDao.querySixMonthOrderMoneyList(SalesOrderServiceImpl.class.getName(), states, tenantId);
        outputObject.setBeans(beans);
    }

    @Override
    @IgnoreTenant
    public void queryTwelveMonthProfitMoneyList(InputObject inputObject, OutputObject outputObject) {
        List<String> states = Arrays.asList(FlowableStateEnum.PASS.getKey(), ErpOrderStateEnum.COMPLETED.getKey());
        List<String> idKeys = Arrays.asList(SalesOrderServiceImpl.class.getName(),
            PurchaseOrderServiceImpl.class.getName(),
            RetailOutLetServiceImpl.class.getName());
        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
        List<Map<String, Object>> beans = erpPageDao.queryTwelveMonthProfitMoneyList(idKeys, states, tenantId);
        outputObject.setBeans(beans);
    }

    /**
     * bean 的 key 为流程图节点 code，value 为当前租户下的笔数：
     * A01 采购申请单总笔数
     * A02 采购订单总笔数
     * A03 到货单总笔数
     * A04 质检单总笔数
     * A05 采购入库单总笔数
     * A06 采购退货单总笔数
     * A07 采购换货单总笔数
     * B01 销售订单总笔数
     * B02 销售出库单总笔数
     * B03 销售退货单总笔数
     * B04 销售换货单总笔数
     * C01 零售出库单总笔数
     * C02 零售退货单总笔数
     * D01 借出申请单总笔数
     * D02 借出出库单总笔数
     * D03 归还申请单总笔数
     * D04 归还入库单总笔数
     * E01 待出库单据笔数：来源单据审批通过/部分完成/已完成，且出库状态为待出库或部分出库
     * E02 仓库出库单总笔数
     * E03 待入库单据笔数：来源单据审批通过/部分完成/已完成，且入库状态为待入库或部分入库
     * E04 仓库入库单总笔数
     * G01 出货计划单总笔数
     * G02 生产计划单总笔数
     * G03 加工单总笔数
     * G04 车间任务总笔数
     * I01 领料单总笔数
     * I02 领料出库单总笔数
     * I03 补料单总笔数
     * I04 补料出库单总笔数
     * I05 退料单总笔数
     * I06 退料入库单总笔数
     */
    @Override
    @IgnoreTenant
    public void queryProcessFlowCount(InputObject inputObject, OutputObject outputObject) {
        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
        Map<String, Object> params = new HashMap<>();
        params.put("tenantId", tenantId);
        params.put("purchaseOrderIdKey", PurchaseOrderServiceImpl.class.getName());
        params.put("purchaseDeliveryIdKey", PurchaseDeliveryServiceImpl.class.getName());
        params.put("purchasePutIdKey", PurchasePutServiceImpl.class.getName());
        params.put("purchaseReturnsIdKey", PurchaseReturnsServiceImpl.class.getName());
        params.put("purchaseExchangesIdKey", PurchaseExchangesServiceImpl.class.getName());
        params.put("salesOrderIdKey", SalesOrderServiceImpl.class.getName());
        params.put("salesOutLetIdKey", SalesOutLetServiceImpl.class.getName());
        params.put("salesReturnsIdKey", SalesReturnsServiceImpl.class.getName());
        params.put("salesExchangesIdKey", SalesExchangesServiceImpl.class.getName());
        params.put("retailOutLetIdKey", RetailOutLetServiceImpl.class.getName());
        params.put("retailReturnsIdKey", RetailReturnsServiceImpl.class.getName());
        params.put("productLeadOutStockIdKey", ProductLeadOutStockServiceImpl.class.getName());
        params.put("productReturnInStockIdKey", ProductReturnInStockServiceImpl.class.getName());
        params.put("depotOutIdKey", DepotOutServiceImpl.class.getName());
        params.put("depotPutIdKey", DepotPutServiceImpl.class.getName());
        params.put("requisitionMaterialIdKey", RequisitionMaterialServiceImpl.class.getName());
        params.put("requisitionOutLetIdKey", RequisitionOutLetServiceImpl.class.getName());
        params.put("patchMaterialIdKey", PatchMaterialServiceImpl.class.getName());
        params.put("patchOutLetIdKey", PatchOutLetServiceImpl.class.getName());
        params.put("returnMaterialIdKey", ReturnMaterialServiceImpl.class.getName());
        params.put("returnPutIdKey", ReturnPutServiceImpl.class.getName());
        params.put("outFromIdKeys", DepotOutFromType.getAllIdKeys());
        params.put("putFromIdKeys", DepotPutFromType.getAllIdKeys());
        params.put("waitOutOtherStates", Arrays.asList(DepotOutState.NEED_OUT.getKey(), DepotOutState.PARTIAL_OUT.getKey()));
        params.put("waitPutOtherStates", Arrays.asList(DepotPutState.NEED_PUT.getKey(), DepotPutState.PARTIAL_PUT.getKey()));
        params.put("waitDocStates", Arrays.asList(FlowableStateEnum.PASS.getKey(),
            ErpOrderStateEnum.PARTIALLY_COMPLETED.getKey(),
            ErpOrderStateEnum.COMPLETED.getKey()));

        List<Map<String, Object>> rows = erpPageDao.queryProcessFlowCount(params);
        Map<String, Object> bean = new HashMap<>();
        if (rows != null) {
            for (Map<String, Object> row : rows) {
                Object code = row.get("code");
                if (code == null) {
                    code = row.get("CODE");
                }
                if (code == null) {
                    continue;
                }
                Object cnt = row.get("cnt");
                if (cnt == null) {
                    cnt = row.get("CNT");
                }
                bean.put(String.valueOf(code), cnt == null ? 0 : Integer.parseInt(String.valueOf(cnt)));
            }
        }
        outputObject.setBean(bean);
    }

}
