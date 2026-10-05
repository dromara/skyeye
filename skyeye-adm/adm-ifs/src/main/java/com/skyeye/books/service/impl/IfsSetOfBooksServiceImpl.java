/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.books.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.books.dao.IfsSetOfBooksDao;
import com.skyeye.books.entity.SetOfBooks;
import com.skyeye.books.service.IfsSetOfBooksService;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: IfsSetOfBooksServiceImpl
 * @Description: 账套管理服务类
 * @author: skyeye云系列
 * @date: 2021/11/21 14:15
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "账套管理", groupName = "账套管理")
public class IfsSetOfBooksServiceImpl extends SkyeyeBusinessServiceImpl<IfsSetOfBooksDao, SetOfBooks> implements IfsSetOfBooksService {

    @Override
    public void validatorEntity(SetOfBooks entity) {
        super.validatorEntity(entity);
        if (DateUtil.compare(entity.getEndTime(), entity.getStartTime())) {
            // 结束时间早于开始时间
            throw new CustomException("结束时间不能早于开始时间");
        }
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        for (Map<String, Object> bean : beans) {
            String startTime = bean.get("startTime").toString();
            String endTime = bean.get("endTime").toString();
            String currentTime = DateUtil.getTimeAndToString();
            if (DateUtil.getDistanceDay(startTime, currentTime) >= 0 && DateUtil.getDistanceDay(currentTime, endTime) >= 0) {
                // startTime <= 当前时间 <= endTime
                bean.put("haveAccess", true);
            } else {
                bean.put("haveAccess", false);
            }
        }
        return beans;
    }

    @Override
    public String resolveSetOfBooksId(String setOfBooksId, String voucherDate) {
        if (StrUtil.isNotBlank(setOfBooksId)) {
            return setOfBooksId;
        }
        QueryWrapper<SetOfBooks> qw = new QueryWrapper<>();
        qw.eq(MybatisPlusUtil.toColumns(SetOfBooks::getEnabled), EnableEnum.ENABLE_USING.getKey());
        List<SetOfBooks> books = list(qw);
        if (CollectionUtil.isEmpty(books)) {
            throw new CustomException("未配置启用账套，通用凭证模板无法生成凭证");
        }
        if (books.size() == 1) {
            return books.get(0).getId();
        }
        String date = StrUtil.blankToDefault(voucherDate, DateUtil.getYmdTimeAndToString());
        List<SetOfBooks> inRange = books.stream()
            .filter(item -> inBooksDateRange(item, date))
            .collect(Collectors.toList());
        if (inRange.size() == 1) {
            return inRange.get(0).getId();
        }
        throw new CustomException("存在多个启用账套，请在业务单据或凭证模板中指定账套");
    }

    private boolean inBooksDateRange(SetOfBooks books, String date) {
        if (books == null || StrUtil.hasBlank(books.getStartTime(), books.getEndTime(), date)) {
            return false;
        }
        return DateUtil.getDistanceDay(books.getStartTime(), date) >= 0
            && DateUtil.getDistanceDay(date, books.getEndTime()) >= 0;
    }

}

