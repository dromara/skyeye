
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
		 	url: sysMainMation.wagesBasePath + "wages003",
		 	params: {rowId: parent.rowId},
		 	pagination: false,
			method: "GET",
		 	template: $("#showBaseTemplate").html(),
		 	ajaxSendLoadBefore: function(hdb) {
		 	},
		 	ajaxSendAfter:function (json) {
				$("input:radio[name=monthlyClearing][value=" + json.bean.monthlyClearing + "]").attr("checked", true);
				$("input:radio[name=wagesType][value=" + json.bean.wagesType + "]").attr("checked", true);
				matchingLanguage();
				form.render();
		 	    form.on('submit(formEditBean)', function (data) {
		 	        if (winui.verifyForm(data.elem)) {
						var params = {
							nameCn: $("#nameCn").val(),
							nameEn: $("#nameEn").val(),
							key: $("#key").val(),
							wagesType: $("input[name='wagesType']:checked").val(),
							monthlyClearing: $("input[name='monthlyClearing']:checked").val(),
							rowId: parent.rowId
		 	        	};
		 	        	AjaxPostUtil.request({url: sysMainMation.wagesBasePath + "wages004", params: params, type: 'json', method: "PUT", callback: function (json) {
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