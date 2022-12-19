
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

	showGrid({
		id: "showForm",
		url: flowableBasePath + "assetarticles013",
		params: {id: parent.rowId},
		pagination: false,
		method: 'GET',
		template: $("#beanTemplate").html(),
		ajaxSendLoadBefore: function(hdb) {
		},
		ajaxSendAfter:function (json) {
			// 附件回显
			skyeyeEnclosure.showDetails({"enclosureUploadBtn": json.bean.enclosureInfo});
			matchingLanguage();
			form.render();
		}
	});

});