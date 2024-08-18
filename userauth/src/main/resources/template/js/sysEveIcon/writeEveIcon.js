
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form'], function (exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
	var $ = layui.$,
		form = layui.form;
	var id = GetUrlParam("id");

	if (!isNull(id)) {
		AjaxPostUtil.request({url: reqBasePath + "querySysIconById", params: {id: id}, type: 'json', method: 'GET', callback: function (json) {
			$("#iconClass").val(json.bean.iconClass);
			$("#iconyl").attr("class", "fa fa-fw " + $("#iconClass").val());
		}});
	}

	matchingLanguage();
	form.render();
	form.on('submit(formWriteBean)', function (data) {
		if (winui.verifyForm(data.elem)) {
			var params = {
				id: isNull(id) ? '' : id,
				iconClass: $("#iconClass").val(),
			};
			AjaxPostUtil.request({url: reqBasePath + "writeSysIcon", params: params, type: 'json', method: 'POST', callback: function (json) {
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

	//预览
	$("body").on("click", "#yl", function() {
		$("#iconyl").attr("class", "fa fa-fw " + $("#iconClass").val());
	});
});