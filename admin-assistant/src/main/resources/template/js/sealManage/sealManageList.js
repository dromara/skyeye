
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
	
	// 新增印章
	authBtn('1566464558428');
	
	// 印章列表
	table.render({
		id: 'seallistTable',
		elem: '#seallistTable',
		method: 'post',
		url: flowableBasePath + 'seal001',
		where: getTableParams(),
		even: true,
		page: true,
		limits: getLimits(),
		limit: getLimit(),
		cols: [[
			{ title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
			{ field: 'sealName', title: '印章名称', width: 170, templet: function (d) {
				return '<a lay-event="details" class="notice-title-click">' + d.sealName + '</a>';
			}},
			{ field: 'enableTime', title: '启用日期', align: 'center', width: 120 },
			{ field: 'borrowId', title: '借用人', align: 'center', width: 100 },
			{ field: 'sealAdmin', title: '管理人', align: 'center', width: 100 },
			{ title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 150, toolbar: '#seallisttableBar'}
		]],
		done: function(json) {
			matchingLanguage();
		}
	});

	table.on('tool(seallistTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'details') { //详情
        	details(data);
        } else if (layEvent === 'delet') { //删除
        	delet(data);
        } else if (layEvent === 'edit') { //编辑
        	edit(data);
        }
    });
	
	form.render();
	
	// 详情
	function details(data) {
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/sealManage/sealManageDetails.html", 
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "sealManageDetails",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
			}});
	}
	
	// 删除
	function delet(data) {
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "seal003", params: {rowId: data.id}, type: 'json', callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}

	// 新增印章
	$("body").on("click", "#seallistaddBean", function() {
    	_openNewWindows({
			url: "../../tpl/sealManage/sealManageAdd.html", 
			title: "新增印章",
			pageId: "sealManageAdd",
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
			url: "../../tpl/sealManage/sealManageEdit.html", 
			title: "编辑印章",
			pageId: "sealManageEdit",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	}
	
    $("body").on("click", "#reloadseallistTable", function() {
    	loadTable();
    });
    
    // 搜索表单
	$("body").on("click", "#seallistformSearch", function() {
    	table.reloadData("seallistTable", {page: {curr: 1}, where: getTableParams()});
	});
    
    function loadTable() {
    	table.reloadData("seallistTable", {where: getTableParams()});
    }
    
    function getTableParams() {
    	return {
    		sealName: $("#sealName").val()
    	};
    }
    
    exports('sealManageList', {});
});
