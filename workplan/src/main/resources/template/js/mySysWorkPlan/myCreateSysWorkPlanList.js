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
		table = layui.table,
		laydate = layui.laydate;

	laydate.render({
		elem: '#executeTime',
		range: '~'
	});

	// 获取我创建的任务计划列表
	table.render({
		id: 'messageTable',
		elem: '#messageTable',
		method: 'post',
		url: reqBasePath + 'sysworkplan015',
		where: getTableParams(),
		even: true,
		page: true,
		limits: getLimits(),
		limit: getLimit(),
		cols: [[
			{ title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
			{ field: 'title', title: '计划标题', width: 300, templet: function(d){
				return '<a lay-event="details" class="notice-title-click">' + d.title + '</a>';
			}},
			{ field: 'planType', title: '计划类型', align: 'center', width: 100, templet: function(d){
				if(d.planType == 1){
					return '个人计划';
				}else if(d.planType == 2){
					return '部门计划';
				}else if(d.planType == 3){
					return '公司计划';
				}
				return '';
			}},
			{ field: 'planCycle', title: '计划周期', align: 'center', width: 100, templet: function(d){
				if(d.planCycle == 1){
					return '日计划';
				}else if(d.planCycle == 2){
					return '周计划';
				}else if(d.planCycle == 3){
					return '月计划';
				}else if(d.planCycle == 4){
					return '季度计划';
				}else if(d.planCycle == 5){
					return '半年计划';
				}else if(d.planCycle == 6){
					return '年计划';
				}
				return '';
			}},
			{ field: 'startTime', title: '开始时间', align: 'center', width: 100 },
			{ field: 'endTime', title: '结束时间', align: 'center', width: 100 },
			{ field: 'whetherMail', title: '邮件通知', align: 'center', width: 100 },
			{ field: 'whetherNotice', title: '消息通知', align: 'center', width: 100 },
			{ field: 'whetherTime', title: '定时通知', align: 'center', width: 100 },
			{ field: 'whetherNotify', title: '通知状态', align: 'center', width: 100 },
			{ field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 100 },
			{ title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 240, toolbar: '#tableBar'}
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
		}else if (layEvent === 'del') { //删除
			del(data, obj);
		}else if (layEvent === 'timingSend') { //定时发送
			timingSend(data);
		}else if (layEvent === 'cancleTiming') { //取消定时发送
			cancleTiming(data);
		}else if (layEvent === 'details'){ // 详情
			details(data);
		}
	});

	// 定时发送
	function timingSend(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/sysworkplan/sysworkplantiming.html",
			title: "定时发送",
			pageId: "sysworkplantiming",
			area: ['40vw', '60vh'],
			callBack: function(refreshCode){
				if (refreshCode == '0') {
					winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
					loadTable();
				} else if (refreshCode == '-9999') {
					winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2, time: 2000});
				}
			}});
	}

	// 详情
	function details(data){
		rowId = data.id;
		executorId = data.executorId;
		_openNewWindows({
			url: "../../tpl/sysworkplan/sysworkplandetails.html",
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "sysworkplantiming",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
			}});
	}

	// 编辑
	function edit(data){
		rowId = data.id;
		var title = "";
		var url = "";
		if(data.planType === '1' || data.planType == 1){//个人计划
			title = "编辑个人计划";
			url = "../../tpl/sysworkplan/sysworkplanedit.html";
		}else if(data.planType === '2' || data.planType == 2){//部门计划
			title = "编辑部门计划";
			url = "../../tpl/sysworkplan/sysworkplandepedit.html";
		}else if(data.planType === '3' || data.planType == 3){//公司计划
			title = "编辑公司计划";
			url = "../../tpl/sysworkplan/sysworkplancomedit.html";
		}
		_openNewWindows({
			url: url,
			title: title,
			pageId: "sysworkplancomedit",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
				if (refreshCode == '0') {
					winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
					loadTable();
				} else if (refreshCode == '-9999') {
					winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2, time: 2000});
				}
			}});
	}

	// 取消定时发送
	function cancleTiming(data){
		layer.confirm("确定取消定时发送吗？", { icon: 3, title: '取消定时发送' }, function (index) {
			layer.close(index);
			AjaxPostUtil.request({url:reqBasePath + "sysworkplan005", params:{planId: data.id}, type: 'json', callback: function(json){
				if (json.returnCode == 0) {
					winui.window.msg("已取消定时发送", {icon: 1, time: 2000});
					loadTable();
				} else {
					winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
				}
			}});
		});
	}

	// 删除
	function del(data, obj){
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
			layer.close(index);
			AjaxPostUtil.request({url:reqBasePath + "sysworkplan006", params:{planId: data.id}, type: 'json', callback: function(json){
				if (json.returnCode == 0) {
					winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
					loadTable();
				} else {
					winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
				}
			}});
		});
	}

	form.render();
	form.on('submit(formSearch)', function (data) {
		if (winui.verifyForm(data.elem)) {
			refreshTable();
		}
		return false;
	});

	$("body").on("click", "#reloadTable", function(){
		loadTable();
	});

	function refreshTable(){
		table.reload("messageTable", {page: {curr: 1}, where: getTableParams()});
	}

	function loadTable(){
		table.reload("messageTable", {where: getTableParams()});
	}

	function getTableParams(){
		var startTime = "";
		var endTime = "";
		if(isNull($("#executeTime").val())){
			startTime = "";
			endTime = "";
		} else {
			startTime = $("#executeTime").val().split('~')[0].trim() + " 00:00:00";
			endTime = $("#executeTime").val().split('~')[1].trim() + " 23:59:59";
		}
		return {
			title: $("#title").val(),
			planType: $("#planType").val(),
			planCycle: $("#planCycle").val(),
			startTime: startTime,
			endTime: endTime
		};
	}

	exports('myCreateSysWorkPlanList', {});
});