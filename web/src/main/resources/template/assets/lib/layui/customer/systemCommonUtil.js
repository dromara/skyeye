// 系统工具函数
var systemCommonUtil = {

    /**
     * 已经选择的员工列表
     */
    staffChooseList: [],

    /**
     * 已经选择的图标资源列表
     */
    sysIconChooseClass: [],

    /**
     * 获取系统账户信息
     *
     * @param callback 回执函数
     */
    getSysAccountListByType: function (callback){
        AjaxPostUtil.request({url: flowableBasePath + "account009", params: {}, type: 'json', method: "GET", callback: function(json) {
            if(json.returnCode == 0) {
                if(typeof(callback) == "function") {
                    callback(json);
                }
            } else {
                winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
            }
        }, async: false});
    },

    /**
     * 判断当前登录用户是否可以申请转正
     */
    judgeCurrentUserRegularWorker: function () {
        var result = false;
        AjaxPostUtil.request({url: flowableBasePath + "judgeCurrentUserRegularWorker", params: {}, type: 'json', method: "GET", callback: function(json) {
            if(json.returnCode == 0) {
                result = json.bean.canApply;
            } else {
                winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
            }
        }, async: false});
        return result;
    },

    /**
     * 判断当前登录用户是否可以申请离职
     */
    judgeCurrentUserQuit: function () {
        var result = false;
        AjaxPostUtil.request({url: flowableBasePath + "judgeCurrentUserQuit", params: {}, type: 'json', method: "GET", callback: function(json) {
            if(json.returnCode == 0) {
                result = json.bean.canApply;
            } else {
                winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
            }
        }, async: false});
        return result;
    },

    /**
     * 获取当前登录用户所属企业的所有部门信息
     *
     * @param callback 回执函数
     */
    queryDepartmentListByCurrentUserBelong: function (callback){
        AjaxPostUtil.request({url: reqBasePath + "queryDepartmentListByCurrentUserBelong", params: {}, type: 'json', method: "GET", callback: function(json) {
            if(json.returnCode == 0) {
                if(typeof(callback) == "function") {
                    callback(json);
                }
            } else {
                winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
            }
        }, async: false});
    },

    /**
     * 根据部门id获取岗位集合
     *
     * @param callback 回执函数
     */
    queryJobListByDepartmentId: function (departmentId, callback){
        AjaxPostUtil.request({url: reqBasePath + "companyjob007", params: {departmentId: departmentId}, type: 'json', method: "GET", callback: function(json) {
            if(json.returnCode == 0) {
                if(typeof(callback) == "function") {
                    callback(json);
                }
            } else {
                winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
            }
        }, async: false});
    },

    /**
     * 获取当前登录员工信息
     *
     * @param callback 回执函数
     * @param errorCallback 接口返回失败时的回调函数
     */
    getSysCurrentLoginUserMation: function (callback, errorCallback){
        AjaxPostUtil.request({url: reqBasePath + "login002", params: {}, type: 'json', method: "POST", callback: function(json) {
            if(json.returnCode == 0) {
                if(typeof(callback) == "function") {
                    callback(json);
                }
            } else {
                if(typeof(errorCallback) == "function") {
                    errorCallback();
                } else {
                    winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
                }
            }
        }, async: false});
    },

    /**
     * 获取系统企业信息
     *
     * @param callback 回执函数
     */
    getSysCompanyList: function (callback){
        AjaxPostUtil.request({url: reqBasePath + "companymation008", params: {}, type: 'json', method: "GET", callback: function(json) {
            if(json.returnCode == 0) {
                if(typeof(callback) == "function") {
                    callback(json);
                }
            } else {
                winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
            }
        }, async: false});
    },

    /**
     * 获取系统收支项目信息
     *
     * @param type 收支项目类型  1.收入  2.支出
     * @param callback 回执函数
     */
    getSysInoutitemListByType: function (type, callback){
        AjaxPostUtil.request({url: flowableBasePath + "inoutitem007", params: {type: type}, type: 'json', method: "GET", callback: function(json) {
            if(json.returnCode == 0) {
                if(typeof(callback) == "function") {
                    callback(json);
                }
            } else {
                winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
            }
        }, async: false});
    },

    /**
     * 加载图片
     *
     * @param src 图片地址
     */
    showPicImg: function (src){
        var imagesList = [];
        imagesList.push({
            "alt": "",
            "pid": "skyeye", //图片id
            "src": src, //原图地址
            "thumb": "" //缩略图地址
        });
        layer.photos({
            photos: {
                "title": "", //相册标题
                "id": 123, //相册id
                "start": 0, //初始显示的图片序号，默认0
                "data": imagesList
            },
            anim: 5, //0-6的选择，指定弹出图片动画类型，默认随机
            tab: function () {
                var num = 0;
                $("#layui-layer-photos").parent().append('<div class="skyeye-image-operator">' +
                    '<button id="xuanzhuan" type="button" class="layui-btn layui-btn-normal layui-btn-xs">旋转</button>' +
                    '</div>');

                $(document).on("click", "#xuanzhuan", function(e) {
                    num = (num + 45) % 360;
                    $("#layui-layer-photos").css('transform', 'rotate(' + num + 'deg)');
                });

                $(document).on("mousewheel DOMMouseScroll", ".layui-layer-phimg", function (e) {
                    var delta = (e.originalEvent.wheelDelta && (e.originalEvent.wheelDelta > 0 ? 1 : -1)) || // chrome & ie
                        (e.originalEvent.detail && (e.originalEvent.detail > 0 ? -1 : 1)); // firefox
                    var imagep = $(".layui-layer-phimg").parent().parent();
                    var image = $(".layui-layer-phimg").parent();
                    var h = image.height();
                    var w = image.width();
                    if (delta > 0) {
                        if (h < (window.innerHeight)) {
                            h = h * 1.05;
                            w = w * 1.05;
                        }
                    } else if (delta < 0) {
                        if (h > 100) {
                            h = h * 0.95;
                            w = w * 0.95;
                        }
                    }
                    imagep.css("top", (window.innerHeight - h) / 2);
                    imagep.css("left", (window.innerWidth - w) / 2);
                    image.height(h);
                    image.width(w);
                    imagep.height(h);
                    imagep.width(w);
                });
            }
        });
    },

    /**
     * 员工选择页面(包含账号)
     *
     * @param callback 回调函数
     */
    openSysUserStaffChoosePage: function (callback){
        _openNewWindows({
            url: "../../tpl/common/sysusersel.html",
            title: "员工选择",
            pageId: "sysuserselpage",
            area: ['90vw', '90vh'],
            callBack: function(refreshCode){
                if (refreshCode == '0') {
                    if(typeof(callback) == "function") {
                        callback(systemCommonUtil.staffChooseList);
                    }
                } else if (refreshCode == '-9999') {
                    winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
                }
            }});
    },

    /**
     * 系统资源图标选择页面
     *
     * @param callback 回调函数
     */
    openSysEveIconChoosePage: function (callback){
        _openNewWindows({
            url: "../../tpl/sysEveIcon/sysEveIconListChoose.html",
            title: "图标选择",
            pageId: "sysEveIconListChoose",
            area: ['90vw', '90vh'],
            callBack: function(refreshCode){
                if (refreshCode == '0') {
                    if(typeof(callback) == "function") {
                        callback(systemCommonUtil.sysIconChooseClass);
                    }
                } else if (refreshCode == '-9999') {
                    winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
                }
            }});
    },

    /**
     * tagEditor组件移除所有tag
     *
     * @param id 对象id
     */
    tagEditorRemoveAll: function(id){
        var tags = $('#' + id).tagEditor('getTags')[0].tags;
        for (i = 0; i < tags.length; i++) {
            $('#' + id).tagEditor('removeTag', tags[i]);
        }
    },

    /**
     * 表格禁止指定行数据选择
     *
     * @param index 行坐标
     * @param type 'radio': 单选；'checkbox': 多选
     */
    disabledRow: function(index, type) {
        // 第index行复选框不可选
        let t = $(".layui-table tr[data-index=" + index + "] input[type='" + type + "']");
        t.prop('disabled', true);
        t.addClass('layui-btn-disabled');
        t.next().css("cursor", "not-allowed");
    },

    /**
     * 对页面url添加版本控制
     *
     * @param url
     */
    getHasVersionUrl: function (url) {
        // 判断是否有问号
        url += (url.indexOf("?") == -1 ? "?" : "&");
        url = url + 'v='+ skyeyeVersion;
        return url;
    },

    /**
     * 获取路径的访问地址
     *
     * @param url
     */
    getFilePath: function (url) {
        if (url.startsWith("../../assets/")) {
            return homePagePath + url;
        } else {
            return fileBasePath + url;
        }
    },

    // 员工在职状态
    sysUserStaffState: {
        "onTheJob": {"id": 1, "name": "在职(转正的员工)"},
        "quit": {"id": 2, "name": "离职"},
        "probation": {"id": 3, "name": "见习(用于实习生)"},
        "probationPeriod": {"id": 4, "name": "试用期(用于未转正的员工)"},
        "retire": {"id": 5, "name": "退休"}
    },

    getSysUserStaffStateList: function (){
        var list = [];
        $.each(systemCommonUtil.sysUserStaffState, function (key, value){
            list.push(value);
        });
        return list;
    }

};