
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

	// 所有已完成流程
	table.render({
	    id: 'messageAllComplateTable',
	    elem: '#messageAllComplateTable',
	    method: 'post',
	    url: flowableBasePath + 'activitimode018',
	    where: getTableParams(),
	    even: true,
	    page: true,
		limits: getLimits(),
		limit: getLimit(),
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
			{ field: 'processInstanceId', title: '流程ID', width: 280, templet: function (d) {
				return '<a lay-event="details" class="notice-title-click">' + getNotUndefinedVal(d.historicProcessInstance?.processInstanceId) + '</a>';
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
			{ field: 'lastUpdateTime', title: '完成时间', align: 'center', width: 150, templet: function (d) {
				if (!isNull(d.historicProcessInstance?.endTime)) {
					var str = d.historicProcessInstance?.endTime.toString();
					str = str.substring(0, str.length - 3);
					return date('Y-m-d H:i:s', str);
				} else {
					return "";
				}
			}}
	    ]],
	    done: function(json) {
	    	matchingLanguage();
			initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "请输入流程ID", function () {
				table.reloadData("messageAllComplateTable", {page: {curr: 1}, where: getTableParams()});
			});
	    }
	});
	
	table.on('tool(messageAllComplateTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'details') { //详情
			data.processInstanceId = data.historicProcessInstance?.processInstanceId;
			activitiUtil.activitiDetails(data);
        }
    });

	form.render();
	$("body").on("click", "#loadAllComplateTable", function() {
		loadTable();
	});

	function loadTable() {
		table.reloadData("messageAllComplateTable", {where: getTableParams()});
	}

	function getTableParams() {
		return $.extend(true, {}, initTableSearchUtil.getSearchValue("messageAllComplateTable"));
	}
    
    exports('allcomplatepossess', {});
});
