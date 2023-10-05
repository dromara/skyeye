
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
		url: sysMainMation.erpBasePath + 'erpdepartstock001',
		where: getTableParams(),
		even: true,
		page: true,
		limits: getLimits(),
		limit: getLimit(),
		cols: [[
			{ title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
			{ field: 'materialMation', title: '产品', align: 'left', width: 120, templet: function (d) {return isNull(d.materialMation) ? '' : d.materialMation.name}},
			{ field: 'normsMation', title: '规格', align: 'left', width: 200, templet: function (d) {return isNull(d.normsMation) ? '' : d.normsMation.name}},
			{ field: 'departmentMation', title: '处理部门', align: 'left', width: 200, templet: function (d) {return isNull(d.departmentMation) ? '' : d.departmentMation.name}},
			{ field: 'stock', title: '部门库存', align: 'center', width: 80 }
		]],
		done: function(json) {
			matchingLanguage();
			initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "暂不支持搜索", function () {
				table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
			});
		}
	});

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
    
    exports('erpDepartStockList', {});
});
