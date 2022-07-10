
// 表格高级查询插件
var initTableSearchUtil = {

    // 多个表格加载时存储的对象信息
    tableMap: {},

    // 列ID后缀
    fileIdSuffix: "Search",

    /**
     * 加载表格的高级查询
     *
     * @param $table 表格对象
     * @param searchParams 高级查询的参数
     * @param form form表单对象
     */
    initAdvancedSearch: function ($table, searchParams, form) {
        if(isNull(initTableSearchUtil.tableMap[$table.id])){
            initTableSearchUtil.tableMap[$table.id] = {
                table: $table,
                searchParams: searchParams
            };
        }
        initTableSearchUtil.loadSearchSign($table.id, searchParams, form);
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
        // 初始化监听事件
        initTableSearchUtil.initEvent(form);
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
     */
    searchBox: function (tableId, fieldId, searchParam) {
        return '<form class="layui-form search-form" action="" id="searchBox">' +
            '<div class="box">' +
                '<div class="layui-form-item layui-col-xs12">' +
                    '<div class="layui-input-block">' +
                        '<select id="' + fieldId + '" lay-filter="' + fieldId + '" lay-search="" win-verify="required">' +
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
                        '<button type="button" class="layui-btn layui-btn-normal" table-id="' + tableId + '">确定</button>' +
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
            options += '<option value="' + item.value + '">' + item.title + '</option>';
        });
        return options;
    },

    /**
     * 获取表单组件
     * @param fieldId 字段列id
     * @param searchParam 高级查询的参数
     */
    getFormUnit: function (fieldId, searchParam) {
        var type = searchParam.type;
        if (type === 'input') {
            return '<input type="text" id="' + fieldId + '" name="' + fieldId + '" placeholder="请输入要搜索的内容" class="layui-input" />';
        }
    },

    /**
     * 初始化监听事件
     *
     * @param form form表单对象
     */
    initEvent: function (form) {

        // 筛选按钮点击
        $("body").on("click", ".layui-table-search", function (e) {
            $("#searchBox").remove();
            var tableId = $(this).attr("search-table-id");
            var fieldId = $(this).attr("search-sign");
            var value = initTableSearchUtil.getPointSearchParams(tableId, fieldId);
            // 加载筛选框
            $("body").append(initTableSearchUtil.searchBox(tableId, fieldId, value));
            // 设置位置
            $("#searchBox").css("left", document.body.scrollLeft + event.clientX + 10);
            $("#searchBox").css("top", document.body.scrollLeft + event.clientY + 18);
            form.render();
        });

        // 取消
        $("body").on("click", ".searchCancle", function (e) {
            $(".search-form").hide();
        });

    },

    /**
     * 获取指定表格，指定列的筛选条件信息
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
    }

}
