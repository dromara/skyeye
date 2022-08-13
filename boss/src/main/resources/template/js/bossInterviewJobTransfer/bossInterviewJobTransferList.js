var rowId = "";

// 岗位调动申请申请
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

    // 新增
    authBtn('1651308552871');

    laydate.render({elem: '#createTime', range: '~'});

    table.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        url: flowableBasePath + 'queryBossInterviewJobTransferList',
        where: getTableParams(),
        even: true,
        page: true,
        limits: getLimits(),
        limit: getLimit(),
        cols: [[
            { title: systemLanguage["com.skyeye.serialNumber"][languageType], rowspan: '2', type: 'numbers'},
            { field: 'transferStaffName', title: '申请人', rowspan: '2', width: 140},
            { title: '原岗位信息', align: 'center', colspan: '4'},
            { title: '申请岗位信息', align: 'center', colspan: '4'},
            { field: 'transferType', title: '调岗类型', rowspan: '2', width: 90, templet: function(d) {
                return bossUtil.getTransferTypeNameById(d.transferType);
            }},
            { field: 'processInstanceId', title: '流程ID', rowspan: '2', width: 100, templet: function (d) {
                return '<a lay-event="processDetails" class="notice-title-click">' + d.processInstanceId + '</a>';
            }},
            { field: 'stateName', title: '状态', rowspan: '2', width: 90, templet: function(d) {
                return activitiUtil.showStateName2(d.state, 1);
            }},
            { field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], rowspan: '2', width: 140 },
            { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], rowspan: '2', align: 'center', width: 150 },
            { field: 'lastUpdateName', title: systemLanguage["com.skyeye.lastUpdateName"][languageType], rowspan: '2', align: 'left', width: 140 },
            { field: 'lastUpdateTime', title: systemLanguage["com.skyeye.lastUpdateTime"][languageType], rowspan: '2', align: 'center', width: 150},
            { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', rowspan: '2', align: 'center', width: 257, toolbar: '#messageTableBar'}
        ],
            [
                { field: 'primaryCompanyName', title: '企业', align: 'left', width: 150},
                { field: 'primaryDepartmentName', title: '部门', align: 'left', width: 150},
                { field: 'primaryJobName', title: '岗位', align: 'left', width: 150},
                { field: 'primaryJobScoreName', title: '岗位定级', align: 'left', width: 150},
                { field: 'currentCompanyName', title: '企业', align: 'left', width: 150},
                { field: 'currentDepartmentName', title: '部门', align: 'left', width: 150},
                { field: 'currentJobName', title: '岗位', align: 'left', width: 150},
                { field: 'currentJobScoreName', title: '岗位定级', align: 'left', width: 150}
            ]
        ],
        done: function(){
            matchingLanguage();
        }
    });

    table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'details') { // 详情
            details(data);
        }else if (layEvent === 'edit') { // 编辑
            edit(data);
        }else if (layEvent === 'subApproval') { // 提交审批
            subApproval(data);
        }else if(layEvent === 'cancellation') { // 作废
            cancellation(data);
        }else if(layEvent === 'processDetails') { // 流程详情
            activitiUtil.activitiDetails(data);
        }else if(layEvent === 'revoke') { // 撤销申请
            revoke(data);
        }
    });

    // 添加
    $("body").on("click", "#addBean", function() {
        _openNewWindows({
            url: "../../tpl/bossInterviewJobTransfer/bossInterviewJobTransferAdd.html",
            title: systemLanguage["com.skyeye.addPageTitle"][languageType],
            pageId: "bossInterviewJobTransferAdd",
            area: ['90vw', '90vh'],
            callBack: function(refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
    });

    // 撤销
    function revoke(data){
        layer.confirm('确认撤销该申请吗？', { icon: 3, title: '撤销操作' }, function (index) {
            layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "revokeBossInterviewJobTransfer", params: {processInstanceId: data.processInstanceId}, type: 'json', method: "PUT", callback: function (json) {
                winui.window.msg("提交成功", {icon: 1, time: 2000});
                loadTable();
            }});
        });
    }

    // 编辑申请
    function edit(data){
        rowId = data.id;
        _openNewWindows({
            url: "../../tpl/bossInterviewJobTransfer/bossInterviewJobTransferEdit.html",
            title: systemLanguage["com.skyeye.editPageTitle"][languageType],
            pageId: "bossInterviewJobTransferEdit",
            area: ['90vw', '90vh'],
            callBack: function(refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }
        });
    }

    // 提交审批
    function subApproval(data){
        layer.confirm(systemLanguage["com.skyeye.approvalOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.approvalOperation"][languageType]}, function (index) {
            layer.close(index);
            activitiUtil.startProcess(sysActivitiModel["bossInterviewJobTransfer"]["key"], function (approvalId) {
                var params = {
                    id: data.id,
                    approvalId: approvalId
                };
                AjaxPostUtil.request({url: flowableBasePath + "editBossInterviewJobTransferToSubApproval", params: params, type: 'json', method: "POST", callback: function (json) {
                    winui.window.msg("提交成功", {icon: 1, time: 2000});
                    loadTable();
                }});
            });
        });
    }

    // 作废
    function cancellation(data){
        layer.confirm('确认作废该申请吗？', { icon: 3, title: '作废操作' }, function (index) {
            layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "updateBossInterviewJobTransferToCancellation", params: {id: data.id}, type: 'json', method: "PUT", callback: function (json) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
        });
    }

    // 详情
    function details(data){
        rowId = data.id;
        _openNewWindows({
            url: "../../tpl/bossInterviewJobTransfer/bossInterviewJobTransferDetails.html",
            title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
            pageId: "bossInterviewJobTransferDetails",
            area: ['90vw', '90vh'],
            callBack: function(refreshCode) {
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

    function loadTable(){
        table.reloadData("messageTable", {where: getTableParams()});
    }

    function getTableParams(){
        var startTime = "", endTime = "";
        if(!isNull($("#createTime").val())){
            startTime = $("#createTime").val().split('~')[0].trim() + ' 00:00:00';
            endTime = $("#createTime").val().split('~')[1].trim() + ' 23:59:59';
        }
        return {
            state: $("#state").val(),
            startTime: startTime,
            endTime: endTime
        };
    }

    exports('bossInterviewJobTransferList', {});
});
