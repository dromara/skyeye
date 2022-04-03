
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
	
	// 证照借用
	authBtn('1596969413440');
    
	laydate.render({
		elem: '#createTime',
		range: '~'
	});
	
	// 证照借用列表
	table.render({
		id: 'borrowTable',
		elem: '#borrowTable',
		method: 'post',
		url: flowableBasePath + 'licenceborrow001',
		where: getTableParams(),
		even: true,
		page: true,
		limits: getLimits(),
		limit: getLimit(),
		cols: [[
			{ title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
			{ field: 'title', title: '标题', width: 300, templet: function(d){
				return '<a lay-event="borrowDedails" class="notice-title-click">' + d.title + '</a>';
			}},
			{ field: 'oddNum', title: '单号', width: 200, align: 'center' },
			{ field: 'processInstanceId', title: '流程ID', width: 80, align: 'center', templet: function(d){
				if(!isNull(d.processInstanceId)){
					return '<a lay-event="processDetails" class="notice-title-click">' + d.processInstanceId + '</a>';
				}else{
					return "";
				}
			}},
			{ field: 'stateName', title: '状态', width: 90, align: 'center', templet: function(d){
				if(d.state == '0'){
					return "<span>" + d.stateName + "</span>";
				}else if(d.state == '1'){
					return "<span class='state-new'>" + d.stateName + "</span>";
				}else if(d.state == '2'){
					return "<span class='state-up'>" + d.stateName + "</span>";
				}else if(d.state == '3'){
					return "<span class='state-down'>" + d.stateName + "</span>";
				}else if(d.state == '4'){
					return "<span class='state-down'>" + d.stateName + "</span>";
				}else if(d.state == '5'){
					return "<span class='state-error'>" + d.stateName + "</span>";
				}
			}},
			{ field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], width: 150, align: 'center'},
			{ title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 250, toolbar: '#borrowTableBar'}
		]],
		done: function(){
			matchingLanguage();
		}
	});

	// 证照借用的操作事件
	table.on('tool(borrowTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'borrowDedails') { //借用详情
        	borrowDedails(data);
        }else if (layEvent === 'processDetails') { //流程详情
			activitiUtil.activitiDetails(data);
        }else if (layEvent === 'borrowedit') { //编辑借用申请
        	borrowEdit(data);
        }else if (layEvent === 'subApproval') { //提交审批
        	subApproval(data);
        }else if(layEvent === 'cancellation') {//借用作废
        	cancellation(data);
        }else if(layEvent === 'revoke') {//撤销
        	revoke(data);
        }
    });
	
	// 撤销
	function revoke(data){
		var msg = '确认撤销该借用申请吗？';
		layer.confirm(msg, { icon: 3, title: '撤销操作' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "licenceborrow010", params:{processInstanceId: data.processInstanceId}, type: 'json', callback: function(json){
    			if(json.returnCode == 0){
    				winui.window.msg("提交成功", {icon: 1, time: 2000});
    				loadBorrowTable();
    			}else{
    				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
    			}
    		}});
		});
	}
	
	// 编辑证照借用申请
	function borrowEdit(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/licenceManageBorrow/licenceManageBorrowEdit.html", 
			title: "编辑证照借用申请",
			pageId: "licenceManageBorrowEdit",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1,time: 2000});
                	loadBorrowTable();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
                }
			}
		});
	}
	
	// 证照借用提交审批
	function subApproval(data){
		layer.confirm(systemLanguage["com.skyeye.approvalOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.approvalOperation"][languageType]}, function (index) {
			layer.close(index);
			activitiUtil.startProcess(sysActivitiModel["licenceManageBorrow"]["key"], function (approvalId) {
				var params = {
					rowId: data.id,
					approvalId: approvalId
				};
				AjaxPostUtil.request({url: flowableBasePath + "licenceborrow006", params: params, type: 'json', callback: function(json){
					if(json.returnCode == 0){
						winui.window.msg("提交成功", {icon: 1, time: 2000});
						loadBorrowTable();
					}else{
						winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
					}
				}});
			});
		});
	}
	
	// 证照借用作废
	function cancellation(data){
		var msg = '确认作废该条借用申请吗？';
		layer.confirm(msg, { icon: 3, title: '作废操作' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "licenceborrow007", params:{rowId: data.id}, type: 'json', callback: function(json){
    			if(json.returnCode == 0){
    				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1,time: 2000});
    				loadBorrowTable();
    			}else{
    				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
    			}
    		}});
		});
	}
	
	// 证照借用详情
	function borrowDedails(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/licenceManageBorrow/licenceManageBorrowDetails.html", 
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "licenceManageBorrowDetails",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
			}
		});
	}

	// 添加证照借用
	$("body").on("click", "#addBorrowBean", function(){
    	_openNewWindows({
			url: "../../tpl/licenceManageBorrow/licenceManageBorrowAdd.html", 
			title: "证照借用申请",
			pageId: "licenceManageBorrowAdd",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1,time: 2000});
                	loadBorrowTable();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
                }
			}});
    });

	// 搜索表单
	form.render();
	form.on('submit(formSearch)', function (data) {
		if (winui.verifyForm(data.elem)) {
			table.reload("borrowTable", {page: {curr: 1}, where: getTableParams()});
		}
		return false;
	});

	// 刷新证照借用列表
    $("body").on("click", "#reloadBorrowTable", function(){
    	loadBorrowTable();
    });
    
    // 刷新证照借用列表数据
    function loadBorrowTable(){
    	table.reload("borrowTable", {where: getTableParams()});
    }
    
    function getTableParams(){
    	var startTime = "", endTime = "";
		if(!isNull($("#createTime").val())){
    		startTime = $("#createTime").val().split('~')[0].trim() + ' 00:00:00';
    		endTime = $("#createTime").val().split('~')[1].trim() + ' 23:59:59';
    	}
    	return {
    		state: $("#state").val(),
    		startTime: startTime,
    		endTime: endTime
    	};
    }
    
    exports('licenceManageBorrowList', {});
});
