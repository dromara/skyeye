

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({  //指定js别名
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	
	layui.use(['form'], function (form) {
		var $ = layui.$;
		form.render();
		
		form.on('submit(login)', function (data) {
			//表单验证
 	        if (winui.verifyForm(data.elem)) {
 	        	if(isNull($("#userCode").val())){
 	        		top.winui.window.msg("请输入用户名", {icon: 2,time: 2000});
 	        	}else if(isNull($("#password").val())){
 	        		top.winui.window.msg("请输入密码", {icon: 2,time: 2000});
 	        	}else{
 	        		var params = {
 	        			userCode:$("#userCode").val(),
 	        			password:$("#password").val()
 	        		};
 	        		AjaxPostUtil.request({url:reqBasePath + "login001", params:params, type:'json', callback:function(json){
 		 	   			if(json.returnCode == 0){
 			 	   			location.href = "index.html";
 		 	   			}else{
 		 	   				top.winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
 		 	   			}
 		 	   		}});
 	        	}
 	        }
			return false;
		});
		
	});
	
	exports('login', {});
});