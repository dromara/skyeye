/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.youbenzi.md2.export;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.youbenzi.md2.markdown.Block;
import com.youbenzi.md2.markdown.BlockType;
import com.youbenzi.md2.markdown.ValuePart;


public class HTMLDecorator implements Decorator{

	private StringBuilder content = new StringBuilder();
	
	public void beginWork(String outputFilePath, String sysWaterMark) {
		
	}
	public void decorate(List<Block> list, String webRootNdc) {
		for (Block block : list) {
			try{
				String str;
				switch (block.getType()) {
				case CODE:
					str = codeParagraph(block.getValueParts());
					break;
				case HEADLINE:
					str = headerParagraph(block.getValueParts(), block.getLevel());
					break;
				case QUOTE:
					str = quoteParagraph(block.getListData());
					break;
				case TABLE:
					str = tableParagraph(block.getTableData());
					break;
				case UNORDERED_LIST:
					str = unorderedListParagraph(block.getListData());
					break;
				case ORDERED_LIST:
					str = orderedListParagraph(block.getListData());
					break;
				default:
					str = commonTextParagraph(block.getValueParts());
					break;
				}

				content.append(str);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	public void afterWork(String outputFilePath) {
		File file =  new File(outputFilePath);
		FileWriter writer = null;
		try {
			writer = new FileWriter(file);
			writer.write(content.toString());
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			try {
				if(writer!=null){
					writer.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public String outputHtml(){
		return content.toString();
	}
	
	private String codeParagraph(ValuePart[] valueParts){

		String value = valueParts[0].getValue();
		StringBuilder tmp = new StringBuilder("<pre><code>\n");
		value = value.replaceAll("<", "&lt;");
		value = value.replaceAll(">", "&gt;");
		tmp.append(value).append("\n");
		tmp.append("</code></pre>\n");
		
		return tmp.toString();
	}
	
	private String headerParagraph(ValuePart[] valueParts, int level){
		level = level + 1;
		StringBuilder tmp = new StringBuilder("<h"+level+">");
		
		for (ValuePart valuePart : valueParts) {
			BlockType[] types = valuePart.getTypes();
			String value  = valuePart.getValue();
			if(types!=null){
				for (BlockType type : types) {
					value = formatByType(type, value, valuePart);
				}
			}
			tmp.append(value);
		}
		tmp.append("</h"+level+">\n");
		return tmp.toString();
	}

	private String quoteParagraph(List<Block> listData){
		StringBuilder tmp = new StringBuilder("<blockquote>\n");
		for (Block block : listData) {
			tmp.append(listLine(block.getValueParts(), "p"));
		}
		tmp.append("</blockquote>\n");
		
		return tmp.toString();
	}
	
	private String formatByType(BlockType type, String value, ValuePart valuePart){
		switch (type) {
			case BOLD_WORD:
				return "<strong>"+value+"</strong>";
			case ITALIC_WORD:
				return "<em>"+value+"</em>";
			case STRIKE_WORD:
				return "<del>"+value+"</del>";
			case CODE_WORD:
				return "<code>"+value+"</code>";
			case HEADLINE:
				return "<h"+valuePart.getLevel()+">"+value+"</h"+valuePart.getLevel()+">";
			case LINK:
				return "<a href=\""+valuePart.getUrl()+"\" title=\"" + value + "\">"+value+"</a>";
			case IMG:
				return "<img src=\"" + valuePart.getUrl() + "\" title=\"" 
					+ valuePart.getTitle() + "\" alt=\""+ valuePart.getTitle() +"\" />";
			default:
				return value;
		}
	}
	
	private String tableParagraph(List<List<String>> tableData){
		
		int nRows = tableData.size();
    	int nCols = 0;
    	for (List<String> list : tableData) {
			int s = list.size();
			if(nCols<s){
				nCols = s;
			}
		}
    	StringBuilder tmp = new StringBuilder("<table>\n");
    	
        for (int i=0; i<nRows; i++) {
			tmp.append("<tr>\n");
			List<String> colDatas = tableData.get(i);
			for(int j=0; j<nCols; j++){
				
				if(i==0){
					tmp.append("<th>");
				}else{
					tmp.append("<td>");
				}
				tmp.append("<p>");
				try {
					tmp.append(colDatas.get(j));
				} catch (Exception e) {
					tmp.append("");
				}
				tmp.append("</p>");
				if(i==0){
					tmp.append("</th>\n");
				}else{
					tmp.append("</td>\n");
				}
			}
			tmp.append("</tr>\n");
		}
        tmp.append("</table>\n");
        return tmp.toString();
    }
	
	private String unorderedListParagraph(List<Block> listData){
		StringBuilder tmp = new StringBuilder("<ul>\n");
		for (Block block : listData) {
			tmp.append(listLine(block.getValueParts(), "li"));
		}
		tmp.append("</ul>\n");
		
		return tmp.toString();
	}
	
	private String orderedListParagraph(List<Block> listData){
		StringBuilder tmp = new StringBuilder("<ol>\n");
		for (Block block : listData) {
			tmp.append(listLine(block.getValueParts(), "li"));
		}
		tmp.append("</ol>\n");
		
		return tmp.toString();
	}
	
	private String commonTextParagraph(ValuePart[] valueParts){
		
		return listLine(valueParts, "p");
	}
	
	private String listLine(ValuePart[] valueParts, String tag){
		StringBuilder tmp = new StringBuilder();
		if(valueParts==null){
			return tmp.toString();
		}
		tmp.append("<"+tag+">");
		for (ValuePart valuePart : valueParts) {
			BlockType[] types = valuePart.getTypes();
			
			String value = valuePart.getValue();
			if(hasLink(types)){
				value = valuePart.getTitle();
			}
			if(types!=null){
				for (BlockType type : types) {
					value = formatByType(type, value, valuePart);
				}
			}
			tmp.append(value);
		}
		tmp.append("</"+tag+">\n");
		return tmp.toString();
	}
	
	private boolean hasLink(BlockType[] types){
		if(types==null){
			return false;
		}
		for (BlockType blockType : types) {
			if(blockType==BlockType.LINK){
				return true;
			}
		}
		return false;
	}
}
