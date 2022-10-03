
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

	    // 初始化动态表单
	    initDsForm();
	    
	    textool.init({eleId: 'remark', maxlength: 200});

        showGrid({
            id: "actId",
            url: flowableBasePath + "activitimode009",
            params: {},
            pagination: false,
            template: getFileContent('tpl/template/select-option.tpl'),
            ajaxSendLoadBefore: function(hdb) {
            },
            ajaxSendAfter:function (json) {
                form.render('select');
            }
        });
        
        //页面类型变化事件
 		form.on('radio(pageTypes)', function (data) {
 			var val = data.value;
	    	if(val == '1'){//指定页面
	    		$(".TypeIsTwo").addClass("layui-hide");
	    		$(".TypeIsOne").removeClass("layui-hide");
	    	} else if (val == '2'){//动态表单
	    		$(".TypeIsTwo").removeClass("layui-hide");
	    		$(".TypeIsOne").addClass("layui-hide");
	    	} else {
	    		winui.window.msg('状态值错误', {icon: 2, time: 2000});
	    	}
        });
 		
 		//初始化动态表单
 		function initDsForm(){
 			showGrid({
 				id: "dsFormId",
 				url: flowableBasePath + "actmodletype020",
 				params: {},
 				pagination: false,
 				template: getFileContent('tpl/template/select-option-must.tpl'),
 				ajaxSendAfter: function (json) {
 					form.render('select');
 				}
 			})
 		}
 		
 		matchingLanguage();
 		form.render();
 	    form.on('submit(formAddBean)', function (data) {
 	        if (winui.verifyForm(data.elem)) {
 	        	var params = {
 	        		typeId: parent.rowId,
 	        		title: $("#typeName").val(),
 	        		actId: $("#actId").val(),
 	        		pageTypes: data.field.pageTypes,
 	        		tokenUrl: $("#tokenUrl").val(),
 	        		remark: $("#remark").val(),
					commonUsed: data.field.commonUsed
 	        	};
				if (params.pageTypes == 1) {
					params.addPageUrl = $("#pageUrl").val();
					params.editPageUrl = $("#editPageUrl").val();
					params.revokeMapping = $("#revokeMapping").val();
					if (isNull(params.pageUrl)) {
						winui.window.msg("请输入新增页面地址", {icon: 2, time: 2000});
						return false;
					}
					if (isNull(params.editPageUrl)) {
						winui.window.msg("请输入编辑页面地址", {icon: 2, time: 2000});
						return false;
					}
					if (isNull(params.revokeMapping)) {
						winui.window.msg("请输入撤销接口", {icon: 2, time: 2000});
						return false;
					}
					params.dsFormId = "";
				} else if (params.pageTypes == 2) {
					params.addPageUrl = "";
					params.editPageUrl = "";
					params.revokeMapping = "";
					params.dsFormId = $("#dsFormId").val();
					if (isNull(params.dsFormId)) {
						winui.window.msg("请选择表单页面", {icon: 2, time: 2000});
						return false;
					}
				}
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

	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	});
});