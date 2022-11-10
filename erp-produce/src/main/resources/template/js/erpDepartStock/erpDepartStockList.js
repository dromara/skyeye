
var rowId = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form', 'soulTable'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		form = layui.form,
		table = layui.table,
		soulTable = layui.soulTable;
	
	table.render({
		id: 'messageTable',
		elem: '#messageTable',
		method: 'post',
		url: flowableBasePath + 'erpdepartstock001',
		where: getTableParams(),
		even: true,
		page: true,
		limits: getLimits(),
		limit: getLimit(),
		overflow: {
			type: 'tips',
			hoverTime: 300, // 悬停时间，单位ms, 悬停 hoverTime 后才会显示，默认为 0
			minWidth: 150, // 最小宽度
			maxWidth: 500 // 最大宽度
		},
		cols: [[
			{ title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
			{ field: 'materialName', title: '商品名称', align: 'left', width: 150, templet: function (d) {
				return '<a lay-event="details" class="notice-title-click">' + d.materialName + '</a>';
			}},
			{ field: 'materialModel', title: '型号', align: 'left', width: 150 },
			{ field: 'categoryName', title: '所属类型', align: 'center', width: 100 },
			{ field: 'typeName', title: '商品来源', align: 'left', width: 100 },
			{ field: 'norms', title: '库存', align: 'center', width: 100, templet: function (d) {
				var str = "";
				$.each(d.norms, function(i, item) {
					str += '<span class="layui-badge layui-bg-blue">' + item.allTock + '</span>' + item.name + '<br>';
				});
				return str;
			}},
			{ field: 'enabled', title: '状态', align: 'center', width: 60, templet: function (d) {
				if (d.enabled == '0') {
					return "<span class='state-down'>禁用</span>";
				} else if (d.enabled == '1') {
					return "<span class='state-up'>启用</span>";
				}
			}},
			{ field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 }
		]],
		done: function(json) {
			matchingLanguage();
			soulTable.render(this);
			initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "请输入商品名称，型号", function () {
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
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/material/materialDetails.html",
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "materialDetails",
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
    
    exports('erpDepartStockList', {});
});
