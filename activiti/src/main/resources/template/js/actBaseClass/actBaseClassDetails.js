
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
	    
	    var beanTemplate = $('#beanTemplate').html();
	    showGrid({
		 	id: "showForm",
		 	url: flowableBasePath + "actbaseclass006",
		 	params: {rowId: parent.rowId},
		 	pagination: false,
		 	template: beanTemplate,
		 	ajaxSendAfter:function (json) {
		 		matchingLanguage();
		 	}
		});
	});
});