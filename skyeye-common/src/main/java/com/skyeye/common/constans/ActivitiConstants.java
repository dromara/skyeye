/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.common.constans;

import com.skyeye.common.util.ToolUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 *
 * @ClassName: ActivitiConstants
 * @Description: 工作流模块常量类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/16 23:04
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class ActivitiConstants {

    public static final String APPROVAL_PASS = "pass";

    public static final String APPROVAL_NO_PASS = "nopass";

    /**
     * 工作流用户表格对象
     */
    public static enum ActivitiUserElement {
        ROW_INDEX("rowIndex", null, "序号", "ro", "center", false, false,
                false, false, "eq", "70", "", false, true,
                "#rspan", true, true),
        IS_SELECTED("isSelected", null, "选择", "ro", "center", false, false,
                false, false, "eq", "50", "fnRenderSelectUser", false, true,
                "#rspan", true, true),
        ID("id", null, "用户ID", "ro", "center", false, false,
                false, false, "eq", "100", "", false, true,
                "#rspan", true, true),
        FIRST_NAME("firstName", null, "用户名", "ro", "center", false, false,
                false, false, "eq", "120", "", false, true,
                "#rspan", true, true),
        LAST_NAME("lastName", null, "登录名", "ro", "center", false, false,
                false, false, "eq", "120", "", false, true,
                "#rspan", true, true),
        EMAIL("email", null, "邮箱", "ro", "center", false, false,
                false, false, "eq", "120", "", false, true,
                "#rspan", true, true),
        NAME("name", null, "用户组", "ro", "center", false, false,
                false, false, "eq", "120", "", false, true,
                "#rspan", true, true);

        private String key;
        private String id;
        private String header;
        private String type;
        private String align;
        private boolean allowSort;
        private boolean allowSearch;
        private boolean hidden;
        private boolean enableTooltip;
        private String operator;
        private String width;
        private String fnRender;
        private boolean isServerCondition;
        private boolean isExport;
        private String subHeader;
        private boolean isQuote;
        private boolean isImport;

        ActivitiUserElement(String key, String id, String header, String type, String align, boolean allowSort, boolean allowSearch,
                            boolean hidden, boolean enableTooltip, String operator, String width, String fnRender, boolean isServerCondition, boolean isExport,
                            String subHeader, boolean isQuote, boolean isImport) {
            this.key = key;
            this.id = id;
            this.header = header;
            this.type = type;
            this.align = align;
            this.allowSort = allowSort;
            this.allowSearch = allowSearch;
            this.hidden = hidden;
            this.enableTooltip = enableTooltip;
            this.operator = operator;
            this.width = width;
            this.fnRender = fnRender;
            this.isServerCondition = isServerCondition;
            this.isExport = isExport;
            this.subHeader = subHeader;
            this.isQuote = isQuote;
            this.isImport = isImport;
        }

        public String getKey() {
            return key;
        }

        public String getId() {
            return id;
        }

        public String getHeader() {
            return header;
        }

        public String getType() {
            return type;
        }

        public String getAlign() {
            return align;
        }

        public boolean getAllowSort() {
            return allowSort;
        }

        public boolean getAllowSearch() {
            return allowSearch;
        }

        public boolean getHidden() {
            return hidden;
        }

        public boolean getEnableTooltip() {
            return enableTooltip;
        }

        public String getOperator() {
            return operator;
        }

        public String getWidth() {
            return width;
        }

        public String getFnRender() {
            return fnRender;
        }

        public boolean getServerCondition() {
            return isServerCondition;
        }

        public boolean getExport() {
            return isExport;
        }

        public String getSubHeader() {
            return subHeader;
        }

        public boolean getQuote() {
            return isQuote;
        }

        public boolean getImport() {
            return isImport;
        }
    }

    public static List<Map<String, Object>> getAllList(){
        List<Map<String, Object>> beans = new ArrayList<>();
        for (ActivitiUserElement value : ActivitiUserElement.values()){
            Map<String, Object> bean = ToolUtil.javaBean2Map(value);
            beans.add(bean);
        }
        return beans;
    }

    public static Map<String, Object> getMap2PointKey(String key){
        for (ActivitiUserElement value : ActivitiUserElement.values()){
            if(key.equals(value.getKey())){
                return ToolUtil.javaBean2Map(value);
            }
        }
        return null;
    }

    public static List<Map<String, Object>> getPointKeyList(String... keys){
        List<Map<String, Object>> beans = new ArrayList<>();
        for (ActivitiUserElement value : ActivitiUserElement.values()){
            if(Arrays.asList(keys).contains(value.getKey())){
                Map<String, Object> bean = ToolUtil.javaBean2Map(value);
                beans.add(bean);
            }
        }
        return beans;
    }

    public static List<Map<String, Object>> getActivitiUserColumnList() {
        return getPointKeyList("rowIndex", "isSelected", "id", "firstName", "lastName", "email");
    }

    public static Map<String, Object> getActivitiUserColumnMap() {
        Map<String, Object> bean = new HashMap<>();
        bean.put("rowIndex", getMap2PointKey("rowIndex"));
        bean.put("isSelected", getMap2PointKey("isSelected"));
        bean.put("id", getMap2PointKey("id"));
        bean.put("firstName", getMap2PointKey("firstName"));
        bean.put("lastName", getMap2PointKey("lastName"));
        bean.put("email", getMap2PointKey("email"));
        return bean;
    }

    public static List<Map<String, Object>> getActivitiGroupColumnList() {
        return getPointKeyList("rowIndex", "isSelected", "id", "name");
    }

    public static Map<String, Object> getActivitiGroupColumnMap() {
        Map<String, Object> bean = new HashMap<>();
        bean.put("rowIndex", getMap2PointKey("rowIndex"));
        bean.put("isSelected", getMap2PointKey("isSelected"));
        bean.put("id", getMap2PointKey("id"));
        bean.put("name", getMap2PointKey("name"));
        return bean;
    }

    public static List<Map<String, Object>> getActivitiUserColumnListByGroupId() {
        return getPointKeyList("rowIndex", "firstName", "lastName", "email");
    }

    public static Map<String, Object> getActivitiUserColumnMapByGroupId() {
        Map<String, Object> bean = new HashMap<>();
        bean.put("rowIndex", getMap2PointKey("rowIndex"));
        bean.put("firstName", getMap2PointKey("firstName"));
        bean.put("lastName", getMap2PointKey("lastName"));
        bean.put("email", getMap2PointKey("email"));
        return bean;
    }

    public static enum ActivitiObjectType {

        ACTIVITI_ASSET_USE_PAGE("资产领用", "../../tpl/assetManageUse/assetManageUseAdd.html",
                "com.skyeye.eve.activitifactory.AssetUseActivitiFactory", "com.json.entity.AssetUseEntity"),
        ACTIVITI_ASSET_PURCHAES_PAGE("资产采购", "../../tpl/assetManagePurchase/assetManagePurchaseAdd.html",
                "com.skyeye.eve.activitifactory.AssetPurchaseFactory", "com.json.entity.AssetPurchaseEntity"),
        ACTIVITI_ASSET_RETURN_PAGE("资产归还", "../../tpl/assetManageReturn/assetManageReturnAdd.html",
                "com.skyeye.eve.activitifactory.AssetReturnFactory", "com.json.entity.AssetReturnEntity"),

        ACTIVITI_ASSETARTICLES_USE_PAGE("用品领用", "../../tpl/assetArticlesUse/assetArticlesUseAdd.html",
                "com.skyeye.eve.activitifactory.AssetArticlesUseFactory", "com.json.entity.AssetArticlesUseEntity"),
        ACTIVITI_ASSETARTICLES_PURCHASE_PAGE("用品采购", "../../tpl/assetArticlesPurchase/assetArticlesPurchaseAdd.html",
                "com.skyeye.eve.activitifactory.AssetArticlesPurchaseFactory", "com.json.entity.AssetArticlesPurchaseEntity"),

        ACTIVITI_SEAL_USE_PAGE("印章借用", "../../tpl/sealManageBorrow/sealManageBorrowAdd.html",
                "com.skyeye.eve.activitifactory.SealBorrowFactory", "com.json.entity.SealBorrowEntity"),
        ACTIVITI_SEAL_REVERT_PAGE("印章归还", "../../tpl/sealManageRevert/sealManageRevertAdd.html",
                "com.skyeye.eve.activitifactory.SealRevertFactory", "com.json.entity.SealRevertEntity"),

        ACTIVITI_LICENCE_USE_PAGE("证照借用", "../../tpl/licenceManageBorrow/licenceManageBorrowAdd.html",
                "com.skyeye.eve.activitifactory.LicenceBorrowFactory", "com.json.entity.LicenceBorrowEntity"),
        ACTIVITI_LICENCE_REVERT_PAGE("证照归还", "../../tpl/licenceManageRevert/licenceManageRevertAdd.html",
                "com.skyeye.eve.activitifactory.LicenceRevertFactory", "com.json.entity.LicenceRevertEntity"),

        ACTIVITI_CONFERENCEROOM_USE_PAGE("会议室预定", "../../tpl/conFerenceRoomReserve/conFerenceRoomReserveAdd.html",
                 "com.skyeye.eve.activitifactory.ConferenceRoomReserveFactory", "com.json.entity.ConferenceRoomReserveEntity"),

        ACTIVITI_VEHICLE_USE_PAGE("用车申请", "../../tpl/vehicleManageUse/vehicleManageUseAdd.html",
                 "com.skyeye.eve.activitifactory.VehicleUseFactory", "com.json.entity.VehicleUseEntity"),

        PRO_TASK_PAGE("项目任务申请", "../../tpl/protask/protaskadd.html",
                 "com.skyeye.activitifactory.ProTaskFactory", "com.json.entity.ProTaskEntity"),
        PRO_PROJECT_PAGE("项目立项申请", "../../tpl/proproject/proprojectadd.html",
                 "com.skyeye.activitifactory.ProProjectFactory", "com.json.entity.ProProjectEntity"),
        PRO_WORKLOAD_PAGE("项目工作量申请", "../../tpl/proworkload/proworkloadadd.html",
                 "com.skyeye.activitifactory.ProWorkloadFactory", "com.json.entity.ProWorkloadEntity"),
        PRO_FILE_PAGE("项目文档申请", "../../tpl/profile/profileadd.html",
                 "com.skyeye.activitifactory.ProFileFactory", "com.json.entity.ProFileEntity"),
        PRO_COST_EXPENSE_PAGE("项目费用报销申请", "../../tpl/procostexpense/procostexpenseadd.html",
                 "com.skyeye.activitifactory.ProCostExpenseFactory", "com.json.entity.ProCostExpenseEntity"),

        CHECK_WORK_BUSINESS_TRIP("出差申请", "../../tpl/checkWorkBusinessTrip/checkWorkBusinessTripAdd.html",
                 "com.skyeye.eve.activitifactory.CheckWorkBusinessTripFactory", "com.json.entity.CheckWorkBusinessTripEntity"),
        CHECK_WORK_CANCEL_LEAVE_PAGE("销假申请", "../../tpl/checkWorkCancelLeave/checkWorkCancelLeaveAdd.html",
                 "com.skyeye.eve.activitifactory.CheckWorkCancelLeaveFactory", "com.json.entity.CheckWorkCancelLeaveEntity"),
        CHECK_WORK_LEAVE_PAGE("请假申请", "../../tpl/checkWorkLeave/checkWorkLeaveAdd.html",
                 "com.skyeye.eve.activitifactory.CheckWorkLeaveFactory", "com.json.entity.CheckWorkLeaveEntity"),
        CHECK_WORK_OVERTIME_PAGE("加班申请", "../../tpl/checkWorkOvertime/checkWorkOvertimeAdd.html",
                 "com.skyeye.eve.activitifactory.CheckWorkOvertimeFactory", "com.json.entity.CheckWorkOvertimeEntity"),

        CRM_CONTRACT_PAGE("客户合同申请", "../../tpl/crmcontractmanage/mycrmcontractadd.html",
                 "com.skyeye.activitifactory.CrmContractFactory", "com.json.entity.CrmContractEntity"),
        CRM_OPPORTUNITY_PAGE("客户商机申请", "../../tpl/crmopportunity/crmopportunityadd.html",
                 "com.skyeye.activitifactory.CrmOpportunityFactory", "com.json.entity.CrmOpportunityTypeOneEntity"),

        ERP_PURCHASE_ORDER_PAGE("采购订单申请", "../../tpl/purchaseorder/purchaseorderadd.html",
                "com.skyeye.activitifactory.PurchaseOrderActivitiFactory", "com.json.entity.erp.PurchaseOrderEntity"),
        ERP_PUT_IS_PURCHASE_PAGE("采购入库单申请", "../../tpl/purchaseput/purchaseputadd.html",
                "com.skyeye.activitifactory.PurchasePutActivitiFactory", "com.json.entity.erp.PurchasePutEntity"),
        ERP_OUT_IS_PURCHASE_RETURNS_PAGE("采购退货单申请", "../../tpl/purchasereturns/purchasereturnsadd.html",
                "com.skyeye.activitifactory.PurchaseReturnsActivitiFactory", "com.json.entity.erp.PurchaseReturnsEntity"),

        ERP_OUT_IS_RETAIL_PAGE("零售出库单申请", "../../tpl/retailoutlet/retailoutletadd.html",
                "com.skyeye.activitifactory.RetailOutLetActivitiFactory", "com.json.entity.erp.RetailOutLetEntity"),
        ERP_PUT_IS_RETAIL_RETURNS_PAGE("零售退货单申请", "../../tpl/retailreturns/retailreturnsadd.html",
                "com.skyeye.activitifactory.RetailReturnsActivitiFactory", "com.json.entity.erp.RetailReturnsEntity"),

        ERP_OUTCHASE_ORDER_PAGE("销售订单申请", "../../tpl/salesorder/salesorderadd.html",
                "com.skyeye.activitifactory.SalesOrderActivitiFactory", "com.json.entity.erp.SalesOrderEntity"),
        ERP_OUT_IS_SALES_OUTLET_PAGE("销售出库单申请", "../../tpl/salesoutlet/salesoutletadd.html",
                "com.skyeye.activitifactory.SalesOutLetActivitiFactory", "com.json.entity.erp.SalesOutLetEntity"),
        ERP_PUT_IS_SALES_RETURNS_PAGE("销售退货单申请", "../../tpl/salesreturns/salesreturnsadd.html",
                "com.skyeye.activitifactory.SalesReturnsActivitiFactory", "com.json.entity.erp.SalesReturnsEntity"),

        ERP_OUT_IS_OTHERS_PAGE("其他出库单申请", "../../tpl/otheroutlets/otheroutletsadd.html",
                "com.skyeye.activitifactory.OtherOutLetsActivitiFactory", "com.json.entity.erp.OtherOutLetsEntity"),
        ERP_PUT_IS_OTHERS_PAGE("其他入库单申请", "../../tpl/otherwarehous/otherwarehousadd.html",
                "com.skyeye.activitifactory.OtherWareHousActivitiFactory", "com.json.entity.erp.OtherWareHousEntity");

        private String title;
        private String key;
        private String factoryPath;
        private String transformClassPath;

        ActivitiObjectType(String title, String key, String factoryPath, String transformClassPath){
            this.title = title;
            this.key = key;
            this.factoryPath = factoryPath;
            this.transformClassPath = transformClassPath;
        }

        public static String getFactoryClassPath(String key){
            for (ActivitiObjectType value : ActivitiObjectType.values()){
                if(value.getKey().equals(key)){
                    return value.getFactoryPath();
                }
            }
            return StringUtils.EMPTY;
        }

        public static String getTransformClassPath(String key){
            for (ActivitiObjectType value : ActivitiObjectType.values()){
                if(value.getKey().equals(key)){
                    return value.getTransformClassPath();
                }
            }
            return StringUtils.EMPTY;
        }

        public String getTitle() {
            return title;
        }

        public String getKey() {
            return key;
        }

        public String getFactoryPath() {
            return factoryPath;
        }

        public String getTransformClassPath() {
            return transformClassPath;
        }
    }

    public static enum ActivitiState{
        DRAFT(0, "草稿"),
        IN_EXAMINE(1, "审核中"),
        PASS(2, "审核通过"),
        NO_PASS(3, "审核不通过"),
        NULLIFY(4, "作废"),
        REVOKE(5, "撤销");

        private Integer state;
        private String stateName;

        ActivitiState(Integer state, String stateName){
            this.state = state;
            this.stateName = stateName;
        }

        public static String getStateNameByState(Integer state){
            for (ActivitiState value : ActivitiState.values()){
                if(value.getState() == state){
                    return value.getStateName();
                }
            }
            return StringUtils.EMPTY;
        }

        public Integer getState() {
            return state;
        }

        public String getStateName() {
            return stateName;
        }

    }

    /**
     * 获取提交类型获取子单据的状态
     *
     * @param subType 提交类型  1.草稿  2.提交审批
     * @return 子单据状态
     */
    public static String getSave2DBState(String subType) {
        String state = "";
        if ("1".equals(subType)) {
            // 保存为草稿，状态为未提交审批
            state = "0";
        } else if ("2".equals(subType)) {
            // 保存为提交审批，状态为等待审批
            state = "1";
        }
        return state;
    }

}
