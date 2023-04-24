
var rowId = "";

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
        url: shopBasePath + 'queryMyWriteMemberList',
        where: getTableParams(),
        even: true,
        page: true,
        limits: getLimits(),
        limit: getLimit(),
        cols: [[
            { title: systemLanguage["com.skyeye.serialNumber"][languageType], fixed: 'left', type: 'numbers' },
            { field: 'name', title: '会员称呼', align: 'left', width: 140, fixed: 'left' },
            { field: 'phone', title: '联系电话', align: 'center', width: 100},
            { field: 'email', title: '电子邮箱', align: 'left', width: 120},
            { field: 'enabled', title: '状态', align: 'center', width: 80, templet: function (d) {
                return shopUtil.getEnableStateName(d.enabled);
            }}
        ]],
	    done: function(json) {
	    	matchingLanguage();
            initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "请输入会员称呼，联系电话", function () {
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

    exports('myWriteMemberList', {});
});
