
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
		url: flowableBasePath + "actmodletype021",
		params: {rowId:parent.rowId},
		pagination: false,
		template: $("#beanTemplate").html(),
		ajaxSendAfter:function(json){
			if(json.bean.pageTypes == 1){
				$("#pageTypes").html('指定页面');
				$(".TypeIsTwo").addClass("layui-hide");
				$(".TypeIsOne").removeClass("layui-hide");
			}else{
				$("#pageTypes").html('表单页面');
				$(".TypeIsTwo").removeClass("layui-hide");
				$(".TypeIsOne").addClass("layui-hide");
			}

			if(json.bean.menuIconType == 1){
				$("#menuIconType").html('Icon');
				$(".menuIconTypeIsTwo").addClass("layui-hide");
				$(".menuIconTypeIsOne").removeClass("layui-hide");
			}else{
				$("#menuIconType").html('图片');
				$(".menuIconTypeIsTwo").removeClass("layui-hide");
				$(".menuIconTypeIsOne").addClass("layui-hide");
			}

			if(json.bean.commonUsed == 1){
				$("#commonUsed").html('是');
			}else{
				$("#commonUsed").html('否');
			}
			matchingLanguage();
			form.render();
		}
	});
});