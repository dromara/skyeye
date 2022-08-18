
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
	
	authBtn('1637677810338');
	
	table.render({
		id: 'messageTable',
		elem: '#messageTable',
		method: 'post',
		url: flowableBasePath + 'bossIntervieweeFrom001',
		where: getTableParams(),
		even: true,
		page: true,
		limits: getLimits(),
		limit: getLimit(),
		cols: [[
			{ title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
			{ field: 'title', title: '来源', align: 'left', width: 150 },
			{ field: 'fromUrl', title: '来源地址', align: 'left', width: 300 },
			{ field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], width: 120 },
			{ field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 },
			{ field: 'lastUpdateName', title: systemLanguage["com.skyeye.lastUpdateName"][languageType], align: 'left', width: 100 },
			{ field: 'lastUpdateTime', title: systemLanguage["com.skyeye.lastUpdateTime"][languageType], align: 'center', width: 150 },
			{ title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 150, toolbar: '#tableBar'}
		]],
		done: function(){
			matchingLanguage();
		}
	});

	table.on('tool(messageTable)', function (obj) {
		var data = obj.data;
		var layEvent = obj.event;
		if (layEvent === 'edit') { //编辑
			edit(data);
		} else if (layEvent === 'delet') { //删除
			delet(data);
		}
	});

	// 添加
	$("body").on("click", "#addBean", function() {
    	_openNewWindows({
			url: "../../tpl/bossIntervieweeFrom/bossIntervieweeFromAdd.html",
			title: systemLanguage["com.skyeye.addPageTitle"][languageType],
			pageId: "bossIntervieweeFromAdd",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
    });
	
	// 删除
	function delet(data) {
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
			layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "bossIntervieweeFrom003", params: {id: data.id}, type: 'json', method: "DELETE", callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}
	
	// 编辑
	function edit(data) {
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/bossIntervieweeFrom/bossIntervieweeFromEdit.html",
			title: systemLanguage["com.skyeye.editPageTitle"][languageType],
			pageId: "bossIntervieweeFromEdit",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}
		});
	}

	form.render();
	form.on('submit(formSearch)', function (data) {
		if (winui.verifyForm(data.elem)) {
			refreshTable();
		}
		return false;
	});

	// 刷新数据
    $("body").on("click", "#reloadTable", function() {
    	loadTable();
    });
    
    function loadTable() {
    	table.reloadData("messageTable", {where: getTableParams()});
    }
    
    function refreshTable(){
    	table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
    }

    function getTableParams() {
    	return {
    		title: $("#title").val()
    	};
	}
    
    exports('bossIntervieweeFromList', {});
});
