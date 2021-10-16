/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.youbenzi.md2.markdown.builder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import com.youbenzi.md2.markdown.Block;
import com.youbenzi.md2.markdown.BlockType;
import com.youbenzi.md2.markdown.MDAnalyzer;
import com.youbenzi.md2.markdown.MDToken;
import com.youbenzi.md2.markdown.ValuePart;

public abstract class ListBuilder implements BlockBuilder{

	private String content;
	private BlockType blockType;
	public ListBuilder(String content, BlockType blockType){
		this.content = content;
		this.blockType = blockType;
	}
	
	public Block bulid() {
		Block result = new Block();
		BufferedReader br = new BufferedReader(new StringReader(content));
		String value;
		try {
			value = br.readLine();
			List<Block> listData = new ArrayList<Block>();
			while (value!=null) {
				value = value.trim();
				int index = computeCharIndex(value);
				if(index<0){
					value = br.readLine();
					continue;
				}
				value = value.substring(index+1).trim();
				
				if(value.equals("")){	//空行直接忽略
					value = br.readLine();
					continue;
				}
				int i = 0;
				if(value.startsWith(MDToken.HEADLINE)){	//检查是否有标题格式
					i = value.lastIndexOf(MDToken.HEADLINE);
				}
				if(i>0){
					value = value.substring(i+1).trim();
				}
				
				List<ValuePart> list = MDAnalyzer.analyzeTextLine(value);
				if(i>0){
					for (ValuePart valuePart : list) {
						valuePart.addType(BlockType.HEADLINE);
						valuePart.setLevel(i);
					}
				}
				Block block = new Block();
				block.setType(blockType);
				block.setValueParts(list);
				listData.add(block);
				value = br.readLine();
			}
			result.setType(blockType);
			result.setListData(listData);
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public boolean isRightType() {
		return false;
	}
	
	public abstract int computeCharIndex(String str);

}
