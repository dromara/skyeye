
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

	var categoryId = "";
	sysDictDataUtil.showDictDataListByDictTypeCode(sysDictData["erpMaterialCategory"]["key"], 'selectTree', "materialCategoryType", '', form, function () {
		initTable();
	}, function (chooseId) {
		categoryId = chooseId;
		refreshTable();
	});

	function initTable(){
		table.render({
		    id: 'messageTable',
	        elem: '#messageTable',
	        method: 'post',
	        url: flowableBasePath + 'material016',
	        where: getTableParams(),
	        even: true,
	        page: true,
	        limits: [8, 16, 24, 32, 40, 48, 56],
	        overflow: {
	            type: 'tips',
	            hoverTime: 300, // 悬停时间，单位ms, 悬停 hoverTime 后才会显示，默认为 0
	            minWidth: 150, // 最小宽度
	            maxWidth: 500 // 最大宽度
	        },
	        limit: 8,
		    cols: [[
		        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
		        { field: 'name', title: '商品名称', align: 'left', width: 150, templet: function (d) {
			        	return '<a lay-event="details" class="notice-title-click">' + d.name + '</a>';
			    }},
		        { field: 'model', title: '型号', align: 'left', width: 150 },
		        { field: 'categoryName', title: '所属类型', align: 'center', width: 100 },
		        { field: 'typeName', title: '商品来源', align: 'left', width: 100 },
		        { field: 'norms', title: '库存', width: 380, templet: function (d) {
		        	var str = "";
		        	$.each(d.norms, function(i, item) {
		        		str += '<span class="layui-badge layui-bg-blue">' + item.name + '【' + item.allStock + '】</span><br>';
		        	});
		        	return str;
		        }},
		        { field: 'enabled', title: '状态', align: 'center', width: 60, templet: function (d) {
					return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("commonEnable", 'id', d.enabled, 'name');
		        }}
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
	    
	    form.render();
	}
    
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

	function refreshTable(){
		table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
	}

	function getTableParams() {
		return $.extend(true, {categoryId: categoryId}, initTableSearchUtil.getSearchValue("messageTable"));
	}
    
    exports('materialReserveList', {});
});
