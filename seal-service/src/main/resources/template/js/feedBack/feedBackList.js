
var objectKey = "";
var objectId = "";

layui.config({
	base: basePath,
	version: skyeyeVersion
}).extend({
	window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'form', 'table'], function (exports) {
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

	var state;
	AjaxPostUtil.request({url: sysMainMation.sealServiceBasePath + "querySealServiceOrderById", params: {id: objectId}, type: 'json', method: 'GET', callback: function(json) {
		state = json.bean.state;
		if (state != 'beCompleted' && state != 'beEvaluated') {
			$("#addBean").hide();
		}
		initTable();
	}});

	function initTable() {
		table.render({
			id: 'messageTable',
			elem: '#messageTable',
			method: 'post',
			url: sysMainMation.sealServiceBasePath + 'queryFeedBackList',
			where: getTableParams(),
			even: true,
			page: true,
			limits: getLimits(),
			limit: getLimit(),
			cols: [[
				{ title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
				{ field: 'typeId', title: '反馈类型', width: 200, align: 'left', templet: function (d) {
					return sysDictDataUtil.getDictDataNameByCodeAndKey(sysDictData["amsServiceFeedbBackType"]["key"], d.typeId);
				}},
				{ field: 'content', title: '反馈内容', align: 'left', width: 300 },
				{ field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], align: 'left', width: 120 },
				{ field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 },
				{ field: 'lastUpdateName', title: systemLanguage["com.skyeye.lastUpdateName"][languageType], align: 'left', width: 120 },
				{ field: 'lastUpdateTime', title: systemLanguage["com.skyeye.lastUpdateTime"][languageType], align: 'center', width: 150 },
				{ title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 120, templet: function (d) {
					var str = '';
					if (state == 'beCompleted' || state == 'beEvaluated') {
						str += '<a class="layui-btn layui-btn-xs layui-btn-normal" lay-event="edit"><language showName="com.skyeye.editBtn"></language></a>';
						str += '<a class="layui-btn layui-btn-xs layui-btn-danger" lay-event="del">删除</a>';
					}
					return str;
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
			if (layEvent === 'edit') { //编辑
				edit(data);
			} else if (layEvent === 'del'){ //删除
				del(data, obj);
			}
		});
	}

	// 添加
	$("body").on("click", "#addBean", function() {
		_openNewWindows({
			url: systemCommonUtil.getUrl('FP2023091000006&objectId=' + objectId + '&objectKey=' + objectKey, null),
			title: systemLanguage["com.skyeye.addPageTitle"][languageType],
			pageId: "feedBackAdd",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	});

	// 编辑
	function edit(data) {
		_openNewWindows({
			url: systemCommonUtil.getUrl('FP2023091000007&objectId=' + objectId + '&objectKey=' + objectKey + '&id=' + data.id, null),
			title: systemLanguage["com.skyeye.editPageTitle"][languageType],
			pageId: "feedBackEdit",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}
		});
	}

	// 删除
	function del(data, obj) {
		layer.confirm('确认删除选中数据吗？', {icon: 3, title: '删除操作'}, function (index) {
			layer.close(index);
			AjaxPostUtil.request({url: sysMainMation.sealServiceBasePath + "deleteFeedBackById", params: {id: data.id}, type: 'json', method: 'DELETE', callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
		});
	}

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

	exports('feedBackList', {});
});
