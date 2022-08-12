
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
			{ field: 'typeName', title: '资产类型', align: 'left', width: 100 },
			{ field: 'assetNum', title: '资产编号', align: 'left', width: 100 },
			{ field: 'specifications', title: '资产规格', align: 'left', width: 120 },
			{ field: 'employeeName', title: '领用人', align: 'left', width: 120 },
			{ field: 'assetAdmin', title: '管理员', align: 'left', width: 120 },
		]],
		done: function(){
			matchingLanguage();
		}
	});

    $("body").on("click", "#reloadmessageTable", function() {
    	loadTable();
    });
	form.render();
	form.on('submit(formSearch)', function (data) {
		if (winui.verifyForm(data.elem)) {
			refreshTable();
		}
		return false;
	});

    function loadTable(){
    	table.reloadData("messageTable", {where: getTableParams()});
    }

	function refreshTable(){
		table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
	}
    
    function getTableParams(){
    	return {
			assetName: $("#assetName").val(),
			assetNum: $("#assetNum").val(),
			specifications: $("#specifications").val()
    	};
    }
    
    exports('myAssetManagement', {});
});
