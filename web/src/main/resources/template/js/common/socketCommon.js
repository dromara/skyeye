
var etiger = {};

layui.define(["jquery"], function(exports) {
	var jQuery = layui.jquery;
	(function($) {
		// 消息socket
    	etiger.socket = {
			webSocket : "",
			init : function() {
				if (!window.WebSocket) {
					alert("你的浏览器不支持websocket，请升级到IE10以上浏览器，或者使用谷歌、火狐、360浏览器。");
					return;
				}
				webSocket = new WebSocket(webSocketPath + "talkwebsocket/" + userId);
				webSocket.onerror = function(event) {
					alert("websockt连接发生错误，请刷新页面重试!")
				};
				webSocket.onopen = function(event) {
					console.log("%c open socket success", "color: blue");
				};
				webSocket.onmessage = function(event) {
					var received_msg = event.data;
	    			try {
	            	    if (typeof JSON.parse(received_msg) == "object") {
	            		    var jsonData = JSON.parse(received_msg);
	            		    if(jsonData.messageType == '1'){//上线提醒
	            		    	layim.setFriendStatus(jsonData.userId, 'online');
	            		    } else if (jsonData.messageType == '2'){//下线提醒
	            		    	layim.setFriendStatus(jsonData.userId, 'offline');
	            		    } else if (jsonData.messageType == '3'){//在线名单
	            		    	$.each(jsonData.onlineUsers, function(i, item) {
	            		    		layim.setFriendStatus(item, 'online');
	            		    	});
	            		    } else if (jsonData.messageType == '4'){//普通消息
	    						layimGetMessage(jsonData.username, jsonData.avatar, jsonData.fromId, 'friend', jsonData.textMessage);
	            		    } else if (jsonData.messageType == '5'){//系统消息
	            		    	sendSystemMsg(jsonData);
	            		    } else if (jsonData.messageType == '6'){//全体消息
	            		    	
	            		    } else if (jsonData.messageType == '7'){//群组邀请消息
	            		    	var messageTotal = layim.getMessageTotal();
	            		    	messageTotal++;
	            		    	layim.msgbox(messageTotal);
	            		    } else if (jsonData.messageType == '8'){//隐身消息
	            		    	layim.setFriendStatus(jsonData.userId, 'offline');
	            		    } else if (jsonData.messageType == '9'){//隐身上线消息
	            		    	layim.setFriendStatus(jsonData.userId, 'online');
	            		    } else if (jsonData.messageType == '10'){//搜索账号入群审核同意后通知用户加载群信息
	    						var groupId = jsonData.id;
	    						var groupname = jsonData.groupname;
	    						var avatar = jsonData.avatar;
	    						layim.addList({
			 	   					type: 'group', //列表类型，只支持friend和group两种
			 	   					avatar: avatar, //群组头像
			 	   					groupname: groupname, //群组名称
			 	   					id: groupId //群组id
			 	   				});
	            		    } else if (jsonData.messageType == '11'){//群聊
	    						layimGetMessage(jsonData.username, jsonData.avatar, jsonData.id, 'group', jsonData.textMessage);
	            		    } else if (jsonData.messageType == '12'){//退出群聊--群主接收消息
	            		    	var groupId = jsonData.groupId;
	            		    	var userName = jsonData.userName;
	            		    	layimGetMessage('系统消息', '../../assets/images/eye.png', groupId, 'group', userName + '退出了群聊。');
	            		    } else if (jsonData.messageType == '13'){//解散群聊--群员接收消息
	            		    	var groupId = jsonData.id;
	            		    	var userName = jsonData.userName;
	            		    	layimGetMessage('系统消息', '../../assets/images/eye.png', groupId, 'group', userName + '解散了群聊。');
	            		    	layim.removeList({
	            		            id: groupId,
	            		            type: 'group'
	            		    	});
	            		    } else if (jsonData.messageType == '1301'){//群聊被解散后，成员再次发送消息，返回的内容
	            		    	layimGetMessage('系统消息', '../../assets/images/eye.png', jsonData.groupId, 'group', '该群聊已被解散，消息发送失败。');
	            		    }
	            	    }
	                } catch(e) {
	                	console.log(e);
	                }
				}
			},
			send : function(data) {
				this.waitForConnection(function() {// 连接建立才能发送消息
					webSocket.send(data);
				}, 500);
			},
			sendData : function(data) {
				this.waitForConnection(function() {
					webSocket.send(data);
				}, 500);
			},
			waitForConnection : function(callback, interval) {// 判断连接是否建立
				if (webSocket.readyState === 1) {
					callback();
				} else {
					var that = this;
					setTimeout(function() {
						that.waitForConnection(callback, interval);
					}, interval);
				}
			},
			close: function(){
				webSocket.close();
			}
		};
		
		function sendInfoMsg(message){
			sendMsg(message, 'info');
		}
		
		function sendSuccessMsg(message){
			sendMsg(message, 'success');
		}
		
		function sendErrorMsg(message){
			sendMsg(message, 'error');
		}
		
		function sendWarningMsg(message){
			sendMsg(message, 'warning');
		}
		
		function sendMsg(message, style){
			spop({
				template: message,
				position: 'bottom-right',
				style: style
			});
		}
		
		function sendSystemMsg(json){
			// 所属大类  1.我的输出  2.我的发送  3.我的获取
			if(json.bigType == 1){
				sendOneType(json);
			} else if (json.bigType == 2){
				sendTwoType(json);
			} else if (json.bigType == 3){
				sendThreeType(json);
			}
		}
		
		function sendOneType(json){
			var str = "";
			if(json.jobType == 100){
				str = '笔记输出压缩包 ' + getResultMsg(json);
			}
			sendByState(json, str);
		}
		
		function sendTwoType(json){
			var str = "";
			
			sendByState(json, str);
		}
		
		function sendThreeType(json){
			var str = "";
			if(json.jobType == 2){
				str = '收件箱邮件获取 ' + getResultMsg(json);
			} else if (json.jobType == 3){
				str = '已发送邮件获取 ' + getResultMsg(json);
			} else if (json.jobType == 4){
				str = '已删除邮件获取 ' + getResultMsg(json);
			} else if (json.jobType == 5){
				str = '草稿箱邮件获取 ' + getResultMsg(json);
			}
			sendByState(json, str);
		}
		
		function getResultMsg(json){
			if(json.state == 3){
				return '执行失败';
			} else if (json.state == 4){
				return '执行成功';
			} else if (json.state == 5){
				return '执行部分成功';
			}
		}
		function sendByState(json, str){
			if(json.state == 3){
				sendErrorMsg(str);
			} else if (json.state == 4){
				sendSuccessMsg(str);
			} else if (json.state == 5){
				sendWarningMsg(str);
			}
		}
		
		function layimGetMessage(userName, avatar, id, type, content){
			layim.getMessage({
				username: userName,
				avatar: avatar,
				id: id,
				type: type,
				content: content
			});
		}
		
	})(jQuery);
});