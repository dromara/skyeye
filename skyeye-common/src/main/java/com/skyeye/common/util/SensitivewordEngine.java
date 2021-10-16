/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.common.util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 敏感词过滤工具类
 * 
 * @author 卫志强
 *
 */
public class SensitivewordEngine {
	/**
	 * 敏感词库
	 */
	@SuppressWarnings("rawtypes")
	public static Map sensitiveWordMap = null;

	/**
	 * 只过滤最小敏感词
	 */
	public static int minMatchTYpe = 1;

	/**
	 * 过滤所有敏感词
	 */
	public static int maxMatchType = 2;

	/**
	 * 敏感词库敏感词数量
	 * 
	 * @return
	 */
	public static int getWordSize() {
		if (SensitivewordEngine.sensitiveWordMap == null) {
			return 0;
		}
		return SensitivewordEngine.sensitiveWordMap.size();
	}

	/**
	 * 是否包含敏感词
	 * 
	 * @param txt
	 * @param matchType
	 * @return
	 */
	public static boolean isContaintSensitiveWord(String txt, int matchType) {
		boolean flag = false;
		for (int i = 0; i < txt.length(); i++) {
			int matchFlag = checkSensitiveWord(txt, i, matchType);
			if (matchFlag > 0) {
				flag = true;
			}
		}
		return flag;
	}

	/**
	 * 获取敏感词内容
	 * 
	 * @param txt
	 * @param matchType
	 * @return 敏感词内容
	 */
	public static Set<String> getSensitiveWord(String txt, int matchType) {
		Set<String> sensitiveWordList = new HashSet<String>();
		for (int i = 0; i < txt.length(); i++) {
			int length = checkSensitiveWord(txt, i, matchType);
			if (length > 0) {
				// 将检测出的敏感词保存到集合中
				sensitiveWordList.add(txt.substring(i, i + length));
				i = i + length - 1;
			}
		}
		return sensitiveWordList;
	}

	/**
	 * 替换敏感词
	 * 
	 * @param txt
	 * @param matchType
	 * @param replaceChar
	 * @return
	 */
	public static String replaceSensitiveWord(String txt, int matchType,
			String replaceChar) {
		String resultTxt = txt;
		Set<String> set = getSensitiveWord(txt, matchType);
		Iterator<String> iterator = set.iterator();
		String word = null;
		String replaceString = null;
		while (iterator.hasNext()) {
			word = iterator.next();
			replaceString = getReplaceChars(replaceChar, word.length());
			resultTxt = resultTxt.replaceAll(word, replaceString);
		}
		return resultTxt;
	}

	/**
	 * 替换敏感词内容
	 * 
	 * @param replaceChar
	 * @param length
	 * @return
	 */
	private static String getReplaceChars(String replaceChar, int length) {
		String resultReplace = replaceChar;
		for (int i = 1; i < length; i++) {
			resultReplace += replaceChar;
		}
		return resultReplace;
	}

	/**
	 * 检查敏感词数量
	 * 
	 * @param txt
	 * @param beginIndex
	 * @param matchType
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static int checkSensitiveWord(String txt, int beginIndex,
			int matchType) {
		boolean flag = false;
		// 记录敏感词数量
		int matchFlag = 0;
		char word = 0;
		Map nowMap = SensitivewordEngine.sensitiveWordMap;
		for (int i = beginIndex; i < txt.length(); i++) {
			word = txt.charAt(i);
			// 判断该字是否存在于敏感词库中
			nowMap = (Map) nowMap.get(word);
			if (nowMap != null) {
				matchFlag++;
				// 判断是否是敏感词的结尾字，如果是结尾字则判断是否继续检测
				if ("1".equals(nowMap.get("isEnd"))) {
					flag = true;
					// 判断过滤类型，如果是小过滤则跳出循环，否则继续循环
					if (SensitivewordEngine.minMatchTYpe == matchType) {
						break;
					}
				}
			} else {
				break;
			}
		}
		if (!flag) {
			matchFlag = 0;
		}
		return matchFlag;
	}

}
