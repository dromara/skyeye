
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form', 'codemirror', 'xml', 'clike', 'css', 'htmlmixed', 'javascript', 'nginx',
           'solr', 'sql', 'vue'], function (exports) {
	var index = parent.layer.getFrameIndex(window.name);
	winui.renderColor();
	var $ = layui.$,
		form = layui.form,
		table = layui.table;
	
	var jsCreateClick = false;//是否检索生成
	
	var editId = "";//当前编辑的模板id
	
	var list = [];//存储模板生成集合
	//集合内容
	//{
	//		modelId:模板id,
	//		content:当前内容,
	//		tableName:表名,
	//		groupId:模板所属分组id
	//		modelName:模板别名
	//		modelContent:默认内容
	//		fileName:文件名称
	//		modelType:模板类型
	//}
	
	matchingLanguage();
	form.render();
	form.on('submit(formSearch)', function (data) {
        if (winui.verifyForm(data.elem)) {
        	var subData = list;
        	if(subData.length == 0){
        		winui.window.msg('请先生成转换结果', {icon: 2,time: 2000});
        	}else if($('#modelList').find('li').length > subData.length){
        		winui.window.msg('您有模板未生成代码文件，请检查。', {icon: 2,time: 2000});
        	}else{
        		for(var i = 0; i < subData.length; i++){
        			subData[i].modelContent = "";
        			subData[i].content = subData[i].content
						.replace(/\+/g, "%2B")
						.replace(/\&/g, "%26")
						.replace(/\%/g, "%25");
        		}
        		AjaxPostUtil.request({url:reqBasePath + "codemodel014", params:{jsonData: JSON.stringify(subData)}, type: 'json', callback: function(json){
        			if(json.returnCode == 0){
        				winui.window.msg('保存成功，请前往生成历史下载。', {icon: 1,time: 2000}, function(){
	        				parent.layer.close(index);
	        				parent.refreshCode = '0';
        				});
        			}else{
        				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
        			}
        		}});
        	}
        }
        return false;
	});

	// 展示模板内容的对象
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
	
	// 加载模块
	showGrid({
	 	id: "modelList",
	 	url: reqBasePath + "codemodel013",
	 	params: {groupId: parent.rowId},
	 	pagination: false,
	 	template: getFileContent('tpl/codemodelgroup/codeModelListItem.tpl'),
	 	ajaxSendLoadBefore: function(hdb, json){
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
		            area: ['90vw', '90vh'],
		            content: $("#modelContentDiv").html(),
		        });
	 		},
	 		'click .selResult':function(index, row){//查看转换结果
	 			if(jsCreateClick){
	 				var s = getListItem(list, row.id);
	 				if(isNull(s)){
	 					winui.window.msg('请先转换模板', {icon: 2,time: 2000});
	 				}else{
	 					editId = row.id;
	 					var mode = returnModel(row.modelType);
	 					if (!isNull(mode.length)) {
	 						textEditor.setOption('mode', mode);
	 					} 
	 					textEditor.setValue(s.content);
	 				}
	 			}else{
	 				winui.window.msg('请先选择数据库表名检索生成', {icon: 2,time: 2000});
	 			}
	 		}
	 	},
	 	ajaxSendAfter:function(json){
			list = [].concat(json.rows);
	 	}
	});
	
	// 加载数据库表列表
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
	
	// txtcenter DIV内的输入框内容变化事件
	$("body").on("keyup", ".txtcenter input", function(e){
		for(var i = 0; i < list.length; i++){
			list[i].content = codeDocUtil.replaceModelContent(list[i].modelContent);
			list[i].fileName = $("#tableZhName").val() + list[i].modelName;
			if(list[i].modelId == editId){
				textEditor.setValue(list[i].content);
			}
		}
	});
	
	// 检索数据
	$("body").on("click", "#jsCreate", function(e){
		AjaxPostUtil.request({url: reqBasePath + "codemodel011", params: {tableName: $("#tableName").val()}, type: 'json', callback: function(data){
			if(data.returnCode == 0) {
				console.log(data)
				AjaxPostUtil.request({url: reqBasePath + "codemodel012", params: {tableName: $("#tableName").val()}, type: 'json', callback: function(json){
					if(json.returnCode == 0) {
						jsCreateClick = true;
						editId = "";
						textEditor.setValue('');
						$("#tableZhName").val(json.bean.tableName);
						$("#tableFirstISlowerName").val(json.bean.tableFirstISlowerName);
						$("#ControllerPackageName").val(json.bean.ControllerPackageName);
						$("#ServicePackageName").val(json.bean.ServicePackageName);
						$("#ServiceImplPackageName").val(json.bean.ServiceImplPackageName);
						$("#DaoPackageName").val(json.bean.DaoPackageName);
						$("#tableISlowerName").val(json.bean.tableISlowerName);
						$("#tableBzName").val(json.bean.tableBzName);
						// 遍历模板赋值文件名
						$('#modelList').find('li').each(function() {
							var label = $(this).find("label");
							var modleType = label.attr('modeltype');
							if(modleType == 'Java' || modleType == 'xml') {
								label.html(json.bean.tableName + label.attr("thiscontent"));
							}else{
								label.html(json.bean.tableFirstISlowerName + label.attr("thiscontent"));
							}
						});
						transformResult();
						winui.window.msg('检索成功', {icon: 1,time: 2000});
					}else{
						winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
					}
				}});
			}else{
				winui.window.msg(data.returnMessage, {icon: 2,time: 2000});
			}
		}});
	});

	function transformResult() {
		$.each(list, function (i, row){
			var content = codeDocUtil.replaceModelContent(row.modelContent);
			var s = {
				modelId: row.id,
				content: content,
				tableName: $("#tableName").val(),
				groupId: parent.rowId,
				modelName: row.modelName,
				modelContent: row.modelContent,
				fileName: $("#tableZhName").val() + row.modelName,
				modelType: row.modelType,
			};
			insertListIn(list, s);
		});
	}
	
	/**
	 * 向集合中添加新元素
	 */
	function insertListIn(list, s){
		var isIn = false;
		for(var i = 0; i < list.length; i++){
			if(list[i].modelId == s.modelId){//存在则替换
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
