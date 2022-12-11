var customerList = new Array();//客户信息列表

// 项目信息管理
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'laydate', 'tagEditor', 'form'], function (exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
	var $ = layui.$,
		form = layui.form,
		laydate = layui.laydate;
	var serviceClassName = sysServiceMation["proProject"]["key"];
	var ue;

	showGrid({
		id: "showForm",
		url: flowableBasePath + "proproject006",
		params: {rowId: parent.rowId},
		pagination: false,
		template: getFileContent('tpl/proproject/proprojecteditTemplate.tpl'),
		ajaxSendLoadBefore: function(hdb) {},
		ajaxSendAfter: function (json) {
			if(json.bean.state == 1){
				$(".typeTwo").removeClass("layui-hide");
			} else {
				$(".typeOne").removeClass("layui-hide");
			}
			// 计划开始时间
			laydate.render({elem: '#startTime', type: 'date', trigger: 'click'});

			// 计划完成时间
			laydate.render({elem: '#endTime', type: 'date', trigger: 'click'});

			ue = ueEditorUtil.initEditor('container');
			ue.addListener("ready", function () {
				ue.setContent(json.bean.businessContent);
			});

			// 项目分类
			sysDictDataUtil.showDictDataListByDictTypeCode(sysDictData["pmProjectType"]["key"], 'select', "typeId", json.bean.typeId, form);

			// 获取当前登录用户所属企业的所有部门信息
			systemCommonUtil.queryDepartmentListByCurrentUserBelong(function(data) {
				$("#departmentId").html(getDataUseHandlebars(getFileContent('tpl/template/select-option.tpl'), data));
				$("#departmentId").val(json.bean.departmentId);
				form.render('select');
			});

			sysCustomerUtil.customerMation = {
				id: json.bean.customerId
			};

			// 附件回显
			skyeyeEnclosure.initTypeISData({'enclosureUpload': json.bean.businessEnclosureInfo});

			matchingLanguage();
			form.render();
		}
	});

	// 保存为草稿
	form.on('submit(formEditBean)', function(data) {
		if(winui.verifyForm(data.elem)) {
			saveData('1', "");
		}
		return false;
	});

	// 提交审批
	form.on('submit(formSubBean)', function(data) {
		if(winui.verifyForm(data.elem)) {
			activitiUtil.startProcess(serviceClassName, null, function (approvalId) {
				saveData("2", approvalId);
			});
		}
		return false;
	});

	// 工作流中保存
	form.on('submit(save)', function(data) {
		if(winui.verifyForm(data.elem)) {
			saveData('3', "");
		}
		return false;
	});

	function saveData(subType, approvalId) {
		var params = {
			rowId: parent.rowId,
			projectName: $("#projectName").val(),
			projectNumber: $("#projectNumber").val(),
			startTime: $("#startTime").val(),
			endTime: $("#endTime").val(),
			contactName: $("#contactName").val(),
			telphone: $("#telphone").val(),
			mobile: $("#mobile").val(),
			mail: $("#mail").val(),
			qq: $("#qq").val(),
			estimatedWorkload: $("#estimatedWorkload").val(),
			estimatedCost: $("#estimatedCost").val(),
			businessContent: $("#businessContent").val(),
			customerId: sysCustomerUtil.customerMation.id,
			departmentId: $("#departmentId").val(),
			typeId: $("#typeId").val(),
			contractId: $("#contractId").val(),
			businessEnclosureInfo: skyeyeEnclosure.getEnclosureIdsByBoxId('enclosureUpload'),
			subType: subType, // 1：保存为草稿  2.提交到工作流  3.在工作流中编辑
			approvalId: approvalId,
		};
		//获取内容
		params.businessContent = encodeURIComponent(ue.getContent());
		if(isNull(params.businessContent)){
			winui.window.msg("请填写业务需求和目标", {icon: 2, time: 2000});
			return false;
		}
		AjaxPostUtil.request({url: flowableBasePath + "proproject007", params: params, type: 'json', callback: function (json) {
			parent.layer.close(index);
			parent.refreshCode = '0';
		}});
	}

	function contractId(id){
		//合同
		showGrid({
			id: "contractId",
			url: flowableBasePath + "mycrmcontract008",
			params: {id: id},
			pagination: false,
			template: getFileContent('tpl/template/select-option.tpl'),
			ajaxSendLoadBefore: function(hdb) {
			},
			ajaxSendAfter:function (json) {
				form.render('select');
			}
		});
	}

	$("body").on("click", "#customMationSel", function (e) {
		sysCustomerUtil.openSysCustomerChoosePage(function (customerMation) {
			contractId(customerMation.id);
			$("#customName").val(customerMation.customName);
			$("#telphone").val(customerMation.workPhone);
			$("#mobile").val(customerMation.mobilePhone);
			$("#mail").val(customerMation.email);
			$("#qq").val(customerMation.qq);
		});
	});


	$("body").on("click", "#cancle", function() {
		parent.layer.close(index);
	});
});