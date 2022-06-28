
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form', 'tagEditor'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$;
	    
	    showGrid({
		 	id: "showForm",
		 	url: reqBasePath + "emailsendmodel003",
		 	params: {id: parent.rowId},
		 	pagination: false,
			method: "GET",
		 	template: $("#showTemplate").html(),
		 	ajaxSendLoadBefore: function(hdb, json){
		 		if(!isNull(json.bean.toPeople)){
					json.bean.toPeople = json.bean.toPeople.split(',');
				}
				if(!isNull(json.bean.toCc)){
					json.bean.toCc = json.bean.toCc.split(',');
				}
				if(!isNull(json.bean.toBcc)){
					json.bean.toBcc = json.bean.toBcc.split(',');
				}

		 	},
		 	ajaxSendAfter: function (json) {
		 		matchingLanguage();
		 		form.render();
		 	}
		});

	});
});