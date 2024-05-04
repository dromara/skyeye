
var objectKey = "";
var objectId = "";

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
	objectKey = GetUrlParam("objectKey");
	objectId = GetUrlParam("objectId");
	if (isNull(objectKey) || isNull(objectId)) {
		winui.window.msg("请传入适用对象信息", {icon: 2, time: 2000});
		return false;
	}

	var authPermission = teamObjectPermissionUtil.checkTeamBusinessAuthPermission(objectId, 'crmInvoiceAuthEnum');

	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: sysMainMation.crmBasePath + 'queryInvoiceList',
	    where: getTableParams(),
	    even: true,
	    page: true,
	    limits: getLimits(),
	    limit: getLimit(),
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
			{ field: 'oddNumber', title: '单据编号', align: 'center', width: 150, templet: function (d) {
				return '<a lay-event="details" class="notice-title-click">' + d.oddNumber + '</a>';
			}},
			{ field: 'price', title: '开票金额（元）', width: 120 },
			{ field: 'invoicTime', title: '开票日期', align: 'center', width: 100 },
			{ field: 'contractId', title: '合同', width: 200, templet: function (d) {
				return getNotUndefinedVal(d.contractMation?.title);
			}},
			{ field: 'typeId', title: '开票类型', width: 150, templet: function (d) {
				return sysDictDataUtil.getDictDataNameByCodeAndKey('INVOICE_TYPE', d.typeId);
			}},
			{ field: 'processInstanceId', title: '流程ID', align: 'center', width: 100, templet: function (d) {
				return '<a lay-event="processDetails" class="notice-title-click">' + getNotUndefinedVal(d.processInstanceId) + '</a>';
			}},
			{ field: 'invoiceHeaderId', title: '发票抬头', width: 120, templet: function (d) {
				return getNotUndefinedVal(d.invoiceHeaderMation?.name);
			}},
			{ field: 'state', title: '状态', width: 90, templet: function (d) {
				return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("flowableStateEnum", 'id', d.state, 'name');
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
				return str;
			}}
	    ]],
	    done: function(json) {
	    	matchingLanguage();
			initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "请输入单据编号", function () {
				table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
			});
	    }
	});
	
	table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
		if (layEvent === 'edit') { //编辑
			edit(data);
		} else if (layEvent === 'details'){ //详情
			details(data);
		} else if (layEvent === 'processDetails') { //流程详情
			activitiUtil.activitiDetails(data);
		} else if (layEvent === 'subApproval') { //提交审批
			subApproval(data);
		} else if (layEvent === 'del') { //删除
			del(data);
		} else if (layEvent === 'cancellation') { //作废
			cancellation(data);
		} else if (layEvent === 'revoke') { //撤销
			revoke(data);
		}
    });

	// 新增
	$("body").on("click", "#addBean", function() {
    	parent._openNewWindows({
			url: systemCommonUtil.getUrl('FP2024050300005&objectId=' + objectId + '&objectKey=' + objectKey, null),
			title: systemLanguage["com.skyeye.addPageTitle"][languageType],
			pageId: "crmContractAdd",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
    });
	
	// 编辑
	function edit(data) {
		parent._openNewWindows({
			url: systemCommonUtil.getUrl('FP2024050300006&objectId=' + objectId + '&objectKey=' + objectKey + '&id=' + data.id, null),
			title: systemLanguage["com.skyeye.editPageTitle"][languageType],
			pageId: "crmContractEdit",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	}

	// 详情
	function details(data) {
		parent._openNewWindows({
			url: systemCommonUtil.getUrl('FP2024050300007&objectId=' + objectId + '&objectKey=' + objectKey + '&id=' + data.id, null),
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "crmContractDetails",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
			}});
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
				AjaxPostUtil.request({url: sysMainMation.crmBasePath + "submitInvoiceToApproval", params: params, type: 'json', method: 'POST', callback: function (json) {
					winui.window.msg("提交成功", {icon: 1, time: 2000});
					loadTable();
				}});
			});
		});
	}

	// 删除
	function del(data, obj) {
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function (index) {
			layer.close(index);
			AjaxPostUtil.request({url: sysMainMation.crmBasePath + "deleteInvoiceById", params: {id: data.id}, type: 'json', method: 'DELETE', callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
		});
	}

	// 作废
	function cancellation(data, obj) {
		var msg = '确认作废该条数据吗？';
		layer.confirm(msg, { icon: 3, title: '作废操作' }, function (index) {
			layer.close(index);
			AjaxPostUtil.request({url: sysMainMation.crmBasePath + "invalidInvoice", params: {id: data.id}, type: 'json', method: 'POST', callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
		});
	}

	// 撤销
	function revoke(data) {
		var msg = '确认撤销该数据吗？';
		layer.confirm(msg, { icon: 3, title: '撤销申请提交' }, function (index) {
			layer.close(index);
			AjaxPostUtil.request({url: sysMainMation.crmBasePath + "revokeInvoice", params: {processInstanceId: data.processInstanceId}, type: 'json', method: 'PUT', callback: function (json) {
				winui.window.msg("提交成功", {icon: 1, time: 2000});
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
	
    exports('crmInvoiceList', {});
});