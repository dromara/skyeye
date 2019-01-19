
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).define(['table', 'jquery', 'winui', 'form'], function (exports) {
	
	winui.renderColor();
	
	var $ = layui.$,
	form = layui.form,
	table = layui.table;
	
	//搜索表单
	form.render();
	form.on('submit(formSearch)', function (data) {
    	//表单验证
        if (winui.verifyForm(data.elem)) {
        	loadTable();
        }
        return false;
	});
	
	//输入框回车
    $("#browserAddress").keydown(function(e){
    	if (e.keyCode == "13") {
    		$("#contentUrl").attr('src', $("#browserAddress").val());
    		return false;
    	}
    });
    
    //刷新
    $("body").on("click", "#refresh", function(e){
    	$("#contentUrl").attr('src', $("#browserAddress").val());
    });
    
    //主页
    $("body").on("click", "#homePage", function(e){
    	$("#browserAddress").val('https://www.baidu.com/');
    	$("#contentUrl").attr('src', 'https://www.baidu.com/');
    });
    
    exports('browser', {});
});
