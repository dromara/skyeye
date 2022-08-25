
var rowId = ""; // 讨论板id
var proId = ""; // 项目id

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
	proId = parent.rowId;
	
	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: flowableBasePath + 'prodiscuss001',
	    where: getTableParams(),
	    even: true,
	    page: true,
		limits: getLimits(),
		limit: getLimit(),
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
	        { field: 'title', title: '主题', align: 'left', width: 300, templet: function (d) {
	        	return '<a lay-event="discussDetails" class="notice-title-click">' + d.title + '</a>';
	        }},
	        { field: 'createName', title: '作者', align: 'left', width: 120 },
	        { field: 'replyNum', title: '回复', align: 'left', width: 80 },
	        { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 120 },
	        { field: 'recoveryTime', title: '最后回复时间', align: 'center', width: 120 }
	    ]],
	    done: function(json) {
	    	matchingLanguage();
			initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "请输入主题", function () {
				table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
			});
	    }
	});
	
	table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'discussDetails'){ //详情
        	discussDetails(data);
        }
    });
	
	// 社区发帖
	$("body").on("click", "#addBean", function() {
    	_openNewWindows({
			url: "../../tpl/proprojectdiscuss/proprojectdiscussadd.html",
			title: "社区发帖",
			pageId: "proprojectdiscussadd",
			area: ['80vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
    });
	
	// 讨论版详情
	function discussDetails(data) {
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/proprojectdiscuss/proprojectdiscussdetail.html", 
			title: data.title,
			pageId: "proprojectdiscussdetail",
			maxmin: true,
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
			}});
	}

	form.render();
	// 刷新数据
    $("body").on("click", "#reloadTable", function() {
    	loadTable();
    });

    function loadTable() {
    	table.reloadData("messageTable", {where: getTableParams()});
    }

	function getTableParams() {
		return $.extend(true, {proId: parent.rowId}, initTableSearchUtil.getSearchValue("messageTable"));
	}

    exports('proprojectdiscusslist', {});
});