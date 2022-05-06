/**
 * 开始菜单模块
 */
layui.define(['jquery', 'element', 'layer', 'winui'], function (exports) {
    "use strict";

    var $ = layui.jquery,
        element = layui.element;;

    //开始菜单构造函数
    var Menu = function (options) {
        this.options = options || {
            url: winui.path + 'json/allmenu.json',
            method: 'get'
        };
        this.data = null;
    };

    //渲染HTML
    Menu.prototype.render = function (callback) {
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
                maxopen = isNull(item.maxOpen) ? '' : 'win-maxopen="' + item.maxOpen + '"',
                winIcon = isNull(item.icon) ? '' : 'win-icon="' + item.icon + '"',
                isParent = item.childs ? ' parent' : '',
        		menuIconBg = isNull(item.menuIconBg) ? '' : 'win-menuIconBg="' + item.menuIconBg + '"',
				menuIconColor = isNull(item.menuIconColor) ? '' : 'win-menuIconColor="' + item.menuIconColor + '"',
                menuSysWinUrl = isNull(item.sysWinUrl) ? '' : 'win-sysWinUrl="' + item.sysWinUrl + '"',
				menuDeskTopId = 'win-menuDeskTopId="' + item.deskTopId + '"';
            var icon = "";
        	if(item.menuIconType === '1' || item.menuIconType == 1){//icon
        		if(!isNull(item.menuIconColor)){
        			icon = '<i class="fa ' + item.icon + ' fa-fw" style="color: ' + item.menuIconColor + '"></i>';
        		}else{
        			icon = '<i class="fa ' + item.icon + ' fa-fw"></i>';
        		}
        		winIcon = (item.icon == '' || item.icon == undefined) ? '' : 'win-icon="' + item.icon + '"';
        	}else if(item.menuIconType === '2' || item.menuIconType == 2){//图片
        		icon = '<img src="' + fileBasePath + item.menuIconPic + '" />';
        		winIcon = (item.menuIconPic == '' || item.menuIconPic == undefined) ? '' : 'win-icon="' + item.menuIconPic + '"';
        	}
            if(index == 0){
            	var extend = item.extend ? ' layui-nav-itemed' : '';
            }else{
            	var extend = '';
            }
            html += '<li class="layui-nav-item ' + isParent + ' ' + extend + '" ' + id + ' ' + url + ' ' + title + ' ' + opentype + ' ' + maxopen + ' ' + winIcon + ' ' + menuIconBg + ' ' + menuIconColor + ' ' + menuSysWinUrl + ' ' + menuDeskTopId + '>';
            if(!isNull(item.menuIconBg)){
            	html += '<a><div class="winui-menu-icon" style="background-color: ' + item.menuIconBg + '!important;">';
            }else{
            	html += '<a><div class="winui-menu-icon">';
            }
            html += icon;
            html += '</div>';
            html += '<span class="winui-menu-name">' + item.name + '</span></a>';
            if (item.childs) {
                html += '<dl class="layui-nav-child">';
                $(item.childs).each(function (cIndex, cItem) {
                	if(languageType == 'cn'){
						cItem.name = cItem.menuNameEn;
					}
                    var cId = (cItem.id == '' || cItem.id == undefined) ? '' : 'win-id="' + cItem.id + '"',
                        cUrl = (cItem.pageURL == '' || cItem.pageURL == undefined) ? '' : 'win-url="' + cItem.pageURL + '"',
                        cTitle = isNull(cItem.name) ? '' : 'win-title="' + cItem.name + '"',
                        cOpentype = (cItem.openType == '' || cItem.openType == undefined) ? '' : 'win-opentype="' + cItem.openType + '"',
                        cMaxopen = (cItem.maxOpen == '' || cItem.maxOpen == undefined) ? '' : 'win-maxopen="' + cItem.maxOpen + '"',
                        cWinIcon = (cItem.icon == '' || cItem.icon == undefined) ? '' : 'win-icon="' + cItem.icon + '"',
                		cmenuIconBg = (cItem.menuIconBg == '' || cItem.menuIconBg == undefined) ? '' : 'win-menuIconBg="' + cItem.menuIconBg + '"',
        				cmenuIconColor = (cItem.menuIconColor == '' || cItem.menuIconColor == undefined) ? '' : 'win-menuIconColor="' + cItem.menuIconColor + '"',
                        cmenuSysWinUrl = isNull(cItem.sysWinUrl) ? '' : 'win-sysWinUrl="' + cItem.sysWinUrl + '"',
						menuDeskTopId = 'win-menuDeskTopId="' + cItem.deskTopId + '"';;
                    var cicon = "";
                    if(cItem.menuIconType === '1' || cItem.menuIconType == 1){//icon
                    	cWinIcon = (cItem.icon == '' || cItem.icon == undefined) ? '' : 'win-icon="' + cItem.icon + '"';
                    	if(!isNull(cItem.menuIconColor)){
                        	cicon = '<i class="fa ' + cItem.icon + ' fa-fw" style="color: ' + cItem.menuIconColor + '"></i>';
                        }else{
                        	cicon = '<i class="fa ' + cItem.icon + ' fa-fw"></i>';
                        }
                	}else if(cItem.menuIconType === '2' || cItem.menuIconType == 2){//图片
                		cicon = '<img src="' + fileBasePath + cItem.menuIconPic + '" />';
                		cWinIcon = (cItem.menuIconPic == '' || cItem.menuIconPic == undefined) ? '' : 'win-icon="' + cItem.menuIconPic + '"'
                	}
                    html += '<dd ' + cId + ' ' + cUrl + ' ' + cTitle + ' ' + cOpentype + ' ' + cMaxopen + ' ' + cWinIcon + ' ' + cmenuIconBg + ' ' + cmenuIconColor + ' ' + cmenuSysWinUrl + ' ' + menuDeskTopId + '>';
                    if(!isNull(cItem.menuIconBg)){
                    	html += '<a><div class="winui-menu-icon" style="background-color: ' + cItem.menuIconBg + '!important;">';
                    }else{
                    	html += '<a><div class="winui-menu-icon">';
                    }
                    html += cicon;
                    html += '</div>';
                    html += '<span class="winui-menu-name">' + cItem.name + '</span></a>';
                });
                html += '</dl>';
            }
            html += '</li>';
        });
        $('.winui-menu').html(html);

        //初始化layui的element（可以从新监听点击事件）
        layui.element.init('nav', 'winuimenu');

        //调用渲染完毕的回调函数
        if (typeof callback === 'function')
            callback.call(this, menuItem);
    }

    //设置数据
    Menu.prototype.setData = function (callback) {
        var obj = this,
            currOptions = obj.options;

        if (!currOptions.url || !currOptions.method)
            return;
        $.ajax({
            url: currOptions.url,
            type: currOptions.method,
            headers: getRequestHeaders(),
            data: $.extend({}, currOptions.data),
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

    //开始菜单项构造函数
    var MenuItem = function () {
        this.contextmenuOptions = {};
    };

    //菜单项单击事件
    MenuItem.prototype.onclick = function (callback) {
        element.on('nav(winuimenu)', callback);
    };

    //菜单项右键菜单定义
    MenuItem.prototype.contextmenu = function (options) {
        if (!options.item)
            return;

        //重置右键事件
        common.resetEvent('.winui-menu li:not(.parent),.winui-menu dd', 'mouseup', function (e) {
            if (!e) e = window.event;
            var currentItem = this;
            if (e.button == 2) {
                var left = e.clientX;
                var top = e.clientY;
                //右键点击
                var div = '<ul class="menu-contextmenu" style="top:' + top + 'px;left:' + left + 'px;">';
                $(options.item).each(function (index, item) {
                	if(item.text == '--'){
                		div += '<li class="win-left-right-menu"></li>';
                	}else{
                		var icon = item.icon ? '<i class="fa ' + item.icon + ' fa-fw"></i>' : '';
                		div += '<li>' + icon + item.text + '</li>';
                	}
                });
                div += '</ul>';
                //移除之前任务项右键菜单
                $('.menu-contextmenu').remove();
                //渲染当前任务项右键菜单
                $('body').append(div);
                //绑定单击回调函数
                $('ul.menu-contextmenu li').on('click', function () {
                    var index = $(this).index();
                    if (typeof options.item[index].callBack !== 'function')
                        return;
                    //调用回调函数
                    options.item[index].callBack.call(this, $(currentItem).attr('win-id'), $(currentItem));
                    $('.menu-contextmenu').remove();
                });
                //阻止右键菜单冒泡
                $('.menu-contextmenu li').on('click mousedown', call.sp);
            }
        });

        this.contextmenuOptions = options;
    };


    var menuItem = new MenuItem();
    
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

    //公共事件
    var common = {
        //重置元素事件
        resetEvent: function (selector, eventName, func) {
            if (typeof func != "function") return;
            $(selector).off(eventName).on(eventName, func);
        },
        //定位桌面应用
        locaApp: function () {
        	//计算一竖排能容纳几个应用
        	var appHeight = 103;
            var appWidth = 90;
	        var maxCount = parseInt($('.winui-desktop').height() / 100);
	        var oldTemp = 0;
	        var rowspan = 0;
	        var colspan = 0;
	        //定位桌面应用
	        var __obj = getActiveArticle();
	        __obj.children(".winui-desktop-item").each(function (index, elem) {
	            var newTemp = parseInt(index / maxCount);
	            colspan = parseInt(index / maxCount);
	            rowspan = oldTemp == newTemp ? rowspan : 0;
	            if (rowspan == 0 && oldTemp != newTemp) oldTemp++;
	            $(this).css('top', appHeight * rowspan + 'px').css('left', appWidth * colspan + 'px');
	            rowspan++;
	        });
	    },
	    //给桌面按钮添加单击事件
        reloadOnClick: function(callback){
        	if (typeof callback !== "function") return;
            //重置单击事件
            common.resetEvent('.winui-desktop-item', 'click', function (event) {
                event.stopPropagation();
                callback.call(this, $(this).attr('win-id'), this);
                //移除选中状态
                $('.winui-desktop>.desktop-item-page>.winui-desktop-item').removeClass('winui-this');
            });
        },
        //图片url获取主题色
        imgUrlToThemeColor: function(url, cb, light) {
        	if(!light) {
        		light = 1.0
        	}
        	var img = new Image;
        	img.src = url;
        	img.crossOrigin = 'anonymous'; //跨域声明（只在chrome和firefox有效——吗？）
        	img.onload = function() {
        		try {
        			var canvas = document.createElement("canvas");
        			canvas.width = img.width;
        			canvas.height = img.height;
        			var ctxt = canvas.getContext('2d');
        			ctxt.drawImage(img, 0, 0);
        			var data = ctxt.getImageData(0, 0, img.width, img.height).data; //读取整张图片的像素。
        			var r = 0,
        				g = 0,
        				b = 0,
        				a = 0;
        			var red, green, blue, alpha;
        			var pixel = img.width * img.height;
        			for(var i = 0, len = data.length; i < len; i += 4) {
        				red = data[i];
        				r += red; //红色色深
        				green = data[i + 1];
        				g += green; //绿色色深
        				blue = data[i + 2];
        				b += blue; //蓝色色深
        				alpha = data[i + 3];
        				a += alpha; //透明度
        			}
        			r = parseInt(r / pixel * light);
        			g = parseInt(g / pixel * light);
        			b = parseInt(b / pixel * light);
        			a = 1; //a/pixel/255;
        			var color = "rgba(" + r + "," + g + "," + b + "," + a + ")";
        			if(cb) {
        				cb(color);
        			}
        		} catch(e) {
        			console.warn(e)
        		}
        	};
        },
        //获取图片尺寸
        imgUrlToSize: function(url, cb) {
        	var img = new Image;
        	img.src = url;
        	img.onload = function() {
        		if(cb) {
        			cb({
        				width: img.width,
        				height: img.height,
        			});
        		}
        	};
        },
    };

    //基础事件
    var call = {
        //阻止事件冒泡
        sp: function (event) {
            layui.stope(event);
        }
    };

    var menu = new Menu();

    //配置
    menu.config = function (options) {
        options = options || {};
        for (var key in options) {
            this.options[key] = options[key];
        }
        return this;
    };

    //初始化
    menu.init = function (options, callback) {
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
    }

    winui.menu = menu;
    //公共方法
    winui.util = common;

    exports('start', {});

    delete layui.start;
});