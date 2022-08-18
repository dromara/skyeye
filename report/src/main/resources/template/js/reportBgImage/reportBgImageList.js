
var rowId = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window',
}).define(['window', 'table', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		form = layui.form,
		table = layui.table;
	
	authBtn('1625280805000');

	// 背景图模型列表
	table.render({
		id: 'messageTable',
		elem: '#messageTable',
		method: 'post',
		url: reportBasePath + 'reportbgimage001',
		where: getTableParams(),
		even: true,
		page: true,
		limits: getLimits(),
		limit: getLimit(),
		cols: [[
			{ title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
			{ field: 'title', title: '标题', align: 'left', width: 150 },
			{ field: 'logo', title: '背景图', align: 'center', width: 120, templet: function (d) {
				return '<img src="' + fileBasePath + d.imagePath + '" class="cursor" lay-event="printsPicUrl">';
			}},
			{ field: 'firstTypeName', title: '一级分类', align: 'left', width: 120 },
			{ field: 'secondTypeName', title: '二级分类', align: 'left', width: 120 },
			{ field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], width: 120 },
			{ field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 },
			{ field: 'lastUpdateName', title: systemLanguage["com.skyeye.lastUpdateName"][languageType], align: 'left', width: 120 },
			{ field: 'lastUpdateTime', title: systemLanguage["com.skyeye.lastUpdateTime"][languageType], align: 'center', width: 150 },
			{ title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 250, toolbar: '#tableBar'}
		]],
		done: function(){
			matchingLanguage();
		}
	});

	table.on('tool(messageTable)', function (obj) {
		var data = obj.data;
		var layEvent = obj.event;
		if (layEvent === 'edit') { // 编辑
			edit(data);
		} else if (layEvent === 'delet') { // 删除
			delet(data);
		} else if (layEvent === 'printsPicUrl') { // 图片预览
			systemCommonUtil.showPicImg(systemCommonUtil.getFilePath(data.imagePath));
		}
	});
	
	// 添加
	$("body").on("click", "#addBean", function() {
		_openNewWindows({
			url: "../../tpl/reportBgImage/reportBgImageAdd.html",
			title: systemLanguage["com.skyeye.addPageTitle"][languageType],
			pageId: "reportBgImageAdd",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	});

	// 编辑
	function edit(data) {
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/reportBgImage/reportBgImageEdit.html",
			title: systemLanguage["com.skyeye.editPageTitle"][languageType],
			pageId: "reportBgImageEdit",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	}

	// 删除
	function delet(data) {
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
			layer.close(index);
			AjaxPostUtil.request({url: reportBasePath + "reportbgimage003", params: {id: data.id}, type: 'json', method: "DELETE", callback: function(json) {
				winui.window.msg("删除成功", {icon: 1, time: 2000});
				loadTable();
			}});
		});
	}
	
	form.on('submit(formSearch)', function (data) {
		if (winui.verifyForm(data.elem)) {
			table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
		}
		return false;
	});
	// 刷新数据
	$("body").on("click", "#reloadTable", function() {
		loadTable();
	});

	function loadTable() {
		table.reloadData("messageTable", {where: getTableParams()});
	}

	function getTableParams() {
		return {
			title: $("#title").val()
		};
	}
    
    exports('reportBgImageList', {});
});
