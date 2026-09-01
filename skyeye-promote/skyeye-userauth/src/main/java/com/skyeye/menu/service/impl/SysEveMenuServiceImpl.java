/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.menu.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.dsform.entity.DsFormPage;
import com.skyeye.dsform.service.DsFormPageService;
import com.skyeye.exception.CustomException;
import com.skyeye.menu.dao.SysEveMenuDao;
import com.skyeye.menu.entity.SysMenu;
import com.skyeye.menu.entity.SysMenuQueryDo;
import com.skyeye.menu.service.SysEveMenuService;
import com.skyeye.operate.classenum.MenuPageType;
import com.skyeye.rest.report.service.IReportPageService;
import com.skyeye.server.entity.ServiceBeanCustom;
import com.skyeye.server.service.ServiceBeanCustomService;
import com.skyeye.win.service.SysEveDesktopService;
import com.skyeye.win.service.SysEveWinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: SysEveMenuServiceImpl
 * @Description: 菜单管理
 * @author: skyeye云系列--卫志强
 * @date: 2022/9/3 11:24
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "菜单管理", groupName = "菜单管理", tenant = TenantEnum.PLATE, memoryCache = true)
public class SysEveMenuServiceImpl extends SkyeyeBusinessServiceImpl<SysEveMenuDao, SysMenu> implements SysEveMenuService {

    @Autowired
    private SysEveMenuDao sysEveMenuDao;

    @Autowired
    private SysEveDesktopService sysEveDesktopService;

    @Autowired
    private SysEveWinService sysEveWinService;

    @Autowired
    private DsFormPageService dsFormPageService;

    @Autowired
    private ServiceBeanCustomService serviceBeanCustomService;

    /**
     * 菜单链接打开类型，父菜单默认为1.1：打开iframe，2：打开html。
     */
    public static final Integer SYS_MENU_OPEN_TYPE_IS_IFRAME = 1;
    public static final Integer SYS_MENU_OPEN_TYPE_IS_HTML = 2;

    /**
     * 菜单类型
     */
    public static final String SYS_MENU_TYPE_IS_IFRAME = "win";
    public static final String SYS_MENU_TYPE_IS_HTML = "html";

