
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
	
	authBtn('1570697180609');
	
	table.render({
	    id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        url: flowableBasePath + 'material001',
        where: getTableParams(),
        even: true,
        page: true,
        limits: getLimits(),
	    limit: getLimit(),
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
	        { field: 'name', title: '商品名称', align: 'left', width: 150, templet: function (d) {
		        	return '<a lay-event="details" class="notice-title-click">' + d.name + '</a>';
		    }},
	        { field: 'model', title: '型号', align: 'left', width: 150 },
	        { field: 'categoryName', title: '所属类型', width: 100 },
	        { field: 'typeName', title: '商品来源', align: 'left', width: 100 },
			{ field: 'unitType', title: '规格类型', align: 'center', width: 100 },
	        { field: 'enabled', title: '状态', align: 'center', width: 60, templet: function (d) {
				if (d.enabled == 2) {
					return "<span class='state-down'>禁用</span>";
				} else if (d.enabled == 1) {
					return "<span class='state-up'>启用</span>";
				}
	        }},
			{ field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], width: 120 },
			{ field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 },
			{ field: 'lastUpdateName', title: systemLanguage["com.skyeye.lastUpdateName"][languageType], align: 'left', width: 120 },
			{ field: 'lastUpdateTime', title: systemLanguage["com.skyeye.lastUpdateTime"][languageType], align: 'center', width: 150 },
	        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 120, toolbar: '#tableBar' }
	    ]],
	    done: function(json) {
	    	matchingLanguage();
			initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "请输入商品名称，规格", function () {
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
        } else if (layEvent === 'details') { //详情
        	details(data);
        }
    });
    
    // 详情
	function details(data) {
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/material/materialDetails.html",
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "materialDetails",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
			}});
	}
	
	// 添加
	$("body").on("click", "#addBean", function() {
    	_openNewWindows({
			url: "../../tpl/material/materialAdd.html",
			title: systemLanguage["com.skyeye.addPageTitle"][languageType],
			pageId: "materialAdd",
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
            AjaxPostUtil.request({url: flowableBasePath + "material006", params: {rowId: data.id}, type: 'json', callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}
	
	// 编辑
	function edit(data) {
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/material/materialEdit.html",
			title: systemLanguage["com.skyeye.editPageTitle"][languageType],
			pageId: "materialEdit",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}
		});
	}

	form.render();
	// 刷新数据
    $("body").on("click", "#reloadTable", function() {
    	loadTable();
    });
    
    function loadTable() {
    	table.reloadData("messageTable", {where: getTableParams()});
    }
    
    function getTableParams() {
    	return $.extend(true, {}, initTableSearchUtil.getSearchValue("messageTable"));
    }
    
    exports('materialList', {});
});
