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
		url: reqBasePath + "queryCompanyMationById",
		params: {id: parent.rowId},
		pagination: false,
		method: 'GET',
		template: $("#beanTemplate").html(),
		ajaxSendLoadBefore: function(hdb) {
		},
		ajaxSendAfter:function (json) {
			ue = ueEditorUtil.initEditor('remark');
			ue.addListener("ready", function () {
				ue.setContent(json.bean.remark);
			});

			if (json.bean.pId == '0' || isNull(json.bean.pId)) {
				$("#parentIdBox").addClass("layui-hide");
				$("input:radio[name=companyType][value='1']").attr("checked", true);
			} else {
				$("input:radio[name=companyType][value='2']").attr("checked", true);
			}
			// 初始化总公司
			loadParentCompany(json.bean.pId, json.bean.id);

			areaUtil.initArea('0', 'address', form, json.bean);

			form.on('radio(companyType)', function (data) {
				var val = data.value;
				if (val == '1') {//总公司
					$("#parentIdBox").addClass("layui-hide");
				} else if (val == '2') {//子公司
					$("#parentIdBox").removeClass("layui-hide");
				} else {
					winui.window.msg('状态值错误', {icon: 2, time: 2000});
				}
			});

			// 设置个人所得税比例
			initTable();
			initTableChooseUtil.deleteAllRow('taxRateList');
			if (!isNull(json.bean.taxRate)){
				$.each(json.bean.taxRate, function(i, item) {
					initTableChooseUtil.resetData('taxRateList', item);
				});
			}

			matchingLanguage();
			form.render();
			form.on('submit(formEditBean)', function (data) {
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
						taxRate: JSON.stringify(initTableChooseUtil.getDataList('taxRateList').dataList),
						id: parent.rowId
					};

					params = $.extend(true, params, areaUtil.getValue());

					AjaxPostUtil.request({url: reqBasePath + "writeCompanyMation", params: params, type: 'json', method: 'POST', callback: function (json) {
						parent.layer.close(index);
						parent.refreshCode = '0';
					}});
				}
				return false;
			});

		}
	});

	function initTable() {
		initTableChooseUtil.initTable({
			id: "taxRateList",
			cols: [
				{id: 'minMoney', title: '薪资范围-最小值', formType: 'input', verify: 'required|money', width: '100'},
				{id: 'maxMoney', title: '薪资范围-最大值', formType: 'input', verify: 'required|money', width: '100'},
				{id: 'janRate', title: '一月(%)', formType: 'input', verify: 'required|percentage', width: '90'},
				{id: 'febRate', title: '二月(%)', formType: 'input', verify: 'required|percentage', width: '90'},
				{id: 'marRate', title: '三月(%)', formType: 'input', verify: 'required|percentage', width: '90'},
				{id: 'aprRate', title: '四月(%)', formType: 'input', verify: 'required|percentage', width: '90'},
				{id: 'mayRate', title: '五月(%)', formType: 'input', verify: 'required|percentage', width: '90'},
				{id: 'junRate', title: '六月(%)', formType: 'input', verify: 'required|percentage', width: '90'},
				{id: 'julRate', title: '七月(%)', formType: 'input', verify: 'required|percentage', width: '90'},
				{id: 'augRate', title: '八月(%)', formType: 'input', verify: 'required|percentage', width: '90'},
				{id: 'septRate', title: '九月(%)', formType: 'input', verify: 'required|percentage', width: '90'},
				{id: 'octRate', title: '十月(%)', formType: 'input', verify: 'required|percentage', width: '90'},
				{id: 'novRate', title: '十一月(%)', formType: 'input', verify: 'required|percentage', width: '90'},
				{id: 'decRate', title: '十二月(%)', formType: 'input', verify: 'required|percentage', width: '90'},
			],
			deleteRowCallback: function (trcusid) {
			},
			addRowCallback: function (trcusid) {
			},
			form: form,
			minData: 0
		});
	}

	// 加载总公司
	function loadParentCompany(pId, id){
		showGrid({
			id: "OverAllCompany",
			url: reqBasePath + "companymation006",
			params: {notId: id},
			pagination: false,
			template: getFileContent('tpl/template/select-option.tpl'),
			ajaxSendLoadBefore: function(hdb) {
			},
			ajaxSendAfter:function (json) {
				$("#OverAllCompany").val(pId);
				form.render('select');
			}
		});
	}

	// 取消
	$("body").on("click", "#cancle", function() {
		parent.layer.close(index);
	});
});