var rowId = "";
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	var $ = layui.$;
	
	var addListTemplate = $('#addListTemplate').html();
	
	//公共标题
	$("#forumTitle").html(getFileContent("tpl/forumshow/commontitle.tpl"));
	//菜单
	$("body").append(getFileContent("tpl/forumshow/commonmenu.tpl"));
	
	loadList();
	function loadList(){
		$("#addList").empty();
		//初始化数据
	    showGrid({
		 	id: "addList",
		 	url: reqBasePath + "forumcontent001",
		 	params: {},
		 	pagination: true,
		 	pagesize: 12,
		 	template: addListTemplate,
		 	ajaxSendLoadBefore: function(hdb){
		 	},
		 	ajaxSendAfter:function (json) {
		 		var row = json.rows;
		 		for(var i = 0;i < row.length; i++){
		 			var id = row[i].id;
		 			var tagName = row[i].tagName;
		 			if(!isNull(tagName)){
		 				var tagArr = tagName.split(",");
		 				var tagStr = "";
			 			for(var j = 0;j < tagArr.length; j++){
			 				tagStr += "<strong rowId='" + tagArr[j].split("-")[1] + "'>" + tagArr[j].split("-")[0] + "</strong>"
			 			}
			 			$(".my-forum-main-span").each(function(){
				 			var thisId  = $(this).parents('div[class^="my-forum-main"]').eq(0).attr("rowId");
				 			if(thisId == id){
				 				$(this).append(tagStr);
				 			}
					 	});
		 			}
		 		}
		 		matchingLanguage();
		 	}
	    });
	}
	
	$("body").on("click", "#addList .my-operator-list a", function (e) {
		rowId = $(this).parents('div[class^="my-forum-main"]').eq(0).attr("rowId");
		var type = $(this).attr("type");
		if(type == "edit"){
			edit();
		}else if(type == "delete"){
			del();
		}
	});
	
	//编辑
	function edit(){
		location.href = '../../tpl/forumshow/myforumedit.html?id=' + rowId;
	}
	
	//删除
	function del(){
		layer.confirm('确认删除该帖子吗？', { icon: 3, title: '删除帖子' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: reqBasePath + "forumcontent003", params:{rowId: rowId}, type: 'json', callback: function (json) {
    			if (json.returnCode == 0) {
    				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
    				loadList();
    			} else {
    				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
    			}
    		}});
		});
	}
	
	//我的操作
	$("body").on("click", ".suspension-menu-icon", function (e) {
		if($(".drop-down-menu").is(':hidden')){
			$(".drop-down-menu").show();
			$(".suspension-menu-icon").removeClass("rotate").addClass("rotate1");
		} else {
			$(".drop-down-menu").hide();
			$(".suspension-menu-icon").removeClass("rotate1").addClass("rotate");
		}
	});
	
	//详情
	$("body").on("click", "#addList .my-forum-main .forum-desc, .my-forum-main em", function (e) {
		rowId = $(this).parents('div[class^="my-forum-main"]').eq(0).attr("rowId");
		location.href = '../../tpl/forumshow/forumitem.html?id=' + rowId;
	});
	
	//标签点击事件
	$("body").on("click", "#addList .my-forum-main-span strong", function (e) {
		rowId = $(this).attr("rowId");
		location.href = "../../tpl/forumshow/forumtaglist.html?id=" + rowId;
	});
	
    exports('myposts', {});
});
