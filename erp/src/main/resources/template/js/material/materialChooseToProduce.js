
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
	var selTemplate = getFileContent('tpl/template/select-option.tpl');
	// 组件使用
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
			ids.push(item.id);
		});
		tableCheckBoxUtil.init({
			gridId: 'messageTable',
			filterId: 'messageTable',
			fieldName: 'id',
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
				}}
		    ]],
			done: function (json, curr, count) {
				matchingLanguage();
				// 设置选中
				tableCheckBoxUtil.checkedDefault({
					gridId: 'messageTable',
					fieldName: 'id'
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
			$("#type" + item.id).val(item.typeId);
		});
		form.render();
	});

	// 保存
	$("body").on("click", "#saveChoose", function() {
		var rows = $("#tBody tr");
		var proList = new Array();
		let checkTable = false;
		$.each(rows, function(i, item) {
			var materialId = $(item).attr("rowid");
			proList.push({
				materialId: materialId,
				bomId: $("#bom" + materialId).val(),
				normsId: $("#norms" + materialId).val()
			});
			if (isNull($("#norms" + materialId).val())) {
				checkTable = true;
				winui.window.msg("请选择商品规格", {icon: 2, time: 2000});
				return false;
			}
		});

		if (!checkTable) {
			var params = {
				proList: JSON.stringify(proList)
			};
			AjaxPostUtil.request({url: sysMainMation.erpBasePath + "material015", params: params, type: 'json', method: 'POST', callback: function(json) {
				parent.materialMationList = [].concat(json.rows);
				parent.layer.close(index);
				parent.refreshCode = '0';
			}});
		}
	});

	form.on('select(unitListChoose)', function (data) {
		var materialId = data.elem.id.replace("norms", "");
		var value = data.value;
		if (isNull(value)) {
			$("#modelId").html("");
		} else {
			AjaxPostUtil.request({url: sysMainMation.erpBasePath + "queryBomListByNormsId", params: {normsId: value}, type: 'json', method: 'GET', callback: function (json) {
				$("#bom" + materialId).html(getDataUseHandlebars(selTemplate, json));
				form.render("select");
			}});
		}
	});

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