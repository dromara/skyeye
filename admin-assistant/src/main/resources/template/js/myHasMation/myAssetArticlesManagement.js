
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

	// 我的用品领用历史列表
	table.render({
		id: 'messageTable',
		elem: '#messageTable',
		method: 'post',
		url: sysMainMation.admBasePath + 'myhasmation004',
		where: getTableParams(),
		even: true,
		page: true,
		limits: getLimits(),
		limit: getLimit(),
		cols: [[
			{ title: systemLanguage["com.skyeye.serialNumber"][languageType], rowspan: '3', type: 'numbers' },
			{ field: 'name', title: '用品名称', align: 'left', rowspan: '3', width: 120 },
			{ field: 'articlesNum', title: '用品编号', align: 'left', rowspan: '3', width: 200 },
			{ field: 'specifications', title: '用品规格', align: 'left', rowspan: '3', width: 80 },
			{ field: 'typeName', title: '用品类型', align: 'left', rowspan: '3', width: 120 },
			{ field: 'applyUseNum', title: '领用数量', align: 'left', rowspan: '3', width: 80 },
			{ title: '所属单据', align: 'center', colspan: '3'}
		],[
			{ field: 'title', title: '单据标题', align: 'left', width: 240},
			{ field: 'oddNumber', title: '单据编号', align: 'center', width: 180},
			{ field: 'createTime', title: '单据日期', align: 'center', width: 140}
		]],
		done: function(json) {
			matchingLanguage();
			initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "请输入单号，用品名称", function () {
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
    
    exports('myAssetArticlesManagement', {});
});
