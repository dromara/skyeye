
// 车辆信息详情
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
		 	url: flowableBasePath + "vehicle007",
		 	params: {rowId:parent.rowId},
		 	pagination: false,
		 	template: getFileContent('tpl/vehicleManage/vehicleManageDetailsTemplate.tpl'),
		 	ajaxSendLoadBefore: function(hdb){
		 	},
		 	ajaxSendAfter:function (json) {
		 		$("#vehicleImg").attr("src", fileBasePath + json.bean.vehicleImg);
		 		// 附件回显
				skyeyeEnclosure.showDetails({"enclosureUpload": json.bean.enclosureInfo});
		        matchingLanguage();
		 	}
		});
	    
	    $("body").on("click", "#vehicleImg", function() {
			systemCommonUtil.showPicImg($(this).attr("src"));
	    });
	});
});