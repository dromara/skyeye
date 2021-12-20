
var isPic = false;//是否执行生成图片

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'swiper'], function (exports) {
	winui.renderColor();
	layui.use(['form', 'codemirror', 'xml', 'clike', 'css', 'htmlmixed', 'javascript', 'nginx',
	           'solr', 'sql', 'vue'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
	    	form = layui.form;
	    
	    $("#download").hide();
	    
	    var htmlContent = CodeMirror.fromTextArea(document.getElementById("htmlContent"), {
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
 		
 		var htmlJsContent = CodeMirror.fromTextArea(document.getElementById("htmlJsContent"), {
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
 		
 		var wxmlContent = CodeMirror.fromTextArea(document.getElementById("wxmlContent"), {
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
 		
 		var wxmlJsDataContent = CodeMirror.fromTextArea(document.getElementById("wxmlJsDataContent"), {
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
 		
 		var wxmlJsMethodContent = CodeMirror.fromTextArea(document.getElementById("wxmlJsMethodContent"), {
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
 		
 		var wxmlJsMethodCreateContent = CodeMirror.fromTextArea(document.getElementById("wxmlJsMethodCreateContent"), {
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
	    
	    showGrid({
		 	id: "rmTypeId",
		 	url: reqBasePath + "common001",
		 	params: {},
		 	pagination: false,
		 	template: getFileContent('tpl/template/select-option.tpl'),
		 	ajaxSendLoadBefore: function(hdb){
		 	},
		 	ajaxSendAfter:function(json){
		 		matchingLanguage();
		 		form.render();
		 		//小程序分类变化事件
		 		form.on('select(selectParent)', function(data){
		 			showGrid({
		 	    	 	id: "rmGroupId",
		 	    	 	url: reqBasePath + "common002",
		 	    	 	params: {parentId: data.value},
		 	    	 	pagination: false,
		 	    	 	template: getFileContent('tpl/template/select-option.tpl'),
		 	    	 	ajaxSendLoadBefore: function(hdb){
		 	    	 	},
		 	    	 	ajaxSendAfter:function(json){
		 	    	 		form.render('select');
		 	    	 	}
		 	        });
		 		});
		 		
			    form.on('submit(formAddBean)', function (data) {
			        if (winui.verifyForm(data.elem)) {
			        	if(!isPic){
			        		winui.window.msg("请先生成预览图", {icon: 2,time: 2000});
			        	}else if(isNull(htmlContent.getValue())){
		        			winui.window.msg("请填写HTML内容", {icon: 2,time: 2000});
		        		}else if(isNull(wxmlContent.getValue())){
		        			winui.window.msg("请填写WXML内容", {icon: 2,time: 2000});
		        		}else{
		        			var oCanvas = document.getElementById("thecanvas");
		        			var imgData = oCanvas.toDataURL();
		        			AjaxPostUtil.request({url:reqBasePath + "common004", params:{images:imgData, type:1}, type:'json', callback:function(json1){
		        				if(json1.returnCode == 0){
		        					var params = {
	        							rmTypeId: $("#rmTypeId").val(),
	        							rmGroupId: $("#rmGroupId").val(),
	        							htmlContent: encodeURI(htmlContent.getValue().replace(/\+/g, "%2B").replace(/\&/g, "%26")),
	        							htmlJsContent: encodeURI(htmlJsContent.getValue().replace(/\+/g, "%2B").replace(/\&/g, "%26")),
	        							wxmlContent: encodeURI(wxmlContent.getValue().replace(/\+/g, "%2B").replace(/\&/g, "%26")),
	        							wxmlJsDataContent: encodeURI(wxmlJsDataContent.getValue().replace(/\+/g, "%2B").replace(/\&/g, "%26")),
	        							wxmlJsMethodContent: encodeURI(wxmlJsMethodContent.getValue().replace(/\+/g, "%2B").replace(/\&/g, "%26")),
	        							wxmlJsMethodCreateContent: encodeURI(wxmlJsMethodCreateContent.getValue().replace(/\+/g, "%2B").replace(/\&/g, "%26")),
	        							img: json1.bean.picUrl
		        					};
		        					
		        					AjaxPostUtil.request({url:reqBasePath + "rmxcx016", params:params, type:'json', callback:function(json){
		        						if(json.returnCode == 0){
		        							parent.layer.close(index);
		        							parent.refreshCode = '0';
		        						}else{
		        							winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
		        						}
		        					}});
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
	    
	    // 下载canvas图片
	    $("body").on("click", "#download", function(){
	    	var oCanvas = document.getElementById("thecanvas");
	    	var img_data1 = Canvas2Image.saveAsPNG(oCanvas, true).getAttribute('src');
	    	saveFile(img_data1, 'richer.png');
	    });
	    
	    // 生成图片
	    $("body").on("click", "#createPic", function(){
	    	if(isNull($("#printPic").html().trim())){
	    		winui.window.msg('请填写HTML内容', {icon: 2,time: 2000});
	    	}else{
	    		html2canvas($("#printPic"), {
	    			onrendered: function(canvas) {
	    				// 添加属性
	    				canvas.setAttribute('id','thecanvas');
	    				// 读取属性值
	    				document.getElementById('images').innerHTML = '';
	    				document.getElementById('images').appendChild(canvas);
	    				$("#download").show();
	    			}
	    		});
	    		isPic = true;
	    	}
	    });
	    
	    // 保存文件函数
	    var saveFile = function(data, filename){
	    	var save_link = document.createElementNS('http://www.w3.org/1999/xhtml', 'a');
	    	save_link.href = data;
	        save_link.download = filename;
	        var event = document.createEvent('MouseEvents');
	        event.initMouseEvent('click', true, false, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);
	        save_link.dispatchEvent(event);
	    };
	    
	    // 取消
	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	    
	});
});