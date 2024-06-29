
var rowId = "";
var objectId = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form', 'soulTable'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		form = layui.form,
		table = layui.table,
		soulTable = layui.soulTable;

	objectId = GetUrlParam("objectId");
	if (isNull(objectId)) {
		winui.window.msg("请传入适用对象信息", {icon: 2, time: 2000});
		return false;
	}

	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: reqBasePath + 'queryOperateList',
	    where: getTableParams(),
	    even: false,
	    page: false,
		overflow: {
			type: 'tips',
			header: true,
			total: true
		},
		limit: 100,
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
	        { field: 'name', title: '名称', align: 'left', width: 120 },
			{ field: 'position', title: '展示位置', align: 'center', width: 100, templet: function (d) {
				return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("operatePosition", 'id', d.position, 'name');
			}},
			{ field: 'color', title: '颜色', align: 'center', width: 80, templet: function (d) {
				return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("buttonColorType", 'id', d.color, 'name');
			}},
			{ field: 'authPointNum', title: '权限编码', align: 'center', width: 120 },
			{ field: 'eventType', title: '事件类型', align: 'center', width: 100, templet: function (d) {
				return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("eventType", 'id', d.eventType, 'name');
			}},
			{ field: 'orderBy', title: '序号', align: 'left', width: 80 },
			{ field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], width: 120 },
			{ field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 },
			{ field: 'lastUpdateName', title: systemLanguage["com.skyeye.lastUpdateName"][languageType], align: 'left', width: 120 },
			{ field: 'lastUpdateTime', title: systemLanguage["com.skyeye.lastUpdateTime"][languageType], align: 'center', width: 150 },
	        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 200, toolbar: '#tableBar' }
	    ]],
	    done: function(json) {
			soulTable.render(this);
	    	matchingLanguage();
	    }
	});
	
	table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'delet'){ // 删除
        	delet(data);
        } else if (layEvent === 'edit'){ // 编辑
        	edit(data);
        }
    });

	// 添加
	$("body").on("click", "#addBean", function() {
		rowId = '';
		openWritePage();
    });

	// 删除
	function delet(data) {
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: reqBasePath + "deleteOperateById", params: {id: data.id}, type: 'json', method: "DELETE", callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}

	// 编辑
	function edit(data) {
		rowId = data.id;
		openWritePage();
	}

	function openWritePage() {
		_openNewWindows({
			url: "../../tpl/operate/writeOperate.html",
			title: systemLanguage["com.skyeye.recordPageTitle"][languageType],
			pageId: "writeOperate",
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
		table.reloadData("messageTable", {where: getTableParams()});
	}

	function getTableParams() {
		return {className: objectId};
	}

    exports('operateList', {});
});
