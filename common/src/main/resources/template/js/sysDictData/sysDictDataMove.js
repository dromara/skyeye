
layui.config({
	base: basePath,
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'eleTree'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$;

		treeSelectUtil.init({
			eleTree: layui.eleTree,
			elem: 'parentId',
			url: reqBasePath + "queryDictDataListByDictTypeCodeAndNotId?dictTypeCode=" + parent.parentNode.dictCode + '&notId=' + parent.parentNode.id
		});

		matchingLanguage();
		form.render();
		form.on('submit(formEditBean)', function (data) {
			if (winui.verifyForm(data.elem)) {
				var params = {
					id: parent.parentNode.id,
					parentId: $("#parentId").attr("parentId"),
				};
				AjaxPostUtil.request({url: reqBasePath + "setDictDataParent", params: params, type: 'json', method: "PUT", callback: function (json) {
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