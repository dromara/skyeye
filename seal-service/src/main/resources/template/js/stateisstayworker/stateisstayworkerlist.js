
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
	
	//分类
	showGrid({
	 	id: "typeId",
	 	url: flowableBasePath + "sealseservicetype008",
	 	params: {},
	 	pagination: false,
	 	template: getFileContent('tpl/template/select-option.tpl'),
	 	ajaxSendLoadBefore: function(hdb){
	 	},
	 	ajaxSendAfter:function(j){
	 		form.render('select');
	 		initTable();
	 	}
	});
	
	// 跟单时间
	laydate.render({elem: '#declarationTime', range: '~'});
		
	// 待派工表格渲染
	function initTable(){
		table.render({
		    id: 'messageTable',
		    elem: '#messageTable',
		    method: 'post',
		    url: flowableBasePath + 'sealseservice002',
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
		        { field: 'serviceTypeName', title: '服务类型', align: 'left', width: 120 },
		        { field: 'declarationTime', title: '报单时间', align: 'center', width: 140 },
		        { field: 'customerName', title: '客户名称', align: 'left', width: 120 },
		        { field: 'productName', title: '产品名称', align: 'left', width: 120 },
		        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 150, toolbar: '#tableBar'}
		    ]],
		    done: function(){
		    	matchingLanguage();
		    }
		});
		
		table.on('tool(messageTable)', function (obj) {
	        var data = obj.data;
	        var layEvent = obj.event;
	        if (layEvent === 'stayWork') { //派工
	        	stayWork(data);
	        }else if (layEvent === 'details'){ //详情
	        	details(data);
	        }else if (layEvent === 'edit') { //编辑
	        	edit(data);
	        }else if (layEvent === 'delete'){ //删除
	        	deleteRow(data);
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
    	table.reload("messageTable", {where: {orderNum: $("#orderNum").val(), typeId: $("#typeId").val(), customerName: $("#customerName").val(), firstTime: startTime, lastTime: endTime}});
    }
    
    function refreshTable(){
    	if(isNull($("#declarationTime").val())){
    		startTime = "";
    		endTime = "";
    	} else {
    		startTime = $("#declarationTime").val().split('~')[0].trim();
    		endTime = $("#declarationTime").val().split('~')[1].trim();
    	}
    	table.reload("messageTable", {page: {curr: 1}, where: {orderNum: $("#orderNum").val(), typeId: $("#typeId").val(), customerName: $("#customerName").val(), firstTime: startTime, lastTime: endTime}});
    }

	//派工
	function stayWork(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/stateisstayworker/dispatchedworker.html", 
			title: "派工",
			pageId: "dispatchedworker",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	}
	
	//编辑
	function edit(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/sealseservice/sealseserviceedit.html", 
			title: systemLanguage["com.skyeye.editPageTitle"][languageType],
			pageId: "sealseserviceedit",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	}
	
	//详情
	function details(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/sealseservice/sealseservicedetails.html", 
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "sealseservicedetails",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
			}});
	}
	
	//删除
	function deleteRow(data){
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
			layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "sealseservice020", params: {rowId: data.id}, type: 'json', callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}
	
    exports('stateisstayworkerlist', {});
});