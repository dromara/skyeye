
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

	authBtn('1656731571539');

	table.render({
		id: 'messageTable',
		elem: '#messageTable',
		method: 'post',
		url: reqBasePath + 'queryTeamTemplate',
		where: getTableParams(),
		even: true,
		page: true,
		limits: getLimits(),
		limit: getLimit(),
		cols: [[
			{ title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
			{ field: 'name', title: '名称', align: 'left', width: 200, templet: function (d) {
				return '<a lay-event="select" class="notice-title-click">' + d.name + '</a>';
			}},
			{ field: 'objectType', title: '适用类型', width: 150, templet: function (d) {
				return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("teamObjectType", 'id', d.objectType, 'name');
			}},
			{ field: 'enabled', title: '状态', align: 'center', width: 100, templet: function (d) {
				return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("commonEnable", 'id', d.enabled, 'name');
			}},
			{ field: 'isUsed', title: '使用状态', align: 'center', width: 100, templet: function (d) {
				return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("isUsed", 'id', d.isUsed, 'name');
			}},
			{ field: 'remark', title: '备注', width: 300 },
			{ field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], width: 120 },
			{ field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 },
			{ field: 'lastUpdateName', title: systemLanguage["com.skyeye.lastUpdateName"][languageType], align: 'left', width: 120 },
			{ field: 'lastUpdateTime', title: systemLanguage["com.skyeye.lastUpdateTime"][languageType], align: 'center', width: 150 },
			{ title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 180, toolbar: '#tableBar' }
		]],
		done: function(json) {
			matchingLanguage();
			initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "请输入名称，编码", function () {
				table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
			});
		}
	});

	table.on('tool(messageTable)', function (obj) {
		var data = obj.data;
		var layEvent = obj.event;
		if (layEvent === 'del') { // 删除
			del(data, obj);
		} else if (layEvent === 'edit') { // 编辑
			edit(data);
		} else if (layEvent === 'select') { // 详情
			select(data);
		}
	});

	// 删除
	function del(data, obj) {
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function (index) {
			layer.close(index);
			AjaxPostUtil.request({url: reqBasePath + "deleteTeamTemplateById", params: {id: data.id}, type: 'json', method: "DELETE", callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
		});
	}

	// 编辑
	function edit(data) {
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/teamTemplate/teamTemplateEdit.html",
			title: systemLanguage["com.skyeye.editPageTitle"][languageType],
			pageId: "teamTemplateEdit",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	}

	// 详情
	function select(data) {
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/teamTemplate/teamTemplateDetails.html",
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "teamTemplateDetails",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
			}});
	}

	// 新增
	$("body").on("click", "#addBean", function() {
		_openNewWindows({
			url: "../../tpl/teamTemplate/teamTemplateAdd.html",
			title: systemLanguage["com.skyeye.addPageTitle"][languageType],
			pageId: "teamTemplateAdd",
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

	exports('teamTemplateList', {});
});
