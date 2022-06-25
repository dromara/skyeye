
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'ClipboardJS'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
		var $ = layui.$;
		
		matchingLanguage();
		form.render();
	    
		var clipboard;//复制对象
		
		var shareType = '2';
		form.on('radio(shareType)', function (data) {
			shareType = data.value;
			if(shareType == '2'){
				$("#yes").show();
				$("#no").hide();
			} else {
				$("#yes").hide();
				$("#no").show();
			}
        });
		
		//分享
		$("body").on("click", "#createShareUrl", function(){
			var params = {
				rowId: parent.shareId,
				shareType: shareType
			};
			AjaxPostUtil.request({url:reqBasePath + "fileconsole016", params: params, type: 'json', callback: function(json){
	   			if (json.returnCode == 0) {
	   				json.bean.shareUrl = reqBasePath + json.bean.shareUrl;
	   				var str = "链接：" + json.bean.shareUrl;
	   				if(json.bean.shareType === '1'){//无提取码
	   					json.bean.shareType = "none";
	   					json.bean.btnName = "复制链接";
	   				} else {//有提取码
	   					json.bean.shareType = "block";
	   					json.bean.btnName = "复制链接及提取码";
	   					str += "   提取码：" + json.bean.sharePassword;
	   				}
	   				var html = getDataUseHandlebars(getFileContent('tpl/fileconsole/shareFileTemplate.tpl'), json);
	   				$("#shareDiv").html(html);
	   				$("#copyBtn").attr("data-clipboard-text", str);
	   				form.render();
	   				
	   				//复制
	   				clipboard = new ClipboardJS('#copyBtn');
	   				clipboard.on('success', function(e) {
	   					winui.window.msg("复制成功", {icon: 1, time: 2000});
	   				});
	   				clipboard.on('error', function(e) {
	   					winui.window.msg("浏览器不支持！", {icon: 2, time: 2000});
	   				});
	   			} else {
	   				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
	   			}
	   		}});
	    });
		
		//取消
	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	    
	});
});

