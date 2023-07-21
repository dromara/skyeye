
// 客户工具类
var sysCustomerUtil = {

    /**
     * 已经选的的客户信息
     */
    customerMation: {},

    /**
     * 客户选择页面
     *
     * @param callback 回调函数
     */
    openSysCustomerChoosePage: function (callback) {
        _openNewWindows({
            url: "../../tpl/customerManage/customerChoose.html",
            title: "选择客户",
            pageId: "customerChoose",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                if(typeof(callback) == "function") {
                    callback(sysCustomerUtil.customerMation);
                }
            }});
    },

};


// 考勤相关工具类
var checkWorkUtil = {

    checkWorkTimeColor: ['layui-bg-gray', 'layui-bg-blue', 'layui-bg-orange'],

    /**
     * 获取当前登陆人的考勤班次
     *
     * @param callback 回执函数
     */
    getCurrentUserCheckWorkTimeList: function (callback) {
        AjaxPostUtil.request({url: sysMainMation.checkworkBasePath + "checkworktime007", params: {}, type: 'json', method: "GET", callback: function(json) {
            if(typeof(callback) == "function") {
                callback(json);
            }
        }, async: false});
    },

    // 类型为1初始化单休
    resetSingleBreak: function(id){
        var _box;
        if(isNull(id)){
            _box = $(".weekDay");
        } else {
            _box = $("#" + id + " .weekDay");
        }
        $.each(_box, function(i, item) {
            var clas = getArrIndexOfPointStr(checkWorkUtil.checkWorkTimeColor, $(item).attr("class"));
            $(item).removeClass(clas);
            if(i < 6){
                $(item).addClass('layui-bg-blue');
            } else {
                $(item).addClass('layui-bg-gray');
            }
        });
    },

    // 类型为2初始化双休
    resetWeekend: function(id){
        var _box;
        if (isNull(id)) {
            _box = $(".weekDay");
        } else {
            _box = $("#" + id + " .weekDay");
        }
        $.each(_box, function (i, item) {
            var clas = getArrIndexOfPointStr(checkWorkUtil.checkWorkTimeColor, $(item).attr("class"));
            $(item).removeClass(clas);
            if (i < 5) {
                $(item).addClass('layui-bg-blue');
            } else {
                $(item).addClass('layui-bg-gray');
            }
        });
    },

    // 类型为3初始化单双休
    resetSingleAndDoubleBreak: function(id){
        var _box;
        if (isNull(id)) {
            _box = $(".weekDay");
        } else {
            _box = $("#" + id + " .weekDay");
        }
        $.each(_box, function (i, item) {
            var clas = getArrIndexOfPointStr(checkWorkUtil.checkWorkTimeColor, $(item).attr("class"));
            $(item).removeClass(clas);
            if (i < 5) {
                $(item).addClass('layui-bg-blue');
            } else if (i == 5) {
                $(item).addClass('layui-bg-orange');
            } else {
                $(item).addClass('layui-bg-gray');
            }
        });
    },

    // 类型为4初始化自定休
    resetCustomizeDay: function(days, id){
        checkWorkUtil.resetCustomize(id);
        if (isNull(days)) {
            return;
        }
        $.each(days, function (i, item) {
            var _this = $("span[value='" + item.day + "']");
            if (!isNull(id)) {
                _this = $("#" + id).find("span[value='" + item.day + "']");
            }
            var clas = getArrIndexOfPointStr(checkWorkUtil.checkWorkTimeColor, _this.attr("class"));
            _this.removeClass(clas);
            if (item.type == 1) {
                _this.addClass('layui-bg-blue');
            } else if (item.type == 2) {
                _this.addClass('layui-bg-orange');
            }
        });
    },

    // 类型为4初始化自定休
    resetCustomize: function(id){
        var _box;
        if (isNull(id)) {
            _box = $(".weekDay");
        } else {
            _box = $("#" + id + " .weekDay");
        }
        $.each(_box, function (i, item) {
            var clas = getArrIndexOfPointStr(checkWorkUtil.checkWorkTimeColor, $(item).attr("class"));
            $(item).removeClass(clas);
            $(item).addClass('layui-bg-gray');
        });
    }

};

// 招聘模块工具函数
var bossUtil = {

    /**
     * 打开我负责的未入职的面试者选择页面--表单组件中使用
     *
     * @param callback 回调函数
     */
    openBossIntervieweeChoosePage: function (callback) {
        _openNewWindows({
            url: systemCommonUtil.getUrl('FP2023061200001', null),
            title: "面试者",
            pageId: "myChargeBossIntervieweeListChoose",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                if(typeof(callback) == "function") {
                    callback(chooseItemMation);
                }
            }});
    },

    /**
     * 打开我负责的人员需求选择页面--表单组件中使用
     *
     * @param callback 回调函数
     */
    bossPersonRequireChooseMation: {}, // 已经选择的人员需求信息
    openBossPersonRequireChoosePage: function (callback) {
        _openNewWindows({
            url: "../../tpl/bossPersonRequire/bossPersonRequireMyChargeListChoose.html",
            title: "人员需求",
            pageId: "bossPersonRequireMyChargeListChoose",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                if(typeof(callback) == "function") {
                    callback(bossUtil.bossPersonRequireChooseMation);
                }
            }});
    }

};

// 项目管理相关工具类
var proUtil = {

    /**
     * 获取我参与的项目列表
     *
     * @param callback 回执函数
     */
    queryMyProjectsList: function (callback) {
        AjaxPostUtil.request({url: flowableBasePath + "queryMyProjectsList", params: {}, type: 'json', method: "GET", callback: function(json) {
            if(typeof(callback) == "function") {
                callback(json);
            }
        }, async: false});
    },

};