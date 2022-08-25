
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$;
	    
	    AjaxPostUtil.request({url: flowableBasePath + "asset013", params: {rowId: parent.rowId}, type: 'json', method: "GET", callback: function (json) {
			json.bean.stateName = activitiUtil.showStateName2(json.bean.state, 1);

			$("#showForm").html(getDataUseHandlebars($("#useTemplate").html(), json));

			// 附件回显
			skyeyeEnclosure.showDetails({"enclosureUploadBtn": json.bean.enclosureInfo});
			matchingLanguage();
	    }});

		// 图片查看
		$("body").on("click", ".photo-img", function() {
			systemCommonUtil.showPicImg($(this).attr("src"));
		});

	});
});