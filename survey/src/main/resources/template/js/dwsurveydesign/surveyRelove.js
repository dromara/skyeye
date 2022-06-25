
var url = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'ClipboardJS'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
	    	form = layui.form;
	    var rulYm = "";
	    if(reqBasePath.indexOf("localhost") != -1){
	    	rulYm = "http://127.0.0.1:8081/"
	    }else{
	    	rulYm = reqBasePath;
	    }
	    
	    //刷新页面参数
	    url = rulYm + 'tpl/dwsurveydesign/surveyTemplate.html?rowId=' + parent.rowId;
	    $("#pcAddress").val(url);
	    $("#copyUrl").attr("data-clipboard-text", url);
	    
		//加入分享js
		var content = "大家好，我刚刚通过skyeye制作了一份调查问卷【" + parent.surveyName + "】，需要您的帮助，您的意见非常重要。快快来帮忙回答。";
		var params = {
			text: content,
			url: url,
			pic: 'https://images.gitee.com/uploads/images/2018/1207/083137_48330589_1541735.jpeg'
		};
		$("#bdshare").attr("data", JSON.stringify(params));
		
		matchingLanguage();
		form.render();
		document.getElementById("bdshell_js").src = "http://bdimg.share.baidu.com/static/js/shell_v2.js?cdnversion=" + Math.ceil(new Date()/3600000);
		
		//打开链接
		$("body").on("click", "#openUrl", function(e){
			window.open(url);
		});
		
		//复制链接
		var clipboard = new ClipboardJS('#copyUrl');
		clipboard.on('success', function(e) {
			winui.window.msg("复制成功", {icon: 1, time: 2000});
		});
		clipboard.on('error', function(e) {
			winui.window.msg("浏览器不支持！", {icon: 2, time: 2000});
		});
		
	});
	    
});