
var rowId = "";
var startTime = "";
var endTime = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form', 'laydate'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		form = layui.form,
		laydate = layui.laydate,
		table = layui.table;

	// 售后服务类型
	sysDictDataUtil.showDictDataListByDictTypeCode(sysDictData["amsServiceType"]["key"], 'select', "typeId", '', form);

	// 跟单时间
	laydate.render({elem: '#declarationTime', range: '~'});

	initTable();
	// 待审核表格渲染
	function initTable(){
		table.render({
		    id: 'messageTable',
		    elem: '#messageTable',
		    method: 'post',
		    url: flowableBasePath + 'sealseservice008',
		    where: {orderNum: $("#orderNum").val(), typeId: $("#typeId").val(), customerName: $("#customerName").val(), firstTime: '', lastTime: ''},
		    even: true,
		    page: true,
		    limits: [8, 16, 24, 32, 40, 48, 56],
		    limit: 8,
		    cols: [[
		        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
		        { field: 'orderNum', title: '工单号', align: 'center', width: 220, templet: function (d) {
		        	return '<a lay-event="details" class="notice-title-click">' + d.orderNum + '</a>';
		        }},
		        { field: 'serviceTypeName', title: '服务类型', align: 'left', width: 100 },
		        { field: 'declarationTime', title: '报单时间', align: 'center', width: 140 },
		        { field: 'customerName', title: '客户名称', align: 'left', width: 120 },
		        { field: 'productName', title: '产品名称', align: 'left', width: 120 },
		        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 120, toolbar: '#tableBar'}
		    ]],
		    done: function(){
		    	matchingLanguage();
		    }
		});
		
		table.on('tool(messageTable)', function (obj) {
	        var data = obj.data;
	        var layEvent = obj.event;
	        if (layEvent === 'stayExamine') { //审核
	        	stayExamine(data);
	        }else if (layEvent === 'details'){ //详情
	        	details(data);
	        }
	        
	    });
	}
	
	form.render();
	
	$("body").on("click", "#formSearch", function() {
		refreshTable();
	});
	
	$("body").on("click", "#reloadTable", function() {
    	loadTable();
    });
    
    function loadTable(){
    	if(isNull($("#declarationTime").val())){
    		startTime = "";
    		endTime = "";
    	} else {
    		startTime = $("#declarationTime").val().split('~')[0].trim();
    		endTime = $("#declarationTime").val().split('~')[1].trim();
    	}
    	table.reloadData("messageTable", {where: {orderNum: $("#orderNum").val(), typeId: $("#typeId").val(), customerName: $("#customerName").val(), firstTime: startTime, lastTime: endTime}});
    }
    
    function refreshTable(){
    	if(isNull($("#declarationTime").val())){
    		startTime = "";
    		endTime = "";
    	} else {
    		startTime = $("#declarationTime").val().split('~')[0].trim();
    		endTime = $("#declarationTime").val().split('~')[1].trim();
    	}
    	table.reloadData("messageTable", {page: {curr: 1}, where: {orderNum: $("#orderNum").val(), typeId: $("#typeId").val(), customerName: $("#customerName").val(), firstTime: startTime, lastTime: endTime}});
    }

	//审核
	function stayExamine(data){
		layer.confirm('确认审核该数据吗？', {icon: 3, title: '审核操作'}, function (index) {
			layer.close(index);
            
            AjaxPostUtil.request({url: flowableBasePath + "sealseservice038", params: {rowId: data.id}, type: 'json', callback: function (json) {
				winui.window.msg("审核成功", {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}
	
	//详情
	function details(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/sealseservice/sealseservicedetails.html", 
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "sealseservicedetails",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode) {
			}});
	}
	
    exports('stateisstayexaminelist', {});
});