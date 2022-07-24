
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window',
}).define(['window', 'jquery', 'winui', 'fileUpload'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$;
		var selOption = getFileContent('tpl/template/select-option.tpl');

		// 桌面信息
		systemCommonUtil.getSysDesttop(function (json) {
			$("#desktop").html(getDataUseHandlebars(selOption, json));
			form.render('select');
		});
		form.on('select(desktop)', function(data) {
			var value = data.value;
			if (isNull(value)) {
				$("#parentId").html("");
				form.render('select');
				return;
			}
			showGrid({
				id: "parentId",
				url: reqBasePath + "queryAppWorkPageListByParentId",
				params: {parentId: '0', desktopId: value},
				pagination: false,
				method: "GET",
				template: selOption,
				ajaxSendLoadBefore: function (hdb) {},
				ajaxSendAfter: function (json) {
					form.render('select');
				}
			});
		});

		// 初始化上传
		$("#logo").upload(systemCommonUtil.uploadCommon003Config('logo', 12, '', 1));

	    matchingLanguage();
 		form.render();
 	    form.on('submit(formAddBean)', function (data) {
 	        if (winui.verifyForm(data.elem)) {
 	        	var params = {
        			title: $("#title").val(),
        			url: $("#url").val(),
					parentId: $("#parentId").val(),
					urlType: data.field.urlType
 	        	};
 	        	params.logo = $("#logo").find("input[name='upload']").attr("oldurl");
 	        	AjaxPostUtil.request({url: reqBasePath + "appworkpage004", params: params, type: 'json', callback: function (json) {
					parent.layer.close(index);
					parent.refreshCode = '0';
					parent.chooseId = params.parentId;//将选中的目录id传给父页面
	 	   		}});
 	        }
 	        return false;
 	    });
 	    
	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	});
});