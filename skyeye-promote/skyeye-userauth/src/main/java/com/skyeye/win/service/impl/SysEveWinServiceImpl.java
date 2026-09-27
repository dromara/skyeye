/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.win.service.impl;

import cn.hutool.core.util.StrUtil;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.tenant.TenantTypeEnum;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.exception.CustomException;
import com.skyeye.win.dao.SysEveWinDao;
import com.skyeye.win.entity.SysWin;
import com.skyeye.win.service.SysEveWinService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: SysEveWinServiceImpl
 * @Description: 服务信息管理服务类--平台租户
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 23:26
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "前台服务管理", groupName = "基础模块", tenant = TenantEnum.PLATE, allowDynamicAttrKey = false)
public class SysEveWinServiceImpl extends SkyeyeBusinessServiceImpl<SysEveWinDao, SysWin> implements SysEveWinService {

    @Override
    @IgnoreTenant
    public void queryPageList(InputObject inputObject, OutputObject outputObject) {
        super.queryPageList(inputObject, outputObject);
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        if (tenantEnable) {
            String tenantId = TenantContext.getTenantId();
            if (!StrUtil.equals(tenantId, TenantTypeEnum.PLATFORM.getCode())) {
                throw new CustomException("该接口只能平台租户调用！");
            }
            commonPageInfo.setTenantId(tenantId);
        }
        List<Map<String, Object>> beans = skyeyeBaseMapper.queryWinMationList(commonPageInfo);
        return beans;
    }

    @Override
    protected void deletePreExecution(String id) {
        Map<String, Object> bean = skyeyeBaseMapper.queryChildMationById(id);
        if (Integer.parseInt(bean.get("menuNum").toString()) > 0) {
            throw new CustomException("该服务存在功能菜单，请先进行菜单移除操作。");
        }
    }

    @Override
    public void querySysEveWinList(InputObject inputObject, OutputObject outputObject) {
        List<Map<String, Object>> beans = skyeyeBaseMapper.querySysEveWinList();
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }

    @Override
    @IgnoreTenant
    public List<SysWin> selectByIds(String... ids) {
        return super.selectByIds(ids);
    }
}
