var rowId = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form', 'laydate'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		form = layui.form,
		laydate = layui.laydate,
		table = layui.table;

	// 跟单分类
	sysDictDataUtil.showDictDataListByDictTypeCode(sysDictData["crmDocumentaryType"]["key"], 'select', "typeId", '', form);

	// 跟单时间
	laydate.render({elem: '#documentaryTime', range: '~'});
	
	// 所有跟单列表
	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: flowableBasePath + 'documentary007',
	    where: getTableParams(),
	    even: true,
	    page: true,
	    limits: [8, 16, 24, 32, 40, 48, 56],
	    limit: 8,
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
	        { field: 'opportunityName', title: '商机', align: 'left', width: 300, templet: function (d) {
	        	return '<a lay-event="details" class="notice-title-click">' + d.opportunityName + '</a>';
	        }},
	        { field: 'typeName', title: '类型', align: 'center', width: 120 },
	        { field: 'documentaryTime', title: '跟单时间', align: 'center', width: 150 },
	        { field: 'createName', title: '跟单人', align: 'center', width: 120 },
	        { field: 'detail', title: '详细内容', align: 'center', width: 220 }
	    ]],
	    done: function(){
	    	matchingLanguage();
	    }
	});
	
	table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'details'){ // 详情
        	details(data);
        }
    });
	
	// 详情
	function details(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/documentarymanage/documentaryDetails.html",
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "documentaryDetails",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
			}});
	}

	form.render();
	form.on('submit(formSearch)', function (data) {
		if (winui.verifyForm(data.elem)) {
			table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
		}
		return false;
	});

	$("body").on("click", "#reloadTable", function() {
		loadTable();
	});

	function loadTable(){
		table.reloadData("messageTable", {where: getTableParams()});
	}
	
	function getTableParams(){
		var startTime = "";
		var endTime = "";
		if(isNull($("#documentaryTime").val())){
    		startTime = "";
    		endTime = "";
    	} else {
    		startTime = $("#documentaryTime").val().split('~')[0].trim();
    		endTime = $("#documentaryTime").val().split('~')[1].trim();
    	}
    	return {
    		opportunityName: $("#opportunityName").val(),
    		typeId: $("#typeId").val(),
    		startTime: startTime,
    		endTime: endTime
    	};
	}
	
    exports('documentarylist', {});
});