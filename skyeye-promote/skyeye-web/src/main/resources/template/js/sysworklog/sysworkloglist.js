
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).define(['table', 'jquery', 'winui'], function (exports) {
	
	winui.renderColor();
	
	var $ = layui.$,
	table = layui.table;
	//表格渲染
	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: reqBasePath + 'login006',
	    where:{},
	    even:true,  //隔行变色
	    page: true,
	    limits: [8, 16, 24, 32, 40, 48, 56],
	    limit: 8,
	    cols: [[
	        { title: '序号', type: 'numbers'},
	        { field: 'userName', title: '操作人', width: 120 },
	        { field: 'ip', title: '操作ip', width: 150 },
	        { field: 'message', title: '日志信息', width: 250 },
	        { field: 'realPath', title: '真实链接', width: 500 },
	        { field: 'logLevel', title: '日志类型', width: 150 },
	        { field: 'createTime', title: '创建时间', width: 180 },
	    ]]
	});
	
    exports('sysworkloglist', {});
});
