
var objectKey = "";
var objectId = "";

layui.config({
	base: basePath,
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'form'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		form = layui.form,
		table = layui.table;
	objectKey = GetUrlParam("objectKey");
	objectId = GetUrlParam("objectId");
	if (isNull(objectKey) || isNull(objectId)) {
		winui.window.msg("请传入适用对象信息", {icon: 2, time: 2000});
		return false;
	}

	var authPermission = teamObjectPermissionUtil.checkTeamBusinessAuthPermission(objectId, 'taskAuthEnum');
	var btnStr = `<div style="" class="type-group" id="temp">`;
	var firstBtn = true;
	if (authPermission['list']) {
		var defaultClassName = firstBtn ? 'plan-select' : '';
		firstBtn = false;
		btnStr += `<button type="button" class="layui-btn layui-btn-primary type-btn ${defaultClassName}" data-type="list" table-id="messageTable"><i class="layui-icon"></i>所有任务</button>`
	}
	if (authPermission['myExecute']) {
		var defaultClassName = firstBtn ? 'plan-select' : '';
		firstBtn = false;
		btnStr += `<button type="button" class="layui-btn layui-btn-primary type-btn ${defaultClassName}" data-type="${myExecute}" table-id="messageTable"><i class="layui-icon"></i>我执行的任务</button>`
	}
	if (authPermission['myCreate']) {
		var defaultClassName = firstBtn ? 'plan-select' : '';
		btnStr += `<button type="button" class="layui-btn layui-btn-primary type-btn ${defaultClassName}" data-type="myCreate" table-id="messageTable"><i class="layui-icon"></i>我创建的任务</button>`
	}
	btnStr += `</div>`;
	$(".winui-toolbar").before(btnStr);

	table.render({
		id: 'messageTable',
		elem: '#messageTable',
		method: 'post',
		url: sysMainMation.crmBasePath + 'queryCrmOpportunityList',
		where: getTableParams(),
		even: true,
		page: true,
		limits: getLimits(),
		limit: getLimit(),
		cols: [[
			{ title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
			{ field: 'name', title: '任务名称', width: 200, templet: function (d) {
					return '<a lay-event="details" class="notice-title-click">' + d.name + '</a>';
				}},
			{ field: 'oddNumber', title: '任务编号', align: 'left', width: 120 },
			{ field: 'startTime', title: '开始时间', align: 'center', width: 100 },
			{ field: 'endTime', title: '结束时间', align: 'center', width: 100 },
			{ field: 'estimatedWorkload', title: '预计工作量', align: 'center', width: 120 },
			{ field: 'actualWorkload', title: '实际工作量', align: 'center', width: 120 },
			{ field: 'processInstanceId', title: '流程ID', align: 'center', width: 100, templet: function (d) {
				return '<a lay-event="processDetails" class="notice-title-click">' + d.processInstanceId + '</a>';
			}},
			{ field: 'state', title: '状态', width: 90, templet: function (d) {
				return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("taskStateEnum", 'id', d.state, 'name');
			}},
			{ field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], align: 'left', width: 120 },
			{ field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 },
			{ field: 'lastUpdateName', title: systemLanguage["com.skyeye.lastUpdateName"][languageType], align: 'left', width: 120 },
			{ field: 'lastUpdateTime', title: systemLanguage["com.skyeye.lastUpdateTime"][languageType], align: 'center', width: 150 },
			{ title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 250, templet: function (d) {
				var str = '';
				if (d.editRow == 1) {
					if (authPermission['submitToApproval']) {
						str += '<a class="layui-btn layui-btn-xs" lay-event="subApproval">提交审批</a>';
					}
					if (authPermission['edit']) {
						str += '<a class="layui-btn layui-btn-xs layui-btn-normal" lay-event="edit"><language showName="com.skyeye.editBtn"></language></a>';
					}
					if (authPermission['delete']) {
						str += '<a class="layui-btn layui-btn-xs layui-btn-danger" lay-event="del">删除</a>';
					}
					if (authPermission['invalid']) {
						str += '<a class="layui-btn layui-btn-xs layui-btn-danger" lay-event="cancellation">作废</a>';
					}
				}
				if (d.editRow == 2 && d.state == 'inExamine') {
					if (authPermission['revoke']) {
						str += '<a class="layui-btn layui-btn-xs layui-btn-danger" lay-event="revoke">撤销</a>';
					}
				}
				if (d.state == 'pass' && authPermission['executing']) {
					str += '<a class="layui-btn layui-btn-xs" lay-event="stateChange">执行</a>';
				}
				if (d.state == 'executing' && authPermission['completed']) {
					str += '<a class="layui-btn layui-btn-xs" lay-event="stateChange">完成</a>';
				}
				if (d.state == 'completed' && authPermission['close']) {
					str += '<a class="layui-btn layui-btn-xs" lay-event="stateChange">关闭</a>';
				}
				return str;
			}}
		]],
		done: function(json) {
			matchingLanguage();
			initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "请输入商机名称", function () {
				table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
			});
		}
	});

	tableTree.getTable().on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'details'){ //详情
        	details(data);
        } else if (layEvent === 'edit') { //编辑
        	edit(data);
        } else if (layEvent === 'del'){ //删除
        	del(data, obj);
        } else if (layEvent === 'subApproval') { //提交审批
        	subApproval(data, obj);
        } else if (layEvent === 'processDetails') {//流程详情
			activitiUtil.activitiDetails(data);
        } else if (layEvent === 'executionBegin') {//开始执行
        	executionBegin(data, obj);
        } else if (layEvent === 'revoke') {//撤销任务审批申请
        	revoke(data);
        } else if (layEvent === 'taskSplit') {//拆分任务
        	taskSplit(data);
        } else if (layEvent === 'cancellation') {//作废
        	cancellation(data, obj);
        } else if (layEvent === 'executionOver') {//执行完成
        	executionOver(data);
        } else if (layEvent === 'executionClose') {//任务关闭
        	executionClose(data, obj);
        }
    });

	// 添加
	$("body").on("click", "#addBean", function() {
		isSplitTask = false;
		_openNewWindows({
			url: "../../tpl/protask/protaskadd.html",
			title: "新增任务",
			pageId: "protaskadd",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	});

	// 执行完成
	function executionOver(data) {
		_openNewWindows({
			url: "../../tpl/protask/protaskexecution.html",
			title: '执行信息',
			pageId: "protaskexecution",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	}

	// 详情
	function details(data) {
		_openNewWindows({
			url: "../../tpl/protask/protaskdetails.html",
			title: "任务详情",
			pageId: "protaskdetails",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
			}});
	}

	// 编辑
	function edit(data) {
		_openNewWindows({
			url: "../../tpl/protask/protaskedit.html",
			title: "编辑任务",
			pageId: "protaskedit",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}
		});
	}

	// 开始执行
	function executionBegin(data, obj){
		var msg = obj ? '确认开始执行【' + obj.data.taskName + '】吗？' : '确认开始执行该任务吗？';
		layer.confirm(msg, { icon: 3, title: '任务开始执行' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: sysMainMation.projectBasePath + "protask012", params: {id: data.id}, type: 'json', method: 'POST', callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}
	
	// 任务关闭
	function executionClose(data, obj){
		var msg = obj ? '确认关闭【' + obj.data.taskName + '】吗？' : '确认关闭该任务吗？';
		layer.confirm(msg, { icon: 3, title: '关闭任务' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: sysMainMation.projectBasePath + "protask014", params: {id: data.id}, type: 'json', method: 'POST', callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}
	
	// 撤销任务审批申请
	function revoke(data) {
		var msg = '确认从工作流中撤销选中数据吗？';
		layer.confirm(msg, { icon: 3, title: '撤销任务审批申请' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: sysMainMation.projectBasePath + "protask007", params: {processInstanceId: data.processInstanceId}, type: 'json', method: 'POST', callback: function (json) {
				winui.window.msg("提交成功", {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}

	// 任务提交审批
	function subApproval(data, obj){
		layer.confirm(systemLanguage["com.skyeye.approvalOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.approvalOperation"][languageType]}, function (index) {
			layer.close(index);
			activitiUtil.startProcess(data.serviceClassName, null, function (approvalId) {
				var params = {
					id: data.id,
					approvalId: approvalId
				};
				AjaxPostUtil.request({url: sysMainMation.projectBasePath + "protask008", params: params, type: 'json', method: 'POST', callback: function (json) {
					winui.window.msg("提交成功", {icon: 1, time: 2000});
					loadTable();
				}});
			});
		});
	}
	
	// 作废
	function cancellation(data, obj){
		var msg = obj ? '确认作废【' + obj.data.taskName + '】吗？' : '确认作废该任务信息吗？';
		layer.confirm(msg, { icon: 3, title: '任务作废' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: sysMainMation.projectBasePath + "protask009", params: {id: data.id}, type: 'json', method: 'POST', callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}
	
	// 删除
	function del(data, obj) {
		var msg = obj ? '确认删除【' + obj.data.taskName + '】吗？' : '确认删除选中数据吗？';
		layer.confirm(msg, {icon: 3, title: '删除任务'}, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: sysMainMation.projectBasePath + "protask006", params: {id: data.id}, type: 'json', method: 'POST', callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				loadTable();
    		}});
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
		return $.extend(true, {objectKey: objectKey, objectId: objectId}, initTableSearchUtil.getSearchValue("messageTable"));
	}

    exports('proTaskList', {});
});
