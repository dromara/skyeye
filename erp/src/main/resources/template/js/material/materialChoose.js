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

	var checkType = '1';//商品选择类型：1.单选；2.多选

	if (!isNull(parent.erpOrderUtil.productCheckType)){
		checkType = parent.erpOrderUtil.productCheckType;
	}

	var s = "商品选择规则：";
	if(checkType == "1"){
		s += '1.单选，双击指定行数据即可选中；';
	} else {
		s += '1.多选；';
		//显示保存按钮
		$("#saveCheckBox").show();
	}
	s += '如没有查到要选择的商品，请检查商品信息是否满足当前规则。';
	$("#showInfo").html(s);

	var categoryId = "";
	sysDictDataUtil.showDictDataListByDictTypeCode(sysDictData["erpMaterialCategory"]["key"], 'selectTree', "materialCategoryType", '', form, function () {
		initTable();
	}, function (chooseId) {
		categoryId = chooseId;
		refreshTable();
	});

	function initTable() {
		if(checkType == '2'){
			var ids = [];
			$.each(parent.erpOrderUtil.chooseProductMation, function(i, item) {
				ids.push(item.materialId);
			});
			tableCheckBoxUtil.init({
				gridId: 'messageTable',
				filterId: 'messageTable',
				fieldName: 'id',
				ids: ids
			});
		}

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
		    	{ type: checkType == '1' ? 'radio' : 'checkbox'},
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
				{ field: 'allStock', title: '当前库存', align: 'left', width: 200, templet: function (d) {
					var str = "";
					$.each(d.materialNorms, function(i, item) {
						if (!isNull(item.overAllStock)) {
							str += '<span class="layui-badge layui-bg-blue">' + item.name + '【' + item.overAllStock.allStock + '】</span><br>';
						} else {
							str += '<span class="layui-badge layui-bg-blue">' + item.name + '【0】</span><br>';
						}
					});
					return str;
				}}
		    ]],
		    done: function(json, curr, count) {
		    	matchingLanguage();
		    	if(checkType == '1'){
					for (var i = 0; i < json.rows.length; i++) {
						// 未启用设置为不可选中
						if (json.rows[i].enabled != 1) {
							systemCommonUtil.disabledRow(json.rows[i].LAY_TABLE_INDEX, 'radio');
						}
					}
			    	$('#messageTable').next().find('.layui-table-body').find("table" ).find("tbody").children("tr").on('dblclick',function(){
						var dubClick = $('#messageTable').next().find('.layui-table-body').find("table").find("tbody").find(".layui-table-hover");
						if (!dubClick.find("input[type='radio']").prop("disabled")) {
							dubClick.find("input[type='radio']").prop("checked", true);
							form.render();
							var chooseIndex = JSON.stringify(dubClick.data('index'));
							var obj = json.rows[chooseIndex];
							parent.erpOrderUtil.chooseProductMation = obj;

							parent.refreshCode = '0';
							parent.layer.close(index);
						}
					});

					$('#messageTable').next().find('.layui-table-body').find("table" ).find("tbody").children("tr").on('click',function(){
						var click = $('#messageTable').next().find('.layui-table-body').find("table").find("tbody").find(".layui-table-hover");
						if (!click.find("input[type='radio']").prop("disabled")) {
							click.find("input[type='radio']").prop("checked", true);
							form.render();
						}
					})
		    	} else {
					for (var i = 0; i < json.rows.length; i++) {
						// 未启用设置为不可选中
						if (json.rows[i].enabled != 1) {
							systemCommonUtil.disabledRow(json.rows[i].LAY_TABLE_INDEX, 'checkbox');
						}
					}
		    		// 多选
		    		tableCheckBoxUtil.checkedDefault({
						gridId: 'messageTable',
						fieldName: 'id'
					});
		    	}

				initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "请输入商品名称，型号", function () {
					table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
				});

		    }
		});

		form.render();
	}

	// 保存按钮-多选才有
	$("body").on("click", "#saveCheckBox", function() {
		var selectedData = tableCheckBoxUtil.getValueList({
			gridId: 'messageTable'
		});
		if (selectedData.length == 0) {
			winui.window.msg("请选择商品", {icon: 2, time: 2000});
			return false;
		}
		parent.erpOrderUtil.chooseProductMation = [].concat(selectedData);
		parent.layer.close(index);
		parent.refreshCode = '0';
	});

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

    exports('materialChoose', {});
});