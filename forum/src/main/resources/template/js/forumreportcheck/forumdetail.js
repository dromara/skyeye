
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	var $ = layui.$;
	
	//帖子信息展示
	AjaxPostUtil.request({url: reqBasePath + "forumcontent006", params: {rowId:parent.forumId}, type: 'json', callback: function (json) {
		$("#content").html(json.bean.content);
		$("#title").html(json.bean.title);
		$("#createTime").html(json.bean.createTime);
		$("#photo").html("<img userId=" + json.bean.userId + " alt='' src=" + json.bean.userPhoto + ">");
		matchingLanguage();
	}});
	
    exports('forumdetail', {});
});
