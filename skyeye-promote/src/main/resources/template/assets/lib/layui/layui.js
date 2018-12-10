/*!

 @Title: Layui
 @Description：经典模块化前端框架

 */

var fileBasePath = getBaseRootPath();//上传文件展示路径
var reqBasePath = getBaseRootPath();
var basePath = "../../assets/lib/winui/";
var skyeyeVersion = "1.0.0-beta";
var filePicType = ['png', 'jpg', 'gif', 'jpeg', 'PNG', 'JPG', 'GIF', 'JPEG'];
var fileDocType = ['txt', 'docx', 'doc', 'xlsx', 'xls', 'pdf', 'ppt', 'TXT', 'DOCX', 'DOC', 'XLSX', 'XLS', 'PDF', 'PPT'];

//showmodel.js没有数据时的默认展示
var noBeansMation = "<div class='noMation col-lg-12 col-sm-12 col-xs-12'><img src='../../assets/images/noMation.png' style='max-width:100px'/><br/><font class='noMationFont'>暂无数据</font></div>";
var noMatchingBeansMation = "<div class='noMation col-lg-12 col-sm-12 col-xs-12'><img src='../../assets/images/noMation.png' style='max-width:100px'/><br/><font class='noMationFont'>暂无匹配项</font></div>";

//动态引入获取IP的路径
document.write("<script type=\"text/javascript\" src=\"http://pv.sohu.com/cityjson?ie=utf-8\"></script>");

//动态引入模板引擎
document.write("<script type=\"text/javascript\" src=\"../../assets/lib/layui/lay/modules/hdb/handlebars-v4.0.5.js\"></script>");

function getBaseRootPath(){
	var curWwwPath = window.document.location.href;  
    var pathName = window.document.location.pathname;  
    var pos = curWwwPath.indexOf(pathName);  
    var localhostPaht = curWwwPath.substring(0, pos);  
    var projectName = pathName.substring(0, pathName.substr(1).indexOf('/') + 1);  
    return(localhostPaht + "/");//http://127.0.0.1:8080/
}

