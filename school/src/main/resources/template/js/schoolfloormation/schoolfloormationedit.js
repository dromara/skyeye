
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$;
	    
	    showGrid({
		 	id: "showForm",
		 	url: schoolBasePath + "schoolfloormation003",
		 	params: {rowId:parent.rowId},
		 	pagination: false,
		 	template: getFileContent('tpl/schoolfloormation/schoolfloormationeditTemplate.tpl'),
		 	ajaxSendAfter:function (json) {
				// 获取当前登陆用户所属的学校列表
				schoolUtil.queryMyBelongSchoolList(function (data) {
					$("#schoolId").html(getDataUseHandlebars(getFileContent('tpl/template/select-option-must.tpl'), data));
					$("#schoolId").val(json.bean.schoolId);
					form.render("select");
				});

			    matchingLanguage();
		 	    form.on('submit(formEditBean)', function (data) {
		 	        if (winui.verifyForm(data.elem)) {
		 	        	var params = {
		 	        		rowId: parent.rowId,
		 	        		name: $("#typeName").val(),
		 	        		schoolId: $("#schoolId").val()
		 	        	};

		 	        	AjaxPostUtil.request({url:schoolBasePath + "schoolfloormation004", params: params, type: 'json', callback: function (json) {
							parent.layer.close(index);
							parent.refreshCode = '0';
		 	        	}});
		 	        }
		 	        return false;
		 	    });
		 	}
		});
	    
	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	});
});