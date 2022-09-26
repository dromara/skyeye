
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
	    url: flowableBasePath + 'sealseservice031',
	    where: getTableParams(),
	    even: true,
	    page: true,
		limits: getLimits(),
		limit: getLimit(),
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
	        { field: 'materialName', title: '配件名称', align: 'left', width: 200 },
	        { field: 'unitName', title: '计量单位', align: 'left', width: 80 },
	        { field: 'materialModel', title: '配件规格', align: 'left', width: 150 },
	        { field: 'normsFormtRetailPrice', title: '单价', align: 'left', width: 100 },
	        { field: 'operNumber', title: '剩余数量', align: 'left', width: 100 }
	    ]],
	    done: function(json) {
	    	matchingLanguage();
			initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "请输入剩余数量TODO待完善插件", function () {
				table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
			});
	    }
	});
	
	table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
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

    exports('mypartslist', {});
});