
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
	
	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: flowableBasePath + 'sealseservice029',
	    where: getTableParams(),
	    even: true,
	    page: true,
		limits: getLimits(),
		limit: getLimit(),
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
	        { field: 'orderNum', title: '工单号', align: 'center', width: 220, templet: function (d) {
	        	return '<a lay-event="details" class="notice-title-click">' + d.orderNum + '</a>';
	        }},
	        { field: 'applyNum', title: '申领单号', align: 'center', width: 220, templet: function (d) {
	        	return '<a lay-event="appDetails" class="notice-title-click">' + d.applyNum + '</a>';
	        }},
	        { field: 'customerName', title: '客户名称', align: 'left', width: 150 },
	        { field: 'applyUserName', title: '申领人', width: 120 },
	        { field: 'applyTime', title: '申领时间', align: 'center', width: 140 },
	        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 150, toolbar: '#tableBar' }
	    ]],
	    done: function(json) {
	    	matchingLanguage();
			initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "请输入工单号，申领单号", function () {
				table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
			});
	    }
	});
	
	table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'details'){ //详情
        	details(data);
        } else if (layEvent === 'appDetails'){ //申领单详情
        	appDetails(data);
        } else if (layEvent === 'examine'){ //审核
        	examine(data);
        }
    });
	
	// 工单详情
	function details(data) {
		rowId = data.serviceId;
		_openNewWindows({
			url: "../../tpl/sealseservice/sealseservicedetails.html", 
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "sealseservicedetails",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
			}});
	}
	
	// 申领单详情
	function appDetails(data) {
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/partsclaim/partsclaimdetails.html", 
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "sealseservicedetails",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
			}});
	}
	
	// 申领单审核
	function examine(data) {
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/partsclaim/partsclaimexamine.html", 
			title: "审核",
			pageId: "partsclaimexamine",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	}

	form.render();
	$("body").on("click", "#reloadTable", function() {
		loadTable();
	});

	function loadTable() {
		table.reloadData("messageTable", {where: getTableParams()});
	}

	function getTableParams() {
		return $.extend(true, {}, initTableSearchUtil.getSearchValue("messageTable"));
	}
	
    exports('allpartsclaimtoexaminelist', {});
});