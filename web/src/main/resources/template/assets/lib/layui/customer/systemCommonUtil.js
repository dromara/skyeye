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
            url: "../../tpl/syseveuserstaff/sysEveUserStaffChoose.html",
            title: "员工选择",
            pageId: "sysuserselpage",
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
    },

    /**
     * 展示枚举类的集合数据
     *
     * @param code 枚举类对应的前台code
     * @param showType 展示类型
     * @param showBoxId 展示位置
     * @param defaultId 默认回显值
     * @param form form对象
     * @param callback 回调函数
     */
    showEnumDataListByClassName: function (code, showType, showBoxId, defaultId, form, callback) {
        var json = systemCommonUtil.getEnumDataListByClassName(code);
        if (showType == 'select') {
            $("#" + showBoxId).html(getDataUseHandlebars(getFileContent('tpl/template/select-option.tpl'), json));
            if (!isNull(defaultId)) {
                $("#" + showBoxId).val(defaultId);
            } else {
                $.each(json.rows, function (i, item) {
                    if (item.isDefault) {
                        $("#" + showBoxId).val(item.id);
                    }
                });
            }
            form.render('select');
        } else if (showType == 'checkbox') {
            $("#" + showBoxId).html(getDataUseHandlebars(getFileContent('tpl/template/checkbox-property.tpl'), json));
            if (!isNull(defaultId)) {
                var arr = defaultId.split(",");
                for(var i = 0; i < arr.length; i++){
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
            $("#" + showBoxId).html(getDataUseHandlebars('{{#each rows}}<input type="radio" name="' + showBoxId + 'Name" value="{{id}}" title="{{name}}" />{{/each}}', json));
            if (!isNull(defaultId)) {
                $("#" + showBoxId + " input:radio[name=" + showBoxId + "Name][value=" + defaultId + "]").attr("checked", true);
            } else {
                $.each(json.rows, function (i, item) {
                    if (item.isDefault) {
                        $("#" + showBoxId + " input:radio[name=" + showBoxId + "Name][value=" + item.id + "]").attr("checked", true);
                    }
                });
            }
            form.render('radio');
        }
        if (typeof (callback) == "function") {
            callback(json);
        }
    },

    getEnumDataListByClassName: function (code) {
        var params = {
            className: encodeURIComponent(skyeyeClassEnum[code]["className"])
        };
        if (!isNull(skyeyeClassEnum[code]["filterValue"])) {
            params["filterValue"] = skyeyeClassEnum[code]["filterValue"];
            params["filterKey"] = skyeyeClassEnum[code]["filterKey"];
        }
        var result = {};
        AjaxPostUtil.request({url: reqBasePath + "getEnumDataByClassName", params: params, type: 'json', method: "POST", callback: function(json) {
            result = json;
        }, async: false});
        return result;
    },

};