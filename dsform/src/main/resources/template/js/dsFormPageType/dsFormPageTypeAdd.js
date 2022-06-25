
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'form'], function (exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
	var $ = layui.$,
		form = layui.form;

	dsFormUtil.loadDsFormPageTypeByPId('parentId', "0");

	matchingLanguage();
	form.render();
	form.on('submit(formAddBean)', function (data) {
		if (winui.verifyForm(data.elem)) {
			var params = {
				typeName: $("#typeName").val(),
				typeNameEn: $("#typeNameEn").val(),
				encoded: $("#encoded").val(),
				parentId: isNull($("#parentId").val()) ? "0" : $("#parentId").val()
			};
			AjaxPostUtil.request({url: flowableBasePath + "dsformpagetype002", params:params, type: 'json', method: "POST", callback: function(json){
				if (json.returnCode == 0) {
					parent.layer.close(index);
					parent.refreshCode = '0';
				}else{
					winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
				}
			}});
		}
		return false;
	});

	$("body").on("click", "#cancle", function(){
		parent.layer.close(index);
	});
});