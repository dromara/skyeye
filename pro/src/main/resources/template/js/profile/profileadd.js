
// 项目文档
layui.config({
	base: basePath,
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'fileUpload', 'form'], function (exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
	var $ = layui.$,
		form = layui.form;
	var selOption = getFileContent('tpl/template/select-option.tpl');

	var ue = UE.getEditor('content',{
		//初始化高度
		initialFrameHeight: 400,
		maximumWords: 10000
	});
	UE.Editor.prototype._bkGetActionUrl = UE.Editor.prototype.getActionUrl;
	UE.Editor.prototype.getActionUrl = function(action){
		if (action == 'uploadimage' || action == 'uploadfile' || action == 'uploadvideo' || action == 'uploadimage'){//上传单个图片,上传附件,上传视频,多图上传
			return reqBasePath + '/upload/editUploadController/uploadContentPic';
		} else if(action == 'listimage'){
			return reqBasePath + '/upload/editUploadController/downloadContentPic';
		}else{
			return this._bkGetActionUrl.call(this, action);
		}
	};

	taskTypeSelect();

	//所属分类选择
	function taskTypeSelect(){
		showGrid({
			id: "fileType",
			url: flowableBasePath + "profiletype008",
			params: {},
			pagination: false,
			template: selOption,
			ajaxSendLoadBefore: function(hdb){
			},
			ajaxSendAfter: function(json){
				form.render('select');
				proIdSelect();
			}
		});
	}

	// 获取我参与的项目列表
	function proIdSelect(){
		proUtil.queryMyProjectsList(function (data){
			$("#proId").html(getDataUseHandlebars(selOption, data));
			form.render('select');
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
			activitiUtil.startProcess(sysActivitiModel["proFile"]["key"], function (approvalId) {
				saveData("2", approvalId);
			});
		}
		return false;
	});

	function saveData(subType, approvalId){
		var params = {
			title: $("#title").val(),
			typeId: $("#fileType").val(),
			content: encodeURIComponent(ue.getContent()),
			proId: $("#proId").val(),
			enclosureInfo: skyeyeEnclosure.getEnclosureIdsByBoxId('enclosureUpload'),
			subType: subType, // 表单类型 1.保存草稿  2.提交审批
			approvalId: approvalId
		};
		if(isNull(ue.getContent())){
			winui.window.msg('请填写文档内容！', {icon: 2,time: 2000});
			return false;
		}
		AjaxPostUtil.request({url: flowableBasePath + "profile002", params: params, type: 'json', callback: function(json){
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