
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
		url: flowableBasePath + "dsformpage006",
		params: {rowId:parent.rowId},
		pagination: false,
		method: "GET",
		template: $("#showTemplate").html(),
		ajaxSendAfter:function (json) {

			matchingLanguage();
			form.render();
			form.on('submit(formEditBean)', function (data) {
				if (winui.verifyForm(data.elem)) {
					var params = {
						rowId: parent.rowId,
						pageName: $("#pageName").val(),
						pageDesc: $("#pageDesc").val()
					};
					AjaxPostUtil.request({url: flowableBasePath + "dsformpage007", params: params, type: 'json', method: "PUT", callback: function (json) {
						parent.layer.close(index);
						parent.refreshCode = '0';
					}});
				}
				return false;
			});
		}
	});

	$("body").on("click", "#cancle", function() {
		parent.layer.close(index);
	});
});