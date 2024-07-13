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
        url: sysMainMation.schoolBasePath + 'queryAssignmentSubListByAssignmentId',
        where: getTableParams(),
        even: true,
        page: true,
        limits: getLimits(),
        limit: getLimit(),
        cols: [[
            { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
            { field: 'name', title: '姓名', align: 'left', width: 150, templet: function(d) {
                    return getNotUndefinedVal(d.createMation?.name);
            }},
            { field: 'accountNumber', title: '学号', align: 'left', width: 180, templet: function(d) {
                    return getNotUndefinedVal(d.createMation?.accountNumber);
            }},
            { field: 'content', title: '内容', align: 'left', width: 180, templet: function (d) {
                    return '<a lay-event="details" class="notice-title-click">' + d.content + '</a>';
            }},
            { field: 'state', title: '状态', align: 'left',width: 150, templet: function(d) {
                var str = '';
                if (d.state == 'beCorrected') {
                    str +='未批改';
                }else{
                    str +='已批改';
                }
                    return str;
            }},
            { field: 'score', title: '分数', align: 'left',width: 150},
            { field: 'lastUpdateTime', title: '最后提交时间', align: 'center', width: 250 },
            { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 257, toolbar: '#tableBar' }
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
        if (layEvent === 'correct') { //批改
            correct(data);
        } else if (layEvent === 'details') { //详情
            details(data);
        }
    });

    // 详情
    function details(data) {
        parent.parent._openNewWindows({
            url: systemCommonUtil.getUrl('FP2024071300001&id=' + data.id, null),
            title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
            pageId: "homeworkDetails",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
            }});
    }

    //批改
    function correct(data) {
        console.log(11)
        parent.parent._openNewWindows({
            url: '../../tpl/homework/correct.html?id=' + data.id,
            title: '作业批改',
            pageId: "homeworkCorrect",
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
        let params = {
            assignmentId: assignmentId
        }
        return $.extend(true, params, initTableSearchUtil.getSearchValue("messageTable"));
    }

    exports('hasSubmit', {});
});
