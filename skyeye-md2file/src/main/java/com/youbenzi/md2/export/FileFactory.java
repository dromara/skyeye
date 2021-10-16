/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.youbenzi.md2.export;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.List;

import com.youbenzi.md2.markdown.Block;
import com.youbenzi.md2.markdown.MDAnalyzer;

/**
 * 文档生成工厂
 *
 * @author yangyingqiang
 * 2015年5月1日 下午2:35:08
 */
public class FileFactory {

    public static String Encoding = "UTF-8";

    public static void produce(File file, String outputFilePath, String webRootNdc, String sysWaterMark) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), Encoding));
        produce(reader, outputFilePath, webRootNdc, sysWaterMark);
    }

    public static void produce(InputStream is, String outputFilePath, String webRootNdc, String sysWaterMark) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName(Encoding)));
        produce(reader, outputFilePath, webRootNdc, sysWaterMark);
    }

    public static void produce(String mdText, String outputFilePath, String webRootNdc, String sysWaterMark) throws Exception {
        BufferedReader reader = new BufferedReader(new StringReader(mdText));
        produce(reader, outputFilePath, webRootNdc, sysWaterMark);
    }

    public static void produce(BufferedReader reader, String outputFilePath, String webRootNdc, String sysWaterMark) throws Exception {
        List<Block> list = MDAnalyzer.analyze(reader);
        produce(list, outputFilePath, webRootNdc, sysWaterMark);
    }

    public static void produce(List<Block> list, String outputFilePath, String webRootNdc, String sysWaterMark) throws Exception {
        String ext = getExtOfFile(outputFilePath);
        Decorator decorator = BuilderFactory.build(ext);

        decorator.beginWork(outputFilePath, sysWaterMark);
        decorator.decorate(list, webRootNdc);
        decorator.afterWork(outputFilePath);
    }

    private static String getExtOfFile(String outputFilePath) {
        if (outputFilePath == null) {
            return "";
        }
        int i = outputFilePath.lastIndexOf(".");
        if (i < 0) {
            return "";
        }
        return outputFilePath.substring(i + 1);
    }

}
