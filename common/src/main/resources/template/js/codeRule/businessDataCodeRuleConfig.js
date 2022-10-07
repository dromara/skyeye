
layui.config({
	base: basePath,
	version: skyeyeVersion
}).extend({
	window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'form'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		form = layui.form;

	// 获取所有编码规则
	var codeRuleSelectHtml = '';
	AjaxPostUtil.request({url: reqBasePath + "getAllCodeRuleList", params: {}, type: 'json', method: "GET", callback: function(json) {
		codeRuleSelectHtml = getDataUseHandlebars(getFileContent('tpl/template/select-option.tpl'), json);
	}, async: false});

	// 获取需要配置编码规则的业务功能对象
	showGrid({
		id: "showForm",
		url: reqBasePath + "queryClassCodeRuleList",
		params: {},
		pagination: false,
		method: "GET",
		template: $("#baseTemplate").html(),
		ajaxSendLoadBefore: function (hdb, json) {
			$.each(json.bean, function (key, appIdValue) {
				$.each(appIdValue, function (i, groupNameValue) {
					$.each(groupNameValue, function (j, bean) {
						bean.selectHtml = codeRuleSelectHtml;
					});
				});
			});
		},
		ajaxSendAfter: function (json) {
			$.each(json.bean, function (key, appIdValue) {
				$.each(appIdValue, function (i, groupNameValue) {
					$.each(groupNameValue, function (j, bean) {
						$("#" + bean.id).val(bean.codeRuleId);
					})
				});
			});

			form.render();
			form.on('select(codeRole)', function(data) {
				var cudeRuleId = data.value;
				var id = data.elem.id;
				AjaxPostUtil.request({url: reqBasePath + "editClassCodeRuleConfig", params: {id: id, cudeRuleId: cudeRuleId}, type: 'json', method: "POST", callback: function (json) {
					winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				}});
			});
		}
	});

	exports('businessDataCodeRuleConfig', {});
});
