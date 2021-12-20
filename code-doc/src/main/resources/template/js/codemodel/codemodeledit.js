
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window',
}).define(['window', 'table', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form', 'codemirror', 'xml', 'clike', 'css', 'htmlmixed', 'javascript', 'nginx',
	           'solr', 'sql', 'vue'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
	    	form = layui.form;
	    var editor;
	    
	    showGrid({
		 	id: "showForm",
		 	url: reqBasePath + "codemodel009",
		 	params: {rowId: parent.rowId},
		 	pagination: false,
		 	template: getFileContent('tpl/codemodel/codemodeleditTemplate.tpl'),
		 	ajaxSendLoadBefore: function(hdb){
		 	},
		 	ajaxSendAfter:function(json){
		 		AjaxPostUtil.request({url:reqBasePath + "exexplain004", params: {type: 1}, type:'json', callback:function(j){
					if(j.returnCode == 0){
		   				$("#exexplaintocodemodelTitle").html(j.bean.title);
		   				$("#exexplaintocodemodelContent").html(j.bean.content);
		   			}else{
		   				winui.window.msg(j.returnMessage, {icon: 2,time: 2000});
		   			}
		   		}});
		 		
		 		editor = CodeMirror.fromTextArea(document.getElementById("modelContent"), {
		 			mode : returnModel(json.bean.modelType),  // 模式
		            theme : "eclipse",  // CSS样式选择
		            indentUnit : 4,  // 缩进单位，默认2
		            smartIndent : true,  // 是否智能缩进
		            tabSize : 4,  // Tab缩进，默认4
		            readOnly : false,  // 是否只读，默认false
		            showCursorWhenSelecting : true,
		            lineNumbers : true,  // 是否显示行号
		            styleActiveLine: true, //line选择是是否加亮
		            matchBrackets: true,
		        });
		 		
		 		$("#modelType").val(json.bean.modelType);
		 		
		 		matchingLanguage();
		 		form.render();
		 		form.on('select(selectParent)', function(data){
					var mode = returnModel($("#modelType").val());
					if (!isNull(mode.length)) {
						editor.setOption('mode', mode)
					} 
		 		});
		 		
		 		form.on('submit(formEditBean)', function (data) {
			    	
			        if (winui.verifyForm(data.elem)) {
			        	if(isNull(editor.getValue())){
			        		winui.window.msg('请输入模板内容', {icon: 2,time: 2000});
			        	}else{
				        	var params = {
			        			modelName: $("#modelName").val(),
			        			modelContent: encodeURIComponent(editor.getValue()),
			        			modelText: encodeURIComponent(editor.getValue()),
			        			modelType: $("#modelType").val(),
			        			groupId: parent.groupId,
			        			rowId: parent.rowId
				        	};
				        	
				        	AjaxPostUtil.request({url:reqBasePath + "codemodel010", params:params, type:'json', callback:function(json){
				 	   			if(json.returnCode == 0){
					 	   			parent.layer.close(index);
					 	        	parent.refreshCode = '0';
				 	   			}else{
				 	   				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
				 	   			}
				 	   		}});
			        	}
			        }
			        return false;
			    });
		 		
		 	}
	    });
	    
	    //取消
	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	    
	});
	    
});