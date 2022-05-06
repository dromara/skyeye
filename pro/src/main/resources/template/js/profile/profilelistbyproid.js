
var rowId = "";

layui.config({
	base: basePath,
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'form', 'table'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		form = layui.form,
		table = layui.table;

	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: flowableBasePath + 'profile012',
	    where: {rowId: parent.rowId},
	    even: true,
	    page: true,
	    limits: [8, 16, 24, 32, 40, 48, 56],
	    limit: 8,
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
	        { field: 'title', title: '名称', align: 'left', width: 250, templet: function(d){
	        	return '<a lay-event="details" class="notice-title-click">' + d.title + '</a>';
	        }},
	        { field: 'typeName', title: '文档分类', align: 'left', width: 120},
	        { field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], align: 'left', width: 120},
	        { field: 'updateTime', title: '更新时间', align: 'center', width: 140}
	    ]],
	    done: function(){
	    	matchingLanguage();
	    }
	});
	
	table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'details'){ //详情
        	details(data);
        }
    });
    form.render();

	//详情
	function details(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/profile/profiledetails.html", 
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "profiledetails",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
			}});
	}
	
	//刷新数据
    $("body").on("click", "#reloadTable", function(){
    	loadTable();
    });

    function loadTable(){
    	table.reload("messageTable", {where:{rowId: parent.rowId}});
    }

    exports('profilelistbyproid', {});
});
