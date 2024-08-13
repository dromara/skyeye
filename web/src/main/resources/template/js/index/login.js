

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

		if (!isNull(env)) {
			winui.window.open({
				id: '公告',
				type: 1,
				title: '演示公告',
				content: '<form class="layui-form" action="" id="showForm" autocomplete="off">' +
					'    <div class="layui-form-item layui-col-xs12">' +
					'        <span class="hr-title">基本信息</span><hr>' +
					'    </div>' +
					'	 <div class="layui-form-item layui-col-xs6">' +
					'        <label class="layui-form-label">链接：</label>' +
					'        <div class="layui-input-block ver-center">' +
					'        	<a href="https://gitee.com/doc_wei01/skyeye/blob/company_server/%E5%B8%B8%E8%A7%81%E9%97%AE%E9%A2%98.md" style="color: blue" target="_blank">常见问题</a>' +
					'        	<a href="https://articles.zsxq.com/id_xi3xhacte72g.html" style="color: blue" target="_blank">开发文档</a>' +
					'        </div>' +
					'    </div>' +
					'	 <div class="layui-form-item layui-col-xs6">' +
					'        <label class="layui-form-label">项目地址：</label>' +
					'        <div class="layui-input-block ver-center">' +
					'        	<a href="https://gitee.com/doc_wei01/skyeye" style="color: blue" target="_blank">项目地址1</a>' +
					'			<a href="https://gitee.com/doc_wei01/erp-pro" style="color: blue" target="_blank">项目地址2</a>' +
					'        </div>' +
					'    </div>' +
					'	 <div class="layui-form-item layui-col-xs12">' +
					'        <label class="layui-form-label">作者：</label>' +
					'        <div class="layui-input-block ver-center" style="font-weight: 800">' +
					'        	卫志强' +
					'        </div>' +
					'    </div>' +
					'	 <div class="layui-form-item layui-col-xs6">' +
					'        <label class="layui-form-label">作者微信：</label>' +
					'        <div class="layui-input-block ver-center">' +
					'        	<img src="../../assets/author/chatgpt的微信.jpg" style="width: 200px;">' +
					'        </div>' +
					'    </div>' +
					'	 <div class="layui-form-item layui-col-xs6">' +
					'        <label class="layui-form-label">知识星球：</label>' +
					'        <div class="layui-input-block ver-center">' +
					'        	<img src="../../assets/author/知识星球.png" style="width: 200px;">' +
					'        </div>' +
					'    </div>' +
					'	 <div class="layui-form-item layui-col-xs12">' +
					'        <label class="layui-form-label">体验地址：</label>' +
					'        <div class="layui-input-block ver-center" style="font-weight: 800">' +
					'        	`Star`后，关注下方微信公众号，回复`skyeye`获取' +
					'        	<img src="../../assets/author/微信公众号.jpg" style="width: 200px;">' +
					'        </div>' +
					'    </div>' +
					'</form>',
				area: ['80vw', '70vh']
			});
		}

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
 	        	if (!isLogin) {
					if (isNull($("#userCode").val())) {
						top.winui.window.msg("请输入用户名", {icon: 2, time: 2000});
					} else if (isNull($("#password").val())) {
						top.winui.window.msg("请输入密码", {icon: 2, time: 2000});
					} else {
						var params = {
							userCode: $("#userCode").val(),
							password: $("#password").val()
						};
						isLogin = true;
						AjaxPostUtil.request({url: reqBasePath + "login001", params: params, type: 'json', callback: function (json) {
							isLogin = false;
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
							isLogin = false;
							winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
						}});
					}
 	        	}
 	        }
			return false;
		});
	});
	
	exports('login', {});
});