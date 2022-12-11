
var responsIdList = new Array();// 商机负责人返回的集合或者进行回显的集合
var partIdList = new Array();// 商机参与人返回的集合或者进行回显的集合
var followIdList = new Array();// 商机关注人返回的集合或者进行回显的集合

// 客户商机
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'laydate', 'tagEditor', 'textool'], function (exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
    var $ = layui.$,
    	laydate = layui.laydate,
    	form = layui.form,
    	textool = layui.textool;
	var serviceClassName = sysServiceMation["crmOpportUnity"]["key"];
    var selOption = getFileContent('tpl/template/select-option.tpl');
    
    showGrid({
	 	id: "showForm",
	 	url: flowableBasePath + "opportunity004",
	 	params: {rowId: parent.rowId},
	 	pagination: false,
	 	template: getFileContent('tpl/crmOpportunity/crmopportunityeditTemplate.tpl'),
	 	ajaxSendLoadBefore: function(hdb) {
	 	},
	 	ajaxSendAfter: function (json) {

			sysCustomerUtil.customerMation = {
	 			id: json.bean.customerId,
	 			customName: json.bean.customerName
	 		};
	 		$("#customName").val(sysCustomerUtil.customerMation.customName);
	 		
	 		textool.init({eleId: 'businessNeed', maxlength: 1000});
	 		
	 		if(json.bean.state == '1'){
				$(".typeTwo").removeClass("layui-hide");
			} else {
				$(".typeOne").removeClass("layui-hide");
			}

			// 商机来源
			sysDictDataUtil.showDictDataListByDictTypeCode(sysDictData["crmOpportunityFrom"]["key"], 'select', "fromId", json.bean.fromId, form);

			// 获取当前登录用户所属企业的所有部门信息
			systemCommonUtil.queryDepartmentListByCurrentUserBelong(function(data) {
				$("#subDepartments").html(getDataUseHandlebars(selOption, data));
				$("#subDepartments").val(json.bean.subDepartments);
				form.render('select');
			});

			// 选择入职时间
			laydate.render({elem: '#estimateEndTime', range: false});

			// 附件回显
			skyeyeEnclosure.initTypeISData({'enclosureUpload': json.bean.enclosureInfo});

        	matchingLanguage();

	 	   /*商机负责人选择开始*/ 
	 	   var responsIdNames = "";
           responsIdList = json.bean.responsId;
           $.each(responsIdList, function (i, item) {
        	   responsIdNames += item.name + ',';
           });
           $('#responsId').tagEditor({
				initialTags: responsIdNames.split(','),
           		placeholder: '请选择商机负责人',
           		editorTag: false,
           		beforeTagDelete: function(field, editor, tags, val) {
					responsIdList = [].concat(arrayUtil.removeArrayPointName(responsIdList, val));
	           	}
           });
           $("body").on("click", "#responsIdSelPeople", function (e) {
			   systemCommonUtil.userReturnList = [].concat(responsIdList);
			   systemCommonUtil.chooseOrNotMy = "1"; // 人员列表中是否包含自己--1.包含；其他参数不包含
			   systemCommonUtil.chooseOrNotEmail = "2"; // 人员列表中是否必须绑定邮箱--1.必须；其他参数没必要
			   systemCommonUtil.checkType = "2"; // 人员选择类型，1.多选；其他。单选
			   systemCommonUtil.openSysUserStaffChoosePage(function (userReturnList) {
				   // 重置数据
				   responsIdList = [].concat(systemCommonUtil.tagEditorResetData('responsId', userReturnList));
			   });
           });
           /*商机负责人选择结束*/
           
           /*商机参与人选择开始*/ 
	 	   var partIdNames = "";
           partIdList = json.bean.partId;
           $.each(partIdList, function (i, item) {
        	   partIdNames += item.name + ',';
           });
           $('#partId').tagEditor({
	           	initialTags: partIdNames.split(','),
	           	placeholder: '请选择商机参与人',
	           	editorTag: false,
	           	beforeTagDelete: function(field, editor, tags, val) {
					partIdList = [].concat(arrayUtil.removeArrayPointName(partIdList, val));
	           	}
           });
           $("body").on("click", "#partIdSelPeople", function (e) {
			   systemCommonUtil.userReturnList = [].concat(partIdList);
			   systemCommonUtil.chooseOrNotMy = "1"; // 人员列表中是否包含自己--1.包含；其他参数不包含
			   systemCommonUtil.chooseOrNotEmail = "2"; // 人员列表中是否必须绑定邮箱--1.必须；其他参数没必要
			   systemCommonUtil.checkType = "1"; // 人员选择类型，1.多选；其他。单选
			   systemCommonUtil.openSysUserStaffChoosePage(function (userReturnList) {
				   // 重置数据
				   partIdList = [].concat(systemCommonUtil.tagEditorResetData('partId', userReturnList));
			   });
           });
           /*商机参与人选择结束*/
           
           /*商机关注人选择开始*/ 
	 	   var followIdNames = "";
           followIdList = json.bean.followId;
           $.each(followIdList, function (i, item) {
        	   followIdNames += item.name + ',';
           });
           $('#followId').tagEditor({
               	initialTags: followIdNames.split(','),
               	placeholder: '请选择商机关注人',
               	editorTag: false,
               	beforeTagDelete: function(field, editor, tags, val) {
					followIdList = [].concat(arrayUtil.removeArrayPointName(followIdList, val));
               	}
           });
           $("body").on("click", "#followIdSelPeople", function (e) {
			   systemCommonUtil.userReturnList = [].concat(followIdList);
			   systemCommonUtil.chooseOrNotMy = "1"; // 人员列表中是否包含自己--1.包含；其他参数不包含
			   systemCommonUtil.chooseOrNotEmail = "2"; // 人员列表中是否必须绑定邮箱--1.必须；其他参数没必要
			   systemCommonUtil.checkType = "1"; // 人员选择类型，1.多选；其他。单选
			   systemCommonUtil.openSysUserStaffChoosePage(function (userReturnList) {
				   // 重置数据
				   followIdList = [].concat(systemCommonUtil.tagEditorResetData('followId', userReturnList));
			   });
           });
           /*商机关注人选择结束*/
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
	form.on('submit(subBean)', function(data) {
		if(winui.verifyForm(data.elem)) {
			saveData('3', "");
		}
		return false;
	});

	function saveData(subType, approvalId) {
		var params = {
			rowId: parent.rowId,
			title: $("#title").val(),
			city: $("#city").val(),
			detailAddress: $("#detailAddress").val(),
			estimatePrice: $("#estimatePrice").val(),
			estimateEndTime: $("#estimateEndTime").val(),
			contacts: $("#contacts").val(),
			department: $("#department").val(),
			job: $("#job").val(),
			workPhone: $("#workPhone").val(),
			mobilePhone: $("#mobilePhone").val(),
			email: $("#email").val(),
			qq: $("#qq").val(),
			businessNeed: $("#businessNeed").val(),
			customerId: sysCustomerUtil.customerMation.id,
			fromId: $("#fromId").val(),
			subDepartments: $("#subDepartments").val(),
			enclosureInfo: skyeyeEnclosure.getEnclosureIdsByBoxId('enclosureUpload'),
			subType: subType, // 1：保存为草稿  2.提交到工作流  3.在工作流中编辑
			approvalId: approvalId,
			responsId: systemCommonUtil.tagEditorGetItemData('responsId', responsIdList), // 商机负责人
			partId: systemCommonUtil.tagEditorGetAllData('partId', partIdList), // 商机参与人
			followId: systemCommonUtil.tagEditorGetAllData('followId', followIdList) // 商机关注人
		};
		// 如果商机负责人为空
		if (isNull(params.responsId)) {
			winui.window.msg('请选择商机负责人', {icon: 2, time: 2000});
			return false;
		}
		AjaxPostUtil.request({url: flowableBasePath + "opportunity012", params: params, type: 'json', callback: function(json) {
			parent.layer.close(index);
			parent.refreshCode = '0';
		}});
	}
	
	// 客户选择
    $("body").on("click", "#customMationSel", function(e) {
		sysCustomerUtil.openSysCustomerChoosePage(function (customerMation) {
			$("#customName").val(customerMation.customName);
			$("#industryName").html(customerMation.industryName);
			$("#city").val(customerMation.city);
			$("#detailAddress").val(customerMation.detailAddress);
			$("#contacts").val(customerMation.contacts);
			$("#department").val(customerMation.department);
			$("#job").val(customerMation.job);
			$("#workPhone").val(customerMation.workPhone);
			$("#mobilePhone").val(customerMation.mobilePhone);
			$("#email").val(customerMation.email);
			$("#qq").val(customerMation.qq);
		});
    });

    $("body").on("click", "#cancle", function() {
    	parent.layer.close(index);
    });
});