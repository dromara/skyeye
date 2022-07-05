
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
	var reserveTemplate = $("#reserveTemplate").html();

	AjaxPostUtil.request({url: flowableBasePath + "conferenceroomreserve004", params: {rowId: parent.rowId}, type: 'json', callback: function(json) {
		$("#showForm").append(getDataUseHandlebars(reserveTemplate, json));
		// 附件回显
		skyeyeEnclosure.initTypeISData({'enclosureUpload': json.bean.enclosureInfo});

		// 预定时间
		laydate.render({elem : '#reserveTime', type : 'datetime', trigger : 'click', range : true});

		// 会议室
		showGrid({
			id: "conferenceRoom",
			url: flowableBasePath + "conferenceroomreserve008",
			params: {},
			pagination: false,
			template: getFileContent('tpl/template/select-option-must.tpl'),
			ajaxSendLoadBefore: function(hdb){
			},
			ajaxSendAfter:function(j){
				$("#conferenceRoom").val(json.bean.conferenceRoom);
				form.render('select');
			}
		});

		if(json.bean.state == '1'){
			$(".typeTwo").removeClass("layui-hide");
		} else {
			$(".typeOne").removeClass("layui-hide");
		}
		matchingLanguage();
	}});

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
			activitiUtil.startProcess(sysActivitiModel["conFerenceRoomReserve"]["key"], function (approvalId) {
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
			startTime: $("#reserveTime").val().split(' - ')[0].trim(),
			endTime: $("#reserveTime").val().split(' - ')[1].trim(),
			reserveReason: $("#reserveReason").val(),
			remark: $("#remark").val(),
			conferenceRoom: $("#conferenceRoom").val(),
			enclosureInfo: skyeyeEnclosure.getEnclosureIdsByBoxId('enclosureUpload'),
			subType: subType, // 1：保存为草稿  2.提交到工作流  3.在工作流中编辑
			approvalId: approvalId,
		};
		AjaxPostUtil.request({url: flowableBasePath + "conferenceroomreserve005", params: params, type: 'json', callback: function(json) {
			parent.layer.close(index);
			parent.refreshCode = '0';
		}});
	}

	$("body").on("click", "#cancle", function() {
		parent.layer.close(index);
	});
});