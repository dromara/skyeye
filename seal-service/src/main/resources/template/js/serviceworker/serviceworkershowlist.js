
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form'], function (exports) {
	var index = parent.layer.getFrameIndex(window.name);
	winui.renderColor();
	
	var $ = layui.$,
		form = layui.form,
		table = layui.table;
	
	
	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: reqBasePath + 'sealseserviceworker006',
	    where: {userName: $("#userName").val()},
	    even: true,
	    page: true,
	    limits: [8, 16, 24, 32, 40, 48, 56],
	    limit: 8,
	    cols: [[
	    	{ type: 'radio'},
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
	        { field: 'userName', title: '用户姓名', align: 'left', width: 80 },
	        { field: 'proName', title: '所在省', align: 'left', width: 80 },
	        { field: 'cityName', title: '所在市', align: 'left', width: 80 },
	        { field: 'areaName', title: '所在区', align: 'left', width: 80 },
	        { field: 'orderNumber', title: '工单数', align: 'left', width: 60 },
	        { field: 'stateName', title: '状态', align: 'center', width: 80, templet: function(d){
	        	if(d.orderNumber > 0){
	        		return '<span class="state-down">' + d.stateName + '</span>';
	        	}else{
	        		return '<span class="state-up">' + d.stateName + '</span>';
	        	}
	        }},
	        { field: 'addDetail', title: '详细地址', align: 'left', width: 400 },
	        { field: 'createTime', title: '创建时间', align: 'center', width: 150}
	    ]],
	    done: function(res, curr, count){
	    	matchingLanguage();
	    	$('#messageTable').next().find('.layui-table-body').find("table" ).find("tbody").children("tr").on('dblclick',function(){
				var dubClick = $('#messageTable').next().find('.layui-table-body').find("table").find("tbody").find(".layui-table-hover");
				dubClick.find("input[type='radio']").prop("checked", true);
				form.render();
				var id = JSON.stringify(dubClick.data('index'));
				var obj = res.rows[id];
				parent.serviceUser = obj;
				parent.layer.close(index);
				parent.refreshCode = '0';
			});
			
			$('#messageTable').next().find('.layui-table-body').find("table" ).find("tbody").children("tr").on('click',function(){
				var click = $('#messageTable').next().find('.layui-table-body').find("table").find("tbody").find(".layui-table-hover");
				click.find("input[type='radio']").prop("checked", true);
				form.render();
			})
	    }
	});
	
	form.render();
	
	
	$("body").on("click", "#formSearch", function(){
		refreshTable();
	});
	
	$("body").on("click", "#reloadTable", function(){
    	loadTable();
    });
    
    function loadTable(){
    	table.reload("messageTable", {where: {userName: $("#userName").val()}});
    }
    
    function refreshTable(){
    	table.reload("messageTable", {page: {curr: 1}, where: {userName: $("#userName").val()}});
    }

    exports('serviceworkershowlist', {});
});