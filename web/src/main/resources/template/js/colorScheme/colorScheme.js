
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	var $ = layui.$;
	
	matchingLanguage();
	
	$("body").on("click", ".layadmin-setTheme-color-scheme", function (e) {
		var className = $(this).attr("className");
		parent.$("body").attr("class", className);
	});
	
    exports('blogitem', {});
});
