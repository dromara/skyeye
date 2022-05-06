
// 个人中心
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form', 'flow'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		form = layui.form,
		table = layui.table,
		flow = layui.flow,
		device = layui.device();
		
	var forumTemplate = $('#forumTemplate').html();

	// 获取当前登录员工信息
	systemCommonUtil.getSysCurrentLoginUserMation(function (data){
		$(".layadmin-homepage-pad-img").attr("src", fileBasePath + data.bean.userPhoto);
		$(".layadmin-homepage-font").html(data.bean.jobNumber + " " + data.bean.userName);
		$(".layadmin-homepage-min-font").html(data.bean.companyName + " " + data.bean.departmentName + " " + data.bean.jobName);
	});
	loadMyOtherMation();
	matchingLanguage();

	// 加载我的其他信息
	function loadMyOtherMation(){
		AjaxPostUtil.request({url: reqBasePath + "sys032", params: {}, type: 'json', callback: function(json) {
			if(json.returnCode == 0) {
				$(".layadmin-homepage-pad-hor").find("mdall").html(json.bean.userSign);
				// 加载我的论坛
				loadMyForum();
			} else {
				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
			}
		}});
	}
	
	// 加载我的论坛
	function loadMyForum(){
		$("#myForum").html('');
		flow.load({
			elem: '#myForum',
			isAuto: true,
			scrollElem: "#myForum",
			done: function(page, next) {
				var lis = [];
				AjaxPostUtil.request({url:reqBasePath + "forumcontent001", params:{page: page, limit: 15}, type: 'json', callback: function(json){
		   			if(json.returnCode == 0){
						lis.push(getDataUseHandlebars(forumTemplate, json));
						next(lis.join(''), (page * 15) < json.total);
						matchingLanguage();
						loadMyForumTag(json);
		   			}else{
		   				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
		   			}
		   		}});
			}
		});
	}
	
	// 加载论坛标签
	function loadMyForumTag(json){
		var rows = json.rows;
 		for(var i = 0;i < rows.length; i++){
 			var id = rows[i].id;
 			var tagName = rows[i].tagName;
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
	}
	
	// 论坛点击
	$("body").on("click", "#myForum .my-operator-list a", function(e){
		var rowId = $(this).parents('div[class^="my-forum-main"]').eq(0).attr("rowId");
		var type = $(this).attr("type");
		if(type == "edit"){
			edit(rowId);
		}else if(type == "delete"){
			del(rowId);
		}
	});
	
	// 论坛编辑
	function edit(rowId){
		openOtherPage('forumBtn', '../../tpl/forumshow/myforumedit.html?id=' + rowId, '<i class="fa fa-calendar"></i>论坛');
	}
	
	// 论坛删除
	function del(rowId){
		layer.confirm('确认删除该帖子吗？', { icon: 3, title: '删除帖子' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url:reqBasePath + "forumcontent003", params:{rowId: rowId}, type: 'json', callback: function(json){
    			if(json.returnCode == 0){
    				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1,time: 2000});
    				loadMyForum();
    			}else{
    				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
    			}
    		}});
		});
	}
	
	// 论坛详情
	$("body").on("click", "#myForum .my-forum-main .forum-desc, .my-forum-main em", function(e){
		var rowId = $(this).parents('div[class^="my-forum-main"]').eq(0).attr("rowId");
		openOtherPage('forumBtn', '../../tpl/forumshow/forumitem.html?id=' + rowId, '<i class="fa fa-calendar"></i>论坛');
	});
	
	// 论坛标签点击事件
	$("body").on("click", "#myForum .my-forum-main-span strong", function(e){
		var rowId = $(this).attr("rowId");
		openOtherPage('forumBtn', "../../tpl/forumshow/forumtaglist.html?id=" + rowId, '<i class="fa fa-calendar"></i>论坛');
	});
	
	//修改密码
	$("body").on("click", "#updatePassword", function(e){
		_openNewWindows({
			url: "../../tpl/syspersonal/editpassword.html", 
			title: "修改密码",
			skin: 'update-password-html',
			pageId: "editpassword",
			area: ['700px', '300px'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1,time: 2000});
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
                }
			}});
	});
	
	function openOtherPage(layId, url, title){
		// 否则判断该tab项是否以及存在
		var isData = false; // 初始化一个标志，为false说明未打开该tab项 为true则说明已有
		$.each(parent.$("#LAY_app_tabsheader li[lay-id]"), function() {
			// 如果点击左侧菜单栏所传入的id 在右侧tab项中的lay-id属性可以找到，则说明该tab项已经打开
			if($(this).attr("lay-id") === layId) {
				isData = true;
			}
		})
		rowId = $(this).parent().attr("id");
		if(isData) {
			parent.active.tabDelete(layId);
		}
		// 标志为false 新增一个tab项
		parent.active.tabAdd(url, layId, title);
    	parent.active.tabChange(layId);
	}
    
    exports('syspersonal', {});
});
