layui.define(["jquery"], function(exports) {
	var jQuery = layui.jquery;
	(function($) {
		! function(t, n, e, i) {
			var o = function(t, n) {
				this.init(t, n)
			};
			o.prototype = {
				init: function(t, n) {
					this.ele = t, this.defaults = {
						menu: [],
						target: function(t) {},
						width: 100,
						itemHeight: 28,
						bgColor: "#fff",
						color: "#333",
						fontSize: 14,
						hoverBgColor: "#f5f5f5",
						rightClass: ""
					}, this.opts = e.extend(!0, {}, this.defaults, n), this.random = (new Date).getTime() + parseInt(1e3 * Math.random()), this.eventBind()
				},
				renderMenu: function() {
					var t = this,
						n = "#uiContextMenu_" + this.random;
					if(!(e(n).length > 0)) {
						var t = this,
							i = '<ul class="ul-context-menu" id="uiContextMenu_' + this.random + '">';
						e.each(this.opts.menu, function(t, n) {
							if(n.text != '--'){
								var iconStr = '';
								if(isNull(n.icon))
									iconStr = '<i class="' + 'fa' + '"></i>';
								else
									iconStr = '<i class="' + n.icon + '"></i>';
								if(!isNull(n.img))
									iconStr = '<img src="' + n.img + '"/>';
								i += '<li class="';
								if(!isNull(n.children)){
									i += 'ui-context-mouse-menu-item';
								} else {
									i += 'ui-context-menu-item';
								}
								var id = _getRandomString(32);
								n.id = id;
								i += '" id="' + id + '"><a href="javascript:void(0);">' + iconStr + '<span>' + n.text + '</span>';
								if(!isNull(n.children)){
									i += '<i class="fa fa-caret-right menu-right"></i>';
								}
								i += '</a>';
								if(!isNull(n.children)){
									i += '<ul class="child-context-menu" style="left: ' + n.width + '">';
									e.each(n.children, function(index, item) {
										if(item.text != '--'){
											var id = _getRandomString(32);
											var iconChildStr = '';
											if(isNull(item.icon))
												iconChildStr = '<i class="' + 'fa' + '"></i>';
											else
												iconChildStr = '<i class="' + item.icon + '"></i>';
											if(!isNull(item.img))
												iconChildStr = '<img src="' + item.img + '"/>';
											item.id = id;
											i += '<li class="ui-context-menu-item';
											i += '" id="' + id + '"><a href="javascript:void(0);">' + iconChildStr + '<span>' + item.text + '</span>';
											i += '</a></li>';
										} else {
											i += '<li class="context-menu-line"></li>'
										}
									});
									i += '</ul>';
								}
								i += '</li>';
							} else {
								i += '<li class="context-menu-line"></li>'
							}
						}), 
						i += "</ul>", 
						e("body").append(i).find(".ul-context-menu").hide(), 
						this.initStyle(n);
						//添加点击事件
						var menuClick = e(n).find(".ui-context-menu-item");
						var menu = this.opts.menu;
						e.each(menuClick, function(index, item) {
							//遍历menu json串
							e.each(menu, function(menuIndex, m) {
								if(!isNull(m.children)){
									//遍历子菜单
									e.each(m.children, function(childIndex, child) {
										if(child.id === $(item).attr("id")){
											$(item).on('click', function(e){
												child.callback();
											});
											return true;
										}
									});
								} else {
									if(m.id === $(item).attr("id")){
										$(item).on('click', function(e){
											m.callback();
										});
										return true;
									}
								}
							});
						});
						e(n).on("mouseover", ".ui-context-mouse-menu-item", function(n) {
							$(".child-context-menu").hide();
							var child = $(this).find('.child-context-menu');
							child.css({"left": $(this).css("width")});
							child.show();
						}).on("mouseout", ".ui-context-mouse-menu-item", function(n) {
							$(this).find(".child-context-menu").hide();
						});
					}
				},
				initStyle: function(t) {
					var n = this.opts;
					e(t).css({
						width: n.width,
						backgroundColor: n.bgColor
					}).find(".ui-context-menu-item a").css({
						color: n.color,
						fontSize: n.fontSize,
						height: n.itemHeight,
						lineHeight: n.itemHeight + "px"
					}).hover(function() {
						e(this).css({
							backgroundColor: n.hoverBgColor
						})
					}, function() {
						e(this).css({
							backgroundColor: n.bgColor
						})
					});
					
					e(t).css({
						width: n.width,
						backgroundColor: n.bgColor
					}).find(".ui-context-mouse-menu-item a").css({
						color: n.color,
						fontSize: n.fontSize,
						height: n.itemHeight,
						lineHeight: n.itemHeight + "px"
					}).hover(function() {
						e(this).css({
							backgroundColor: n.hoverBgColor
						})
					}, function() {
						e(this).css({
							backgroundColor: n.bgColor
						})
					})
				},
				menuItemClick: function(t) {
					var n = this,
						e = t.index();
					t.parent(".ul-context-menu").hide(), 
					n.opts.menu[e].callback && "function" == typeof n.opts.menu[e].callback && n.opts.menu[e].callback()
				},
				setPosition: function(t) {
					var n = this.opts;
					if($(t.target).attr('class') === 'right-center-is-content'
						|| (judgeStrInStrs(n.rightClass, $(t.target).attr('class')) != -1 && !isNull(n.rightClass))){
						$(".child-context-menu").hide();
						var winHeight = $(window).height();
						var winWidth = $(window).width();
						var tHeight = t.clientY;
						var tWidth = t.clientX;
						var thisHeight = e("#uiContextMenu_" + this.random).height();
						var thisWidth = e("#uiContextMenu_" + this.random).width();
						var left = "";
						var top = "";
						if(winHeight - tHeight < thisHeight){
							top = t.clientY - thisHeight;
						} else {
							top = t.clientY + 2;
						}
						if(winWidth - tWidth < thisWidth){
							left = t.clientX - thisWidth;
						} else {
							left = t.clientX + 2;
						}
						e("#uiContextMenu_" + this.random).css({
							left: left,
							top: top
						}).show();
					}
				},
				eventBind: function() {
					var t = this;
					this.ele.on("contextmenu", function(n) {
						n.preventDefault(), t.renderMenu(), t.setPosition(n), t.opts.target && "function" == typeof t.opts.target && t.opts.target(e(this))
					}), e(n).on("click", function() {
						e(".ul-context-menu").hide()
					})
				}
			}, e.fn.contextMenu = function(t) {
				return new o(this, t), this
			}
		}(window, document, jQuery);
	})(jQuery);
	exports('contextMenu', null);
});