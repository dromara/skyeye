
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).define(['table', 'jquery', 'winui', 'form'], function (exports) {
	
	winui.renderColor();
	
	var $ = layui.$,
	form = layui.form,
	table = layui.table;
	console.log(parent.rowId);
    
    function loadTable(){
    	table.reload("messageTable", {where:{proName:$("#proName").val()}});
    }
    
    exports('mysmpropagelist', {});
});
