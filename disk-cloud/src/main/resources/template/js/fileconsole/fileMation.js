
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
		
		AjaxPostUtil.request({url: sysMainMation.diskCloudBasePath + "fileconsole031", params: {id: parent.shareId}, type: 'json', method: 'GET', callback: function (json) {
			$("#fileName").html(json.bean.name);
			$("#fileType").html(json.bean.type);
			$("#createName").html(json.bean.createName);
			$("#createTime").html(json.bean.createTime);
			$("#fileSize").html(json.bean.turnSize);
			matchingLanguage();
			form.render();
		}});
		
	});
});

