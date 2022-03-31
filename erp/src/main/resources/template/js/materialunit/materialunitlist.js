
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
	
	authBtn('1569160351079');
	
	
	table.render({
	    id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        url: flowableBasePath + 'materialunit001',
        where: {groupName: $("#groupName").val()},
        even: true,
        page: true,
        limits: [8, 16, 24, 32, 40, 48, 56],
        limit: 8,
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
	        { field: 'groupName', title: '组名', align: 'left', width: 120 },
	        { field: 'unitName', title: '计量单位', align: 'left', width: 300 },
	        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 257, toolbar: '#tableBar'}
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
        }else if (layEvent === 'delet') { //删除
        	delet(data);
        }
    });
	
	form.render();
	
	
	form.on('submit(formSearch)', function (data) {
        
        if (winui.verifyForm(data.elem)) {
        	refreshloadTable();
        }
        return false;
    });
	
	//添加
	$("body").on("click", "#addBean", function(){
    	_openNewWindows({
			url: "../../tpl/materialunit/materialunitadd.html", 
			title: systemLanguage["com.skyeye.addPageTitle"][languageType],
			pageId: "materialunitadd",
			area: ['70vw', '70vh'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1,time: 2000});
                	loadTable();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
                }
			}});
    });
	
	//删除
	function delet(data){
		var msg = '确认删除选中数据吗？';
		layer.confirm(msg, { icon: 3, title: '删除计量单位' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "materialunit003", params: {rowId: data.id}, type: 'json', callback: function(json){
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
			url: "../../tpl/materialunit/materialunitedit.html", 
			title: systemLanguage["com.skyeye.editPageTitle"][languageType],
			pageId: "materialunitedit",
			area: ['70vw', '70vh'],
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
	
	//刷新数据
    $("body").on("click", "#reloadTable", function(){
    	loadTable();
    });
    
    function loadTable(){
    	table.reload("messageTable", {where:{groupName: $("#groupName").val()}});
    }
    
    function refreshloadTable(){
    	table.reload("messageTable", {page: {curr: 1}, where:{groupName: $("#groupName").val()}});
    }
    
    exports('materialunitlist', {});
});
