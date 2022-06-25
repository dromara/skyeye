
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
	    
	    $("#content").val(parent.ueEditObj);
	    
	    var layContent = layedit.build('content', {
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
	        	if(isNull(layedit.getContent(layContent))){
	        		winui.window.msg('请输入内容。', {icon: 2, time: 2000});
	        	}else{
	        		parent.ueEditObj = layedit.getContent(layContent);
	        		parent.layer.close(index);
	 	        	parent.refreshCode = '0';
	        	}
	        }
	        return false;
	    });
	});
});