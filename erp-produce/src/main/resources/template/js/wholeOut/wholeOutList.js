
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

    // 新增
    authBtn('1720835673573');

    table.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        url: sysMainMation.erpBasePath + 'queryWholeOrderOutList',
        where: getTableParams(),
        even: true,
        page: true,
        limits: getLimits(),
        limit: getLimit(),
        cols: [[
            { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
            { field: 'oddNumber', title: '生产单号', align: 'center', width: 200, templet: function (d) {
                    return '<a lay-event="details" class="notice-title-click">' + d.oddNumber + '</a>';
                }},
            { field: 'salesOrderNum', width: 200, title: '关联销售单', align: 'center', templet: function (d) {return isNull(d.sealOrderMation) ? '' : d.sealOrderMation.oddNumber}},
            { field: 'name', title: '产品名称', align: 'left',width: 150, templet: function (d) {return isNull(d.materialMation) ? '' : d.materialMation.name}},
            { field: 'model', title: '产品型号', align: 'left',width: 150, templet: function (d) {return isNull(d.materialMation) ? '' : d.materialMation.model}},
            { field: 'norms', title: '产品规格', align: 'left',width: 150, templet: function (d) {return isNull(d.normsMation) ? '' : d.normsMation.name}},
            { field: 'number', width: 100, title: '计划生产数量'},
            { field: 'processInstanceId', title: '流程ID', width: 100, templet: function (d) {
                    return '<a lay-event="processDetails" class="notice-title-click">' + getNotUndefinedVal(d.processInstanceId) + '</a>';
                }},
            { field: 'state', title: '状态', width: 90, templet: function (d) {
                    return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("flowableStateEnum", 'id', d.state, 'name');
                }},
            { field: 'planStartDate', width: 150, align: 'center', title: '计划开始时间'},
            { field: 'planComplateDate', width: 150, align: 'center', title: '计划结束时间'},
            { field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], width: 120 },
            { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 },
            { field: 'lastUpdateName', title: systemLanguage["com.skyeye.lastUpdateName"][languageType], align: 'left', width: 120 },
            { field: 'lastUpdateTime', title: systemLanguage["com.skyeye.lastUpdateTime"][languageType], align: 'center', width: 150 },
            { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 240, toolbar: '#tableBar'}
        ]],
        done: function(json) {
            matchingLanguage();
            initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "请输入生产单号", function () {
                table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
            });
        }
    });

    table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'delete') { //删除
            del(data, obj);
        } else if (layEvent === 'edit') { //编辑
            edit(data);
        } else if (layEvent === 'details') { //详情
            details(data);
        } else if (layEvent === 'subApproval') { //提交审核
            subApproval(data);
        } else if (layEvent === 'processDetails') { // 工作流流程详情查看
            activitiUtil.activitiDetails(data);
        } else if (layEvent === 'revoke') { //撤销
            revoke(data);
        }
    });

    // 新增
    $("body").on("click", "#addBean", function() {
        _openNewWindows({
            url:  systemCommonUtil.getUrl('FP2024071300002', null),
            title: systemLanguage["com.skyeye.addPageTitle"][languageType],
            pageId: "erpProductionAdd",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
    });

    // 编辑
    function edit(data) {
        _openNewWindows({
            url:  systemCommonUtil.getUrl('FP2024071300003&id=' + data.id, null),
            title: systemLanguage["com.skyeye.editPageTitle"][languageType],
            pageId: "erpProductionEdit",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
    }

    // 详情
    function details(data) {
        _openNewWindows({
            url:  systemCommonUtil.getUrl('FP2024071300004&id=' + data.id, null),
            title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
            pageId: "erpProductionDetail",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
            }});
    }

    // // 删除
    // function del(data, obj) {
    //     layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function (index) {
    //         layer.close(index);
    //         AjaxPostUtil.request({url: sysMainMation.erpBasePath + "deleteProductionById", params: {id: data.id}, type: 'json', method: 'DELETE', callback: function (json) {
    //                 winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
    //                 loadTable();
    //             }});
    //     });
    // }

    // // 提交审批
    // function subApproval(data) {
    //     layer.confirm(systemLanguage["com.skyeye.approvalOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.approvalOperation"][languageType]}, function (index) {
    //         layer.close(index);
    //         activitiUtil.startProcess(data.serviceClassName, null, function (approvalId) {
    //             var params = {
    //                 id: data.id,
    //                 approvalId: approvalId
    //             };
    //             AjaxPostUtil.request({url: sysMainMation.erpBasePath + "erpproduction007", params: params, type: 'json', method: 'POST', callback: function (json) {
    //                     winui.window.msg("提交成功", {icon: 1, time: 2000});
    //                     loadTable();
    //                 }});
    //         });
    //     });
    // }

    // // 撤销
    // function revoke(data) {
    //     layer.confirm('确认撤销该申请吗？', { icon: 3, title: '撤销操作' }, function (index) {
    //         layer.close(index);
    //         AjaxPostUtil.request({url: sysMainMation.erpBasePath + "revokeProduction", params: {processInstanceId: data.processInstanceId}, type: 'json', method: "PUT", callback: function (json) {
    //                 winui.window.msg("提交成功", {icon: 1, time: 2000});
    //                 loadTable();
    //             }});
    //     });
    // }

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

    exports('wholeOutList', {});
});
