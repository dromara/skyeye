
var isPic = false;//是否执行生成图片

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).define(['table', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name); //获取窗口索引
	    var $ = layui.$,
	    form = layui.form;
	    
	    $("#download").hide();
	    
	    showGrid({
		 	id: "rmTypeId",
		 	url: reqBasePath + "common001",
		 	params: {},
		 	pagination: false,
		 	template: getFileContent('tpl/template/select-option.tpl'),
		 	ajaxSendLoadBefore: function(hdb){
		 	},
		 	ajaxSendAfter:function(json){
		 		//搜索表单
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
			    	//表单验证
			        if (winui.verifyForm(data.elem)) {
			        	if(isPic){
			        		var oCanvas = document.getElementById("thecanvas");
					    	var imgData = oCanvas.toDataURL();
					    	AjaxPostUtil.request({url:reqBasePath + "common004", params:{images:imgData, type:1}, type:'json', callback:function(json1){
				 	   			if(json1.returnCode == 0){
					 	   			var params = {
					 	   				rmTypeId: $("#rmTypeId").val(),
					 	   				rmGroupId: $("#rmGroupId").val(),
					 	   				htmlContent: encodeURI($("#htmlContent").val()),
					 	   				htmlJsContent: encodeURI($("#htmlJsContent").val()),
					 	   				wxmlContent: encodeURI($("#wxmlContent").val()),
					 	   				wxmlJsContent: encodeURI($("#wxmlJsContent").val()),
					        			img: json1.bean.picUrl
						        	};
						        	
						        	AjaxPostUtil.request({url:reqBasePath + "rmxcx016", params:params, type:'json', callback:function(json){
						 	   			if(json.returnCode == 0){
							 	   			parent.layer.close(index);
							 	        	parent.refreshCode = '0';
						 	   			}else{
						 	   				top.winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
						 	   			}
						 	   		}});
				 	   			}else{
				 	   				top.winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
				 	   			}
				 	   		}});
			        	}else{
			        		top.winui.window.msg("请先生成预览图", {icon: 2,time: 2000});
			        	}
			        }
			        return false;
			    });
		 	}
	    });
	    
	    //HTML内容变化事件
	    $("body").on("input", "#htmlContent", function(){
	    	$("#htmlJsContentScript").html('<script>layui.define(["jquery"], function(exports) {var jQuery = layui.jquery;(function($) {' + $("#htmlJsContent").val() + '})(jQuery);});</script>');
	    	$("#printPic").html($(this).val());
	    });
	    
	    //HTML-JS内容变化事件
	    $("body").on("change", "#htmlJsContent", function(){
	    	$("#htmlJsContentScript").html('<script>layui.define(["jquery"], function(exports) {var jQuery = layui.jquery;(function($) {' + $("#htmlJsContent").val() + '})(jQuery);});</script>');
	    	$("#htmlJsContent").val(do_js_beautify($(this).val()));
	    });
	    
	    //下载canvas图片
	    $("body").on("click", "#download", function(){
	    	var oCanvas = document.getElementById("thecanvas");
	    	var img_data1 = Canvas2Image.saveAsPNG(oCanvas, true).getAttribute('src');
	    	saveFile(img_data1, 'richer.png');
	    });
	    
	    //生成图片
	    $("body").on("click", "#createPic", function(){
	    	if(isNull($("#printPic").html().trim())){
	    		top.winui.window.msg('请填写HTML内容', {icon: 2,time: 2000});
	    	}else{
	    		html2canvas($("#printPic"), {
	    			onrendered: function(canvas) {
	    				//添加属性
	    				canvas.setAttribute('id','thecanvas');
	    				//读取属性值
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
	    
	    function getBlob(canvas) { //获取blob对象
		  	var data = canvas.toDataURL("image/jpeg", 1);
		  	data = data.split(',')[1];
		  	data = window.atob(data);
		  	var ia = new Uint8Array(data.length);
		  	for (var i = 0; i < data.length; i++) {
		  		ia[i] = data.charCodeAt(i);
		  	}
		  	return new Blob([ia], {
		  		type: "image/jpeg"
		  	});
	    }
	    
	    //取消
	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	    
	});
	    
});