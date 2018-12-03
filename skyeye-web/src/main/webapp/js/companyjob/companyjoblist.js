
var rowId = "";

var companyId = "";
var departmentId = "";

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
			if($("#demoTree1 li").length > 0){
				$("#demoTree1 li").eq(0).children('div').click();
			}
		}
	});

	//单击节点 监听事件
	dtree.on("node('demoTree1')" ,function(param){
		companyId = param.nodeId;
		dtree.render({
			elem: "#demoTree2",  //绑定元素
			url: reqBasePath + 'companydepartment006?companyId=' + companyId, //异步接口
			dataStyle: 'layuiStyle',
			done: function(){
				departmentId = "";
				initLoatTable();//初始化加载表格
				if($("#demoTree2 li").length > 0){
					$("#demoTree2 li").eq(0).children('div').click();
				}
			}
		});
	});
	
	dtree.on("node('demoTree2')" ,function(param){
		departmentId = param.nodeId;
		loadTable();
	});
	
	function initLoatTable(){
		//表格渲染
		table.render({
		    id: 'messageTable',
		    elem: '#messageTable',
		    method: 'post',
		    url: reqBasePath + 'companyjob001',
		    where:{departmentId: departmentId, jobName: $("#jobName").val()},
		    even:true,  //隔行变色
		    page: true,
		    limits: [8, 16, 24, 32, 40, 48, 56],
		    limit: 8,
		    cols: [[
		        { title: '序号', type: 'numbers'},
		        { field: 'jobName', title: '职位名称', width: 180 },
		        { field: 'id', title: '职位简介', width: 100, templet: function(d){
		        	return '<i class="fa fa-fw fa-html5 cursor" lay-event="jobDesc"></i>';
		        }},
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
	        }else if (layEvent === 'jobDesc') { //职位简介
	        	layer.open({
		            id: '职位简介',
		            type: 1,
		            title: '职位简介',
		            shade: 0.3,
		            area: ['1200px', '600px'],
		            content: data.jobDesc,
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
		var msg = obj ? '确认删除公司部门职位信息【' + obj.data.jobName + '】吗？' : '确认删除选中数据吗？';
		layer.confirm(msg, { icon: 3, title: '删除公司部门职位信息' }, function (index) {
			layer.close(index);
            //向服务端发送删除指令
            AjaxPostUtil.request({url:reqBasePath + "companyjob003", params:{rowId: data.id}, type:'json', callback:function(json){
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
			url: "../../tpl/companyjob/companyjobedit.html", 
			title: "编辑公司部门职位信息",
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
	
	//刷新数据
    $("body").on("click", "#reloadTable", function(){
    	loadTable();
    });
    
    //新增
    $("body").on("click", "#addBean", function(){
    	_openNewWindows({
			url: "../../tpl/companyjob/companyjobadd.html", 
			title: "新增公司部门职位信息",
			pageId: "companyjobadd",
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
    	table.reload("messageTable", {where:{departmentId: departmentId, jobName: $("#jobName").val()}});
    }
    
    exports('companyjoblist', {});
});
