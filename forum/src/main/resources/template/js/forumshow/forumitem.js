var rowId = "";//帖子id
var belongCommentId = "";//评论id
var commentUserId = "";//评论人id
var replyType = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	var $ = layui.$;
	var commentTemplate = $('#commentTemplate').html();
	var replyTemplate = $('#replyTemplate').html();
	var textareaTemplate = $('#textareaTemplate').html();
	
	//公共标题
	$("#forumTitle").html(getFileContent("tpl/forumshow/commontitle.tpl"));
	//菜单
	$("body").append(getFileContent("tpl/forumshow/commonmenu.tpl"));
	
	rowId = GetUrlParam("id");

	var currentUserId = "";
	// 获取当前登录员工信息
	systemCommonUtil.getSysCurrentLoginUserMation(function (data){
		currentUserId = data.bean.id;
	});

	//帖子信息展示
	AjaxPostUtil.request({url:reqBasePath + "forumcontent006", params: {rowId:rowId}, type: 'json', callback: function(json){
		if (json.returnCode == 0) {
			if(json.bean.createId != currentUserId){
				// 如果不是用户自己的帖子，则显示举报按钮
				$("#forumReport").removeClass("layui-hide");
			}
			$("#content").html(json.bean.content);
			$("#title").html(json.bean.title);
			$("#createTime").html(json.bean.createTime);
			$("#photo").html("<img userId=" + json.bean.userId + " alt='' src=" + json.bean.userPhoto + ">");
			loadCommentList();//加载评论信息
		} else {
			winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
		}
	}});
	
	//发表评论
	$("body").on("click", "#commentContentAdd", function(e){
		var content = $("#commentContent").val();
		if(isNull(content)){
			winui.window.msg("评论内容不能为空！", {icon: 2, time: 2000});
		} else {
			AjaxPostUtil.request({url:reqBasePath + "forumcontent008", params:{forumId:rowId, content:content}, type: 'json', callback: function(json){
 	   			if (json.returnCode == 0) {
 	   				loadCommentList();//刷新评论信息
 	   				$("#commentContent").val("");
 	   			} else {
 	   				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
 	   			}
 	   		}});
		}
	});
	
	//评论下的回复按钮
	$("body").on("click", ".operator-btn span", function(e){
		$(".comment-text-textarea").empty();
		$(".se-comment-text-textarea").empty();
		belongCommentId = $(this).attr("rowid");
		commentUserId = $(this).attr("rowUserid");
		rowUsername = $(this).attr("rowUsername");
		replyType = "1";
		var Item = $(this).parents(".comment-text").find(".comment-text-textarea");
		var params = {rowUsername:rowUsername};
		Item.append(getDataUseHandlebars(textareaTemplate, params));
	});
	
	//回复信息下的回复按钮
	$("body").on("click", ".se-operator-btn span", function(e){
		$(".comment-text-textarea").empty();
		$(".se-comment-text-textarea").empty();
		belongCommentId = $(this).attr("rowid");
		commentUserId = $(this).attr("rowUserid");
		rowUsername = $(this).attr("rowUsername");
		replyType = "2";
		var Item = $(this).parents(".se-comment-text").find(".se-comment-text-textarea");
		var params = {rowUsername:rowUsername};
		Item.append(getDataUseHandlebars(textareaTemplate, params));
	});
	
	//点击发表回复评论
	$("body").on("click", "#forumCommentAdd", function(e){
		var replyContent = $("#replyContent").val();
		if(isNull(replyContent)){
			winui.window.msg("回复内容不能为空！", {icon: 2, time: 2000});
		} else {
			AjaxPostUtil.request({url:reqBasePath + "forumcontent010", params:{forumId:rowId, belongCommentId:belongCommentId, content:replyContent, replyId:commentUserId}, type: 'json', callback: function(json){
 	   			if (json.returnCode == 0) {
 	   				$(".comment-text-textarea").empty();
 	   				$(".se-comment-text-textarea").empty();
 	   			    var commentName = "";
 	   			    var commentUserId = "";
					// 获取当前登录员工信息
					systemCommonUtil.getSysCurrentLoginUserMation(function (data){
						commentName =  data.bean.userName;
						commentUserId =  data.bean.id;
					});
					var params = {
						belongCommentId: belongCommentId,
						replyName: commentName,
						commentName: rowUsername,
						content: replyContent,
						commentTime: '刚刚',
						commentUserId: commentUserId
					};
					if(replyType == "1"){
						$("span[rowid=" + belongCommentId + "]").parents('div[class^="comment-text"]').eq(0).find(".second-comment-item").removeClass("layui-hide");
						$("span[rowid=" + belongCommentId + "]").parents('div[class^="first-comment-item"]').eq(0).find(".second-comment-item").append(getDataUseHandlebars(replyTemplate, params));
					}else if(replyType == "2"){
						$("span[rowid=" + belongCommentId + "]").parents('div[class^="se-comment-text"]').eq(0).after(getDataUseHandlebars(replyTemplate, params));
					}
 	   			} else {
 	   				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
 	   			}
 	   		}});
		}
	});
	
	//我的操作
	$("body").on("click", ".suspension-menu-icon", function(e){
		if($(".drop-down-menu").is(':hidden')){
			$(".drop-down-menu").show();
			$(".suspension-menu-icon").removeClass("rotate").addClass("rotate1");
		} else {
			$(".drop-down-menu").hide();
			$(".suspension-menu-icon").removeClass("rotate1").addClass("rotate");
		}
	});
	
	//举报
	$("body").on("click", "#forumReport", function(e){
		_openNewWindows({
			url: "../../tpl/forumshow/forumreport.html", 
			title: "帖子举报",
			pageId: "forumreport",
			area: ['40vw', '60vh'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg("举报成功", {icon: 1, time: 2000});
                } else if (refreshCode == '-9999') {
                	winui.window.msg("举报失败", {icon: 2, time: 2000});
                }
			}});
	});
	
	//加载评论列表
	function loadCommentList(){
		$("#addCommentList").empty();
		showGrid({
		 	id: "addCommentList",
		 	url: reqBasePath + "forumcontent009",
		 	params: {forumId:rowId},
		 	pagination: false,
		 	pagesize: 10,
		 	template: commentTemplate,
		 	ajaxSendLoadBefore: function(hdb){
		 	},
		 	ajaxSendAfter:function(j){
		 		AjaxPostUtil.request({url:reqBasePath + "forumcontent011", params:{forumId:rowId}, type: 'json', callback: function(json){
	 				if(json.returnCode == 0) {
	 					var row = json.rows;
	 			 		for(var i = 0;i < row.length; i++){
	 			 			var belongCommentId = row[i].belongCommentId;
	 			 			$(".second-comment-item").each(function(){
	 				 			var thisCommentId  = $(this).attr("rowid");
	 				 			if(belongCommentId == thisCommentId){
	 				 				$(this).removeClass("layui-hide");
	 				 				var params = {
	 				 					belongCommentId: row[i].belongCommentId,
		 			 					replyName: row[i].replyName,
		 			 					commentName: row[i].commentName,
		 			 					content: row[i].content,
		 			 					commentTime: row[i].commentTime,
		 			 					commentUserId: row[i].commentUserId
				 			 	    };
	 				 				$(this).append(getDataUseHandlebars(replyTemplate, params));
	 				 			}
	 				 		});
	 			 		}
	 			 		matchingLanguage();
	 				}else {
	 					winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
	 				}
	 			}});
		 	}
	    });
	}
	
    exports('forumitem', {});
});
