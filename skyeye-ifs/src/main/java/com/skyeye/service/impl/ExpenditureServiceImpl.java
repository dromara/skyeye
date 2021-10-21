/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.service.impl;

import cn.hutool.json.JSONUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.constans.ErpConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.ExcelUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.dao.ExpenditureDao;
import com.skyeye.eve.dao.SysEveUserStaffDao;
import com.skyeye.service.ExpenditureService;
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
 * @ClassName: ExpenditureServiceImpl
 * @Description: 支出单服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/6/6 23:41
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class ExpenditureServiceImpl implements ExpenditureService {

    @Autowired
    private ExpenditureDao expenditureDao;
    
    @Autowired
    private SysEveUserStaffDao sysEveUserStaffDao;

    /**
     * 支出单类型
     */
    private static final String ORDER_TYPE = ErpConstants.DepoTheadSubType.EXPENDITURE_ORDER.getType();

    /**
     * 查询支出单列表信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void queryExpenditureByList(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        params.put("type", ORDER_TYPE);
        Page pages = PageHelper.startPage(Integer.parseInt(params.get("page").toString()), Integer.parseInt(params.get("limit").toString()));
        List<Map<String, Object>> beans = expenditureDao.queryExpenditureByList(params);
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    /**
     * 添加支出单
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
    @Transactional(value="transactionManager")
    public void insertExpenditure(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        // 财务主表ID
        String useId = ToolUtil.getSurFaceId();
        // 财务子表实体集合信息
        List<Map<String, Object>> entitys = new ArrayList<>();
        BigDecimal allPrice = getAllPriceAndChildList(useId, params.get("initemStr").toString(), entitys);
        if(entitys.size() == 0){
            outputObject.setreturnMessage("请选择支出项目");
            return;
        }
        Map<String, Object> accountHead = new HashMap<>();
        String orderNum = ErpConstants.DepoTheadSubType.getOrderNumBySubType(ORDER_TYPE);
        accountHead.put("id", useId);
        accountHead.put("type", ORDER_TYPE);//支出单
        accountHead.put("billNo", orderNum);
        accountHead.put("totalPrice", allPrice);
        accountHead.put("organId", params.get("organId"));
        accountHead.put("operTime", params.get("operTime"));
        accountHead.put("accountId", params.get("accountId"));
        accountHead.put("handsPersonId", params.get("handsPersonId"));
        accountHead.put("remark", params.get("remark"));
        accountHead.put("changeAmount", params.get("changeAmount"));
        accountHead.put("deleteFlag", 0);
        expenditureDao.insertExpenditure(accountHead);
        expenditureDao.insertExpenditureItem(entitys);
    }

    private BigDecimal getAllPriceAndChildList(String useId, String initemStr, List<Map<String, Object>> entitys) {
        List<Map<String, Object>> jArray = JSONUtil.toList(initemStr, null);
        // 主单总价
        BigDecimal allPrice = new BigDecimal("0");
        for(int i = 0; i < jArray.size(); i++){
            Map<String, Object> bean = jArray.get(i);
            Map<String, Object> entity = new HashMap<>();
            // 获取子项金额
            BigDecimal itemAllPrice = new BigDecimal(bean.get("initemMoney").toString());
            entity.put("id", ToolUtil.getSurFaceId());
            entity.put("headerId", useId);
            entity.put("inOutItemId", bean.get("initemId"));
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
     * 查询支出单用于数据回显
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void queryExpenditureToEditById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        Map<String, Object> bean = expenditureDao.queryExpenditureToEditById(params);
        if(bean != null && !bean.isEmpty()){
        	List<Map<String, Object>> beans = expenditureDao.queryExpenditureItemsToEditById(params);
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
     * 编辑支出单信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
	@Override
    @Transactional(value="transactionManager")
    public void editExpenditureById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        String useId = params.get("id").toString();
        // 财务子表实体集合信息
        List<Map<String, Object>> entitys = new ArrayList<>();
        BigDecimal allPrice = getAllPriceAndChildList(useId, params.get("initemStr").toString(), entitys);
        if(entitys.size() == 0){
            outputObject.setreturnMessage("请选择支出项目");
            return;
        }
        Map<String, Object> accountHead = new HashMap<>();
        accountHead.put("id", useId);
        accountHead.put("totalPrice", allPrice);
        accountHead.put("organId", params.get("organId"));
        accountHead.put("operTime", params.get("operTime"));
        accountHead.put("accountId", params.get("accountId"));
        accountHead.put("handsPersonId", params.get("handsPersonId"));
        accountHead.put("remark", params.get("remark"));
        accountHead.put("changeAmount", params.get("changeAmount"));
        expenditureDao.editExpenditureById(accountHead);
        // 删除之前的绑定信息
        expenditureDao.deleteExpenditureItemById(params);
        expenditureDao.insertExpenditureItem(entitys);
    }

    /**
     * 删除支出单信息
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    @Transactional(value="transactionManager")
    public void deleteExpenditureById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        params.put("deleteFlag", 1);
        expenditureDao.editExpenditureByDeleteFlag(params);
        expenditureDao.editExpenditureItemsByDeleteFlag(params);
    }

    /**
     * 查看支出单详情
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void queryExpenditureByDetail(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> params = inputObject.getParams();
        // 获取财务主表信息
        Map<String, Object> bean = expenditureDao.queryExpenditureDetailById(params);
        if(bean != null && !bean.isEmpty()){
            // 获取子表信息
            List<Map<String, Object>> beans = expenditureDao.queryExpenditureItemsDetailById(bean);
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
        List<Map<String, Object>> beans = expenditureDao.queryMationToExcel(params);
        String[] key = new String[]{"billNo", "supplierName", "totalPrice", "hansPersonName", "billTime"};
        String[] column = new String[]{"单据编号", "往来单位", "合计金额", "经手人", "单据日期"};
        String[] dataType = new String[]{"", "data", "data", "data", "data"};
        //支出单信息导出
        ExcelUtil.createWorkBook("支出单", "支出单详细", beans, key, column, dataType, inputObject.getResponse());
	}
}
