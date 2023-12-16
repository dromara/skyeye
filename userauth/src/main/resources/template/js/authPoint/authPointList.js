
var objectKey = "";
var objectId = "";

var rowId = "";

var parentId = "";

var parentType = "";
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'tableTreeDj', 'jquery', 'winui', 'form'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		form = layui.form,
		tableTree = layui.tableTreeDj;

	objectKey = GetUrlParam("objectKey");
	objectId = GetUrlParam("objectId");
	if (isNull(objectKey) || isNull(objectId)) {
		winui.window.msg("请传入适用对象信息", {icon: 2, time: 2000});
		return false;
	}
	
	tableTree.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: reqBasePath + 'queryAuthPointList',
	    where: getTableParams(),
		limit: 10000,
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
	        { field: 'name', title: '权限点名称', width: 250 },
	        { field: 'authMenu', title: '接口url', width: 300 },
			{ field: 'orderBy', title: '排序', align: 'center', width: 80 },
	        { field: 'menuNum', title: '权限点编号', align: 'center', width: 120 },
	        { field: 'useNum', title: '使用数量', align: 'center', width: 80 },
			{ field: 'typeName', title: '类型', align: 'center', width: 80 },
			{ field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], width: 120 },
			{ field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 },
			{ field: 'lastUpdateName', title: systemLanguage["com.skyeye.lastUpdateName"][languageType], align: 'left', width: 120 },
			{ field: 'lastUpdateTime', title: systemLanguage["com.skyeye.lastUpdateTime"][languageType], align: 'center', width: 150 },
	        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 200, toolbar: '#tableBar'}
	    ]],
		isPage: false,
	    done: function(json) {
	    	matchingLanguage();
			initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "请输入名称，编号", function () {
				tableTree.reload("messageTable", {page: {curr: 1}, where: getTableParams()});
			});
	    }
	}, {
		keyId: 'id',
		keyPid: 'parentId',
		title: 'name',
		defaultShow: true
	});

	tableTree.getTable().on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
		if (layEvent === 'del') { // 删除
			del(data, obj);
		} else if (layEvent === 'edit') { // 编辑
			edit(data);
		} else if (layEvent === 'addChild') { // 新增分组/新增数据权限
			addPage(data);
		}
    });
	
	// 删除
	function del(data, obj) {
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: reqBasePath + "deleteAuthPointById", params: {id: data.id}, type: 'json', method: 'DELETE', callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}
	
	// 编辑权限点
	function edit(data) {
		var url = '../../tpl/authPoint/authPointWrite.html?objectId=' + objectId + '&objectKey=' + objectKey;
		if (!isNull(data)) {
			url += '&parentType=' + data.type + '&id=' + data.id
		}
		_openNewWindows({
			url: url,
			title: systemLanguage["com.skyeye.editPageTitle"][languageType],
			pageId: "authPointEdit",
			area: ['500px', '300px'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	}
	
    // 新增权限点
    $("body").on("click", "#addBean", function() {
		addPage(null);
    });

	function addPage(data) {
		var url = '../../tpl/authPoint/authPointWrite.html?objectId=' + objectId + '&objectKey=' + objectKey;
		if (!isNull(data)) {
			url += '&parentType=' + data.type + '&parentId=' + data.id
		}
		_openNewWindows({
			url: url,
			title: systemLanguage["com.skyeye.addPageTitle"][languageType],
			pageId: "authPointAdd",
			area: ['500px', '300px'],
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
		return $.extend(true, {objectKey: objectKey, objectId: objectId}, initTableSearchUtil.getSearchValue("messageTable"));
	}

    exports('authPointList', {});
});
