
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).define(['table', 'jquery', 'winui', 'fileUpload'], function (exports) {
	winui.renderColor();
	layui.use(['form', 'codemirror', 'xml', 'clike', 'css', 'htmlmixed', 'javascript', 'nginx',
	           'solr', 'sql', 'vue'], function (form) {
		var index = parent.layer.getFrameIndex(window.name); //获取窗口索引
	    var $ = layui.$,
	    form = layui.form;
	    var htmlContent, htmlJsContent, wxmlContent, wxmlJsDataContent, wxmlJsMethodContent, wxmlJsMethodCreateContent;
	    
	    showGrid({
		 	id: "showForm",
		 	url: reqBasePath + "rmxcx020",
		 	params: {rowId: parent.rowId},
		 	pagination: false,
		 	template: getFileContent('tpl/rmgroupmember/rmgroupmembereditTemplate.tpl'),
		 	ajaxSendLoadBefore: function(hdb){
		 	},
		 	ajaxSendAfter:function(json){
		 		//初始化上传
		 		$("#printsPicUrl").upload({
		            "action": reqBasePath + "common003",
		            "data-num": "1",
		            "data-type": "PNG,JPG,jpeg,gif",
		            "uploadType": 1,
		            "data-value": json.bean.printsPicUrl,
		            //该函数为点击放大镜的回调函数，如没有该函数，则不显示放大镜
		            "function": function (_this, data) {
		                show("#printsPicUrl", data);
		            }
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
		 		
		 		htmlJsContent = CodeMirror.fromTextArea(document.getElementById("htmlJsContent"), {
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
		 		
		 		wxmlContent = CodeMirror.fromTextArea(document.getElementById("wxmlContent"), {
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
		 		
		 		wxmlJsDataContent = CodeMirror.fromTextArea(document.getElementById("wxmlJsDataContent"), {
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
		 		
		 		wxmlJsMethodContent = CodeMirror.fromTextArea(document.getElementById("wxmlJsMethodContent"), {
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
		 		
		 		wxmlJsMethodCreateContent = CodeMirror.fromTextArea(document.getElementById("wxmlJsMethodCreateContent"), {
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
		 		
		 		//初始化效果图
		 		$("#printPic").html(htmlContent.getValue());
		    	$("#htmlJsContentScript").html('<script>layui.define(["jquery"], function(exports) {var jQuery = layui.jquery;(function($) {' + htmlJsContent.getValue() + '})(jQuery);});</script>');
		 		
		 		//搜索表单
		 		form.render();
		 		
			    form.on('submit(formEditBean)', function (data) {
			    	//表单验证
			        if (winui.verifyForm(data.elem)) {
			        	if(isNull(htmlContent.getValue())){
		        			top.winui.window.msg("请填写HTML内容", {icon: 2,time: 2000});
		        		}else if(isNull(wxmlContent.getValue())){
		        			top.winui.window.msg("请填写WXML内容", {icon: 2,time: 2000});
		        		}else{
			 	   			var params = {
			 	   				htmlContent: encodeURI(htmlContent.getValue().replace(/\+/g, "%2B").replace(/\&/g, "%26")),
			 	   				htmlJsContent: encodeURI(htmlJsContent.getValue().replace(/\+/g, "%2B").replace(/\&/g, "%26")),
			 	   				wxmlContent: encodeURI(wxmlContent.getValue().replace(/\+/g, "%2B").replace(/\&/g, "%26")),
				 	   			wxmlJsDataContent: encodeURI(wxmlJsDataContent.getValue().replace(/\+/g, "%2B").replace(/\&/g, "%26")),
								wxmlJsMethodContent: encodeURI(wxmlJsMethodContent.getValue().replace(/\+/g, "%2B").replace(/\&/g, "%26")),
								wxmlJsMethodCreateContent: encodeURI(wxmlJsMethodCreateContent.getValue().replace(/\+/g, "%2B").replace(/\&/g, "%26")),
			        			rowId: parent.rowId,
				        	};
			 	   			
			 	   			params.img = $("#printsPicUrl").find("input[type='hidden'][name='upload']").attr("oldurl");
			 	   			
				        	AjaxPostUtil.request({url:reqBasePath + "rmxcx021", params:params, type:'json', callback:function(json){
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
	    
	    //HTML内容变化事件
	    htmlContent.on("change",function(){
	    	$("#printPic").html(htmlContent.getValue());
	    	$("#htmlJsContentScript").html('<script>layui.define(["jquery"], function(exports) {var jQuery = layui.jquery;(function($) {' + htmlJsContent.getValue() + '})(jQuery);});</script>');
		});
	    
	    //HTML-JS内容变化事件
	    htmlJsContent.on("change",function(){
	    	$("#printPic").html(htmlContent.getValue());
	    	$("#htmlJsContentScript").html('<script>layui.define(["jquery"], function(exports) {var jQuery = layui.jquery;(function($) {' + htmlJsContent.getValue() + '})(jQuery);});</script>');
		});
	    
	    //取消
	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	    
	});
	    
});