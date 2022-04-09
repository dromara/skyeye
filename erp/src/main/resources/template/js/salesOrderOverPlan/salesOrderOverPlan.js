
// 销售订单统筹
layui.config({
	base: basePath,
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'laydate'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
			laydate = layui.laydate;
	    var beanTemplate = $("#beanTemplate").html();

	    showGrid({
		 	id: "showForm",
		 	url: flowableBasePath + "erpcommon001",
		 	params: {rowId: parent.rowId},
		 	pagination: false,
		 	template: beanTemplate,
		 	ajaxSendAfter:function(json){
				// 销售订单状态
		 		$("#statusName").html(activitiUtil.showStateName(json.bean.state, json.bean.submitType));

				laydate.render({
					elem: '#lateDesignTime',
					type: 'datetime',
					min: json.bean.operTime,
					trigger: 'click'
				});
				laydate.render({
					elem: '#latePurchaseTime',
					type: 'datetime',
					min: json.bean.operTime,
					trigger: 'click'
				});
				laydate.render({
					elem: '#lateProduceTime',
					type: 'datetime',
					min: json.bean.operTime,
					trigger: 'click'
				});
				laydate.render({
					elem: '#lateQualityTime',
					type: 'datetime',
					min: json.bean.operTime,
					trigger: 'click'
				});
		 		matchingLanguage();
		 		form.render();
				form.on('submit(formAddBean)', function(data) {
					if(winui.verifyForm(data.elem)) {
						var params = {
							rowId: parent.rowId,
							lateDesignTime: $("#lateDesignTime").val(),
							latePurchaseTime: $("#latePurchaseTime").val(),
							lateProduceTime: $("#lateProduceTime").val(),
							lateQualityTime: $("#lateQualityTime").val()
						};
						AjaxPostUtil.request({url: flowableBasePath + "erpordersaleoverplan002", params: params, type: 'json', callback: function(json) {
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
		 	}
		});

		$("body").on("click", "#cancle", function() {
			parent.layer.close(index);
		});

	});
});