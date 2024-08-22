
var staffId = "";

var rowId = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
	    	table = layui.table;
	    staffId = GetUrlParam("objectId");
	    
	    showGrid({
		 	id: "showForm",
		 	url: reqBasePath + "querySysUserStaffById",
		 	params: {id: staffId},
		 	pagination: false,
			method: "GET",
		 	template: $("#beanTemplate").html(),
		 	ajaxSendAfter:function (json) {
		 		if (json.bean.state == 2) {
					// 离职
		 			$("#leaveTime").show();
		 			$("#leaveReason").show();
		 		}
		 		$("#userPhoto").attr("src", systemCommonUtil.getFilePath(json.bean.userPhoto));
		 		matchingLanguage();
		 		form.render();
		 	}
		});
		
	    $("body").on("click", "#userPhoto", function() {
			systemCommonUtil.showPicImg($(this).attr("src"));
	    });
	    
	    $("body").on("click", ".workDay", function() {
	    	rowId = $(this).attr("rowId");
	    	_openNewWindows({
				url: "../../tpl/checkWorkTime/checkWorkTimeDetails.html", 
				title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
				pageId: "checkWorkTimeDetails",
				area: ['90vw', '90vh'],
				callBack: function (refreshCode) {
				}});
	    });
	    
	});
});