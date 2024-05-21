
var taskId = "";//任务id

var taskType = "";//流程类型

var processInstanceId = "";//流程id

// 待我审批
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		table = layui.table,
		form = layui.form;

	// 待我审批
	table.render({
	    id: 'messageMyNeedDealtTable',
	    elem: '#messageMyNeedDealtTable',
	    method: 'post',
	    url: flowableBasePath + 'activitimode008',
	    where: getTableParams(),
	    even: true,
	    page: true,
		limits: getLimits(),
		limit: getLimit(),
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
	        { field: 'processInstanceId', title: '流程ID', width: 280, templet: function (d) {
				return '<a lay-event="details" class="notice-title-click">' + getNotUndefinedVal(d.processInstanceId) + '</a>';
			}},
	        { field: 'taskType', title: '类型', width: 150, templet: function (d) {
				return getNotUndefinedVal(d.processMation?.title);
			}},
	        { field: 'createName', title: '申请人', width: 120, templet: function (d) {
				return getNotUndefinedVal(d.processMation?.createName);
			}},
	        { field: 'createTime', title: '申请时间', align: 'center', width: 150, templet: function (d) {
				return getNotUndefinedVal(d.processMation?.createTime);
			}},
	        { field: 'taskName', title: '当前节点', width: 130, templet: function (d) {
	        	return '[' + d.taskName + ']';
	        }},
	        { field: 'suspended', title: '状态<i id="stateDesc" class="fa fa-question-circle" style="margin-left: 5px"></i>', align: 'center', width: 100, templet: function (d) {
	        	if (d.suspended) {
	        		return "<span class='state-down'>挂起</span>";
	        	} else {
	        		return "<span class='state-up'>正常</span>";
	        	}
	        }},
	        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 100, toolbar: '#myNeedDealtTableBar'}
	    ]],
	    done: function(json) {
	    	matchingLanguage();
			initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "请输入流程ID", function () {
				table.reloadData("messageMyNeedDealtTable", {page: {curr: 1}, where: getTableParams()});
			});
			// 该方法用于解决,使用fixed固定列后,行高和其他列不一致的问题
			$(".layui-table-main tr").each(function (index, val) {
				$($(".layui-table-fixed .layui-table-body tbody tr")[index]).height($(val).height());
			});
	    }
	});
	
	table.on('tool(messageMyNeedDealtTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'subTasks') { //提交任务
        	subTasks(data, obj);
        } else if (layEvent === 'details') { //详情
			activitiUtil.activitiDetails(data);
        }
    });

	// 提交任务
	function subTasks(data, obj){
		taskId = data.taskId;
		taskType = data.taskType;
		processInstanceId = data.processInstanceId;
		_openNewWindows({
			url: "../../tpl/approvalActiviti/approvalProcessTask.html",
			title: "流程审批",
			pageId: "approvalProcess",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg("提交成功", {icon: 1, time: 2000});
				loadTable();
			}});
	}

	form.render();
	$("body").on("click", "#reloadMyNeedDealtTable", function() {
		loadTable();
	});

	function loadTable() {
		table.reloadData("messageMyNeedDealtTable", {where: getTableParams()});
	}

	function getTableParams() {
		return $.extend(true, {}, initTableSearchUtil.getSearchValue("messageMyNeedDealtTable"));
	}
    
    exports('pendingProcess', {});
});
