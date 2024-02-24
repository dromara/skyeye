
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
        method: 'post',
        url: sysMainMation.scheduleBasePath + 'syseveschedule019',
        where: getTableParams(),
        even: true,
        page: true,
        limits: getLimits(),
        limit: getLimit(),
        cols: [[
            { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
            { field: 'name', title: '标题', width: 150 },
            { field: 'startTime', title: '开始时间', align: 'center', width: 150 },
            { field: 'endTime', title: '结束时间', align: 'center', width: 150 },
            { field: 'allDay', title: '是否全天', align: 'center', width: 80, templet: function (d) {
                return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("whetherEnum", 'id', d.allDay, 'name');
            }},
            { field: 'type', title: '日程类型', width: 100, templet: function (d) {
                return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("checkDayType", 'id', d.type, 'name');
            }},
            { field: 'imported', title: '重要性', align: 'center', width: 100, templet: function (d) {
                return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("scheduleImported", 'id', d.imported, 'name');
            }},
            { field: 'remindTime', title: '提醒时间', align: 'center', width: 150 },
            { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 }
        ]],
        done: function(json) {
            matchingLanguage();
            initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "请输入名称", function () {
                table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
            });
        }
    });

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

    exports('myScheduleTableList', {});
});
