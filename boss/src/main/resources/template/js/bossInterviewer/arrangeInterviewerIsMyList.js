var rowId = "";

// 获取面试官为当前登录用户的面试者信息列表
layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form', 'laydate'], function (exports) {
    winui.renderColor();
    var $ = layui.$,
        form = layui.form,
        laydate = layui.laydate,
        table = layui.table;

    laydate.render({elem: '#createTime', range: '~'});

    table.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        url: flowableBasePath + 'queryArrangementInterviewerIsMyList',
        where: getTableParams(),
        even: true,
        page: true,
        limits: getLimits(),
        limit: getLimit(),
        cols: [[
            { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
            { field: 'interviewName', title: '面试者', width: 100},
            { field: 'departmentName', title: '面试部门', width: 140},
            { field: 'jobName', title: '面试岗位', width: 150 },
            { field: 'interviewTime', title: '面试时间', width: 140, align: 'center' },
            { field: 'interviewer', title: '面试官', width: 140 },
            { field: 'state', title: '面试状态', width: 160, templet: function (d) {
                return bossUtil.showArrangeInterviewerStateName(d.state);
            }},
            { field: 'createTime', title: systemLanguage["com.skyeye.entryTime"][languageType], align: 'center', width: 150 },
            { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 257, toolbar: '#messageTableBar'}
        ]],
        done: function(){
            matchingLanguage();
        }
    });

    table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'details') { // 详情
            details(data);
        } else if (layEvent === 'interview') { // 面试
            interview(data);
        }
    });

    // 面试
    function interview(data) {
        rowId = data.id;
        _openNewWindows({
            url: "../../tpl/bossInterviewer/interviewerResult.html",
            title: '面试',
            pageId: "interviewerResult",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }
        });
    }

    // 详情
    function details(data) {
        rowId = data.id;
        _openNewWindows({
            url: "../../tpl/bossInterviewArrangement/bossInterviewArrangementDetails.html?type=interviewerResult",
            title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
            pageId: "bossInterviewArrangementDetails",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
            }
        });
    }

    form.render();
    form.on('submit(formSearch)', function (data) {
        if (winui.verifyForm(data.elem)) {
            table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
        }
        return false;
    });

    // 刷新
    $("body").on("click", "#reloadTable", function() {
        loadTable();
    });

    function loadTable() {
        table.reloadData("messageTable", {where: getTableParams()});
    }

    function getTableParams() {
        var startTime = "", endTime = "";
        if (!isNull($("#createTime").val())) {
            startTime = $("#createTime").val().split('~')[0].trim() + ' 00:00:00';
            endTime = $("#createTime").val().split('~')[1].trim() + ' 23:59:59';
        }
        return {
            state: $("#state").val(),
            startTime: startTime,
            endTime: endTime
        };
    }

    exports('arrangeInterviewerIsMyList', {});
});
