
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
        readOnly : false,  // 是否只读，默认false
        showCursorWhenSelecting : true,
        lineNumbers : true,  // 是否显示行号
        styleActiveLine: true, //line选择是是否加亮
        matchBrackets: true,
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
					editor.setOption('mode', mode)
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
		 		
	 		},
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
	 	   				$("#tableZhName").val(json.bean.tableName);
	 	   				$("#tableFirstISlowerName").val(json.bean.tableFirstISlowerName);
	 	   				$("#ControllerPackageName").val(json.bean.ControllerPackageName);
	 	   				$("#ServicePackageName").val(json.bean.ServicePackageName);
    	 	   			$("#ServiceImplPackageName").val(json.bean.ServiceImplPackageName);
    	 	   			$("#DaoPackageName").val(json.bean.DaoPackageName);
    	 	   			//遍历模板
						$('#modelList').find('li').each(function() {
							var label = $(this).find("label");
							$("#" + label.attr("relation")).val(json.bean.tableName + label.attr("thiscontent"));
						});

	 	   			}else{
	 	   				top.winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
	 	   			}
	 	   		}});
    	 	}
    	});
	});
	
    exports('usemodelgroup', {});
});
