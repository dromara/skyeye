
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form', 'laydate'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		form = layui.form,
		table = layui.table,
		laydate = layui.laydate;

	laydate.render({
		elem: '#monthTime',
		type: 'month'
	});

	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: reqBasePath + 'userStaffCapital001',
	    where: getTableParams(),
	    even: true,
	    page: true,
	    limits: getLimits(),
	    limit: getLimit(),
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], fixed: 'left', type: 'numbers'},
	        { field: 'companyName', title: '公司名称', align: 'left', width: 150, fixed: 'left'},
	        { field: 'departmentName', title: '部门名称', align: 'left', width: 140},
	        { field: 'monthTime', title: '未结算月份', align: 'center', width: 120 },
			{ field: 'monthAllMony', title: '未结算总金额', align: 'right', width: 140 },
	        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 200, toolbar: '#tableBar'}
	    ]],
	    done: function(){
    		matchingLanguage();
	    }
	});
	
	table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === '') { //
        	edit(data);
        }
    });
	
	form.render();
	form.on('submit(formSearch)', function (data) {
        if (winui.verifyForm(data.elem)) {
        	refreshTable();
        }
        return false;
	});

    $("body").on("click", "#reloadTable", function() {
    	loadTable();
    });
    
    function loadTable(){
    	table.reloadData("messageTable", {where: getTableParams()});
    }
    
    function refreshTable(){
    	table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
    }
    
    function getTableParams(){
    	return {
			companyName: $("#companyName").val(),
			departmentName: $("#departmentName").val(),
			monthTime: $("#monthTime").val()
    	};
    }
    
    exports('sysEveUserStaffCapitalWaitPayMonthList', {});
});
