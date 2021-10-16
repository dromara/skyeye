/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.youbenzi.md2.export.builder;

import com.youbenzi.md2.export.Decorator;

public class DocDecoratorBuilder implements DecoratorBuilder{

	public Decorator build() {
		throw new RuntimeException("暂未支持word doc文档的导出");
	}

}
