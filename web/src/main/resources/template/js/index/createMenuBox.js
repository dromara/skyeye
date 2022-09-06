layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window',
}).define(['window', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
	    	form = layui.form;
	    
	    var deskTopId = parent.$("#desktop-sel").val();
	    
	    matchingLanguage();
	    form.on('submit(formAddBean)', function (data) {
	        if (winui.verifyForm(data.elem)) {
	        	var params = {
        			menuBoxName: $("#menuBoxName").val(),
        			deskTopId: deskTopId
	        	};
	        	
	        	AjaxPostUtil.request({url: reqBasePath + "sysevewindragdrop001", params: params, type: 'json', callback: function (json) {
					parent.childParams = json.bean;
					parent.layer.close(index);
					parent.refreshCode = '0';
	 	   		}});
	        }
	        return false;
	    });
	    
	    // 取消
	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	});
	    
});