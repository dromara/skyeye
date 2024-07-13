
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

	// 我名下的资产列表
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
			{ field: 'name', title: '资产名称', width: 120, templet: function(d) {
					return getNotUndefinedVal(d.assetMation?.name);
				}},
			{ field: 'assetImg', title: '图片', align: 'center', width: 60, templet: function (d) {
				return '<img src="' + systemCommonUtil.getFilePath(d.assetMation.assetImg) + '" class="photo-img" lay-event="assetImg">';
			}},
			{ field: 'typeId', title: '资产类型', width: 120, templet: function(d) {
				return sysDictDataUtil.getDictDataNameByCodeAndKey("ADM_ASSET_TYPE", d.assetMation.typeId);
			}},
			{ field: 'assetNum', title: '资产编号', align: 'left', width: 150 },
			{ field: 'assetAdminMation', title: '管理员', width: 120, templet: function(d) {
				return getNotUndefinedVal(d.assetAdminMation?.name);
			}},
			{ field: 'createTime', title: '申领时间', align: 'center', width: 150 },
		]],
		done: function(json) {
			matchingLanguage();
			initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "请输入资产名称，资产编号", function () {
				table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
			});
		}
	});

	table.on('tool(messageTable)', function (obj) {
		var data = obj.data;
		var layEvent = obj.event;
		if (layEvent === 'assetImg') { // 图片预览
			systemCommonUtil.showPicImg(systemCommonUtil.getFilePath(data.assetImg));
		}
	});

	form.render();
	$("body").on("click", "#reloadTable", function() {
    	loadTable();
    });

    function loadTable() {
    	table.reloadData("messageTable", {where: getTableParams()});
    }

    function getTableParams() {
		return $.extend(true, {state: "myUse"}, initTableSearchUtil.getSearchValue("messageTable"));
    }
    
    exports('myAssetManagement', {});
});
