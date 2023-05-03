
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
    var serviceClassName = sysServiceMation["putIsPurchase"]["key"];
    authBtn('1571813042536');//新增

    table.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        url: sysMainMation.erpBasePath + 'purchaseput001',
        where: getTableParams(),
        even: true,
        page: true,
        limits: getLimits(),
	    limit: getLimit(),
        cols: [[
            { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
            { field: 'oddNumber', title: '单号', align: 'left', width: 220, templet: function (d) {
		        var str = '<a lay-event="details" class="notice-title-click">' + d.oddNumber + '</a>';
		        if (!isNull(d.parentOrderId)) {
		        	str += '<span class="state-new">[转]</span>';
		        }
		        return str;
		    }},
            { field: 'holderMation', title: '供应商', align: 'left', width: 150, templet: function (d) {
                return d.holderMation.name;
            }},
            { field: 'totalPrice', title: '合计金额', align: 'left', width: 120 },
            { field: 'operTime', title: '单据日期', align: 'center', width: 140 },
            { field: 'processInstanceId', title: '流程ID', width: 100, templet: function (d) {
                return '<a lay-event="processDetails" class="notice-title-click">' + d.processInstanceId + '</a>';
            }},
            { field: 'state', title: '状态', width: 90, templet: function (d) {
                return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("flowableStateEnum", 'id', d.state, 'name');
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
            url:  systemCommonUtil.getUrl('FP2023042300001', null),
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
		rowId = data.id;
		_openNewWindows({
            url:  systemCommonUtil.getUrl('FP2023042300002&id=' + data.id, null),
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
		rowId = data.id;
		_openNewWindows({
            url:  systemCommonUtil.getUrl('FP2023042300003&id=' + data.id, null),
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

    exports('purchasePutList', {});
});
