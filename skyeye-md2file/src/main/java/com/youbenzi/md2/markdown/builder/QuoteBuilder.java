/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.youbenzi.md2.markdown.builder;

import com.youbenzi.md2.markdown.BlockType;
import com.youbenzi.md2.markdown.MDToken;

public class QuoteBuilder extends ListBuilder{

	public QuoteBuilder(String content){
		super(content, BlockType.QUOTE);
	}

	@Override
	public int computeCharIndex(String str) {
		return str.indexOf(MDToken.QUOTE);
	}
}
