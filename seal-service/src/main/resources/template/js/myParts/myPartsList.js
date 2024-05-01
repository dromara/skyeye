
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
	
	// 获取我申领的未使用的配件
	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: sysMainMation.sealServiceBasePath + 'sealseservice031',
	    where: getTableParams(),
	    even: true,
	    page: true,
		limits: getLimits(),
		limit: getLimit(),
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
			{ field: 'materialId', title: '产品', align: 'left', width: 150, templet: function (d) {
				return d.materialMation?.name;
			}},
			{ field: 'normsId', title: '规格', align: 'left', width: 400, templet: function (d) {
				return d.normsMation?.name;
			}},
	        { field: 'stock', title: '剩余数量', align: 'left', width: 100 }
	    ]],
	    done: function(json) {
	    	matchingLanguage();
			initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, '', function () {
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

    exports('myPartsList', {});
});