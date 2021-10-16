/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.youbenzi.md2.markdown;

import java.util.Arrays;
import java.util.List;

public class Block {

	private BlockType type;
	private ValuePart[] valueParts;
	private int level;
	private List<List<String>> tableData;
	private List<Block> listData;
	
	public BlockType getType() {
		return type;
	}
	public void setType(BlockType type) {
		this.type = type;
	}
	public ValuePart[] getValueParts() {
		return valueParts;
	}
	public void setValueParts(ValuePart... valueParts) {
		this.valueParts = valueParts;
	}
	public void setValueParts(List<ValuePart> parts) {
		this.valueParts = new ValuePart[parts.size()];
		for (int i=0, l=parts.size(); i<l; i++) {
			this.valueParts[i] = parts.get(i);
		}
	}
	
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public List<List<String>> getTableData() {
		return tableData;
	}
	public void setTableData(List<List<String>> tableData) {
		this.tableData = tableData;
	}
	public List<Block> getListData() {
		return listData;
	}
	public void setListData(List<Block> listData) {
		this.listData = listData;
	}
	@Override
	public String toString() {
		return "blockType:"+type+"|valueParts:"+Arrays.toString(valueParts)+"|level:"+level;
	}
}
