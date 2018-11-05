
var rowId = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).define(['table', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form', 'layedit'], function (form) {
		var index = parent.layer.getFrameIndex(window.name); //获取窗口索引
	    var $ = layui.$,
	    form = layui.form,
	    layedit = layui.layedit;
	    
	    var layContent;
	    
	    AjaxPostUtil.request({url:reqBasePath + "exexplaintocodemodel002", params:{}, type:'json', callback:function(json){
   			if(json.returnCode == 0){
   				if(!isNull(json.bean)){
   					rowId = json.bean.id;
   					$("#title").val(json.bean.title);
   					$("#content").val(json.bean.content);
   				}
   				layContent = layedit.build('content', {
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
   			}else{
   				top.winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
   			}
   		}});
	    
		form.render();
		
	    form.on('submit(formAddBean)', function (data) {
	    	//表单验证
	        if (winui.verifyForm(data.elem)) {
	        	if(isNull(layedit.getContent(layContent))){
	        		top.winui.window.msg('请输入内容。', {icon: 2,time: 2000});
	        	}else{
	        		var params = {
        				title: $("#title").val(),
        				content: encodeURI(layedit.getContent(layContent)),
	        		};
	        		if(isNull(rowId)){
	        			AjaxPostUtil.request({url:reqBasePath + "exexplaintocodemodel001", params:params, type:'json', callback:function(json){
	        				if(json.returnCode == 0){
	        					rowId = json.bean.id;
	        					top.winui.window.msg('保存成功', {icon: 1,time: 2000});
	        				}else{
	        					top.winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
	        				}
	        			}});
	        		}else{
	        			params.rowId = rowId;
	        			AjaxPostUtil.request({url:reqBasePath + "exexplaintocodemodel003", params:params, type:'json', callback:function(json){
	        				if(json.returnCode == 0){
	        					top.winui.window.msg('保存成功', {icon: 1,time: 2000});
	        				}else{
	        					top.winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
	        				}
	        			}});
	        		}
	        	}
	        }
	        return false;
	    });
	    
	});
	    
});