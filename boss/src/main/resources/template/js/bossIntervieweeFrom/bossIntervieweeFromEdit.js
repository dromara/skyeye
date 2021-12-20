
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'textool'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
	    	textool = layui.textool;
	    
	    showGrid({
		 	id: "showForm",
		 	url: reqBasePath + "bossIntervieweeFrom004",
		 	params: {id: parent.rowId},
		 	pagination: false,
			method: "GET",
		 	template: $("#beanTemplate").html(),
		 	ajaxSendAfter:function(json){
		 		textool.init({
			    	eleId: 'desc',
			    	maxlength: 500,
			    	tools: ['count', 'copy', 'reset', 'clear']
			    });

			    matchingLanguage();
		 	    form.on('submit(formEditBean)', function (data) {
		 	        if (winui.verifyForm(data.elem)) {
		 	        	var params = {
		 	        		id: parent.rowId,
							title: $("#title").val(),
							fromUrl: $("#fromUrl").val(),
							desc: $("#desc").val()
		 	        	};

		 	        	AjaxPostUtil.request({url: reqBasePath + "bossIntervieweeFrom005", params: params, type: 'json', method: "PUT", callback: function(json){
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