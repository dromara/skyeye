package com.skyeye.performance.scheme.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.performance.scheme.dao.PerfSchemeItemDao;
import com.skyeye.performance.scheme.entity.PerfSchemeItem;
import com.skyeye.performance.scheme.service.PerfSchemeItemService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@SkyeyeService(name = "绩效考核方案指标", groupName = "员工绩效", manageShow = false)
public class PerfSchemeItemServiceImpl extends SkyeyeBusinessServiceImpl<PerfSchemeItemDao, PerfSchemeItem> implements PerfSchemeItemService {

    @Override
    public void deleteBySchemeId(String schemeId) {
        QueryWrapper<PerfSchemeItem> wrapper = new QueryWrapper<>();
        wrapper.eq(MybatisPlusUtil.toColumns(PerfSchemeItem::getSchemeId), schemeId);
        remove(wrapper);
    }

    @Override
    public void saveSchemeItems(String schemeId, List<PerfSchemeItem> itemList) {
        deleteBySchemeId(schemeId);
        if (CollectionUtil.isEmpty(itemList)) {
            return;
        }
        for (PerfSchemeItem item : itemList) {
            item.setSchemeId(schemeId);
        }
        createEntity(itemList, StrUtil.EMPTY);
    }

    @Override
    public List<PerfSchemeItem> queryBySchemeId(String schemeId) {
        QueryWrapper<PerfSchemeItem> wrapper = new QueryWrapper<>();
        wrapper.eq(MybatisPlusUtil.toColumns(PerfSchemeItem::getSchemeId), schemeId);
        wrapper.orderByAsc(MybatisPlusUtil.toColumns(PerfSchemeItem::getOrderBy));
        return list(wrapper);
    }

    @Override
    public Map<String, List<PerfSchemeItem>> queryBySchemeIds(List<String> schemeIds) {
        QueryWrapper<PerfSchemeItem> wrapper = new QueryWrapper<>();
        wrapper.in(MybatisPlusUtil.toColumns(PerfSchemeItem::getSchemeId), schemeIds);
        wrapper.orderByAsc(MybatisPlusUtil.toColumns(PerfSchemeItem::getOrderBy));
        return list(wrapper).stream().collect(Collectors.groupingBy(PerfSchemeItem::getSchemeId));
    }
}
