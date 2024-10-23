
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
    var serviceClassName = sysServiceMation["outchaseOrder"]["key"];
    authBtn('1571813326930');//新增

    table.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        url: sysMainMation.erpBasePath + 'salesorder001',
        where: getTableParams(),
        even: true,
        page: true,
        limits: getLimits(),
	    limit: getLimit(),
        cols: [[
            { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers', rowspan: '2' },
            { field: 'oddNumber', title: '单据编号', rowspan: '2', align: 'left', width: 200, templet: function (d) {
		        return '<a lay-event="details" class="notice-title-click">' + d.oddNumber + '</a>';
		    }},
            { field: 'holderMation', title: '客户', rowspan: '2', align: 'left', width: 200, templet: function (d) {
                return getNotUndefinedVal(d.holderMation?.name);
            }},
            { field: 'totalPrice', title: '合计金额', rowspan: '2', align: 'left', width: 120 },
            { field: 'operTime', title: '单据日期', rowspan: '2', align: 'center', width: 140 },
            { colspan: '2', title: '来源单据信息', align: 'center' },
            { field: 'processInstanceId', title: '流程ID', rowspan: '2', width: 100, templet: function (d) {
                return '<a lay-event="processDetails" class="notice-title-click">' + getNotUndefinedVal(d.processInstanceId) + '</a>';
            }},
            { field: 'state', title: '状态', rowspan: '2', width: 90, templet: function (d) {
                return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("erpOrderStateEnum", 'id', d.state, 'name');
            }},
            { field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], rowspan: '2', width: 120 },
            { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], rowspan: '2', align: 'center', width: 150 },
            { field: 'lastUpdateName', title: systemLanguage["com.skyeye.lastUpdateName"][languageType], rowspan: '2', align: 'left', width: 120 },
            { field: 'lastUpdateTime', title: systemLanguage["com.skyeye.lastUpdateTime"][languageType], rowspan: '2', align: 'center', width: 150 },
            { title: systemLanguage["com.skyeye.operation"][languageType], rowspan: '2', fixed: 'right', align: 'center', width: 300, toolbar: '#tableBar' }
        ], [
            { field: 'fromTypeId', title: '来源类型', width: 150, templet: function (d) {
                return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("purchaseOrderFromType", 'id', d.fromTypeId, 'name');
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
            erpOrderUtil.deleteOrderMation(data.id, serviceClassName, function() {
                loadTable();
            });
        } else if (layEvent === 'details') { //详情
        	details(data);
        } else if (layEvent === 'edit') { //编辑
        	edit(data);
        } else if (layEvent === 'subApproval') { //提交审核
            erpOrderUtil.submitOrderMation(data.id, serviceClassName, function() {
                loadTable();
            });
        } else if (layEvent === 'turnSales') { //转销售出库单
        	turnSales(data);
        } else if (layEvent === 'turnReturns') { //转销售退货单
            turnReturns(data);
        }else if (layEvent === 'turnToPreProductionPlan') { //转出货计划单
            turnToPreProductionPlan(data);
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
            url:  systemCommonUtil.getUrl('FP2023042600005', null),
            title: systemLanguage["com.skyeye.addPageTitle"][languageType],
            pageId: "salesOrderAdd",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
    });

    // 编辑
    function edit(data) {
        _openNewWindows({
            url:  systemCommonUtil.getUrl('FP2023042600006&id=' + data.id, null),
            title: systemLanguage["com.skyeye.editPageTitle"][languageType],
            pageId: "salesOrderEdit",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
    }

    // 详情
	function details(data) {
		_openNewWindows({
            url:  systemCommonUtil.getUrl('FP2023042600007&id=' + data.id, null),
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "salesOrderDetails",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
			}});
	}

    // 销售订单转销售出库
	function turnSales(data) {
		_openNewWindows({
			url:"../../tpl/salesOrder/salesOrderToOutlet.html?id=" + data.id,
            title: "转销售出库",
			pageId: "salesOrderToOut",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
			}});
    }

    // 销售订单转销售退货
    function turnReturns(data) {
        _openNewWindows({
            url:"../../tpl/salesOrder/salesOrderToReturns.html?id=" + data.id,
            title: "转销售退货",
            pageId: "salesOrderToReturns",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
    }

    // 销售订单转出货计划
    function turnToPreProductionPlan(data) {
        _openNewWindows({
            url:"../../tpl/salesOrder/salesOrderToPreProductionPlan.html?id=" + data.id,
            title: "转出货计划",
            pageId: "salesOrderToReturns",
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

    exports('salesOrderList', {});
});
