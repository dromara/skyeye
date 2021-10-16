/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.common.constans;

import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.SpringUtils;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.service.SystemFoundationSettingsService;
import com.skyeye.jedis.JedisClientService;
import net.sf.json.JSONArray;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 *
 * @ClassName: ErpConstants
 * @Description: ERP常量类
 * @author: skyeye云系列--卫志强
 * @date: 2021/6/6 23:27
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class ErpConstants {

	/**
	 * 一天的秒数
	 */
	private static final int ONE_DAY_SECONDS = 10 * 24 * 60;
	
	// 产品类型在redis中的key
	public static final String SYS_MATERIAL_CATEGORY_REDIS_KEY = "sys_material_category_redis_key";
	public static String getSysMaterialCategoryRedisKeyById(){
		return SYS_MATERIAL_CATEGORY_REDIS_KEY;
	}
	
	// 仓库在redis中存储的key
	public static final String STORE_HOUSE_REDIS_KEY = "store_house_redis_key";
	public static String getStoreHouseRedisKeyByUserId(){
		return STORE_HOUSE_REDIS_KEY;
	}
	
	// 单据主表类型
	public static enum DepoTheadSubType {
		/*****************************ERP模块********************************/
		// 入库
    	PUT_IS_PURCHASE("采购入库单", "CGRK", "1", "com.skyeye.factory.impl.PurchasePutFactory",
				"../../tpl/purchaseput/purchaseputadd.html", 1, true, 2),
    	PUT_IS_SALES_RETURNS("销售退货单", "XSTH", "2", "com.skyeye.factory.impl.SalesReturnsFactory",
				"../../tpl/salesreturns/salesreturnsadd.html", 1, true, 2),
    	PUT_IS_RETAIL_RETURNS("零售退货单", "LSTH", "3", "com.skyeye.factory.impl.RetailReturnsFactory",
				"../../tpl/retailreturns/retailreturnsadd.html", 1, true, 2),
    	PUT_IS_OTHERS("其他入库单", "QTRK", "4", "com.skyeye.factory.impl.OtherWareHousFactory",
				"../../tpl/otherwarehous/otherwarehousadd.html", 1, true, 2),
    	// 出库
    	OUT_IS_SALES_OUTLET("销售出库单", "XSCK", "5", "com.skyeye.factory.impl.SalesOutLetFactory",
				"../../tpl/salesoutlet/salesoutletadd.html", 1, true, 1),
    	OUT_IS_PURCHASE_RETURNS("采购退货单", "CGTH", "6", "com.skyeye.factory.impl.PurchaseReturnsFactory",
				"../../tpl/purchasereturns/purchasereturnsadd.html", 1, true, 1),
    	OUT_IS_ALLOCATION("调拨单", "DBCK", "7", "", "", 0, true, null),
    	OUT_IS_RETAIL("零售出库单", "LSCK", "8", "com.skyeye.factory.impl.RetailOutLetFactory",
				"../../tpl/retailoutlet/retailoutletadd.html", 1, true, 1),
    	OUT_IS_OTHERS("其他出库单", "QTCK", "9", "com.skyeye.factory.impl.OtherOutLetsFactory",
				"../../tpl/otheroutlets/otheroutletsadd.html", 1, true, 1),
		// 采购单
    	PURCHASE_ORDER("采购订单", "CGDD", "10", "com.skyeye.factory.impl.PurchaseOrderFactory",
				"../../tpl/purchaseorder/purchaseorderadd.html", 1, true, 2),
		// 销售单
		OUTCHASE_ORDER("销售订单", "XSDD", "11", "com.skyeye.factory.impl.SalesOrderFactory",
				"../../tpl/salesorder/salesorderadd.html", 1, true, 1),
		// 拆分单
		SPLIT_LIST_ORDER("拆分订单", "CFDD", "12", "", "", 0, true, null),
		// 组装单
		ASSEMBLY_SHEET_ORDER("组装订单", "ZZDD", "13", "", "", 0, true, null),
		// 调拨单
		ALLOCATION_FORM_ORDER("调拨订单", "DBDD", "14", "", "", 0, true, null),
		// 验收入库单
		PUT_ACCEPTANCE_WAREHOUSING("验收入库单", "YSRK", "15", "", "", 0, true, null),

		/*****************************生产模块********************************/
		// 加工单
		MACHIN_HEADER("加工单", "JG", "16", "", "", 0, true, null),
		// 加工单子单据（工序验收单）
		MACHIN_CHILD("工序验收单", "JGZD", "17", "", "", 0, true, null),
		// 生产计划单
		PRODUCTION_HEAD("生产计划单", "SCJH", "18", "", "", 0, true, null),
		// 领料单
		PICK_PICKING("领料单", "LLDD", "19", "", "", 0, true, null),
		// 补料单
		PICK_REPLENISHMENT("补料单", "BLDD", "20", "", "", 0, true, null),
		// 退料单
		PICK_RETURN("退料单", "TLDD", "21", "", "", 0, true, null),

		/*****************************财务模块********************************/
		EXPENDITURE_ORDER("支出订单", "CWZCDD", "22", "", "", 0, true, null),
		INCOME_ORDER("收入订单", "CWSRDD", "23", "", "", 0, true, null),
		RECEIVABLES_ORDER("收款订单", "CWSKDD", "24", "", "", 0, true, null),
		PAYMENT_ORDER("付款订单", "CWFKDD", "25", "", "", 0, true, null),
		TRANSFER_ORDER("转账订单", "CWZZDD", "26", "", "", 0, true, null),
		ADVANCE_ORDER("收预付款", "CWYFDD", "27", "", "", 0, true, null);

		// 单据标题
        private String title;
        // 单据起始前缀
        private String code;
        // 单据类型
        private String type;
        // 单据对应的工厂执行类
        private String factoryClassPath;
        // 是否需要审核开关控制的单据--0.不需要；1.需要
        private Integer needExamineOrder;
        // 该类型的单据是否需要审核的开关--true需要审核；false不需要审核
		private Boolean examineSwitch;
		// 1.出库 2.入库 3.其他
		private Integer outInWarehouse;
		// erp单据提交到工作流中的key
		private String activityKey;
		
        DepoTheadSubType(String title, String code, String type, String factoryClassPath, String activityKey,
						 Integer needExamineOrder, Boolean examineSwitch, Integer outInWarehouse) {
            this.title = title;
            this.code = code;
            this.type = type;
            this.factoryClassPath = factoryClassPath;
            this.activityKey = activityKey;
            this.needExamineOrder = needExamineOrder;
            this.examineSwitch = examineSwitch;
            this.outInWarehouse = outInWarehouse;
        }
	
        public static String getOrderCode(String type){
            for (DepoTheadSubType q : DepoTheadSubType.values()){
                if(q.getType().equals(type)){
                    return q.getCode();
                }
            }
            return "";
        }

		public static String getActivityKey(String type){
			for (DepoTheadSubType q : DepoTheadSubType.values()){
				if(q.getType().equals(type)){
					return q.getActivityKey();
				}
			}
			return "";
		}

		public static String getFactoryClassPath(String type){
			for (DepoTheadSubType q : DepoTheadSubType.values()){
				if(q.getType().equals(type)){
					return q.getFactoryClassPath();
				}
			}
			return StringUtils.EMPTY;
		}

		public static Integer getOutInWarehouse(String type){
			for (DepoTheadSubType q : DepoTheadSubType.values()){
				if(q.getType().equals(type)){
					return q.getOutInWarehouse();
				}
			}
			return null;
		}

		public static String getTitle(String type){
			for (DepoTheadSubType q : DepoTheadSubType.values()){
				if(q.getType().equals(type)){
					return q.getTitle();
				}
			}
			return StringUtils.EMPTY;
		}

		/**
		 * 根据订单类型获取订单编号
		 *
		 * @param subType 订单类型
		 * @return 订单编号
		 * @throws Exception
		 */
        public static String getOrderNumBySubType(String subType) throws Exception {
            // 获取在redis中的key
            String key = String.format(Locale.ROOT, "order_num_cache_%s_%s", subType, DateUtil.getTimeIsYMD());
            JedisClientService jedisClient = SpringUtils.getBean(JedisClientService.class);
            String num = "1";
            if (!ToolUtil.isBlank(jedisClient.get(key))) {
                num = jedisClient.get(key);
                num = CalculationUtil.add(num, "1", 0);
            }
            jedisClient.set(key, num);
            jedisClient.expire(key, ONE_DAY_SECONDS);
            // 类型 + 年月日 + num "%010d"：0为int类型，0代表前面要补的字符 7代表字符串长度,d表示参数为整数类型
            return ErpConstants.DepoTheadSubType.getOrderCode(subType) + DateUtil.getTimeIsYMD()
                + String.format(Locale.ROOT, "%07d", Integer.parseInt(num));
        }

		/**
		 * 获取所有需要审核的单据信息
		 *
		 * @return 所有需要审核的单据信息
		 */
		public static List<Map<String, Object>> getNeedExamineOrderISOne(){
			List<Map<String, Object>> beans = new ArrayList<>();
			for (DepoTheadSubType q : DepoTheadSubType.values()){
				if(q.getNeedExamineOrder() == 1){
					Map<String, Object> bean = new HashMap<>();
					bean.put("title", q.getTitle());
					bean.put("code", q.getCode());
					bean.put("examineSwitch", q.getExamineSwitch());
					beans.add(bean);
				}
			}
			return beans;
		}

		/**
		 * 根据单据类型判断该类型的单据是否需要审批
		 *
		 * @param type 单据类型
		 * @return true需要审核；false不需要审核
		 * @throws Exception
		 */
		public static Boolean whetherNeedExamineByType(String type) throws Exception {
			SystemFoundationSettingsService systemFoundationSettingsService = SpringUtils.getBean(SystemFoundationSettingsService.class);
			// 获取单据审批配置信息
			Map<String, Object> setting = systemFoundationSettingsService.getSystemFoundationSettings();
			String erpExamineBasicDesign = setting.get("erpExamineBasicDesign").toString();
			List<Map<String, Object>> examins = JSONArray.fromObject(erpExamineBasicDesign);
			String orderCode = getOrderCode(type);
			// 根据单据code获取已经设置的单据审批信息
			Map<String, Object> orderExaminMation = examins.stream().filter(bean -> orderCode.equals(bean.get("code").toString())).findFirst().orElse(null);
			if(orderExaminMation != null && !orderExaminMation.isEmpty()){
				return Boolean.valueOf(orderExaminMation.get("examineSwitch").toString());
			}
			return true;
		}

		public String getTitle() {
			return title;
		}

		public String getType() {
			return type;
		}

		public String getFactoryClassPath() {
			return factoryClassPath;
		}

		public String getCode() {
			return code;
		}

		public String getActivityKey() {
			return activityKey;
		}

		public Integer getNeedExamineOrder() {
			return needExamineOrder;
		}

		public Boolean getExamineSwitch() {
			return examineSwitch;
		}

		public Integer getOutInWarehouse() {
			return outInWarehouse;
		}
	}
	
	/**
	 * 出库
	 */
	public static final String ERP_HEADER_TYPE_IS_EX_WAREHOUSE = "1";
	
	/**
	 * 入库
	 */
	public static final String ERP_HEADER_TYPE_IS_IN_WAREHOUSE = "2";
	
	/**
	 * 其他
	 */
	public static final String ERP_HEADER_TYPE_IS_OTHER = "3";


	/**
	 * 未审核
	 */
	public static final String ERP_HEADER_STATUS_IS_NOT_APPROVED = "0";
	/**
	 * 审核中
	 */
	public static final String ERP_HEADER_STATUS_IS_IN_APPROVED = "1";
	/**
	 * 审核通过
	 */
	public static final String ERP_HEADER_STATUS_IS_APPROVED_PASS = "2";
	/**
	 * 审核拒绝
	 */
	public static final String ERP_HEADER_STATUS_IS_APPROVED_NOT_PASS = "3";
	/**
	 * 已完成
	 */
	public static final String ERP_HEADER_STATUS_IS_COMPLATE = "4";
	/**
	 * 撤销
	 */
	public static final String ERP_HEADER_STATUS_IS_REVOKE = "5";

	// 订单详情在redis中的key
	public static final String SYS_ORDER_DETAILS_KEY = "sys_order_details_cache";
	public static String getSysOrderDetailsCacheKey(String productionId){
		return SYS_ORDER_DETAILS_KEY + "_" + productionId;
	}
	
}
