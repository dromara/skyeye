
layui.config({
	base: basePath,
	version: skyeyeVersion
}).extend({
	window: 'js/winui.window'
}).define(['window', 'jquery', 'winui'], function(exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
	var $ = layui.$;
	var beanTemplate = $("#beanTemplate").html();
	
	AjaxPostUtil.request({url: flowableBasePath + "procostexpense003", params:{rowId: parent.rowId}, type: 'json', callback: function (json) {
		// 附件回显
		skyeyeEnclosure.showDetails({"enclosureUploadBox": json.bean.enclosureInfo});

		var _html = getDataUseHandlebars(beanTemplate, json);
		$("#showForm").html(_html);
		matchingLanguage();
    }});

});