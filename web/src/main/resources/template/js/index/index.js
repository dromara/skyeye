
var loadBottomMenuIcon;//是否只展示图标，1是  0否

var friendList = null;//好友列表
var friendChooseList = "";//已选中群组成员

var layim = '';//聊天对象
var userId = "";

var childParams = {};//子页面操作时传递的值

var parentRowId;//index所需要修改东西时的id

var setBgIsColor = false;//顶部模块进行切换时，判断是否需要重置背景

layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window',
    desktop: 'js/winui.desktop',//桌面加载模块
    start: 'js/winui.start',//左下角开始菜单
    helper: 'js/winui.helper'
}).define(['window', 'desktop', 'start', 'helper', 'layim', 'radialin', 'contextMenu', 'dragula', 'jqueryUI', 'form', 'fullscreenslider', 'spop'], function (exports) {
    var $ = layui.jquery,
    	form = layui.form,
    	element = layui.element;
    var winuiLoad;
	layim = layui.layim;
    
    $(document).attr("title", sysMainMation.mationTitle);
    $(".load-title").find("font").html(sysMainMation.mationTitle + $(".load-title").find("font").html());
    $("#sysTitle").html(sysMainMation.mationTitle);
    
    // 页面加载进度条
    $(".winui-load-mation-bg").css({"background-image": "url('/assets/images/win-load.jpg')"});
    winuiLoad = radialIndicator($('#winui-load'), {
    	barBgColor: '#E3E3E3',
        barColor: '#8A2BE2',
        radius: 100,
        barWidth: 5,
        initValue: 0,
        fontSize: 15,
        percentage: true,
        end: function(val){
        	if(val == '100'){
        		$(".winui-load-mation-bg").fadeOut(1000);
        		$(".winui-load-mation").fadeOut(1000);
        	}
        }
    });

	// 文件管理、日程、笔记、论坛权限
	authBtn('1645958796795');
	authBtn('1645959056141');
	authBtn('1645959177299');
	authBtn('1645959237037');
    
    $(function () {
    	var loadIndex = 20;
    	winuiLoad.animate(loadIndex);
    	var loadInterval = setInterval(function(){ 
    		if(loadIndex < 90){
    			loadIndex = loadIndex + 3;
    			winuiLoad.animate(loadIndex);
    		} else {
    			clearInterval(loadInterval);
    		}
    	}, 10);
    	var currentUserMation = {};
		// 获取当前登录员工信息
		systemCommonUtil.getSysCurrentLoginUserMation(function (data){
			currentUserMation = data.bean;
		});
		$("#userPhoto").attr("src", fileBasePath + currentUserMation.userPhoto);
		$("#userName").html(currentUserMation.userCode + '(' + currentUserMation.userName + ')');
		userId = currentUserMation.id;
		loadBottomMenuIcon = currentUserMation.loadBottomMenuIcon;
		if(isNull(currentUserMation.winBgPicUrl)) {
			currentUserMation.winBgPicUrl = fileBasePath + '/images/upload/winbgpic/default.jpg';
		} else {
			currentUserMation.winBgPicUrl = fileBasePath + currentUserMation.winBgPicUrl;
		}
		if(isNull(currentUserMation.winLockBgPicUrl)) {
			currentUserMation.winLockBgPicUrl = fileBasePath + '/images/upload/winbgpic/default.jpg';
		} else {
			currentUserMation.winLockBgPicUrl = fileBasePath + currentUserMation.winLockBgPicUrl;
		}

		// 获取桌面消息
		AjaxPostUtil.request({url: reqBasePath + "login009", params: {language: languageType}, type: 'json', method: "GET", callback: function(l){
			var deskTopName = new Array();
			var defaultName = (languageType == "zh" ? "默认桌面" : "Default desktop");
			deskTopName.push(defaultName);
			var desktopSel = '<option value="winfixedpage00000000">' + defaultName + '</option>';
			//初始化桌面
			$.each(l.rows, function(i, item){
				deskTopName.push(item.name);
				desktopSel += '<option value="' + item.id + '">' + item.name + '</option>';
				$("#winui-desktop").append('<article class="desktop-item-page section" id="' + item.id + '" art-title="' + item.name + '"></article>');
			});
			$('#winui-desktop').fullpage({
				'navigation': true,
				scrollingSpeed: 500,
				navigationTooltips: deskTopName,
				resize: true,
				afterLoad: function(anchorLink, index){
					var id = $("#winui-desktop").find(".desktop-item-page").eq(index - 1).attr("id");
					$("#desktop-sel").val(id);
				}
			});
			$("#desktop-sel").html(desktopSel);
			$("#winui-desktop").find(".desktop-item-page").html("");
			$("body").on('change', '#desktop-sel', function (e) {
				var val = $("#desktop-sel").prop('selectedIndex');
				$.fn.fullpage.moveTo(++val);
			});

			//加载win系统内容
			initWinConfig(currentUserMation);
			//加载聊天
			initTalk();
			//加载右键
			initRightMenu();
			//扩展桌面助手工具
			winui.helper.addTool([{
				tips: '主题设置',
				icon: 'fa-paw',
				click: function (e) {
					winui.window.openTheme(loadBottomMenuIcon);
				}
			}, {
				tips: '添加便签',
				icon: 'fa-pencil-square-o',
				click: function(e) {
					var tagsCount = $('.winui-helper-content').children('.tags-content').length;
					if(tagsCount >= 5) {
						layer.msg('最多只能存五条便签', {
							zIndex: layer.zIndex
						});
						return;
					}
					$('.winui-helper-content').append('<hr /><div class="tags-content"><textarea placeholder="请输入便签"></textarea></div>');
					var $currTextarea = $('.winui-helper-content').children('.tags-content').eq(tagsCount).children('textarea');
					$currTextarea.focus();
					$currTextarea.on('blur', function() {
						var _this = this;
						var content = $(_this).val();
						var id = $(_this).attr("rowid");
						if($.trim(content) === '') {
							if(isNull(id)){
								$(_this).parent().prev().remove();
								$(_this).remove();
							} else {
								AjaxPostUtil.request({url: reqBasePath + "stickynotes004", params:{rowId: id}, type: 'json', callback: function (json) {
									$(_this).parent().prev().remove();
									$(_this).remove();
								}});
							}
						} else {
							if(isNull(id)){
								AjaxPostUtil.request({url: reqBasePath + "stickynotes001", params:{content: content}, type: 'json', callback: function (json) {
									$(_this).attr("rowid", json.bean.id);
								}});
							} else {
								AjaxPostUtil.request({url: reqBasePath + "stickynotes003", params:{rowId: id,content: content}, type: 'json', callback: function (json) {
								}});
							}
						}
					});
				}
			}, {
				tips: '消息中心',
				icon: 'fa-list-ul',
				click: function(e) {
					winui.window.openSysNotice(loadBottomMenuIcon);
				}
			}]);

			// 读取本地便签
			AjaxPostUtil.request({url: reqBasePath + "stickynotes002", params: {}, type: 'json', method: "GET", callback: function (json) {
				var tags = json.rows;
				$.each(tags, function(index, item) {
					if(index >= 5)
						return;
					$('.winui-helper-content').append('<hr /><div class="tags-content"><textarea placeholder="请输入便签" rowid="' + item.id + '">' + item.content + '</textarea></div>');
					$('.winui-helper-content').children('.tags-content').eq(index).children('textarea').on('blur', function() {
						var _this = this;
						var content = $(_this).val();
						var id = $(_this).attr("rowid");
						if($.trim(content) === '') {
							if(isNull(id)){
								$(_this).parent().prev().remove();
								$(_this).remove();
							} else {
								AjaxPostUtil.request({url: reqBasePath + "stickynotes004", params:{rowId: id}, type: 'json', callback: function (json) {
									$(_this).parent().prev().remove();
									$(_this).remove();
								}});
							}
						} else {
							if(isNull(id)){
								AjaxPostUtil.request({url: reqBasePath + "stickynotes001", params:{content: content}, type: 'json', callback: function (json) {
									$(_this).attr("rowid", json.bean.id);
								}});
							} else {
								AjaxPostUtil.request({url: reqBasePath + "stickynotes003", params:{rowId: id,content: content}, type: 'json', callback: function (json) {
								}});
							}
						}
					});
				});
			}});

			//加载消息通知
			initNoticeList();

			//进度条加载到100
			if(loadIndex != 100) {
				clearInterval(loadInterval);
				winuiLoad.animate(100);
			}

			// 判断是否显示锁屏（这个要放在最后执行）
			if(window.localStorage.getItem("lockscreen") == "true") {
				winui.lockScreen(function(password) {
					if(!isNull(password)) {
						var pJudge = false;
						AjaxPostUtil.request({url: reqBasePath + "login008", params: {password: password}, type: 'json', method: "POST", callback: function(json) {
							pJudge = true;
						}, errorCallback: function (json) {
							pJudge = false;
							winui.window.msg(json.returnMessage, {shift: 6});
						}, async: false});
						return pJudge;
					} else {
						winui.window.msg('请输入密码', {shift: 6});
						return false;
					}
				});
			}

			matchingLanguage();
		}});
    });
    
    /**
     * 获取桌面当前显示的桌面
     */
    function getActiveArticle(){
        var article = $("#winui-desktop").find('article');
        var articIndex = -1;
        $.each(article, function(i, item){
            if($(item).hasClass('active')){
                articIndex = i;
                return false;
            }
        });
        return article.eq(articIndex);
    }
    
    //初始化右键
    function initRightMenu(){
    	$("body").contextMenu({
			width: 150, // width
			itemHeight: 30, // 菜单项height
			bgColor: "#FFFFFF", // 背景颜色
			color: "#0A0A0A", // 字体颜色
			fontSize: 12, // 字体大小
			hoverBgColor: "#99CC66", // hover背景颜色
			target: function(ele) { // 当前元素
			},
			rightClass: 'desktop-item-page section fixed-page fp-section active fp-table,desktop-item-page section fp-section fp-table active' + 
						',desktop-item-page section fixed-page fp-section fp-table active',
			menu: [{ // 菜单项
					text: "新建",
					icon: "fa fa-plus-square",
					children: [{
						text: "盒子",
						icon: "fa fa-dropbox",
						callback: function() {
							_openNewWindows({
								url: "../../tpl/index/createMenuBox.html", 
								title: "新建文件夹",
								pageId: "createMenuBoxDialog",
								area: ['600px', '200px'],
								skin: 'top-message-mation',
								callBack: function(refreshCode){
									var boxStr = '<div class="winui-desktop-item win-menu-group" id="' + childParams.id + '" win-id="' + childParams.id + '" win-url="--" win-title="' + childParams.menuBoxName + '" win-opentype="2" win-maxopen="-1" win-menuiconbg="#44adb1">'
													+ '<div class="winui-icon winui-icon-font">'
														+ '<div class="icon-drawer"></div>'
														+ '<div class="icon-child">'
														+ '</div>'
													+ '</div>'
													+ '<p>' + childParams.menuBoxName + '</p>'
												+ '</div>';
									var obj = getActiveArticle();
									obj.html(obj.html() + boxStr);
									//重新排列桌面
									winui.util.locaApp();
									//重新初始化拖拽事件
									initMenuToBox();
									winui.util.reloadOnClick(function (id, elem) {
										var item = $(elem);
										if(item.find(".icon-drawer").length > 0){
											showBigWin(elem);
										} else {
											OpenWindow(elem);
										}
									});
									top.winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
								}});
						}
					}, {
						text: "快捷方式",
						icon: "fa fa-chain",
						callback: function() {
							_openNewWindows({
								url: "../../tpl/index/createMenu.html", 
								title: "新建快捷方式",
								pageId: "createMenuDialog",
								area: ['700px', '450px'],
								skin: 'top-message-mation',
								callBack: function(refreshCode){
									var str = '';
									var iconTypeI = '';
									var menuIcon = '';
									if(childParams.menuIconType === '1' || childParams.menuIconType == 1){
										str = '<i class="fa ' + childParams.menuIcon + ' fa-fw" win-i-id="' + childParams.id + '"></i>';
										iconTypeI = 'winui-icon-font';
										menuIcon = childParams.menuIcon;
									}else if(childParams.menuIconType === '2' || childParams.menuIconType == 2){
										str = '<img src="' + fileBasePath + childParams.menuIconPic + '"/>';
										iconTypeI = 'winui-icon-img';
										menuIcon = childParams.menuIconPic;
									}
									var boxStr = '<div class="winui-desktop-item sec-btn winui-this" win-id="' + childParams.id + '" win-url="' + childParams.menuUrl + '" win-title="' + childParams.titleName + '" win-opentype="' + childParams.openType + '" win-maxopen="-1" win-menuiconbg="' + childParams.menuIconBg + '" win-menuiconcolor="' + childParams.menuIconColor + '" win-icon="' + menuIcon + '">'
													+ '<div class="winui-icon ' + iconTypeI + '" style="background-color: ' + childParams.menuIconBg + '; color:' + childParams.menuIconColor + '">' + str + '</div>'
													+ '<p>' + childParams.menuName + '</p>'
												'</div>';
									var obj = getActiveArticle();
									obj.html(obj.html() + boxStr);
									//重新排列桌面
									winui.util.locaApp();
									//重新初始化拖拽事件
									initMenuToBox();
									winui.util.reloadOnClick(function (id, elem) {
										var item = $(elem);
										if(item.find(".icon-drawer").length > 0){
											showBigWin(elem);
										} else {
											OpenWindow(elem);
										}
									});
									//重置右键事件
									initDeskTopMenuRightClick();
									top.winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
								}});
						}
					}]
				}, {
					text: "--"
				}, {
					text: "全屏模式",
					icon: "fa fa-fw fa-object-group",
					callback: function() {
						fullScreen();
					}
				}, {
					text: "退出全屏",
					icon: "fa fa-fw fa-object-ungroup",
					callback: function() {
						exitFullScreen();
					}
				}, {
					text: "--"
				}, {
					text: "锁屏",
					icon: "fa fa-fw fa-power-off",
					callback: function() {
						winui.lockScreen(function (password) {
	   		            	if(!isNull(password)){
		   		         		var pJudge = false;
		   		         		AjaxPostUtil.request({url: reqBasePath + "login008", params: {password: password}, type: 'json', method: "POST", callback: function (json) {
									pJudge = true;
		   		      	   		}, errorCallback: function (json) {
									pJudge = false;
									winui.window.msg(json.returnMessage, {shift: 6});
								}, async: false});
		   		         		return pJudge;
		   		         	} else {
		   		         		winui.window.msg('请输入密码', {shift: 6});
		   		                return false;
		   		         	}
	   		             });
					}
				}, {
					text: "--"
				}, {
					text: "刷新",
					icon: "fa fa-refresh",
					callback: function() {
						window.location.reload(true);
					}
				}, {
					text: "清除缓存",
					icon: "fa fa-trash-o",
					callback: function() {
						winui.window.confirm('如果清除缓存系统会自动退出，请保存好手上工作！', {id: 'exit-confim', icon: 3, btn: ['确认清除', '稍后清除'], title: '提示', skin: 'msg-skin-message', success: function(layero, index){
							var times = $("#exit-confim").parent().attr("times");
							var zIndex = $("#exit-confim").parent().css("z-index");
							$("#layui-layer-shade" + times).css({'z-index': zIndex});
						}}, function (index) {
							localStorage.clear();
							sessionStorage.clear();
							deleteCookie();
							winui.window.msg('缓存清除成功，两秒后自动刷新页面！', { shift: 1 }, function() {
								location.href = "login.html";
							});
				        });
					}
				}, {
					text: "--"
				}, {
					text: "退出系统",
					icon: "fa fa-sign-out",
					callback: function() {
						winui.hideStartMenu();
						winui.window.confirm('确认注销吗?', {id: 'exit-confim', icon: 3, title: '提示', skin: 'msg-skin-message', success: function(layero, index){
							var times = $("#exit-confim").parent().attr("times");
							var zIndex = $("#exit-confim").parent().css("z-index");
							$("#layui-layer-shade" + times).css({'z-index': zIndex});
						}}, function (index) {
				        	AjaxPostUtil.request({url: reqBasePath + "login003", params: {}, type: 'json', method: "POST", callback: function (json) {
								if (etiger != null) {
									etiger.socket.close();
								}
								location.href = "login.html";
				 	   		}, errorCallback: function (json) {
								if (etiger != null) {
									etiger.socket.close();
								}
								location.href = "login.html";
							}});
				        });
					}
				}, {
					text: "关于",
					icon: "fa fa-info-circle",
					callback: function() {
						winui.window.config({
				            anim: 0,
				            miniAnim: 0,
				            maxOpen: -1
				        }).open({
				            id: '关于我们',
				            type: 1,
				            title: '<i class="fa fa-fw title-icon fa-info-circle" style="background-color: #0491fe;color: #ecf3f8;"></i><font class="win-title-class">关于我们</font>',
				            content: '<p style="padding:20px;">skyeye<br/><br/>官方网站：https://gitee.com/doc_wei01/skyeye<br/><br/>版权：<br/><br/>作者：卫志强<br/><br/>版本：2.1.0</p>',
				            loadBgColor: '#0491fe',
				            loadIcon: 'fa fa-info-circle',
				            loadIconColor: '#ecf3f8',
				            area: ['400px', '400px'],
				            maxOpen: '-1',
				            loadBottomMenuIcon: loadBottomMenuIcon,
				            iconTitle: '<i class="fa fa-fw title-icon-big fa-info-circle" style="background-color: #0491fe;color: #ecf3f8;"></i>',
				            refresh:true
				        });
					}
				}
			]
		});
    }
    
    // 清除所有的cookie
    function deleteCookie() {
        var cookies = document.cookie.split(";");
        for (var i = 0; i < cookies.length; i++) {
            var cookie = cookies[i];
            var eqPos = cookie.indexOf("=");
            var name = eqPos > -1 ? cookie.substr(0, eqPos) : cookie;
            document.cookie = name + "=;expires=Thu, 01 Jan 1970 00:00:00 GMT; path=/";
        }
        if(cookies.length > 0) {
            for (var i = 0; i < cookies.length; i++) {
                var cookie = cookies[i];
                var eqPos = cookie.indexOf("=");
                var name = eqPos > -1 ? cookie.substr(0, eqPos) : cookie;
                var domain = location.host.substr(location.host.indexOf('.'));
                document.cookie = name + "=;expires=Thu, 01 Jan 1970 00:00:00 GMT; path=/; domain=" + domain;
            }
        }
    }
    
    function initTalk(){
    	layim.config({
			brief: false,// 是否简约模式（如果true则不显示主面板）
			title: '天眼',
			init: {
				url: reqBasePath + "companychat001",
			},//好友接口
			members: {
				url: reqBasePath + 'companytalkgroup007',
			},//群员借口
			uploadImage: {
				url: reqBasePath + "common003?type=9", //（返回的数据格式见下文）
				type: '' //默认post
			},//上传图片接口
			uploadFile: {
				url: reqBasePath + "common003?type=10", //（返回的数据格式见下文）
				type: '' //默认post
			},//上传文件接口
			initSkin: '2.jpg', //1-5 设置初始背景
			notice: true, //是否开启桌面消息提醒，默认false
			msgbox: '../../tpl/chat/invitation.html', //消息盒子页面地址，若不开启，剔除该项即可
			joingroup: {
				url: '../../tpl/chat/searchMation.html' //加入群聊页面地址，若不开启，剔除该项即可
			}, 
			customMation: {
				url: '../../tpl/chat/customMation.html' //加入群聊页面地址，若不开启，剔除该项即可
			}, 
			find: {
				url: '../../tpl/chat/createGroup.html', //发现页面地址，若不开启，剔除该项即可
				brforeCallback: function(){
					friendList = $(".layim-list-friend").prop("outerHTML");
				},//加载页面前执行的方法
				returnCallback: function(){
					var toStrId = friendChooseList.split(",");
					$.each(toStrId, function(i, item){
						if(!isNull(item)){
							var sendMessage = {
								to: item,//收信人id
								type: 7//群组邀请消息
							};
							etiger.socket.send(JSON.stringify(sendMessage));
						}
					});
				}
			},
			chatLog: '../../tpl/chat/chatLog.html' //聊天记录页面地址，若不开启，剔除该项即可
		});
    	
    	//监听在线状态的切换事件
		layim.on('online', function(data) {
			if(data == 'online'){//上线
				var sendMessage = {
					userId: userId,//发送人id
					type: 9
				};
				etiger.socket.send(JSON.stringify(sendMessage));
			}else if(data == 'hide'){//下线
				var sendMessage = {
					userId: userId,//发送人id
					type: 8
				};
				etiger.socket.send(JSON.stringify(sendMessage));
			}
		});
		
		//监听签名修改
		layim.on('sign', function(value) {
			AjaxPostUtil.request({url: reqBasePath + "companychat002", params:{userSign: value}, type: 'json', callback: function (json) {
				winui.window.msg('保存成功', {icon: 1, skin: 'msg-skin-message'});
 	   		}});
		});
		
		//监听layim建立就绪
		layim.on('ready', function(res) {
			//初始化websocket
	    	etiger.socket.init();
			layim.msgbox(0); //模拟消息盒子有新消息，实际使用时，一般是动态获得
			//判断当前是否是锁屏状态，以防止刷新后依然可以进行聊天
			if (window.localStorage.getItem("lockscreen") == "true") {
				$('.talk-btn').hide();
			}
		});
		
		//监听发送消息
		layim.on('sendMessage', function(data) {
			var To = data.to;
			var mine = data.mine;
			if(To.type === 'group') {
				var sendMessage = {
					username: mine.username,
					avatar: mine.avatar,
					to: To.id,
					type: 11,
					userId: userId,
					message: data.mine.content
				};
				etiger.socket.send(JSON.stringify(sendMessage));
			} else {//个人聊天
				var sendMessage = {
					message: data.mine.content,//消息
					userId: userId,//发送人id
					to: To.id,//收信人id
					type: 4,
					username: mine.username,
					avatar: mine.avatar
				};
				etiger.socket.send(JSON.stringify(sendMessage));
			}
		});
		
		//监听查看群员
		layim.on('members', function(data) {
			console.log(data);
		});

		//监听聊天窗口的切换
		layim.on('chatChange', function(res) {
			var type = res.data.type;
			console.log(res.data.id)
			if(type === 'friend') {
				//模拟标注好友状态
			} else if(type === 'group') {
				//模拟系统消息
			}
		});
    }
    
    //初始化配置信息
    function initWinConfig(currentUserMation){
    	//设置窗口点击事件
    	$("body").on("click", ".sec-clsss-btn", function (e) {
    		winui.window.close($('#childWindow').parent());
    		OpenWindow($(this).prop("outerHTML"));
    	});

    	winui.config({
            settings: {
                color: currentUserMation.winThemeColor,
                taskbarMode: currentUserMation.winTaskPosition,
                startSize: currentUserMation.winStartMenuSize,
                bgSrc: currentUserMation.winBgPicUrl,
                lockBgSrc: currentUserMation.winLockBgPicUrl,
                vagueBgSrc: currentUserMation.winBgPicVague,
                vagueBgSrcValue: currentUserMation.winBgPicVagueValue
            },
            desktop: {//桌面菜单栏
                options: {
                	url: reqBasePath + 'login004',
                    method: 'get',
                },    //可以为{}
                done: function (desktopApp) {
                    desktopApp.onclick(function (id, elem) {
                    	var item = $(elem);
                    	if(item.find(".icon-drawer").length > 0){
                    		showBigWin(elem);
                    	} else {
                    		OpenWindow(elem);
                    	}
                    }),
                    //重置右键事件
                    initDeskTopMenuRightClick();
                }
            },
            menu: {//左下角菜单栏右键效果
                options: {
                    url: reqBasePath + 'login005',
                    method: 'get',
                },
                done: function (menuItem) {
                    //监听开始菜单点击
                    menuItem.onclick(function (elem) {
                        OpenWindow(elem);
                    });
                    menuItem.contextmenu({
                    	item: [{
                        	icon: 'fa-folder-open-o', 
                        	text: '打开',
                        	callBack: function (id, elem) {
                            	var item = $(elem);
                            	if(item.find(".icon-drawer").length > 0){
                            		showBigWin(elem);
                            	} else {
                            		OpenWindow(elem);
                            		winui.window.close($('#childWindow').parent());
                            	}
                            }
                        }, {
                        	icon: ' fa-copy', 
                        	text: '发送到桌面',
                        	callBack: function (id, elem) {
                        		AjaxPostUtil.request({url: reqBasePath + "sysevewindragdrop010", params:{rowId: id}, type: 'json', callback: function (json) {
									top.winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
									var thisMenuIcon = json.bean.icon;
									var thisMenuBg = json.bean.menuIconBg;
									var thisMenuIconColor = json.bean.menuIconColor;
									var thisMenuId = json.bean.id;
									var thisMenuUrl = json.bean.pageURL;
									var thisMenuTitle = json.bean.title;
									var thisMenuOpenType = json.bean.openType;
									var thisMenuMaxOpen = json.bean.maxOpen;
									var menuIconType = json.bean.menuIconType;
									var thisDeskTopId = json.bean.deskTopId;

									if(isNull(thisDeskTopId)){
										$.fn.fullpage.moveTo(1);
									} else {
										if($("#winui-desktop").find('article[id="' + thisDeskTopId + '"]').length > 0){
											var topIndex = $("#winui-desktop").find('article[id="' + thisDeskTopId + '"]').index();
											$.fn.fullpage.moveTo(++topIndex);
										} else {
											$.fn.fullpage.moveTo(1);
										}
									}

									var str = "";
									var iconTypeI = ""
									if(menuIconType === '1' || menuIconType == 1){
										iconTypeI = "winui-icon-font";
										str = '<i class="fa ' + thisMenuIcon + ' fa-fw" win-i-id="' + thisMenuId + '"></i>';
										thisMenuIcon = json.bean.icon;
									}else if(menuIconType === '2' || menuIconType == 2){
										iconTypeI = "winui-icon-img";
										str = '<img src="' + fileBasePath + json.bean.menuIconPic + '" />';
										thisMenuIcon = json.bean.menuIconPic;
									}
									if(json.bean.parentId != '0'){//二级菜单
										var parentId = json.bean.parentId.split(',')[0];
										if($("#winui-desktop").find('div[id="' + parentId + '"]').length == 0){//二级菜单的父菜单没有在桌面,此处不需要去获取当前滚动展示的模块
											var boxStr = '<div class="winui-desktop-item sec-btn winui-this" win-id="' + thisMenuId + '" win-url="' + thisMenuUrl + '" win-title="' + thisMenuTitle + '" win-opentype="' + thisMenuOpenType + '" win-maxopen="-1" win-menuiconbg="' + thisMenuBg + '" win-menuiconcolor="' + thisMenuIconColor + '" win-icon="' + thisMenuIcon + '">'
											+ '<div class="winui-icon ' + iconTypeI + '" style="background-color: ' + thisMenuBg + '; color:' + thisMenuIconColor + '">' + str + '</div>'
											+ '<p>' + thisMenuTitle + '</p>'
											'</div>';
											var obj = getActiveArticle();
											obj.html(obj.html() + boxStr);
											//重新排列桌面
											winui.util.locaApp();
											//重新初始化拖拽事件
											initMenuToBox();
											winui.util.reloadOnClick(function (id, elem) {
												var item = $(elem);
												if(item.find(".icon-drawer").length > 0){
													showBigWin(elem);
												} else {
													OpenWindow(elem);
												}
											});
											//重置右键事件
											initDeskTopMenuRightClick();
										} else {//二级菜单的父菜单在桌面上
											var iTag = '<i class="fa icon-drawer-icon" win-i-id="' + thisMenuId + '">' + str + '</i>';
											//此处不需要去获取当前滚动展示的模块
											$("#winui-desktop").find('div[id="' + parentId + '"]').find('div[class="icon-drawer"]').append(iTag);
											var menuStr = '<div class="winui-desktop-item sec-clsss-btn sec-btn" win-id="' + thisMenuId + '" win-url="' + thisMenuUrl + '" win-title="' + thisMenuTitle + '" win-opentype="' + thisMenuOpenType + '" win-maxopen="' + thisMenuMaxOpen + '" win-menuiconbg="' + thisMenuBg + '" win-menuiconcolor="' + thisMenuIconColor + '" win-icon="' + thisMenuIcon + '">'
											+ '<div class="winui-icon ' + iconTypeI + '" style="background-color: ' + thisMenuBg + '">' + str + '</div>'
											+ '<p>' + thisMenuTitle + '</p></div>';
											$("#winui-desktop").find('div[id="' + parentId + '"]').find('div[class="icon-child"]').append(menuStr);
											//重新排列桌面
											winui.util.locaApp();
											winui.util.reloadOnClick(function (id, elem) {
												var item = $(elem);
												if(item.find(".icon-drawer").length > 0){
													showBigWin(elem);
												} else {
													OpenWindow(elem);
												}
											});
										}
									} else {//一级菜单
										var boxStr = '<div class="winui-desktop-item sec-btn winui-this" win-id="' + thisMenuId + '" win-url="' + thisMenuUrl + '" win-title="' + thisMenuTitle + '" win-opentype="' + thisMenuOpenType + '" win-maxopen="-1" win-menuiconbg="' + thisMenuBg + '" win-menuiconcolor="' + thisMenuIconColor + '" win-icon="' + thisMenuIcon + '">'
										+ '<div class="winui-icon ' + iconTypeI + '" style="background-color: ' + thisMenuBg + '; color:' + thisMenuIconColor + '">' + str + '</div>'
										+ '<p>' + thisMenuTitle + '</p>'
										'</div>';
										var obj = getActiveArticle();
										obj.html(obj.html() + boxStr);
										//重新排列桌面
										winui.util.locaApp();
										//重新初始化拖拽事件
										initMenuToBox();
										winui.util.reloadOnClick(function (id, elem) {
											var item = $(elem);
											if(item.find(".icon-drawer").length > 0){
												showBigWin(elem);
											} else {
												OpenWindow(elem);
											}
										});
										//重置右键事件
										initDeskTopMenuRightClick();
									}
        			 	   		}});
                        	}
                        }, {
                        	text: '--'
                        }, {
                        	icon: 'fa-qq', 
                        	text: '自定义',
                        	callBack: function (id, elem) {
                        		winui.window.msg('自定义回调');
                        	}
                        }]
                    });
                }
            }
        }).init({
            audioPlay: true, //是否播放音乐（开机音乐只会播放一次，第二次播放需要关闭当前页面从新打开，刷新无用）
            renderBg: true //是否渲染背景图 （由于js是写在页面底部，所以不太推荐使用这个来渲染，背景图应写在css或者页面头部的时候就开始加载）
        }, function () {
            initMenuToBox();
        });
    }

    // 开始菜单磁贴点击
    $('.winui-tile').on('click', function () {
        OpenWindow(this);
    });

    // 初始化通知信息
    function initNoticeList(){
    	showGrid({
		 	id: "notice-list",
		 	url: sysMainMation.noticeBasePath + "syseveusernotice002",
		 	params: {},
		 	pagination: false,
		 	template: getFileContent('tpl/index/noReadNotice.tpl'),
		 	ajaxSendLoadBefore: function(hdb){
		 	},
		 	options: {},
		 	ajaxSendAfter:function (json) {
		 		if(json.total == 0){
                    $("#showMyNoticeNum").hide();
                } else {
                    $("#showMyNoticeNum").show();
                    $("#showMyNoticeNum").html(json.total);
                }
		 		//移除全部消息
		 		$(".notice-remove").on("click", function (e) {
		 			var noticeList = $("#notice-list").find(".winui-message-item");
		 			var idsStr = "";
		 			$.each(noticeList, function(index, item){
		 				idsStr = $(item).attr("rowid") + ",";
		 			});
		 			$("#showMyNoticeNum").hide();
                    $("#showMyNoticeNum").html("0");
		 			if(!isNull(idsStr)){
		 				AjaxPostUtil.request({url: sysMainMation.noticeBasePath + "syseveusernotice006", params: {rowIds: idsStr}, type: 'json', callback: function (json) {
							$.each(noticeList, function(index, item){
								setTimeout(function (e) {
									$(item).animate({'margin-left': '390px'}, 500, function() {
										$(item).remove();
									});
								}, index * 200);
							});
			 	   		}});
		 			}
		 		});
		 		
		 		//查看详细信息
		 		$(".winui-message-item").on("click", function (e) {
		 			var _this = $(this);
		 			parentRowId = _this.attr("rowid");
		 			AjaxPostUtil.request({url: sysMainMation.noticeBasePath + "syseveusernotice003", params: {rowId: parentRowId}, type: 'json', callback: function (json) {
						_openNewWindows({
							url: "../../tpl/index/noticeDetail.html",
							title: "消息详情",
							pageId: "noticeDetail" + (new Date()).valueOf(),
							area: ['600px', '400px'],
							shade: false,
							callBack: function(refreshCode){
						}});
						_this.animate({'margin-left': '390px'}, 500, function() {
							_this.remove();
							var noticeShowListLength = $("#notice-list").find(".winui-message-item").length;
							if(noticeShowListLength === '0' || noticeShowListLength == 0){
								$("#showMyNoticeNum").hide();
								$("#showMyNoticeNum").html("0");
							} else {
								$("#showMyNoticeNum").html(noticeShowListLength);
							}
						});
		 	   		}});
		 		});
		 		
		 		//删除
		 		$(".notice-item-remove").on("click", function (e) {
		 			var _this = $(this).parent().parent();
		 			parentRowId = _this.attr("rowid");
		 			AjaxPostUtil.request({url: sysMainMation.noticeBasePath + "syseveusernotice004", params: {rowId: parentRowId}, type: 'json', callback: function (json) {
						_this.animate({'margin-left': '390px'}, 500, function() {
							_this.remove();
							var noticeShowListLength = $("#notice-list").find(".winui-message-item").length;
							if(noticeShowListLength === '0' || noticeShowListLength == 0){
								$("#showMyNoticeNum").hide();
								$("#showMyNoticeNum").html("0");
							} else {
								$("#showMyNoticeNum").html(noticeShowListLength);
							}
						});
		 	   		}});
		 			return false;
		 		});
		 	}
	    });
    }
    
    //个人中心点击
    $('.winui-start-syspersonal').on('click', function () {
    	$('.win-10-menu').removeClass("switch-checked");
		$(".win-sys-wicket").css({'display': 'none'});
		$("#win10Btn").addClass("switch-checked");
		$(".win-file-switch").css({'z-index': '0'});//顶部菜单栏
        OpenWindow(this);
    });
    
    //展开
    $('.winui-start-item.winui-start-show').on('click', function () {
    	if($(".winui-start-left").css("width") === '210px'){//当前状态：展开
    		$(".winui-start-left").animate({
    			width: '48px' 
    		});
    		$(".winui-start-left").css({'background-color': 'rgba(0, 0, 0, 0.3)'});
    	} else {//当前状态：关闭
    		$(".winui-start-left").animate({
    			width: '210px' 
    		});
    		$(".winui-start-left").css({'background-color': 'rgba(0, 0, 0, 0.9)'});
    	}
    });
    
    $(".winui-start-left").mouseleave(function (){
    	$(".winui-start-left").animate({
			width: '48px' 
		});
    	$(".winui-start-left").css({'background-color': 'rgba(0, 0, 0, 0.3)'});
    });

    // 打开窗口的方法（可自己根据需求来写）
    function OpenWindow(menuItem) {
		var $this = $(menuItem);
        var url = $this.attr('win-url');
        var menuIconBg = $this.attr("win-menuiconbg");
        var menuIconColor = $this.attr("win-menuiconcolor");
        var menuIcon = $this.attr("win-icon");
		var menuSysWinUrl = $this.attr("win-sysWinUrl");
        var str = '', iconStr = '';
        if(menuIcon.indexOf('fa-') != -1){//icon图标
	        str = '<i class="fa fa-fw title-icon ';//图标+文字
	        iconStr = '<i class="fa fa-fw title-icon-big ';//图标
	        if(!isNull(menuIcon)){
	        	str += menuIcon;
	        	iconStr += menuIcon;
	        }
	        str += '" style="';
	        iconStr += '" style="';
	        if(!isNull(menuIconBg)){
	        	str += 'background-color: ' + menuIconBg + ';';
	        	iconStr += 'background-color: ' + menuIconBg + ';';
	        }
	        if(!isNull(menuIconColor)){
	        	str += 'color: ' + menuIconColor + ';';
	        	iconStr += 'color: ' + menuIconColor + ';';
	        }
	        str += '"></i>';
	        iconStr += '"></i>';
        } else {//图片
        	str = '<img class="title-icon" src="' + menuIcon + '"/>';
        	iconStr = '<img class="title-icon-big" src="' + menuIcon + '"/>';
        }
        var title = str + '<font class="win-title-class">' + $this.attr('win-title') + '</font>';
        var iconTitle = iconStr;
        var id = $this.attr('win-id');
        var type = parseInt($this.attr('win-opentype'));
        var maxOpen = parseInt($this.attr('win-maxopen')) || -1;
        if (url == 'theme') {
            winui.window.openTheme(loadBottomMenuIcon);
            return;
        }
        if (!url || !title || !id) {
            winui.window.msg('菜单配置错误（菜单链接、标题、id缺一不可）');
            return;
        }
        url = indexMenu.getUrlPath(url, menuSysWinUrl);
        if (type === 1) {
        	// 新窗口打开
			window.open(url);
        } else {
			// 核心方法（参数请看文档，config是全局配置 open是本次窗口配置 open优先级大于config）
			winui.window.config({
				anim: 0,
				miniAnim: 0,
				maxOpen: -1
			}).open({
				id: id,
				type: type,
				title: title,
				content: url,
				loadBgColor: menuIconBg,
				loadIcon: menuIcon,
				loadIconColor: menuIconColor,
				area: ['90vw','90vh'],
				maxOpen: maxOpen,
				loadBottomMenuIcon: loadBottomMenuIcon,
				iconTitle: iconTitle,
				refresh: true,
				success: function(){
					indexMenu.sendMessageToChildIFrame(url, id);
				}
			});
		}
    }
    
    //打开二级窗口
    function showBigWin(menuItem){
    	var menu = $(menuItem);
    	var title = "";
    	if(isNull(menu[0].outerText)){
    		title = menu[0].innerText;
    	} else {
    		title = menu[0].outerText;
    	}
    	var str = '<div class="childWindow-title-input-box">'
				+ '<input type="text" placeholder="请输入盒子标题" class="layui-input" value="' + title + '" id="childWindowInput"/>'
				+ '<button class="layui-btn layui-btn-primary layui-btn-sm" id="cancleChildWindow"><language showName="com.skyeye.cancel"></language></button>'
				+ '<button class="layui-btn layui-btn-normal layui-btn-sm" id="saveChildWindow" rowid="' + menu[0].id + '"><language showName="com.skyeye.save"></language></button>'
				+ '</div>';
    	winui.window.config({
            anim: 0,
            miniAnim: 0,
            maxOpen: -1
        }).open({
        	id: 'childWindow',
            type: 1,
            title: '<font id="childWindowtext">' + title + '</font>' + str,
    		closeBtn: 1,
    		fixed: false,
    		move: false,
            content: menu.find(".icon-child").html(),
            area: ['600px', '260px'],
            shadeClose: true,
            skin: 'sec-clsss top-message-mation',
            scrollbar: false,
            shade: 0.5,
            maxmin: false,
            success: function(layero, index){
            	//获取当前显示的模块
                var obj = getActiveArticle();
                dragula([document.getElementById('childWindow'), document.getElementById(obj.attr("id"))], {
            		revertOnSpill: true,
            		accepts: function(el, target) {//拖拽中
        				return target !== document.getElementById('childWindow');
        			}
            	}).on('drag', function (el) {//开始拖拽
            		var times = $("#childWindow").parent().attr("times");
            		$("#layui-layer-shade" + times).css({width: 0});
            	}).on('drop', function (el, container) {//放置
            		var times = $("#childWindow").parent().attr("times");
            		$("#layui-layer-shade" + times).css({width: '100%'});
        			var thisMenuIcon = $(el).eq(0).attr("win-icon");
        			var thisMenuBg = $(el).eq(0).attr("win-menuiconbg");
        			var thisMenuIconColor = $(el).eq(0).attr("win-menuIconColor");
        			var thisMenuId = $(el).eq(0).attr("win-id");

        			$('i[win-i-id="' + thisMenuId + '"]').remove();
        			$('div[class="winui-desktop-item sec-clsss-btn sec-btn"][win-id="' + thisMenuId + '"]').remove();
        			$(el).removeClass('sec-clsss-btn');
        			$(el).find('div[class="winui-icon winui-icon-font"]').html('<i class="fa ' + thisMenuIcon + ' fa-fw" style="background-color: ' + thisMenuBg + '; color: ' + thisMenuIconColor + '" win-i-id="' + thisMenuId + '"></i>');
        			//重新排列桌面
        			winui.util.locaApp();
        			winui.util.reloadOnClick(function (id, elem) {
        				var item = $(elem);
        				if(item.find(".icon-drawer").length > 0){
        					showBigWin(elem);
        				} else {
        					OpenWindow(elem);
        				}
        			});
        			AjaxPostUtil.request({url: reqBasePath + "sysevewindragdrop004", params:{rowId: thisMenuId, parentId: ""}, type: 'json', callback: function (json) {
    				}});
        			
            	}).on('cancel', function (el, container) {//拖拽取消
            		var times = $("#childWindow").parent().attr("times");
            		$("#layui-layer-shade" + times).css({width: '100%'});
            	});
            },
            end: function(layero, index){
            	initMenuToBox();
            }
        });
    	initDeskTopMenuRightClick();
    }
    
    //盒子标题双击
    $("body").on('dblclick', '#childWindowtext', function (e) {
    	$(this).hide();
    	$(".childWindow-title-input-box").show();
    	$("#childWindowInput").val($('#childWindowtext').html());
    });
    //盒子标题取消
    $("body").on('click', '#cancleChildWindow', function (e) {
    	$('#childWindowtext').show();
    	$(".childWindow-title-input-box").hide();
    });
    //盒子标题保存
    $("body").on('click', '#saveChildWindow', function (e) {
    	if(isNull($("#childWindowInput").val())){
    		winui.window.msg('请输入盒子标题', {shift: 6, skin: 'msg-skin-message'});
    	} else {
    		var rowId = $(this).attr("rowid");
    		var menuName = $("#childWindowInput").val();
    		AjaxPostUtil.request({url: reqBasePath + "sysevewindragdrop005", params: {rowId: rowId}, type: 'json', method: "POST", callback: function (json) {
				var menuType = json.bean.menuType;
				if(menuType == '1'){//系统菜单
					winui.window.msg('无法编辑系统菜单。', {shift: 6, skin: 'msg-skin-message'});
				}else if(menuType == '3'){//自定义菜单盒子
					var params = {
						menuBoxName: menuName,
						rowId: rowId
					};
					AjaxPostUtil.request({url: reqBasePath + "sysevewindragdrop007", params: params, type: 'json', callback: function (json) {
						// 此处不需要去获取当前滚动展示的模块
						$("#winui-desktop").find('div[id="' + rowId + '"]').attr("win-title", menuName);
						$("#winui-desktop").find('div[id="' + rowId + '"]').find('p').html(menuName);
						$('#childWindowtext').show();
						$(".childWindow-title-input-box").show();
						$('#childWindowtext').html(menuName);
					}});
				}
			}});
    	}
    });

    // 注销登录
    $('.logout').on('click', function () {
        winui.hideStartMenu();
        winui.window.confirm('确认注销吗?', {id: 'exit-confim', icon: 3, title: '提示', skin: 'msg-skin-message', success: function(layero, index){
			var times = $("#exit-confim").parent().attr("times");
			var zIndex = $("#exit-confim").parent().css("z-index");
			$("#layui-layer-shade" + times).css({'z-index': zIndex});
		}}, function (index) {
        	AjaxPostUtil.request({url: reqBasePath + "login003", params: {}, type: 'json', method: "POST", callback: function (json) {
				if (etiger != null) {
					etiger.socket.close();
				}
				location.href = "login.html";
 	   		}, errorCallback: function (json) {
				if (etiger != null) {
					etiger.socket.close();
				}
				location.href = "login.html";
			}});
        });
    });

    //个人信息下拉框选项
    $('body').on('mouseenter', '.user-main-mation', function (e) {
		$(".win-file-switch").css({'z-index': '99999901'});
	}).on('mouseleave', '.user-main-mation', function() {
		if($(".switch-menu").find(".switch-checked").attr("id") === 'win10Btn'){
			$(".win-file-switch").css({'z-index': '0'});
		}
	});
    
	/**
	 * 初始化菜单向文件夹中移动
	 */
	function initMenuToBox(){
		var articleObj = $("#winui-desktop article");
        $.each(articleObj, function(i, item){
            var _articObj = $(item);
            //初始化完毕回调
            var winMenuGroup = _articObj.find(".win-menu-group");
            var menuGroup = new Array();
            menuGroup.push(document.getElementById(_articObj.attr("id")));
            $.each(winMenuGroup, function(i, bean){
                menuGroup.push(document.getElementById($(bean).attr("id")));
            });
            //向盒子中拖拽
            dragula(menuGroup, {
                accepts: function (el, target) {
                    if($(el).hasClass("winui-desktop-item")){
                        return true;
                    } else {
                        return false;
                    }
                }
            }).on('drop', function (el, container) {//放置
                if($(container).attr("win-url") == '--'){//直接加入box盒子中
                    var boxId = $(container).attr("win-id");//盒子id
                    var thisMenuId = $(el).eq(0).attr("win-id");//退拽中的id
                    if( $('#' + boxId).find(".winui-icon-img").find(".icon-drawer").find('i[win-i-id="' + thisMenuId + '"]').length == 0 && !isNull(thisMenuId)){
                        var drawer = $('#' + boxId).find(".icon-drawer");//盒子icon
                        var child = $('#' + boxId).find(".icon-child");//盒子child
                        var thisMenuIcon = $(el).eq(0).attr("win-icon");
                        var thisMenuBg = $(el).eq(0).attr("win-menuiconbg");
                        var thisMenuIconColor = $(el).eq(0).attr("win-menuIconColor");
                        var thisMenuUrl = $(el).eq(0).attr("win-url");
                        var thisMenuTitle = $(el).eq(0).attr("win-title");
                        var thisMenuOpenType = $(el).eq(0).attr("win-opentype");
                        var thisMenuMaxOpen = $(el).eq(0).attr("win-maxopen");
						
                        var iconTypeI = "", iconSmallI = "", iconBigI = "";
                        if(thisMenuIcon.indexOf('fa-') != -1){//icon图标
                            iconTypeI = "winui-icon-font";
                            iconBigI = '<i class="fa ' + thisMenuIcon + ' fa-fw" style="background-color: ' + thisMenuBg + '; color: ' + thisMenuIconColor + '" win-i-id="' + thisMenuId + '"></i>';
                            iconSmallI = '<i class="fa fa-fw icon-drawer-icon ' + thisMenuIcon + '" style="background-color: ' + thisMenuBg + '; color: ' + thisMenuIconColor + '" win-i-id="' + thisMenuId + '"></i>';
                        } else {//图片
                            iconTypeI = "winui-icon-img";
                            iconBigI = '<img src="' + fileBasePath + thisMenuIcon + '" />';
                            iconSmallI = '<i class="fa icon-drawer-icon" win-i-id="' + thisMenuId + '"><img src="' + fileBasePath + thisMenuIcon + '" /></i>';
                        }
                        var menuStr = '<div class="winui-desktop-item sec-clsss-btn sec-btn" win-id="' + thisMenuId + '" win-url="' + thisMenuUrl + '" win-title="' + thisMenuTitle + '" win-opentype="' + thisMenuOpenType + '" win-maxopen="' + thisMenuMaxOpen + '" win-menuiconbg="' + thisMenuBg + '" win-menuiconcolor="' + thisMenuIconColor + '" win-icon="' + thisMenuIcon + '">'
                        + '<div class="winui-icon ' + iconTypeI + '" style="background-color: ' + thisMenuBg + '">' + iconBigI + '</div>'
                        + '<p>' + thisMenuTitle + '</p></div>';
						
                        drawer.html(drawer.html() + iconSmallI);//在盒子内部追加icon
                        child.html(child.html() + menuStr);
                        $('#' + boxId).children('div[win-id="' + thisMenuId + '"]').remove();
                        $('div[class="winui-desktop-item sec-btn"][win-id="' + thisMenuId + '"]').remove();
                        //重新排列桌面
                        winui.util.locaApp();
                        winui.util.reloadOnClick(function (id, elem) {
                            var item = $(elem);
                            if(item.find(".icon-drawer").length > 0){
                                showBigWin(elem);
                            } else {
                                OpenWindow(elem);
                            }
                        });
                        AjaxPostUtil.request({url: reqBasePath + "sysevewindragdrop004", params:{rowId: thisMenuId, parentId: boxId}, type: 'json', callback: function (json) {
                        }});
                    }
					
                }
            });
        });
	}
	
	$('.win-10-menu').on('click', function () {
		$('.win-10-menu').removeClass("switch-checked");
		$(".win-sys-wicket").css({'display': 'none'});
		$(this).addClass("switch-checked");
		var id = $(this).attr("id");
		var bgImg = winui.settings.bgSrc;
		if(id === "win10Btn"){//桌面
			$(".win-file-switch").css({'z-index': '0'});//顶部菜单栏
		}else if(id === "file10Console"){//文件管理
			$(".win-file-switch").css({'z-index': '99999901'});//顶部菜单栏
			if(!setBgIsColor){
				$(".optreat-fileconsole-bg").css({'background-image': 'url("' + bgImg + '")'});
			}
			$(".optreat-fileconsole-bg").css({'display': 'block'});
			$(".optreat-fileconsole-content").css({'display': 'block'});
			if(isNull($(".optreat-fileconsole-content").find("iframe").attr("src"))){
				$(".optreat-fileconsole-content").find("iframe").attr("src", "../../tpl/fileconsole/fileconsolelist.html");
			}
		}else if(id === "scheduleBtn"){//日程
			$(".win-file-switch").css({'z-index': '99999901'});//顶部菜单栏
			if(!setBgIsColor){
				$(".optreat-schedule-bg").css({'background-image': 'url("' + bgImg + '")'});
			}
			$(".optreat-schedule-bg").css({'display': 'block'});
			$(".optreat-schedule-content").css({'display': 'block'});
			if(isNull($(".optreat-schedule-content").find("iframe").attr("src"))){
				$(".optreat-schedule-content").find("iframe").attr("src", "../../tpl/index/mySchedule.html");
			}
		}else if(id === "file10Btn"){//笔记
			$(".win-file-switch").css({'z-index': '99999901'});//顶部菜单栏
			if(!setBgIsColor){
				$(".optreat-filewrite-bg").css({'background-image': 'url("' + bgImg + '")'});
			}
			$(".optreat-filewrite-bg").css({'display': 'block'});
			$(".optreat-filewrite-content").css({'display': 'block'});
			if(isNull($(".optreat-filewrite-content").find("iframe").attr("src"))){
				$(".optreat-filewrite-content").find("iframe").attr("src", "../../tpl/index/myNote.html");
			}
		}else if(id === "forumBtn"){//论坛
            $(".win-file-switch").css({'z-index': '99999901'});//顶部菜单栏
            if(!setBgIsColor){
                $(".optreat-forum-bg").css({'background-image': 'url("' + bgImg + '")'});
            }
            $(".optreat-forum-bg").css({'display': 'block'});
            $(".optreat-forum-content").css({'display': 'block'});
            if(isNull($(".optreat-forum-content").find("iframe").attr("src"))){
                $(".optreat-forum-content").find("iframe").attr("src", "../../tpl/forumshow/forumlist.html");
            }
        }
	});
	
	function initDeskTopMenuRightClick(){
		winui.desktop.initRightMenu({
			item: [{
            	icon: 'fa fa-folder-open-o',
        		text: "打开",
        		callBack: function (id, elem) {
                	var item = $(elem);
                	if(item.find(".icon-drawer").length > 0){
                		showBigWin(elem);
                	} else {
                		OpenWindow(elem);
                		winui.window.close($('#childWindow').parent());
                	}
                }
            }, {
            	text: '--'
            }, {
            	icon: 'fa fa-tags',
        		text: "重命名/配置",
        		callBack: function (id, elem, events) {
        			AjaxPostUtil.request({url: reqBasePath + "sysevewindragdrop005", params: {rowId: id}, type: 'json', method: "POST", callback: function (json) {
						var menuType = json.bean.menuType;
						if(menuType == '1'){//系统菜单
							winui.window.msg('无法编辑系统菜单。', {shift: 6, skin: 'msg-skin-message'});
						}else if(menuType == '2'){//自定义快捷方式
							winui.window.close($('#childWindow').parent());
							parentRowId = id;
							_openNewWindows({
								url: "../../tpl/index/editMenu.html",
								title: "重置快捷方式",
								pageId: "editMenuDialog",
								area: ['700px', '450px'],
								skin: 'top-message-mation',
								callBack: function(refreshCode){
									//此处不需要去获取当前滚动展示的模块
									$("#winui-desktop").find('div[win-id="' + childParams.rowId + '"]').attr("win-title", childParams.titleName);
									$("#winui-desktop").find('div[win-id="' + childParams.rowId + '"]').attr("win-url", childParams.menuUrl);
									$("#winui-desktop").find('div[win-id="' + childParams.rowId + '"]').attr("win-menuiconbg", childParams.menuIconBg);
									$("#winui-desktop").find('div[win-id="' + childParams.rowId + '"]').attr("win-menuiconcolor", childParams.menuIconColor);
									if(childParams.menuIconType === '1' || childParams.menuIconType == 1){//icon
										$("#winui-desktop").find('div[win-id="' + childParams.rowId + '"]').find('div').attr('class', 'winui-icon winui-icon-font');
										$("#winui-desktop").find('div[win-id="' + childParams.rowId + '"]').attr("win-icon", childParams.menuIcon);
										if($("#winui-desktop").find('div[win-id="' + childParams.rowId + '"]').find('div').find('i').length == 0){
											var str = '<i class="" win-i-id="' + childParams.rowId + '"></i>';
											$("#winui-desktop").find('div[win-id="' + childParams.rowId + '"]').find('div').html(str);
										}
										$("#winui-desktop").find('div[win-id="' + childParams.rowId + '"]').find('div').find('i').attr("class", "fa " + childParams.menuIcon + " fa-fw");
										$("#winui-desktop").find('div[win-id="' + childParams.rowId + '"]').find('div').find('i').css({'color': childParams.menuIconColor});
										$("#winui-desktop").find('i[win-i-id="' + childParams.rowId + '"]').css({'background-color': childParams.menuIconBg, 'color': childParams.menuIconColor});
										var iconITag = $("#winui-desktop").find('i[win-i-id="' + childParams.rowId + '"]');
										$.each(iconITag, function(i, item){
											if($(item).hasClass("icon-drawer-icon")){
												$(item).attr("class", "fa " + childParams.menuIcon + " fa-fw icon-drawer-icon");
											} else {
												$(item).attr("class", "fa " + childParams.menuIcon + " fa-fw");
											}
										});
									}else if(childParams.menuIconType === '2' || childParams.menuIconType == 2){//图片
										$("#winui-desktop").find('div[win-id="' + childParams.rowId + '"]').find('div').attr('class', 'winui-icon winui-icon-img');
										$("#winui-desktop").find('div[win-id="' + childParams.rowId + '"]').attr("win-icon", childParams.menuIconPic);
										if($("#winui-desktop").find('div[win-id="' + childParams.rowId + '"]').find('div').find('i').length > 0){
											var str = '<img src="' + fileBasePath + childParams.menuIconPic + '">';
											$("#winui-desktop").find('div[win-id="' + childParams.rowId + '"]').find('div').html(str);
										}
									}
									$("#winui-desktop").find('div[win-id="' + childParams.rowId + '"]').find('div').css({'background-color': childParams.menuIconBg, 'color': childParams.menuIconColor});
									$("#winui-desktop").find('div[win-id="' + childParams.rowId + '"]').find('p').html(childParams.menuName);
									top.winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
								}});
						}else if(menuType == '3'){//自定义菜单盒子
							parentRowId = id;
							_openNewWindows({
								url: "../../tpl/index/editMenuBox.html",
								title: "重命名盒子",
								pageId: "editMenuBoxDialog",
								area: ['600px', '200px'],
								skin: 'top-message-mation',
								callBack: function(refreshCode){
									$("#winui-desktop").find('div[id="' + childParams.rowId + '"]').attr("win-title", childParams.menuBoxName);
									$("#winui-desktop").find('div[id="' + childParams.rowId + '"]').find('p').html(childParams.menuBoxName);
									top.winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
								}});
						}
					}});
                }
            }, {
            	text: '--'
            }, {
            	icon: 'fa fa-trash-o fa-lg',
        		text: "删除",
        		callBack: function (id, elem, events) {
                    layer.confirm('确定永久删除吗？', { id: 'delete-always-menu', icon: 3, title: '删除快捷方式/文件夹', skin: 'msg-skin-message', success: function(layero, index){
            			var times = $("#delete-always-menu").parent().attr("times");
            			var zIndex = $("#delete-always-menu").parent().css("z-index");
            			$("#layui-layer-shade" + times).css({'z-index': zIndex});
            		}}, function (index) {
    					layer.close(index);
    					AjaxPostUtil.request({url: reqBasePath + "sysevewindragdrop003", params: {rowId: id}, type: 'json', method: "POST", callback: function (json) {
							$(elem).remove();
							$("i[win-i-id=" + $(elem).attr('win-id') + "]").remove();
							$("div[win-id=" + $(elem).attr('win-id') + "]").remove();
							//从新排列桌面app
							events.reLocaApp();
    					}});
    				});
                }
            }]
        });
	}

	var nowType = isNull(getCookie("languageType")) ? "zh" : getCookie("languageType");
    if(nowType == "zh"){
        // 中文
        $("#switchLanguage").find("a").attr("title", "切换英文");
		$("#switchLanguage").find("a").html('<svg t="1598752278678" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="5850" width="16" height="16"><path d="M294.144 617.301333v-102.4h101.717333v102.4H294.144z m159.061333 0v-102.4H555.52v102.4h-102.4zM884.394667 51.968c76.202667 0 138.581333 61.44 139.093333 136.106667v476.16c0 74.666667-62.378667 136.106667-138.581333 136.106666h-32.768v41.386667c0 74.666667-62.378667 136.106667-138.581334 136.106667H138.581333C62.378667 977.834667 0 916.394667 0 841.728V353.792c0-73.216 59.306667-133.034667 132.949333-136.106667v-29.610666c0-74.752 62.464-136.106667 138.666667-136.106667h612.693333zM611.754667 687.786667V462.250667H452.693333V384h-56.832v78.250667H239.872V690.346667h54.272v-25.088h101.717333V806.4h56.832V665.258667h102.314667v22.528h56.746667z m333.994666 37.376a83.541333 83.541333 0 0 0 25.6-60.842667v-476.16a85.333333 85.333333 0 0 0-25.6-60.416 88.490667 88.490667 0 0 0-61.44-25.002667h-612.693333a86.186667 86.186667 0 0 0-87.04 85.333334v29.696h528.469333c76.202667 0 138.581333 61.44 138.581334 136.106666V750.08h32.768c23.04 0 44.970667-8.704 61.44-25.088z" fill="#ddd" p-id="5851"></path></svg>');
    } else {
        // 英文
        $("#switchLanguage").find("a").attr("title", "切换中文");
        $("#switchLanguage").find("a").html('<svg t="1598752407969" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="6614" width="16" height="16"><path d="M884.394667 52.053333c76.202667 0 138.581333 61.269333 139.093333 136.021334v476.16c0 74.666667-62.378667 136.106667-138.581333 136.106666h-32.768v41.386667c0 74.666667-62.378667 136.106667-138.581334 136.106667H138.581333C62.378667 977.834667 0 916.394667 0 841.728V353.792c0-73.216 59.306667-133.034667 132.949333-136.106667v-29.610666c0-74.752 62.464-136.106667 138.666667-136.106667h612.693333z m61.44 673.024a83.541333 83.541333 0 0 0 25.514666-60.842666v-476.16a85.333333 85.333333 0 0 0-25.6-60.416 88.490667 88.490667 0 0 0-61.44-25.002667h-612.693333a86.186667 86.186667 0 0 0-87.04 85.333333v29.696h528.469333c76.202667 0 138.581333 61.44 138.581334 136.106667V750.08h32.768c23.04 0 44.970667-8.704 61.44-25.088zM398.933333 484.181333v42.496H254.634667V651.093333h-57.856v49.664h169.472c-3.584 6.144-7.68 11.776-12.288 17.92-27.136 31.744-79.872 58.88-157.696 80.384l29.184 46.592c77.824-21.504 134.144-51.712 168.96-91.136 12.8-15.872 23.552-33.792 32.768-53.76h1.024c27.136 60.416 94.72 108.544 202.752 145.408l29.696-48.128c-90.624-24.576-150.016-56.832-177.664-97.28h172.544V651.093333h-58.88V526.677333h-143.36v-42.496h-54.272zM306.858667 651.093333v-74.752h92.16v7.68c-1.536 23.552-5.12 46.08-11.264 67.072h-80.896z m137.216 0c4.608-20.48 7.68-42.496 9.216-67.072v-7.68H544.426667v74.752H444.074667zM199.338667 407.381333v49.152h113.152v43.008h54.272V456.533333H485.546667v43.008h54.272V456.533333h113.152v-49.152H539.818667V366.933333H485.546667v40.448H366.762667V366.933333h-54.272v40.448H199.338667z" fill="#ddd" p-id="6615"></path></svg>');
    }
    // 切换语言
    $("body").on("click", "#switchLanguage", function (e) {
        var nowType = isNull(getCookie("languageType")) ? "zh" : getCookie("languageType");
        if(nowType == "zh"){
        	// 中文，设置为英文
            setCookie('languageType', "cn");
		} else {
            // 英文，设置为中文
            setCookie('languageType', "zh");
		}
        window.location.reload();
    });
	
	//切换风格
	$('body').on('click', '.winui-switching-style', function (e) {
		location.href = "../../tpl/traditionpage/index.html";
	});
	
	//顶部滚动事件
	$('body').on('click', '#left-scoolor', function (e) {
		var scoolor = $('.switch-menu').scrollLeft();
		if(scoolor > 0){
			scoolor = scoolor - ($('.switch-menu').width() - 200) / 2;
			$('.switch-menu').scrollLeft(scoolor);
		}
	});
	$('body').on('click', '#right-scoolor', function (e) {
		var scoolor = $('.switch-menu').scrollLeft();
		if(scoolor < ($('.switch-menu').width() - 200)){
			scoolor = ($('.switch-menu').width() - 200) / 2 + scoolor;
			$('.switch-menu').scrollLeft(scoolor);
		}
	});
	
	initLoadTopScoolor();
	//监听窗口变化
	$(window).resize(function(){
		initLoadTopScoolor();
	});
	
	function initLoadTopScoolor(){
		$('.switch-menu').scrollLeft(10);
		if($('.switch-menu').scrollLeft() > 0){
			$(".switch-menu-scoolor").css({"visibility": "visible"});
		} else {
			$(".switch-menu-scoolor").css({"visibility": "hidden"});
		}
		$('.switch-menu').scrollLeft(0);
	}

	//页面刷新或者关闭时，关闭socket
	window.onbeforeunload = function(){
		if (etiger != null) {
			etiger.socket.close();
		}
	}
	
	matchingLanguage();
	
    exports('index', {});
});
