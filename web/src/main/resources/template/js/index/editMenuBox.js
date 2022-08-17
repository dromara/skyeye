layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window',
}).define(['window', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
	    	form = layui.form;
	    
	    AjaxPostUtil.request({url: reqBasePath + "sysevewindragdrop006", params: {rowId: parent.parentRowId}, type: 'json', callback: function (json) {
			$("#menuBoxName").val(json.bean.menuBoxName);
			matchingLanguage();
			form.on('submit(formEditBean)', function (data) {
				if (winui.verifyForm(data.elem)) {
					var params = {
						menuBoxName: $("#menuBoxName").val(),
						rowId: parent.parentRowId
					};

					AjaxPostUtil.request({url: reqBasePath + "sysevewindragdrop007", params: params, type: 'json', callback: function (json) {
						parent.childParams = params;
						parent.layer.close(index);
						parent.refreshCode = '0';
					}});
				}
				return false;
			});
   		}});
	    
	    //取消
	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	});
	    
});