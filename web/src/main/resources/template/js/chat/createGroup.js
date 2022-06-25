layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'fileUpload'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
	    	form = layui.form;
	    
	    //初始化好友列表
	    $(".friend-list-box").html(parent.friendList);
	    $(".friend-list-box").find(".layim-tab-content").show();
	    $(".friend-list-box").find(".layui-layim-list").find("li").find("img").before('<input type="checkbox" name="friendCheckBox" class="friendCheckBox" lay-filter="friendCheckBox">');
	    $(".friend-list-box").find(".layui-layim-list").find("li").removeClass('layim-list-gray');
	    
	    //初始化上传
 		$("#groupImg").upload({
            "action": reqBasePath + "common003",
            "data-num": "1",
            "data-type": "PNG,JPG,jpeg,gif",
            "uploadType": 7,
            "function": function (_this, data) {
                show("#groupImg", data);
            }
        });
	    
        matchingLanguage();
		form.render();
		
		form.on('checkbox(friendCheckBox)', function (data) {
			if(this.checked){//选中
				var oldHtml = $(this).parent().prop("outerHTML");
				var removeDivHtml = $(oldHtml).find('.layui-form-checkbox').prop("outerHTML");
				var removeInputHtml = $(oldHtml).find('input').prop("outerHTML");
				oldHtml = oldHtml.replace(removeDivHtml, '').replace(removeInputHtml, '');
				$("#chooseFriend").append(oldHtml);
			} else {//取消选中
				var id = $(this).parent().attr("id");
				$("#chooseFriend").find('li[id="' + id + '"]').remove();
			}
		});
		
	    form.on('submit(formAddBean)', function (data) {
	        if (winui.verifyForm(data.elem)) {
	        	var params = {
        			groupName: $("#groupName").val(),
        			groupDesc: $("#groupDesc").val(),
        			userIds: '',
 	        	};
 	        	params.groupImg = $("#groupImg").find("input[type='hidden'][name='upload']").attr("oldurl");
 	        	if(isNull(params.groupImg)){
 	        		winui.window.msg('请上传群组头像', {icon: 2, time: 2000});
 	        		return false;
 	        	}
 	        	if($("#chooseFriend").find('li').length == 0){
 	        		winui.window.msg('群内至少拥有两名成员。', {icon: 2, time: 2000});
 	        		return false;
 	        	}
 	        	$.each($("#chooseFriend").find('li'), function(i, item){
 	        		params.userIds = params.userIds + $(item).attr("id").replace('layim-friend', '') + ",";
 	        	});
 	        	
 	        	AjaxPostUtil.request({url:reqBasePath + "companytalkgroup001", params:params, type: 'json', callback: function(json){
	 	   			if (json.returnCode == 0) {
	 	   				parent.friendChooseList = params.userIds;
	 	   				parent.layim.addList({
	 	   					type: 'group', //列表类型，只支持friend和group两种
	 	   					avatar: json.bean.groupImg, //群组头像
	 	   					groupname: json.bean.groupName, //群组名称
	 	   					id: json.bean.id, //群组id
	 	   				});
		 	   			parent.layer.close(index);
		 	        	parent.refreshCode = '0';
	 	   			} else {
	 	   				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
	 	   			}
	 	   		}});
	        }
	        return false;
	    });
	    
	    
	    $("body").on("click", ".layim-list-friend li", function(e){
	    	if($(this).find("ul").hasClass("layui-show")){
	    		$(this).find("ul").removeClass("layui-show");
	    		$(this).find("h5").find("i").html('&#xe602;');
	    	} else {
	    		$(this).find("ul").addClass("layui-show");
	    		$(this).find("h5").find("i").html('&#xe61a;');
	    	}
	    });
	    
	    $("body").on("click", ".layim-list-friend ul", function(e){
	    	e.stopPropagation();
	    });
	    
	    //取消
	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	});
	    
});