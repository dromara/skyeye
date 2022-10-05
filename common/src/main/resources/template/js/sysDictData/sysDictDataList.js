
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

	authBtn('1656776769966');

	table.render({
		id: 'messageTable',
		elem: '#messageTable',
		method: 'post',
		url: reqBasePath + 'queryDictDataList',
		where: getTableParams(),
		even: true,
		page: true,
		limits: getLimits(),
		limit: getLimit(),
		cols: [[
			{ title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
			{ field: 'dictName', title: '名称', width: 200 },
			{ field: 'dictTypeName', title: '所属分类', width: 200 },
			{ field: 'dictSort', title: '排序', width: 60 },
			{ field: 'isDefault', title: '默认值', align: 'center', width: 80, templet: function (d) {
				if (d.isDefault == 'Y') {
					return '是';
				} else {
					return '否';
				}
			}},
			{ field: 'status', title: '状态', align: 'center', width: 80, templet: function (d) {
				if (d.status == 0) {
					return "<span class='state-new'>启用</span>";
				} else if (d.status == 1) {
					return "<span class='state-down'>禁用</span>";
				}
			}},
			{ field: 'remark', title: '备注', width: 200 },
			{ field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], width: 120 },
			{ field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 },
			{ field: 'lastUpdateName', title: systemLanguage["com.skyeye.lastUpdateName"][languageType], align: 'left', width: 120 },
			{ field: 'lastUpdateTime', title: systemLanguage["com.skyeye.lastUpdateTime"][languageType], align: 'center', width: 150 },
			{ title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 180, toolbar: '#tableBar' }
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
		if (layEvent === 'del') { // 删除
			del(data, obj);
		} else if (layEvent === 'edit') { // 编辑
			edit(data);
		}
	});

	// 删除
	function del(data, obj) {
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function (index) {
			layer.close(index);
			AjaxPostUtil.request({url: reqBasePath + "deleteDictDataMationById", params: {id: data.id}, type: 'json', method: "DELETE", callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
		});
	}

	// 编辑
	function edit(data) {
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/sysDictData/sysDictDataEdit.html",
			title: systemLanguage["com.skyeye.editPageTitle"][languageType],
			pageId: "sysDictDataEdit",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	}

	// 新增
	$("body").on("click", "#addBean", function() {
		_openNewWindows({
			url: "../../tpl/sysDictData/sysDictDataAdd.html",
			title: systemLanguage["com.skyeye.addPageTitle"][languageType],
			pageId: "sysDictDataAdd",
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

	exports('sysDictDataList', {});
});
