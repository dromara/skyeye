/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.forum.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.forum.dao.ForumCommentDao;
import com.skyeye.eve.forum.entity.ForumComment;
import com.skyeye.eve.forum.entity.ForumContent;
import com.skyeye.eve.forum.service.ForumCommentService;
import com.skyeye.eve.forum.service.ForumContentService;
import com.skyeye.eve.service.IAuthUserService;
import com.skyeye.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: ForumCommentServiceImpl
 * @Description: 论坛评论管理
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 11:09
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "论坛评论管理", groupName = "论坛评论管理")
public class ForumCommentServiceImpl extends SkyeyeBusinessServiceImpl<ForumCommentDao, ForumComment> implements ForumCommentService {

    @Autowired
    private ForumContentService forumContentService;

    @Autowired
    private IAuthUserService iAuthUserService;


    @Override
    public void createPrepose(ForumComment forumComment) {
        String currentUserId = InputObject.getLogParamsStatic().get("id").toString();
        forumComment.setCommentId(currentUserId);
        forumComment.setCommentTime(DateUtil.getTimeAndToString());
    }

    @Override
    public void createPostpose(ForumComment forumComment, String userId) {
        ForumContent forumContent = forumContentService.selectById(forumComment.getForumId());
        if (forumContent.getCommentNum() != null) {
            forumContentService.updateCommentCount(forumContent.getId(), CalculationUtil.add(CommonNumConstants.NUM_ZERO,
                    forumContent.getCommentNum(), String.valueOf(CommonNumConstants.NUM_ONE)));
        }
    }

    @Override
    public void deletePostpose(ForumComment forumComment) {
        String id = forumComment.getId();
        QueryWrapper<ForumComment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ForumComment::getBelongCommentId), id);
        List<ForumComment> bean = list(queryWrapper);
        if(CollectionUtil.isNotEmpty(bean)){
            remove(queryWrapper);
        }
        // 查询所有评论条数，更新帖子评论总数
        QueryWrapper<ForumComment> countQueryWrapper = new QueryWrapper<>();
        countQueryWrapper.eq(MybatisPlusUtil.toColumns(ForumComment::getForumId), forumComment.getForumId());
        long count = count(countQueryWrapper);
        forumContentService.updateCommentCount(forumComment.getForumId(), String.valueOf(count));
    }

    @Override
    protected void deletePreExecution(ForumComment forumComment) {
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        if (!userId.equals(forumComment.getCreateId())) {
            throw new CustomException("无权限");
        }
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        String forumId = inputObject.getParams().get("id").toString();
        String tenantId = null;
        if (tenantEnable) {
            tenantId = TenantContext.getTenantId();
        }
        // 按父评论分页
        List<Map<String, Object>> beans = skyeyeBaseMapper.queryForumCommentList(forumId, tenantId);
        List<String> ids = beans.stream().map(bean -> bean.get("id").toString()).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(ids)) {
            return new ArrayList<>();
        }
        // 查询子节点信息
        List<String> childIds = skyeyeBaseMapper.queryAllChildIdsByParentId(ids, tenantId);
        beans = skyeyeBaseMapper.queryForumCommentListByIds(childIds, tenantId);
        // 设置评论人信息和回复人信息
        iAuthUserService.setMationForMap(beans, "commentId", "commentMation");
        iAuthUserService.setMationForMap(beans, "replyId", "replyMation");
        iAuthUserService.setMationForMap(beans, "createId", "createMation");
        iAuthUserService.setMationForMap(beans, "lastUpdateId", "lastUpdateMation");
        return beans;
    }

    @Override
    public Boolean builderTree(List<Map<String, Object>> beans, OutputObject outputObject) {
        List<Tree<String>> treeNodes = TreeUtil.build(beans, String.valueOf(CommonNumConstants.NUM_ZERO), new TreeNodeConfig(),
            (treeNode, tree) -> {
                tree.setId(treeNode.get("id").toString());
                tree.setParentId(treeNode.get("belongCommentId").toString());
                tree.setName(treeNode.get("content").toString());
                tree.putExtra("createName", treeNode.get("createName"));
                tree.putExtra("createTime", treeNode.get("createTime"));
                tree.putExtra("createMation", treeNode.get("createMation"));
            });
        beans.clear();
        beans.addAll(JSONUtil.toList(JSONUtil.toJsonStr(treeNodes), null));
        return false;
    }

    @Override
    public Integer countNumByForumId(String forumId) {
        QueryWrapper<ForumComment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ForumComment::getForumId), forumId);
        long count = count(queryWrapper);
        return (int) count;
    }

    @Override
    public List<String> queryListByForumIds(List<String> idList) {
        List<String> result = new ArrayList<>();
        QueryWrapper<ForumComment> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(ForumComment::getForumId), idList)
                .orderByDesc(MybatisPlusUtil.toColumns(ForumComment::getCreateTime));
        List<ForumComment> commentList = list(queryWrapper);
        if (CollectionUtil.isEmpty(commentList)) {
            return result;
        }
        for (ForumComment forumComment : commentList) {
            if (!result.contains(forumComment.getForumId())) {
                result.add(forumComment.getForumId());
                if (result.size() == 15) {
                    break;
                }
            }
        }
        return result;
    }

    @Override
    public void queryMyForumCommentList(InputObject inputObject, OutputObject outputObject) {
        String userId = inputObject.getLogParams().get("id").toString();
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<ForumComment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ForumComment::getCreateId), userId)
                .orderByDesc(MybatisPlusUtil.toColumns(ForumComment::getCreateTime));
        List<ForumComment> beans = list(queryWrapper);
        iAuthUserService.setName(beans, "createId", "createName");
        iAuthUserService.setName(beans, "lastUpdateId", "lastUpdateName");
        iAuthUserService.setDataMation(beans, ForumComment::getReplyId);
        forumContentService.setDataMation(beans, ForumComment::getForumId);
        outputObject.setBeans(beans);
        outputObject.settotal(page.getTotal());
    }

    /**
     * 根据帖子id删除评论
     * */
    @Override
    public void deleteByForumId(String id) {
        QueryWrapper<ForumComment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ForumComment::getForumId), id);
        remove(queryWrapper);
    }
}
