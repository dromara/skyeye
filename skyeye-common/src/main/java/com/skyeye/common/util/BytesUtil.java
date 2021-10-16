/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.common.util;

/**
 * byte操作类. 
 * @author Administrator
 *
 */
public class BytesUtil {
	
	/**
	 * 由于String.subString对汉字处理存在问题（把一个汉字视为一个字节)，因此在 包含汉字的字符串时存在隐患，现调整如下：
	 * @param src 要截取的字符串
	 * @param start_idx 开始坐标（包括该坐标)
	 * @param end_idx 截止坐标（包括该坐标）
	 * @return
	 */
	public static String substring(String src, int start_idx, int end_idx) {
		byte[] b = src.getBytes();
		String tgt = "";
		for (int i = start_idx; i <= end_idx; i++) {
			tgt += (char) b[i];
		}
		return tgt;
	}

	/**
	 * bytes转M或者KB
	 *
	 * @param size 大小
	 * @return
	 */
	public static String sizeFormatNum2String(long size) {
		if (size > 1024 * 1024){
			return String.format("%.2f", (double) size / 1048576) + "M";
		} else{
			return String.format("%.2f", (double) size / (1024)) + "KB";
		}
	}

}
