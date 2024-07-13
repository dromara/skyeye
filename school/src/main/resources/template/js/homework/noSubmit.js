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

    var assignmentId = GetUrlParam("id");

    table.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'get',
        url: sysMainMation.schoolBasePath + 'queryAssignmentNotSubListByAssignmentId',
        where: getTableParams(),
        even: true,
        page: true,
        limits: getLimits(),
        limit: getLimit(),
        cols: [[
            { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
            { field: 'name', title: '姓名', align: 'center', width: 200},
            { field: 'accountNumber', title: '学号', align: 'left', width: 200 },
            { field: 'signature', title: '个性签名', align: 'left',width: 250},
            { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 250 }
        ]],
        done: function(json) {
            matchingLanguage();
            initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "暂不支持搜索", function () {
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
        let params = {
            assignmentId: assignmentId
        }
        return $.extend(true, params, initTableSearchUtil.getSearchValue("messageTable"));
    }

    exports('noSubmit', {});
});
