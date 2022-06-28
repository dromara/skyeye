
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);

	// 添加方向光光源
	threeUtil.initDirectionalLight();

	// 加入模型到场景
	$("body").on("click", "#modeList .item", function (e) {
		var url = $(this).attr("src");
		sysFileUtil.getFileByUrl(url, function(file) {
			var manager = new THREE.LoadingManager();
			editor.loader.loadFile(file, manager);
		});
	});
	
	matchingLanguage();
	
	// 加入地板到场景
	$("body").on("click", "#floorList .item", function (e) {
		var url = $(this).attr("src");
		threeUtil.initFloor(url);
	});
	
});

