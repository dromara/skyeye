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

	// 获取所有任务计划列表
	table.render({
		id: 'messageTable',
		elem: '#messageTable',
		method: 'post',
		url: sysMainMation.workplanBasePath + 'sysworkplan016',
		where: getTableParams(),
		even: true,
		page: true,
		limits: getLimits(),
		limit: getLimit(),
		cols: [[
			{ title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
			{ field: 'title', title: '计划标题', width: 300, templet: function (d) {
				return '<a lay-event="details" class="notice-title-click">' + d.title + '</a>';
			}},
			{ field: 'planType', title: '计划类型', align: 'center', width: 100, templet: function (d) {
				if(d.planType == 1){
					return '个人计划';
				}else if(d.planType == 2){
					return '部门计划';
				}else if(d.planType == 3){
					return '公司计划';
				}
				return '';
			}},
			{ field: 'planCycle', title: '计划周期', align: 'center', width: 100, templet: function (d) {
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
			{ field: 'userName', title: '计划下达人', width: 100 },
			{ field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 100 }
		]],
		done: function(){
			matchingLanguage();
		}
	});

	table.on('tool(messageTable)', function (obj) {
		var data = obj.data;
		var layEvent = obj.event;
		if (layEvent === 'details'){ // 详情
			details(data);
		}
	});

	// 详情
	function details(data) {
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/sysworkplan/sysworkplandetails.html",
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "sysworkplantiming",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
			}});
	}

	form.render();
	form.on('submit(formSearch)', function (data) {
		if (winui.verifyForm(data.elem)) {
			refreshTable();
		}
		return false;
	});

	$("body").on("click", "#reloadTable", function() {
		loadTable();
	});

	function refreshTable(){
		table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
	}

	function loadTable() {
		table.reloadData("messageTable", {where: getTableParams()});
	}

	function getTableParams() {
		var startTime = "";
		var endTime = "";
		if(isNull($("#executeTime").val())) {
			startTime = "";
			endTime = "";
		} else {
			startTime = $("#executeTime").val().split('~')[0].trim() + " 00:00:00";
			endTime = $("#executeTime").val().split('~')[1].trim() + " 23:59:59";
		}
		return {
			title: $("#title").val(),
			userName: $("#userName").val(),
			planType: $("#planType").val(),
			planCycle: $("#planCycle").val(),
			startTime: startTime,
			endTime: endTime
		};
	}

	exports('allSysWorkPlanList', {});
});