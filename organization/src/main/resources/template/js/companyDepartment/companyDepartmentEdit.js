layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
	var $ = layui.$,
		form = layui.form;
	var ue = null;
	    
	showGrid({
		id: "showForm",
		url: reqBasePath + "queryDepartmentMationById",
		params: {id: parent.rowId},
		pagination: false,
		method: 'GET',
		template: $("#beanTemplate").html(),
		ajaxSendLoadBefore: function(hdb) {
		},
		ajaxSendAfter:function (json) {
			$("#overtimeSettlementType").val(json.bean.overtimeSettlementType);

			ue = ueEditorUtil.initEditor('remark');
			ue.addListener("ready", function () {
				ue.setContent(json.bean.remark);
			});

			// 加载公司数据
			systemCommonUtil.getSysCompanyList(function(data) {
				$("#companyId").html(getDataUseHandlebars(getFileContent('tpl/template/select-option.tpl'), data));
				$("#companyId").val(json.bean.companyId);
			});

			matchingLanguage();
			form.render();
			form.on('submit(formEditBean)', function (data) {
				if (winui.verifyForm(data.elem)) {
					var params = {
						name: $("#name").val(),
						remark: encodeURIComponent(ue.getContent()),
						companyId: isNull($("#companyId").val()) ? '0' : $("#companyId").val(),
						parentId: '0',
						overtimeSettlementType: $("#overtimeSettlementType").val(),
						id: parent.rowId
					};

					AjaxPostUtil.request({url: reqBasePath + "writeDepartmentMation", params: params, type: 'json', method: "POST", callback: function (json) {
						parent.layer.close(index);
						parent.refreshCode = '0';
					}});
				}
				return false;
			});
		}
	});

	// 取消
	$("body").on("click", "#cancle", function() {
		parent.layer.close(index);
	});
});