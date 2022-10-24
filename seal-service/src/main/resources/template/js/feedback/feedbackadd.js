
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
		var customerId = "",//客户id
			productId = "";//商品id
		
		AjaxPostUtil.request({url: flowableBasePath + "feedback002", params: {serviceId: parent.serviceId}, type: 'json', callback: function(json) {
			$("#orderNum").html(json.bean.orderNum);
			$("#customerName").html(json.bean.customerName);
			$("#productName").html(json.bean.productName);
			productId = json.bean.productId;
			customerId = json.bean.customerId;

			// 售后服务反馈类型
			sysDictDataUtil.showDictDataListByDictTypeCode(sysDictData["amsServiceFeedbBackType"]["key"], 'select', "typeId", '', form);

			matchingLanguage();
		}});

		skyeyeEnclosure.init('enclosureUpload');
		form.on('submit(formAddBean)', function(data) {
			if(winui.verifyForm(data.elem)) {
				var params = {
					typeId: $("#typeId").val(),
					content: $("#content").val(),
					customId: customerId,
					proId: productId,
					serviceId: parent.serviceId,
					enclosureInfo: skyeyeEnclosure.getEnclosureIdsByBoxId('enclosureUpload')
				};
				AjaxPostUtil.request({url: flowableBasePath + "feedback003", params: params, type: 'json', callback: function(json) {
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