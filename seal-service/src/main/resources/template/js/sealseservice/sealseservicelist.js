
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

	// 所有工单列表
	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: flowableBasePath + 'sealseservice001',
	    where: {orderNum: $("#orderNum").val(), state: $("#state").val(), contacts: $("#contacts").val(), receiver: $("#receiver").val()},
	    even: true,
	    page: true,
		limits: getLimits(),
		limit: getLimit(),
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
	        { field: 'orderNum', title: '工单号', align: 'center', width: 220, templet: function (d) {
	        	return '<a lay-event="details" class="notice-title-click">' + d.orderNum + '</a>';
	        }},
	        { field: 'serviceTypeName', title: '服务类型', align: 'left', width: 100 },
	        { field: 'stateName', title: '状态', align: 'left', width: 80 },
	        { field: 'declarationTime', title: '报单时间', align: 'center', width: 140 },
	        { field: 'customerName', title: '客户名称', align: 'left', width: 120 },
	        { field: 'contacts', title: '联系人', align: 'left', width: 80 },
	        { field: 'phone', title: '联系电话', align: 'center', width: 100 },
	        { field: 'receiver', title: '工单接收人', align: 'left', width: 100 },
	        { field: 'typeName', title: '工单类型', align: 'center', width: 80 },
	        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 150, toolbar: '#tableBar'}
	    ]],
	    done: function(){
	    	matchingLanguage();
	    }
	});
	
	table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'edit') { //编辑
        	edit(data);
        }else if (layEvent === 'details'){ //详情
        	details(data);
        }else if (layEvent === 'delete'){ //删除
        	deleteRow(data);
        }else if (layEvent === 'stayworker'){ //派工
        	stayWork(data);
        }else if (layEvent === 'receipt'){ //接单
        	stayReceipt(data);
        }else if (layEvent === 'signIn'){ //签到
        	signIn(data);
        }else if (layEvent === 'finished'){ //完工
        	finishedPro(data);
        }else if (layEvent === 'evaluate'){ //评价
        	evaluate(data);
        }else if (layEvent === 'examine'){ //审核
        	examine(data);
        }else if (layEvent === 'feedBack'){ //情况反馈
        	feedBack(data);
        }
    });
	
	form.render();
	
	$("body").on("click", "#formSearch", function() {
		refreshTable();
	});
	
	$("body").on("click", "#reloadTable", function() {
    	loadTable();
    });
    
    function loadTable(){
    	table.reload("messageTable", {where: {orderNum: $("#orderNum").val(), state: $("#state").val(), contacts: $("#contacts").val(), receiver: $("#receiver").val()}});
    }
    
    function refreshTable(){
    	table.reload("messageTable", {page: {curr: 1}, where: {orderNum: $("#orderNum").val(), state: $("#state").val(), contacts: $("#contacts").val(), receiver: $("#receiver").val()}});
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
	
	// 详情
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
	
	// 派工
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
	
	// 接单
	function stayReceipt(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/stateisstayreceipt/receipt.html", 
			title: "接单",
			pageId: "receipt",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	}
	
	//签到
	function signIn(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/stateisstaysignin/signin.html", 
			title: "签到",
			pageId: "signin",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	}
	
	//完工
	function finishedPro(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/stateisstayfinishedpro/stateisstayfinished.html", 
			title: "完工",
			pageId: "stateisstayfinished",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	}
	
	//评价
	function evaluate(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/stateisstayevaluate/stateisstayevaluate.html", 
			title: "评价",
			pageId: "stateisstayevaluate",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	}
	
	//审核
	function examine(data){
		layer.confirm('确认审核该数据吗？', {icon: 3, title: '审核操作'}, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "sealseservice038", params: {rowId: data.id}, type: 'json', callback: function (json) {
				winui.window.msg("审核成功", {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}
	
	//情况反馈
	function feedBack(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/feedback/feedbacklist.html", 
			title: "情况反馈",
			pageId: "feedbacklist",
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
	
    exports('sealseservicelist', {});
});