
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$;

		form.verify({
			password : function(value, item){
				if(value.length < 6){
					return "密码长度不能小于6位";
				}
			},
			confirmPwd : function(value, item){
				if($("#password").val() != value){
					return "两次输入密码不一致，请重新输入！";
				}
			}
		});

		matchingLanguage();
		form.render();
		form.on('submit(formEditPwdBean)', function (data) {
			if (winui.verifyForm(data.elem)) {
				var params = {
					rowId: parent.rowId,
					password: $("#password").val(),
				};

				AjaxPostUtil.request({url: reqBasePath + "sys005", params: params, type: 'json', method: "POST", callback: function (json) {
					parent.layer.close(index);
					parent.refreshCode = '0';
				}});
			}
			return false;
		});

	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	});
});