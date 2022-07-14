
var content = "";
var executionResultContent = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window',
}).define(['window', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$;
	    var useTemplate = $("#useTemplate").html();

	    AjaxPostUtil.request({url: flowableBasePath + "protask003", params:{rowId: parent.rowId}, type: 'json', callback: function (json) {
			$("#showForm").html(getDataUseHandlebars(useTemplate, json));
			// 附件回显
			skyeyeEnclosure.showDetails({
				"enclosureUploadBtn": json.bean.enclosureInfo, // 任务说明附件回显
				"executionEnclosureUploadBtn": json.bean.executionEnclosureInfo // 执行结果附件回显
			});

			content = json.bean.taskInstructions;
			$("#taskInstructionsShowBox").attr("src", "taskinstructionsshow.html");
			if(json.bean.state == '3'){
				$(".actual").removeClass("layui-hide");
				executionResultContent = json.bean.executionResult;
				$("#executionResultShowBox").attr("src", "executionresultshow.html");
			}
			matchingLanguage();
	    }});

	});
});