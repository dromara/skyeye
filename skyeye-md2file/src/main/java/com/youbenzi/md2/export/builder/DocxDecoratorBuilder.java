/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.youbenzi.md2.export.builder;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

import com.youbenzi.md2.export.Decorator;
import com.youbenzi.md2.export.DocxDecorator;

public class DocxDecoratorBuilder implements DecoratorBuilder{

	public Decorator build() {
		return new DocxDecorator(new XWPFDocument());
	}

}
