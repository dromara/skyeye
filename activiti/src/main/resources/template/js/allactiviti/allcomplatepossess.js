
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		table = layui.table,
		form = layui.form;

	//所有已完成流程
	table.render({
	    id: 'messageAllComplateTable',
	    elem: '#messageAllComplateTable',
	    method: 'post',
	    url: flowableBasePath + 'activitimode018',
	    where:{},
	    even:true,
	    page: true,
		limits: getLimits(),
		limit: getLimit(),
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
	        { field: 'processInstanceId', title: '流程ID', width: 100 },
	        { field: 'taskType', title: '类型', width: 100 },
	        { field: 'createName', title: '申请人', width: 120},
	        { field: 'createTime', title: '申请时间', align: 'center', width: 150 },
	        { field: 'name', title: '当前节点', width: 130, templet: function(d){
	        	return '[' + d.name + ']';
	        }},
	        { field: 'agencyName', title: '审批人', width: 120},
	        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 240, toolbar: '#myAllComplateTableBar'}
	    ]],
	    done: function(){
	    	matchingLanguage();
	    }
	});
	
	table.on('tool(messageAllComplateTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'selTasks') { //详情
			activitiUtil.activitiDetails(data);
        }
    });
    
	// 刷新所有已完成流程
	$("body").on("click", "#loadAllComplateTable", function(){
		reloadAllComplateTable();
	});
	
    function reloadAllComplateTable(){
    	table.reload("messageAllComplateTable", {where:{}});
    }
    
    exports('allcomplatepossess', {});
});
