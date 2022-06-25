
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	
	var $ = layui.$,
		table = layui.table;
	
	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: reqBasePath + 'sysdatasqlbackups002',
	    where:{},
	    even:true,
	    page: false,
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
	        { field: 'tableName', title: '表名称', align: 'center', width: 150 },
	        { field: 'tableComment', title: '表备注', align: 'center', width: 150 },
	        { field: 'tablesRows', title: '记录条数', align: 'center', width: 150 },
	        { field: 'tableSize', title: '数据大小', align: 'center', width: 150 },
	        { field: 'indexSize', title: '索引大小', align: 'center', width: 150 },
	    ]],
	    done: function(){
	    	matchingLanguage();
	    }
	});
	
	//开始备份
	$("body").on("click", "#addBean", function(){
		winui.window.msg("开始备份", {icon: 1, time: 2000});
		AjaxPostUtil.request({url:reqBasePath + "sysdatasqlbackups003", params:{}, type: 'json', callback: function(json){
   			if (json.returnCode == 0) {
   				winui.window.msg("备份成功", {icon: 1, time: 2000});
   			}else{
   				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
   			}
   		}});
    });

    exports('sysdatatablelist', {});
});
