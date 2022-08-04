/**
 * 非表格分页加载插件
 */
// showmodel.js没有数据时的默认展示
var noBeansMation = "<div class='noMation col-lg-12 col-sm-12 col-xs-12'><img src='../../assets/images/noMation.png' style='max-width:100px'/><br/><font class='noMationFont'>暂无数据</font></div>";
var noMatchingBeansMation = "<div class='noMation col-lg-12 col-sm-12 col-xs-12'><img src='../../assets/images/noMation.png' style='max-width:100px'/><br/><font class='noMationFont'>暂无匹配项</font></div>";

var dataGrid_setting = [];
var dataGrid = function(ele, opt) {
    this.defaults = {
        // id
        id: "",
        // 请求url
        url: null,
        // 如果url为空，则加载data数据
        data: null,
        // 模板
        template: null,
        // 请求类型
        method: "POST",
        // 参数
        params: null,
        // 是否分页
        pagination: false,
        // 页显示
        pagesize: 10,
        // 页索引
        pageindex: 1,
        // 总页数
        totalpage: null,
        // 点击分页之前的回调函数
        pageClickBefore: function(index){},
        // 点击分页之后的回调函数
        pageClickAfter: function(index){},
        // ajax请求之前的回调函数
        ajaxSendBefore:function (json) {},
        // ajax请求之后的加载数据之前的回调函数
        ajaxSendLoadBefore:function(hdb, json){},
        // ajax请求之后的回调函数
        ajaxSendAfter:function (json) {},
        // ajax请求之后加载错误的回调函数
        ajaxSendErrorAfter:function (json) {},
        // 按钮监听事件
        options:null,
        // handlber对象
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
        var json = this.getAjaxDate(_op.settings.url, _op.settings.params, _op.settings.method);
        if(json.returnCode === '0' || json.returnCode === 0){
            //总页数=向上取整(总数/每页数)
            _op.settings.totalpage = Math.ceil((json.total) / _op.settings.pagesize);
            //开始页数
            var startPage = _op.settings.pagesize * (pn - 1);
            //结束页数
            var endPage = startPage + _op.settings.pagesize;
            if(typeof(_op.settings.ajaxSendLoadBefore) == "function") {
                _op.settings.ajaxSendLoadBefore(_op.settings.hdb, json);
            }
            var myTemplate = null;
            if(json.total == 0){
                myTemplate = _op.settings.hdb.compile(noBeansMation);
                layui.$("#" + _op.settings.id + "showFoot").hide();
            } else {
                layui.$("#" + _op.settings.id + "showFoot").show();
                myTemplate = _op.settings.hdb.compile(_op.settings.template);
            }
            layui.$("#" + this._id + "").empty().html(myTemplate(json));
            this.registermousehover();
            this.customClickPage(json);
            if(typeof(_op.settings.ajaxSendAfter) == "function") {
                _op.settings.ajaxSendAfter(json);
            }
        } else {
            winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
            if(typeof(_op.settings.ajaxSendErrorAfter) == "function") {
                _op.settings.ajaxSendErrorAfter(json);
            }
        }
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
        var json = this.getAjaxDate(_op.settings.url, _op.settings.params, _op.settings.method);
        if(json.returnCode === '0' || json.returnCode === 0){
            //总页数=向上取整(总数/每页数)
            _op.settings.totalpage = Math.ceil((json.total) / _op.settings.pagesize);
            //开始页数
            var startPage = _op.settings.pagesize * (pn - 1);
            //结束页数
            var endPage = startPage + _op.settings.pagesize;
            if(typeof(_op.settings.ajaxSendLoadBefore) == "function") {
                _op.settings.ajaxSendLoadBefore(_op.settings.hdb, json);
            }
            var myTemplate = null;
            if(json.total == 0){
                myTemplate = _op.settings.hdb.compile(noBeansMation);
                layui.$("#" + _op.settings.id + "showFoot").hide();
            } else {
                _op.settings.total = json.total;
                layui.$("#" + _op.settings.id + "showFoot").show();
                myTemplate = _op.settings.hdb.compile(_op.settings.template);
            }
            layui.$("#" + _op.settings.id + "showBody").empty().html(myTemplate(json));
            this.registermousehover();
            this.customClickPage(json);
            if(typeof(_op.settings.ajaxSendAfter) == "function") {
                _op.settings.ajaxSendAfter(json);
            }
        } else {
            winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
            if(typeof(_op.settings.ajaxSendErrorAfter) == "function") {
                _op.settings.ajaxSendErrorAfter(json);
            }
        }
    },
    //初始化分页
    createFoot: function() {
        var totalsubpageTmep = "";
        if(1 === _op.settings.pageindex){
            totalsubpageTmep += "<li class='ali'><a href='javascript:void(0);' class='page_btn_dis' data-go='' id='firstPage'><<</a></li>";
            totalsubpageTmep += "<li class='ali'><a href='javascript:void(0);' class='page_btn_dis' data-go='' id='UpPage'><</a></li>";
        } else {
            totalsubpageTmep += "<li class='ali'><a href='javascript:void(0);' class='page_btn' data-go='' id='firstPage'><<</a></li>";
            totalsubpageTmep += "<li class='ali'><a href='javascript:void(0);' class='page_btn' data-go='' id='UpPage'><</a></li>";
        }
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
                if(_op.settings.pageindex == start){
                    totalsubpageTmep += "<li class='ali'><a href='javascript:void(0);' class='geraltTb_pager showgrid-active' data-go='' >" + start + "</a></li>";
                } else {
                    totalsubpageTmep += "<li class='ali'><a href='javascript:void(0);' class='geraltTb_pager' data-go='' >" + start + "</a></li>";
                }
            }
        }
        if(_op.settings.pageindex + 2 < _op.settings.totalpage - 1 && _op.settings.pageindex >= 1 && _op.settings.totalpage > 5) {
            totalsubpageTmep += "<li class='ali'><a href='javascript:void(0);' class='geraltTb_' data-go='' >...</a></li>";
        }
        if(_op.settings.pageindex != _op.settings.totalpage && _op.settings.pageindex < _op.settings.totalpage - 2 && _op.settings.totalpage != 4) {
            totalsubpageTmep += "<li class='ali'><a href='javascript:void(0);' class='geraltTb_pager' data-go='' >" + _op.settings.totalpage + "</a></li>";
        }
        if(_op.settings.totalpage === _op.settings.pageindex){
            totalsubpageTmep += "<li class='ali'><a href='javascript:void(0);' class='page_btn_dis' data-go=''>></a></li>";
            totalsubpageTmep += "<li class='ali'><a href='javascript:void(0);' class='page_btn_dis' data-go=''>>></a></li>";
        } else {
            totalsubpageTmep += "<li class='ali'><a href='javascript:void(0);' class='page_btn' data-go='' id='nextPage'>></a></li>";
            totalsubpageTmep += "<li class='ali'><a href='javascript:void(0);' class='page_btn' data-go='' id='lastPage'>>></a></li>";
        }
        totalsubpageTmep += "<li class='ali'><a href='javascript:void(0);' class='pageBtn' data-go=''>" + systemLanguage["com.skyeye.toThe"][languageType]
            + "&nbsp;&nbsp;<input type='text' id='pageInput' value='"
            + _op.settings.pageindex + "' class='pageInput'/>&nbsp;&nbsp;" + systemLanguage["com.skyeye.page2"][languageType]
            + "<span id='skippage'>" + systemLanguage["com.skyeye.determine"][languageType]
            + "</span>&nbsp;&nbsp;" + systemLanguage["com.skyeye.all"][languageType] + "&nbsp;&nbsp;"
            + _op.settings.total + "&nbsp;&nbsp;" + systemLanguage["com.skyeye.total"][languageType] + "</a></li>";
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
    customClickPage: function (json) {
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
                    _op.settings.pageindex = parseInt(value);
                    _op.createBody(parseInt(_op.settings.totalpage));
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
    getAjaxDate: function(url, parms, method) {
        //定义一个全局变量来接受$post的返回值
        var result;
        if(!isNull(url)){
            //用ajax的同步方式
            layui.$.ajax({
                url: url,
                async: false, // 改为同步方式
                dataType: "json",
                headers: getRequestHeaders(),
                type: method,
                data: parms,
                beforeSend: function () {
                    $("body").append(maskReqStr);
                },
                success: function(data) {
                    //移除请求遮罩层
                    $("body").find(".mask-req-str").remove();
                    result = data;
                },
                error: function(XMLHttpRequest, textStatus, xhr){
                    //移除请求遮罩层
                    $("body").find(".mask-req-str").remove();
                    var sessionstatus = XMLHttpRequest.getResponseHeader('SESSIONSTATUS');
                    if (sessionstatus == "TIMEOUT") {//超时跳转
                        var win = window;
                        while (win != win.top) {
                            win = win.top;
                        }
                        result = eval('(' + '{"returnMessage":"登录超时。","returnCode":-9999,"total":0,"rows":"","bean":""}' + ')');
                        win.location.href = "../../tpl/index/login.html";//XMLHttpRequest.getResponseHeader("CONTEXTPATH");
                    } else if (sessionstatus == "NOAUTHPOINT") {
                        result = eval('(' + '{"returnMessage":"您不具备该权限。","returnCode":-9999,"total":0,"rows":"","bean":""}' + ')');
                    } else {
                        result = eval('(' + '{"returnMessage": url + "服务不存在。","returnCode":-9999,"total":0,"rows":"","bean":""}' + ')');
                    }
                }
            });
        } else{
            result = _op.settings.data;
        }
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