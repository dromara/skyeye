package com.skyeye.feeapplication.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.feeapplication.dao.FeeApplicationDao;
import com.skyeye.feeapplication.entity.FeeApplication;
import com.skyeye.feeapplication.service.FeeApplicationService;
import com.skyeye.organization.service.IDepmentService;
import com.skyeye.rest.project.service.IProProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: FeeApplicationServiceImpl
 * @Description: 费用申请服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/5 14:17
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "费用申请", groupName = "费用申请", flowable = true)
public class FeeApplicationServiceImpl extends SkyeyeBusinessServiceImpl<FeeApplicationDao, FeeApplication> implements FeeApplicationService {

    @Autowired
    private IDepmentService iDepmentService;

    @Autowired
    private IProProjectService iProProjectService;

    @Override
    protected void validatorEntity(FeeApplication entity) {
        super.validatorEntity(entity);
        // 只能申请当月的费用申请
        String todayDate = DateUtil.getPointTime(DateUtil.YYYY_MM);
        String invoiceDate = entity.getInvoiceDate();
        if (!todayDate.equals(invoiceDate.substring(0, 7))) {
            throw new CustomException("只能申请当月的费用申请");
        }
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
    public FeeApplication selectById(String id) {
        FeeApplication feeApplication = super.selectById(id);
        iDepmentService.setDataMation(feeApplication, FeeApplication::getDepartmentId);
        iAuthUserService.setDataMation(feeApplication, FeeApplication::getApplicantId);
        iProProjectService.setDataMation(feeApplication, FeeApplication::getProjectId);
        return feeApplication;
    }

    @Override
    public void queryFeeApplicationAnalysis(InputObject inputObject, OutputObject outputObject) {
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
            List<Map<String, Object>> result = getBeans(startPeriod, endPeriod);
            outputObject.setBeans(result);
        } else {
            String startPeriod = year + StrUtil.DASHED + CommonNumConstants.NUM_ZERO + CommonNumConstants.NUM_ONE; // 本期开始时间
            String endPeriod = year + StrUtil.DASHED + CommonNumConstants.NUM_TWELVE;  // 本期结束时间
            List<Map<String, Object>> result = getBeans(startPeriod, endPeriod);
            outputObject.setBeans(result);
        }
    }

