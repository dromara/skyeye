
// 工单反馈
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

			// 售后服务反馈类型
			sysDictDataUtil.showDictDataListByDictTypeCode(sysDictData["amsServiceFeedbBackType"]["key"], 'select', "typeId", '', form);

			// 获取反馈信息
			AjaxPostUtil.request({url: flowableBasePath + "feedback004", params: {rowId: parent.rowId}, type: 'json', callback: function(j) {
				$("#typeId").val(j.bean.typeId);
				$("#content").val(j.bean.content);
				// 附件回显
				skyeyeEnclosure.initTypeISData({'enclosureUpload': json.bean.enclosureInfo});

				matchingLanguage();
				form.render();
				form.on('submit(formEditBean)', function(data) {
					if(winui.verifyForm(data.elem)) {
						var params = {
							typeId: $("#typeId").val(),
							content: $("#content").val(),
							rowId: parent.rowId,
							enclosureInfo: skyeyeEnclosure.getEnclosureIdsByBoxId('enclosureUpload')
						};
						AjaxPostUtil.request({url: flowableBasePath + "feedback005", params: params, type: 'json', callback: function(json) {
							parent.layer.close(index);
							parent.refreshCode = '0';
						}});
					}
					return false;
				});
			}});
		}});

		$("body").on("click", "#cancle", function() {
			parent.layer.close(index);
		});
	});
});