/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.forum.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.forum.dao.ForumHotDao;
import com.skyeye.eve.forum.entity.ForumContent;
import com.skyeye.eve.forum.entity.ForumHot;
import com.skyeye.eve.forum.service.ForumContentService;
import com.skyeye.eve.forum.service.ForumHotService;
import com.skyeye.eve.forum.service.ForumTagService;
import com.skyeye.eve.service.IAuthUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: ForumHotServiceImpl
 * @Description: 论坛热门帖子管理服务层--强隔离
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:52
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "论坛热门帖子管理", groupName = "论坛热门帖子管理",allowDynamicAttrKey = false)
public class ForumHotServiceImpl extends SkyeyeBusinessServiceImpl<ForumHotDao, ForumHot> implements ForumHotService {

    @Autowired
    private ForumContentService forumContentService;

    @Autowired
    private ForumTagService forumTagService;

    @Autowired
    private IAuthUserService iAuthUserService;

    @Override
    public void queryHotForumList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        String today = DateUtil.getPointTime(DateUtil.YYYY_MM_DD);
        List<ForumContent> beans = forumContentService.getTodayHotForumList(today);
        forumContentService.setAnonymous(beans);
        forumTagService.setTagMationForContentList(beans);
        iAuthUserService.setDataMation(beans, ForumContent::getCreateId);
        outputObject.setBeans(beans);
        outputObject.settotal(page.getTotal());
    }

    @Override
    public void editHotForumMation() {
        // 取出前三十天的帖子
        String beforeDay = getBeforeOrFutureDay(-29);
        String today = DateUtil.getTimeAndToString();
        QueryWrapper<ForumContent> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ForumContent::getState), CommonNumConstants.NUM_ONE)
            .gt(MybatisPlusUtil.toColumns(ForumContent::getBrowseNum), CommonNumConstants.NUM_ZERO)
            .between(MybatisPlusUtil.toColumns(ForumContent::getCreateTime), beforeDay, today)
            .orderByDesc(MybatisPlusUtil.toColumns(ForumContent::getCreateTime));
        List<ForumContent> forumContents = forumContentService.list(queryWrapper);

        // 取出 浏览量、评论量
        Map<String, List<Integer>> listMap = new HashMap<>();
        for (ForumContent forumContent : forumContents) {
            List<Integer> nums = new ArrayList<>();
            nums.add(Integer.valueOf(forumContent.getBrowseNum()));
            nums.add(Integer.valueOf(forumContent.getCommentNum()));
            listMap.put(forumContent.getId(), nums);
        }
        // 计算权重
        Map<String, Float> weightMap = new HashMap<>();
        listMap.forEach((k, v) -> {
            weightMap.put(k, (float) (v.get(CommonNumConstants.NUM_ZERO) * 0.7 + v.get(CommonNumConstants.NUM_ONE) * 0.3));
        });
        // 排序权重
        Map<String, Float> ids = new HashMap<>();
        Set<Map.Entry<String, Float>> entries = weightMap.entrySet();
        CollectionUtil.sort(entries, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        for (Map.Entry<String, Float> entry : entries) {
            ids.put(entry.getKey(), entry.getValue());
        }
        // 取出前10 的帖子
        if (ids.size() > CommonNumConstants.NUM_TEN) {
            ids = ids.entrySet()
                .stream().limit(CommonNumConstants.NUM_TEN)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }
        List<ForumHot> beans = new ArrayList<>();
        for (Map.Entry<String, Float> entry : ids.entrySet()) {
            ForumHot forumHot = new ForumHot();
            forumHot.setForumId(entry.getKey());
            forumHot.setTagId(StrUtil.EMPTY);
            forumHot.setOrderBy(entry.getValue());
            forumHot.setUpdateTime(today);
            beans.add(forumHot);
        }
        createEntity(beans, null);
    }

    @Override
    public void queryHotTagList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        // 获取前一个月的日期范围yyyy-MM-dd
        LocalDate today = LocalDate.now();
        LocalDate beforeMonth = today.minusMonths(1);
        String start = beforeMonth.format(DateTimeFormatter.ISO_DATE);
        String end = today.format(DateTimeFormatter.ISO_DATE);
        QueryWrapper<ForumHot> queryWrapper = new QueryWrapper<>();
        queryWrapper.between(MybatisPlusUtil.toColumns(ForumHot::getUpdateTime), start, end)
            .eq(MybatisPlusUtil.toColumns(ForumHot::getForumId), StrUtil.EMPTY)
            .orderByDesc(MybatisPlusUtil.toColumns(ForumHot::getOrderBy));
        List<ForumHot> forumHots = list(queryWrapper);
        // 设置标签信息
        for (ForumHot forumHot : forumHots) {
            forumHot.setForumTag(forumTagService.selectById(forumHot.getTagId()));
        }
        outputObject.setBeans(forumHots);
        outputObject.settotal(page.getTotal());
    }

    /**
     * 获取热门标签
     */
    @Override
    public void queryHotForumTagList() {
        String beforeDay = getBeforeOrFutureDay(-29);
        String today = DateUtil.getTimeAndToString();
        QueryWrapper<ForumContent> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ForumContent::getState), CommonNumConstants.NUM_ONE)
            .between(MybatisPlusUtil.toColumns(ForumContent::getCreateTime), beforeDay, today)
            .select(MybatisPlusUtil.toColumns(ForumContent::getTagId))
            .orderByDesc(MybatisPlusUtil.toColumns(ForumContent::getCreateTime));
        List<ForumContent> forumContents = forumContentService.list(queryWrapper);

        List<String> tagIds = new ArrayList<>();
        for (ForumContent content : forumContents) {
            String[] tagId = content.getTagId().split(",");
            for (int i = 0; i < tagId.length; i++) {
                if (StrUtil.isNotEmpty(tagId[i])) {
                    tagIds.add(tagId[i]);
                }
            }
        }
        // 分组统计
        Map<String, Long> collect = tagIds.stream().collect(
            Collectors.groupingBy(e -> e, Collectors.counting())
        );
        // 排序
        List<Map.Entry<String, Long>> collectSort = collect.entrySet()
            .stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toList());
        Map<String, Float> tagHot = new HashMap<>();
        for (Map.Entry<String, Long> entry : collectSort) {
            tagHot.put(entry.getKey(), (float) entry.getValue());
        }
        // 取前10
        if (tagHot.size() > CommonNumConstants.NUM_TEN) {
            tagHot = tagHot.entrySet()
                .stream().limit(CommonNumConstants.NUM_TEN)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }
        // 保存到ForumHot
        List<ForumHot> forumHots = new ArrayList<>();
        for (Map.Entry<String, Float> entry : tagHot.entrySet()) {
            ForumHot forumHot = new ForumHot();
            forumHot.setTagId(entry.getKey());
            forumHot.setForumId(StrUtil.EMPTY);
            forumHot.setOrderBy(entry.getValue());
            forumHot.setUpdateTime(today);
            forumHots.add(forumHot);
        }
        createEntity(forumHots, null);
    }

    /**
     * 根据帖子id删除热门记录
     */
    @Override
    public void deleteByForumId(String id) {
        QueryWrapper<ForumHot> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ForumHot::getForumId), id);
        remove(queryWrapper);
    }

    public String getBeforeOrFutureDay(int num) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, num);
        Date m = c.getTime();
        return format.format(m);
    }
}
