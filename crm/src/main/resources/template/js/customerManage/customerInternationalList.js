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

	// 客户分类
	sysDictDataUtil.showDictDataListByDictTypeCode(sysDictData["crmCustomerType"]["key"], 'select', "typeId", '', form);

	// 客户来源
	sysDictDataUtil.showDictDataListByDictTypeCode(sysDictData["crmCustomerFrom"]["key"], 'select', "fromId", '', form);

	// 客户所属行业
	sysDictDataUtil.showDictDataListByDictTypeCode(sysDictData["crmCustomerIndustry"]["key"], 'select', "industryId", '', form);

	// 公海客户群
	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: flowableBasePath + 'customer012',
	    where: getTableParams(),
	    even: true,
	    page: true,
	    limits: getLimits(),
	    limit: getLimit(),
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
	        { field: 'name', title: '客户名称', align: 'left', width: 300, templet: function (d) {
	        	return '<a lay-event="details" class="notice-title-click">' + d.name + '</a>';
	        }},
	        { field: 'typeName', title: '客户分类', align: 'left', width: 120 },
	        { field: 'fromName', title: '客户来源', align: 'left', width: 120 },
	        { field: 'industryName', title: '所属行业', align: 'left', width: 180 },
	        { field: 'noDocumentaryDayNum', title: '未跟单天数', align: 'left', width: 100 },
	        { field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], align: 'left', width: 120 },
	        { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 100 },
	        { field: 'lastUpdateName', title: systemLanguage["com.skyeye.lastUpdateName"][languageType], align: 'left', width: 120 },
	        { field: 'lastUpdateTime', title: '最后修改时间', align: 'center', width: 100}
	    ]],
	    done: function (json) {
	    	matchingLanguage();
	    	var str = "超过" + json.bean. noDocumentaryDayNum + "天未跟单";
	    	if(parseInt(json.bean.noChargeId) == 1){
	    		str += '，或者未指定负责人';
	    	}
	    	str += '的客户。';
	    	$("#showInfo").html(str);
	    }
	});
	
	table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'details'){ // 详情
        	details(data);
        }
    });
	
	form.render();
	
	// 详情
	function details(data) {
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/customerManage/customerDetails.html",
			title: "客户详情",
			pageId: "customerDetails",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
			}});
	}
	
	$("body").on("click", "#formSearch", function() {
		refreshTable();
	});
	
	$("body").on("click", "#reloadTable", function() {
    	loadTable();
    });
    
    function loadTable() {
    	table.reloadData("messageTable", {where: getTableParams()});
    }
    
    function refreshTable(){
    	table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
    }
    
    function getTableParams() {
    	return {
    		name: $("#customerName").val(),
    		typeId: $("#typeId").val(),
    		fromId: $("#fromId").val(),
    		industryId: $("#industryId").val()
    	};
    }
	
    exports('customerInternationalList', {});
});