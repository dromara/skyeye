/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.youbenzi.md2.markdown;

import java.io.BufferedReader;
import java.util.List;

public class TextOrTable {

	private boolean isTable;
	private BufferedReader reader;
	private List<List<String>> tableData;
	
	public TextOrTable(boolean isTable){
		this.isTable = isTable;
	}
	
	public boolean isTable() {
		return isTable;
	}
	public void setTable(boolean isTable) {
		this.isTable = isTable;
	}
	public BufferedReader getReader() {
		return reader;
	}
	public void setReader(BufferedReader reader) {
		this.reader = reader;
	}
	public List<List<String>> getTableData() {
		return tableData;
	}
	public void setTableData(List<List<String>> tableData) {
		this.tableData = tableData;
	}
}
