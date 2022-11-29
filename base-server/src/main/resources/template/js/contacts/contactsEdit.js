
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
		 	url: reqBasePath + "queryContactsMationById",
		 	params: {id: parent.rowId},
		 	pagination: false,
			method: 'GET',
		 	template: $("#beanTemplate").html(),
		 	ajaxSendLoadBefore: function(hdb) {
		 	},
		 	ajaxSendAfter: function (json) {
		 		$("input:radio[name=isDefault][value=" + json.bean.isDefault + "]").attr("checked", true);
		 		
		 		matchingLanguage();
		 		form.render();
		 	    form.on('submit(formEditBean)', function (data) {
		 	        if (winui.verifyForm(data.elem)) {
		 	        	var params = {
	 	        			id: parent.rowId,
							objectId: parent.objectId,
							objectKey: parent.objectKey,
							name: $("#name").val(),
		 	        		contacts: $("#contacts").val(),
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
		 	}
		});
		
	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	});
});