
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'fileUpload'], function (exports) {
	winui.renderColor();
	layui.use(['form', 'textool'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
		    form = layui.form,
			textool = layui.textool;
	    
	    AjaxPostUtil.request({url: reqBasePath + "sysevewin003", params: {id: parent.rowId}, type: 'json', method: "GET", callback: function (json) {
			$("#sysName").val(json.bean.sysName);
			$("#sysUrl").val(json.bean.sysUrl);
			$("#content").val(json.bean.content);
			textool.init({eleId: 'content', maxlength: 200});
		}});
   		matchingLanguage();
		form.render();
	    form.on('submit(formEditBean)', function (data) {
	        if (winui.verifyForm(data.elem)) {
        		var params = {
					sysName: $("#sysName").val(),
					content: $("#content").val(),
					sysUrl: $("#sysUrl").val(),
    				id: parent.rowId,
        		};
    			AjaxPostUtil.request({url: reqBasePath + "writeSysEveWinMation", params: params, type: 'json', method: "POST", callback: function (json) {
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