
// 表格高级查询插件
var initTableSearchUtil = {

    // 多个表格加载时存储的对象信息
    tableMap: {},

    // 列ID后缀
    fileIdSuffix: "Search",

    // 已选择的列筛选条件 {"tableId": {"key": {"operator": ">", "operatorName": "大于", "value": "张三"}}}
    chooseMap: {},

    /**
     * 加载表格的高级查询
     *
     * @param $table 表格对象
     * @param searchParams 高级查询的参数
     * @param form form表单对象
     * @param keywordPlaceholder 关键字搜索的提示语
     * @param callback 搜索条件点击确定时的回调，用来刷新表格
     */
    initAdvancedSearch: function ($table, searchParams, form, keywordPlaceholder, callback) {
        var tableId = $table.id;
        // 同一个表格只加载一次
        if(isNull(initTableSearchUtil.tableMap[tableId])){
            initTableSearchUtil.tableMap[tableId] = {
                table: $table,
                searchParams: searchParams,
                keywordPlaceholder: keywordPlaceholder,
                callback: callback
            };
            // 加载筛选条件展示框
            $("div[lay-id='" + tableId + "']").parent().prepend('<div class="filter-search-box" id="filter' + tableId + '"></div>');
            // 初始化监听事件
            initTableSearchUtil.initEvent(form);
            // 加载表格对应的关键字搜索信息
            initTableSearchUtil.initTableKeyWordSearch(tableId, keywordPlaceholder);
        }
        if (isNull(searchParams)) {
            return;
        }
        initTableSearchUtil.loadSearchSign(tableId, searchParams, form);
    },

    /**
     * 初始化表格高级搜索列的标识
     *
     * @param tableId 表格id
     * @param searchParams 高级查询的参数
     * @param form form表单对象
     */
    loadSearchSign: function (tableId, searchParams, form) {
        var fixTh = $("div[lay-id='" + tableId + "'] .layui-table-fixed").find("thead").eq(0).find("th");
        var th = $("div[lay-id='" + tableId + "'] .layui-table-header").find("thead").eq(0).find("th");
        $.each(searchParams, function (key, value) {
            // 现在固定的表头上找，没有找到的话，再去可移动的表头上找
            var findIdent = false;
            var fieldId = key + initTableSearchUtil.fileIdSuffix;
            if ($("span[search-table-id='" + tableId + "'][search-sign='" + fieldId + "']").length == 0) {
                // 固定的标头
                $.each(fixTh, function (j, _th) {
                    var _this = $(_th);
                    if (_this.attr("data-field") == key) {
                        // 加载筛选标识
                        _this.find(".layui-table-cell").append(initTableSearchUtil.searchSignHtml(tableId, fieldId));
                        findIdent = true;
                    }
                });
                if (!findIdent) {
                    $.each(th, function (j, _th) {
                        var _this = $(_th);
                        if (_this.attr("data-field") == key) {
                            // 加载筛选标识
                            _this.find(".layui-table-cell").append(initTableSearchUtil.searchSignHtml(tableId, fieldId));
                        }
                    });
                }
            }
        });
        // 展示筛选内容
        initTableSearchUtil.loadChooseHtml(tableId);
    },

    /**
     * 加载表格对应的关键字搜索信息
     *
     * @param tableId 表格id
     * @param keywordPlaceholder 关键字搜索的提示语
     */
    initTableKeyWordSearch: function (tableId, keywordPlaceholder) {
        if (isNull(keywordPlaceholder)) {
            return false;
        }
        var str;
        if (typeof keywordPlaceholder === 'object') {
            str = '<div class="keyword-box">' +
                '<input type="text" id="' + tableId + 'KeyWord" placeholder="' + keywordPlaceholder.value + '" class="layui-input search-input-keyword" />' +
                '<i class="fa fas fa-search input-icon search-btn-keyword" id="' + tableId + 'SearchTable" title="' + systemLanguage["com.skyeye.search2"][languageType] + '"></i>' +
                '</div>';
        } else {
            str = '<div class="keyword-box">' +
                '<input type="text" id="' + tableId + 'KeyWord" placeholder="' + keywordPlaceholder + '" class="layui-input search-input-keyword" />' +
                '<i class="fa fas fa-search input-icon search-btn-keyword" id="' + tableId + 'SearchTable" title="' + systemLanguage["com.skyeye.search2"][languageType] + '"></i>' +
                '</div>';
        }
        $(".winui-tool").append(str);
        // 当对输入框有要求时
        if (typeof keywordPlaceholder === 'object') {
            // 是否有默认值
            if (!isNull(keywordPlaceholder.defaultValue)) {
                $("#" + tableId + "KeyWord").val(keywordPlaceholder.defaultValue);
            }
            if (keywordPlaceholder.type == 'month') {
                var jsCon;
                // 是否必填
                if (keywordPlaceholder.required == 'required') {
                    jsCon = '<script>layui.define(["laydate"], function(exports) {' +
                        'var laydate = layui.laydate;laydate.render({elem: "#' + tableId + 'KeyWord", type: "' + keywordPlaceholder.type + '", trigger: "click", btns: ["confirm"]});' +
                        '})</script>';
                } else {
                    jsCon = '<script>layui.define(["laydate"], function(exports) {' +
                        'var laydate = layui.laydate;laydate.render({elem: "#' + tableId + 'KeyWord", type: "' + keywordPlaceholder.type + '", trigger: "click"});' +
                        '})</script>';
                }
                $(".winui-tool").append(jsCon);
            }
        }
        // 监听搜索的回车按钮
        $("#" + tableId + "KeyWord").keypress(function (event) {
            if (event.keyCode == 13) {
                // 回车搜索
                $("#" + tableId + "SearchTable").click();
            }
        });
    },

    /**
     * 获取筛选的标识
     *
     * @param tableId 表格id
     * @param fieldId 字段列id
     */
    searchSignHtml: function (tableId, fieldId) {
        return '<span class="layui-table-search layui-inline" search-table-id="' + tableId + '" search-sign="' + fieldId + '">' +
            '<i class="fa fas fa-filter" aria-hidden="true"></i>' +
            '</span>';
    },

    /**
     * 加载筛选框
     *
     * @param tableId 表格id
     * @param fieldId 字段列id
     * @param searchParam 高级查询的参数
     * @param fieldName 字段列名称
     */
    searchBox: function (tableId, fieldId, searchParam, fieldName) {
        return '<form class="layui-form search-form" action="" id="searchBox">' +
            '<div class="box">' +
                '<div class="layui-form-item layui-col-xs12">' +
                    '<div class="layui-input-block" id="searchFilterCriteria">' +
                        '<select id="sel' + fieldId + '" lay-filter="sel' + fieldId + '" lay-search="" win-verify="required">' +
                            initTableSearchUtil.getOptions(searchParam) +
                        '</select>' +
                    '</div>' +
                '</div>' +
                '<div class="layui-form-item layui-col-xs12">' +
                    '<div class="layui-input-block" id="searchContent' + fieldId + '">' +
                    '</div>' +
                '</div>' +
                '<div class="layui-form-item layui-col-xs12" style="margin-top: 30px; text-align: center">' +
                    '<div class="layui-input-block">' +
                        '<button type="button" class="layui-btn layui-btn-primary searchCancle">取消</button>' +
                        '<button type="button" class="layui-btn layui-btn-normal searchDefine" table-id="' + tableId + '" field-id="' + fieldId + '"' +
                        ' field-name="' + fieldName + '">确定</button>' +
                    '</div>' +
                '</div>' +
            '</div>' +
        '</form>';
    },

    /**
     * 获取筛选条件的下拉框内容
     *
     * @param searchParam 高级查询的参数
     */
    getOptions: function (searchParam) {
        var searchCondition = searchParam.searchCondition;
        var options = "<option value=''>请选择</option>";
        $.each(searchCondition, function (i, item) {
            options += '<option value="' + item.operator + '">' + item.operatorName + '</option>';
        });
        return options;
    },

    /**
     * 获取表单组件
     *
     * @param fieldId 字段列id
     * @param searchParam 高级查询的参数
     */
    getFormUnit: function (fieldId, searchParam) {
        var type = searchParam.dataType;
        // 获取筛选条件
        var operator = $("#sel" + fieldId).val();
        if (type === 'input') {
            // 文本
            return '<input type="text" id="' + fieldId + '" name="' + fieldId + '" placeholder="请输入要搜索的内容" class="layui-input" />';
        } else if (type === 'date') {
            // 日期
            return '<input type="text" id="' + fieldId + '" name="' + fieldId + '" placeholder="请输入要搜索的内容" class="layui-input" />';
        } else if (type === 'user') {
            // 用户
            return '<div id="' + fieldId + '" class="layui-input search-user"></div>' +
                '<i class="fa fa-user-plus input-icon" id="userSearchSelPeople"></i>';
        } else if (type === 'userStaff') {
            // 员工
            return '<div id="' + fieldId + '" class="layui-input search-user-staff"></div>' +
                '<i class="fa fa-user-plus input-icon" id="userStaffSearchSelPeople"></i>';
        } else if (type === 'virtualSelect') {
            // 接口-下拉框  virtualDataFrom: {"url": "", "valueKey": "", "showKey": ""}
            var dataFrom = initTableSearchUtil.getVirtualSelectData(searchParam);
            var options = "<option value=''>全部</option>";
            $.each(dataFrom, function (i, item) {
                options += '<option value="' + item[searchParam.virtualDataFrom.valueKey] + '">' + item[searchParam.virtualDataFrom.showKey] + '</option>';
            });
            return initTableSearchUtil.setSelectBox(fieldId, operator, options);
        } else if (type === 'constantSelect') {
            // 常量-下拉框  constantDataFrom: [{"id": "", "name": ""}]
            var dataFrom = searchParam.constantDataFrom;
            var options = "<option value=''>全部</option>";
            $.each(dataFrom, function (i, item) {
                options += '<option value="' + item.id + '">' + item.name + '</option>';
            });
            return initTableSearchUtil.setSelectBox(fieldId, operator, options);
        }
    },

    /**
     * 接口-下拉框 获取数据
     *
     * @param searchParam 高级查询的参数
     * @returns {*[]}
     */
    getVirtualSelectData: function (searchParam) {
        var url = "";
        eval('url = ' + searchParam.virtualDataFrom.service + ' + "' + searchParam.virtualDataFrom.url + '"');
        var paramsStr = "";
        if (!isNull(searchParam.virtualDataFrom.params)) {
            $.each(searchParam.virtualDataFrom.params, function (key, value) {
                paramsStr += key + '=' + eval(value) + '&';
            });
        }
        // 加上参数
        url += '?' + paramsStr;
        var dataFrom = [];
        AjaxPostUtil.request({url: url, params: {}, type: 'json', method: searchParam.virtualDataFrom.method, callback: function(json) {
            dataFrom = [].concat(json.rows);
        }, async: false});
        return dataFrom;
    },

    /**
     * 设置下拉框的属性
     *
     * @param fieldId 字段列id
     * @param operator 筛选条件
     * @param options 可选项
     */
    setSelectBox: function (fieldId, operator, options) {
        if (operator == 'in') {
            return '<select id="' + fieldId + '" multiple lay-filter="' + fieldId + '" lay-search="" >' +
                options +
                '</select>';
        }
        return '<select id="' + fieldId + '" lay-filter="' + fieldId + '" lay-search="" >' +
            options +
            '</select>';
    },

    /**
     * 初始化事件，例如：日期的要初始化后才能使用
     *
     * @param fieldId 字段列id
     * @param searchParam 高级查询的参数
     */
    initFormUnitEvent: function (fieldId, searchParam) {
        var type = searchParam.dataType;
        // 获取筛选条件
        var operator = $("#sel" + fieldId).val();
        if (type === 'date') {
            var jsCon = '<script>layui.define(["laydate"], function(exports) {' +
                'var laydate = layui.laydate;laydate.render({elem: "#' + fieldId + '", type: "date", trigger: "click"});' +
                '})</script>';
            if (operator == 'between') {
                // 区间
                jsCon = '<script>layui.define(["laydate"], function(exports) {' +
                    'var laydate = layui.laydate;laydate.render({elem: "#' + fieldId + '", range: "~", trigger: "click"});' +
                    '})</script>';
            }
            $("#searchBox").append(jsCon);
        } else if (type === 'user') {
            systemCommonUtil.userReturnList = [];
            $("#" + fieldId).val('');
        } else if (type === 'userStaff') {
            if (operator == 'in') {
                systemCommonUtil.checkStaffMation = [];
            } else {
                systemCommonUtil.checkStaffMation = {};
            }
            $("#" + fieldId).val('');
        }
    },

    /**
     * 获取表单组件显示出来的值
     *
     * @param fieldId 字段列id
     * @param searchParam 高级查询的参数
     */
    getFormUnitShowValue: function (fieldId, searchParam) {
        var type = searchParam.dataType;
        // 获取筛选条件
        var operator = $("#sel" + fieldId).val();
        if (type === 'input') {
            return $("#" + fieldId).val();
        } else if (type === 'date') {
            // 日期
            return $("#" + fieldId).val();
        } else if (type === 'user') {
            // 用户
            if (systemCommonUtil.userReturnList.length == 0) {
                return null;
            }
            if (operator == 'in') {
                var text = [];
                $.each(systemCommonUtil.userReturnList, function (i, item) {
                    text.push(item.name);
                });
                return text.toString();
            }
            return systemCommonUtil.userReturnList[0].name;
        } else if (type === 'userStaff') {
            // 员工
            if (systemCommonUtil.checkStaffMation.length == 0) {
                return null;
            }
            if (operator == 'in') {
                var text = [];
                $.each(systemCommonUtil.checkStaffMation, function (i, item) {
                    text.push(item.name);
                });
                return text.toString();
            }
            return systemCommonUtil.checkStaffMation.jobNumber + '_' + systemCommonUtil.checkStaffMation.userName;
        } else if (type === 'virtualSelect' || type === 'constantSelect') {
            // 接口-下拉框  virtualDataFrom / 常量-下拉框  constantDataFrom
            if (operator == 'in') {
                var text = [];
                $.each($("#" + fieldId).find("option:selected"), function (i, item) {
                    text.push(item.outerText);
                });
                return text.toString();
            }
            return $("#" + fieldId).find("option:selected").text();
        }
    },

    /**
     * 获取表单组件没有显示出来的值,例如select等
     *
     * @param fieldId 字段列id
     * @param searchParam 高级查询的参数
     */
    getFormUnitHideValue: function (fieldId, searchParam) {
        var type = searchParam.dataType;
        // 获取筛选条件
        var operator = $("#sel" + fieldId).val();
        if (type === 'input') {
            return $("#" + fieldId).val();
        } else if (type === 'date') {
            // 日期
            if (operator == 'between') {
                var time = new Array();
                time.push($("#" + fieldId).val().split('~')[0].trim());
                time.push($("#" + fieldId).val().split('~')[1].trim());
                return time;
            }
            // 日期
            return $("#" + fieldId).val();
        } else if (type === 'user') {
            // 用户
            if (systemCommonUtil.userReturnList.length == 0) {
                return null;
            }
            if (operator == 'in') {
                var text = [];
                $.each(systemCommonUtil.userReturnList, function (i, item) {
                    text.push("'" + item.id + "'");
                });
                return text.toString();
            }
            return systemCommonUtil.userReturnList[0].id;
        } else if (type === 'userStaff') {
            // 员工
            if (systemCommonUtil.checkStaffMation.length == 0) {
                return null;
            }
            if (operator == 'in') {
                var text = [];
                $.each(systemCommonUtil.checkStaffMation, function (i, item) {
                    text.push("'" + item.id + "'");
                });
                return text.toString();
            }
            return systemCommonUtil.checkStaffMation.id;
        } else if (type === 'virtualSelect' || type === 'constantSelect') {
            // 接口-下拉框  virtualDataFrom / 常量-下拉框  constantDataFrom
            if (operator == 'in') {
                if (isNull($("#" + fieldId).val())) {
                    return null;
                }
                var value = [];
                $.each($("#" + fieldId).val(), function (i, item) {
                    value.push("'" + item + "'");
                });
                return value.toString();
            }
            return $("#" + fieldId).val();
        }
    },

    /**
     * 回显筛选值
     *
     * @param tableId 表格id
     * @param fieldId 字段列id
     * @param paramConfig 高级查询的参数配置
     */
    resetFormDefaultValue: function (tableId, fieldId, paramConfig) {
        // 判断是否有筛选历史，如果有，则回显
        var tableChooseMap = isNull(initTableSearchUtil.chooseMap[tableId]) ? {} : initTableSearchUtil.chooseMap[tableId];
        var confimValue = tableChooseMap[fieldId];
        if (!isNull(confimValue)) {
            // 获取筛选条件
            var operator = confimValue.operator;
            // 设置默认筛选条件
            $("#sel" + fieldId).val(operator);
            // 加载搜索框
            $("#searchContent" + fieldId).html(initTableSearchUtil.getFormUnit(fieldId, paramConfig));
            // 初始化事件，例如：日期的要初始化后才能使用
            initTableSearchUtil.initFormUnitEvent(fieldId, paramConfig);

            // 根据类型设置默认值
            var type = paramConfig.dataType;
            if (type === 'input') {
                $("#" + fieldId).val(confimValue.showValue);
            } else if (type === 'date') {
                // 日期
                $("#" + fieldId).val(confimValue.showValue);
            } else if (type === 'user') {
                // 用户
                var str = "";
                systemCommonUtil.userReturnList = [];
                var userIds = confimValue.hideValue.replaceAll("'", "").split(',');
                $.each(confimValue.showValue.split(','), function (i, value) {
                    str += '<span class="layui-badge layui-bg-blue skyeye-badge">' + value + '' +
                        '<i class="layui-icon layui-unselect layui-tab-close search-user-del" user-id="' + userIds[i] + '" title="删除">&#x1006;</i>' +
                        '</span>';
                    systemCommonUtil.userReturnList.push({
                        id: userIds[i],
                        name: value
                    });
                });
                $("#" + fieldId).html(str);
            } else if (type === 'userStaff') {
                // 员工
                var str = "";
                if (operator == 'in') {
                    systemCommonUtil.checkStaffMation = [];
                    var staffIds = confimValue.hideValue.replaceAll("'", "").split(',');
                    $.each(confimValue.showValue.split(','), function (i, value) {
                        str += '<span class="layui-badge layui-bg-blue skyeye-badge">' + value + '' +
                            '<i class="layui-icon layui-unselect layui-tab-close search-user-staff-del" staff-id="' + staffIds[i] + '" title="删除" operator="' + operator + '">&#x1006;</i>' +
                            '</span>';
                        systemCommonUtil.checkStaffMation.push({
                            id: staffIds[i],
                            name: value
                        });
                    });
                } else {
                    str = '<span class="layui-badge layui-bg-blue skyeye-badge">' + confimValue.showValue + '' +
                        '<i class="layui-icon layui-unselect layui-tab-close search-user-staff-del" staff-id="' + confimValue.hideValue + '" title="删除" operator="' + operator + '">&#x1006;</i>' +
                        '</span>';
                    systemCommonUtil.checkStaffMation = {
                        id: confimValue.hideValue,
                        jobNumber: confimValue.showValue.split('_')[0],
                        userName: confimValue.showValue.split('_')[1]
                    };
                }
                $("#" + fieldId).html(str);
            } else if (type === 'virtualSelect' || type === 'constantSelect') {
                // 接口-下拉框  virtualDataFrom / 常量-下拉框  constantDataFrom
                if (operator == 'in') {
                    $("#" + fieldId).val(confimValue.hideValue.replaceAll("'", "").split(','));
                } else {
                    $("#" + fieldId).val(confimValue.hideValue);
                }
            }
        }
    },

    /**
     * 初始化监听事件
     *
     * @param form form表单对象
     */
    initEvent: function (form) {

        // 筛选按钮点击
        $("body").on("click", ".layui-table-search", function (event) {
            // 阻止事件冒泡
            event.stopPropagation();
            $("#searchBox").remove();
            var tableId = $(this).attr("search-table-id");
            var fieldId = $(this).attr("search-sign");
            var paramConfig = initTableSearchUtil.getPointSearchParams(tableId, fieldId);
            var fieldName = $(this).parent().find('span').html();
            // 加载表单筛选框
            $("body").append(initTableSearchUtil.searchBox(tableId, fieldId, paramConfig, fieldName));

            // 设置位置
            $("#searchBox").css("left", $(this).offset().left - 5);
            $("#searchBox").css("top", $(this).offset().top + $(this).outerHeight());

            // 设置默认值
            initTableSearchUtil.resetFormDefaultValue(tableId, fieldId, paramConfig);
            form.render();
            form.on('select(sel' + fieldId + ')', function (data) {
                if (isNull(data.value)) {
                    return;
                }
                $("#searchContent" + fieldId).html(initTableSearchUtil.getFormUnit(fieldId, paramConfig));
                // 初始化事件，例如：日期的要初始化后才能使用
                initTableSearchUtil.initFormUnitEvent(fieldId, paramConfig);
                form.render();
            });

            // 点击空白处，下拉框隐藏-------开始
            var tag = $("#searchBox");
            var flag = true;
            $(document).bind("click", function (e) {
                // 点击空白处，设置的弹框消失
                var target = $(e.target);
                if (target.closest(tag).length == 0 && flag == true) {
                    $(tag).remove();
                    flag = false;
                }
            });
            // 点击空白处，下拉框隐藏-------结束
        });

        // 搜索
        $("body").on("click", ".search-btn-keyword", function() {
            var tableId = $(this).attr("id").replace('SearchTable', '');
            // 加载回调函数
            var mation = initTableSearchUtil.tableMap[tableId];
            if (typeof (mation.callback) == "function") {
                mation.callback();
            }
        });

        $("body").on("click", ".type-btn", function (e) {
            $(this).parent().find('.type-btn').removeClass("plan-select");
            $(this).addClass("plan-select");
            var tableId = $(this).attr('table-id');
            // 加载回调函数
            var mation = initTableSearchUtil.tableMap[tableId];
            if (typeof (mation.callback) == "function") {
                mation.callback();
            }
        });

        // 取消
        $("body").on("click", ".searchCancle", function (e) {
            $(".search-form").hide();
        });

        // 确定
        $("body").on("click", ".searchDefine", function (e) {
            var tableId = $(this).attr("table-id");
            var fieldId = $(this).attr("field-id");
            var paramConfig = initTableSearchUtil.getPointSearchParams(tableId, fieldId);

            if (isNull($("#sel" + fieldId).val())) {
                winui.window.msg('请选择筛选条件.', {icon: 2, time: 2000});
                return false;
            }
            var chooseValue = initTableSearchUtil.getFormUnitHideValue(fieldId, paramConfig);
            if (isNull(chooseValue)) {
                winui.window.msg('筛选值不能为空.', {icon: 2, time: 2000});
                return false;
            }

            // 点击确定获取值
            var confimValue = {
                "fieldId": fieldId,
                "fieldName": $(this).attr("field-name"),
                "operator": $("#sel" + fieldId).val(),
                "operatorName": $("#sel" + fieldId).find("option:selected").text(),
                "showValue": initTableSearchUtil.getFormUnitShowValue(fieldId, paramConfig),
                "hideValue": chooseValue
            };

            var tableChooseMap = isNull(initTableSearchUtil.chooseMap[tableId]) ? {} : initTableSearchUtil.chooseMap[tableId];
            tableChooseMap[fieldId] = confimValue;
            // 设置选中的值
            initTableSearchUtil.chooseMap[tableId] = tableChooseMap;
            // 移除选择框
            $("#searchBox").remove();
            // 展示筛选内容
            initTableSearchUtil.loadChooseHtml(tableId);
            // 加载回调函数
            var mation = initTableSearchUtil.tableMap[tableId];
            if (typeof (mation.callback) == "function") {
                mation.callback();
            }
        });

        // 删除筛选条件
        $("body").on("click", ".search-del", function (e) {
            var tableId = $(this).attr("table-id");
            var fieldId = $(this).attr("field-id");

            var tableChooseMap = isNull(initTableSearchUtil.chooseMap[tableId]) ? {} : initTableSearchUtil.chooseMap[tableId];
            delete tableChooseMap[fieldId];
            initTableSearchUtil.chooseMap[tableId] = tableChooseMap;

            $(this).parent().remove();
            // 加载回调函数
            var mation = initTableSearchUtil.tableMap[tableId];
            if (typeof (mation.callback) == "function") {
                mation.callback();
            }
        });

        // 用户选择
        $("body").on("click", "#userSearchSelPeople", function(e) {
            var operator = $("#searchBox").find("#searchFilterCriteria").find("select").val();
            var fieldId = $("#searchBox").find(".searchDefine").attr("field-id");
            if (operator == 'in') {
                systemCommonUtil.checkType = "1"; // 人员选择类型，1.多选；其他。单选
            } else {
                systemCommonUtil.checkType = "2"; // 人员选择类型，1.多选；其他。单选
            }
            systemCommonUtil.chooseOrNotMy = "1"; // 人员列表中是否包含自己--1.包含；其他参数不包含
            systemCommonUtil.chooseOrNotEmail = "2"; // 人员列表中是否必须绑定邮箱--1.必须；其他参数没必要
            systemCommonUtil.openSysUserStaffChoosePage(function (userReturnList) {
                var str = "";
                $.each(userReturnList, function (key, value) {
                    str += '<span class="layui-badge layui-bg-blue skyeye-badge">' + value.name + '' +
                        '<i class="layui-icon layui-unselect layui-tab-close search-user-del" user-id="' + value.id + '" title="删除">&#x1006;</i>' +
                        '</span>';
                });
                $("#" + fieldId).html(str);
            });
        });
        // 【用户选择】删除
        $("body").on("click", ".search-user-del", function(event) {
            var userId = $(this).attr('user-id');
            systemCommonUtil.userReturnList = [].concat(arrayUtil.removeArrayPointKey(systemCommonUtil.userReturnList, 'id', userId));
            $(this).parent().remove();
            // 阻止事件冒泡
            event.stopPropagation();
        });

        // 员工选择
        $("body").on("click", "#userStaffSearchSelPeople", function(e) {
            var operator = $("#searchBox").find("#searchFilterCriteria").find("select").val();
            var fieldId = $("#searchBox").find(".searchDefine").attr("field-id");
            if (operator == 'in') {
                systemCommonUtil.userStaffCheckType = true; // 选择类型，默认单选，true:多选，false:单选
            } else {
                systemCommonUtil.userStaffCheckType = false; // 选择类型，默认单选，true:多选，false:单选
            }
            systemCommonUtil.openSysAllUserStaffChoosePage(function (checkStaffMation){
                var str = "";
                if (operator == 'in') {
                    $.each(checkStaffMation, function (key, value) {
                        str += '<span class="layui-badge layui-bg-blue skyeye-badge">' + value.name + '' +
                            '<i class="layui-icon layui-unselect layui-tab-close search-user-staff-del" staff-id="' + value.id + '" title="删除" operator="' + operator + '">&#x1006;</i>' +
                            '</span>';
                    });
                } else {
                    str = '<span class="layui-badge layui-bg-blue skyeye-badge">' + checkStaffMation.jobNumber + '_' + checkStaffMation.userName + '' +
                        '<i class="layui-icon layui-unselect layui-tab-close search-user-staff-del" staff-id="' + checkStaffMation.id + '" title="删除" operator="' + operator + '">&#x1006;</i>' +
                        '</span>';
                }
                $("#" + fieldId).html(str);
            });
        });
        // 【员工选择】删除
        $("body").on("click", ".search-user-staff-del", function(event) {
            var staffId = $(this).attr('staff-id');
            var operator = $(this).attr('operator');
            if (operator == 'in') {
                systemCommonUtil.checkStaffMation = [].concat(arrayUtil.removeArrayPointKey(systemCommonUtil.checkStaffMation, 'id', staffId));
            } else {
                systemCommonUtil.checkStaffMation = {};
            }
            $(this).parent().remove();
            // 阻止事件冒泡
            event.stopPropagation();
        });

    },

    /**
     * 加载选中列筛选条件的内容
     *
     * @param tableId 表格id
     */
    loadChooseHtml: function (tableId) {
        $("#" + tableId)
        $("div[lay-id='" + tableId + "'] .layui-table-search").removeClass("layui-table-choose");
        var tableChooseMap = isNull(initTableSearchUtil.chooseMap[tableId]) ? {} : initTableSearchUtil.chooseMap[tableId];
        var str = "";
        $.each(tableChooseMap, function (key, value) {
            var fieldId = value.fieldId;
            $("span[search-table-id='" + tableId + "'][search-sign='" + fieldId + "']").addClass('layui-table-choose');
            str += '<span class="layui-badge layui-bg-blue skyeye-badge" title="' + value.showValue + '"><div>' + value.fieldName + ' ' + value.operatorName + ' ' + value.showValue + '' +
                '</div><i class="layui-icon layui-unselect layui-tab-close search-del" table-id="' + tableId + '" field-id="' + fieldId + '" title="删除">&#x1006;</i>' +
                '</span>';
        });
        $("#filter" + tableId).html(str);
    },

    /**
     * 获取指定表格以及指定列的筛选条件信息
     *
     * @param tableId 表格id
     * @param fieldId 字段列id
     */
    getPointSearchParams: function (tableId, fieldId) {
        var mation = initTableSearchUtil.tableMap[tableId];
        var searchParams = mation.searchParams;
        var result = {};
        $.each(searchParams, function (key, value) {
            var $fieldId = key + initTableSearchUtil.fileIdSuffix;
            if (fieldId === $fieldId) {
                result = value;
                return;
            }
        });
        return result;
    },

    /**
     * 获取搜索内容进行表格刷新
     *
     * @param tableId 表格id
     */
    getSearchValue: function (tableId) {
        var tableChooseMap = isNull(initTableSearchUtil.chooseMap[tableId]) ? {} : initTableSearchUtil.chooseMap[tableId];
        var searchCondition = [];
        $.each(tableChooseMap, function (key, confimValue) {
            searchCondition.push({
                "attributeKey": key.slice(0, key.lastIndexOf(initTableSearchUtil.fileIdSuffix)),
                "operator": confimValue.operator,
                "attributeValue": confimValue.hideValue
            });
        });
        var retult = {
            "dynamicCondition": JSON.stringify(searchCondition),
            "keyword": $("#" + tableId + "KeyWord").val(),
        };
        $.each($(".type-group"), function (i, item) {
            var idKey = $(item).attr('id');
            var chooseValue = $(item).find('.plan-select').data('type');
            retult[idKey] = chooseValue;
        });
        return retult;
    }

}
