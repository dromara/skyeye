/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.common.constans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: RmProGramConstants
 * @Description: 小程序设计系统常量类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/4 22:08
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class RmProGramConstants {

    // 页面路径的序列号key
    public static final String REDIS_PROJECT_PAGE_FILE_PATH = "projectpagefilepath";

    // 页面名称的序列号key
    public static final String REDIS_PROJECT_PAGE_FILE_NAME = "projectpagefilename";

    // 页面路径的序列号默认值
    public static final String REDIS_PROJECT_PAGE_FILE_PATH_NUM = "1000";

    // 页面名称的序列号默认值
    public static final String REDIS_PROJECT_PAGE_FILE_NAME_NUM = "1000";

    public static List<Map<String, Object>> transform() {
        return Entity.getList();
    }

    public static enum Entity {
        TEMPLATE("1", "template", "folder", "0", ""),
        HTML("2", "html", "folder", "1", ""),
        JS("3", "js", "folder", "1", ""),

        ASSETS("11", "assets", "folder", "1", ""),
        WINUI("12", "winui", "folder", "11", ""),

        //资源文件
        JQUERY("13", "jquery-min.js", "file", "12", "/images/smpromodel/winui/jquery-min.js"),
        JWEIXIN("14", "jweixin-1.0.0.js", "file", "12", "/images/smpromodel/winui/jweixin-1.0.0.js"),
        WEUICSS("15", "weui.css", "file", "12", "/images/smpromodel/winui/weui.css"),
        WEUIJS("16", "weui.min.js", "file", "12", "/images/smpromodel/winui/weui.min.js");

        private String id;
        private String fileName;
        private String fileType;
        private String parentId;
        private String filePath;

        /**
         *
         * @param id
         * @param fileName 文件名称
         * @param fileType 文件类型
         * @param parentId 文件所属父id
         * @param filePath 文件路径
         */
        Entity(String id, String fileName, String fileType, String parentId, String filePath){
            this.id = id;
            this.fileName = fileName;
            this.fileType = fileType;
            this.parentId = parentId;
            this.filePath = filePath;
        }

        public static List<Map<String, Object>> getList(){
            List<Map<String, Object>> beans = new ArrayList<>();
            Map<String, Object> bean;
            for (Entity q : Entity.values()){
                bean = new HashMap<>();
                bean.put("id", q.getId());
                bean.put("fileName", q.getFileName());
                bean.put("fileType", q.getFileType());
                bean.put("parentId", q.getParentId());
                bean.put("filePath", q.getFilePath());
                beans.add(bean);
            }
            return beans;
        }

        public String getId() {
            return id;
        }

        public String getFileName() {
            return fileName;
        }

        public String getFileType() {
            return fileType;
        }

        public String getParentId() {
            return parentId;
        }

        public String getFilePath() {
            return filePath;
        }

    }

}
