
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

	// 加载列表数据权限
	loadAuthBtnGroup('messageTable', '1714464811697');
    
	table.render({
		id: 'messageTable',
		elem: '#messageTable',
		method: 'post',
		url: sysMainMation.admBasePath + 'queryGwSendDocumentList',
		where: getTableParams(),
		even: true,
		page: true,
		limits: getLimits(),
		limit: getLimit(),
		cols: [[
			{ title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
			{ field: 'title', title: '标题', width: 300 },
			{ field: 'sendDepartmentMation', title: '发文部门', width: 150, templet: function (d) {
				return d.sendDepartmentMation?.name;
			}},
			{ field: 'year', title: '年份', align: 'center', width: 100 },
			{ field: 'number', title: '第几号文', align: 'center', width: 100 },
			{ field: 'enterprise', title: '企字', align: 'center', width: 100 },
			{ field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 },
			{ title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 150, toolbar: '#messageTableBar' }
		]],
		done: function(json) {
			matchingLanguage();
			initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "请输入标题", function () {
				table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
			});
		}
	});

	// 操作事件
	table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'preview') {//预览
			systemCommonUtil.showPicImg(systemCommonUtil.getFilePath(data.picPath));
		} else if (layEvent === 'download') {//下载
			download(fileBasePath + data.path, data.title);
		}
    });
	
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
    
    exports('gwDocumentSeeList', {});
});
