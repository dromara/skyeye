
var rowId = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).define(['treeGrid', 'jquery', 'winui', 'form'], function (exports) {
	
	winui.renderColor();
	
	var $ = layui.$,
	form = layui.form,
	treeGrid = layui.treeGrid;
	//表格渲染
	treeGrid.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        idField: 'id',
        url: reqBasePath + 'companymation001',
        cellMinWidth: 100,
        where:{companyName: $("#companyName").val()},
        treeId: 'id',//树形id字段名称
        treeUpId: 'pId',//树形父id字段名称
        treeShowName: 'companyName',//以树形式显示的字段
        cols: [[
            { field:'companyName', width:300, title: '公司名称'},
            { field:'companyDesc', width:100, title: '公司简介', templet: function(d){
	        	return '<i class="fa fa-fw fa-html5 cursor" lay-event="companyDesc"></i>';
	        }},
	        { field: 'departmentNum', title: '部门数', width: 180 },
	        { field: 'userNum', title: '员工数', width: 180 },
            { field:'id', width:400, title: '公司地址', templet: function(d){
            	var str = d.provinceName + " ";
            	if(!isNull(d.cityName)){
            		str += d.cityName + " ";
            	}
            	if(!isNull(d.areaName)){
            		str += d.areaName + " ";
            	}
            	if(!isNull(d.townshipName)){
            		str += d.townshipName + " ";
            	}
            	if(!isNull(d.addressDetailed)){
            		str += d.addressDetailed;
            	}
	        	return str;
	        }},
	        { field:'createTime', width:150, title: '录入时间'},
            { title: '操作', fixed: 'right', align: 'center', width: 240, toolbar: '#tableBar'}
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
        }else if (layEvent === 'companyDesc') { //公司简介
        	layer.open({
	            id: '公司简介',
	            type: 1,
	            title: '公司简介',
	            shade: 0.3,
	            area: ['1200px', '600px'],
	            content: data.companyDesc,
	        });
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
		var msg = obj ? '确认删除公司信息【' + obj.data.companyName + '】吗？' : '确认删除选中数据吗？';
		layer.confirm(msg, { icon: 3, title: '删除公司信息' }, function (index) {
			layer.close(index);
            //向服务端发送删除指令
            AjaxPostUtil.request({url:reqBasePath + "companymation003", params:{rowId: data.id}, type:'json', callback:function(json){
    			if(json.returnCode == 0){
    				top.winui.window.msg("删除成功", {icon: 1,time: 2000});
    				loadTable();
    			}else{
    				top.winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
    			}
    		}});
		});
	}
	
	//编辑分类
	function edit(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/companymation/companymationedit.html", 
			title: "编辑公司信息",
			pageId: "companymationedit",
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
			url: "../../tpl/companymation/companymationadd.html", 
			title: "新增公司信息",
			pageId: "companymationadd",
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
    	treeGrid.query("messageTable", {where:{companyName: $("#companyName").val()}});
    }
    
    exports('companymationlist', {});
});
