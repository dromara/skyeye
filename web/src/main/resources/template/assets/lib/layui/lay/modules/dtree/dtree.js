/***
 * 自定义树组件封装模块
 */

layui.define(['jquery','layer','element','form'], function(exports) {
	var $ = layui.$,
		layer = layui.layer,
		element = layui.element,
		form = layui.form;
	
	// 树的公共定义样式汇总
	var LI_NAV_CHILD = "nav-tree-ul-sid", LI_NAV_ITEM = "nav-tree-item", 
		LI_DIV_ITEM = "nav-tree-div", ICONFONT = "iconfont", ICONFONTSPECIAL="iconfont-special", 
		LI_DIV_MENUBAR = "dtree-menubar",LI_DIV_MENUBAR_DOWN = "icon-move-down", LI_DIV_MENUBAR_UP = "icon-move-up", LI_DIV_MENUBAR_REFRESH = "icon-refresh", LI_DIV_MENUBAR_DELETE = "icon-delete1", LI_DIV_MENUBAR_SEARCH = "icon-search_list_light",
		LI_DIV_TOOLBAR = "nav-tree-toolbar-div", TOOLBAR_SHOW = "nav-tree-toolbar-div-show", LI_DIV_TOOLBAR_ADD = "icon-roundadd", LI_DIV_TOOLBAR_EDIT = "icon-bianji", LI_DIV_TOOLBAR_DEL = "icon-roundclose",
		LI_DIV_SPREAD_LAST = "icon-dian",
		LI_DIV_CHECKBAR = "nav-tree-checkbox-div", LI_DIV_CHECKBAR_ON = "icon-fuxuankuangxuanzhong", LI_DIV_CHECKBAR_OUT = "icon-fuxuankuang", LI_DIV_CHECKBAR_NOALL = "icon-fuxuankuang-banxuan",
		LI_CLICK_CHECKBAR = "d-click-checkbar",		//绑定点击复选框时需要用到
		LI_DIV_TEXT_CLASS = "t-click", UL_ROOT="dtree";
	
	// 树的自定义样式
	var DTREE = "dtree-",			//自定义样式前缀
		ITEMTHIS = "-item-this",	//自定义样式当前行选中后缀
		ITEM = "-item",				//自定义样式当前行后缀
		IFONT = "-iconfont",		//自定义样式图标样式后缀
		FICON = "-ficon",			//自定义样式一级图标样式后缀
		ICON = "-icon",				//自定义样式二级图标样式后缀
		CBOX = "-checkbox",			//自定义样式复选框样式后缀
		CHS = "-choose";			//自定义样式复选框选中样式后缀
	
	// 树的公共指定
	var NAV_THIS = "nav-this",	//当前节点
		NAV_SHOW = "nav-show",	//显示子节点
		ICON_HIDE = "icon-hide" //隐藏dot图标
		$BODY = $("body"),		//body选择器
		MOD_NAME = "dtree",		//模块名称
		VERSION = "v2.4.5",		//版本	
		DTrees = {};			//当前被实例化的树的集合
	
	// 树的一级节点图标集合
	var firstIconArray = {
		"-1": {"open": "icon-null-open", "close": "icon-null-close"},			//未指定
		"0" : {"open": "icon-jian", "close": "icon-jia"},
		"1" : {"open": "icon-xiangxia1", "close": "icon-xiangyou"}
	}
	
	// 树的二级节点图标集合
	var nodeIconArray = {
		"-1": {"open": "icon-null-open", "close": "icon-null-close"},			//未指定
		"0" : {"open": "icon-wenjianjiazhankai", "close": "icon-weibiaoti5"}
	};
	
	var leafIconArray = {
		"-1": "icon-null",			//未指定
		"0" : "icon-weibiaoti5", 		//文件夹
		"1" : "icon-yonghu",			//人员
		"2" : "icon-fenzhijigou",		//机构
		"3" : "icon-fenguangbaobiao",	//报表
		"4" : "icon-xinxipilu",			//信息
		"5" : "icon-shuye1",				//叶子
		"6" : "icon-caidan_xunzhang",	//勋章
		"7" : "icon-normal-file"		//文件
	};
	
	// 树自定义操作事件名称集合	绑定d-*的事件
	var eventName = {
		addToolbar: "addToolbar",						//点击toolbar新增
		editToolbar: "editToolbar",						//点击toolbar编辑
		delToolbar: "delToolbar",						//点击toolbar删除
		extToolbar: "extToolbar",						//自定义toolbar处理
		checkNodeClick: "checkNodeClick",				//点击复选框
		itemNodeClick: "itemNodeClick",					//点击子节点div
		
		moveDown: "moveDown",							//menubar展开节点
		moveUp: "moveUp",								//menubar收缩节点
		refresh: "refresh",								//menubar刷新树
		remove: "remove",								//menubar删除选中节点
		searchNode: "searchNode"						//menubar查询节点
	};
	
	// 树的公共事件
	var event = {
		getElemId: function(options){	// 根据传入的参数获取ID
			var elem = options.elem || "";
			var obj = options.obj || $(elem);
			
			if (obj.length == 0) {	//页面中未找到绑定id
				return "";
			} else {
				var id = $(obj)[0].id;
				return id;
			}
		},
		checkBindId: function(id){	// 查找当前ID是否已被树绑定
			var tree = DTrees[id];
			if(typeof tree !== "object"){
				return false;	//未绑定
			} else {
				return true;	//绑定
			}
		},
		escape: function(html){
			if(typeof html !== 'string') return '';
			return html.replace(entityReg.escape, function(match){return entityMap.escape[match];});
		},
		unescape: function(str){
			if(typeof str !== 'string') return '';
			return str.replace(entityReg.unescape, function(match){return entityMap.unescape[match];});
		}
	};
	
	// 特殊符号转义
	var keys = Object.keys || function(obj) {
		obj = Object(obj);
		var arr = [];
		for(var a in obj) arr.push(a);
		return arr;
	};
	var invert = function(obj){
		obj = Object(obj);
		var result = {};
		for(var a in obj) result[obj[a]] = a;
		return result;
	};
	var entityMap = {
		escape: {
			"&" : "&amp;",
			"<" : "&lt;",
			">" : "&gt;",
			"'" : "&quo;"
		}
	};
	entityMap.unescape = invert(entityMap.escape);
	var entityReg = {
		escape: RegExp('[' + keys(entityMap.escape).join('') + ']', 'g'),
		unescape: RegExp('(' + keys(entityMap.unescape).join('|') + ')', 'g'),
	};
	
	//异步加载接口
	var AjaxHelper = {
		request : function(config) {
			var data = config.data ? config.data : {};
			var async = (typeof (config.async) === "boolean") ? config.async : true;
			data.userToken = getCookie('userToken');
			data.loginPCIp = returnCitySN["cip"];
			$.ajax({
				type : config.type ? config.type : "POST",
				headers : config.headers,
				url : config.url,
				dataType : config.dataType ? config.dataType : "json",
				data : data,
				async : async,
				success : config.success,
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					var sessionstatus = XMLHttpRequest.getResponseHeader('SESSIONSTATUS');
					if (typeof (config.error) === "function") {
						config.error();
					} else {
						if (sessionstatus == "TIMEOUT") {//超时跳转
							var win = window;
							while (win != win.top){
								win = win.top;
							}
							win.location.href = reqBasePath + "/tpl/index/login.html";//XMLHttpRequest.getResponseHeader("CONTEXTPATH");  
						}else if(sessionstatus == "NOAUTHPOINT"){
							layer.msg('您不具备该权限。',{icon:5, shift:6});
						}
					}
				},
				statusCode : {
					404 : function() {
						layer.msg('未找到指定请求，请检查访问路径！',{icon:5, shift:6});
					},
					500 : function() {
						layer.msg('系统错误，请联系管理员。',{icon:5, shift:6});
					}
				},
				complete : function(XMLHttpRequest, textStatus) {
					if (typeof (config.complete) === "function") {
						config.complete(XMLHttpRequest, textStatus);
					}
				}
			});
		},
		serialize: function(param){	//json序列化   key=value&key1=value1
			var p = "?";
			for (var key in param) {
				p += key + "=" + param[key] + "&";
			}
			p = p.substring(0, p.length-1);
			return p;
		}
	};
	
	// 树类
	var DTree = function(options){
		var node = {		// 树节点选中时，包含当前节点的全部信息
			nodeId: "",		//节点ID
			parentId: "",	//父节点ID
			context: "",	//节点内容
			isLeaf: "",		//是否叶子节点
			level: "",		//层级
			spread: "",		//节点展开状态
			dataType: "",	//节点标记
			ischecked: "",	//节点复选框选中状态
			initchecked: "",	//节点复选框初始状态
			basicData: ""		//用户自定义的记录节点数据
		};
		
		// 树返回的json格式
		var response = {
			statusName: "returnCode",		//返回标识
			statusCode: 0,		//返回码
			message: "returnMessage",		//返回信息
			rootName: "rows",		//根节点名称
			treeId: "id",			//节点ID
			parentId: "parentId",	//父节点ID
			title: "title",		//节点名称
			iconClass: "iconClass",		//自定义图标
			childName: "children",	//子节点名称
			isLast: "isLast",		//是否最后一级节点
			level: "level",			//层级
			checkArr: "checkArr",	//复选框列表
			isChecked: "isChecked", //是否选中
			type: "type",			//复选框标记
			basicData: "basicData"	//表示用户自定义需要存储在树节点中的数据
				
		};
		
		// 树的默认发起请求参数格式，最后会将value作为参数名称传递
		var defaultRequest = {
			nodeId: "nodeId",		//节点ID
			parentId: "parentId",	//父节点ID
			context: "context",	//节点内容
			isLeaf: "isLeaf",		//是否叶子节点
			level: "level",		//层级
			spread: "spread",		//节点展开状态
			dataType: "dataType",	//节点标记
			ischecked: "ischecked",	//节点复选框选中状态
			initchecked: "initchecked",	//节点复选框初始状态
			basicData: "basicData"		//用户自定义的记录节点数据
		};
		
		var toolbarStyle = {
			title: "节点",
			area: ["60%","80%"]
		}
		
		var toolbarFun = {
			addTreeNode: function(param) {	//添加树节点后调用的函数，用于用户自定义，如未指定则树不会发生变化
				return ;
			},	
			editTreeNode: function(param) {	//编辑树节点后调用的函数，用于用户自定义，如未指定则树不会发生变化
				return ;
			},
			editTreeLoad: function(param){	// 编辑树的数据回显，用于打开编辑时，回填数据
				return ;
			},
			delTreeNode: function(param){	//删除树后调用的函数，用于用户自定义，如未指定则树不会发生变化
				return ;
			}
		};
		
		var menubarFun = {
			remove: function(checkbarNodes){			//删除复选框选中节点，需要用户自定义，如未指定则树只是页面上做了修改
				return true;
			}
		};
			
		var checkbarFun = {
			chooseBefore: function($i, node){	// 复选框点击前回调
				return true;
			},
			chooseDone: function(checkbarNodesParam) {	//复选框点击事件完毕后，返回该树关于复选框操作的全部信息，用于用户自定义，如未指定则树只是页面上做了修改
				return ;
			}
		};
		
		// 树点击节点时，打开iframe页面参数配置
		var iframe = {
			iframeElem: "",		//iframe的ID
			iframeUrl: "",		//树关联的frame地址
			iframeLoad: "leaf",	//点击哪一层加载frame： node：所有节点， leaf：默认，最后一级
			iframeDefaultRequest: defaultRequest,  //iframe的默认参数,目的是与加载树的参数不一样
			iframeRequest: {}	//iframe的自定义参数
		};
		
		var iframeFun = {
			iframeDone: function(iframeParam){	//iframe加载完毕后，用于用户自定义事件
				return ;
			}
		};
		
		var style = {
			item: "",
			itemThis: "",
			ifont: "",
			icon: "",
			cbox: "",
			chs: ""
		};
		
		/** 默认赋值**/
		this.response = response;
		this.defaultRequest = defaultRequest;
		this.toolbarFun = toolbarFun;
		this.toolbarStyle = toolbarStyle;
		this.menubarFun = menubarFun;
		this.checkbarFun = checkbarFun;
		this.iframe = iframe;
		this.iframeFun = iframeFun;
		this.style = style;
		
		/** 数据绑定**/
		this.node = node;	// 当前节点的全部信息
		this.checkbarNode = [];	// 复选框标记的全部节点数据
		this.checkArrLen = 0;	//添加节点的时判断复选框个数
		this.temp = [];	// 临时变量
		
		this.setting(options);
	};
	
	/******************** 初始参数加载 ********************/
	// 设置值
	DTree.prototype.setting = function(options) {
		this.options = options || {};
		
		/** 绑定元素参数（必填，2个参数项必填一个）**/
		this.elem = this.options.elem || "";			//树绑定的元素ID：#elem
		this.obj = this.options.obj || $(this.elem);	//树绑定的jquery元素，用于当元素是延迟加载出来的话，可以用这个找到
		
		/** 基本参数**/
		this.level = this.options.level || 1;			//默认初始层级为1
		this.initLevel = this.options.initLevel || 2;	//默认展开节点  2节    
		this.type = this.options.type || "load";	// 树的加载方式  all，全量树，  load，增量树，默认load
		this.cache = (typeof (this.options.cache) === "boolean") ? this.options.cache : true;		//开启数据缓存
		
		/** 样式相关参数**/
		this.firstIconArray = $.extend(firstIconArray, this.options.firstIconArray) || firstIconArray;	//用户自定义一级图标集合，node
		this.nodeIconArray = $.extend(nodeIconArray, this.options.nodeIconArray) || nodeIconArray;	//用户自定义二级图标集合，node
		this.leafIconArray = $.extend(leafIconArray, this.options.leafIconArray) || leafIconArray;	//用户自定义二级图标集合，leaf
		this.skin = this.options.skin || "theme";	// 自定义样式 
		if(this.skin == "layui"){ // layui主题
			this.ficon = this.options.ficon || "1";		// 一级图标样式，0：+，-
			this.dot = (typeof (this.options.dot) === "boolean") ? this.options.dot : false;		//是否显示一级图标的小圆点，默认不显示
			this.icon = this.options.icon || "7";	//二级图标样式，0：文件夹，1：人员，2：机构，3：报表，4：信息，5：叶子，6：勋章, -1：不显示二级图标。默认'1'
			this.nodeIcon = (typeof this.icon === 'string' || typeof this.icon === 'number') ? (this.icon == "-1" ? "-1" : "-1") : this.icon[0];		// 二级图标中的node节点图标
		} else { // 默认主题  或者自定义主题
			this.ficon = this.options.ficon || "0";		// 一级图标样式，0：+，-
			this.dot = (typeof (this.options.dot) === "boolean") ? this.options.dot : true;		//是否显示一级图标的小圆点，默认显示
			this.icon = this.options.icon || "5";	//二级图标样式，0：文件夹，1：人员，2：机构，3：报表，4：信息，5：叶子，6：勋章, -1：不显示二级图标。默认'5'
			this.nodeIcon = (typeof this.icon === 'string' || typeof this.icon === 'number') ? (this.icon == "-1" ? "-1" : "0") : this.icon[0];		// 二级图标中的node节点图标
		}
		
		/** 内置样式属性*/
		this.ficonOpen =  this.firstIconArray[this.ficon]["open"]; // 一级图标中的node节点open图标
		this.ficonClose = this.firstIconArray[this.ficon]["close"]; // 一级图标中的node节点close图标
		this.nodeIconOpen =  this.nodeIconArray[this.nodeIcon]["open"];  // 二级图标中的node节点open图标
		this.nodeIconClose =  this.nodeIconArray[this.nodeIcon]["close"]; // 二级图标中的node节点close图标
		this.leafIcon = (typeof this.icon === 'string' || typeof this.icon === 'number') ? this.icon : this.icon[1];	// 二级图标中的leaf节点图标
		this.leafIconShow =  this.leafIconArray[this.leafIcon]; // 二级图标中的leaf节点图标
		
		this.style.item = DTREE + this.skin + ITEM;
		this.style.itemThis = DTREE + this.skin + ITEMTHIS;
		this.style.ifont = DTREE + this.skin + IFONT;
		this.style.ficon = DTREE + this.skin + FICON;
		this.style.icon = DTREE + this.skin + ICON;
		this.style.cbox = DTREE + this.skin + CBOX;
		this.style.chs = DTREE + this.skin + CHS;
		
		/** 数据加载参数**/
		this.url = this.options.url || "";		//请求地址
		this.async = (typeof (this.options.async) === "boolean") ? this.options.async : true;	//异步同步加载,默认异步加载
		this.headers = this.options.headers || {};		// ajax header属性
		this.method = this.options.method || "post";	//请求类型
		this.dataType = this.options.dataType || "json";	//参数类型
		this.defaultRequest = $.extend(this.defaultRequest, this.options.defaultRequest) || this.defaultRequest;	//默认请求参数
		this.request = this.options.request || {};		//用户自定义请求参数
		this.response = $.extend(this.response, this.options.response) || this.response;	//返回json格式
		this.data = this.options.data || [];		//初始化指定该参数，则不会访问异步接口
		this.dataFormat = this.options.dataFormat || "levelRelationship"  //用于用户配置的data数据格式，list：列表，  levelRelationship：层级关系，默认
		this.dataStyle = this.options.dataStyle || "defaultStyle";  //用于用户配置layui通用的json数据风格,layuiStyle:layui风格，defaultStyle：默认风格
		this.done = this.options.done || function(data, obj){};		//树加载完毕后的回调（仅限异步加载）
		
		/** 工具栏参数**/
		this.toolbar = this.options.toolbar || false;	//是否开启可编辑模式
		this.toolbarStyle = $.extend(this.toolbarStyle, this.options.toolbarStyle) || this.toolbarStyle;	//toolbar的自定义风格，标题，弹框大小
		this.toolbarScroll = this.options.toolbarScroll || "body";	//树的上级div容器，让树可以显示滚动条的div容器
		this.toolbarLoad = this.options.toolbarLoad || "node";	//toolbar作用范围：node:所有节点，noleaf:非最后一级节点，leaf:最后一级
		this.toolbarShow = this.options.toolbarShow || ["add","edit","delete"];		// toolbar三个按钮自定义加载
		this.toolbarBtn = this.options.toolbarBtn || null;		// toolbar增删改中内容的自定义加载
		this.toolbarExt = this.options.toolbarExt || [];		// toolbar按钮扩展
		this.toolbarFun = $.extend(this.toolbarFun, this.options.toolbarFun) || this.toolbarFun;		// toolbar事件加载
		
		/** 菜单栏参数**/
		this.menubar = this.options.menubar || false;	//是否打开菜单栏
		this.menubarFun = $.extend(this.menubarFun, this.options.menubarFun) || this.menubarFun;	//menubar事件加载
		
		/** 复选框参数**/
		this.checkbar = this.options.checkbar || false;	//是否开启复选框模式
		this.checkbarLoad = this.options.checkbarLoad || "node";  // 复选框作用范围，node：所有节点， leaf：最后一级；默认所有节点
		this.checkbarType = this.options.checkbarType || "all" ;	//复选框选中形式	all：子集选中父级也选中，  no-all：子集选中父级半选中，子集全选父级选中，p-casc：父级选中子集全选，子集无法改变父级选中状态， self：没有任何级联关系   默认all
		this.checkbarData = this.options.checkbarData || "choose" ;	//复选框记录数据类型形式，  change表示记录变更数据，choose表示记录选中数据，all，记录全部数据，默认choose
		this.checkbarFun =  $.extend(this.checkbarFun, this.options.checkbarFun) || this.checkbarFun;	// checkbar事件加载
		
		/** iframe模式参数**/
		this.useIframe = this.options.useIframe || false;	// 是否加载iframe 默认false，
		this.iframe = $.extend(this.iframe, this.options.iframe) || this.iframe;	//iframe配置
		this.iframeFun = $.extend(this.iframeFun, this.options.iframeFun) || this.iframeFun;	//iframe事件加载
		
	};
	
	// 设置值
	DTree.prototype.reloadSetting = function(options) {
		this.options = $.extend(this.options, options) || this.options;
		
		/** 绑定元素参数**/
		this.elem = this.options.elem || this.elem;			//树绑定的元素ID：#elem
		this.obj = this.options.obj || this.obj;	//树绑定的jquery元素，用于当元素是延迟加载出来的话，可以用这个找到
		
		/** 基本参数**/
		this.level = this.options.level || this.level; 				//默认初始节点为1
		this.initLevel = this.options.initLevel || this.initLevel;	//默认展开节点  2节    
		this.type = this.options.type || this.type;		// 树的加载方式  all，全量树，  load，增量树，默认load
		this.cache = (typeof (this.options.cache) === "boolean") ? this.options.cache : this.cache;		//开启数据缓存
		
		/** 样式相关参数**/
		this.firstIconArray = $.extend(firstIconArray, this.options.firstIconArray) || this.firstIconArray;	//用户自定义一级图标集合，node
		this.nodeIconArray = $.extend(nodeIconArray, this.options.nodeIconArray) || this.nodeIconArray;	//用户自定义二级图标集合，node
		this.leafIconArray = $.extend(leafIconArray, this.options.leafIconArray) || this.leafIconArray;	//用户自定义二级图标集合，leaf
		this.skin = this.options.skin || this.skin;	// 自定义样式 
		this.ficon = this.options.ficon || this.ficon;		// 一级图标样式，0：+，-
		this.dot = (typeof (this.options.dot) === "boolean") ? this.options.dot : this.dot;		//是否显示一级图标的小圆点，默认不显示
		this.icon = this.options.icon || this.icon;	//二级图标样式，0：文件夹，1：人员，2：机构，3：报表，4：信息，5：叶子，6：勋章, -1：不显示二级图标。默认'1'
		
		/** 内置样式属性*/
		this.ficonOpen =  this.firstIconArray[this.ficon]["open"]; // 一级图标中的node节点open图标
		this.ficonClose = this.firstIconArray[this.ficon]["close"]; // 一级图标中的node节点close图标
		this.nodeIcon = (typeof this.icon === 'string' || typeof this.icon === 'number') ? (this.icon == "-1" ? "-1" : "0") : this.icon[0];		// 二级图标中的node节点图标
		this.nodeIconOpen =  this.nodeIconArray[this.nodeIcon]["open"];  // 二级图标中的node节点open图标
		this.nodeIconClose =  this.nodeIconArray[this.nodeIcon]["close"]; // 二级图标中的node节点close图标
		this.leafIcon = (typeof this.icon === 'string' || typeof this.icon === 'number') ? this.icon : this.icon[1];	// 二级图标中的leaf节点图标
		this.leafIconShow =  this.leafIconArray[this.leafIcon]; // 二级图标中的leaf节点图标
		
		this.style.item = DTREE + this.skin + ITEM;
		this.style.itemThis = DTREE + this.skin + ITEMTHIS;
		this.style.ifont = DTREE + this.skin + IFONT;
		this.style.ficon = DTREE + this.skin + FICON;
		this.style.icon = DTREE + this.skin + ICON;
		this.style.cbox = DTREE + this.skin + CBOX;
		this.style.chs = DTREE + this.skin + CHS;
		
		/** 数据加载参数**/
		this.url = this.options.url || this.url;		//请求地址
		this.async = (typeof (this.options.async) === "boolean") ? this.options.async : this.async;	//异步同步加载,默认异步加载
		this.headers = this.options.headers || this.headers;		// ajax header属性
		this.method = this.options.method || this.method;	//请求类型
		this.dataType = this.options.dataType || this.dataType;	//参数类型
		this.defaultRequest = $.extend(this.defaultRequest, this.options.defaultRequest) || this.defaultRequest;	//默认请求参数
		this.request = this.options.request || this.request;		//用户自定义请求参数
		this.response = $.extend(this.response, this.options.response) || this.response;	//返回json格式
		this.data = this.options.data || this.data;		//初始化指定该参数，则不会访问异步接口
		this.dataFormat = this.options.dataFormat || this.dataFormat  //用于用户配置的data数据格式，list：列表，  levelRelationship：层级关系，默认		
		this.dataStyle = this.options.dataStyle || this.dataStyle;  //用于用户配置layui通用的json数据风格,layuiStyle:layui风格，defaultStyle：默认风格
		this.done = this.options.done || this.done;		//树加载完毕后的回调（仅限异步加载）
		
		/** 可编辑模式参数**/
		this.toolbar = this.options.toolbar || this.toolbar;	//是否开启可编辑模式
		this.toolbarStyle = $.extend(this.toolbarStyle, this.options.toolbarStyle) || this.toolbarStyle;	//toolbar的自定义风格，标题，弹框大小
		this.toolbarScroll = this.options.toolbarScroll || this.toolbarScroll;	//树的上级div容器，让树可以显示滚动条的div容器
		this.toolbarLoad = this.options.toolbarLoad || this.toolbarLoad;	//toolbar作用范围：node:所有节点，noleaf:非最后一级节点，leaf:最后一级
		this.toolbarShow = this.options.toolbarShow || this.toolbarShow;		// toolbar三个按钮
		this.toolbarBtn = this.options.toolbarBtn || this.toolbarBtn;		// toolbar增删改中内容的自定义加载
		this.toolbarExt = this.options.toolbarExt || this.toolbarExt;		// toolbar按钮扩展
		this.toolbarFun = $.extend(this.toolbarFun, this.options.toolbarFun) || this.toolbarFun;		// toolbar事件加载
		
		/** 菜单栏参数**/
		this.menubar = this.options.menubar || this.menubar;	//是否打开菜单栏
		this.menubarFun = $.extend(this.menubarFun, this.options.menubarFun) || this.menubarFun;	//menubar事件加载
		
		/** 复选框参数**/
		this.checkbar = this.options.checkbar || this.checkbar;	//是否开启复选框模式
		this.checkbarLoad = this.options.checkbarLoad || this.checkbarLoad;  // 复选框作用范围，node：所有节点， leaf：最后一级；默认所有节点
		this.checkbarType = this.options.checkbarType || this.checkbarType ;	//复选框选中形式	all：子集选中父级也选中，  no-all：子集选中父级半选中，子集全选父级选中，p-casc：父级选中子集全选，子集无法改变父级选中状态， self：没有任何级联关系   默认all
		this.checkbarData = this.options.checkbarData || this.checkbarData ;	//复选框记录数据类型形式，  change表示记录变更数据，choose表示记录选中数据，all，记录全部数据，默认choose
		this.checkbarFun =  $.extend(this.checkbarFun, this.options.checkbarFun)|| this.checkbarFun ;	// checkbar事件加载
		
		/** iframe模式参数**/
		this.useIframe = this.options.useIframe || this.useIframe;	// 是否加载iframe 默认false，
		this.iframe = $.extend(this.iframe, this.options.iframe) || this.iframe;	//iframe配置
		this.iframeFun = $.extend(this.iframeFun, this.options.iframeFun) || this.iframeFun;	//iframe事件加载
		
	};
	
	/******************** 初始化数据区域 ********************/
	// 重新加载树
	DTree.prototype.reload = function(options){
		var _this = this;
		_this.reloadSetting(options);
		_this.init();
	};
	
	// 初始化树
	DTree.prototype.init = function(){
		var _this = this;
		if (typeof _this !== "object") {
			layer.msg("树组件未成功加载，请检查配置", {icon:5});
			return ;
		}
		
		if(_this.data && _this.data.length > 0) {
			//先将ul中的元素清空
			_this.obj.html("");
			
			// 第一次解析树
			if (_this.dataFormat == 'list'){
				//1.识别根节点ul中的data-id标签，判断顶级父节点
				var pid = _this.obj.attr("data-id");
				//2.构建一个存放节点的树组
				var rootListData = _this.queryListTreeByPid(pid, _this.data);
				_this.loadListTree(rootListData, _this.data, 1);
			} else {
				_this.loadTree(_this.data);
			}
			
		} else {
			if (!_this.url) {
				layer.msg("数据请求异常，url参数未指定", {icon:5});
				return ;
			}
			
			//先将ul中的元素清空
			_this.obj.html("");
			
			var index = layer.load(1);
			AjaxHelper.request({
				async: _this.async,
				headers: _this.headers,
				type: _this.method,
				url: _this.url,
				dataType: _this.dataType,
				data: _this.getRequestParam(),
				success: function(result) {
					if (typeof result === 'string') {
						result = $.parseJSON(result);
					}
					var code = "";
					if (_this.dataStyle == 'layuiStyle'){
						code = result[_this.response.statusName];
					} else {
						code = result[_this.response.statusName];
					}
					
					if (code == _this.response.statusCode) {
						// 第一次解析树
						if (_this.dataFormat == 'list'){
							//1.识别根节点ul中的data-id标签，判断顶级父节点
							var pid = _this.obj.attr("data-id");
							//2.构建一个存放节点的树组
							var rootListData = _this.queryListTreeByPid(pid, result[_this.response.rootName]);
							_this.loadListTree(rootListData, result[_this.response.rootName], 1);
						} else {
							_this.loadTree(result[_this.response.rootName]);
						}
						
						// 加载完毕后的回调
						_this.done(result, _this.obj);
					} else {
						if (_this.dataStyle == 'layuiStyle'){
							layer.msg(result[_this.response.message], {icon:2});
						} else {
							layer.msg(result.status[_this.response.message], {icon:2});
						}
					}
					layer.close(index);
				},
				complete: function(){
					layer.close(index);
				}
			});
		}
	};
	
	// 加载子节点
	DTree.prototype.getChild = function($div, async) {
		var _this = this,
			$ul = $div.next("ul");
		
		if (!_this.url) {
			layer.msg("数据请求异常，url参数未指定", {icon:5});
			return ;
		}
		
		_this.setNodeParam($div);
		
		var index = layer.load(1);
		AjaxHelper.request({
			async: (typeof (async) === "boolean") ? async : _this.async,
			headers: _this.headers,
			type: _this.method,
			url: _this.url,
			dataType: _this.dataType,
			data:  _this.getRequestParam(),
			success: function(result) {
				if (typeof result === 'string') {
					result = $.parseJSON(result);
				}
				var code = "";
				if (_this.dataStyle == 'layuiStyle'){
					code = result[_this.response.statusName];
				} else {
					code = result.status[_this.response.statusName];
				}
				
				if (code == _this.response.statusCode) {
					// 解析树
					if (_this.dataFormat == 'list'){
						var pid = _this.node.nodeId;
						var level = _this.node.nodeLevel;
						var pListData = _this.queryListTreeByPid(pid, result[_this.response.rootName]);
						_this.loadListTree(pListData, result[_this.response.rootName], level++, $ul);
					} else {
						_this.loadTree(result[_this.response.rootName], $ul);
					}
					
					$ul.addClass(NAV_SHOW);
				} else {
					if (_this.dataStyle == 'layuiStyle'){
						layer.msg(result[_this.response.message], {icon:2});
					} else {
						layer.msg(result.status[_this.response.message], {icon:2});
					}
				}
				
				layer.close(index);
			},
			complete: function(){
				layer.close(index);
			}
		});
	};
	
	// 初始化树或者拼接树
	DTree.prototype.loadListTree = function(pListData, listData, level, $ul){		
		var _this = this;
		$ul = $ul || _this.getNowNodeUl();	//当前选中的节点或根节点
		if (pListData.length > 0){
			for (var i = 0; i < pListData.length; i++) {
				// 1.获取已知节点的全部数据
				var data = pListData[i];	
				if(typeof data !== "object") continue;
				var treeId = data[_this.response.treeId],
					parentId = data[_this.response.parentId],
					title = data[_this.response.title],
					checkArr = data[_this.response.checkArr],
					iconClass = data[_this.response.iconClass],
					basicData = data[_this.response.basicData],
					isLast = false;
				if(!iconClass){iconClass = '';}
				if(basicData) {
					basicData = event.escape(JSON.stringify(basicData));
				}else{
					basicData = "";
				}
				
				if(checkArr && checkArr.length > 0){
					_this.checkArrLen = checkArr.length;		// 获取复选框个数
				}
				
				// 2. 根据已知数据的id判断该条数据是否还有子数据
				var childListData = _this.queryListTreeByPid(treeId, listData);
				if(childListData.length == 0){
					// 不存在,则为最后一级节点
					isLast = true;
				}
				
				// 3. 页面元素加载数据
				if ($ul.hasClass(UL_ROOT)) {
					var dom = _this.getDom(treeId, parentId, title, isLast, iconClass, checkArr, level);
					$ul.append(_this.getLiItemDom(dom, treeId, parentId, isLast, level, basicData, "root"));
				} else {
					var dom = _this.getDom(treeId, parentId, title, isLast, iconClass, checkArr, level);
					$ul.append(_this.getLiItemDom(dom, treeId, parentId, isLast, level, basicData, "item"));
				}
				
				// 4.有子数据的元素加载子节点
				if(childListData.length > 0){
					var cLevel = parseInt(level)+1;
					_this.loadListTree(childListData, listData, cLevel, _this.obj.find("ul[data-id='"+data[_this.response.treeId]+"']"));
				}
			}
		}
	}
	
	// 根据父ID查找list数据中匹配的元素
	DTree.prototype.queryListTreeByPid = function(pid, listData){		
		var _this = this;
		var rootListData = [];
		if (listData) {
			for (var i = 0; i < listData.length; i++) {
				var data = listData[i];		
				if(typeof data !== "object") continue;
				if(pid == "null" || pid == null){
					if(data[_this.response.parentId] == null) {
						rootListData.push(data);
					}
				} else {
					if (data[_this.response.parentId] == pid){
						rootListData.push(data);
					}
				}
			}
		}
		return rootListData;
	}
	
	// 初始化树或者拼接树
	DTree.prototype.loadTree = function(root, $ul){		
		var _this = this;
		if (root) {
			$ul = $ul || _this.getNowNodeUl();	//当前选中的节点或根节点
			for (var i = 0; i < root.length; i++) {	// 遍历跟节点或追加的跟节点
				var data = root[i];		
				if(typeof data !== "object") continue;
				var treeId = data[_this.response.treeId],
					parentId = data[_this.response.parentId],
					title = data[_this.response.title],
					isLast = data[_this.response.isLast],
					checkArr = data[_this.response.checkArr],
					level = data[_this.response.level],
					iconClass = data[_this.response.iconClass],
					basicData = data[_this.response.basicData];
				if(!iconClass){iconClass = '';}
				if(basicData) {
					basicData = event.escape(JSON.stringify(basicData));
				}else{
					basicData = "";
				}
				
				if(checkArr && checkArr.length > 0){
					_this.checkArrLen = checkArr.length;		// 获取复选框个数
				}
				if ($ul.hasClass(UL_ROOT)) {
					var dom = _this.getDom(treeId, parentId, title, isLast, iconClass, checkArr, level);
					$ul.append(_this.getLiItemDom(dom, treeId, parentId, isLast, level, basicData, "root"));
				} else {
					var dom = _this.getDom(treeId, parentId, title, isLast, iconClass, checkArr, level);
					$ul.append(_this.getLiItemDom(dom, treeId, parentId, isLast, level, basicData, "item"));
				}
				
				var children = data[_this.response.childName] || [];
				if (children.length != 0) {
					_this.loadChildTree(children, _this.obj.find("ul[data-id='"+data[_this.response.treeId]+"']"));
				}
			}
		}
	};
	
	//遍历子树
	DTree.prototype.loadChildTree = function(children, $ul){	
		var _this = this;
		for (var i = 0; i < children.length; i++) {	// 遍历子节点
			var data = children[i];
			if(typeof data !== "object") continue;
			var treeId = data[_this.response.treeId],
				parentId = data[_this.response.parentId],
				title = data[_this.response.title],
				isLast = data[_this.response.isLast],
				checkArr = data[_this.response.checkArr],
				level = data[_this.response.level],
				iconClass = data[_this.response.iconClass],
				basicData = data[_this.response.basicData];
			if(!iconClass){iconClass = "";}
			if(basicData){
				basicData = event.escape(JSON.stringify(basicData));
			}else{
				basicData = "";
			}
					
			var dom = _this.getDom(treeId, parentId, title, isLast, iconClass, checkArr, level);
			$ul.append(_this.getLiItemDom(dom, treeId, parentId, isLast, level, basicData, "item"));
			var child = data[_this.response.childName] || [];
			if (child.length != 0) {
				_this.loadChildTree(child, _this.obj.find("ul[data-id='"+data[_this.response.treeId]+"']"));
			}
		}
	};
	
	//新增节点的dom值
	DTree.prototype.getDom = function(treeId, parentId, title, isLast, iconClass, checkArr, level) {	
		var _this = this,
			rootId = "#"+_this.obj[0].id,
			toolbar = _this.toolbar,
			checkbar = _this.checkbar;
		
		return {
			spread: function() {	// + - 图标
				// 获取图标的变量
				var ficon = _this.ficon,
					ficonOpen =  _this.ficonOpen,
					ficonClose = _this.ficonClose;
				
				return isLast ? "<i class='"+ICONFONT+" "+LI_DIV_SPREAD_LAST+" "+(!_this.dot ? ICON_HIDE : "")+" "+_this.style.ifont+" "+_this.style.ficon+"' data-spread='last' data-id='"+treeId+"' root-id='"+rootId+"'></i>" : 
					((level < _this.initLevel) ? "<i class='"+ICONFONT+" "+ficonOpen+" "+_this.style.ifont+" "+_this.style.ficon+"' data-spread='open' data-id='"+treeId+"' root-id='"+rootId+"'></i>" : "<i class='"+ICONFONT+" "+ficonClose+" "+_this.style.ifont+" "+_this.style.ficon+"' data-spread='close' data-id='"+treeId+"' root-id='"+rootId+"'></i>");
			},
			node: function() {	// 二级图标样式
				// 获取图标的变量
				var nodeIcon = _this.nodeIcon,
					leafIcon = _this.leafIcon;
				
				var leafIconShow = _this.leafIconShow,
					nodeIconOpen =  _this.nodeIconOpen,
					nodeIconClose =  _this.nodeIconClose;
				if(iconClass){
					leafIconShow = iconClass;
					nodeIconOpen = iconClass;
					nodeIconClose = iconClass;
				}
				
				if(nodeIcon != "-1" && leafIcon != "-1"){	// 都加载
					return isLast ? "<i class='"+ICONFONT+" "+leafIconShow+" "+ICONFONTSPECIAL+" "+_this.style.ifont+" "+_this.style.icon+"' data-spread='last' data-id='"+treeId+"' root-id='"+rootId+"'></i>" : 
						((level < _this.initLevel) ? "<i class='"+ICONFONT+" "+nodeIconOpen+" "+ICONFONTSPECIAL+" "+_this.style.ifont+" "+_this.style.icon+"' data-spread='open' data-id='"+treeId+"' root-id='"+rootId+"'></i>" : "<i class='"+ICONFONT+" "+nodeIconClose+" "+ICONFONTSPECIAL+" "+_this.style.ifont+" "+_this.style.icon+"' data-spread='close' data-id='"+treeId+"' root-id='"+rootId+"'></i>");
				}
				
				if(nodeIcon != "-1" && leafIcon == "-1"){	// 加载node 隐藏leaf
					return isLast ? "<i class='"+ICONFONT+" "+leafIconShow+" "+ICONFONTSPECIAL+" "+_this.style.ifont+" "+_this.style.icon+"' data-spread='last' data-id='"+treeId+"' root-id='"+rootId+"'></i>" : 
						((level < _this.initLevel) ? "<i class='"+ICONFONT+" "+nodeIconOpen+" "+ICONFONTSPECIAL+" "+_this.style.ifont+" "+_this.style.icon+"' data-spread='open' data-id='"+treeId+"' root-id='"+rootId+"'></i>" : "<i class='"+ICONFONT+" "+nodeIconClose+" "+ICONFONTSPECIAL+" "+_this.style.ifont+" "+_this.style.icon+"' data-spread='close' data-id='"+treeId+"' root-id='"+rootId+"'></i>");
				}
				
				if(nodeIcon == "-1" && leafIcon != "-1"){	// 隐藏node 加载leaf
					return isLast ? "<i class='"+ICONFONT+" "+leafIconShow+" "+ICONFONTSPECIAL+" "+_this.style.ifont+" "+_this.style.icon+"' data-spread='last' data-id='"+treeId+"' root-id='"+rootId+"'></i>" : 
						((level < _this.initLevel) ? "<i class='"+ICONFONT+" "+nodeIconOpen+" "+ICONFONTSPECIAL+" "+_this.style.ifont+" "+_this.style.icon+"' data-spread='open' data-id='"+treeId+"' root-id='"+rootId+"'></i>" : "<i class='"+ICONFONT+" "+nodeIconClose+" "+ICONFONTSPECIAL+" "+_this.style.ifont+" "+_this.style.icon+"' data-spread='close' data-id='"+treeId+"' root-id='"+rootId+"'></i>");
				}
				
				if(nodeIcon == "-1" && leafIcon == "-1"){	// 都隐藏
					return isLast ? "<i class='"+ICONFONT+" "+leafIconShow+" "+ICONFONTSPECIAL+" "+_this.style.ifont+" "+_this.style.icon+"' data-spread='last' data-id='"+treeId+"' root-id='"+rootId+"'></i>" : 
						((level < _this.initLevel) ? "<i class='"+ICONFONT+" "+nodeIconOpen+" "+ICONFONTSPECIAL+" "+_this.style.ifont+" "+_this.style.icon+"' data-spread='open' data-id='"+treeId+"' root-id='"+rootId+"'></i>" : "<i class='"+ICONFONT+" "+nodeIconClose+" "+ICONFONTSPECIAL+" "+_this.style.ifont+" "+_this.style.icon+"' data-spread='close' data-id='"+treeId+"' root-id='"+rootId+"'></i>");
				}
			},
			checkbox: function() {	// 复选框
				var flag = false;
				if(_this.checkbarLoad == "node"){if (checkbar) {flag = true;}} else {if (isLast) {if (checkbar) {flag = true;}}}
				
				if(flag){
					var result = "<div class='"+LI_DIV_CHECKBAR+"' data-id='"+treeId+"' root-id='"+rootId+"'>";
					if(checkArr.length > 0){	// 如果此处报错，说明后台数据中没有提供checkArr字段
						for (var i = 0; i < checkArr.length; i++) {
							var checkData = checkArr[i];
							var isChecked = checkData.isChecked;
							var CHOOSE_CLASS = LI_DIV_CHECKBAR_OUT;
							if (isChecked == "2") {	//半选择
								CHOOSE_CLASS = LI_DIV_CHECKBAR_NOALL + " " + _this.style.chs;
							} else if (isChecked == "1") {	//选择
								CHOOSE_CLASS = LI_DIV_CHECKBAR_ON + " " + _this.style.chs;
							} else {	//未选择或者无值
								CHOOSE_CLASS = LI_DIV_CHECKBAR_OUT;
							}
							result += "<i class='"+ICONFONT+" "+_this.style.ifont+" "+_this.style.cbox+" "+CHOOSE_CLASS+"' data-id='"+treeId+"' root-id='"+rootId+"' data-checked='"+checkData.isChecked+"' data-initchecked='"+checkData.isChecked+"' data-type='"+checkData.type+"' d-click='"+eventName.checkNodeClick+"' data-par='."+LI_CLICK_CHECKBAR+"'></i>";
						}
					}
					result += "</div>";
					return result;
				}
				
				return "";
			},
			text: function() {	// 文字显示
				return "<cite class='"+LI_DIV_TEXT_CLASS+"' data-id='"+treeId+"' data-leaf='"+(isLast ? "leaf" : "node")+"' title='" + title + "'>"+title+"</cite>";
			},
			ul: function() {	//子节点ul
				return isLast ? "<ul class='"+LI_NAV_CHILD+"' data-id='"+treeId+"' root-id='"+rootId+"'></ul>" : 
					((level < _this.initLevel) ? "<ul class='"+LI_NAV_CHILD+" "+NAV_SHOW+"' data-id='"+treeId+"' root-id='"+rootId+"'></ul>" : "<ul class='"+LI_NAV_CHILD+"' data-id='"+treeId+"' root-id='"+rootId+"'></ul>");
			}	
		};
		
	};
	
	// 获取拼接好的li
	DTree.prototype.getLiItemDom =  function(dom, treeId, parentId, isLast, level, basicData, flag) {	
		var _this = this,
			rootId =  "#"+_this.obj[0].id;
		
		var div = "<div class='"+LI_DIV_ITEM+" "+_this.style.item+"' data-id='"+treeId+"' root-id='"+rootId+"' d-click='"+eventName.itemNodeClick+"' data-basic='"+basicData+"' ";
		if(_this.toolbar){
			if(_this.toolbarLoad == "node") { div += " d-contextmenu='true'>"; } 
			if(_this.toolbarLoad == "noleaf") { if(!isLast){ div += " d-contextmenu='true'>"; } else { div += " d-contextmenu='false'>";} }
			if(_this.toolbarLoad == "leaf") { if(isLast){ div += " d-contextmenu='true'>"; } else { div += " d-contextmenu='false'>";} }
		} else { div += " d-contextmenu='false'>"; }
		
		var li = ["<li " + "class='"+LI_CLICK_CHECKBAR+" "+ LI_NAV_ITEM +"'" + "data-id='"+treeId+"'" + "data-pid='"+(flag == "root" ? (parentId ? parentId : "-1") : parentId)+"'" + "root-id='"+rootId+"'" + "data-index='"+level+"'" + ">" +
		          div ,
		          dom.spread(),
		          dom.node(),
		          dom.checkbox(),
		          dom.text(),
		          "</div>", dom.ul(), "</li>"].join("");
		return li;
	};
	
	// 初始化节点，用于数据回显
	DTree.prototype.dataInit = function(chooseId){	
		var _this = this;
		var $div = _this.obj.find("div[data-id='"+chooseId+"']");
		$div.parent().find("."+NAV_THIS).removeClass(NAV_THIS);
		$div.parent().find("."+_this.style.itemThis).removeClass(_this.style.itemThis);
		$div.addClass(NAV_THIS);
		$div.addClass(_this.style.itemThis);
		// 将该节点的父节点全部展开
		var $li_parents = $div.parents("."+LI_NAV_ITEM);
		$li_parents.children("ul").addClass(NAV_SHOW);
		$li_parents.children("."+LI_DIV_ITEM).children("i[data-spread]."+_this.ficonClose).addClass(_this.ficonOpen);
		$li_parents.children("."+LI_DIV_ITEM).children("i[data-spread]."+_this.ficonClose).removeClass(_this.ficonClose);
		$li_parents.children("."+LI_DIV_ITEM).children("i[data-spread]."+_this.nodeIconClose).addClass(_this.nodeIconOpen);
		$li_parents.children("."+LI_DIV_ITEM).children("i[data-spread]."+_this.nodeIconClose).removeClass(_this.nodeIconClose);
		return _this.getNowParam();
	};
	
	/******************** 基础事件区域 ********************/
	// 展开或隐藏节点  作用点： div
	DTree.prototype.clickSpread = function($div) {	
		var $i_spread = $div.find("i[data-spread]").eq(0),
			$i_node = $div.find("i[data-spread]").eq(1),
			i_node_class = $i_node.attr("class"),
			$cite = $div.find("cite[data-leaf]").eq(0),
			spread = $i_spread.attr("data-spread"),
			$ul = $div.next("ul");
		var _this = this;
		
		if ($ul.length > 0) {
			if (spread == "close") {
				if (_this.type=="load") {	//增加加载
					if (_this.cache) {	//开启缓存
						if ($ul.html()) {
							$ul.addClass(NAV_SHOW);
						} else {	//加载节点
							_this.getChild($div);
						}
					}else {	//每次取新的数据
						$ul.html("");
						_this.getChild($div);
					}
				} else {	// 全量加载
					$ul.addClass(NAV_SHOW);
				}
				$div.find("i[data-spread]").attr("data-spread","open");
				$i_spread.removeClass(_this.ficonClose);
				$i_spread.addClass(_this.ficonOpen);
				
				var node_class = _this.nodeIconClose;
				if(i_node_class.indexOf(node_class) > 0){
					$i_node.removeClass(_this.nodeIconClose);
					$i_node.addClass(_this.nodeIconOpen);
				}
				
			} else if (spread == "open") {
				$ul.removeClass(NAV_SHOW);
				$div.find("i[data-spread]").attr("data-spread","close");
				$i_spread.removeClass(_this.ficonOpen);
				$i_spread.addClass(_this.ficonClose);
				
				var node_class = _this.nodeIconOpen;
				if(i_node_class.indexOf(node_class) > 0){
					$i_node.removeClass(_this.nodeIconOpen);
					$i_node.addClass(_this.nodeIconClose);
				}
			} 
		}
	};
	
	// 数据格式化
	DTree.prototype.escape = function(html){
		return event.escape(html);
	};
	
	// 格式化数据转回正常数据
	DTree.prototype.unescape = function(str){
		return event.unescape(str);
	};
	
	
	/******************** 菜单栏区域 ********************/
	// 开启菜单栏
	DTree.prototype.openMenubar = function(){
		var _this = this;
		if (_this.menubar) {	//开启菜单栏
			
			var menubarDiv = _this.getMenubarDom();
			_this.obj.before(menubarDiv);
		}
	};
	
	// 获取菜单栏
	DTree.prototype.getMenubarDom = function(){	
		var _this = this;
		var i_move_down = "<button class='layui-btn layui-btn-sm layui-btn-primary' d-click='"+eventName.moveDown+"' title='展开节点'><i class='"+ICONFONT+" "+LI_DIV_MENUBAR_DOWN+"'></i></button>";
		var i_move_up = "<button class='layui-btn layui-btn-sm layui-btn-primary' d-click='"+eventName.moveUp+"' title='收缩节点'><i class='"+ICONFONT+" "+LI_DIV_MENUBAR_UP+"'></i></button>";
		var i_refresh = "<button class='layui-btn layui-btn-sm layui-btn-primary' d-click='"+eventName.refresh+"' title='刷新'><i class='"+ICONFONT+" "+LI_DIV_MENUBAR_REFRESH+"'></i></button>";
		var i_remove = (_this.checkbar) ? "<button class='layui-btn layui-btn-sm layui-btn-primary' d-click='"+eventName.remove+"' title='删除选中节点'><i class='"+ICONFONT+" "+LI_DIV_MENUBAR_DELETE+"'></i></button>" : "";
		var i_discover = "<button class='layui-btn layui-btn-sm layui-btn-primary' d-click='"+eventName.searchNode+"' title='查询节点'><i class='"+ICONFONT+" "+LI_DIV_MENUBAR_SEARCH+"'></i></button>";
		var div = ["<div class='"+LI_DIV_MENUBAR+"' id='dtree_menubar_"+_this.obj[0].id+"'><div class='layui-btn-group'>",
		           i_move_down,
		           i_move_up,
		           i_refresh,
		           i_remove,
		           i_discover, "</div></div>"].join("");
		
		return div;
	};
	
	// 展开所有节点
	DTree.prototype.openAllNode = function(obj){	
		var _this = this;
	
		var $ulNode = obj || _this.obj.children("li").children("ul");
		
		// 遍历所有ul子节点
		for (var i = 0; i < $ulNode.length; i++) {
			// 获取当前节点的信息
			var $ul = $($ulNode[i]),
				$div = $ul.prev("div"),
				$i_spread = $div.find("i[data-spread]").eq(0),
				$i_node = $div.find("i[data-spread]").eq(1),
				i_node_class = $i_node.attr("class"),
				$cite = $div.find("cite[data-leaf]").eq(0),
				spread = $i_spread.attr("data-spread"),
				leaf = $cite.attr("data-leaf");
			
			if (leaf == "leaf") {
				// 说明是叶子了，则继续循环下一个
				continue;		
			}
			
			if (spread == "open") {
				// 说明该节点已经展开了，则进行子节点循环
			} else {
				if (_this.type=="load") {	//是否全量加载
					if (_this.cache) {	//是否开启缓存
						if ($ul.html()) {
							$ul.addClass(NAV_SHOW);
						} else {	//加载节点
							_this.getChild($div);
						}
					}else {	//每次取新的数据
						$ul.html("");
						_this.getChild($div);
					}
				} else {	// 全量加载
					$ul.addClass(NAV_SHOW);
				}
				$div.find("i[data-spread]").attr("data-spread","open");
				$i_spread.removeClass(_this.ficonClose);
				$i_spread.addClass(_this.ficonOpen);
				
				var node_class = _this.nodeIconClose;
				if(i_node_class.indexOf(node_class) > 0){
					$i_node.removeClass(_this.nodeIconClose);
					$i_node.addClass(_this.nodeIconOpen);
				}
			}
			
			var $childUl = $ul.children("li").children("ul");
			_this.openAllNode($childUl);
		}
	};
	
	//收缩所有节点
	DTree.prototype.closeAllNode = function(){	
		var _this = this;
		$("."+LI_NAV_CHILD).each(function(){
			// 获取当前节点的信息
			var $ul = $(this),
				$div = $ul.prev("div"),
				$i_spread = $div.find("i[data-spread]").eq(0),
				$i_node = $div.find("i[data-spread]").eq(1),
				i_node_class = $i_node.attr("class"),
				$cite = $div.find("cite[data-leaf]").eq(0),
				spread = $i_spread.attr("data-spread"),
				leaf = $cite.attr("data-leaf");
			
			$ul.removeClass(NAV_SHOW);
			$div.find("i[data-spread]").attr("data-spread","close");
			$i_spread.removeClass(_this.ficonOpen);
			$i_spread.addClass(_this.ficonClose);
			
			var node_class = _this.nodeIconOpen;
			if(i_node_class.indexOf(node_class) > 0){
				$i_node.removeClass(_this.nodeIconOpen);
				$i_node.addClass(_this.nodeIconClose);
			}
		});
	};
	
	//模糊查询该值，展开该值节点
	DTree.prototype.searchNode = function(value){	
		var _this = this;
		var b = false;
		var $lis = [];
		_this.obj.find("cite[data-leaf]").each(function(){
			var $nthis = $(this);
			var html = $nthis.html();
			if(html.indexOf(value) > -1){
				if($nthis.attr("data-leaf") == "leaf") {
					// 叶子节点提供包含父节点的所有信息
					var title = "";
					$nthis.parents("li").each(function(){
						title = "-" + $(this).find("cite[data-leaf]").html() + title;
					});
					title = title.substring(1, title.length);
					$nthis.attr("title", title);
				}
				// 保存当前cite所在的li及父li中包含该值，则只保留父的
				var i = 0;
				$nthis.parents("li").each(function(){
					var html2 = $(this).find("cite[data-leaf]").html();
					if(html2.indexOf(value) > -1){
						i++;
					}
					if(i >= 2){
						return true;
					}
				});
				if (i < 2){
					$lis.push($nthis.closest("li").prop("outerHTML"));
				}
			}
		});
		if($lis.length > 0) {
			b = true;
			// 1.将树节点清空
			_this.obj.html("");
			// 2.遍历所有cite节点，展开当前cite节点
			for(var i=0; i<$lis.length; i++){
				_this.obj.append($lis[i]);
			}
		}
		return b;
	};
	
	/******************** 工具栏区域 ********************/
	// 开启工具栏
	DTree.prototype.openToolbar = function(){
		var _this = this;
		if (_this.toolbar) {	//开启工具栏
			
			var toolbarDiv = _this.getToolbarDom();
			_this.obj.before(toolbarDiv);
		}
	};
	
	// 获取工具栏
	DTree.prototype.getToolbarDom = function(){	
		var _this = this;
		var toolbarShow = _this.toolbarShow;
		var toolbarExt = _this.toolbarExt;
		var dd = "";
		var div = "";
		if(toolbarShow.length > 0){
			for(var i=0; i<toolbarShow.length; i++){
				var show = toolbarShow[i];
				if(show == "add"){
					dd += "<dd><a d-click='"+eventName.addToolbar+"'><i class='"+ICONFONT+" "+LI_DIV_TOOLBAR_ADD+"'></i>新增"+_this.toolbarStyle.title+"</a></dd>";
				}
				if(show == "edit"){
					dd += "<dd><a d-click='"+eventName.editToolbar+"'><i class='"+ICONFONT+" "+LI_DIV_TOOLBAR_EDIT+"'></i>编辑"+_this.toolbarStyle.title+"</a></dd>";
				}
				if(show == "delete"){
					dd += "<dd><a d-click='"+eventName.delToolbar+"'><i class='"+ICONFONT+" "+LI_DIV_TOOLBAR_DEL+"'></i>删除"+_this.toolbarStyle.title+"</a></dd>";
				}
			}
		}
		if(toolbarExt.length > 0){
			for(var i=0; i<toolbarExt.length; i++){
				var ext = toolbarExt[i];
				dd += "<dd><a d-click='"+eventName.extToolbar+"' d-toolbarId='"+ext.toolbarId+"'><i class='"+ICONFONT+" "+ext.icon+"'></i>"+ext.title+"</a></dd>";
			}
		}
		if(dd.length > 0){
			div = ["<div class='"+LI_DIV_TOOLBAR+" layui-nav' id='dtree_toolbar_"+_this.obj[0].id+"'><div class='layui-nav-item'>",
		           "<dl class='layui-nav-child layui-anim'>",dd,
		           "</dl></div></div>"].join("");
		}
		return div;
	};
	
	// 加载toolBar中的内容
	DTree.prototype.loadToolBar = function(title, name){	
		var _this = this;
		var toolbarShow = _this.toolbarShow;
		var nodeBarContents = _this.toolbarBtn;
		var html = "";
		switch (name) {
			case eventName.addToolbar:
	
				//1. 必须加载的节点内容
				var nowNode = ['<div class="layui-form-item">',
								'<label class="layui-form-label">当前选中：</label>',
								'<div class="layui-input-block f-input-par">',
									'<input type="text" name="nodeTitle" class="layui-input f-input" value="'+title+'" readonly/>',
								'</div>',
							'</div>'].join('');
				
				var addNodeName = ['<div class="layui-form-item">',
				               '<label class="layui-form-label">新增'+_this.toolbarStyle.title+'：</label>',
				               '<div class="layui-input-block f-input-par">',
				               '<input type="text" name="addNodeName" class="layui-input f-input-next-clear f-input" value="" lay-verify="required"/>',
				               '</div>',
				               '</div>'].join('');
	
				var addNodeBtn = ['<div class="layui-form-item">',
					               '<div class="layui-input-block" style="margin-left:0px;text-align:center;">',
					               '<button type="button" class="layui-btn layui-btn-normal btn-w100" lay-submit lay-filter="dtree_addNode_form">确认添加</button>',
					               '</div>',
					               '</div>'].join('');
				//2. 用户自定义的节点内容
				var addNodeBar = ['<div class="nav-tree-toolbar-div-tool"><form class="layui-form layui-form-pane" lay-filter="dtree_addNode_form">', nowNode, addNodeName];
				if(nodeBarContents != null && nodeBarContents.length > 0){
					if(nodeBarContents[0] != null && nodeBarContents[0] != undefined && nodeBarContents[0].length > 0){
						var addNodeBarContents = nodeBarContents[0];
						
						for(var j=0; j<addNodeBarContents.length; j++){
							var type = addNodeBarContents[j].type;
							if(!type){type = "text";}
							switch (type) {
								case "text":
									var textNode = ['<div class="layui-form-item">',
										               '<label class="layui-form-label" title="'+addNodeBarContents[j].label+'">'+addNodeBarContents[j].label+'：</label>',
										               '<div class="layui-input-block f-input-par">',
										               '<input type="text" name="'+addNodeBarContents[j].name+'" class="layui-input f-input-next-clear f-input" value="'+(addNodeBarContents[j].value ? addNodeBarContents[j].value : "")+'"/>',
										               '</div>',
										               '</div>'].join('');
									addNodeBar.push(textNode);
									break;
								case "textarea":
									var textareaNode = ['<div class="layui-form-item layui-form-text">',
										               '<label class="layui-form-label">'+addNodeBarContents[j].label+'：</label>',
										               '<div class="layui-input-block f-input-par">',
										               '<textarea name="'+addNodeBarContents[j].name+'" class="layui-textarea f-input">'+(addNodeBarContents[j].value ? addNodeBarContents[j].value : "")+'</textarea>',
										               '</div>',
										               '</div>'].join('');
									addNodeBar.push(textareaNode);
									break;
								case "select":
									var optionsData = addNodeBarContents[i].optionsData;
									var options = "";
									var defaultValue = addNodeBarContents[j].value ? addNodeBarContents[j].value : "";
									for(var key in optionsData){
										if(defaultValue == optionsData[key]){
											options += "<option value='"+key+"' selected>"+optionsData[key]+"</option>";
										} else {
											options += "<option value='"+key+"'>"+optionsData[key]+"</option>";
										}
									}
									var selectNode = ['<div class="layui-form-item">',
								                       		'<label class="layui-form-label" title="'+addNodeBarContents[j].label+'">'+addNodeBarContents[j].label+'：</label>',
							                       			'<div class="layui-input-block f-input-par">',
							                       				'<select name="'+addNodeBarContents[j].name+'">',
							                       				options,
							                       				'</select>',
				                       						'</div>',
				                   						'</div>'].join('');	
									addNodeBar.push(selectNode);
									break;
								case "hidden":
									var hiddenNode = ['<input type="hidden" name="'+addNodeBarContents[j].name+'" value="'+(addNodeBarContents[j].value ? addNodeBarContents[j].value : "")+'"/>'].join('');
									addNodeBar.push(selectNode);
									break;
									
							}
						}
					}
				}
				addNodeBar.push(addNodeBtn);
				addNodeBar.push('</form></div>');
				html = addNodeBar.join('');
				break;

			case eventName.editToolbar:

				//1. 必须加载的节点内容
				var nowNode = ['<div class="layui-form-item">',
								'<label class="layui-form-label">当前选中：</label>',
								'<div class="layui-input-block f-input-par">',
									'<input type="text" name="nodeTitle" class="layui-input f-input" value="'+title+'" readonly/>',
								'</div>',
							'</div>'].join('');
				
				var editNodeName = ['<div class="layui-form-item">',
					                   '<label class="layui-form-label">编辑'+_this.toolbarStyle.title+'：</label>',
					                   '<div class="layui-input-block f-input-par">',
					                   '<input type="text" name="editNodeName" class="layui-input f-input-next-clear f-input" value="'+title+'" lay-verify="required"/>',
					                   '</div>',
					                   '</div>'].join('');
					
					
				var editNodeBtn = ['<div class="layui-form-item">',
				                  '<div class="layui-input-block" style="margin-left:0px;text-align:center;">',
				                  '<button type="button" class="layui-btn layui-btn-normal btn-w100" lay-submit lay-filter="dtree_editNode_form">确认编辑</button>',
				                  '</div>',
				                  '</div>'].join('');
				
				var editNodeBar = ['<div class="nav-tree-toolbar-div-tool"><form class="layui-form layui-form-pane" lay-filter="dtree_editNode_form">', nowNode, editNodeName];
				//2. 用户自定义的节点内容
				if(nodeBarContents != null && nodeBarContents.length > 0){
					
					if(nodeBarContents[1] != null && nodeBarContents[1] != undefined && nodeBarContents[1].length > 0){
						var editNodeBarContents = nodeBarContents[1];
						
						for(var j=0; j<editNodeBarContents.length; j++){
							var type = editNodeBarContents[j].type;
							if(!type){type = "text";}
							switch (type) {
								case "text":
									var textNode = ['<div class="layui-form-item">',
										               '<label class="layui-form-label" title="'+editNodeBarContents[j].label+'">'+editNodeBarContents[j].label+'：</label>',
										               '<div class="layui-input-block f-input-par">',
										               '<input type="text" name="'+editNodeBarContents[j].name+'" class="layui-input f-input-next-clear f-input" value="'+(editNodeBarContents[j].value ? editNodeBarContents[j].value : "")+'"/>',
										               '</div>',
										               '</div>'].join('');
									editNodeBar.push(textNode);
									break;
								case "textarea":
									var textareaNode = ['<div class="layui-form-item layui-form-text">',
										               '<label class="layui-form-label">'+editNodeBarContents[j].label+'：</label>',
										               '<div class="layui-input-block f-input-par">',
										               '<textarea name="'+editNodeBarContents[j].name+'" class="layui-textarea f-input">'+(editNodeBarContents[j].value ? editNodeBarContents[j].value : "")+'</textarea>',
										               '</div>',
										               '</div>'].join('');
									editNodeBar.push(textareaNode);
									break;
								case "select":
									var optionsData = editNodeBarContents[j].optionsData;
									var options = "";
									var defaultValue = editNodeBarContents[j].value ? editNodeBarContents[j].value : "";
									for(var key in optionsData){
										if(defaultValue == optionsData[key]){
											options += "<option value='"+key+"' selected>"+optionsData[key]+"</option>";
										} else {
											options += "<option value='"+key+"'>"+optionsData[key]+"</option>";
										}
									}
									var selectNode = ['<div class="layui-form-item">',
								                       		'<label class="layui-form-label" title="'+editNodeBarContents[j].label+'">'+editNodeBarContents[j].label+'：</label>',
							                       			'<div class="layui-input-block f-input-par">',
							                       				'<select name="'+editNodeBarContents[j].name+'">',
							                       				options,
							                       				'</select>',
				                       						'</div>',
				                   						'</div>'].join('');	
									editNodeBar.push(selectNode);
									break;
								case "hidden":
									var hiddenNode = ['<input type="hidden" name="'+editNodeBarContents[j].name+'" class="layui-input f-input-next-clear f-input" value="'+(editNodeBarContents[j].value ? editNodeBarContents[j].value : "")+'"/>'].join('');
									editNodeBar.push(hiddenNode);
									break;
							}
						}
					}
				}
				
				editNodeBar.push(editNodeBtn);
				editNodeBar.push('</form></div>');
				html = editNodeBar.join('');
				break;
		}
		return html;
	};
	
	// 新增节点后改变节点内容
	DTree.prototype.changeTreeNodeAdd = function(returnID){
		var _this = this;
		var temp = _this.temp;
		var id = temp[0],
			$ul = temp[1],
			$div = temp[2];
		if(returnID){
			var $thisDiv = _this.obj.find("[data-id='"+id+"']");
			if(typeof returnID === "object"){
				// 如果是JSON格式数据，则将当前DIV删除，重新建造DIV
				$thisDiv.remove();
				var treeId = returnID[_this.response.treeId],
					pId = returnID[_this.response.parentId],
					title1 = returnID[_this.response.title],
					isLast = returnID[_this.response.isLast],
					checkArr = returnID[_this.response.checkArr],
					level1 = returnID[_this.response.level],
					iconClass = returnID[_this.response.iconClass],
					basicData = returnID[_this.response.basicData];
				if(basicData) {
					basicData = event.escape(JSON.stringify(basicData));
				}else{
					basicData = "";
				}
				
				if(treeId){
					dom = _this.getDom(treeId, pId, title1, isLast, iconClass, checkArr, level1);
					$ul.append(_this.getLiItemDom(dom, treeId, pId, title1, level1, basicData, "item"));
					// 建造完毕后，选中该DIV
					var $addDiv = $ul.find("div[data-id='"+returnID.id+"']");
					_this.setNodeParam($addDiv)
				} else {
					layer.msg("添加失败,节点ID为undefined！",{icon:5});
					// 将li节点删除
			    	$ul.find("li[data-id='"+id+"']").remove();
			    	// 重新赋值
		    		_this.setNodeParam($div);
		    		// 临时变量制空
		    		_this.temp = []; 
		    		return ;
				}
			}else if(typeof returnID === "string" || typeof this.icon === 'number'){
				$thisDiv.attr("data-id", returnID);
				// 将li节点展示
				$ul.find("li[data-id='"+returnID+"']").show();
				var $addDiv = $ul.find("div[data-id='"+returnID+"']");
				_this.setNodeParam($addDiv)
			}
			
			// 判断当前点击的节点是否是最后一级节点，如果是，则需要修改节点的样式
			var $icon_i = $div.find("i[data-spread]");
			if ($icon_i.eq(0).attr("data-spread") == "last") {
				$icon_i.attr("data-spread","open");
				$icon_i.eq(0).removeClass(LI_DIV_SPREAD_LAST);
				$icon_i.eq(0).removeClass(ICON_HIDE);
				$icon_i.eq(0).addClass(_this.ficonOpen);
				$icon_i.eq(1).removeClass(leafIconArray[_this.leafIcon]);
				$icon_i.eq(1).addClass(_this.nodeIconOpen);
			} else {	//如果不是，也要修改节点样式
				$icon_i.attr("data-spread","open");
				$icon_i.eq(0).removeClass(_this.ficonClose);
				$icon_i.eq(0).addClass(_this.ficonOpen);
				$icon_i.eq(1).removeClass(_this.nodeIconClose);
				$icon_i.eq(1).addClass(_this.nodeIconOpen);
			}
			$ul.addClass(NAV_SHOW);	//展开UL
		} else {
			// 将li节点删除
	    	$ul.find("li[data-id='"+id+"']").remove();
	    	// 重新赋值
    		_this.setNodeParam($div);
		}
		
		_this.temp = []; // 临时变量制空
		
	}
	
	// 修改节点后改变节点内容
	DTree.prototype.changeTreeNodeEdit = function(flag){
		var _this = this;
		var temp = _this.temp;
		var $cite = temp[0],
			$div = temp[1];
		
		if(!flag){
			$cite.html(title);
			node = _this.getNodeParam($div);
		}
		
		_this.temp = []; // 临时变量制空
	}
	
	// 编辑页打开后显示编辑页内容
	DTree.prototype.changeTreeNodeDone = function(param){
		var _this = this;
		form.val('dtree_editNode_form', param);
		form.render();
	}
	
	// 删除节点后改变节点内容
	DTree.prototype.changeTreeNodeDel = function(flag){
		var _this = this;
		var temp = _this.temp;
		var $p_li = temp[0],
			$p_ul = $p_li.parent("ul"),
			$p_div = temp[1];
		
		if(flag){
			$p_li.remove();
			// 判断父级ul中是否还存在li,如果不存在，则需要修改节点的样式
			if($p_ul.children("li").length == 0){
				var $icon_i = $p_div.find("i[data-spread]");
				$icon_i.attr("data-spread","last");
				$icon_i.eq(0).removeClass(_this.ficonOpen);
				$icon_i.eq(0).removeClass(_this.ficonClose);
				if(!_this.dot){$icon_i.eq(0).addClass(ICON_HIDE);}
				$icon_i.eq(0).addClass(LI_DIV_SPREAD_LAST);
				
				$icon_i.eq(1).removeClass(_this.nodeIconOpen);
				$icon_i.eq(1).removeClass(_this.nodeIconClose);
				$icon_i.eq(1).addClass(leafIconArray[_this.leafIcon]);
			}
			_this.initNodeParam();
		}
		
		_this.temp = []; // 临时变量制空
	}
	
	
	/******************** 复选框区域 ********************/
	// 初始化复选框的值
	DTree.prototype.chooseDataInit = function(chooseIds){	
		var _this = this;
		var chooseId = chooseIds.split(",");
		for (var i=0; i<chooseId.length; i++) {
			_this.obj.find("i[d-click='"+eventName.checkNodeClick+"']").each(function(){
				if ($(this).attr("data-id") == chooseId[i]) {
					_this.checkStatus($(this)).check();
				}
			});
		}
		// 展开选中节点的父节点
		var $li_parents = _this.obj.find("i[d-click='"+eventName.checkNodeClick+"'][data-checked='1']").parents("."+LI_NAV_ITEM);
		$li_parents.children("ul").addClass(NAV_SHOW);
		$li_parents.children("."+LI_DIV_ITEM).children("i[data-spread]."+_this.ficonClose).addClass(_this.ficonOpen);
		$li_parents.children("."+LI_DIV_ITEM).children("i[data-spread]."+_this.ficonClose).removeClass(_this.ficonClose);
		$li_parents.children("."+LI_DIV_ITEM).children("i[data-spread]."+_this.nodeIconClose).addClass(_this.nodeIconOpen);
		$li_parents.children("."+LI_DIV_ITEM).children("i[data-spread]."+_this.nodeIconClose).removeClass(_this.nodeIconClose);
		return _this.getCheckbarNodesParam();
	};
	
	//实现复选框点击，子集选中父级也选中
	DTree.prototype.checkAllOrNot =  function($i) {	
		var _this = this;
		//$i 当前点击的checkbox
		var dataPar = $i.attr("data-par"),
			dataType = $i.attr("data-type"),
			$li = $i.closest(dataPar),		//当前checkbox的上级li节点
			$parent_li = $i.parents(dataPar),		//当前checkbox的所有父级li节点
			$child_li = $li.find(dataPar);	//当前checkbox的上级li节点下的所有子级li节点
		
		if ($i.attr("data-checked") == "1") {
			// 处理当前节点的选中状态
			_this.checkStatus($i).noCheck();
			
			// 处理子级节点的选中状态
			var $child_li_i = $child_li.find(">."+LI_DIV_ITEM+">."+LI_DIV_CHECKBAR+">i[data-type='"+dataType+"']");
			 _this.checkStatus($child_li_i).noCheck();
			
			// 处理父级节点的选中状态
			for (var i = 1, item = $parent_li; i < item.length; i++) {
				var flag = item.eq(i).find(">."+LI_NAV_CHILD+" ."+LI_DIV_CHECKBAR+">i[data-type='"+dataType+"'][data-checked='1']").length;
				if (flag == 0) {	
					//把父级去掉选中
					var $item_i = item.eq(i).find(">."+LI_DIV_ITEM+">."+LI_DIV_CHECKBAR+">i[data-type='"+dataType+"']");
					_this.checkStatus($item_i).noCheck();
				}
			}
		} else {
			// 处理当前节点的选中状态
			_this.checkStatus($i).check();
			
			// 处理子级节点的选中状态
			var $child_li_i = $child_li.find(">."+LI_DIV_ITEM+">."+LI_DIV_CHECKBAR+">i[data-type='"+dataType+"']");
			_this.checkStatus($child_li_i).check();
			
			// 处理父级节点的选中状态
			for (var i = 1, item = $parent_li; i < item.length; i++) {
				var $item_i = item.eq(i).find(">."+LI_DIV_ITEM+">."+LI_DIV_CHECKBAR+">i[data-type='"+dataType+"']");
				//把父级选中
				_this.checkStatus($item_i).check();
			}
		}
	};

	//实现复选框点击， no-all 子集选中父级半选中，子集全选父级选中
	DTree.prototype.checkAllOrNoallOrNot =  function($i) {	
		var _this = this;
		//$i 当前点击的checkbox
		var $div = $i.closest("."+LI_DIV_ITEM),	
			dataPar = $i.attr("data-par"),
			dataType = $i.attr("data-type"),
			$li = $i.closest(dataPar),		//当前checkbox的上级li节点
			$parent_li = $i.parents(dataPar),		//当前checkbox的所有父级li节点
			$child_li = $li.find(dataPar);	//当前checkbox的上级li节点下的所有子级li节点
		
		if ($i.attr("data-checked") == "1") {	//当前复选框为选中状态，点击后变为未选中状态
			// 处理当前节点的选中状态
			_this.checkStatus($i).noCheck();
			
			// 处理子级节点的选中状态
			var $child_li_i = $child_li.find(">."+LI_DIV_ITEM+">."+LI_DIV_CHECKBAR+">i[data-type='"+dataType+"']");
			_this.checkStatus($child_li_i).noCheck();
			
			// 处理父级节点的选中状态
			for (var i = 1, item = $parent_li; i < item.length; i++) {
				var flag = item.eq(i).find(">."+LI_NAV_CHILD+" ."+LI_DIV_CHECKBAR+">i[data-type='"+dataType+"'][data-checked='1']").length;
				var $item_i = item.eq(i).find(">."+LI_DIV_ITEM+">."+LI_DIV_CHECKBAR+">i[data-type='"+dataType+"']");
				if (flag == 0) {	
					//把父级去掉选中
					_this.checkStatus($item_i).noCheck();
				} else {
					//把父级半选
					_this.checkStatus($item_i).noallCheck();
				}
			}
		} else {		//当前复选框为未选中状态，点击后变为选中状态
			// 处理当前节点的选中状态
			_this.checkStatus($i).check();
			
			// 处理子级节点的选中状态
			var $child_li_i = $child_li.find(">."+LI_DIV_ITEM+">."+LI_DIV_CHECKBAR+">i[data-type='"+dataType+"']");
			_this.checkStatus($child_li_i).check();
			
			// 处理父级节点的选中状态
			for (var i = 1, item = $parent_li; i < item.length; i++) {
				var flag1 = item.eq(i).find(">."+LI_NAV_CHILD+" ."+LI_DIV_CHECKBAR+">i[data-type='"+dataType+"'][data-checked='1']").length;
				var flag2 = item.eq(i).find(">."+LI_NAV_CHILD+" ."+LI_DIV_CHECKBAR+">i[data-type='"+dataType+"']").length;
				var $item_i = item.eq(i).find(">."+LI_DIV_ITEM+">."+LI_DIV_CHECKBAR+">i[data-type='"+dataType+"']");
				if (flag1 != flag2) {
					// 父级复选框半选
					_this.checkStatus($item_i).noallCheck();
				} else {
					// 父级复选框全选
					_this.checkStatus($item_i).check();
				}
			}
		}
	};
	
	//实现复选框点击，p-casc：父级选中子集全选，子集无法改变父级选中状态
	DTree.prototype.checkAllOrPcascOrNot = function($i) {	
		var _this = this;
		//$i 当前点击的checkbox
		var $div = $i.closest("."+LI_DIV_ITEM),	
		dataPar = $i.attr("data-par"),
		dataType = $i.attr("data-type"),
		$li = $i.closest(dataPar),		//当前checkbox的上级li节点
		$parent_li = $i.parents(dataPar),		//当前checkbox的所有父级li节点
		$child_li = $li.find(dataPar);	//当前checkbox的上级li节点下的所有子级li节点
		
		if ($i.attr("data-checked") == "1") {	//当前复选框为选中状态，点击后变为未选中状态
			// 处理当前节点的选中状态
			_this.checkStatus($i).noCheck();
			
			// 处理子级节点的选中状态
			var $child_li_i = $child_li.find(">."+LI_DIV_ITEM+">."+LI_DIV_CHECKBAR+">i[data-type='"+dataType+"']");
			_this.checkStatus($child_li_i).noCheck();
			
		} else {		//当前复选框为未选中状态，点击后变为选中状态
			// 处理当前节点的选中状态
			_this.checkStatus($i).check();
			
			// 处理子级节点的选中状态
			var $child_li_i = $child_li.find(">."+LI_DIV_ITEM+">."+LI_DIV_CHECKBAR+">i[data-type='"+dataType+"']");
			_this.checkStatus($child_li_i).check();
		}
	};
	
	//实现复选框点击，self：各自选中互不影响
	DTree.prototype.checkOrNot = function($i) {	
		var _this = this;
		//$i 当前点击的checkbox
		var $div = $i.closest("."+LI_DIV_ITEM),	
		dataPar = $i.attr("data-par"),
		dataType = $i.attr("data-type"),
		$li = $i.closest(dataPar),		//当前checkbox的上级li节点
		$parent_li = $i.parents(dataPar),		//当前checkbox的所有父级li节点
		$child_li = $li.find(dataPar);	//当前checkbox的上级li节点下的所有子级li节点
		
		if ($i.attr("data-checked") == "1") {	//当前复选框为选中状态，点击后变为未选中状态
			// 处理当前节点的选中状态
			_this.checkStatus($i).noCheck();
		} else {		//当前复选框为未选中状态，点击后变为选中状态
			// 处理当前节点的选中状态
			_this.checkStatus($i).check();
		}
	};
	
	//实现复选框点击
	DTree.prototype.changeCheck = function() {	
		var _this = this;
		var temp = _this.temp;
		var $i = temp[0];
		
		// 复选框选中事件
		if (_this.checkbarType == "all") {
			_this.checkAllOrNot($i);
		} else if(_this.checkbarType == "no-all") {
			_this.checkAllOrNoallOrNot($i);
		} else if(_this.checkbarType == "p-casc") {
			_this.checkAllOrPcascOrNot($i);
		} else if(_this.checkbarType == "self") {
			_this.checkOrNot($i);
		} else {
			_this.checkAllOrNot($i);
		}
		
		// 获取复选框选中节点的内容
		var checkbarNodes = _this.setAndGetCheckbarNodesParam();
		
		// 用户自定义想做的事情
		_this.checkbarFun.chooseDone(checkbarNodes);
		layui.event.call(this, MOD_NAME, "chooseDone("+$(_this.obj)[0].id+")", checkbarNodes);
		_this.temp = [];
	};
	
	//复选框半选状态初始化设置
	DTree.prototype.initNoAllCheck = function(){		
		var _this = this;
		//1.获取所有选中节点
		var $is = _this.obj.find("i[data-checked='1']");
		if($is.length > 0){
			for ( var key = 0; key < $is.length; key++) {
				var $i = $($is[key]),
					dataPar = $i.attr("data-par"),
					dataType = $i.attr("data-type"),
					$li = $i.closest(dataPar),		//当前checkbox的上级li节点
					$parent_li = $i.parents(dataPar),		//当前checkbox的所有父级li节点
					$child_li = $li.find(dataPar);	//当前checkbox的上级li节点下的所有子级li节点
				
				// 处理父级节点的选中状态
				for (var i = 1, item = $parent_li; i < item.length; i++) {
					var flag1 = item.eq(i).find(">."+LI_NAV_CHILD+" ."+LI_DIV_CHECKBAR+">i[data-type='"+dataType+"'][data-checked='1']").length;
					var flag2 = item.eq(i).find(">."+LI_NAV_CHILD+" ."+LI_DIV_CHECKBAR+">i[data-type='"+dataType+"']").length;
					var $item_i = item.eq(i).find(">."+LI_DIV_ITEM+">."+LI_DIV_CHECKBAR+">i[data-type='"+dataType+"']");
					if (flag1 != flag2) {
						// 父级复选框半选
						_this.checkStatus($item_i).noallCheck();
					} else {
						// 父级复选框全选
						_this.checkStatus($item_i).check();
					}
				}
			}
		}
	};
	
	//复选框选中状态初始化设置
	DTree.prototype.initAllCheck = function(){		
		var _this = this;
		//1.获取所有选中节点
		var $is = _this.obj.find("i[data-checked='1']");
		if($is.length > 0){
			for ( var key = 0; key < $is.length; key++) {
				var $i = $($is[key]),
				dataPar = $i.attr("data-par"),
				dataType = $i.attr("data-type"),
				$li = $i.closest(dataPar),		//当前checkbox的上级li节点
				$parent_li = $i.parents(dataPar),		//当前checkbox的所有父级li节点
				$child_li = $li.find(dataPar);	//当前checkbox的上级li节点下的所有子级li节点
				
				// 处理父级节点的选中状态
				for (var i = 1, item = $parent_li; i < item.length; i++) {
					var $item_i = item.eq(i).find(">."+LI_DIV_ITEM+">."+LI_DIV_CHECKBAR+">i[data-type='"+dataType+"']");
					// 父级复选框全选
					_this.checkStatus($item_i).check();
				}
			}
		}
	};
	
	// 设置复选框选中/未选中/半选  _this.checkStatus($i).check();  _this.checkStatus($i).noCheck();   _this.checkStatus($i).noallCheck();
	DTree.prototype.checkStatus = function($i) {
		var _this = this;
		return {
			check: function(){
				$i.removeClass(LI_DIV_CHECKBAR_OUT);
				$i.removeClass(LI_DIV_CHECKBAR_NOALL);
				$i.addClass(LI_DIV_CHECKBAR_ON);
				$i.addClass(_this.style.chs);
				$i.attr("data-checked","1");
			},
			noCheck: function(){
				$i.removeClass(LI_DIV_CHECKBAR_NOALL);
				$i.removeClass(LI_DIV_CHECKBAR_ON);
				$i.removeClass(_this.style.chs);
				$i.addClass(LI_DIV_CHECKBAR_OUT);
				$i.attr("data-checked","0");
			},
			noallCheck: function(){
				$i.removeClass(LI_DIV_CHECKBAR_OUT);
				$i.removeClass(LI_DIV_CHECKBAR_ON);
				$i.addClass(LI_DIV_CHECKBAR_NOALL);
				$i.addClass(_this.style.chs);
				$i.attr("data-checked","2");
			}
		}
	}
	
	// 设置树的复选框操作值的全部参数,并获取
	DTree.prototype.setAndGetCheckbarNodesParam = function() {
		var _this = this;
		//操作前先清空
		_this.checkbarNode = [];
		// 选择所有复选框节点
		if (_this.checkbarData == "change"){	//记录变更数据
			_this.obj.find("i[data-par]").each(function(){
				var $i = $(this);
					$div = $i.closest("."+LI_DIV_ITEM);
					
				if ($i.attr("data-checked") != $i.attr("data-initchecked")) {	
					_this.checkbarNode.push(_this.getRequestParam(_this.getTempNode($div, $i)));
				}
			});
		} else if (_this.checkbarData == "all"){	//记录全部数据
			_this.obj.find("i[data-par][data-checked]").each(function(){
				var $i = $(this);
					$div = $i.closest("."+LI_DIV_ITEM);
				_this.checkbarNode.push(_this.getRequestParam(_this.getTempNode($div, $i)));
			
			});
		} else {	//记录选中数据
			_this.obj.find("i[data-par][data-checked='1']").each(function(){
				var $i = $(this);
					$div = $i.closest("."+LI_DIV_ITEM);
				_this.checkbarNode.push(_this.getRequestParam(_this.getTempNode($div, $i)));
			
			});
		}
		return _this.checkbarNode;
	};
	
	// 获取树的复选框操作值的全部参数
	DTree.prototype.getCheckbarNodesParam = function() {
		var _this = this;
		return _this.setAndGetCheckbarNodesParam();
	};
	
	//判断复选框是否发生变更
	DTree.prototype.changeCheckbarNodes = function(){	
		var flag = false;
		var _this = this;
		_this.obj.find("i[data-par]").each(function(){
			var $i = $(this);
				$div = $i.closest("."+LI_DIV_ITEM);
				
			if ($i.attr("data-checked") != $i.attr("data-initchecked")) {	
				flag = true;
				return true;
			}
		});
		return flag;
	};
	
	
	/******************** iframe区域 ********************/
	// 加载iframe
	DTree.prototype.loadIframe = function($div, iframeParam) {	
    	var _this = this;
    	var $cite = $div.find("cite[data-leaf]").eq(0);
    	if (!_this.useIframe) {		// 启用iframe
			return false;
		}
    	var iframeElem = _this.iframe.iframeElem,
			iframeUrl = _this.iframe.iframeUrl,
			iframeLoad = _this.iframe.iframeLoad;
    	
    	var flag = iframeLoad == "leaf" ? (($cite.attr("data-leaf") == "leaf") ? true : false) : true;
		
		if (flag) {
			if ($(iframeElem).length > 0) {		//iframe存在
				if (!iframeUrl) {
					layer.msg("数据请求异常，iframeUrl参数未指定", {icon:5});
					return false;
				}
				var param = AjaxHelper.serialize(iframeParam);
				var url = iframeUrl + param;
				$(iframeElem).attr("src", url);
			} else {
				layer.msg("iframe绑定异常，请确认页面中是否有iframe页对应的容器", {icon:5});
				return false;
			}
		}
		return flag;
	};
	
	// 获取传递出去的参数，根据iframe.iframeDefaultRequest、iframe.iframeRequest和node拼出发出请求的参数
	DTree.prototype.getIframeRequestParam = function(nodes){
		var _this = this;
		var request = _this.iframe.iframeRequest,
			defaultRequestNames = _this.iframe.iframeDefaultRequest,
			node = nodes || _this.node,
			requestParam = {};
			
		// 先拼用户自定义的，在拼树生成的，这样的话用户可以自定义当树未生成时的节点的初始值
		for ( var key in request) {
			requestParam[key] = request[key];
		}
		for ( var key in defaultRequestNames) {
			var paramName = defaultRequestNames[key];
			var paramValue = node[key];
			if(typeof paramValue === "boolean"){
				requestParam[paramName] = paramValue;
			}else {
				if(paramValue){
					requestParam[paramName] = paramValue;
				}
			}
		}
		
		// 解决传递中文的乱码问题
		var reg = /[\u4E00-\u9FA5\uF900-\uFA2D]/;	//正则匹配中文
		for(var key in requestParam){
			if(reg.test(requestParam[key])) {
				var str = requestParam[key];
				requestParam[key] = encodeURI(encodeURI(str));
			}
		}
		
		return requestParam;
	};
	
	/******************** 数据回调区域 ********************/
	// 获取当前选中节点下一个UL 或根节点。为了将新节点放入ul下
	DTree.prototype.getNowNodeUl =  function() {
		var _this = this;
		return (_this.obj.find("div[data-id]").parent().find("."+NAV_THIS).length == 0) ? _this.obj : _this.obj.find("div[data-id]").parent().find("."+NAV_THIS).next("ul");
	};
	
	// 获取当前选中节点 或根节点。
	DTree.prototype.getNowNode =  function() {	
		var _this = this;
		return (_this.obj.find("div[data-id]").parent().find("."+NAV_THIS).length == 0) ? _this.obj : _this.obj.find("div[data-id]").parent().find("."+NAV_THIS);
	};
	
	// 设置当前选中节点的全部参数
	DTree.prototype.setNodeParam = function($div) {
		var _this = this;
			_this.node.nodeId = $div.attr("data-id"),
			_this.node.parentId = $div.parent().attr("data-pid"),
			_this.node.context = $div.find("cite[data-leaf]").eq(0).text(),
			_this.node.isLeaf = $div.find("cite[data-leaf]").eq(0).attr("data-leaf") == "leaf" ? true : false,
			_this.node.level = $div.parent().attr("data-index");
			_this.node.spread = $div.find("i[data-spread]").eq(0).attr("data-spread") == "open" ? true : false;
			_this.node.basicData = $div.attr("data-basic");
			if ($div.find("i[data-par]")) {
				var dataTypes = "", ischeckeds = "", initcheckeds = "";
				$div.find("i[data-par]").each(function(){
					dataTypes += $(this).attr("data-type") + ",";
					ischeckeds += $(this).attr("data-checked") + ",";
					initcheckeds += $(this).attr("data-initchecked") + ",";
				});
				dataTypes = dataTypes.substring(0, dataTypes.length-1);
				ischeckeds = ischeckeds.substring(0, ischeckeds.length-1);
				initcheckeds = initcheckeds.substring(0, initcheckeds.length-1);
				
				_this.node.dataType = dataTypes;
				_this.node.ischecked = ischeckeds;
				_this.node.initchecked = initcheckeds;
			}
	};
	
	// 获取当前选中节点的全部参数
	DTree.prototype.getNodeParam = function($div) {
		var _this = this;
		if ($div) {
			_this.setNodeParam($div);
		} else {
			if(_this.obj.find("div[data-id]").parent().find("."+NAV_THIS).length == 0){
				_this.initNodeParam();
			}
		}
		return this.node;
	};
	
	// 重置参数
	DTree.prototype.initNodeParam = function(){
		var _this = this;
		_this.node.nodeId = "",
		_this.node.parentId = "",
		_this.node.context = "",
		_this.node.isLeaf = "",
		_this.node.level = "";
		_this.node.spread = "",
		_this.node.dataType = "";
		_this.node.ischecked = "";
		_this.node.initchecked = "";
		_this.node.basicData = "";
	};
	
	// 获取传递出去的参数，根据defaultRequest、request和node拼出发出请求的参数
	DTree.prototype.getRequestParam = function(nodes){
		var _this = this;
		var request = _this.request,
			defaultRequestNames = _this.defaultRequest,
			node = nodes || _this.node,
			requestParam = {};
		
		// 先拼用户自定义的，在拼树生成的，这样的话用户可以自定义当树未生成时的节点的初始值
		for ( var key in request) {
			requestParam[key] = request[key];
		}
		for ( var key in defaultRequestNames) {
			var paramName = defaultRequestNames[key];
			var paramValue = node[key];
			if(typeof paramValue === "boolean"){
				requestParam[paramName] = paramValue;
			}else {
				if(paramValue){
					requestParam[paramName] = paramValue;
				}
			}
			
		}
		return requestParam;
	};
	
	// 获取当前选中值
	DTree.prototype.getNowParam = function(){
		var _this = this;
		return _this.getRequestParam(_this.getNodeParam());	
	};
	
	// 获取参数的上级节点
	DTree.prototype.getParentParam = function(id){		
		var _this = this;
		var pId = _this.obj.find("div[data-id='"+id+"']").parent().attr("data-pid");
		var $div = _this.obj.find("div[data-id='"+pId+"']");
		var nodes = {};
		if($div.length > 0){
			nodes.nodeId = $div.attr("data-id"),
			nodes.parentId = $div.parent().attr("data-pid"),
			nodes.context = $div.find("cite[data-leaf]").eq(0).text(),
			nodes.isLeaf = $div.find("cite[data-leaf]").eq(0).attr("data-leaf") == "leaf" ? true : false,
			nodes.level = $div.parent().attr("data-index");
			nodes.spread = $div.find("i[data-spread]").eq(0).attr("data-spread") == "open" ? true : false;
			nodes.basicData = $div.attr("data-basic");
			if ($div.find("i[data-par]")) {
				var dataTypes = "", ischeckeds = "", initcheckeds = "";
				$div.find("i[data-par]").each(function(){
					dataTypes += $(this).attr("data-type") + ",";
					ischeckeds += $(this).attr("data-checked") + ",";
					initcheckeds += $(this).attr("data-initchecked") + ",";
				});
				dataTypes = dataTypes.substring(0, dataTypes.length-1);
				ischeckeds = ischeckeds.substring(0, ischeckeds.length-1);
				initcheckeds = initcheckeds.substring(0, initcheckeds.length-1);
				
				nodes.dataType = dataTypes;
				nodes.ischecked = ischeckeds;
				nodes.initchecked = initcheckeds;
			}
			return _this.getRequestParam(nodes);	
		} else {
			return {};
		}
	};
	
	// 获取一个临时的node参数
	DTree.prototype.getTempNode = function($div, $i){		
		var _this = this;
		var temp_node = {};
		temp_node.nodeId = $div.attr("data-id"),
		temp_node.parentId = $div.parent().attr("data-pid"),
		temp_node.context = $div.find("cite[data-leaf]").eq(0).text(),
		temp_node.isLeaf = $div.find("cite[data-leaf]").eq(0).attr("data-leaf") == "leaf" ? true : false,
		temp_node.level = $div.parent().attr("data-index");
		temp_node.spread = $div.find("i[data-spread]").eq(0).attr("data-spread") == "open" ? true : false;
		temp_node.basicData = $div.attr("data-basic");
		temp_node.dataType = $i.attr("data-type");
		temp_node.ischecked = $i.attr("data-checked");
		temp_node.initchecked = $i.attr("data-initchecked");
		return temp_node;
	};
	
	
	
	/******************** 事件回调区域 ********************/
	// 绑定浏览器事件
	DTree.prototype.bindBrowserEvent = function(nodes){
		var _this = this;
		
		// 绑定文件夹展开/收缩的图标的点击事件，点击时给当前节点的div添加选中class
		_this.obj.on("click", "i[data-spread]", function(event) {
			event.stopPropagation();
			var $i = $(this),
				$div = $i.parent("div"),
				$cite = $div.find("cite"),
				node = _this.getNodeParam($div),
				$ul = $div.next("ul"),
				$p_li = $div.parent("li[data-index]"),	//当前选中节点的顶级li节点
				$p_ul = $p_li.parent("ul");
			var $toolBarDiv = _this.obj.prev('div#dtree_toolbar_'+_this.obj[0].id);
			$toolBarDiv.find(".layui-nav-child").removeClass('layui-anim-fadein layui-show');
			
			_this.obj.find("div[data-id]").parent().find("."+NAV_THIS).removeClass(NAV_THIS);
			_this.obj.find("div[data-id]").parent().find("."+_this.style.itemThis).removeClass(_this.style.itemThis);
			$div.addClass(NAV_THIS);
			$div.addClass(_this.style.itemThis);
			
			_this.clickSpread($div);	// 展开或隐藏节点
			
			// 树状态改变后，用户自定义想做的事情
			layui.event.call(this, MOD_NAME, "changeTree("+$(_this.obj)[0].id+")",  _this.getRequestParam(node));
		});
		
		// 绑定所有子节点div的单击事件，点击时触发加载iframe或用户自定义想做的事情
		_this.obj.on("click", "div[d-click='"+eventName.itemNodeClick+"']", function(event) {
			event.stopPropagation();
			var $div = $(this),
				$cite = $div.find("cite"),
				node = _this.getNodeParam($div),
				$ul = $div.next("ul"),
				$p_li = $div.parent("li[data-index]"),	//当前选中节点的顶级li节点
				$p_ul = $p_li.parent("ul");
			var $toolBarDiv = _this.obj.prev('div#dtree_toolbar_'+_this.obj[0].id);
			$toolBarDiv.find(".layui-nav-child").removeClass('layui-anim-fadein layui-show');
				
			_this.obj.find("div[data-id]").parent().find("."+NAV_THIS).removeClass(NAV_THIS);
			_this.obj.find("div[data-id]").parent().find("."+_this.style.itemThis).removeClass(_this.style.itemThis);
			$div.addClass(NAV_THIS);
			$div.addClass(_this.style.itemThis);
			
			if (_this.useIframe) {
				var iframeParam = _this.getIframeRequestParam(node);
				var flag = _this.loadIframe($div, iframeParam);	// 加载iframe
				if (flag) {
					// iframe加载完毕后，用户自定义想做的事情
					_this.iframeFun.iframeDone(iframeParam);
					layui.event.call(this, MOD_NAME, "iframeDone("+$(_this.obj)[0].id+")",  iframeParam);
				}
			} else {
				// 单击事件执行完毕后，用户自定义想做的事情
				layui.event.call(this, MOD_NAME, "node("+$(_this.obj)[0].id+")",  _this.getRequestParam(node));
			}
		});
		
		// 绑定所有子节点div的双击事件，暴露on给用户自定义
		_this.obj.on("dblclick", "div[d-click='"+eventName.itemNodeClick+"']", function(event) {
			event.stopPropagation();
			var $div = $(this),
				$cite = $div.find("cite"),
				node = _this.getNodeParam($div),
				$ul = $div.next("ul"),
				$p_li = $div.parent("li[data-index]"),	//当前选中节点的顶级li节点
				$p_ul = $p_li.parent("ul");
			var $toolBarDiv = _this.obj.prev('div#dtree_toolbar_'+_this.obj[0].id);
			$toolBarDiv.find(".layui-nav-child").removeClass('layui-anim-fadein layui-show');
				
			_this.obj.find("div[data-id]").parent().find("."+NAV_THIS).removeClass(NAV_THIS);
			_this.obj.find("div[data-id]").parent().find("."+_this.style.itemThis).removeClass(_this.style.itemThis);
			$div.addClass(NAV_THIS);
			$div.addClass(_this.style.itemThis);
			// 双击事件执行完毕后，用户自定义想做的事情
			layui.event.call(this, MOD_NAME, "nodedblclick("+$(_this.obj)[0].id+")",  _this.getRequestParam(node));
		});
		
		//绑定所有子节点div的右键点击事件，用于显示toolbar
		_this.obj.on("contextmenu", "div[d-click='"+eventName.itemNodeClick+"'][d-contextmenu]", function(e){
			var $div = $(this),
				contextmenu = $div.attr("d-contextmenu");
			if(_this.toolbar){
				var $toolBarDiv = _this.obj.prev('div#dtree_toolbar_'+_this.obj[0].id);
				$toolBarDiv.find(".layui-nav-child").removeClass('layui-anim-fadein layui-show');
				var e = e || window.event,
				mx = e.pageX +5,
				my = $div.offset().top - _this.obj.closest(_this.toolbarScroll).offset().top +10;
			
				if(contextmenu == "true"){
					_this.obj.find("div[data-id]").parent().find("."+NAV_THIS).removeClass(NAV_THIS);
					_this.obj.find("div[data-id]").parent().find("."+_this.style.itemThis).removeClass(_this.style.itemThis);
					$div.addClass(NAV_THIS);
					$div.addClass(_this.style.itemThis);
					$toolBarDiv.find(".layui-nav-child").addClass('layui-anim-fadein layui-show');
					$toolBarDiv.css({'left':mx+'px','top':my+'px'});
				}
			}
			e.stopPropagation();
			return false;
		});
		
		// 绑定装载树的上层出现滚动条的容器，让toolbar隐藏
		_this.obj.closest(_this.toolbarScroll).scroll(function() {
			var $toolBarDiv = _this.obj.prev('div#dtree_toolbar_'+_this.obj[0].id);
			$toolBarDiv.find(".layui-nav-child").removeClass('layui-anim-fadein layui-show');
		});
		// 绑定body的单击，让toolbar隐藏
		$BODY.on("click", function(event){
			var $toolBarDiv = _this.obj.prev('div#dtree_toolbar_'+_this.obj[0].id);
			$toolBarDiv.find(".layui-nav-child").removeClass('layui-anim-fadein layui-show');
		});
		
		// 绑定toolbar的节点添加按钮
		_this.obj.prev('div#dtree_toolbar_'+_this.obj[0].id).on("click", "a[d-click='"+eventName.addToolbar+"']", function(event) {
			event.stopPropagation();
			var $div = _this.getNowNode(),
				node = _this.getNodeParam($div),
				$ul = $div.next("ul"),
				$p_li = $div.parent("li[data-index]"),	//当前选中节点的顶级li节点
				$p_ul = $p_li.parent("ul"),	//当前选中节点的顶级li节点的父级ul
				$cite = $div.children("cite");	//当前选中节点的text
				title = $cite.html();
			var $toolBarDiv = _this.obj.prev('div#dtree_toolbar_'+_this.obj[0].id);
			$toolBarDiv.find(".layui-nav-child").removeClass('layui-anim-fadein layui-show');
				
			var content = _this.loadToolBar(title, eventName.addToolbar);
				
			layer.open({
				title: "新增"+_this.toolbarStyle.title,
				type: 1,
				area: _this.toolbarStyle.area,
				content: content,
				success: function(layero, index){
					form.on("submit(dtree_addNode_form)",function(data){
						var data = data.field;
						var parentId = $div.attr("data-id"),
							id = $div.attr("data-id")+"_node_"+$ul[0].childNodes.length,
							isLeaf = true,
							isChecked = "0",
							level = parseInt($p_li.attr("data-index"))+1;
							
						// 创建子节点的DOM，添加子节点
						var checkArr = [];
						if (_this.checkArrLen > 0) {
							for (var i = 0; i < _this.checkArrLen; i++) {
								checkArr.push({"type":i,"isChecked":"0"});
							}
						}
				    	var dom = _this.getDom(id, parentId, data.addNodeName, true, "", checkArr, level);
				    	$ul.append(_this.getLiItemDom(dom, id, parentId, true, level, "", "item"));
				    	// 先将li节点隐藏
				    	$ul.find("li[data-id='"+id+"']").hide();
				    	// 重新赋值
				    	var $addDiv = $ul.find("div[data-id='"+id+"']");
			    		node = _this.getNodeParam($addDiv);
						
			    		//获取组装后的requestNode,组合参数
			    		var requestNode = _this.getRequestParam(node);
			    		requestNode = $.extend(requestNode, data);
			    		
			    		_this.temp = [id, $ul, $div];
						// 用户自定义想做的事情
			    		_this.toolbarFun.addTreeNode(requestNode);
			    		
			    		layer.close(index);
						return false;
					});
				}
			});
		});
		
		// 绑定toolbar的节点编辑按钮
		_this.obj.prev('div#dtree_toolbar_'+_this.obj[0].id).on("click", "a[d-click='"+eventName.editToolbar+"']", function(event) {
			event.stopPropagation();
			var $div = _this.getNowNode(),
				node = _this.getNodeParam($div),
				$ul = $div.next("ul"),
				$p_li = $div.parent("li[data-index]"),	//当前选中节点的顶级li节点
				$p_ul = $p_li.parent("ul"),	//当前选中节点的顶级li节点的父级ul
				$cite = $div.children("cite");	//当前选中节点的text
				title = $cite.html();
			var $toolBarDiv = _this.obj.prev('div#dtree_toolbar_'+_this.obj[0].id);
			$toolBarDiv.find(".layui-nav-child").removeClass('layui-anim-fadein layui-show');
				
			var content = _this.loadToolBar(title, eventName.editToolbar);
				
			layer.open({
				title: "编辑"+_this.toolbarStyle.title,
				type: 1,
				area: _this.toolbarStyle.area,
				content: content,
				success: function(layero, index){
					_this.toolbarFun.editTreeLoad(_this.getRequestParam(node));
					
					form.on("submit(dtree_editNode_form)",function(data){
						var data = data.field;
						$cite.html(data.editNodeName);
						node = _this.getNodeParam($div);
						var requestNode = _this.getRequestParam(node);
			    		requestNode = $.extend(requestNode, data);
			    		_this.temp = [$cite, $div];
			    		_this.toolbarFun.editTreeNode(requestNode);
						
						layer.close(index);
					});
				}
			});
		});
		
		// 绑定toolbar的节点删除按钮
		_this.obj.prev('div#dtree_toolbar_'+_this.obj[0].id).on("click", "a[d-click='"+eventName.delToolbar+"']", function(event) {
			event.stopPropagation();
			var $div = _this.getNowNode(),
				$p_li = $div.parent("li[data-index]"),	//当前选中节点的顶级li节点
				$p_ul = $p_li.parent("ul"),	//当前选中节点的顶级li节点的父级ul
				$p_div = $p_ul.prev("div"); //当前选中节点的顶级li节点的父级ul的前一个div
			var $toolBarDiv = _this.obj.prev('div#dtree_toolbar_'+_this.obj[0].id);
			$toolBarDiv.find(".layui-nav-child").removeClass('layui-anim-fadein layui-show');
			layer.confirm('确定要删除该'+_this.toolbarStyle.title+'？', {icon: 3, title:'删除'+_this.toolbarStyle.title}, function(index){
				var node = _this.getNodeParam($div);
				_this.temp = [$p_li, $p_div];
				_this.toolbarFun.delTreeNode(_this.getRequestParam(node));
				
				layer.close(index);
			});
		});
		
		// 绑定toolbar的自定义事件按钮
		_this.obj.prev('div#dtree_toolbar_'+_this.obj[0].id).on("click", "a[d-click='"+eventName.extToolbar+"']", function(event) {
			event.stopPropagation();
			var $toolBarDiv = _this.obj.prev('div#dtree_toolbar_'+_this.obj[0].id);
			$toolBarDiv.find(".layui-nav-child").removeClass('layui-anim-fadein layui-show');
			var $div = _this.getNowNode();
			var toolbarId = $(this).attr("d-toolbarId");
			if(_this.toolbarExt.length > 0){
				for(var i=0; i<_this.toolbarExt.length; i++){
					var ext = _this.toolbarExt[i];
					if (toolbarId == ext.toolbarId){
						ext.handler(_this.getRequestParam(_this.getNodeParam($div)));
						break;
					}
				}
			}
		});
		
		
		// 绑定menubar的点击按钮事件
		_this.obj.prev('div#dtree_menubar_'+_this.obj[0].id).on("click", "button[d-click]", function(event) {
			event.stopPropagation();
			var $toolBarDiv = _this.obj.prev('div#dtree_toolbar_'+_this.obj[0].id);
			$toolBarDiv.find(".layui-nav-child").removeClass('layui-anim-fadein layui-show');
			var dClick = $(this).attr("d-click");
			switch (dClick) {
				case eventName.moveDown:	// 展开节点
					_this.openAllNode();
					break;
				case eventName.moveUp:	// 收缩节点
					_this.closeAllNode();
					break;
				case eventName.refresh:
					_this.obj.html("");	// 清空树结构
					_this.initNodeParam(); // 清空参数
					_this.init(); //执行初始化方法
					break;
				case eventName.remove:
					var len = _this.obj.find("i[data-par][data-checked='1']").length;
					if(len == 0){
						layer.msg("请至少选中一个节点",{icon:2});
					}else{
						//操作前先清空
						_this.checkbarNode = [];
						// 选择所有复选框节点
						var i_node = {};
						_this.obj.find("i[data-par][data-checked='1']").each(function(){
							var $i = $(this);
							$div = $i.closest("."+LI_DIV_ITEM);
							
							_this.checkbarNode.push(_this.getRequestParam(_this.getTempNode($div, $i)));
						});
						
						layer.confirm('确定要删除选中节点？', {icon: 3, title:'删除选中节点'}, function(index1){
							var flag = _this.menubarFun.remove(_this.checkbarNode);
							if(flag){
								_this.obj.find("i[data-par][data-checked='1']").closest("."+LI_DIV_ITEM).remove();
								_this.checkbarNode=[];
							}
							
							layer.close(index1);
						});
						
					}
					break;
				case eventName.searchNode:
					layer.prompt({
						formType: 0,
						value: "",
						title: '查询节点'
					}, function(value, index1, elem){
						if (value) {
							var flag = _this.searchNode(value);
							if (!flag) {
								layer.msg("该名称节点不存在！", {icon:5});
							}
						} else {
							layer.msg("未指定查询节点名称", {icon:5});
						}
						layer.close(index1);
					});
					break;
			}
			return false;
		});
		
		// 绑定cheboxbar的节点复选框
		_this.obj.on("click", "i[d-click='"+eventName.checkNodeClick+"']", function(event) {
			var $toolBarDiv = _this.obj.prev('div#dtree_toolbar_'+_this.obj[0].id);
			$toolBarDiv.find(".layui-nav-child").removeClass('layui-anim-fadein layui-show');
			var $i = $(this),
				$div = $i.closest("div[d-click='"+eventName.itemNodeClick+"']"),
				node = _this.getNodeParam($div);
			// 复选框选中前的回调
			var flag = _this.checkbarFun.chooseBefore($i, _this.getRequestParam(node));
			_this.temp = [$i];
			if(flag){_this.changeCheck();}
			event.stopPropagation();
		});
	};
	
	/** 外部访问 **/
	var dtree = {
		render: function(options){	// 初始化树
			var dTree = null;
			var id = event.getElemId(options);
			if(id == "") {
				layer.msg("页面中未找到绑定id", {icon:5});
			} else {
				dTree = DTrees[id];
				if(typeof dTree === 'object'){
					var tempObj = dTree.obj[0];
					dTree.reloadSetting(options);
					var newObj = dTree.obj[0];
					dTree.init();
					if(tempObj != newObj){ dTree.bindBrowserEvent(); }
				} else {
					// 创建树
					dTree = new DTree(options);
					// 添加到树数组中去
					DTrees[id] = dTree;
					
					dTree.openMenubar();
					dTree.openToolbar();
					dTree.init();
					dTree.bindBrowserEvent();
				}
			}
			
			return dTree;
		},
		reload: function(dTree, options){
			if(typeof dTree === "string"){
				dTree = DTrees[dTree];
			} 
			if(typeof dTree === "undefined"){
				layer.msg("方法获取失败，请检查ID或对象传递是否正确",{icon:2});
				return ;
			}
			var tempObj = dTree.obj[0];
			dTree.reloadSetting(options);
			var newObj = dTree.obj[0];
			dTree.init();
			if(tempObj != newObj){ dTree.bindBrowserEvent(); }
		},
		on: function(events, callback) {	// 绑定事件
			if(events.indexOf("'") > 0){
				events = events.replace(/'/g,"");
			}
			if(events.indexOf('"') > 0) {
				events = events.replace(/"/g,"");
			}
			return layui.onevent.call(this, MOD_NAME, events, callback);
		},
		getNowParam: function(dTree){
			if(typeof dTree === "string"){
				dTree = DTrees[dTree];
			}
			if(typeof dTree === "undefined"){
				layer.msg("方法获取失败，请检查ID或对象传递是否正确",{icon:2});
				return ;
			}
			return dTree.getNowParam();	// 获取当前选中值
		},
		getParentParam: function(dTree, id){		// 获取参数的上级节点
			if(typeof dTree === "string"){
				dTree = DTrees[dTree];
			}
			if(typeof dTree === "undefined"){
				layer.msg("方法获取失败，请检查ID或对象传递是否正确",{icon:2});
				return ;
			}
			return dTree.getParentParam(id);
		},
		getCheckbarNodesParam: function(dTree){		
			if(typeof dTree === "string"){
				dTree = DTrees[dTree];
			}
			if(typeof dTree === "undefined"){
				layer.msg("方法获取失败，请检查ID或对象传递是否正确",{icon:2});
				return ;
			}
			return dTree.getCheckbarNodesParam();	// 获取复选框选中值
		},
		dataInit: function(dTree, chooseId){	// 初始化选中树，针对数据返选
			if(typeof dTree === "string"){
				dTree = DTrees[dTree];
			}
			if(typeof dTree === "undefined"){
				layer.msg("方法获取失败，请检查ID或对象传递是否正确",{icon:2});
				return ;
			}
			if(chooseId){
				return dTree.dataInit(chooseId);
			}
		},
		chooseDataInit: function(dTree, chooseIds){	// 初始化复选框的值
			if(typeof dTree === "string"){
				dTree = DTrees[dTree];
			}
			if(typeof dTree === "undefined"){
				layer.msg("方法获取失败，请检查ID或对象传递是否正确",{icon:2});
				return ;
			}
			if(chooseIds){
				return dTree.chooseDataInit(chooseIds);
			}
		},
		changeCheckbarNodes: function(dTree){	//判断复选框是否发生变更
			if(typeof dTree === "string"){
				dTree = DTrees[dTree];
			}
			if(typeof dTree === "undefined"){
				layer.msg("方法获取失败，请检查ID或对象传递是否正确",{icon:2});
				return ;
			}
			return dTree.changeCheckbarNodes();
		},
		escape: function(html){
			return event.escape(html);
		},
		unescape: function(str){
			return event.unescape(str);
		},
		version: function(){
			return VERSION;
		}
	};
	layui.link(basePath + '../../lib/layui/lay/modules/dtree/dtree.css');
	layui.link(basePath + '../../lib/layui/lay/modules/dtree/font/iconfont.css');
	exports('dtree', dtree);
});