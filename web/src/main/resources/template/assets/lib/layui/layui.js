/*!
 * Layui
 * Classic modular Front-End UI library
 * MIT Licensed
 */

// 文件路径
var fileBasePath = "http://127.0.0.1:8081/";

// 接口请求地址
var reqBasePath = "http://127.0.0.1:8081/"; // 总项目
var shopBasePath = "http://127.0.0.1:8082/"; // 商城项目

var basePath = "../../assets/lib/winui/";
var maskReqStr = '<div class="mask-req-str"><div class="cent"><i class="fa fa-spin fa-spinner fa-fw"></i><br><br><font>数据请求中</font></div></div>';
var skyeyeVersion = "1.0.0-beta";
var webSocketPath = "";//聊天socket-开发
var returnCitySN = {"cip": "", "cid": "CN", "cname": "CHINA"};//ip json
var sysMainMation = '';//系统基础信息json
var addrs = '';//地址json
//文件类型
var imageType = ["png", "jpg", "xbm", "bmp", "webp", "jpeg", "svgz", "git", "ico", "tiff", "svg", "jiff", "pjpeg", "pjp", "tif", "gif"];
var officeType = ["docx", "doc", "xls", "xlsx", "ppt", "pptx", "wps", "et", "dps", "csv", "pdf"];
var vedioType = ["mp4", "rm", "rmvb", "wmv", "avi", "3gp", "mkv"];
var audioType = ["mp3", "wav", "cd"];
var packageType = ["zip", "rar"];
var epubType = ["epub"];
var aceType = ["txt", "sql", "java", "css", "html", "htm", "json", "js", "tpl"];

//系统cookies从哪里获取， true：从用户登陆获取；false：跨域获取
var getCookiesByUrl = true;

if(!getCookiesByUrl){//跨域获取
	if(isNull(getCookie("userToken"))){
		if(isNull(GetUrlParam("userToken"))){//如果url后面没有跟usertoken，返回404页面
			location.href = '../../tpl/sysmessage/500.html';
		}else{
			setCookie('userToken', GetUrlParam("userToken"), 's1800');
		}
	}
}

var MiniSite = new Object();
/**
 * 判断浏览器
 */
MiniSite.Browser={
	ie:/msie/.test(window.navigator.userAgent.toLowerCase()),
	moz:/gecko/.test(window.navigator.userAgent.toLowerCase()),
	opera:/opera/.test(window.navigator.userAgent.toLowerCase()),
	safari:/safari/.test(window.navigator.userAgent.toLowerCase())
};
/**
 * JsLoader对象用来加载外部的js文件
 */
MiniSite.JsLoader={
	/**
	 * 加载外部的js文件
	 * @param sUrl 要加载的js的url地址
	 * @fCallback js加载完成之后的处理函数
	 */
	load:function(sUrl,fCallback){
		var _script = document.createElement('script');
		_script.setAttribute('charset','gbk');
		_script.setAttribute('type','text/javascript');
		_script.setAttribute('src',sUrl);
		document.getElementsByTagName('head')[0].appendChild(_script);
		if(MiniSite.Browser.ie){
			_script.onreadystatechange=function(){
				if(this.readyState == 'loaded'||this.readyStaate == 'complete'){
					if(fCallback != undefined){
						fCallback();
					}

				}
			};
		} else if(MiniSite.Browser.moz){
			_script.onload = function(){
				if(fCallback != undefined){
					fCallback();
				}
			};
		} else{
			if(fCallback != undefined){
				fCallback();
			}
		}
	}
};

//系统基础信息
if(isNull(getCookie("sysMainMation"))){
	MiniSite.JsLoader.load("../../json/main.js", function(){
		setCookie('sysMainMation', JSON.stringify(sysMainMation), 'd30');
	});
}else{
	sysMainMation = JSON.parse(unescape(getCookie("sysMainMation")));
}

webSocketPath = sysMainMation.webSocketPath;

