/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.pay.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.pay.core.PayClient;
import com.skyeye.pay.core.PayClientConfig;
import com.skyeye.pay.core.PayClientFactory;
import com.skyeye.pay.dao.PayChannelDao;
import com.skyeye.pay.entity.PayApp;
import com.skyeye.pay.entity.PayChannel;
import com.skyeye.pay.enums.PayType;
import com.skyeye.pay.service.PayAppService;
import com.skyeye.pay.service.PayChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Validator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: PayChannelServiceImpl
 * @Description: 支付渠道服务层--平台隔离
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/9 14:31
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "支付渠道", groupName = "支付渠道", tenant = TenantEnum.PLATE)
public class PayChannelServiceImpl extends SkyeyeBusinessServiceImpl<PayChannelDao, PayChannel> implements PayChannelService {

    @Autowired
    private PayAppService payAppService;

    @Autowired
    private PayClientFactory payClientFactory;

    @Autowired
    private Validator validator;

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        payAppService.setMationForMap(beans, "appId", "appMation");
        return beans;
    }

    @Override
    @IgnoreTenant
    public PayChannel selectById(String id) {
        PayChannel payChannel = super.selectById(id);
        payAppService.setDataMation(payChannel, PayChannel::getAppId);
        payChannel.setCodeNumMation(PayType.getMation(payChannel.getCodeNum()));

        // 解析配置
        Class<? extends PayClientConfig> payClass = PayType.getByCode(payChannel.getCodeNum()).getConfigClass();
        if (ObjectUtil.isNull(payClass)) {
            throw new CustomException("支付渠道的配置不存在");
        }
        PayClientConfig config = JSONUtil.toBean(payChannel.getConfig(), payClass);
        payChannel.setConfigMation(config);

        return payChannel;
    }

    @Override
    public void validatorEntity(PayChannel entity) {
        super.validatorEntity(entity);
        // 解析配置
        Class<? extends PayClientConfig> payClass = PayType.getByCode(entity.getCodeNum()).getConfigClass();
        if (ObjectUtil.isNull(payClass)) {
            throw new CustomException("支付渠道的配置不存在");
        }
        PayClientConfig config = JSONUtil.toBean(entity.getConfig(), payClass);
        Assert.notNull(config);
        // 验证参数
        config.validate(validator);
    }

    @Override
    public void updatePrepose(PayChannel payChannel) {
        QueryWrapper<PayChannel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CommonConstants.ID, payChannel.getId());
        PayChannel one = getOne(queryWrapper);
        if (ObjectUtil.isEmpty(one)) {
            throw new CustomException("该支付应用信息不存在");
        }
    }

    @Override
    @IgnoreTenant
    public PayClient getPayClient(String id) {
        PayChannel payChannel = selectById(id);
        if (ObjectUtil.isEmpty(payChannel)) {
            throw new CustomException("该支付渠道不存在");
        }
        return payClientFactory.createOrUpdatePayClient(id, payChannel.getCodeNum(), payChannel.getConfigMation());
    }

    @Override
    @IgnoreTenant
    public PayChannel getPayChannelByCode(String codeNum) {
        return getPayChannelByCode(null, codeNum);
    }

    @Override
    @IgnoreTenant
    public PayChannel getPayChannelByCode(String appKey, String codeNum) {
        MPJLambdaWrapper<PayChannel> queryWrapper = new MPJLambdaWrapper<PayChannel>()
            .innerJoin(PayApp.class, PayApp::getId, PayChannel::getAppId)
            .eq(PayApp::getEnabled, EnableEnum.ENABLE_USING.getKey())
            .eq(PayChannel::getEnabled, EnableEnum.ENABLE_USING.getKey())
            .eq(PayChannel::getCodeNum, codeNum);
        if (StrUtil.isNotBlank(appKey)) {
            queryWrapper.eq(PayApp::getAppKey, appKey.trim());
        }
        PayChannel one = getOne(queryWrapper, false);
        if (ObjectUtil.isEmpty(one)) {
            throw new CustomException(StrUtil.isNotBlank(appKey)
                ? String.format("支付应用[%s]下未找到渠道[%s]", appKey, codeNum)
                : "该支付渠道不存在");
        }
        return one;
    }

    @Override
    @IgnoreTenant
    public void queryEnabledPayChannelList(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String appKey = params.get("appKey").toString();
        String clientType = params.get("clientType").toString();
        PayApp payApp = payAppService.getEnabledPayAppByAppKey(appKey);

        QueryWrapper<PayChannel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(PayChannel::getAppId), payApp.getId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(PayChannel::getEnabled), EnableEnum.ENABLE_USING.getKey());
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(PayChannel::getCodeNum));
        List<PayChannel> channelList = list(queryWrapper);

        List<Map<String, Object>> rows = new ArrayList<>();
        for (PayChannel payChannel : channelList) {
            if (StrUtil.isNotBlank(clientType) && !PayType.supportsClientType(payChannel.getCodeNum(), clientType)) {
                continue;
            }
            Map<String, Object> row = new HashMap<>();
            row.put("id", payChannel.getId());
            row.put("codeNum", payChannel.getCodeNum());
            row.put("feeRate", payChannel.getFeeRate());
            row.put("appId", payChannel.getAppId());
            row.put("appKey", payApp.getAppKey());
            row.put("codeNumName", PayType.getMation(payChannel.getCodeNum()).get("name"));
            row.put("clientTypes", PayType.getMation(payChannel.getCodeNum()).get("clientTypes"));
            row.put("remark", payChannel.getRemark());
            rows.add(row);
        }
        outputObject.setBeans(rows);
        outputObject.settotal(rows.size());
    }
}