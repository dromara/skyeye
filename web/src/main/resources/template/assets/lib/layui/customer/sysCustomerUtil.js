
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
            var _this = $("span[value='" + item.weekNumber + "']");
            if (!isNull(id)) {
                _this = $("#" + id).find("span[value='" + item.weekNumber + "']");
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

// 财务模块工具类
var sysIfsUtil = {

    /**
     * 会计科目选择页面
     *
     * @param callback 回调函数
     */
    chooseAccountSubjectMation: {}, // 已经选择的会计科目信息
    openSysAccountSubjectChoosePage: function (callback) {
        _openNewWindows({
            url: "../../tpl/ifsAccountSubject/ifsAccountSubjectListChoose.html",
            title: "会计科目选择",
            pageId: "ifsAccountSubjectListChoose",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                if (typeof (callback) == "function") {
                    callback(sysIfsUtil.chooseAccountSubjectMation);
                }
            }
        });
    },

    /**
     * 凭证选择页面
     *
     * @param callback 回调函数
     */
    chooseVoucherMation: {}, // 已经选择的凭证信息
    openIfsVoucherChoosePage: function (callback) {
        _openNewWindows({
            url: "../../tpl/ifsVoucher/ifsVoucherListChoose.html",
            title: "凭证选择",
            pageId: "ifsVoucherListChoose",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                if (typeof (callback) == "function") {
                    callback(sysIfsUtil.chooseVoucherMation);
                }
            }
        });
    },

    /**
     * 账套选择页面
     *
     * @param callback 回调函数
     */
    ifsSetOfBooksMation: {},
    openIfsSetOfBooksListChoosePage: function (callback) {
        _openNewWindows({
            url: "../../tpl/ifsSetOfBooks/ifsSetOfBooksListChoose.html",
            title: "账套选择",
            pageId: "ifsSetOfBooksListChoose",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                if (typeof (callback) == "function") {
                    callback(sysIfsUtil.ifsSetOfBooksMation);
                }
            }
        });
    }

}

// 系统文件相关处理工具
var sysFileUtil = {

    /**
     * 获取文件路径后缀
     *
     * @param url 文件路径
     */
    getFileExt: function (url) {
        return (/[.]/.exec(url)) ? /[^.]+$/.exec(url.toLowerCase()) : '';
    },

    /**
     * 下载
     * @param  {String} url 目标文件地址
     * @param  {String} filename 想要保存的文件名称
     */
    download: function (url, filename) {
        sysFileUtil.getBlob(url, function (blob) {
            sysFileUtil.saveAs(blob, filename);
        });
    },

    /**
     * 下载图片
     *
     * @param path
     * @param imgName
     */
    downloadImage: function (path, imgName) {
        var _OBJECT_URL;
        var request = new XMLHttpRequest();
        request.addEventListener('readystatechange', function (e) {
            if (request.readyState == 4) {
                _OBJECT_URL = URL.createObjectURL(request.response);
                var $a = $("<a></a>").attr("href", _OBJECT_URL).attr("download", imgName);
                $a[0].click();
            }
        });
        request.responseType = 'blob';
        request.open('get', path);
        request.send();
    },

    /**
     * 获取 blob
     * @param  {String} url 目标文件地址
     * @return {cb}
     */
    getBlob: function (url, cb) {
        var xhr = new XMLHttpRequest();
        xhr.open('GET', url, true);
        xhr.responseType = 'blob';
        xhr.onload = function () {
            if (xhr.status === 200) {
                cb(xhr.response);
            }
        };
        xhr.send();
    },

    /**
     * 根据文件路径转换成File对象
     *
     * @param url 文件路径
     * @param callback 回调函数
     */
    getFileByUrl: function (url, callback) {
        sysFileUtil.getBlob(url, function (blob) {
            const files = new File(
                [blob],
                sysFileUtil.getFileNameByUrl(url)
            );
            callback(files);
        });
    },

    /**
     * 根据文件路径获取文件名
     *
     * @param url 文件路径
     * @returns {*}
     */
    getFileNameByUrl: function (url) {
        // 通过\分隔字符串，成字符串数组
        var arr = url.split('\\');
        // 取最后一个，就是文件全名,含后缀
        var fileName = arr[arr.length - 1];
        return fileName;
    },

    /**
     * 保存
     * @param  {Blob} blob
     * @param  {String} filename 想要保存的文件名称
     */
    saveAs: function (blob, filename) {
        if (window.navigator.msSaveOrOpenBlob) {
            navigator.msSaveBlob(blob, filename);
        } else {
            var link = document.createElement('a');
            var body = document.querySelector('body');
            link.href = window.URL.createObjectURL(blob);
            link.download = filename;
            // fix Firefox
            link.style.display = 'none';
            body.appendChild(link);
            link.click();
            body.removeChild(link);
            window.URL.revokeObjectURL(link.href);
        }
        ;
    }

}

