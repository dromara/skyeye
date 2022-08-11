
// 项目文档
layui.config({
	base: basePath,
	version: skyeyeVersion
}).extend({
	window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'fileUpload', 'form'], function(exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
	var $ = layui.$,
		form = layui.form;
	var selOption = getFileContent('tpl/template/select-option.tpl');
	var ue;

	showGrid({
		id: "showForm",
		url: flowableBasePath + "profile004",
		params: {rowId: parent.rowId},
		pagination: false,
		template: getFileContent('tpl/profile/profileeditTemplate.tpl'),
		ajaxSendAfter:function (json) {
			// 附件回显
			skyeyeEnclosure.initTypeISData({'enclosureUpload': json.bean.enclosureInfo});

			if(json.bean.state == '1'){
				$(".typeTwo").removeClass("layui-hide");
			} else {
				$(".typeOne").removeClass("layui-hide");
			}

			ue = ueEditorUtil.initEditor('content');
			ue.addListener("ready", function () {
				ue.setContent(json.bean.content);
			});

			// 项目文档分类
			sysDictDataUtil.showDictDataListByDictTypeCode(sysDictData["pmFileType"]["key"], 'select', "fileType", json.bean.typeId, form);

			// 获取我参与的项目列表
			proUtil.queryMyProjectsList(function (data){
				$("#proId").html(getDataUseHandlebars(selOption, data));
				$("#proId").val(json.bean.proId);
				form.render('select');
			});

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
			activitiUtil.startProcess(sysActivitiModel["proFile"]["key"], function (approvalId) {
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
			typeId: $("#fileType").val(),
			content: encodeURIComponent(ue.getContent()),
			proId: $("#proId").val(),
			enclosureInfo: skyeyeEnclosure.getEnclosureIdsByBoxId('enclosureUpload'),
			subType: subType, // 1：保存为草稿  2.提交到工作流  3.在工作流中编辑
			approvalId: approvalId,
		};
		if(isNull(ue.getContent())){
			winui.window.msg('请填写文档内容！', {icon: 2, time: 2000});
			return false;
		}
		AjaxPostUtil.request({url: flowableBasePath + "profile005", params: params, type: 'json', callback: function(json) {
			parent.layer.close(index);
			parent.refreshCode = '0';
		}});
	}

	$("body").on("click", "#cancle", function() {
		parent.layer.close(index);
	});
});