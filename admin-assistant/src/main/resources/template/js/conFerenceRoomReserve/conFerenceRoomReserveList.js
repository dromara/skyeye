
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
	
	// 新增预定
	authBtn('1596947245794');

	laydate.render({
		elem: '#createTime',
		range: '~'
	});
	
	// 会议室预定列表
	table.render({
	    id: 'reserveTable',
	    elem: '#reserveTable',
	    method: 'post',
	    url: reqBasePath + 'conferenceroomreserve001',
	    where: getTableParams(),
	    even: true,
	    page: true,
	    limits: getLimits(),
    	limit: getLimit(),
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
	        { field: 'title', title: '标题', width: 300, templet: function(d){
        		return '<a lay-event="reserveDedails" class="notice-title-click">' + d.title + '</a>';
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
	        { field: 'createTime', title: '创建时间', width: 150, align: 'center'},
	        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 250, toolbar: '#reserveTableBar'}
	    ]],
	    done: function(){
	    	matchingLanguage();
	    }
	});
	
	// 会议室预定的操作事件
	table.on('tool(reserveTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'reserveDedails') { //预定详情
        	reserveDedails(data);
        }else if (layEvent === 'processDetails') { //流程详情
			activitiUtil.activitiDetails(data);
        }else if (layEvent === 'reserveedit') { //编辑预定申请
        	reserveEdit(data);
        }else if (layEvent === 'subApproval') { //提交审批
        	subApproval(data);
        }else if(layEvent === 'cancellation') {//预定作废
        	cancellation(data);
        }else if (layEvent === 'revoke') { //撤销
        	revoke(data);
        }
    });
	
	// 撤销
	function revoke(data){
		var msg = '确认撤销该预定申请吗？';
		layer.confirm(msg, { icon: 3, title: '撤销操作' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url:reqBasePath + "conferenceroomreserve010", params:{processInstanceId: data.processInstanceId}, type:'json', callback:function(json){
    			if(json.returnCode == 0){
    				winui.window.msg("提交成功", {icon: 1, time: 2000});
    				loadReserveTable();
    			}else{
    				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
    			}
    		}});
		});
	}
	
	// 编辑会议室预定申请
	function reserveEdit(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/conFerenceRoomReserve/conFerenceRoomReserveEdit.html", 
			title: "会议室预定申请",
			pageId: "conFerenceRoomReserveEdit",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1,time: 2000});
                	loadReserveTable();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
                }
			}
		});
	}
	
	// 会议室预定提交审批
	function subApproval(data){
		layer.confirm(systemLanguage["com.skyeye.approvalOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.approvalOperation"][languageType]}, function (index) {
			layer.close(index);
			activitiUtil.startProcess(sysActivitiModel["conFerenceRoomReserve"]["key"], function (approvalId) {
				var params = {
					rowId: data.id,
					approvalId: approvalId
				};
				AjaxPostUtil.request({url: reqBasePath + "conferenceroomreserve006", params: params, type: 'json', callback: function(json){
					if(json.returnCode == 0){
						winui.window.msg("提交成功", {icon: 1, time: 2000});
						loadReserveTable();
					}else{
						winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
					}
				}});
			});
		});
	}
	
	// 会议室预定作废
	function cancellation(data){
		var msg = '确认作废该条预定申请吗？';
		layer.confirm(msg, { icon: 3, title: '申请作废操作' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url:reqBasePath + "conferenceroomreserve007", params:{rowId: data.id}, type:'json', callback:function(json){
    			if(json.returnCode == 0){
    				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1,time: 2000});
    				loadReserveTable();
    			}else{
    				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
    			}
    		}});
		});
	}
	
	// 会议室预定详情
	function reserveDedails(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/conFerenceRoomReserve/conFerenceRoomReserveDetails.html", 
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "conFerenceRoomReserveDetails",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
			}
		});
	}

	// 添加会议室预定
	$("body").on("click", "#addReserveBean", function(){
    	_openNewWindows({
			url: "../../tpl/conFerenceRoomReserve/conFerenceRoomReserveAdd.html", 
			title: "会议室预定申请",
			pageId: "addreserve",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1,time: 2000});
                	loadReserveTable();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
                }
			}});
    });

	// 搜索表单
	form.render();
	form.on('submit(formSearch)', function (data) {
		if (winui.verifyForm(data.elem)) {
			table.reload("reserveTable", {page: {curr: 1}, where: getTableParams()});
		}
		return false;
	});

	// 刷新会议室预定列表
    $("body").on("click", "#reloadReserveTable", function(){
    	loadReserveTable();
    });
    
    // 刷新会议室预定列表数据
    function loadReserveTable(){
    	table.reload("reserveTable", {where: getTableParams()});
    }
    
    function getTableParams(){
    	var startTime = "", endTime = "";
		if(!isNull($("#createTime").val())){
    		startTime = $("#createTime").val().split('~')[0].trim() + ' 00:00:00';
    		endTime = $("#createTime").val().split('~')[1].trim() + ' 23:59:59';
    	}
    	return {
    		approvalState: $("#approvalState").val(),
    		startTime: startTime,
    		endTime: endTime
    	};
    }
    
    exports('conFerenceRoomReserveList', {});
});
