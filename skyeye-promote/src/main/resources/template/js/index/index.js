layui.config({
    base: basePath, //指定 winui 路径
    version: skyeyeVersion
}).extend({  //指定js别名
    window: 'js/winui.window',
    desktop: 'js/winui.desktop',//桌面加载模块
    start: 'js/winui.start',//左下角开始菜单
    helper: 'js/winui.helper'
}).define(['window', 'desktop', 'start', 'helper', 'layim'], function (exports) {
    var $ = layui.jquery,
    layim = layui.layim;
    
    $(function () {
    	
    	AjaxPostUtil.request({url:reqBasePath + "login002", params:{}, type:'json', callback:function(json){
   			if(json.returnCode == 0){
//   				winui.window.msg('Welcome To WinAdmin', {
//   		            time: 4500,
//   		            offset: '40px',
//   		            btn: ['点击进入全屏'],
//   		            btnAlign: 'c',
//   		            yes: function (index) {
//   		                winui.fullScreen(document.documentElement);
//   		                layer.close(index);
//   		            }
//   		        });

//   		        winui.window.open({
//   		            id: '公告',
//   		            type: 1,
//   		            title: '演示公告',
//   		            content: '<p style="padding:20px;">半成品仅供参观，多数设置本地存储，清除浏览器缓存即失效。<br/><br/>慢工出细活，如有需要的朋友请耐心等待。<br/><br/>望社区案例多多点赞，谢谢各位！<br/><br/>特色很多，如：<span style="color:#FF5722">桌面助手，主题设置</span>，大家慢慢参观</p>',
//   		            area: ['400px', '400px']
//   		        });
   				if(isNull(json.bean.winBgPicUrl)){
   					json.bean.winBgPicUrl = fileBasePath + '/assets/winbgpic/default.jpg';
   				}else{
   					json.bean.winBgPicUrl = fileBasePath + json.bean.winBgPicUrl;
   				}
   				if(isNull(json.bean.winLockBgPicUrl)){
   					json.bean.winLockBgPicUrl = fileBasePath + '/assets/winbgpic/default.jpg';
   				}else{
   					json.bean.winLockBgPicUrl = fileBasePath + json.bean.winLockBgPicUrl;
   				}
   		        initWinConfig(json);
   		        
   		        //加载聊天
   		        initTalk();
   		        
   			}else{
   				location.href = "login.html";
   			}
   		}});
    	
    });
    
    //自动回复
    var autoReplay = [
		'您好，我现在有事不在，一会再和您联系。',
		'你没发错吧？face[微笑] ',
		'洗澡中，请勿打扰，偷窥请购票，个体四十，团体八折，订票电话：一般人我不告诉他！face[哈哈] ',
		'你好，我是主人的美女秘书，有什么事就跟我说吧，等他回来我会转告他的。face[心] face[心] face[心] ',
		'face[威武] face[威武] face[威武] face[威武] ',
		'<（@￣︶￣@）>',
		'你要和我说话？你真的要和我说话？你确定自己想说吗？你一定非说不可吗？那你说吧，这是自动回复。',
		'face[黑线]  你慢慢说，别急……',
		'(*^__^*) face[嘻嘻] ，是天眼吗？'
	];
    
    function initTalk(){
    	layim.config({
			brief: false,// 是否简约模式（如果true则不显示主面板）
			title: '天眼',
			init: {
				url: 'getList.json',
				data: {}
			},//好友接口
			members: {
				url: 'getMembers.json',
				data: {}
			},//群员借口
			uploadImage: {
				url: '/upload/image', //（返回的数据格式见下文）
				type: '' //默认post
			},//上传图片接口
			uploadFile: {
				url: '/upload/file', //（返回的数据格式见下文）
				type: '' //默认post
			},//上传文件接口
			tool: [{
				alias: 'code',
				title: '代码',
				icon: '&#xe64e;'
			}],//扩展工具栏
			initSkin: '5.jpg', //1-5 设置初始背景
			notice: true, //是否开启桌面消息提醒，默认false
			msgbox: layui.cache.dir + 'css/modules/layim/html/msgbox.html', //消息盒子页面地址，若不开启，剔除该项即可
			find: layui.cache.dir + 'css/modules/layim/html/find.html', //发现页面地址，若不开启，剔除该项即可
			chatLog: layui.cache.dir + 'css/modules/layim/html/chatLog.html', //聊天记录页面地址，若不开启，剔除该项即可
		});
    	
    	//监听在线状态的切换事件
		layim.on('online', function(data) {
			//console.log(data);
		});
		
		//监听签名修改
		layim.on('sign', function(value) {
			//console.log(value);
		});
		
		layim.on('tool(code)', function(insert) {
			layer.prompt({
				title: '插入代码',
				formType: 2,
				shade: 0
			}, function(text, index) {
				layer.close(index);
				insert('[pre class=layui-code]' + text + '[/pre]'); //将内容插入到编辑器
			});
		});
		
		//监听layim建立就绪
		layim.on('ready', function(res) {

			//console.log(res.mine);
			layim.msgbox(5); //模拟消息盒子有新消息，实际使用时，一般是动态获得

			//添加好友（如果检测到该socket）
			layim.addList({
				type: 'group',
				avatar: "http://tva3.sinaimg.cn/crop.64.106.361.361.50/7181dbb3jw8evfbtem8edj20ci0dpq3a.jpg",
				groupname: 'Angular开发',
				id: "12333333",
				members: 0
			});
			layim.addList({
				type: 'friend',
				avatar: "http://tp2.sinaimg.cn/2386568184/180/40050524279/0",
				username: '冲田杏梨',
				groupid: 2,
				id: "1233333312121212",
				remark: "本人冲田杏梨将结束AV女优的工作"
			});

			setTimeout(function() {
				//接受消息（如果检测到该socket）
//				layim.getMessage({
//					username: "Hi",
//					avatar: "http://qzapp.qlogo.cn/qzapp/100280987/56ADC83E78CEC046F8DF2C5D0DD63CDE/100",
//					id: "10000111",
//					type: "friend",
//					content: "临时：" + new Date().getTime()
//				});

			}, 3000);
		});
		
		//监听发送消息
		layim.on('sendMessage', function(data) {
			var To = data.to;
			//console.log(data);
			if(To.type === 'friend') {
				layim.setChatStatus('<span style="color:#FF5722;">对方正在输入。。。</span>');
			}

			//演示自动回复
			setTimeout(function() {
				var obj = {};
				if(To.type === 'group') {
					obj = {
						username: '模拟群员' + (Math.random() * 100 | 0),
						avatar: layui.cache.dir + 'images/face/' + (Math.random() * 72 | 0) + '.gif',
						id: To.id,
						type: To.type,
						content: autoReplay[Math.random() * 9 | 0]
					};
				} else {
					obj = {
						username: To.name,
						avatar: To.avatar,
						id: To.id,
						type: To.type,
						content: autoReplay[Math.random() * 9 | 0]
					};
					layim.setChatStatus('<span style="color:#FF5722;">在线</span>');
				}
				layim.getMessage(obj);
			}, 1000);
		});
		
		//监听查看群员
		layim.on('members', function(data) {
			//console.log(data);
		});

		//监听聊天窗口的切换
		layim.on('chatChange', function(res) {
			var type = res.data.type;
			console.log(res.data.id)
			if(type === 'friend') {
				//模拟标注好友状态
				//layim.setChatStatus('<span style="color:#FF5722;">在线</span>');
			} else if(type === 'group') {
				//模拟系统消息
				layim.getMessage({
					system: true,
					id: res.data.id,
					type: "group",
					content: '模拟群员' + (Math.random() * 100 | 0) + '加入群聊'
				});
			}
		});
    }
    
    //初始化配置信息
    function initWinConfig(json){
    	
    	//设置窗口点击事件
    	$("body").on("dblclick", ".sec-clsss-btn", function(e){
    		winui.window.close($('#childWindow').parent());
    		OpenWindow($(this).prop("outerHTML"));
    	});
    	
    	winui.config({
            settings: {
                color: json.bean.winThemeColor,
                taskbarMode: json.bean.winTaskPosition,
                startSize: json.bean.winStartMenuSize,
                bgSrc: json.bean.winBgPicUrl,
                lockBgSrc: json.bean.winLockBgPicUrl
            },
            desktop: {//桌面菜单栏
                options: {
                	url: reqBasePath + 'login004',
                    method: 'get',
                    data: {loginPCIp: returnCitySN["cip"]}
                },    //可以为{}  默认 请求 json/desktopmenu.json
                done: function (desktopApp) {
                    desktopApp.ondblclick(function (id, elem) {
                    	var item = $(elem);
                    	if(item.find(".icon-drawer").length > 0){
                    	}else{
                    		OpenWindow(elem);
                    	}
                    });
                    desktopApp.onclick(function (id, elem) {
                    	var item = $(elem);
                    	if(item.find(".icon-drawer").length > 0){
                    		showBigWin(elem);
                    	}
                    }),
                    desktopApp.contextmenu({
                        item: ["打开", "删除", '右键菜单可自定义'],
                        item1: function (id, elem) {
                        	var item = $(elem);
                        	if(item.find(".icon-drawer").length > 0){
                        		showBigWin(elem);
                        	}else{
                        		OpenWindow(elem);
                        	}
                        },
                        item2: function (id, elem, events) {
                            winui.window.msg('删除回调');
                            $(elem).remove();
                            //从新排列桌面app
                            events.reLocaApp();
                        },
                        item3: function (id, elem, events) {
                            winui.window.msg('自定义回调');
                        }
                    });
                }
            },
            menu: {//左下角菜单栏右键效果
                options: {
                    url: reqBasePath + 'login005',
                    method: 'get',
                    data: {loginPCIp: returnCitySN["cip"]}
                },
                done: function (menuItem) {
                    //监听开始菜单点击
                    menuItem.onclick(function (elem) {
                        OpenWindow(elem);
                    });
                    menuItem.contextmenu({
                        item: [{icon: 'fa-cog', text: '设置'}, 
                               {icon: 'fa-close', text: '关闭'}, 
                               {icon: 'fa-qq', text: '右键菜单可自定义'}],
                        item1: function (id, elem) {
                            //设置回调
                            console.log(id);
                            console.log(elem);
                        },
                        item2: function (id, elem) {
                            //关闭回调
                        },
                        item3: function (id, elem) {
                            winui.window.msg('自定义回调');
                        }
                    });
                }
            }
        }).init({
            audioPlay: true, //是否播放音乐（开机音乐只会播放一次，第二次播放需要关闭当前页面从新打开，刷新无用）
            renderBg: true //是否渲染背景图 （由于js是写在页面底部，所以不太推荐使用这个来渲染，背景图应写在css或者页面头部的时候就开始加载）
        }, function () {
            //初始化完毕回调
        });
    }

    //开始菜单磁贴点击
    $('.winui-tile').on('click', function () {
        OpenWindow(this);
    });

    //开始菜单左侧主题按钮点击
    $('.winui-start-item.winui-start-individuation').on('click', function () {
        winui.window.openTheme();
    });
    
    //个人中心点击
    $('.winui-start-item.winui-start-syspersonal').on('click', function () {
        winui.window.openSysPersonal();
    });

    //打开窗口的方法（可自己根据需求来写）
    function OpenWindow(menuItem) {
    	AjaxPostUtil.request({url:reqBasePath + "login002", params:{}, type:'json', callback:function(json){
   			if(json.returnCode == 0){
   				var $this = $(menuItem);
   		        var url = $this.attr('win-url');
   		        var title = $this.attr('win-title');
   		        var id = $this.attr('win-id');
   		        var type = parseInt($this.attr('win-opentype'));
   		        var maxOpen = parseInt($this.attr('win-maxopen')) || -1;
   		        if (url == 'theme') {
   		            winui.window.openTheme();
   		            return;
   		        }
   		        if (!url || !title || !id) {
   		            winui.window.msg('菜单配置错误（菜单链接、标题、id缺一不可）');
   		            return;
   		        }
   		        var content;
   		        if (type === 1) {
   		            $.ajax({
   		                type: 'get',
   		                url: url,
   		                async: false,
   		                success: function (data) {
   		                    content = data;
   		                },
   		                error: function (e) {
   		                    var page = '';
   		                    switch (e.status) {
   		                        case 404:
   		                            page = '404.html';
   		                            break;
   		                        case 500:
   		                            page = '500.html';
   		                            break;
   		                        default:
   		                            content = "打开窗口失败";
   		                    }
   		                    $.ajax({
   		                        type: 'get',
   		                        url: reqBasePath + 'tpl/sysmessage/' + page,
   		                        async: false,
   		                        success: function (data) {
   		                            content = data;
   		                        },
   		                        error: function () {
   		                            layer.close(load);
   		                        }
   		                    });
   		                }
   		            });
   		        } else {
   		            content = url;
   		        }
   		        //核心方法（参数请看文档，config是全局配置 open是本次窗口配置 open优先级大于config）
   		        winui.window.config({
   		            anim: 0,
   		            miniAnim: 0,
   		            maxOpen: -1
   		        }).open({
   		            id: id,
   		            type: type,
   		            title: title,
   		            content: content,
   		            area: ['90vw','90vh'],
   		            //,offset: ['10vh', '15vw']
   		            maxOpen: maxOpen,
   		            //, max: false
   		            //, min: false
   		            refresh:true
   		        });
   			}else{
   				location.href = "login.html";
   			}
   		}});
    }
    
    function showBigWin(menuItem){
    	var menu = $(menuItem);
    	winui.window.config({
            anim: 0,
            miniAnim: 0,
            maxOpen: -1
        }).open({
        	id: 'childWindow',
            type: 1,
    		title: false,
    		closeBtn: 1,
            content: menu.find(".icon-child").html(),
            area: ['30vw', '40vh'],
            shadeClose: true,
            skin: 'sec-clsss',
            scrollbar: false,
            shade: 0.5,
            maxmin: false
        });
    }

    //注销登录
    $('.logout').on('click', function () {
        winui.hideStartMenu();
        winui.window.confirm('确认注销吗?', { icon: 3, title: '提示' }, function (index) {
        	AjaxPostUtil.request({url:reqBasePath + "login003", params:{}, type:'json', callback:function(json){
 	   			if(json.returnCode == 0){
	 	   			location.href = "login.html";
 	   			}else{
 	   				location.href = "login.html";
 	   			}
 	   		}});
        });
    });


    // 判断是否显示锁屏（这个要放在最后执行）
    if (window.localStorage.getItem("lockscreen") == "true") {
        winui.lockScreen(function (password) {
            //模拟解锁验证
            if (password === 'winadmin') {
                return true;
            } else {
                winui.window.msg('密码错误', { shift: 6 });
                return false;
            }
        });
    }

    //扩展桌面助手工具
    winui.helper.addTool([{
        tips: '锁屏',
        icon: 'fa-power-off',
        click: function (e) {
            winui.lockScreen(function (password) {
                //模拟解锁验证
                if (password === 'winadmin') {
                    return true;
                } else {
                    winui.window.msg('密码错误', { shift: 6 });
                    return false;
                }
            });
        }
    }, {
        tips: '切换壁纸',
        icon: 'fa-television',
        click: function (e) {
            layer.msg('这个是自定义的工具栏', { zIndex: layer.zIndex });
        }
    }]);

    exports('index', {});
});