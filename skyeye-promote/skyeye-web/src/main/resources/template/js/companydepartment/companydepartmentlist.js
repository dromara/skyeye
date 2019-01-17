
var rowId = "";

var companyId = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).define(['table', 'jquery', 'winui', 'form', 'dtree'], function (exports) {
	
	winui.renderColor();
	
	var $ = layui.$,
	form = layui.form,
	dtree = layui.dtree,
	table = layui.table;
	
	dtree.render({
		elem: "#demoTree1",  //绑定元素
		url: reqBasePath + 'companymation007', //异步接口
		dataStyle: 'layuiStyle',
		done: function(){
			initLoatTable();//初始化加载表格
			if($("#demoTree1 li").length > 0){
				$("#demoTree1 li").eq(0).children('div').click();
			}
		}
	});

	//单击节点 监听事件
	dtree.on("node('demoTree1')" ,function(param){
		companyId = param.nodeId;
		loadTable();
	});
	
	function initLoatTable(){
		//表格渲染
		table.render({
		    id: 'messageTable',
		    elem: '#messageTable',
		    method: 'post',
		    url: reqBasePath + 'companydepartment001',
		    where:{departmentName: $("#departmentName").val(), companyId: companyId},
		    even:true,  //隔行变色
		    page: true,
		    limits: [8, 16, 24, 32, 40, 48, 56],
		    limit: 8,
		    cols: [[
		        { title: '序号', type: 'numbers'},
		        { field: 'departmentName', title: '部门名称', width: 180 },
		        { field: 'id', title: '部门简介', width: 100, templet: function(d){
		        	return '<i class="fa fa-fw fa-html5 cursor" lay-event="departmentDesc"></i>';
		        }},
		        { field: 'jobNum', title: '职位数', width: 180 },
		        { field: 'userNum', title: '员工数', width: 180 },
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
	        }else if (layEvent === 'departmentDesc') { //部门简介
	        	layer.open({
		            id: '部门简介',
		            type: 1,
		            title: '部门简介',
		            shade: 0.3,
		            area: ['1200px', '600px'],
		            content: data.departmentDesc,
		        });
	        }
	    });
	}
	
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
		var msg = obj ? '确认删除公司部门信息【' + obj.data.departmentName + '】吗？' : '确认删除选中数据吗？';
		layer.confirm(msg, { icon: 3, title: '删除公司部门信息' }, function (index) {
			layer.close(index);
            //向服务端发送删除指令
            AjaxPostUtil.request({url:reqBasePath + "companydepartment003", params:{rowId: data.id}, type:'json', callback:function(json){
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
			url: "../../tpl/companydepartment/companydepartmentedit.html", 
			title: "编辑公司部门信息",
			pageId: "companydepartmentedit",
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
			url: "../../tpl/companydepartment/companydepartmentadd.html", 
			title: "新增公司部门信息",
			pageId: "companydepartmentadd",
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
    	table.reload("messageTable", {where:{departmentName: $("#departmentName").val(), companyId: companyId}});
    }
    
    exports('companydepartmentlist', {});
});
