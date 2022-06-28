
var rowId = "";

var actKey = "";
var dsFormId = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		form = layui.form,
		table = layui.table;
	
	initFourNumList();
	function initFourNumList(){
		AjaxPostUtil.request({url: reqBasePath + "mainpage001", params: {}, type: 'json', callback: function (json) {
   			if (json.returnCode == 0) {
 	   			$("#checkOnWorkNum").html(json.bean.checkOnWorkNum);
 	   			$("#diskCloudFileNum").html(json.bean.diskCloudFileNum);
 	   			$("#forumNum").html(json.bean.forumNum);
 	   			$("#knowledgeNum").html(json.bean.knowledgeNum);
 	   			initNoticeList();
   			} else {
   				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
   			}
   		}});
	}
	
	function initNoticeList(){
		AjaxPostUtil.request({url: reqBasePath + "mainpage002", params: {}, type: 'json', callback: function (json) {
   			if (json.returnCode == 0) {
   				$.each(json.rows, function(i, item){
   					if(i == 0){
   						item.className = "layui-this";
   						item.contentClassName = "layui-show";
   					}
   				});
 	   			$("#noticeTitle").append(getDataUseHandlebars($("#noticeTitleTemplate").html(), json));
 	   			$("#noticeContent").append(getDataUseHandlebars($("#noticeContentTemplate").html(), json));
 	   			initForumList();
   			} else {
   				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
   			}
   		}});
	}
	
	function initForumList(){
		AjaxPostUtil.request({url: reqBasePath + "mainpage003", params: {}, type: 'json', callback: function (json) {
   			if (json.returnCode == 0) {
 	   			$("#forumContent").append(getDataUseHandlebars($("#forumContentTemplate").html(), json));
 	   			initKnowledgeList();
   			} else {
   				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
   			}
   		}});
	}
	
	function initKnowledgeList(){
		AjaxPostUtil.request({url: reqBasePath + "mainpage004", params: {}, type: 'json', callback: function (json) {
   			if (json.returnCode == 0) {
 	   			$("#knowledgeList").html(getDataUseHandlebars($("#knowledgeContentTemplate").html(), json));
 	   			matchingLanguage();
				initHotActModel();
 	   			form.render();
   			} else {
   				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
   			}
   		}});
	}

	function initHotActModel(){
		showGrid({
			id: "actModelShow",
			url: flowableBasePath + "actmodletype022",
			params: {},
			pagination: false,
			template: $("#actModelTemplate").html(),
			ajaxSendLoadBefore: function(hdb){
				// 展示图标
				hdb.registerHelper("compare1", function(v1, v2, v3, v4, options){
					var str = "";
					if(v1 == 1){
						// icon
						str = '<i class="fa fa-fw ' + v3 + '" style="font-size: 18px; line-height: 30px; width: 100%; color: ' + v4 + '"></i>';
					} else {
						str = '<img src="' + reqBasePath + v2 + '" class="photo-img" style="width: 25px; height: 25px">';
					}
					return str;
				});
			},
			ajaxSendAfter:function (json) {
				matchingLanguage();
			}
		});
	}
	
	// 公告详情
	$("body").on("click", ".notice", function (e) {
		rowId = $(this).attr("id");
		_openNewWindows({
			url: "../../tpl/sysnoticefront/sysnoticefrontdetails.html", 
			title: "公告详情",
			pageId: "sysnoticefrontdetails",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
			}});
	});
	
	// 知识库详情
	$("body").on("click", ".knowledge", function (e) {
		rowId = $(this).attr("id");
		_openNewWindows({
			url: "../../tpl/knowledgePageShow/details.html", 
			title: "知识库详情",
			pageId: "knowledgePageShowDetailsPage",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
			}});
	});
	
	// 论坛帖详情
	$("body").on("click", "#forumContent li h3, #forumContent li p", function (e) {
		// 否则判断该tab项是否以及存在
		var isData = false; // 初始化一个标志，为false说明未打开该tab项 为true则说明已有
		$.each(parent.$("#LAY_app_tabsheader li[lay-id]"), function() {
			// 如果点击左侧菜单栏所传入的id 在右侧tab项中的lay-id属性可以找到，则说明该tab项已经打开
			if($(this).attr("lay-id") === "forumBtn") {
				isData = true;
			}
		})
		rowId = $(this).parent().attr("id");
		if(isData) {
			parent.active.tabDelete("forumBtn");
		}
		// 标志为false 新增一个tab项
		parent.active.tabAdd("../../tpl/forumshow/forumitem.html?id=" + rowId, 'forumBtn', '<i class="fa fa-calendar"></i>论坛');
    	parent.active.tabChange("forumBtn");
	});

	// 流程任务配置点击
	$("body").on("click", ".act-model-li", function (e) {
		var title = $(this).attr("showTitle");
		var url = $(this).attr("pageUrl");
		dsFormId = $(this).attr("dsFormId");
		actKey = $(this).attr("actid");
		_openNewWindows({
			url: url,
			title: title,
			pageId: "openLaunchTaskPage",
			area: ['100vw', '100vh'],
			callBack: function(refreshCode){
				if (refreshCode == '0') {
					winui.window.msg("提交成功", {icon: 1, time: 2000});
				} else if (refreshCode == '-9999') {
					winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2, time: 2000});
				}
			}});
	});
    
    exports('mainPage', {});
});
