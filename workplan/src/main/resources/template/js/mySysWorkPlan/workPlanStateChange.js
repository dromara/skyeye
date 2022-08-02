
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window',
}).define(['window', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
	var $ = layui.$,
		form = layui.form;

	matchingLanguage();
	form.render();
	form.on('submit(formSubBean)', function (data) {
		if (winui.verifyForm(data.elem)) {
			var params = {
				rowId: parent.rowId,
				state: $("#state").val(),
				remark: $("#remark").val()
			};
			AjaxPostUtil.request({url: sysMainMation.workplanBasePath + "sysworkplan014", params: params, type: 'json', callback: function (json) {
				parent.layer.close(index);
				parent.refreshCode = '0';
			}});
		}
		return false;
	});

	// 取消
	$("body").on("click", "#cancle", function() {
		parent.layer.close(index);
	});
});