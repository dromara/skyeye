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
	
	authBtn('1582382603658');
		
	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: flowableBasePath + 'sealseserviceworker001',
	    where: {userName: $("#userName").val()},
	    even: true,
	    page: true,
	    limits: [8, 16, 24, 32, 40, 48, 56],
	    limit: 8,
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
	        { field: 'userName', title: '用户姓名', align: 'left', width: 80 },
	        { field: 'proName', title: '所在省', align: 'left', width: 80 },
	        { field: 'cityName', title: '所在市', align: 'left', width: 80 },
	        { field: 'areaName', title: '所在区', align: 'left', width: 80 },
	        { field: 'orderNumber', title: '工单数', align: 'left', width: 60 },
	        { field: 'stateName', title: '状态', align: 'center', width: 80, templet: function (d) {
	        	if(d.orderNumber > 0){
	        		return '<span class="state-down">' + d.stateName + '</span>';
	        	} else {
	        		return '<span class="state-up">' + d.stateName + '</span>';
	        	}
	        }},
	        { field: 'addDetail', title: '详细地址', align: 'left', width: 400 },
	        { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 },
	        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 150, toolbar: '#tableBar'}
	    ]],
	    done: function(json) {
	    	matchingLanguage();
	    }
	});
	
	table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'edit') { //编辑
        	edit(data);
        } else if (layEvent === 'delete'){ //删除
        	del(data);
        }
    });
	
	form.render();
	
	$("body").on("click", "#formSearch", function() {
		refreshTable();
	});
	
	$("body").on("click", "#reloadTable", function() {
    	loadTable();
    });
    
    function loadTable() {
    	table.reloadData("messageTable", {where: {userName: $("#userName").val()}});
    }
    
    function refreshTable(){
    	table.reloadData("messageTable", {page: {curr: 1}, where: {userName: $("#userName").val()}});
    }

	//新增
	$("body").on("click", "#addBean", function() {
    	_openNewWindows({
			url: "../../tpl/serviceworker/serviceworkeradd.html", 
			title: systemLanguage["com.skyeye.addPageTitle"][languageType],
			pageId: "serviceworkeradd",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
    });
	
	//编辑
	function edit(data) {
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/serviceworker/serviceworkeredit.html", 
			title: systemLanguage["com.skyeye.editPageTitle"][languageType],
			pageId: "serviceworkeredit",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	}
	
	//删除
	function del(data, obj) {
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
			layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "sealseserviceworker003", params: {rowId: data.id}, type: 'json', callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}
	
    exports('serviceworkerlist', {});
});