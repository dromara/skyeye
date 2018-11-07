
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).define(['table', 'jquery', 'winui', 'fileUpload'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name); //获取窗口索引
	    var $ = layui.$,
	    form = layui.form;
	    
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
		 		
		 		//搜索表单
		 		form.render();
		 		
			    form.on('submit(formEditBean)', function (data) {
			    	//表单验证
			        if (winui.verifyForm(data.elem)) {
			        	
		 	   			var params = {
		 	   				htmlContent: encodeURI($("#htmlContent").val()),
		 	   				htmlJsContent: encodeURI($("#htmlJsContent").val()),
		 	   				wxmlContent: encodeURI($("#wxmlContent").val()),
		 	   				wxmlJsContent: encodeURI($("#wxmlJsContent").val()),
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
//	    	$("#htmlJsContent").val(do_js_beautify($(this).val()));
	    });
	    
	    //取消
	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	    
	});
	    
});