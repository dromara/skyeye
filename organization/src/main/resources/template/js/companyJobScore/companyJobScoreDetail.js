
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
		url: reqBasePath + "companyjobscore003",
		params: {id: parent.rowId},
		pagination: false,
		method: "GET",
		template: $("#showBaseTemplate").html(),
		ajaxSendLoadBefore: function(hdb, json) {
			json.bean.enabled = skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("commonEnable", 'id', json.bean.enabled, 'name');
		},
		ajaxSendAfter:function (json) {
			matchingLanguage();
			form.render();
		}
	});

	$("body").on("click", "#cancle", function() {
		parent.layer.close(index);
	});
});