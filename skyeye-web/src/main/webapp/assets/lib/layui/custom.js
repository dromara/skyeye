
//操作添加或者编辑时，判断表格是否需要刷新,为0则刷新，否则则不刷新
var refreshCode = "";

/**
 * 打开新的窗口
 * @param url
 * @param params
 * @param title
 */
function _openNewWindows(mation){
	AjaxPostUtil.request({url:reqBasePath + "login002", params:{}, type:'json', callback:function(json){
		if(json.returnCode == 0){
			var index = layer.load(1);
			if(isNull(mation.url)){
				top.winui.window.msg("页面路径不能为空", {icon: 2,time: 2000});
				return;
			}
			if(isNull(mation.pageId)){
				top.winui.window.msg("缺少页面ID", {icon: 2,time: 2000});
				return;
			}
			if(isNull(mation.title)){
				mation.title = "窗口";
			}
			if(!isNull(mation.params)){
				var s = "";
				for(var param in mation.params)
					s += "&" + param + "=" + mation.params[param];
				mation.url = mation.url + "?" + s.slice(1);
			}
			if(isNull(mation.area)){
				mation.area = [window.screen.width / 2 + 'px', window.screen.height / 2 + 'px'];
			}
			if(isNull(mation.offset)){
				mation.offset = ['15vh', '20vw'];
			}
			if(isNull(mation.maxmin)){
				mation.maxmin = false;
			}
		    var index = layer.load(1);
		    refreshCode = "";
		    layui.$.ajax({
		        type: 'get',
		        url: mation.url,
		        async: true,
		        success: function (data) {
		            layer.close(index);
		            //从桌面打开
//		            top.winui.window.open
		            var pageIndex = layer.open({
		            	id: mation.pageId,
		                type: 2,
		                title: mation.title,
		                content: mation.url,
		                area: mation.area,
		                offset: mation.offset,
		                maxmin: mation.maxmin,
		                end: function(){
		                	if(typeof(mation.callBack) == "function") {
		                		mation.callBack(refreshCode);
		        			}
		                }
		            });
		            if(mation.maxmin){
		            	layer.full(pageIndex);
		            }
		        },
		        error: function (xml) {
		            layer.close(index);
		            top.winui.window.msg("获取页面失败", {icon: 2,time: 2000});
		        }
		    });
		}else{
			location.href = "login.html";
		}
	}});
}

/**
 * 非表格分页加载插件
 */
var dataGrid_setting = [];
var dataGrid = function(ele, opt) {
	this.defaults = {
		//id
		id: "",
		//请求url
		url: null,
		//模板
		template: null,
		//参数
		params: null,
		//是否分页
		pagination: false,
		//页显示
		pagesize: 10,
		//页索引
		pageindex: 1,
		//总页数
		totalpage: null,
		//点击分页之前的回调函数
		pageClickBefore: function(index){},
		//点击分页之后的回调函数
		pageClickAfter: function(index){},
		//ajax请求之前的回调函数
		ajaxSendBefore:function(json){},
		//ajax请求之后的加载数据之前的回调函数
		ajaxSendLoadBefore:function(hdb){},
		//ajax请求之后的回调函数
		ajaxSendAfter:function(json){},
		//按钮监听事件
		options:null,
		//handlber对象
		hdb:null
	}
	this.settings = layui.$.extend({}, this.defaults, opt);
}

