
var rowId = "";
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).define(['table', 'jquery', 'winui', 'form'], function (exports) {
	
	winui.renderColor();
	
	var $ = layui.$,
	form = layui.form,
	table = layui.table;
	//表格渲染
	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: reqBasePath + 'sys006',
	    where:{menuName:$("#menuName").val(), menuUrl:$("#menuUrl").val()},
	    even:true,  //隔行变色
	    page: true,
	    limits: [8, 16, 24, 32, 40, 48, 56],
	    limit: 8,
	    cols: [[
	        { field: 'id', type: 'checkbox' },
	        { field: 'menuName', title: '菜单名称', width: 120 },
	        { field: 'menuIcon', title: '图标码', width: 120 },
	        { field: 'titleName', title: '标题名称', width: 120 },
	        { field: 'menuLevel', title: '菜单级别', width: 120, templet: function(d){
	        	if(d.parentId == '0'){
	        		return "创世菜单";
	        	}else{
	        		return "子菜单-->" + d.menuLevel + "级子菜单";
	        	}
	        }},
	        { field: 'menuType', title: '菜单类型', width: 100 },
	        { field: 'menuUrl', title: '菜单链接', width: 100 },
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
	        { title: '操作', fixed: 'right', align: 'center', width: 120, toolbar: '#tableBar'}
	    ]]
	});
	
	table.on('tool(messageTable)', function (obj) { //注：tool是工具条事件名，test是table原始容器的属性 lay-filter="对应的值"
        var data = obj.data; //获得当前行数据
        var layEvent = obj.event; //获得 lay-event 对应的值
        if (layEvent === 'del') { //删除
        	del(data);
        }else if (layEvent === 'edit') { //编辑
        	edit(data);
        }
    });
	
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
	function del(data){
		
	}
	
	//编辑
	function edit(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/syseveuser/syseveuseredit.html", 
			title: "编辑用户",
			pageId: "syseveuseredit",
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	top.winui.window.msg("操作成功", {icon: 1,time: 2000});
                	loadTable();
                } else if (refreshCode == '-9999') {
                	top.winui.window.msg("操作失败", {icon: 2,time: 2000});
                }
			}});
	}
	
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
    	table.reload("messageTable", {where:{menuName:$("#menuName").val(), menuUrl:$("#menuUrl").val()}});
    }
    
    exports('sysevemenulist', {});
});
