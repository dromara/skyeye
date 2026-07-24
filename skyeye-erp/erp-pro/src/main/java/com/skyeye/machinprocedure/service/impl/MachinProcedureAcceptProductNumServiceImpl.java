package com.skyeye.machinprocedure.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.machinprocedure.dao.MachinProcedureAcceptProductNumDao;
import com.skyeye.machinprocedure.entity.MachinProcedureAcceptProductNum;
import com.skyeye.machinprocedure.service.MachinProcedureAcceptProductNumService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@SkyeyeService(name = "工序验收员工生产数量", groupName = "工序验收", manageShow = false)
public class MachinProcedureAcceptProductNumServiceImpl extends SkyeyeBusinessServiceImpl<MachinProcedureAcceptProductNumDao, MachinProcedureAcceptProductNum> implements MachinProcedureAcceptProductNumService {

    @Override
    public void writeList(String parentId, List<MachinProcedureAcceptProductNum> machinProcedureAcceptProductNumList) {
        deleteByParentId(parentId);
        if (CollectionUtil.isEmpty(machinProcedureAcceptProductNumList)) {
            return;
        }
        for (MachinProcedureAcceptProductNum productNum : machinProcedureAcceptProductNumList) {
            productNum.setParentId(parentId);
        }
        createEntity(machinProcedureAcceptProductNumList, StrUtil.EMPTY);
    }

    @Override
    public void deleteByParentId(String parentId) {
        QueryWrapper<MachinProcedureAcceptProductNum> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(MachinProcedureAcceptProductNum::getParentId), parentId);
        remove(queryWrapper);
    }

    @Override
    public List<MachinProcedureAcceptProductNum> queryListByParentIds(List<String> acceptIdList) {
        if (CollectionUtil.isEmpty(acceptIdList)) {
            return new ArrayList<>();
        }
        QueryWrapper<MachinProcedureAcceptProductNum> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(MachinProcedureAcceptProductNum::getParentId), acceptIdList);
        return list(queryWrapper);
    }

    @Override
    public List<MachinProcedureAcceptProductNum> queryMachinProcedureAcceptProductNumByStaffId(String staffId) {
        QueryWrapper<MachinProcedureAcceptProductNum> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(MachinProcedureAcceptProductNum::getStaffId), staffId);
        return list(queryWrapper);
    }

    @Override
    public List<MachinProcedureAcceptProductNum> queryListByParentIdOnly(String parentId) {
        QueryWrapper<MachinProcedureAcceptProductNum> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(MachinProcedureAcceptProductNum::getParentId), parentId);
        return list(queryWrapper);
    }

}
