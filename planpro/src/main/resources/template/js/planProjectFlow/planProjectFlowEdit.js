layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
	    	form = layui.form;
	    
		showGrid({
		 	id: "showForm",
		 	url: reqBasePath + "planprojectflow004",
		 	params: {rowId: parent.rowId},
		 	pagination: false,
			method: "GET",
		 	template: $("#beanTemplate").html(),
		 	ajaxSendLoadBefore: function(hdb){
		 		//是否共享
		 		hdb.registerHelper("compare2", function(v1, options){
					if(v1 == '2'){//公开分享
						return 'checked';
					}else if(v1 == '1'){//不分享
						return '';
					} else {
						return '';
					}
				});
		 		hdb.registerHelper("compare3", function(v1, options){
					if(v1 == '2'){//公开分享
						return 'true';
					}else if(v1 == '1'){//不分享
						return 'false';
					} else {
						return 'false';
					}
				});
		 	},
		 	ajaxSendAfter:function (json) {
		 		
		 		$("#projectName").html(parent.projectName);
		 		
		 		matchingLanguage();
		 		form.render();
		 		
		 		//是否共享
		 		form.on('switch(isShare)', function (data) {
		 			$(data.elem).val(data.elem.checked);
		 		});
		 		
		 		form.on('submit(formEditBean)', function (data) {
			        if (winui.verifyForm(data.elem)) {
			        	var params = {
		        			rowId: parent.rowId,
		        			title: $("#title").val(),
			        	};
			        	
			        	if(data.field.isShare){
			        		params.isShare = '2';
			        	} else {
			        		params.isShare = '1';
			        	}
			        	
			        	AjaxPostUtil.request({url: reqBasePath + "planprojectflow005", params: params, type: 'json', method: "PUT", callback: function (json) {
							parent.layer.close(index);
							parent.refreshCode = '0';
			 	   		}});
			        }
			        return false;
			    });
		 		
		 	}
	    });
		
	    //取消
	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	    
	});
});