    @Autowired
    private IReportPageService iReportPageService;

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        SysMenuQueryDo sysMenuQuery = inputObject.getParams(SysMenuQueryDo.class);
        List<Map<String, Object>> beans = sysEveMenuDao.querySysMenuList(sysMenuQuery);
        List<String> ids = beans.stream().map(bean -> bean.get("id").toString()).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(ids)) {
            return beans;
        }
        // 查询子节点信息(包含当前节点)
        List<String> childIds = sysEveMenuDao.queryAllChildIdsByParentId(ids);
        beans = selectMapByIds(childIds).values().stream().map(bean -> BeanUtil.beanToMap(bean)).collect(Collectors.toList());
        beans = beans.stream()
            .sorted(Comparator.comparing(bean -> Integer.parseInt(bean.get("orderNum").toString()))).collect(Collectors.toList());
        beans.forEach(bean -> {
            bean.put("lay_is_open", true);
        });
        return beans;
    }

    @Override
    public void validatorEntity(SysMenu entity) {
        super.validatorEntity(entity);
        if (StrUtil.equals(entity.getId(), entity.getParentId())) {
            throw new CustomException("父菜单不能为自己！");
        }
    }

    @Override
    public void createPrepose(SysMenu entity) {
        // 设置菜单链接打开类型
        setOpenType(entity);
        // 设置菜单级别
        setMenuLevel(entity);
    }

    @Override
    public void updatePrepose(SysMenu entity) {
        // 设置菜单链接打开类型
        setOpenType(entity);
        // 设置菜单级别
        setMenuLevel(entity);
    }

    private void setMenuLevel(SysMenu sysMenu) {
        if ("0".equals(sysMenu.getParentId())) {
            sysMenu.setLevel(0);
        } else {
            sysMenu.setLevel(CommonNumConstants.NUM_ONE);
        }
    }

    private void setOpenType(SysMenu sysMenu) {
        if (SYS_MENU_TYPE_IS_IFRAME.equals(sysMenu.getType())) {
            // iframe
            sysMenu.setOpenType(SYS_MENU_OPEN_TYPE_IS_IFRAME);
        } else if (SYS_MENU_TYPE_IS_HTML.equals(sysMenu.getType())) {
            // html
            sysMenu.setOpenType(SYS_MENU_OPEN_TYPE_IS_HTML);
        }
    }

    @Override
    public void deletePreExecution(String id) {
        // 判断菜单有没有角色使用，没有则可以删除
        Map<String, Object> useMenuBean = sysEveMenuDao.queryUseThisMenuRoleById(id);
        if (CollectionUtil.isNotEmpty(useMenuBean)) {
            if (Integer.parseInt(useMenuBean.get("roleNum").toString()) > 0) {
                throw new CustomException("该菜单正在被一个或多个角色使用，无法删除。");
            }
        }
    }

    @Override
    public void deletePostpose(String id) {
        // 删除子菜单
        deleteByParentId(id);
    }

    private void deleteByParentId(String parentId) {
        QueryWrapper<SysMenu> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(SysMenu::getParentId), parentId);
        remove(queryWrapper);
    }

    /**
     * 根据父菜单ID查看子菜单
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void querySysMenuMationBySimpleLevel(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        List<Map<String, Object>> beans = sysEveMenuDao.querySysMenuMationBySimpleLevel(map);
        // 桌面信息
        sysEveDesktopService.setMationForMap(beans, "desktopId", "desktopMation");
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }

    /**
     * 全部系统菜单树，供 AI 技能「打开页面」等下拉选用。
     */
    @Override
    public void queryAllSysMenuTreeList(InputObject inputObject, OutputObject outputObject) {
        QueryWrapper<SysMenu> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(SysMenu::getOrderNum));
        List<SysMenu> menuList = list(queryWrapper);
        List<Map<String, Object>> beans = menuList.stream().map(menu -> {
            Map<String, Object> row = new HashMap<>();
            row.put("id", menu.getId());
            row.put("name", menu.getName());
            row.put("parentId", StrUtil.blankToDefault(menu.getParentId(), "0"));
            row.put("path", StrUtil.nullToEmpty(menu.getPath()));
            row.put("pageUrl", StrUtil.nullToEmpty(menu.getPageUrl()));
            row.put("orderNum", menu.getOrderNum() == null ? 0 : menu.getOrderNum());
            row.put("level", menu.getLevel());
            return row;
        }).collect(Collectors.toList());
        beans = ToolUtil.listToTree(beans, "id", "parentId", "children");
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }

    @Override
    public SysMenu selectById(String id) {
        SysMenu sysMenu = super.selectById(id);
        if (StrUtil.isEmpty(sysMenu.getId())) {
            return null;
        }

        sysEveDesktopService.setDataMation(sysMenu, SysMenu::getDesktopId);
        sysEveWinService.setDataMation(sysMenu, SysMenu::getSysWinId);

        if (sysMenu.getPageType() == MenuPageType.LAYOUT.getKey()) {
            // 表单布局
            DsFormPage dsFormPage = dsFormPageService.getDataFromDb(sysMenu.getPageUrl());
            ServiceBeanCustom serviceBeanCustom = serviceBeanCustomService.selectServiceBeanCustom(dsFormPage.getAppId(), dsFormPage.getClassName());
            dsFormPage.setServiceBeanCustom(serviceBeanCustom);
            sysMenu.setDsFormPage(dsFormPage);
        } else if (sysMenu.getPageType() == MenuPageType.REPORT.getKey()) {
            // 报表页面
            sysMenu.setReportPage(iReportPageService.queryDataMationById(sysMenu.getPageUrl()));
        }
        if (!sysMenu.getParentId().equals("0")) {
            sysMenu.setParentMenu(selectById(sysMenu.getParentId()));
        }
        return sysMenu;
    }

    @Override
    @IgnoreTenant
    public List<SysMenu> selectByIds(String... ids) {
        List<SysMenu> sysMenuList = super.selectByIds(ids);
        // 桌面信息
        sysEveDesktopService.setDataMation(sysMenuList, SysMenu::getDesktopId);
        // 服务信息
        sysEveWinService.setDataMation(sysMenuList, SysMenu::getSysWinId);
        return sysMenuList;
    }

}
