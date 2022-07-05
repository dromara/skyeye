
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
		 	url: reqBasePath + "noticetype006",
		 	params: {rowId:parent.rowId},
		 	pagination: false,
		 	template: getFileContent('tpl/sysnoticetype/sysnoticetypeeditTemplate.tpl'),
		 	ajaxSendAfter:function (json) {
		 		if(json.bean.pId === '二级类型'){	//该类型为二级类型
		 			$("#parentIdBox").removeClass("layui-hide");
 	        	}
 	        	
 	        	matchingLanguage();
				form.render();
		 	    form.on('submit(formEditBean)', function (data) {
		 	        if (winui.verifyForm(data.elem)) {
		 	        	var params = {
		 	        		rowId: parent.rowId,
		 	        		typeName: $("#typeName").val(),
		 	        	};
		 	        	AjaxPostUtil.request({url: reqBasePath + "noticetype007", params: params, type: 'json', callback: function (json) {
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