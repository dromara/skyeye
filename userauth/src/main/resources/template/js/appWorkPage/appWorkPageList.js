
var rowId = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'form', 'table', 'contextMenu'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		form = layui.form,
		table = layui.table;
	
	authBtn('1563602200836');
	
	table.render({
		id: 'messageTable',
		elem: '#messageTable',
		method: 'post',
		url: reqBasePath + 'queryAppWorkPageList',
		where: getTableParams(),
		even: true,
		page: true,
		limits: getLimits(),
		limit: getLimit(),
		cols: [[
			{ title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
			{ field: 'title', title: '名称', align: 'left', width: 120 },
			{ field: 'logo', title: 'LOGO', width: 60, templet: function (d) {
				var str = '';
				if (!isNull(d.logo)){
					str = '<img src="' + fileBasePath + d.logo + '" class="photo-img" lay-event="iconPath">';
				}
				return str;
			}},
			{ field: 'type', title: '菜单类型', align: 'left', width: 100, align: 'center', templet: function (d) {
				if (d.type == '1') {
					return "目录";
				} else if (d.type == '2') {
					return "页面";
				}
			}},
			{ field: 'desktopName', title: '所属桌面', align: 'left', width: 120 },
			{ field: 'parentTitle', title: '所属目录', align: 'left', width: 120 },
			{ field: 'childNum', title: '子页面数量', align: 'left', width: 120 },
			{ field: 'url', title: '页面路径', align: 'left', width: 300},
			{ field: 'orderBy', title: '排序号', align: 'left', width: 80},
			{ field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], width: 120 },
			{ field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 },
			{ field: 'lastUpdateName', title: systemLanguage["com.skyeye.lastUpdateName"][languageType], align: 'left', width: 120 },
			{ field: 'lastUpdateTime', title: systemLanguage["com.skyeye.lastUpdateTime"][languageType], align: 'center', width: 150 },
			{ title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 320, toolbar: '#tableBar'}
		]],
		done: function(json) {
			matchingLanguage();
			initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "请输入页面名称", function () {
				table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
			});
		}
	});

	table.on('tool(messageTable)', function (obj) { 
        var data = obj.data; 
        var layEvent = obj.event;
		if (layEvent === 'delete') { //删除
			del(data, obj);
		} else if (layEvent === 'edit') { //编辑
			edit(data);
		} else if (layEvent === 'top') { //上移
			topOne(data);
		} else if (layEvent === 'lower') { //下移
			lowerOne(data);
		} else if (layEvent === 'authpoint') { //权限点
			authpoint(data);
		} else if (layEvent === 'iconPath') { //logo预览
			systemCommonUtil.showPicImg(systemCommonUtil.getFilePath(data.logo));
		}
    });

	// 新增菜单
	$("body").on("click", "#addBean", function() {
		_openNewWindows({
			url: "../../tpl/appWorkPage/appWorkPageAdd.html",
			title: "新增菜单",
			pageId: "appWorkPageAdd",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	});
	
	// 编辑菜单
	function edit(data) {
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/appWorkPage/appWorkPageEdit.html", 
			title: "编辑菜单",
			pageId: "appWorkPageEdit",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	}
	
	// 删除菜单
	function del(data, obj){
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
			layer.close(index);
            AjaxPostUtil.request({url: reqBasePath + "appworkpage007", params: {rowId: data.id}, type: 'json', callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}
	
	// 上移
	function topOne(data) {
		AjaxPostUtil.request({url: reqBasePath + "appworkpage008", params: {rowId: data.id, parentId: data.parentId}, type: 'json', callback: function (json) {
			winui.window.msg(systemLanguage["com.skyeye.moveUpOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
			loadTable();
		}});
	}
	
	// 下移
	function lowerOne(data) {
		AjaxPostUtil.request({url: reqBasePath + "appworkpage009", params: {rowId: data.id, parentId: data.parentId}, type: 'json', callback: function (json) {
			winui.window.msg(systemLanguage["com.skyeye.moveDownOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
			loadTable();
		}});
	}
	
	// 权限点
    function authpoint(data) {
		menuId = data.id;
		_openNewWindows({
			url: "../../tpl/appWorkPageAuthPoint/appWorkPageAuthPointList.html",
			title: "权限点",
			pageId: "appWorkPageAuthPointList",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
			}});
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
    
    exports('appWorkPageList', {});
});
