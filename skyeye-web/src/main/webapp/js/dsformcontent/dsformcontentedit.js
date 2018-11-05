
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).define(['table', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form', 'codemirror', 'xml', 'clike', 'css', 'htmlmixed', 'javascript', 'nginx',
	           'solr', 'sql', 'vue'], function (form) {
		var index = parent.layer.getFrameIndex(window.name); //获取窗口索引
	    var $ = layui.$,
	    form = layui.form;
	    var htmlEditor, jsEditor;
	    
	    showGrid({
		 	id: "showForm",
		 	url: reqBasePath + "dsform004",
		 	params: {rowId: parent.rowId},
		 	pagination: false,
		 	template: getFileContent('tpl/dsformcontent/dsformcontenteditTemplate.tpl'),
		 	ajaxSendLoadBefore: function(hdb){
		 	},
		 	ajaxSendAfter:function(json){
		 		
		 		htmlEditor = CodeMirror.fromTextArea(document.getElementById("htmlContent"), {
		            mode : "xml",  // 模式
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
			    
			    jsEditor = CodeMirror.fromTextArea(document.getElementById("jsContent"), {
		            mode : "text/javascript",  // 模式
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
		 		
		 		form.render();
		 		
		 		form.on('submit(formEditBean)', function (data) {
			    	//表单验证
			        if (winui.verifyForm(data.elem)) {
			        	if(isNull(htmlEditor.getValue())){
			        		top.winui.window.msg('请输入模板内容', {icon: 2,time: 2000});
			        	}else{
			        		var params = {
		        				contentName: $("#contentName").val(),
		        				htmlContent: encodeURI(htmlEditor.getValue()),
		        				htmlType: $("#htmlType").val(),
		        				jsContent: encodeURI(jsEditor.getValue()),
		        				jsType: $("#jsType").val(),
		        				rowId: parent.rowId
			        		};
				        	
				        	AjaxPostUtil.request({url:reqBasePath + "dsform005", params:params, type:'json', callback:function(json){
				 	   			if(json.returnCode == 0){
					 	   			parent.layer.close(index);
					 	        	parent.refreshCode = '0';
				 	   			}else{
				 	   				top.winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
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