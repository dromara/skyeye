
layui.config({
	base: basePath,
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'form', 'soulTable', 'table'].concat(dsFormUtil.mastHaveImport), function (exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
	var $ = layui.$,
		form = layui.form,
		table = layui.table,
		soulTable = layui.soulTable;
	var tableDataList = new Array();
	// 对齐方式
	var alignmentData = skyeyeClassEnumUtil.getEnumDataListByClassName("alignment");
	var rowNum = 1;
	var pageType = GetUrlParam("pageType");
	var cols = [];
	if (pageType == 'processAttr') {
		// 流程属性布局
		cols = [[
			{ type: 'checkbox', align: 'center' },
			{ field: 'test', title: '', align: 'left', width: 40, templet: function (d) {return '<i class="fa fa-arrows drag-row" />';}},
			{ field: 'attrKey', title: '属性<i class="red">*</i>', align: 'left', width: 150, templet: function (d) {
				return `<input type="text" id="attrKey${d.id}" placeholder="请填写属性Key" cus-id="${d.id}" win-verify="required" class="layui-input tableInput" ` +
					`value="` + (isNull(d.attrKey) ? "" : d.attrKey) + `"/>`;
			}},
			{ field: 'name', title: '名称', align: 'left', width: 120, templet: function (d) {
				return `<input type="text" id="name${d.id}" placeholder="请填写名称" cus-id="${d.id}" win-verify="required" class="layui-input tableInput" ` +
					`value="` + (isNull(d.name) ? "" : d.name) + `"/>`;
			}},
			{ field: 'align', title: '对齐方式<i class="red">*</i>', align: 'left', width: 120, templet: function (d) {
				var _html = `<select lay-filter="tableSelect" lay-search="" id="align${d.id}" cus-id="${d.id}" win-verify="required"><option value="">全部</option>`;
				$.each(alignmentData.rows, function (i, item) {
					if (item.id == d.align) {
						_html += `<option value="${item.id}" selected="selected">${item.name}</option>`;
					} else {
						_html += `<option value="${item.id}">${item.name}</option>`;
					}
				});
				_html += `</select>`;
				return _html;
			}},
			{ field: 'width', title: '宽度<i class="red">*</i>', align: 'left', width: 120, templet: function (d) {
				return `<input type="text" id="width${d.id}" placeholder="请填写宽度" cus-id="${d.id}" class="layui-input tableInput" win-verify="required|number" ` +
					`value="` + (isNull(d.width) ? "" : d.width) + `"/>`;
			}},
			{ field: 'templetBox', title: '脚本', align: 'left', width: 700, templet: function (d) {
				return `<textarea id="templet${d.id}" placeholder="请填写脚本" cus-id="${d.id}" class="tableInput templateClass"></textarea>`;
			}},
		]];
	} else {
		// 新增/编辑布局
		cols = [[
			{ type: 'checkbox', align: 'center' },
			{ field: 'test', title: '', align: 'left', width: 40, templet: function (d) {return '<i class="fa fa-arrows drag-row" />';}},
			{ field: 'attrKey', title: '属性<i class="red">*</i>', align: 'left', width: 150, templet: function (d) {
				return `<input type="text" id="attrKey${d.id}" placeholder="请填写属性Key" cus-id="${d.id}" win-verify="required" class="layui-input tableInput" ` +
					`value="` + (isNull(d.attrKey) ? "" : d.attrKey) + `"/>`;
			}},
			{ field: 'name', title: '名称', align: 'left', width: 120, templet: function (d) {
				return `<input type="text" id="name${d.id}" placeholder="请填写名称" cus-id="${d.id}" win-verify="required" class="layui-input tableInput" ` +
					`value="` + (isNull(d.name) ? "" : d.name) + `"/>`;
			}},
			{ field: 'width', title: '宽度<i class="red">*</i>', align: 'left', width: 120, templet: function (d) {
				return `<input type="text" id="width${d.id}" placeholder="请填写宽度" cus-id="${d.id}" class="layui-input tableInput" win-verify="required|number" ` +
					`value="` + (isNull(d.width) ? "" : d.width) + `"/>`;
			}},
		]]
	}

	var jsEditorMap = {};
	table.render({
		id: 'messageTable',
		elem: '#messageTable',
		method: 'get',
		data: tableDataList,
		even: true,
		page: false,
		rowDrag: {
			trigger: '.drag-row',
			done: function(obj) {}
		},
		cols: cols,
		done: function(json) {
			matchingLanguage();
			if ($(`div[lay-id='messageTable']`).find('.place-holder').length == 0) {
				$(`div[lay-id='messageTable']`).find('.layui-table-body').append('<div class="place-holder"></div>');
			}
			soulTable.render(this);

			jsEditorMap = {};
			$.each($(".templateClass"), function (i, item) {
				var id = $(item).attr('cus-id');
				var tableData = getInPoingArr(tableDataList, 'id', id);
				var jsEditor = CodeMirror.fromTextArea(document.getElementById("templet" + id), codeUtil.getConfig('text/javascript'));
				if (!isNull(tableData.templet)) {
					jsEditor.setValue(tableData.templet);
				}
				jsEditorMap[id] = jsEditor;
			});
		}
	});

	matchingLanguage();
	form.render();
	form.on('submit(formEditBean)', function (data) {
		if (winui.verifyForm(data.elem)) {
			if (table.cache.messageTable.length == 0) {
				winui.window.msg('请选择表格属性.', {icon: 2, time: 2000});
				return false;
			}
			tableDataList = [].concat(table.cache.messageTable);
			$.each(tableDataList, function (i, item) {
				item.orderBy = i + 1;
				if (pageType == 'processAttr') {
					item.templet = jsEditorMap[item.id].getValue();
				}
				item.id = null;
			});

			console.log(tableDataList);
			// parent.layer.close(index);
			// parent.refreshCode = '0';
		}
		return false;
	});

	form.on('select(tableSelect)', function(data) {
		var id = data.elem.id;
		buildData($(`#${id}`));
	});
	$("body").on("input", ".tableInput", function () {
		buildData($(this));
	});
	$("body").on("change", ".tableInput", function () {
		buildData($(this));
	});

	function buildData(_this) {
		var id = _this.attr('cus-id');
		var key = _this.attr('id').replace(id, '');
		$.each(tableDataList, function (j, item) {
			if (item.id == id) {
				item[key] = _this.val();
			}
		});
	}

	$("body").on("click", "#addRow", function() {
		addRow();
	});

	$("body").on("click", "#deleteRow", function() {
		deleteRow();
	});

	// 新增行
	function addRow() {
		tableDataList = [].concat(table.cache.messageTable);
		tableDataList.push({id: rowNum});
		table.reloadData("messageTable", {data: tableDataList});
		rowNum++;
	}

	// 删除行
	function deleteRow() {
		tableDataList = [].concat(table.cache.messageTable);
		var check_box = table.checkStatus('messageTable').data;
		for (var i = 0;  i < check_box.length; i++){
			var list = [];
			$.each(tableDataList, function(j, item) {
				if(item.id != check_box[i].id){
					list.push(item);
				}
			});
			tableDataList = [].concat(list);
		}
		table.reloadData("messageTable", {data: tableDataList});
	}

	$("body").on("click", "#cancle", function() {
		parent.layer.close(index);
	});
});