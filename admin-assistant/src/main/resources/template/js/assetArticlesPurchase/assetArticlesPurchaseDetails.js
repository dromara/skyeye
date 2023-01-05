
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		form = layui.form;

	AjaxPostUtil.request({url: flowableBasePath + "queryArticlesPurchaseById", params: {id: parent.rowId}, type: 'json', method: 'GET', callback: function (json) {
		$("#showForm").html(getDataUseHandlebars($("#useTemplate").html(), json));

		// 附件回显
		skyeyeEnclosure.showDetails({"enclosureUploadBtn": json.bean.enclosureInfo});
		matchingLanguage();
		form.render();
	}});
});