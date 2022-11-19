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

	initTableChooseUtil.initTable({
		id: "taxRateList",
		cols: [
			{id: 'minMoney', title: '薪资范围-最小值<i class="red">*</i>', formType: 'input', verify: 'required|money', width: '100'},
			{id: 'maxMoney', title: '薪资范围-最大值<i class="red">*</i>', formType: 'input', verify: 'required|money', width: '100'},
			{id: 'janRate', title: '一月(%)<i class="red">*</i>', formType: 'input', verify: 'required|percentage', width: '90'},
			{id: 'febRate', title: '二月(%)<i class="red">*</i>', formType: 'input', verify: 'required|percentage', width: '90'},
			{id: 'marRate', title: '三月(%)<i class="red">*</i>', formType: 'input', verify: 'required|percentage', width: '90'},
			{id: 'aprRate', title: '四月(%)<i class="red">*</i>', formType: 'input', verify: 'required|percentage', width: '90'},
			{id: 'mayRate', title: '五月(%)<i class="red">*</i>', formType: 'input', verify: 'required|percentage', width: '90'},
			{id: 'junRate', title: '六月(%)<i class="red">*</i>', formType: 'input', verify: 'required|percentage', width: '90'},
			{id: 'julRate', title: '七月(%)<i class="red">*</i>', formType: 'input', verify: 'required|percentage', width: '90'},
			{id: 'augRate', title: '八月(%)<i class="red">*</i>', formType: 'input', verify: 'required|percentage', width: '90'},
			{id: 'septRate', title: '九月(%)<i class="red">*</i>', formType: 'input', verify: 'required|percentage', width: '90'},
			{id: 'octRate', title: '十月(%)<i class="red">*</i>', formType: 'input', verify: 'required|percentage', width: '90'},
			{id: 'novRate', title: '十一月(%)<i class="red">*</i>', formType: 'input', verify: 'required|percentage', width: '90'},
			{id: 'decRate', title: '十二月(%)<i class="red">*</i>', formType: 'input', verify: 'required|percentage', width: '90'},
		],
		deleteRowCallback: function (trcusid) {
		},
		addRowCallback: function (trcusid) {
		},
		form: form,
		minData: 0
	});

	showGrid({
		id: "OverAllCompany",
		url: reqBasePath + "companymation006",
		params: {},
		pagination: false,
		template: getFileContent('tpl/template/select-option.tpl'),
		ajaxSendLoadBefore: function(hdb) {
		},
		ajaxSendAfter:function (json) {
			form.render('select');
		}
	});

	// 默认隐藏父公司
	$("#parentIdBox").addClass("layui-hide");
	areaUtil.initArea('0', 'address', form, null);

	form.on('radio(companyType)', function (data) {
		var val = data.value;
		if (val == '1') {//总公司
			$("#parentIdBox").addClass("layui-hide");
		} else if (val == '2') {//子公司
			$("#parentIdBox").removeClass("layui-hide");
		}
	});

	matchingLanguage();
	form.render();
	form.on('submit(formAddBean)', function (data) {
		if (winui.verifyForm(data.elem)) {
			var pId = '0';
			if($("input[name='companyType']:checked").val() == '2'){
				if(isNull($("#OverAllCompany").val())) {
					winui.window.msg('请选择总公司', {icon: 2, time: 2000});
					return false;
				} else {
					pId = $("#OverAllCompany").val();
				}
			}
			var params = {
				name: $("#name").val(),
				remark: encodeURIComponent(ue.getContent()),
				pId: pId,
				taxRate: JSON.stringify(initTableChooseUtil.getDataList('taxRateList').dataList)
			};
			params = $.extend(true, params, areaUtil.getValue());

			AjaxPostUtil.request({url: reqBasePath + "writeCompanyMation", params: params, type: 'json', method: 'POST', callback: function (json) {
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