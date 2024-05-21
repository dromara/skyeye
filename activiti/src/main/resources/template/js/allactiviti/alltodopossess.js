
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

	// 所有待办
	table.render({
	    id: 'messageAllConductTable',
	    elem: '#messageAllConductTable',
	    method: 'post',
	    url: flowableBasePath + 'activitimode019',
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
				return d.processMation.createName;
			}},
			{ field: 'createTime', title: '申请时间', align: 'center', width: 150, templet: function (d) {
				return d.processMation.createTime;
			}},
			{ field: 'taskName', title: '当前节点', width: 130, templet: function (d) {
				return '[' + d.taskName + ']';
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
	        { field: 'suspended', title: '状态', align: 'center', width: 130, templet: function (d) {
				if (d.suspended) {
					return "<span class='state-down'>挂起</span>";
				} else {
					return "<span class='state-up'>正常</span>";
				}
	        }},
	        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 100, toolbar: '#allConductTableBar'}
	    ]],
	    done: function(json) {
	    	matchingLanguage();
			initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "请输入流程ID", function () {
				table.reloadData("messageAllConductTable", {page: {curr: 1}, where: getTableParams()});
			});
			// 该方法用于解决,使用fixed固定列后,行高和其他列不一致的问题
			$(".layui-table-main tr").each(function (index, val) {
				$($(".layui-table-fixed .layui-table-body tbody tr")[index]).height($(val).height());
			});
	    }
	});
	
	table.on('tool(messageAllConductTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'hangUp') { //挂起
        	hangUp(data, obj);
        } else if (layEvent === 'activation') { //激活
        	activation(data, obj);
        } else if (layEvent === 'details') { //详情
			activitiUtil.activitiDetails(data);
        }
    });
    
	// 挂起
	function hangUp(data, obj){
		var msg = '确认挂起该流程吗？';
		layer.confirm(msg, { icon: 3, title: '挂起' }, function (index) {
			layer.close(index);
	        AjaxPostUtil.request({url: flowableBasePath + "activitimode020", params: {processInstanceId: data.processInstanceId}, type: 'json', callback: function (json) {
				winui.window.msg("该流程已挂起", {icon: 1, time: 2000});
				loadTable();
			}});
		});
	}
	
	// 激活
	function activation(data, obj){
		var msg = '确认激活该流程吗？';
		layer.confirm(msg, { icon: 3, title: '激活' }, function (index) {
			layer.close(index);
	        AjaxPostUtil.request({url: flowableBasePath + "activitimode021", params: {processInstanceId: data.processInstanceId}, type: 'json', callback: function (json) {
				winui.window.msg("该流程已激活", {icon: 1, time: 2000});
				loadTable();
			}});
		});
	}
	
	form.render();
	$("body").on("click", "#reloadAllConductTable", function() {
		loadTable();
	});

	function loadTable() {
		table.reloadData("messageAllConductTable", {where: getTableParams()});
	}

	function getTableParams() {
		return $.extend(true, {}, initTableSearchUtil.getSearchValue("messageAllConductTable"));
	}
    
    exports('alltodopossess', {});
});
