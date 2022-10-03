
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
	var $ = layui.$,
		form = layui.form;

	showGrid({
		id: "showForm",
		url: flowableBasePath + "queryActModelDetailsById",
		params: {id: parent.rowId},
		pagination: false,
		method: 'GET',
		template: $("#beanTemplate").html(),
		ajaxSendAfter:function (json) {
			if(json.bean.pageTypes == 1){
				$("#pageTypes").html('指定页面');
				$(".TypeIsTwo").addClass("layui-hide");
				$(".TypeIsOne").removeClass("layui-hide");
			} else {
				$("#pageTypes").html('表单页面');
				$(".TypeIsTwo").removeClass("layui-hide");
				$(".TypeIsOne").addClass("layui-hide");
			}

			if(json.bean.iconType == 1){
				$("#iconType").html('Icon');
				$(".iconTypeIsTwo").addClass("layui-hide");
				$(".iconTypeIsOne").removeClass("layui-hide");
			} else {
				$("#iconType").html('图片');
				$(".iconTypeIsTwo").removeClass("layui-hide");
				$(".iconTypeIsOne").addClass("layui-hide");
			}

			if(json.bean.commonUsed == 1){
				$("#commonUsed").html('是');
			} else {
				$("#commonUsed").html('否');
			}
			matchingLanguage();
			form.render();
		}
	});
});