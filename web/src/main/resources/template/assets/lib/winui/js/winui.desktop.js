/**
 * 桌面模块
 */
layui.define(['jquery', 'layer', 'winui'], function (exports) {
    "use strict";

    var $ = layui.jquery;

    //桌面构造函数
    var Desktop = function (options) {
        this.options = options || {
            url: winui.path + 'json/desktopmenu.json',
            method: 'get'
        };
        this.data = null;
    };

    // 渲染HTML
    Desktop.prototype.render = function (callback) {
        if (this.data === null) return;
        var html = '';
        $(this.data).each(function (index, item) {
        	if(languageType == 'cn'){
				item.name = item.menuNameEn;
			}
        	var id = isNull(item.id) ? '' : 'win-id="' + item.id + '"',
        		url = isNull(item.pageURL) ? '' : 'win-url="' + item.pageURL + '"',
        		title = isNull(item.name) ? '' : 'win-title="' + item.name + '"',
        		opentype = isNull(item.openType) ? '' : 'win-opentype="' + item.openType + '"',
        		maxOpen = isNull(item.maxOpen) ? '' : 'win-maxOpen="' + item.maxOpen + '"',
				menuIconBg = isNull(item.menuIconBg) ? '' : 'win-menuIconBg="' + item.menuIconBg + '"',
				menuIconColor = isNull(item.menuIconColor) ? '' : 'win-menuIconColor="' + item.menuIconColor + '"',
				menuSysWinUrl = isNull(item.sysWinUrl) ? '' : 'win-sysWinUrl="' + item.sysWinUrl + '"',
				menuIcon = '';
        	var icon = "", isFaIcon = "";
        	if(item.menuIconType === '1'){//icon
        		menuIcon = isNull(item.icon) ? '' : 'win-icon="' + item.icon + '"';
        		if (!isNull(item.menuIconColor)){
        			icon = '<i class="fa ' + item.icon + ' fa-fw" style="color: ' + item.menuIconColor + '" win-i-id="' + item.id + '"></i>';
        		} else {
        			icon = '<i class="fa ' + item.icon + ' fa-fw" win-i-id="' + item.id + '"></i>';
        		}
        		isFaIcon = "winui-icon-font";
        	}else if(item.menuIconType === '2'){//图片
        		menuIcon = isNull(item.menuIconPic) ? '' : 'win-icon="' + item.menuIconPic + '"';
        		icon = '<img src="' + fileBasePath + item.menuIconPic + '" class="desktop-img"/>';
        		isFaIcon = "winui-icon-img";
        	}
        	if(isNull(item.childs)){//没有子菜单
        		if(item.pageURL == '--' && item.menuLevel == '0'){
        			html += '<div class="winui-desktop-item win-menu-group" id="' + item.id + '" ' + id + ' ' + url + ' ' + title + ' ' + opentype + ' ' + maxOpen + ' ' + menuIconBg + ' ' + menuIconColor + '>';
            		html += '<div class="winui-icon ' + isFaIcon + '">';
            		html += '<div class="icon-drawer"></div>';
            		html += '<div class="icon-child"></div>';
            		html += '</div>';
            		html += '<p>' + item.name + '</p>';
            		html += '</div>';
        		} else {
        			html += '<div class="winui-desktop-item sec-btn" ' + id + ' ' + url + ' ' + title + ' ' + opentype + ' ' + maxOpen + ' ' + menuIconBg + ' ' + menuIconColor + ' ' + menuIcon + ' ' + menuSysWinUrl + '>';
        			if (!isNull(item.menuIconBg)){
        				html += '<div class="winui-icon ' + isFaIcon + '" style="background-color: ' + item.menuIconBg + '">';
        			} else {
        				html += '<div class="winui-icon ' + isFaIcon + '">';
        			}
        			html += icon;
        			html += '</div>';
        			html += '<p>' + item.name + '</p>';
        			html += '</div>';
        		}
        	} else {//有子菜单
        		html += '<div class="winui-desktop-item win-menu-group" id="' + item.id + '" ' + id + ' ' + url + ' ' + title + ' ' + opentype + ' ' + maxOpen + ' ' + menuIconBg + ' ' + menuIconColor + ' ' + menuSysWinUrl + '>';
        		html += '<div class="winui-icon ' + isFaIcon + '">';
        		html += '<div class="icon-drawer">';
        		var childsIconContent = '';
        		var childsHtml = '';
        		$(item.childs).each(function (index, bean) {
        			if(languageType == 'cn'){
						bean.name = bean.menuNameEn;
					}
        			var childsid = isNull(bean.id) ? '' : 'win-id="' + bean.id + '"',
        				childsurl = isNull(bean.pageURL) ? '' : 'win-url="' + bean.pageURL + '"',
        				childstitle = isNull(bean.name) ? '' : 'win-title="' + bean.name + '"',
        				childsopentype = isNull(bean.openType) ? '' : 'win-opentype="' + bean.openType + '"',
        				childsmaxOpen = isNull(bean.maxOpen) ? '' : 'win-maxOpen="' + bean.maxOpen + '"',
        				childsmenuIconBg = isNull(bean.menuIconBg) ? '' : 'win-menuIconBg="' + bean.menuIconBg + '"',
        				childsmenuIconColor = isNull(bean.menuIconColor) ? '' : 'win-menuIconColor="' + bean.menuIconColor + '"',
						childsmenuSysWinUrl = isNull(bean.sysWinUrl) ? '' : 'win-sysWinUrl="' + bean.sysWinUrl + '"',
        				childsmenuIcon = '';
        			//如果子菜单的所属桌面和父菜单的一样或者该菜单所属桌面不存在
                    if(bean.deskTopId === item.deskTopId || $('.winui-desktop').find("article[id='" + bean.deskTopId + "']").length == 0 || bean.deskTopId === 'winfixedpage00000000'){
        				var childsicon;
        				var childsiconsmall;
        				var childsisFaIcon = "";
        				if(bean.menuIconType === '1'){//icon
        					childsmenuIcon = isNull(bean.icon) ? '' : 'win-icon="' + bean.icon + '"';
        					if (!isNull(bean.menuIconColor)){
        						if (!isNull(bean.menuIconBg)){
        							childsiconsmall = '<i class="fa ' + bean.icon + ' fa-fw icon-drawer-icon" style="color: ' + bean.menuIconColor + ';background-color: ' + bean.menuIconBg + '" win-i-id="' + bean.id + '"></i>';
        							childsicon = '<i class="fa ' + bean.icon + ' fa-fw" style="color: ' + bean.menuIconColor + ';background-color: ' + bean.menuIconBg + '" win-i-id="' + bean.id + '"></i>';
        						} else {
        							childsiconsmall = '<i class="fa ' + bean.icon + ' fa-fw icon-drawer-icon" style="color: ' + bean.menuIconColor + '" win-i-id="' + bean.id + '"></i>';
        							childsicon = '<i class="fa ' + bean.icon + ' fa-fw" style="color: ' + bean.menuIconColor + '" win-i-id="' + bean.id + '"></i>';
        						}
        					} else {
        						if (!isNull(bean.menuIconBg)){
        							childsiconsmall = '<i class="fa ' + bean.icon + ' fa-fw icon-drawer-icon" style="background-color: ' + bean.menuIconBg + '" win-i-id="' + bean.id + '"></i>';
        							childsicon = '<i class="fa ' + bean.icon + ' fa-fw" style="background-color: ' + bean.menuIconBg + '" win-i-id="' + bean.id + '"></i>';
        						} else {
        							childsiconsmall = '<i class="fa ' + bean.icon + ' fa-fw icon-drawer-icon" win-i-id="' + bean.id + '"></i>';
        							childsicon = '<i class="fa ' + bean.icon + ' fa-fw" win-i-id="' + bean.id + '"></i>';
        						}
        					}
        					childsisFaIcon = "winui-icon-font";
        				}else if(bean.menuIconType === '2'){//图片
        					childsmenuIcon = isNull(bean.menuIconPic) ? '' : 'win-icon="' + bean.menuIconPic + '"';
        					childsiconsmall = '<i class="fa icon-drawer-icon" win-i-id="' + bean.id + '"><img src="' + fileBasePath + bean.menuIconPic + '" class="desktop-img"/></i>';
        					childsicon = '<img src="' + fileBasePath + bean.menuIconPic + '" class="desktop-img"/>';
        					childsisFaIcon = "winui-icon-img";
        				}
        				
        				childsIconContent += childsiconsmall;
        				childsHtml += '<div class="winui-desktop-item sec-clsss-btn sec-btn" ' + childsid + ' ' + childsurl + ' ' + childstitle + ' ' + childsopentype + ' ' + childsmaxOpen + ' ' + childsmenuIconBg + ' ' + childsmenuIconColor + ' ' + childsmenuIcon + ' ' + childsmenuSysWinUrl + '>';
        				if (!isNull(bean.menuIconBg)){
        					childsHtml += '<div class="winui-icon ' + childsisFaIcon + '" style="background-color: ' + bean.menuIconBg + '">';
        				} else {
        					childsHtml += '<div class="winui-icon ' + childsisFaIcon + '">';
        				}
        				childsHtml += childsicon;
        				childsHtml += '</div>';
        				childsHtml += '<p>' + bean.name + '</p>';
        				childsHtml += '</div>';
        			} else {//如果子菜单的所属桌面和父菜单的不一样
        				var childDeskHtml = "";
        				var childsicon;
        				var childsisFaIcon = "";
        				if(bean.menuIconType === '1'){//icon
        					childsmenuIcon = isNull(bean.icon) ? '' : 'win-icon="' + bean.icon + '"';
        					if (!isNull(bean.menuIconColor)){
        						if (!isNull(bean.menuIconBg)){
        							childsicon = '<i class="fa ' + bean.icon + ' fa-fw" style="color: ' + bean.menuIconColor + ';background-color: ' + bean.menuIconBg + '" win-i-id="' + bean.id + '"></i>';
        						} else {
        							childsicon = '<i class="fa ' + bean.icon + ' fa-fw" style="color: ' + bean.menuIconColor + '" win-i-id="' + bean.id + '"></i>';
        						}
        					} else {
        						if (!isNull(bean.menuIconBg)){
        							childsicon = '<i class="fa ' + bean.icon + ' fa-fw" style="background-color: ' + bean.menuIconBg + '" win-i-id="' + bean.id + '"></i>';
        						} else {
        							childsicon = '<i class="fa ' + bean.icon + ' fa-fw" win-i-id="' + bean.id + '"></i>';
        						}
        					}
        					childsisFaIcon = "winui-icon-font";
        				}else if(bean.menuIconType === '2'){//图片
        					childsmenuIcon = isNull(bean.menuIconPic) ? '' : 'win-icon="' + bean.menuIconPic + '"';
        					childsicon = '<img src="' + fileBasePath + bean.menuIconPic + '" class="desktop-img"/>';
        					childsisFaIcon = "winui-icon-img";
        				}
        				childDeskHtml += '<div class="winui-desktop-item sec-btn" ' + childsid + ' ' + childsurl + ' ' + childstitle + ' ' + childsopentype + ' ' + childsmaxOpen + ' ' + childsmenuIconBg + ' ' + childsmenuIconColor + ' ' + childsmenuIcon + ' ' + childsmenuSysWinUrl + '>';
            			if (!isNull(bean.menuIconBg)){
            				childDeskHtml += '<div class="winui-icon ' + childsisFaIcon + '" style="background-color: ' + bean.menuIconBg + '">';
            			} else {
            				childDeskHtml += '<div class="winui-icon ' + childsisFaIcon + '">';
            			}
            			childDeskHtml += childsicon;
            			childDeskHtml += '</div>';
            			childDeskHtml += '<p>' + bean.name + '</p>';
            			childDeskHtml += '</div>';
            			if(isNull(bean.deskTopId)){
                    		$('.winui-desktop>.fixed-page').append(childDeskHtml);
                    	} else {
                    		if($('.winui-desktop').find("article[id='" + bean.deskTopId + "']").length > 0){
                    			$('.winui-desktop').find("article[id='" + bean.deskTopId + "']").append(childDeskHtml);
                    		} else {
                    			$('.winui-desktop>.fixed-page').append(childDeskHtml);
                    		}
                    	}
        			}
        		});
        		html += childsIconContent;
        		html += '</div>';
        		html += '<div class="icon-child">';
        		html += childsHtml;
        		html += '</div>';
        		html += '</div>';
        		html += '<p>' + item.name + '</p>';
        		html += '</div>';
        	}
        	if(isNull(item.deskTopId)){
        		$('.winui-desktop>.fixed-page').append(html);
        	} else {
        		if($('.winui-desktop').find("article[id='" + item.deskTopId + "']").length > 0){
        			$('.winui-desktop').find("article[id='" + item.deskTopId + "']").append(html);
        		} else {
        			$('.winui-desktop>.fixed-page').append(html);
        		}
        	}
        	html = '';
        });
        //定位应用
        common.locaApp();
        //调用渲染完毕的回调函数
        if (typeof callback === 'function')
            callback.call(this, desktopApp);
    };

    //设置数据
    Desktop.prototype.setData = function (callback) {
        var obj = this, currOptions = obj.options;
        if (!currOptions.url || !currOptions.method)
            return;
        $.ajax({
            url: currOptions.url,
            type: currOptions.method,
            data: $.extend({}, currOptions.data),
			headers: getRequestHeaders(),
            dataType: 'json',
            async: false,
            success: function (res) {
                res = res.rows;
                if (typeof res === "string") {
                    obj.data = JSON.parse(res);
                    if (typeof callback === 'function')
                        callback.call(obj);
                } else if (typeof (res) == "object" && (Object.prototype.toString.call(res).toLowerCase() == "[object object]" || Object.prototype.toString.call(res).toLowerCase() == "[object array]")) {
                    obj.data = res;
                    if (typeof callback === 'function')
                        callback.call(obj);
                } else {
                    layer.msg('请对接口返回json对象或者json字符串', {
                        offset: '40px',
                        zIndex: layer.zIndex
                    });
                }
            },
            error: function (e) {
                if (e.status != 200) {
                    console.error(e.statusText);
                } else {
                    layer.msg('请对接口返回json对象或者json字符串', {
                        offset: '40px',
                        zIndex: layer.zIndex
                    });
                }
            }
        });
    };

    //桌面应用构造函数
    var DesktopApp = function () {
        this.contextmenuOptions = {};
    };

    //桌面应用右键菜单定义
    DesktopApp.prototype.contextmenu = function (options) {
        if (!options.item)
            return;

        //重置右键事件
        common.resetEvent('.winui-desktop>.desktop-item-page>.winui-desktop-item', 'mouseup', function (e) {
            if (!e) e = window.event;
            var currentItem = this;
            if (e.button == 2) {
                var left = e.clientX;
                var top = e.clientY;

                var div = '<ul class="app-contextmenu" style="top:' + top + 'px;left:' + left + 'px;">';
                $(options.item).each(function (index, item) {
                	if (!isNull(item.icon)){
                		div += '<li><i class="right-menu-icon ' + item.icon + '"></i>' + item.text + '</li>';
                	} else {
                		div += '<li>' + item.text + '</li>';
                	}
                });
                div += '</ul>';

                //移除之前右键菜单
                $('.app-contextmenu').remove();
                //渲染当前右键菜单
                $('body').append(div);
                //绑定单击回调函数
                $('ul.app-contextmenu li').on('click', function () {
                    var index = $(this).index();
                    if (typeof options['item' + (index + 1)] !== 'function')
                        return;
                    //调用回调函数
                    options['item' + (index + 1)].call(this, $(currentItem).attr('win-id'), $(currentItem), { reLocaApp: common.locaApp });

                    $('.app-contextmenu').remove();
                    //移除选中状态
                    $('.winui-desktop>.desktop-item-page>.winui-desktop-item').removeClass('winui-this');
                });
                //阻止右键菜单冒泡
                $('.app-contextmenu li').on('click mousedown', call.sp);
            }
            $(currentItem).addClass('winui-this').siblings().removeClass('winui-this');
        });

        this.contextmenuOptions = options;
    }

    //桌面应用单击事件
    DesktopApp.prototype.onclick = function (callback) {
        if (typeof callback !== "function") return;
        //重置单击事件
        common.resetEvent('.winui-desktop-item', 'click', function (event) {
            event.stopPropagation();
            callback.call(this, $(this).attr('win-id'), this);
            //移除选中状态
            $('.winui-desktop>.desktop-item-page>.winui-desktop-item').removeClass('winui-this');
        });
    }

    //桌面应用双击事件
    DesktopApp.prototype.ondblclick = function (callback) {
        if (typeof callback !== "function") return;
        //重置双击事件
        common.resetEvent('.winui-desktop-item', 'dblclick', function (event) {
            event.stopPropagation();
            callback.call(this, $(this).attr('win-id'), this);
            //移除选中状态
            $('.winui-desktop>.desktop-item-page>.winui-desktop-item').removeClass('winui-this');
        });
    }

    var desktopApp = new DesktopApp();

    //公共事件
    var common = {
        //重置元素事件
        resetEvent: function (selector, eventName, func) {
            if (typeof func != "function") return;
            $(selector).off(eventName).on(eventName, func);
        },
        //定位桌面应用
        locaApp: function () {
        	var article = $("#winui-desktop").find('article');
        	$.each(article, function(i, item){
        		$(item).find(".winui-desktop-item")
        		//计算一竖排能容纳几个应用
        		var appHeight = 103;
        		var appWidth = 90;
        		var maxCount = parseInt($('.winui-desktop').height() / 100);
        		var oldTemp = 0;
        		var rowspan = 0;
        		var colspan = 0;
        		//定位桌面应用
        		$(item).children(".winui-desktop-item").each(function (index, elem) {
        			var newTemp = parseInt(index / maxCount);
        			colspan = parseInt(index / maxCount);
        			rowspan = oldTemp == newTemp ? rowspan : 0;
        			if (rowspan == 0 && oldTemp != newTemp) oldTemp++;
        			$(this).css('top', appHeight * rowspan + 'px').css('left', appWidth * colspan + 'px');
        			rowspan++;
        		});
        	});
        }
    };

    //基础事件
    var call = {
        //阻止事件冒泡
        sp: function (event) {
            layui.stope(event);
        }
    };

    var desktop = new Desktop();

    //配置
    desktop.config = function (options) {
        options = options || {};
        for (var key in options) {
            this.options[key] = options[key];
        }
        return this;
    };

    //初始化
    desktop.init = function (options, callback) {
        if (typeof options === 'object') {
            this.config(options);
        } else if (typeof options == 'fuction') {
            callback = options;
        }
        //缓存回调函数
        this.done = callback = callback || this.done;

        this.setData(function () {
            this.render(callback);
        });
    };
    
    //重置二级菜单右键
    desktop.initRightMenu = function(options){
    	if (!options.item)
            return;

        //重置右键事件
        common.resetEvent('.winui-desktop-item', 'mouseup', function (e) {
            if (!e) e = window.event;
            var currentItem = this;
            if (e.button == 2) {
                var left = e.clientX;
                var top = e.clientY;
                var div = '<ul class="app-contextmenu" style="top:' + top + 'px;left:' + left + 'px;">';
                $(options.item).each(function (index, item) {
                	if(item.text == '--'){
                		div += '<li class="win-left-right-menu"></li>';
                	} else {
                		if (!isNull(item.icon)){
                			div += '<li><i class="right-menu-icon ' + item.icon + '"></i>' + item.text + '</li>';
                		} else {
                			div += '<li>' + item.text + '</li>';
                		}
                	}
                });
                div += '</ul>';
                //移除之前右键菜单
                $('.app-contextmenu').remove();
                //渲染当前右键菜单
                $('body').append(div);
                //绑定单击回调函数
                $('ul.app-contextmenu li').on('click', function () {
                    var index = $(this).index();
                    if (typeof options.item[index].callBack !== 'function')
                        return;
                    //调用回调函数
                    options.item[index].callBack.call(this, $(currentItem).attr('win-id'), $(currentItem), { reLocaApp: common.locaApp });
                    $('.app-contextmenu').remove();
                    //移除选中状态
                    $('.winui-desktop>.desktop-item-page>.winui-desktop-item').removeClass('winui-this');
                });
                //阻止右键菜单冒泡
                $('.app-contextmenu li').on('click mousedown', call.sp);
            }
            $(currentItem).addClass('winui-this').siblings().removeClass('winui-this');
        });

        this.contextmenuOptions = options;
    }

    winui.desktop = desktop;

    exports('desktop', {});

    delete layui.desktop;
});