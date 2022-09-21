
var rowId = "";

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

	// 未申领的资产明细列表
	table.render({
		id: 'messageTable',
		elem: '#messageTable',
		method: 'post',
		url: flowableBasePath + 'queryAssetReportList',
		where: getTableParams(),
		even: true,
		page: true,
		limits: getLimits(),
		limit: getLimit(),
		cols: [[
			{ title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
			{ field: 'assetNum', title: '资产编号', width: 160, templet: function (d) {
				return '<a lay-event="details" class="notice-title-click">' + d.assetNum + '</a>';
			}},
			{ field: 'unitPrice', title: '采购单价', width: 100 },
			{ field: 'fromName', title: '资产来源', width: 120 },
			{ field: 'barCodeMation', title: '条形码', align: 'center', width: 100, templet: function (d) {
				return '<img src="' + systemCommonUtil.getFilePath(d.barCodeMation.imagePath) + '" class="photo-img" lay-event="barCode" style="width: 100px">';
			}},
			{ field: 'stateName', title: '状态', align: 'center', width: 100 },
			{ field: 'assetAdmin', title: '管理员', width: 80 },
			{ field: 'useUserName', title: '申领人', width: 120 },
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
		if (layEvent === 'details') { // 详情
			details(data);
		} else if (layEvent === 'barCode') { // 条形码预览
			systemCommonUtil.showPicImg(systemCommonUtil.getFilePath(data.barCodeMation.imagePath));
		}
    });

	// 详情
	function details(data) {
		rowId = data.id;
		_openNewWindows({
			url: "",
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "assetManageDetails",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
			}});
	}

	form.render();
    $("body").on("click", "#reloadTable", function() {
    	loadassetTable();
    });

    function loadassetTable() {
    	table.reloadData("messageTable", {where: getTableParams()});
    }

    function getTableParams() {
		return $.extend(true, {assetId: parent.rowId}, initTableSearchUtil.getSearchValue("messageTable"));
    }

    exports('assetReportList', {});
});
