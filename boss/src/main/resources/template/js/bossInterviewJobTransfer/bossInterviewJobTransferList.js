
// 岗位调动申请申请
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
    var serviceClassName = sysServiceMation["bossInterviewJobTransfer"]["key"];

    // 新增
    authBtn('1651308552871');

    table.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        url: sysMainMation.bossBasePath + 'queryBossInterviewJobTransferList',
        where: getTableParams(),
        even: true,
        page: true,
        limits: getLimits(),
        limit: getLimit(),
        cols: [[
            { title: systemLanguage["com.skyeye.serialNumber"][languageType], rowspan: '2', type: 'numbers' },
            { field: 'oddNumber', title: '单号', width: 200, align: 'center', rowspan: '2', templet: function (d) {
                return '<a lay-event="details" class="notice-title-click">' + d.oddNumber + '</a>';
            }},
            { field: 'transferStaffMation', title: '申请人', rowspan: '2', width: 140, templet: function(d) {
                return d.transferStaffMation?.name;
            }},
            { title: '原岗位信息', align: 'center', colspan: '4'},
            { title: '申请岗位信息', align: 'center', colspan: '4'},
            { field: 'transferType', title: '调岗类型', rowspan: '2', width: 90, templet: function(d) {
                return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("bossUserTransferType", 'id', d.transferType, 'name');
            }},
            { field: 'processInstanceId', title: '流程ID', rowspan: '2', width: 100, templet: function (d) {
                return '<a lay-event="processDetails" class="notice-title-click">' + d.processInstanceId + '</a>';
            }},
            { field: 'state', title: '状态', rowspan: '2', width: 90, templet: function(d) {
                return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("flowableStateEnum", 'id', d.state, 'name');
            }},
            { field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], rowspan: '2', width: 140 },
            { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], rowspan: '2', align: 'center', width: 150 },
            { field: 'lastUpdateName', title: systemLanguage["com.skyeye.lastUpdateName"][languageType], rowspan: '2', align: 'left', width: 140 },
            { field: 'lastUpdateTime', title: systemLanguage["com.skyeye.lastUpdateTime"][languageType], rowspan: '2', align: 'center', width: 150 },
            { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', rowspan: '2', align: 'center', width: 257, toolbar: '#messageTableBar' }
        ],
            [
                { field: 'primaryCompanyName', title: '企业', align: 'left', width: 150, templet: function(d) {
                    return d.primaryCompanyMation?.name;
                }},
                { field: 'primaryDepartmentName', title: '部门', align: 'left', width: 150, templet: function(d) {
                    return d.primaryDepartmentMation?.name;
                }},
                { field: 'primaryJobName', title: '岗位', align: 'left', width: 150, templet: function(d) {
                    return d.primaryJobMation?.name;
                }},
                { field: 'primaryJobScoreName', title: '岗位定级', align: 'left', width: 150, templet: function(d) {
                    return d.primaryJobScoreMation?.name;
                }},
                { field: 'currentCompanyName', title: '企业', align: 'left', width: 150, templet: function(d) {
                    return d.currentCompanyMation?.name;
                }},
                { field: 'currentDepartmentName', title: '部门', align: 'left', width: 150, templet: function(d) {
                    return d.currentDepartmentMation?.name;
                }},
                { field: 'currentJobName', title: '岗位', align: 'left', width: 150, templet: function(d) {
                    return d.currentJobMation?.name;
                }},
                { field: 'currentJobScoreName', title: '岗位定级', align: 'left', width: 150, templet: function(d) {
                    return d.currentJobScoreMation?.name;
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
            url: systemCommonUtil.getUrl('FP2023061400001', null),
            title: systemLanguage["com.skyeye.addPageTitle"][languageType],
            pageId: "bossInterviewJobTransferAdd",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
    });

    // 撤销
    function revoke(data) {
        layer.confirm('确认撤销该申请吗？', { icon: 3, title: '撤销操作' }, function (index) {
            layer.close(index);
            AjaxPostUtil.request({url: sysMainMation.bossBasePath + "revokeJobTransfer", params: {processInstanceId: data.processInstanceId}, type: 'json', method: "PUT", callback: function (json) {
                winui.window.msg("提交成功", {icon: 1, time: 2000});
                loadTable();
            }});
        });
    }

    // 编辑申请
    function edit(data) {
        _openNewWindows({
            url: systemCommonUtil.getUrl('FP2023061400002&id=' + data.id, null),
            title: systemLanguage["com.skyeye.editPageTitle"][languageType],
            pageId: "bossInterviewJobTransferEdit",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }
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
                AjaxPostUtil.request({url: sysMainMation.bossBasePath + "submitJobTransfer", params: params, type: 'json', method: "POST", callback: function (json) {
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
            AjaxPostUtil.request({url: sysMainMation.bossBasePath + "invalidJobTransfer", params: {id: data.id}, type: 'json', method: "POST", callback: function (json) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
        });
    }

    // 详情
    function details(data) {
        _openNewWindows({
            url: systemCommonUtil.getUrl('FP2023061400003&id=' + data.id, null),
            title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
            pageId: "bossInterviewJobTransferDetails",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
            }
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

    exports('bossInterviewJobTransferList', {});
});
