
//操作添加或者编辑时，判断表格是否需要刷新,为0则刷新，否则则不刷新
var refreshCode = "";

/**
 * 打开新的窗口
 * @param url
 * @param params
 * @param title
 */
function _openNewWindows(mation){
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
    var index = layer.load(1);
    refreshCode = "";
    layui.$.ajax({
        type: 'get',
        url: mation.url,
        async: true,
        success: function (data) {
            layer.close(index);
            //从桌面打开
//            top.winui.window.open
            layer.open({
            	id: mation.pageId,
                type: 2,
                title: mation.title,
                content: mation.url,
                area: [window.screen.width / 2 + 'px', window.screen.height / 2 + 'px'],
                offset: ['15vh', '20vw'],
                end: function(){
                	if(typeof(mation.callBack) == "function") {
                		mation.callBack(refreshCode);
        			}
                }
            });
        },
        error: function (xml) {
            layer.close(index);
            top.winui.window.msg("获取页面失败", {icon: 2,time: 2000});
        }
    });
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
		layui.$("#" + this._id + "").empty().html("<div class='row row-model' id='" + _op.settings.id + "showBody'></div><div class='row row-model' id='" + _op.settings.id + "showFoot' style='text-align: center;'><div class='pagec' id='pagearea'><ul class='pagination'></ul></div></div>");
	},
	//初始化元素
	createBodyNoFoot: function(pn) {
		if(typeof(_op.settings.ajaxSendBefore) == "function") {
			_op.settings.ajaxSendBefore(event);
		}
		var offset = (_op.settings.pageindex - 1) * _op.settings.pagesize;
		var pageParams = {
				offset: offset,
				limit: _op.settings.pagesize
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
				limit: _op.settings.pagesize
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
		param = '';
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
        	dataGrid_setting.splice(index, 1);
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
		if(i.id == id){
			return i.settings;
		}
	}
}

/**
 * 上传组件
 */
/**
 * Created by wzq on 2018/7/2.
 */
function fileUpload(param) {
	var pm = {
		fileType: "*", //文件类型限制，默认不限制，注意写的是文件后缀['png','jpg','JPG','gif']
		msg: '', //此处可以有温馨提示内容...
		maxFileNumber: 1, //文件个数限制，为整数
		uploadUrl: "#", //文件上传地址
		onUpload: function() {}, ////在上传后执行的函数
		async: true, //文件上传默认异步
		isHiddenUploadBt: true, //是否隐藏上传按钮
		isHiddenChooseBt: false
	};
	layui.$.extend(pm, param);
	initUpload({
		id: pm.id,
		"uploadUrl": pm.uploadUrl,
		"maxFileNumber": pm.maxFileNumber,
		"onUpload": pm.onUploadFun,
		"fileType": pm.fileType,
		"msg": pm.msg,
		"async": pm.async,
		isHiddenUploadBt: pm.isHiddenUploadBt, //是否隐藏上传按钮
		isHiddenChooseBt: pm.isHiddenChooseBt
	});
}
var initUpload = function(opt) {
	if(typeof opt != "object") {
		alert('参数错误!');
		return;
	}
	var uploadId = opt.id.replace("#", "");
	if(isNull(uploadId)) {
		alert("要设定一个id!");
		return;
	}

	layui.$.each(uploadTools.getInitOption(uploadId), function(key, value) {
		if(isNull(opt[key])) {
			opt[key] = value;
		}
	});
	uploadTools.flushOpt(opt);
	uploadTools.initWithLayout(opt); //初始化布局
	uploadTools.initWithDrag(opt); //初始化拖拽
	uploadTools.initWithSelectFile(opt); //初始化选择文件按钮
	uploadTools.initWithUpload(opt); //初始化上传
	uploadTools.initWithCleanFile(opt);
	uploadFileList.initFileList(opt);
}
/**
 * 上传基本工具和操作
 */
