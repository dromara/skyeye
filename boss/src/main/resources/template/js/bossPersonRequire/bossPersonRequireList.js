
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
    var serviceClassName = sysServiceMation["bossPersonRequire"]["key"];

    // 新增
    authBtn('1649569413156');

    table.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        url: sysMainMation.bossBasePath + 'queryBossPersonRequireList',
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
                return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("bossPersonRequireState", 'id', d.state, 'name');
            }},
            { field: 'wages', title: '薪资范围', width: 120 },
            { field: 'recruitNum', title: '需求人数', width: 100 },
            { field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], width: 120 },
            { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 },
            { field: 'lastUpdateName', title: systemLanguage["com.skyeye.lastUpdateName"][languageType], align: 'left', width: 120 },
            { field: 'lastUpdateTime', title: systemLanguage["com.skyeye.lastUpdateTime"][languageType], align: 'center', width: 150 },
            { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 257, toolbar: '#messageTableBar'}
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
        if (layEvent === 'details') { // 详情
            details(data);
        } else if (layEvent === 'edit') { // 编辑
            edit(data);
        } else if (layEvent === 'subApproval') { // 提交审批
            subApproval(data);
        } else if (layEvent === 'cancellation') { // 作废
            cancellation(data);
        } else if (layEvent === 'processDetails') { // 流程详情
            activitiUtil.activitiDetails(data);
        } else if (layEvent === 'revoke') { // 撤销申请
            revoke(data);
        }
    });

    // 添加
    $("body").on("click", "#addBean", function() {
        _openNewWindows({
            url: systemCommonUtil.getUrl('', null),
            title: systemLanguage["com.skyeye.addPageTitle"][languageType],
            pageId: "bossPersonRequireAdd",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
    });

    // 编辑申请
    function edit(data) {
        _openNewWindows({
            url: systemCommonUtil.getUrl('&id=' + data.id, null),
            title: systemLanguage["com.skyeye.editPageTitle"][languageType],
            pageId: "bossPersonRequireEdit",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }
        });
    }

    // 详情
    function details(data) {
        _openNewWindows({
            url: systemCommonUtil.getUrl('&id=' + data.id, null),
            title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
            pageId: "bossPersonRequireDetails",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
            }
        });
    }

    // 撤销
    function revoke(data) {
        layer.confirm('确认撤销该申请吗？', { icon: 3, title: '撤销操作' }, function (index) {
            layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "revokeBossPersonRequire", params: {processInstanceId: data.processInstanceId}, type: 'json', method: "PUT", callback: function (json) {
                winui.window.msg("提交成功", {icon: 1, time: 2000});
                loadTable();
            }});
        });
    }

    // 提交审批
    function subApproval(data) {
        layer.confirm(systemLanguage["com.skyeye.approvalOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.approvalOperation"][languageType]}, function (index) {
            layer.close(index);
            activitiUtil.startProcess(serviceClassName, function (approvalId) {
                var params = {
                    id: data.id,
                    approvalId: approvalId
                };
                AjaxPostUtil.request({url: flowableBasePath + "editBossPersonRequireToSubApproval", params: params, type: 'json', method: "POST", callback: function (json) {
                    winui.window.msg("提交成功", {icon: 1, time: 2000});
                    loadTable();
                }});
            });
        });
    }

    // 作废
    function cancellation(data) {
        layer.confirm('确认作废该申请吗？', { icon: 3, title: '作废操作' }, function (index) {
            layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "updateBossPersonRequireToCancellation", params: {id: data.id}, type: 'json', method: "PUT", callback: function (json) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
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
        return $.extend(true, {}, initTableSearchUtil.getSearchValue("messageTable"));
    }

    exports('bossPersonRequireList', {});
});
