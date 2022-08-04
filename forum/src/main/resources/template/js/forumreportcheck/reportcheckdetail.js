
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
		 	url: sysMainMation.forumBasePath + "forumreport005",
		 	params: {rowId: parent.rowId},
		 	pagination: false,
		 	template: getFileContent('tpl/forumreportcheck/reportcheckdetailsTemplate.tpl'),
		 	ajaxSendLoadBefore: function(hdb){
		 	},
		 	ajaxSendAfter:function (json) {
		 		if(json.bean.examineState == "未审核"){
		 			$(".examine").addClass("layui-hide");
		 		}else if(json.bean.examineState == "审核通过"){
		 			$(".nopass").addClass("layui-hide");
		 		}
		 		if(json.bean.reportType != "其他"){
		 			$(".reportcontent").addClass("layui-hide");
		 		}
		 		matchingLanguage();
		 	}
		});
	    
	});
});