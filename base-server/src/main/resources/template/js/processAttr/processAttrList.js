
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

	objectId = GetUrlParam("objectId");
	if (isNull(objectId)) {
		winui.window.msg("请传入适用对象信息", {icon: 2, time: 2000});
		return false;
	}

	AjaxPostUtil.request({url: flowableBasePath + 'queryActFlowListByClassName', params: {className: objectId}, type: 'json', method: "POST", callback: function (json) {
		$("#actFlowId").html(getDataUseHandlebars(`{{#each rows}}<option value="{{id}}">{{flowName}}</option>{{/each}}`, json));
	}, async: false});

	form.on('select(actFlowId)', function(data) {
		loadTable();
	});

	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: reqBasePath + 'queryAttrTransformList',
	    where: getTableParams(),
	    even: true,
	    page: false,
	    limits: getLimits(),
	    limit: getLimit(),
	    cols: [[
			{ title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
			{ field: 'label', title: '属性', align: 'left', width: 150 },
			{ field: 'proportion', title: '宽度比例', align: 'left', width: 120, templet: function (d) {
				return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("widthScale", 'id', d.proportion, 'name');
			}},
			{ field: 'orderBy', title: '排序', align: 'left', width: 100 },
			{ field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], width: 120 },
			{ field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 },
			{ field: 'lastUpdateName', title: systemLanguage["com.skyeye.lastUpdateName"][languageType], align: 'left', width: 120 },
			{ field: 'lastUpdateTime', title: systemLanguage["com.skyeye.lastUpdateTime"][languageType], align: 'center', width: 150 },
			{ title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 180, toolbar: '#tableBar' }
		]],
	    done: function(json) {
	    	matchingLanguage();
	    }
	});

	table.on('tool(messageTable)', function (obj) {
		var data = obj.data;
		var layEvent = obj.event;
		if (layEvent === 'del') { // 删除
			del(data, obj);
		} else if (layEvent === 'edit') { // 编辑
			edit(data);
		}
	});

	// 删除
	function del(data, obj) {
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function (index) {
			layer.close(index);
			AjaxPostUtil.request({url: reqBasePath + "deleteAttrTransformById", params: {id: data.id}, type: 'json', method: "DELETE", callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
		});
	}

	// 编辑
	function edit(data) {
		rowId = data.id;
		if (isNull($("#actFlowId"))) {
			winui.window.msg('请先绑定流程.', {icon: 2, time: 2000});
			return false;
		}
		_openNewWindows({
			url: "../../tpl/processAttr/processAttrEdit.html",
			title: systemLanguage["com.skyeye.editPageTitle"][languageType],
			pageId: "processAttrEdit",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	}

	// 新增
	$("body").on("click", "#addBean", function() {
		if (isNull($("#actFlowId"))) {
			winui.window.msg('请先绑定流程.', {icon: 2, time: 2000});
			return false;
		}
		_openNewWindows({
			url: "../../tpl/processAttr/processAttrAdd.html",
			title: systemLanguage["com.skyeye.addPageTitle"][languageType],
			pageId: "processAttrAdd",
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
		return {
			className: objectId,
			actFlowId: $('#actFlowId').val()
		};
	}

    exports('processAttrList', {});
});