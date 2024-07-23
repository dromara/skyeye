
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

    table.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'POST',
        url: sysMainMation.erpBasePath + 'queryInventoryChildList',
        where: getTableParams(),
        even: true,
        page: true,
        limits: getLimits(),
        limit: getLimit(),
        cols: [[
            { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers', rowspan: '2' },
            { field: 'oddNumber', title: '单号', width: 200, align: 'center', templet: function (d) {
                return '<a lay-event="details" class="notice-title-click">' + d.oddNumber + '</a>';
            }},
            { field: 'materialId', title: '产品名称', align: 'left', width: 150, templet: function (d) {
                return getNotUndefinedVal(d.materialMation?.name);
            }},
            { field: 'normsId', title: '产品规格', align: 'left', width: 150, templet: function (d) {
                return getNotUndefinedVal(d.normsMation?.name);
            }},
            { field: 'depotId', title: "仓库", align: 'left', width: 150, templet: function (d) {
                return getNotUndefinedVal(d.depotMation?.name);
            }},
            { field: 'planStartTime', title: '计划开始时间', rowspan: '2', align: 'center', width: 140 },
            { field: 'planEndTime', title: '计划结束时间', rowspan: '2', align: 'center', width: 140 },
            { field: 'lossNum', title: '盘亏数量', rowspan: '2', align: 'center', width: 140 },
            { field: 'realNumber', title: '实际盘点数量', rowspan: '2', align: 'center', width: 140 },
            { field: 'profitNum', title: '盘盈数量', rowspan: '2', align: 'center', width: 140 },
            { field: 'planNumber', title: '盘点总数量', rowspan: '2', align: 'center', width: 140 },
            { field: 'state', title: '状态', width: 150, align: 'center', templet: function (d) {
                return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("inventoryChildState", 'id', d.state, 'name');
            }},
            { field: 'type', title: '类型', width: 90, align: 'center', templet: function (d) {
                return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("materialNormsCodeType", 'id', d.type, 'name');
            }},
            { field: 'unitPrice', title: '单价', rowspan: '2', align: 'center', width: 140 },
            { title: systemLanguage["com.skyeye.operation"][languageType], rowspan: '2', fixed: 'right', align: 'center', width: 200, toolbar: '#tableBar'}
        ]],
        done: function(json) {
            matchingLanguage();
            initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "请输入单号", function () {
                table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
            });
        }
    });

    table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'complete') { //删除
            complete(data);
        }
    });

    // 完成
    function complete(data) {
        _openNewWindows({
            url: "../../tpl/inventoryTaskList/complateInventoryChild.html?id=" + data.id,
            title: "盘点完成",
            pageId: "complateInventoryChild",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
    }

    form.render();
    $("body").on("click", "#reloadTable", function() {
        loadTable();
    });
    function loadTable() {
        table.reloadData("messageTable", {where: getTableParams()});
    }

    function getTableParams() {
        return $.extend(true, {}, initTableSearchUtil.getSearchValue("messageTable"));
    }

    exports('myTaskInventoryList', {});
});
