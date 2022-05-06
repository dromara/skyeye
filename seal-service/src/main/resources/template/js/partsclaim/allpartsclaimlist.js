
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
	
	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: flowableBasePath + 'sealseservice024',
	    where: {orderNum: $("#orderNum").val(), applyNum: $("#applyNum").val(), customerName: $("#customerName").val()},
	    even: true,
	    page: true,
	    limits: [8, 16, 24, 32, 40, 48, 56],
	    limit: 8,
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
	        { field: 'orderNum', title: '工单号', align: 'center', width: 220, templet: function(d){
	        	return '<a lay-event="details" class="notice-title-click">' + d.orderNum + '</a>';
	        }},
	        { field: 'applyNum', title: '申领单号', align: 'center', width: 220, templet: function(d){
	        	return '<a lay-event="appDetails" class="notice-title-click">' + d.applyNum + '</a>';
	        }},
	        { field: 'customerName', title: '客户名称', align: 'left', width: 150 },
	        { field: 'applyUserName', title: '申领人', align: 'center', width: 140 },
	        { field: 'applyTime', title: '申领时间', align: 'center', width: 140 }
	    ]],
	    done: function(){
	    	matchingLanguage();
	    }
	});
	
	table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'details'){ //详情
        	details(data);
        }else if (layEvent === 'appDetails'){ //申领单详情
        	appDetails(data);
        }
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
    	table.reload("messageTable", {where: {orderNum: $("#orderNum").val(), applyNum: $("#applyNum").val(), customerName: $("#customerName").val()}});
    }
    
    function refreshTable(){
    	table.reload("messageTable", {page: {curr: 1}, where: {orderNum: $("#orderNum").val(), applyNum: $("#applyNum").val(), customerName: $("#customerName").val()}});
    }

	//工单详情
	function details(data){
		rowId = data.serviceId;
		_openNewWindows({
			url: "../../tpl/sealseservice/sealseservicedetails.html", 
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "sealseservicedetails",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
			}});
	}
	
	//申领单详情
	function appDetails(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/partsclaim/partsclaimdetails.html", 
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "sealseservicedetails",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
			}});
	}
	
    exports('allpartsclaimlist', {});
});