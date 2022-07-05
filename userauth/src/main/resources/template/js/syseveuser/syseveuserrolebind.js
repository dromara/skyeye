
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$;
	    showGrid({
		 	id: "showForm",
		 	url: reqBasePath + "sys019",
		 	params: {rowId:parent.rowId},
		 	pagination: false,
		 	template: getFileContent('tpl/syseveuser/rolelist.tpl'),
		 	ajaxSendLoadBefore: function(hdb){},
		 	ajaxSendAfter:function (json) {
		 		matchingLanguage();
		 		form.render();
		 	    form.on('submit(formEditBindRole)', function (data) {
		 	        if (winui.verifyForm(data.elem)) {
		 	        	var str = "";
	 	        		$.each($('input[type=checkbox]:checked'),function(){
		                    str += $(this).val() + ",";
		                });
	 	        		if(isNull(str)){
	 	        			winui.window.msg("请选择角色", {icon: 2, time: 2000});
	 	        			return false;
	 	        		}
		 	        	var params = {
		 	        		rowId: parent.rowId,
		 	        		roleIds: str,
		 	        	};
		 	        	AjaxPostUtil.request({url: reqBasePath + "sys020", params: params, type: 'json', method: "POST", callback: function (json) {
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