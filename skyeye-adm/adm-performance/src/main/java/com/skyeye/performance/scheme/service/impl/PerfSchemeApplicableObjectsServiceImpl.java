package com.skyeye.performance.scheme.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.performance.scheme.dao.PerfSchemeApplicableObjectsDao;
import com.skyeye.performance.scheme.entity.PerfSchemeApplicableObjects;
import com.skyeye.performance.scheme.service.PerfSchemeApplicableObjectsService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@SkyeyeService(name = "绩效考核方案适用对象", groupName = "员工绩效", manageShow = false)
public class PerfSchemeApplicableObjectsServiceImpl
    extends SkyeyeBusinessServiceImpl<PerfSchemeApplicableObjectsDao, PerfSchemeApplicableObjects>
    implements PerfSchemeApplicableObjectsService {

    @Override
    public void deleteBySchemeId(String schemeId) {
        QueryWrapper<PerfSchemeApplicableObjects> wrapper = new QueryWrapper<>();
        wrapper.eq(MybatisPlusUtil.toColumns(PerfSchemeApplicableObjects::getSchemeId), schemeId);
        remove(wrapper);
    }

    @Override
    public void saveApplicableObjects(String schemeId, List<PerfSchemeApplicableObjects> list) {
        deleteBySchemeId(schemeId);
        if (CollectionUtil.isEmpty(list)) {
            return;
        }
        for (PerfSchemeApplicableObjects item : list) {
            item.setSchemeId(schemeId);
        }
        createEntity(list, StrUtil.EMPTY);
    }

    @Override
    public List<PerfSchemeApplicableObjects> queryBySchemeId(String schemeId) {
        QueryWrapper<PerfSchemeApplicableObjects> wrapper = new QueryWrapper<>();
        wrapper.eq(MybatisPlusUtil.toColumns(PerfSchemeApplicableObjects::getSchemeId), schemeId);
        return list(wrapper);
    }

    @Override
    public Map<String, List<PerfSchemeApplicableObjects>> queryBySchemeIds(List<String> schemeIds) {
        QueryWrapper<PerfSchemeApplicableObjects> wrapper = new QueryWrapper<>();
        wrapper.in(MybatisPlusUtil.toColumns(PerfSchemeApplicableObjects::getSchemeId), schemeIds);
        return list(wrapper).stream().collect(Collectors.groupingBy(PerfSchemeApplicableObjects::getSchemeId));
    }
}