dataGrid.prototype = {
	_id: null,
	_op: null,
	init: function() {
		this._id = this.settings.id;
		_op = this;
		_op.settings.hdb = Handlebars;
		this.create();
		this.bindEvent();
	},
	create: function() {
		//初始化元素
		if(this.settings.pagination){
			this.InitializeElement();
			this.createBody(1);//初始化动态行
		} else{
			this.createBodyNoFoot(1);//初始化动态行
		}
		//选择是否分页
		if(this.settings.pagination) {
			this.createFoot();
		}
	},
	bindEvent: function() {
		if(this.settings.pagination){
			//每页点击事件
			this.itemClickPage();
			//添加上一页事件
			this.registerUpPage();
			//添加下一页事件
			this.registerNextPage();
			//添加首页事件
			this.registerFirstPage();
			//添加最后一页事件
			this.registerlastPage();
			//添加跳转事件
			this.registerSkipPage();
		}
		//添加鼠标悬浮事件
		this.registermousehover();
		//添加全选全不选事件
		this.registercheckall();
	},
	//初始化元素
	InitializeElement: function() {
		layui.$("#" + this._id + "").empty().html("<div class='layui-col-xs12 row-model' id='" + _op.settings.id + "showBody'></div><div class='layui-col-xs12 row-model' id='" + _op.settings.id + "showFoot' style='text-align: center;'><div class='pagec layui-col-xs12' id='pagearea'><ul class='pagination layui-col-xs6'></ul></div></div>");
	},
	//初始化元素
	createBodyNoFoot: function(pn) {
		if(typeof(_op.settings.ajaxSendBefore) == "function") {
			_op.settings.ajaxSendBefore(event);
		}
		var offset = (_op.settings.pageindex - 1) * _op.settings.pagesize;
		var pageParams = {
				offset: offset,
				limit: _op.settings.pagesize,
				page: _op.settings.pageindex
		};
		_op.settings.params = layui.$.extend({}, _op.settings.params, pageParams);
		var json = this.getAjaxDate(_op.settings.url, _op.settings.params);
		//总页数=向上取整(总数/每页数)
		_op.settings.totalpage = Math.ceil((json.total) / _op.settings.pagesize);
		//开始页数
		var startPage = _op.settings.pagesize * (pn - 1);
		//结束页数
		var endPage = startPage + _op.settings.pagesize;
		if(typeof(_op.settings.ajaxSendLoadBefore) == "function") {
			_op.settings.ajaxSendLoadBefore(_op.settings.hdb);
		}
		var myTemplate = null;
		if(json.total == 0){
			myTemplate = _op.settings.hdb.compile(noBeansMation);
			layui.$("#" + _op.settings.id + "showFoot").hide();
		}else{
			layui.$("#" + _op.settings.id + "showFoot").show();
			myTemplate = _op.settings.hdb.compile(_op.settings.template);
		}
		layui.$("#" + this._id + "").empty().html(myTemplate(json));
		this.registermousehover();
		if(typeof(_op.settings.ajaxSendAfter) == "function") {
			_op.settings.ajaxSendAfter(json);
		}
		this.customClickPage(json);
	},
	//循环添加行
	createBody: function(pn) {
		if(typeof(_op.settings.ajaxSendBefore) == "function") {
			_op.settings.ajaxSendBefore(event);
		}
		var offset = (_op.settings.pageindex - 1) * _op.settings.pagesize;
		var pageParams = {
				offset: offset,
				limit: _op.settings.pagesize,
				page: _op.settings.pageindex
		};
		_op.settings.params = layui.$.extend({}, _op.settings.params, pageParams);
		var json = this.getAjaxDate(_op.settings.url, _op.settings.params);
		//总页数=向上取整(总数/每页数)
		_op.settings.totalpage = Math.ceil((json.total) / _op.settings.pagesize);
		//开始页数
		var startPage = _op.settings.pagesize * (pn - 1);
		//结束页数
		var endPage = startPage + _op.settings.pagesize;
		if(typeof(_op.settings.ajaxSendLoadBefore) == "function") {
			_op.settings.ajaxSendLoadBefore(_op.settings.hdb);
		}
		var myTemplate = null;
		if(json.total == 0){
			myTemplate = _op.settings.hdb.compile(noBeansMation);
			layui.$("#" + _op.settings.id + "showFoot").hide();
		}else{
			layui.$("#" + _op.settings.id + "showFoot").show();
			myTemplate = _op.settings.hdb.compile(_op.settings.template);
		}
		layui.$("#" + _op.settings.id + "showBody").empty().html(myTemplate(json));
		this.registermousehover();
		if(typeof(_op.settings.ajaxSendAfter) == "function") {
			_op.settings.ajaxSendAfter(json);
		}
		this.customClickPage(json);
	},
	//初始化分页
	createFoot: function() {
		var totalsubpageTmep = "";
		totalsubpageTmep += "<li class='ali'><a href='javascript:void(0);' data-go='' id='firstPage'><<</a></li>";
		totalsubpageTmep += "<li class='ali'><a href='javascript:void(0);' data-go='' id='UpPage'><</a></li>";
		// 页码大于等于4的时候，添加第一个页码元素
		if(_op.settings.pageindex != 1 && _op.settings.pageindex >= 4 && _op.settings.totalpage != 4) {
			totalsubpageTmep += "<li class='ali'><a href='javascript:void(0);' class='geraltTb_pager' data-go='' >" + 1 + "</a></li>";
		}
		/* 当前页码>4, 并且<=总页码，总页码>5，添加“···”*/
		if(_op.settings.pageindex - 2 > 2 && _op.settings.pageindex <= _op.settings.totalpage && _op.settings.totalpage > 5) {
			totalsubpageTmep += "<li class='ali'><a href='javascript:void(0);' class='geraltTb_' data-go='' >...</a></li>";
		}
		/* 当前页码的前两页 */
		var start = _op.settings.pageindex - 2;
		/* 当前页码的后两页 */
		var end = _op.settings.pageindex + 2;
		
		if((start > 1 && _op.settings.pageindex < 4) || _op.settings.pageindex == 1) {
			end++;
		}
		if(_op.settings.pageindex > _op.settings.totalpage - 4 && _op.settings.pageindex >= _op.settings.totalpage) {
			start--;
		}
		for(; start <= end; start++) {
			if(start <= _op.settings.totalpage && start >= 1) {
				totalsubpageTmep += "<li class='ali'><a href='javascript:void(0);' class='geraltTb_pager' data-go='' >" + start + "</a></li>";
			}
		}
		if(_op.settings.pageindex + 2 < _op.settings.totalpage - 1 && _op.settings.pageindex >= 1 && _op.settings.totalpage > 5) {
			totalsubpageTmep += "<li class='ali'><a href='javascript:void(0);' class='geraltTb_' data-go='' >...</a></li>";
		}
		if(_op.settings.pageindex != _op.settings.totalpage && _op.settings.pageindex < _op.settings.totalpage - 2 && _op.settings.totalpage != 4) {
			totalsubpageTmep += "<li class='ali'><a href='javascript:void(0);' class='geraltTb_pager' data-go='' >" + _op.settings.totalpage + "</a></li>";
		}
		totalsubpageTmep += "<li class='ali'><a href='javascript:void(0);' data-go='' id='nextPage'>></a></li>";
		totalsubpageTmep += "<li class='ali'><a href='javascript:void(0);' data-go='' id='lastPage'>>></a></li>";
		totalsubpageTmep += "<li class='ali'><a href='javascript:void(0);' class='pageBtn' data-go=''>第" + _op.settings.pageindex + "页   <input type='text' id='pageInput' class='pageInput'/><span id='skippage'>   跳转</span></a></li>";
		layui.$(".pagination").html(totalsubpageTmep);
	},
	//添加鼠标悬浮事件
	registermousehover: function() {
		//添加鼠标悬浮事件
	},
	//添加全选全不选事件
	registercheckall: function() {
		//添加全选全不选事件
	},
	//自定义按钮事件
	customClickPage: function(json){
		var options = _op.settings.options;
		for(var _option in options){
			if(typeof(options[_option]) == "function") {
				this.addEventHandler(layui.$(_option.split(" ")[1]), _option.split(" ")[0], this.clickCallFun, json, options[_option]);
			}
		}
	},
	//自定义点击执行事件
	clickCallFun: function(objs, json, fun){
		fun(objs.index(this), json.rows[objs.index(this)]);
	},
	/**
	 * 添加事件监听函数
	 * @param {Object} obj 要添加监听的对象或元素
	 * @param {Object} eventName 事件名
	 * @param {Object} fun 监听函数的名称
	 * @param {Object} param 给监听函数传的参数，这里就传了一个参数
	 */
	addEventHandler: function(objs, eventName, fun, param, callFun) {
		var fn = fun;
		var obj = null;
		for(var i = 0; i<objs.length; i++){
			obj = objs[i];
			if(param) {
				fn = function(e) {
					fun.call(this, objs, param, callFun); //继承监听函数,并传入参数以初始化;
				}
			}
			if(obj.attachEvent) {
				obj.attachEvent('on' + eventName, fn);
			} else if(obj.addEventListener) {
				obj.addEventListener(eventName, fn, false);
			} else {
				obj["on" + eventName] = fn;
			}
		}
	},
	//固定页点击事件
	itemClickPage: function(){
		layui.$("#" + _op.settings.id + "showFoot").delegate("a.geraltTb_pager", "click", function(selector) {
			_op.settings = getObject(selector.delegateTarget.id.replace('showFoot',''));
			var current = parseInt(layui.$(this).text());
			if(typeof(_op.settings.pageClickBefore) == "function") {
				_op.settings.pageClickBefore(_op.settings.pageindex);
			}
			_op.settings.pageindex = current;
			_op.createBody(_op.settings.pageindex);
			_op.createFoot();
			if(typeof(_op.settings.pageClickAfter) == "function") {
				_op.settings.pageClickAfter(current);
			}
		});
	},
	//添加首页事件
	registerFirstPage: function() {
		layui.$("#" + _op.settings.id + "showFoot").delegate("#firstPage", "click", function(selector) {
			_op.settings = getObject(selector.delegateTarget.id.replace('showFoot',''));
			if(typeof(_op.settings.pageClickBefore) == "function") {
				_op.settings.pageClickBefore(_op.settings.pageindex);
			}
			_op.settings.pageindex = 1;
			_op.createBody(_op.settings.pageindex);
			_op.createFoot();
			if(typeof(_op.settings.pageClickAfter) == "function") {
				_op.settings.pageClickAfter(_op.settings.pageindex);
			}
		});
	},
	//添加上一页事件
	registerUpPage: function() {
		layui.$("#" + _op.settings.id + "showFoot").delegate("#UpPage", "click", function(selector) {
			_op.settings = getObject(selector.delegateTarget.id.replace('showFoot',''));
			if(typeof(_op.settings.pageClickBefore) == "function") {
				_op.settings.pageClickBefore(_op.settings.pageindex);
			}
			if(_op.settings.pageindex == 1) {
				alert("已经是第一页了");
				return;
			}
			_op.settings.pageindex = _op.settings.pageindex - 1;
			_op.createBody(_op.settings.pageindex);
			_op.createFoot();
			if(typeof(_op.settings.pageClickAfter) == "function") {
				_op.settings.pageClickAfter(_op.settings.pageindex);
			}
		});
	},
	//添加下一页事件
	registerNextPage: function() {
		layui.$("#" + _op.settings.id + "showFoot").delegate("#nextPage", "click", function(selector) {
			_op.settings = getObject(selector.delegateTarget.id.replace('showFoot',''));
			if(typeof(_op.settings.pageClickBefore) == "function") {
				_op.settings.pageClickBefore(_op.settings.pageindex);
			}
			if(_op.settings.pageindex == _op.settings.totalpage) {
				alert("已经是最后一页了");
				return;
			}
			_op.settings.pageindex = _op.settings.pageindex + 1;
			_op.createBody(_op.settings.pageindex);
			_op.createFoot();
			if(typeof(_op.settings.pageClickAfter) == "function") {
				_op.settings.pageClickAfter(_op.settings.pageindex);
			}
		});
	},
	//添加尾页事件
	registerlastPage: function() {
		layui.$("#" + _op.settings.id + "showFoot").delegate("#lastPage", "click", function(selector) {
			_op.settings = getObject(selector.delegateTarget.id.replace('showFoot',''));
			if(typeof(_op.settings.pageClickBefore) == "function") {
				_op.settings.pageClickBefore(_op.settings.pageindex);
			}
			_op.settings.pageindex = _op.settings.totalpage;
			_op.createBody(_op.settings.totalpage);
			_op.createFoot();
			if(typeof(_op.settings.pageClickAfter) == "function") {
				_op.settings.pageClickAfter(_op.settings.pageindex);
			}
		});
	},
	//添加页数跳转事件
	registerSkipPage: function() {
		layui.$("#" + _op.settings.id + "showFoot").delegate("#skippage", "click", function(selector) {
			_op.settings = getObject(selector.delegateTarget.id.replace('showFoot',''));
			var value = layui.$("#pageInput").val();
			if(!isNaN(parseInt(value))) {
				if(parseInt(value) <= _op.settings.totalpage){
					if(typeof(_op.settings.pageClickBefore) == "function") {
						_op.settings.pageClickBefore(_op.settings.pageindex);
					}
					_op.settings.pageindex = value;
					_op.createBody(parseInt(_op.settings.pageindex));
					_op.createFoot();
					if(typeof(_op.settings.pageClickAfter) == "function") {
						_op.settings.pageClickAfter(value);
					}
				} 
				else alert("超出页总数");
			} else alert("请输入数字");
		});
	},
	//添加异步ajax事件
	getAjaxDate: function(url, parms) {
		//定义一个全局变量来接受$post的返回值
		var result;
		//用ajax的同步方式
		parms.userToken = getCookie('userToken');
		layui.$.ajax({
			url: url,
			async: false, //改为同步方式
			dataType: "json",
			data: parms,
			success: function(data) {
				result = data;
			}
		});
		return result;
	}
}

