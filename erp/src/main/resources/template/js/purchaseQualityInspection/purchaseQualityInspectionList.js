
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
    var serviceClassName = sysServiceMation["purchaseQualityInspection"]["key"];
    authBtn('1718282987406');//新增

    table.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        url: sysMainMation.erpBasePath + 'queryQualityInspectionList',
        where: getTableParams(),
        even: true,
        page: true,
        limits: getLimits(),
        limit: getLimit(),
        cols: [[
            { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers', rowspan: '2' },
            { field: 'oddNumber', title: '单号', rowspan: '2', align: 'left', width: 220, templet: function (d) {
                    var str = '<a lay-event="details" class="notice-title-click">' + d.oddNumber + '</a>';
                    if (!isNull(d.fromId)) {
                        str += '<span class="state-new">[转]</span>';
                    }
                    return str;
                }},
            { field: 'operTime', title: '单据日期', rowspan: '2', align: 'center', width: 140 },
            { colspan: '2', title: '来源单据信息', align: 'center' },
            { field: 'processInstanceId', title: '流程ID', rowspan: '2', width: 100, templet: function (d) {
                    return '<a lay-event="processDetails" class="notice-title-click">' + getNotUndefinedVal(d.processInstanceId) + '</a>';
                }},
            { field: 'state', title: '状态', rowspan: '2', width: 90, templet: function (d) {
                    return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("flowableStateEnum", 'id', d.state, 'name');
                }},
            { field: 'returnState', title: '退货状态', rowspan: '2', width: 90, templet: function (d) {
                    return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("qualityInspectionReturnState", 'id', d.returnState, 'name');
                }},
            { field: 'putState', title: '入库状态', rowspan: '2', width: 150, templet: function (d) {
                    return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("qualityInspectionPutState", 'id', d.putState, 'name');
                }},
            { field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], rowspan: '2', width: 120 },
            { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], rowspan: '2', align: 'center', width: 150 },
            { field: 'lastUpdateName', title: systemLanguage["com.skyeye.lastUpdateName"][languageType], rowspan: '2', align: 'left', width: 120 },
            { field: 'lastUpdateTime', title: systemLanguage["com.skyeye.lastUpdateTime"][languageType], rowspan: '2', align: 'center', width: 150 },
            { title: systemLanguage["com.skyeye.operation"][languageType], rowspan: '2', fixed: 'right', align: 'center', width: 200, toolbar: '#tableBar'}
        ], [
            { field: 'fromTypeId', title: '来源类型', width: 150, templet: function (d) {
                    return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("qualityInspectionFromType", 'id', d.fromTypeId, 'name');
                }},
            { field: 'fromId', title: '单据编号', width: 200, templet: function (d) {
                    return getNotUndefinedVal(d.fromMation?.oddNumber);
                }}
        ]],
        done: function(json) {
            matchingLanguage();
            initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "请输入单号", function () {
                table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
            });
        }
    });

    table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'delete') { //删除
            delet(data);
        } else if (layEvent === 'details') { //详情
            details(data);
        } else if (layEvent === 'edit') { //编辑
            edit(data);
        }else if (layEvent === 'qualityInspectionToPut') { //转采购入库单
            qualityInspectionToPut(data);
        } else if (layEvent === 'qualityInspectionToReturns') { //转退货单
            qualityInspectionToReturns(data);
        } else if (layEvent === 'subApproval') { //提交审核
            subApproval(data);
        } else if (layEvent === 'processDetails') { // 工作流流程详情查看
            activitiUtil.activitiDetails(data);
        } else if (layEvent === 'revoke') { //撤销
            revoke(data);
        }
    });

    // 添加
    $("body").on("click", "#addBean", function() {
        _openNewWindows({
            url: systemCommonUtil.getUrl('FP2024061300001', null),
            title: systemLanguage["com.skyeye.addPageTitle"][languageType],
            pageId: "purchaseQualityInspectionAdd",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
    });

    // 编辑
    function edit(data) {
        _openNewWindows({
            url: systemCommonUtil.getUrl('FP2024061300002&id=' + data.id, null),
            title: systemLanguage["com.skyeye.editPageTitle"][languageType],
            pageId: "purchaseQualityInspectionEdit",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
    }

    // 详情
    function details(data) {
        _openNewWindows({
            url: systemCommonUtil.getUrl('FP2024061300003&id=' + data.id, null),
            title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
            pageId: "purchaseQualityInspectionDetails",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
            }});
    }

    // 提交审批
    function subApproval(data) {
        layer.confirm(systemLanguage["com.skyeye.approvalOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.approvalOperation"][languageType]}, function (index) {
            layer.close(index);
            activitiUtil.startProcess(data.serviceClassName, null, function (approvalId) {
                var params = {
                    id: data.id,
                    approvalId: approvalId
                };
                AjaxPostUtil.request({url: sysMainMation.erpBasePath + "submitQualityInspectionToApproval", params: params, type: 'json', method: 'POST', callback: function (json) {
                        winui.window.msg("提交成功。", {icon: 1, time: 2000});
                        loadTable();
                    }});
            });
        });
    }

    // 删除
    function delet(data) {
        layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function (index) {
            layer.close(index);
            AjaxPostUtil.request({url: sysMainMation.erpBasePath + "deleteQualityInspection", params: {id: data.id}, type: 'json', method: "POST", callback: function (json) {
                    winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
                    loadTable();
                }});
        });
    }

    // 撤销
    function revoke(data) {
        layer.confirm('确认撤销该申请吗？', { icon: 3, title: '撤销操作' }, function (index) {
            layer.close(index);
            AjaxPostUtil.request({url: sysMainMation.erpBasePath + "revokeQualityInspection", params: {processInstanceId: data.processInstanceId}, type: 'json', method: "PUT", callback: function (json) {
                    winui.window.msg("撤销成功。", {icon: 1, time: 2000});
                    loadTable();
                }});
        });
    }

    // 转采购入库
    function qualityInspectionToPut(data) {
        _openNewWindows({
            url: "../../tpl/purchaseQualityInspection/qualityInspectionToPut.html?id=" + data.id,
            title: "转采购入库",
            pageId: "qualityInspectionToPut",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
    }
    //转退货单
    function qualityInspectionToReturns(data){
        parent._openNewWindows({
            url: "../../tpl/purchaseQualityInspection/qualityInspectionToReturns.html?id=" + data.id,
            title: '转退货单',
            pageId: "qualityInspectionToReturns",
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
        return $.extend(true, {}, initTableSearchUtil.getSearchValue("messageTable"));
    }

    exports('queryQualityInspectionList', {});
});
