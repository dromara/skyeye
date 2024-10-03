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
        	var id = 'win-id="' + item.id + '"',
                pageType = 'win-pageType="' + item.pageType + '"',
                url = 'win-url="' + item.pageURL + '"',
        		title = 'win-title="' + item.name + '"',
        		opentype = 'win-opentype="' + item.openType + '"',
        		maxOpen = 'win-maxOpen="' + item.maxOpen + '"',
				iconBg = isNull(item.iconBg) ? '' : 'win-menuIconBg="' + item.iconBg + '"',
				iconColor = isNull(item.iconColor) ? '' : 'win-menuIconColor="' + item.iconColor + '"',
				sysWinUrl = isNull(item.sysWinUrl) ? '' : 'win-sysWinUrl="' + item.sysWinUrl + '"';
            if (item.id == 'c876a6c27094454d914bee1ce57333db'){
                console.log(item)
            }

        	var iconParams = desktopMenuUtil.getDecktopMenuIcon(item);
        	if (isNull(item.childs) || item.childs.length == 0) {//没有子菜单
        		if(item.pageURL == '--' && item.menuLevel == '0'){
        			html += `<div class="winui-desktop-item win-menu-group" id="${item.id}" ${id} ${pageType} ${url} ${title} ${opentype} ${maxOpen} ${iconBg} ${iconColor}>
								<div class="winui-icon ${iconParams.isFaIcon}">
									<div class="icon-drawer"></div>
									<div class="icon-child"></div>
								</div>
								<p>${item.name}</p>
							</div>`;
        		} else {
        			html += `<div class="winui-desktop-item sec-btn" ${id} ${pageType} ${url} ${title} ${opentype} ${maxOpen} ${iconBg} ${iconColor} ${iconParams.menuIcon} ${sysWinUrl}>`;
        			if (!isNull(item.iconBg)){
        				html += '<div class="winui-icon ' + iconParams.isFaIcon + '" style="background-color: ' + item.iconBg + '">';
        			} else {
        				html += '<div class="winui-icon ' + iconParams.isFaIcon + '">';
        			}
        			html += iconParams.icon;
        			html += '</div>';
        			html += '<p>' + item.name + '</p>';
        			html += '</div>';
        		}
        	} else {//有子菜单
        		html += `<div class="winui-desktop-item win-menu-group" id="${item.id}" ${id} ${pageType} ${url} ${title} ${opentype} ${maxOpen} ${iconBg} ${iconColor} ${sysWinUrl}>`;
        		html += '<div class="winui-icon ' + iconParams.isFaIcon + '">';
        		html += '<div class="icon-drawer">';
        		var childsIconContent = '';
        		var childsHtml = '';
        		$(item.childs).each(function (index, bean) {
        			var cId = 'win-id="' + bean.id + '"',
                        cPageType = 'win-pageType="' + bean.pageType + '"',
                        cPageUrl = 'win-url="' + bean.pageURL + '"',
        				cTitle = 'win-title="' + bean.name + '"',
        				cOpenType = 'win-opentype="' + bean.openType + '"',
        				cMaxOpen = 'win-maxOpen="' + bean.maxOpen + '"',
        				cIconBg = isNull(bean.iconBg) ? '' : 'win-menuIconBg="' + bean.iconBg + '"',
        				cIconColor = isNull(bean.iconColor) ? '' : 'win-menuIconColor="' + bean.iconColor + '"',
						cSysWinUrl = isNull(bean.sysWinUrl) ? '' : 'win-sysWinUrl="' + bean.sysWinUrl + '"';

					var childIconParams = desktopMenuUtil.getDecktopMenuIcon(bean);
        			// 如果子菜单的所属桌面和父菜单的一样
                    if(bean.deskTopId === item.deskTopId){
        				childsIconContent += childIconParams.smallIcon;
        				childsHtml += `<div class="winui-desktop-item sec-clsss-btn sec-btn" ${cId} ${cPageType} ${cPageUrl} ${cTitle} ${cOpenType} ${cMaxOpen} ${cIconBg} ${cIconColor} + ${childIconParams.menuIcon} ${cSysWinUrl}>`;
        				if (!isNull(bean.iconBg)){
        					childsHtml += '<div class="winui-icon ' + childIconParams.isFaIcon + '" style="background-color: ' + bean.iconBg + '">';
        				} else {
        					childsHtml += '<div class="winui-icon ' + childIconParams.isFaIcon + '">';
        				}
        				childsHtml += childIconParams.icon;
        				childsHtml += '</div>';
        				childsHtml += '<p>' + bean.name + '</p>';
        				childsHtml += '</div>';
        			} else {//如果子菜单的所属桌面和父菜单的不一样
        				var childDeskHtml = "";
        				childDeskHtml += `<div class="winui-desktop-item sec-btn" ${cId} ${cPageType} ${cPageUrl} ${cTitle} ${cOpenType} ${cMaxOpen} ${cIconBg} ${cIconColor} ${childIconParams.childsmenuIcon} ${cSysWinUrl}>`;
            			if (!isNull(bean.iconBg)){
            				childDeskHtml += '<div class="winui-icon ' + childIconParams.isFaIcon + '" style="background-color: ' + bean.iconBg + '">';
            			} else {
            				childDeskHtml += '<div class="winui-icon ' + childIconParams.isFaIcon + '">';
            			}
            			childDeskHtml += childIconParams.icon;
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
        	$.each(article, function(i, item) {
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