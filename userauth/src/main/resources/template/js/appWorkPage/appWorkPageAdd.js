
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

		$("#itemBox").html($("#folderTemplate").html());
		// 桌面信息
		systemCommonUtil.getSysDesttop(function (json) {
			$("#desktop").html(getDataUseHandlebars(selOption, json));
			form.render('select');
		});
		form.on('select(desktop)', function(data) {
			if ($("input[name='type']:checked").val() == 2) {
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
			}
		});

		form.on('radio(type)', function (data) {
			var val = data.value;
			if (val == 1) {
				// 目录
				$("#itemBox").html($("#folderTemplate").html());
			} else if (val == 2) {
				// 页面
				$("#itemBox").html($("#pageTemplate").html());
				// 初始化上传
				$("#logo").upload(systemCommonUtil.uploadCommon003Config('logo', 12, '', 1));
			}
			// 桌面信息
			systemCommonUtil.getSysDesttop(function (json) {
				$("#desktop").html(getDataUseHandlebars(selOption, json));
				form.render('select');
			});
			matchingLanguage();
			form.render();
		});


	    matchingLanguage();
 		form.render();
 	    form.on('submit(formAddBean)', function (data) {
 	        if (winui.verifyForm(data.elem)) {
				var type = $("input[name='type']:checked").val();
				var params;
				if (type == 1){
					params = {
						title: $("#title").val(),
						desktopId: $("#desktop").val(),
						parentId: '0',
						type: type
					};
				}else if (type == 2){
					params = {
						title: $("#title").val(),
						desktopId: $("#desktop").val(),
						parentId: $("#parentId").val(),
						type: type,
						urlType: $("input[name='urlType']:checked").val(),
						url: $("#url").val(),
						logo: $("#logo").find("input[name='upload']").attr("oldurl")
					};
					if (isNull(params.logo)) {
						winui.window.msg('请上传图片', {icon: 2, time: 2000});
						return false;
					}
				}
				AjaxPostUtil.request({url: reqBasePath + "writeAppWorkPageMation", params: params, type: 'json', callback: function (json) {
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