/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.youbenzi.md2.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import com.youbenzi.md2.export.HTMLDecorator;
import com.youbenzi.md2.markdown.Block;
import com.youbenzi.md2.markdown.MDAnalyzer;

public class MDUtil {

	public static String markdown2Html(File file, String webRootNdc) {
		BufferedReader reader = null;
		String lineStr = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			StringBuffer sb = new StringBuffer();
			while ((lineStr = reader.readLine()) != null) {
				sb.append(lineStr).append("\n");
			}
			return markdown2Html(sb.toString(), webRootNdc);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public static String markdown2Html(String mdStr, String webRootNdc) {
		if (mdStr == null) {
			return null;
		}
		BufferedReader reader = new BufferedReader(new StringReader(mdStr));
		List<Block> list = MDAnalyzer.analyze(reader);

		HTMLDecorator decorator = new HTMLDecorator();

		decorator.decorate(list, webRootNdc);

		return decorator.outputHtml();
	}

	public static void main(String[] args) {
		// System.out.println(MDUtil.markdown2Html(new
		// File("test_file/md_for_test.md")));
		System.out.println(MDUtil.markdown2Html(new File("test_file/test_file2.md"), ""));
		// System.out.println(MDUtil.markdown2Html(" [Nodejs
		// 文档](https://nodejs.org/documentation/)"));
	}
}
