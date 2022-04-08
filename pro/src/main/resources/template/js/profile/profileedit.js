
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
		ajaxSendAfter:function(json){
			// 附件回显
			skyeyeEnclosure.initTypeISData({'enclosureUpload': json.bean.enclosureInfo});

			if(json.bean.state == '1'){
				$(".typeTwo").removeClass("layui-hide");
			}else{
				$(".typeOne").removeClass("layui-hide");
			}

			ue = UE.getEditor('content',{
				//初始化高度
				initialFrameHeight: 400,
				maximumWords: 10000
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
			ue.addListener("ready", function () {
				ue.setContent(json.bean.content);
			});

			//所属分类选择
			showGrid({
				id: "fileType",
				url: flowableBasePath + "profiletype008",
				params: {},
				pagination: false,
				template: getFileContent('tpl/template/select-option.tpl'),
				ajaxSendLoadBefore: function(hdb){
				},
				ajaxSendAfter: function(j){
					$("#fileType").val(json.bean.typeId);
					form.render('select');
					proIdSelect();
				}
			});

			// 获取我参与的项目列表
			function proIdSelect(){
				proUtil.queryMyProjectsList(function (data){
					$("#proId").html(getDataUseHandlebars(selOption, data));
					$("#proId").val(json.bean.proId);
					form.render('select');
				});
			}

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

	function saveData(subType, approvalId){
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
			winui.window.msg('请填写文档内容！', {icon: 2,time: 2000});
			return false;
		}
		AjaxPostUtil.request({url: flowableBasePath + "profile005", params: params, type: 'json', callback: function(json) {
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