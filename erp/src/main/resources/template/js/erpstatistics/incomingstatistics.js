
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
		url: flowableBasePath + 'statistics003',
		where: getTableParams(),
		even: true,
		page: true,
		limits: getLimits(),
		limit: getLimit(),
		cols: [[
			{ title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
			{ field: 'materialName', title: '产品名称', align: 'left', width: 250 },
			{ field: 'materialModel', title: '型号', align: 'left', width: 150 },
			{ field: 'unitName', title: '单位', align: 'left', width: 80 },
			{ field: 'currentTock', title: '进货数量', align: 'left', width: 100 },
			{ field: 'currentTockMoney', title: '进货金额', align: 'left', width: 120 },
			{ field: 'returnCurrentTock', title: '退货数量', align: 'left', width: 100 },
			{ field: 'returnCurrentTockMoney', title: '退货金额', align: 'left', width: 120 }
		]],
		done: function(json) {
			matchingLanguage();
			initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form,
				{value: "请选择日期", type: 'month', defaultValue: getOneYMFormatDate(), required: 'required'},
				function () {
				table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
			});
		}
	});

	form.render();
    $("body").on("click", "#reloadTable", function() {
        loadTable();
    });

    // 刷新
    function loadTable() {
		table.reloadData("messageTable", {where: getTableParams()});
    }

	function getTableParams() {
		var params = {};
		if ($("#messageTableKeyWord").length == 0) {
			params["keyword"] = getOneYMFormatDate();
		}
		return $.extend(true, params, initTableSearchUtil.getSearchValue("messageTable"));
	}

    exports('incomingstatistics', {});
});
