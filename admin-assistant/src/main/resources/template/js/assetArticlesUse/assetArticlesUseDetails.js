
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'form'], function (exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
	var $ = layui.$,
		form = layui.form;

	AjaxPostUtil.request({url: flowableBasePath + "queryAssetArticlesApplyUseById", params: {id: parent.rowId}, type: 'json', method: 'GET', callback: function (json) {
		$("#showForm").html(getDataUseHandlebars($("#useTemplate").html(), json));

		// 附件回显
		skyeyeEnclosure.showDetails({"enclosureUploadBtn": json.bean.enclosureInfo});
		matchingLanguage();
		form.render();
	}});
});