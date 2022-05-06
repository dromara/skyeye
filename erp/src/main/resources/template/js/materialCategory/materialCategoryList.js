
var rowId = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'treeGrid', 'jquery', 'winui', 'form'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		form = layui.form,
		treeGrid = layui.treeGrid;
	
	authBtn('1568558491168');
	treeGrid.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    idField: 'id',
	    url: flowableBasePath + 'materialcategory001',
	    cellMinWidth: 100,
	    where: getTableParams(),
	    treeId: 'id',//树形id字段名称
        treeUpId: 'pId',//树形父id字段名称
        treeShowName: 'name',//以树形式显示的字段
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
	        { field: 'name', title: '名称', align: 'left', width: 360 },
	        { field: 'orderBy', title: systemLanguage["com.skyeye.serialNumber"][languageType], align: 'center', width: 80 },
	        { field: 'remark', title: '备注', align: 'left', width: 200 },
	        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 250, toolbar: '#tableBar'}
	    ]],
	    isPage:false,
	    done: function(){
	    	matchingLanguage();
	    }
	});
	
	treeGrid.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'edit') { //编辑
        	edit(data);
        }else if (layEvent === 'delet') { //删除
        	delet(data);
        }else if (layEvent === 'upMove') { //上移
        	upMove(data);
        }else if (layEvent === 'downMove') { //下移
        	downMove(data);
        }
    });
	
	// 添加
	$("body").on("click", "#addBean", function(){
    	_openNewWindows({
			url: "../../tpl/materialCategory/materialCategoryAdd.html",
			title: systemLanguage["com.skyeye.addPageTitle"][languageType],
			pageId: "materialCategoryAdd",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1,time: 2000});
                	loadTable();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
                }
			}});
    });
	
	// 删除
	function delet(data){
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
			layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "materialcategory003", params: {rowId: data.id}, type: 'json', method: "DELETE", callback: function(json){
    			if(json.returnCode == 0){
    				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
    				loadTable();
    			}else{
    				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
    			}
    		}});
		});
	}
	
	//编辑
	function edit(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/materialCategory/materialCategoryEdit.html",
			title: systemLanguage["com.skyeye.editPageTitle"][languageType],
			pageId: "materialCategoryEdit",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1,time: 2000});
                	loadTable();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
                }
			}
		});
	}
	
	// 上移
	function upMove(data){
        AjaxPostUtil.request({url: flowableBasePath + "materialcategory006", params: {rowId: data.id}, type: 'json', method: "PUT", callback: function(json){
			if(json.returnCode == 0){
				winui.window.msg(systemLanguage["com.skyeye.moveUpOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				loadTable();
			}else{
				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
			}
		}});
	}
	
	// 下移
	function downMove(data){
        AjaxPostUtil.request({url: flowableBasePath + "materialcategory007", params: {rowId: data.id}, type: 'json', method: "PUT", callback: function(json){
			if(json.returnCode == 0){
				winui.window.msg(systemLanguage["com.skyeye.moveDownOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				loadTable();
			}else{
				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
			}
		}});
	}

	form.render();
	form.on('submit(formSearch)', function (data) {
		if (winui.verifyForm(data.elem)) {
			refreshloadTable();
		}
		return false;
	});

	// 刷新数据
    $("body").on("click", "#reloadTable", function(){
    	loadTable();
    });
    
    function loadTable(){
    	treeGrid.query("messageTable", {where: getTableParams()});
    }
    
    function refreshloadTable(){
    	treeGrid.query("messageTable", {page: {curr: 1}, where: getTableParams()});
    }

    function getTableParams(){
    	return {
    		name: $("#name").val()
    	};
	}
    
    exports('materialCategoryList', {});
});
