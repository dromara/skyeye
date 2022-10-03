
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

	    showGrid({
		 	id: "showForm",
		 	url: flowableBasePath + "sealseservice028",
		 	params: {id: parent.rowId},
			method: 'GET',
		 	pagination: false,
		 	template: $("#beanTemplate").html(),
		 	ajaxSendAfter:function (json) {
				// 附件回显
				skyeyeEnclosure.showDetails({"enclosureUpload": json.bean.enclosureInfo});

 	        	matchingLanguage();
 	        	form.on('submit(formSubBean)', function (data) {
			        if (winui.verifyForm(data.elem)) {
			        	var msg = '确认提交审核吗？';
			    		layer.confirm(msg, { icon: 3, title: '审核操作' }, function (i) {
			    			layer.close(i);
			    			var jStr = {
				    			opinion: $("#opinion").val(),
				    			isAgree: $("input[name='flag']:checked").val(),
				    			rowId: parent.rowId
				            };
				            AjaxPostUtil.request({url: flowableBasePath + "sealseservice030", params: jStr, type: 'json', callback: function (json) {
								parent.layer.close(index);
								parent.refreshCode = '0';
				 	   		}});
			    		});
			        }
			        return false;
			    });
		 		form.render();
		 	}
		});
		
	    $("body").on("click", "#cancle", function() {
			parent.layer.close(index);
		});
	    
	});
});