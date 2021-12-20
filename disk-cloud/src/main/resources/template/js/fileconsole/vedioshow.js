
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'dplayer'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
		var $ = layui.$;
		
		matchingLanguage();
		const dp = new DPlayer({
		    container: document.getElementById('vedioShow'),
		    autoplay: true,
		    screenshot: false,
		    video: {
		        url: fileBasePath + parent.fileUrl,
		        pic: fileBasePath + parent.fileThumbnail,
		        thumbnails: fileBasePath + parent.fileThumbnail,
		    }
		});
	});
});

