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
    authBtn('1720796297124');

    table.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        // 获取加工单列表
        url: sysMainMation.erpBasePath + 'erpmachin001',
        where: getTableParams(),
        even: true,
        page: true,
        limits: getLimits(),
        limit: getLimit(),
        cols: [[
            { title: systemLanguage["com.skyeye.serialNumber"][languageType], rowspan: '2', type: 'numbers' },
            { field: 'oddNumber', title: '单号', rowspan: '2', align: 'left', width: 220, templet: function (d) {
                    var str = '<a lay-event="details" class="notice-title-click">' + d.oddNumber + '</a>';
                    if (!isNull(d.fromId)) {
                        str += '<span class="state-new">[转]</span>';
                    }
                    return str;
                }},
            { colspan: '2', title: '来源单据信息', align: 'center' },
            { field: 'processInstanceId', title: '流程ID', rowspan: '2', width: 280, templet: function (d) {
                    return '<a lay-event="processDetails" class="notice-title-click">' + getNotUndefinedVal(d.processInstanceId) + '</a>';
                }},
            { field: 'state', title: '状态', rowspan: '2', width: 90, templet: function (d) {
                    return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("erpOrderStateEnum", 'id', d.state, 'name');
                }},
            { field: 'pickState', rowspan: '2', title: '领料状态', align: 'left', width: 80, templet: function (d) {
                    return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("machinPickStateEnum", 'id', d.pickState, 'name');
                }},
            { field: 'departmentMation', title: '加工部门', rowspan: '2', align: 'left', width: 200, templet: function (d) {
                    return getNotUndefinedVal(d.departmentMation?.name);
                }},
            { field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], rowspan: '2',width: 180 },
            { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', rowspan: '2',width: 150 },
            { field: 'lastUpdateName', title: systemLanguage["com.skyeye.lastUpdateName"][languageType], align: 'left', rowspan: '2',width: 180 },
            { field: 'lastUpdateTime', title: systemLanguage["com.skyeye.lastUpdateTime"][languageType], align: 'center', rowspan: '2',width: 150 },
            { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', rowspan: '2',width: 300, toolbar: '#tableBar'}
        ],[
            { field: 'fromTypeId', title: '来源类型', rowspan: '2',width: 150, templet: function (d) {
                    return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("machinFromType", 'id', d.fromTypeId, 'name');
                }},
            { field: 'fromId', title: '单据编号', rowspan: '2',width: 200, templet: function (d) {
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
        } else if (layEvent === 'gantt') { //甘特图
            gantt(data);
        }else if (layEvent === 'processingToMaterialRequisition') { //转领料单
            processingToMaterialRequisition(data);
        } else if (layEvent === 'processingToSupplementMaterials') { //转补料单
            processingToSupplementMaterials(data);
        } else if (layEvent === 'processingToReturnMaterials') { //转退料单
            processingToReturnMaterials(data);
        }
    });


    // 新增
    $("body").on("click", "#addBean", function() {
        _openNewWindows({
            url:  systemCommonUtil.getUrl('FP2023100300001', null),
            title: systemLanguage["com.skyeye.addPageTitle"][languageType],
            pageId: "machiningAdd",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
    });

    // 甘特图
    function gantt(data) {
        parent._openNewWindows({
            url: "../../tpl/departmentMachining/machiningGantt.html?id=" + data.id + "&state=" + data.state,
            title: "车间任务安排甘特图",
            pageId: "machiningGantt",
            area: ['100vw', '100vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
    }

    // 转领料单
    function processingToMaterialRequisition(data) {
        _openNewWindows({
            url: "../../tpl/departmentMachining/processingToMaterialRequisition.html?id=" + data.id,
            title: "转领料单",
            pageId: "processingToMaterialRequisition",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
    }

    //转补料单
    function processingToSupplementMaterials(data){
        _openNewWindows({
            url: "../../tpl/departmentMachining/processingToSupplementMaterials.html?id=" + data.id,
            title: '转补料单',
            pageId: "processingToSupplementMaterials",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
    }

    //转退料单
    function processingToReturnMaterials(data){
        _openNewWindows({
            url: "../../tpl/departmentMachining/processingToReturnMaterials.html?id=" + data.id,
            title: '转退料单',
            pageId: "processingToReturnMaterials",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
    }

    // 编辑
    function edit(data) {
        _openNewWindows({
            url:  systemCommonUtil.getUrl('FP2023100300002&id=' + data.id, null),
            title: systemLanguage["com.skyeye.editPageTitle"][languageType],
            pageId: "machiningEdit",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
    }

    // 详情
    function details(data) {
        _openNewWindows({
            url:  systemCommonUtil.getUrl('FP2023100300003&id=' + data.id, null),
            title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
            pageId: "machiningDetail",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
            }});
    }

    // 删除
    function del(data, obj) {
        layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function (index) {
            layer.close(index);
            AjaxPostUtil.request({url: sysMainMation.erpBasePath + "deleteMachinById", params: {id: data.id}, type: 'json', method: 'DELETE', callback: function (json) {
                    winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
                    loadTable();
                }});
        });
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
                AjaxPostUtil.request({url: sysMainMation.erpBasePath + "erpmachin007", params: params, type: 'json', method: 'POST', callback: function (json) {
                        winui.window.msg("提交成功", {icon: 1, time: 2000});
                        loadTable();
                    }});
            });
        });
    }

    // 撤销
    function revoke(data) {
        layer.confirm('确认撤销该申请吗？', { icon: 3, title: '撤销操作' }, function (index) {
            layer.close(index);
            AjaxPostUtil.request({url: sysMainMation.erpBasePath + "revokeMachin", params: {processInstanceId: data.processInstanceId}, type: 'json', method: "PUT", callback: function (json) {
                    winui.window.msg("提交成功", {icon: 1, time: 2000});
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

    function getTableParams() {
        return $.extend(true, {}, initTableSearchUtil.getSearchValue("messageTable"));
    }

    exports('departmentMachiningList', {});
});
