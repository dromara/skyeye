
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
		 	url: reqBasePath + "notice012",
		 	params: {rowId:parent.rowId},
		 	pagination: false,
		 	template: getFileContent('tpl/sysnotice/sysnoticedetailsTemplate.tpl'),
		 	ajaxSendAfter:function (json) {
		 		if(json.bean.sendType == "群发所有人"){
		 			$("#sendTo").hide();
		 		}
		 		if(json.bean.timeSend != "设置"){
		 			$("#sendTime").hide();
		 		}
		 		if(json.bean.state == "新建"){
		 			$("#stateUp").hide();
		 		}
		 		matchingLanguage();
		 	}
		});
	});
});