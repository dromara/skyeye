
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'tagEditor'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$;

		// 举报类型
		sysDictDataUtil.showDictDataListByDictTypeCode(sysDictData["bbsForumReportType"]["key"], 'select', "reportType", '', form);
 		// 下拉框监听，如果类型是其他，则显示举报内容输入框
 		form.on('select(reportType)', function(data) {
			if (data.value == 'other') {
				$("#contentHide").removeClass("layui-hide");
			} else {
				$("#contentHide").addClass("layui-hide");
				$("#reportContent").val("");
			}
		});
	    
		matchingLanguage();
 		form.render();
 	    form.on('submit(formAddBean)', function (data) {
 	        if (winui.verifyForm(data.elem)) {
 	        	var params = {
 	        		forumId: parent.rowId,
 	        		reportContent: $("#reportContent").val(),
 	        		reportDesc: $("#reportDesc").val()
 	        	};
 	        	params.reportType = data.field.reportType;
 	        	AjaxPostUtil.request({url: sysMainMation.forumBasePath + "forumreport001", params: params, type: 'json', callback: function (json) {
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