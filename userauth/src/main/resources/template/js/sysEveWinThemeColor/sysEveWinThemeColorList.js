
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
	
	authBtn('1552962892226');
	
	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: reqBasePath + 'sysevewinthemecolor001',
	    where:{colorClass: $("#colorClass").val()},
	    even: true,
	    page: true,
	    limits: [8, 16, 24, 32, 40, 48, 56],
	    limit: 8,
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
	        { field: 'colorClass', title: '颜色属性', width: 180 },
	        { field: 'id', title: '主题颜色', width: 180, templet: function (d) {
	        	return '<div class="color-choose" style="width: auto"><div class="' + d.colorClass + '"></div></div>';
	        }},
	        { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], width: 180 },
	        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 240, toolbar: '#tableBar'}
	    ]],
	    done: function(json) {
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
	function del(data, obj) {
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: reqBasePath + "sysevewinthemecolor003", params: {rowId: data.id}, type: 'json', callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}
	
	//编辑分类
	function edit(data) {
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/sysEveWinThemeColor/sysEveWinThemeColorEdit.html",
			title: "编辑主题",
			pageId: "sysEveWinThemeColorEdit",
			area: ['400px', '400px'],
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
			url: "../../tpl/sysEveWinThemeColor/sysEveWinThemeColorAdd.html",
			title: "新增主题",
			pageId: "sysEveWinThemeColorAdd",
			area: ['400px', '400px'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
    });
    
    function loadTable() {
    	table.reloadData("messageTable", {where:{colorClass: $("#colorClass").val()}});
    }
    
    function refreshTable(){
    	table.reloadData("messageTable", {page: {curr: 1}, where:{colorClass: $("#colorClass").val()}});
    }
    
    exports('sysEveWinThemeColorList', {});
});
