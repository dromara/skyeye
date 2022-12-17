
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'form', 'eleTree'], function (exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
	var $ = layui.$,
		form = layui.form;

	showGrid({
		id: "showForm",
		url: flowableBasePath + "queryActFlowMationById",
		params: {id: parent.rowId},
		method: 'GET',
		pagination: false,
		template: $("#beanTemplate").html(),
		ajaxSendAfter:function (json) {

			treeSelectUtil.init({
				eleTree: layui.eleTree,
				elem: 'serviceClassName',
				url: reqBasePath + "queryServiceClassForTree",
				defaultId: json.bean.serviceClassName
			});

			matchingLanguage();
			form.render();
			form.on('submit(formEditBean)', function (data) {
				if (winui.verifyForm(data.elem)) {
					var params = {
						id: parent.rowId,
						flowName: $("#flowName").val(),
						modelKey: $("#modelKey").val(),
						serviceClassName: $("#serviceClassName").attr("serviceClassName")
					};

					AjaxPostUtil.request({url: flowableBasePath + "writeActFlowMation", params: params, type: 'json', method: 'POST', callback: function (json) {
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