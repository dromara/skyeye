var rowId = "";

// 人员需求申请
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
    authBtn('1649569413156');

    laydate.render({
        elem: '#createTime',
        range: '~'
    });

    table.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        url: flowableBasePath + 'queryBossPersonRequireList',
        where: getTableParams(),
        even: true,
        page: true,
        limits: getLimits(),
        limit: getLimit(),
        cols: [[
            { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
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
            { field: 'createTime', title: systemLanguage["com.skyeye.entryTime"][languageType], width: 150},
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
            url: "../../tpl/bossPersonRequire/bossPersonRequireAdd.html",
            title: systemLanguage["com.skyeye.addPageTitle"][languageType],
            pageId: "bossPersonRequireAdd",
            area: ['90vw', '90vh'],
            callBack: function(refreshCode){
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
    });

    // 撤销
    function revoke(data){
        layer.confirm('确认撤销该申请吗？', { icon: 3, title: '撤销操作' }, function (index) {
            layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "revokeBossPersonRequire", params: {processInstanceId: data.processInstanceId}, type: 'json', method: "PUT", callback: function (json) {
                if (json.returnCode == 0) {
                    winui.window.msg("提交成功", {icon: 1, time: 2000});
                    loadTable();
                } else {
                    winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
                }
            }});
        });
    }

    // 编辑申请
    function edit(data){
        rowId = data.id;
        _openNewWindows({
            url: "../../tpl/bossPersonRequire/bossPersonRequireEdit.html",
            title: systemLanguage["com.skyeye.editPageTitle"][languageType],
            pageId: "bossPersonRequireEdit",
            area: ['90vw', '90vh'],
            callBack: function(refreshCode){
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }
        });
    }

    // 提交审批
    function subApproval(data){
        layer.confirm(systemLanguage["com.skyeye.approvalOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.approvalOperation"][languageType]}, function (index) {
            layer.close(index);
            activitiUtil.startProcess(sysActivitiModel["assetArticlesPurchase"]["key"], function (approvalId) {
                var params = {
                    id: data.id,
                    approvalId: approvalId
                };
                AjaxPostUtil.request({url: flowableBasePath + "editBossPersonRequireToSubApproval", params: params, type: 'json', method: "POST", callback: function (json) {
                    if (json.returnCode == 0) {
                        winui.window.msg("提交成功", {icon: 1, time: 2000});
                        loadTable();
                    } else {
                        winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
                    }
                }});
            });
        });
    }

    // 作废
    function cancellation(data){
        layer.confirm('确认作废该申请吗？', { icon: 3, title: '作废操作' }, function (index) {
            layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "updateBossPersonRequireToCancellation", params: {id: data.id}, type: 'json', method: "PUT", callback: function (json) {
                if (json.returnCode == 0) {
                    winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                    loadTable();
                } else {
                    winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
                }
            }});
        });
    }

    // 详情
    function details(data){
        rowId = data.id;
        _openNewWindows({
            url: "../../tpl/bossPersonRequire/bossPersonRequireDetails.html",
            title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
            pageId: "bossPersonRequireDetails",
            area: ['90vw', '90vh'],
            callBack: function(refreshCode){
            }
        });
    }

    form.render();
    form.on('submit(formSearch)', function (data) {
        if (winui.verifyForm(data.elem)) {
            table.reload("messageTable", {page: {curr: 1}, where: getTableParams()});
        }
        return false;
    });

    // 刷新
    $("body").on("click", "#reloadTable", function() {
        loadTable();
    });

    function loadTable(){
        table.reload("messageTable", {where: getTableParams()});
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

    exports('bossPersonRequireList', {});
});
