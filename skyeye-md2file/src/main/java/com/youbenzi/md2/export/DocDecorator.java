/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.youbenzi.md2.export;

import java.util.List;

import com.youbenzi.md2.markdown.Block;

public class DocDecorator implements Decorator{
	
	
	public void beginWork(String outputFilePath, String sysWaterMark) {
	}
	
	public void decorate(List<Block> block, String webRootNdc) {
		throw new RuntimeException("暂未支持word doc文档的导出");
	}

	public void afterWork(String outputFilePath) {
		// TODO Auto-generated method stub
		
	}

}
