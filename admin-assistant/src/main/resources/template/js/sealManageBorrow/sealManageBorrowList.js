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
	var serviceClassName = sysServiceMation["sealManageBorrow"]["key"];
	
	// 印章借用
	authBtn('1596961728905');
    
	laydate.render({elem: '#createTime', range: '~'});
	
	// 印章借用列表
	table.render({
		id: 'borrowTable',
		elem: '#borrowTable',
		method: 'post',
		url: flowableBasePath + 'sealborrow001',
		where: getTableParams(),
		even: true,
		page: true,
		limits: getLimits(),
		limit: getLimit(),
		cols: [[
			{ title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
			{ field: 'title', title: '标题', width: 300, templet: function (d) {
				return '<a lay-event="borrowDedails" class="notice-title-click">' + d.title + '</a>';
			}},
			{ field: 'oddNum', title: '单号', width: 200, align: 'center' },
			{ field: 'processInstanceId', title: '流程ID', width: 80, align: 'center', templet: function (d) {
				if (!isNull(d.processInstanceId)) {
					return '<a lay-event="processDetails" class="notice-title-click">' + d.processInstanceId + '</a>';
				} else {
					return "";
				}
			}},
			{ field: 'stateName', title: '状态', width: 90, align: 'center', templet: function (d) {
				return activitiUtil.showStateName2(d.state, 1);
			}},
			{ field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], width: 150, align: 'center'},
			{ title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 250, toolbar: '#borrowTableBar'}
		]],
		done: function(json) {
			matchingLanguage();
		}
	});

	// 印章借用的操作事件
	table.on('tool(borrowTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'borrowDedails') { //借用详情
        	borrowDedails(data);
        } else if (layEvent === 'processDetails') { //流程详情
			activitiUtil.activitiDetails(data);
        } else if (layEvent === 'borrowedit') { //编辑借用申请
        	borrowEdit(data);
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
		layer.confirm(msg, { icon: 3, title: '撤销操作'}, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "sealborrow010", params: {processInstanceId: data.processInstanceId}, type: 'json', method: "PUT", callback: function (json) {
				winui.window.msg("提交成功", {icon: 1, time: 2000});
				loadBorrowTable();
    		}});
		});
	}
	
	// 编辑印章借用申请
	function borrowEdit(data) {
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/sealManageBorrow/sealManageBorrowEdit.html", 
			title: "编辑印章借用申请",
			pageId: "sealManageBorrowEdit",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadBorrowTable();
			}
		});
	}
	
	// 印章借用提交审批
	function subApproval(data) {
		layer.confirm(systemLanguage["com.skyeye.approvalOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.approvalOperation"][languageType]}, function (index) {
			layer.close(index);
			activitiUtil.startProcess(serviceClassName, null, function (approvalId) {
				var params = {
					rowId: data.id,
					approvalId: approvalId
				};
				AjaxPostUtil.request({url: flowableBasePath + "sealborrow006", params: params, type: 'json', callback: function (json) {
					winui.window.msg("提交成功", {icon: 1, time: 2000});
					loadBorrowTable();
				}});
			});
		});
	}
	
	// 印章借用作废
	function cancellation(data) {
		var msg = '确认作废该条借用申请吗？';
		layer.confirm(msg, { icon: 3, title: '作废操作' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "sealborrow007", params: {rowId: data.id}, type: 'json', callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadBorrowTable();
    		}});
		});
	}
	
	// 印章借用详情
	function borrowDedails(data) {
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/sealManageBorrow/sealManageBorrowDetails.html", 
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "sealManageBorrowDetails",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
			}
		});
	}
	
	// 添加印章借用
	$("body").on("click", "#addBorrowBean", function() {
    	_openNewWindows({
			url: "../../tpl/sealManageBorrow/sealManageBorrowAdd.html", 
			title: "印章借用申请",
			pageId: "sealManageBorrowAdd",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadBorrowTable();
			}});
    });

	form.render();
	form.on('submit(formSearch)', function (data) {
		if (winui.verifyForm(data.elem)) {
			table.reloadData("borrowTable", {page: {curr: 1}, where: getTableParams()});
		}
		return false;
	});

	// 刷新印章借用列表
    $("body").on("click", "#reloadBorrowTable", function() {
    	loadBorrowTable();
    });
    
    // 刷新印章借用列表数据
    function loadBorrowTable(){
    	table.reloadData("borrowTable", {where: getTableParams()});
    }
    
    function getTableParams() {
    	var startTime = "", endTime = "";
		if (!isNull($("#createTime").val())) {//一定要记得，当createTime为空时
    		startTime = $("#createTime").val().split('~')[0].trim() + ' 00:00:00';
    		endTime = $("#createTime").val().split('~')[1].trim() + ' 23:59:59';
    	}
    	return {
    		state: $("#state").val(),
    		startTime: startTime,
    		endTime: endTime
    	};
    }
    
    exports('sealManageBorrowList', {});
});
