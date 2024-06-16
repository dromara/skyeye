// 系统工具函数
var systemCommonUtil = {

    /**
     * 获取系统账户信息
     *
     * @param callback 回执函数
     */
    getSysAccountListByType: function (callback) {
        AjaxPostUtil.request({url: sysMainMation.ifsBasePath + "account009", params: {}, type: 'json', method: "GET", callback: function(json) {
            if (typeof(callback) == "function") {
                callback(json);
            }
        }, async: false});
    },

    /**
     * 获取系统桌面信息
     *
     * @param callback 回执函数
     */
    getSysDesttop: function (callback) {
        AjaxPostUtil.request({url: reqBasePath + "desktop011", params: {}, type: 'json', method: "GET", callback: function(json) {
            if (typeof(callback) == "function") {
                callback(json);
            }
        }, async: false});
    },

    /**
     * 判断当前登录用户是否可以申请转正
     */
    judgeCurrentUserRegularWorker: function () {
        var result = false;
        AjaxPostUtil.request({url: flowableBasePath + "judgeCurrentUserRegularWorker", params: {}, type: 'json', method: "GET", callback: function(json) {
            result = json.bean.canApply;
        }, async: false});
        return result;
    },

    /**
     * 获取当前登录用户所属企业的所有部门信息
     *
     * @param callback 回执函数
     */
    queryDepartmentListByCurrentUserBelong: function (callback) {
        AjaxPostUtil.request({url: reqBasePath + "queryDepartmentListByCurrentUserBelong", params: {}, type: 'json', method: "GET", callback: function(json) {
            if (typeof(callback) == "function") {
                callback(json);
            }
        }, async: false});
    },

    /**
     * 根据部门id获取岗位集合(组件中使用)
     *
     * @param callback 回执函数
     */
    queryJobListByDepartmentId: function (departmentId, callback){
        if (isNull(departmentId)) {
            if (typeof(callback) == "function") {
                callback({});
            }
        } else {
            AjaxPostUtil.request({url: reqBasePath + "companyjob007", params: {departmentId: departmentId}, type: 'json', method: "GET", callback: function(json) {
                if (typeof(callback) == "function") {
                    callback(json);
                }
            }, async: false});
        }
    },

    /**
     * 获取当前登录员工信息
     *
     * @param callback 回执函数
     * @param errorCallback 接口返回失败时的回调函数
     */
    getSysCurrentLoginUserMation: function (callback, errorCallback){
        AjaxPostUtil.request({url: reqBasePath + "login002", params: {}, type: 'json', method: "POST", callback: function(json) {
            if (typeof(callback) == "function") {
                callback(json);
            }
        }, errorCallback: function (json) {
            if (typeof(errorCallback) == "function") {
                errorCallback();
            } else {
                winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
            }
        }, async: false});
    },

    /**
     * 获取系统企业信息
     *
     * @param callback 回执函数
     */
    getSysCompanyList: function (callback) {
        AjaxPostUtil.request({url: reqBasePath + "companymation008", params: {}, type: 'json', method: "GET", callback: function(json) {
            if (typeof(callback) == "function") {
                callback(json);
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
            if (typeof(callback) == "function") {
                callback(json);
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
                        h = h * 1.05;
                        w = w * 1.05;
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
    chooseOrNotMy: 1, // 人员列表中是否包含自己--1.包含；其他参数不包含
    chooseOrNotEmail: 1, // 人员列表中是否必须绑定邮箱--1.必须；其他参数没必要
    checkType: 1, // 人员选择类型，1.多选；其他。单选
    userReturnList: [], // 人员选择后的集合
    openSysUserStaffChoosePage: function (callback) {
        _openNewWindows({
            url: "../../tpl/common/sysusersel.html",
            title: "员工选择",
            pageId: "sysuserselpage",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                if (typeof (callback) == "function") {
                    callback(systemCommonUtil.userReturnList);
                }
            }
        });
    },

    /**
     * 员工选择页面(所有员工)--组件管理中使用
     *
     * @param callback 回调函数
     */
    userStaffCheckType: false, // 选择类型，默认单选，true:多选，false:单选
    checkStaffMation: [], // 选择时返回的对象
    openSysAllUserStaffChoosePage: function (callback) {
        _openNewWindows({
            url: "../../tpl/sysEveUserStaff/sysEveUserStaffChoose.html",
            title: "员工选择",
            pageId: "sysEveUserStaffChoose",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                if (typeof (callback) == "function") {
                    callback(systemCommonUtil.checkStaffMation);
                }
            }
        });
    },

    /**
     * 系统资源图标选择页面
     *
     * @param callback 回调函数
     */
    sysIconChooseClass: [], // 已经选择的图标资源列表
    openSysEveIconChoosePage: function (callback) {
        _openNewWindows({
            url: "../../tpl/sysEveIcon/sysEveIconListChoose.html",
            title: "图标选择",
            pageId: "sysEveIconListChoose",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                if(typeof(callback) == "function") {
                    callback(systemCommonUtil.sysIconChooseClass);
                }
            }});
    },

    /**
     * tagEditor组件移除所有tag
     *
     * @param id 对象id
     */
    tagEditorRemoveAll: function(id) {
        var tags = $('#' + id).tagEditor('getTags')[0].tags;
        for (i = 0; i < tags.length; i++) {
            $('#' + id).tagEditor('removeTag', tags[i]);
        }
    },
    /**
     * tagEditor组件重置数据
     *
     * @param id 对象id
     * @param data 数据
     */
    tagEditorResetData: function(id, data) {
        // 移除所有tag
        systemCommonUtil.tagEditorRemoveAll(id);
        // 添加新的tag
        $.each(data, function(i, item) {
            $('#' + id).tagEditor('addTag', item.name);
        });
        return data;
    },
    /**
     * tagEditor组件获取第一条数据
     *
     * @param id 组件id
     * @param list 集合
     * @returns {string|*}
     */
    tagEditorGetItemData: function (id, list) {
        var tags = $('#' + id).tagEditor('getTags')[0].tags;
        if (list.length == 0 || isNull(tags)) {
            return "";
        } else {
            return list[0].id;
        }
    },
    /**
     * tagEditor组件获取所有数据的id
     *
     * @param id 组件id
     * @param list 集合
     * @returns {string|*}
     */
    tagEditorGetAllData: function (id, list) {
        var tags = $('#' + id).tagEditor('getTags')[0].tags;
        if (list.length == 0 || isNull(tags)) {
            return [];
        } else {
            var ids = [];
            $.each(list, function (i, item) {
                ids.push(item.id);
            });
            return ids;
        }
    },

    /**
     * 禁用所有行
     *
     * @param type
     */
    disabledAllRow: function(type) {
        $(".layui-table tr input[type='" + type + "']").prop('disabled', true);
        $(".layui-table tr input[type='" + type + "']").addClass('layui-btn-disabled');
        $(".layui-table tr input[type='" + type + "']").next().css("cursor", "not-allowed");
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
     * 表格开启指定行数据选择
     *
     * @param index 行坐标
     * @param type 'radio': 单选；'checkbox': 多选
     */
    enabledRow: function(index, type) {
        // 第index行复选框不可选
        let t = $(".layui-table tr[data-index=" + index + "] input[type='" + type + "']");
        t.prop('disabled', false);
        t.removeClass('layui-btn-disabled');
        t.next().css("cursor", "allowed");
    },

    checkUrl: function (url) {
        return (url.startsWith('../') || url.startsWith('--'));
    },

    /**
     * 获取url地址
     *
     * @param url 系统自定义路径
     * @param menuSysWinUrl 分布式系统域名
     * @returns {string|*}
     */
    getUrl: function (url, menuSysWinUrl) {
        if (systemCommonUtil.checkUrl(url)) {
            // 自定义页面
            url = indexMenu.getUrlPath(url, menuSysWinUrl);
        } else {
            url = dsFormPageUrl + url;
        }
        return url
    },

    /**
     * 对页面url添加版本控制
     *
     * @param url
     */
    getHasVersionUrl: function (url) {
        var versionStr = 'v='+ skyeyeVersion;
        if (url.indexOf(versionStr) == -1) {
            // 判断是否有问号
            url += (url.indexOf("?") == -1 ? "?" : "&");
            url = url + versionStr;
        }
        return url;
    },

    /**
     * 获取路径的访问地址
     *
     * @param url
     */
    getFilePath: function (url) {
        if (isNull(url)) {
            return "";
        }
        if (url.startsWith("../../assets/")) {
            return homePagePath + url;
        } else {
            return fileBasePath + url;
        }
    },

    /**
     * 根据类型获取部分功能的使用说明
     *
     * @param type 类型  1.代码生成器模板介绍  2.动态表单内容项说明介绍  3.动态表单数据展示模板说明介绍  4.小程序标签属性说明介绍
     * @param callback 回调函数
     */
    queryExplainMationByType: function (type, callback) {
        AjaxPostUtil.request({url: reqBasePath + "queryExExplainMationToShow", params: {type: type}, type: 'json', method: "GET", callback: function(json) {
            if (typeof(callback) == "function") {
                callback(json);
            }
        }, async: false});
    },

    // 给部分功能设置图标
    iconChooseHtml: '<div class="layui-form-item">' +
        '            <label class="layui-form-label">图标类型<i class="red">*</i></label>' +
        '            <div class="layui-input-block winui-radio">' +
        '                <input type="radio" name="iconType" value="1" title="Icon" lay-filter="iconType" checked/>' +
        '                <input type="radio" name="iconType" value="2" title="图片" lay-filter="iconType" />' +
        '            </div>' +
        '        </div>' +
        '        <div class="layui-form-item iconTypeIsOne">' +
        '            <label class="layui-form-label">图标<i class="red">*</i></label>' +
        '            <div class="layui-input-block">' +
        '                <input type="text" id="icon" name="icon" placeholder="请输入图标src或者class" class="layui-input"/>' +
        '            </div>' +
        '        </div>' +
        '        <div class="layui-form-item iconTypeIsOne">' +
        '            <label class="layui-form-label">图标预览</label>' +
        '            <div class="layui-input-block">' +
        '                <div class="layui-col-xs12">' +
        '                <div class="layui-col-xs2">' +
        '                   <div class="winui-icon winui-icon-font" style="width: 60px; height: 60px; overflow: hidden">' +
        '                       <i id="iconShow" class="" style="font-size: 48px; line-height: 65px;"></i>' +
        '                   </div>' +
        '                </div>' +
        '                <div class="layui-col-xs5">' +
        '                <div class="layui-input-inline" style="width: 120px;">' +
        '            <input type="text" value="" class="layui-input" placeholder="请选择图标颜色" id="iconColorinput" />' +
        '        </div>' +
        '        <div id="iconColor"></div>' +
        '                </div>' +
        '                <div class="layui-col-xs5">' +
        '                <div class="layui-input-inline" style="width: 120px;">' +
        '            <input type="text" value="" class="layui-input" placeholder="请选择背景颜色" id="iconBginput" />' +
        '        </div>' +
        '        <div id="iconBg"></div>' +
        '                </div>' +
        '</div>' +
        '            </div>' +
        '        </div>' +
        '        <div class="layui-form-item iconTypeIsTwo layui-hide">' +
        '            <label class="layui-form-label">菜单图片<i class="red">*</i></label>' +
        '            <div class="layui-input-block">' +
        '                <div class="upload" id="iconPic"></div>' +
        '            </div>' +
        '        </div>',
    // 新增时初始化html,并添加监听事件
    initIconChooseHtml: function (showBoxId, form, colorpicker, uploadType) {
        $("#" + showBoxId).html(systemCommonUtil.iconChooseHtml);
        systemCommonUtil.initIconEvent(form, colorpicker, uploadType, "", "#1c97f5" , "#1c97f5");
        form.render();
    },
    // 编辑时初始化html,并添加监听事件
    initEditIconChooseHtml: function (showBoxId, form, colorpicker, uploadType, params) {
        $("#" + showBoxId).html(systemCommonUtil.iconChooseHtml);
        $("input:radio[name=iconType][value=" + params.iconType + "]").attr("checked", true);
        $("#icon").val(params.icon);
        $("#iconShow").attr("class", "fa fa-fw " + $("#icon").val());
        if (parseInt(params.iconType) == 1) { // icon
            $(".iconTypeIsTwo").addClass("layui-hide");
        } else if (parseInt(params.iconType) == 2) { // 图片
            $(".iconTypeIsTwo").removeClass("layui-hide");
            $(".iconTypeIsOne").addClass("layui-hide");
        }
        // 设置图标颜色
        if (isNull(params.iconColor)) {
            $("#iconShow").css({'color': 'white'});
        } else {
            $('#iconColorinput').val(params.iconColor);
            $("#iconShow").css({'color': params.iconColor});
        }
        // 自定义svg图标时
        if (!isNull(params.icon) && params.icon.indexOf('skyeye-') >= 0) {
            $("#iconShow").css({
                'filter': 'drop-shadow(' + params.iconColor + ' 80px 0)',
                'transform': 'translateX(-80px)',
            });
        }

        // 设置图标背景颜色
        if (isNull(params.iconBg)) {
            $("#iconShow").parent().css({'background-color': 'white'});
        } else {
            $('#iconBginput').val(params.iconBg);
            $("#iconShow").parent().css({'background-color': params.iconBg});
        }
        systemCommonUtil.initIconEvent(form, colorpicker, uploadType, params.iconPic, params.iconBg, params.iconColor);
        form.render();
    },
    initIconEvent: function (form, colorpicker, uploadType, uploadDefaultValue, iconBg, iconColor) {
        // 初始化上传
        $("#iconPic").upload(systemCommonUtil.uploadCommon003Config('iconPic', uploadType, uploadDefaultValue, 1));

        // 菜单图标类型变化事件
        form.on('radio(iconType)', function (data) {
            var val = data.value;
            if (val == '1') {//icon
                $(".iconTypeIsTwo").addClass("layui-hide");
                $(".iconTypeIsOne").removeClass("layui-hide");
            } else if (val == '2') {//图片
                $(".iconTypeIsTwo").removeClass("layui-hide");
                $(".iconTypeIsOne").addClass("layui-hide");
            }
        });

        colorpicker.render({
            elem: '#iconBg',
            color: iconBg,
            done: function(color) {
                $('#iconBginput').val(color);
                $("#iconShow").parent().css({'background-color': color});
            },
            change: function(color) {
                $("#iconShow").parent().css({'background-color': color});
            }
        });

        colorpicker.render({
            elem: '#iconColor',
            color: iconColor,
            done: function(color) {
                $('#iconColorinput').val(color);
                var icon = $("#icon").val();
                $("#iconShow").css({
                    'color': color,
                });
                // 自定义svg图标时
                if (icon.indexOf('skyeye-') >= 0) {
                    $("#iconShow").css({
                        'filter': 'drop-shadow(' + color + ' 80px 0)',
                        'transform': 'translateX(-80px)',
                    });
                }
            },
            change: function(color) {
                var icon = $("#icon").val();
                $("#iconShow").css({
                    'color': color,
                });
                // 自定义svg图标时
                if (icon.indexOf('skyeye-') >= 0) {
                    $("#iconShow").css({
                        'filter': 'drop-shadow(' + color + ' 80px 0)',
                        'transform': 'translateX(-80px)',
                    });
                }
            }
        });

        // 菜单图标选中事件
        $("body").on("focus", "#icon", function(e) {
            systemCommonUtil.openSysEveIconChoosePage(function(sysIconChooseClass) {
                $("#icon").val(sysIconChooseClass);
                $("#iconShow").css({'color': 'white'});
                $("#iconShow").attr("class", "fa fa-fw " + $("#icon").val());
            });
        });
    },
    // 获取图标选中数据
    getIconChoose: function (inputParams) {
        var params = {};
        if (!isNull(inputParams)) {
            params = inputParams;
        }
        params["iconChooseResult"] = true;
        var iconType = $("input[name='iconType']:checked").val();
        params["iconType"] = iconType;
        if (iconType == '1') {
            if (isNull($("#icon").val())) {
                winui.window.msg("请选择菜单图标", {icon: 2, time: 2000});
                params["iconChooseResult"] = false;
            }
            params["iconPic"] = '';
            params["icon"] = $("#icon").val();
            params["iconBg"] = $('#iconBginput').val();
            params["iconColor"] = $('#iconColorinput').val();
        } else if (iconType == '2') {
            params["iconPic"] = $("#iconPic").find("input[type='hidden'][name='upload']").attr("oldurl");
            if (isNull(params["iconPic"])) {
                winui.window.msg('请上传菜单logo', {icon: 2, time: 2000});
                params["iconChooseResult"] = false;
            }
            params["icon"] = '';
            params["iconBg"] = '';
            params["iconColor"] = '';
        }
        return params;
    },
    // 获取图标显示脚本
    initIconShow: function (bean) {
        var str = '';
        if (bean.iconType == '1') {
            if (isNull(bean.icon)) {
                return str;
            }
            if (isNull(bean.iconBg)) {
                str += '<div class="winui-icon winui-icon-font" style="text-align: center; overflow: hidden">';
            } else {
                str += '<div class="winui-icon winui-icon-font" style="text-align: center; overflow: hidden; background-color:' + bean.iconBg + '">';
            }
            // 自定义svg图标时
            if (bean.icon.indexOf('skyeye-') >= 0) {
                if (isNull(bean.iconColor)) {
                    str += '<i class="fa fa-fw skyeye-svg ' + bean.icon + '" style="filter: drop-shadow(white 80px 0); transform: translateX(-80px)"></i>';
                } else {
                    str += '<i class="fa fa-fw skyeye-svg ' + bean.icon + '" style="filter: drop-shadow(' + bean.iconColor + ' 80px 0); transform: translateX(-80px)"></i>';
                }
            } else {
                if (isNull(bean.iconColor)) {
                    str += '<i class="fa fa-fw skyeye-fa ' + bean.icon + '" style="color: white"></i>';
                } else {
                    str += '<i class="fa fa-fw skyeye-fa ' + bean.icon + '" style="color: ' + bean.iconColor + '"></i>';
                }
            }
            str += '</div>';
        } else if (bean.iconType = '2') {
            str = '<img src="' + fileBasePath + bean.iconPic + '" class="photo-img" lay-event="iconPic">';
        }
        return str;
    },

    /**
     * 统一上传到common003接口文件的配置信息
     *
     * @param id 组件id
     * @param uploadType 上传类型
     * @param uploadDefaultValue 默认展示的值
     * @param uploadNum 允许上传的图片数量
     */
    uploadCommon003Config: function (id, uploadType, uploadDefaultValue, uploadNum) {
        return {
            "action": reqBasePath + "common003",
            "data-num": uploadNum,
            "data-type": "PNG,JPG,jpeg,gif",
            "uploadType": uploadType,
            "data-value": uploadDefaultValue,
            "function": function (_this, data) {
                show('#' + id, data);
            }
        };
    },

    formatFloat: function (moneyStr) {
        return parseFloat(moneyStr).toFixed(2);
    }

};

// 行政区划工具函数
var areaUtil = {

    tips: {
        0: '最少选择到省级别',
        1: '最少选择到市级别',
        2: '最少选择到区县级别',
        3: '最少选择到乡镇级别'
    },

    selectId: {
        0: 'provinceId',
        1: 'cityId',
        2: 'areaId',
        3: 'townshipId',
        'details': 'absoluteAddress'
    },

    form: null,
    leastRequireLevel: -1,

    initArea: function (leastRequireLevel, showBoxId, form, defaultParams) {
        areaUtil.form = form;
        areaUtil.leastRequireLevel = isNull(leastRequireLevel) ? -1 : leastRequireLevel;
        areaUtil.loadBox(showBoxId);
        areaUtil.loadData(defaultParams);
    },

    loadBox: function (showBoxId) {
        var tip = areaUtil.tips[areaUtil.leastRequireLevel];
        var str = `<div class="layui-col-xs3" id="${areaUtil.selectId['0']}Box">
                    </div>
                    <div class="layui-col-xs3" id="${areaUtil.selectId['1']}Box">
                    </div>
                    <div class="layui-col-xs3" id="${areaUtil.selectId['2']}Box">
                    </div>
                    <div class="layui-col-xs3" id="${areaUtil.selectId['3']}Box">
                    </div>
                    <div class="layui-col-xs12" id="${areaUtil.selectId['details']}Box">
                    </div>
                    <div class="layui-form-mid layui-word-aux">${tip}</div>`;
        $(`#${showBoxId}`).html(str);
    },

    loadData: function (defaultParams) {
        // 获取省的数据
        var data = areaUtil.loadDataFromAjax('0');

        areaUtil.readerSelect(data, 0, defaultParams);

        areaUtil.readerDetails(defaultParams);

        areaUtil.renderListener();
    },

    loadDataFromAjax: function (pId) {
        var data = [];
        AjaxPostUtil.request({url: reqBasePath + "queryAreaListByPId", params: {pId: pId}, type: 'json', callback: function (json) {
            data = [].concat(json.rows);
        }, async: false});
        return data;
    },

    readerSelect: function (data, level, defaultParams) {
        var id = areaUtil.selectId[level];
        if (isNull(id)) {
            return false;
        }
        var str = `<select id="${id}" win-verify="${level <= areaUtil.leastRequireLevel ? 'required' : ''}" level="${level}" lay-filter="${id}" lay-search=""><option value="">请选择</option>`;
        for (var i = 0; i < data.length; i++) {
            str += `<option value="${data[i].id}">${data[i].name}</option>`;
        }
        str += `</select>`;
        $(`#${id}Box`).html(str);

        // 设置值
        if (!isNull(defaultParams)) {
            var value = defaultParams[id];
            if (!isNull(value)) {
                $(`#${id}`).val(value);
                var nextData = areaUtil.loadDataFromAjax(value);
                areaUtil.readerSelect(nextData, level + 1, defaultParams);
            }
        }

        areaUtil.form.render('select');
    },

    renderListener: function () {
        $.each(areaUtil.selectId, function (key, value) {
            if (!isNaN(key)) {
                areaUtil.form.on(`select(${value})`, function(data) {
                    layui.$(data.elem).parent('dd').nextAll().remove();
                    // 获取当前的级别
                    var level = parseInt($(`#${value}`).attr('level'));
                    if (!isNull(data.value)) {
                        var nextData = areaUtil.loadDataFromAjax(data.value);
                        areaUtil.readerSelect(nextData, level + 1, null);
                    }
                    $.each(areaUtil.selectId, function (key1, value1) {
                        if (key1 > (level + 1) && !isNaN(key1)) {
                            $(`#${value1}Box`).html('');
                        }
                    });
                });
            }
        });
    },

    readerDetails(defaultParams) {
        var id = areaUtil.selectId['details'];
        var str = `<input type="text" id="${id}" name="${id}" placeholder="请输入详细地址" class="layui-input" maxlength="100"/>`;
        $(`#${id}Box`).html(str);
        // 设置值
        if (!isNull(defaultParams)) {
            var value = defaultParams[id];
            if (!isNull(value)) {
                $(`#${id}`).val(value);
            }
        }
    },

    getValue: function () {
        var result = {};
        $.each(areaUtil.selectId, function (key, value) {
            var resultValue = $(`#${value}`).val();
            result[value] = isNull(resultValue) ? '' : resultValue;
        });
        return result;
    }
};

// tab页工具
var tabPageUtil = {

    manageTabHtml: `<div class="layui-tab layui-tab-brief" lay-filter="manageTab" id="manageTab">
                        <ul class="layui-tab-title"></ul>
                        <div class="layui-tab-content"></div>
                    </div>`,

    // 标题放置方向，暂时没有使用。horizontal：水平放置；vertical：垂直放置
    direction: 'horizontal',

    headerTemplate: `{{#rows}}<li class="">{{title}}</li>{{/rows}}`,

    contentTemplate: `{{#rows}}
                        <div class="layui-tab-item" style="height: 100%">
                            <iframe id="showBean" style="width: 100%; border: 0px; height: 100%" scrolling="no"></iframe>
                        </div>
                      {{/rows}}`,

    config: null,

    init: function (_config) {
        tabPageUtil.initialization();
        tabPageUtil.config = $.extend(true, tabPageUtil.config, _config);
        if (_config.suffixData == null) {
            tabPageUtil.config.suffixData = [];
        }

        // 获取页面信息
        var pageList = teamObjectPermissionUtil.getPageUrl(tabPageUtil.config.objectType);
        pageList = tabPageUtil.addPageMation(pageList);

        tabPageUtil.config.pageList = pageList;

        $(`#${tabPageUtil.config.id}`).html(tabPageUtil.manageTabHtml);
        // 初始化设置第一个为默认页面
        $("#manageTab").find(".layui-tab-title").html(getDataUseHandlebars(tabPageUtil.headerTemplate, {rows: pageList}));
        $("#manageTab").find(".layui-tab-title").find('li').eq(0).addClass('layui-this');

        $("#manageTab").find(".layui-tab-content").html(getDataUseHandlebars(tabPageUtil.contentTemplate, {rows: pageList}));
        $("#manageTab").find(".layui-tab-content").find('.layui-tab-item').eq(0).addClass('layui-show');
        tabPageUtil.setPageUrl(pageList[0]);

        tabPageUtil.initEvent();
    },

    initialization: function () {
        tabPageUtil.config = {
            id: '', // 展示位置
            prefixData: [], // 集合前面追加的其他需要加载的数据 例如：{title: '详情', pageUrl: '../../tpl/customerManage/customerDetails.html'}
            suffixData: [], // 集合后面追加的其他需要加载的数据 例如：{title: '详情', pageUrl: '../../tpl/customerManage/customerDetails.html'}
            element: null, // element对象
            objectType: '', // 适用对象  例如：客户，项目等
            pageList: [], // 页面信息
            object: {
                objectId: '', // 业务对象id
                objectKey: '', // 业务对象key
            }
        };
    },

    initEvent: function () {
        tabPageUtil.config.element.render();
        tabPageUtil.config.element.on('tab(manageTab)', function (obj) {
            var mation = tabPageUtil.config.pageList[obj.index];
            if (!isNull(mation)) {
                tabPageUtil.setPageUrl(mation);
            }
        });
    },

    setPageUrl: function (mation) {
        var objectType = tabPageUtil.config.objectType;
        var url = mation.pageUrl + (mation.pageUrl.indexOf("?") == -1 ? "?" : "&") + "objectId=" + tabPageUtil.config.object.objectId + "&objectKey=" + tabPageUtil.config.object.objectKey
            + "&objectType=" + objectType;
        $("#manageTab").find(".layui-tab-content").find('.layui-show').find('iframe').attr('src', url);
    },

    addPageMation: function (pageList) {
        var prefixData = tabPageUtil.config.prefixData;
        var suffixData = tabPageUtil.config.suffixData;
        if (!isNull(prefixData)) {
            $.each(prefixData, function (i, item) {
                pageList.unshift(item);
            });
        }
        if (!isNull(suffixData)) {
            $.each(suffixData, function (i, item) {
                pageList.push(item);
            });
        }
        // 移除权限文字
        $.each(pageList, function (i, item) {
            item.title = item.title.replace('权限', '');
        });
        return pageList;
    }

};

// 代码编辑器
var codeUtil = {

    getConfig: function (mode) {
        return {
            mode : mode,  // 模式
            theme : "eclipse",  // CSS样式选择
            indentUnit : 4,  // 缩进单位，默认2
            smartIndent : true,  // 是否智能缩进
            tabSize : 4,  // Tab缩进，默认4
            readOnly : false,  // 是否只读，默认false
            showCursorWhenSelecting : true,
            lineNumbers : true,  // 是否显示行号
            styleActiveLine: true, //line选择是是否加亮
            matchBrackets: true,
        };
    }

};

// 树结构工具
var ztreeUtil = {

    addDiyDom: function (treeId, treeNode) {
        // css样式以及位置调整
        var spaceWidth = 8;
        var switchObj = $("#" + treeNode.tId + "_switch"),
            icoObj = $("#" + treeNode.tId + "_ico");
        switchObj.remove();
        icoObj.before(switchObj);
        if (isNull(treeNode.icon)) {
            // 如果图标为空，就移除dom元素
            icoObj.remove();
        }
        if (treeNode.level >= 1) {
            var spaceStr = "<span style='display: inline-block;width:" + (spaceWidth * treeNode.level) + "px'></span>";
            switchObj.before(spaceStr);
        }

        // 操作数据
        var aObj = $("#" + treeNode.tId + "_a");
        if ($("#diyBtn_" + treeNode.id).length > 0) return;
        aObj.after("");
        aObj.addClass("tree_a");
        aObj.attr("ztreerowid", treeNode.id);
    },

    initEventListener: function (treeId) {
        // 树的动画效果
        $("body").on("mouseover", `#${treeId} a`, function (e) {
            $(`#${treeId}`).find("a").removeClass('mouseOver');
            $(this).addClass('mouseOver');
        });
        $("body").on("mouseleave", `#${treeId}`, function (e) {
            $(`#${treeId}`).find("a").removeClass('mouseOver');
        });
    }

};

// 树状下拉框工具
var treeSelectUtil = {

    config: {
        eleTree: null,
        elem: '',
        url: '',
        idKey: 'id',
        nameKey: 'name',
        defaultId: '',
        ajaxCallback: null,
        clickCallback: null
    },

    init: function (_config) {
        treeSelectUtil.config = $.extend(true, treeSelectUtil.config, _config);

        treeSelectUtil.initShow();
    },

    initShow: function () {
        $(`#${treeSelectUtil.config.elem}`).after(`<div class="eleTree ele5" lay-filter="data5"></div>`);
        treeSelectUtil.config.eleTree.render({
            elem: '.ele5',
            url: treeSelectUtil.config.url,
            defaultExpandAll: true,
            expandOnClickNode: false,
            highlightCurrent: true,
            request: {
                name: treeSelectUtil.config.nameKey,
                key: treeSelectUtil.config.idKey,
            },
            done: function (res) {
                // 设置默认值
                if (!isNull(treeSelectUtil.config.defaultId)) {
                    var html = $(".ele5").find(`div[data-id='${treeSelectUtil.config.defaultId}']`).find('.eleTree-node-content-label').html();
                    $(`#${treeSelectUtil.config.elem}`).val(html);
                    $(`#${treeSelectUtil.config.elem}`).attr(`${treeSelectUtil.config.elem}`, treeSelectUtil.config.defaultId);
                }
                if (typeof treeSelectUtil.config.ajaxCallback == "function") {
                    treeSelectUtil.config.ajaxCallback(res);
                }
            }
        });

        $(".ele5").hide();
        $(`#${treeSelectUtil.config.elem}`).on("click", function (e) {
            e.stopPropagation();
            $(".ele5").toggle();
        });
        treeSelectUtil.config.eleTree.on("nodeClick(data5)", function(d) {
            if (d.data.currentData.disabled) {
                d.event.stopPropagation();
                return false;
            }
            $(`#${treeSelectUtil.config.elem}`).val(d.data.currentData[treeSelectUtil.config.nameKey]);
            $(`#${treeSelectUtil.config.elem}`).attr(`${treeSelectUtil.config.elem}`, d.data.currentData[treeSelectUtil.config.idKey]);
            if (typeof treeSelectUtil.config.clickCallback == "function") {
                treeSelectUtil.config.clickCallback(d.data.currentData[treeSelectUtil.config.idKey]);
            }
            $(".ele5").hide();
        })
        $(document).on("click",function() {
            $(".ele5").hide();
        })
    }

};

// 数据展示方式
var dataShowType = {

    showDataType: [
        {id: 'select', name: '下拉框'},
        {id: 'checkbox', name: '多选框'},
        {id: 'radio', name: '单选框'},
        {id: 'verificationSelect', name: '多选下拉框'},
        {id: 'radioTree', name: '单选框树'},
        {id: 'checkboxTree', name: '多选框树'},
        {id: 'selectTree', name: '树结构展示'}
    ],

    /**
     * 展示数据
     *
     * @param json 数据
     * @param showType 展示类型
     * @param showBoxId 展示位置
     * @param defaultId 默认回显值
     * @param form form对象
     * @param callback 回调函数
     * @param chooseCallback 如果是提供选择的树插件类型，则具备点击节点的回调事件
     * @param valueKey value展示的key
     * @param isRoot 如是是树结构，则表示是否展示根节点
     */
    showData: function (json, showType, showBoxId, defaultId, form, callback, chooseCallback, valueKey, isRoot) {
        var _box = $("#" + showBoxId);
        _box.html('');
        _box.attr('showType', showType);
        var winRequired = _box.attr('win-required');
        winRequired = isNull(winRequired) ? '' : `win-verify='${winRequired}'`;
        if (showType == 'select') {
            // 下拉框
            if (_box.is('select')) {
                _box.html(getDataUseHandlebars(getFileContent('tpl/template/select-option.tpl'), json));
            } else {
                _box.html(`<select ${winRequired} lay-search="">` + getDataUseHandlebars(getFileContent('tpl/template/select-option.tpl'), json) + '</select>');
            }
            if (!isNull(defaultId)) {
                if (_box.is('select')) {
                    _box.val(defaultId);
                } else {
                    _box.find('select').val(defaultId);
                }
            } else {
                $.each(json.rows, function (i, item) {
                    if (item.isDefault) {
                        _box.val(item.id);
                    }
                });
            }
            form.render('select');
        } else if (showType == 'checkbox') {
            // 多选框
            _box.html(getDataUseHandlebars(getFileContent('tpl/template/checkbox-property.tpl'), json));
            if (!isNull(defaultId)) {
                var arr = defaultId.split(",");
                for (var i = 0; i < arr.length; i++) {
                    $('input:checkbox[rowId="' + arr[i] + '"]').attr("checked", true);
                }
            } else {
                $.each(json.rows, function (i, item) {
                    if (item.isDefault) {
                        $('input:checkbox[rowId="' + item.id + '"]').attr("checked", true);
                    }
                });
            }
            form.render('checkbox');
        } else if (showType == 'radio') {
            // 单选框
            _box.html(getDataUseHandlebars(`{{#each rows}}<input type="radio" name="${showBoxId}Name" lay-filter="${showBoxId}Filter" value="{{id}}" title="{{name}}" />{{/each}}`, json));
            if (!isNull(defaultId) || defaultId + '' == '0') {
                $("#" + showBoxId + " input:radio[name=" + showBoxId + "Name][value=" + defaultId + "]").attr("checked", true);
            } else {
                $.each(json.rows, function (i, item) {
                    if (item.isDefault) {
                        $("#" + showBoxId + " input:radio[name=" + showBoxId + "Name][value=" + item.id + "]").attr("checked", true);
                    }
                });
            }
            form.render('radio');
        } else if (showType == 'verificationSelect') {
            // 多选下拉框
            var html = `<div id="${showBoxId}Div" class="xm-select-demo"></div>`;
            _box.html(html);
            var optionValueKey = isNull(valueKey) ? "id" : valueKey;
            layui.define(["xmSelect"], function(exports) {
                var xmSelect = layui.xmSelect;
                var data = [].concat(json.rows);
                $.each(data, function (i, item) {
                    item.selected = false;
                });
                // 设置选中值
                var chooseId = [];
                if (!isNull(defaultId)) {
                    var defaultIds = defaultId.split(",");
                    $.each(defaultIds, function (m, str) {
                        $.each(data, function (i, item) {
                            if (item[optionValueKey] == str) {
                                item.selected = true;
                                chooseId.push(str);
                            }
                        });
                    });
                }
                // 设置默认选中
                $.each(data, function (i, item) {
                    if (item.isDefault && !item.selected) {
                        item.selected = true;
                        chooseId.push(item[optionValueKey]);
                    }
                });
                $(`#${showBoxId}`).attr('value', JSON.stringify(chooseId));

                var _select = xmSelect.render({
                    el: `#${showBoxId}Div`,
                    data: json.rows,
                    autoRow: true,
                    model: {
                        label: {
                            type: 'block',
                            block: {
                                // 最大显示数量, 0:不限制
                                showCount: 3,
                                // 是否显示删除图标
                                showIcon: true,
                            }
                        }
                    },
                    theme: {
                        color: '#0089ff',
                    },
                    prop: {
                        name: 'name',
                        value: optionValueKey
                    },
                    filterable: true,
                    toolbar: {
                        show: true,
                        list: ['ALL', 'REVERSE', 'CLEAR']
                    },
                    on: function(data) {
                        var arr = data.arr;
                        var newChooseId = [];
                        $.each(arr, function (i, item) {
                            newChooseId.push(item[optionValueKey]);
                        });
                        $(`#${showBoxId}`).attr('value', JSON.stringify(newChooseId));
                    },
                    done: function(data) {
                    }
                });
            });
        } else if (showType == 'radioTree' || showType == 'checkboxTree') {
            // 单选框树/多选框树
            var _html = sysDictDataUtil.getShowTteeHtml(showBoxId, isNull(isRoot) ? '0' : isRoot);
            var _js = `<script>
                        layui.define(["jquery", 'fsTree'], function(exports) {
                            var jQuery = layui.jquery,
                                fsTree = layui.fsTree;
                            (function($) {
                                var ${showBoxId}Object;
                                fsTree.render({
                                    id: "${showBoxId}Tree",
                                    simpleData: '` + JSON.stringify(json.treeRows) + `',
                                    checkEnable: true,
                                    loadEnable: false,
                                    chkStyle: '${showType}' == 'radioTree' ? "radio" : "checkbox",
                                    showLine: false,
                                    showIcon: true,
                                    expandSpeed: 'fast',
                                    onCheck: function (event, treeId, treeNode) {
                                        if ('${showType}' == 'checkboxTree') {
                                            var zTree = ${showBoxId}Object.getCheckedNodes(true);
                                            var ids = [];
                                            $.each(zTree, function(i, item) {
                                                ids.push(item.id);
                                            });
                                            $('#${showBoxId}').attr('chooseId', JSON.stringify(ids));
                                        } else {
                                            $('#${showBoxId}').attr('chooseId', treeNode.id);
                                        }
                                    }
                                }, function(id) {
                                    ${showBoxId}Object = $.fn.zTree.getZTreeObj(id);
                                    fuzzySearch(id, '#${showBoxId}Name', null, true);
                                });
                                if (` + !isNull(defaultId) + `) {
                                    var zTree = ${showBoxId}Object.getCheckedNodes(false);
                                    var type = '${showType}' == 'radioTree' ? "radio" : "checkbox";
                                    var tempArray = type == 'checkbox' ? JSON.parse('${defaultId}') : [].push('${defaultId}');
                                    for (var i = 0; i < zTree.length; i++) {
                                        if ('${showType}' == 'checkboxTree') {
                                            if($.inArray(zTree[i].id, tempArray) >= 0){
                                                ${showBoxId}Object.checkNode(zTree[i], true, true);
                                            }
                                        } else {
                                            if(zTree[i].id == '${defaultId}'){
                                                ${showBoxId}Object.checkNode(zTree[i], true, true);
                                                $('#${showBoxId}').attr('chooseId', zTree[i].id);
                                            }
                                        }
                                    }
                                    if ('${showType}' == 'checkboxTree') {
                                        $('#${showBoxId}').attr('chooseId', JSON.stringify(tempArray));
                                    }
                                }
                            })(jQuery);});
                       </script>`;
            _box.append(_html + _js);
        } else if (showType == 'selectTree') {
            // 提供选择的树插件
            var _html = sysDictDataUtil.getShowTteeHtml(showBoxId, '1');
            _html += `<link href="../../assets/lib/winui/css/customer/ztree/common-tree.css" rel="stylesheet" />`;
            var _js = `<script>
                        layui.define(["jquery", 'fsTree'], function(exports) {
                            var jQuery = layui.jquery,
                                fsTree = layui.fsTree;
                            (function($) {
                                fsTree.render({
                                    id: "${showBoxId}Tree",
                                    simpleData: '` + JSON.stringify(json.treeRows) + `',
                                    checkEnable: false,
                                    loadEnable: false,
                                    showLine: false,
                                    showIcon: true,
                                    expandSpeed: 'fast',
                                    addDiyDom: ztreeUtil.addDiyDom,
                                    clickCallback: onClickTree,
                                    onDblClick: onClickTree
                                }, function(id) {
                                    fuzzySearch(id, '#${showBoxId}Name', null, true);
                                    ztreeUtil.initEventListener(id);
                                });
                                function onClickTree(event, treeId, treeNode) {
                                    var chooseId;
                                    if (treeNode == undefined || treeNode.id == 0) {
                                        chooseId = "";
                                    } else {
                                        chooseId = treeNode.id;
                                    }
                                    $('#${showBoxId}Choose').val(chooseId).change();
                                }
                            })(jQuery);});
                       </script>`;
            _box.append(_html + _js);
            $("#" + showBoxId + "Choose").on("change", function() {
                if (typeof (chooseCallback) == "function") {
                    chooseCallback($(this).val());
                }
            });
        }
        if (typeof (callback) == "function") {
            callback(json);
        }
    },
    
    getData: function (showBoxId) {
        var showType = $("#" + showBoxId).attr('showType');
        var value;
        if (showType == 'select') {
            // 下拉框
            if ($("#" + showBoxId).is('select')) {
                value = $("#" + showBoxId).val();
            } else {
                value = $("#" + showBoxId).find('select').val();
            }
        } else if (showType == 'checkbox') {
            // 多选框
            var checkRow = $(`#${showBoxId} input[type='checkbox']:checked`);
            var checkTrueList = [];
            $.each(checkRow, function (i, item) {
                checkTrueList.push($(item).attr('id'));
            });
            value = checkTrueList;
        } else if (showType == 'radio') {
            // 单选框
            value = $(`#${showBoxId} input:radio:checked`).val()
        } else if (showType == 'verificationSelect') {
            // 多选下拉框
            value = $(`#${showBoxId}`).attr('value')
        } else if (showType == 'radioTree' || showType == 'checkboxTree') {
            // 单选框树/多选框树
            value = $(`#${showBoxId}`).attr('chooseId');
        } else if (showType == 'selectTree') {
        }
        return value;
    }

};

// 桌面菜单显示工具
var desktopMenuUtil = {

    /**
     * 获取左下角菜单图标
     *
     * @param menu
     * @returns {{winIcon: string, icon: string}}
     */
    getMenuIcon: function (menu) {
        var icon, winIcon;
        if (parseInt(menu.iconType) == 1) {
            // icon
            icon = `<i class="fa fa-fw ${menu.icon}" style="color: ${menu.iconColor}"></i>`;
            winIcon = `win-icon="${menu.icon}"`;
        } else if (parseInt(menu.iconType) == 2) {
            // 图片
            icon = `<img src="${fileBasePath}${menu.iconPic}" />`;
            winIcon = `win-icon="${menu.iconPic}"`;
        }
        return {
            icon: icon,
            winIcon: winIcon
        };
    },

    /**
     * 获取桌面菜单图标
     *
     * @param menu
     * @returns {{winIcon: string, icon: string}}
     */
    getDecktopMenuIcon: function (menu) {
        var icon, smallIcon, menuIcon, isFaIcon;
        if (parseInt(menu.iconType) == 1) {
            // icon
            smallIcon = `<i class="fa fa-fw icon-drawer-icon ${menu.icon}" style="color: ${menu.iconColor}; background-color: ${menu.iconBg}" win-i-id="${menu.id}"></i>`;
            icon = `<i class="fa fa-fw ${menu.icon}" style="color: ${menu.iconColor}; background-color: ${menu.iconBg}" win-i-id="${menu.id}"></i>`;
            menuIcon = `win-icon="${menu.icon}"`;
            isFaIcon = "winui-icon-font";
        } else if (parseInt(menu.iconType) == 2) {
            // 图片
            smallIcon = `<i class="fa icon-drawer-icon" win-i-id="${menu.id}"><img src="${fileBasePath}${menu.iconPic}" class="desktop-img"/></i>`;
            icon = `<img src="${fileBasePath}${menu.iconPic}" class="desktop-img"/>`;
            menuIcon = `win-icon="${menu.iconPic}"`;
            isFaIcon = "winui-icon-img";
        }
        return {
            icon: icon,
            smallIcon: smallIcon,
            menuIcon: menuIcon,
            isFaIcon: isFaIcon
        };
    },

    getTraditionPageMenuIcon: function (menu) {
        if(menu.iconType === 1){
            // icon
            return '<i class="fa ' + menu.icon + ' fa-fw"></i>';
        } else if (menu.iconType === 2){
            // 图片
            return '<img src="' + fileBasePath + menu.iconPic + '" />';
        }
        return '';
    }

};

// 公共的一些html脚本
var commonHtml = {

    'customPageUrl': `<div class="layui-form-item layui-col-xs12">
                        <label class="layui-form-label">页面地址<i class="red">*</i></label>
                        <div class="layui-input-block">
                            <input type="text" id="pageUrl" name="pageUrl" placeholder="请输入页面地址" win-verify="required" class="layui-input" maxlength="200"/>
                            <div class="layui-form-mid layui-word-aux">如果不想跳转，可填写：--<br>如果想跳转,格式为：../../tpl/model/modellist.html</div>
                        </div>
                    </div>`,
    'dsFormPage': `<div class="layui-form-item layui-col-xs12">
                        <label class="layui-form-label">表单布局<i class="red">*</i></label>
                        <div class="layui-input-block">
                            <input type="text" id="pageUrl" name="pageUrl" placeholder="请选择表单布局" win-verify="required" class="layui-input" readonly="readonly"/>
                            <i class="fa fa-plus-circle input-icon chooseBtn" style="top: 12px;"></i>
                        </div>
                    </div>`,
    'businessApi': `<div class="layui-form-item layui-col-xs12">
                        <span class="hr-title">请求事件</span><hr>
                    </div>
                    <div class="layui-form-item layui-col-xs6">
                        <label class="layui-form-label">所属服务<i class="red">*</i></label>
                        <div class="layui-input-block">
                            <select id="serviceStr" name="serviceStr" lay-filter="serviceStr" win-verify="required"></select>
                        </div>
                    </div>
                    <div class="layui-form-item layui-col-xs6">
                        <label class="layui-form-label">接口地址<i class="red">*</i></label>
                        <div class="layui-input-block">
                            <input type="text" id="api" name="api" placeholder="请输入接口地址" win-verify="required" class="layui-input" maxlength="200"/>
                        </div>
                    </div>
                    <div class="layui-form-item layui-col-xs6">
                        <label class="layui-form-label">请求方式<i class="red">*</i></label>
                        <div class="layui-input-block">
                            <select id="method" name="method" lay-filter="method" win-verify="required"></select>
                        </div>
                    </div>
                    <div class="layui-form-item layui-col-xs12">
                        <label class="layui-form-label">接口入参</label>
                        <div class="layui-input-block" id="apiParams">
                            
                        </div>
                    </div>`

};