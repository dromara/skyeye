
var objectKey = "";
var objectId = "";
var holderId = "";

var parentId = "";

layui.config({
	base: basePath,
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'form', 'tableTreeDj', 'fsTree'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		form = layui.form,
		fsTree = layui.fsTree,
		tableTree = layui.tableTreeDj;
	objectKey = GetUrlParam("objectKey");
	objectId = GetUrlParam("objectId");
	if (isNull(objectKey) || isNull(objectId)) {
		winui.window.msg("请传入适用对象信息", {icon: 2, time: 2000});
		return false;
	}

	var authPermission = teamObjectPermissionUtil.checkTeamBusinessAuthPermission(objectId, 'taskAuthEnum');
	var btnStr = `<div style="" class="type-group" id="type">`;
	var firstBtn = true;
	if (authPermission['list']) {
		var defaultClassName = firstBtn ? 'plan-select' : '';
		firstBtn = false;
		btnStr += `<button type="button" class="layui-btn layui-btn-primary type-btn ${defaultClassName}" data-type="list" table-id="messageTable"><i class="layui-icon"></i>所有任务</button>`
	}
	if (authPermission['myExecute']) {
		var defaultClassName = firstBtn ? 'plan-select' : '';
		firstBtn = false;
		btnStr += `<button type="button" class="layui-btn layui-btn-primary type-btn ${defaultClassName}" data-type="myExecute" table-id="messageTable"><i class="layui-icon"></i>我执行的任务</button>`
	}
	if (authPermission['myCreate']) {
		var defaultClassName = firstBtn ? 'plan-select' : '';
		btnStr += `<button type="button" class="layui-btn layui-btn-primary type-btn ${defaultClassName}" data-type="myCreate" table-id="messageTable"><i class="layui-icon"></i>我创建的任务</button>`
	}
	btnStr += `</div>`;
	$(".winui-toolbar").before(btnStr);

	/********* tree 处理   start *************/
	fsTree.render({
		id: "treeDemo",
		url: sysMainMation.projectBasePath + 'queryAllApprovalMilestoneList?objectId=' + objectId,
		checkEnable: false,
		showLine: false,
		showIcon: true,
		addDiyDom: ztreeUtil.addDiyDom,
		clickCallback: onClickTree,
		onDblClick: onClickTree
	}, function(id) {
		fuzzySearch(id, '#name', null, true);
		initLoadTable();
		ztreeUtil.initEventListener(id);
	});

	//异步加载的方法
	function onClickTree(event, treeId, treeNode) {
		if(treeNode == undefined) {
			holderId = "";
		} else {
			holderId = treeNode.id;
		}
		loadTable();
	}
	function initLoadTable() {
		tableTree.render({
			id: 'messageTable',
			elem: '#messageTable',
			method: 'post',
			url: sysMainMation.projectBasePath + 'queryProTaskList',
			where: getTableParams(),
			even: true,
			page: true,
			limits: getLimits(),
			limit: getLimit(),
			cols: [[
				{ title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
				{ field: 'oddNumber', title: '任务单号', width: 200, align: 'center', templet: function (d) {
					return '<a lay-event="details" class="notice-title-click">' + d.oddNumber + '</a>';
				}},
				{ field: 'name', title: '任务名称', width: 200 },
				{ field: 'milestoneId', title: '里程碑', align: 'center', width: 100, templet: function (d) {
					return getNotUndefinedVal(d.milestoneMation?.name);
				}},
				{ field: 'startTime', title: '开始时间', align: 'center', width: 100 },
				{ field: 'endTime', title: '结束时间', align: 'center', width: 100 },
				{ field: 'estimatedWorkload', title: '预计工作量', align: 'center', width: 120 },
				{ field: 'actualWorkload', title: '实际工作量', align: 'center', width: 120 },
				{ field: 'processInstanceId', title: '流程ID', align: 'center', width: 100, templet: function (d) {
					return '<a lay-event="processDetails" class="notice-title-click">' + getNotUndefinedVal(d.processInstanceId) + '</a>';
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
					str += '<a class="layui-btn layui-btn-xs layui-btn-normal" lay-event="add">新增子任务</a>';
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
						str += '<a class="layui-btn layui-btn-xs" lay-event="executionBegin">执行</a>';
					}
					if (d.state == 'executing' && authPermission['completed']) {
						str += '<a class="layui-btn layui-btn-xs" lay-event="executionOver">完成</a>';
					}
					if (d.state == 'completed' && authPermission['close']) {
						str += '<a class="layui-btn layui-btn-xs" lay-event="executionClose">关闭</a>';
					}
					return str;
				}}
			]],
			done: function(json) {
				matchingLanguage();
				initTableSearchUtil.initAdvancedSearch($("#messageTable")[0], json.searchFilter, form, "请输入任务名称", function () {
					tableTree.reload("messageTable", {page: {curr: 1}, where: getTableParams()});
				});
			}
		}, {
			keyId: 'id',
			keyPid: 'parentId',
			title: 'name',
		});

		tableTree.getTable().on('tool(messageTable)', function (obj) {
			var data = obj.data;
			var layEvent = obj.event;
			if (layEvent === 'details'){ //新增子任务
				addNew(data);
			} else if (layEvent === 'details'){ //详情
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
			} else if (layEvent === 'cancellation') {//作废
				cancellation(data, obj);
			} else if (layEvent === 'executionOver') {//执行完成
				executionOver(data);
			} else if (layEvent === 'executionClose') {//任务关闭
				executionClose(data, obj);
			}
		});
	}

	// 添加
	$("body").on("click", "#addBean", function() {
		parentId = "";
		_openNewWindows({
			url: systemCommonUtil.getUrl('FP2023080500001&objectId=' + objectId + '&objectKey=' + objectKey + "&callbackFun=savePreParams", null),
			title: "新增任务",
			pageId: "protaskadd",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	});

	// 新增子任务
	function addNew(data) {
		parentId = data.id;
		_openNewWindows({
			url: systemCommonUtil.getUrl('FP2023080500001&objectId=' + objectId + '&objectKey=' + objectKey + "&callbackFun=savePreParams", null),
			title: '新增子任务',
			pageId: "protaskadd",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	}

	// 执行完成
	function executionOver(data) {
		_openNewWindows({
			url: systemCommonUtil.getUrl('FP2023080500005&objectId=' + objectId + '&objectKey=' + objectKey + '&id=' + data.id, null),
			title: '完成任务',
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
			url: systemCommonUtil.getUrl('FP2023080500003&objectId=' + objectId + '&objectKey=' + objectKey + '&id=' + data.id, null),
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
			url: systemCommonUtil.getUrl('FP2023080500002&objectId=' + objectId + '&objectKey=' + objectKey + '&id=' + data.id + "&callbackFun=savePreParams", null),
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
		var msg = obj ? '确认开始执行【' + obj.data.name + '】吗？' : '确认开始执行该任务吗？';
		layer.confirm(msg, { icon: 3, title: '任务开始执行' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: sysMainMation.projectBasePath + "executionTask", params: {id: data.id}, type: 'json', method: 'POST', callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}
	
	// 任务关闭
	function executionClose(data, obj){
		var msg = obj ? '确认关闭【' + obj.data.name + '】吗？' : '确认关闭该任务吗？';
		layer.confirm(msg, { icon: 3, title: '关闭任务' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: sysMainMation.projectBasePath + "closeTask", params: {id: data.id}, type: 'json', method: 'POST', callback: function (json) {
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
            AjaxPostUtil.request({url: sysMainMation.projectBasePath + "revokeTask", params: {processInstanceId: data.processInstanceId}, type: 'json', method: 'PUT', callback: function (json) {
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
				AjaxPostUtil.request({url: sysMainMation.projectBasePath + "submitToApprovalTask", params: params, type: 'json', method: 'POST', callback: function (json) {
					winui.window.msg("提交成功", {icon: 1, time: 2000});
					loadTable();
				}});
			});
		});
	}
	
	// 作废
	function cancellation(data, obj){
		var msg = obj ? '确认作废【' + obj.data.name + '】吗？' : '确认作废该任务信息吗？';
		layer.confirm(msg, { icon: 3, title: '任务作废' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: sysMainMation.projectBasePath + "invalidTask", params: {id: data.id}, type: 'json', method: 'POST', callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}
	
	// 删除
	function del(data, obj) {
		var msg = obj ? '确认删除【' + obj.data.name + '】吗？' : '确认删除选中数据吗？';
		layer.confirm(msg, {icon: 3, title: '删除任务'}, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: sysMainMation.projectBasePath + "deleteProTaskById", params: {id: data.id}, type: 'json', method: 'POST', callback: function (json) {
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
		tableTree.reload("messageTable", {where: getTableParams()});
	}

	function getTableParams() {
		let params = {
			objectKey: objectKey,
			objectId: objectId,
			holderId: holderId == '0' ? '' : holderId
		};
		return $.extend(true, params, initTableSearchUtil.getSearchValue("messageTable"));
	}

    exports('proTaskList', {});
});

// 写入页面保存之前的回调函数
function savePreParams(params) {
	params.parentId = parentId
}
