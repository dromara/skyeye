
var content = "";

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
	    
	    var beanTemplate = $('#beanTemplate').html();
	    
	    //为了iframe高度自适应
	    var loadNum = 0;
	    var interval = null;
	    
	    AjaxPostUtil.request({url: sysMainMation.knowlgBasePath + "knowledgecontent011", params: {rowId: parent.rowId}, type: 'json', callback: function (json) {
			var str = getDataUseHandlebars(beanTemplate, json);
			$("#checkDetails").html(str);
			content = json.bean.content;
			$("#knowledgecontentshowBox").attr("src", "knowledgecontentshow.html");
			interval = setInterval(iFrameHeight, 300);
			matchingLanguage();
		}});
		
		function iFrameHeight() {
	        var ifm = document.getElementById("knowledgecontentshowBox");
	        var subWeb = document.frames ? document.frames["knowledgecontentshowBox"].document : ifm.contentDocument;
	        if(ifm != null && subWeb != null) {
	        	ifm.height = subWeb.body.scrollHeight;
	        	loadNum++;
	        	if(loadNum > 10){
	            	clearInterval(interval);//停止
	        	}
	        }
	    }
		 		
 		//单选框变化事件
 		form.on('radio(examineState)', function (data) {
 			var val = data.value;
 			if(val == '2'){//审核通过
 				$("#reasonHide").addClass("layui-hide");
 				$("#examineNopassReason").val("");
 			} else if (val == '3'){//审核不通过
 				$("#reasonHide").removeClass("layui-hide");
 			} else {
 				winui.window.msg('状态值错误', {icon: 2, time: 2000});
 			}
 		});
 		
 		form.render();
 		form.on('submit(formAddBean)', function (data) {
 			if (winui.verifyForm(data.elem)) {
 				var params = {
					rowId: parent.rowId,
					examineState: data.field.examineState,
					examineNopassReason: $("#examineNopassReason").val()
 				};
 				if(params.examineState == '3'){
 					if(isNull(params.examineNopassReason)){
 						winui.window.msg("请填写审核不通过原因", {icon: 2, time: 2000});
 						return false;
 					}
 				}
 				AjaxPostUtil.request({url: sysMainMation.knowlgBasePath + "knowledgecontent012", params: params, type: 'json', callback: function (json) {
					parent.layer.close(index);
					parent.refreshCode = '0';
 				}});
 			}
 			return false;
 		});
		 	
	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	});
});