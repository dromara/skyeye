
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

	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: sysMainMation.erpBasePath + 'queryHolderNormsList',
	    where: getTableParams(),
	    even: true,
	    page: true,
	    limits: getLimits(),
	    limit: getLimit(),
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
			{ field: 'materialId', title: '产品名称', align: 'left', width: 150, templet: function (d) {
				return getNotUndefinedVal(d.materialMation?.name);
			}},
			{ field: 'normsId', title: '产品规格', align: 'left', width: 450, templet: function (d) {
				return getNotUndefinedVal(d.normsMation?.name);
			}},
			{ field: 'allOperNumber', title: '交易数量', align: 'center', width: 90, templet: function (d) {
				let itemCode = d.materialMation?.itemCode;
				if (itemCode == 1) {
					// 一物一码
					return '<a lay-event="details" class="notice-title-click">' + d.allOperNumber + '</a>';
				}
				return getNotUndefinedVal(d.allOperNumber);
			}}
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
		if (layEvent === 'details') { // 详情
			details(data);
		}
    });

	// 详情
	function details(data) {
		_openNewWindows({
			url:  '../../tpl/holder/holderNormsChildList.html?normsId=' + data.normsId + '&holderId=' + data.holderId + '&holderKey=' + data.holderKey,
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "holderNormsChildList",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
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
		return $.extend(true, {holderKey: objectKey, holderId: objectId}, initTableSearchUtil.getSearchValue("messageTable"));
	}
	
    exports('holderNormsList', {});
});