var uploadTools = {
	/**
	 * 基本配置参数
	 * @param uploadId
	 * @returns {{uploadId: *, url: string, autoCommit: string, canDrag: boolean, fileType: string, size: string, ismultiple: boolean, showSummerProgress: boolean}}
	 */
	"getInitOption": function(uploadId) {
		var initOption = {
			"uploadId": uploadId,
			"uploadUrl": "#", //必须，上传地址
			"selfUploadBtId": "", //自定义文件上传按钮id
			"scheduleStandard": false, //模拟进度的模式
			"autoCommit": false, //是否自动上传
			"isHiddenUploadBt": true, //是否隐藏上传按钮
			"isHiddenChooseBt": false, //是否隐藏选择文件行
			"isHiddenCleanBt": true, //是否隐藏清除按钮
			"isAutoClean": false, //是否上传完成后自动清除
			"canDrag": true, //是否可以拖动
			"velocity": 10,
			"fileType": "*", //文件类型
			"size": "-1", //文件大小限制,单位kB
			"ismultiple": true, //是否选择多文件
			"filelSavePath": "", //文件上传地址，后台设置的根目录
			"msg": "", //温馨提示内容
			"beforeUpload": function() { //在上传前面执行的回调函数
			},
			"onUpload": function() { //在上传之后
				//alert("hellos");
			},
			"onDelete": function() { //在删除之后
			}
		};
		return initOption;
	},
	/**
	 * 初始化文件上传
	 * @param opt
	 */
	"initWithUpload": function(opt) {
		var uploadId = opt.uploadId;
		if(!opt.isHiddenUploadBt) {
			layui.$("#" + uploadId + " .uploadBts .uploadFileBt").off();
			layui.$("#" + uploadId + " .uploadBts .uploadFileBt").on("click", function() {
				uploadEvent.uploadFileEvent(opt);
			});
			layui.$("#" + uploadId + " .uploadBts .uploadFileBt i").css("color", "#0099FF");
		}
		if(opt.selfUploadBtId != null && opt.selfUploadBtId != "") {
			if(uploadTools.foundExitById(opt.selfUploadBtId)) {
				layui.$("#" + opt.selfUploadBtId).off();
				layui.$("#" + opt.selfUploadBtId).on("click", function() {
					uploadEvent.uploadFileEvent(opt);
				});
			}
		}

	},
	/**
	 * 查找某个对象是否存在
	 * @param id
	 * @returns {boolean}
	 */
	"foundExitById": function(id) {
		return layui.$("#" + id).size() > 0;
	},
	/**
	 * 初始化清除文件
	 * @param opt
	 */
	"initWithCleanFile": function(opt) {
		var uploadId = opt.uploadId;
		if(!opt.isHiddenCleanBt) {
			layui.$("#" + uploadId + " .uploadBts .cleanFileBt").off();
			layui.$("#" + uploadId + " .uploadBts .cleanFileBt").on("click", function() {
				uploadEvent.cleanFileEvent(opt);
			});
			layui.$("#" + uploadId + " .uploadBts .cleanFileBt i").css("color", "#0099FF");
		}
	},
	/**
	 * 初始化选择文件按钮
	 * @param opt
	 */
	"initWithSelectFile": function(opt) {
		var uploadId = opt.uploadId;
		setTimeout(function() {
			layui.$("#" + uploadId + " .uploadBts .selectFileBt").off();
			layui.$("#" + uploadId + " .uploadBts .selectFileBt").on("click", function() {
				uploadEvent.selectFileEvent(opt);
			});
		}, 1000);

	},
	/**
	 * 返回显示文件类型的模板
	 * @param isImg 是否式图片：true/false
	 * @param fileType 文件类型
	 * @param fileName 文件名字
	 * @param isImgUrl 如果事文件时的文件地址默认为null
	 */
	"getShowFileType": function(isImg, fileType, fileName, isImgUrl, fileCodeId, delBtn) {
		if(delBtn == undefined) {
			delBtn = true;
		}
		var showTypeStr = "<div class='fileType'>" + fileType + "</div> <i class='iconfont icon-wenjian'></i>"; //默认显示类型
		if(isImg) {
			if(!isNull(isImgUrl)) { //图片显示类型
				showTypeStr = "<img src='" + isImgUrl + "'/>";
			}
		}
		var modelStr = "";
		if((fileCodeId + "").indexOf("liuq|") > -1) {
			fileCodeId = fileCodeId.substring(5);
			modelStr += "<div class='fileItem'  fileCodeId='" + fileCodeId + "' oldFile='" + fileCodeId + "' onclick='showFilemsg(\"" + fileCodeId + "\")'>";
		} else {
			modelStr += "<div class='fileItem'  fileCodeId='" + fileCodeId + "'>";
		}
		modelStr += "<div class='imgShow'>";
		modelStr += showTypeStr;
		modelStr += " </div>";
		modelStr += "<div class='status'>";
		if(delBtn) {
			modelStr += "<i class='iconfont icon-shanchu'></i>";
		}
		modelStr += "</div>";
		modelStr += " <div class='fileName'>";
		modelStr += fileName;
		modelStr += "</div>";
		modelStr += " </div>";
		return modelStr;
	},
	/**
	 * 初始化布局
	 * @param opt 参数对象
	 */
	"initWithLayout": function(opt) {
		var uploadId = opt.uploadId;
		//选择文件和上传按钮模板
		var btsStr = "";
		btsStr += "<div class='uploadBts'>";
		//上传按钮
		if(!opt.isHiddenChooseBt) {
			btsStr += "<div>";
			btsStr += "<div class='selectFileBt'>选择文件</div>";
			btsStr += "</div>";
		}

		//上传按钮
		if(!opt.isHiddenUploadBt) {
			btsStr += "<div class='uploadFileBt'>";
			btsStr += "<i class='iconfont icon-shangchuan'></i>";
			btsStr += " </div>";
		}
		//清理按钮
		if(!opt.isHiddenCleanBt) {
			btsStr += "<div class='cleanFileBt'>";
			btsStr += "<i class='iconfont icon-qingchu'></i>";
			btsStr += " </div>";
		}
		//提示内容
		btsStr += "<div class='file_msg' style='padding:10px;'>";
		btsStr += opt.msg;
		btsStr += " </div>";

		btsStr += "</div>";
		layui.$("#" + uploadId).append(btsStr);

		//添加文件显示框
		var boxStr = "<div class='box'></div>";
		layui.$("#" + uploadId).append(boxStr);
	},
	/**
	 * 初始化拖拽事件
	 * @param opt 参数对象
	 */
	"initWithDrag": function(opt) {
		var canDrag = opt.canDrag;
		var uploadId = opt.uploadId;
		if(canDrag) {
			layui.$(document).on({
				dragleave: function(e) { //拖离 
					e.preventDefault();
				},
				drop: function(e) { //拖后放 
					e.preventDefault();
				},
				dragenter: function(e) { //拖进 
					e.preventDefault();
				},
				dragover: function(e) { //拖来拖去 
					e.preventDefault();
				}
			});
			var box = layui.$("#" + uploadId + " .box").get(0);
			if(box != null) {
				//验证图片格式，大小，是否存在
				box.addEventListener("drop", function(e) {
					uploadEvent.dragListingEvent(e, opt);
				});
			}
		}
	},
	/**
	 * 删除文件
	 * @param opt
	 */
	"initWithDeleteFile": function(opt) {
		var uploadId = opt.uploadId;
		layui.$("#" + uploadId + " .fileItem .status i").off();
		layui.$("#" + uploadId + " .fileItem .status i").on("click", function() {
			uploadEvent.deleteFileEvent(opt, this);
			var deleteFileName = layui.$(this).parent().parent().attr('filecodeid');
			var fileInputHidden = layui.$("input[name=" + uploadId + "]").val();
			layui.$("input[name=" + uploadId + "]").val(fileInputHidden.replace(deleteFileName, '').replace('||', '|'));
			return false;
		})
	},
	/**
	 * 获取文件名后缀
	 * @param fileName 文件名全名
	 * */
	"getSuffixNameByFileName": function(fileName) {
		var str = fileName;
		var pos = str.lastIndexOf(".") + 1;
		var lastname = str.substring(pos, str.length);
		return lastname;
	},
	/**
	 * 判断某个值是否在这个数组内
	 * */
	"isInArray": function(strFound, arrays) {
		var ishave = false;
		for(var i = 0; i < arrays.length; i++) {
			if(strFound == arrays[i]) {
				ishave = true;
				break;
			}
		}
		return ishave;
	},
	/**
	 * 文件是否已经存在
	 * */
	"fileIsExit": function(file, opt) {
		var fileList = uploadFileList.getFileList(opt);
		var ishave = false;
		for(var i = 0; i < fileList.length; i++) {
			//文件名相同，文件大小相同
			if(fileList[i] != null && fileList[i].name == file.name && fileList[i].size == file.size) {
				ishave = true;
			}
		}
		return ishave;
	},
	/**
	 * 添加文件到列表
	 * */
	"addFileList": function(fileList, opt) {
		var uploadId = opt.uploadId;
		var boxJsObj = layui.$("#" + uploadId + " .box").get(0);
		var fileListArray = uploadFileList.getFileList(opt);
		var fileNumber = uploadTools.getFileNumber(opt);
		if(fileNumber + fileList.length > opt.maxFileNumber) {
			alert("最多只能上传" + opt.maxFileNumber + "个文件");
			return;
		}
		var imgtest = /image\/(\w)*/; //图片文件测试
		var fileTypeArray = opt.fileType; //文件类型集合
		var fileSizeLimit = opt.size; //文件大小限制
		for(var i = 0; i < fileList.length; i++) {
			//判断文件是否存在
			if(uploadTools.fileIsExit(fileList[i], opt)) {
				alert("文件（" + fileList[i].name + "）已经存在！");
				continue;
			}
			var fileTypeStr = uploadTools.getSuffixNameByFileName(fileList[i].name);
			//文件大小显示判断
			if(fileSizeLimit != -1 && fileList[i].size > (fileSizeLimit * 1000)) {
				alert("文件（" + fileList[i].name + "）超出了大小限制！请控制在" + fileSizeLimit + "KB内");
				continue;
			}
			//文件类型判断
			if(fileTypeArray == "*" || uploadTools.isInArray(fileTypeStr, fileTypeArray)) {
				var fileTypeUpcaseStr = fileTypeStr.toUpperCase();
				if(imgtest.test(fileList[i].type)) {
					//var imgUrlStr = window.webkitURL.createObjectURL(fileList[i]);//获取文件路径
					var imgUrlStr = ""; //获取文件路径
					if(window.createObjectURL != undefined) { // basic
						imgUrlStr = window.createObjectURL(fileList[i]);
					} else if(window.URL != undefined) { // mozilla(firefox)
						imgUrlStr = window.URL.createObjectURL(fileList[i]);
					} else if(window.webkitURL != undefined) { // webkit or chrome
						imgUrlStr = window.webkitURL.createObjectURL(fileList[i]);
					}
					var fileModel = uploadTools.getShowFileType(true, fileTypeUpcaseStr, fileList[i].name, imgUrlStr, fileListArray.length);
					layui.$(boxJsObj).append(fileModel);
				} else {
					var fileModel = uploadTools.getShowFileType(true, fileTypeUpcaseStr, fileList[i].name, null, fileListArray.length);
					layui.$(boxJsObj).append(fileModel);
				}
				uploadTools.initWithDeleteFile(opt);
				fileListArray[fileListArray.length] = fileList[i];
			} else {
				alert("不支持该格式文件上传:" + fileList[i].name);
			}
		}
		layui.$("#" + uploadId).trigger("check");
		uploadFileList.setFileList(fileListArray, opt);

	},
	/**
	 * 清除选择文件的input
	 * */
	"cleanFilInputWithSelectFile": function(opt) {
		var uploadId = opt.uploadId;
		layui.$("#" + uploadId + "_file").remove();
	},

	/**
	 * 上传文件失败集体显示
	 * @param opt
	 */
	"uploadError": function(opt) {
		var uploadId = opt.uploadId;
		layui.$("#" + uploadId + " .box .fileItem .status>i").addClass("iconfont icon-cha");
		var progressBar = layui.$("#" + uploadId + " .subberProgress .progress>div");
		progressBar.css("width", "0%");
		progressBar.html("0%");
	},
	/**
	 * 上传文件失败集体显示
	 * @param opt
	 */
	"uploadSuccess": function(opt) {
		var uploadId = opt.uploadId;
		layui.$("#" + uploadId + " .box .fileItem .status>i").off();
		layui.$("#" + uploadId + " .box .fileItem .status>i").addClass("iconfont icon-gou");
		var progressBar = layui.$("#" + uploadId + " .subberProgress .progress>div");
		progressBar.css("width", "0%");
		progressBar.html("0%");
	},
	/**
	 * 获取文件上传总数据量
	 * @param opt
	 * @returns {number}
	 */
	"getFilesDataAmount": function(opt) {
		var fileList = uploadFileList.getFileList(opt);
		var summer = 0;
		for(var i = 0; i < fileList.length; i++) {
			var fileItem = fileList[i];
			if(fileItem != null)
				summer = parseFloat(summer) + fileItem.size;
		}
		return summer;
	},
	/**
	 * 上传文件
	 */
	"uploadFile": function(opt) {
		var uploadUrl = opt.uploadUrl;
		var fileList = uploadFileList.getFileList(opt);

		var formData = new FormData();
		var fileNumber = uploadTools.getFileNumber(opt);
		if(fileNumber <= 0) {
			alert("没有文件，不支持上传");
			return;
		}

		for(var i = 0; i < fileList.length; i++) {
			if(fileList[i] != null) {
				formData.append("file", fileList[i]);
			}
		}
		if(opt.otherData != null && opt.otherData != "") {
			for(var j = 0; j < opt.otherData.length; j++) {
				formData.append(opt.otherData[j].name, opt.otherData[j].value);
			}
		}

		formData.append("filelSavePath", opt.filelSavePath);
		if(uploadUrl != "#" && uploadUrl != "") {
			uploadTools.disableFileUpload(opt); //禁用文件上传
			uploadTools.disableCleanFile(opt); //禁用清除文件
			layui.$.ajax({
				type: "post",
				url: uploadUrl,
				data: formData,
				async: opt.async,
				processData: false,
				contentType: false,
				success: function(data) {
					uploadTools.initWithCleanFile(opt);
					//setTimeout(function(){opt.onUpload(opt,data)},500);
					opt.onUpload(opt, data)
					if(opt.isAutoClean) {
						setTimeout(function() {
							uploadEvent.cleanFileEvent(opt);
						}, 2000);
					}
				},
				error: function(e) {

				}
			});

		} else {
			uploadTools.disableFileUpload(opt); //禁用文件上传
			uploadTools.disableCleanFile(opt); //禁用清除文件
		}
		if(opt.uploadUrl == "#" || opt.uploadUrl == "") {
			uploadTools.getFileUploadPregressMsg(opt);
		}

	},
	/**
	 *  获取文件上传进度信息
	 */
	"getFileUploadPregressMsg": function(opt) {
		var uploadId = opt.uploadId;
		layui.$("#" + uploadId + " .box .fileItem .status>i").removeClass();
		if(opt.uploadUrl == "#" || opt.uploadUrl == "") {
			if(opt.velocity == null || opt.velocity == "" || opt.velocity <= 0) {
				opt.velocity = 1;
			}
			var filesDataAmount = uploadTools.getFilesDataAmount(opt);
			var percent = 0;
			var bytesRead = 0;
			var intervalId = setInterval(function() {

				bytesRead += 5000 * parseFloat(opt.velocity);

				if(!opt.scheduleStandard) {
					percent = bytesRead / filesDataAmount * 100;
					percent = percent.toFixed(2);
					if(percent >= 100) {
						clearInterval(intervalId);
						percent = 100; //不能大于100
						uploadTools.initWithCleanFile(opt);
						uploadTools.uploadSuccess(opt);
					}
				} else {
					percent += parseFloat(opt.velocity);
					if(percent >= 100) {
						clearInterval(intervalId);
						percent = 100; //不能大于100
						uploadTools.initWithCleanFile(opt);
						uploadTools.uploadSuccess(opt);
					}
				}

			}, 500);
		}
	},
	/**
	 * 禁用文件上传
	 */
	"disableFileUpload": function(opt) {
		if(!opt.isHiddenUploadBt) {
			var uploadId = opt.uploadId;
			layui.$("#" + uploadId + " .uploadBts .uploadFileBt").off();
			layui.$("#" + uploadId + " .uploadBts .uploadFileBt i").css("color", "#DDDDDD");
		}
	},
	/**
	 * 禁用文件清除
	 */
	"disableCleanFile": function(opt) {
		if(!opt.isHiddenCleanBt) {
			var uploadId = opt.uploadId;
			layui.$("#" + uploadId + " .uploadBts .cleanFileBt").off();
			layui.$("#" + uploadId + " .uploadBts .cleanFileBt i").css("color", "#DDDDDD");
		}

	},
	/**
	 * 获取文件个数
	 * @param opt
	 */
	"getFileNumber": function(opt) {
		var number = 0;
		var fileList = uploadFileList.getFileList(opt);
		for(var i = 0; i < fileList.length; i++) {
			if(fileList[i] != null) {
				number++;
			}
		}
		return number;
	},
	"flushOpt": function(opt) {
		var uploadId = opt.uploadId;
		layui.$("#" + uploadId).data("opt", opt);
	},
	"getOpt": function(uploadId) {
		var opt = layui.$("#" + uploadId).data("opt");
		return opt;
	},
	//搞个假文件进去
	"showFile": function(imgString, opt, host, delBtn) {
		if(host == undefined) {
			host = "";
		}
		var imags = "JPG|GIF|PNG|JPEG";
		var S = imgString.split("|");
		for(var i = 0; i < S.length; i++) {
			var filename = S[i];
			if(filename != "") {
				var filetype = filename.substring(filename.lastIndexOf(".") + 1).toUpperCase();
				filename = filename.substring(0, filename.lastIndexOf("-")).substring(filename.lastIndexOf("/") + 1) + "." + filetype;
				// console.log(filename);
				if(imags.indexOf(filetype) > -1) {
					// console.log(host+S[i]);
					var fileModel = uploadTools.getShowFileType(true, filetype, filename, host + S[i], "liuq|" + S[i], delBtn);
					var boxJsObj = layui.$("#" + opt.uploadId + " .box").get(0);
					layui.$(boxJsObj).append(fileModel);
				} else {
					var fileModel = uploadTools.getShowFileType(true, filetype, filename, null, "liuq|" + S[i], delBtn);
					var boxJsObj = layui.$("#" + opt.uploadId + " .box").get(0);
					layui.$(boxJsObj).append(fileModel);
				}
			}

		}
		if(!opt.isHiddenDelBt) {
			uploadTools.initWithDeleteFile(opt);
		}

	},
};
/**
 * 上传事件操作
 * */
