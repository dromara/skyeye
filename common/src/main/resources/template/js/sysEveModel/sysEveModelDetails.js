
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$;
	    var beanTemplate = $("#beanTemplate").html();
	    
	    showGrid({
		 	id: "showForm",
		 	url: reqBasePath + "sysevemodel006",
		 	params: {id: parent.rowId},
		 	pagination: false,
		 	template: beanTemplate,
			method: "GET",
			ajaxSendLoadBefore: function(hdb, json){
				json.bean.logo = fileBasePath + json.bean.logo;
			},
		 	ajaxSendAfter:function (json) {
		 		matchingLanguage();
		 		form.render();
		 	}
		});

		$("body").on("click", ".photo-img", function() {
			var url = $(this).attr("src");
			systemCommonUtil.showPicImg(url);
		});
		
	});
});