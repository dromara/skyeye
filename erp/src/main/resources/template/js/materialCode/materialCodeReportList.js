
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

	var materialId = getNotUndefinedVal(GetUrlParam("materialId"));
	var normsId = getNotUndefinedVal(GetUrlParam("normsId"));
	var depotId = getNotUndefinedVal(GetUrlParam("depotId"));
	var objectId = getNotUndefinedVal(GetUrlParam("objectId"));

	// 产品规格明细列表
	table.render({
		id: 'messageTable',
		elem: '#messageTable',
		method: 'post',
		url: sysMainMation.erpBasePath + 'queryMaterialNormsCodeList',
		where: getTableParams(),
		even: true,
		page: true,
		limits: getLimits(),
		limit: getLimit(),
		cols: [[
			{ title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
			{ field: 'name', title: '商品名称', align: 'left', width: 150, templet: function (d) {
				return getNotUndefinedVal(d.materialMation?.name);
			}},
			{ field: 'model', title: '型号', align: 'left', width: 150, templet: function (d) {
				return getNotUndefinedVal(d.materialMation?.name);
			}},
			{ field: 'normsMation', title: '规格', align: 'left', width: 200, templet: function (d) {
				return getNotUndefinedVal(d.normsMation?.name);
			}},
			{ field: 'depotMation', title: '所在仓库', align: 'left', width: 200, templet: function (d) {
				return getNotUndefinedVal(d.depotMation?.name);
			}},
			{ field: 'categoryId', title: '所属分类', align: 'center', width: 100, templet: function (d) {
				return sysDictDataUtil.getDictDataNameByCodeAndKey("ERP_MATERIAL_CATEGORY", getNotUndefinedVal(d.materialMation?.categoryId));
			}},
			{ field: 'barCodeMation', title: '条形码', align: 'center', width: 150, templet: function (d) {
				return '<img src="' + systemCommonUtil.getFilePath(d.barCodeMation.imagePath) + '" class="photo-img" lay-event="barCode" style="width: 100px">';
			}},
			{ field: 'inDepot', title: '状态', align: 'center', width: 100, templet: function(d) {
				return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("materialNormsCodeInDepot", 'id', d.inDepot, 'name');
			}},
			{ field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 },
			{ field: 'qualityWarehousingTime', title: "质检入库时间", align: 'center', width: 150 },
		]],
		done: function(json) {
			matchingLanguage();
			initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "请输入资产编号", function () {
				table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
			});
		}
	});

	table.on('tool(messageTable)', function (obj) {
		var data = obj.data;
		var layEvent = obj.event;
		if (layEvent === 'barCode') { // 条形码预览
			systemCommonUtil.showPicImg(systemCommonUtil.getFilePath(data.barCodeMation.imagePath));
		}
    });

	form.render();
    $("body").on("click", "#reloadTable", function() {
    	loadassetTable();
    });

    function loadassetTable() {
    	table.reloadData("messageTable", {where: getTableParams()});
    }

    function getTableParams() {
		let params = {
			materialId: materialId,
			normsId: normsId,
			depotId: depotId,
			objectId: objectId
		}
		return $.extend(true, params, initTableSearchUtil.getSearchValue("messageTable"));
    }

    exports('materialCodeReportList', {});
});