var uploadEvent = {
	/**
	 * 拖动时操作事件
	 */
	"dragListingEvent": function(e, opt) {

		e.preventDefault(); //取消默认浏览器拖拽效果 
		var fileList = e.dataTransfer.files; //获取文件对象
		uploadTools.addFileList(fileList, opt);
		if(opt.autoCommit) {
			uploadEvent.uploadFileEvent(opt);
		}

	},
	/**
	 * 删除文件对应的事件
	 * */
	"deleteFileEvent": function(opt, obj) {
		var fileItem = layui.$(obj).parent().parent();
		var fileCodeId = fileItem.attr("fileCodeId");
		var fileListArray = uploadFileList.getFileList(opt);
		delete fileListArray[fileCodeId];
		uploadFileList.setFileList(fileListArray, opt);
		fileItem.remove();

	},
	/**
	 * 选择文件按钮事件
	 * @param opt
	 */
	"selectFileEvent": function(opt) {
		var uploadId = opt.uploadId;
		var ismultiple = opt.ismultiple;
		var inputObj = document.createElement('input');
		inputObj.setAttribute('id', uploadId + '_file');
		inputObj.setAttribute('type', 'file');
		inputObj.setAttribute("style", 'visibility:hidden');
		if(ismultiple) { //是否选择多文件
			inputObj.setAttribute("multiple", "multiple");
		}
		layui.$(inputObj).on("change", function() {
			uploadEvent.selectFileChangeEvent(this.files, opt);
		});
		document.body.appendChild(inputObj);
		inputObj.click();
	},
	/**
	 * 选择文件后对文件的回调事件
	 * @param opt
	 */
	"selectFileChangeEvent": function(files, opt) {
		uploadTools.addFileList(files, opt);
		uploadTools.cleanFilInputWithSelectFile(opt);
		if(opt.autoCommit) {
			uploadEvent.uploadFileEvent(opt);
		}
	},
	/**
	 * 上传文件的事件
	 * */
	"uploadFileEvent": function(opt) {
		uploadTools.flushOpt(opt);
		if(opt.beforeUpload != null && (typeof opt.beforeUpload === "function")) {
			opt.beforeUpload(opt);
		}
		uploadTools.uploadFile(opt);
	},
	/**
	 * 清除文件事件
	 */
	"cleanFileEvent": function(opt) {
		var uploadId = opt.uploadId;
		if(opt.showSummerProgress) {
			layui.$("#" + uploadId + " .subberProgress").css("display", "none");
			layui.$("#" + uploadId + " .subberProgress .progress>div").css("width", "0%");
			layui.$("#" + uploadId + " .subberProgress .progress>div").html("0%");
		}
		uploadTools.cleanFilInputWithSelectFile(opt);
		uploadFileList.setFileList([], opt);
		layui.$("#" + uploadId + " .box").html("");
		uploadTools.initWithUpload(opt); //初始化上传
	}
};

