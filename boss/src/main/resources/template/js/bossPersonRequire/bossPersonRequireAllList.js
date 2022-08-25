var rowId = "";

// 获取所有审批通过状态之后的人员需求申请列表
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
        url: flowableBasePath + 'queryAllBossPersonRequireList',
        where: getTableParams(),
        even: true,
        page: true,
        limits: getLimits(),
        limit: getLimit(),
        cols: [[
            { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
            { field: 'createName', title: '申请人', width: 140},
            { field: 'applyDepartmentName', title: '申请人部门', width: 140},
            { field: 'recruitJobName', title: '需求岗位', width: 150 },
            { field: 'recruitDepartmentName', title: '需求部门', width: 140 },
            { field: 'wages', title: '薪资', width: 120 },
            { field: 'recruitNum', title: '需求人数', width: 100 },
            { field: 'processInstanceId', title: '流程ID', width: 100, templet: function (d) {
                return '<a lay-event="processDetails" class="notice-title-click">' + d.processInstanceId + '</a>';
            }},
            { field: 'stateName', title: '状态', width: 90, templet: function (d) {
                if(d.state == 6){
                    return '<span class="state-new">招聘中</span>';
                } else if(d.state == 7){
                    return '<span class="state-new">招聘结束</span>';
                } else{
                    return activitiUtil.showStateName2(d.state, 1);
                }
            }},
            { field: 'createTime', title: systemLanguage["com.skyeye.entryTime"][languageType], width: 150 },
            { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 257, toolbar: '#messageTableBar'}
        ]],
        done: function(json) {
            matchingLanguage();
        }
    });

    table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'details') { // 详情
            details(data);
        } else if (layEvent === 'processDetails') { // 流程详情
            activitiUtil.activitiDetails(data);
        } else if (layEvent === 'setPersonLiable') { // 设置责任人
            setPersonLiable(data);
        }
    });

    // 设置责任人
    function setPersonLiable(data) {
        rowId = data.id;
        _openNewWindows({
            url: "../../tpl/bossPersonRequire/bossPersonRequirePersonLiable.html",
            title: '设置责任人',
            pageId: "bossPersonRequirePersonLiable",
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
            url: "../../tpl/bossPersonRequire/bossPersonRequireDetails.html",
            title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
            pageId: "bossPersonRequireDetails",
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

    exports('bossPersonRequireAllList', {});
});
