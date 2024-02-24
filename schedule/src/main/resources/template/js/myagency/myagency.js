
var rowId = "";

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
	
	table.render({
		id: 'messageTable',
		elem: '#messageTable',
		method: 'post',
		url: sysMainMation.scheduleBasePath + 'myagency001',
		where: getTableParams(),
		even: true,
		page: true,
		limits: getLimits(),
		limit: getLimit(),
		cols: [[
			{ title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
			{ field: 'name', title: '待办名称', width: 120 },
			{ field: 'startTime', title: '开始时间', align: 'center', width: 150 },
			{ field: 'endTime', title: '结束时间', align: 'center', width: 150 },
			{ field: 'type', title: '日程类型', width: 100, templet: function (d) {
				return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("checkDayType", 'id', d.type, 'name');
			}},
			{ field: 'remindTime', title: '提醒时间', align: 'center', width: 180 },
			{ title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 100, toolbar: '#tableBar'}
		]],
		done: function(json) {
			matchingLanguage();
			initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "请输入名称", function () {
				table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
			});
		}
	});

	table.on('tool(messageTable)', function (obj) {
		var data = obj.data;
		var layEvent = obj.event;
		if (layEvent === 'cancleAgency') { // 取消代办
			cancleAgency(data);
		}
	});

	// 取消代办
	function cancleAgency(data, obj){
		var msg = obj ? '确认删除【' + obj.data.name + '】的日程吗？' : '确认删除日程吗？';
		layer.confirm(msg, { icon: 3, title: '删除日程' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: sysMainMation.scheduleBasePath + "syseveschedule007", params: {id: data.id}, type: 'json', method: 'DELETE', callback: function (json) {
				winui.window.msg("删除成功", {icon: 1, time: 2000});
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
		return $.extend(true, {}, initTableSearchUtil.getSearchValue("messageTable"));
	}
    
    exports('myagency', {});
});
