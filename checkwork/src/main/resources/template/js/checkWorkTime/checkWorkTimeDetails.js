
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
		 	url: sysMainMation.checkworkBasePath + "queryCheckWorkTimeById",
		 	params: {id: parent.rowId},
		 	pagination: false,
		 	method: "GET",
		 	template: $("#beanTemplate").html(),
			ajaxSendLoadBefore: function(hdb, json){
				json.bean.typeName = skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("checkWorkTimeType", 'id', json.bean.type, 'name');
				json.bean.enabled = skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("commonEnable", 'id', json.bean.enabled, 'name');
			},
		 	ajaxSendAfter:function (json) {
		 		var type = json.bean.type;
				if (type == 1) {
					checkWorkUtil.resetSingleBreak();
				} else if (type == 2) {
					checkWorkUtil.resetWeekend();
				} else if (type == 3) {
					checkWorkUtil.resetSingleAndDoubleBreak();
				} else if (type == 4) {
					checkWorkUtil.resetCustomizeDay(json.bean.checkWorkTimeWeekList);
				}

		 		matchingLanguage();
		 		form.render();
		 	}
		});

	});
});