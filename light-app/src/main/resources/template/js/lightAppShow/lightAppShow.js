
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'form'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		form = layui.form;

	// 初始化左侧菜单数据
    showGrid({
	 	id: "setting",
	 	url: sysMainMation.lightAppBasePath + "queryLightAppTypeUpList",
	 	params: {},
		method: 'GET',
	 	pagination: false,
	 	template: $("#lightAppShowItemTemplate").html(),
	 	ajaxSendLoadBefore: function(hdb, json) {
			$.each(json.rows, function (i, item) {
				item.iconHtml = systemCommonUtil.initIconShow(item);
			});
	 	},
	 	ajaxSendAfter:function (json) {
	 		// 初始化所有上线列表数据
	 	    showList("");
	 	    matchingLanguage();
	 	}	
    });

	function showList(typeId){
		showGrid({
			id: "appList",
			url: sysMainMation.lightAppBasePath + "queryLightAppUpList",
			params: {typeId: typeId},
			pagination: false,
			method: 'GET',
			template: $("#lightAppShowListTemplate").html(),
			ajaxSendLoadBefore: function(hdb) {
			},
			ajaxSendAfter:function (json) {
			}
		});
	}
    
	$("body").on("click", ".setting a", function (e) {
		$(".setting a").removeClass("selected");
		$(this).addClass("selected");
		$("#appList").empty();
		
		var clickName = $(this).attr("rowname");
		var clickId = $(this).attr("rowid");
		$("#title").text(clickName);
	    showList(clickId);
	});
	
	$("body").on("click", ".app-list button", function (e) {
		var id = $(this).attr("rowid");
		AjaxPostUtil.request({url: sysMainMation.lightAppBasePath + "insertLightAppToWin", params: {id: id}, type: 'json', method: 'POST', callback: function (json) {
			top.winui.window.msg("添加成功，请刷新页面即可看到该应用。", {icon: 1,time: 3000});
		}});
	});
	
	$("body").on("click", ".app-list img", function (e) {
        var url = $(this).attr("rowurl");
        var title = $(this).attr("rowtitle");
        if(url.substr(0, 7).toLowerCase() == "http://" || url.substr(0, 8).toLowerCase() == "https://"){
            layer.open({
                type: 2, 
                title:title,
                area: ['90vw','90vh'],
                content: url
            });
        } else {
            winui.window.msg("链接格式错误", {icon: 2, time: 2000});
        }
    });
	
	form.render();
	
    exports('lightAppShow', {});
});