var showGrid = function (options) {
	var showGrid = new dataGrid(this, options);
	layui.$.each(dataGrid_setting, function(index, item){  
		if(!isNull(item)){
			if(item.settings.id == showGrid.settings.id){
				dataGrid_setting.splice(index, 1);
				return;
			}
		}
	});
	dataGrid_setting.push(_createObject(showGrid.settings.id, showGrid.settings));
	return showGrid.init();
}

var refreshGrid = function (id, option){
	var _option = layui.$.extend({}, getObject(id), option);
	var showGrid = new dataGrid(this, _option);
	layui.$.each(dataGrid_setting, function(index, item){  
        if(item.settings.id == showGrid.settings.id){
        	dataGrid_setting[index].settings.pageindex = 1;
        	showGrid.settings = dataGrid_setting[index].settings;
        	showGrid.settings.params = layui.$.extend({}, showGrid.settings.params, option.params);
        	dataGrid_setting.splice(index, 1, showGrid);
        	return;
		}
	});
	dataGrid_setting.push(_createObject(showGrid.settings.id, showGrid.settings));
	return showGrid.init();
}

var _createObject = function(id, settings){
    var obj = {
        id : id,
        settings : settings
    };
    return obj;
}

var getObject = function(id){
	for(var i in dataGrid_setting){
		if(dataGrid_setting[i].id == id){
			return dataGrid_setting[i].settings;
		}
	}
}