// 附件插件
var skyeyeEnclosure = {
    enclosureListKey: 'skyeyeJsonKey',
    enclosureBtnTemplate: '<button type="button" class="layui-btn layui-btn-primary layui-btn-xs" id="{{btnId}}">附件上传</button>',
    /**
     * 初始化附件插件，多个使用逗号隔开，只支持id
     *
     * @param ids 需要初始化的附件盒子的id
     * @param callback 回调函数
     */
    init: function (ids, callback) {
        var idsArray = ids.split(',');
        $.each(idsArray, function (i, id) {
            // 按钮id
            var btnId = id + "Btn";
            // 初始化数据为[]
            $("#" + id).attr(skyeyeEnclosure.enclosureListKey, JSON.stringify([]));
            // 加载dom对象
            skyeyeEnclosure.loadEnclosureHTML(id, btnId, 1);
            // 添加[附件上传]按钮的监听事件
            skyeyeEnclosure.initClick(id, btnId, callback);
        });
    },

    /**
     * 初始化附件插件，多个使用逗号隔开，只支持id
     *
     * @param param {需要初始化的附件盒子的id: 默认数据}
     * @param callback 回调函数
     */
    initTypeISData: function (param, callback) {
        $.each(param, function (boxId, data) {
            // 按钮id
            var btnId = boxId + "Btn";
            var dataList = skyeyeEnclosure.getDataList(data);
            // 初始化数据
            $("#" + boxId).attr(skyeyeEnclosure.enclosureListKey, JSON.stringify(dataList));
            // 加载dom对象
            skyeyeEnclosure.loadEnclosureHTML(boxId, btnId, 1);
            // 添加[附件上传]按钮的监听事件
            skyeyeEnclosure.initClick(boxId, btnId, callback);
        });
    },

    getDataList: function (data) {
        var dataList = [];
        if (!isNull(data)) {
            if (!isNull(data.enclosureInfoList)) {
                dataList = [].concat(data.enclosureInfoList);
            } else {
                dataList = [].concat(data);
            }
        }
        return dataList;
    },

    /**
     * 初始化点击事件
     *
     * @param id 盒子id
     * @param btnId 按钮id
     * @param callback 回调函数
     */
    initClick: function (id, btnId, callback){
        $("body").on("click", "#" + btnId, function() {
            _openNewWindows({
                url: "../../tpl/sysEnclosure/enclosureBusinessChoose.html?boxId=" + id,
                title: "上传附件",
                pageId: "enclosureBusinessChoose",
                area: ['70vw', '70vh'],
                callBack: function (refreshCode) {
                    // 重新加载dom对象
                    skyeyeEnclosure.loadEnclosureHTML(id, btnId, 1);
                    if(typeof(callback) == "function") {
                        callback(skyeyeEnclosure.getJSONEnclosureListByBoxId(id));
                    }
                }});
        });
    },

    /**
     * 附件详情展示
     *
     * @param param
     */
    showDetails: function (param) {
        $.each(param, function (boxId, data) {
            // 按钮id
            var btnId = boxId + "Btn";
            var dataList = skyeyeEnclosure.getDataList(data);
            // 初始化数据
            $("#" + boxId).attr(skyeyeEnclosure.enclosureListKey, JSON.stringify(dataList));
            // 加载dom对象
            skyeyeEnclosure.loadEnclosureHTML(boxId, btnId, 2);
        });
    },

    /**
     * 加载附件列表
     *
     * @param boxId 盒子id
     * @param btnId 按钮id
     * @param type 1.允许出现[附件上传]按钮 2.不允许出现[附件上传]按钮
     */
    loadEnclosureHTML: function (boxId, btnId, type){
        var enclosureList = skyeyeEnclosure.getJSONEnclosureListByBoxId(boxId);
        var str = "";
        $.each(enclosureList, function(i, item) {
            if(type == 1){
                str += '<br><a rowid="' + item.id + '" class="enclosureItem" rowpath="' + item.fileAddress + '" href="javascript:;" style="color:blue;">' + item.name + '</a>';
            } else {
                str += '<a rowid="' + item.id + '" class="enclosureItem" rowpath="' + item.fileAddress + '" href="javascript:;" style="color:blue;">' + item.name + '</a><br>';
            }
        });
        var btnHtml = "";
        if(type == 1){
            btnHtml += getDataUseHandlebars(skyeyeEnclosure.enclosureBtnTemplate, {btnId: btnId});
        }
        $("#" + boxId).html(btnHtml + str);

        // 加载点击下载事件
        skyeyeEnclosure.initDownloadEvent();
    },

    /**
     * 加载点击下载事件
     */
    initDownloadEvent: function () {

        $("body").on("click", ".enclosureItem", function() {
            download(fileBasePath + $(this).attr("rowpath"), $(this).html());
        });

    },

    /**
     * 获取指定id的附件
     *
     * @param id 盒子id
     */
    getJSONEnclosureListByBoxId: function (id) {
        return [].concat(JSON.parse($("#" + id).attr(skyeyeEnclosure.enclosureListKey)));
    },

    /**
     * 获取指定id的附件id，逗号隔开
     *
     * @param id 盒子id
     */
    getEnclosureIdsByBoxId: function (id){
        var enclosureList = skyeyeEnclosure.getJSONEnclosureListByBoxId(id);
        var enclosureInfo = "";
        $.each(enclosureList, function (i, item) {
            enclosureInfo += item.id + ',';
        })
        return enclosureInfo;
    }
};

// 集合工具类函数
var arrayUtil = {

    /**
     * 移除集合中指定name的元素
     *
     * @param list 集合
     * @param name 指定name
     */
    removeArrayPointName: function (list, name) {
        return arrayUtil.removeArrayPointKey(list, 'name', name);
    },

    /**
     * 移除集合中指定key的元素
     *
     * @param list 集合
     * @param key 指定key
     * @param value 指定的值
     */
    removeArrayPointKey: function (list, key, value) {
        var inArray = -1;
        $.each(list, function(i, item) {
            if(value === item[key]) {
                inArray = i;
                return false;
            }
        });
        if(inArray != -1) {
            list.splice(inArray, 1);
        }
        return [].concat(list);
    }

}