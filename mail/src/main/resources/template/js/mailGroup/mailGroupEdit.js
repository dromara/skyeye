
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
	    
	    AjaxPostUtil.request({url: sysMainMation.mailBasePath + "mailGroup004", params:{rowId: parent.rowId}, type: 'json', callback: function (json) {
			$("#name").val(json.bean.name);
			$("#desc").val(json.bean.desc);

			matchingLanguage();
			form.render();
			form.on('submit(formAddBean)', function (data) {
				if (winui.verifyForm(data.elem)) {
					var params = {
						name: $("#name").val(),
						desc: $("#desc").val(),
						rowId: parent.rowId
					};
					AjaxPostUtil.request({url: sysMainMation.mailBasePath + "mailGroup005", params: params, type: 'json', callback: function (json) {
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