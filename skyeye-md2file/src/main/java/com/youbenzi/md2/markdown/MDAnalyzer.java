/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.youbenzi.md2.markdown;

import com.youbenzi.md2.markdown.builder.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Pattern;

public class MDAnalyzer {
	
	private static List<String> mdTokenInLine = Arrays.asList(MDToken.BOLD_WORD, MDToken.ITALIC_WORD, MDToken.ITALIC_WORD_2, MDToken.STRIKE_WORD, MDToken.CODE_WORD, MDToken.IMG, MDToken.LINK);

	public static List<Block> analyze(BufferedReader reader){
		List<TextOrTable> list = tableFilter(reader);
		List<Block> blocks = new ArrayList<Block>();
		for (TextOrTable textAndTable : list) {
			if(textAndTable.isTable()){
				Block block = new Block();
				block.setType(BlockType.TABLE);
				block.setTableData(textAndTable.getTableData());
				blocks.add(block);
			}else{
				List<Block> tmps = analyzeTextNoTable(textAndTable.getReader());
				for (Block block : tmps) {
					blocks.add(block);
				}
			}
		}
		return blocks;
	}
	
	/**
	 * 不包含table格式的语法解析
	 * @param reader
	 * @return
	 */
	private static List<Block> analyzeTextNoTable(BufferedReader reader){
		List<Block> list = new ArrayList<Block>();
		try {
			List<String> lines = new ArrayList<String>();
			String tmp = reader.readLine();
			while (tmp != null) {	//将内容每一行都存入list中
				lines.add(tmp);
				tmp = reader.readLine();
			}
			boolean hasCode = true;	//内容是否包含代码格式的标志
			for (int idx = 0, si = lines.size(); idx < si; idx++) {
				Block block = null;
				String str = lines.get(idx);
				if(str.trim().equals("")){	//空行直接忽略
					continue;
				}
				if(str.trim().startsWith(MDToken.CODE) && hasCode){
					StringBuilder sb = new StringBuilder();
					boolean isCodeEnd = false;
					for (int idx1 = (idx+1); idx1 < si; idx1++) {
						str = lines.get(idx1);
						if(str.trim().equals(MDToken.CODE)){	//检查是否有代码结束符
							isCodeEnd = true;
							idx = idx1;
							break;
						}else{
							sb.append(str+"\n");
						}
					}

					if(isCodeEnd){	
						block = new CodeBuilder(sb.toString()).bulid();
					}else{	//没代码结束符，则下次不会再进来检查代码格式，游标置回代码格式检查之前
						idx = idx - 1;
						hasCode = false;
						continue;
					}
				}else if(str.startsWith(MDToken.CODE_BLANK)){
					Object[] tmps = analyzerList(idx, lines, new ListBuilderCon() {
						@Override
						public Block newBuilder(String str) {
							return new CodeBuilder(str).bulid();
						}
						
						@Override
						public boolean isRightType(String lineStr) {
							return lineStr.startsWith(MDToken.CODE_BLANK);
						}
						
						@Override
						public StringBuilder how2AppendIfBlank(StringBuilder sb) {
							return sb.append("\n");
						}
						
						public StringBuilder how2AppendIfNotBlank(StringBuilder sb, String value) {
							return sb.append(value.substring(MDToken.CODE_BLANK.length())+"\n");
						}
					});

					idx = (Integer)tmps[0];
					block = (Block)tmps[1];
				}else if(str.trim().startsWith(MDToken.HEADLINE)){
					block = new HeaderBuilder(str).bulid();
				}else if(isQuote(str)){
					Object[] tmps = analyzerList(idx, lines, new ListBuilderCon() {
						public Block newBuilder(String str) {
							return new QuoteBuilder(str).bulid();
						}
						
						public boolean isRightType(String lineStr) {
							return isQuote(lineStr);
						}
					});
					idx = (Integer)tmps[0];
					block = (Block)tmps[1];
				}else if(isUnOrderedList(str)){
					Object[] tmps = analyzerList(idx, lines, new ListBuilderCon() {
						public Block newBuilder(String str) {
							return new UnorderedListBuilder(str).bulid();
						}
						
						public boolean isRightType(String lineStr) {
							return isUnOrderedList(lineStr);
						}
					});

					idx = (Integer)tmps[0];
					block = (Block)tmps[1];
				}else if(isOrderedList(str)){
					Object[] tmps = analyzerList(idx, lines, new ListBuilderCon() {
						public Block newBuilder(String str) {
							return new OrderedListBuilder(str).bulid();
						}
						
						public boolean isRightType(String lineStr) {
							return isOrderedList(lineStr);
						}
					});

					idx = (Integer)tmps[0];
					block = (Block)tmps[1];
				}else{
					if((idx+1) < si){
						String nextStr = lines.get(idx+1);
						int lvl = HeaderBuilder.isRightType(nextStr);
						if(lvl>0){
							block = new HeaderBuilder(str).bulid(lvl);
							idx++;
						}
					}
					if(block==null){
						block = new CommonTextBuilder(str).bulid();
					}
				}
				if(block!=null){
					list.add(block);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			if(reader!=null){
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return list;
	}
	
	/**
	 * 筛选表格
	 * @param reader 缓存的数据
	 * @return 按文本顺序排列的文本或者表格的列表
	 */
	public static List<TextOrTable> tableFilter(BufferedReader reader){
		List<TextOrTable> list = new ArrayList<TextOrTable>();
		List<String> lines = new ArrayList<String>();
		
		try {
			String tmp = reader.readLine();
			while (tmp != null) {	//将内容每一行都存入list中
				lines.add(tmp);
				tmp = reader.readLine();
			}
//			boolean inCode = false;
			StringBuffer sb = new StringBuffer();
			for (int i = 0, l = lines.size(); i < l; i++) {
				String str = lines.get(i);
				boolean hasTable = false;
				if(str.indexOf("|")>-1 ){	//检查是否有table的分隔符
					hasTable = true;
					boolean isStart = false;
					boolean isEnd = false;
					if(str.startsWith("\\|")){	//去头
						str = str.substring(1);
						isStart = true;
					}
					if(str.endsWith("\\|")){	//去尾
						str = str.substring(0, str.length()-1);
						isEnd = true;
					}
					String[] parts = str.split("\\|");
					if(parts.length<=1 && !(isStart&&isEnd)){
						hasTable = false;
					}
				}
				if(hasTable){
					if((i+1)<l){	//检查到符合规范的table头之后，检测下一行是否为 ---|---的类似字符串
						String nextLine = lines.get(i+1);
						String[] nextParts = nextLine.split("\\|");
						for (String part : nextParts) {

							part = part.trim().replaceAll("-", "");
							if(part.length()>0){
								hasTable = false;
							}
							if(!hasTable){
								break;
							}
						}
					}else{
						hasTable = false;
					}
				}
				if(hasTable){	//检查到真的有table存在

					if(!sb.toString().equals("")){	//把已存入stringbuffer的内容先归档
						TextOrTable text = new TextOrTable(false);
						text.setReader(new BufferedReader(new StringReader(sb.toString())));
						list.add(text);
						
						sb = new StringBuffer("");	//将stringbuffer重新置为空
					}
					
					List<List<String>> tableDataList = new ArrayList<List<String>>();
					int tableLineNum = i+1; 	//---|---的行数，此行不能放入table的data
					for (int j = i; j < l; j++){
						if(j==tableLineNum){
							continue;
						}
						String tableLine = lines.get(j);
						String[] cellDatas = tableLine.split("\\|");
						if(cellDatas.length>=2){	//此行是table的数据
							tableDataList.add(Arrays.asList(cellDatas));
							if(j==(l-1)){	//到内容底部，table数据结束，归档
								tableDataList = trimTableData(tableDataList);
								TextOrTable table = new TextOrTable(true);
								table.setTableData(tableDataList);
								list.add(table);
								
								i = j;	//设置游标，跳出循环
								break;
							}
						}else{	//table数据结束，归档
							tableDataList = trimTableData(tableDataList);
							TextOrTable table = new TextOrTable(true);
							table.setTableData(tableDataList);
							list.add(table);
							
							i = (j-1);	//设置游标，跳出循环
							break;
						}
					}
				}else{
					sb.append(str+"\n");
				}
			}
			if(!sb.toString().equals("")){
				TextOrTable text = new TextOrTable(false);
				text.setReader(new BufferedReader(new StringReader(sb.toString())));
				list.add(text);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	/**
	 * 删除表格数据中的头尾空白列
	 * @param tableDataList
	 * @return
	 */
	private static List<List<String>> trimTableData(List<List<String>> tableDataList){
		boolean isFirstEmpty = true;
		boolean isLastEmpty = true;
		for (int k = 0, m=tableDataList.size(); k < m; k++) {
			List<String> tmps = tableDataList.get(k);
			if(!tmps.get(0).trim().equals("") && isFirstEmpty){
				isFirstEmpty = false;
			}
			if(!tmps.get(tmps.size()-1).trim().equals("") && isLastEmpty){
				isLastEmpty = false;
			}
		}
		if(isLastEmpty){
			for (int k = 0, m=tableDataList.size(); k < m; k++) {
				List<String> tmps = tableDataList.get(k);
				List<String> newTmps = new ArrayList<String>();
				for (int n = 0, o=tmps.size(); n < o; n++) {
					if(n<(o-1)){
						newTmps.add(tmps.get(n));
					}
				}
				tableDataList.set(k, newTmps);
			}
		}
		if(isFirstEmpty){
			for (int k = 0, m=tableDataList.size(); k < m; k++) {
				List<String> tmps = tableDataList.get(k);
				List<String> newTmps = new ArrayList<String>();
				for (int n = 0, o=tmps.size(); n < o; n++) {
					if(n>0){
						newTmps.add(tmps.get(n));
					}
				}
				tableDataList.set(k, newTmps);
			}
		}
		return tableDataList;
	}
	
	/**
	 * 对一行文本进行语法分析，主要针对加粗，斜体等能在句中使用的格式
	 * @param text 一行文本
	 * @return 结果
	 */
	public static List<ValuePart> analyzeTextLine(String text){
		text = text.trim();
		return analyzeTextLine(text, new ArrayList<String>(), new ArrayList<String>());
	}
	
	/**
	 * 对一行文本进行语法分析，主要针对加粗，斜体等能在句中使用的格式
	 * @param text 一行文本
	 * @param notCheckMDTokens 已经检查过的md语法
	 * @param currentTypes	当前文本已有的语法
	 * @return 结果
	 */
	public static List<ValuePart> analyzeTextLine(String text, List<String> notCheckMDTokens, List<String> currentTypes){
		List<ValuePart> result = new ArrayList<ValuePart>();
		if(text == null || text.length() < 0 ){
			return result;
		}
		for (Entry<String, String> entry : MDToken.PLACEHOLDER_MAP.entrySet()) {
			text = text.replace(entry.getKey(), entry.getValue());
		}
		
		int i = text.length();
		String mdToken = null;
		for (String tmp : mdTokenInLine) {	//检查是否有指定的md语法
			if(notCheckMDTokens.contains(tmp)){
				continue;
			}
			int j = text.indexOf(tmp);
			if(j>-1 && i>j){	//找到第一个符合要求的md语法
				i = j;
				mdToken = tmp;
			}
		}
		if(mdToken!=null){	//有指定的md语法
			LinkOrImageBeanTmp linkOrImageBeanTmp = null;
			int j = -1;
			if(mdToken.equals(MDToken.LINK) || mdToken.equals(MDToken.IMG)){
				linkOrImageBeanTmp = hasLinkOrImage(text, mdToken.equals(MDToken.LINK));
				if(linkOrImageBeanTmp!=null){
					j = linkOrImageBeanTmp.getEndIndex();
				}
			}else{
				j = text.indexOf(mdToken, i + mdToken.length());
			}
			if(j>-1){	//该语法完整
				if(i>0){
					String v1 = text.substring(0, i);
					ValuePart valuePart = createValuePart(v1, currentTypes);
					result.add(valuePart);
				}

				notCheckMDTokens.add(mdToken);
				currentTypes.add(mdToken);
				if(linkOrImageBeanTmp!=null){
					List<String> ct4Link = new ArrayList<String>();
					for (String type : currentTypes) {
						ct4Link.add(type);
					}
					ValuePart valuePart = null;
					if(linkOrImageBeanTmp.isLink()){
						valuePart = analyzeTextInLink(linkOrImageBeanTmp.getTitle(), notCheckMDTokens, ct4Link);
						String tmpValue = valuePart.getTitle()+"("+linkOrImageBeanTmp.getUrl()+")";
						valuePart.setValue(tmpValue);
					}else{
						valuePart = createValuePart(linkOrImageBeanTmp.getUrl(), ct4Link);
						valuePart.setTitle(linkOrImageBeanTmp.getTitle());
					}
					valuePart.setUrl(linkOrImageBeanTmp.getUrl());
					result.add(valuePart);
				} else {
					String v2 = text.substring(i+mdToken.length(), j);
					List<ValuePart> tmpList2 = analyzeTextLine(v2, notCheckMDTokens, currentTypes);
					for (ValuePart valuePart : tmpList2) {
						result.add(valuePart);
					}
				}
				String v3 = "";
				if(mdToken.equals(MDToken.IMG)){	//image的开始符是两个字符，结束符是一个字符，所以要特殊处理
					v3 = text.substring(j + 1);
				}else{	//其它标签的开始符跟结束符长度一致
					v3 = text.substring(j + mdToken.length());
				}
				
				notCheckMDTokens.remove(notCheckMDTokens.size()-1);
				currentTypes.remove(currentTypes.size()-1);
				List<ValuePart> tmpList1 = analyzeTextLine(v3, notCheckMDTokens, currentTypes);
				for (ValuePart valuePart : tmpList1) {
					result.add(valuePart);
				}
			}else{	//该语法不完整，没结束符
				notCheckMDTokens.add(mdToken);
				List<ValuePart> tmpList = analyzeTextLine(text, notCheckMDTokens, currentTypes);
				for (ValuePart valuePart : tmpList) {
					result.add(valuePart);
				}
			}
		}else{	//没有指定的md语法
			if(text!=null && text.length()>0){
				ValuePart valuePart = createValuePart(text, currentTypes);
				result.add(valuePart);
			}
		}
		return result;
	}
	
	private static ValuePart createValuePart(String value, List<String> mdTokens){
		ValuePart valuePart = new ValuePart();
		valuePart.setValue(value);
		if(mdTokens.size()>0){
			BlockType[] types = new BlockType[mdTokens.size()];
			int i = 0;
			for (int k=(mdTokens.size()-1); k>=0; k--) {
				types[i] = MDToken.convert(mdTokens.get(k));	//这里引入i，是为了数组反序
				i++;
			}
			valuePart.setTypes(types);
		}
		
		return valuePart;
	}
	
	private static LinkOrImageBeanTmp hasLinkOrImage(String str, boolean isLink){
		LinkOrImageBeanTmp linkOrImageBean = new LinkOrImageBeanTmp();
		linkOrImageBean.setLink(isLink);
		String token = null;
		if(isLink){
			token = MDToken.LINK;
		}else{
			token = MDToken.IMG;
		}
		int i = str.indexOf(token);
		int j = str.indexOf("]", i);
		if(j>0){
			int k = str.indexOf("(", j);
			if(k>0&&k==(j+1)){
				int l = str.indexOf(")", k);
				if(l>0){
					String strHasUrl = str.substring(k+1, l).trim();
					int m = strHasUrl.indexOf(" ");
					String url = "";
					if(m>-1){
						url = strHasUrl.substring(0, m);
					}else{
						url = strHasUrl;
					}
					String title = str.substring(i + token.length(), j);
					linkOrImageBean.setBeginIndex(i);
					linkOrImageBean.setEndIndex(l);
					linkOrImageBean.setTitle(title);
					linkOrImageBean.setUrl(url);
					return linkOrImageBean;
				}
			}
		}
		return null;
	}
	
	private static ValuePart analyzeTextInLink(String str, List<String> notCheckMDTokens, List<String> currentTypes){
		String mdToken = null;
		for (String tmp : mdTokenInLine) {	//检查是否有指定的md语法
			if(notCheckMDTokens.contains(tmp)){
				continue;
			}
			if(str.startsWith(tmp)){
				int end = str.indexOf(tmp, tmp.length());
				if(end>0){
					mdToken = tmp;
					break;
				}
			}
		}
		if(mdToken!=null){
			notCheckMDTokens.add(mdToken);
			currentTypes.add(mdToken);
			str = str.substring(mdToken.length(), str.length() - mdToken.length());
			return analyzeTextInLink(str, notCheckMDTokens, currentTypes);
		}else{
			ValuePart valuePart = createValuePart("", currentTypes);
			valuePart.setTitle(str);
			return valuePart;
		}
	}
	
	private static boolean isOrderedList(String str){
		return Pattern.matches("^[\\d]+\\. [\\d\\D][^\n]*$", str);
	}
	
	private static boolean isUnOrderedList(String str){
		return str.trim().startsWith(MDToken.UNORDERED_LIST1) || str.trim().startsWith(MDToken.UNORDERED_LIST2);
	}
	
	private static boolean isQuote(String str){
		return str.trim().startsWith(MDToken.QUOTE);
	}

	private static Object[] analyzerList(int idx, List<String> lines, ListBuilderCon listBuilderCon){
		StringBuilder sb = new StringBuilder();
		int si = lines.size();
		for (int idx1 = idx; idx1 < si; idx1++) {
			String str = lines.get(idx1);
			if(str.trim().equals("")){
				idx = idx1;
				sb = listBuilderCon.how2AppendIfBlank(sb);
				continue;
			}
			if(!listBuilderCon.isRightType(str)){	//检查是否为列表语法
				idx = idx1-1;
				break;
			}else{
				sb = listBuilderCon.how2AppendIfNotBlank(sb, str);
			}
			if(idx1==(si-1)){	//是否已到行尾
				idx = idx1;
			}
		}
		Object[] tmps = new Object[2];
		tmps[0] = idx;
		tmps[1] = listBuilderCon.newBuilder(sb.toString());
		return tmps;
	}
	
	public static void main(String[] args) {
		String text = "\\\\_q312_ \\*\\**123123**123123**";
		for (Entry<String, String> entry : MDToken.PLACEHOLDER_MAP.entrySet()) {
			text = text.replace(entry.getKey(), entry.getValue());
		}
		
		System.out.println(text);
	}
}
