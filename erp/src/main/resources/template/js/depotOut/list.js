
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
    var serviceClassName = sysServiceMation["depotOutOrder"]["key"];
    authBtn('1719830958582');//新增

    table.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        url: sysMainMation.erpBasePath + 'queryDepotOutOrderList',
        where: getTableParams(),
        even: true,
        page: true,
        limits: getLimits(),
        limit: getLimit(),
        cols: [[
            { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
            { field: 'oddNumber', title: '单据单号', align: 'left', width: 220, templet: function (d) {
                var str = '<a lay-event="details" class="notice-title-click">' + d.oddNumber + '</a>';
                if (!isNull(d.fromId)) {
                    str += '<span class="state-new">[转]</span>';
                }
                return str;
            }},
            { field: 'fromTypeId', title: '来源单据类型', width: 120, templet: function (d) {
                return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("depotOutFromType", 'id', d.fromTypeId, 'name');
            }},
            { field: 'createName', title: '申请人', width: 120 },
            { field: 'createTime', title: '申请时间', align: 'center', width: 150 },
            { field: 'totalPrice', title: '合计金额', align: 'left', width: 120 },
            { field: 'operTime', title: '单据日期', align: 'center', width: 140 },
            { field: 'state', title: '状态', rowspan: '2', width: 90, templet: function (d) {
                    return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("erpOrderStateEnum", 'id', d.state, 'name');
                }},
            { field: 'otherState', title: '出库状态', width: 90, templet: function (d) {
                return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("depotOutState", 'id', d.otherState, 'name');
            }},
            { title: systemLanguage["com.skyeye.operation"][languageType], rowspan: '2', fixed: 'right', align: 'center', width: 200, toolbar: '#tableBar'}
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
            erpOrderUtil.deleteOrderMation(data.id, serviceClassName, function() {
                loadTable();
            });
        } else if (layEvent === 'details') { //详情
            details(data);
        } else if (layEvent === 'edit') { //编辑
            edit(data);
        } else if (layEvent === 'subApproval') { //提交
            erpOrderUtil.submitOrderMation(data.id, serviceClassName, function() {
                loadTable();
            });
        } else if (layEvent === 'processDetails') { // 工作流流程详情查看
            activitiUtil.activitiDetails(data);
        } else if (layEvent === 'revoke') { //撤销
            erpOrderUtil.revokeOrderMation(data.processInstanceId, serviceClassName, function() {
                loadTable();
            });
        }
    });

    // 添加
    $("body").on("click", "#addBean", function() {
        _openNewWindows({
            url:  systemCommonUtil.getUrl('FP2024070100005', null),
            title: systemLanguage["com.skyeye.addPageTitle"][languageType],
            pageId: "purchasePutAdd",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
    });

    // 编辑
    function edit(data) {
        _openNewWindows({
            url:  systemCommonUtil.getUrl('FP2024070100006&id=' + data.id, null),
            title: systemLanguage["com.skyeye.editPageTitle"][languageType],
            pageId: "purchasePutEdit",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
    }

    // 详情
    function details(data) {
        _openNewWindows({
            url:  systemCommonUtil.getUrl('FP2024070100007&id=' + data.id, null),
            title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
            pageId: "purchasePutDetails",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
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

    exports('depotOutList', {});
});