var uploadFileList = {
	"initFileList": function(opt) {
		opt.fileList = new Array();
	},
	"getFileList": function(opt) {
		return opt.fileList;
	},
	"setFileList": function(fileList, opt) {
		opt.fileList = fileList;
		uploadTools.flushOpt(opt);
	}
};
var formTake = {
	"getData": function(formId) {
		var formData = {};
		var $form = layui.$("#" + formId);
		var input_doms = $form.find("input[name][ignore!='true'],textarea[name][ignore!='true']");
		var select_doms = $form.find("select[name][ignore!='true']");
		for(var i = 0; i < input_doms.length; i++) {
			var inputItem = input_doms.eq(i);
			var inputName = "";
			if(inputItem.attr("type") == "radio") {
				if(inputItem.is(":checked")) {
					inputName = inputItem.attr("name");
					formData[inputName] = layui.$.trim(inputItem.val());
				}
			} else {
				inputName = inputItem.attr("name");
				formData[inputName] = layui.$.trim(inputItem.val());
			}

		}
		for(var j = 0; j < select_doms.length; j++) {
			var selectItem = select_doms.eq(j);
			var selectName = selectItem.attr("name");
			formData[selectName] = layui.$.trim(selectItem.val());
		}
		return formData;
	},
	"getDataWithUploadFile": function(formId) {
		var formData = [];
		var $form = layui.$("#" + formId);
		var input_doms = $form.find("input[name][ignore!='true'],textarea[name][ignore!='true']");
		var select_doms = $form.find("select[name][ignore!='true']");
		for(var i = 0; i < input_doms.length; i++) {
			var inputItem = input_doms.eq(i);
			var inputName = "";
			if(inputItem.attr("type") == "radio") {
				if(inputItem.is(":checked")) {
					inputName = inputItem.attr("name");
					formData[formData.length] = {
						"name": inputName,
						"value": layui.$.trim(inputItem.val())
					}
				}
			} else {
				inputName = inputItem.attr("name");
				formData[formData.length] = {
					"name": inputName,
					"value": layui.$.trim(inputItem.val())
				}
			}
		}
		for(var j = 0; j < select_doms.length; j++) {
			var selectItem = select_doms.eq(j);
			var selectName = selectItem.attr("name");
			formData[formData.length] = {
				"name": selectName,
				"value": layui.$.trim(selectItem.val())
			}
		}
		return formData;
	}
};

function showFilemsg(file) {
	window.open(file);
}
	


