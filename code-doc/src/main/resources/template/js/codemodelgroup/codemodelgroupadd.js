
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form', 'layedit'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
		    form = layui.form,
		    layedit = layui.layedit;
	    
	    var layContent = layedit.build('groupDesc', {
	    	tool: [
	    	       'strong' //加粗
	    	       ,'italic' //斜体
	    	       ,'underline' //下划线
	    	       ,'del' //删除线
	    	       ,'|' //分割线
	    	       ,'left' //左对齐
	    	       ,'center' //居中对齐
	    	       ,'right' //右对齐
	    	       ,'link' //超链接
	    	       ,'unlink' //清除链接
	    	       ,'face' //表情
	    	     ]
	    });
	    
	    matchingLanguage();
		form.render();
	    form.on('submit(formAddBean)', function (data) {
	        if (winui.verifyForm(data.elem)) {
	        	var params = {
        			groupName: $("#groupName").val(),
        			groupDesc: encodeURIComponent(layedit.getContent(layContent)),
	        	};
	        	AjaxPostUtil.request({url:reqBasePath + "codemodel002", params:params, type: 'json', callback: function(json){
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
	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	    
	});
	    
});