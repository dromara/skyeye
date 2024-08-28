﻿/**
 * 窗口管理模块
 */
layui.define(['layer', 'winui'], function (exports) {
    "use strict";
    var $ = layui.jquery, THIS = 'winui-this', MOVE = '.layui-layer-title', taskbarHeight = 40;

    //弹窗构造函数
    var WinLayer = function () {
        this.settings = {
            type: 2    //iframe层
            , anim: 0    //平滑放大
            , miniAnim: 0    //缩小动画0
            , maxOpen: -1    //禁用打开最大化
            , area: ['70vw', '80vh']
            , offset: 'auto'  //居中
            , min: true  //显示最小化按钮
            , max: true  //显示最大化按钮
            , refresh: false    //显示刷新按钮
        }
        this.minisizeAnim = ['anim-minisize0', 'anim-minisize1'];
    };
    //配置
    WinLayer.prototype.config = function (options) {
        options = options || {};
        for (var key in options) {
            this.settings[key] = options[key];
        }
        return this;
    }
    //打开窗口
    WinLayer.prototype.open = function (options) {
        var windowfunc = this;
        MOVE = '.layui-layer-title';
        //获取window，判断window是否存在
        if (common.getWindow(options.id)) {
            //置顶window
            windowfunc.setTop(options.id);
            //隐藏开始菜单
            $('.winui-start').fadeOut(200);
            //移除开始按钮的选中样式
            $('.winui-taskbar-start').removeClass(THIS);
            //返回windowId
            return options.id;
        }
        if (!winui.taskAuto()) {
            return;
        }
        if(isNull(options.shadeClose)){
        	options.shadeClose = false;
        }
        if(isNull(options.skin)){
        	options.skin = 'winui-window';
        }
        if(isNull(options.closeBtn) && options.closeBtn != 0){
        	options.closeBtn = '1';
        }
        if(isNull(options.maxmin) && options.maxmin != false){
        	options.maxmin = true;
        }
        if(isNull(options.zIndex)){
        	options.zIndex = layer.zIndex;
        }
        if(isNull(options.addToButtomMenu) && options.addToButtomMenu != false){
        	options.addToButtomMenu = true;
        }
        if (!isNull(options.move) || options.move == false){
        	MOVE = options.move;
        }
        if(isNull(options.fixed) && options.fixed != false){
        	options.fixed = true;
        }

        //打开窗口
        var windowIndex = layer.open({
            id: options.id || winui.guid(),
            type: options.type || this.settings.type,
            title: options.title || '',
            content: options.content || '',
            area: options.area || this.settings.area,
            offset: options.offset || this.settings.offset,
            anim: options.anim || this.settings.anim,
            move: MOVE,
            fixed: options.fixed,
            shade: options.shade || 0,
            maxmin: options.maxmin,   //允许最大最小化
            moveOut: true,  //允许拖出窗外
            skin: options.skin,   //窗口皮肤
            zIndex: options.zIndex,
            shadeClose: options.shadeClose, 	//点击空白处关闭
            scrollbar: false,
            closeBtn: options.closeBtn,
            winBottomMenuIcon: options.winBottomMenuIcon || '0',//是否只展示图标，1是  0否
            //销毁回调
            end: options.end || function () {
            	if(typeof(options.cancel) == "function") {
        			options.cancel(windowDom, index);
        		}
            },
            //打开回调
            success: function (layero, index) {
            	//重置遮罩层
            	var times = $("#" + options.id || winui.guid()).parent().attr("times");
				var zIndex = $("#" + options.id || winui.guid()).parent().css("z-index");
				$("#layui-layer-shade" + times).css({'z-index': zIndex});
            	
        		common.setWindowBody(layero);
        		common.showLoading(layero, options);
        		if(typeof(options.success) == "function") {
        			options.success(layero, index);
        		}
            },
            //关闭回调
            cancel: function (index, windowDom) {
                //1.隐藏窗口（layui会自动移除窗口）
                $(windowDom).addClass('layui-hide');
                //2.移除任务项
                var currTaskItem = common.getTaskItemByWindowDom(windowDom);
                $(currTaskItem).remove();
                //3.将当前可见的最顶层窗口对应的任务项选中
                var topWindow = common.getTopWindow();
                var topTaskItem = common.getTaskItemByWindowDom(topWindow);
                common.selectDom(topTaskItem);
            },
            //最小化回调
            min: function (window) {
                windowfunc.minimize(window);
                return false;   //更改了layui的最小化事件
            },
            //最大化回调
            full: function (window) {
                $(window).find('.layui-layer-min').css('display', '');
                $(window).find('.layui-layer-max.layui-layer-maxmin').html('<i class="fa fa-window-restore" style="font-size:14px;left:18px;top:10px" ><i>');

                if ($('body').attr('class').replace(/\btaskbarIn.*?\b/g, '') !== $('body').attr('class')) {
                    //根据任务栏模式调整位置
                    switch (winui.settings.taskbarMode) {
                        case 'top':
                            $(window).css('top', taskbarHeight + 'px');
                            break;
                        case 'bottom':
                            $(window).css('top', '0px');
                            break;
                        case 'left':
                            break;
                        case 'right':
                            break;
                        default:
                    }
                    //重置window高度
                    $(window).css('height', parseInt($(window).css('height').replace('px', '')) - taskbarHeight + 'px');
                    $(window).find('.layui-layer-content').css('height', parseInt($(window).find('.layui-layer-content').css('height').replace('px', '')) - taskbarHeight + 'px');
                    //重置window iframe高度
                    var ifarme = $(window).find('iframe')[0];
                    if (ifarme) {
                        $(ifarme).css('height', parseInt($(ifarme).css('height').replace('px', '')) - taskbarHeight + 'px');
                    }
                }

                common.setWindowBody(window);
            },
            //还原回调
            restore: function (window) {
                $(window).find('.layui-layer-max').html('<i class="layui-icon" style="font-size:12px;left:18px;" >&#xe626;<i>');
                common.setWindowBody(window);
            },
            //拉伸回调
            resizing: function (window) {
                common.setWindowBody(window);
            },
            //拖动完毕后的回调方法
            moveEnd: function (window) {
            }
        });
        var windowDom = common.getWindow(options.id);
        if (!isNull(windowDom)){
        	//重新获取window
            if (((options.type || this.settings.type) == 2) && (options.refresh === undefined ? this.settings.refresh : options.refresh)) {
                $(windowDom).find('.layui-layer-setwin').prepend('<a class="layui-layer-ico layui-layer-refresh"><i class="layui-icon" style="font-size:14px;left:17px;font-weight:600;">&#x1002;<i></a>');
                $(windowDom).find('.layui-layer-refresh').on('click', function (e) {
                    var $iframe = $(windowDom).find('iframe');
                    try {
                        $iframe.attr('src', $iframe[0].contentWindow.location.href);
                    } catch (e) {
                        $iframe.attr('src', $iframe.attr('src'));
                    }
                });
            }
            $(windowDom).find('.layui-layer-max').html('<i class="layui-icon" style="font-size:12px;left:18px;" >&#xe626;<i>');
            $(windowDom).find('.layui-layer-close').html('<i class="layui-icon">&#x1006;<i>');
            //打开最大化
            switch ((options.maxOpen || this.settings.maxOpen)) {
                case 1:
                    if ((options.anim || this.settings.anim) !== -1) {
                        $(windowDom).one('webkitAnimationEnd mozAnimationEnd MSAnimationEnd oanimationend animationend', function () {
                            $(windowDom).find('.layui-layer-max').trigger('click');
                        });
                    } else {
                        $(windowDom).find('.layui-layer-max').trigger('click');
                    }
                    break;
                case 2:
                    var top;
                    //根据任务栏模式调整位置
                    switch (winui.settings.taskbarMode) {
                        case 'top':
                            top = taskbarHeight + 'px';
                            break;
                        case 'bottom':
                            top = '0';
                            break;
                        case 'left':
                            break;
                        case 'right':
                            break;
                        default:
                    }
                    layer.style(windowIndex, {
                        'top': top,
                        'left': '0',
                        'width': $(window).width() + 'px',
                        'height': $(window).height() - taskbarHeight + 'px'
                    });
                    break;
                default:
            };
            //去除最小化按钮
            if (!(options.min === undefined ? this.settings.min : options.min))
                $(windowDom).find('.layui-layer-min').remove();
            //去除最大化按钮
            if (!(options.max === undefined ? this.settings.max : options.max))
                $(windowDom).find('.layui-layer-max').remove();
            if(options.addToButtomMenu){
            	//增加任务项
            	var taskItem = common.addTaskItem(options.id, options.title, options.iconTitle, options.winBottomMenuIcon);
            	//选中任务项
            	common.selectDom(taskItem);
            	//绑定任务项mouseup事件
            	common.resetMouseUp(taskItem, call.taskItemMouseUp);
            }
            //双击窗口标题栏最大化(由于不明原因，标题栏的拖动好像阻碍了标题栏的双击事件，所以这里用mousedown模拟双击)
            var lastTime = 0;
            $(windowDom).find(MOVE).on('mousedown', function () {
                $('.layui-layer-move').css('cursor', 'default');
                var thisTime = new Date().getTime();
                if (thisTime - lastTime < 300) {
                    $(windowDom).find('.layui-layer-max').trigger('click');
                }
                lastTime = thisTime;
            });
            //移除最小化最大化关闭按钮a标签的href属性
            $(windowDom).find('.layui-layer-setwin').children('a').removeAttr('href');
            //点击置顶
            layer.setTop($(windowDom));
            //手动置顶windowDom
            windowfunc.setTop(windowDom);
            //鼠标按下窗口时选中对应任务栏
            $(windowDom).on('mousedown', function () {
                var taskItem = common.getTaskItemByWindowDom(this);
                common.selectDom(taskItem);
            });
        }
        
        //隐藏开始菜单
        $('.winui-start').fadeOut(200);
        //移除开始按钮的选中样式
        $('.winui-taskbar-start').removeClass(THIS);

        //返回windowId
        return options.id;
    };
    //最小化窗口
    WinLayer.prototype.minimize = function (param) {
        if (param !== undefined) {
            var windowDom = common.getWindow(param);
            //缩小动画
            var anim = this.minisizeAnim[this.settings.miniAnim];
            if (anim) {
                $(windowDom).addClass(anim);
                setTimeout(function () {
                    //1.隐藏窗体
                    $(windowDom).addClass('layui-hide').removeClass(anim);
                    //2.去除当前任务项选中
                    var currTaskItem = common.getTaskItemByWindowDom(windowDom);
                    $(currTaskItem).removeClass(THIS);
                    //3.将当前可见的最顶层窗口对应的任务项选中
                    var topWindow = common.getTopWindow();
                    var topTaskItem = common.getTaskItemByWindowDom(topWindow);
                    common.selectDom(topTaskItem);
                }, 200);
            } else {
                //1.隐藏窗体
                $(windowDom).addClass('layui-hide');
                //2.去除当前任务项选中
                var currTaskItem = common.getTaskItemByWindowDom(windowDom);
                $(currTaskItem).removeClass(THIS);
                //3.将当前可见的最顶层窗口对应的任务项选中
                var topWindow = common.getTopWindow();
                var topTaskItem = common.getTaskItemByWindowDom(topWindow);
                common.selectDom(topTaskItem);
            }
        } else {
            //隐藏所有窗体
            $('.winui-window').addClass('layui-hide');
            //去除所有任务项选中
            $('.winui-task-item').removeClass(THIS);
        }
    };
    //关闭窗口
    WinLayer.prototype.close = function (param) {
        var windowDom = common.getWindow(param);
        $(windowDom).find('.layui-layer-ico.layui-layer-close').trigger('click');
    };

    //判断变量是否是Dom对象
    var isDom = (typeof HTMLElement === 'object') ?
        function (obj) {
            return obj instanceof HTMLElement;
        } :
        function (obj) {
            return (obj && typeof obj === 'object' && obj.nodeType === 1 && typeof obj.nodeName === 'string');
        }
    //判断变量是否是Jq对象
    var isJquery = function (obj) {
        return obj instanceof $;
    }

    //公共事件
    var common = {
        //获取WindowId（传入WindowDom）
        getWindowIdByWindow: function (window) {
            return $(window).find('.layui-layer-content').prop('id');
        },
        //获取任务项（传入WindowDom）
        getTaskItemByWindowDom: function (WindowDom) {
            var windowId = $(WindowDom).find('.layui-layer-content').prop('id');
            return common.getTaskItem(windowId);
        },
        //获取任务项的Dom对象（传入id或者任务项的dom对象或者任务项的jq对象）
        getTaskItem: function (param) {
            return typeof param === 'string' ? $('.winui-task-item[win-id=' + param + ']')[0] : (isDom(param) ? param : (isJquery(param) ? param[0] : null));
        },
        //获取窗口的Dom对象（传入id或者窗口的dom对象或者窗口的jq对象）
        getWindow: function (param) {
            return typeof param === 'string' ? $('.winui-window:has(.layui-layer-content[id=' + param + '])')[0] : (isDom(param) ? param : (isJquery(param) ? param[0] : null));
        },
        //为dom添加winui-this类
        selectDom: function (selector) {
            $(selector).addClass(THIS).siblings().removeClass(THIS);
        },
        //添加任务项（返回添加的任务项dom）
        addTaskItem: function (id, title, iconTitle, winBottomMenuIcon) {
        	var taskItem;
        	if (winBottomMenuIcon === '1') {//只展示图标
        		var menuTitle = $(title);
        		$(menuTitle[0]).css({'display': 'none'});
        		$(menuTitle[1]).css({'display': 'none'});
        		title = $(menuTitle[0]).prop("outerHTML") + $(menuTitle[1]).prop("outerHTML");
        		taskItem = $('<li win-id="' + id + '" class="winui-task-item task-item-icon-box">' + iconTitle + title + '</li>');
        	} else {//展示图标+文字
        		var menuTitle = $(iconTitle);
        		$(menuTitle[0]).css({'display': 'none'});
        		iconTitle = $(menuTitle[0]).prop("outerHTML");
        		taskItem = $('<li win-id="' + id + '" class="winui-task-item">' + iconTitle + title + '</li>');
        	}
            $('.winui-taskbar-task:visible').append(taskItem);
            return taskItem;
        },
        //重置元素的MouseUp事件
        resetMouseUp: function (selector, func) {
            if (typeof func !== "function") return;
            $(selector).off('mouseup').on('mouseup', func);
        },
        //获得可见的最顶层Window对象
        getTopWindow: function () {
            var nextWindow;
            var initIndex = 0;
            $('.winui-window:visible').each(function () {
                var zIndex = $(this).css('z-index');
                if (zIndex > initIndex) {
                    nextWindow = this;
                }
            });
            return nextWindow;
        },
        //判断是否是顶层Window对象
        isTopWindow: function (window) {
            if (!$(window).is(':visible')) return false;
            var currIndex = $(window).css('z-index');
            var returnVal = true;
            $('.winui-window:visible').each(function () {
                var zIndex = $(this).css('z-index');
                if (zIndex > currIndex) {
                    returnVal = false;
                    return returnVal;
                }
            });
            return returnVal;
        },
        //调整iframe中body的高度
        setWindowBody: function (window) {
            var body = $(window).find('iframe').contents().find('body.winui-window-body');
            if (body) {
                body.css('height', $(window).find('iframe').css('height'));
            }
        },
        //移除layui-hide类样式
        showWindow: function (param) {
            $(common.getWindow(param)).removeClass('layui-hide');
        },
        //添加layui-hide类样式
        hideWindow: function (param) {
            $(common.getWindow(param)).addClass('layui-hide');
        },
        //显示加载进度
        showLoading: function(window, options){
        	var body = $(window).find('iframe').contents().find('body');
            if (body) {
            	var str = '<div class="page-load" ';
            	if (!isNull(options.loadBgColor)){
            		str += 'style="background-color:' + options.loadBgColor + ';';
            		if (!isNull(options.zIndex)){
            			var zIndex = options.zIndex + 1;
            			str += 'z-index:' + zIndex;
            		}
            		str += '"';
            	}
            	str += '>';
            	if (!isNull(options.loadIcon)){
            		str += '<div class="page-icon">';
            		if(options.loadIcon.indexOf('fa-') != -1){//icon
	            		str += '<i class="fa page-icon-title fa-fw ' + options.loadIcon + '" ';
	            		if (!isNull(options.loadIconColor)){
	                    	str += 'style="color: ' + options.loadIconColor + '" ';
	                    } else {
	                    	str += 'style="color: white" ';
	                    }
	            		str += '></i></div>';
            		} else {//图片
            			str += '<img class="page-icon-title" src="' + options.loadIcon + '"/>';
            		}
            	}
            	str += '<div class="page-icon-load"><i class="fa page-icon-title-load fa-fw fa-spin fa-spinner"></i>';
            	str += '<font>加载中</font></div></div>';
                //页面加载项
//            	body.append(str);
//                $(window).ready(function (e) {
//                	setTimeout(function (e) {
//                		body.find(".page-load").remove();
//                	}, 1500);
//                });
            }
        }
    };

    //基础事件
    var call = {
        //阻止事件冒泡
        sp: function (event) {
            layui.stope(event);
        },
        //任务栏项鼠标释放事件
        taskItemMouseUp: function (e) {
            if (!e) e = window.event;
            var currentItem = this;
            if (e.button == 0) {
                //左键点击
                //获取window对象
                var currWindow = common.getWindow($(currentItem).attr('win-id'));
                //获取window的Id
                var id = common.getWindowIdByWindow(currWindow);
                //判断是否是顶层window
                if (common.isTopWindow(currWindow)) {
                    //如果是顶层窗口则缩小
                    //1.隐藏窗体
                    common.hideWindow(currWindow);
                    //2.去除当前任务项选中
                    var currTaskItem = common.getTaskItemByWindowDom(currWindow);
                    $(currTaskItem).removeClass(THIS);
                    //3.将当前可见的最顶层窗口对应的任务项选中
                    var topWindow = common.getTopWindow();
                    var topTaskItem = common.getTaskItemByWindowDom(topWindow);
                    common.selectDom(topTaskItem);
                } else {
                    //如果不是顶层则置顶
                    if ($(currWindow).is(':hidden')) {
                        //显示窗口
                        common.showWindow(currWindow);
                        //选中当前点击的任务项
                        common.selectDom(this);
                    }
                    //置顶窗口
                    $(currWindow).find(MOVE).trigger('mousedown').trigger('mouseup');
                }
            }
            if (e.button == 2) {
                //右键点击
                common.selectDom(currentItem);

                var left = $(currentItem).offset().left;
                var style = '';
                //根据任务栏模式调整位置
                switch (winui.settings.taskbarMode) {
                    case 'top':
                        style = 'top:' + taskbarHeight + 'px;left:' + (left - 20) + 'px;';
                        break;
                    case 'bottom':
                        style = 'bottom:' + taskbarHeight + 'px;left:' + (left - 20) + 'px;';
                        break;
                    case 'left':
                        break;
                    case 'right':
                        break;
                    default:
                }
                var div = '<ul class="task-contextmenu" style="' + style + 'width:auto">';
                div += '<li class="closeAll">关闭全部窗口</li>';   //关闭所有标签
                div += '<li class="closeOther">关闭其他窗口</li>';   //关闭所有标签
                div += '<li class="close"><i class="fa fa-power-off fa-fw"></i>关闭当前窗口</li>';    //关闭窗口
                div += '</ul>';
                //移除之前任务项右键菜单
                $('.task-contextmenu').remove();
                //渲染当前任务项右键菜单
                $('body').append(div);
                //绑定关闭所有窗口事件
                $('.task-contextmenu .closeAll').off('mouseup').on('mouseup', function (e) {
                    if (!e) e = window.event;
                    if (e.button == 0) {
                        $('.winui-window.layui-layer').find('.layui-layer-ico.layui-layer-close').trigger('click');
                    }
                    $('.task-contextmenu').remove();
                });
                //绑定关闭其他窗口事件
                $('.task-contextmenu .closeOther').off('mouseup').on('mouseup', function (e) {
                    if (!e) e = window.event;
                    if (e.button == 0) {
                        var windowDom = common.getWindow($(currentItem).attr('win-id'));
                        $('.winui-window.layui-layer').not(windowDom).find('.layui-layer-ico.layui-layer-close').trigger('click');
                    }
                    $('.task-contextmenu').remove();
                });
                //绑定关闭当前窗口事件
                $('.task-contextmenu .close').off('mouseup').on('mouseup', function (e) {
                    if (!e) e = window.event;
                    if (e.button == 0) {
                        var windowDom = common.getWindow($(currentItem).attr('win-id'));
                        $(windowDom).find('.layui-layer-ico.layui-layer-close').trigger('click');
                    }
                    $('.task-contextmenu').remove();
                });
                //阻止右键菜单冒泡
                $('.task-contextmenu li').on('click mousedown', call.sp);
            }
        }
    };

    var winLayer = new WinLayer();
    
    // 置顶窗口（显示窗口且置于顶层）
    winLayer.setTop = function (param) {
        var windowDom = common.getWindow(param);
        if (windowDom) {
            if ($(windowDom).is(':hidden')) {
                //显示窗口
                $(common.getWindow(windowDom)).removeClass('layui-hide');
                //选中当前点击的任务项
                common.selectDom(this);
            }
            //置顶窗口
            $(windowDom).trigger('mousedown');
        }
    };

    // 显示信息（为了显示在所有窗口最前面而添加的方法）
    winLayer.msg = function (msg, options, func) {
    	//移除请求遮罩层
        $("body").find(".mask-req-str").remove();
        if (!options) {
            options = {
                offset: '40px'
            };
        }
        options.zIndex = layer.zIndex;
        layer.msg(msg, options, func);
    };

    // 显示提示框（为了显示在所有窗口最前面而添加的方法）
    winLayer.confirm = function (msg, options, yes, cancel) {
        var type = typeof options === 'function';
        if (type) {
            cancel = yes;
            yes = options;
            options = {};
        }
        options.zIndex = layer.zIndex;
        if (!isNull(options.skin))
        	options.skin += ' layer-ext-winconfirm';
        else
        	options.skin = 'layer-ext-winconfirm';
        layer.confirm(msg, options, yes, cancel);
        // 替换窗体的关闭图标
        $('.layer-ext-winconfirm').find('.layui-layer-close').html('<i class="layui-icon">&#x1006;<i>');
        // 移除移动窗体的div
        $('.layui-layer-move').remove();
        // 移除最小化最大化关闭按钮a标签的href属性
        $('.layer-ext-winconfirm').find('.layui-layer-setwin').children('a').removeAttr('href');
    };

    // 显示加载（为了显示在所有窗口最前面而添加的方法）
    winLayer.load = function (icon, options) {
        icon = icon || 1;
        options = options || {};
        options.zIndex = layer.zIndex;
        return layer.load(icon, options);
    };

    // 打开主题设置窗口
    winLayer.openTheme = function (winBottomMenuIcon) {
        var that = this;
        $.get(winui.path + '../../../tpl/sysTheme/sysTheme.html', {}, function (content) {
        	var title = '<i class="fa title-icon fa-fw fa-cog" style="background-color: #0491fe;color: #ecf3f8;"></i>' + '<font class="win-title-class">主题设置</font>';
        	var iconTitle = '<i class="fa title-icon-big fa-fw fa-cog" style="background-color: #0491fe;color: #ecf3f8;"></i>';
            that.open({
                id: 'winui-theme',
                type: 1,
                title: title,
                content: content,
                winBottomMenuIcon: winBottomMenuIcon,
                iconTitle: iconTitle
            });
        });
    }
    
    // 打开我的消息通知
    winLayer.openSysNotice = function (winBottomMenuIcon) {
        var that = this;
    	var title = '<i class="fa title-icon fa-fw fa-list-ul" style="background-color: #0491fe;color: #ecf3f8;"></i>' + '<font class="win-title-class">消息中心</font>';
    	var iconTitle = '<i class="fa title-icon-big fa-fw fa-list-ul" style="background-color: #0491fe;color: #ecf3f8;"></i>';
    	that.open({
            id: 'winui-sys-notice',
            type: 2,
            title: title,
            content: systemCommonUtil.getUrl('FP2024013100007', null),
            winBottomMenuIcon: winBottomMenuIcon,
            iconTitle: iconTitle
        });
    }

    winui.window = winLayer;

    exports('window', {});

    delete layui.window;
});