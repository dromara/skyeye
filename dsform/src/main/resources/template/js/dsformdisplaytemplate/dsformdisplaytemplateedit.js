layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form', 'codemirror', 'xml', 'clike', 'css', 'htmlmixed', 'javascript', 'nginx', 'solr', 'sql', 'vue'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
	    	form = layui.form;
	    
		showGrid({
		 	id: "showForm",
		 	url: reqBasePath + "dsformdisplaytemplate004",
		 	params: {rowId: parent.rowId},
		 	pagination: false,
		 	template: getFileContent('tpl/dsformdisplaytemplate/dsformdisplaytemplateeditTemplate.tpl'),
		 	ajaxSendLoadBefore: function(hdb){
		 	},
		 	ajaxSendAfter:function(json){
		 		
		 		var templateContent = CodeMirror.fromTextArea(document.getElementById("templateContent"), {
		            mode : "xml",  // 模式
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
		 		
		        matchingLanguage();
		 		form.render();
		 		//模板规范说明
		 		AjaxPostUtil.request({url:reqBasePath + "exexplain004", params:{type: 3}, type:'json', callback:function(j){
		   			if(j.returnCode == 0){
		   				$("#exexplaintodsformdisplaytemplateTitle").html(j.bean.title);
		   				$("#exexplaintodsformdisplaytemplateContent").html(j.bean.content);
		   			}else{
		   				winui.window.msg(j.returnMessage, {icon: 2,time: 2000});
		   			}
		   		}});
		 		
		 		form.on('submit(formEditBean)', function (data) {
			        if (winui.verifyForm(data.elem)) {
			        	var params = {
		        			templateName:$("#templateName").val(),
		        			templateContent:encodeURI(templateContent.getValue().replace(/\+/g, "%2B").replace(/\&/g, "%26")),
		        			rowId:parent.rowId
			        	};
			        	AjaxPostUtil.request({url:reqBasePath + "dsformdisplaytemplate005", params:params, type:'json', callback:function(json){
			 	   			if(json.returnCode == 0){
				 	   			parent.layer.close(index);
				 	        	parent.refreshCode = '0';
			 	   			}else{
			 	   				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
			 	   			}
			 	   		}});
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