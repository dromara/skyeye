var rowId = "";

var taskType = "";//流程详情的主标题
var processInstanceId = "";//流程id

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
	
	// 新增资产归还申请
	authBtn('1597243733271');
	
	// '资产归还'页面的选取时间段表格
	laydate.render({elem: '#returnCreateTime', range: '~'});
	
	// 资产归还管理开始
	table.render({
		id: 'returnTable',
		elem: '#returnTable',
		method: 'post',
		url: flowableBasePath + 'asset025',
		where: getTableParams(),
		even: true,
		page: true,
		limits: getLimits(),
		limit: getLimit(),
		cols: [[
			{ title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
			{ field: 'title', title: '标题', width: 300, templet: function (d) {
				return '<a lay-event="returnDedails" class="notice-title-click">' + d.title + '</a>';
			}},
			{ field: 'oddNum', title: '单号', width: 200, align: 'center' },
			{ field: 'processInstanceId', title: '流程ID', width: 80, align: 'center', templet: function (d) {
				return '<a lay-event="returnProcessDetails" class="notice-title-click">' + d.processInstanceId + '</a>';
			}},
			{ field: 'stateName', title: '状态', width: 90, align: 'center', templet: function (d) {
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
			{ title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 257, toolbar: '#returnTableBar'}
		]],
		done: function(){
			matchingLanguage();
		}
	});

	// 资产归还的操作事件
	table.on('tool(returnTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'returnDedails') { //归还详情
        	returnDedails(data);
        }else if (layEvent === 'returnEdit') { //编辑归还申请
        	returnEdit(data);
        }else if (layEvent === 'returnSubApproval') { //归还提交审批
        	returnSubApproval(data);
        }else if(layEvent === 'returnCancellation') {//归还作废
        	returnCancellation(data);
        }else if(layEvent === 'returnProcessDetails') {//归还流程详情
			activitiUtil.activitiDetails(data);
        }else if(layEvent === 'returnRevoke') {//撤销归还申请
        	returnRevoke(data);
        }
    });
	
	// 资产归还详情
	function returnDedails(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/assetManageReturn/assetManageReturnDetails.html", 
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "assetManageReturnDetails",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
			}
		});
	}
	
	// 撤销资产归还
	function returnRevoke(data){
		var msg = '确认撤销该资产归还申请吗？';
		layer.confirm(msg, { icon: 3, title: '撤销操作' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "asset038", params:{processInstanceId: data.processInstanceId}, type: 'json', method: "PUT", callback: function (json) {
				winui.window.msg("提交成功", {icon: 1, time: 2000});
				loadReturnTable();
    		}});
		});
	}
	
	// 新增资产归还
	$("body").on("click", "#addReturnBean", function() {
    	_openNewWindows({
			url: "../../tpl/assetManageReturn/assetManageReturnAdd.html", 
			title: "资产归还申请",
			pageId: "assetManageReturnAdd",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadReturnTable();
			}});
    });
	
	// 编辑资产归还申请
	function returnEdit(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/assetManageReturn/assetManageReturnEdit.html", 
			title: "编辑资产归还申请",
			pageId: "assetManageReturnEdit",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadReturnTable();
			}
		});
	}
	
	// 资产归还作废
	function returnCancellation(data){
		var msg = '确认作废该条归还申请吗？';
		layer.confirm(msg, { icon: 3, title: '作废操作' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "asset030", params:{rowId: data.id}, type: 'json', callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadReturnTable();
    		}});
		});
	}
	
	// 资产归还提交审批
	function returnSubApproval(data){
		layer.confirm(systemLanguage["com.skyeye.approvalOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.approvalOperation"][languageType]}, function (index) {
			layer.close(index);
			activitiUtil.startProcess(sysActivitiModel["assetManageReturn"]["key"], function (approvalId) {
				var params = {
					rowId: data.id,
					approvalId: approvalId
				};
				AjaxPostUtil.request({url: flowableBasePath + "asset028", params: params, type: 'json', callback: function (json) {
					winui.window.msg("提交成功", {icon: 1, time: 2000});
					loadReturnTable();
				}});
			});
		});
	}

	// 搜索表单
	form.render();
	form.on('submit(formSearch)', function (data) {
		if (winui.verifyForm(data.elem)) {
			table.reload("returnTable", {page: {curr: 1}, where: getTableParams()});
		}
		return false;
	});

	// 刷新归还数据
    $("body").on("click", "#reloadReturnTable", function() {
    	loadReturnTable();
    });
    
	// 刷新归还列表数据
    function loadReturnTable(){
    	table.reload("returnTable", {where: getTableParams()});
    }

    function getTableParams(){
    	var startTime = "", endTime = "";
		if(!isNull($("#returnCreateTime").val())){
    		startTime = $("#returnCreateTime").val().split('~')[0].trim() + ' 00:00:00';
    		endTime = $("#returnCreateTime").val().split('~')[1].trim() + ' 23:59:59';
    	}
    	return {
    		state: $("#returnState").val(),
    		startTime: startTime,
    		endTime: endTime
    	};
    }
    
    exports('assetManageReturnList', {});
});
