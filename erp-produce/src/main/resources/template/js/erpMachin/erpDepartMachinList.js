
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
        
    table.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        url: sysMainMation.erpBasePath + 'erpmachin009',
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
            { field: 'state', rowspan: '2', title: '状态', width: 90, templet: function (d) {
                return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("machinStateEnum", 'id', d.state, 'name');
            }},
		    { field: 'pickState', rowspan: '2', title: '领料状态', align: 'left', width: 80, templet: function (d) {
                return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("machinPickStateEnum", 'id', d.pickState, 'name');
		    }},
            { colspan: '3', title: '加工信息', align: 'center' },
            { field: 'createName', rowspan: '2', title: systemLanguage["com.skyeye.createName"][languageType], width: 120 },
            { field: 'createTime', rowspan: '2', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 },
            { field: 'lastUpdateName', rowspan: '2', title: systemLanguage["com.skyeye.lastUpdateName"][languageType], align: 'left', width: 120 },
            { field: 'lastUpdateTime', rowspan: '2', title: systemLanguage["com.skyeye.lastUpdateTime"][languageType], align: 'center', width: 150 },
            { title: systemLanguage["com.skyeye.operation"][languageType], rowspan: '2', fixed: 'right', align: 'center', width: 200, toolbar: '#tableBar'}
        ], [
            { field: 'materialMation', title: '名称', align: 'left', width: 120, templet: function (d) {return isNull(d.materialMation) ? '' : d.materialMation.name}},
            { field: 'normsMation', title: '规格', align: 'left', width: 200, templet: function (d) {return isNull(d.normsMation) ? '' : d.normsMation.name}},
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
        if (layEvent === 'details') { // 详情
        	details(data);
        }
    });

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

    exports('erpDepartMachinList', {});
});