    /**
     * 时间格式 YYYY-MM
     * */
    @Override
    public List<FeeApplication> queryFeeApplicationList(String  time) {
        QueryWrapper<FeeApplication> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(FeeApplication::getState), FlowableStateEnum.PASS.getKey());
        queryWrapper.apply("date_format(" + MybatisPlusUtil.toColumns(FeeApplication::getInvoiceDate) + ", '%Y-%m') = {0}", time);
        return list(queryWrapper);
    }

    private List<Map<String, Object>> getBeans(String startPeriod, String endPeriod) {
        QueryWrapper<FeeApplication> queryWrapper = new QueryWrapper<>();
        queryWrapper.apply("date_format(" + MybatisPlusUtil.toColumns(FeeApplication::getInvoiceDate) + ", '%Y-%m') >= {0}", startPeriod)
                .apply("date_format(" + MybatisPlusUtil.toColumns(FeeApplication::getInvoiceDate) + ", '%Y-%m') <= {0}", endPeriod);
        List<FeeApplication> bean = list(queryWrapper);
        List<Map<String, Object>> result = new ArrayList<>();
        if (CollectionUtil.isEmpty(bean)) {
            return result;
        }
        // 按typeId分组
        Map<String, List<FeeApplication>> map = bean.stream().collect(Collectors.groupingBy(FeeApplication::getTypeId));
        //求金额
        for (Map.Entry<String, List<FeeApplication>> entry : map.entrySet()) {
            String price = "0";
            for (FeeApplication feeApplication : entry.getValue()) {
                price = CalculationUtil.add(CommonNumConstants.NUM_TWO,
                        StrUtil.isEmpty(feeApplication.getPrice()) ? "0" : feeApplication.getPrice(),
                        price);
            }
            Map<String, Object> deptInfo = new HashMap<>();
            deptInfo.put("typeId", entry.getKey());
            deptInfo.put("price", price);
            result.add(deptInfo);
        }
        return result;
    }

    @Override
    public void queryDepartmentFeeAnalysis(InputObject inputObject, OutputObject outputObject) {
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
            List<Map<String, Object>> result = getDepartmentFeeAnalysis(startPeriod, endPeriod);
            outputObject.setBeans(result);
        } else {
            String startPeriod = year + StrUtil.DASHED + CommonNumConstants.NUM_ZERO + CommonNumConstants.NUM_ONE; // 本期开始时间
            String endPeriod = year + StrUtil.DASHED + CommonNumConstants.NUM_TWELVE;  // 本期结束时间
            List<Map<String, Object>> result = getDepartmentFeeAnalysis(startPeriod, endPeriod);
            outputObject.setBeans(result);
        }
    }

    private List<Map<String, Object>> getDepartmentFeeAnalysis(String startPeriod, String endPeriod) {
        QueryWrapper<FeeApplication> queryWrapper = new QueryWrapper<>();
        queryWrapper.apply("date_format(" + MybatisPlusUtil.toColumns(FeeApplication::getInvoiceDate) + ", '%Y-%m') >= {0}", startPeriod)
                .apply("date_format(" + MybatisPlusUtil.toColumns(FeeApplication::getInvoiceDate) + ", '%Y-%m') <= {0}", endPeriod);
        List<FeeApplication> bean = list(queryWrapper);
        List<Map<String, Object>> result = new ArrayList<>();
        if (CollectionUtil.isEmpty(bean)) {
            return result;
        }
        // 计算总金额使用stream流，SUM求和
        Double totalAmount = bean.stream().mapToDouble(item->Double.parseDouble(item.getPrice())).sum();
        // 根据部门id分组
        Map<String, List<FeeApplication>> groupByDepartmentId = bean.stream().collect(Collectors.groupingBy(FeeApplication::getDepartmentId));
        for (Map.Entry<String, List<FeeApplication>> entry : groupByDepartmentId.entrySet()) {
            Map<String,Object> resultMap = new HashMap<>();
            List<Map<String, Object>> temp = new ArrayList<>();
            //根据typeId分组
            Map<String, List<FeeApplication>> groupByTypeId = entry.getValue().stream().collect(Collectors.groupingBy(FeeApplication::getTypeId));
            for (Map.Entry<String, List<FeeApplication>> typeEntry : groupByTypeId.entrySet()) {
                Map<String, Object> map = new HashMap<>();
                map.put("typeId", typeEntry.getKey());
                // 求金额
                String price = "0";
                for (FeeApplication feeApplication : typeEntry.getValue()) {
                    price = CalculationUtil.add(CommonNumConstants.NUM_TWO,
                            StrUtil.isEmpty(feeApplication.getPrice()) ? "0" : feeApplication.getPrice(),
                            price);
                }
                map.put("price", price);
                temp.add(map);
            }
            // 计算每个部门合计花费
            String totalPrice = "0";
            for (Map<String, Object> map : temp) {
                totalPrice = CalculationUtil.add(CommonNumConstants.NUM_TWO, totalPrice, map.get("price").toString());
            }
            // 计算占比
            if(Double.parseDouble(totalPrice) > 0){
                double rate = Double.parseDouble(totalPrice) /totalAmount *100;
                resultMap.put("rate", String.format("%.2f", rate) + "%");
            }else {
                resultMap.put("rate", "0%");
            }
            resultMap.put("departmentId", entry.getKey());
            resultMap.put("typePriceList", temp);
            resultMap.put("totalPrice", totalPrice);
            result.add(resultMap);
        }
        // result根据部门id分组
        iDepmentService.setMationForMap(result,"departmentId","departmentMation");
        return result;
    }
}
