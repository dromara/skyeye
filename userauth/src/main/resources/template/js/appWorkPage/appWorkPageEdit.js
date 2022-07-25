
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

		AjaxPostUtil.request({url: reqBasePath + "appworkpage005", params: {rowId: parent.rowId}, type: 'json', method: 'GET', callback: function (json) {
			$("#title").val(json.bean.title);
			var type = json.bean.type;
			if (type == 1) {
				// 目录
				$("#typeName").html('目录');
				$("#itemBox").html($("#folderTemplate").html());
			} else if (type == 2) {
				// 页面
				$("#typeName").html('页面');
				$("#itemBox").html($("#pageTemplate").html());
				$("#url").val(json.bean.url);
				showGrid({
					id: "parentId",
					url: reqBasePath + "queryAppWorkPageListByParentId",
					params: {parentId: '0', desktopId: json.bean.desktopId},
					pagination: false,
					method: "GET",
					template: selOption,
					ajaxSendLoadBefore: function (hdb) {},
					ajaxSendAfter: function (data) {
						$("#parentId").val(json.bean.parentId);
						form.render('select');
					}
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
				$("input:radio[name=urlType][value=" + json.bean.urlType + "]").attr("checked", true);
				// 初始化上传
				$("#logo").upload(systemCommonUtil.uploadCommon003Config('logo', 12, json.bean.logo, 1));
			}

			// 桌面信息
			systemCommonUtil.getSysDesttop(function (data) {
				$("#desktop").html(getDataUseHandlebars(selOption, data));
				$("#desktop").val(json.bean.desktopId);
				form.render('select');
			});

			matchingLanguage();
			form.render();
			form.on('submit(formEditMenu)', function (data) {
				if (winui.verifyForm(data.elem)) {
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
					params.id = parent.rowId;
					AjaxPostUtil.request({url: reqBasePath + "writeAppWorkPageMation", params: params, type: 'json', callback: function (json) {
						parent.layer.close(index);
						parent.refreshCode = '0';
					}});
				}
				return false;
			});
		}});

 	    // 取消
	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	    
	});
});