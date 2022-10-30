
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'textool'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
	    	textool = layui.textool;
	    
	    showGrid({
		 	id: "showForm",
		 	url: flowableBasePath + "materialcategory004",
		 	params: {id: parent.rowId},
		 	pagination: false,
			method: "GET",
		 	template: $("#beanTemplate").html(),
		 	ajaxSendAfter:function (json) {
		 		
		 		textool.init({eleId: 'remark', maxlength: 200});
		 		
			    matchingLanguage();
				form.render();
		 	    form.on('submit(formEditBean)', function (data) {
		 	        if (winui.verifyForm(data.elem)) {
		 	        	var params = {
		 	        		id: parent.rowId,
		 	        		name: $("#typeName").val(),
		 	        		remark: $("#remark").val(),
							parentId: json.bean.parentId
		 	        	};
		 	        	AjaxPostUtil.request({url: flowableBasePath + "writeMaterialCategoryMation", params: params, type: 'json', method: "POST", callback: function (json) {
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