
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
		 	url: flowableBasePath + "checkworkreason006",
		 	params: {rowId: parent.rowId},
		 	pagination: false,
			method: "GET",
		 	template: $("#beanTemplate").html(),
		 	ajaxSendAfter:function(json){
		 		matchingLanguage();
		 	    form.on('submit(formEditBean)', function (data) {
		 	        if (winui.verifyForm(data.elem)) {
		 	        	var params = {
		 	        		rowId: parent.rowId,
		 	        		appealName: $("#appealName").val(),
		 	        	};
		 	        	AjaxPostUtil.request({url: flowableBasePath + "checkworkreason007", params: params, type: 'json', method: "PUT", callback: function(json){
		 	        		if(json.returnCode == 0){
		 	        			parent.layer.close(index);
		 	        			parent.refreshCode = '0';
		 	        		}else{
		 	        			winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
		 	        		}
		 	        	}});
		 	        }
		 	        return false;
		 	    });
		 	}
		});
	    
	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	});
});