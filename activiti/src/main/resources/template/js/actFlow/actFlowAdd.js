
// 关联的工作流业务模型
var skyeyeClassFlowableMation = {};

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'form'], function (exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
	var $ = layui.$,
		form = layui.form;

	form.on('radio(businessLinkType)', function (data) {
		var val = data.value;
		if (val == '1') {
			// 不关联业务数据
			$(".business").addClass("layui-hide");
		} else if (val == '2') {
			// 关联业务数据
			$(".business").removeClass("layui-hide");
		}
	});

	matchingLanguage();
	form.render();
	form.on('submit(formAddBean)', function (data) {
		if (winui.verifyForm(data.elem)) {
			var params = {
				flowName: $("#flowName").val(),
				modelKey: $("#modelKey").val(),
				businessLinkType: data.field.businessLinkType,
				serviceClassName: $("#serviceClassName").attr("className")
			};
			if (params.businessLinkType == 2) {
				params.businessKey = $("#businessKey").val();
				params.businessData = $("#businessData").val();
				if (isNull(params.businessKey)) {
					winui.window.msg("请输入属性字段", {icon: 2, time: 2000});
					return false;
				}
				if (isNull(params.businessData)) {
					winui.window.msg("请输入属性值", {icon: 2, time: 2000});
					return false;
				}
				params.dsFormId = "";
			}

			AjaxPostUtil.request({url: flowableBasePath + "writeActFlowMation", params: params, type: 'json', method: 'POST', callback: function (json) {
				parent.layer.close(index);
				parent.refreshCode = '0';
			}});
		}
		return false;
	});

	// 工作流业务模型选择
	$("body").on("click", "#serviceClassNameSel", function (e) {
		_openNewWindows({
			url: "../../tpl/skyeyeClassFlowable/skyeyeClassFlowableChoose.html",
			title: "工作流业务模型选择",
			pageId: "skyeyeClassFlowableChoose",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				$("#serviceClassName").val(skyeyeClassFlowableMation.serviceName);
				$("#serviceClassName").attr("className", skyeyeClassFlowableMation.className);
			}});
	});

	$("body").on("click", "#cancle", function() {
		parent.layer.close(index);
	});
});