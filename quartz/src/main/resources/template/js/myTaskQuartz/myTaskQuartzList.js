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
            url: reqBasePath + 'sysquartz002',
            where: getTableParams(),
            even: true,
            page: true,
            limits: getLimits(),
            limit: getLimit(),
            cols: [[
                { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
                { field: 'name', title: '任务名称', width: 250 },
                { field: 'groups', title: '任务分组', align: 'left', width: 160 },
                { field: 'cron', title: 'CRON表达式', width: 180 },
                { field: 'remark', title: '备注', align: 'left', width: 200 }
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

    // 刷新数据
    $("body").on("click", "#reloadTable", function(){
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

    exports('myTaskQuartzList', {});
});
