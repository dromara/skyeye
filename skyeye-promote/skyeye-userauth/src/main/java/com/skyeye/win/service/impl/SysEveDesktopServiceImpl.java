/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.win.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.DeleteFlagEnum;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.win.dao.SysEveDesktopDao;
import com.skyeye.win.entity.SysDesktop;
import com.skyeye.win.service.SysEveDesktopService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: SysEveDesktopServiceImpl
 * @Description: 桌面信息管理服务层--平台隔离
 * @author: skyeye云系列--卫志强
 * @date: 2023/2/22 19:22
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "桌面信息管理", groupName = "基础模块", tenant = TenantEnum.PLATE, allowDynamicAttrKey = false)
public class SysEveDesktopServiceImpl extends SkyeyeBusinessServiceImpl<SysEveDesktopDao, SysDesktop> implements SysEveDesktopService {

    @Autowired
    private SysEveDesktopDao sysEveDesktopDao;

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        commonPageInfo.setDeleteFlag(DeleteFlagEnum.NOT_DELETE.getKey());
        List<Map<String, Object>> beans = sysEveDesktopDao.querySysDesktopList(commonPageInfo);
        return beans;
    }

    @Override
    public void validatorEntity(SysDesktop entity) {
        QueryWrapper<SysDesktop> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SysDesktop::getDeleteFlag), DeleteFlagEnum.NOT_DELETE.getKey());
        queryWrapper.and(wrapper ->
            wrapper.eq(MybatisPlusUtil.toColumns(SysDesktop::getName), entity.getName())
                .or().eq(MybatisPlusUtil.toColumns(SysDesktop::getDesktopCode), entity.getDesktopCode()));
        if (StringUtils.isNotEmpty(entity.getId())) {
            queryWrapper.ne(CommonConstants.ID, entity.getId());
        }
        SysDesktop checkSysEveDesktop = getOne(queryWrapper);
        if (ObjectUtil.isNotEmpty(checkSysEveDesktop)) {
            throw new CustomException("该名称/编码已存在，请更换.");
        }
    }

    @Override
    protected void deletePreExecution(String id) {
        Map<String, Object> bean = sysEveDesktopDao.querySysDesktopStateAndMenuNumById(id);
        if (!"0".equals(bean.get("allNum").toString())) {
            throw new CustomException("该桌面下包含有子菜单，无法删除.");
        }
    }

    /**
     * 获取全部的桌面用于系统菜单
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryAllSysDesktopList(InputObject inputObject, OutputObject outputObject) {
        List<Map<String, Object>> beans = queryAllDataForMap();
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }

    @Override
    @IgnoreTenant
    public List<SysDesktop> selectByIds(String... ids) {
        return super.selectByIds(ids);
    }

    @Override
    @IgnoreTenant
    public List<Map<String, Object>> queryAllDataForMap() {
        List<Map<String, Object>> desktopList = super.queryAllDataForMap();
        desktopList.forEach(desktop -> {
            desktop.put("pId", "0");
            desktop.put("sysName", "基础系统");
            desktop.put("pageType", "桌面");
            desktop.put("type", "page");
        });
        desktopList = desktopList.stream()
            .sorted(Comparator.comparing(bean -> Integer.parseInt(bean.get("orderBy").toString()))).collect(Collectors.toList());
        return desktopList;
    }

    @Override
    @IgnoreTenant
    public List<SysDesktop> queryAllData() {
        return super.queryAllData();
    }
}
