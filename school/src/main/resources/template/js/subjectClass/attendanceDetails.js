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

    var id = GetUrlParam("id");
    var checkList = new Array();

    var params = {
        id: id,
    };
    AjaxPostUtil.request({url: sysMainMation.schoolBasePath + "queryCheckworkById", params: params, type: 'json', method: 'GET', callback: function (json) {
        checkList=json.bean.checkworkSignList
        console.log(checkList)
    }, async: false});

    table.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'get',
        data: checkList,
        where: getTableParams(),
        even: false,
        page: false,
        rowDrag: {
            trigger: 'row',
            done: function(obj) {}
        },
        cols: [[
            { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
            { field: 'name', title: '姓名', align: 'center', width: 200, templet: function (d) {
                return getNotUndefinedVal(d.userMation?.realName);
            }},
            { field: 'name', title: '学号', align: 'center', width: 200, templet: function (d) {
                return getNotUndefinedVal(d.userMation?.studentNumber);
            }},
            { field: 'state', title: '签到状态', align: 'left',width: 150, templet: function(d) {
                return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("checkworkStateEnum", 'id', d.state, 'name');
            }},
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
            id: id
        }
        return $.extend(true, params, initTableSearchUtil.getSearchValue("messageTable"));
    }

    exports('attendanceDetails', {});
});
