
var actFlowMation = {};

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'colorpicker', 'textool', 'fileUpload'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$;
	    var colorpicker = layui.colorpicker,
	    	textool = layui.textool;

		// 加载图标信息
		systemCommonUtil.initIconChooseHtml('iconMation', form, colorpicker, 17);

	    textool.init({eleId: 'remark', maxlength: 200});

 		matchingLanguage();
 		form.render();
 	    form.on('submit(formAddBean)', function (data) {
 	        if (winui.verifyForm(data.elem)) {
 	        	var params = {
 	        		typeId: parent.rowId,
 	        		title: $("#typeName").val(),
 	        		tokenUrl: $("#tokenUrl").val(),
 	        		remark: $("#remark").val(),
					commonUsed: data.field.commonUsed,
					actFlowId: $("#actFlowId").attr("actFlowId"),
					addPageUrl: $("#addPageUrl").val(),
					editPageUrl: $("#editPageUrl").val(),
					revokeMapping: $("#revokeMapping").val()
 	        	};
				// 获取图标信息
				params = systemCommonUtil.getIconChoose(params);
				if (!params["iconChooseResult"]) {
					return false;
				}

 	        	AjaxPostUtil.request({url: flowableBasePath + "writeActModelMation", params: params, type: 'json', method: 'POST', callback: function (json) {
					parent.layer.close(index);
					parent.refreshCode = '0';
 	        	}});
 	        }
 	        return false;
 	    });

		// 工作流模型选择
		$("body").on("click", "#actFlowIdSel", function (e) {
			_openNewWindows({
				url: "../../tpl/actFlow/actFlowChoose.html",
				title: "工作流模型选择",
				pageId: "actFlowChoose",
				area: ['90vw', '90vh'],
				callBack: function (refreshCode) {
					$("#actFlowId").val(actFlowMation.flowName);
					$("#actFlowId").attr("actFlowId", actFlowMation.id);
				}});
		});

	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	});
});