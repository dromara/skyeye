
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
	    url: reqBasePath + 'dsformlimitrequirement001',
	    where:{requireName:$("#requireName").val(), formerRequirement:$("#formerRequirement").val(), afterRequirement:$("#afterRequirement").val()},
	    even:true,  //隔行变色
	    page: true,
	    limits: [8, 16, 24, 32, 40, 48, 56],
	    limit: 8,
	    cols: [[
	        { title: '序号', type: 'numbers'},
	        { field: 'requireName', title: '限制标题', width: 120 },
	        { field: 'formerRequirement', title: '前端限制标签', width: 160 },
	        { field: 'afterRequirement', title: '后端限制标签', width: 160 },
	        { field: 'createTime', title: '创建时间', width: 180 },
	        { title: '操作', fixed: 'right', align: 'center', width: 240, toolbar: '#tableBar'}
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
		var msg = obj ? '确认删除限制条件【' + obj.data.requireName + '】吗？' : '确认删除选中数据吗？';
		layer.confirm(msg, { icon: 3, title: '删除限制条件' }, function (index) {
			layer.close(index);
            //向服务端发送删除指令
            AjaxPostUtil.request({url:reqBasePath + "dsformlimitrequirement003", params:{rowId: data.id}, type:'json', callback:function(json){
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
			url: "../../tpl/dsformlimitrequirement/dsformlimitrequirementedit.html", 
			title: "编辑限制条件",
			pageId: "dsformlimitrequirementedit",
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
    
    //新增
    $("body").on("click", "#addBean", function(){
    	_openNewWindows({
			url: "../../tpl/dsformlimitrequirement/dsformlimitrequirementadd.html", 
			title: "新增限制条件",
			pageId: "dsformlimitrequirementadd",
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
    	table.reload("messageTable", {where:{requireName:$("#requireName").val(), formerRequirement:$("#formerRequirement").val(), afterRequirement:$("#afterRequirement").val()}});
    }
    
    exports('dsformlimitrequirementlist', {});
});
