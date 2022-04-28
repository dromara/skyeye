
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
	
	// 归还新增
	authBtn('1596972093939');
	
	laydate.render({
		elem: '#revertTime',
		range: '~'
	});
	
	// 证照归还列表
	table.render({
		id: 'revertTable',
		elem: '#revertTable',
		method: 'post',
		url: flowableBasePath + 'licencerevert001',
		where: getTableParams(),
		even: true,
		page: true,
		limits: getLimits(),
		limit: getLimit(),
		cols: [[
			{ title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
			{ field: 'title', title: '标题', width: 300, templet: function(d){
				return '<a lay-event="revertdedails" class="notice-title-click">' + d.title + '</a>';
			}},
			{ field: 'oddNum', title: '单号', width: 200, align: 'center' },
			{ field: 'processInstanceId', title: '流程ID', width: 80, align: 'center', templet: function(d){
				if(!isNull(d.processInstanceId)){
					return '<a lay-event="revertProcessDetails" class="notice-title-click">' + d.processInstanceId + '</a>';
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
			{ title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 250, toolbar: '#revertTableBar'}
		]],
		done: function(){
			matchingLanguage();
		}
	});

	// 证照归还的操作事件
	table.on('tool(revertTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'revertdedails') { //归还详情
        	revertDedails(data);
        }else if (layEvent === 'revertProcessDetails') { //流程详情
			activitiUtil.activitiDetails(data);
        }else if (layEvent === 'revertedit') { //编辑归还申请
        	revertEdit(data);
        }else if (layEvent === 'revertsubapproval') { //提交审批
        	revertSubApproval(data);
        }else if(layEvent === 'revertcancellation') {//归还作废
        	revertCancellation(data);
        }else if(layEvent === 'revertrevoke') {//撤销
        	revertrevoke(data);
        }
    });
	
	// 撤销
	function revertrevoke(data){
		var msg = '确认撤销该归还申请吗？';
		layer.confirm(msg, { icon: 3, title: '撤销操作' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "licencerevert010", params:{processInstanceId: data.processInstanceId}, type: 'json', method: "PUT", callback: function(json){
    			if(json.returnCode == 0){
    				winui.window.msg("提交成功", {icon: 1, time: 2000});
    				loadRevertTable();
    			}else{
    				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
    			}
    		}});
		});
	}
	
	// 编辑证照归还申请
	function revertEdit(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/licenceManageRevert/licenceManageRevertEdit.html", 
			title: "编辑证照归还申请",
			pageId: "licenceManageRevertEdit",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1,time: 2000});
                	loadRevertTable();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
                }
			}
		});
	}
	
	// 证照归还提交审批
	function revertSubApproval(data){
		layer.confirm(systemLanguage["com.skyeye.approvalOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.approvalOperation"][languageType]}, function (index) {
			layer.close(index);
			activitiUtil.startProcess(sysActivitiModel["licenceManageRevert"]["key"], function (approvalId) {
				var params = {
					rowId: data.id,
					approvalId: approvalId
				};
				AjaxPostUtil.request({url: flowableBasePath + "licencerevert006", params: params, type: 'json', callback: function(json){
					if(json.returnCode == 0){
						winui.window.msg("提交成功", {icon: 1, time: 2000});
						loadRevertTable();
					}else{
						winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
					}
				}});
			});
		});
	}
	
	// 证照归还作废
	function revertCancellation(data){
		var msg = '确认作废该条归还申请吗？';
		layer.confirm(msg, { icon: 3, title: '作废操作' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "licencerevert007", params:{rowId: data.id}, type: 'json', callback: function(json){
    			if(json.returnCode == 0){
    				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1,time: 2000});
    				loadRevertTable();
    			}else{
    				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
    			}
    		}});
		});
	}
	
	// 证照归还详情
	function revertDedails(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/licenceManageRevert/licenceManageRevertDetails.html", 
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "licenceManageRevertDetails",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
			}
		});
	}

	// 添加证照归还
	$("body").on("click", "#addRevertBean", function(){
    	_openNewWindows({
			url: "../../tpl/licenceManageRevert/licenceManageRevertAdd.html", 
			title: "证照归还申请",
			pageId: "licenceManageRevertAdd",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1,time: 2000});
                	loadRevertTable();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
                }
			}});
    });

	// 搜索表单
	form.render();
	form.on('submit(formSearch)', function (data) {
		if (winui.verifyForm(data.elem)) {
			table.reload("revertTable", {page: {curr: 1}, where: getTableParams()});
		}
		return false;
	});

	// 刷新证照归还列表
    $("body").on("click", "#reloadRevertTable", function(){
    	loadRevertTable();
    });
    
    // 刷新证照归还列表数据
    function loadRevertTable(){
    	table.reload("revertTable", {where: getTableParams()});
    }
    
    function getTableParams(){
    	var startTime = "", endTime = "";
		if(!isNull($("#revertTime").val())){
    		startTime = $("#revertTime").val().split('~')[0].trim() + ' 00:00:00';
    		endTime = $("#revertTime").val().split('~')[1].trim() + ' 23:59:59';
    	}
    	return {
    		state: $("#revertstate").val(),
    		startTime: startTime,
    		endTime: endTime
    	};
    }
    
    exports('licenceManageRevertList', {});
});
