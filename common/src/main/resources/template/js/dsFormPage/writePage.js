
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
	var selOption = getFileContent('tpl/template/select-option.tpl');

	$("#serviceStr").html(getDataUseHandlebars(selOption, {rows: serviceMap}));

	if (!isNull(parent.rowId)) {
		AjaxPostUtil.request({url: reqBasePath + "dsformpage006", params: {id: parent.rowId}, type: 'json', method: 'GET', callback: function (json) {
			$("#name").val(json.bean.name);
			$("#remark").val(json.bean.remark);
			skyeyeClassEnumUtil.showEnumDataListByClassName("dsFormPageType", 'select', "type", json.bean.type, form);

			var businessApi = json.bean.businessApi;
			$("#serviceStr").val(businessApi.serviceStr);
			$("#api").val(businessApi.api);
			skyeyeClassEnumUtil.showEnumDataListByClassName("httpMethodEnum", 'select', "method", businessApi.method, form);

			loadOperate(json.bean.operateIdList);
		}});
	} else {
		skyeyeClassEnumUtil.showEnumDataListByClassName("dsFormPageType", 'select', "type", '', form);
		skyeyeClassEnumUtil.showEnumDataListByClassName("httpMethodEnum", 'select', "method", '', form);
		loadOperate(null);
	}

	function loadOperate(defaultValue) {
		AjaxPostUtil.request({url: reqBasePath + "queryOperateList", params: {className: parent.objectId}, type: 'json', method: 'POST', callback: function (json) {
			var value = isNull(defaultValue) ? '' : defaultValue.toString();
			dataShowType.showData(json, 'verificationSelect', 'operateIdList', value, form);
		}});
	}

	matchingLanguage();
	form.render();
	form.on('submit(formWriteBean)', function (data) {
		if (winui.verifyForm(data.elem)) {
			var params = {
				id: isNull(parent.rowId) ? '' : parent.rowId,
				name: $("#name").val(),
				remark: $("#remark").val(),
				type: $("#type").val(),
				className: parent.objectId,
				operateIdList: isNull($('#operateIdList').attr('value')) ? [] : $('#operateIdList').attr('value')
			};
			var businessApi = {
				serviceStr: $("#serviceStr").val(),
				api: $("#api").val(),
				method: $("#method").val()
			};
			params.businessApi = JSON.stringify(businessApi);
			AjaxPostUtil.request({url: reqBasePath + "writeDsFormPage", params: params, type: 'json', method: "POST", callback: function (json) {
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