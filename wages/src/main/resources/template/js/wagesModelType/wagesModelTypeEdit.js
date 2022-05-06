
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
		 	url: reqBasePath + "wagesmodeltype003",
		 	params: {rowId: parent.rowId},
		 	pagination: false,
			method: "GET",
		 	template: $("#showBaseTemplate").html(),
		 	ajaxSendLoadBefore: function(hdb){
		 	},
		 	ajaxSendAfter:function(j){
				matchingLanguage();
				form.render();
		 	    form.on('submit(formEditBean)', function (data) {
		 	        if (winui.verifyForm(data.elem)) {
						var params = {
							nameCn: $("#nameCn").val(),
							nameEn: $("#nameEn").val(),
							rowId: parent.rowId
		 	        	};
		 	        	AjaxPostUtil.request({url:reqBasePath + "wagesmodeltype004", params: params, type: 'json', method: "PUT", callback: function(json){
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