
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
		url: flowableBasePath + 'myhasmation001',
		where: getTableParams(),
		even: true,
		page: true,
		limits: getLimits(),
		limit: getLimit(),
		cols: [[
			{ title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
			{ field: 'assetName', title: '资产名称', align: 'left', width: 120 },
			{ field: 'assetImg', title: '图片', align: 'center', width: 60, templet: function (d) {
				return '<img src="' + systemCommonUtil.getFilePath(d.assetImg) + '" class="photo-img" lay-event="assetImg">';
			}},
			{ field: 'typeName', title: '类型', align: 'left', width: 100 },
			{ field: 'assetNum', title: '资产编号', align: 'left', width: 150 },
			{ field: 'specifications', title: '资产规格', align: 'left', width: 120 },
			{ field: 'assetAdmin', title: '管理员', align: 'left', width: 120 },
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
	$("body").on("click", "#reloadmessageTable", function() {
    	loadTable();
    });

    function loadTable() {
    	table.reloadData("messageTable", {where: getTableParams()});
    }

    function getTableParams() {
		return $.extend(true, {}, initTableSearchUtil.getSearchValue("messageTable"));
    }
    
    exports('myAssetManagement', {});
});
