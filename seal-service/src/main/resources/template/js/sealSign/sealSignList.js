
var objectKey = "";
var objectId = "";

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
	objectKey = GetUrlParam("objectKey");
	objectId = GetUrlParam("objectId");
	if (isNull(objectKey) || isNull(objectId)) {
		winui.window.msg("请传入适用对象信息", {icon: 2, time: 2000});
		return false;
	}

	AjaxPostUtil.request({url: sysMainMation.sealServiceBasePath + "querySealServiceOrderById", params: {id: objectId}, type: 'json', method: 'GET', callback: function(json) {
		if (json.bean.state !== 'beSigned') {
			$("#addBean").hide();
		}
	}});

	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: sysMainMation.sealServiceBasePath + 'querySealSignList',
	    where: getTableParams(),
	    even: true,
	    page: true,
	    limits: getLimits(),
	    limit: getLimit(),
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
			{ field: 'longitude', title: '经度', align: 'left', width: 100 },
			{ field: 'latitude', title: '纬度', align: 'left', width: 100 },
	        { field: 'address', title: '签到地址', align: 'left', width: 300 },
	        { field: 'signTime', title: '签到时间', align: 'center', width: 150 },
			{ field: 'remark', title: '备注', align: 'left', width: 150 }
	    ]],
	    done: function(json) {
	    	matchingLanguage();
			initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "暂不支持搜索", function () {
				table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
			});
	    }
	});
	
	table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
    });

	// 新增
	$("body").on("click", "#addBean", function() {
    	parent._openNewWindows({
			url: systemCommonUtil.getUrl('FP2023082800013&objectId=' + objectId + '&objectKey=' + objectKey, null),
			title: '签到',
			pageId: "sealSignAdd",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
    });
	
	form.render();
	$("body").on("click", "#reloadTable", function() {
		loadTable();
	});
	function loadTable() {
		table.reloadData("messageTable", {where: getTableParams()});
	}

	function getTableParams() {
		return $.extend(true, {objectKey: objectKey, objectId: objectId}, initTableSearchUtil.getSearchValue("messageTable"));
	}
	
    exports('sealSignList', {});
});