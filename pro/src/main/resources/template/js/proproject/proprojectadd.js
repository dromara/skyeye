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

	var ue = UE.getEditor('container',{
		//初始化高度
		initialFrameHeight: 800,
		maximumWords: 100000
	});
	UE.Editor.prototype._bkGetActionUrl = UE.Editor.prototype.getActionUrl;
	UE.Editor.prototype.getActionUrl = function(action){
		if (action == 'uploadimage' || action == 'uploadfile' || action == 'uploadvideo' || action == 'uploadimage'){//上传单个图片,上传附件,上传视频,多图上传
			return reqBasePath + '/upload/editUploadController/uploadContentPic?userToken=' + getCookie('userToken');
		} else if(action == 'listimage'){
			return reqBasePath + '/upload/editUploadController/downloadContentPic?userToken=' + getCookie('userToken');
		}else{
			return this._bkGetActionUrl.call(this, action);
		}
	};

	//项目分类
	showGrid({
		id: "typeId",
		url: reqBasePath + "proprojecttype008",
		params: {},
		pagination: false,
		template: getFileContent('tpl/template/select-option.tpl'),
		ajaxSendLoadBefore: function(hdb){
		},
		ajaxSendAfter:function(json){
			form.render('select');
			departmentId();
		}
	});

	function departmentId(){
		//所属部门
		showGrid({
			id: "departmentId",
			url: reqBasePath + "mycrmcontract006",
			params: {},
			pagination: false,
			template: getFileContent('tpl/template/select-option.tpl'),
			ajaxSendLoadBefore: function(hdb){
			},
			ajaxSendAfter:function(json){
				form.render('select');
				customerId();
			}
		});
	}

	function customerId(){
		//客户
		showGrid({
			id: "customerId",
			url: reqBasePath + "customer007",
			params: {},
			pagination: false,
			template: getFileContent('tpl/template/select-option.tpl'),
			ajaxSendLoadBefore: function(hdb){
			},
			ajaxSendAfter:function(json){
				customerList = json.rows;
				form.render('select');
			}
		});

		form.on('select(customerId)', function(data){
			if(!isNull(data.value) && data.value != '请选择') {
				contractId(data.value);
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
			}else{
				$("#contractId").html("");
				form.render('select');
			}
		});
	}

	function contractId(id){
		//合同
		showGrid({
			id: "contractId",
			url: reqBasePath + "mycrmcontract008",
			params: {id: id},
			pagination: false,
			template: getFileContent('tpl/template/select-option.tpl'),
			ajaxSendLoadBefore: function(hdb){
			},
			ajaxSendAfter:function(json){
				form.render('select');
			}
		});
	}

	skyeyeEnclosure.init('enclosureUpload');
	matchingLanguage();
	form.render();

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
			activitiUtil.startProcess(sysActivitiModel["proProject"]["key"], function (approvalId) {
				saveData("2", approvalId);
			});
		}
		return false;
	});

	function saveData(subType, approvalId){
		var params = {
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
			customerId: $("#customerId").val(),
			departmentId: $("#departmentId").val(),
			typeId: $("#typeId").val(),
			contractId: $("#contractId").val(),
			businessEnclosureInfo: skyeyeEnclosure.getEnclosureIdsByBoxId('enclosureUpload'),
			subType: subType, // 表单类型 1.保存草稿  2.提交审批
			approvalId: approvalId
		};
		// 获取内容
		params.businessContent = encodeURIComponent(ue.getContent());
		if(isNull(params.businessContent)){
			winui.window.msg("请填写业务需求和目标", {icon: 2,time: 2000});
			return false;
		}
		AjaxPostUtil.request({url: reqBasePath + "proproject003", params: params, type: 'json', callback: function(json){
			if (json.returnCode == 0){
				parent.layer.close(index);
				parent.refreshCode = '0';
			} else {
				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
			}
		}});
	}

	$("body").on("click", "#cancle", function(){
		parent.layer.close(index);
	});
});