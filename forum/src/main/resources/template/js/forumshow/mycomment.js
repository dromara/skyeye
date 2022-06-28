
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	var $ = layui.$;
	
	//公共标题
	$("#forumTitle").html(getFileContent("tpl/forumshow/commontitle.tpl"));
	//菜单
	$("body").append(getFileContent("tpl/forumshow/commonmenu.tpl"));
	
	var addListTemplate = $('#addListTemplate').html();
	
	loadList();
	function loadList(){
		$("#addList").empty();
        //初始化数据
        showGrid({
             id: "addList",
             url: reqBasePath + "forumcontent021",
             params: {},
             pagination: true,
             pagesize: 12,
             template: addListTemplate,
             ajaxSendLoadBefore: function(hdb){
             },
             ajaxSendAfter:function (json) {
                 var row = json.rows;
                 for(var i = 0;i < row.length; i++){
                     var commentId = row[i].commentId;
                     var tagName = row[i].tagName;
                     if(!isNull(tagName)){
                         var tagArr = tagName.split(",");
                         var tagStr = "";
                         for(var j = 0;j < tagArr.length; j++){
                             tagStr += "<strong rowId='" + tagArr[j].split("-")[1] + "'>" + tagArr[j].split("-")[0] + "</strong>"
                         }
                         $(".my-forum-main-span").each(function(){
                             var thisId  = $(this).parents('div[class^="my-forum-main"]').eq(0).attr("commentId");
                             if(thisId == commentId){
                                 $(this).append(tagStr);
                                 if(!isNull(row[i].replyName)){
                                     $(this).parents('div[class^="my-forum-main"]').eq(0).find(".replyperson").append("<span>&nbsp;&nbsp;回复&nbsp;&nbsp;</span><span class='name-span'>" + row[i].replyName + "</span>");
                                 }
                             }
                         });
                     }
                 }
                 matchingLanguage();
             }
        });
	}
	
	//详情
    $("body").on("click", "#addList .my-forum-main em", function (e) {
        rowId = $(this).parents('div[class^="my-forum-main"]').eq(0).attr("forumId");
        location.href = '../../tpl/forumshow/forumitem.html?id=' + rowId;
    });
	
    //标签点击事件
    $("body").on("click", "#addList .my-forum-main-span strong", function (e) {
        rowId = $(this).attr("rowId");
        location.href = "../../tpl/forumshow/forumtaglist.html?id=" + rowId;
    });
    
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
	
    //删除
    $("body").on("click", "#addList .my-operator-list a", function (e) {
        rowId = $(this).parents('div[class^="my-forum-main"]').eq(0).attr("commentId");
        layer.confirm('确认删除该评论吗？', { icon: 3, title: '删除评论' }, function (index) {
            layer.close(index);
            
            AjaxPostUtil.request({url: reqBasePath + "forumcontent022", params:{rowId: rowId}, type: 'json', callback: function (json) {
                if (json.returnCode == 0) {
                    winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
                    loadList();
                } else {
                    winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
                }
            }});
        });
    });
	
    exports('mycomment', {});
});
