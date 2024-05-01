
var rowId = "";

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
	var sysWinId = "";
	
	authBtn('1552958167410');
	
	function initLoadTable() {
		tableTree.render({
		    id: 'messageTable',
		    elem: '#messageTable',
		    method: 'post',
		    url: reqBasePath + 'sys006',
		    where: getTableParams(),
		    even: true,
		    page: true,
		    limits: getLimits(),
	    	limit: getLimit(),
		    cols: [[
		        { field: 'name', title: '菜单名称', width: 120 },
		        { field: 'id', title: '图标', align: 'center', width: 60, templet: function (d) {
		        	return systemCommonUtil.initIconShow(d);
		        }},
				{ field: 'orderNum', title: '排序', align: 'center', width: 80 },
		        { field: 'level', title: '菜单类型', align: 'center', width: 100, templet: function (d) {
		        	return d.level == 0 ? '父菜单' : '子菜单';
		        }},
		        { field: 'desktopName', title: '所属桌面', width: 120, templet: function (d) {
					return d.desktopMation?.name;
				}},
		        { field: 'isShare', title: '共享', align: 'center', width: 80, templet: function (d) {
					return d.isShare == 0 ? '否' : '是';
		        }},
		        { field: 'pageUrl', title: '菜单链接', width: 160 },
				{ field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], width: 120 },
				{ field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 },
				{ field: 'lastUpdateName', title: systemLanguage["com.skyeye.lastUpdateName"][languageType], align: 'left', width: 120 },
				{ field: 'lastUpdateTime', title: systemLanguage["com.skyeye.lastUpdateTime"][languageType], align: 'center', width: 150 },
		        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 320, toolbar: '#tableBar' }
		    ]],
		    done: function(json) {
		    	matchingLanguage();
				initTableSearchUtil.initAdvancedSearch($("#messageTable")[0], json.searchFilter, form, "请输入菜单名称", function () {
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
			if (layEvent === 'del') { // 删除
				del(data, obj);
			} else if (layEvent === 'edit') { // 编辑
				edit(data);
			} else if (layEvent === 'top') { // 上移
				topOne(data);
			} else if (layEvent === 'lower') { // 下移
				lowerOne(data);
			} else if (layEvent === 'authpoint') { // 权限点
				authpoint(data);
			} else if (layEvent === 'iconPic') { // 图片
				systemCommonUtil.showPicImg(fileBasePath + data.iconPic);
			} else if (layEvent === 'details') { // 详情
				details(data);
			}
	    });
	}
	
	/********* tree 处理   start *************/
	var tree = fsTree.render({
		id: "treeDemo",
		url: reqBasePath + "querySysEveWinList",
		clickCallback: onClickTree,
		onDblClick: onClickTree
	}, function(id) {
		initLoadTable();
	});

	//异步加载的方法
	function onClickTree(event, treeId, treeNode) {
		if(treeNode == undefined) {
			sysWinId = "";
		} else {
			sysWinId = treeNode.id;
		}
		loadTable();
	}
	/********* tree 处理   end *************/
	
    // 删除
	function del(data, obj) {
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: reqBasePath + "deleteMenuById", params: {id: data.id}, type: 'json', method: 'DELETE', callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}
	
	// 详情
    function details(data) {
        rowId = data.id;
        _openNewWindows({
            url: "../../tpl/sysEveMenu/sysEveMenuDetails.html",
            title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
            pageId: "sysEveMenuDetails",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
            }});
    }
	
	// 编辑
	function edit(data) {
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/sysEveMenu/sysEveMenuEdit.html",
			title: systemLanguage["com.skyeye.editPageTitle"][languageType],
			pageId: "sysEveMenuEdit",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	}
	
	// 上移
	function topOne(data) {
		AjaxPostUtil.request({url: reqBasePath + "sys022", params: {id: data.id}, type: 'json', method: 'POST', callback: function (json) {
			winui.window.msg(systemLanguage["com.skyeye.moveUpOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
			loadTable();
		}});
	}
	
	// 下移
	function lowerOne(data) {
		AjaxPostUtil.request({url: reqBasePath + "sys023", params: {id: data.id}, type: 'json', method: 'POST', callback: function (json) {
			winui.window.msg(systemLanguage["com.skyeye.moveDownOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
			loadTable();
		}});
	}
    
    // 新增菜单
    $("body").on("click", "#addBean", function() {
    	_openNewWindows({
			url: "../../tpl/sysEveMenu/sysEveMenuAdd.html",
			title: systemLanguage["com.skyeye.addPageTitle"][languageType],
			pageId: "sysEveMenuAdd",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
    });
    
    // 权限点
    function authpoint(data) {
		_openNewWindows({
			url: '../../tpl/authPoint/authPointList.html?objectId=' + data.id + '&objectKey=' + data.serviceClassName,
			title: systemLanguage["com.skyeye.authorityPointPage"][languageType],
			pageId: "sysEveMenuAuthPointList",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
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
		return $.extend(true, {sysWinId: sysWinId}, initTableSearchUtil.getSearchValue("messageTable"));
    }
    
    exports('sysEveMenuList', {});
});
