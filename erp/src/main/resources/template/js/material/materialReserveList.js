
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
	var selTemplate = getFileContent('tpl/template/select-option.tpl');

	var categoryId = "";
	sysDictDataUtil.showDictDataListByDictTypeCode(sysDictData["erpMaterialCategory"]["key"], 'selectTree', "materialCategoryType", '', form, function () {
		// 加载当前用户所属仓库
		let depotHtml = '';
		AjaxPostUtil.request({url: sysMainMation.erpBasePath + "queryStaffBelongDepotList", params: {}, type: 'json', method: "GET", callback: function(json) {
			depotHtml = getDataUseHandlebars(selTemplate, json);
		}, async: false});
		initTable(depotHtml);
	}, function (chooseId) {
		categoryId = chooseId;
		refreshTable();
	});

	var depotId = "";
	form.on('select(depotId)', function(data) {
		var thisRowValue = data.value;
		depotId = isNull(thisRowValue) ? "" : thisRowValue;
		loadTable();
	});

	function initTable(depotHtml){
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
		        { field: 'norms', title: '库存', width: 500, templet: function (d) {
		        	var str = "";
					if (isNull(d.materialNorms)) {
						return "";
					}
		        	$.each(d.materialNorms, function(i, item) {
						str += '<span class="layui-badge layui-bg-blue">' + item.name + '【';
						// 如果仓库ID为空，则显示所有仓库的库存，否则显示当前仓库的库存
						if (isNull(depotId)) {
							if (!isNull(item.overAllStock)) {
								str += item.overAllStock.allStock
							} else {
								str += 0;
							}
						} else {
							if (!isNull(item.depotTock)) {
								str += item.depotTock.allStock;
							} else {
								str += 0
							}
						}
						str +=  '】</span><br>';
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
				}, `<label class="layui-form-label">仓库</label><div class="layui-input-inline">
						<select id="depotId" name="depotId" lay-filter="depotId" lay-search="">
						${depotHtml}
					</select></div>`);
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
		let params = {
			depotId: depotId,
			categoryId: categoryId
		};
		return $.extend(true, params, initTableSearchUtil.getSearchValue("messageTable"));
	}
    
    exports('materialReserveList', {});
});
