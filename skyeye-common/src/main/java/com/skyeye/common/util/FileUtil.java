/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * @ClassName: FileUtil
 * @Description: 文件操作类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/7 20:43
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class FileUtil {

    private static Logger logger = LoggerFactory.getLogger(FileUtil.class);

    /**
     * 删除单个文件
     *
     * @param fileAddress 要删除文件的地址
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String fileAddress) {
        File file = new File(fileAddress);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                logger.info("delete file success: {}", fileAddress);
                return true;
            } else {
                logger.warn("delete file fail: {}", fileAddress);
                return false;
            }
        } else {
            logger.warn("delete file fail, because this file is not exit: {}", fileAddress);
            return false;
        }
    }

    /**
     * 获取文件内容
     *
     * @param fileAddress 文件地址
     * @return 文件内容
     */
    public static String readFileContent(String fileAddress) {
        File file = new File(fileAddress);
        BufferedReader reader = null;
        StringBuffer sbf = new StringBuffer();
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempStr;
            while ((tempStr = reader.readLine()) != null) {
                sbf.append(tempStr);
            }
            reader.close();
            return sbf.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return sbf.toString();
    }

}
