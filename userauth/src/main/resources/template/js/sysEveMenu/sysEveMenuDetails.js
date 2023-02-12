
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
			ajaxSendLoadBefore: function(hdb, json) {
				if (json.bean.pageType) {
					json.bean.pageType = '自定义页面';
					json.bean.pageName = json.bean.pageUrl;
				} else {
					json.bean.pageType = '表单布局';
					dsFormUtil.dsFormChooseMation = json.bean.dsFormPage;
					var serviceName = json.bean.dsFormPage.serviceBeanCustom.serviceBean.name;
					json.bean.pageName = serviceName + '【' + json.bean.dsFormPage.name + '】';
				}
				if (json.bean.sysType == 1) {
					json.bean.sysTypeName = '是';
				} else {
					json.bean.sysTypeName = '否';
				}
				if (json.bean.isShare == 0) {
					json.bean.isShareName = '否';
				} else {
					json.bean.isShareName = '是';
				}
			},
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