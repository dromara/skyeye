
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
        
    authBtn('1593442212544');// 新增
        
    table.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        url: sysMainMation.erpBasePath + 'erpmachin001',
        where: getTableParams(),
        even: true,
        page: true,
        limits: getLimits(),
        limit: getLimit(),
        cols: [[
            { title: systemLanguage["com.skyeye.serialNumber"][languageType], rowspan: '2', type: 'numbers' },
            { field: 'oddNumber', rowspan: '2', title: '单据编号', align: 'center', width: 180, templet: function (d) {
		        return '<a lay-event="details" class="notice-title-click">' + d.oddNumber + '</a>';
		    }},
            { field: 'productionMation', rowspan: '2', title: '生产计划单', align: 'center', width: 200, templet: function (d) {return isNull(d.productionMation) ? '' : d.productionMation.oddNumber}},
            { colspan: '3', title: '加工成品信息', align: 'center' },
            { field: 'processInstanceId', rowspan: '2', title: '流程ID', width: 100, templet: function (d) {
                return '<a lay-event="processDetails" class="notice-title-click">' + d.processInstanceId + '</a>';
            }},
            { field: 'state', rowspan: '2', title: '状态', width: 90, templet: function (d) {
                return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("machinStateEnum", 'id', d.state, 'name');
            }},
            { colspan: '3', title: '加工信息', align: 'center' },
            { field: 'createName', rowspan: '2', title: systemLanguage["com.skyeye.createName"][languageType], width: 120 },
            { field: 'createTime', rowspan: '2', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 },
            { field: 'lastUpdateName', rowspan: '2', title: systemLanguage["com.skyeye.lastUpdateName"][languageType], align: 'left', width: 120 },
            { field: 'lastUpdateTime', rowspan: '2', title: systemLanguage["com.skyeye.lastUpdateTime"][languageType], align: 'center', width: 150 },
            { title: systemLanguage["com.skyeye.operation"][languageType], rowspan: '2', fixed: 'right', align: 'center', width: 200, toolbar: '#tableBar'}
        ], [
	        { field: 'materialMation', title: '名称', align: 'left', width: 120, templet: function (d) {return isNull(d.materialMation) ? '' : d.materialMation.name}},
	    	{ field: 'normsMation', title: '规格', align: 'left', width: 80, templet: function (d) {return isNull(d.normsMation) ? '' : d.normsMation.name}},
	        { field: 'needNum', title: '加工数量', align: 'center', width: 80 },
	        { field: 'departmentMation', title: '加工部门', align: 'left', width: 100, templet: function (d) {return isNull(d.departmentMation) ? '' : d.departmentMation.name}},
	    	{ field: 'startTime', title: '开始时间', align: 'center', width: 150 },
	        { field: 'endTime', title: '结束时间', align: 'center', width: 150 }
        ]],
        done: function(json) {
        	matchingLanguage();
            initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "请输入单据编号", function () {
                table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
            });
        }
    });

    table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'delete') { // 删除
            del(data);
        } else if (layEvent === 'details') { // 详情
        	details(data);
        } else if (layEvent === 'edit') { // 编辑
        	edit(data);
        } else if (layEvent === 'subApproval') { // 提交审核
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
            url:  systemCommonUtil.getUrl('FP2023100300001', null),
            title: systemLanguage["com.skyeye.addPageTitle"][languageType],
            pageId: "erpMachinAdd",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
    });

    // 编辑
    function edit(data) {
        _openNewWindows({
            url:  systemCommonUtil.getUrl('FP2023100300002&id=' + data.id, null),
            title: systemLanguage["com.skyeye.editPageTitle"][languageType],
            pageId: "erpMachinEdit",
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
            pageId: "erpMachinDetails",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
            }});
    }

    // 删除
    function del(data) {
        layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function (index) {
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

    function getTableParams(){
    	return $.extend(true, {}, initTableSearchUtil.getSearchValue("messageTable"));
    }

    exports('erpMachinList', {});
});
