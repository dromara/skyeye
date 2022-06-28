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

		var ue = ueEditorUtil.initEditor('content');
	    
	    matchingLanguage();
		form.render();
		
		//是否共享
 		form.on('switch(isShare)', function (data) {
 			//同步开关值
 			$(data.elem).val(data.elem.checked);
 		});
		
	    form.on('submit(formAddBean)', function (data) {
	    	
	        if (winui.verifyForm(data.elem)) {
	        	var params = {
        			projectName: $("#projectName").val(),
        			projectDesc: encodeURIComponent(ue.getContent())
	        	};
	        	if(data.field.isShare){
	        		params.isShare = '2';
	        	} else {
	        		params.isShare = '1';
	        	}
	        	
	        	AjaxPostUtil.request({url: reqBasePath + "planproject002", params: params, type: 'json', method: "POST", callback: function (json) {
	 	   			if (json.returnCode == 0) {
		 	   			parent.layer.close(index);
		 	        	parent.refreshCode = '0';
	 	   			} else {
	 	   				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
	 	   			}
	 	   		}});
	        }
	        return false;
	    });
	    
	    //取消
	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	});
	    
});