
var rowId = "";

var parentNode = null;

layui.config({
	base: basePath,
	version: skyeyeVersion
}).extend({
	window: 'js/winui.window'
}).define(['window', 'tableTreeDj', 'jquery', 'winui', 'form', 'fsTree'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		form = layui.form,
		fsTree = layui.fsTree,
		tableTree = layui.tableTreeDj;
	var dictTypeId = '';

	authBtn('1656776769966');

	/********* tree 处理   start *************/
	fsTree.render({
		id: "treeDemo",
		url: reqBasePath + "queryDictTypeListByEnabled?enabled=1",
		clickCallback: onClickTree,
		onDblClick: onClickTree
	}, function(id) {
		fuzzySearch(id, '#name', null, true);
		initLoadTable();
	});

	//异步加载的方法
	function onClickTree(event, treeId, treeNode) {
		if(treeNode == undefined) {
			dictTypeId = "";
		} else {
			dictTypeId = treeNode.id;
		}
		loadTable();
	}
	/********* tree 处理   end *************/
	function initLoadTable() {
		tableTree.render({
			id: 'messageTable',
			elem: '#messageTable',
			method: 'post',
			url: reqBasePath + 'queryDictDataList',
			where: getTableParams(),
			cols: [[
				{ title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
				{ field: 'dictName', title: '名称', width: 200 },
				{ field: 'dictSort', title: '排序', width: 60 },
				{ field: 'isDefault', title: '默认值', align: 'center', width: 80, templet: function (d) {
					return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("commonIsDefault", 'id', d.isDefault, 'name');
				}},
				{ field: 'enabled', title: '状态', align: 'center', width: 80, templet: function (d) {
					return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("commonEnable", 'id', d.enabled, 'name');
				}},
				{ field: 'remark', title: '备注', width: 200 },
				{ field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], width: 120 },
				{ field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 },
				{ field: 'lastUpdateName', title: systemLanguage["com.skyeye.lastUpdateName"][languageType], align: 'left', width: 120 },
				{ field: 'lastUpdateTime', title: systemLanguage["com.skyeye.lastUpdateTime"][languageType], align: 'center', width: 150 },
				{ title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 240, toolbar: '#tableBar' }
			]],
			isPage: false,
			done: function(json) {
				matchingLanguage();
				initTableSearchUtil.initAdvancedSearch($("#messageTable")[0], json.searchFilter, form, "请输入名称", function () {
					tableTree.reload("messageTable", {page: {curr: 1}, where: getTableParams()});
				});
			}
		}, {
			keyId: 'id',
			keyPid: 'parentId',
			title: 'dictName',
		});
	}

	tableTree.getTable().on('tool(messageTable)', function (obj) {
		var data = obj.data;
		var layEvent = obj.event;
		if (layEvent === 'del') { // 删除
			del(data, obj);
		} else if (layEvent === 'edit') { // 编辑
			edit(data);
		} else if (layEvent === 'add') { // 新增子节点
			parentNode = data;
			addPage();
		} else if (layEvent === 'move') { // 移动
			move(data);
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

	// 移动
	function move(data) {
		parentNode = data;
		_openNewWindows({
			url: "../../tpl/sysDictData/sysDictDataMove.html",
			title: '移动',
			pageId: "sysDictDataMove",
			area: ['50vw', '40vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	}

	// 新增
	$("body").on("click", "#addBean", function() {
		parentNode = null;
		addPage();
	});

	function addPage() {
		_openNewWindows({
			url: "../../tpl/sysDictData/sysDictDataAdd.html",
			title: systemLanguage["com.skyeye.addPageTitle"][languageType],
			pageId: "sysDictDataAdd",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	}

	form.render();
	$("body").on("click", "#reloadTable", function() {
		loadTable();
	});

	function loadTable() {
		tableTree.reload("messageTable", {where: getTableParams()});
	}

	function getTableParams() {
		return $.extend(true, {dictTypeId: dictTypeId}, initTableSearchUtil.getSearchValue("messageTable"));
	}

	exports('sysDictDataList', {});
});
