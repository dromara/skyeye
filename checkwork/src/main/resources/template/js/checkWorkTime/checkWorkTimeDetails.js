
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
		 	url: flowableBasePath + "checkworktime003",
		 	params: {rowId: parent.rowId},
		 	pagination: false,
		 	method: "GET",
		 	template: $("#beanTemplate").html(),
		 	ajaxSendAfter:function (json) {
		 		var type = json.bean.type;
		 		if(type == 1){
					checkWorkUtil.resetSingleBreak();
		    	} else if (type == 2){
					checkWorkUtil.resetWeekend();
		    	} else if (type == 3){
					checkWorkUtil.resetSingleAndDoubleBreak();
		    	} else if (type == 4){
					checkWorkUtil.resetCustomizeDay(json.bean.days);
		    	}

		 		matchingLanguage();
		 		form.render();
		 	}
		});

	});
});