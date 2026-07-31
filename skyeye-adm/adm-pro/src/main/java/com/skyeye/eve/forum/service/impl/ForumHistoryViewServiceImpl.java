/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.forum.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.forum.dao.ForumHistoryViewDao;
import com.skyeye.eve.forum.entity.ForumContent;
import com.skyeye.eve.forum.entity.ForumHistoryView;
import com.skyeye.eve.forum.service.ForumContentService;
import com.skyeye.eve.forum.service.ForumHistoryViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName: ForumHistoryViewServiceImpl
 * @Description: 历史帖子信息管理
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/9 14:31
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "历史帖子管理", groupName = "历史帖子管理")
public class ForumHistoryViewServiceImpl extends SkyeyeBusinessServiceImpl<ForumHistoryViewDao, ForumHistoryView> implements ForumHistoryViewService {

    @Autowired
    private ForumContentService forumContentService;

    @Override
    public String createEntity(ForumHistoryView entity, String userId) {
        QueryWrapper<ForumHistoryView> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ForumHistoryView::getForumId), entity.getForumId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(ForumHistoryView::getCreateId), userId);
        ForumHistoryView historyView = getOne(queryWrapper, false);
        if (ObjectUtil.isNotEmpty(historyView) && StrUtil.isNotEmpty(historyView.getId())) {
            UpdateWrapper<ForumHistoryView> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, historyView.getId());
            updateWrapper.set(MybatisPlusUtil.toColumns(ForumHistoryView::getCreateTime), DateUtil.getTimeAndToString());
            update(updateWrapper);
            return StrUtil.EMPTY;
        }
        return super.createEntity(entity, userId);
    }

    @Override
    public void createPrepose(ForumHistoryView forumHistoryView) {
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        forumHistoryView.setCreateId(userId);
        forumHistoryView.setCreateTime(DateUtil.getTimeAndToString());
    }

    @Override
    public void createPostpose(ForumHistoryView forumHistoryView, String userId) {
        ForumContent forumContent = forumContentService.selectById(forumHistoryView.getForumId());
        if (StrUtil.isNotEmpty(forumContent.getBrowseNum())) {
            Integer flag = Integer.parseInt(forumContent.getBrowseNum()) + CommonNumConstants.NUM_ONE;
            forumContentService.updateViewCount(forumHistoryView.getForumId(), String.valueOf(flag));
        } else {
            forumContentService.updateViewCount(forumHistoryView.getForumId(), String.valueOf(CommonNumConstants.NUM_ZERO));
        }
    }

    @Override
    public void deleteByForumId(String id) {
        QueryWrapper<ForumHistoryView> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ForumHistoryView::getForumId), id);
        remove(queryWrapper);
    }

    @Override
    public List<ForumHistoryView> queryMyHistory(String currentUserId) {
        QueryWrapper<ForumHistoryView> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ForumHistoryView::getCreateId), currentUserId);
        // 使用 IN 子查询优化性能：先找出每个 forumId 的最大 createTime 和对应的 id，然后使用 IN 查询
        // 这种方式只执行一次子查询，而不是对每条记录都执行，性能更好
        String forumIdColumn = MybatisPlusUtil.toColumns(ForumHistoryView::getForumId);
        String createTimeColumn = MybatisPlusUtil.toColumns(ForumHistoryView::getCreateTime);
        String createIdColumn = MybatisPlusUtil.toColumns(ForumHistoryView::getCreateId);
        String idColumn = CommonConstants.ID;
        String tableName = "forum_history_view";
        // 使用 IN 子查询：先找出每个 forumId 下 createTime 最大（如果相同则 id 最大）的记录的 id
        // 使用 GROUP BY 配合子查询，性能比相关子查询好，且兼容性更好
        queryWrapper.apply(idColumn + " IN (" +
            "SELECT t1." + idColumn + " " +
            "FROM " + tableName + " t1 " +
            "INNER JOIN (" +
            "  SELECT " + forumIdColumn + ", MAX(" + createTimeColumn + ") as max_time " +
            "  FROM " + tableName + " " +
            "  WHERE " + createIdColumn + " = {0} " +
            "  GROUP BY " + forumIdColumn +
            ") t2 ON t1." + forumIdColumn + " = t2." + forumIdColumn + " " +
            "AND t1." + createTimeColumn + " = t2.max_time " +
            "AND t1." + createIdColumn + " = {0} " +
            "INNER JOIN (" +
            "  SELECT " + forumIdColumn + ", " + createTimeColumn + ", MAX(" + idColumn + ") as max_id " +
            "  FROM " + tableName + " " +
            "  WHERE " + createIdColumn + " = {0} " +
            "  GROUP BY " + forumIdColumn + ", " + createTimeColumn +
            ") t3 ON t1." + forumIdColumn + " = t3." + forumIdColumn + " " +
            "AND t1." + createTimeColumn + " = t3." + createTimeColumn + " " +
            "AND t1." + idColumn + " = t3.max_id" +
            ")", currentUserId);
        queryWrapper.orderByDesc(createTimeColumn);
        return list(queryWrapper);
    }
}