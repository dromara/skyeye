package com.skyeye.common.constans;

public enum QuType {

	YESNO("是非题", "yesno", 0, "", ""), 
	RADIO("单选题", "radio", 1, "dw_qu_radio", "qu_id"), 
	CHECKBOX("多选题", "checkbox", 2, "dw_qu_checkbox", "qu_id"), 
	FILLBLANK("填空题", "fillblank", 3, "", ""),

	MULTIFILLBLANK("多项填空题", "multi-fillblank", 4, "dw_qu_multi_fillblank", "qu_id"), // 组合填空题
	ANSWER("多行填空题", "answer", 5, "", ""), // 原问答题
	BIGQU("大题", "bigqu", 6, "", ""),

	ENUMQU("枚举题", "enumqu", 7, "", ""), 
	SCORE("评分题", "score", 8, "dw_qu_score", "qu_id"), 
	ORDERQU("排序题", "orderby", 9, "dw_qu_orderby", "qu_id"), 
	PROPORTION("比重题", "proportion", 10, "", ""), 
	
	CHENRADIO("矩阵单选题", "chen-radio", 11, "dw_qu_chen_column,dw_qu_chen_row", "qu_id,qu_id"), 
	CHENFBK("矩阵填空题", "chen-fbk", 12, "dw_qu_chen_column,dw_qu_chen_row", "qu_id,qu_id"), 
	CHENCHECKBOX("矩阵多选题", "chen-checkbox", 13, "dw_qu_chen_column,dw_qu_chen_row", "qu_id,qu_id"), 
	COMPCHENRADIO("复合矩阵单选题", "comp-chen-radio", 14, "", ""),

	UPLOADFILE("文件上传题", "sendfile", 15, "", ""), 
	PAGETAG("分页标记", "pagetag", 16, "", ""), 
	PARAGRAPH("段落说明", "paragraph", 17, "", ""), 
	CHENSCORE("矩阵评分题", "chen-score", 18, "dw_qu_chen_column,dw_qu_chen_row", "qu_id,qu_id"),
	
	COMPRADIO("复合单选", "comp-radio", 19, "", ""), 
	COMPCHECKBOX("复合多选", "comp-checkbox", 20, "", "");

	private String cnName;
	private String actionName;
	private int index;
	private String tableName;
	private String questionId;

	QuType(String cnName, String actionName, int index, String tableName, String questionId) {
		this.cnName = cnName;
		this.actionName = actionName;
		this.index = index;
		this.tableName = tableName;
		this.questionId = questionId;
	}

	public String getCnName() {
		return cnName;
	}

	public void setCnName(String cnName) {
		this.cnName = cnName;
	}

	public String getActionName() {
		return actionName;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
	
	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	public String getQuestionId() {
		return questionId;
	}

	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}

	public static String getActionName(int index){
		for (QuType q : QuType.values()){
			if(q.getIndex() == index){
				return q.actionName;
			}
		}
		return null;
	}
	
	public static String getCName(int index){
		for (QuType q : QuType.values()){
			if(q.getIndex() == index){
				return q.cnName;
			}
		}
		return null;
	}
	
	public static String getTableName(int index){
		for (QuType q : QuType.values()){
			if(q.getIndex() == index){
				return q.tableName;
			}
		}
		return null;
	}
	
	public static String getQuestionId(int index){
		for (QuType q : QuType.values()){
			if(q.getIndex() == index){
				return q.questionId;
			}
		}
		return null;
	}
	
	public static int getIndex(String actionName){
		for (QuType q : QuType.values()){
			if(q.getActionName().replaceAll("-", "").equals(actionName.toLowerCase())){
				return q.index;
			}
		}
		return -1;
	}

}
