
var rowId = "";
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).define(['table', 'jquery', 'winui', 'form', 'codemirror', 'xml', 'clike', 'css', 'htmlmixed', 'javascript', 'nginx',
           'solr', 'sql', 'vue'], function (exports) {
	
	winui.renderColor();
	
	var $ = layui.$,
	form = layui.form,
	table = layui.table;
	
	var jsCreateClick = false;//是否检索生成
	
	var editId = "";//当前编辑的模板id
	
	var list = [];//存储模板生成集合
	//集合内容
	//var s = {
	//		modelId:模板id,
	//		content:当前内容,
	//		tableName:表名,
	//		groupId:模板所属分组id
	//		modelName:模板别名
	//		modelContent:默认内容
	//}
	
	form.render();
	form.on('submit(formSearch)', function (data) {
    	//表单验证
        if (winui.verifyForm(data.elem)) {
        	
        }
        return false;
	});
	
	var editor = CodeMirror.fromTextArea(document.getElementById("modelContent"), {
        mode : "text/x-java",  // 模式
        theme : "eclipse",  // CSS样式选择
        indentUnit : 2,  // 缩进单位，默认2
        smartIndent : true,  // 是否智能缩进
        tabSize : 4,  // Tab缩进，默认4
        readOnly : true,  // 是否只读，默认false
        showCursorWhenSelecting : true,
        lineNumbers : true,  // 是否显示行号
        styleActiveLine: true, //line选择是是否加亮
        matchBrackets: true,
    });
	
	var textEditor = CodeMirror.fromTextArea(document.getElementById("textContent"), {
        mode : "text/x-java",  // 模式
        theme : "eclipse",  // CSS样式选择
        indentUnit : 2,  // 缩进单位，默认2
        smartIndent : true,  // 是否智能缩进
        tabSize : 4,  // Tab缩进，默认4
        readOnly : true,  // 是否只读，默认false
        showCursorWhenSelecting : true,
        lineNumbers : true,  // 是否显示行号
        styleActiveLine: true, //line选择是是否加亮
        matchBrackets: true,
    });
	
	textEditor.on("change",function(){
		for(var i = 0; i < list.length; i++){
			if(list[i].modelId == editId){
				list[i].content = textEditor.getValue();
				break;
			}
		}
	});
	
	//加载模块
	showGrid({
	 	id: "modelList",
	 	url: reqBasePath + "codemodel013",
	 	params: {groupId: parent.rowId},
	 	pagination: false,
	 	template: getFileContent('tpl/codemodelgroup/usemodelgroupmodel.tpl'),
	 	ajaxSendLoadBefore: function(hdb){
	 	},
	 	options: {
	 		'click .selModel':function(index, row){//查看模板
	 			var mode = returnModel(row.modelType);
	        	if (!isNull(mode.length)) {
					editor.setOption('mode', mode);
				} 
	        	editor.setValue(row.modelContent);
	        	layer.open({
		            id: '模板内容',
		            type: 1,
		            title: '模板内容',
		            shade: 0.3,
		            area: ['1200px', '600px'],
		            content: $("#modelContentDiv").html(),
		        });
	 		},
	 		'click .selResult':function(index, row){//查看转换结果
	 			if(jsCreateClick){
	 				var s = getListItem(list, row.id);
	 				if(isNull(s)){
	 					top.winui.window.msg('请先转换模板', {icon: 2,time: 2000});
	 				}else{
	 					editId = row.id;
	 					var mode = returnModel(row.modelType);
	 					if (!isNull(mode.length)) {
	 						textEditor.setOption('mode', mode);
	 					} 
	 					textEditor.setOption('readOnly', false);
	 					textEditor.setValue(s.content);
	 				}
	 			}else{
	 				top.winui.window.msg('请先选择数据库表名检索生成', {icon: 2,time: 2000});
	 			}
	 		},
	 		'click .createResult':function(index, row){//转换结果
	 			if(jsCreateClick){
	 				var content = replaceModelContent(row.modelContent, $("#ControllerPackageName").val(), $("#ServicePackageName").val(),
	 						$("#ServiceImplPackageName").val(), $("#DaoPackageName").val(), $("#tableZhName").val(),
	 						$("#tableFirstISlowerName").val(), $("#tableISlowerName").val(), $("#tableBzName").val());
	 				var s = {
 						modelId: row.id,
 						content: content,
 						tableName: $("#tableName").val(),
 						groupId: parent.rowId,
 						modelName: row.modelName,
 						modelContent: row.modelContent
	 				};
	 				insertListIn(list, s);
	 				top.winui.window.msg('转换成功', {icon: 1,time: 2000});
	 			}else{
	 				top.winui.window.msg('请先选择数据库表名检索生成', {icon: 2,time: 2000});
	 			}
	 		}
	 	},
	 	ajaxSendAfter:function(json){
	 	}
	});
	
	//加载数据库表列表
	showGrid({
	 	id: "tableName",
	 	url: reqBasePath + "database002",
	 	params: {},
	 	pagination: false,
	 	template: getFileContent('tpl/template/select-option.tpl'),
	 	ajaxSendLoadBefore: function(hdb){
	 	},
	 	ajaxSendAfter:function(json){
	 		form.render('select');
	 	}
	});
	
	//转换结果效果切换
	$("body").on("click", ".createResult", function(e){
		if(jsCreateClick){
			$(this).addClass("layui-btn-normal");
		}
	});
	
	//txtcenter DIV内的输入框内容变化事件
	$("body").on("keyup", ".txtcenter input", function(e){
		for(var i = 0; i < list.length; i++){
			list[i].content = replaceModelContent(list[i].modelContent, $("#ControllerPackageName").val(), $("#ServicePackageName").val(),
								$("#ServiceImplPackageName").val(), $("#DaoPackageName").val(), $("#tableZhName").val(),
		 						$("#tableFirstISlowerName").val(), $("#tableISlowerName").val(), $("#tableBzName").val());
			if(list[i].modelId == editId){
				textEditor.setOption('readOnly', false);
				textEditor.setValue(list[i].content);
			}
		}
	});
	
	//检索生成
	$("body").on("click", "#jsCreate", function(e){
		showGrid({
    	 	id: "tableParameterBody",
    	 	url: reqBasePath + "codemodel011",
    	 	params: {tableName: $("#tableName").val()},
    	 	pagination: false,
    	 	template: getFileContent('tpl/codemodelgroup/usemodelgrouptableparameter.tpl'),
    	 	ajaxSendLoadBefore: function(hdb){
    	 	},
    	 	ajaxSendAfter:function(json){
    	 		AjaxPostUtil.request({url:reqBasePath + "codemodel012", params:{tableName: $("#tableName").val()}, type:'json', callback:function(json){
	 	   			if(json.returnCode == 0){
	 	   				$(".createResult").removeClass("layui-btn-normal");
	 	   				jsCreateClick = true;
	 	   				list = [];
	 	   				editId = "";
		 	   			textEditor.setOption('readOnly', true);
	 					textEditor.setValue('');
	 	   				$("#tableZhName").val(json.bean.tableName);
	 	   				$("#tableFirstISlowerName").val(json.bean.tableFirstISlowerName);
	 	   				$("#ControllerPackageName").val(json.bean.ControllerPackageName);
	 	   				$("#ServicePackageName").val(json.bean.ServicePackageName);
    	 	   			$("#ServiceImplPackageName").val(json.bean.ServiceImplPackageName);
    	 	   			$("#DaoPackageName").val(json.bean.DaoPackageName);
    	 	   			$("#tableISlowerName").val(json.bean.tableISlowerName);
    	 	   			$("#tableBzName").val(json.bean.tableBzName);
    	 	   			//遍历模板
						$('#modelList').find('li').each(function() {
							var label = $(this).find("label");
							$("#" + label.attr("relation")).val(json.bean.tableName + label.attr("thiscontent"));
						});
						top.winui.window.msg('检索成功', {icon: 1,time: 2000});
	 	   			}else{
	 	   				top.winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
	 	   			}
	 	   		}});
    	 	}
    	});
	});
	
	/**
	 * 向集合中添加新元素
	 */
	function insertListIn(list, s){
		var isIn = false;
		for(var i = 0; i < list.length; i++){
			if(list[i].modelId == s.modelId){
				list[i].content = s.content;
				isIn = true;
				break;
			}
		}
		if(!isIn){//不存在
			list.push(s);
		}
	}
	
	/**
	 * 获取集合中的一项
	 */
	function getListItem(list, id){
		for(var i = 0; i < list.length; i++){
			if(list[i].modelId == id){
				return list[i];
			}
		}
		return null;
	}
	
    exports('usemodelgroup', {});
});
