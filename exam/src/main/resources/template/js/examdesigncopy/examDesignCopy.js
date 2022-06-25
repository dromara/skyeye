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
	    
	    $("#surveyName").val("复制 - " + parent.surveyName);
	    
	    matchingLanguage();
		form.render();
	    form.on('submit(formAddBean)', function (data) {
	        if (winui.verifyForm(data.elem)) {
	        	var params = {
        			surveyName: $("#surveyName").val(),
        			rowId: parent.rowId
	        	};
	        	AjaxPostUtil.request({url:schoolBasePath + "exam027", params:params, type: 'json', callback: function(json){
	 	   			if (json.returnCode == 0) {
		 	   			parent.layer.close(index);
		 	        	parent.refreshCode = '0';
	 	   			} else {
	 	   				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
	 	   			}
	 	   		}});
	        }
	        return false;
	    });
	    
	    //取消
	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	});
});