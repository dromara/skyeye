
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'cookie'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
		var $ = layui.$;
		
		var rowId = GetUrlParam("id");
	    
		AjaxPostUtil.request({url: reqBasePath + "fileconsole019", params: {rowId: rowId}, type: 'json', callback: function (json) {
			if(isNull(json.bean)){
				$("#showForm").hide();
				$("#showFormNone").show();
			} else {
				if(json.bean.shareType == 2){//私密分享
					$("#showFormNone").hide();
					$("#showForm").show();
					$("#userName").html(json.bean.userName);
					$("#userPhoto").attr("src", json.bean.userPhoto);
					$("#userPhoto").attr("alt", json.bean.userName);
				}else if(json.bean.shareType == 1){//公开分享--跳转列表页面
					location.href = "../../tpl/shareFile/shareFileList.html?id=" + rowId;
				}
			}

			matchingLanguage();
			form.render();
   		}});
		
		$("body").on("click", "#tqShareFile", function (e) {
			if(isNull($("#sharePassword").val())){
				winui.window.msg("请输入提取码", {icon: 2, time: 2000});
				return;
			}
			AjaxPostUtil.request({url: reqBasePath + "fileconsole020", params: {rowId: rowId, sharePassword: $("#sharePassword").val()}, type: 'json', callback: function (json) {
				if(isNull(json.bean)){
					$("#showForm").hide();
					$("#showFormNone").show();
				} else {//跳转列表页面
					$.cookie("file" + rowId, $("#sharePassword").val(), {path: '/' });
					location.href = "../../tpl/shareFile/shareFileList.html?id=" + rowId;
				}
	   		}});
		});
		
	});
});

