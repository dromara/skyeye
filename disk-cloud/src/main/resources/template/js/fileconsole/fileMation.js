
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
		
		AjaxPostUtil.request({url:reqBasePath + "fileconsole031", params:{rowId: parent.shareId}, type:'json', callback:function(json){
			if(json.returnCode == 0){
				$("#fileName").html(json.bean.fileName);
				$("#fileType").html(json.bean.fileType);
				$("#createName").html(json.bean.createUserName);
				$("#createTime").html(json.bean.createTime);
				$("#fileSize").html(json.bean.fileSize);
				matchingLanguage();
				form.render();
			}else{
				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
			}
		}});
		
	});
});

