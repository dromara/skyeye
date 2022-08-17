layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
		    form = layui.form;
	    
	    AjaxPostUtil.request({url: reqBasePath + "sysevemenuauthpoint003", params: {rowId: parent.rowId}, type: 'json', callback: function (json) {
			var type = json.bean.type;
			if (type == 1) {
				// 如果为1，则需要编辑权限点
				$("#showForm").html($("#authPointTemplate").html());
			} else if (type == 2) {
				// 如果为2，则需要编辑数据权限分组
				$("#showForm").html($("#groupTemplate").html());
			} else if (type == 3) {
				// 如果为3，写需要编辑数据权限
				$("#showForm").html($("#dataAuthPointTemplate").html());
			}
			$("#authMenuName").val(json.bean.authMenuName);
			$("#authMenu").val(json.bean.authMenu);
			matchingLanguage();
			form.render();
			form.on('submit(formEditBean)', function (data) {
				if (winui.verifyForm(data.elem)) {
					var params = {
						authMenuName: $("#authMenuName").val(),
						authMenu: $("#authMenu").val(),
						menuId: parent.menuId,
						parentId: json.bean.parentId,
						type: type,
						id: parent.rowId
					};
					AjaxPostUtil.request({url: reqBasePath + "writeSysEveMenuAuthPointMation", params: params, type: 'json', callback: function (json) {
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