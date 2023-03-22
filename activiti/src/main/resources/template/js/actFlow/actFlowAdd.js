
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

	treeSelectUtil.init({
		eleTree: layui.eleTree,
		elem: 'applyServiceClassName',
		url: reqBasePath + "queryServiceClassForTree"
	});

	matchingLanguage();
	form.render();
	form.on('submit(formAddBean)', function (data) {
		if (winui.verifyForm(data.elem)) {
			var params = {
				flowName: $("#flowName").val(),
				modelKey: $("#modelKey").val(),
				applyServiceClassName: $("#serviceClassName").attr("applyServiceClassName")
			};

			AjaxPostUtil.request({url: flowableBasePath + "writeActFlowMation", params: params, type: 'json', method: 'POST', callback: function (json) {
				parent.layer.close(index);
				parent.refreshCode = '0';
			}});
		}
		return false;
	});

	$("body").on("click", "#cancle", function() {
		parent.layer.close(index);
	});
});