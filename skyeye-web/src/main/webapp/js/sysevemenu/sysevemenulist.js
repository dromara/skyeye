
var rowId = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).define(['table', 'jquery', 'winui', 'form', 'fsCommon', 'fsTree'], function (exports) {
	winui.renderColor();
	
	var $ = layui.$,
	form = layui.form,
	fsTree = layui.fsTree,
	fsCommon = layui.fsCommon,
	table = layui.table;
	var parentId = "";
	
	//搜索表单
	form.render();
	form.on('submit(formSearch)', function (data) {
    	//表单验证
        if (winui.verifyForm(data.elem)) {
        	loadTable();
        }
        return false;
	});
	
	//删除
	function del(data, obj){
		var msg = obj ? '确认删除菜单【' + obj.data.menuName + '】吗？' : '确认删除选中数据吗？';
		layer.confirm(msg, { icon: 3, title: '删除系统菜单' }, function (index) {
			layer.close(index);
            //向服务端发送删除指令
            AjaxPostUtil.request({url:reqBasePath + "sys011", params:{rowId: data.id}, type:'json', callback:function(json){
    			if(json.returnCode == 0){
    				top.winui.window.msg("删除成功", {icon: 1,time: 2000});
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
		_openNewWindows({
			url: "../../tpl/sysevemenu/sysevemenuedit.html", 
			title: "编辑菜单",
			pageId: "sysevemenuedit",
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	top.winui.window.msg("操作成功", {icon: 1,time: 2000});
                	loadTable();
                } else if (refreshCode == '-9999') {
                	top.winui.window.msg("操作失败", {icon: 2,time: 2000});
                }
			}});
	}
	
	function initLoadTable(){
		//表格渲染
		table.render({
		    id: 'messageTable',
		    elem: '#messageTable',
		    method: 'post',
		    url: reqBasePath + 'sys006',
		    where:{menuName:$("#menuName").val(), menuUrl:$("#menuUrl").val(), parentId:parentId, menuLevel:$("#menuLevel").val()},
		    even:true,  //隔行变色
		    page: true,
		    limits: [8, 16, 24, 32, 40, 48, 56],
		    limit: 8,
		    cols: [[
		        { title: '序号', type: 'numbers'},
		        { field: 'menuName', title: '菜单名称', width: 120 },
		        { field: 'menuIcon', title: '图标码', width: 180 },
		        { field: 'id', title: '图标', width: 60, templet: function(d){
		        	return '<i class="fa fa-fw ' + d.menuIcon + '"></i>';
		        }},
		        { field: 'titleName', title: '标题名称', width: 120 },
		        { field: 'menuLevel', title: '菜单级别', width: 180, templet: function(d){
		        	if(d.parentId == '0'){
		        		return "创世菜单";
		        	}else{
		        		return "子菜单-->" + d.menuLevel + "级子菜单";
		        	}
		        }},
		        { field: 'roleNum', title: '使用角色数量', width: 150 },
		        { field: 'menuParentName', title: '父菜单', width: 100 },
		        { field: 'menuType', title: '菜单类型', width: 100 },
		        { field: 'menuUrl', title: '菜单链接', width: 160 },
		        { field: 'menuSysType', title: '系统菜单', width: 100, templet: function(d){
		        	if(d.menuSysType == 2){
		        		return '否';
		        	}else if(d.menuSysType == 1){
		        		return '是';
		        	}else{
		        		return '参数错误';
		        	}
		        }},
		        { field: 'createTime', title: '创建时间', width: 180 },
		        { field: 'userName', title: '创建人', width: 150 },
		        { title: '操作', fixed: 'right', align: 'center', width: 120, toolbar: '#tableBar'}
		    ]]
		});
		
		table.on('tool(messageTable)', function (obj) { //注：tool是工具条事件名，test是table原始容器的属性 lay-filter="对应的值"
	        var data = obj.data; //获得当前行数据
	        var layEvent = obj.event; //获得 lay-event 对应的值
	        if (layEvent === 'del') { //删除
	        	del(data, obj);
	        }else if (layEvent === 'edit') { //编辑
	        	edit(data);
	        }
	    });
		
		showGrid({
		 	id: "menuLevel",
		 	url: reqBasePath + "sys021",
		 	params: {},
		 	pagination: false,
		 	template: getFileContent('tpl/template/select-option.tpl'),
		 	ajaxSendLoadBefore: function(hdb){
		 	},
		 	ajaxSendAfter:function(json){
		 		console.log(json);
		 		form.render('select');
		 	}
		});
	}
	
	/********* tree 处理   start *************/

	var trees = {};

	var treeDoms = $("ul.fsTree");
	if(treeDoms.length > 0) {
		$(treeDoms).each(function(i) {
			var treeId = $(this).attr("id");
			var funcNo = $(this).attr("funcNo");
			var url = $(this).attr("url");
			var tree = fsTree.render({
				id: treeId,
				funcNo: funcNo,
				url: url + "?userToken=" + getCookie('userToken'),
				clickCallback: onClickTree,
				onDblClick: onClickTree,
				getTree: getTree
			});
			if(treeDoms.length == 1) {
				trees[treeId] = tree;
			} else {
				//深度拷贝对象
				trees[treeId] = $.extend(true, {}, tree);
			}
		});
		//绑定按钮事件
		fsCommon.buttonEvent("tree", getTree);
	}
	
	initLoadTable();

	function getTree(treeId) {
		if($.isEmpty(trees)) {
			fsCommon.warnMsg("未配置tree！");
			return;
		}
		if($.isEmpty(treeId)) {
			treeId = "treeDemo";
		}
		return trees[treeId];
	}
	
	//异步加载的方法
	function onClickTree(event, treeId, treeNode) {
		if(treeNode == undefined) {
			parentId = "";
		} else {
			parentId = treeNode.id;
		}
		loadTable();
	}
	
	/********* tree 处理   end *************/
	
	//刷新数据
    $("body").on("click", "#reloadTable", function(){
    	loadTable();
    });
    
    //新增菜单
    $("body").on("click", "#addBean", function(){
    	_openNewWindows({
			url: "../../tpl/sysevemenu/sysevemenuadd.html", 
			title: "新增菜单",
			pageId: "sysevemenuadd",
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
    	table.reload("messageTable", {where:{menuName:$("#menuName").val(), menuUrl:$("#menuUrl").val(), parentId:parentId, menuLevel:$("#menuLevel").val()}});
    }
    
    exports('sysevemenulist', {});
});