// 用户中英文类型
var languageType = isNull(getCookie("languageType")) ? "zh" : getCookie("languageType");
// 系统中英文
var systemLanguage;
if(isNull(localStorage.getItem("systemLanguage"))){
	jsGetJsonFile("../../json/language.json", function(data) {
		systemLanguage = data;
		localStorage.setItem("systemLanguage", JSON.stringify(data));
	});
}else{
	systemLanguage = JSON.parse(unescape(localStorage.getItem("systemLanguage")));
}

// 系统单据类型
var systemOrderType;
if(isNull(localStorage.getItem("systemOrderType"))){
	jsGetJsonFile("../../json/sysOrderType.json", function(data) {
		systemOrderType = data;
		localStorage.setItem("systemOrderType", JSON.stringify(data));
	});
}else{
	systemOrderType = JSON.parse(unescape(localStorage.getItem("systemOrderType")));
}

// 动态表单关联json文件
var sysDsFormWithCodeType;
if(isNull(localStorage.getItem("sysDsFormWithCodeType"))){
	jsGetJsonFile("../../json/sysDsFormWithCodeType.json", function(data) {
		sysDsFormWithCodeType = data;
		localStorage.setItem("sysDsFormWithCodeType", JSON.stringify(data));
	});
}else{
	sysDsFormWithCodeType = JSON.parse(unescape(localStorage.getItem("sysDsFormWithCodeType")));
}

// 工作流流程模型关联json文件
var sysActivitiModel;
if(isNull(localStorage.getItem("sysActivitiModel"))){
	jsGetJsonFile("../../json/activitiNameKey.json", function(data) {
		sysActivitiModel = data;
		localStorage.setItem("sysActivitiModel", JSON.stringify(data));
	});
}else{
	sysActivitiModel = JSON.parse(unescape(localStorage.getItem("sysActivitiModel")));
}

/**
 * 原生js获取json文件内容
 * @param url json路径地址
 */
function jsGetJsonFile(url, callback){
	var request = new XMLHttpRequest();
	request.open("get", url);/*设置请求方法与路径*/
	request.send(null);/*不发送数据到服务器*/
	request.onload = function () {/*XHR对象获取到返回信息后执行*/
		if (request.status == 200) {/*返回状态为200，即为数据获取成功*/
			var json = JSON.parse(request.responseText);
			if(callback != undefined){
				callback(json);
			}
		}
	}
}

/**
 * 获取url后面的参数值
 * @param paraName
 * @returns
 */
function GetUrlParam(paraName) {
	var url = document.location.toString();
	var arrObj = url.split("?");
	if(arrObj.length > 1) {
		var arrPara = arrObj[1].split("&");
		var arr;
		for(var i = 0; i < arrPara.length; i++) {
			arr = arrPara[i].split("=");
			if(arr != null && arr[0] == paraName) {
				return arr[1];
			}
		}
		return "";
	} else {
		return "";
	}
}

function getBaseRootPath(){
	var curWwwPath = window.document.location.href;
	var pathName = window.document.location.pathname;
	var pos = curWwwPath.indexOf(pathName);
	var localhostPaht = curWwwPath.substring(0, pos);
	var projectName = pathName.substring(0, pathName.substr(1).indexOf('/') + 1);
	return(localhostPaht + "/");//http://127.0.0.1:8080/
}

var getFileContent = function(url){
	var realPath = "../../" + url;
	var content = null;
	layui.$.ajax({
		url: realPath,
		dataType: 'text',
		async:false,
		success: function(data) {
			content = data;
		}
	});
	return content;
}

//判断内容是否为空
function isNull(str){
	if(str == null || str == "" || str == '' || str == "null" || str == "undefined"){
		return true;
	}else{
		return false;
	}
}

