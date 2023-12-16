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
		var objectKey = GetUrlParam("objectKey");
		var objectId = GetUrlParam("objectId");
		var parentId = GetUrlParam("parentId");
		var parentType = GetUrlParam("parentType");
		var id = GetUrlParam("id");
		var type = null;
		if (!isNull(id)) {
			AjaxPostUtil.request({url: reqBasePath + "queryAuthPointById", params: {id: id}, type: 'json', method: 'GET', callback: function (json) {
				type = json.bean.type;
				parentId = json.bean.parentId;
				if (type == 1) {
					// 如果为1，则需要编辑权限点
					$("#showForm").html($("#authPointTemplate").html());
				} else if (type == 2) {
					// 如果为2，则需要编辑数据权限分组
					$("#showForm").html($("#groupTemplate").html());
				} else if (type == 3) {
					// 如果为3，写需要编辑数据权限
					$("#showForm").html($("#dataAuthPointTemplate").html());
					$("#orderBy").val(json.bean.orderBy);
				}
				$("#name").val(json.bean.name);
				$("#authMenu").val(json.bean.authMenu);
				matchingLanguage();
				form.render();
			}});
		} else {
			if (isNull(parentType)) {
				// 如果为空，则需要添加权限点
				parentId = "0";
				type = 1;
				$("#showForm").html($("#authPointTemplate").html());
			} else if (parentType == 1) {
				// 如果为1，则需要添加数据权限分组
				type = 2;
				$("#showForm").html($("#groupTemplate").html());
			} else if (parentType == 2) {
				// 如果为2，写需要添加数据权限
				type = 3;
				$("#showForm").html($("#dataAuthPointTemplate").html());
			}
			matchingLanguage();
			form.render();
		}

	    form.on('submit(formWriteBean)', function (data) {
	        if (winui.verifyForm(data.elem)) {
	        	var params = {
        			name: $("#name").val(),
        			authMenu: $("#authMenu").val(),
					parentId: parentId,
					objectId: objectId,
					objectKey: objectKey,
					orderBy: null,
					type: type,
					id: isNull(id) ? '' : id
	        	};

				if (params.type == 3) {
					params.orderBy = $("#orderBy").val()
				}
	        	AjaxPostUtil.request({url: reqBasePath + "writeAuthPoint", params: params, type: 'json', method: 'POST', callback: function (json) {
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