
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
     * @param callback 搜索条件点击确定时的回调，用来刷新表格
     */
    initAdvancedSearch: function ($table, searchParams, form, callback) {
        var tableId = $table.id;
        // 同一个表格只加载一次
        if(isNull(initTableSearchUtil.tableMap[tableId])){
            initTableSearchUtil.tableMap[tableId] = {
                table: $table,
                searchParams: searchParams,
                callback: callback
            };
            // 加载筛选条件展示框
            $("div[lay-id='" + tableId + "']").parent().prepend('<div class="filter-search-box" id="filter' + tableId + '"></div>');
            // 初始化监听事件
            initTableSearchUtil.initEvent(form);
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
        var th = $("div[lay-id='" + tableId + "'] .layui-table-header").find("thead").eq(0).find("th");
        $.each(searchParams, function (key, value) {
            $.each(th, function (j, _th) {
                var _this = $(_th);
                if (_this.attr("data-field") == key) {
                    var fieldId = key + initTableSearchUtil.fileIdSuffix;
                    // 加载筛选标识
                    _this.find(".layui-table-cell").append(initTableSearchUtil.searchSignHtml(tableId, fieldId));
                }
            });
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
                    '<div class="layui-input-block">' +
                        '<select id="sel' + fieldId + '" lay-filter="' + fieldId + '" lay-search="" win-verify="required">' +
                            initTableSearchUtil.getOptions(searchParam) +
                        '</select>' +
                    '</div>' +
                '</div>' +
                '<div class="layui-form-item layui-col-xs12">' +
                    '<div class="layui-input-block">' +
                        initTableSearchUtil.getFormUnit(fieldId, searchParam) +
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
        var options = "";
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
        if (type === 'input') {
            return '<input type="text" id="' + fieldId + '" name="' + fieldId + '" placeholder="请输入要搜索的内容" class="layui-input" />';
        }
    },

    /**
     * 获取表单组件的值
     *
     * @param fieldId 字段列id
     * @param searchParam 高级查询的参数
     */
    getFormUnitValue: function (fieldId, searchParam) {
        var type = searchParam.dataType;
        if (type === 'input') {
            return $("#" + fieldId).val();
        }
    },

    /**
     * 回显筛选值
     *
     * @param tableId 表格id
     * @param fieldId 字段列id
     * @param searchParam 高级查询的参数
     */
    resetFormDefaultValue: function (tableId, fieldId, searchParam) {
        // 判断是否有筛选历史，如果有，则回显
        var tableChooseMap = isNull(initTableSearchUtil.chooseMap[tableId]) ? {} : initTableSearchUtil.chooseMap[tableId];
        var confimValue = tableChooseMap[fieldId];
        if (!isNull(confimValue)) {
            $("#sel" + fieldId).val(confimValue.operator);

            var type = searchParam.dataType;
            if (type === 'input') {
                return $("#" + fieldId).val(confimValue.value);
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
            // 加载筛选框
            $("body").append(initTableSearchUtil.searchBox(tableId, fieldId, paramConfig, fieldName));
            // 设置位置
            $("#searchBox").css("left", $(this).offset().left - 5);
            $("#searchBox").css("top", $(this).offset().top + $(this).outerHeight());

            // 设置默认值
            initTableSearchUtil.resetFormDefaultValue(tableId, fieldId, paramConfig);
            form.render();

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

        // 取消
        $("body").on("click", ".searchCancle", function (e) {
            $(".search-form").hide();
        });

        // 确定
        $("body").on("click", ".searchDefine", function (e) {
            var tableId = $(this).attr("table-id");
            var fieldId = $(this).attr("field-id");
            var paramConfig = initTableSearchUtil.getPointSearchParams(tableId, fieldId);

            var chooseValue = initTableSearchUtil.getFormUnitValue(fieldId, paramConfig);
            if (isNull(chooseValue)) {
                winui.window.msg('请输入筛选值.', {icon: 2, time: 2000});
                return false;
            }

            // 点击确定获取值
            var confimValue = {
                "fieldName": $(this).attr("field-name"),
                "operator": $("#sel" + fieldId).val(),
                "operatorName": $("#sel" + fieldId).find("option:selected").text(),
                "value": chooseValue
            };

            var tableChooseMap = isNull(initTableSearchUtil.chooseMap[tableId]) ? {} : initTableSearchUtil.chooseMap[tableId];
            tableChooseMap[fieldId] = confimValue;
            // 设置选中的值
            initTableSearchUtil.chooseMap[tableId] = tableChooseMap;
            // 移除选择框
            $("#searchBox").remove();
            // 展示筛选内容
            initTableSearchUtil.loadChooseHtml(tableId, fieldId);
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
        });

    },

    /**
     * 加载选中列筛选条件的内容
     *
     * @param tableId 表格id
     * @param fieldId 字段列id
     */
    loadChooseHtml: function (tableId, fieldId) {
        var tableChooseMap = isNull(initTableSearchUtil.chooseMap[tableId]) ? {} : initTableSearchUtil.chooseMap[tableId];
        var str = "";
        $.each(tableChooseMap, function (key, value) {
            str += '<span class="layui-badge layui-bg-blue skyeye-badge">' + value.fieldName + ' ' + value.operatorName + ' ' + value.value + '' +
                '<i class="layui-icon layui-unselect layui-tab-close search-del" table-id="' + tableId + '" field-id="' + fieldId + '" title="删除">&#x1006;</i>' +
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
                "attributeValue": confimValue.value
            });
        });
        return {
            "dynamicCondition": JSON.stringify(searchCondition)
        };
    }

}