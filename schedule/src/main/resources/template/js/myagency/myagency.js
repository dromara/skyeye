
var rowId = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form'], function (exports) {
	winui.renderColor();
	
	var $ = layui.$,
		form = layui.form,
		table = layui.table;
	
	form.render();
	form.on('submit(formSearch)', function (data) {
        if (winui.verifyForm(data.elem)) {
        	loadTable();
        }
        return false;
	});
	
	initLoadTable();
	function initLoadTable(){
		table.render({
		    id: 'messageTable',
		    elem: '#messageTable',
		    method: 'post',
		    url: reqBasePath + 'myagency001',
		    where:{myagencyType: $("#myagencyType").val(), myagencyName: $("#myagencyName").val()},
		    even: true,
		    page: true,
		    limits: getLimits(),
	    	limit: getLimit(),
		    cols: [[
		        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
		        { field: 'title', title: '待办名称', width: 120 },
		        { field: 'startTime', title: '开始时间', align: 'center', width: 150 },
		        { field: 'endTime', title: '结束时间', align: 'center', width: 150 },
		        { field: 'typeName', title: '待办类型', width: 80 },
		        { field: 'remindTime', title: '提醒时间', width: 180 },
		        { field: 'cron', title: 'CRON表达式', width: 180 },
		        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 240, toolbar: '#tableBar'}
		    ]],
		    done: function(){
		    	matchingLanguage();
		    }
		});
		
		table.on('tool(messageTable)', function (obj) {
	        var data = obj.data;
	        var layEvent = obj.event;
	        if (layEvent === 'cancleAgency') { //取消代办
	        	cancleAgency(data);
	        }
	    });
	}
	
	//刷新数据
    $("body").on("click", "#reloadTable", function() {
    	loadTable();
    });
    
	//取消代办
	function cancleAgency(data, obj){
		var msg = obj ? '确认取消【' + obj.data.title + '】的提醒吗？' : '确认取消提醒吗？';
		layer.confirm(msg, { icon: 3, title: '取消代办' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url:reqBasePath + "myagency002", params:{rowId: data.id}, type: 'json', callback: function(json){
    			if (json.returnCode == 0) {
    				winui.window.msg("取消成功", {icon: 1, time: 2000});
    				loadTable();
    			} else {
    				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
    			}
    		}});
		});
	}
	
    function loadTable(){
    	table.reload("messageTable", {where:{myagencyType:$("#myagencyType").val(), myagencyName:$("#myagencyName").val()}});
    }
    
    exports('myagency', {});
});
