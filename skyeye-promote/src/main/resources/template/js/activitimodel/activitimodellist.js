

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).define(['table', 'jquery', 'winui', 'form'], function (exports) {
	
	winui.renderColor();
	//模板分组ID
	groupId = parent.rowId;
	var $ = layui.$,
	form = layui.form,
	table = layui.table;
	//表格渲染
	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: reqBasePath + 'activitimode002',
	    where:{},
	    even:true,  //隔行变色
	    page: true,
	    limits: [8, 16, 24, 32, 40, 48, 56],
	    limit: 8,
	    cols: [[
	        { title: '序号', type: 'numbers'},
	        { field: 'id', title: '模型编号', width: 120 },
	        { field: 'name', title: '模型名称', width: 120 },
	        { field: 'version', title: '版本', width: 120},
	        { field: 'deploymentId', title: '状态', width: 120, templet: function(d){
	        	if(isNull(d.deploymentId)){
	        		return "<span class='state-new'>未发布</span>";
	        	}else{
	        		return "<span class='state-up'>已发布</span>";
	        	}
	        }},
	        { field: 'createTime', title: '创建时间', width: 180, templet: function(d){
	        	var str = d.createTime.toString();
	        	str = str.substring(0, str.length - 3);
	        	return date('Y-m-d H:i:s', str);
	        }},
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
        }else if (layEvent === 'fb') { //发布
        	fb(data);
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
		var msg = obj ? '确认删除模型【' + obj.data.name + '】吗？' : '确认删除选中数据吗？';
		layer.confirm(msg, { icon: 3, title: '删除模型' }, function (index) {
			layer.close(index);
            //向服务端发送删除指令
            AjaxPostUtil.request({url:reqBasePath + "activitimode006", params:{rowId: data.id}, type:'json', callback:function(json){
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
			url: "../../static/modeler.html?modelId=" + rowId,
			title: "绘制流程",
			pageId: "canveractivitimodeledit",
			maxmin: true,
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	top.winui.window.msg("操作成功", {icon: 1,time: 2000});
                	loadTable();
                } else if (refreshCode == '-9999') {
                	top.winui.window.msg("操作失败", {icon: 2,time: 2000});
                }
			}});
	}
	
	//发布
	function fb(data){
		rowId = data.id;
		var msg = data ? '确认发布模型【' + data.name + '】吗？' : '确认发布选中数据吗？';
		layer.confirm(msg, { icon: 3, title: '发布模型' }, function (index) {
			layer.close(index);
			AjaxPostUtil.request({url:reqBasePath + "activitimode003", params:{modelId: rowId}, type:'json', callback:function(json){
				if(json.returnCode == 0){
					top.winui.window.msg("发布成功", {icon: 1,time: 2000});
					loadTable();
				}else{
					top.winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
				}
			}});
		});
	}
	
	//刷新数据
    $("body").on("click", "#reloadTable", function(){
    	loadTable();
    });
    
    //新增
    $("body").on("click", "#addBean", function(){
    	AjaxPostUtil.request({url:reqBasePath + "activitimode001", params:{}, type:'json', callback:function(json){
			if(json.returnCode == 0){
				_openNewWindows({
					url: "../../static/modeler.html?modelId=" + json.bean.id,
					title: "绘制流程",
					pageId: "canveractivitimodel",
					maxmin: true,
					callBack: function(refreshCode){
		                if (refreshCode == '0') {
		                	top.winui.window.msg("操作成功", {icon: 1,time: 2000});
		                	loadTable();
		                } else if (refreshCode == '-9999') {
		                	top.winui.window.msg("操作失败", {icon: 2,time: 2000});
		                }
					}});
			}else{
				top.winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
			}
		}});
    });
    
    function loadTable(){
    	table.reload("messageTable", {where:{}});
    }
    
    exports('codemodellist', {});
});
