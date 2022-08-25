
var disRowId = "";//讨论板id
var rowId = "";//商机id

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
	
	
	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: flowableBasePath + 'opportunity018',
	    where: {title: $("#title").val(), opportunityTitle: $("#opportunityTitle").val()},
	    even: true,
	    page: true,
	    limits: [8, 16, 24, 32, 40, 48, 56],
	    limit: 8,
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
	        { field: 'title', title: '主题', align: 'left', width: 300, templet: function (d) {
	        	return '<a lay-event="discussDetails" class="notice-title-click">' + d.title + '</a>';
	        }},
	        { field: 'opportunityTitle', title: '所属商机', align: 'left', width: 200 },
	        { field: 'createName', title: '作者', align: 'left', width: 80 },
	        { field: 'replyNum', title: '回复', align: 'left', width: 80 },
	        { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 120 },
	        { field: 'recoveryTime', title: '最后回复时间', align: 'center', width: 120 }
	    ]],
	    done: function(json) {
	    	matchingLanguage();
	    }
	});
	
	table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'discussDetails'){ //详情
        	discussDetails(data);
        }
    });
	
	//讨论版详情
	function discussDetails(data) {
		disRowId = data.id;
		rowId = data.opportunityId;
		_openNewWindows({
			url: "../../tpl/crmdiscuss/discussdetail.html", 
			title: data.title,
			pageId: "discussdetailpage",
			maxmin: true,
			callBack: function (refreshCode) {
			}});
	}
	
	form.render();
	form.on('submit(formSearch)', function (data) {
        if (winui.verifyForm(data.elem)) {
        	refreshTable();
        }
        return false;
	});

	//刷新数据
    $("body").on("click", "#reloadTable", function() {
    	loadTable();
    });

    function loadTable() {
    	table.reloadData("messageTable", {where: {title: $("#title").val(), opportunityTitle: $("#opportunityTitle").val()}});
    }

    function refreshTable(){
    	table.reloadData("messageTable", {page: {curr: 1}, where: {title: $("#title").val(), opportunityTitle: $("#opportunityTitle").val()}});
    }

    exports('alldiscusslist', {});
});