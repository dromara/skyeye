
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
    var serviceClassName = sysServiceMation["purchaseRequest"]["key"];
    authBtn('1716519960380');//新增

    table.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        url: sysMainMation.erpBasePath + 'queryPurchaseRequestList',
        where: getTableParams(),
        even: true,
        page: true,
        limits: getLimits(),
        limit: getLimit(),
        cols: [[
            { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
            { field: 'oddNumber', title: '单号', width: 200, align: 'center', templet: function (d) {
                return '<a lay-event="details" class="notice-title-click">' + d.oddNumber + '</a>';
            }},
            { field: 'processInstanceId', title: '流程ID', width: 100, templet: function (d) {
                return '<a lay-event="processDetails" class="notice-title-click">' + getNotUndefinedVal(d.processInstanceId) + '</a>';
            }},
            { field: 'title', title: '单据主题', align: 'left', width: 120 },
            { field: 'fromTypeId', title: '单据来源', width: 90, templet: function (d) {
                return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("purchaseRequestFromType", 'id', d.fromTypeId, 'name');
            }},
            { field: 'projectMation', title: '项目', align: 'left', width: 150, templet: function (d) {
                return getNotUndefinedVal(d.projectMation?.name);
            }},
            { field: 'totalPrice', title: '合计金额', align: 'left', width: 120 },
            { field: 'operTime', title: '单据日期', align: 'center', width: 140 },
            { field: 'inquiryState', title: '询价状态', width: 90, templet: function (d) {
                return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("purchaseRequestInquiryState", 'id', d.inquiryState, 'name');
            }},
            { field: 'state', title: '状态', width: 120, templet: function (d) {
                return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("purchaseRequestStateEnum", 'id', d.state, 'name');
            }},
            { field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], width: 120 },
            { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 },
            { field: 'lastUpdateName', title: systemLanguage["com.skyeye.lastUpdateName"][languageType], align: 'left', width: 120 },
            { field: 'lastUpdateTime', title: systemLanguage["com.skyeye.lastUpdateTime"][languageType], align: 'center', width: 150 },
            { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 200, toolbar: '#tableBar'}
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
        } else if (layEvent === 'subApproval') { //提交审核
            subApproval(data);
        }  else if (layEvent === 'processDetails') { // 工作流流程详情查看
            activitiUtil.activitiDetails(data);
        } else if (layEvent === 'revoke') { //撤销
            revoke(data);
        } else if (layEvent === 'inquiry') { //询价
            inquiry(data);
        } else if (layEvent === 'fixedPrice') { //定价
            fixedPrice(data);
        } else if (layEvent === 'toContract') { //转采购合同
            toContract(data);
        }
    });

    // 添加
    $("body").on("click", "#addBean", function() {
        _openNewWindows({
            url: systemCommonUtil.getUrl('FP2024052400001', null),
            title: systemLanguage["com.skyeye.addPageTitle"][languageType],
            pageId: "purchaseRequestAdd",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
    });

    // 删除
    function delet(data) {
        layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function (index) {
            layer.close(index);
            AjaxPostUtil.request({url: sysMainMation.erpBasePath + "deletePurchaseRequest", params: {id: data.id}, type: 'json', method: "DELETE", callback: function (json) {
                winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
        });
    }

    // 编辑
    function edit(data) {
        _openNewWindows({
            url: systemCommonUtil.getUrl('FP2024052400002&id=' + data.id, null),
            title: systemLanguage["com.skyeye.editPageTitle"][languageType],
            pageId: "purchaseRequestEdit",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
    }

    // 转采购合同
    function toContract(data) {
        _openNewWindows({
            url: '../../tpl/purchaseRequest/requestToContract.html?id=' + data.id,
            title: '采购合同',
            pageId: "requestToContract",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
    }

    // 询价
    function inquiry(data) {
        _openNewWindows({
            url: systemCommonUtil.getUrl('FP2024052400005&id=' + data.id, null),
            title: systemLanguage["com.skyeye.inquiryPageTitle"][languageType],
            pageId: "purchaseRequestInquiry",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
    }

    //定价
    function fixedPrice(data){
        _openNewWindows({
            url: systemCommonUtil.getUrl('FP2024052500001&id=' + data.id, null),
            title: systemLanguage["com.skyeye.fixedPricePageTitle"][languageType],
            pageId: "purchaseRequestFixedPrice",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
    }


    // 详情
    function details(data) {
        _openNewWindows({
            url: systemCommonUtil.getUrl('FP2024052400003&id=' + data.id, null),
            title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
            pageId: "purchaseRequestDetails",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
            }});
    }

    // 撤销
    function revoke(data) {
        layer.confirm('确认撤销该申请吗？', { icon: 3, title: '撤销操作' }, function (index) {
            layer.close(index);
            AjaxPostUtil.request({url: sysMainMation.erpBasePath + "revokePurchaseRequest", params: {processInstanceId: data.processInstanceId}, type: 'json', method: "PUT", callback: function (json) {
                winui.window.msg("提交成功", {icon: 1, time: 2000});
                loadTable();
            }});
        });
    }

    // 提交审批
    function subApproval(data) {
        layer.confirm(systemLanguage["com.skyeye.approvalOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.approvalOperation"][languageType]}, function (index) {
            layer.close(index);
            activitiUtil.startProcess(serviceClassName, null, function (approvalId) {
                var params = {
                    id: data.id,
                    approvalId: approvalId
                };
                AjaxPostUtil.request({url: sysMainMation.erpBasePath + "submitPurchaseRequestToApproval", params: params, type: 'json', method: "POST", callback: function (json) {
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

    exports('purchaseRequestList', {});
});
