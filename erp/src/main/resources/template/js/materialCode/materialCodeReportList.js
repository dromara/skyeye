
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
	var fromObjectId = getNotUndefinedVal(GetUrlParam("fromObjectId"));
	var toObjectId = getNotUndefinedVal(GetUrlParam("toObjectId"));

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
			{ field: 'normsMation', title: '规格', align: 'left', width: 200, templet: function (d) {
				return getNotUndefinedVal(d.normsMation?.name);
			}},
			{ field: 'codeNum', title: '编码', align: 'left', width: 180, templet: function (d) {
				return d.codeNum;
			}},
			{ field: 'depotMation', title: '所在仓库', align: 'left', width: 170, templet: function (d) {
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
			{ title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 120, toolbar: '#tableBar' }
		]],
		done: function(json) {
			matchingLanguage();
			initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "请输入编号", function () {
				table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
			});
		}
	});

	table.on('tool(messageTable)', function (obj) {
		var data = obj.data;
		var layEvent = obj.event;
		if (layEvent === 'barCode') { // 条形码预览
			systemCommonUtil.showPicImg(systemCommonUtil.getFilePath(data.barCodeMation.imagePath));
		} else if (layEvent === 'delete') { // 删除
			delet(data);
		}
    });

	// 删除
	function delet(data) {
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function (index) {
			layer.close(index);
			AjaxPostUtil.request({url: sysMainMation.erpBasePath + "deleteMaterialNormsCodeById", params: {id: data.id}, type: 'json', method: "DELETE", callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
		});
	}

	// 新增
	$("body").on("click", "#addBean", function() {
		_openNewWindows({
			url: "../../tpl/materialCode/materialCodeAdd.html?materialId=" + materialId,
			title: systemLanguage["com.skyeye.addPageTitle"][languageType],
			pageId: "materialCodeAdd",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	});

	// 批量复制
	$("body").on("click", "#batchCopy", function() {
		_openNewWindows({
			url: "../../tpl/materialCode/batchCopy.html?materialId=" + materialId,
			title: '批量复制',
			pageId: "materialCodeBatchCopy",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
			}});
	});

	form.render();
    $("body").on("click", "#reloadTable", function() {
		loadTable();
    });

    function loadTable() {
    	table.reloadData("messageTable", {where: getTableParams()});
    }

    function getTableParams() {
		let params = {
			materialId: materialId,
			normsId: normsId,
			depotId: depotId,
			fromObjectId: fromObjectId,
			toObjectId: toObjectId
		}
		return $.extend(true, params, initTableSearchUtil.getSearchValue("messageTable"));
    }

    exports('materialCodeReportList', {});
});
