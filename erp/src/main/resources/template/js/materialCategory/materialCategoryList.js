
var rowId = "";

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
	
	authBtn('1568558491168');
	tableTree.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: flowableBasePath + 'materialcategory001',
	    where: getTableParams(),
		even: true,
		page: true,
		limits: getLimits(),
		limit: getLimit(),
	    cols: [[
	        { field: 'name', title: '名称', align: 'left', width: 200 },
	        { field: 'sort', title: systemLanguage["com.skyeye.serialNumber"][languageType], align: 'center', width: 80 },
	        { field: 'remark', title: '备注', align: 'left', width: 200 },
			{ field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], width: 120 },
			{ field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 },
			{ field: 'lastUpdateName', title: systemLanguage["com.skyeye.lastUpdateName"][languageType], align: 'left', width: 120 },
			{ field: 'lastUpdateTime', title: systemLanguage["com.skyeye.lastUpdateTime"][languageType], align: 'center', width: 150 },
	        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 200, toolbar: '#tableBar' }
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
        if (layEvent === 'edit') { //编辑
        	edit(data);
        } else if (layEvent === 'delet') { //删除
        	delet(data);
        } else if (layEvent === 'upMove') { //上移
        	upMove(data);
        } else if (layEvent === 'downMove') { //下移
        	downMove(data);
        }
    });
	
	// 添加
	$("body").on("click", "#addBean", function() {
    	_openNewWindows({
			url: "../../tpl/materialCategory/materialCategoryAdd.html",
			title: systemLanguage["com.skyeye.addPageTitle"][languageType],
			pageId: "materialCategoryAdd",
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
            AjaxPostUtil.request({url: flowableBasePath + "materialcategory003", params: {id: data.id}, type: 'json', method: "DELETE", callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}
	
	// 编辑
	function edit(data) {
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/materialCategory/materialCategoryEdit.html",
			title: systemLanguage["com.skyeye.editPageTitle"][languageType],
			pageId: "materialCategoryEdit",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}
		});
	}
	
	// 上移
	function upMove(data) {
        AjaxPostUtil.request({url: flowableBasePath + "materialcategory006", params: {id: data.id}, type: 'json', method: "PUT", callback: function (json) {
			winui.window.msg(systemLanguage["com.skyeye.moveUpOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
			loadTable();
		}});
	}
	
	// 下移
	function downMove(data) {
        AjaxPostUtil.request({url: flowableBasePath + "materialcategory007", params: {id: data.id}, type: 'json', method: "PUT", callback: function (json) {
			winui.window.msg(systemLanguage["com.skyeye.moveDownOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
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
    	return $.extend(true, {}, initTableSearchUtil.getSearchValue("messageTable"));
	}
    
    exports('materialCategoryList', {});
});
