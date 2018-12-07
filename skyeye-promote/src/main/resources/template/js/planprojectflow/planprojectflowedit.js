layui.config({
	base: basePath, 
	version: skyeyeVersion
}).define(['jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name); //获取窗口索引
	    var $ = layui.$,
	    form = layui.form;
	    
		showGrid({
		 	id: "showForm",
		 	url: reqBasePath + "planprojectflow004",
		 	params: {rowId: parent.rowId},
		 	pagination: false,
		 	template: getFileContent('tpl/planprojectflow/planprojectfloweditTemplate.tpl'),
		 	ajaxSendLoadBefore: function(hdb){
		 		//是否共享
		 		hdb.registerHelper("compare2", function(v1, options){
					if(v1 == '2'){//公开分享
						return 'checked';
					}else if(v1 == '1'){//不分享
						return '';
					}else{
						return '';
					}
				});
		 		hdb.registerHelper("compare3", function(v1, options){
					if(v1 == '2'){//公开分享
						return 'true';
					}else if(v1 == '1'){//不分享
						return 'false';
					}else{
						return 'false';
					}
				});
		 	},
		 	ajaxSendAfter:function(json){
		 		
		 		$("#projectName").html(parent.projectName);
		 		
		 		form.render();
		 		
		 		//是否共享
		 		form.on('switch(isShare)', function (data) {
		 			//同步开关值
		 			$(data.elem).val(data.elem.checked);
		 		});
		 		
		 		form.on('submit(formEditBean)', function (data) {
			    	//表单验证
			        if (winui.verifyForm(data.elem)) {
			        	var params = {
		        			rowId: parent.rowId,
		        			title: $("#title").val(),
			        	};
			        	
			        	if(data.field.isShare){
			        		params.isShare = '2';
			        	}else{
			        		params.isShare = '1';
			        	}
			        	
			        	AjaxPostUtil.request({url:reqBasePath + "planprojectflow005", params:params, type:'json', callback:function(json){
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