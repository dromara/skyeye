
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'tableTreeDj', 'jquery', 'winui', 'form'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		form = layui.form,
        tableTree = layui.tableTreeDj;
	
    tableTree.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        url: reqBasePath + 'systarea001',
        where: getTableParams(),
        cols: [[
            { field: 'name', width: 300, title: '区域名称' },
            { field: 'id', width: 100, title: 'id' },
            { field: 'pId', title: 'pId' },
        ]],
	    done: function(json) {
	    	matchingLanguage();
            initTableSearchUtil.initAdvancedSearch($("#messageTable")[0], json.searchFilter, form, "请输入名称", function () {
                tableTree.reload("messageTable", {page: {curr: 1}, where: getTableParams()});
            });
	    }
    }, {
        keyId: 'id',
        keyPid: 'pId',
        title: 'name',
    });
	
    form.render();
    $("body").on("click", "#reloadTable", function() {
        loadTable();
    });

    function loadTable() {
        tableTree.reload("messageTable", {where: getTableParams()});
    }

    function getTableParams() {
        return $.extend(true, {}, initTableSearchUtil.getSearchValue("messageTable"));
    }
    
    exports('tarea', {});
});
