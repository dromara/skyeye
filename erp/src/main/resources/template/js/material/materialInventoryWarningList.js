
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
		url: sysMainMation.erpBasePath + 'material017',
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
			{ title: systemLanguage["com.skyeye.serialNumber"][languageType], rowspan: '2', type: 'numbers' },
			{ field: 'name', title: '产品名称', rowspan: '2', align: 'left', width: 150 },
			{ field: 'model', title: '型号', rowspan: '2', align: 'left', width: 150 },
			{ field: 'normsName', title: '规格', rowspan: '2', align: 'left', width: 300 },
			{ field: 'categoryId', title: '所属分类', rowspan: '2', align: 'center', width: 100, templet: function (d) {
				return sysDictDataUtil.getDictDataNameByCodeAndKey("ERP_MATERIAL_CATEGORY", d.categoryId);
			}},
			{ field: 'fromType', title: '产品来源', rowspan: '2', align: 'center', width: 100, templet: function (d) {
				return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("materialFromType", 'id', d.fromType, 'name');
			}},
			{ field: 'type', title: '产品类型', rowspan: '2', align: 'left', width: 100, templet: function (d) {
				return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("materialType", 'id', d.type, 'name');
			}},
			{ field: 'unit', title: '产品规格类型', rowspan: '2', align: 'center', width: 100, templet: function (d) {
				return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("materialUnit", 'id', d.unit, 'name');
			}},
			{ title: '库存', colspan: '2', align: 'center', width: 100 }
		],[
			{ field: 'safetyTock', title: '安全存量', align: 'center', width: 80 },
			{ field: 'allStock', title: '当前库存', align: 'center', width: 120, templet: function (d) {
				if (!isNull(d.overAllStock)) {
					return d.overAllStock.allStock;
				}
				return "0";
			}}
		]],
		done: function(json) {
			matchingLanguage();
			soulTable.render(this);
		}
	});

	form.render();
	form.on('submit(formSearch)', function (data) {
        if (winui.verifyForm(data.elem)) {
        	refreshloadTable();
        }
        return false;
    });
    
	// 刷新数据
    $("body").on("click", "#reloadTable", function() {
    	loadTable();
    });
    
    function loadTable() {
    	table.reloadData("messageTable", {where: getTableParams()});
    }
    
    function refreshloadTable() {
    	table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
    }
    
    function getTableParams() {
    	return {
    		materialName: $("#materialName").val(), 
    		model: $("#model").val(), 
    		typeNum: $("#typeNum").val()
    	};
    }
    
    exports('materialInventoryWarningList', {});
});
