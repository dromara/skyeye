
layui.config({
	base: basePath,
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'form', 'textool'], function (exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
	var $ = layui.$,
		form = layui.form,
		textool = layui.textool;

	var className = GetUrlParam('className');
	var attrKey = GetUrlParam('attrKey');

	AjaxPostUtil.request({url: reqBasePath + "queryAttrDefinitionCustom", params: {className: className, attrKey: attrKey}, type: 'json', method: "GET", callback: function (json) {
		$("#showForm").html(getDataUseHandlebars($("#beanTemplate").html(), json));
		textool.init({eleId: 'remark', maxlength: 200});

		var id = isNull(json.bean.id) ? '' : json.bean.id;
		matchingLanguage();
		form.render();
		form.on('submit(formEditBean)', function (data) {
			if (winui.verifyForm(data.elem)) {
				var params = {
					className: className,
					attrKey: attrKey,
					name: $("#name").val(),
					remark: $("#remark").val(),
					id: id
				};
				AjaxPostUtil.request({url: reqBasePath + "saveAttrDefinitionCustom", params: params, type: 'json', method: "POST", callback: function (json) {
					parent.layer.close(index);
					parent.refreshCode = '0';
				}});
			}
			return false;
		});
	}});

	$("body").on("click", "#cancle", function() {
		parent.layer.close(index);
	});
});