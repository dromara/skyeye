
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

	// 加载列表数据权限
	loadAuthBtnGroup('messageTable', '1574672406390');
	authBtn('1574672416396');//新增

	table.render({
		id: 'messageTable',
		elem: '#messageTable',
		method: 'post',
		url: sysMainMation.projectBasePath + 'queryProProjectList',
		where: getTableParams(),
		even: true,
		page: true,
		limits: getLimits(),
		limit: getLimit(),
		cols: [[
			{ title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers', rowspan: 2},
			{ field: 'oddNumber', title: '系统单号', width: 200, align: 'center', templet: function (d) {
				return '<a lay-event="details" class="notice-title-click">' + d.oddNumber + '</a>';
			}},
			{ field: 'numberCode', title: '项目单号', width: 200 },
			{ field: 'name', title: '项目名称', width: 300 },
			{ field: 'processInstanceId', title: '流程ID', width: 80, align: 'center', templet: function (d) {
				return '<a lay-event="processDetails" class="notice-title-click">' + d.processInstanceId + '</a>';
			}},
			{ field: 'state', title: '状态', width: 90, align: 'center', templet: function (d) {
				return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("projectStateEnum", 'id', d.state, 'name');
			}},
			{ field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], width: 120 },
			{ field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 },
			{ field: 'lastUpdateName', title: systemLanguage["com.skyeye.lastUpdateName"][languageType], align: 'left', width: 120 },
			{ field: 'lastUpdateTime', title: systemLanguage["com.skyeye.lastUpdateTime"][languageType], align: 'center', width: 150 },
			{ title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 250, toolbar: '#tableBar'}
		]],
		done: function(json) {
			matchingLanguage();
			initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "请输入单号，项目名称", function () {
				table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
			});
		}
	});
		
	table.on('tool(messageTable)', function (obj) {
		var data = obj.data;
		var layEvent = obj.event;
		if (layEvent === 'edit') { // 编辑
			edit(data);
		} else if (layEvent === 'details'){ // 详情
			details(data);
		} else if (layEvent === 'processDetails') { // 流程详情
			activitiUtil.activitiDetails(data);
		} else if (layEvent === 'subApproval') { // 提交审批
			subApproval(data);
		} else if (layEvent === 'del') { // 删除
			del(data);
		} else if (layEvent === 'revoke') { // 撤销
			revoke(data);
		} else if (layEvent === 'cancellation') { // 作废
			cancellation(data);
		} else if (layEvent === 'execute') { // 开始执行
			execute(data);
		} else if (layEvent === 'end') { // 成果和总结
			end(data);
		} else if (layEvent === 'giveService') { // 服务
			giveService(data);
		}
	});

	// 成果和总结
	function end(data) {
		_openNewWindows({
			url: systemCommonUtil.getUrl('FP2023080300005&id=' + data.id, null),
			title: "成果和总结",
			pageId: "proPerfectinForm",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	}

	// 新增
	$("body").on("click", "#addBean", function() {
		_openNewWindows({
			url: systemCommonUtil.getUrl('FP2023080300001', null),
			title: "新增项目",
			pageId: "projectAdd",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	});

	// 编辑
	function edit(data) {
		_openNewWindows({
			url: systemCommonUtil.getUrl('FP2023080300002&id=' + data.id, null),
			title: "编辑项目",
			pageId: "projectEdit",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	}

	// 服务
	function giveService(data) {
		_openNewWindows({
			url: systemCommonUtil.getUrl('../../tpl/project/projectManage.html?objectId=' + data.id + '&objectKey=' + data.serviceClassName, null),
			title: "服务",
			pageId: "projectGiveService",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	}

	// 详情
	function details(data) {
		_openNewWindows({
			url: systemCommonUtil.getUrl('FP2023080300003&id=' + data.id, null),
			title: "项目详情",
			pageId: "projectDetails",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	}

    // 删除
	function del(data) {
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: sysMainMation.projectBasePath + "proproject009", params: {id: data.id}, type: 'json', method: "DELETE", callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}
	
	// 撤销
	function revoke(data) {
		layer.confirm("确定撤销申请吗？", {icon: 3, title: '撤销操作'}, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: sysMainMation.projectBasePath + "proproject010", params: {processInstanceId: data.processInstanceId}, type: 'json', method: "PUT", callback: function (json) {
				winui.window.msg("撤销成功", {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}
	
	// 作废
	function cancellation(data) {
		layer.confirm("确定作废该项目吗？", {icon: 3, title: '作废操作'}, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: sysMainMation.projectBasePath + "proproject011", params: {id: data.id}, type: 'json', method: "POST", callback: function (json) {
				winui.window.msg("作废成功", {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}
	
	// 开始执行
	function execute(data) {
		layer.confirm("确定开始执行该项目吗？", {icon: 3, title: '执行操作'}, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: sysMainMation.projectBasePath + "proproject012", params: {id: data.id}, type: 'json', method: "POST", callback: function (json) {
				winui.window.msg("执行成功", {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}
	
	// 提交审批
	function subApproval(data) {
		layer.confirm(systemLanguage["com.skyeye.approvalOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.approvalOperation"][languageType]}, function (index) {
			layer.close(index);
			activitiUtil.startProcess(data.serviceClassName, null, function (approvalId) {
				var params = {
					id: data.id,
					approvalId: approvalId
				};
				AjaxPostUtil.request({url: sysMainMation.projectBasePath + "proproject008", params: params, type: 'json', method: "POST", callback: function (json) {
					winui.window.msg("提交成功", {icon: 1, time: 2000});
					loadTable();
				}});
			});
		});
	}

	form.render();
	$("body").on("click", "#reloadTable", function() {
		loadTable();
	});
	function loadTable() {
		table.reloadData("messageTable", {where: getTableParams()});
	}

	function getTableParams() {
		return $.extend(true, {}, initTableSearchUtil.getSearchValue("messageTable"));
	}

    exports('projectList', {});
});