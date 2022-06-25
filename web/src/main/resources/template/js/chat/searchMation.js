layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
	    	form = layui.form;
	    
		showGrid({
		 	id: "search-group-list",
		 	url: reqBasePath + "companytalkgroup005",
		 	params: {groupNameOrNum: 'skyeye'},
		 	pagination: true,
		 	pagesize: 66,
		 	template: getFileContent('tpl/chat/searchMation.tpl'),
		 	ajaxSendLoadBefore: function(hdb){
		 		hdb.registerHelper('compareimg', function(v1, options) {
		 			if(isNull(v1)){
		        		return '../../assets/images/os_windows.png';
		        	}else{
		        		return fileBasePath + v1;
		        	}
		 		});
		 		
		 		hdb.registerHelper('compare1', function(v1, options) {
		 			if(isNull(v1)){
		        		return '暂无简介';
		        	}else{
		        		return v1;
		        	}
		 		});
		 		
		 		hdb.registerHelper('compare2', function(v1, v2, v3, v4, options) {
		 			if(!isNull(v1)){
		        		return '<font class="in-this-group">已在该群聊</font>';
		        	}else{
		        		if(v3 < v4){
		        			return '<button type="button" class="layui-btn layui-btn-xs layui-btn-normal inGroup" rowid="' + v2 + '">' + 
					        			'<i class="fa fa-plus"></i>' + 
					        			'<font>加群</font>' + 
				        			'</button>';
		        		}else{
		        			return '<font class="in-this-group">群聊人数已满</font>';
		        		}
		        	}
		 		});
		 	},
		 	options: {},
		 	ajaxSendAfter:function(json){
		 		matchingLanguage();
		 		form.render();
		 	}
	    });
	    
	    //查找
	    $("body").on("click", "#reSearch", function(){
	    	if(isNull($("#groupNameOrNum").val())){
	    		winui.window.msg('请输入搜索内容', {icon: 2, time: 2000});
	    	}else{
	    		refreshGrid("search-group-list", {params:{groupNameOrNum:$("#groupNameOrNum").val()}});
	    	}
	    });
	    
	    //加入群聊
	    $("body").on("click", ".inGroup", function(e){
	    	var _this = $(this);
	    	AjaxPostUtil.request({url:reqBasePath + "companytalkgroup006", params:{groupId: $(this).attr("rowid")}, type: 'json', callback: function(json){
 	   			if (json.returnCode == 0) {
 	   				_this.parent().html('<font>等待审核</font>');
 	   				winui.window.msg('等待管理员审核', {icon: 1, time: 2000});
 	   				if(json.total == '1'){
 	   					var sendMessage = {
							to: json.bean.inviteUserId,//收信人id
							type: 7,//群组邀请消息
						};
						parent.etiger.socket.send(JSON.stringify(sendMessage));
 	   				}
 	   			}else{
 	   				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
 	   			}
 	   		}});
	    });
	    
	});
	    
});