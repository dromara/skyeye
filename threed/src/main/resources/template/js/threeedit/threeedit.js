
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);

	// 加入模型到场景
	$("body").on("click", "#modeList .item", function(e){
		var url = $(this).attr("src");
		sysFileUtil.getFileByUrl(url, function(file) {
			var manager = new THREE.LoadingManager();
			editor.loader.loadFile(file, manager);
		});
	});
	
	matchingLanguage();
	
	//加入地板到场景
	$("body").on("click", "#floorList .item", function(e){
		var url = $(this).attr("src");
		var loader = new THREE.TextureLoader();
		//加载一个资源
		var groundTexture = loader.load(url);
		console.log(editor);
		console.log(groundTexture);
		groundTexture.wrapS = groundTexture.wrapT = THREE.RepeatWrapping;
		groundTexture.repeat.set(5, 5);
		//地基材料
		var groundMaterial = new THREE.MeshLambertMaterial({ map: groundTexture });
		var mesh = new THREE.Mesh(new THREE.PlaneBufferGeometry(30, 30), groundMaterial);
		mesh.rotation.x = -Math.PI / 2;
		mesh.receiveShadow = true;
		editor.scene.add(mesh);
	});
	
});

