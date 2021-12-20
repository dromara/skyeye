
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
			productId = "";//产品id
		
		AjaxPostUtil.request({url: reqBasePath + "feedback002", params: {serviceId: parent.serviceId}, type: 'json', callback: function(json) {
			if(json.returnCode == 0) {
				$("#orderNum").html(json.bean.orderNum);
				$("#customerName").html(json.bean.customerName);
				$("#productName").html(json.bean.productName);
				productId = json.bean.productId;
				customerId = json.bean.customerId;
				showGrid({
				 	id: "typeId",
				 	url: reqBasePath + "crmservicefeedbacktype008",
				 	params: {},
				 	pagination: false,
				 	template: getFileContent('tpl/template/select-option.tpl'),
				 	ajaxSendLoadBefore: function(hdb){
				 	},
				 	ajaxSendAfter:function(data){
				 		form.render("select");
				 	}
				});
				matchingLanguage();
			} else {
				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
			}
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
				AjaxPostUtil.request({url: reqBasePath + "feedback003", params: params, type: 'json', callback: function(json) {
					if(json.returnCode == 0) {
						parent.layer.close(index);
						parent.refreshCode = '0';
					} else {
						winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
					}
				}});
			}
			return false;
		});

		$("body").on("click", "#cancle", function() {
			parent.layer.close(index);
		});
	});
});