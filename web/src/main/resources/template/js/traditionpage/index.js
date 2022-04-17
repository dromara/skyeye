
var active;
var userId;

layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'radialin', 'form', 'element', 'tautocomplete', 'spop'], function (exports) {
    var $ = layui.jquery,
    	form = layui.form,
    	element = layui.element;
    
    $(document).attr("title", sysMainMation.mationTitle);
    $(".tradition-left-top").find("span").html(sysMainMation.mationTitle);
    // 修改版权
	$(".tradition-right-bottom-copyright").html(sysMainMation.copyright);
    
    //顶部桌面模板
	var desktopTemplate = $("#desktopTemplate").html();
	//菜单盒子模板
	var menuBoxTemplate = $("#menuBoxTemplate").html();
	//二级菜单模板
	var menuMoreTemplate = $("#menuMoreTemplate").html();
	//一级菜单模板
	var menuTemplate = $("#menuTemplate").html();

	// 文件管理、日程、笔记、论坛权限
	authBtn('1645958796795');
	authBtn('1645959056141');
	authBtn('1645959177299');
	authBtn('1645959237037');
	
    $(function () {
		// 获取当前登录员工信息
		systemCommonUtil.getSysCurrentLoginUserMation(function (data){
			userId = data.bean.id;
			$("#userPhoto").attr("src", fileBasePath + data.bean.userPhoto);
			$("#userName").html(data.bean.userCode + '(' + data.bean.userName + ')');
		});
		// 加载首页
		initDefaultPage();
		// 加载菜单数据
		loadMenuListToShow();

		etiger.socket.init();
    });
    
    //搜索自动补充数据来源
    var data = new Array();
    
    //加载菜单数据
    function loadMenuListToShow(){
    	//获取桌面消息
        AjaxPostUtil.request({url:reqBasePath + "login009", params: {language: languageType}, type: 'json', method: "GET", callback: function(l){
            if(l.returnCode == 0) {
            	var str = "";//顶部桌面字符串
            	var menuBoxStr = "";//多个菜单的字符串
            	var jsonStr = {};
            	var defaultName = (languageType == "zh" ? "默认桌面" : "Default desktop");
            	jsonStr = {
        			bean: {
        				id: 'winfixedpage00000000',
        				name: defaultName,
        				show: 'block',
        				chooseDeskTop: ' select'
        			}
        		};
            	str += getDataUseHandlebars(desktopTemplate, jsonStr);
            	menuBoxStr += getDataUseHandlebars(menuBoxTemplate, jsonStr);
            	$.each(l.rows, function(i, row){
            		row.show = 'none';
            		jsonStr = {
            			bean: row
            		};
            		str += getDataUseHandlebars(desktopTemplate, jsonStr);
            		menuBoxStr += getDataUseHandlebars(menuBoxTemplate, jsonStr);
            	});
            	$(".desktop-menu-box").find("ul").html(str);
            	$("#sysMenuListBox").html(menuBoxStr);
            	
            	//重新计算头部宽度
				initDeskTopMenuBox();
				
				// 加载菜单
				AjaxPostUtil.request({url:reqBasePath + "login005", params:{}, type: 'json', method: "GET", callback: function(json){
		   			if(json.returnCode == 0){
		   				var menuStr;
		   				$.each(json.rows, function(i, row){
		   					menuStr = "";
		   					if(row.menuIconType === 1){//icon
		   						row.icon = '<i class="fa ' + row.icon + ' fa-fw"></i>';
		   					}else if(row.menuIconType === 2){//图片
		   						row.icon = '<img src="' + fileBasePath + row.menuIconPic + '" />';
		   					}
	   						if(languageType == 'cn'){
	   							row.name = row.menuNameEn;
	   						}
		   					if(row.pageURL != '--'){
		   						//一级菜单
		   						menuStr = getDataUseHandlebars(menuTemplate, {bean: row});
		   						if(isNull(row.deskTopId)){
		   							$("ul[menurowid='winfixedpage00000000']").append(menuStr);
		   						}else{
		   							$("ul[menurowid='" + row.deskTopId + "']").append(menuStr);
		   						}
		   						data.push({id: row.id, name: row.name, pageURL: row.pageURL, winName: isNull(row.deskTopId) ? defaultName : $(".desktop-menu-box").find("li[rowid='" + row.deskTopId + "']").find('span').html()});
		   					}else{
		   						//二级菜单
		   						if(!isNull(row.childs)){
			   						$.each(row.childs, function(j, child){
			   							if(child.menuIconType === 1){//icon
			   								child.icon = '<i class="fa ' + child.icon + ' fa-fw"></i>';
					   					}else if(child.menuIconType === 2){//图片
					   						child.icon = '<img src="' + fileBasePath + child.menuIconPic + '" />';
					   					}
					   					if(languageType == 'cn'){
				   							child.name = child.menuNameEn;
				   						}
					   					data.push({id: child.id, name: child.name, pageURL: child.pageURL, winName: isNull(row.deskTopId) ? defaultName : $(".desktop-menu-box").find("li[rowid='" + row.deskTopId + "']").find('span').html()});
			   						});
		   						}
		   						menuStr = getDataUseHandlebars(menuMoreTemplate, {bean: row});
		   						if(isNull(row.deskTopId)){
		   							$("ul[menurowid='winfixedpage00000000']").append(menuStr);
		   						}else{
		   							$("ul[menurowid='" + row.deskTopId + "']").append(menuStr);
		   						}
		   					}
		   				});
		   				
		   				var text2 = $("#Text2").tautocomplete({
							width: "500px",
							placeholder: $("#Text2").attr("placeholder"),
							columns: [{field: 'name', title: '菜单名称'}, {field: 'winName', title: '所属桌面'}],
							data: function() {
								var filterData = [];
								var searchData = eval("/" + text2.searchdata() + "/gi");
								$.each(data, function(i, v) {
									if(v.name.search(new RegExp(searchData)) != -1) {
										filterData.push(v);
									}
								});
								return filterData;
							},
							onchange: function() {
								if(!isNull(text2.id())){
									var dataMenu = $("#sysMenuListBox").find("a[data-id='" + text2.id() + "']");
									indexMenu.loadTraditionPage(dataMenu);
								}
							}
						});
						
						matchingLanguage();
		    		} else {
		    			winui.window.msg(json.returnMessage, {shift: 6});
		    		}
		   		}});
            } else {
            	winui.window.msg(l.returnMessage, {shift: 6});
    		}
   		}});
    }
    
    //计算头部宽度
    function initDeskTopMenuBox(){
    	var items = $(".desktop-menu-box").find('li');
    	var maxWidth = 50;
    	$.each(items, function(i, item){
    		maxWidth += $(item).width();
    	});
    	$(".desktop-menu-box").find('ul').css({'width': maxWidth + 'px'});
    }
    
	//触发事件
    active = {
    	//在这里给active绑定几项事件，后面可通过active调用这些事件
    	tabAdd: function(url, id, name) {
    		url = systemCommonUtil.getHasVersionUrl(url);
    		//新增一个Tab项 传入三个参数，分别对应其标题，tab页面的地址，还有一个规定的id，是标签中data-id的属性值
    		//关于tabAdd的方法所传入的参数可看layui的开发文档中基础方法部分
    		element.tabAdd('menubox', {
    			title: name,
    			content: '<iframe data-frameid="' + id + '" scrolling="auto" frameborder="0" src="' + url + '"></iframe>',
    			id: id //规定好的id
    		})
    		CustomRightClick(id); //给tab绑定右击事件
    	},
    	tabChange: function(id) {
    		//切换到指定Tab项
    		element.tabChange('menubox', id); //根据传入的id传入到指定的tab项
    	},
    	tabDelete: function(id) {
    		element.tabDelete("menubox", id); //删除
    	},
    	tabDeleteAll: function(ids) { //删除所有
    		$.each(ids, function(i, item) {
    			element.tabDelete("menubox", item); //ids是一个数组，里面存放了多个id，调用tabDelete方法分别删除
    		})
    	}
    };

    // 当点击有page-item-click属性的标签时，即左侧菜单栏中内容 ，触发点击事件
    $("body").on("click", ".page-item-click", function(e){
    	var dataMenu = $(this);
    	if("win" === dataMenu.attr("data-type")){
    		window.open(dataMenu.attr("data-url"));
    	} else {
			indexMenu.loadTraditionPage(dataMenu);
    	}
    });

    //右键事件
    function CustomRightClick(id) {
    	//取消右键  rightmenu属性开始是隐藏的 ，当右击的时候显示，左击的时候隐藏
    	$('#LAY_app_tabsheader li').on('contextmenu', function() {
    		return false;
    	})
    	$('#LAY_app_tabsheader, #LAY_app_tabsheader li').click(function() {
    		$('.rightmenu').hide();
    	});
    	//桌面点击右击 
    	$('#LAY_app_tabsheader li').on('contextmenu', function(e) {
    		var popupmenu = $(".rightmenu");
    		popupmenu.find("li").attr("data-id", id); //在右键菜单中的标签绑定id属性
    		//判断右侧菜单的位置 
    		l = ($(document).width() - e.clientX) < popupmenu.width() ? (e.clientX - popupmenu.width()) : e.clientX;
    		t = ($(document).height() - e.clientY) < popupmenu.height() ? (e.clientY - popupmenu.height()) : e.clientY;
    		popupmenu.css({
    			left: l,
    			top: t
    		}).show(); //进行绝对定位
    		return false;
    	});
    }
    
    //加载初始化界面-首页
    function initDefaultPage(){
    	active.tabAdd("../../tpl/mainPage/mainPage.html", "initDefaultPageId", '<i class="layui-icon layui-icon-home"></i>');
    	active.tabChange("initDefaultPageId");
    }
    
    //菜单点击
    $("body").on("click", ".menu-box-none", function(e){
    	if($(this).parent().hasClass("layui-nav-itemed")){
    		$(this).parent().removeClass("layui-nav-itemed");
    	}else{
    		$(this).parent().addClass("layui-nav-itemed");
    	}
    });

    //关闭操作
    $("body").on("click", ".rightmenu li, .right-close-operator dd a", function(e){
    	//右键菜单中的选项被点击之后，判断type的类型，决定关闭所有还是关闭当前。
    	if($(this).attr("data-type") == "closethis") {
    		var choosePage = $("#LAY_app_tabsheader li[class='layui-this']").attr('lay-id');
    		if(isNull(choosePage)){
    			winui.window.msg('请选中当前标签页~', {shift: 6});
    			return;
    		}
    		//如果关闭当前，即根据显示右键菜单时所绑定的id，执行tabDelete
    		if(choosePage != 'initDefaultPageId'){
    			active.tabDelete(choosePage);
    		}else{
    			winui.window.msg('首页不能关闭~', {shift: 6});
    		}
    	} else if($(this).attr("data-type") == "closeall") {
    		var tabtitle = $("#LAY_app_tabsheader li");
    		var ids = new Array();
    		$.each(tabtitle, function(i) {
    			if($(this).attr("lay-id") != 'initDefaultPageId'){
    				ids.push($(this).attr("lay-id"));
    			}
    		})
    		//如果关闭所有 ，即将所有的lay-id放进数组，执行tabDeleteAll
    		active.tabDeleteAll(ids);
    	} else if($(this).attr("data-type") == "closeother") {//关闭其他标签页
    		var choosePage = $("#LAY_app_tabsheader li[class='layui-this']").attr('lay-id');
    		if(isNull(choosePage)){
    			winui.window.msg('请选中当前标签页~', {shift: 6});
    			return;
    		}
    		var tabtitle = $("#LAY_app_tabsheader li");
    		var ids = new Array();
    		$.each(tabtitle, function(i) {
    			if($(this).attr("lay-id") != 'initDefaultPageId' && $(this).attr("lay-id") != choosePage){
    				ids.push($(this).attr("lay-id"));
    			}
    		})
    		//如果关闭所有 ，即将所有的lay-id放进数组，执行tabDeleteAll
    		active.tabDeleteAll(ids);
    	}
    	$('.rightmenu').hide(); //最后再隐藏右键菜单
    });
    
    // 全屏操作
    var isFullScreen = false;// 全屏参数
    $("body").on("click", "#isFullScreen", function(e){
    	if(!isFullScreen){// 非全屏状态
    		isFullScreen = true;
    		fullScreen();
    	}else{// 全屏状态
    		isFullScreen = false;
    		exitFullScreen();
    	}
    });

    // 个人中心
    $("body").on("click", ".winui-start-syspersonal", function(e){
    	// 否则判断该tab项是否以及存在
		var isData = false; // 初始化一个标志，为false说明未打开该tab项 为true则说明已有
		$.each($("#LAY_app_tabsheader li[lay-id]"), function() {
			// 如果点击左侧菜单栏所传入的id 在右侧tab项中的lay-id属性可以找到，则说明该tab项已经打开
			if($(this).attr("lay-id") === "syspersonal") {
				isData = true;
			}
		})
		if(isData == false) {
			// 标志为false 新增一个tab项
	    	active.tabAdd("../../tpl/syspersonal/syspersonal.html", "syspersonal", '<i class="fa fa-user"></i>个人中心');
		}
    	active.tabChange("syspersonal");
    });
    
    // 任务空间
    $("body").on("click", ".winui-start-job-space", function(e){
		window.open("../../tpl/jobSpace/jobSpace.html");
    });
    
    // 配色方案
    $("body").on("click", ".winui-start-color-scheme", function(e){
		layer.open({
			type: 2,
			title: '配色方案',
			shadeClose: true,
			closeBtn: 0,
			shade: 0.3,
			area: ['380px', '95%'],
			offset: 'rb',
			content: '../../tpl/colorScheme/colorScheme.html'
		}); 
    });
    
    // 切换风格
    $("body").on("click", ".winui-switching-style", function(e){
    	location.href = "../../tpl/index/index.html";
    });

    var nowType = isNull(getCookie("languageType")) ? "zh" : getCookie("languageType");
    if(nowType == "zh"){
        // 中文
        $("#switchLanguage").find("a").attr("title", "切换英文");
		$("#switchLanguage").find("a").html('<svg t="1598752278678" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="5850" width="16" height="16"><path d="M294.144 617.301333v-102.4h101.717333v102.4H294.144z m159.061333 0v-102.4H555.52v102.4h-102.4zM884.394667 51.968c76.202667 0 138.581333 61.44 139.093333 136.106667v476.16c0 74.666667-62.378667 136.106667-138.581333 136.106666h-32.768v41.386667c0 74.666667-62.378667 136.106667-138.581334 136.106667H138.581333C62.378667 977.834667 0 916.394667 0 841.728V353.792c0-73.216 59.306667-133.034667 132.949333-136.106667v-29.610666c0-74.752 62.464-136.106667 138.666667-136.106667h612.693333zM611.754667 687.786667V462.250667H452.693333V384h-56.832v78.250667H239.872V690.346667h54.272v-25.088h101.717333V806.4h56.832V665.258667h102.314667v22.528h56.746667z m333.994666 37.376a83.541333 83.541333 0 0 0 25.6-60.842667v-476.16a85.333333 85.333333 0 0 0-25.6-60.416 88.490667 88.490667 0 0 0-61.44-25.002667h-612.693333a86.186667 86.186667 0 0 0-87.04 85.333334v29.696h528.469333c76.202667 0 138.581333 61.44 138.581334 136.106666V750.08h32.768c23.04 0 44.970667-8.704 61.44-25.088z" fill="#666666" p-id="5851"></path></svg>');
    }else{
        // 英文
        $("#switchLanguage").find("a").attr("title", "切换中文");
        $("#switchLanguage").find("a").html('<svg t="1598752407969" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="6614" width="16" height="16"><path d="M884.394667 52.053333c76.202667 0 138.581333 61.269333 139.093333 136.021334v476.16c0 74.666667-62.378667 136.106667-138.581333 136.106666h-32.768v41.386667c0 74.666667-62.378667 136.106667-138.581334 136.106667H138.581333C62.378667 977.834667 0 916.394667 0 841.728V353.792c0-73.216 59.306667-133.034667 132.949333-136.106667v-29.610666c0-74.752 62.464-136.106667 138.666667-136.106667h612.693333z m61.44 673.024a83.541333 83.541333 0 0 0 25.514666-60.842666v-476.16a85.333333 85.333333 0 0 0-25.6-60.416 88.490667 88.490667 0 0 0-61.44-25.002667h-612.693333a86.186667 86.186667 0 0 0-87.04 85.333333v29.696h528.469333c76.202667 0 138.581333 61.44 138.581334 136.106667V750.08h32.768c23.04 0 44.970667-8.704 61.44-25.088zM398.933333 484.181333v42.496H254.634667V651.093333h-57.856v49.664h169.472c-3.584 6.144-7.68 11.776-12.288 17.92-27.136 31.744-79.872 58.88-157.696 80.384l29.184 46.592c77.824-21.504 134.144-51.712 168.96-91.136 12.8-15.872 23.552-33.792 32.768-53.76h1.024c27.136 60.416 94.72 108.544 202.752 145.408l29.696-48.128c-90.624-24.576-150.016-56.832-177.664-97.28h172.544V651.093333h-58.88V526.677333h-143.36v-42.496h-54.272zM306.858667 651.093333v-74.752h92.16v7.68c-1.536 23.552-5.12 46.08-11.264 67.072h-80.896z m137.216 0c4.608-20.48 7.68-42.496 9.216-67.072v-7.68H544.426667v74.752H444.074667zM199.338667 407.381333v49.152h113.152v43.008h54.272V456.533333H485.546667v43.008h54.272V456.533333h113.152v-49.152H539.818667V366.933333H485.546667v40.448H366.762667V366.933333h-54.272v40.448H199.338667z" fill="#666666" p-id="6615"></path></svg>');
    }
    // 切换语言
    $("body").on("click", "#switchLanguage", function(e){
        var nowType = isNull(getCookie("languageType")) ? "zh" : getCookie("languageType");
        if(nowType == "zh"){
        	// 中文，设置为英文
            setCookie('languageType', "cn", 'd30');
		}else{
            // 英文，设置为中文
            setCookie('languageType', "zh", 'd30');
		}
        window.location.reload();
    });
    
    // 菜单收缩按钮
    var notOrContraction = false;// 菜单收缩参数
    $("body").on("click", "#notOrContraction", function(e){
    	if(!notOrContraction){// 展开状态
    		notOrContraction = true;
    		$(".tradition-left").animate({width: "0px"});
    		$(".tradition-right").animate({width: "100%"});
    	}else{// 收缩状态
    		notOrContraction = false;
    		$(".tradition-left").animate({width: "220px"});
    		var _width = $("body").width();
    		$(".tradition-right").animate({width: (_width - 220) + "px"}, function () {
    			$(this).css({width:"calc(100% - 220px)"})
    		});
    	}
    });
    
    // 退出
    $("body").on("click", ".logout", function(e){
    	winui.window.confirm('确认注销吗?', {id: 'exit-confim', icon: 3, title: '提示', skin: 'msg-skin-message', success: function(layero, index){
			var times = $("#exit-confim").parent().attr("times");
			var zIndex = $("#exit-confim").parent().css("z-index");
			$("#layui-layer-shade" + times).css({'z-index': zIndex});
		}}, function (index) {
        	AjaxPostUtil.request({url: reqBasePath + "login003", params: {}, type: 'json', method: "POST", callback: function(json){
 	   			if(json.returnCode == 0){
 	   				if (etiger != null) {
		 	   			etiger.socket.close();
		 	   		}
	 	   			location.href = "../../tpl/index/login.html";
 	   			}else{
 	   				location.href = "../../tpl/index/login.html";
 	   			}
 	   		}});
        });
    });
    
    // 头部桌面列表滚动事件
    $("body").on("wheel", ".desktop-menu-box", function(e){
    	var _this = $(this);
    	var right = _this.find("ul").width() - _this[0].offsetWidth;
    	if (_this.scrollLeft() < right && e.originalEvent.deltaY > 0) {
            //禁止事件默认行为（此处禁止鼠标滚轮行为关联到"屏幕滚动条上下移动"行为）  
            var left = (_this.scrollLeft() + 50);
            _this.scrollLeft(left) 
        }
        if (_this.scrollLeft() > 0 && e.originalEvent.deltaY < 0) {
            //禁止事件默认行为（此处禁止鼠标滚轮行为关联到"屏幕滚动条上下移动"行为）  
            var left = (_this.scrollLeft() - 50);
            _this.scrollLeft(left) 
        }
    });
    
    // 头部桌面列表点击事件
    $("body").on("click", ".desktop-menu-box ul .layui-nav-item", function(e){
    	$("#sysMenuListBox").find(".layui-nav-tree").hide();
    	$(".desktop-menu-box ul .layui-nav-item").removeClass('select');
    	$(this).addClass('select');
    	var rowId = $(this).attr("rowid");
    	$("#sysMenuListBox").find("ul[menurowid='" + rowId + "']").show();
    });
    
    // 消息中心
    $("body").on("click", "#messageCenter", function(e){
		indexMenu.loadTraditionPage($(this));
    });
    
    // 左侧底部功能
    $("body").on("click", ".tradition-left-bottom .other-item", function(e){
    	var dataMenu = $(this);
    	var icon = dataMenu.find(".other-item-img").html();
		indexMenu.loadTraditionPage(dataMenu, icon);
    });
    
	$(window).resize(function () {
	});
	
	matchingLanguage();

    exports('traditionpageindex', {});
});
