
var filePath = "";

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
	    url: reqBasePath + 'codemodel015',
	    where:{groupId:groupId},
	    even:true,  //隔行变色
	    page: true,
	    limits: [8, 16, 24, 32, 40, 48, 56],
	    limit: 8,
	    cols: [[
	        { title: '序号', type: 'numbers'},
	        { field: 'tableName', title: '表名', width: 180 },
	        { field: 'filePath', title: '文件名', width: 300 },
	        { field: 'isExist', title: '是否可下载', width: 120 },
	        { field: 'createTime', title: '创建时间', width: 180 },
	        { title: '操作', fixed: 'right', align: 'center', width: 240, toolbar: '#tableBar'}
	    ]]
	});
	
	table.on('tool(messageTable)', function (obj) { //注：tool是工具条事件名，test是table原始容器的属性 lay-filter="对应的值"
        var data = obj.data; //获得当前行数据
        var layEvent = obj.event; //获得 lay-event 对应的值
        if (layEvent === 'download') { //下载
        	download(data, obj);
        }else if(layEvent === 'creatFile') { //同步文件
        	creatFile(data, obj);
        }
    });
	
	//下载
	function download(data){
		filePath = data.filePath;
		window.open(reqBasePath + "codemodel017?filePath=" + filePath);
	}
	
	//同步文件
	function creatFile(data){
		filePath = data.filePath;
		AjaxPostUtil.request({url:reqBasePath + "codemodel016", params:{filePath:filePath}, type:'json', callback:function(json){
			if(json.returnCode == 0){
				top.winui.window.msg('生成完成，请下载。', {icon: 1,time: 2000});
				loadTable();
			}else{
				top.winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
			}
		}});
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
	
	//刷新数据
    $("body").on("click", "#reloadTable", function(){
    	loadTable();
    });
    
    function loadTable(){
    	table.reload("messageTable", {where:{groupId:groupId}});
    }
    
    exports('codemodelhistorylist.js', {});
});
