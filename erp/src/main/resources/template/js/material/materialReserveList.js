
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
	        url: sysMainMation.erpBasePath + 'material016',
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
		        { field: 'name', title: '产品名称', align: 'left', width: 150 },
		        { field: 'model', title: '型号', align: 'left', width: 150 },
		        { field: 'categoryId', title: '所属分类', align: 'center', width: 100, templet: function (d) {
					return sysDictDataUtil.getDictDataNameByCodeAndKey("ERP_MATERIAL_CATEGORY", d.categoryId);
				}},
		        { field: 'fromType', title: '产品来源', align: 'center', width: 100, templet: function (d) {
					return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("materialFromType", 'id', d.fromType, 'name');
				}},
				{ field: 'type', title: '产品类型', align: 'left', width: 100, templet: function (d) {
					return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("materialType", 'id', d.type, 'name');
				}},
				{ field: 'unit', title: '产品规格类型', align: 'center', width: 100, templet: function (d) {
					return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("materialUnit", 'id', d.unit, 'name');
				}},
		        { field: 'norms', title: '库存', width: 150, templet: function (d) {
		        	var str = "";
		        	$.each(d.materialNorms, function(i, item) {
						if (!isNull(item.overAllStock)) {
							str += '<span class="layui-badge layui-bg-blue">' + item.name + '【' + item.overAllStock.allStock + '】</span><br>';
						}
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
