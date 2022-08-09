
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
	
	authBtn('1552954959607');
	
	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: reqBasePath + 'sys013',
	    where: getTableParams(),
	    even: true,
	    page: true,
		limits: getLimits(),
		limit: getLimit(),
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
	        { field: 'roleName', title: '角色名称', width: 120 },
	        { field: 'roleDesc', title: '角色描述', width: 520 },
	        { field: 'userNum', title: '使用用户数量', width: 150 },
	        { field: 'parentName', title: '父角色', width: 150 },
	        { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 },
	        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 300, toolbar: '#tableBar' }
	    ]],
	    done: function(json) {
	    	matchingLanguage();
			initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "请输入角色名称", function () {
				table.reload("messageTable", {page: {curr: 1}, where: getTableParams()});
			});
	    }
	});
	
	table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'del') { // 删除
        	del(data, obj);
        }else if (layEvent === 'edit') { // 编辑
        	edit(data);
        }else if (layEvent === 'appmenu') { // 手机端菜单授权
            appmenu(data);
        }else if (layEvent === 'pcMenu') { // PC端菜单授权
			pcMenu(data);
		}
    });
	
	// 删除
	function del(data, obj){
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], { icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType] }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: reqBasePath + "sys018", params: {rowId: data.id}, type: 'json', method: "DELETE", callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}
	
	// 编辑
	function edit(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/sysEveRole/sysEveRoleEdit.html",
			title: "编辑角色",
			pageId: "sysEveRoleEdit",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	}
	
	// 手机端菜单授权
    function appmenu(data){
        rowId = data.id;
        _openNewWindows({
            url: "../../tpl/sysEveRole/sysEveRoleAppMenu.html",
            title: "手机端菜单授权",
            pageId: "sysEveRoleAppMenu",
            area: ['90vw', '90vh'],
            callBack: function(refreshCode){}});
    }

	// PC端菜单授权
	function pcMenu(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/sysEveRole/sysEveRolePCMenu.html",
			title: "PC端菜单授权",
			pageId: "sysEveRolePCMenu",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){}});
	}
	
    // 新增角色
    $("body").on("click", "#addBean", function() {
    	_openNewWindows({
			url: "../../tpl/sysEveRole/sysEveRoleAdd.html",
			title: "新增角色",
			pageId: "sysEveRoleAdd",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
    });

	form.render();
	// 刷新数据
	$("body").on("click", "#reloadTable", function() {
		loadTable();
	});

    function loadTable(){
    	table.reload("messageTable", {where: getTableParams()});
    }

    function getTableParams() {
    	return $.extend(true, {}, initTableSearchUtil.getSearchValue("messageTable"));
	}
    
    exports('sysEveRoleList', {});
});
