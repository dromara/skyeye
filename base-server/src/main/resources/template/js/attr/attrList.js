
var objectId = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form', 'soulTable'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		form = layui.form,
		table = layui.table,
		soulTable = layui.soulTable;

	objectId = GetUrlParam("objectId");
	if (isNull(objectId)) {
		winui.window.msg("请传入适用对象信息", {icon: 2, time: 2000});
		return false;
	}

	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: reqBasePath + 'queryAttrDefinitionList',
	    where: getTableParams(),
	    even: true,
	    page: false,
		overflow: {
			type: 'tips',
			header: true,
			total: true
		},
	    limits: getLimits(),
	    limit: getLimit(),
	    cols: [[
			{ title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
			{ field: 'attrKey', title: '属性', align: 'left', width: 150 },
			{ field: 'name', title: '名称', align: 'left', width: 120, templet: function (d) {
				if (!isNull(d.attrDefinitionCustom)) {
					return d.attrDefinitionCustom.name;
				}
				return d.name;
			}},
			{ field: 'attrType', title: '类型', align: 'left', width: 120 },
			{ field: 'componentName', title: '关联组件', align: 'left', width: 140, templet: function (d) {
				if (!isNull(d.attrDefinitionCustom) && !isNull(d.attrDefinitionCustom.dsFormComponent)) {
					return d.attrDefinitionCustom.dsFormComponent.name;
				}
				return '';
			}},
			{ field: 'required', title: '限制条件', align: 'left', width: 140 },
			{ field: 'modelAttribute', title: '是否模型属性', align: 'center', width: 100, templet: function (d) {
				if (d.modelAttribute) {
					return '是';
				}
				return '否';
			}},
			{ field: 'whetherInputParams', title: '是否作为入参', align: 'center', width: 100, templet: function (d) {
				if (d.whetherInputParams) {
					return '是';
				}
				return '否';
			}},
			{ field: 'remark', title: '备注', align: 'left', width: 150 },
			{ title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 120, toolbar: '#tableBar' }
		]],
	    done: function(json) {
			soulTable.render(this);
	    	matchingLanguage();
	    }
	});

	table.on('tool(messageTable)', function (obj) {
		var data = obj.data;
		var layEvent = obj.event;
		if (layEvent === 'edit') { // 编辑
			edit(data);
		} else if (layEvent === 'restore') { // 还原
			restore(data);
		}
	});

	// 删除
	function restore(data) {
		layer.confirm('还原操作', {icon: 3, title: '确定还原该数据吗？'}, function (index) {
			layer.close(index);
			var params = {
				className: objectId,
				attrKey: data.attrKey
			};
			AjaxPostUtil.request({url: reqBasePath + "deleteAttrDefinitionCustom", params: params, type: 'json', method: 'DELETE', callback: function (json) {
				winui.window.msg('还原成功', {icon: 1, time: 2000});
				loadTable();
			}});
		});
	}

	// 编辑
	function edit(data) {
		_openNewWindows({
			url: "../../tpl/attr/writeAttr.html?className=" + objectId + '&attrKey=' + data.attrKey,
			title: systemLanguage["com.skyeye.editPageTitle"][languageType],
			pageId: "writeAttr",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}
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
		return {className: objectId};
	}
	
    exports('attrList', {});
});