/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.youbenzi.md2.markdown;

import java.util.LinkedHashMap;
import java.util.Map;

public class MDToken {
	
	public static String QUOTE = ">";
	public static String CODE = "```";
	public static String CODE_BLANK = "    ";
	public static String HEADLINE = "#";
	public static String IMG = "![";
	public static String LINK = "[";
	public static String UNORDERED_LIST1 = "* ";
	public static String UNORDERED_LIST2 = "- ";
	public static String BOLD_WORD = "**";
	public static String ITALIC_WORD = "_";
	public static String ITALIC_WORD_2 = "*";
	public static String STRIKE_WORD = "~~";
	public static String CODE_WORD = "`";
	
	public static Map<String, String> PLACEHOLDER_MAP = new LinkedHashMap<String, String>() {	//需要显示的特殊符号的占位符
		private static final long serialVersionUID = 5649442662460683378L;
		{
			put("\\\\", "$BACKSLASH");
			put("\\" + IMG, "$IMG");
			put("\\" + LINK, "$LINK");
//			put("\\" + BOLD_WORD, "$BOLDWORD");	//因为有 ITALIC_WORD_2 的存在，所以不需要这个
			put("\\" + ITALIC_WORD, "$ITALICWORD");
			put("\\" + ITALIC_WORD_2, "$2ITALICWORD");
			put("\\" + STRIKE_WORD, "$STRIKEWORD");
			put("\\" + CODE_WORD, "$CODEWORD");
			put("\\", "");
		}
	};
	
	public static BlockType convert(String mdToken){
		if(mdToken.equals(QUOTE)){
			return BlockType.QUOTE;
		}else if(mdToken.equals(CODE)){
			return BlockType.CODE;
		}else if(mdToken.equals(HEADLINE)){
			return BlockType.HEADLINE;
		}else if(mdToken.equals(IMG)){
			return BlockType.IMG;
		}else if(mdToken.equals(BOLD_WORD)){
			return BlockType.BOLD_WORD;
		}else if(mdToken.equals(ITALIC_WORD) || mdToken.equals(ITALIC_WORD_2)){
			return BlockType.ITALIC_WORD;
		}else if(mdToken.equals(STRIKE_WORD)){
			return BlockType.STRIKE_WORD;
		}else if(mdToken.equals(CODE_WORD)){
			return BlockType.CODE_WORD;
		}else if(mdToken.equals(LINK)){
			return BlockType.LINK;
		}else {
			return BlockType.NONE;
		}
	}
}
