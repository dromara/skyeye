
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
	
	authBtn('1636280749353');

	tableTree.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: flowableBasePath + 'dsformpagetype001',
	    where: getTableParams(),
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
			{ field: 'typeName', title: '中文名称', width: 180 },
			{ field: 'typeNameEn', title: '英文名称', width: 180 },
			{ field: 'code', title: '编码', align: 'left', width: 100 },
			{ field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], width: 120 },
			{ field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 },
			{ field: 'lastUpdateName', title: systemLanguage["com.skyeye.lastUpdateName"][languageType], align: 'left', width: 120 },
			{ field: 'lastUpdateTime', title: systemLanguage["com.skyeye.lastUpdateTime"][languageType], align: 'center', width: 150 },
	        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 130, toolbar: '#tableBar'}
	    ]],
	    done: function(json) {
	    	matchingLanguage();
	    }
	}, {
		keyId: 'id',
		keyPid: 'parentId',
		title: 'typeName',
	});

	tableTree.getTable().on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if(layEvent === 'delet'){ // 删除
        	delet(data);
        } else if (layEvent === 'edit'){ // 编辑
        	edit(data);
        }
    });
	
	// 添加
	$("body").on("click", "#addBean", function() {
    	_openNewWindows({
			url: "../../tpl/dsFormPageType/dsFormPageTypeAdd.html",
			title: systemLanguage["com.skyeye.addPageTitle"][languageType],
			pageId: "dsFormPageTypeAdd",
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
            AjaxPostUtil.request({url: flowableBasePath + "dsformpagetype003", params: {id: data.id}, type: 'json', method: "DELETE", callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}

	// 编辑
	function edit(data) {
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/dsFormPageType/dsFormPageTypeEdit.html",
			title: systemLanguage["com.skyeye.editPageTitle"][languageType],
			pageId: "dsFormPageTypeEdit",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	}

	// 刷新数据
    $("body").on("click", "#reloadTable", function() {
    	loadTable();
    });

	form.render();
	form.on('submit(formSearch)', function (data) {
		if (winui.verifyForm(data.elem)) {
			loadTable();
		}
		return false;
	});
    
    function loadTable() {
		tableTree.reload("messageTable", {where: getTableParams()});
    }
    
    function getTableParams() {
    	return {
    		typeName: $("#typeName").val(),
			encoded: $("#encoded").val()
    	};
	}
    
    exports('dsFormPageTypeList', {});
});
