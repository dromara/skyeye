var rowId = "";

var objectKey = "";
var objectId = "";

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
	objectKey = GetUrlParam("objectKey");
	objectId = GetUrlParam("objectId");
	if (isNull(objectKey) || isNull(objectId)) {
		winui.window.msg("请传入适用对象信息", {icon: 2, time: 2000});
		return false;
	}

	var authPermission = teamObjectPermissionUtil.checkTeamBusinessAuthPermission(objectId, 'contactsAuthEnum');

	authBtn('1596375844035');
	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: reqBasePath + 'queryContactsList',
	    where: getTableParams(),
	    even: true,
	    page: true,
	    limits: getLimits(),
	    limit: getLimit(),
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
	        { field: 'name', title: '名称', align: 'left', width: 100 },
	        { field: 'department', title: '部门', align: 'left', width: 100 },
	        { field: 'job', title: '职位', align: 'left', width: 100 },
	        { field: 'workPhone', title: '办公电话', align: 'left', width: 100 },
	        { field: 'mobilePhone', title: '移动电话', align: 'center', width: 100 },
	        { field: 'email', title: '邮箱', align: 'left', width: 140 },
	        { field: 'qq', title: 'QQ', align: 'left', width: 100 },
	        { field: 'wechat', title: '微信', align: 'left', width: 100 },
	        { field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], align: 'left', width: 120 },
	        { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 },
	        { field: 'lastUpdateName', title: systemLanguage["com.skyeye.lastUpdateName"][languageType], align: 'left', width: 120 },
			{ field: 'lastUpdateTime', title: systemLanguage["com.skyeye.lastUpdateTime"][languageType], align: 'center', width: 150 },
	        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 150, templet: function (d) {
				var str = '';
				if (authPermission['edit']) {
					str += '<a class="layui-btn layui-btn-xs" lay-event="edit"><language showName="com.skyeye.editBtn"></language></a>'
				}
				if (authPermission['delete']) {
					str += '<a class="layui-btn layui-btn-xs layui-btn-danger" lay-event="delete"><language showName="com.skyeye.deleteBtn"></language></a>'
				}
				return str;
			}}
	    ]],
	    done: function(json) {
	    	matchingLanguage();
			initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "请输入名称，移动电话", function () {
				table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
			});
	    }
	});
	
	table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
		if (layEvent === 'edit') {
			edit(data);
		} else if (layEvent === 'delete') {
			del(data);
		}
    });
    
    
	// 新增
	$("body").on("click", "#addBean", function() {
    	_openNewWindows({
			url: "../../tpl/contacts/contactsAdd.html",
			title: systemLanguage["com.skyeye.addPageTitle"][languageType],
			pageId: "contactsAdd",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
    });
	
	// 编辑
	function edit(data) {
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/contacts/contactsEdit.html",
			title: systemLanguage["com.skyeye.editPageTitle"][languageType],
			pageId: "contactsEdit",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	}
	
	// 删除
	function del(data, obj) {
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: reqBasePath + "deleteContactsMationById", params: {id: data.id}, type: 'json', method: 'DELETE', callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
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
		return $.extend(true, {objectKey: objectKey, objectId: objectId}, initTableSearchUtil.getSearchValue("messageTable"));
	}
	
    exports('contactsList', {});
});