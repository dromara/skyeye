
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'form'], function (exports) {
	
	winui.renderColor();
	
	var $ = layui.$,
		form = layui.form;
	
	var newForumListTemplate = $('#newForumListTemplate').html();
	var newCommentListTemplate = $('#newCommentListTemplate').html();
	var hotTagListTemplate = $('#hotTagListTemplate').html();
	var activeUsersListTemplate = $('#activeUsersListTemplate').html();
	var hotForumListTemplate = $('#hotForumListTemplate').html();
	var searchForumListTemplate = $('#searchForumListTemplate').html();
	
	//公共标题
	$("#forumTitle").html(getFileContent("tpl/forumshow/commontitle.tpl"));
	//菜单
	$("body").append(getFileContent("tpl/forumshow/commonmenu.tpl"));
	
	//遮罩层显示
	$(".fileconsole-mask").show();
	
	loadForumList();
	
	//加载指定目录下的文件和目录
	function loadForumList(){
		//遮罩层显示
		$(".fileconsole-mask").show();
		setTimeout(function(){
			loadForumListFirst();
		}, 200);
	}
	
	function loadForumListFirst(){
		//遮罩层隐藏
		$(".fileconsole-mask").hide();
		form.render('select');
	}
	
	//全部热门
	$("body").on("click", "#allHot", function(e){
		location.href = "../../tpl/forumshow/forumtaglist.html?id=hot";
	});
	
	//帖子搜索
	$("body").on("click", "#forumSearch", function(e){
		var searchValue = $("#iconClass").val();
		if(!isNull(searchValue)){
			$(".main-content-two").addClass("layui-hide");
			$(".main-content-one").removeClass("layui-hide");
			//加载搜索出的帖子
			loadSearchForumList(searchValue);
		}else{
			$(".main-content-one").addClass("layui-hide");
			$(".main-content-two").removeClass("layui-hide");
		}
	});
	
	function loadSearchForumList(searchValue){
		$("#searchForumList").empty();
	    showGrid({
		 	id: "searchForumList",
		 	url: reqBasePath + "forumcontent018",
		 	params: {searchValue:searchValue},
		 	pagination: false,
		 	pagesize: 10,
		 	template: searchForumListTemplate,
		 	ajaxSendLoadBefore: function(hdb){
		 	},
		 	ajaxSendAfter:function(json){
		 		var row = json.rows;
		 		for(var i = 0;i < json.total; i++){
		 			var id = row[i].id;
		 			var forumContent = row[i].forumContent;
		 			$(".search-forum-desc").each(function(){
		 				var thisId  = $(this).parents('div[class^="my-forum-main"]').eq(0).attr("rowid");
			 			if(id == thisId){
			 				$(this).find("em").append(forumContent);
			 			}
				 	});
		 		}
		 	}
	    });
	}
	
	//搜索帖子详情
    $("body").on("click", "#searchForumList .my-forum-main em", function(e){
        var rowId = $(this).parents('div[class^="my-forum-main"]').eq(0).attr("rowid");
        location.href = '../../tpl/forumshow/forumitem.html?id=' + rowId;
    });
	
	//话题详情
	$("body").on("click", ".forum-invitation, .layui-text-center", function(e){
		var rowId = $(this).parents('div[class^="layuiadmin-card-text"]').eq(0).attr("rowid");
		location.href = '../../tpl/forumshow/forumitem.html?id=' + rowId;
	});
	
	//话题详情
	$("body").on("click", ".layuiadmin-card-status p", function(e){
		var rowId = $(this).attr("rowid");
		location.href = '../../tpl/forumshow/forumitem.html?id=' + rowId;
	});
	
	//标签点击事件
	$("body").on("click", "#hotTagList a", function(e){
		rowId = $(this).attr("rowid");
		location.href = "../../tpl/forumshow/forumtaglist.html?id=" + rowId;
	});
	
	//我的操作
	$("body").on("click", ".suspension-menu-icon", function(e){
		if($(".drop-down-menu").is(':hidden')){
			$(".drop-down-menu").show();
			$(".suspension-menu-icon").removeClass("rotate").addClass("rotate1");
		}else{
			$(".drop-down-menu").hide();
			$(".suspension-menu-icon").removeClass("rotate1").addClass("rotate");
		}
	});
	
	//加载最新贴子
	loadNewForumList();
	function loadNewForumList(){
		$("#newForumList").empty();
	    showGrid({
		 	id: "newForumList",
		 	url: reqBasePath + "forumcontent007",
		 	params: {},
		 	pagination: false,
		 	template: newForumListTemplate,
		 	ajaxSendLoadBefore: function(hdb, json){
				for(var i = 0;i < json.rows.length; i++) {
					json.rows[i].userPhoto = fileBasePath + json.rows[i].userPhoto;
				}
		 	},
		 	ajaxSendAfter:function(json){
		 	}
	    });
	}
	
	//加载最新评论
	loadNewCommentList();
	function loadNewCommentList(){
		$("#newCommentList").empty();
	    showGrid({
		 	id: "newCommentList",
		 	url: reqBasePath + "forumcontent013",
		 	params: {},
		 	pagination: false,
		 	template: newCommentListTemplate,
		 	ajaxSendLoadBefore: function(hdb, json){
				for(var i = 0;i < json.rows.length; i++) {
					json.rows[i].userPhoto = fileBasePath + json.rows[i].userPhoto;
				}
		 	},
		 	ajaxSendAfter:function(json){
		 	}
	    });
	}
	
	//加载热门标签
	loadHotTagList();
	function loadHotTagList(){
		$("#hotTagList").empty();
	    showGrid({
		 	id: "hotTagList",
		 	url: reqBasePath + "forumcontent015",
		 	params: {},
		 	pagination: false,
		 	template: hotTagListTemplate,
		 	ajaxSendLoadBefore: function(hdb){
		 	},
		 	ajaxSendAfter:function(json){
		 		$("#hotTagList").find("a[rowid='']").remove();
		 	}
	    });
	}
	
	// 加载活跃用户
	loadActiveUsersList();
	function loadActiveUsersList(){
		$("#activeUsersList").empty();
	    showGrid({
		 	id: "activeUsersList",
		 	url: reqBasePath + "forumcontent016",
		 	params: {},
		 	pagination: false,
		 	template: activeUsersListTemplate,
		 	ajaxSendLoadBefore: function(hdb, json){
				for(var i = 0;i < json.rows.length; i++) {
					json.rows[i].userPhoto = fileBasePath + json.rows[i].userPhoto;
				}
		 	},
		 	ajaxSendAfter:function(json){
		 	}
	    });
	}
	
	//加载热门贴
	loadHotForumList();
	function loadHotForumList(){
		$("#hotForumList").empty();
	    showGrid({
		 	id: "hotForumList",
		 	url: reqBasePath + "forumcontent017",
		 	params: {},
		 	pagination: false,
		 	template: hotForumListTemplate,
		 	ajaxSendLoadBefore: function(hdb, json){
				for(var i = 0;i < json.rows.length; i++) {
					json.rows[i].userPhoto = fileBasePath + json.rows[i].userPhoto;
				}
		 	},
		 	ajaxSendAfter:function(json){
		 		var row = json.rows;
                for(var i = 0;i < row.length; i++){
                    var id = row[i].id;
                    var tagName = row[i].tagName;
                    if(!isNull(tagName)){
                        var tagArr = tagName.split(",");
                        var tagStr = "";
                        for(var j = 0;j < tagArr.length; j++){
                            tagStr += "<a rowId='" + tagArr[j].split("-")[1] + "'>" + tagArr[j].split("-")[0] + "</a>"
                        }
                        $(".layui-text-bottom-a").each(function(){
                            var thisId  = $(this).parents('div[class^="layuiadmin-card-text"]').eq(0).attr("rowid");
                            if(thisId == id){
                                $(this).append(tagStr);
                            }
                        });
                    }
                }
		 	}
	    });
	}
	
	matchingLanguage();
	
	//标签点击事件
    $("body").on("click", "#hotForumList .layui-text-bottom-a a", function(e){
        rowId = $(this).attr("rowId");
        location.href = "../../tpl/forumshow/forumtaglist.html?id=" + rowId;
    });
	
    exports('forumlist', {});
});
