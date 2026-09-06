/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.loan.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.loan.dao.LoanRepayDao;
import com.skyeye.loan.entity.LoanBorrow;
import com.skyeye.loan.entity.LoanRepay;
import com.skyeye.loan.service.LoanBorrowService;
import com.skyeye.loan.service.LoanRepayService;
import com.skyeye.loan.service.UserLoanService;
import com.skyeye.rest.project.service.IProProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: LoanRepayServiceImpl
 * @Description: 还款单服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/5 14:20
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "还款单", groupName = "还款单", flowable = true)
public class LoanRepayServiceImpl extends SkyeyeBusinessServiceImpl<LoanRepayDao, LoanRepay> implements LoanRepayService {

    @Autowired
    private UserLoanService userLoanService;

    @Autowired
    private LoanBorrowService loanBorrowService;

    @Autowired
    private IProProjectService iProProjectService;

    @Override
    public void validatorEntity(LoanRepay entity) {
        super.validatorEntity(entity);
        entity.setRepayTime(entity.getRepayTime().substring(0, 10));
        if(StrUtil.isNotEmpty(entity.getLoanBorrowId())){
            LoanBorrow loanBorrow = loanBorrowService.selectById(entity.getLoanBorrowId());
            double notPrice = Double.parseDouble(loanBorrow.getPrice())-Double.parseDouble(loanBorrow.getPaidPrice());
            if (Double.parseDouble(entity.getPrice())>notPrice){
                throw new CustomException("还款金额不能大于未还金额");
            }
        }
    }

    @Override
    public QueryWrapper<LoanRepay> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<LoanRepay> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isNotEmpty(commonPageInfo.getObjectId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(LoanRepay::getProjectId), commonPageInfo.getObjectId());
        } else {
            // 我创建的
            queryWrapper.eq(MybatisPlusUtil.toColumns(LoanRepay::getCreateId), InputObject.getLogParamsStatic().get("id").toString());
        }
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        // 借款单信息
        loanBorrowService.setMationForMap(beans, "loanBorrowId", "loanBorrowMation");
        iProProjectService.setMationForMap(beans, "projectId", "projectMation");
        return beans;
    }

    @Override
    public LoanRepay selectById(String id) {
        LoanRepay loanRepay = super.selectById(id);
        iSysDictDataService.setDataMation(loanRepay, LoanRepay::getPayTypeId);
        loanBorrowService.setDataMation(loanRepay, LoanRepay::getLoanBorrowId);
        iProProjectService.setDataMation(loanRepay, LoanRepay::getProjectId);
        return loanRepay;
    }

    @Override
    public void approvalEndIsSuccess(LoanRepay entity) {
        userLoanService.calcUserLoanPrice(entity.getCreateId(), entity.getPrice(), false);
        if (StrUtil.isNotEmpty(entity.getLoanBorrowId())) {
            // 更新借款单状态
            loanBorrowService.updateLoanBorrowStatePrice(entity.getLoanBorrowId(), entity.getPrice());
        }
    }

    @Override
    public List<LoanRepay> queryLoanRepayList(String time) {
        QueryWrapper<LoanRepay> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(LoanRepay::getState), FlowableStateEnum.PASS.getKey());
        queryWrapper.apply("date_format(" + MybatisPlusUtil.toColumns(LoanRepay::getCreateTime) + ", '%Y-%m') = {0}", time);
        return list(queryWrapper);
    }
}
