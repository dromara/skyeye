
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
	var serviceClassName = sysServiceMation["licenceManageBorrow"]["key"];
	
	authBtn('1596969413440');
    
	// 证照借用列表
	table.render({
		id: 'messageTable',
		elem: '#messageTable',
		method: 'post',
		url: sysMainMation.admBasePath + 'licenceborrow001',
		where: getTableParams(),
		even: true,
		page: true,
		limits: getLimits(),
		limit: getLimit(),
		cols: [[
			{ title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
			{ field: 'oddNumber', title: '单号', width: 200, align: 'center', templet: function (d) {
				return '<a lay-event="details" class="notice-title-click">' + d.oddNumber + '</a>';
			}},
			{ field: 'title', title: '标题', width: 300 },
			{ field: 'processInstanceId', title: '流程ID', width: 80, align: 'center', templet: function (d) {
				return '<a lay-event="processDetails" class="notice-title-click">' + getNotUndefinedVal(d.processInstanceId) + '</a>';
			}},
			{ field: 'state', title: '状态', width: 90, align: 'center', templet: function (d) {
				return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("flowableStateEnum", 'id', d.state, 'name');
			}},
			{ field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], width: 120 },
			{ field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 },
			{ field: 'lastUpdateName', title: systemLanguage["com.skyeye.lastUpdateName"][languageType], align: 'left', width: 120 },
			{ field: 'lastUpdateTime', title: systemLanguage["com.skyeye.lastUpdateTime"][languageType], align: 'center', width: 150 },
			{ title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 200, toolbar: '#messageTableBar' }
		]],
		done: function(json) {
			matchingLanguage();
			initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "请输入单号，标题", function () {
				table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
			});
		}
	});

	// 证照借用的操作事件
	table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'details') { //借用详情
			details(data);
        } else if (layEvent === 'processDetails') { //流程详情
			activitiUtil.activitiDetails(data);
        } else if (layEvent === 'edit') { //编辑借用申请
			edit(data);
        } else if (layEvent === 'subApproval') { //提交审批
        	subApproval(data);
        } else if (layEvent === 'cancellation') {//借用作废
        	cancellation(data);
        } else if (layEvent === 'revoke') {//撤销
        	revoke(data);
        }
    });
	
	// 撤销
	function revoke(data) {
		var msg = '确认撤销该借用申请吗？';
		layer.confirm(msg, { icon: 3, title: '撤销操作' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: sysMainMation.admBasePath + "licenceborrow010", params: {processInstanceId: data.processInstanceId}, type: 'json', method: "PUT", callback: function (json) {
				winui.window.msg("提交成功", {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}
	
	// 编辑证照借用申请
	function edit(data) {
		_openNewWindows({
			url: systemCommonUtil.getUrl('FP2023062500002&id=' + data.id, null),
			title: "编辑证照借用申请",
			pageId: "licenceManageBorrowEdit",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}
		});
	}
	
	// 证照借用提交审批
	function subApproval(data) {
		layer.confirm(systemLanguage["com.skyeye.approvalOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.approvalOperation"][languageType]}, function (index) {
			layer.close(index);
			activitiUtil.startProcess(serviceClassName, null, function (approvalId) {
				var params = {
					id: data.id,
					approvalId: approvalId
				};
				AjaxPostUtil.request({url: sysMainMation.admBasePath + "licenceborrow006", params: params, type: 'json', method: "POST", callback: function (json) {
					winui.window.msg("提交成功", {icon: 1, time: 2000});
					loadTable();
				}});
			});
		});
	}
	
	// 证照借用作废
	function cancellation(data) {
		var msg = '确认作废该条借用申请吗？';
		layer.confirm(msg, { icon: 3, title: '作废操作' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: sysMainMation.admBasePath + "licenceborrow007", params: {id: data.id}, type: 'json', method: "POST", callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}
	
	// 证照借用详情
	function details(data) {
		_openNewWindows({
			url: systemCommonUtil.getUrl('FP2023062500003&id=' + data.id, null),
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "licenceManageBorrowDetails",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
			}
		});
	}

	// 添加证照借用
	$("body").on("click", "#addBean", function() {
    	_openNewWindows({
			url: systemCommonUtil.getUrl('FP2023062500001', null),
			title: "证照借用申请",
			pageId: "licenceManageBorrowAdd",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
    });

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
    
    exports('licenceManageBorrowList', {});
});
