/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.common.constans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: NoteConstants
 * @Description: 笔记系统相关的常量类
 * @author: skyeye云系列--卫志强
 * @date: 2021/6/14 11:55
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class NoteConstants {

    // 笔记管理---目录logo图片
    public static final String SYS_FILE_CONSOLE_IS_FOLDER_LOGO_PATH = "../../assets/images/folder-show.png";

    // 笔记文件夹目录集合
    public static final String SYS_FILE_MYNOTE_LIST_MATION = "sys_file_mynote_list_mation_";

    public static String getSysFileMyNoteListMation(String folderId, String userId) {
        return SYS_FILE_MYNOTE_LIST_MATION + folderId + "_" + userId;
    }

    /**
     * 获取我的笔记默认的文件夹
     *
     * @return
     */
    public static final List<Map<String, Object>> getFileMyNoteDefaultFolder() {
        List<Map<String, Object>> beans = new ArrayList<>();
        Map<String, Object> newnotes = new HashMap<>();
        newnotes.put("id", "1");
        newnotes.put("name", "最新笔记");
        newnotes.put("pId", "0");
        newnotes.put("isParent", 0);// 是否是文件夹 0否1是
        newnotes.put("icon", "../../assets/images/note-folder.png");// 图标
        beans.add(newnotes);
        Map<String, Object> myfiles = new HashMap<>();
        myfiles.put("id", "2");
        myfiles.put("name", "我的文件夹");
        myfiles.put("pId", "0");
        myfiles.put("isParent", 1);// 是否是文件夹 0否1是
        myfiles.put("icon", "../../assets/images/my-folder-icon.png");// 图标
        beans.add(myfiles);
        return beans;
    }

}
