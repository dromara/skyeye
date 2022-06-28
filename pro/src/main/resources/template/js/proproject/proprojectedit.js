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
	var ue;

	showGrid({
		id: "showForm",
		url: flowableBasePath + "proproject006",
		params: {rowId: parent.rowId},
		pagination: false,
		template: getFileContent('tpl/proproject/proprojecteditTemplate.tpl'),
		ajaxSendLoadBefore: function(hdb){
		},
		ajaxSendAfter: function (json) {
			if(json.bean.state == 1){
				$(".typeTwo").removeClass("layui-hide");
			} else {
				$(".typeOne").removeClass("layui-hide");
			}
			//计划开始时间
			laydate.render({
			  elem: '#startTime',
			  type: 'date',
			  trigger: 'click'
			});

			//计划完成时间
			laydate.render({
			  elem: '#endTime',
			  type: 'date',
			  trigger: 'click'
			});

			ue = ueEditorUtil.initEditor('container');
			ue.addListener("ready", function () {
				ue.setContent(json.bean.businessContent);
			});

			//项目分类
			showGrid({
				id: "typeId",
				url: flowableBasePath + "proprojecttype008",
				params: {},
				pagination: false,
				template: getFileContent('tpl/template/select-option.tpl'),
				ajaxSendLoadBefore: function(hdb){
				},
				ajaxSendAfter:function(j){
					$("#typeId").val(json.bean.typeId);
					form.render('select');
					departmentId();
				}
			});

			function departmentId(){
				// 获取当前登录用户所属企业的所有部门信息
				systemCommonUtil.queryDepartmentListByCurrentUserBelong(function(data){
					$("#departmentId").html(getDataUseHandlebars(getFileContent('tpl/template/select-option.tpl'), data));
					$("#departmentId").val(json.bean.departmentId);
					form.render('select');
					customerId();
				});
			}

			function customerId(){
				//客户
				showGrid({
					id: "customerId",
					url: flowableBasePath + "customer007",
					params: {},
					pagination: false,
					template: getFileContent('tpl/template/select-option.tpl'),
					ajaxSendLoadBefore: function(hdb){
					},
					ajaxSendAfter:function(j){
						customerList = j.rows;
						$("#customerId").val(json.bean.customerId);
						if(!isNull(json.bean.customerId)){
							contractId(json.bean.customerId, 1);
						}
						form.render('select');
					}
				});

				form.on('select(customerId)', function(data){
					if(!isNull(data.value) && data.value != '请选择') {
						contractId(data.value, 2);
						$.each(customerList, function(i, item){
							if(data.value == item.id){
								$("#contactName").val(item.contacts);
								$("#telphone").val(item.workPhone);
								$("#mobile").val(item.mobilePhone);
								$("#mail").val(item.email);
								$("#qq").val(item.qq);
								return false;
							}
						});
					} else {
						$("#contractId").html("");
						form.render('select');
					}
				});
			}

			function contractId(id, type){
				//合同
				showGrid({
					id: "contractId",
					url: flowableBasePath + "mycrmcontract008",
					params: {id: id},
					pagination: false,
					template: getFileContent('tpl/template/select-option.tpl'),
					ajaxSendLoadBefore: function(hdb){
					},
					ajaxSendAfter:function(j){
						if(type == 1){
							$("#contractId").val(json.bean.contractId);
						}
						form.render('select');
					}
				});
			}

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
			activitiUtil.startProcess(sysActivitiModel["proProject"]["key"], function (approvalId) {
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
			customerId: $("#customerId").val(),
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