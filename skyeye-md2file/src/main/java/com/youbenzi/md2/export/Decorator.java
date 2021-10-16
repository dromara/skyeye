/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.youbenzi.md2.export;

import java.util.List;

import com.youbenzi.md2.markdown.Block;

public interface Decorator {
	
	public void beginWork(String outputFilePath, String sysWaterMark) throws Exception;
	
	public void decorate(List<Block> list, String webRootNdc) throws Exception;
	
	public void afterWork(String outputFilePath);
	
}
