
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui'], function (exports) {
	var $ = layui.$;
	
	var beanTemplate = $('#beanTemplate').html();
    
	//为了iframe高度自适应
    var loadNum = 0;
    var interval = null;
	
    AjaxPostUtil.request({url:reqBasePath + "knowledgecontent014", params: {rowId:parent.rowId}, type:'json', callback:function(json){
		if(json.returnCode == 0){
			var str = getDataUseHandlebars(beanTemplate, json);
			$("#showForm").html(str);
			content = json.bean.content;
	 		$("#knowledgecontentshowBox").attr("src", "knowledgecontentshow.html");
	 		interval = setInterval(iFrameHeight, 300);

			var lavel = isNull(json.bean.label) ? [] : json.bean.label.split(',');
			var str = "";
			$.each(lavel, function(i, item){
				str += '<span class="layui-badge layui-bg-blue">' + item + '</span>';
			});
			$('#label').html(str);

	 		matchingLanguage();
		}else{
			winui.window.msg(j.returnMessage, {icon: 2,time: 2000});
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
	
    exports('knowledgeuncheckdetail', {});
});
