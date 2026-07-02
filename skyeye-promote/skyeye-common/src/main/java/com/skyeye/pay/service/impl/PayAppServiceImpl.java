/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.pay.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.pay.dao.PayAppDao;
import com.skyeye.pay.entity.PayApp;
import com.skyeye.pay.service.PayAppService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 支付应用服务：维护 PayApp 配置，并解析两层回调地址。
 */
@Service
@SkyeyeService(name = "支付应用管理", groupName = "支付应用管理", tenant = TenantEnum.PLATE)
public class PayAppServiceImpl extends SkyeyeBusinessServiceImpl<PayAppDao, PayApp> implements PayAppService {

    @Override
    public void updatePrepose(PayApp payApp) {
        verify(payApp.getId());
    }

    private void verify(String id) {
        QueryWrapper<PayApp> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CommonConstants.ID, id);
        PayApp one = getOne(queryWrapper);
        if (ObjectUtil.isEmpty(one)) {
            throw new CustomException("该支付应用信息不存在");
        }
    }

    @Override
    protected void writePostpose(PayApp entity, String userId) {
        super.writePostpose(entity, userId);
    }

    public List<Map<String, Object>> queryDataList(InputObject inputObject) {
        QueryWrapper<PayApp> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(PayApp::getEnabled), CommonNumConstants.NUM_ONE);
        List<PayApp> list = list(queryWrapper);
        return JSONUtil.toList(JSONUtil.toJsonStr(list), null);
    }

    @Override
    @IgnoreTenant
    public PayApp getEnabledPayApp() {
        // 支付模块不隔离租户，需忽略租户上下文查询平台级配置
        QueryWrapper<PayApp> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(PayApp::getEnabled), EnableEnum.ENABLE_USING.getKey());
        PayApp payApp = getOne(queryWrapper, false);
        if (ObjectUtil.isEmpty(payApp)) {
            throw new CustomException("未配置已启用的支付应用，请先在支付应用中完成配置");
        }
        return payApp;
    }

    @Override
    @IgnoreTenant
    public PayApp getEnabledPayAppByAppKey(String appKey) {
        if (StrUtil.isBlank(appKey)) {
            throw new CustomException("支付应用标识(appKey)不能为空");
        }
        QueryWrapper<PayApp> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(PayApp::getAppKey), appKey.trim());
        queryWrapper.eq(MybatisPlusUtil.toColumns(PayApp::getEnabled), EnableEnum.ENABLE_USING.getKey());
        PayApp payApp = getOne(queryWrapper, false);
        if (ObjectUtil.isEmpty(payApp)) {
            throw new CustomException(String.format("未找到已启用的支付应用[%s]，请先在支付应用中配置并启用", appKey));
        }
        return payApp;
    }

    @Override
    @IgnoreTenant
    public String buildChannelOrderNotifyUrl(PayApp payApp, String channelId) {
        // 注册到微信/支付宝的 notify 地址，每个渠道 id 唯一，便于 PayNotify 路由到对应 PayClient
        if (ObjectUtil.isEmpty(payApp) || StrUtil.isBlank(payApp.getChannelNotifyUrl())) {
            throw new CustomException("请在支付应用中配置渠道回调地址(channelNotifyUrl)");
        }
        return StrUtil.removeSuffix(payApp.getChannelNotifyUrl().trim(), "/") + "/" + channelId;
    }

    @Override
    @IgnoreTenant
    public String getBusinessOrderNotifyUrl(PayApp payApp) {
        // pay 模块验签成功后 HTTP 转发到此地址，由具体业务完成订单状态变更与权益交付
        if (ObjectUtil.isEmpty(payApp) || StrUtil.isBlank(payApp.getOrderNotifyUrl())) {
            throw new CustomException("请在支付应用中配置业务支付回调地址(orderNotifyUrl)");
        }
        return payApp.getOrderNotifyUrl();
    }

    @Override
    @IgnoreTenant
    public <M> void setDataMation(M bean, SFunction<M, ?> sFunction) {
        super.setDataMation(bean, sFunction);
    }
}
