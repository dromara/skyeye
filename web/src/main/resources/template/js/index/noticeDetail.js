
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window',
}).define(['window', 'table', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
	    	form = layui.form;
		showGrid({
		 	id: "notice-content",
		 	url: reqBasePath + "syseveusernotice007",
		 	params: {rowId: parent.parentRowId},
		 	pagination: false,
		 	template: getFileContent('tpl/index/noticeDetailTemplate.tpl'),
		 	ajaxSendLoadBefore: function(hdb){
		 	},
		 	ajaxSendAfter:function (json) {
		 		matchingLanguage();
		 	}
	    });
		
	});
	    
});