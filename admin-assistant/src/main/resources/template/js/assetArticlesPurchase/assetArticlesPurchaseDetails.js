
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
	    var $ = layui.$;

	    AjaxPostUtil.request({url: flowableBasePath + "assetarticles026", params: {rowId: parent.rowId}, type: 'json', callback: function (json) {
			// 状态
			json.bean.stateName = activitiUtil.showStateName2(json.bean.state, 1);

			$("#showForm").html(getDataUseHandlebars($("#useTemplate").html(), json));

			// 附件回显
			skyeyeEnclosure.showDetails({"enclosureUploadBtn": json.bean.enclosureInfo});

			matchingLanguage();
	    }});

	});
});