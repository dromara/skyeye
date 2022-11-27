
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
 	    form.on('submit(formAddBean)', function (data) {
 	        if (winui.verifyForm(data.elem)) {
 	        	var params = {
					objectId: parent.objectId,
					objectKey: parent.objectKey,
 	        		name: $("#name").val(),
 	        		department: $("#department").val(),
 	        		job: $("#job").val(),
 	        		workPhone: $("#workPhone").val(),
 	        		mobilePhone: $("#mobilePhone").val(),
 	        		email: $("#email").val(),
 	        		qq: $("#qq").val(),
 	        		wechat: $("#wechat").val(),
 	        		isDefault: $("input[name='isDefault']:checked").val()
 	        	};
 	        	AjaxPostUtil.request({url: reqBasePath + "writeContactsMation", params: params, type: 'json', method: 'POST', callback: function (json) {
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