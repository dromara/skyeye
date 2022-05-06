layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
	    	form = layui.form;
	    
		showGrid({
		 	id: "showForm",
		 	url: reqBasePath + "sysevewinthemecolor004",
		 	params: {rowId: parent.rowId},
		 	pagination: false,
		 	template: getFileContent('tpl/sysevewinthemecolor/sysevewinthemecoloreditTemplate.tpl'),
		 	ajaxSendLoadBefore: function(hdb){
		 	},
		 	ajaxSendAfter:function(json){
		 		matchingLanguage();
		 		form.render();
		 		form.on('submit(formEditBean)', function (data) {
			        if (winui.verifyForm(data.elem)) {
			        	var params = {
		        			colorClass:$("#colorClass").val(),
		        			rowId:parent.rowId
			        	};
			        	AjaxPostUtil.request({url:reqBasePath + "sysevewinthemecolor005", params:params, type: 'json', callback: function(json){
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
		
		$("body").on("keyup paste", "#colorClass", function(){
	    	$("#modelEffect").attr("class", $(this).val());
	    });
		
	    //取消
	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	});
});