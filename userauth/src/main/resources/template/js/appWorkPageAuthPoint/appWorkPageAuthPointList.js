
var menuId = '';

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
	
	authBtn('1563605451628');
	
	menuId = parent.menuId;

	tableTree.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: reqBasePath + 'appworkpageauthpoint001',
	    where: getTableParams(),
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
	        { field: 'authMenuName', title: '权限点名称', width: 120 },
	        { field: 'authMenu', title: '接口url', width: 200 },
			{ field: 'menuNum', title: '权限点编号', align: 'center', width: 120 },
			{ field: 'useNum', title: '使用数量', align: 'center', width: 80 },
			{ field: 'typeName', title: '类型', align: 'center', width: 80 },
			{ field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], width: 120 },
			{ field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 },
			{ field: 'lastUpdateName', title: systemLanguage["com.skyeye.lastUpdateName"][languageType], align: 'left', width: 120 },
			{ field: 'lastUpdateTime', title: systemLanguage["com.skyeye.lastUpdateTime"][languageType], align: 'center', width: 150 },
	        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 200, toolbar: '#tableBar'}
	    ]],
	    done: function(json) {
	    	matchingLanguage();
	    }
	}, {
		keyId: 'id',
		keyPid: 'parentId',
		title: 'authMenuName',
	});

	tableTree.getTable().on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
		if (layEvent === 'del') { //删除
			del(data, obj);
		} else if (layEvent === 'edit') { //编辑
			edit(data);
		} else if (layEvent === 'addChild') { // 新增分组/新增数据权限
			addPage(data);
		}
    });

	// 删除
	function del(data, obj) {
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
			layer.close(index);
            AjaxPostUtil.request({url: reqBasePath + "appworkpageauthpoint005", params: {rowId: data.id}, type: 'json', callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}
	
	// 编辑权限点
	function edit(data) {
		rowId = data.id;
		var title = '';
		if (data.type == 1) {
			title = '编辑权限点';
		} else if (data.type == 2) {
			title = '编辑分组';
		} else if (data.type == 3) {
			title = '编辑数据权限';
		}
		_openNewWindows({
			url: "../../tpl/appWorkPageAuthPoint/appWorkPageAuthPointEdit.html",
			title: title,
			pageId: "appWorkPageAuthPointEdit",
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
		var title = '新增权限点';
		parentId = "0";
		parentType = "";
		if (!isNull(data)) {
			parentId = data.id;
			parentType = data.type;
			if (parentType == 1) {
				// 如果是权限点，则新增分组
				title = '新增分组';
			} else if (parentType == 2) {
				// 如果是分组，则新增数据权限
				title = '新增数据权限';
			}
		}
		_openNewWindows({
			url: "../../tpl/appWorkPageAuthPoint/appWorkPageAuthPointAdd.html",
			title: title,
			pageId: "appWorkPageAuthPointAdd",
			area: ['500px', '300px'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	}
    
	form.render();
	form.on('submit(formSearch)', function (data) {
		if (winui.verifyForm(data.elem)) {
			loadTable();
		}
		return false;
	});

	// 刷新数据
	$("body").on("click", "#reloadTable", function() {
		loadTable();
	});

	function loadTable() {
		tableTree.reload("messageTable", {where: getTableParams()});
	}

	function getTableParams() {
		return {
			authMenuName: $("#authMenuName").val(),
			authMenu: $("#authMenu").val(),
			menuNum: $("#menuNum").val(),
			menuId: menuId
		};
	}
    
    exports('appWorkPageAuthPointList', {});
});
