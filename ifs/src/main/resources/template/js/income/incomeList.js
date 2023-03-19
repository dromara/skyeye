
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
    var serviceClassName = sysServiceMation["incomeOrder"]["key"];
    authBtn('1571638020191');//新增

    var orderType = "";
    sysDictDataUtil.showDictDataListByDictTypeCode(sysDictData["ifsOrderType"]["key"], 'selectTree', "ifsOrderType", '', form, function () {
        initTable();
    }, function (chooseId) {
        orderType = chooseId;
        refreshTable();
    });

    function initTable() {
        table.render({
            id: 'messageTable',
            elem: '#messageTable',
            method: 'post',
            url: sysMainMation.ifsBasePath + 'income001',
            where: getTableParams(),
            even: true,
            page: true,
            limits: getLimits(),
            limit: getLimit(),
            cols: [[
                { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
                { field: 'oddNumber', title: '单据编号', align: 'left', width: 200, templet: function (d) {
                    return '<a lay-event="details" class="notice-title-click">' + d.oddNumber + '</a>';
                }},
                { field: 'processInstanceId', title: '流程ID', width: 100, templet: function (d) {
                    return '<a lay-event="processDetails" class="notice-title-click">' + d.processInstanceId + '</a>';
                }},
                { field: 'state', title: '状态', align: 'left', width: 80, templet: function (d) {
                    return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("flowableStateEnum", 'id', d.state, 'name');
                }},
                { field: 'type', title: '单据类型', align: 'left', width: 80, templet: function (d) {
                    return sysDictDataUtil.getDictDataNameByCodeAndKey("IFS_ORDER_TYPE", d.type);
                }},
                { field: 'holderKey', title: '往来单位类型', align: 'left', width: 100, templet: function (d) {
                    return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("correspondentEnterEnum", 'id', d.holderKey, 'name');
                }},
                { field: 'supplierName', title: '往来单位', align: 'left', width: 150 },
                { field: 'totalPrice', title: '合计金额', align: 'left', width: 120 },
                { field: 'handsPersonName', title: '经手人', align: 'left', width: 120 },
                { field: 'operTime', title: '单据日期', align: 'center', width: 120 },
                { field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], width: 120 },
                { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 },
                { field: 'lastUpdateName', title: systemLanguage["com.skyeye.lastUpdateName"][languageType], align: 'left', width: 120 },
                { field: 'lastUpdateTime', title: systemLanguage["com.skyeye.lastUpdateTime"][languageType], align: 'center', width: 150 },
                { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 200, toolbar: '#tableBar' }
            ]],
            done: function(json) {
                matchingLanguage();
                initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "请输入单据编号", function () {
                    refreshTable();
                });
            }
        });

        table.on('tool(messageTable)', function (obj) {
            var data = obj.data;
            var layEvent = obj.event;
            if (layEvent === 'delete') { //删除
                deleteIncome(data);
            } else if (layEvent === 'details') { //详情
                details(data);
            } else if (layEvent === 'edit') { //编辑
                edit(data);
            } else if (layEvent === 'subExamine') { //提交审核
                subExamine(data);
            } else if (layEvent === 'processDetails') {//采购流程详情
                activitiUtil.activitiDetails(data);
            } else if (layEvent === 'revoke') { //撤销
                revorke(data);
            }
        });
    }

    // 添加
    $("body").on("click", "#addBean", function() {
        _openNewWindows({
            url: systemCommonUtil.getUrl('FP2023031800001', null),
            title: systemLanguage["com.skyeye.addPageTitle"][languageType],
            pageId: "incomeAdd",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
    });

    // 编辑
    function edit(data) {
        _openNewWindows({
            url: systemCommonUtil.getUrl('FP2023031800002&id=' + data.id, null),
            title: systemLanguage["com.skyeye.editPageTitle"][languageType],
            pageId: "incomeEdit",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
    }

    // 详情
    function details(data) {
        rowId = data.id;
        _openNewWindows({
            url: "../../tpl/income/incomeInfo.html",
            title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
            pageId: "incomeInfo",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
            }});
    }

    // 删除
    function deleteIncome(data) {
        layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function (index) {
            AjaxPostUtil.request({url: sysMainMation.ifsBasePath + "income005", params: {rowId: data.id}, type: 'json', method: "DELETE", callback: function (json) {
                winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
        });
    }

    // 撤销申请
    function revorke(data) {
        layer.confirm('确认撤销该申请吗？', { icon: 3, title: '撤销操作' }, function (index) {
            layer.close(index);
            AjaxPostUtil.request({url: sysMainMation.ifsBasePath + "income009", params: {processInstanceId: data.processInstanceId}, type: 'json', method: "PUT", callback: function (json) {
                winui.window.msg("提交成功", {icon: 1, time: 2000});
                loadTable();
            }});
        });
    }

    // 提交数据
    function subExamine(data){
        layer.confirm(systemLanguage["com.skyeye.approvalOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.approvalOperation"][languageType]}, function (index) {
            layer.close(index);
            activitiUtil.startProcess(serviceClassName, null, function (approvalId) {
                var params = {
                    id: data.id,
                    approvalId: approvalId
                };
                AjaxPostUtil.request({url: sysMainMation.ifsBasePath + "income008", params: params, type: 'json', callback: function (json) {
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

    function refreshTable() {
        table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
    }

    function getTableParams() {
        return $.extend(true, {orderType: orderType}, initTableSearchUtil.getSearchValue("messageTable"));
    }

    exports('incomeList', {});
});
