
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
	    
	    AjaxPostUtil.request({url: flowableBasePath + "erpproduction006", params: {orderId: parent.rowId}, type: 'json', callback: function (json) {
			$("#showForm").html(getDataUseHandlebars($("#beanTemplate").html(), json));

			matchingLanguage();
			form.render();
			form.on('submit(formExamineBean)', function (data) {
				if (winui.verifyForm(data.elem)) {
					var msg = '确认提交吗？';
					layer.confirm(msg, { icon: 3, title: '提交审批' }, function (i) {
						layer.close(i);
						var jStr = {
							approvalContent: $("#opinion").val(),
							state: $("input[name='flag']:checked").val(),
							orderId: parent.rowId
						};
						AjaxPostUtil.request({url: flowableBasePath + "erpproduction008", params: jStr, type: 'json', callback: function (json) {
							parent.layer.close(index);
							parent.refreshCode = '0';
						}});
					});
				}
				return false;
			});
        }});
	    
		// 取消
	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	    
	});
});