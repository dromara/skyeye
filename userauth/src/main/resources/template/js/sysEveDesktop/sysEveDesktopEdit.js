
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'fileUpload'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$;
	    
	    showGrid({
		 	id: "showForm",
		 	url: reqBasePath + "desktop006",
		 	params: {id: parent.rowId},
		 	pagination: false,
			method: "GET",
		 	template: $("#beanTemplate").html(),
		 	ajaxSendAfter:function (json) {
				$("input:radio[name=state][value=" + json.bean.state + "]").attr("checked", true);
				// 初始化上传
				$("#logo").upload(systemCommonUtil.uploadCommon003Config('logo', 12, json.bean.logo, 1));
		 		matchingLanguage();
				form.render();
		 	    form.on('submit(formEditBean)', function (data) {
		 	        if (winui.verifyForm(data.elem)) {
		 	        	var params = {
		 	        		id: parent.rowId,
		 	        		desktopName: $("#desktopName").val(),
		 	        		desktopCnName: $("#desktopCnName").val(),
							state: $("input[name='state']:checked").val(),
							appPageUrl: $("#appPageUrl").val(),
							logo: $("#logo").find("input[name='upload']").attr("oldurl")
		 	        	};
						if (isNull(params.logo)) {
							winui.window.msg('请上传图片', {icon: 2, time: 2000});
							return false;
						}
		 	        	AjaxPostUtil.request({url: reqBasePath + "writeSysEveDesktopMation", params: params, type: 'json', method: "POST", callback: function (json) {
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