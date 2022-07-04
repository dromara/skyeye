
var menuId = '';

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
	
	authBtn('1552958039634');
	
	menuId = parent.menuId;
	
	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: reqBasePath + 'sysevemenuauthpoint001',
	    where: getTableParams(),
	    even: true,
	    page: true,
	    limits: getLimits(),
    	limit: getLimit(),
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
	        { field: 'authMenuName', title: '权限点名称', width: 120 },
	        { field: 'authMenu', title: '接口url', width: 200 },
	        { field: 'menuNum', title: '权限点编号', width: 150 },
	        { field: 'useNum', title: '使用数量', align: 'center', width: 80 },
	        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 120, toolbar: '#tableBar'}
	    ]],
	    done: function(){
	    	matchingLanguage();
	    }
	});
	
	table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'del') { //删除
        	del(data, obj);
        }else if (layEvent === 'edit') { //编辑
        	edit(data);
        }
    });
	
	form.render();
	form.on('submit(formSearch)', function (data) {
        if (winui.verifyForm(data.elem)) {
        	refreshTable();
        }
        return false;
	});
	
	//删除
	function del(data, obj){
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
			layer.close(index);
            AjaxPostUtil.request({url: reqBasePath + "sysevemenuauthpoint005", params:{rowId: data.id}, type: 'json', callback: function (json) {
    			if (json.returnCode == 0) {
    				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
    				loadTable();
    			} else {
    				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
    			}
    		}});
		});
	}
	
	//编辑权限点
	function edit(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/sysevemenuauthpoint/sysevemenuauthpointedit.html", 
			title: "编辑权限点",
			pageId: "sysevemenuauthpointedit",
			area: ['500px', '300px'],
			callBack: function(refreshCode){
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	}
	
	//刷新数据
    $("body").on("click", "#reloadTable", function() {
    	loadTable();
    });
    
    //新增权限点
    $("body").on("click", "#addBean", function() {
    	_openNewWindows({
			url: "../../tpl/sysevemenuauthpoint/sysevemenuauthpointadd.html", 
			title: "新增权限点",
			pageId: "sysevemenuauthpointadd",
			area: ['500px', '300px'],
			callBack: function(refreshCode){
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
    });
    
    function loadTable(){
    	table.reload("messageTable", {where: getTableParams()});
    }
    
    function refreshTable(){
    	table.reload("messageTable", {page: {curr: 1}, where: getTableParams()});
    }
    
    function getTableParams(){
    	return {
    		authMenuName: $("#authMenuName").val(),
    		authMenu: $("#authMenu").val(),
    		menuNum: $("#menuNum").val(),
    		menuId: menuId
		};
	}
    
    exports('sysevemenuauthpointlist', {});
});
