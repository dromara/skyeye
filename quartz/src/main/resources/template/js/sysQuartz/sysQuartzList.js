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
            url: reqBasePath + 'sysquartz001',
            where: getTableParams(),
            even: true,
            page: true,
            limits: getLimits(),
            limit: getLimit(),
            cols: [[
                { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
                { field: 'name', title: '任务名称', width: 220 },
                { field: 'groups', title: '任务分组', align: 'left', width: 120 },
                { field: 'cron', title: 'CRON表达式', width: 180 },
                { field: 'quartzKey', title: '任务Key', width: 160 },
                { field: 'runHistoryBtn', title: '执行历史', align: "center", width: 120, templet: function (d) {
                    return '<a lay-event="runHistoryBtnDetail" class="notice-title-click">执行历史</a>';
                } },
                { field: 'remark', title: '备注', align: 'left', width: 200 },
                { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 100, toolbar: '#tableBar'}
            ]],
            done: function(){
                matchingLanguage();
            }
        });

        table.on('tool(messageTable)', function (obj) {
            var data = obj.data;
            var layEvent = obj.event;
            if (layEvent === 'runTask') { // 启动任务
                runTask(data);
            }else if (layEvent === 'runHistoryBtnDetail') { // 执行历史
                runHistoryBtnDetail(data);
            }
        });
    }

    // 启动任务
    function runTask(data){
        layer.confirm('确认启动任务吗？', {icon: 3, title: '启动任务'}, function(index){
            layer.close(index);
            AjaxPostUtil.request({url: reqBasePath + "sysquartz003", params:{rowId: data.id}, type: 'json', callback: function (json) {
                if (json.returnCode == 0) {
                    winui.window.msg('启动成功', {icon: 1, time: 2000});
                    loadTable();
                } else {
                    winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
                }
            }});
        });
    }

    // 执行历史
    function runHistoryBtnDetail(data){
        _openNewWindows({
            url: "../../tpl/sysQuartzRunHistory/sysQuartzRunHistory.html?quartzId=" + data.id,
            title: "执行历史",
            pageId: "sysQuartzRunHistory",
            area: ['90vw', '90vh'],
            callBack: function(refreshCode){
            }});
    }

    form.render();
    form.on('submit(formSearch)', function (data) {
        if (winui.verifyForm(data.elem)) {
            refreshTable();
        }
        return false;
    });

    // 刷新数据
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

    exports('sysQuartzList', {});
});
