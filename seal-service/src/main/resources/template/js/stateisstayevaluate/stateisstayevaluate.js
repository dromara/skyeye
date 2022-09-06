
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
		 	url: flowableBasePath + "sealseservice036",
		 	params: {rowId: parent.rowId},
		 	pagination: false,
		 	template: $("#beanTemplate").html(),
		 	ajaxSendLoadBefore: function(hdb) {},
		 	ajaxSendAfter: function (json) {

				// 售后服务类型
				sysDictDataUtil.showDictDataListByDictTypeCode(sysDictData["amsServiceEvaluateType"]["key"], 'select', "evaluateType", '', form);

				matchingLanguage();
		 		form.render();
		 	    form.on('submit(evaluate)', function (data) {
		 	        if (winui.verifyForm(data.elem)) {
		 	        	var params = {
		        			rowId: parent.rowId,
		        			evaluateType: $("#evaluateType").val(),
		        			content: $("#content").val()
		 	        	};
	 	 	        	AjaxPostUtil.request({url: flowableBasePath + "sealseservice037", params: params, type: 'json', callback: function (json) {
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