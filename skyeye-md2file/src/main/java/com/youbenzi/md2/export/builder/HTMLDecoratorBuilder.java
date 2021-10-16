/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.youbenzi.md2.export.builder;

import com.youbenzi.md2.export.Decorator;
import com.youbenzi.md2.export.HTMLDecorator;

public class HTMLDecoratorBuilder implements DecoratorBuilder{

	public Decorator build() {
		return new HTMLDecorator();
	}

}
