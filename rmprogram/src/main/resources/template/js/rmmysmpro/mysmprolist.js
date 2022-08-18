
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
	
	authBtn('1560823576324');

	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: sysMainMation.rmprogramBasePath + 'rmxcx022',
	    where: {proName: $("#proName").val()},
	    even: true,
	    page: true,
	    limits: [8, 16, 24, 32, 40, 48, 56],
	    limit: 8,
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
	        { field: 'proName', title: '项目名称', width: 120 },
	        { field: 'proDesc', title: '项目描述', width: 520 },
	        { field: 'pageNum', title: '页面数量', width: 120 },
	        { field: 'modelNum', title: '组件数量', width: 120 },
	        { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], width: 180 },
	        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 240, toolbar: '#tableBar'}
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
        } else if (layEvent === 'edit') { //编辑
        	edit(data);
        } else if (layEvent === 'consolePro') { //管理项目
        	consolePro(data);
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
		var msg = obj ? '确认删除项目【' + obj.data.proName + '】吗？' : '确认删除选中数据吗？';
		layer.confirm(msg, { icon: 3, title: '删除项目' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: sysMainMation.rmprogramBasePath + "rmxcx024", params: {rowId: data.id}, type: 'json', callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}
	
	//管理项目
	function consolePro(data) {
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/rmmysmpropage/mysmpropagelist.html", 
			title: "管理项目",
			pageId: "rmmysmproconsolePro",
			area: ['100vw', '100vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	}
	
	//编辑
	function edit(data) {
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/rmmysmpro/mysmproedit.html", 
			title: "编辑项目",
			pageId: "rmmysmproedit",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	}
	
	//刷新数据
    $("body").on("click", "#reloadTable", function() {
    	loadTable();
    });
    
    //新增
    $("body").on("click", "#addBean", function() {
    	_openNewWindows({
			url: "../../tpl/rmmysmpro/mysmproadd.html", 
			title: "新增项目",
			pageId: "rmmysmproadd",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
    });
    
    function loadTable() {
    	table.reloadData("messageTable", {where:{proName:$("#proName").val()}});
    }
    
    function refreshTable(){
    	table.reloadData("messageTable", {page: {curr: 1}, where:{proName:$("#proName").val()}});
    }
    
    exports('mysmprolist', {});
});