function showFilemsg(file) {
	window.open(file);
}

/**
 * 格式化js代码格式
 */
function do_js_beautify(str) {
	var js_source = str.replace(/^\s+/, '');
	if(js_source.length==0)
		return;
    var tabsize = '1';
    tabchar = '	';
    if (tabsize == 1)
    	tabchar = '\t';
    return js_beautify(js_source, tabsize, tabchar);
}

/*

JS Beautifier
---------------
 $Date: 2008-06-10 14:49:11 +0300 (Tue, 10 Jun 2008) $
 $Revision: 60 $


 Written by Einars "elfz" Lielmanis, <elfz@laacz.lv> 
     http://elfz.laacz.lv/beautify/

 Originally converted to javascript by Vital, <vital76@gmail.com> 
     http://my.opera.com/Vital/blog/2007/11/21/javascript-beautify-on-javascript-translated


 You are free to use this in any way you want, in case you find this useful or working for you.

 Usage:
   js_beautify(js_source_text);

*/


function js_beautify(js_source_text, indent_size, indent_character, indent_level)
{

   var input, output, token_text, last_type, last_text, last_word, current_mode, modes, indent_string;
   var whitespace, wordchar, punct, parser_pos, line_starters, in_case;
   var prefix, token_type, do_block_just_closed, var_line, var_line_tainted;



   function trim_output()
   {
       while (output.length && (output[output.length - 1] === ' ' || output[output.length - 1] === indent_string)) {
           output.pop();
       }
   }

   function print_newline(ignore_repeated)
   {
       ignore_repeated = typeof ignore_repeated === 'undefined' ? true: ignore_repeated;
       
       trim_output();

       if (!output.length) {
           return; // no newline on start of file
       }

       if (output[output.length - 1] !== "\n" || !ignore_repeated) {
           output.push("\n");
       }
       for (var i = 0; i < indent_level; i++) {
           output.push(indent_string);
       }
   }



   function print_space()
   {
       var last_output = output.length ? output[output.length - 1] : ' ';
       if (last_output !== ' ' && last_output !== '\n' && last_output !== indent_string) { // prevent occassional duplicate space
           output.push(' ');
       }
   }


   function print_token()
   {
       output.push(token_text);
   }

   function indent()
   {
       indent_level++;
   }


   function unindent()
   {
       if (indent_level) {
           indent_level--;
       }
   }


   function remove_indent()
   {
       if (output.length && output[output.length - 1] === indent_string) {
           output.pop();
       }
   }


   function set_mode(mode)
   {
       modes.push(current_mode);
       current_mode = mode;
   }


   function restore_mode()
   {
       do_block_just_closed = current_mode === 'DO_BLOCK';
       current_mode = modes.pop();
   }


   function in_array(what, arr)
   {
       for (var i = 0; i < arr.length; i++)
       {
           if (arr[i] === what) {
               return true;
           }
       }
       return false;
   }



   function get_next_token()
   {
       var n_newlines = 0;
       var c = '';

       do {
           if (parser_pos >= input.length) {
               return ['', 'TK_EOF'];
           }
           c = input.charAt(parser_pos);

           parser_pos += 1;
           if (c === "\n") {
               n_newlines += 1;
           }
       }
       while (in_array(c, whitespace));

       if (n_newlines > 1) {
           for (var i = 0; i < 2; i++) {
               print_newline(i === 0);
           }
       }
       var wanted_newline = (n_newlines === 1);


       if (in_array(c, wordchar)) {
           if (parser_pos < input.length) {
               while (in_array(input.charAt(parser_pos), wordchar)) {
                   c += input.charAt(parser_pos);
                   parser_pos += 1;
                   if (parser_pos === input.length) {
                       break;
                   }
               }
           }

           // small and surprisingly unugly hack for 1E-10 representation
           if (parser_pos !== input.length && c.match(/^[0-9]+[Ee]$/) && input.charAt(parser_pos) === '-') {
               parser_pos += 1;

               var t = get_next_token(parser_pos);
               c += '-' + t[0];
               return [c, 'TK_WORD'];
           }

           if (c === 'in') { // hack for 'in' operator
               return [c, 'TK_OPERATOR'];
           }
           return [c, 'TK_WORD'];
       }
       
       if (c === '(' || c === '[') {
           return [c, 'TK_START_EXPR'];
       }

       if (c === ')' || c === ']') {
           return [c, 'TK_END_EXPR'];
       }

       if (c === '{') {
           return [c, 'TK_START_BLOCK'];
       }

       if (c === '}') {
           return [c, 'TK_END_BLOCK'];
       }

       if (c === ';') {
           return [c, 'TK_END_COMMAND'];
       }

       if (c === '/') {
           var comment = '';
           // peek for comment /* ... */
           if (input.charAt(parser_pos) === '*') {
               parser_pos += 1;
               if (parser_pos < input.length) {
                   while (! (input.charAt(parser_pos) === '*' && input.charAt(parser_pos + 1) && input.charAt(parser_pos + 1) === '/') && parser_pos < input.length) {
                       comment += input.charAt(parser_pos);
                       parser_pos += 1;
                       if (parser_pos >= input.length) {
                           break;
                       }
                   }
               }
               parser_pos += 2;
               return ['/*' + comment + '*/', 'TK_BLOCK_COMMENT'];
           }
           // peek for comment // ...
           if (input.charAt(parser_pos) === '/') {
               comment = c;
               while (input.charAt(parser_pos) !== "\x0d" && input.charAt(parser_pos) !== "\x0a") {
                   comment += input.charAt(parser_pos);
                   parser_pos += 1;
                   if (parser_pos >= input.length) {
                       break;
                   }
               }
               parser_pos += 1;
               if (wanted_newline) {
                   print_newline();
               }
               return [comment, 'TK_COMMENT'];
           }

       }

       if (c === "'" || // string
       c === '"' || // string
       (c === '/' &&
       ((last_type === 'TK_WORD' && last_text === 'return') || (last_type === 'TK_START_EXPR' || last_type === 'TK_END_BLOCK' || last_type === 'TK_OPERATOR' || last_type === 'TK_EOF' || last_type === 'TK_END_COMMAND')))) { // regexp
           var sep = c;
           var esc = false;
           c = '';

           if (parser_pos < input.length) {

               while (esc || input.charAt(parser_pos) !== sep) {
                   c += input.charAt(parser_pos);
                   if (!esc) {
                       esc = input.charAt(parser_pos) === '\\';
                   } else {
                       esc = false;
                   }
                   parser_pos += 1;
                   if (parser_pos >= input.length) {
                       break;
                   }
               }

           }

           parser_pos += 1;
           if (last_type === 'TK_END_COMMAND') {
               print_newline();
           }
           return [sep + c + sep, 'TK_STRING'];
       }

       if (in_array(c, punct)) {
           while (parser_pos < input.length && in_array(c + input.charAt(parser_pos), punct)) {
               c += input.charAt(parser_pos);
               parser_pos += 1;
               if (parser_pos >= input.length) {
                   break;
               }
           }
           return [c, 'TK_OPERATOR'];
       }

       return [c, 'TK_UNKNOWN'];
   }


   //----------------------------------

   indent_character = indent_character || ' ';
   indent_size = indent_size || 4;

   indent_string = '';
   while (indent_size--) {
       indent_string += indent_character;
   }

   input = js_source_text;

   last_word = ''; // last 'TK_WORD' passed
   last_type = 'TK_START_EXPR'; // last token type
   last_text = ''; // last token text
   output = [];

   do_block_just_closed = false;
   var_line = false;
   var_line_tainted = false;

   whitespace = "\n\r\t ".split('');
   wordchar = 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_$'.split('');
   punct = '+ - * / % & ++ -- = += -= *= /= %= == === != !== > < >= <= >> << >>> >>>= >>= <<= && &= | || ! !! , : ? ^ ^= |='.split(' ');

   // words which should always start on new line.
   line_starters = 'continue,try,throw,return,var,if,switch,case,default,for,while,break,function'.split(',');

   // states showing if we are currently in expression (i.e. "if" case) - 'EXPRESSION', or in usual block (like, procedure), 'BLOCK'.
   // some formatting depends on that.
   current_mode = 'BLOCK';
   modes = [current_mode];

   indent_level = indent_level || 0;
   parser_pos = 0; // parser position
   in_case = false; // flag for parser that case/default has been processed, and next colon needs special attention
   while (true) {
       var t = get_next_token(parser_pos);
       token_text = t[0];
       token_type = t[1];
       if (token_type === 'TK_EOF') {
           break;
       }

       switch (token_type) {

       case 'TK_START_EXPR':
           var_line = false;
           set_mode('EXPRESSION');
           if (last_type === 'TK_END_EXPR' || last_type === 'TK_START_EXPR') {
               // do nothing on (( and )( and ][ and ]( ..
           } else if (last_type !== 'TK_WORD' && last_type !== 'TK_OPERATOR') {
               print_space();
           } else if (in_array(last_word, line_starters) && last_word !== 'function') {
               print_space();
           }
           print_token();
           break;

       case 'TK_END_EXPR':
           print_token();
           restore_mode();
           break;

       case 'TK_START_BLOCK':
           
           if (last_word === 'do') {
               set_mode('DO_BLOCK');
           } else {
               set_mode('BLOCK');
           }
           if (last_type !== 'TK_OPERATOR' && last_type !== 'TK_START_EXPR') {
               if (last_type === 'TK_START_BLOCK') {
                   print_newline();
               } else {
                   print_space();
               }
           }
           print_token();
           indent();
           break;

       case 'TK_END_BLOCK':
           if (last_type === 'TK_START_BLOCK') {
               // nothing
               trim_output();
               unindent();
           } else {
               unindent();
               print_newline();
           }
           print_token();
           restore_mode();
           break;

       case 'TK_WORD':

           if (do_block_just_closed) {
               print_space();
               print_token();
               print_space();
               break;
           }

           if (token_text === 'case' || token_text === 'default') {
               if (last_text === ':') {
                   // switch cases following one another
                   remove_indent();
               } else {
                   // case statement starts in the same line where switch
                   unindent();
                   print_newline();
                   indent();
               }
               print_token();
               in_case = true;
               break;
           }


           prefix = 'NONE';
           if (last_type === 'TK_END_BLOCK') {
               if (!in_array(token_text.toLowerCase(), ['else', 'catch', 'finally'])) {
                   prefix = 'NEWLINE';
               } else {
                   prefix = 'SPACE';
                   print_space();
               }
           } else if (last_type === 'TK_END_COMMAND' && (current_mode === 'BLOCK' || current_mode === 'DO_BLOCK')) {
               prefix = 'NEWLINE';
           } else if (last_type === 'TK_END_COMMAND' && current_mode === 'EXPRESSION') {
               prefix = 'SPACE';
           } else if (last_type === 'TK_WORD') {
               prefix = 'SPACE';
           } else if (last_type === 'TK_START_BLOCK') {
               prefix = 'NEWLINE';
           } else if (last_type === 'TK_END_EXPR') {
               print_space();
               prefix = 'NEWLINE';
           }

           if (last_type !== 'TK_END_BLOCK' && in_array(token_text.toLowerCase(), ['else', 'catch', 'finally'])) {
               print_newline();
           } else if (in_array(token_text, line_starters) || prefix === 'NEWLINE') {
               if (last_text === 'else') {
                   // no need to force newline on else break
                   print_space();
               } else if ((last_type === 'TK_START_EXPR' || last_text === '=') && token_text === 'function') {
                   // no need to force newline on 'function': (function
                   // DONOTHING
               } else if (last_type === 'TK_WORD' && (last_text === 'return' || last_text === 'throw')) {
                   // no newline between 'return nnn'
                   print_space();
               } else if (last_type !== 'TK_END_EXPR') {
                   if ((last_type !== 'TK_START_EXPR' || token_text !== 'var') && last_text !== ':') {
                       // no need to force newline on 'var': for (var x = 0...)
                       if (token_text === 'if' && last_type === 'TK_WORD' && last_word === 'else') {
                           // no newline for } else if {
                           print_space();
                       } else {
                           print_newline();
                       }
                   }
               } else {
                   if (in_array(token_text, line_starters) && last_text !== ')') {
                       print_newline();
                   }
               }
           } else if (prefix === 'SPACE') {
               print_space();
           }
           print_token();
           last_word = token_text;

           if (token_text === 'var') {
               var_line = true;
               var_line_tainted = false;
           }

           break;

       case 'TK_END_COMMAND':

           print_token();
           var_line = false;
           break;

       case 'TK_STRING':

           if (last_type === 'TK_START_BLOCK' || last_type === 'TK_END_BLOCK') {
               print_newline();
           } else if (last_type === 'TK_WORD') {
               print_space();
           }
           print_token();
           break;

       case 'TK_OPERATOR':

           var start_delim = true;
           var end_delim = true;
           if (var_line && token_text !== ',') {
               var_line_tainted = true;
               if (token_text === ':') {
                   var_line = false;
               }
           }

           if (token_text === ':' && in_case) {
               print_token(); // colon really asks for separate treatment
               print_newline();
               break;
           }

           in_case = false;

           if (token_text === ',') {
               if (var_line) {
                   if (var_line_tainted) {
                       print_token();
                       print_newline();
                       var_line_tainted = false;
                   } else {
                       print_token();
                       print_space();
                   }
               } else if (last_type === 'TK_END_BLOCK') {
                   print_token();
                   print_newline();
               } else {
                   if (current_mode === 'BLOCK') {
                       print_token();
                       print_newline();
                   } else {
                       // EXPR od DO_BLOCK
                       print_token();
                       print_space();
                   }
               }
               break;
           } else if (token_text === '--' || token_text === '++') { // unary operators special case
               if (last_text === ';') {
                   // space for (;; ++i)
                   start_delim = true;
                   end_delim = false;
               } else {
                   start_delim = false;
                   end_delim = false;
               }
           } else if (token_text === '!' && last_type === 'TK_START_EXPR') {
               // special case handling: if (!a)
               start_delim = false;
               end_delim = false;
           } else if (last_type === 'TK_OPERATOR') {
               start_delim = false;
               end_delim = false;
           } else if (last_type === 'TK_END_EXPR') {
               start_delim = true;
               end_delim = true;
           } else if (token_text === '.') {
               // decimal digits or object.property
               start_delim = false;
               end_delim = false;

           } else if (token_text === ':') {
               // zz: xx
               // can't differentiate ternary op, so for now it's a ? b: c; without space before colon
               if (last_text.match(/^\d+$/)) {
                   // a little help for ternary a ? 1 : 0;
                   start_delim = true;
               } else {
                   start_delim = false;
               }
           }
           if (start_delim) {
               print_space();
           }

           print_token();

           if (end_delim) {
               print_space();
           }
           break;

       case 'TK_BLOCK_COMMENT':

           print_newline();
           print_token();
           print_newline();
           break;

       case 'TK_COMMENT':

           // print_newline();
           print_space();
           print_token();
           print_newline();
           break;

       case 'TK_UNKNOWN':
           print_token();
           break;
       }

       last_type = token_type;
       last_text = token_text;
   }

   return output.join('');

}

/**
 * 根据数据展示
 */
function showDataUseHandlebars(id, source, data){
	//预编译模板
	var template = Handlebars.compile(source);
	//匹配json内容
	var html = template(data);
	//输入模板
	layui.$("#" + id).html(html);
}

function getDataUseHandlebars(source, data){
	//预编译模板
	var template = Handlebars.compile(source);
	//匹配json内容
	var html = template(data);
	//输入模板
	return html;
}


	


