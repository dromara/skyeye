
layui.config({
	base: basePath,
	version: skyeyeVersion
}).extend({
	window: 'js/winui.window'
}).define(['window', 'jquery', 'winui'], function(exports) {
	winui.renderColor();
	layui.use(['form'], function(form) {
		var index = parent.layer.getFrameIndex(window.name);
		var $ = layui.$;
		
		AjaxPostUtil.request({url: flowableBasePath + "feedback002", params: {serviceId: parent.serviceId}, type: 'json', callback: function(json) {
			$("#orderNum").html(json.bean.orderNum);
			$("#customerName").html(json.bean.customerName);
			$("#productName").html(json.bean.productName);

			// 获取反馈信息
			AjaxPostUtil.request({url: flowableBasePath + "feedback007", params: {rowId: parent.rowId}, type: 'json', callback: function(j) {
				$("#createName").html(j.bean.createName);
				$("#typeName").html(j.bean.typeName);
				$("#content").html(j.bean.content);

				// 附件回显
				skyeyeEnclosure.showDetails({"enclosureUploadBox": j.bean.enclosureInfo});
			}});
			matchingLanguage();
		}});
		
	});
});