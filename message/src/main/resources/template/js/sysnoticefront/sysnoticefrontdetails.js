
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window',
}).define(['window', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$;
	    
	    showGrid({
		 	id: "showForm",
		 	url: sysMainMation.noticeBasePath + "notice014",
		 	params: {rowId:parent.rowId},
		 	pagination: false,
		 	template: getFileContent('tpl/sysnoticefront/sysnoticefrontdetailsTemplate.tpl'),
		 	ajaxSendAfter:function (json) {
		 		matchingLanguage();
		 	}
		});
	});
});