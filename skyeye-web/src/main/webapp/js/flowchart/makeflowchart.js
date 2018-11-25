
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).define(['jquery', 'winui', 'g6'], function (exports) {
	
	winui.renderColor();
	
	var index = parent.layer.getFrameIndex(window.name); //获取窗口索引
    var $ = layui.$;
    
    var tip_index = 0;//鼠标移上去的tip的index
    
    var sel = null;//选中项
    
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
	
	//设置点击选中项
	net.on("itemactived", function(e){
		sel = e.item;
	});
	
	$("body").on("mouseenter", "#delete", function(e){//删除
		tip_index = layer.tips('删除', '#delete', {time: 0, tips: 3});
	}).on('mouseleave', '#delete', function(){
        layer.close(tip_index);
    });
	$("body").on("click", "#delete", function(e){//删除
		if(sel){
			net.remove(sel);
		}
		sel = null;
	});
	
	$("body").on("mouseenter", "#addCustom1", function(e){//添加起止节点
		tip_index = layer.tips('起止节点', '#addCustom1', {time: 0, tips: 3});
	}).on('mouseleave', '#addCustom1', function(){
        layer.close(tip_index);
    });
	$("body").on("click", "#addCustom1", function(e){//添加起止节点
		net.beginAdd('node', {
	        label: '[起止节点]',
	        shape: 'circle',
	        color: '#EE4000'
	    });
	});
	
	$("body").on("mouseenter", "#addCustom2", function(e){//添加常规节点
		tip_index = layer.tips('常规节点', '#addCustom2', {time: 0, tips: 3});
	}).on('mouseleave', '#addCustom2', function(){
        layer.close(tip_index);
    });
	$("body").on("click", "#addCustom2", function(e){//添加常规节点
		net.beginAdd('node', {
			label: '[常规节点]',
			shape: 'rect',
			color: '#5CACEE',
		});
	    net.refresh();
	});
	
	$("body").on("mouseenter", "#addCustom3", function(e){//添加条件节点
		tip_index = layer.tips('条件节点', '#addCustom3', {time: 0, tips: 3});
	}).on('mouseleave', '#addCustom3', function(){
        layer.close(tip_index);
    });
	$("body").on("click", "#addCustom3", function(e){//添加条件节点
		net.beginAdd('node', {
	        label: '[条件节点]',
	        shape: 'rhombus',
	        color: '#54FF9F',
	    });
	});

	$("body").on("mouseenter", "#addLine", function(e){//添加普通直线
		tip_index = layer.tips('添加普通直线', '#addLine', {time: 0, tips: 3});
	}).on('mouseleave', '#addLine', function(){
        layer.close(tip_index);
    });
	$("body").on("click", "#addLine", function(e){//添加普通直线
		net.beginAdd('edge', {
	        shape: 'line',
	        color: 'red',
	    });
	});
	
	$("body").on("mouseenter", "#addArrowLine", function(e){//添加箭头直线
		tip_index = layer.tips('添加箭头直线', '#addArrowLine', {time: 0, tips: 3});
	}).on('mouseleave', '#addArrowLine', function(){
        layer.close(tip_index);
    });
	$("body").on("click", "#addArrowLine", function(e){//添加箭头直线
		net.beginAdd('edge', {
	        shape: 'arrow',
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
	
	$("body").on("mouseenter", "#addArrowSmooth", function(e){//添加箭头曲线
		tip_index = layer.tips('添加箭头曲线', '#addArrowSmooth', {time: 0, tips: 3});
	}).on('mouseleave', '#addArrowSmooth', function(){
        layer.close(tip_index);
    });
	$("body").on("click", "#addArrowSmooth", function(e){//添加箭头曲线
		net.beginAdd('edge', {
	        shape: 'smoothArrow'
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
	    console.log(saveData, json);
	});
	
	//可编辑标签属性
	var Util = G6.Util;
	var input = Util.createDOM('<input class="g6-label-input" />', {
		position : 'absolute',
		zIndex : 10,
		display : 'none'
	});
	function hasClass(shape, className) {
		if (shape) {
			const
			clasees = shape.get('class');
			if (clasees && clasees.indexOf(className) !== -1) {
				return true;
			}
		}
		return false;
	}
	function showInputLabel(node) {
		if (!node) {
			return;
		}
		const group = node.get('group');
		const label = group.findBy(function(child) {
			if (hasClass(child, 'label')) {
				return true;
			}
			return false;
		});
		const rootGroup = net.get('rootGroup');
		const bbox = Util.getBBox(label, rootGroup);
		const borderWidth = 1;
		const text = label.attr('text');
		clearAllActived();
		input.value = text;
		input.show();
		input.css({
			top : bbox.minY - borderWidth + 'px',
			left : bbox.minX - borderWidth + 'px',
			width : bbox.width + 'px',
			height : bbox.height + 'px',
			padding : '0px',
			margin : '0px',
			border : borderWidth + 'px solid #999'
		});
		input.focus();
		input.node = node;
	}
	function updateLabel() {
		if (input.visibility) {
			const node = input.node;
			clearAllActived();
			if (input.value !== node.get('model').name) {
				if (input.value) {
					net.update(node, {
						label : input.value
					});
				}
			}
			input.hide();
		}
	}
	function clearAllActived() {
		net.clearAllActived();
		net.refresh(false);
	}
	input.hide = function() {
		input.css({
			display : 'none'
		});
		input.visibility = false;
	};
	input.show = function() {
		input.css({
			display : 'block'
		});
		input.visibility = true;
	};
	input.on('keydown', function(ev) {
		if (ev.keyCode === 13) {
			updateLabel();
		}
	});
	input.on('blur', function(ev) {
		updateLabel();
	});
	const graphContainer = net.get('graphContainer');//获取图表内部容器
	graphContainer.appendChild(input);//追加input输入框
	graphContainer.oncontextmenu = function (e) { return false; }//阻止默认右键菜单
	net.on('contextmenu', function(ev) {// 鼠标右键点击事件
	    console.log("选中类型:", ev.itemType);
	    console.log(ev);
	});
	net.on('itemmouseenter', function(ev) {//子项鼠标悬浮
	    const item = ev.item;
	    net.update(item, {
//	        color: 'red',
	    });
	    net.refresh();
	});
	net.on('itemmouseleave', function(ev) {//子项鼠标离开事件
	    const item = ev.item;
	    net.update(item, {
//	        color: null
	    });
	    net.refresh();
	});
	net.on('itemmousedown', function(ev) {//子项鼠标按下
	    const item = ev.item;
	    net.update(item, {
//	        color: '#9ef'
	    });
	    net.refresh();
	});
	net.on('itemmouseup', function(ev) {//子项鼠标弹起
	    const item = ev.item;
	    net.update(item, {
//	        color: 'null'
	    });
	    net.refresh();
	});
	net.on('dragmove', function(ev) {//拖拽隐藏
	    input.hide();
	});
	net.on('dblclick', function(ev) {//双击显示
	    const item = ev.item;
	    const shape = ev.shape;
	    if (hasClass(shape, 'label') && item && item.get('type') === 'node') {//节点的情况下
	        showInputLabel(item);
	    }
	});
	
	exports('makeflowchart', {});
});
