
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'textool'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
			textool = layui.textool;
	    
	    showGrid({
		 	id: "showForm",
		 	url: reqBasePath + "queryDictTypeMationById",
		 	params: {id: parent.rowId},
		 	pagination: false,
			method: "GET",
		 	template: $("#showTemplate").html(),
		 	ajaxSendAfter: function (json) {

				$("input:radio[name=status][value=" + json.bean.status + "]").attr("checked", true);

				textool.init({eleId: 'remark', maxlength: 200});

				matchingLanguage();
				form.render();
		 	    form.on('submit(formEditBean)', function (data) {
		 	        if (winui.verifyForm(data.elem)) {
		 	        	var params = {
							id: parent.rowId,
							dictName: $("#dictName").val(),
							dictCode: $("#dictCode").val(),
							status: $("input[name='status']:checked").val(),
							dictType: 1,
							remark: $("#remark").val(),
		 	        	};
		 	        	AjaxPostUtil.request({url: reqBasePath + "writeDictTypeMation", params: params, type: 'json', method: "POST", callback: function (json) {
		 	        		if (json.returnCode == 0) {
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