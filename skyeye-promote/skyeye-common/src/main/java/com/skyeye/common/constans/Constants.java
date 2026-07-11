/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.common.constans;

/**
 * @ClassName: Constants
 * @Description: 系统公共常量类
 * @author: skyeye云系列--卫志强
 * @date: 2021/6/6 23:22
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class Constants {

    // win系统桌面图片列表的redis的key
    public static final String SYS_WIN_BG_PIC_REDIS_KEY = "sys_win_bg_pic_redis_key";

    // win系统锁屏桌面图片列表的redis的key
    public static final String SYS_WIN_LOCK_BG_PIC_REDIS_KEY = "sys_win_lock_bg_pic_redis_key";

    // win系统主题颜色列表的redis的key
    public static final String SYS_WIN_THEME_COLOR_REDIS_KEY = "sys_win_theme_color_redis_key";

    // 获取群组成员列表
    public static final String SYS_EVE_TALK_GROUP_USER_LIST = "sys_eve_talk_group_user_list_";

    public static String checkSysEveTalkGroupUserListByGroupId(String groupId) {
        return SYS_EVE_TALK_GROUP_USER_LIST + groupId;
    }

    /** 群成员 userId 列表缓存（WebSocket 推送等高频场景使用） */
    public static String checkSysEveTalkGroupUserIdsByGroupId(String groupId) {
        return SYS_EVE_TALK_GROUP_USER_LIST + groupId + "_user_ids";
    }

}
