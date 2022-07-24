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

		// 所属父级节点类型
		var parentType = parent.parentType;
		// 所属父级节点id
		var parentId = parent.parentId;
		// 所属菜单id
		var menuId = parent.menuId;

		if (isNull(parentType)) {
			// 如果为空，则需要添加权限点
			$("#showForm").html($("#authPointTemplate").html());
		} else if (parentType == 1) {
			// 如果为1，则需要添加数据权限分组
			$("#showForm").html($("#groupTemplate").html());
		} else if (parentType == 2) {
			// 如果为2，写需要添加数据权限
			$("#showForm").html($("#dataAuthPointTemplate").html());
		}

	    matchingLanguage();
	    form.render();
	    form.on('submit(formAddBean)', function (data) {
	        if (winui.verifyForm(data.elem)) {
	        	var params = {
        			authMenuName: $("#authMenuName").val(),
        			authMenu: $("#authMenu").val(),
        			menuId: menuId,
					parentId: parentId
	        	};
				if (isNull(parentType)) {
					// 如果为空，则需要添加权限点
					params.type = 1;
				} else if (parentType == 1) {
					// 如果为1，则需要添加数据权限分组
					params.type = 2;
				} else if (parentType == 2) {
					// 如果为2，写需要添加数据权限
					params.type = 3;
				}
	        	AjaxPostUtil.request({url: reqBasePath + "writeSysEveMenuAuthPointMation", params: params, type: 'json', callback: function (json) {
					parent.layer.close(index);
					parent.refreshCode = '0';
	 	   		}});
	        }
	        return false;
	    });
	    
	    // 取消
	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	});
});