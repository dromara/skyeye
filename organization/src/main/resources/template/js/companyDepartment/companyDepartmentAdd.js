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
	var ue = ueEditorUtil.initEditor('remark');

	// 加载公司数据
	systemCommonUtil.getSysCompanyList(function (json) {
		$("#companyId").html(getDataUseHandlebars(getFileContent('tpl/template/select-option.tpl'), json));
	});

	matchingLanguage();
	form.render();
	form.on('submit(formAddBean)', function (data) {
		if (winui.verifyForm(data.elem)) {
			var params = {
				name: $("#name").val(),
				remark: encodeURIComponent(ue.getContent()),
				companyId: isNull($("#companyId").val()) ? '0' : $("#companyId").val(),
				parentId: '0',
				overtimeSettlementType: $("#overtimeSettlementType").val()
			};

			AjaxPostUtil.request({url: reqBasePath + "writeDepartmentMation", params: params, type: 'json', method: "POST", callback: function (json) {
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