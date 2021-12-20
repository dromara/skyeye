
var content = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'form'], function (exports) {
	
	winui.renderColor();
	
	var $ = layui.$,
		form = layui.form;
	var rowId = parent.rowId;
	
	// 为了iframe高度自适应
    var loadNum = 0;
    var interval = null;
	
	AjaxPostUtil.request({url:reqBasePath + "knowledgecontent006", params:{rowId: rowId}, type:'json', callback:function(json){
		if(json.returnCode == 0){
			$("#title").html(json.bean.title);
			content = json.bean.content;
	 		$("#knowledgecontentshowBox").attr("src", "contentshow.html");
	 		interval = setInterval(iFrameHeight, 300);
	 		matchingLanguage();
		}else{
			winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
		}
	}});
	
    function iFrameHeight() {
        var ifm = document.getElementById("knowledgecontentshowBox");
        var subWeb = document.frames ? document.frames["knowledgecontentshowBox"].document : ifm.contentDocument;
        if(ifm != null && subWeb != null) {
        	ifm.height = subWeb.body.scrollHeight + 50;
        	loadNum++;
        	if(loadNum > 10){
            	clearInterval(interval);//停止
        	}
        }
    }
	
    exports('knowledgePageShowDetails', {});
});
