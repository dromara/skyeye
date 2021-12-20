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
		 	url: reqBasePath + "erpcommon001",
		 	params: {rowId: parent.rowId},
		 	pagination: false,
		 	template: beanTemplate,
		 	ajaxSendAfter:function(json){
		 		AjaxPostUtil.request({url: reqBasePath + "login002", params: {}, type: 'json', callback: function(data) {
					if(data.returnCode == 0) {
						$("#orderDetailTitle").html(data.bean.companyName + '销售订单');
					} else {
						winui.window.msg(data.returnMessage, {icon: 2, time: 2000});
					}
				}});
		 		if(json.bean.status == 0){
		 			$("#statusName").html("<span class='state-down'>未审核</span>");
		 		}else if(json.bean.status == 1){
		 			$("#statusName").html("<span class='state-up'>审核中</span>");
		 		}else if(json.bean.status == 2){
		 			$("#statusName").html("<span class='state-new'>审核通过</span>");
		 		}else if(json.bean.status == 3){
		 			$("#statusName").html("<span class='state-down'>拒绝通过</span>");
		 		}else if(json.bean.status == 4){
		 			$("#statusName").html("<span class='state-new'>已完成</span>");
		 		}
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
						AjaxPostUtil.request({url: reqBasePath + "erpordersaleoverplan002", params: params, type: 'json', callback: function(json) {
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