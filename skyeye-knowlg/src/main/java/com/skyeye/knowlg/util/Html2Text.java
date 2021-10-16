/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.knowlg.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;

public class Html2Text extends HTMLEditorKit.ParserCallback {
	private static Html2Text html2Text = new Html2Text();

	StringBuffer s;

	public Html2Text() {
	}

	public void parse(String str) throws IOException {
		InputStream iin = new ByteArrayInputStream(str.getBytes());
		Reader in = new InputStreamReader(iin);
		s = new StringBuffer();
		ParserDelegator delegator = new ParserDelegator();
		delegator.parse(in, this, Boolean.TRUE);
		iin.close();
		in.close();
	}

	public void handleText(char[] text, int pos) {
		s.append(text);
	}

	public String getText() {
		return s.toString();
	}

	public static String getContent(String str) {
		try {
			html2Text.parse(str);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return html2Text.getText();
	}
}
