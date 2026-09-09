/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.loan.service.impl;

import cn.hutool.core.collection.CollectionUtil;
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
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.loan.classenum.LoanBorrowTypeEnum;
import com.skyeye.loan.classenum.LoanPaidStateEnum;
import com.skyeye.loan.dao.LoanBorrowDao;
import com.skyeye.loan.entity.LoanBorrow;
import com.skyeye.loan.service.LoanBorrowService;
import com.skyeye.loan.service.UserLoanService;
import com.skyeye.organization.service.IDepmentService;
import com.skyeye.rest.project.service.IProProjectService;
import com.xingyuv.http.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: LoanBorrowServiceImpl
 * @Description: 借款单服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/5 14:17
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "借款单", groupName = "借款单", flowable = true)
public class LoanBorrowServiceImpl extends SkyeyeBusinessServiceImpl<LoanBorrowDao, LoanBorrow> implements LoanBorrowService {

    @Autowired
    private UserLoanService userLoanService;

    @Autowired
    private IDepmentService iDepmentService;

    @Autowired
    private IProProjectService iProProjectService;

    @Override
    public void validatorEntity(LoanBorrow entity) {
        super.validatorEntity(entity);
        if (entity.getBorrowType() == LoanBorrowTypeEnum.DEPARTMENT.getKey() && StringUtil.isEmpty(entity.getDepartmentId())) {
            throw new CustomException("请选择借款部门");
        }
        // 申请日期设置日期格式YYYY-MM-dd--字符串截取
        entity.setApplicationTime(entity.getApplicationTime().substring(0, 10));

    }

