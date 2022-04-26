

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'cookie'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var $ = layui.$;
		form.render();

		jsGetJsonFile("../../configRation.json", function(data){
			sysMainMation = data;
			localStorage.setItem("sysMainMation", JSON.stringify(sysMainMation));
		});

		// 主动发起多语言文件加载
        jsGetJsonFile("../../json/language.json", function(data) {
            systemLanguage = data;
            localStorage.setItem("systemLanguage", JSON.stringify(data));
        });

		// 主动发起系统单据类型文件加载
		jsGetJsonFile("../../json/sysOrderType.json", function(data) {
			systemOrderType = data;
			localStorage.setItem("systemOrderType", JSON.stringify(data));
		});

		// 主动发起动态表单关联json文件加载
		jsGetJsonFile("../../json/sysDsFormWithCodeType.json", function(data) {
			sysDsFormWithCodeType = data;
			localStorage.setItem("sysDsFormWithCodeType", JSON.stringify(data));
		});

		// 主动发起工作流流程模型关联json文件加载
		jsGetJsonFile("../../json/activitiNameKey.json", function(data) {
			sysActivitiModel = data;
			localStorage.setItem("sysActivitiModel", JSON.stringify(data));
		});
		
		// 是否登陆中，默认否
		var isLogin = false;
		
		// 根据url参数判断登陆成功后跳转到哪个页面
		var url = unescape(GetUrlParam("url"));
		
		// 修改title
		$(document).attr("title", sysMainMation.mationTitle);
		// 修改版权
		$(".copyright-content").html(sysMainMation.copyright);
		
		$(".lock-body").css({'background-image': 'url("/images/upload/winbgpic/default.jpg")'});
		
		$.cookie('userToken', "", {path: '/' });

		matchingLanguage();
		form.on('submit(login)', function (data) {
 	        if (winui.verifyForm(data.elem)) {
 	        	$("#loginBtn").find("i").attr("class", "fa fa-spin fa-spinner fa-fw");
 	        	if(!isLogin){
	 	        	if(isNull($("#userCode").val())){
	 	        		top.winui.window.msg("请输入用户名", {icon: 2,time: 2000});
	 	        	}else if(isNull($("#password").val())){
	 	        		top.winui.window.msg("请输入密码", {icon: 2,time: 2000});
	 	        	}else{
	 	        		var params = {
	 	        			userCode:$("#userCode").val(),
	 	        			password:$("#password").val()
	 	        		};
	 	        		isLogin = true;
	 	        		AjaxPostUtil.request({url:reqBasePath + "login001", params:params, type: 'json', callback: function(json){
	 	        			isLogin = false;
 	        				$("#loginBtn").find("i").attr("class", "fa fa-arrow-right");
	 		 	   			if(json.returnCode == 0){
	 		 	   				if(json.rows != null){
			 	   					localStorage.setItem("authpoints", JSON.stringify(json.rows));
			 	   				}
	 		 	   				$.cookie('userToken', json.bean.userToken, {path: '/' });
	 		 	   				if(!isNull(json.bean.id)){
	 		 	   					if(checkURL(url)){
			 	   						location.href = url;
			 	   					}else{
			 	   						location.href = "index.html";
			 	   					}
	 		 	   				}
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
	
	exports('login', {});
});