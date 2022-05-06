
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
		 	url: reqBasePath + "sysevemodeltype004",
		 	params: {id: parent.rowId},
		 	pagination: false,
			method: "GET",
		 	template: $("#showTemplate").html(),
		 	ajaxSendAfter: function(json){

				systemModelUtil.loadSysEveModelTypeByPId('parentId', '0');
				$("#parentId").val(json.bean.parentId);

		 		matchingLanguage();
		 	    form.on('submit(formEditBean)', function (data) {
		 	        if (winui.verifyForm(data.elem)) {
		 	        	var params = {
							id: parent.rowId,
							typeName: $("#typeName").val(),
							parentId: isNull($("#parentId").val()) ? "0" : $("#parentId").val()
		 	        	};
		 	        	AjaxPostUtil.request({url:reqBasePath + "sysevemodeltype005", params:params, type: 'json', method: "PUT", callback: function(json){
		 	        		if(json.returnCode == 0){
		 	        			parent.layer.close(index);
		 	        			parent.refreshCode = '0';
		 	        		}else{
		 	        			winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
		 	        		}
		 	        	}});
		 	        }
		 	        return false;
		 	    });
		 	}
		});
	    
	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	});
});