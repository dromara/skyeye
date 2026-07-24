/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.forum.dao;

import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.eve.dao.SkyeyeBaseMapper;
import com.skyeye.eve.forum.entity.ForumComment;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ForumCommentDao
 * @Description: 论坛评论管理数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 11:06
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ForumCommentDao extends SkyeyeBaseMapper<ForumComment> {

    /**
     * 分页查询父评论id
     */
    @IgnoreTenant
    List<Map<String, Object>> queryForumCommentList(@Param("forumId") String forumId);

    /**
     * 根据父id查询所有子节点id
     */
    @IgnoreTenant
    List<String> queryAllChildIdsByParentId(@Param("ids") List<String> ids);

    /**
     * 根据id列表查询评论详情
     */
    @IgnoreTenant
    List<Map<String, Object>> queryForumCommentListByIds(@Param("ids") List<String> ids);

}
