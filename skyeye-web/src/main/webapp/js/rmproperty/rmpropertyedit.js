layui.config({
	base: basePath, 
	version: skyeyeVersion
}).define(['jquery', 'winui', 'codemirror', 'xml', 'clike', 'css', 'htmlmixed', 'javascript', 'nginx', 
           'solr', 'sql', 'vue'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name); //获取窗口索引
	    var $ = layui.$,
	    form = layui.form;
	    var htmlModelContent, jsModelContent, htmlContent, jsContent, jsRelyOnContent;
	    
	    AjaxPostUtil.request({url:reqBasePath + "exexplaintormproperty004", params:{}, type:'json', callback:function(json){
   			if(json.returnCode == 0){
   				layer.open({
   					type: 1,
   					closeBtn: 0,//关闭按钮
   					resize: false,//是否允许拉伸
   					offset: 'l', // 具体配置参考：offset参数项
   					content: json.bean.content,
   					area: ['200px', '400px'],
   					shade: 0, //不显示遮罩
   					title: json.bean.title
   				});
   			}else{
   				top.winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
   			}
   		}});
	    
		showGrid({
		 	id: "showForm",
		 	url: reqBasePath + "rmproperty004",
		 	params: {rowId: parent.rowId},
		 	pagination: false,
		 	template: getFileContent('tpl/rmproperty/rmpropertyeditTemplate.tpl'),
		 	ajaxSendLoadBefore: function(hdb){
		 	},
		 	ajaxSendAfter:function(json){
		 		
		 		htmlModelContent = CodeMirror.fromTextArea(document.getElementById("htmlModelContent"), {
		            mode : "xml",  // 模式
		            theme : "eclipse",  // CSS样式选择
		            indentUnit : 4,  // 缩进单位，默认2
		            smartIndent : true,  // 是否智能缩进
		            tabSize : 4,  // Tab缩进，默认4
		            readOnly : true,  // 是否只读，默认false
		            showCursorWhenSelecting : true,
		            lineNumbers : true,  // 是否显示行号
		            styleActiveLine: true, //line选择是是否加亮
		            matchBrackets: true,
		        });
		      	
		      	jsModelContent = CodeMirror.fromTextArea(document.getElementById("jsModelContent"), {
		            mode : "text/javascript",  // 模式
		            theme : "eclipse",  // CSS样式选择
		            indentUnit : 4,  // 缩进单位，默认2
		            smartIndent : true,  // 是否智能缩进
		            tabSize : 4,  // Tab缩进，默认4
		            readOnly : true,  // 是否只读，默认false
		            showCursorWhenSelecting : true,
		            lineNumbers : true,  // 是否显示行号
		            styleActiveLine: true, //line选择是是否加亮
		            matchBrackets: true,
		        });
		      	
		      	htmlContent = CodeMirror.fromTextArea(document.getElementById("htmlContent"), {
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
		      	
		      	jsContent = CodeMirror.fromTextArea(document.getElementById("jsContent"), {
		            mode : "text/javascript",  // 模式
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
		      	
		      	jsRelyOnContent = CodeMirror.fromTextArea(document.getElementById("jsRelyOnContent"), {
		            mode : "text/javascript",  // 模式
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
		      	
		      	$("input:radio[name=propertyOut][value=" + json.bean.propertyOut + "]").prop("checked", true);
		 		
		 		form.render();
		 		
		 		//展现形式
		      	showGrid({
		    	 	id: "dsFormContentId",
		    	 	url: reqBasePath + "dsform006",
		    	 	params: {},
		    	 	pagination: false,
		    	 	template: getFileContent('tpl/template/select-option.tpl'),
		    	 	ajaxSendLoadBefore: function(hdb){
		    	 	},
		    	 	ajaxSendAfter:function(data){
		    	 		$("#dsFormContentId").val(json.bean.dsFormContentId);
		    	 		form.render('select');
		    	 	}
		        });
              	
              	form.on('select(selectParent)', function(data){
              		AjaxPostUtil.request({url:reqBasePath + "dsform007", params:{rowId: data.value}, type:'json', callback:function(json){
            			if(json.returnCode == 0){
            				htmlModelContent.setValue(json.bean.htmlContent);
            				jsModelContent.setValue(json.bean.jsContent);
            				htmlContent.setValue(json.bean.htmlContent);
            				jsContent.setValue(json.bean.jsContent);
            			}else{
            				top.winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
            			}
            		}});
				});
		 		
		 		form.on('submit(formEditBean)', function (data) {
			    	//表单验证
			        if (winui.verifyForm(data.elem)) {
			        	var params = {
		        			title:$("#title").val(),
		        			propertyTag:$("#propertyTag").val(),
		        			propertyUnit:encodeURI($("#propertyUnit").val()),
		        			dsFormContentId:$("#dsFormContentId").val(),
		        			propertyOut:data.field.propertyOut,
		        			htmlContent:encodeURI(htmlContent.getValue().replace(/\+/g, "%2B").replace(/\&/g, "%26")),
		        			jsContent:encodeURI(jsContent.getValue().replace(/\+/g, "%2B").replace(/\&/g, "%26")),
		        			jsRelyOn:encodeURI(jsRelyOnContent.getValue().replace(/\+/g, "%2B").replace(/\&/g, "%26")),
		        			rowId:parent.rowId
			        	};
			        	
			        	AjaxPostUtil.request({url:reqBasePath + "rmproperty005", params:params, type:'json', callback:function(json){
			 	   			if(json.returnCode == 0){
				 	   			parent.layer.close(index);
				 	        	parent.refreshCode = '0';
			 	   			}else{
			 	   				top.winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
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