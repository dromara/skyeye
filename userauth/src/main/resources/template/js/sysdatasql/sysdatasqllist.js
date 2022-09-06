
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		table = layui.table;
	
	authBtn('1566201610886');
	
	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: reqBasePath + 'sysdatasqlbackups001',
	    where:{},
	    even: true,
	    page: true,
	    limits: [8, 16, 24, 32, 40, 48, 56],
	    limit: 8,
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
	        { field: 'sqlTitle', title: '文件名称', align: 'center', width: 240 },
	        { field: 'fileSize', title: '文件大小', align: 'center', width: 120 },
	        { field: 'mysqlVersion', title: '数据库版本', align: 'center', width: 150 },
	        { field: 'createTime', title: '备份时间', align: 'center', width: 150 },
	        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 220, toolbar: '#tableBar'}
	    ]],
	    done: function(json) {
	    	matchingLanguage();
	    }
	});
	
	table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'reduction') { //还原
        	reduction(data);
        }
    });
	
	//还原数据库
	function reduction(data) {
		var msg = '确认还原该版本吗？';
		layer.confirm(msg, { icon: 3, title: '数据库还原' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: reqBasePath + "sysdatasqlbackups004", params: {rowId: data.id}, type: 'json', callback: function (json) {
				winui.window.msg("还原成功", {icon: 1, time: 2000});
    		}});
		});
	}

	//刷新数据
    $("body").on("click", "#reloadTable", function() {
    	loadTable();
    });
    
    //数据备份
    $("body").on("click", "#addBean", function() {
    	_openNewWindows({
			url: "../../tpl/sysdatasql/sysdatatablelist.html", 
			title: "数据备份",
			pageId: "sysdatatablelistpage",
			maxmin: true,
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
    });
    
    function loadTable() {
    	table.reloadData("messageTable", {where:{}});
    }
    
    exports('sysdatasqllist', {});
});
