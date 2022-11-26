
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
		url: sysMainMation.wagesBasePath + "wages003",
		params: {id: parent.rowId},
		pagination: false,
		method: "GET",
		template: $("#showBaseTemplate").html(),
		ajaxSendLoadBefore: function(hdb) {
		},
		ajaxSendAfter:function (json) {
			skyeyeClassEnumUtil.showEnumDataListByClassName("commonEnable", 'radio', "enabled", json.bean.enabled, form);
			$("input:radio[name=monthlyClearing][value=" + json.bean.monthlyClearing + "]").attr("checked", true);
			$("input:radio[name=wagesType][value=" + json.bean.wagesType + "]").attr("checked", true);
			matchingLanguage();
			form.render();
			form.on('submit(formEditBean)', function (data) {
				if (winui.verifyForm(data.elem)) {
					var params = {
						name: $("#name").val(),
						key: $("#key").html(),
						enabled: $("#enabled input:radio:checked").val(),
						wagesType: $("input[name='wagesType']:checked").val(),
						monthlyClearing: $("input[name='monthlyClearing']:checked").val(),
						id: parent.rowId
					};
					AjaxPostUtil.request({url: sysMainMation.wagesBasePath + "writeWagesFieldTypeMation", params: params, type: 'json', method: "POST", callback: function (json) {
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