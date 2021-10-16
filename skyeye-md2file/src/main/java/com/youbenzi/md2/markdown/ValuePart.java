/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.youbenzi.md2.markdown;

import java.util.Arrays;
import java.util.Map.Entry;

public class ValuePart{
	private String value;
	private BlockType[] types;
	private int level;
	private String url;
	private String title;
	
	public ValuePart(){
		super();
	}
	
	public ValuePart(String value){
		this.value = revertValue(value);
	}
	
	public ValuePart(String value, BlockType... types){
		this.value = revertValue(value);
		this.types = types;
	}
	
	public String getValue() {
		
		return value;
	}
	public void setValue(String value) {
		this.value = revertValue(value);
	}
	public BlockType[] getTypes() {
		return types;
	}
	public void setTypes(BlockType... types) {
		this.types = types;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	public void addType(BlockType type){
		if(types!=null){
			types = Arrays.copyOf(types, types.length+1);
		}else{
			types = new BlockType[1];
		}
		
		types[types.length-1] = type;
	}

	/**
	 * 还原value中的特殊符号占位符
	 * @param value 对象
	 * @return 结果
	 */
	public String revertValue(String value) {

		for (Entry<String, String> entry : MDToken.PLACEHOLDER_MAP.entrySet()) {
			String tmpValue = entry.getKey().substring(1);	//需要去除第一个反斜杠
			value = value.replace(entry.getValue(), tmpValue);
		}
		return value;
	}
	
	@Override
	public String toString() {
		return "value:"+value+"|types:"+Arrays.toString(types);
	}
	
	public static void main(String[] args) {
		ValuePart part = new ValuePart();
		part.setTypes(BlockType.BOLD_WORD, BlockType.CODE);
		System.out.println(Arrays.toString(part.types));
		part.addType(BlockType.HEADLINE);
		System.out.println(Arrays.toString(part.types));
	}
}
