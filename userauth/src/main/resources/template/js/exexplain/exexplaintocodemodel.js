
var rowId = "";

var type = 1;

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
		    form = layui.form;

		var ue
	    AjaxPostUtil.request({url:reqBasePath + "exexplain002", params: {type: type}, type: 'json', callback: function(json){
   			if(json.returnCode == 0){
   				if(!isNull(json.bean)){
   					rowId = json.bean.id;
   					$("#title").val(json.bean.title);
   					$("#content").val(json.bean.content);
   				}
				ue = ueEditorUtil.initEditor('container');
				ue.addListener("ready", function () {
					ue.setContent(json.bean.content);
				});
   			    matchingLanguage();
   			}else{
   				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
   			}
   		}});
	    
		form.render();
	    form.on('submit(formAddBean)', function (data) {
	        if (winui.verifyForm(data.elem)) {
	        	if(isNull(ue.getContent())){
	        		winui.window.msg('请输入内容。', {icon: 2,time: 2000});
	        	}else{
	        		var params = {
        				title: $("#title").val(),
        				content: encodeURIComponent(ue.getContent()),
						type: type
	        		};
	        		if(isNull(rowId)){
	        			AjaxPostUtil.request({url:reqBasePath + "exexplain001", params:params, type: 'json', callback: function(json){
	        				if(json.returnCode == 0){
	        					rowId = json.bean.id;
	        					winui.window.msg(systemLanguage["com.skyeye.addOperationSuccessMsg"][languageType], {icon: 1,time: 2000});
	        				}else{
	        					winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
	        				}
	        			}});
	        		}else{
	        			params.rowId = rowId;
	        			AjaxPostUtil.request({url:reqBasePath + "exexplain003", params:params, type: 'json', callback: function(json){
	        				if(json.returnCode == 0){
	        					winui.window.msg(systemLanguage["com.skyeye.addOperationSuccessMsg"][languageType], {icon: 1,time: 2000});
	        				}else{
	        					winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
	        				}
	        			}});
	        		}
	        	}
	        }
	        return false;
	    });
	    
	});
});