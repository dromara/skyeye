/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.youbenzi.md2.markdown.builder;

import com.youbenzi.md2.markdown.BlockType;

public class OrderedListBuilder extends ListBuilder{

	public OrderedListBuilder(String content){
		super(content, BlockType.ORDERED_LIST);
	}
	
	@Override
	public int computeCharIndex(String str) {
		return str.indexOf(" ");
	}

}
