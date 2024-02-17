
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
	
	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: sysMainMation.diskCloudBasePath + 'fileconsole014',
	    where: getTableParams(),
	    even: true,
	    page: true,
		limits: getLimits(),
		limit: getLimit(),
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
	        { field: 'fileName', title: '文件名', width: 150 },
	        { field: 'fileTypeName', title: '文件类型', width: 120 },
	        { field: 'createTime', title: '删除时间', width: 180 },
	        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 120, toolbar: '#tableBar'}
	    ]],
	    done: function(json) {
	    	matchingLanguage();
			initTableSearchUtil.initAdvancedSearch(this, res.searchFilter, form, "请输入文件/文件夹名称", function () {
				table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
			});
	    }
	});
	
	table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'reduction') { //还原
        	reduction(data);
        }
    });
	
	// 还原
	function reduction(data){
		var msg = '确认还原' + data.fileTypeName + '【' + data.fileName + '】吗？';
		layer.confirm(msg, { icon: 3, title: '还原文件' }, function (index) {
			layer.close(index);
			parent.refreshCode = '0';
            AjaxPostUtil.request({url: sysMainMation.diskCloudBasePath + "deleteFileRecycleBinById", params: {id: data.id}, type: 'json', method: 'DELETE', callback: function (json) {
				winui.window.msg("还原成功", {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}

	form.render();
	$("body").on("click", "#reloadTable", function() {
		loadTable();
	});

	function loadTable() {
		table.reloadData("messageTable", {where: getTableParams()});
	}

	function getTableParams() {
		return $.extend(true, {}, initTableSearchUtil.getSearchValue("messageTable"));
	}
    
    exports('recycleBinListPage', {});
});
