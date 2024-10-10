
var filePath = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form'], function (exports) {
	winui.renderColor();
	// 模板分组ID
	groupId = GetUrlParam("id");
	var $ = layui.$,
		form = layui.form,
		table = layui.table;
	
	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: reqBasePath + 'codemodel015',
	    where:{groupId:groupId},
	    even: true,
	    page: true,
		limits: getLimits(),
		limit: getLimit(),
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
	        { field: 'tableName', title: '表名', width: 180 },
	        { field: 'filePath', title: '文件名', width: 300 },
	        { field: 'isExist', title: '是否可下载', width: 120 },
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
        if (layEvent === 'download') { //下载
        	download(data, obj);
        } else if (layEvent === 'creatFile') { //同步文件
        	creatFile(data, obj);
        }
    });
	
	//下载
	function download(data) {
		filePath = data.filePath;
		window.open(reqBasePath + "codemodel017?filePath=" + filePath);
	}
	
	//同步文件
	function creatFile(data) {
		filePath = data.filePath;
		AjaxPostUtil.request({url: reqBasePath + "codemodel016", params: {filePath:filePath}, type: 'json', callback: function (json) {
			winui.window.msg('生成完成，请下载。', {icon: 1, time: 2000});
			loadTable();
		}});
	}
	
	
	form.render();
	form.on('submit(formSearch)', function (data) {
        if (winui.verifyForm(data.elem)) {
        	refreshTable();
        }
        return false;
	});
	
	//刷新数据
    $("body").on("click", "#reloadTable", function() {
    	loadTable();
    });
    
    function loadTable() {
    	table.reloadData("messageTable", {where:{groupId:groupId}});
    }
    
    function refreshTable(){
    	table.reloadData("messageTable", {page: {curr: 1}, where:{groupId:groupId}});
    }
    
    exports('codemodelhistorylist', {});
});
