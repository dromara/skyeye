

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'cookie'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var $ = layui.$;
		env = GetUrlParam("env");

		// 系统配置文件
		jsGetJsonFile("../../configRation.json?env=" + env, function(data) {
			sysMainMation = data;
			$(".lock-body").css({'background-image': 'url("' + sysMainMation.fileBasePath + '/images/upload/winbgpic/default.jpg")'});
			localStorage.setItem("sysMainMation", JSON.stringify(sysMainMation));
		});

		// 多语言文件加载
        jsGetJsonFile("../../json/language.json", function(data) {
            systemLanguage = data;
            localStorage.setItem("systemLanguage", JSON.stringify(data));
        });

		// 系统模型关联json文件加载
		jsGetJsonFile("../../json/sysServiceMation.json", function(data) {
			sysServiceMation = data;
			localStorage.setItem("sysServiceMation", JSON.stringify(data));
		});

		// 数据字典关联json文件加载
		jsGetJsonFile("../../json/sysDictData.json", function(data) {
			sysDictData = data;
			localStorage.setItem("sysDictData", JSON.stringify(data));
		});

		// 枚举类关联json文件
		jsGetJsonFile("../../json/skyeyeClassEnum.json", function(data) {
			skyeyeClassEnum = data;
			localStorage.setItem("skyeyeClassEnum", JSON.stringify(data));
		});
		
		// 是否登陆中，默认否
		var isLogin = false;
		
		// 根据url参数判断登陆成功后跳转到哪个页面
		var url = unescape(GetUrlParam("url"));
		
		// 修改title
		$(document).attr("title", sysMainMation.mationTitle);
		// 修改版权
		$(".copyright-content").html(sysMainMation.copyright);
		
		localStorage.setItem('userToken', "");

		matchingLanguage();
		form.render();
		form.on('submit(login)', function (data) {
 	        if (winui.verifyForm(data.elem)) {
 	        	$("#loginBtn").find("i").attr("class", "fa fa-spin fa-spinner fa-fw");
 	        	if (!isLogin) {
					if (isNull($("#userCode").val())) {
						top.winui.window.msg("请输入用户名", {icon: 2, time: 2000});
						$("#loginBtn").find("i").attr("class", "fa fa-arrow-right");
					} else if (isNull($("#password").val())) {
						top.winui.window.msg("请输入密码", {icon: 2, time: 2000});
						$("#loginBtn").find("i").attr("class", "fa fa-arrow-right");
					} else {
						var params = {
							userCode: $("#userCode").val(),
							password: $("#password").val()
						};
						isLogin = true;
						AjaxPostUtil.request({url: reqBasePath + "login001", params: params, type: 'json', callback: function (json) {
							isLogin = false;
							$("#loginBtn").find("i").attr("class", "fa fa-arrow-right");
							if (json.rows != null) {
								localStorage.setItem("authpoints", JSON.stringify(json.rows));
							}
							localStorage.setItem('userToken', json.bean.userToken);
							if (!isNull(json.bean.id)) {
								if (checkURL(url)) {
									location.href = url;
								} else {
									location.href = "../../tpl/traditionpage/index.html";
								}
							}
						}, errorCallback: function (json) {
							winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
							$("#loginBtn").find("i").attr("class", "fa fa-arrow-right");
						}});
					}
 	        	}
 	        }
			return false;
		});
	});
	
	exports('login', {});
});