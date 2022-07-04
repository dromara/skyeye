
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
	    
	    matchingLanguage();
 		form.render();
 	    form.on('submit(formAddBean)', function(data) {
 	        if (winui.verifyForm(data.elem)) {
 	        	var params = {
 	        		coverageName: $("#coverageName").val()
 	        	};
 	        	AjaxPostUtil.request({url: flowableBasePath + "coverage002", params: params, type: 'json', callback: function (json) {
					parent.layer.close(index);
					parent.refreshCode = '0';
	 	   		}});
 	        }
 	        return false;
 	    });
 	    
	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	});
});