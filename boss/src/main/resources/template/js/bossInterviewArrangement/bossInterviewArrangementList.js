var rowId = "";

// 面试安排
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
    authBtn('1649929352279');

    laydate.render({elem: '#createTime', range: '~'});

    table.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        url: flowableBasePath + 'queryMyEntryBossInterviewArrangementList',
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
                return bossUtil.showStateName(d.state);
            }},
            { field: 'createTime', title: systemLanguage["com.skyeye.entryTime"][languageType], align: 'center', width: 150},
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
        }else if (layEvent === 'sub') { // 提交
            sub(data);
        }else if(layEvent === 'cancellation') { // 作废
            cancellation(data);
        }else if(layEvent === 'inductionResult') { // 入职
            inductionResult(data);
        }
    });

    // 添加
    $("body").on("click", "#addBean", function() {
        _openNewWindows({
            url: "../../tpl/bossInterviewArrangement/bossInterviewArrangementAdd.html",
            title: systemLanguage["com.skyeye.addPageTitle"][languageType],
            pageId: "bossInterviewArrangementAdd",
            area: ['90vw', '90vh'],
            callBack: function(refreshCode){
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
    });

    // 编辑
    function edit(data){
        rowId = data.id;
        _openNewWindows({
            url: "../../tpl/bossInterviewArrangement/bossInterviewArrangementEdit.html",
            title: systemLanguage["com.skyeye.editPageTitle"][languageType],
            pageId: "bossInterviewArrangementEdit",
            area: ['90vw', '90vh'],
            callBack: function(refreshCode){
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }
        });
    }

    // 入职
    function inductionResult(data){
        rowId = data.id;
        _openNewWindows({
            url: "../../tpl/bossInterviewArrangement/inductionResult.html",
            title: '入职',
            pageId: "inductionResult",
            area: ['90vw', '90vh'],
            callBack: function(refreshCode){
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }
        });
    }

    // 提交
    function sub(data){
        layer.confirm('确认提交该数据吗？', {icon: 3, title: '提交操作'}, function (index) {
            layer.close(index);
            var params = {
                id: data.id,
            };
            AjaxPostUtil.request({url: flowableBasePath + "submitBossInterviewArrangement", params: params, type: 'json', method: "PUT", callback: function (json) {
                winui.window.msg("提交成功", {icon: 1, time: 2000});
                loadTable();
            }});
        });
    }

    // 作废
    function cancellation(data){
        layer.confirm('确认作废该申请吗？', { icon: 3, title: '作废操作' }, function (index) {
            layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "nullifyBossInterviewArrangement", params: {id: data.id}, type: 'json', method: "PUT", callback: function (json) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
        });
    }

    // 详情
    function details(data){
        rowId = data.id;
        _openNewWindows({
            url: "../../tpl/bossInterviewArrangement/bossInterviewArrangementDetails.html",
            title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
            pageId: "bossInterviewArrangementDetails",
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

    exports('bossInterviewArrangementList', {});
});
