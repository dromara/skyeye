
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).define(['table', 'jquery', 'winui', 'form'], function (exports) {
	
	winui.renderColor();
	
	var $ = layui.$,
	form = layui.form,
	table = layui.table;
	
	//修改密码
	$("body").on("click", "#updatePassword", function(e){
		_openNewWindows({
			url: "../../tpl/syspersonal/editpassword.html", 
			title: "修改密码",
			pageId: "editpassword",
			area: ['700px', '300px'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	top.winui.window.msg("操作成功", {icon: 1,time: 2000});
                } else if (refreshCode == '-9999') {
                	top.winui.window.msg("操作失败", {icon: 2,time: 2000});
                }
			}});
	});
    
    exports('syspersonal', {});
});
