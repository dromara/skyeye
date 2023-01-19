
layui.config({
	base: basePath,
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'form', 'textool', 'eleTree'], function (exports) {
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

		// 加载组件信息
		treeSelectUtil.init({
			eleTree: layui.eleTree,
			elem: 'componentId',
			url: reqBasePath + "queryAllDsFormComponentList",
			defaultId: json.bean.componentId
		});

		// 如果不是入参属性，则不能设置特定的一些值
		if (!json.bean.attrDefinition.whetherInputParams) {
			$('.inputParams').hide();
		}

		var id = isNull(json.bean.id) ? '' : json.bean.id;
		matchingLanguage();
		form.render();
		form.on('submit(formEditBean)', function (data) {
			if (winui.verifyForm(data.elem)) {
				var params = {
					className: className,
					attrKey: attrKey,
					name: $("#name").val(),
					componentId: isNull($("#componentId").val()) ? "" : $("#componentId").attr("componentId"),
					minLength: $("#minLength").val(),
					maxLength: $("#maxLength").val(),
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