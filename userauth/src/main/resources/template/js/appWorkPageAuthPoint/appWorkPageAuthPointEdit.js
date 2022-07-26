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
	    
	    AjaxPostUtil.request({url: reqBasePath + "appworkpageauthpoint003", params:{rowId: parent.rowId}, type: 'json', callback: function (json) {
			$("#authMenuName").val(json.bean.authMenuName);
			$("#authMenu").val(json.bean.authMenu);

			matchingLanguage();
			form.render();
			form.on('submit(formEditBean)', function (data) {
				if (winui.verifyForm(data.elem)) {
					var params = {
						authMenuName: $("#authMenuName").val(),
						authMenu: $("#authMenu").val(),
						rowId: parent.rowId,
						menuId: parent.menuId
					};
					AjaxPostUtil.request({url: reqBasePath + "appworkpageauthpoint004", params: params, type: 'json', callback: function (json) {
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