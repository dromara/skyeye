
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'textool', 'form'], function (exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
	var $ = layui.$,
		form = layui.form,
		textool = layui.textool;

	AjaxPostUtil.request({url: reqBasePath + "querySysRoleById", params: {id: parent.rowId}, type: 'json', method: "GET", callback: function (json) {
		$("#name").val(json.bean.name);
		$("#remark").val(json.bean.remark);
		textool.init({eleId: 'roleDesc', maxlength: 250});
	}});

	matchingLanguage();
	form.render();
	form.on('submit(formEditBean)', function (data) {
		if (winui.verifyForm(data.elem)) {
			var params = {
				name: $("#name").val(),
				remark: $("#remark").val(),
				id: parent.rowId
			};

			AjaxPostUtil.request({url: reqBasePath + "writeSysRole", params: params, type: 'json', method: "POST", callback: function (json) {
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