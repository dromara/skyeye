/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.common.constans;

/**
 * @ClassName: ForumConstants
 * @Description: 论坛系统常量类
 * @author: skyeye云系列--卫志强
 * @date: 2021/6/15 21:44
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class ForumConstants {

    // 获取上线的论坛标签
    public static final String FORUM_TAG_UP_STATE_LIST = "forum_tag_up_state_list";

    // 获取论坛敏感词的redis的key
    public static final String FORUM_SENSITIVE_WORDS_ALL = "forum_sensitive_words_all";
    public static String forumSensitiveWordsAll() {
        return FORUM_SENSITIVE_WORDS_ALL;
    }

    // 获取论坛帖子评论量的redis的key(实时的)
    public static final String FORUM_COMMENT_NUMS_BY_ = "forum_comment_nums_by_";
    public static String forumCommentNumsByForumId(String forumId) {
        return FORUM_COMMENT_NUMS_BY_ + forumId;
    }

    // 获取论坛帖子评论量的redis的key(执行上一次定时任务时的)
    public static final String FORUMYESTERDAY_COMMENT_NUMS_BYFORUMID = "forum_yesterday_comment_nums_by_";
    public static String forumYesterdayCommentNumsByForumId(String forumId) {
        return FORUMYESTERDAY_COMMENT_NUMS_BYFORUMID + forumId;
    }

    // 获取论坛帖子浏览量的redis的key(实时的)
    public static final String FORUM_BROWSE_NUMS_BYFORUMID = "forum_browse_nums_by_";
    public static String forumBrowseNumsByForumId(String forumId) {
        return FORUM_BROWSE_NUMS_BYFORUMID + forumId;
    }

    // 获取论坛帖子浏览量的redis的key(执行上一次定时任务时的)
    public static final String FORUMYESTERDAY_BROWSE_NUMS_BYFORUMID = "forum_yesterday_browse_nums_by_";
    public static String forumYesterdayBrowseNumsByForumId(String forumId) {
        return FORUMYESTERDAY_BROWSE_NUMS_BYFORUMID + forumId;
    }

    // 获取用户论坛帖子浏览信息的redis的key
    public static final String FORUM_BROWSE_MATION_BYUSERID = "forum_browse_mation_by_";
    public static String forumBrowseMationByUserid(String userId) {
        return FORUM_BROWSE_MATION_BYUSERID + userId;
    }

    // 获取论坛帖子每天被浏览过帖子的redis的key
    public static final String FORUM_EVERYDAY_BROWSE_IDS_BYTIME = "forum_everyday_browse_ids_by_";
    public static String forumEverydayBrowseIdsByTime(String time) {
        return FORUM_EVERYDAY_BROWSE_IDS_BYTIME + time;
    }

    // 获取论坛每个帖子每天的浏览和评论数的redis的key
    public static final String EVERYFORUM_EVERYDAY_NUMS_BY_ = "everyforum_everyday_nums_by_";
    public static String everyforumEverydayNumsByIdAndTime(String id, String time) {
        return EVERYFORUM_EVERYDAY_NUMS_BY_ + id + "_" + time;
    }

    // 获取solr同步数据的时间的redis的key
    public static final String FORUM_SOLR_SYNCHRONOUSTIME = "forum_solr_synchronoustime";
    public static String forumSolrSynchronoustime() {
        return FORUM_SOLR_SYNCHRONOUSTIME;
    }

}