    @Override
    public QueryWrapper<LoanBorrow> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<LoanBorrow> queryWrapper = super.getQueryWrapper(commonPageInfo);
        // 我创建的
        queryWrapper.eq(MybatisPlusUtil.toColumns(LoanBorrow::getCreateId), InputObject.getLogParamsStatic().get("id").toString());
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        iDepmentService.setMationForMap(beans, "departmentId", "departmentMation");
        iAuthUserService.setMationForMap(beans, "applicantId", "applicantMation");
        iProProjectService.setMationForMap(beans, "projectId", "projectMation");
        return beans;
    }

    @Override
    public LoanBorrow selectById(String id) {
        LoanBorrow loanBorrow = super.selectById(id);
        iSysDictDataService.setDataMation(loanBorrow, LoanBorrow::getPayTypeId);
        iDepmentService.setDataMation(loanBorrow, LoanBorrow::getDepartmentId);
        iAuthUserService.setDataMation(loanBorrow, LoanBorrow::getApplicantId);
        iProProjectService.setDataMation(loanBorrow, LoanBorrow::getProjectId);
        loanBorrow.setName(loanBorrow.getOddNumber());
        return loanBorrow;
    }

    @Override
    public void approvalEndIsSuccess(LoanBorrow entity) {
        userLoanService.calcUserLoanPrice(entity.getCreateId(), entity.getPrice(), true);
    }

    @Override
    public void updateLoanBorrowStatePrice(String loanBorrowId, String price) {
        LoanBorrow loanBorrow = selectById(loanBorrowId);
        price = CalculationUtil.add(CommonNumConstants.NUM_TWO,
                StrUtil.isEmpty(loanBorrow.getPaidPrice()) ? "0" : loanBorrow.getPaidPrice(),
                price);
        UpdateWrapper<LoanBorrow> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, loanBorrowId);
        updateWrapper.set(MybatisPlusUtil.toColumns(LoanBorrow::getPaidPrice), price);
        if (Double.parseDouble(price) >= Double.parseDouble(loanBorrow.getPrice())) {
            updateWrapper.set(MybatisPlusUtil.toColumns(LoanBorrow::getPaidState), LoanPaidStateEnum.PAID.getKey());
        } else if (Double.parseDouble(price) > CommonNumConstants.NUM_ZERO) {
            updateWrapper.set(MybatisPlusUtil.toColumns(LoanBorrow::getPaidState), LoanPaidStateEnum.PART_PAID.getKey());
        } else {
            updateWrapper.set(MybatisPlusUtil.toColumns(LoanBorrow::getPaidState), LoanPaidStateEnum.NOT_PAID.getKey());
        }
        update(updateWrapper);
        refreshCache(loanBorrowId);
    }

    @Override
    public void queryLoanBorrowTypeAnalysis(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String year = params.get("year").toString();
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
            List<Map<String, Object>> result = getLoanBorrowTypeAnalysis(startPeriod, endPeriod);
            outputObject.setBeans(result);
        } else {
            String startPeriod = year + StrUtil.DASHED + CommonNumConstants.NUM_ZERO + CommonNumConstants.NUM_ONE; // 本期开始时间
            String endPeriod = year + StrUtil.DASHED + CommonNumConstants.NUM_TWELVE;  // 本期结束时间
            List<Map<String, Object>> result = getLoanBorrowTypeAnalysis(startPeriod, endPeriod);
            outputObject.setBeans(result);
        }
    }

    private List<Map<String, Object>> getLoanBorrowTypeAnalysis(String startPeriod, String endPeriod) {
        QueryWrapper<LoanBorrow> queryWrapper = new QueryWrapper<>();
        queryWrapper.apply("date_format(" + MybatisPlusUtil.toColumns(LoanBorrow::getCreateTime) + ", '%Y-%m') >= {0}", startPeriod)
                .apply("date_format(" + MybatisPlusUtil.toColumns(LoanBorrow::getCreateTime) + ", '%Y-%m') <= {0}", endPeriod);
        queryWrapper.eq(MybatisPlusUtil.toColumns(LoanBorrow::getState), FlowableStateEnum.PASS.getKey());
        List<LoanBorrow> bean = list(queryWrapper);
        List<Map<String, Object>> result = new ArrayList<>();
        if (CollectionUtil.isEmpty(bean)) {
            return result;
        }
        Map<Integer, Long> map = bean.stream().collect(Collectors.groupingBy(LoanBorrow::getBorrowType, Collectors.counting()));
        LoanBorrowTypeEnum[] types = LoanBorrowTypeEnum.values();
        for (LoanBorrowTypeEnum type : types) {
            Long count = map.getOrDefault(type.getKey(), 0L);
            Map<String, Object> resultItem = new HashMap<>();
            resultItem.put(type.name(), count);
            result.add(resultItem);
        }
        return result;
    }

    @Override
    public void queryLoanBorrowDeptAnalysis(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String year = params.get("year").toString();
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
            List<Map<String, Object>> result = getLoanBorrowDeptAnalysis(startPeriod, endPeriod);
            outputObject.setBeans(result);
        } else {
            String startPeriod = year + StrUtil.DASHED + CommonNumConstants.NUM_ZERO + CommonNumConstants.NUM_ONE; // 本期开始时间
            String endPeriod = year + StrUtil.DASHED + CommonNumConstants.NUM_TWELVE;  // 本期结束时间
            List<Map<String, Object>> result = getLoanBorrowDeptAnalysis(startPeriod, endPeriod);
            outputObject.setBeans(result);
        }
    }

    private List<Map<String, Object>> getLoanBorrowDeptAnalysis(String startPeriod, String endPeriod) {
        QueryWrapper<LoanBorrow> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(LoanBorrow::getBorrowType), LoanBorrowTypeEnum.DEPARTMENT.getKey());
        queryWrapper.apply("date_format(" + MybatisPlusUtil.toColumns(LoanBorrow::getCreateTime) + ", '%Y-%m') >= {0}", startPeriod)
                .apply("date_format(" + MybatisPlusUtil.toColumns(LoanBorrow::getCreateTime) + ", '%Y-%m') <= {0}", endPeriod);
        queryWrapper.eq(MybatisPlusUtil.toColumns(LoanBorrow::getState), FlowableStateEnum.PASS.getKey());
        List<LoanBorrow> bean = list(queryWrapper);
        List<Map<String, Object>> result = new ArrayList<>();
        if (CollectionUtil.isEmpty(bean)) {
            return result;
        }
        // 根据部门id分组
        Map<String, List<LoanBorrow>> map = bean.stream().collect(Collectors.groupingBy(LoanBorrow::getDepartmentId));
        for (Map.Entry<String, List<LoanBorrow>> entry : map.entrySet()) {
            Map<String, Object> tempMap = new HashMap<>();
            tempMap.put("departmentId", entry.getKey());
            // 总借款单数
            tempMap.put("borrowTotalCount", entry.getValue().size());
            // 计算已经还单数量根据状态
            int paidCount = entry.getValue().stream().filter(e -> e.getPaidState() == LoanPaidStateEnum.PAID.getKey()).collect(Collectors.toList()).size();
            tempMap.put("paidCount", paidCount);
            // 计算未还款单数据量
            int notPaidCount = entry.getValue().stream().filter(e -> e.getPaidState() != LoanPaidStateEnum.PAID.getKey()).collect(Collectors.toList()).size();
            tempMap.put("notPaidCount", notPaidCount);
            // 计算借款总金额
            double borrowTotalPrice = entry.getValue().stream().mapToDouble(item -> Double.parseDouble(item.getPrice())).sum();
            tempMap.put("borrowTotalPrice", borrowTotalPrice);
            // 计算还款总金额
            double repayTotalPrice = entry.getValue().stream().mapToDouble(item -> Double.parseDouble(item.getPaidPrice())).sum();
            tempMap.put("repayTotalPrice", repayTotalPrice);
            // 计算未还款金额
            double notRepayTotalPrice = borrowTotalPrice - repayTotalPrice;
            tempMap.put("notRepayTotalPrice", notRepayTotalPrice);
            if (borrowTotalPrice == repayTotalPrice) {
                tempMap.put("state", LoanPaidStateEnum.PAID.getKey());
            } else if (repayTotalPrice > 0) {
                tempMap.put("state", LoanPaidStateEnum.PART_PAID.getKey());
            } else {
                tempMap.put("state", LoanPaidStateEnum.NOT_PAID.getKey());
            }

            result.add(tempMap);
        }
        iDepmentService.setMationForMap(result, "departmentId", "departmentMation");
        return result;
    }

    /**
     * 时间格式 YYYY-MM
     */
    @Override
    public List<LoanBorrow> queryLoanBorrowList(String time) {
        QueryWrapper<LoanBorrow> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(LoanBorrow::getState), FlowableStateEnum.PASS.getKey());
        queryWrapper.apply("date_format(" + MybatisPlusUtil.toColumns(LoanBorrow::getCreateTime) + ", '%Y-%m') = {0}", time);
        return list(queryWrapper);
    }

    @Override
    public void queryUserLoanBorrowList(InputObject inputObject, OutputObject outputObject) {
        QueryWrapper<LoanBorrow> queryWrapper = new QueryWrapper<>();
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        queryWrapper.eq(MybatisPlusUtil.toColumns(LoanBorrow::getApplicantId), userId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(LoanBorrow::getState), FlowableStateEnum.PASS.getKey());
        queryWrapper.ne(MybatisPlusUtil.toColumns(LoanBorrow::getPaidState), LoanPaidStateEnum.PAID.getKey());
        List<LoanBorrow> bean = list(queryWrapper);
        // 转成List<Map<String, Object>> 只有id和name
        List<Map<String, Object>> list = new ArrayList<>();
        for (LoanBorrow loanBorrow : bean) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", loanBorrow.getId());
            map.put("name", loanBorrow.getOddNumber());
            list.add(map);
        }
        outputObject.setBeans(list);
        outputObject.settotal(list.size());
    }

    @Override
    public void queryLoanBorrowPersonAnalysis(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String year = params.get("year").toString();
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
            List<Map<String, Object>> result = getLoanBorrowPersonAnalysis(startPeriod, endPeriod);
            outputObject.setBeans(result);
        } else {
            String startPeriod = year + StrUtil.DASHED + CommonNumConstants.NUM_ZERO + CommonNumConstants.NUM_ONE; // 本期开始时间
            String endPeriod = year + StrUtil.DASHED + CommonNumConstants.NUM_TWELVE;  // 本期结束时间
            List<Map<String, Object>> result = getLoanBorrowPersonAnalysis(startPeriod, endPeriod);
            outputObject.setBeans(result);
        }
    }

    private List<Map<String, Object>> getLoanBorrowPersonAnalysis(String startPeriod, String endPeriod) {
        QueryWrapper<LoanBorrow> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(LoanBorrow::getBorrowType), LoanBorrowTypeEnum.PERSONAL.getKey());
        queryWrapper.apply("date_format(" + MybatisPlusUtil.toColumns(LoanBorrow::getCreateTime) + ", '%Y-%m') >= {0}", startPeriod)
                .apply("date_format(" + MybatisPlusUtil.toColumns(LoanBorrow::getCreateTime) + ", '%Y-%m') <= {0}", endPeriod);
        queryWrapper.eq(MybatisPlusUtil.toColumns(LoanBorrow::getState), FlowableStateEnum.PASS.getKey());
        List<LoanBorrow> bean = list(queryWrapper);
        List<Map<String, Object>> result = new ArrayList<>();
        if (CollectionUtil.isEmpty(bean)) {
            return result;
        }
        // 根据借款人id分组
        Map<String, List<LoanBorrow>> map = bean.stream().collect(Collectors.groupingBy(LoanBorrow::getApplicantId));
        for (Map.Entry<String, List<LoanBorrow>> entry : map.entrySet()) {
            Map<String, Object> tempMap = new HashMap<>();
            tempMap.put("applicantId", entry.getKey());
            // 总借款单数
            tempMap.put("borrowTotalCount", entry.getValue().size());
            // 计算已经还单数量根据状态
            int paidCount = entry.getValue().stream().filter(e -> e.getPaidState() == LoanPaidStateEnum.PAID.getKey()).collect(Collectors.toList()).size();
            tempMap.put("paidCount", paidCount);
            // 计算未还款单数据量
            int notPaidCount = entry.getValue().stream().filter(e -> e.getPaidState() != LoanPaidStateEnum.PAID.getKey()).collect(Collectors.toList()).size();
            tempMap.put("notPaidCount", notPaidCount);
            // 计算借款总金额
            double borrowTotalPrice = entry.getValue().stream().mapToDouble(item -> Double.parseDouble(item.getPrice())).sum();
            tempMap.put("borrowTotalPrice", borrowTotalPrice);
            // 计算还款总金额
            double repayTotalPrice = entry.getValue().stream().mapToDouble(item -> Double.parseDouble(item.getPaidPrice())).sum();
            tempMap.put("repayTotalPrice", repayTotalPrice);
            // 计算未还款金额
            double notRepayTotalPrice = borrowTotalPrice - repayTotalPrice;
            tempMap.put("notRepayTotalPrice", notRepayTotalPrice);
            if (borrowTotalPrice == repayTotalPrice) {
                tempMap.put("state", LoanPaidStateEnum.PAID.getKey());
            } else if (repayTotalPrice > 0) {
                tempMap.put("state", LoanPaidStateEnum.PART_PAID.getKey());
            } else {
                tempMap.put("state", LoanPaidStateEnum.NOT_PAID.getKey());
            }

            result.add(tempMap);
        }
        iAuthUserService.setMationForMap(result, "applicantId", "applicantMation");
        return result;
    }
}
