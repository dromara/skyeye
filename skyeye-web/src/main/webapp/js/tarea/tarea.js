
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
	
    function loadTable(){
    	
    }
    
    exports('tarea', {});
});
