
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window','jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$;
	    
	    showGrid({
		 	id: "showForm",
		 	url: reqBasePath + "diary014",
		 	params: {rowId:parent.rowId},
		 	pagination: false,
		 	template: getFileContent('tpl/jobdiaryMyReceive/jobdiaryMyReceiveMonthDetailsTemplate.tpl'),
		 	ajaxSendAfter:function (json) {
				// 附件回显
				skyeyeEnclosure.showDetails({"enclosureUploadBtn": json.bean.enclosureInfo});
               	matchingLanguage();
            }
		});

	});
});