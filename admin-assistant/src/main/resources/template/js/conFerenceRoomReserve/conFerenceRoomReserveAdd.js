
// 会议室预定
layui.config({
	base: basePath,
	version: skyeyeVersion
}).extend({
	window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'fileUpload', 'laydate', 'form'], function(exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
	var $ = layui.$,
		form = layui.form,
		laydate = layui.laydate;

	// 会议室
	showGrid({
		id: "conferenceRoom",
		url: flowableBasePath + "conferenceroomreserve008",
		params: {},
		pagination: false,
		template: getFileContent('tpl/template/select-option-must.tpl'),
		ajaxSendLoadBefore: function(hdb){
		},
		ajaxSendAfter:function(json){
			form.render('select');
		}
	});

	//预定时间
	laydate.render({
		elem : '#reserveTime',
		type : 'datetime',
		trigger : 'click',
		range : true
	});

	AjaxPostUtil.request({url: reqBasePath + "login002", params: {}, type: 'json', callback: function(json) {
		if(json.returnCode == 0) {
			$("#title").html("会议室预定申请单-" + getYMDFormatDate() + '-' + json.bean.userName);
			$("#name").html(json.bean.userName);
			matchingLanguage();
		} else {
			winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
		}
	}});

	skyeyeEnclosure.init('enclosureUpload');
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
			activitiUtil.startProcess(sysActivitiModel["conFerenceRoomReserve"]["key"], function (approvalId) {
				saveData("2", approvalId);
			});
		}
		return false;
	});

	function saveData(subType, approvalId){
		var params = {
			title: $("#title").html(),
			startTime: $("#reserveTime").val().split(' - ')[0].trim(),
			endTime: $("#reserveTime").val().split(' - ')[1].trim(),
			reserveReason: $("#reserveReason").val(),
			remark: $("#remark").val(),
			conferenceRoom: $("#conferenceRoom").val(),
			enclosureInfo: skyeyeEnclosure.getEnclosureIdsByBoxId('enclosureUpload'),
			subType: subType, // 表单类型 1.保存草稿  2.提交审批
			approvalId: approvalId
		};
		AjaxPostUtil.request({url: flowableBasePath + "conferenceroomreserve002", params: params, type: 'json', callback: function(json) {
			if(json.returnCode == 0) {
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