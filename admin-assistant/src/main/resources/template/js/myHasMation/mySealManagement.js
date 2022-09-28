
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

	// 我借用中的印章列表
	table.render({
		id: 'messageTable',
		elem: '#messageTable',
		method: 'post',
		url: flowableBasePath + 'myhasmation002',
		where: getTableParams(),
		even: true,
		page: true,
		limits: getLimits(),
		limit: getLimit(),
		cols: [[
			{ title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
			{ field: 'sealName', title: '印章名称', align: 'left', width: 120 },
			{ field: 'borrowName', title: '领用人', align: 'left', width: 80 },
			{ field: 'sealAdmin', title: '管理员', align: 'left', width: 80 },
		]],
		done: function(json) {
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

    function loadTable() {
    	table.reloadData("messageTable", {where: getTableParams()});
    }

	function refreshTable(){
		table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
	}
    
    function getTableParams() {
    	return {
			sealName: $("#sealName").val()
    	};
    }
    
    exports('mySealManagement', {});
});
