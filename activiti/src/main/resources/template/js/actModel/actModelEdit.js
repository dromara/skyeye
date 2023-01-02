
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
	    
	    showGrid({
		 	id: "showForm",
		 	url: flowableBasePath + "queryActModelMationById",
		 	params: {id: parent.rowId},
			method: 'GET',
		 	pagination: false,
		 	template: $("#beanTemplate").html(),
		 	ajaxSendAfter: function (json) {
				$("input:radio[name=commonUsed][value=" + json.bean.commonUsed + "]").attr("checked", true);

				// 加载图标信息
				systemCommonUtil.initEditIconChooseHtml('iconMation', form, colorpicker, 17, json.bean);

		 		textool.init({eleId: 'remark', maxlength: 200});
		 		
		        matchingLanguage();
		 		form.render();
		 	    form.on('submit(formEditBean)', function (data) {
		 	        if (winui.verifyForm(data.elem)) {
		 	        	var params = {
		 	        		id: parent.rowId,
		 	        		title: $("#title").val(),
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
		 	}
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