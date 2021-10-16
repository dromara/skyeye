/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.youbenzi.md2.markdown;

public abstract class ListBuilderCon{
	public abstract boolean isRightType(String lineStr);
	public abstract Block newBuilder(String str);
	public StringBuilder how2AppendIfBlank(StringBuilder sb){
		return sb;
	}
	
	public StringBuilder how2AppendIfNotBlank(StringBuilder sb, String value){
		sb.append(value+"\n");
		return sb;
	}
}
