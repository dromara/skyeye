var rowId = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form', 'tableCheckBoxUtil'], function (exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
	var $ = layui.$,
		form = layui.form,
		table = layui.table,
		tableCheckBoxUtil = layui.tableCheckBoxUtil;
	
	// 选择的多商品列表
	materialMationList = parent.materialMationList;
	
	// 设置提示信息
	var s = "商品选择规则：1.多选；如没有查到要选择的商品，请检查商品信息是否满足当前规则。";
	$("#showInfo").html(s);

	var categoryId = "";
	sysDictDataUtil.showDictDataListByDictTypeCode(sysDictData["erpMaterialCategory"]["key"], 'selectTree', "materialCategoryType", '', form, function () {
		initTable();
	}, function (chooseId) {
		categoryId = chooseId;
		refreshTable();
	});

	function initTable(){
		var ids = [];
		$.each(materialMationList, function(i, item) {
			ids.push(item.materialId);
		});
		tableCheckBoxUtil.init({
			gridId: 'messageTable',
			filterId: 'messageTable',
			fieldName: 'materialId',
			ids: ids
		});

		table.render({
		    id: 'messageTable',
		    elem: '#messageTable',
		    method: 'post',
			url: sysMainMation.erpBasePath + 'material010',
		    where: getTableParams(),
			even: true,
		    page: true,
			limits: getLimits(),
			limit: getLimit(),
		    cols: [[
		    	{ type: 'checkbox'},
		        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
				{ field: 'name', title: '商品名称', align: 'left', width: 150 },
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
		        { field: 'procedureMationList', title: '工序', align: 'left', width: 100, templet: function (d) {
		        	var str = ""
		        	$.each(d.procedureMationList, function(i, item) {
		        		str += '<span class="layui-badge layui-bg-gray">' + item.number + '</span>' + item.procedureName + '<br>';
		        	});
		        	return str;
			    }}
		    ]],
			done: function (json, curr, count) {
				matchingLanguage();
				// 设置选中
				tableCheckBoxUtil.checkedDefault({
					gridId: 'messageTable',
					fieldName: 'materialId'
				});

				initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "请输入商品名称，型号", function () {
					table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
				});
			}
		});
		
		form.render();
	}
	
	var $step = $("#step");
	$step.step({
		index: 0,
		time: 500,
		title: ["选择商品", "选择规格"]
	});
	
	// 下一步
	$("body").on("click", "#nextTab", function() {
		var selectedData = tableCheckBoxUtil.getValueList({
			gridId: 'messageTable'
		});
		if(selectedData.length == 0){
			winui.window.msg("请选择商品", {icon: 2, time: 2000});
			return false;
		}
		materialMationList = [].concat(selectedData);
		$step.nextStep();
		$("#firstTab").hide();
		$("#secondTab").show();
		$("#tBody").html(getDataUseHandlebars($("#tableBody").html(), {rows: materialMationList}));
		// 设置商品来源选中
		$.each(materialMationList, function(i, item) {
			$("#type" + item.materialId).val(item.typeId);
		});
		form.render();
	});
	
	// 保存
	$("body").on("click", "#saveChoose", function() {
		var rows = $("#tBody tr");
		var proList = new Array();
		$.each(rows, function(i, item) {
			var _object = $(item);
			var materialId = _object.attr("rowid");
			proList.push({
				materialId: materialId,
				materialName: $("#name" + materialId).html(),
				materialModel: $("#model" + materialId).html(),
				bomId: $("#bom" + materialId).val(),
				normsId: $("#norms" + materialId).val(),
				unitName: $("#norms" + materialId).find("option:selected").text(),
				procedureMationList: getProcedureMationList(materialId),
				pId: '0',
				needNum: '1',
				unitPrice: getNormsPrice(materialId, $("#norms" + materialId).val()),
				wastagePrice: '0.00',
				type: $("#type" + materialId).val(),
				remark: '',
				open: 'true',
				isParent: 1
			});
		});
		var params = {
			proList: JSON.stringify(proList)
		};
		AjaxPostUtil.request({url: flowableBasePath + "material015", params: params, type: 'json', callback: function(json) {
			parent.materialMationList = [].concat(json.rows);
			parent.layer.close(index);
			parent.refreshCode = '0';
		}});
	});
	
	/**
	 * 获取商品工序
	 */
	function getProcedureMationList(materialId) {
		var proIndex = -1;
		$.each(materialMationList, function (i, item) {
			if (materialId == item.materialId) {
				proIndex = i;
				return false;
			}
		});
		if (proIndex >= 0) {
			return materialMationList[proIndex].procedureMationList;
		} else {
			return new Array();
		}
	}
	
	/**
	 * 获取指定规格的采购价/成本价
	 */
	function getNormsPrice(materialId, normsId){
		//项目索引
		var proIndex = -1;
		//规格索引
		var normsIndex = -1;
		$.each(materialMationList, function(i, item) {
			if(materialId == item.materialId){
				proIndex = i;
				$.each(item.unitList, function(j, bean){
					normsIndex = j;
					return false;
				});
				return false;
			}
		});
		if(proIndex >= 0 && normsIndex >= 0){
			return materialMationList[proIndex].unitList[normsIndex].estimatePurchasePrice;
		} else {
			return new Array();
		}
	}
	
	// 上一步
	$("body").on("click", "#prevTab", function() {
		$step.prevStep();
		$("#firstTab").show();
		$("#secondTab").hide();
	});

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
	
    exports('materialChooseToProduce', {});
});