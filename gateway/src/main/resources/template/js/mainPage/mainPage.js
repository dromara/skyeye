
var rowId = "";

var actFlowId = "";
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
			$("#checkOnWorkNum").html(json.bean.checkOnWorkNum);
			$("#diskCloudFileNum").html(json.bean.diskCloudFileNum);
			$("#forumNum").html(json.bean.forumNum);
			initNoticeList();
   		}});
	}
	
	function initNoticeList(){
		AjaxPostUtil.request({url: sysMainMation.noticeBasePath + "queryUserReceivedTopNotice", params: {}, type: 'json', method: 'GET', callback: function (json) {
			$("#noticeContent").append(getDataUseHandlebars($("#noticeContentTemplate").html(), json));
			initForumList();
   		}});
	}
	
	function initForumList(){
		AjaxPostUtil.request({url: reqBasePath + "mainpage003", params: {}, type: 'json', callback: function (json) {
			$("#forumContent").append(getDataUseHandlebars($("#forumContentTemplate").html(), json));
			initKnowledgeList();
   		}});
	}
	
	function initKnowledgeList(){
		AjaxPostUtil.request({url: sysMainMation.knowlgBasePath + "queryEightPassKnowlgList", params: {}, type: 'json', method: 'GET', callback: function (json) {
			$("#knowledgeList").html(getDataUseHandlebars($("#knowledgeContentTemplate").html(), json));
			// 我发表的审核通过的知识库数
			$("#knowledgeNum").html(json.total);
			matchingLanguage();
			form.render();
   		}});
	}

	// 公告详情
	$("body").on("click", ".notice", function (e) {
		_openNewWindows({
			url: systemCommonUtil.getUrl('FP2024013100006&id=' + $(this).attr("id"), null),
			title: "公告详情",
			pageId: "noticeDetailsPage",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
			}});
	});
	
	// 知识库详情
	$("body").on("click", ".knowledge", function (e) {
		_openNewWindows({
			url: systemCommonUtil.getUrl('FP2023101500004&id=' + $(this).attr("id"), null),
			title: "知识库详情",
			pageId: "knowledgeDetailsPage",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
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

    exports('mainPage', {});
});
