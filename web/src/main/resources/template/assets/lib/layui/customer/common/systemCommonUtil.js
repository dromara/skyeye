// 系统工具函数
var systemCommonUtil = {

    /**
     * 获取系统账户信息
     *
     * @param callback 回执函数
     */
    getSysAccountListByType: function (callback) {
        AjaxPostUtil.request({url: flowableBasePath + "account009", params: {}, type: 'json', method: "GET", callback: function(json) {
            if(typeof(callback) == "function") {
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
            if(typeof(callback) == "function") {
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
     * 判断当前登录用户是否可以申请离职
     */
    judgeCurrentUserQuit: function () {
        var result = false;
        AjaxPostUtil.request({url: flowableBasePath + "judgeCurrentUserQuit", params: {}, type: 'json', method: "GET", callback: function(json) {
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
            if(typeof(callback) == "function") {
                callback(json);
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
            if(typeof(callback) == "function") {
                callback(json);
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
            if(typeof(callback) == "function") {
                callback(json);
            }
        }, errorCallback: function (json) {
            if(typeof(errorCallback) == "function") {
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
            if(typeof(callback) == "function") {
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
            if(typeof(callback) == "function") {
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
     * 员工选择页面(所有员工)
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
            return "";
        } else {
            var ids = "";
            $.each(list, function (i, item) {
                ids += item.id + ',';
            });
            return ids;
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
        var versionStr = 'v='+ skyeyeVersion;
        if(url.indexOf(versionStr) == -1) {
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

    // 员工在职状态
    sysUserStaffState: {
        "onTheJob": {"id": 1, "name": "在职(转正的员工)"},
        "quit": {"id": 2, "name": "离职"},
        "probation": {"id": 3, "name": "见习(用于实习生)"},
        "probationPeriod": {"id": 4, "name": "试用期(用于未转正的员工)"},
        "retire": {"id": 5, "name": "退休"}
    },
    getSysUserStaffStateList: function () {
        var list = [];
        $.each(systemCommonUtil.sysUserStaffState, function (key, value) {
            list.push(value);
        });
        return list;
    },

    /**
     * 根据类型获取部分功能的使用说明
     *
     * @param type 类型  1.代码生成器模板介绍  2.动态表单内容项说明介绍  3.动态表单数据展示模板说明介绍  4.小程序标签属性说明介绍
     * @param callback 回调函数
     */
    queryExplainMationByType: function (type, callback) {
        AjaxPostUtil.request({url: reqBasePath + "queryExExplainMationToShow", params: {type: type}, type: 'json', method: "GET", callback: function(json) {
            if(typeof(callback) == "function") {
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
        if (params.icon.indexOf('skyeye-') >= 0) {
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
    getIconChoose: function (params) {
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
        for(var i = 0; i < data.length; i++){
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
            suffixData: [{
                title: '团队',
                pageUrl: '../../tpl/teamBusiness/teamBusinessDetails.html'
            }], // 集合后面追加的其他需要加载的数据 例如：{title: '详情', pageUrl: '../../tpl/customerManage/customerDetails.html'}
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
        tabPageUtil.config.element.on('tab(manageTab)', function (obj) {
            var mation = tabPageUtil.config.pageList[obj.index];
            if (!isNull(mation)) {
                tabPageUtil.setPageUrl(mation);
            }
        });
    },

    setPageUrl: function (mation) {
        var objectType = tabPageUtil.config.objectType;
        var url = mation.pageUrl + "?objectId=" + tabPageUtil.config.object.objectId + "&objectKey=" + tabPageUtil.config.object.objectKey
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
        defaultId: ''
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
            $(".ele5").hide();
        })
        $(document).on("click",function() {
            $(".ele5").hide();
        })
    }

};

