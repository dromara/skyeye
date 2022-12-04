
var rowId;

var categoryId = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	var $ = layui.jquery,
		form = layui.form,
		table = layui.table;
	var serviceClassName = sysServiceMation["enclosure"]["key"];

	catalogTreeUtil.init({
		boxId: 'catalogBox',
		className: serviceClassName,
		addOrUser: true,
		isRoot: 1,
		chooseCallback: function (chooseId) {
			categoryId = chooseId;
			refreshTable();
		}
	});
	
	table.render({
		id: 'messageTable',
		elem: '#messageTable',
		method: 'post',
		url: reqBasePath + 'sysenclosure004',
		where: getTableParams(),
		even: true,
		page: true,
		limits: getLimits(),
		limit: getLimit(),
		cols: [[
			{ title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
			{ field: 'name', title: '文件名', align: 'left', width: 400 },
			{ field: 'size', title: '文件大小', align: 'center', width: 120 },
			{ field: 'createTime', title: '上传时间', align: 'center', width: 150 },
			{ title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 150, toolbar: '#tableBar' }
		]],
		done: function(json) {
			matchingLanguage();
			initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "请输入文件名", function () {
				table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
			});
		}
	});

	table.on('tool(messageTable)', function (obj) {
		var data = obj.data;
		var layEvent = obj.event;
		if (layEvent === 'download'){ //下载
			download(fileBasePath + data.path, data.name);
		}
	});

	// 上传附件
	$("body").on("click", "#addSenclosureBean", function (e) {
		_openNewWindows({
			url: "../../tpl/sysEnclosure/enclosureUpload.html",
			title: "上传附件",
			pageId: "enclosureUpload",
			area: ['70vw', '70vh'],
			callBack: function (refreshCode) {
    			loadThisFolderChild();
			}});
	});

	form.render();
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
		return $.extend(true, {catelogId: categoryId}, initTableSearchUtil.getSearchValue("messageTable"));
	}

    exports('myEnclosureList', {});
});
