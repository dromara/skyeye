
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
		 	url: reqBasePath + "sensitiveword004",
		 	params: {rowId:parent.rowId},
		 	pagination: false,
		 	template: getFileContent('tpl/forumsensitivewords/forumsensitivewordseditTemplate.tpl'),
		 	ajaxSendAfter:function(json){
		 		matchingLanguage();
		 	    form.on('submit(formEditBean)', function (data) {
		 	        if (winui.verifyForm(data.elem)) {
		 	        	var params = {
		 	        		rowId: parent.rowId,
		 	        		sensitiveWord: $("#sensitiveWord").val(),
		 	        	};
		 	        	AjaxPostUtil.request({url:reqBasePath + "sensitiveword005", params:params, type:'json', callback:function(json){
		 	        		if(json.returnCode == 0){
		 	        			parent.layer.close(index);
		 	        			parent.refreshCode = '0';
		 	        		}else{
		 	        			winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
		 	        		}
		 	        	}});
		 	        }
		 	        return false;
		 	    });
		 	}
		});
	    
	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	});
});