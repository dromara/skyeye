
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
    var serviceClassName = sysServiceMation["assetPurchaseReturn"]["key"];
    authBtn('1720321634977');//新增

    table.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        url: sysMainMation.admBasePath + 'queryAssetPurchaseReturnList',
        where: getTableParams(),
        even: true,
        page: true,
        limits: getLimits(),
        limit: getLimit(),
        cols: [[
            { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' ,rowspan: '2' },
            { field: 'oddNumber', title: '单号', rowspan: '2', width: 200, align: 'center', templet: function (d) {
                var str='<a lay-event="details" class="notice-title-click">' + d.oddNumber + '</a>';
                if (!isNull(d.fromId)) {
                    str += '<span class="state-new">[转]</span>';
                }
                return str;
            }},
            { field: 'title', title: '标题', width: 300 ,rowspan: '2'},
            { field: 'processInstanceId', title: '流程ID', rowspan: '2',width: 100, templet: function (d) {
                return '<a lay-event="processDetails" class="notice-title-click">' + getNotUndefinedVal(d.processInstanceId) + '</a>';
            }},
            { field: 'state', title: '状态', rowspan: '2',width: 90, templet: function (d) {
                return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("flowableStateEnum", 'id', d.state, 'name');
            }},
            { colspan: '2', title: '来源单据信息', align: 'center' },
            { field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], rowspan: '2',width: 120 },
            { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], rowspan: '2',align: 'center', width: 150 },
            { field: 'lastUpdateName', title: systemLanguage["com.skyeye.lastUpdateName"][languageType],rowspan: '2', align: 'left', width: 120 },
            { field: 'lastUpdateTime', title: systemLanguage["com.skyeye.lastUpdateTime"][languageType], rowspan: '2',align: 'center', width: 150 },
            { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', rowspan: '2',width: 200,toolbar: '#messageTableBar'}
        ],
            [
            { field: 'fromTypeId', title: '来源类型', width: 150, templet: function (d) {
                return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("assetPurchaseReturnFromType", 'id', d.fromTypeId, 'name');
            }},
            { field: 'fromId', title: '单据编号', width: 200, templet: function (d) {
                return getNotUndefinedVal(d.fromMation?.oddNumber);
            }}
        ]
        ],
        done: function(json) {
            matchingLanguage();
            initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "请输入单号", function () {
                table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
            });
        }
    });

    // 资产采购退货的操作事件
    table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'details') { //退货详情
            details(data);
        } else if (layEvent === 'edit') { //编辑退货申请
            edit(data);
        } else if (layEvent === 'subApproval') { //退货提交审批
            subApproval(data);
        } else if (layEvent === 'delete') { //退货删除
            delet(data);
        } else if (layEvent === 'processDetails') { //退货流程详情
            activitiUtil.activitiDetails(data);
        } else if (layEvent === 'revoke') { //撤销退货申请
            revoke(data);
        }
    });

    // 添加
    $("body").on("click", "#addBean", function() {
        _openNewWindows({
            url: systemCommonUtil.getUrl('FP2024070700001', null),
            title: systemLanguage["com.skyeye.addPageTitle"][languageType],
            pageId: "assetProcurementReturnAdd",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
    });

    // 编辑
    function edit(data) {
        _openNewWindows({
            url: systemCommonUtil.getUrl('FP2024070700002&id=' + data.id, null),
            title: systemLanguage["com.skyeye.editPageTitle"][languageType],
            pageId: "assetProcurementReturnEdit",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
    }

    // 详情
    function details(data) {
        _openNewWindows({
            url: systemCommonUtil.getUrl('FP2024070700003&id=' + data.id, null),
            title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
            pageId: "assetProcurementReturnDetails",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
            }});
    }


    // 撤销退货申请
    function revoke(data) {
        var msg = '确认撤销该资产采购退货申请吗？';
        layer.confirm(msg, { icon: 3, title: '撤销操作' }, function (index) {
            layer.close(index);
            AjaxPostUtil.request({url: sysMainMation.admBasePath + "revokeAssetPurchaseReturn", params: {processInstanceId: data.processInstanceId}, type: 'json', method: "PUT", callback: function (json) {
                winui.window.msg("提交成功", {icon: 1, time: 2000});
                loadTable();
            }});
        });
    }

    // 删除
    function delet(data) {
        layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function (index) {
            layer.close(index);
            AjaxPostUtil.request({url: sysMainMation.admBasePath + "deleteAssetPurchaseReturnById", params: {id: data.id}, type: 'json', method: "DELETE", callback: function (json) {
                winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
        });
    }


    // 资产采购退货提交审批
    function subApproval(data) {
        layer.confirm(systemLanguage["com.skyeye.approvalOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.approvalOperation"][languageType]}, function (index) {
            layer.close(index);
            activitiUtil.startProcess(serviceClassName, null, function (approvalId) {
                var params = {
                    id: data.id,
                    approvalId: approvalId
                };
                AjaxPostUtil.request({url: sysMainMation.admBasePath + "submitAssetPurchaseReturn", params: params, type: 'json', callback: function (json) {
                    winui.window.msg("提交成功", {icon: 1, time: 2000});
                    loadTable();
                }});
            });
        });
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

    exports('assetProcurementReturnList', {});
});
