
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
	
	//获取我使用的配件
	
	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: reqBasePath + 'sealseservice032',
	    where: {materialName: $("#materialName").val(), materialModel: $("#materialModel").val()},
	    even: true,
	    page: true,
	    limits: [8, 16, 24, 32, 40, 48, 56],
	    limit: 8,
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
	        { field: 'materialName', title: '配件名称', align: 'left', width: 200 },
	        { field: 'unitName', title: '计量单位', align: 'left', width: 80 },
	        { field: 'materialModel', title: '配件规格', align: 'left', width: 150 },
	        { field: 'unitPrice', title: '单价', align: 'left', width: 100 },
	        { field: 'operNumber', title: '已用数量', align: 'left', width: 100 }
	    ]],
	    done: function(){
	    	matchingLanguage();
	    }
	});
	
	table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
    });
	
	form.render();
	
	//刷新数据
	$("body").on("click", "#reloadTable", function(){
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
    	table.reload("messageTable", {where: {materialName: $("#materialName").val(), materialModel: $("#materialModel").val()}});
    }
    
    function refreshTable(){
    	table.reload("messageTable", {page: {curr: 1}, where: {materialName: $("#materialName").val(), materialModel: $("#materialModel").val()}});
    }

    exports('myusepartslist', {});
});