
// 3D编辑器相关工具类
var threeUtil = {

    // 编辑器网格数量
    gridWidth: 30,
    gridHeight: 30,
    gridBigCell: 6,

    /**
     * 添加方向光光源
     */
    initDirectionalLight: function () {
        var directionalLight = new THREE.DirectionalLight(0xaaaaaa)
        directionalLight.position.set(0, 200, 100)
        directionalLight.name = "方向光";
        // 开启阴影投射
        directionalLight.castShadow = true;
        editor.scene.add(directionalLight);
    },

    /**
     * 添加地板
     *
     * @param url 地板文件路径
     */
    initFloor: function (url) {
        // 加载一个资源
        var loader = new THREE.TextureLoader();
        var groundTexture = loader.load(url);
        groundTexture.wrapS = groundTexture.wrapT = THREE.RepeatWrapping;
        groundTexture.repeat.set(threeUtil.gridWidth / threeUtil.gridBigCell, threeUtil.gridHeight / threeUtil.gridBigCell);
        // 地基材料
        var groundMaterial = new THREE.MeshLambertMaterial({ map: groundTexture });
        var floor = new THREE.Mesh(new THREE.PlaneBufferGeometry(threeUtil.gridWidth, threeUtil.gridHeight), groundMaterial);
        floor.rotation.x = -Math.PI / 2;
        // 地板接受阴影开启
        floor.receiveShadow = true;
        floor.position.y = 0;
        floor.name = "地板";
        // 设置模型的每个部位都可以投影
        floor.traverse(function (child) {
            if (child.isMesh) {
                child.castShadow = true
                child.receiveShadow = true
            }
        })
        editor.scene.add(floor);
    }


};