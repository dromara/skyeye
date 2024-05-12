
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

	var assetId = GetUrlParam("id");

	// 资产明细列表
	table.render({
		id: 'messageTable',
		elem: '#messageTable',
		method: 'post',
		url: sysMainMation.admBasePath + 'queryAssetReportList',
		where: getTableParams(),
		even: true,
		page: true,
		limits: getLimits(),
		limit: getLimit(),
		cols: [[
			{ title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
			{ field: 'assetNum', title: '资产编号', width: 160 },
			{ field: 'unitPrice', title: '采购单价', width: 100 },
			{ field: 'fromId', title: '资产来源', width: 120, templet: function(d) {
				return sysDictDataUtil.getDictDataNameByCodeAndKey("ADM_ASSET_FROM", d.fromId);
			}},
			{ field: 'barCodeMation', title: '条形码', align: 'center', width: 150, templet: function (d) {
				return '<img src="' + systemCommonUtil.getFilePath(d.barCodeMation.imagePath) + '" class="photo-img" lay-event="barCode" style="width: 100px">';
			}},
			{ field: 'state', title: '状态', align: 'center', width: 100, templet: function(d) {
				return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("assetReportState", 'id', d.state, 'name');
			}},
			{ field: 'assetAdminMation', title: '管理员', width: 120, templet: function(d) {
				return getNotUndefinedVal(d.assetAdminMation?.name);
			}},
			{ field: 'useUserMation', title: '申领人', width: 120, templet: function(d) {
				return getNotUndefinedVal(d.useUserMation?.name);
			}},
			{ field: 'storageArea', title: '存放区域', width: 140 },
			{ field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], width: 120 },
			{ field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 },
			{ field: 'lastUpdateName', title: systemLanguage["com.skyeye.lastUpdateName"][languageType], align: 'left', width: 120 },
			{ field: 'lastUpdateTime', title: systemLanguage["com.skyeye.lastUpdateTime"][languageType], align: 'center', width: 150 },
			{ title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 150, toolbar: '#messageTableBar' }
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
		return $.extend(true, {assetId: assetId}, initTableSearchUtil.getSearchValue("messageTable"));
    }

    exports('assetReportList', {});
});
