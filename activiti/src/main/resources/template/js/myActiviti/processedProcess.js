
// 流程id
var processInstanceId = "";
// 历史审批任务id
var hisTaskId = "";

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
	
	// 我的历史审批任务
	table.render({
	    id: 'messageMyHistoryTaskTable',
	    elem: '#messageMyHistoryTaskTable',
	    method: 'post',
	    url: flowableBasePath + 'activitimode014',
		where: getTableParams(),
	    even: true,
	    page: true,
		limits: getLimits(),
		limit: getLimit(),
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], rowspan: '2', type: 'numbers' },
	        { field: 'processInstanceId', title: '流程ID', rowspan: '2', width: 280, templet: function (d) {
				return '<a lay-event="details" class="notice-title-click">' + getNotUndefinedVal(d.hisTask?.processInstanceId) + '</a>';
			}},
			{ field: 'taskType', title: '类型', rowspan: '2', width: 150, templet: function (d) {
				return getNotUndefinedVal(d.processMation?.title);
			}},
			{ field: 'createName', title: '申请人', rowspan: '2', width: 120, templet: function (d) {
				return getNotUndefinedVal(d.processMation?.createName);
			}},
			{ field: 'createTime', title: '申请时间', rowspan: '2', align: 'center', width: 150, templet: function (d) {
				return getNotUndefinedVal(d.processMation?.createTime);
			}},
			{ field: 'assigneeList', title: '当前审批人', align: 'left', rowspan: '2', width: 150, templet: function (d) {
				if (!isNull(d.assigneeList)) {
					var str = "";
					$.each(d.assigneeList, function(i, item) {
						str += '<span class="layui-badge layui-bg-blue">' + item.name + '</span><br>';
					});
					return str;
				}
				return '';
			}},
			{ title: '我处理的', colspan: '2', align: 'center' },
	        { field: 'weatherEnd', title: '审批进度', align: 'left', rowspan: '2', width: 80, templet: function (d) {
	        	if (d.weatherEnd == 0) {
	        		return "<span class='state-down'>进行中</span>";
	        	} else {
	        		return "<span class='state-up'>已完成</span>";
	        	}
	        }},
	        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', rowspan: '2', align: 'center', width: 150, toolbar: '#myHistoryTaskTableBar'}
	    ], [
			{ field: 'hisTaskName', title: '节点', width: 130, templet: function (d) {
				return '[' + getNotUndefinedVal(d.hisTask?.name) + ']';
			}},
			{ field: 'lastUpdateTime', title: '处理时间', width: 130, templet: function (d) {
				if (!isNull(d.hisTask.endTime)) {
					var str = d.hisTask.endTime.toString();
					str = str.substring(0, str.length - 3);
					return date('Y-m-d H:i', str);
				} else {
					return "";
				}
			}}
		]],
	    done: function(json) {
	    	matchingLanguage();
			initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "请输入流程ID", function () {
				table.reloadData("messageMyHistoryTaskTable", {page: {curr: 1}, where: getTableParams()});
			});
			// 该方法用于解决,使用fixed固定列后,行高和其他列不一致的问题
			$(".layui-table-main tr").each(function (index, val) {
				$($(".layui-table-fixed .layui-table-body tbody tr")[index]).height($(val).height());
			});
	    }
	});
	
	table.on('tool(messageMyHistoryTaskTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'details') { //详情
			data.processInstanceId = data.hisTask?.processInstanceId;
			activitiUtil.activitiDetails(data);
        } else if (layEvent === 'withdraw') { //撤回
        	withdraw(data);
        } else if (layEvent === 'refreshPic') { //刷新流程图
        	refreshPic(data);
        }
    });

	// 撤回
	function withdraw(data) {
		processInstanceId = data.hisTask?.processInstanceId;
		hisTaskId = data.hisTask?.id;
		_openNewWindows({
			url: "../../tpl/activitiCommon/revokeActiviti.html",
			title: "撤回",
			pageId: "revokeActiviti",
			area: ['70vw', '40vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}
		});
	}
	
	// 刷新流程图
	function refreshPic(data) {
		layer.confirm('确认重新生成流程图吗？', { icon: 3, title: '刷新流程图操作' }, function (i) {
			layer.close(i);
            AjaxPostUtil.request({url: flowableBasePath + "activitimode027", params: {processInstanceId: data.hisTask?.processInstanceId}, type: 'json', callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
 	   		}});
		});
	}
	
	form.render();
	$("body").on("click", "#reloadMyHistoryTaskTable", function() {
		loadTable();
	});

	function loadTable() {
		table.reloadData("messageMyHistoryTaskTable", {where: getTableParams()});
	}

	function getTableParams() {
		return $.extend(true, {}, initTableSearchUtil.getSearchValue("messageMyHistoryTaskTable"));
	}
    
    exports('processedProcess', {});
});
