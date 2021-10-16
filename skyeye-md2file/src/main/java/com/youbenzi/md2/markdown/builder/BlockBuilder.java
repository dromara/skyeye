/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.youbenzi.md2.markdown.builder;

import com.youbenzi.md2.markdown.Block;

/**
 * markdown语法块
 * @author yangyingqiang
 * 2015年4月22日 上午11:44:22
 */
public interface BlockBuilder {

	/**
	 * 创建语法块
	 * @return 结果
	 */
	public Block bulid();
	
	/**
	 * 检查内容是否属于当前语法块
	 * @return 结果
	 */
	public boolean isRightType();
}
