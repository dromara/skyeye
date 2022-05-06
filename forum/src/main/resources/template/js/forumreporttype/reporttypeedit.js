
var childIcon = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'fileUpload'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$;
	    
	    //初始化数据
	    showGrid({
		 	id: "showForm",
		 	url: reqBasePath + "forumreporttype003",
		 	params: {rowId: parent.rowId},
		 	pagination: false,
		 	template: getFileContent('tpl/forumreporttype/reporttypeeditTemplate.tpl'),
		 	ajaxSendLoadBefore: function(hdb){
		 	},
		 	ajaxSendAfter:function(json){
		 		matchingLanguage();
		 		form.render();
		 	    form.on('submit(formEditMenu)', function (data) {
		 	        if (winui.verifyForm(data.elem)) {
		 	        	var params = {
	 	        			typeName: $("#typeName").val(),
	 	        			rowId: parent.rowId
		 	        	};
		 	        	AjaxPostUtil.request({url:reqBasePath + "forumreporttype004", params:params, type: 'json', callback: function(json){
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
 	    
 	    //取消
	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	    
	});
});