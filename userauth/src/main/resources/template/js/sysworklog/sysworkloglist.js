
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	
	var $ = layui.$,
		table = layui.table;
	
	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: reqBasePath + 'login006',
	    where:{userName: $("#userName").val(), realPath: $("#realPath").val()},
	    even:true,
	    page: true,
	    limits: getLimits(),
		limit: getLimit(),
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
	        { field: 'userName', title: '操作人', width: 120 },
	        { field: 'ip', title: '操作ip', width: 150 },
	        { field: 'message', title: '日志信息', width: 250 },
	        { field: 'realPath', title: '真实链接', width: 500 },
	        { field: 'logLevel', title: '日志类型', width: 150 },
	        { field: 'createTime', title: '访问时间', width: 180 }
	    ]],
	    done: function(){
	    	matchingLanguage();
	    }
	});
	
	$("body").on("click", "#searchForm", function() {
		refreshTable();
	});
	
	function loadTable(){
    	table.reloadData("messageTable", {where:{userName: $("#userName").val(), realPath: $("#realPath").val()}});
    }
	
	function refreshTable(){
    	table.reloadData("messageTable", {page: {curr: 1}, where:{userName: $("#userName").val(), realPath: $("#realPath").val()}});
    }
	
    exports('sysworkloglist', {});
});
