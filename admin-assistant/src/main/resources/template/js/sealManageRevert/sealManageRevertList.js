
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
	
	// 印章归还
	authBtn('1596962824460');
	
	laydate.render({elem: '#revertTime', range: '~'});
	
	// 印章归还列表
	table.render({
		id: 'revertTable',
		elem: '#revertTable',
		method: 'post',
		url: flowableBasePath + 'sealrevert001',
		where: getTableParams(),
		even: true,
		page: true,
		limits: getLimits(),
		limit: getLimit(),
		cols: [[
			{ title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
			{ field: 'title', title: '标题', width: 300, templet: function (d) {
				return '<a lay-event="revertdedails" class="notice-title-click">' + d.title + '</a>';
			}},
			{ field: 'oddNum', title: '单号', width: 200, align: 'center' },
			{ field: 'processInstanceId', title: '流程ID', width: 80, align: 'center', templet: function (d) {
				if (!isNull(d.processInstanceId)) {
					return '<a lay-event="revertProcessDetails" class="notice-title-click">' + d.processInstanceId + '</a>';
				} else {
					return "";
				}
			}},
			{ field: 'stateName', title: '状态', width: 90, align: 'center', templet: function (d) {
				return activitiUtil.showStateName2(d.state, 1);
			}},
			{ field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], width: 150, align: 'center'},
			{ title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 250, toolbar: '#revertTableBar'}
		]],
		done: function(json) {
			matchingLanguage();
		}
	});

	// 印章归还的操作事件
	table.on('tool(revertTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'revertdedails') { //归还详情
        	revertDedails(data);
        } else if (layEvent === 'revertProcessDetails') { //流程详情
			activitiUtil.activitiDetails(data);
        } else if (layEvent === 'revertedit') { //编辑归还申请
        	revertEdit(data);
        } else if (layEvent === 'revertsubapproval') { //提交审批
        	revertSubApproval(data);
        } else if (layEvent === 'revertcancellation') {//归还作废
        	revertCancellation(data);
        } else if (layEvent === 'revertrevoke') {//撤销
        	revertrevoke(data);
        }
    });
	
	// 撤销
	function revertrevoke(data) {
		var msg = '确认撤销该归还申请吗？';
		layer.confirm(msg, { icon: 3, title: '撤销操作' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "sealrevert010", params: {processInstanceId: data.processInstanceId}, type: 'json', method: "PUT", callback: function (json) {
				winui.window.msg("提交成功", {icon: 1, time: 2000});
				loadRevertTable();
    		}});
		});
	}
	
	// 编辑印章归还申请
	function revertEdit(data) {
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/sealManageRevert/sealManageRevertEdit.html", 
			title: "编辑印章归还申请",
			pageId: "sealManageRevertEdit",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadRevertTable();
			}
		});
	}

	// 印章归还提交审批
	function revertSubApproval(data) {
		layer.confirm(systemLanguage["com.skyeye.approvalOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.approvalOperation"][languageType]}, function (index) {
			layer.close(index);
			activitiUtil.startProcess(sysActivitiModel["sealManageRevert"]["key"], null, function (approvalId) {
				var params = {
					rowId: data.id,
					approvalId: approvalId
				};
				AjaxPostUtil.request({url: flowableBasePath + "sealrevert006", params: params, type: 'json', callback: function (json) {
					winui.window.msg("提交成功", {icon: 1, time: 2000});
					loadRevertTable();
				}});
			});
		});
	}
	
	// 印章归还作废
	function revertCancellation(data) {
		var msg = '确认作废该条归还申请吗？';
		layer.confirm(msg, { icon: 3, title: '作废操作' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "sealrevert007", params: {rowId: data.id}, type: 'json', callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadRevertTable();
    		}});
		});
	}
	
	// 印章归还详情
	function revertDedails(data) {
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/sealManageRevert/sealManageRevertDetails.html", 
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "sealManageRevertDetails",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
			}
		});
	}
	
	// 添加印章归还
	$("body").on("click", "#addRevertBean", function() {
    	_openNewWindows({
			url: "../../tpl/sealManageRevert/sealManageRevertAdd.html", 
			title: "印章归还申请",
			pageId: "sealManageRevertAdd",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadRevertTable();
			}});
    });

	// 搜索表单
	form.render();
	form.on('submit(formSearch)', function (data) {
		if (winui.verifyForm(data.elem)) {
			table.reloadData("revertTable", {page: {curr: 1}, where: getTableParams()})
		}
		return false;
	});

	// 刷新印章归还列表
    $("body").on("click", "#reloadRevertTable", function() {
    	loadRevertTable();
    });
    
    // 刷新印章归还列表数据
    function loadRevertTable(){
    	table.reloadData("revertTable", {where: getTableParams()});
    }
    
    function getTableParams() {
    	var startTime = "", endTime = "";
		if (!isNull($("#revertTime").val())) {
    		startTime = $("#revertTime").val().split('~')[0].trim() + ' 00:00:00';
    		endTime = $("#revertTime").val().split('~')[1].trim() + ' 23:59:59';
    	}
    	return {
    		state: $("#revertstate").val(),
    		startTime: startTime,
    		endTime: endTime
    	};
    }
    
    exports('sealManageRevertList', {});
});
