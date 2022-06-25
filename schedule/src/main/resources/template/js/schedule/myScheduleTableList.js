
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

    initLoadTable();
    function initLoadTable(){
        table.render({
            id: 'messageTable',
            elem: '#messageTable',
            method: 'post',
            url: reqBasePath + 'syseveschedule019',
            where: getTableParams(),
            even: true,
            page: true,
            limits: getLimits(),
            limit: getLimit(),
            cols: [[
                { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
                { field: 'title', title: '标题', width: 150 },
                { field: 'scheduleStartTime', title: '开始时间', align: 'center', width: 150 },
                { field: 'scheduleEndTime', title: '结束时间', align: 'center', width: 150 },
                { field: 'allDay', title: '是否全天', align: 'center', width: 80 },
                { field: 'typeName', title: '日程类型', width: 100 },
                { field: 'scheduleImport', title: '重要性', align: 'center', width: 100 },
                { field: 'scheduleRemindTime', title: '提醒时间', align: 'center', width: 150 },
                { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 }
            ]],
            done: function(){
                matchingLanguage();
            }
        });

        table.on('tool(messageTable)', function (obj) {
            var data = obj.data;
            var layEvent = obj.event;
        });
    }

    form.render();
    form.on('submit(formSearch)', function (data) {
        if (winui.verifyForm(data.elem)) {
            refreshTable();
        }
        return false;
    });

    //刷新数据
    $("body").on("click", "#reloadTable", function() {
        loadTable();
    });

    function loadTable(){
        table.reload("messageTable", {where: getTableParams()});
    }

    function refreshTable(){
        table.reload("messageTable", {page: {curr: 1}, where: getTableParams()});
    }

    function getTableParams(){
        return {};
    }

    exports('myScheduleTableList', {});
});
