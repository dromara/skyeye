
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
	
	authBtn('1563780633455');
	
	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: reqBasePath + 'desktop001',
	    where: getTableParams(),
	    even: true,
	    page: true,
	    limits: getLimits(),
    	limit: getLimit(),
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
	        { field: 'name', title: '桌面名称', align: 'left', width: 150 },
			{ field: 'code', title: '编码', align: 'left', width: 120 },
			{ field: 'logo', title: 'LOGO', align: 'center', width: 60, templet: function (d) {
				if (isNull(d.logo)) {
					return '<img src="../../assets/images/os_windows.png" class="photo-img">';
				} else {
					return '<img src="' + systemCommonUtil.getFilePath(d.logo) + '" class="photo-img" lay-event="logo">';
				}
			}},
	        { field: 'allNum', title: '菜单数量', align: 'center', width: 100 },
			{ field: 'orderBy', title: '序号', align: 'center', width: 80 },
	        { field: 'enabled', title: '状态', width: 80, align: 'center', templet: function (d) {
				return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("commonEnable", 'id', d.enabled, 'name');
	        }},
			{ field: 'appPageUrl', title: 'APP跳转地址', align: 'left', width: 180 },
			{ field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], width: 120 },
			{ field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 },
			{ field: 'lastUpdateName', title: systemLanguage["com.skyeye.lastUpdateName"][languageType], align: 'left', width: 120 },
			{ field: 'lastUpdateTime', title: systemLanguage["com.skyeye.lastUpdateTime"][languageType], align: 'center', width: 150 },
	        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 300, toolbar: '#tableBar'}
	    ]],
	    done: function(json) {
	    	matchingLanguage();
			initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "请输入桌面名称", function () {
				table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
			});
	    }
	});
	
	table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
		if (layEvent === 'edit') { //编辑
			edit(data);
		} else if (layEvent === 'delet') { //删除
			delet(data);
		} else if (layEvent === 'upMove') { //上移
			upMove(data);
		} else if (layEvent === 'downMove') { //下移
			downMove(data);
		} else if (layEvent === 'remove') { //一键移除菜单
			remove(data);
		} else if (layEvent === 'logo') { //logo预览
			systemCommonUtil.showPicImg(systemCommonUtil.getFilePath(data.logo));
		}
    });

	// 添加
	$("body").on("click", "#addBean", function() {
    	_openNewWindows({
			url: "../../tpl/sysEveDesktop/sysEveDesktopAdd.html",
			title: systemLanguage["com.skyeye.addPageTitle"][languageType],
			pageId: "sysEveDesktopAdd",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
    });
	
	// 删除
	function delet(data) {
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: reqBasePath + "desktop003", params: {id: data.id}, type: 'json', method: "DELETE", callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}
	
	// 一键移除菜单
    function remove(data) {
        var msg = '确认一键移除菜单选择该桌面的所有菜单吗？';
        layer.confirm(msg, { icon: 3, title: '一键移除菜单' }, function (index) {
            layer.close(index);
            AjaxPostUtil.request({url: reqBasePath + "desktop012", params: {id: data.id}, type: 'json', method: "POST", callback: function (json) {
				winui.window.msg("移除成功", {icon: 1, time: 2000});
				loadTable();
            }});
        });
    }
	
	// 编辑
	function edit(data) {
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/sysEveDesktop/sysEveDesktopEdit.html",
			title: systemLanguage["com.skyeye.editPageTitle"][languageType],
			pageId: "sysEveDesktopEdit",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}
		});
	}
	
	// 上移
	function upMove(data) {
        AjaxPostUtil.request({url: reqBasePath + "desktop008", params: {id: data.id}, type: 'json', method: "POST", callback: function (json) {
			winui.window.msg(systemLanguage["com.skyeye.moveUpOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
			loadTable();
		}});
	}
	
	// 下移
	function downMove(data) {
        AjaxPostUtil.request({url: reqBasePath + "desktop009", params: {id: data.id}, type: 'json', method: "POST", callback: function (json) {
			winui.window.msg(systemLanguage["com.skyeye.moveDownOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
			loadTable();
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

    exports('sysEveDesktopList', {});
});
