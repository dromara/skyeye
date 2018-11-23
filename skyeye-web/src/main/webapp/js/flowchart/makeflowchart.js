
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).define(['jquery', 'winui', 'g6'], function (exports) {
	
	winui.renderColor();
	
	var index = parent.layer.getFrameIndex(window.name); //获取窗口索引
    var $ = layui.$;
    
	var data = {
		"source" : {
			"nodes" : [ {
				"shape" : "circle",
				"label" : "文本",
				"x" : 925.8749997662843,
				"y" : 286.9586341796129,
				"id" : "8b2a15da",
				"size" : [ 116.25000046743139, 108.08273164077423 ]
			} ],
			"edges" : []
		}
	};
	var net = new G6.Net({
		id : "mountNode", // 此处替换容器id
		height : 900, // 此处替换高度
		rollback : true, // 是否启用回滚机制
		mode : "edit", // 编辑模式
	});
	net.read(data);
	net.render();

	
    exports('makeflowchart', {});
});
