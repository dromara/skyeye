

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
	    url: reqBasePath + 'activitimode007',
	    where:{},
	    even:true,  //隔行变色
	    page: true,
	    limits: [8, 16, 24, 32, 40, 48, 56],
	    limit: 8,
	    cols: [[
	        { title: '序号', type: 'numbers'},
	        { field: 'id', title: '编号', width: 120 },
	        { field: 'name', title: '名称', width: 120 },
	        { field: 'id', title: '状态', width: 120, templet: function(d){
        		return "<span class='state-up'>已发布</span>";
	        }},
	        { field: 'deploymentTime', title: '部署时间', width: 180, templet: function(d){
	        	var str = d.deploymentTime.toString();
	        	str = str.substring(0, str.length - 3);
	        	return date('Y-m-d H:i:s', str);
	        }},
	        { title: '操作', fixed: 'right', align: 'center', width: 240, toolbar: '#tableBar'}
	    ]]
	});
	
	table.on('tool(messageTable)', function (obj) { //注：tool是工具条事件名，test是table原始容器的属性 lay-filter="对应的值"
        var data = obj.data; //获得当前行数据
        var layEvent = obj.event; //获得 lay-event 对应的值
        if (layEvent === 'del') { //取消发布
        	del(data, obj);
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
	
	//取消发布
	function del(data, obj){
		var msg = obj ? '确认取消发布【' + obj.data.name + '】吗？' : '确认取消发布选中数据吗？';
		layer.confirm(msg, { icon: 3, title: '取消发布' }, function (index) {
			layer.close(index);
            //向服务端发送删除指令
            AjaxPostUtil.request({url:reqBasePath + "activitimode006", params:{rowId: data.id}, type:'json', callback:function(json){
    			if(json.returnCode == 0){
    				top.winui.window.msg("取消发布成功", {icon: 1,time: 2000});
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
    
    function loadTable(){
    	table.reload("messageTable", {where:{}});
    }
    
    exports('codemodellist', {});
});
