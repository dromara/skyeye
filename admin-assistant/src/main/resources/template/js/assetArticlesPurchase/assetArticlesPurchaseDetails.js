
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
	    var $ = layui.$;
	    var useTemplate = $("#useTemplate").html();
	    
	    AjaxPostUtil.request({url: flowableBasePath + "assetarticles026", params: {rowId: parent.rowId}, type: 'json', callback: function (json) {
			// 状态
			json.bean.stateName = getStateNameByState(json.bean.state, json.bean.stateName);

			$("#showForm").html(getDataUseHandlebars(useTemplate, json));

			// 附件回显
			skyeyeEnclosure.showDetails({"enclosureUploadBtn": json.bean.enclosureInfo});

			matchingLanguage();
	    }});

	});
});