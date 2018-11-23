
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).define(['jquery', 'winui', 'g6'], function (exports) {
	
	winui.renderColor();
	
	var index = parent.layer.getFrameIndex(window.name); //获取窗口索引
    var $ = layui.$;
    
    var tip_index = 0;//鼠标移上去的tip的index
    
	// 关闭 G6 的体验改进计划打点请求
    G6.track(false);
    
    //左右框高度
    $(".left-div").css({height:window.innerHeight});
    $(".right-div").css({height:window.innerHeight});
    
	var data = {
		"source" : {
			"nodes" : [ {
				"shape" : "circle",// 所用图形
				"label" : "文本",// 文本标签 || 文本图形配置
				"x" : 925.8749997662843,
				"y" : 286.9586341796129,
				"id" : "8b2a15da",// id 必须唯一
				"size" : [ 116.25000046743139, 108.08273164077423 ]// 尺寸 || [宽, 高]
			} ],
			"edges" : []
		}
	};
	
	var net = new G6.Net({
		id : "mountNode", // 此处替换容器id
		height: window.innerHeight, // 此处替换高度
		rollback : true, // 是否启用回滚机制
		mode : "edit", // 编辑模式
		grid: {//背景网格
	        forceAlign: true, // 是否支持网格对齐
	        cell: 10,         // 网格大小 
	    },
	});
	net.read(data);
	net.render();
	
	var i = 1;//位置坐标
	$("body").on("mouseenter", "#addCircle", function(e){//添加圆形节点
		tip_index = layer.tips('添加圆形', '#addCircle', {time: 0, tips: 3});
	}).on('mouseleave', '#addCircle', function(){
        layer.close(tip_index);
    });
	$("body").on("click", "#addCircle", function(e){//添加圆形节点
		net.add('node', {
	        shape: 'circle',
	        id: 'id' + i++,
	        x: 50 + i * 10,
	        y: 50 + i * 10
	    });
	    net.refresh();
	});
	
	$("body").on("mouseenter", "#addRect", function(e){//添加矩形
		tip_index = layer.tips('添加矩形', '#addRect', {time: 0, tips: 3});
	}).on('mouseleave', '#addRect', function(){
        layer.close(tip_index);
    });
	$("body").on("click", "#addRect", function(e){//添加矩形
		net.add('node', {
	        shape: 'rect',
	        id: 'id' + i++,
	        x: 50 + i * 10,
	        y: 50 + i * 10
	    });
	    net.refresh();
	});

	$('#delete').on('click', function() {//根据添加顺序删除
	    if (i > 1) {
	        i = i - 1;
	        const item = net.find('id' + i);
	        net.remove(item);
	        net.refresh();
	    }
	});

	$('#addCustom1').on('click', function() {//添加自定义节点图形
	    net.beginAdd('node', {
	        label: '[未定义]',
	        shape: 'circle'
	    });
	});

	$('#addCustom2').on('click', function() {//添加自定义节点图形
		console.log(1);
	    net.beginAdd('node', {label: '[未定义]'});
	    net.refresh();
	});

	$('#addCustom3').on('click', function() {//添加自定义节点图形
	    net.beginAdd('node', {
	        label: '[未定义]',
	        shape: 'customNode1'//<====
	    });
	});
	
	$("body").on("mouseenter", "#addLine", function(e){//添加普通直线
		tip_index = layer.tips('添加普通直线', '#addLine', {time: 0, tips: 3});
	}).on('mouseleave', '#addLine', function(){
        layer.close(tip_index);
    });
	$("body").on("click", "#addLine", function(e){//添加普通直线
		net.beginAdd('edge', {
	        shape: 'line'
	    });
	});
	
	$("body").on("mouseenter", "#addArrowLine", function(e){//添加箭头直线
		tip_index = layer.tips('添加箭头直线', '#addArrowLine', {time: 0, tips: 3});
	}).on('mouseleave', '#addArrowLine', function(){
        layer.close(tip_index);
    });
	$("body").on("click", "#addArrowLine", function(e){//添加箭头直线
		net.beginAdd('edge', {
	        shape: 'arrow'
	    });
	});
	
	$("body").on("mouseenter", "#addSmooth", function(e){//添加曲线
		tip_index = layer.tips('添加曲线', '#addSmooth', {time: 0, tips: 3});
	}).on('mouseleave', '#addSmooth', function(){
        layer.close(tip_index);
    });
	$("body").on("click", "#addSmooth", function(e){//添加曲线
		net.beginAdd('edge', {
	        shape: 'smooth'
	    });
	});
	
	$("body").on("mouseenter", "#drag", function(e){//拖拽模式
		tip_index = layer.tips('拖拽模式', '#drag', {time: 0, tips: 3});
	}).on('mouseleave', '#drag', function(){
        layer.close(tip_index);
    });
	$("body").on("click", "#drag", function(e){//拖拽模式
		net.changeMode('drag');
	});
	
	$("body").on("mouseenter", "#edit", function(e){//编辑模式
		tip_index = layer.tips('编辑模式', '#edit', {time: 0, tips: 3});
	}).on('mouseleave', '#edit', function(){
        layer.close(tip_index);
    });
	$("body").on("click", "#edit", function(e){//编辑模式
		net.changeMode('edit');
	});
	
	$("body").on("mouseenter", "#consoleJSON", function(e){//保存
		tip_index = layer.tips('保存', '#consoleJSON', {time: 0, tips: 3});
	}).on('mouseleave', '#consoleJSON', function(){
        layer.close(tip_index);
    });
	$("body").on("click", "#consoleJSON", function(e){//保存
		const saveData = net.save();
	    const json = JSON.stringify(saveData, null, 2);
	    console.log(saveData, json); // eslint-disable-line no-console
	});
	
    exports('makeflowchart', {});
});
