
var folderId = "0";//父目录id

var type = "1";//类型  1目录  2流程图

var projectName = "";//项目名称
var projectId = "";//项目id

var rowId = "";//编辑中的id

var designId = "";//设计中的id

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).define(['treeGrid', 'jquery', 'winui', 'g6Plugins'], function (exports) {
	
	winui.renderColor();
	
	var index = parent.layer.getFrameIndex(window.name); //获取窗口索引
    var $ = layui.$,
    treeGrid = layui.treeGrid;
    
    var tip_index = 0;//鼠标移上去的tip的index
    
    var sel = null;//选中项
    
	// 关闭 G6 的体验改进计划打点请求
    G6.track(false);
    
    projectId = parent.rowId;
    projectName = parent.projectName;
    
    treeGrid.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        idField: 'id',
        url: reqBasePath + 'planprojectflow001?projectId=' + parent.rowId,
        cellMinWidth: 100,
        treeId: 'id',//树形id字段名称
        treeUpId: 'pId',//树形父id字段名称
        treeShowName: 'title',//以树形式显示的字段
        cols: [[
            {field:'title', width:140, title: '名称'},
            {field:'type', width:60, title: '类型', templet: function(d){
	        	if(d.type == "1"){
	        		return "目录";
	        	}else{
	        		return "流程图";
	        	}
	        }},
	        {field:'isShare', width:60, title: '分享', templet: function(d){
	        	if(d.isShare == "1"){
	        		return "<span class='state-new'>私有</span>";
	        	}else{
	        		return "<span class='state-up'>公开</span>";
	        	}
	        }},
	        { title: '操作', fixed: 'right', align: 'center', width: 150, toolbar: '#tableBar'}
        ]],
        isPage:false
    });
    
    treeGrid.on('tool(messageTable)', function (obj) { //注：tool是工具条事件名，test是table原始容器的属性 lay-filter="对应的值"
        var data = obj.data; //获得当前行数据
        var layEvent = obj.event; //获得 lay-event 对应的值
        if (layEvent === 'del') { //删除
        	del(data, obj);
        }else if (layEvent === 'edit') { //编辑
        	edit(data);
        }else if (layEvent === 'design') { //设计
        	designId = data.id;
        	$("#flowName").html(data.title);
        	AjaxPostUtil.request({url:reqBasePath + "planprojectflow006", params:{rowId: data.id}, type:'json', callback:function(json){
    			if(json.returnCode == 0){
    				$("#zzc").removeClass("zzc");
    				net.changeData();
    				if(isNull(json.bean.jsonContent)){
    					net.source({});
    				}else{
    					var jsonData = JSON.parse(json.bean.jsonContent);
    					net.source(jsonData.source.nodes, jsonData.source.edges);
    				}
    	            net.render();
    			}else{
    				top.winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
    			}
    		}});
        }else if(layEvent === 'addChildFolder'){ //新增目录
        	addChildFolder(data);
        }else if(layEvent === 'addChildFlower'){ //新增流程图
        	addChildFlower(data);
        }
    });
    
    //删除
	function del(data, obj){
		var msg = "";
		var title = '';
		if(data.type == '1'){
			title = '删除目录';
			msg = obj ? '确认删除目录【' + obj.data.title + '】吗？' : '确认删除选中数据吗？';
		}else{
			title = '删除流程图';
			msg = obj ? '确认删除流程图【' + obj.data.title + '】吗？' : '确认删除选中数据吗？';
		}
		layer.confirm(msg, { icon: 3, title: title }, function (index) {
			layer.close(index);
            //向服务端发送删除指令
            AjaxPostUtil.request({url:reqBasePath + "planprojectflow003", params:{rowId: data.id}, type:'json', callback:function(json){
    			if(json.returnCode == 0){
    				top.winui.window.msg("删除成功", {icon: 1,time: 2000});
    				if(data.id === designId){
    					$("#flowName").html("流程图");
    					$("#zzc").addClass("zzc");
    					net.changeData();
    					net.source({});
						net.render();
    				}
    				loadTable();
    			}else{
    				top.winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
    			}
    		}});
		});
	}
	
	//编辑
	function edit(data){
		rowId = data.id;
		folderId = data.pId;
    	type = data.type;
		var title = '';
		if(data.type == '1'){
			title = '编辑目录';
		}else{
			title = '编辑流程图';
		}
		_openNewWindows({
			url: "../../tpl/planprojectflow/planprojectflowedit.html", 
			title: title,
			pageId: "rmgroupedit",
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	top.winui.window.msg("操作成功", {icon: 1,time: 2000});
                	loadTable();
                } else if (refreshCode == '-9999') {
                	top.winui.window.msg("操作失败", {icon: 2,time: 2000});
                }
			}});
	}
	
	//新增子目录
	function addChildFolder(data){
		folderId = data.id;
    	type = "1";
		_openNewWindows({
			url: "../../tpl/planprojectflow/planprojectflowadd.html", 
			title: "新增目录",
			pageId: "companyjobedit",
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	top.winui.window.msg("操作成功", {icon: 1,time: 2000});
                	loadTable();
                } else if (refreshCode == '-9999') {
                	top.winui.window.msg("操作失败", {icon: 2,time: 2000});
                }
			}});
	}
	
	//新增子流程图
	function addChildFlower(data){
		folderId = data.id;
    	type = "2";
		_openNewWindows({
			url: "../../tpl/planprojectflow/planprojectflowadd.html", 
			title: "新增流程图",
			pageId: "companyjobedit",
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	top.winui.window.msg("操作成功", {icon: 1,time: 2000});
                	loadTable();
                } else if (refreshCode == '-9999') {
                	top.winui.window.msg("操作失败", {icon: 2,time: 2000});
                }
			}});
	}
    
    //新增一级目录
    $("body").on("click", "#addFolder", function(){
    	folderId = "0";
    	type = "1";
		_openNewWindows({
			url: "../../tpl/planprojectflow/planprojectflowadd.html", 
			title: "新增目录",
			pageId: "companyjobedit",
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	top.winui.window.msg("操作成功", {icon: 1,time: 2000});
                	loadTable();
                } else if (refreshCode == '-9999') {
                	top.winui.window.msg("操作失败", {icon: 2,time: 2000});
                }
			}});
    });
    
	//新增一级流程图
    $("body").on("click", "#addFlower", function(){
    	folderId = "0";
    	type = "2";
		_openNewWindows({
			url: "../../tpl/planprojectflow/planprojectflowadd.html", 
			title: "新增流程图",
			pageId: "companyjobedit",
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	top.winui.window.msg("操作成功", {icon: 1,time: 2000});
                	loadTable();
                } else if (refreshCode == '-9999') {
                	top.winui.window.msg("操作失败", {icon: 2,time: 2000});
                }
			}});
    });
    
    function loadTable(){
    	treeGrid.query("messageTable", {where:{}});
    }
    
	var data = {};
	
	const layout_dagre = new G6.Plugins['layout.dagre']({
        rankdir: 'TB',//可取值为： 'TB', 'BT', 'LR', or 'RL' 默认值为 'TB'
        //nodesep:10,//节点间距
        // useEdgeControlPoint:false,//生成边控制点 默认值为 true
    });
	
	var net = new G6.Net({
		id : "mountNode", // 此处替换容器id
		height: window.innerHeight, // 此处替换高度
		rollback : true, // 是否启用回滚机制
		mode : "edit", // 编辑模式
		fitView: 'cc', // 自适应视口为左上
		grid: {//背景网格
	        forceAlign: true, // 是否支持网格对齐
	        cell: 10,         // 网格大小 
	    },
	    plugins: [layout_dagre],
	});
	net.read(data);
	net.render();
	
	net.tooltip({
        title: '节点信息', // @type {String} 标题
        split: ':',  // @type {String} 分割符号
        dx: 10,       // @type {Number} 水平偏移
        dy: 10        // @type {Number} 竖直偏移
    });
	
	net.node().tooltip(['label']);
    net.edge().tooltip(['label']);
	
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
	
	/*$("body").on("mouseenter", "#addLineFlow", function(e){//添加折线
		tip_index = layer.tips('添加折线', '#addLineFlow', {time: 0, tips: 3});
	}).on('mouseleave', '#addLineFlow', function(){
        layer.close(tip_index);
    });
	$("body").on("click", "#addLineFlow", function(e){//添加折线
		net.beginAdd('edge', {
	        shape: 'polyLine'
	    });
	});*/
	
	$("body").on("mouseenter", "#addPolyLineFlow", function(e){//添加箭头折线
		tip_index = layer.tips('添加箭头折线', '#addPolyLineFlow', {time: 0, tips: 3});
	}).on('mouseleave', '#addPolyLineFlow', function(){
        layer.close(tip_index);
    });
	$("body").on("click", "#addPolyLineFlow", function(e){//添加箭头折线
		net.beginAdd('edge', {
	        shape: 'polyLineFlow'
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
	
	$("body").on("mouseenter", "#reLayout", function(e){//自动布局
		tip_index = layer.tips('自动布局', '#reLayout', {time: 0, tips: 3});
	}).on('mouseleave', '#reLayout', function(){
        layer.close(tip_index);
    });
	$("body").on("click", "#reLayout", function(e){//自动布局
		var a = net.save();
		net.changeData();
		$.each(a.source.edges, function(index, item){
			delete item.x;
			delete item.y;
		});
		$.each(a.source.nodes, function(index, item){
			delete item.x;
			delete item.y;
		});
        net.source(a.source.nodes, a.source.edges);
        net.render();
	});
	
	$("body").on("mouseenter", "#consoleJSON", function(e){//保存
		tip_index = layer.tips('保存', '#consoleJSON', {time: 0, tips: 3});
	}).on('mouseleave', '#consoleJSON', function(){
        layer.close(tip_index);
    });
	$("body").on("click", "#consoleJSON", function(e){//保存
		const saveData = net.save();
	    const json = JSON.stringify(saveData, null, 2);
	    AjaxPostUtil.request({url:reqBasePath + "planprojectflow007", params:{rowId: designId, jsonContent: json}, type:'json', callback:function(json){
			if(json.returnCode == 0){
				top.winui.window.msg("保存成功", {icon: 1,time: 2000});
			}else{
				top.winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
			}
		}});
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
//	    console.log("选中类型:", ev.itemType);
//	    console.log(ev);
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
