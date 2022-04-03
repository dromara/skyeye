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
	    
	    //初始化数据
	    showGrid({
		 	id: "invitation-list",
		 	url: reqBasePath + "companytalkgroup002",
		 	params: {},
		 	pagination: true,
		 	pagesize: 20,
		 	template: getFileContent('tpl/chat/invitationItem.tpl'),
		 	ajaxSendLoadBefore: function(hdb){
		 		hdb.registerHelper('compareimg', function(v1, options) {
		 			if(isNull(v1)){
		        		return '../../assets/images/os_windows.png';
		        	}else{
		        		return fileBasePath + v1;
		        	}
		 		});
		 		hdb.registerHelper('compare1', function(v1, options) {
		 			if(v1 == '1'){
		 				return "邀请您加入群聊";
		 			}else if(v1 == '2'){
		 				return "申请加入群聊";
		 			}else{
		 				return "参数错误";
		 			}
		 		});
		 		hdb.registerHelper('compare2', function(v1, v2, options) {
		 			if(v1 == '0'){
		 				return '<button type="button" class="layui-btn layui-btn-xs layui-btn-normal aggreIn" rowid="' + v2 + '">同意</button>' +
		 						'<button type="button" class="layui-btn layui-btn-xs layui-btn-danger refuseIn" rowid="' + v2 + '">拒绝</button>';
		 			}else if(v1 == '1'){
		 				return "<font>已同意</font>";
		 			}else if(v1 == '2'){
		 				return "<font>已拒绝</font>";
		 			}else{
		 				return "参数错误";
		 			}
		 		});
		 	},
		 	options: {},
		 	ajaxSendAfter:function(json){
		 		matchingLanguage();
		 		form.render();
		 		//同意
			    $("body").on("click", ".aggreIn", function(e){
			    	var _this = $(this);
			    	AjaxPostUtil.request({url:reqBasePath + "companytalkgroup003", params:{rowId: $(this).attr("rowid")}, type: 'json', callback: function(json){
		 	   			if(json.returnCode == 0){
		 	   				_this.parent().html('<font>已同意</font>');
		 	   				if(json.bean.inGroupType == '1'){//被邀请进群
			 	   				parent.layim.addList({
			 	   					type: 'group', //列表类型，只支持friend和group两种
			 	   					avatar: fileBasePath + json.bean.groupImg, //群组头像
			 	   					groupname: json.bean.groupName, //群组名称
			 	   					id: json.bean.id, //群组id
			 	   				});
		 	   				}else if(json.bean.inGroupType == '2'){//搜索账号进群
			 	   				var sendMessage = {
			 	   					to: json.bean.userId,//发送人id
			 	   					type: 10,
				 	   				avatar: fileBasePath + json.bean.groupImg, //群组头像
			 	   					groupname: json.bean.groupName, //群组名称
			 	   					id: json.bean.id, //群组id
			 	   				};
			 	   				parent.etiger.socket.send(JSON.stringify(sendMessage));
		 	   				}
		 	   				winui.window.msg('操作成功', {icon: 1,time: 2000});
		 	   			}else{
		 	   				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
		 	   			}
		 	   		}});
			    });
			    
			    //拒绝
			    $("body").on("click", ".refuseIn", function(e){
			    	var _this = $(this);
			    	AjaxPostUtil.request({url:reqBasePath + "companytalkgroup004", params:{rowId: $(this).attr("rowid")}, type: 'json', callback: function(json){
		 	   			if(json.returnCode == 0){
		 	   				_this.parent().html('<font>已拒绝</font>');
		 	   				winui.window.msg('操作成功', {icon: 1,time: 2000});
		 	   			}else{
		 	   				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
		 	   			}
		 	   		}});
			    });
		 	}
	    });
	    
	});
});