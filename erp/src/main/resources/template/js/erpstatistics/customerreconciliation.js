
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
		url: sysMainMation.erpBasePath + 'statistics005',
		where: getTableParams(),
		even: true,
		page: true,
		limits: getLimits(),
		limit: getLimit(),
		cols: [[
			{ title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
			{ field: 'oddNumber', title: '单据编号', align: 'left', width: 250, templet: function (d) {
				var str = d.oddNumber;
				if (!isNull(d.parentOrderId)) {
					str += '<span class="state-new">[转]</span>';
				}
				return str;
			}},
			{ field: 'serviceName', title: '单据类型', align: 'left', width: 100 },
			{ field: 'holderMation', title: '客户', align: 'left', width: 150, templet: function (d) {
				return getNotUndefinedVal(d.holderMation?.name);
			}},
			{ field: 'totalPrice', title: '合计金额', align: 'left', width: 100},
			{ field: 'operTime', title: '单据日期', align: 'center', width: 150}
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

    exports('customerreconciliation', {});
});
