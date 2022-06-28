
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
		 	url: reqBasePath + "maillist012",
		 	params: {rowId: parent.rowId},
		 	pagination: false,
		 	template: $("#beanTemplate").html(),
		 	ajaxSendLoadBefore: function(hdb){
		 	},
		 	ajaxSendAfter:function (json) {
		 		if(json.bean.category == 1){
		 			// 个人通讯录
		 			$("#typeIdBox").removeClass('layui-hide');
		 			$("#readonlyBox").addClass('layui-hide');
		 		}else if(json.bean.category == 2){
		 			// 公共通讯录
		 			$("#typeIdBox").addClass('layui-hide');
		 			$("#readonlyBox").removeClass('layui-hide');
		 		}
		 		matchingLanguage();
		 	}
	    });
	    
	});
});