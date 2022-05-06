
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'textool', 'form'], function (exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
	var $ = layui.$,
		form = layui.form,
		textool = layui.textool;

	textool.init({
		eleId: 'roleDesc',
		maxlength: 250,
		tools: ['count', 'copy', 'reset']
	});

	matchingLanguage();
	form.render();
	form.on('submit(formAddBean)', function (data) {
		if (winui.verifyForm(data.elem)) {
			var params = {
				roleName: $("#roleName").val(),
				roleDesc: $("#roleDesc").val()
			};

			AjaxPostUtil.request({url: reqBasePath + "sys015", params: params, type: 'json', method: "POST", callback: function(json){
				if(json.returnCode == 0) {
					parent.layer.close(index);
					parent.refreshCode = '0';
				}else{
					winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
				}
			}});
		}
		return false;
	});

	// 取消
	$("body").on("click", "#cancle", function(){
		parent.layer.close(index);
	});

});