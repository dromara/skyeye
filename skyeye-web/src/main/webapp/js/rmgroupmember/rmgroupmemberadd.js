
var childIcon = "";//分组ICON

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).define(['table', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name); //获取窗口索引
	    var $ = layui.$,
	    form = layui.form;
	    
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
			        	var params = {
		        			rmTypeId: $("#rmTypeId").val(),
		        			rmGroupName: $("#rmGroupName").val(),
		        			icon: $("#rmGroupIcon").val(),
			        	};
			        	
			        	AjaxPostUtil.request({url:reqBasePath + "rmxcx009", params:params, type:'json', callback:function(json){
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
	    	console.log(1);
	    });
	    
	    //取消
	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	    
	});
	    
});