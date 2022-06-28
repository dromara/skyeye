
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$;
	    
	    showGrid({
		 	id: "showForm",
		 	url: flowableBasePath + "sealseservice036",
		 	params: {rowId: parent.rowId},
		 	pagination: false,
		 	template: $("#beanTemplate").html(),
		 	ajaxSendLoadBefore: function(hdb){
		 	},
		 	ajaxSendAfter: function (json) {
		 		
		 		showGrid({
				 	id: "evaluateType",
				 	url: flowableBasePath + "sealseserviceevaluatetype008",
				 	params: {},
				 	pagination: false,
				 	template: getFileContent('tpl/template/select-option.tpl'),
				 	ajaxSendLoadBefore: function(hdb){
				 	},
				 	ajaxSendAfter:function(data){
				 		form.render("select");
				 	}
				});
		 		
				matchingLanguage();
		 		form.render();
		 	    form.on('submit(evaluate)', function (data) {
		 	        if (winui.verifyForm(data.elem)) {
		 	        	var params = {
		        			rowId: parent.rowId,
		        			evaluateType: $("#evaluateType").val(),
		        			content: $("#content").val()
		 	        	};
	 	 	        	AjaxPostUtil.request({url: flowableBasePath + "sealseservice037", params: params, type: 'json', callback: function (json) {
	 		 	   			if (json.returnCode == 0){
	 			 	   			parent.layer.close(index);
	 			 	        	parent.refreshCode = '0';
	 		 	   			} else {
	 		 	   				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
	 		 	   			}
	 		 	   		}});
		 	        }
		 	        return false;
		 	    });
		 	}
		});
	    
	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	});
});