
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'treeGrid', 'jquery', 'winui', 'form'], function (exports) {
	winui.renderColor();
	
	var $ = layui.$,
		form = layui.form,
		treeGrid = layui.treeGrid;
	
	form.render();
	form.on('submit(formSearch)', function (data) {
        if (winui.verifyForm(data.elem)) {
        	loadTable();
        }
        return false;
	});
	
	treeGrid.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        idField: 'id',
        url: reqBasePath + 'systarea001',
        cellMinWidth: 100,
        where: {addressName: $("#addressName").val()},
        treeId: 'id',//树形id字段名称
        treeUpId: 'pId',//树形父id字段名称
        treeShowName: 'name',//以树形式显示的字段
        cols: [[
            {field:'name', width:300, title: '区域名称'},
            {field:'id', width:100, title: 'id'},
            {field:'pId', title: 'pId'},
        ]],
        isPage:false,
	    done: function(){
	    	matchingLanguage();
	    }
    });
	
	//刷新数据
    $("body").on("click", "#reloadTable", function(){
    	loadTable();
    });
	
	function loadTable(){
    	treeGrid.query("messageTable", {where:{addressName: $("#addressName").val()}});
    }
    
    exports('tarea', {});
});