var getFileContent = function(url){
	var realPath = reqBasePath + url;
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
		event: {}, // 记录模块自定义事件
	}, 
	Layui = function(){
		this.v = '2.2.5'; // 版本号
	},
	getPath = function(){// 获取layui所在目录
		var jsPath = doc.currentScript ? doc.currentScript.src : function(){
			var js = doc.scripts,last = js.length - 1,src;
			for(var i = last; i > 0; i--){
				if(js[i].readyState === 'interactive'){
					src = js[i].src;
					break;
				}
			}
			return src || js[last].src;
		}();
		return jsPath.substring(0, jsPath.lastIndexOf('/') + 1);
	}(),
	error = function(msg){// 异常提示
//		win.console && console.error && console.error('Layui hint: ' + msg);
	},
	isOpera = typeof opera !== 'undefined' && opera.toString() === '[object Opera]',
	modules = {// 内置模块
	    layer: 'modules/layer', // 弹层
	    laydate: 'modules/laydate', // 日期
	    laypage: 'modules/laypage', // 分页
	    laytpl: 'modules/laytpl', // 模板引擎
	    layim: 'modules/layim', // web通讯
	    layedit: 'modules/layedit', // 富文本编辑器
	    ueditor: 'modules/ueditor.all', // 百度富文本编辑器
	    form: 'modules/form', // 表单集
	    colorpicker: 'modules/colorpicker', //颜色选择器
	    slider: 'modules/slider', //滑块
	    upload: 'modules/upload', // 上传
	    tree: 'modules/tree', // 树结构
	    table: 'modules/table', // 表格
	    element: 'modules/element', // 常用元素操作
	    util: 'modules/util', // 工具块
	    flow: 'modules/flow', // 流加载
	    carousel: 'modules/carousel',// 轮播
	    code: 'modules/code', // 代码修饰器
	    jquery: 'modules/jquery', // DOM库（第三方）
	    fsButtonCommon: 'modules/ztree/js/fsButtonCommon',//ztree树依赖项
	    fsCommon: 'modules/ztree/js/fsCommon',//ztree树依赖项
	    fsTree: 'modules/ztree/js/fsTree',//ztree树
	    mobile: 'modules/mobile', // 移动大模块 | 若当前为开发目录，则为移动模块入口，否则为移动模块集合
	    'layui.all': '../layui.all', // PC模块合并版
	    cookie: 'modules/cookie',//cookie
	    fileUpload: 'modules/jQuery.upload.min',//上传
	    dragula: 'modules/dragula',//拖拽
	    codemirror: 'modules/codemirror/codemirror',//代码格式编辑器
	    xml: 'modules/codemirror/xml',//代码格式编辑器xml，html支持
	    clike: 'modules/codemirror/clike',//代码格式编辑器clike支持C，C++，Objective-C，Java，Scala，Kotlin，Ceylon高亮
	    css: 'modules/codemirror/css',//代码格式编辑器css支持CSS
	    htmlmixed: 'modules/codemirror/htmlmixed',//代码格式编辑器htmlmixed，HTML混合模式取决于XML，JavaScript和CSS模式支持
	    javascript: 'modules/codemirror/javascript',//代码格式编辑器javascript支持js
	    nginx: 'modules/codemirror/nginx',//代码格式编辑器nginx支持nginx
	    solr: 'modules/codemirror/solr',//代码格式编辑器solr支持solr
	    sql: 'modules/codemirror/sql',//代码格式编辑器sql支持sql
	    vue: 'modules/codemirror/vue',//代码格式编辑器vue支持vue
	    zclip: 'modules/jquery.zclip',//复制插件
	    swiper: 'modules/swiper/swiper.min',//滚动插件
	    tableSelect: 'modules/tableSelect',//tableSelect 下拉表格选择器
	    treeGrid: 'modules/treetable/treeGrid',//树表格
	    g6: 'modules/flowchart/g6.min',//流程图
	    g6Plugins: 'modules/flowchart/g6-plugins.min',//流程图
	    dtree: 'modules/dtree/dtree',//
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
	Layui.prototype.use = function(apps, callback, exports) {
		var that = this,
			dir = config.dir = config.dir ? config.dir : getPath,
			head = doc.getElementsByTagName('head')[0];
		apps = typeof apps === 'string' ? [apps] : apps;
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
				that.use(apps.slice(1), callback, exports) :
				(typeof callback === 'function' && callback.apply(layui, exports));
		}

		// 如果使用了 layui.all.js
		if(apps.length === 0 ||
			(layui['layui.all'] && modules[item]) ||
			(!layui['layui.all'] && layui['layui.mobile'] && modules[item])
		) {
			return onCallback(), that;
		}

		// 首次加载模块
		if(!config.modules[item]) {
			var node = doc.createElement('script'),
				// 如果是内置模块，则按照 dir 参数拼接模块路径
				// 如果是扩展模块，则判断模块路径值是否为 {/} 开头，
				// 如果路径值是 {/} 开头，则模块路径即为后面紧跟的字符。
				// 否则，则按照 base 参数拼接模块路径
				url = (modules[item] ? (dir + 'lay/') :
					(/^\{\/\}/.test(that.modules[item]) ? '' : (config.base || ''))
				) + (that.modules[item] || item) + '.js';
			url = url.replace(/^\{\/\}/, '');
			//判断URL拼接可访问路径
			if(url.indexOf("http://") >= 0 || url.indexOf("https://") >= 0){
			}else{
				if(isNull(modules[item])){
					url = fileBasePath + url.replace("../../", "");
				}else{
					url = fileBasePath + "assets/lib/layui/" + url.replace("../../", "");
				}
			}
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
				if(++timeout > config.timeout * 1000 / 4) {
					return error(item + ' is not a valid module');
				};
				(typeof config.modules[item] === 'string' && config.status[item]) ?
				onCallback(): setTimeout(poll, 4);
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
			timeout = 0;

		link.rel = 'stylesheet';
		link.href = href + (config.debug ? '?v=' + new Date().getTime() : '');
		link.media = 'all';

		if(!doc.getElementById(id)) {
			head.appendChild(link);
		}

		if(typeof fn !== 'function') return that;

		// 轮询css是否加载完毕
		(function poll() {
			if(++timeout > config.timeout * 1000 / 100) {
				return error(href + ' timeout');
			};
			parseInt(that.getStyle(doc.getElementById(id), 'width')) === 1989 ? function() {
				fn();
			}() : setTimeout(poll, 100);
		}());

		return that;
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

	// css内部加载器
	Layui.prototype.addcss = function(firename, fn, cssname) {
		return layui.link(config.dir + 'css/' + firename, fn, cssname);
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
			callback(img);
		};
		img.onerror = function(e) {
			img.onerror = null;
			error(e);
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

		// 验证模块是否被占用
		options = options || {};
		for(var o in options) {
			if(that[o] || that.modules[o]) {
				error('\u6A21\u5757\u540D ' + o + ' \u5DF2\u88AB\u5360\u7528');
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
		data.href = hash = hash.replace(/^#\//, '');
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
			{
				key: settings
			};

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
			,
			getVersion = function(label) {
				var exp = new RegExp(label + '/([^\\s\\_\\-]+)');
				label = (agent.match(exp) || [])[1];
				return label || false;
			}

			// 返回结果集
			,
			result = {
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

		return result;
	};

	// 提示
	Layui.prototype.hint = function() {
		return {
			error: error
		}
	};

	// 遍历
	Layui.prototype.each = function(obj, fn) {
		var key, that = this;
		if(typeof fn !== 'function') return that;
		obj = obj || [];
		if(obj.constructor === Object) {
			for(key in obj) {
				if(fn.call(obj[key], key, obj[key])) break;
			}
		} else {
			for(key = 0; key < obj.length; key++) {
				if(fn.call(obj[key], key, obj[key])) break;
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

			if(v1 && !v2) {
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
			}
		});

		desc && clone.reverse(); // 倒序
		return clone;
	};

	// 阻止事件冒泡
	Layui.prototype.stope = function(thisEvent) {
		thisEvent = thisEvent || win.event;
		try {
			thisEvent.stopPropagation()
		} catch(e) {
			thisEvent.cancelBubble = true;
		}
	};

	// 自定义模块事件
	Layui.prototype.onevent = function(modName, events, callback) {
		if(typeof modName !== 'string' ||
			typeof callback !== 'function') return this;

		return Layui.event(modName, events, null, callback);
	};

	// 执行自定义模块事件
	Layui.prototype.event = Layui.event = function(modName, events, params, fn) {
		var that = this,
			result = null,
			filter = events.match(/\((.*)\)$/) || [] // 提取事件过滤器字符结构，如：select(xxx)
			,
			eventName = (modName + '.' + events).replace(filter[0], '') // 获取事件名称，如：form.select
			,
			filterName = filter[1] || '' // 获取过滤器名称,，如：xxx
			,
			callback = function(_, item) {
				var res = item && item.call(that, params);
				res === false && result === null && (result = false);
			};

		// 添加事件
		if(fn) {
			config.event[eventName] = config.event[eventName] || {};

			// 这里不再对多次事件监听做支持，避免更多麻烦
			// config.event[eventName][filterName] ?
			// config.event[eventName][filterName].push(fn) :
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
			key === filterName && layui.each(item, callback);
		});

		return result;
	};

	win.layui = new Layui();
  
}(window);

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

//ajax请求
var AjaxPostUtil = {
	// 基础选项  
	options: {
		method: "post", // 默认提交的方法,get post  
		url: "", // 请求的路径 required  
		params: {}, // 请求的参数  
		async: false,//同步
		type: 'text', // 返回的内容的类型,text,xml,json  
		callback: function() {}, // 回调函数 required  
	},

	// 创建XMLHttpRequest对象  
	createRequest: function() {
		var xmlhttp;
		try {
			xmlhttp = new ActiveXObject("Msxml2.XMLHTTP"); // IE6以上版本  
		} catch(e) {
			try {
				xmlhttp = new ActiveXObject("Microsoft.XMLHTTP"); // IE6以下版本  
			} catch(e) {
				try {
					xmlhttp = new XMLHttpRequest();
					if(xmlhttp.overrideMimeType) {
						xmlhttp.overrideMimeType("text/xml");
					}
				} catch(e) {
					alert("您的浏览器不支持Ajax");
				}
			}
		}
		return xmlhttp;
	},
	// 设置基础选项  
	setOptions: function(newOptions) {
		for(var pro in newOptions) {
			this.options[pro] = newOptions[pro];
		}
	},
	// 格式化请求参数  
	formateParameters: function() {
		var paramsArray = [];
		var params = this.options.params;
		for(var pro in params) {
			var paramValue = params[pro];
			/*if(this.options.method.toUpperCase() === "GET")  
			{  
			    paramValue = encodeURIComponent(params[pro]);  
			}*/
			paramsArray.push(pro + "=" + paramValue);
		}
		paramsArray.push("userToken=" + getCookie('userToken'));
		paramsArray.push("loginPCIp=" + returnCitySN["cip"]);
		return paramsArray.join("&");
	},

	// 状态改变的处理  
	readystatechange: function(xmlhttp) {
		var sessionstatus = xmlhttp.getResponseHeader("SESSIONSTATUS");
		var requestmation = xmlhttp.getResponseHeader("REQUESTMATION");
		if (sessionstatus == "TIMEOUT") {//超时跳转
			var win = window;
			while (win != win.top){
				win = win.top;
			}
			win.location.href = "login.html";//XMLHttpRequest.getResponseHeader("CONTEXTPATH");  
		}
		// 获取返回值  
		var returnValue;
		if(xmlhttp.readyState == 4 && (xmlhttp.status == 200 || xmlhttp.status == 0)) {
			switch(this.options.type) {
				case "xml":
					returnValue = xmlhttp.responseXML;
					break;
				case "json":
					var jsonText = xmlhttp.responseText;
					if(requestmation == 'DOWNLOAD'){
						returnValue = eval('(' + '{"returnMessage":"成功","returnCode":0,"total":0,"rows":"","bean":""}' + ')');
					}else{
						if(jsonText) {
							returnValue = eval("(" + jsonText + ")");
						}
					}
					break;
				default:
					returnValue = xmlhttp.responseText;
					break;
			}
			if(returnValue) {
				this.options.callback.call(this, returnValue);
			} else {
				this.options.callback.call(this);
			}
		}
	},

	// 发送Ajax请求  
	request: function(options) {
		var ajaxObj = this;
		// 设置参数  
		ajaxObj.setOptions.call(ajaxObj, options);
		// 创建XMLHttpRequest对象  
		var xmlhttp = ajaxObj.createRequest.call(ajaxObj);
		// 设置回调函数  
		xmlhttp.onreadystatechange = function() {
			ajaxObj.readystatechange.call(ajaxObj, xmlhttp);
		};
		// 格式化参数  
		var formateParams = ajaxObj.formateParameters.call(ajaxObj);
		// 请求的方式  
		var method = ajaxObj.options.method;
		var url = ajaxObj.options.url;
		if("GET" === method.toUpperCase()) {
			url += "?" + formateParams;
		}
		// 建立连接  
		xmlhttp.open(method, url, true);
		if("GET" === method.toUpperCase()) {
			xmlhttp.send(null);
		} else if("POST" === method.toUpperCase()) {
			// 如果是POST提交，设置请求头信息  
			xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
			xmlhttp.send(formateParams);
		}
	}
};

var AjaxGetUtil = {
	// 基础选项  
	options: {
		method: "get", // 默认提交的方法,get post  
		url: "", // 请求的路径 required  
		params: {}, // 请求的参数  
		async:false,//同步
		type: 'text', // 返回的内容的类型,text,xml,json  
		callback: function() {} // 回调函数 required  
	},

	// 创建XMLHttpRequest对象  
	createRequest: function() {
		var xmlhttp;
		try {
			xmlhttp = new ActiveXObject("Msxml2.XMLHTTP"); // IE6以上版本  
		} catch(e) {
			try {
				xmlhttp = new ActiveXObject("Microsoft.XMLHTTP"); // IE6以下版本  
			} catch(e) {
				try {
					xmlhttp = new XMLHttpRequest();
					if(xmlhttp.overrideMimeType) {
						xmlhttp.overrideMimeType("text/xml");
					}
				} catch(e) {
					alert("您的浏览器不支持Ajax");
				}
			}
		}
		return xmlhttp;
	},
	// 设置基础选项  
	setOptions: function(newOptions) {
		for(var pro in newOptions) {
			this.options[pro] = newOptions[pro];
		}
	},
	// 格式化请求参数  
	formateParameters: function() {
		var paramsArray = [];
		var params = this.options.params;
		for(var pro in params) {
			var paramValue = params[pro];
			/*if(this.options.method.toUpperCase() === "GET")  
			{  
			    paramValue = encodeURIComponent(params[pro]);  
			}*/
			paramsArray.push(pro + "=" + paramValue);
		}
		paramsArray.push("userToken=" + getCookie('userToken'));
		paramsArray.push("loginPCIp=" + returnCitySN["cip"]);
		return paramsArray.join("&");
	},

	// 状态改变的处理  
	readystatechange: function(xmlhttp) {
		var sessionstatus = xmlhttp.getResponseHeader("SESSIONSTATUS");
		if (sessionstatus == "TIMEOUT") {//超时跳转
			var win = window;
			while (win != win.top){
				win = win.top;
			}
			win.location.href = "login.html";//XMLHttpRequest.getResponseHeader("CONTEXTPATH");  
		}
		// 获取返回值  
		var returnValue;
		if(xmlhttp.readyState == 4 && (xmlhttp.status == 200 || xmlhttp.status == 0)) {
			switch(this.options.type) {
				case "xml":
					returnValue = xmlhttp.responseXML;
					break;
				case "json":
					var jsonText = xmlhttp.responseText;
					if(jsonText) {
						returnValue = eval("(" + jsonText + ")");
					}
					break;
				default:
					returnValue = xmlhttp.responseText;
					break;
			}
			if(returnValue) {
				this.options.callback.call(this, returnValue);
			} else {
				this.options.callback.call(this);
			}
		}
	},

	// 发送Ajax请求  
	request: function(options) {
		var ajaxObj = this;
		// 设置参数  
		ajaxObj.setOptions.call(ajaxObj, options);
		// 创建XMLHttpRequest对象  
		var xmlhttp = ajaxObj.createRequest.call(ajaxObj);
		// 设置回调函数  
		xmlhttp.onreadystatechange = function() {
			ajaxObj.readystatechange.call(ajaxObj, xmlhttp);
		};
		// 格式化参数  
		var formateParams = ajaxObj.formateParameters.call(ajaxObj);
		// 请求的方式  
		var method = ajaxObj.options.method;
		var url = ajaxObj.options.url;
		if("GET" === method.toUpperCase()) {
			url += "?" + formateParams;
		}
		// 建立连接  
		xmlhttp.open(method, url, true);
		if("GET" === method.toUpperCase()) {
			xmlhttp.send(null);
		} else if("POST" === method.toUpperCase()) {
			// 如果是POST提交，设置请求头信息  
			xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
			xmlhttp.send(formateParams);
		}
	}
};

function returnModel(lang){
	var mode = '';
	switch (lang) {
	case 'Java':
		mode = 'text/x-java';
		break;
	case 'C/C++':
		mode = 'text/x-c++src';
		break;
	case 'Objective-C':
		mode = '';
		break;
	case 'Scala':
		mode = 'text/x-scala';
		break;
	case 'Kotlin':
		mode = 'text/x-kotlin';
		break;
	case 'Ceylon':
		mode = 'text/x-ceylon';
		break;
	case 'xml':
		mode = 'xml';
		break;
	case 'html':
		mode = 'xml';
		break;
	case 'css':
		mode = 'text/css';
		break;
	case 'htmlmixed':
		mode = 'htmlmixed';
		break;
	case 'htmlhh':
		mode = 'htmlmixed';
		break;
	case 'javascript':
		mode = 'text/javascript';
		break;
	case 'nginx':
		mode = 'text/x-nginx-conf';
		break;
	case 'solr':
		mode = 'text/x-solr';
		break;
	case 'sql':
		mode = 'text/x-sql';
		break;
	case 'vue':
		mode = 'text/x-vue';
		break;
	}
	return mode;
}

/**
 * 替换代码生成器模板内容
 * @param str
 */
function replaceModelContent(str, ControllerPackageName, ServicePackageName, ServiceImplPackageName,
		DaoPackageName, tableZhName, tableFirstISlowerName, tableISlowerName, tableBzName){
	str = str.replace(/[$]{{controllerPackage}}/g, ControllerPackageName);
	str = str.replace(/[$]{{servicePackage}}/g, ServicePackageName);
	str = str.replace(/[$]{{serviceImplPackage}}/g, ServiceImplPackageName);
	str = str.replace(/[$]{{daoPackage}}/g, DaoPackageName);
	str = str.replace(/[$]{{tableName}}/g, tableZhName);
	str = str.replace(/[$]{{objectName}}/g, tableFirstISlowerName);
	str = str.replace(/[$]{{urlName}}/g, tableISlowerName);
	str = str.replace(/[$]{{notesName}}/g, tableBzName);
	return str;
}

function show(id, url) {
    var imgSuffix = ["png", "jpg", "jpeg", "gif", "bmp"];
    if (imgSuffix.indexOf(url.substring(url.lastIndexOf(".") + 1).toLowerCase()) < 0) {
        window.open(url);
        return false;
    }

    var imgs = layui.$(id).find("input[type='hidden'][name='upload']").val().split(",");
    var data = [];
    layui.$.each(imgs, function (k, v) {
        var suffix = v.substring(v.lastIndexOf(".") + 1);
        if (imgSuffix.indexOf(suffix.toLowerCase()) > -1) {
            var json = {
                "alt": "",
                "pid": k, //图片id
                "src": v, //原图地址
                "thumb": "" //缩略图地址
            }
            data.push(json);
        }
    })

    layer.photos({
        photos: {
            "title": "", //相册标题
            "id": 123, //相册id
            "start": 0, //初始显示的图片序号，默认0
            "data": data
        } //格式见API文档手册页
        , anim: 5 //0-6的选择，指定弹出图片动画类型，默认随机
    });
}

/** 
 * 时间戳格式化函数 
 * @param  {string} format    格式 
 * @param  {int}    timestamp 要格式化的时间 默认为当前时间 
 * @return {string}           格式化的时间字符串 
 */
function date(format, timestamp){  
    var a, jsdate=((timestamp) ? new Date(timestamp*1000) : new Date()); 
    var pad = function(n, c){ 
        if((n = n + "").length < c){ 
            return new Array(++c - n.length).join("0") + n; 
        } else { 
            return n; 
        } 
    }; 
    var txt_weekdays = ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"]; 
    var txt_ordin = {1:"st", 2:"nd", 3:"rd", 21:"st", 22:"nd", 23:"rd", 31:"st"}; 
    var txt_months = ["", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];  
    var f = { 
        d: function(){return pad(f.j(), 2)}, 
        D: function(){return f.l().substr(0,3)}, 
        j: function(){return jsdate.getDate()}, 
        l: function(){return txt_weekdays[f.w()]}, 
        N: function(){return f.w() + 1}, 
        S: function(){return txt_ordin[f.j()] ? txt_ordin[f.j()] : 'th'}, 
        w: function(){return jsdate.getDay()}, 
        z: function(){return (jsdate - new Date(jsdate.getFullYear() + "/1/1")) / 864e5 >> 0}, 
        W: function(){ 
            var a = f.z(), b = 364 + f.L() - a; 
            var nd2, nd = (new Date(jsdate.getFullYear() + "/1/1").getDay() || 7) - 1; 
            if(b <= 2 && ((jsdate.getDay() || 7) - 1) <= 2 - b){ 
                return 1; 
            } else{ 
                if(a <= 2 && nd >= 4 && a >= (6 - nd)){ 
                    nd2 = new Date(jsdate.getFullYear() - 1 + "/12/31"); 
                    return date("W", Math.round(nd2.getTime()/1000)); 
                } else{ 
                    return (1 + (nd <= 3 ? ((a + nd) / 7) : (a - (7 - nd)) / 7) >> 0); 
                } 
            } 
        }, 
        F: function(){return txt_months[f.n()]}, 
        m: function(){return pad(f.n(), 2)}, 
        M: function(){return f.F().substr(0,3)}, 
        n: function(){return jsdate.getMonth() + 1}, 
        t: function(){ 
            var n; 
            if( (n = jsdate.getMonth() + 1) == 2 ){ 
                return 28 + f.L(); 
            } else{ 
                if( n & 1 && n < 8 || !(n & 1) && n > 7 ){ 
                    return 31; 
                } else{ 
                    return 30; 
                } 
            } 
        }, 
        L: function(){var y = f.Y();return (!(y & 3) && (y % 1e2 || !(y % 4e2))) ? 1 : 0}, 
        Y: function(){return jsdate.getFullYear()}, 
        y: function(){return (jsdate.getFullYear() + "").slice(2)}, 
        a: function(){return jsdate.getHours() > 11 ? "pm" : "am"}, 
        A: function(){return f.a().toUpperCase()}, 
        B: function(){ 
            var off = (jsdate.getTimezoneOffset() + 60)*60; 
            var theSeconds = (jsdate.getHours() * 3600) + (jsdate.getMinutes() * 60) + jsdate.getSeconds() + off; 
            var beat = Math.floor(theSeconds/86.4); 
            if (beat > 1000) beat -= 1000; 
            if (beat < 0) beat += 1000; 
            if ((String(beat)).length == 1) beat = "00"+beat; 
            if ((String(beat)).length == 2) beat = "0"+beat; 
            return beat; 
        }, 
        g: function(){return jsdate.getHours() % 12 || 12}, 
        G: function(){return jsdate.getHours()}, 
        h: function(){return pad(f.g(), 2)}, 
        H: function(){return pad(jsdate.getHours(), 2)}, 
        i: function(){return pad(jsdate.getMinutes(), 2)}, 
        s: function(){return pad(jsdate.getSeconds(), 2)}, 
        O: function(){ 
            var t = pad(Math.abs(jsdate.getTimezoneOffset()/60*100), 4); 
            if (jsdate.getTimezoneOffset() > 0) t = "-" + t; else t = "+" + t; 
            return t; 
        }, 
        P: function(){var O = f.O();return (O.substr(0, 3) + ":" + O.substr(3, 2))}, 
        c: function(){return f.Y() + "-" + f.m() + "-" + f.d() + "T" + f.h() + ":" + f.i() + ":" + f.s() + f.P()}, 
        U: function(){return Math.round(jsdate.getTime()/1000)} 
    }; 
        
    return format.replace(/[\\]?([a-zA-Z])/g, function(t, s){ 
        if( t!=s ){ 
            ret = s; 
        } else if( f[s] ){ 
            ret = f[s](); 
        } else{ 
            ret = s; 
        } 
        return ret; 
    }); 
}


