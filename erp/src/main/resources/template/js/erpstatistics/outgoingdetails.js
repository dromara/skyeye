
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
		url: sysMainMation.erpBasePath + 'statistics002',
		where: getTableParams(),
		even: true,
		page: true,
		limits: getLimits(),
		limit: getLimit(),
		cols: [[
			{ title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
			{ field: 'oddNumber', title: '单据编号', align: 'left', width: 250, templet: function (d) {
				var str = d.oddNumber;
				if (!isNull(d.fromId)) {
					str += '<span class="state-new">[转]</span>';
				}
				return str;
			}},
			{ field: 'serviceName', title: '单据类型', align: 'left', width: 100 },
			{ field: 'name', title: '产品名称', align: 'left',width: 150, templet: function (d) {
				return getNotUndefinedVal(d.materialMation?.name);
			}},
			{ field: 'model', title: '产品型号', align: 'left',width: 150, templet: function (d) {
				return getNotUndefinedVal(d.materialMation?.model);
			}},
			{ field: 'norms', title: '产品规格', align: 'left',width: 150, templet: function (d) {
				return getNotUndefinedVal(d.normsMation?.name);
			}},
			{ field: 'unitPrice', title: '单价', align: 'left', width: 120},
			{ field: 'operNumber', title: '出库数量', align: 'left', width: 100},
			{ field: 'allPrice', title: '金额', align: 'left', width: 120 },
			{ field: 'depotName', title: '仓库', align: 'left', width: 140 },
			{ field: 'operTime', title: '出库日期', align: 'center', width: 140 }
		]],
		done: function(json) {
			matchingLanguage();
			initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "请输入单据编号", function () {
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

    exports('outgoingdetails', {});
});
