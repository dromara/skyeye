
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
	    url: flowableBasePath + 'sealseservicecheckwork001',
	    where: {userName: $("#userName").val()},
	    even: true,
	    page: true,
	    limits: [8, 16, 24, 32, 40, 48, 56],
	    limit: 8,
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
	        { field: 'createTime', title: '签到时间', align: 'center', width: 140 },
	        { field: 'userName', title: '签到人', width: 80, align: 'left'},
	        { field: 'departmentName', title: '签到部门', align: 'left', width: 100},
	        { field: 'orderNum', title: '所属工单', align: 'center', width: 220, templet: function (d) {
	        	return '<a lay-event="details" class="notice-title-click">' + d.orderNum + '</a>';
	        }},
	        { field: 'address', title: '签到地址', align: 'left', width: 160},
	        { field: 'remark', title: '备注', align: 'left', width: 200}
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
        }
    });
	
	form.render();
	
	//详情
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
	
	
	$("body").on("click", "#formSearch", function() {
		refreshTable();
	});
	
	$("body").on("click", "#reloadTable", function() {
    	loadTable();
    });
    
    function loadTable(){
    	table.reload("messageTable", {where: {userName: $("#userName").val()}});
    }
    
    function refreshTable(){
    	table.reload("messageTable", {page: {curr: 1}, where: {userName: $("#userName").val()}});
    }

    exports('sealseservicecheckworklist', {});
});