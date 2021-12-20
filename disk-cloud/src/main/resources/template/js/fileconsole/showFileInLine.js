
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
		var $ = layui.$;
		var str = '';
		
		//图片
		$.each(imageType, function(i, item){
			str += '<a href="javascript:;" class="layui-btn layui-btn-primary">' + item + '</a>';
		});
		$("#showPicType").html(str);
		
		//office
		str = '';
		$.each(officeType, function(i, item){
			str += '<a href="javascript:;" class="layui-btn layui-btn-primary">' + item + '</a>';
		});
		$("#showOfficeType").html(str);
		
		//视频
		str = '';
		$.each(vedioType, function(i, item){
			str += '<a href="javascript:;" class="layui-btn layui-btn-primary">' + item + '</a>';
		});
		$("#showVedioType").html(str);
		
		//压缩包
		str = '';
		$.each(packageType, function(i, item){
			str += '<a href="javascript:;" class="layui-btn layui-btn-primary">' + item + '</a>';
		});
		$("#showPackageType").html(str);
		
		//电子书
		str = '';
		$.each(epubType, function(i, item){
			str += '<a href="javascript:;" class="layui-btn layui-btn-primary">' + item + '</a>';
		});
		$("#epubType").html(str);
		
		//其他文件
		str = '';
		$.each(aceType, function(i, item){
			str += '<a href="javascript:;" class="layui-btn layui-btn-primary">' + item + '</a>';
		});
		$("#showAceType").html(str);
		
		matchingLanguage();
		
	});
});