;!function(win){
	"use strict";
	var doc = document, config = {
			modules: {}, // 记录模块物理路径
			status: {}, // 记录模块加载状态
			timeout: 10, // 符合规范的模块请求最长等待秒数
			event: {} // 记录模块自定义事件
		},
		Layui = function(){
			this.v = '2.6.7'; // 版本号
		},
		// 识别预先可能定义的指定全局对象
		GLOBAL = win.LAYUI_GLOBAL || {},

		// 获取layui所在目录
		getPath = function(){
			var jsPath = doc.currentScript ? doc.currentScript.src : function(){
				var js = doc.scripts,
					last = js.length - 1,
					src;
				for(var i = last; i > 0; i--){
					if(js[i].readyState === 'interactive'){
						src = js[i].src;
						break;
					}
				}
				return src || js[last].src;
			}();
			return config.dir = GLOBAL.dir || jsPath.substring(0, jsPath.lastIndexOf('/') + 1);
		}(),
		error = function(msg, type){// 异常提示
			type = type || 'log';
			win.console && console[type] && console[type]('layui error hint: ' + msg);
		},
		isOpera = typeof opera !== 'undefined' && opera.toString() === '[object Opera]',
		modules = {// 内置模块
			lay: 'lay', //基础 DOM 操作
			layer: 'layer', // 弹层
			laydate: 'laydate', // 日期
			laypage: 'laypage', // 分页
			laytpl: 'laytpl', // 模板引擎
			layim: 'layim', // web通讯
			layedit: 'layedit', // 富文本编辑器
			form: 'form', // 表单集
			colorpicker: 'colorpicker', //颜色选择器
			slider: 'slider', //滑块
			upload: 'upload', // 上传
			tree: 'tree', // 树结构
			table: 'table', // 表格
			element: 'element', // 常用元素操作
			util: 'util', // 工具块
			flow: 'flow', // 流加载
			carousel: 'carousel',// 轮播
			code: 'code', // 代码修饰器
			jquery: 'jquery-min', // DOM库（第三方）
			fsButtonCommon: 'ztree/js/fsButtonCommon',//ztree树依赖项
			fsCommon: 'ztree/js/fsCommon',//ztree树依赖项
			fsTree: 'ztree/js/fsTree',//ztree树
			mobile: 'mobile', // 移动大模块 | 若当前为开发目录，则为移动模块入口，否则为移动模块集合
			'layui.all': '../layui.all', // PC模块合并版
			cookie: 'cookie',//cookie
			fileUpload: 'jQuery.upload.min',//上传
			dragula: 'dragula',//拖拽
			codemirror: 'codemirror/codemirror',//代码格式编辑器
			xml: 'codemirror/xml',//代码格式编辑器xml，html支持
			clike: 'codemirror/clike',//代码格式编辑器clike支持C，C++，Objective-C，Java，Scala，Kotlin，Ceylon高亮
			css: 'codemirror/css',//代码格式编辑器css支持CSS
			htmlmixed: 'codemirror/htmlmixed',//代码格式编辑器htmlmixed，HTML混合模式取决于XML，JavaScript和CSS模式支持
			javascript: 'codemirror/javascript',//代码格式编辑器javascript支持js
			nginx: 'codemirror/nginx',//代码格式编辑器nginx支持nginx
			solr: 'codemirror/solr',//代码格式编辑器solr支持solr
			sql: 'codemirror/sql',//代码格式编辑器sql支持sql
			vue: 'codemirror/vue',//代码格式编辑器vue支持vue
			zclip: 'jquery.zclip',//复制插件
			swiper: 'swiper/swiper.min',//滚动插件
			tableSelect: 'tableSelect',//tableSelect 下拉表格选择器
			treeGrid: 'treetable/treeGrid',//树表格
			g6: 'flowchart/g6.min',//流程图
			g6Plugins: 'flowchart/g6-plugins.min',//流程图
			dtree: 'dtree/dtree',//
			jqueryUI: 'jqueryui/jquery-ui.min',//jQuery拖拽
			validate: 'validate/jquery.validate',//验证
			ClipboardJS: 'clipboard.min',//复制
			radialin: 'radialindicator/radialindicator',//加载进度条
			contextMenu: 'contextMenu',//右键
			fullcalendar: 'fullcalendar/fullcalendar',//日程插件
			dropdown: 'dropdown', //下拉按钮
			webuploader: 'webuploader/webuploader',//上传
			viewer: 'viewer/viewer', //图片展示
			dplayer: 'dplayer/DPlayer.min', //视频播放插件
			tagEditor: 'tagEditor/jquery.tag-editor', //标签输入框
			clock: 'clock/script', //时钟
			moment: 'clock/moment.min', //时钟依赖
			filterizr: 'filterizr/jquery.filterizr', //jquery筛选
			fullscreenslider: 'fullscreenslider/ddfullscreenslider', //块级滚动
			jqprint: 'jquery.jqprint-0.3', //打印插件
			tautocomplete: 'tautocomplete/tautocomplete', //自动补全插件
			fullscreen: 'fullscreen/jquery.fullscreen', //放大插件
			opTable: 'opTable/opTable', //opTable-可加载子内容
			eleTree: 'eleTree/eleTree', //eleTree树组件-https://fly.layui.com/extend/eleTree/#doc
			tableCheckBoxUtil: 'tableCheckBoxUtil/tableCheckBoxUtil', //表格多选框记忆加载工具
			textool: 'textool/textool', //textool 文本输入工具条-https://fly.layui.com/extend/textool/#doc
			soulTable: 'ext/soulTable', //soulTable 表格扩展操作-https://gitee.com/saodiyang/layui-soul-table
			tableChild: 'ext/tableChild', //soulTable 依赖的插件
			tableFilter: 'ext/tableFilter', //soulTable 依赖的插件
			tableMerge: 'ext/tableMerge', //soulTable 依赖的插件
			excel: 'ext/excel', //soulTable 依赖的插件
			weixinAudio: 'audio/weixinAudio', //音频播放插件
			spop: 'spop/spop', //气泡提示
			orgChart: "orgChart/js/jquery.orgchart" //组织结构图
		};

	// 记录基础数据
	Layui.prototype.cache = config;

	// 定义模块
	Layui.prototype.define = function(deps, factory){
		var that = this, type = typeof deps === 'function', callback = function(){
			var setApp = function(app, exports){
				layui[app] = exports;
				config.status[app] = true;
			};
			typeof factory === 'function' && factory(function(app, exports){
				setApp(app, exports);
				config.callback[app] = function(){
					factory(setApp);
				}
			});
			return this;
		};
		type && (factory = deps,deps = []);
		if(layui['layui.all'] || (!layui['layui.all'] && layui['layui.mobile'])){
			return callback.call(that);
		}
		that.use(deps, callback);
		return that;
	};

	// 使用特定模块
	Layui.prototype.use = function(apps, callback, exports, from) {
		var that = this,
			dir = config.dir = config.dir ? config.dir : getPath,
			head = doc.getElementsByTagName('head')[0];

		apps = function(){
			if(typeof apps === 'string'){
				return [apps];
			}
			//当第一个参数为 function 时，则自动加载所有内置模块，且执行的回调即为该 function 参数；
			else if(typeof apps === 'function'){
				callback = apps;
				return ['all'];
			}
			return apps;
		}();
		// 如果页面已经存在jQuery1.7+库且所定义的模块依赖jQuery，则不加载内部jquery模块
		if(window.jQuery && jQuery.fn.on) {
			that.each(apps, function(index, item) {
				if(item === 'jquery') {
					apps.splice(index, 1);
				}
			});
			layui.jquery = layui.$ = jQuery;
		}

		var item = apps[0],
			timeout = 0;
		exports = exports || [];

		// 静态资源host
		config.host = config.host || (dir.match(/\/\/([\s\S]+?)\//) || ['//' + location.host + '/'])[0];

		// 加载完毕
		function onScriptLoad(e, url) {
			var readyRegExp = navigator.platform === 'PLaySTATION 3' ? /^complete$/ : /^(complete|loaded)$/
			if(e.type === 'load' || (readyRegExp.test((e.currentTarget || e.srcElement).readyState))) {
				config.modules[item] = url;
				head.removeChild(node);
				(function poll() {
					if(++timeout > config.timeout * 1000 / 4) {
						return error(item + ' is not a valid module');
					};
					config.status[item] ? onCallback() : setTimeout(poll, 4);
				}());
			}
		}

		// 回调
		function onCallback() {
			exports.push(layui[item]);
			apps.length > 1 ?
				that.use(apps.slice(1), callback, exports, from)
				: ( typeof callback === 'function' && function(){
					//保证文档加载完毕再执行回调
					if(layui.jquery && typeof layui.jquery === 'function' && from !== 'define'){
						return layui.jquery(function(){
							callback.apply(layui, exports);
						});
					}
					callback.apply(layui, exports);
				}() );
		}

		//如果引入了聚合板，内置的模块则不必重复加载
		if( apps.length === 0 || (layui['layui.all'] && modules[item]) ){
			return onCallback(), that;
		}

		//获取加载的模块 URL
		//如果是内置模块，则按照 dir 参数拼接模块路径
		//如果是扩展模块，则判断模块路径值是否为 {/} 开头，
		//如果路径值是 {/} 开头，则模块路径即为后面紧跟的字符。
		//否则，则按照 base 参数拼接模块路径

		var url = ( modules[item] ? (dir + 'lay/modules/')
				: (/^\{\/\}/.test(that.modules[item]) ? '' : (config.base || ''))
		) + (that.modules[item] || item) + '.js';
		url = url.replace(/^\{\/\}/, '');

		//如果扩展模块（即：非内置模块）对象已经存在，则不必再加载
		if(!config.modules[item] && layui[item]){
			config.modules[item] = url; //并记录起该扩展模块的 url
		}

		// 首次加载模块
		if(!config.modules[item]) {
			var node = doc.createElement('script');

			node.async = true;
			node.charset = 'utf-8';
			node.src = url + function() {
				var version = config.version === true ?
					(config.v || (new Date()).getTime()) :
					(config.version || '');
				return version ? ('?v=' + version) : '';
			}();

			head.appendChild(node);

			if(node.attachEvent && !(node.attachEvent.toString && node.attachEvent.toString().indexOf('[native code') < 0) && !isOpera) {
				node.attachEvent('onreadystatechange', function(e) {
					onScriptLoad(e, url);
				});
			} else {
				node.addEventListener('load', function(e) {
					onScriptLoad(e, url);
				}, false);
			}
			config.modules[item] = url;
		} else { // 缓存
			(function poll() {
				if(++timeout > config.timeout * 1000 / 4){
					return error(item + ' is not a valid module', 'error');
				};
				(typeof config.modules[item] === 'string' && config.status[item])
					? onCallback()
					: setTimeout(poll, 4);
			}());
		}

		return that;
	};

	// 获取节点的style属性值
	Layui.prototype.getStyle = function(node, name) {
		var style = node.currentStyle ? node.currentStyle : win.getComputedStyle(node, null);
		return style[style.getPropertyValue ? 'getPropertyValue' : 'getAttribute'](name);
	};

	// css外部加载器
	Layui.prototype.link = function(href, fn, cssname) {
		var that = this,
			link = doc.createElement('link'),
			head = doc.getElementsByTagName('head')[0];

		if(typeof fn === 'string') cssname = fn;

		var app = (cssname || href).replace(/\.|\//g, ''),
			id = link.id = 'layuicss-' + app,
			STAUTS_NAME = 'creating',
			timeout = 0;

		link.rel = 'stylesheet';
		link.href = href + (config.debug ? '?v=' + new Date().getTime() : '');
		link.media = 'all';

		if(!doc.getElementById(id)) {
			head.appendChild(link);
		}

		if(typeof fn !== 'function') return that;

		//轮询 css 是否加载完毕
		(function poll(status) {
			var delay = 100
				,getLinkElem = doc.getElementById(id); //获取动态插入的 link 元素

			//如果轮询超过指定秒数，则视为请求文件失败或 css 文件不符合规范
			if(++timeout > config.timeout * 1000 / delay){
				return error(href + ' timeout');
			};

			//css 加载就绪
			if(parseInt(that.getStyle(getLinkElem, 'width')) === 1989){
				//如果参数来自于初始轮询（即未加载就绪时的），则移除 link 标签状态
				if(status === STAUTS_NAME) getLinkElem.removeAttribute('lay-status');
				//如果 link 标签的状态仍为「创建中」，则继续进入轮询，直到状态改变，则执行回调
				getLinkElem.getAttribute('lay-status') === STAUTS_NAME ? setTimeout(poll, delay) : fn();
			} else {
				getLinkElem.setAttribute('lay-status', STAUTS_NAME);
				setTimeout(function(){
					poll(STAUTS_NAME);
				}, delay);
			}
		}());

		//轮询css是否加载完毕
		/*
        (function poll() {
          if(++timeout > config.timeout * 1000 / 100){
            return error(href + ' timeout');
          };
          parseInt(that.getStyle(doc.getElementById(id), 'width')) === 1989 ? function(){
            fn();
          }() : setTimeout(poll, 100);
        }());
        */

		return that;
	};

	//css 内部加载器
	Layui.prototype.addcss = function(firename, fn, cssname){
		return layui.link(config.dir + 'css/' + firename, fn, cssname);
	};

	// 存储模块的回调
	config.callback = {};

	// 重新执行模块的工厂函数
	Layui.prototype.factory = function(modName) {
		if(layui[modName]) {
			return typeof config.callback[modName] === 'function' ?
				config.callback[modName] :
				null;
		}
	};

	// 图片预加载
	Layui.prototype.img = function(url, callback, error) {
		var img = new Image();
		img.src = url;
		if(img.complete) {
			return callback(img);
		}
		img.onload = function() {
			img.onload = null;
			typeof callback === 'function' && callback(img);
		};
		img.onerror = function(e) {
			img.onerror = null;
			typeof error === 'function' && error(e);
		};
	};

	// 全局配置
	Layui.prototype.config = function(options) {
		options = options || {};
		for(var key in options) {
			config[key] = options[key];
		}
		return this;
	};

	// 记录全部模块
	Layui.prototype.modules = function() {
		var clone = {};
		for(var o in modules) {
			clone[o] = modules[o];
		}
		return clone;
	}();

	// 拓展模块
	Layui.prototype.extend = function(options) {
		var that = this;

		//验证模块是否被占用
		options = options || {};
		for(var o in options){
			if(that[o] || that.modules[o]){
				error(o+ ' Module already exists', 'error');
			} else {
				that.modules[o] = options[o];
			}
		}

		return that;
	};

	// 路由解析
	Layui.prototype.router = function(hash) {
		var that = this,
			hash = hash || location.hash,
			data = {
				path: [],
				search: {},
				hash: (hash.match(/[^#](#.*$)/) || [])[1] || ''
			};

		if(!/^#\//.test(hash)) return data; // 禁止非路由规范
		hash = hash.replace(/^#\//, '');
		data.href = '/' + hash;
		hash = hash.replace(/([^#])(#.*$)/, '$1').split('/') || [];

		// 提取Hash结构
		that.each(hash, function(index, item) {
			/^\w+=/.test(item) ? function() {
				item = item.split('=');
				data.search[item[0]] = item[1];
			}() : data.path.push(item);
		});

		return data;
	};

	//URL 解析
	Layui.prototype.url = function(href){
		var that = this
			,data = {
			//提取 url 路径
			pathname: function(){
				var pathname = href
					? function(){
						var str = (href.match(/\.[^.]+?\/.+/) || [])[0] || '';
						return str.replace(/^[^\/]+/, '').replace(/\?.+/, '');
					}()
					: location.pathname;
				return pathname.replace(/^\//, '').split('/');
			}()

			//提取 url 参数
			,search: function(){
				var obj = {}
					,search = (href
						? function(){
							var str = (href.match(/\?.+/) || [])[0] || '';
							return str.replace(/\#.+/, '');
						}()
						: location.search
				).replace(/^\?+/, '').split('&'); //去除 ?，按 & 分割参数

				//遍历分割后的参数
				that.each(search, function(index, item){
					var _index = item.indexOf('=')
						,key = function(){ //提取 key
						if(_index < 0){
							return item.substr(0, item.length);
						} else if(_index === 0){
							return false;
						} else {
							return item.substr(0, _index);
						}
					}();
					//提取 value
					if(key){
						obj[key] = _index > 0 ? item.substr(_index + 1) : null;
					}
				});

				return obj;
			}()

			//提取 Hash
			,hash: that.router(function(){
				return href
					? ((href.match(/#.+/) || [])[0] || '/')
					: location.hash;
			}())
		};

		return data;
	};

	// 本地持久性存储
	Layui.prototype.data = function(table, settings, storage) {
		table = table || 'layui';
		storage = storage || localStorage;

		if(!win.JSON || !win.JSON.parse) return;

		// 如果settings为null，则删除表
		if(settings === null) {
			return delete storage[table];
		}

		settings = typeof settings === 'object' ?
			settings :
			{key: settings};

		try {
			var data = JSON.parse(storage[table]);
		} catch(e) {
			var data = {};
		}

		if('value' in settings) data[settings.key] = settings.value;
		if(settings.remove) delete data[settings.key];
		storage[table] = JSON.stringify(data);

		return settings.key ? data[settings.key] : data;
	};

	// 本地会话性存储
	Layui.prototype.sessionData = function(table, settings) {
		return this.data(table, settings, sessionStorage);
	}

	// 设备信息
	Layui.prototype.device = function(key) {
		var agent = navigator.userAgent.toLowerCase()

			// 获取版本号
			,getVersion = function(label) {
				var exp = new RegExp(label + '/([^\\s\\_\\-]+)');
				label = (agent.match(exp) || [])[1];
				return label || false;
			}

			// 返回结果集
			,result = {
				os: function() { // 底层操作系统
					if(/windows/.test(agent)) {
						return 'windows';
					} else if(/linux/.test(agent)) {
						return 'linux';
					} else if(/iphone|ipod|ipad|ios/.test(agent)) {
						return 'ios';
					} else if(/mac/.test(agent)) {
						return 'mac';
					}
				}(),
				ie: function() { // ie版本
					return(!!win.ActiveXObject || "ActiveXObject" in win) ? (
						(agent.match(/msie\s(\d+)/) || [])[1] || '11' // 由于ie11并没有msie的标识
					) : false;
				}(),
				weixin: getVersion('micromessenger') // 是否微信
			};

		// 任意的key
		if(key && !result[key]) {
			result[key] = getVersion(key);
		}

		// 移动设备
		result.android = /android/.test(agent);
		result.ios = result.os === 'ios';
		result.mobile = (result.android || result.ios) ? true : false;

		return result;
	};

	// 提示
	Layui.prototype.hint = function() {
		return {
			error: error
		}
	};

	//typeof 类型细分 -> string/number/boolean/undefined/null、object/array/function/…
	Layui.prototype._typeof = function(operand){
		if(operand === null) return String(operand);

		//细分引用类型
		return (typeof operand === 'object' || typeof operand === 'function') ? function(){
			var type = Object.prototype.toString.call(operand).match(/\s(.+)\]$/) || [] //匹配类型字符
				,classType = 'Function|Array|Date|RegExp|Object|Error|Symbol'; //常见类型字符

			type = type[1] || 'Object';

			//除匹配到的类型外，其他对象均返回 object
			return new RegExp('\\b('+ classType + ')\\b').test(type)
				? type.toLowerCase()
				: 'object';
		}() : typeof operand;
	};

	//对象是否具备数组结构（此处为兼容 jQuery 对象）
	Layui.prototype._isArray = function(obj){
		var that = this
			,len
			,type = that._typeof(obj);

		if(!obj || (typeof obj !== 'object') || obj === win) return false;

		len = 'length' in obj && obj.length; //兼容 ie
		return type === 'array' || len === 0 || (
			typeof len === 'number' && len > 0 && (len - 1) in obj //兼容 jQuery 对象
		);
	};

	// 遍历
	Layui.prototype.each = function(obj, fn) {
		var key
			,that = this
			,callFn = function(key, obj){ //回调
			return fn.call(obj[key], key, obj[key])
		};

		if(typeof fn !== 'function') return that;
		obj = obj || [];

		//优先处理数组结构
		if(that._isArray(obj)){
			for(key = 0; key < obj.length; key++){
				if(callFn(key, obj)) break;
			}
		} else {
			for(key in obj){
				if(callFn(key, obj)) break;
			}
		}
		return that;
	};

	// 将数组中的对象按其某个成员排序
	Layui.prototype.sort = function(obj, key, desc) {
		var clone = JSON.parse(
			JSON.stringify(obj || [])
		);

		if(!key) return clone;

		// 如果是数字，按大小排序，如果是非数字，按字典序排序
		clone.sort(function(o1, o2) {
			var isNum = /^-?\d+$/,
				v1 = o1[key],
				v2 = o2[key];

			if(isNum.test(v1)) v1 = parseFloat(v1);
			if(isNum.test(v2)) v2 = parseFloat(v2);

			return v1 - v2;

			/*if(v1 && !v2) {
				return 1;
			} else if(!v1 && v2) {
				return -1;
			}

			if(v1 > v2) {
				return 1;
			} else if(v1 < v2) {
				return -1;
			} else {
				return 0;
			}*/
		});

		desc && clone.reverse(); // 倒序
		return clone;
	};

	// 阻止事件冒泡
	Layui.prototype.stope = function(thisEvent) {
		thisEvent = thisEvent || win.event;
		try { thisEvent.stopPropagation() } catch(e){
			thisEvent.cancelBubble = true;
		}
	};

	//字符常理
	var EV_REMOVE = 'LAYUI-EVENT-REMOVE';

	// 自定义模块事件
	Layui.prototype.onevent = function(modName, events, callback) {
		if(typeof modName !== 'string' ||
			typeof callback !== 'function') return this;

		return Layui.event(modName, events, null, callback);
	};

	// 执行自定义模块事件
	Layui.prototype.event = Layui.event = function(modName, events, params, fn) {
		var that = this
			,result = null
			,filter = (events || '').match(/\((.*)\)$/)||[] //提取事件过滤器字符结构，如：select(xxx)
			,eventName = (modName + '.'+ events).replace(filter[0], '') //获取事件名称，如：form.select
			,filterName = filter[1] || '' //获取过滤器名称,，如：xxx
			,callback = function(_, item){
			var res = item && item.call(that, params);
			res === false && result === null && (result = false);
		};

		//如果参数传入特定字符，则执行移除事件
		if(params === EV_REMOVE){
			delete (that.cache.event[eventName] || {})[filterName];
			return that;
		}

		//添加事件
		if(fn){
			config.event[eventName] = config.event[eventName] || {};

			//这里不再对多次事件监听做支持，避免更多麻烦
			//config.event[eventName][filterName] ? config.event[eventName][filterName].push(fn) :
			config.event[eventName][filterName] = [fn];
			return this;
		}

		// 执行事件回调
		layui.each(config.event[eventName], function(key, item) {
			// 执行当前模块的全部事件
			if(filterName === '{*}') {
				layui.each(item, callback);
				return;
			}

			// 执行指定事件
			key === '' && layui.each(item, callback);
			(filterName && key === filterName) && layui.each(item, callback);
		});

		return result;
	};

	//新增模块事件
	Layui.prototype.on = function(events, modName, callback){
		var that = this;
		return that.onevent.call(that, modName, events, callback);
	}

	//移除模块事件
	Layui.prototype.off = function(events, modName){
		var that = this;
		return t
	};

	win.layui = new Layui();

}(window);

/**
 * 存入cookie
 * s20是代表20秒
 * h是指小时，如12小时则是：h12
 * d是天数，30天则：d30
 */
function setCookie(name, value, time) {
	var strsec = getsec(time);
	var exp = new Date();
	exp.setTime(exp.getTime() + strsec * 1);
	document.cookie = name + "=" + escape(value) + ";expires=" + exp.toGMTString() + ";path=/";
}

function getsec(str) {
	var str1 = str.substring(1, str.length) * 1;
	var str2 = str.substring(0, 1);
	if(str2 == "s") {
		return str1 * 1000;
	} else if(str2 == "h") {
		return str1 * 60 * 60 * 1000;
	} else if(str2 == "d") {
		return str1 * 24 * 60 * 60 * 1000;
	}
}

/**
 * 获取cookie值
 * @param name
 * @returns
 */
function getCookie(name){
	var strcookie = document.cookie;//获取cookie字符串
	var arrcookie = strcookie.split("; ");//分割
	//遍历匹配
	for ( var i = 0; i < arrcookie.length; i++) {
		var arr = arrcookie[i].split("=");
		if (arr[0] == name){
			return arr[1];
		}
	}
	return "";
}


