
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'laydate'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
	    	laydate = layui.laydate;
	    
	    showGrid({
		 	id: "showForm",
		 	url: flowableBasePath + "sealseservice016",
		 	params: {rowId: parent.rowId},
		 	pagination: false,
		 	template: getFileContent('tpl/stateisstayreceipt/receiptTemplate.tpl'),
		 	ajaxSendLoadBefore: function(hdb) {
		 	},
		 	ajaxSendAfter: function (json) {
		 		// 预约开始时间
		 		laydate.render({elem: '#subscribeTime', type: 'datetime', trigger: 'click'});
		 		
		 		matchingLanguage();
		 		form.render();
		 	    form.on('submit(receipt)', function (data) {
		 	        if (winui.verifyForm(data.elem)) {
		 	        	var params = {
		        			rowId: parent.rowId,
		        			subscribeTime: $("#subscribeTime").val(),
		        			remark: $("#remark").val()
		 	        	};
	 	 	        	AjaxPostUtil.request({url: flowableBasePath + "sealseservice017", params: params, type: 'json', callback: function (json) {
							parent.layer.close(index);
							parent.refreshCode = '0';
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