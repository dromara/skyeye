/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.service.impl;

import cn.hutool.json.JSONUtil;
import com.skyeye.common.constans.ErpConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.ExcelUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.dao.AdvanceChargeDao;
import com.skyeye.eve.dao.SysEveUserStaffDao;
import com.skyeye.factory.IfsOrderRunFactory;
import com.skyeye.service.AdvanceChargeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: AdvanceChargeServiceImpl
 * @Description: 收预付款管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/8 21:28
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class AdvanceChargeServiceImpl implements AdvanceChargeService {

    @Autowired
    private AdvanceChargeDao advanceChargeDao;
    
    @Autowired
    private SysEveUserStaffDao sysEveUserStaffDao;

    /**
     * 收预付款类型
     */
    private static final String ORDER_TYPE = ErpConstants.DepoTheadSubType.ADVANCE_ORDER.getType();

    /**
     * 查询收预付款列表信息
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void queryAdvanceChargeByList(InputObject inputObject, OutputObject outputObject) throws Exception {
        IfsOrderRunFactory.run(inputObject, outputObject, ORDER_TYPE).queryOrderList();
    }

    /**
     * 添加收预付款
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
    @Transactional(value="transactionManager")
    public void insertAdvanceCharge(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        // 财务主表ID
        String useId = ToolUtil.getSurFaceId();
        // 处理数据
        List<Map<String, Object>> entitys = new ArrayList<>();
        BigDecimal allPrice = getAllPriceAndChildList(useId, params.get("initemStr").toString(), entitys);
        if(entitys.size() == 0){
            outputObject.setreturnMessage("请选择账户");
            return;
        }
        Map<String, Object> accountHead = new HashMap<>();
        String orderNum = ErpConstants.DepoTheadSubType.getOrderNumBySubType(ORDER_TYPE);
        accountHead.put("id", useId);
        accountHead.put("type", ORDER_TYPE);//收预付款
        accountHead.put("billNo", orderNum);
        accountHead.put("totalPrice", allPrice);
        accountHead.put("organId", params.get("organId"));
        accountHead.put("operTime", params.get("operTime"));
        accountHead.put("handsPersonId", params.get("handsPersonId"));
        accountHead.put("remark", params.get("remark"));
        accountHead.put("changeAmount", params.get("changeAmount"));
        accountHead.put("deleteFlag", 0);
        advanceChargeDao.insertAdvanceCharge(accountHead);
        advanceChargeDao.insertAdvanceChargeItem(entitys);
    }

    private BigDecimal getAllPriceAndChildList(String useId, String initemStr, List<Map<String, Object>> entitys) {
        List<Map<String, Object>> jArray = JSONUtil.toList(initemStr, null);
        // 主单总价
        BigDecimal allPrice = new BigDecimal("0");
        for(int i = 0; i < jArray.size(); i++){
            Map<String, Object> bean = jArray.get(i);
            Map<String, Object> entity = new HashMap<>();
            //获取子项金额
            BigDecimal itemAllPrice = new BigDecimal(bean.get("initemMoney").toString());
            entity.put("id", ToolUtil.getSurFaceId());
            entity.put("headerId", useId);
            entity.put("accountId", bean.get("accountId"));
            entity.put("eachAmount", bean.get("initemMoney"));
            entity.put("remark", bean.get("remark"));
            entity.put("deleteFlag", 0);
            entitys.add(entity);
            // 计算总金额
            allPrice = allPrice.add(itemAllPrice);
        }
        return allPrice;
    }

    /**
     * 查询收预付款用于数据回显
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void queryAdvanceChargeToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        Map<String, Object> bean = advanceChargeDao.queryAdvanceChargeToEditById(params);
        if(bean != null && !bean.isEmpty()){
        	List<Map<String, Object>> beans = advanceChargeDao.queryAdvanceChargeItemsToEditById(params);
        	bean.put("items", beans);
        	// 获取经手人员
        	bean.put("userInfo", sysEveUserStaffDao.queryUserNameList(bean.get("handsPersonId").toString()));
        	outputObject.setBean(bean);
        	outputObject.settotal(1);
        }else{
        	outputObject.setreturnMessage("未查询到信息！");
        }
    }

    /**
     * 编辑收预付款信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
    @Transactional(value="transactionManager")
    public void editAdvanceChargeById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        String useId = params.get("id").toString();
        // 处理数据
        List<Map<String, Object>> entitys = new ArrayList<>();
        BigDecimal allPrice = getAllPriceAndChildList(useId, params.get("initemStr").toString(), entitys);
        if(entitys.size() == 0){
            outputObject.setreturnMessage("请选择账户");
            return;
        }
        Map<String, Object> accountHead = new HashMap<>();
        accountHead.put("id", useId);
        accountHead.put("totalPrice", allPrice);
        accountHead.put("organId", params.get("organId"));
        accountHead.put("operTime", params.get("operTime"));
        accountHead.put("handsPersonId", params.get("handsPersonId"));
        accountHead.put("remark", params.get("remark"));
        accountHead.put("changeAmount", params.get("changeAmount"));
        advanceChargeDao.editAdvanceChargeById(accountHead);
        // 删除之前的绑定信息
        advanceChargeDao.deleteAdvanceChargeItemById(params);
        advanceChargeDao.insertAdvanceChargeItem(entitys);
    }

    /**
     * 删除收预付款信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void deleteAdvanceChargeById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        params.put("deleteFlag", 1);
        advanceChargeDao.editAdvanceChargeByDeleteFlag(params);
        advanceChargeDao.editAdvanceChargeItemsByDeleteFlag(params);
    }

    /**
     * 查看收预付款详情
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void queryAdvanceChargeByDetail(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        //获取财务主表信息
        Map<String, Object> bean = advanceChargeDao.queryAdvanceChargeDetailById(params);
        if(bean != null && !bean.isEmpty()){
            //获取子表信息
            List<Map<String, Object>> beans = advanceChargeDao.queryAdvanceChargeItemsDetailById(bean);
            bean.put("items", beans);
            outputObject.setBean(bean);
            outputObject.settotal(1);
        }else{
            outputObject.setreturnMessage("该数据已不存在.");
        }
    }

    /**
     * 导出Excel
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
	public void queryMationToExcel(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> params = inputObject.getParams();
        params.put("type", ORDER_TYPE);
        List<Map<String, Object>> beans = advanceChargeDao.queryMationToExcel(params);
        String[] key = new String[]{"billNo", "supplierName", "totalPrice", "hansPersonName", "billTime"};
        String[] column = new String[]{"单据编号", "付款会员", "合计金额", "经手人", "单据日期"};
        String[] dataType = new String[]{"", "data", "data", "data", "data"};
        //收预付款单信息导出
        ExcelUtil.createWorkBook("收预付款单", "收预付款单详细", beans, key, column, dataType, inputObject.getResponse());
	}
}
