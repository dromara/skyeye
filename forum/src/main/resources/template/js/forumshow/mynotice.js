
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window',
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
             url: reqBasePath + "forumcontent023",
             params: {},
             pagination: true,
             pagesize: 12,
             template: addListTemplate,
             ajaxSendLoadBefore: function(hdb){
             },
             ajaxSendAfter:function (json) {
                 var row = json.rows;
                 for(var i = 0;i < row.length; i++){
                     if(row[i].noticeTitle == "举报"){
                         var id = row[i].id;
                         $(".forum-state").each(function(){
                             var thisId  = $(this).parents('div[class^="my-forum-main"]').eq(0).attr("rowId");
                             if(thisId == id){
                                 var content = row[i].content;
                                 if(content == "2"){
                                     content = "审核通过"
                                     $(this).addClass("state-new");
                                 }else if(content == "3"){
                                     content = "审核不通过"
                                     $(this).addClass("state-down");
                                 }
                                 $(this).append(content);
                             }
                         });
                     }else if(row[i].noticeTitle == "违规"){
                         var id = row[i].id;
                         $(".my-forum-main-span").each(function(){
                             var thisId  = $(this).parents('div[class^="my-forum-main"]').eq(0).attr("rowId");
                             if(thisId == id){
                                 $(this).find("strong").addClass("violation");
                             }
                         });
                     }
                 }
                 matchingLanguage();
             }
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
        rowId = $(this).parents('div[class^="my-forum-main"]').eq(0).attr("forumId");
        location.href = '../../tpl/forumshow/forumitem.html?id=' + rowId;
    });
	
    //删除
    $("body").on("click", "#addList .my-operator-list a", function (e) {
        rowId = $(this).parents('div[class^="my-forum-main"]').eq(0).attr("rowId");
        layer.confirm('确认删除该通知吗？', { icon: 3, title: '删除通知' }, function (index) {
            layer.close(index);
            AjaxPostUtil.request({url: reqBasePath + "forumcontent024", params:{rowId: rowId}, type: 'json', callback: function (json) {
                winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
                loadList();
            }});
        });
    });
	
    exports('mynotice', {});
});
