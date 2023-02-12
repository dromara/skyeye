
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window',
}).define(['window', 'table', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$;
	    
	    showGrid({
		 	id: "showForm",
		 	url: reqBasePath + "sys040",
		 	params: {id: parent.rowId},
		 	pagination: false,
			method: 'GET',
		 	template: $("#beanTemplate").html(),
		 	ajaxSendAfter:function (json) {
	        	$("#icon").html(systemCommonUtil.initIconShow(json.bean));
	        	
	        	if(json.bean.parentId == '0'){
	        		$("#level").html("父菜单");
	        	} else {
	        		$("#level").html( "子菜单");
	        	}
	        	matchingLanguage();
		 		form.render();
		 	}
		});

		$("body").on("click","#menuIconPic", function() {
			systemCommonUtil.showPicImg(fileBasePath + json.bean.menuIconPic);
		})
	    
	});
});