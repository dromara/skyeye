var rowId = "";

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
	
	// 新增用品采购
	authBtn('1596958747047');
	
	// '用品采购'页面的选取时间段表格
	laydate.render({
		elem: '#caigouCreateTime', //指定元素
		range: '~'
	});
	
	// 展示用品采购列表
	table.render({
		id: 'caigouTable',
		elem: '#caigouTable',
		method: 'post',
		url: flowableBasePath + 'assetarticles025',
		where: getTableParams(),
		even: true,
		page: true,
		limits: getLimits(),
		limit: getLimit(),
		cols: [[
			{ title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
			{ field: 'title', title: '标题', width: 300, templet: function(d){
				return '<a lay-event="caigouDedails" class="notice-title-click">' + d.title + '</a>';
			}},
			{ field: 'oddNum', title: '单号', width: 200 },
			{ field: 'processInstanceId', title: '流程ID', width: 100, templet: function(d){
				return '<a lay-event="caigouProcessDetails" class="notice-title-click">' + d.processInstanceId + '</a>';
			}},
			{ field: 'stateName', title: '状态', width: 90, templet: function(d){
				return activitiUtil.showStateName2(d.state, 1);
			}},
			{ field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], width: 150},
			{ title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 257, toolbar: '#caigouTableBar'}
		]],
		done: function(){
			matchingLanguage();
		}
	});

	// 用品采购的操作事件
	table.on('tool(caigouTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'caigouDedails') { //采购详情
        	caigouDedails(data);
        }else if (layEvent === 'caigouEdit') { //编辑采购申请
        	caigouEdit(data);
        }else if (layEvent === 'caigouSubApproval') { //采购提交审批
        	caigouSubApproval(data);
        }else if(layEvent === 'caigouCancellation') {//采购作废
        	caigouCancellation(data);
        }else if(layEvent === 'caigouProcessDetails') {//采购流程详情
			activitiUtil.activitiDetails(data);
        }else if(layEvent === 'caigouRevoke') {//撤销采购申请
        	caigouRevoke(data);
        }
    });
	
	// 添加用品采购
	$("body").on("click", "#addCaigouBean", function() {
    	_openNewWindows({
			url: "../../tpl/assetArticlesPurchase/assetArticlesPurchaseAdd.html", 
			title: "用品采购申请",
			pageId: "assetArticlesPurchaseAdd",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                	loadCaigouTable();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2, time: 2000});
                }
			}});
    });
	
	// 撤销用品采购
	function caigouRevoke(data){
		var msg = '确认撤销该用品采购申请吗？';
		layer.confirm(msg, { icon: 3, title: '撤销操作' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "assetarticles035", params: {processInstanceId: data.processInstanceId}, type: 'json', method: "PUT", callback: function (json) {
    			if (json.returnCode == 0) {
    				winui.window.msg("提交成功", {icon: 1, time: 2000});
    				loadCaigouTable();
    			} else {
    				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
    			}
    		}});
		});
	}
	
	// 编辑用品采购申请
	function caigouEdit(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/assetArticlesPurchase/assetArticlesPurchaseEdit.html", 
			title: "编辑用品采购申请",
			pageId: "assetArticlesPurchaseEdit",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                	loadCaigouTable();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2, time: 2000});
                }
			}
		});
	}
	
	// 用品采购提交审批
	function caigouSubApproval(data){
		layer.confirm(systemLanguage["com.skyeye.approvalOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.approvalOperation"][languageType]}, function (index) {
			layer.close(index);
			activitiUtil.startProcess(sysActivitiModel["assetArticlesPurchase"]["key"], function (approvalId) {
				var params = {
					rowId: data.id,
					approvalId: approvalId
				};
				AjaxPostUtil.request({url: flowableBasePath + "assetarticles027", params: params, type: 'json', callback: function (json) {
					if (json.returnCode == 0) {
						winui.window.msg("提交成功", {icon: 1, time: 2000});
						loadCaigouTable();
					} else {
						winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
					}
				}});
			});
		});
	}

	// 用品采购作废
	function caigouCancellation(data){
		var msg = '确认作废该条采购申请吗？';
		layer.confirm(msg, { icon: 3, title: '作废操作' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "assetarticles031", params: {rowId: data.id}, type: 'json', callback: function (json) {
    			if (json.returnCode == 0) {
    				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
    				loadCaigouTable();
    			} else {
    				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
    			}
    		}});
		});
	}
	
	// 用品采购详情
	function caigouDedails(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/assetArticlesPurchase/assetArticlesPurchaseDetails.html", 
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "assetArticlesPurchaseDetails",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
			}
		});
	}

	form.render();
	form.on('submit(formSearch)', function (data) {
		if (winui.verifyForm(data.elem)) {
			table.reload("caigouTable", {page: {curr: 1}, where: getTableParams()});
		}
		return false;
	});

	// 刷新采购数据
    $("body").on("click", "#reloadCaigouTable", function() {
    	loadCaigouTable();
    });
    
	// 刷新采购列表数据
    function loadCaigouTable(){
    	table.reload("caigouTable", {where: getTableParams()});
    }
    
    function getTableParams(){
    	var startTime = "", endTime = "";
		if(!isNull($("#caigouCreateTime").val())){
    		startTime = $("#caigouCreateTime").val().split('~')[0].trim() + ' 00:00:00';
    		endTime = $("#caigouCreateTime").val().split('~')[1].trim() + ' 23:59:59';
    	}
    	return {
    		state: $("#caigouState").val(),
    		startTime: startTime,
    		endTime: endTime
    	};
    }
    
    exports('assetArticlesPurchaseList', {});
});
