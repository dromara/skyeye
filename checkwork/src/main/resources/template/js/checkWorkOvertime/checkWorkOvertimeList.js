var rowId = "";

var taskType = "";//流程详情的主标题
var processInstanceId = "";//流程id

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

    // 新增加班申请
    authBtn('1618046226263');

    // 申请时间
    laydate.render({
        elem: '#applyTime',
        range: '~'
    });

    // 我的加班申请列表
    table.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        url: flowableBasePath + 'checkworkovertime001',
        where: getTableParams(),
        even: true,
        page: true,
        limits: getLimits(),
        limit: getLimit(),
        cols: [[
            { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
            { field: 'title', title: '标题', width: 300, templet: function(d){
                    return '<a lay-event="dedails" class="notice-title-click">' + d.title + '</a>';
                }},
            { field: 'oddNum', title: '单号', width: 200 },
            { field: 'processInstanceId', title: '流程ID', width: 100, templet: function(d){
                    return '<a lay-event="processDetails" class="notice-title-click">' + d.processInstanceId + '</a>';
                }},
            { field: 'stateName', title: '状态', width: 90, templet: function(d){
                    if(d.state == '0'){
                        return "<span>" + d.stateName + "</span>";
                    }else if(d.state == '1'){
                        return "<span class='state-new'>" + d.stateName + "</span>";
                    }else if(d.state == '2'){
                        return "<span class='state-up'>" + d.stateName + "</span>";
                    }else if(d.state == '3'){
                        return "<span class='state-down'>" + d.stateName + "</span>";
                    }else if(d.state == '4'){
                        return "<span class='state-down'>" + d.stateName + "</span>";
                    }else if(d.state == '5'){
                        return "<span class='state-error'>" + d.stateName + "</span>";
                    }
                }},
            { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], width: 150},
            { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 257, toolbar: '#messageTableBar'}
        ]],
        done: function(){
            matchingLanguage();
        }
    });

    table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'dedails') { // 详情
            dedails(data);
        }else if (layEvent === 'edit') { // 编辑
            edit(data);
        }else if (layEvent === 'subApproval') { // 提交审批
            subApproval(data);
        }else if(layEvent === 'cancellation') {// 作废
            cancellation(data);
        }else if(layEvent === 'processDetails') {// 流程详情
            activitiUtil.activitiDetails(data);
        }else if(layEvent === 'revoke') {// 撤销申请
            revoke(data);
        }
    });

    // 新增加班申请
    $("body").on("click", "#addBean", function() {
        _openNewWindows({
            url: "../../tpl/checkWorkOvertime/checkWorkOvertimeAdd.html",
            title: "加班申请",
            pageId: "checkWorkOvertimeAdd",
            area: ['90vw', '90vh'],
            callBack: function(refreshCode){
                if (refreshCode == '0') {
                    winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                    loadTable();
                } else if (refreshCode == '-9999') {
                    winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2, time: 2000});
                }
            }});
    });

    // 撤销加班申请
    function revoke(data){
        layer.confirm('确认撤销该申请吗？', { icon: 3, title: '撤销操作' }, function (index) {
            layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "checkworkovertime009", params:{processInstanceId: data.processInstanceId}, type: 'json', method: "PUT", callback: function(json){
                if (json.returnCode == 0) {
                    winui.window.msg("提交成功", {icon: 1, time: 2000});
                    loadTable();
                } else {
                    winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
                }
            }});
        });
    }

    // 编辑加班申请
    function edit(data){
        rowId = data.id;
        _openNewWindows({
            url: "../../tpl/checkWorkOvertime/checkWorkOvertimeEdit.html",
            title: "加班申请",
            pageId: "checkWorkOvertimeEdit",
            area: ['90vw', '90vh'],
            callBack: function(refreshCode){
                if (refreshCode == '0') {
                    winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                    loadTable();
                } else if (refreshCode == '-9999') {
                    winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2, time: 2000});
                }
            }
        });
    }

    // 加班申请提交审批
    function subApproval(data){
        layer.confirm(systemLanguage["com.skyeye.approvalOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.approvalOperation"][languageType]}, function (index) {
            layer.close(index);
            activitiUtil.startProcess(sysActivitiModel["checkWorkOvertime"]["key"], function (approvalId) {
                var params = {
                    rowId: data.id,
                    approvalId: approvalId
                };
                AjaxPostUtil.request({url: flowableBasePath + "checkworkovertime006", params: params, type: 'json', callback: function(json){
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

    // 加班申请作废
    function cancellation(data){
        layer.confirm('确认作废该申请吗？', { icon: 3, title: '作废操作' }, function (index) {
            layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "checkworkovertime007", params:{rowId: data.id}, type: 'json', callback: function(json){
                if (json.returnCode == 0) {
                    winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                    loadTable();
                } else {
                    winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
                }
            }});
        });
    }

    // 加班申请详情
    function dedails(data){
        rowId = data.id;
        _openNewWindows({
            url: "../../tpl/checkWorkOvertime/checkWorkOvertimeDetails.html",
            title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
            pageId: "checkWorkOvertimeDetails",
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

    // 刷新数据
    function loadTable(){
        table.reload("messageTable", {where: getTableParams()});
    }

    function getTableParams(){
        var startTime = "", endTime = "";
        if(!isNull($("#applyTime").val())){
            startTime = $("#applyTime").val().split('~')[0].trim() + ' 00:00:00';
            endTime = $("#applyTime").val().split('~')[1].trim() + ' 23:59:59';
        }
        return {
            state: $("#state").val(),
            startTime: startTime,
            endTime: endTime
        };
    }

    exports('checkWorkOvertimeList', {});
});
