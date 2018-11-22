
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).define(['treeGrid', 'jquery', 'winui', 'form'], function (exports) {
	
	winui.renderColor();
	
	var $ = layui.$,
	form = layui.form,
	treeGrid = layui.treeGrid;
	
	//搜索表单
	form.render();
	form.on('submit(formSearch)', function (data) {
    	//表单验证
        if (winui.verifyForm(data.elem)) {
        	loadTable();
        }
        return false;
	});
	
	treeGrid.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'get',
        idField: 'id',
        url: reqBasePath + 'systarea001',
        cellMinWidth: 100,
        treeId: 'id',//树形id字段名称
        treeUpId: 'pId',//树形父id字段名称
        treeShowName: 'name',//以树形式显示的字段
        cols: [[
            {field:'name', width:300, title: '区域名称'},
            {field:'id', width:100, title: 'id'},
            {field:'pId', title: 'pId'},
        ]],
        page:false
    });
	
    function loadTable(){
    	
    }
    
    exports('tarea', {});
});
