
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

	showList();
	// 我借用中的证照列表
	function showList(){
		table.render({
		    id: 'messageTable',
		    elem: '#messageTable',
		    method: 'post',
		    url: flowableBasePath + 'myhasmation003',
		    where: getTableParams(),
		    even: true,
		    page: true,
		    limits: getLimits(),
	    	limit: getLimit(),
		    cols: [[
		        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
				{ field: 'companyName', title: '所属公司', align: 'left', width: 170 },
				{ field: 'licenceName', title: '资产名称', align: 'left', width: 120 },
				{ field: 'borrowName', title: '领用人', align: 'left', width: 80 },
				{ field: 'licenceAdmin', title: '管理员', align: 'left', width: 80 },
		    ]],
		    done: function(){
		    	matchingLanguage();
		    }
		});
	}

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
			licenceName: $("#licenceName").val()
    	};
    }
    
    exports('myLicenceManagement', {});
});
