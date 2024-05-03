
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'form', 'tableTreeDj'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		form = layui.form,
		tableTree = layui.tableTreeDj;
	
	authBtn('1563602200836');

	tableTree.render({
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
			{ field: 'name', title: '名称', align: 'left', width: 120 },
			{ field: 'logo', title: 'LOGO', width: 60, templet: function (d) {
				var str = '';
				if (!isNull(d.logo)){
					str = '<img src="' + fileBasePath + d.logo + '" class="photo-img" lay-event="iconPath">';
				}
				return str;
			}},
			{ field: 'type', title: '菜单类型', align: 'left', width: 100, align: 'center', templet: function (d) {
				return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("menuType", 'id', d.type, 'name');
			}},
			{ field: 'desktopId', title: '所属桌面', align: 'left', width: 120, templet: function (d) {
				return getNotUndefinedVal(d.desktopMation?.name)
			}},
			{ field: 'url', title: '页面路径', align: 'left', width: 300},
			{ field: 'orderBy', title: '排序号', align: 'left', width: 80},
			{ field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], width: 120 },
			{ field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 },
			{ field: 'lastUpdateName', title: systemLanguage["com.skyeye.lastUpdateName"][languageType], align: 'left', width: 120 },
			{ field: 'lastUpdateTime', title: systemLanguage["com.skyeye.lastUpdateTime"][languageType], align: 'center', width: 150 },
			{ title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 200, toolbar: '#tableBar'}
		]],
		done: function(json) {
			matchingLanguage();
			initTableSearchUtil.initAdvancedSearch($("#messageTable")[0], json.searchFilter, form, "请输入名称", function () {
				tableTree.reload("messageTable", {page: {curr: 1}, where: getTableParams()});
			});
		}
	}, {
		keyId: 'id',
		keyPid: 'parentId',
		title: 'name',
	});

	tableTree.getTable().on('tool(messageTable)', function (obj) {
        var data = obj.data; 
        var layEvent = obj.event;
		if (layEvent === 'delete') { //删除
			del(data, obj);
		} else if (layEvent === 'edit') { //编辑
			edit(data);
		} else if (layEvent === 'authpoint') { //权限点
			authpoint(data);
		} else if (layEvent === 'iconPath') { //logo预览
			systemCommonUtil.showPicImg(systemCommonUtil.getFilePath(data.logo));
		}
    });

	// 新增目录
	$("body").on("click", "#addBean", function() {
		_openNewWindows({
			url: systemCommonUtil.getUrl('FP2023121600001', null),
			title: systemLanguage["com.skyeye.addPageTitle"][languageType],
			pageId: "appWorkPageAddFolder",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	});

	// 新增菜单
	$("body").on("click", "#addMenuBean", function() {
		_openNewWindows({
			url: systemCommonUtil.getUrl('FP2023121600004', null),
			title: systemLanguage["com.skyeye.addPageTitle"][languageType],
			pageId: "appWorkPageAddMenu",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	});
	
	// 编辑菜单/目录
	function edit(data) {
		var url;
		if (data.type == 1) {
			// 目录
			url = systemCommonUtil.getUrl('FP2023121600002&id=' + data.id, null);
		} else {
			// 菜单
			url = systemCommonUtil.getUrl('FP2023121600005&id=' + data.id, null);
		}
		_openNewWindows({
			url: url,
			title: systemLanguage["com.skyeye.editPageTitle"][languageType],
			pageId: "appWorkPageEdit",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	}
	
	// 删除菜单
	function del(data, obj) {
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: reqBasePath + "deleteAppWorkPageById", params: {id: data.id}, type: 'json', method: 'DELETE', callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}
	
	// 权限点
    function authpoint(data) {
		_openNewWindows({
			url: '../../tpl/authPoint/authPointList.html?objectId=' + data.id + '&objectKey=' + data.serviceClassName,
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
		tableTree.reload("messageTable", {where: getTableParams()});
	}

	function getTableParams() {
		return $.extend(true, {}, initTableSearchUtil.getSearchValue("messageTable"));
	}
    
    exports('appWorkPageList', {});
});
