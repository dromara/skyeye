
layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form'], function (exports) {
    winui.renderColor();
    var $ = layui.$,
        form = layui.form,
        table = layui.table;

    var memberId = GetUrlParam("id");
    table.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        url: sysMainMation.shopBasePath + 'queryMealMationByObjectId',
        where: getTableParams(),
        even: true,
        page: true,
        limits: getLimits(),
        limit: getLimit(),
        cols: [[
            { field: 'name', title: '套餐名称', width: 120, rowspan: '2', align: "left", templet: function (d) {
                return getNotUndefinedVal(d.mealMation?.name);
            }},
            { field: 'name', title: '商品名称', width: 150, rowspan: '2', align: "left", templet: function (d) {
                return getNotUndefinedVal(d.materialMation?.name);
            }},
            { field: 'name', title: '商品规格', width: 180, rowspan: '2', align: "left", templet: function (d) {
                return getNotUndefinedVal(d.normsMation?.name);
            }},
            { field: 'mealPrice', title: '套餐价格', rowspan: '2', width: 100, align: "left"},
            { field: 'codeNum', title: '商品规格编码', rowspan: '2', width: 180, align: "left"},
            { field: 'startTime', title: '开始时间', rowspan: '2', width: 100, align: "left"},
            { field: 'endTime', title: '结束时间', rowspan: '2', width: 100, align: "left"},
            { title: '退款订单', colspan: '2', align: 'center' },
            { field: 'state', title: '是否可用', rowspan: '2', width: 90, templet: function (d) {
                return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("whetherEnum", 'id', d.state, 'name');
            }},
            { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', rowspan: '2', align: 'center', width: 100, toolbar: '#tableBar'}
        ],[
            { field: 'isRefund1', title: '退款单号', align: 'center', width: 200, templet: function (d) {
                if (d.isRefund) {
                    return d.mealRefundOrder.oddNumber;
                }
                return "";
            }},
            { field: 'isRefund', title: '退款状态', align: 'center', width: 90, templet: function (d) {
                if (d.isRefund) {
                    return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("flowableStateEnum", 'id', d.mealRefundOrder.state, 'name');
                }
                return "";
            }}
        ]],
        done: function(json) {
            matchingLanguage();
            initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "暂不支持搜索", function () {
                table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
            });
        }
    });

    table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'returnOrder') { // 退款
            returnOrder(data);
        }
    });

    // 退款
    function returnOrder(data) {
        _openNewWindows({
            url: "../../tpl/refundMealOrder/refundMealOrder.html?id=" + data.id + '&price=' + data.mealPrice,
            title: '退款',
            pageId: "returnOrderPage",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }
        });
    }

    form.render();
    $("body").on("click", "#reloadTable", function() {
        loadTable();
    });
    function loadTable() {
        table.reloadData("messageTable", {where: getTableParams()});
    }

    function getTableParams() {
        return $.extend(true, {objectId: memberId}, initTableSearchUtil.getSearchValue("messageTable"));
    }

    exports('memberMealOrderList', {});
});
