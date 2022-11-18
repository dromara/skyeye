
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

	var sysDictType = GetUrlParam("sysDictType");

	sysDictDataUtil.showDictDataListByDictTypeCode(sysDictData[sysDictType]["key"], 'select', "dictDataId", '', form);

	matchingLanguage();
	form.render();
	form.on('submit(formAddBean)', function (data) {
		if (winui.verifyForm(data.elem)) {
			var params = {
				id: $("#dictDataId").val(),
				name: $("#dictDataId").find("option:selected").text()
			};
			parent.turnData = params;
			parent.refreshCode = '0';
			parent.layer.close(index);
		}
		return false;
	});

	$("body").on("click", "#cancle", function() {
		parent.layer.close(index);
	});
});