var userList = new Array();//选择用户返回的集合或者进行回显的集合
var processInstanceId = "";

//已经选择的客户信息
var customerMation = {};

// 合同信息
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'laydate', 'tagEditor', 'textool', 'form'], function (exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
	var $ = layui.$,
		form = layui.form,
		laydate = layui.laydate,
		textool = layui.textool;
	var selOption = getFileContent('tpl/template/select-option.tpl');

	showGrid({
		id: "showForm",
		url: flowableBasePath + "mycrmcontract004",
		params: {rowId: parent.rowId},
		pagination: false,
		template: getFileContent('tpl/crmcontractmanage/mycrmcontracteditTemplate.tpl'),
		ajaxSendLoadBefore: function(hdb){
		},
		ajaxSendAfter: function(json){
			if(json.bean.editRow == "1"){
				$(".typeTwo").addClass("layui-hide");
			}else if(json.bean.editRow == "2"){
				$(".typeOne").addClass("layui-hide");
			}
			processInstanceId = json.bean.processInstanceId;
			//签约日期
			laydate.render({
			  elem: '#signingTime',
			  type: 'date',
			});

			//生效日期
			laydate.render({
			  elem: '#effectTime',
			  type: 'date',
			});

			//服务结束日期
			laydate.render({
			  elem: '#serviceEndTime',
			  type: 'date',
			});

			textool.init({
				eleId: 'technicalTerms',
				maxlength: 200,
				tools: ['count', 'copy', 'reset']
			});

			textool.init({
				eleId: 'businessTerms',
				maxlength: 200,
				tools: ['count', 'copy', 'reset']
			});

			var userNames = [];
			userList = [].concat(json.bean.relationUserId);
			$.each(json.bean.relationUserId, function(i, item){
				userNames.push(item.name);
			});
			//关联人员选择
			$('#relationUserId').tagEditor({
				initialTags: userNames,
				placeholder: '请选择关联人员',
				editorTag: false,
				beforeTagDelete: function(field, editor, tags, val) {
					userList = [].concat(arrayUtil.removeArrayPointName(userList, val));
				}
			});

			// 客户id赋值
			customerMation = {
				id: json.bean.customerId
			};

			// 获取当前登录用户所属企业的所有部门信息
			systemCommonUtil.queryDepartmentListByCurrentUserBelong(function(data){
				$("#departmentId").html(getDataUseHandlebars(selOption, data));
				$("#departmentId").val(json.bean.departmentId);
				form.render('select');
			});

			// 附件回显
			skyeyeEnclosure.initTypeISData({'enclosureUpload': json.bean.enclosureInfo});

			matchingLanguage();
			form.render();
		}
	});

	// 关联人员选择
	$("body").on("click", "#userNameSelPeople", function(e){
		systemCommonUtil.userReturnList = [].concat(userList);
		systemCommonUtil.chooseOrNotMy = "1"; // 人员列表中是否包含自己--1.包含；其他参数不包含
		systemCommonUtil.chooseOrNotEmail = "2"; // 人员列表中是否必须绑定邮箱--1.必须；其他参数没必要
		systemCommonUtil.checkType = "1"; // 人员选择类型，1.多选；其他。单选
		systemCommonUtil.openSysUserStaffChoosePage(function (userReturnList) {
			// 重置数据
			userList = [].concat(systemCommonUtil.tagEditorResetData('relationUserId', userReturnList));
		});
	});

	// 客户选择
	$("body").on("click", "#customMationSel", function(e){
		_openNewWindows({
			url: "../../tpl/customermanage/customerChoose.html",
			title: "选择客户",
			pageId: "customerchooselist",
			area: ['100vw', '100vh'],
			callBack: function(refreshCode){
				if (refreshCode == '0') {
					$("#customName").val(customerMation.customName);
					$("#contacts").val(customerMation.contacts);
					$("#city").val(customerMation.city);
					$("#detailAddress").val(customerMation.detailAddress);
					$("#workPhone").val(customerMation.workPhone);
					$("#mobilePhone").val(customerMation.mobilePhone);
					$("#email").val(customerMation.email);
					$("#qq").val(customerMation.qq);
				} else if (refreshCode == '-9999') {
					winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2, time: 2000});
				}
			}});
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
			activitiUtil.startProcess(sysActivitiModel["myCrmContract"]["key"], function (approvalId) {
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

	function saveData(subType, approvalId){
		if(isNull(customerMation.id)){
			winui.window.msg("请选择客户", {icon: 2, time: 2000});
			return false;
		}

		var params = {
			rowId: parent.rowId,
			city: $("#city").val(),
			detailAddress: $("#detailAddress").val(),
			title: $("#title").val(),
			num: $("#num").val(),
			price: $("#price").val(),
			signingTime: $("#signingTime").val(),
			effectTime: $("#effectTime").val(),
			serviceEndTime: $("#serviceEndTime").val(),
			contacts: $("#contacts").val(),
			workPhone: $("#workPhone").val(),
			mobilePhone: $("#mobilePhone").val(),
			email: $("#email").val(),
			qq: $("#qq").val(),
			technicalTerms: $("#technicalTerms").val(),
			businessTerms: $("#businessTerms").val(),
			customerId: customerMation.id,
			departmentId: $("#departmentId").val(),
			enclosureInfo: skyeyeEnclosure.getEnclosureIdsByBoxId('enclosureUpload'),
			subType: subType, // 1：保存为草稿  2.提交到工作流  3.在工作流中编辑
			approvalId: approvalId,
		};

		//加载关联人员
		if(userList.length == 0 || isNull($('#relationUserId').tagEditor('getTags')[0].tags)){
			winui.window.msg("请选择关联人员", {icon: 2, time: 2000});
		} else {
			var relationUserId = "";
			$.each(userList, function (i, item) {
				relationUserId += item.id + ',';
			});
			params.relationUserId = relationUserId;
		}
		AjaxPostUtil.request({url: flowableBasePath + "mycrmcontract003", params: params, type: 'json', callback: function(json){
			if (json.returnCode == 0){
				parent.layer.close(index);
				parent.refreshCode = '0';
			} else {
				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
			}
		}});
	}

	$("body").on("click", "#cancle", function() {
		parent.layer.close(index);
	});
});