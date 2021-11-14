/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.common.constans;

import com.skyeye.common.util.SpringUtils;
import com.skyeye.eve.service.SystemFoundationSettingsService;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: SystemFoundationSettingsConstants
 * @Description: 系统基础设置常量类
 * @author: skyeye云系列--卫志强
 * @date: 2021/11/13 14:43
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class SystemFoundationSettingsConstants {

    public static enum CustomWithDsFormObject {
        /*****************************ERP模块********************************/
        // 入库
        PUT_IS_PURCHASE("ERP模块", "采购入库单", "CGRK", new ArrayList<>()),
        PUT_IS_SALES_RETURNS("ERP模块", "销售退货单", "XSTH", new ArrayList<>()),
        PUT_IS_RETAIL_RETURNS("ERP模块", "零售退货单", "LSTH", new ArrayList<>()),
        PUT_IS_OTHERS("ERP模块", "其他入库单", "QTRK", new ArrayList<>()),
        // 出库
        OUT_IS_SALES_OUTLET("ERP模块", "销售出库单", "XSCK", new ArrayList<>()),
        OUT_IS_PURCHASE_RETURNS("ERP模块", "采购退货单", "CGTH", new ArrayList<>()),
        OUT_IS_RETAIL("ERP模块", "零售出库单", "LSCK", new ArrayList<>()),
        OUT_IS_OTHERS("ERP模块", "其他出库单", "QTCK", new ArrayList<>()),
        // 采购单
        PURCHASE_ORDER("ERP模块", "采购订单", "CGDD", new ArrayList<>()),
        // 销售单
        OUTCHASE_ORDER("ERP模块", "销售订单", "XSDD", new ArrayList<>());

        // 所属模块
        private String modelName;
        // 标题
        private String title;
        // code为了区分类型
        private String code;
        // 默认绑定的动态表单，需要包含表单id,表单名称,表单编码
        private List<Map<String, Object>> dsFormList;

        CustomWithDsFormObject(String modelName, String title, String code, List<Map<String, Object>> dsFormList){
            this.modelName = modelName;
            this.title = title;
            this.code = code;
            this.dsFormList = dsFormList;
        }

        /**
         * 获取所有需要设置动添表单的类型
         *
         * @return 所有需要设置动添表单的类型
         */
        public static Map<String, List<Map<String, Object>>> getCustomWithDsFormList(){
            List<Map<String, Object>> beans = new ArrayList<>();
            for (CustomWithDsFormObject q : CustomWithDsFormObject.values()){
                Map<String, Object> bean = new HashMap<>();
                bean.put("modelName", q.getModelName());
                bean.put("title", q.getTitle());
                bean.put("code", q.getCode());
                bean.put("dsFormList", q.getDsFormList());
                beans.add(bean);
            }
            Map<String, List<Map<String, Object>>> result = beans.stream().collect(Collectors.groupingBy(map -> map.get("modelName").toString()));
            return result;
        }

        /**
         * 根据code获取已经绑定的动态表单信息
         *
         * @param code 编码
         * @return 已经绑定的动态表单信息
         * @throws Exception
         */
        public static List<Map<String, Object>> getDsFormListByCode(String code) throws Exception {
            // 获取动态表单关联的一些设置
            List<Map<String, Object>> allDsFormList = getAllDsFormList();
            // 根据单据code获取已经设置的单据审批信息
            Map<String, Object> thisDsFormList = allDsFormList.stream().filter(bean -> code.equals(bean.get("code").toString())).findFirst().orElse(null);
            if(thisDsFormList != null && !thisDsFormList.isEmpty()){
                return (List<Map<String, Object>>) thisDsFormList.get("dsFormList");
            }
            return new ArrayList<>();
        }

        private static List<Map<String, Object>> getAllDsFormList() throws Exception {
            // 获取系统配置信息
            SystemFoundationSettingsService systemFoundationSettingsService = SpringUtils.getBean(SystemFoundationSettingsService.class);
            Map<String, Object> setting = systemFoundationSettingsService.getSystemFoundationSettings();
            // 获取动态表单关联的一些设置
            String customWithDsFormList = setting.get("customWithDsFormList").toString();
            Map<String, List<Map<String, Object>>> customWithDsForm = JSONObject.fromObject(customWithDsFormList);
            List<Map<String, Object>> allDsFormList = new ArrayList<>();
            for (Map.Entry<String, List<Map<String, Object>>> entry : customWithDsForm.entrySet()) {
                allDsFormList.addAll(entry.getValue());
            }
            return allDsFormList;
        }

        public String getModelName() {
            return modelName;
        }

        public String getTitle() {
            return title;
        }

        public String getCode() {
            return code;
        }

        public List<Map<String, Object>> getDsFormList() {
            return dsFormList;
        }
    }

}
