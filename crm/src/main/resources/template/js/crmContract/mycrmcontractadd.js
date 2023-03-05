var userList = new Array();//选择用户返回的集合或者进行回显的集合

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
	var serviceClassName = sysServiceMation["myCrmContract"]["key"];

	// 签约日期
	laydate.render({elem: '#signingTime', type: 'date', trigger: 'click'});

	// 生效日期
	laydate.render({elem: '#effectTime', type: 'date', trigger: 'click'});

	// 服务结束日期
	laydate.render({elem: '#serviceEndTime', type: 'date', trigger: 'click'});

	textool.init({eleId: 'technicalTerms', maxlength: 200});

	textool.init({eleId: 'businessTerms', maxlength: 200});

	// 获取当前登录用户所属企业的所有部门信息
	systemCommonUtil.queryDepartmentListByCurrentUserBelong(function(data) {
		$("#departmentId").html(getDataUseHandlebars(getFileContent('tpl/template/select-option.tpl'), data));
		form.render('select');
	});

	skyeyeEnclosure.init('enclosureUpload');
	matchingLanguage();
	form.render();

	// 关联人员
	$('#relationUserId').tagEditor({
		initialTags: [],
		placeholder: '请选择关联人员',
		editorTag: false,
		beforeTagDelete: function(field, editor, tags, val) {
			var listVal = $("#").attr('list');
			var list = isNull(listVal) ? [] : JSON.parse(listVal);
			list = [].concat(arrayUtil.removeArrayPointName(list, val));
			$("#").attr('list', JSON.stringify(list));
		}
	});
	// 关联人员选择选择
	$("body").on("click", "#userNameSelPeople", function (e) {
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
	$("body").on("click", "#customMationSel", function (e) {
		sysCustomerUtil.openSysCustomerChoosePage(function (customerMation){
			$("#customName").val(customerMation.customName);
			$("#contacts").val(customerMation.contacts);
			$("#city").val(customerMation.city);
			$("#detailAddress").val(customerMation.detailAddress);
			$("#workPhone").val(customerMation.workPhone);
			$("#mobilePhone").val(customerMation.mobilePhone);
			$("#email").val(customerMation.email);
			$("#qq").val(customerMation.qq);
		});
	});

	// 保存为草稿
	form.on('submit(formAddBean)', function(data) {
		if(winui.verifyForm(data.elem)) {
			saveData("1", "");
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

	function saveData(subType, approvalId) {
		var params = {
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
			customerId: sysCustomerUtil.customerMation.id,
			departmentId: $("#departmentId").val(),
			relationUserId: systemCommonUtil.tagEditorGetAllData('relationUserId', userList),
			enclosureInfo: skyeyeEnclosure.getEnclosureIdsByBoxId('enclosureUpload'),
			subType: subType, // 表单类型 1.保存草稿  2.提交审批
			approvalId: approvalId
		};

		// 加载关联人员
		if (isNull(params.relationUserId)) {
			winui.window.msg("请选择关联人员", {icon: 2, time: 2000});
			return false;
		}
		AjaxPostUtil.request({url: flowableBasePath + "mycrmcontract002", params: params, type: 'json', callback: function(json) {
			parent.layer.close(index);
			parent.refreshCode = '0';
		}});
	}

	$("body").on("click", "#cancle", function() {
		parent.layer.close(index);
	});

});