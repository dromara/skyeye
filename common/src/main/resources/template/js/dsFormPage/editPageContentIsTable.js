
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
	var tableDataList = parent.temData;
	tableDataList = isNull(tableDataList) ? [] : JSON.parse(tableDataList);
	// 对齐方式
	var alignmentData = skyeyeClassEnumUtil.getEnumDataListByClassName("alignment");
	var rowNum = 1;
	$.each(tableDataList, function (i, item) {
		item["id"] = rowNum;
		rowNum++;
	});

	var parentAttrKey = GetUrlParam("attrKey");
	var parentClassName = GetUrlParam("className");
	var pageType = GetUrlParam("pageType");
	var params = {
		className: parentClassName,
		attrKey: parentAttrKey
	};
	var childAttr = [];
	AjaxPostUtil.request({url: reqBasePath + "queryChildAttrDefinitionList", params: params, type: 'json', method: "POST", callback: function (json) {
		$.each(json.rows, function (i, item) {
			if (item.whetherInputParams) {
				childAttr.push(item);
			}
		});
		if (childAttr.length > 0) {
			childServiceClassName = childAttr[0].className;
		}
	}, async: false});

	var showType = [
		{id: "input", name: "输入框"},
		{id: "chooseInput", name: "选择输入框"},
		{id: "select", name: "下拉框"},
		{id: "detail", name: "文本"}
	];

	var cols;
	if (pageType == 'details') {
		cols = [[
			{ type: 'checkbox', align: 'center' },
			{ field: 'test', title: '', align: 'left', width: 40, templet: function (d) {return '<i class="fa fa-arrows drag-row" />';}},
			{ field: 'attrKey', title: '属性<i class="red">*</i>', align: 'left', width: 150, templet: function (d) {
				var _html = `<select lay-filter="tableSelect" lay-search="" id="attrKey${d.id}" cus-id="${d.id}" win-verify="required"><option value="">全部</option>`;
				$.each(childAttr, function (i, item) {
					if (item.attrKey == d.attrKey) {
						_html += `<option value="${item.attrKey}" selected="selected">${item.name}</option>`;
					} else {
						_html += `<option value="${item.attrKey}">${item.name}</option>`;
					}
				});
				_html += `</select>`;
				return _html;
			}},
			{ field: 'name', title: '显示名称<i class="red">*</i>', align: 'left', width: 120, templet: function (d) {
				return `<input type="text" id="name${d.id}" placeholder="请填写显示名称" cus-id="${d.id}" win-verify="required" class="layui-input tableInput" ` +
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
			{ field: 'showType', title: '显示方式<i class="red">*</i>', align: 'left', width: 120, templet: function (d) {
				var _html = `<select lay-filter="tableSelect" lay-search="" id="showType${d.id}" cus-id="${d.id}" win-verify="required"><option value="">全部</option>`;
				$.each(showType, function (i, item) {
					if (item.id == d.showType) {
						_html += `<option value="${item.id}" selected="selected">${item.name}</option>`;
					} else {
						_html += `<option value="${item.id}">${item.name}</option>`;
					}
				});
				_html += `</select>`;
				return _html;
			}},
			{ field: 'dataFrom', title: '数据源', align: 'left', width: 200, templet: function (d) {
				var disabledClass = d.showType == showType[2].id ? '' : 'layui-btn-disabled';
				var btnName = '选择数据源';
				if (!isNull(d.dataType)) {
					btnName = '更换数据源';
				}
				return `<button id="dataFrom${d.id}" type="button" cus-id="${d.id}" class="chooseDataFrom ${disabledClass}">${btnName}</button>`;
			}}
		]];
	} else {
		cols = [[
			{ type: 'checkbox', align: 'center' },
			{ field: 'test', title: '', align: 'left', width: 40, templet: function (d) {return '<i class="fa fa-arrows drag-row" />';}},
			{ field: 'attrKey', title: '属性<i class="red">*</i>', align: 'left', width: 150, templet: function (d) {
				var _html = `<select lay-filter="tableSelect" lay-search="" id="attrKey${d.id}" cus-id="${d.id}" win-verify="required"><option value="">全部</option>`;
				$.each(childAttr, function (i, item) {
					if (item.attrKey == d.attrKey) {
						_html += `<option value="${item.attrKey}" selected="selected">${item.name}</option>`;
					} else {
						_html += `<option value="${item.attrKey}">${item.name}</option>`;
					}
				});
				_html += `</select>`;
				return _html;
			}},
			{ field: 'name', title: '显示名称<i class="red">*</i>', align: 'left', width: 120, templet: function (d) {
				return `<input type="text" id="name${d.id}" placeholder="请填写显示名称" cus-id="${d.id}" win-verify="required" class="layui-input tableInput" ` +
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
			{ field: 'layFilter', title: '表单监听Filter', align: 'left', width: 120, templet: function (d) {
				return `<input type="text" id="layFilter${d.id}" placeholder="表单监听Filter" cus-id="${d.id}" class="layui-input tableInput" ` +
					`value="` + (isNull(d.layFilter) ? "" : d.layFilter) + `"/>`;
			}},
			{ field: 'width', title: '宽度<i class="red">*</i>', align: 'left', width: 120, templet: function (d) {
				return `<input type="text" id="width${d.id}" placeholder="请填写宽度" cus-id="${d.id}" class="layui-input tableInput" win-verify="required|number" ` +
					`value="` + (isNull(d.width) ? "" : d.width) + `"/>`;
			}},
			{ field: 'showType', title: '显示方式<i class="red">*</i>', align: 'left', width: 120, templet: function (d) {
				var _html = `<select lay-filter="tableSelect" lay-search="" id="showType${d.id}" cus-id="${d.id}" win-verify="required"><option value="">全部</option>`;
				$.each(showType, function (i, item) {
					if (item.id == d.showType) {
						_html += `<option value="${item.id}" selected="selected">${item.name}</option>`;
					} else {
						_html += `<option value="${item.id}">${item.name}</option>`;
					}
				});
				_html += `</select>`;
				return _html;
			}},
			{ field: 'require', title: '限制条件', align: 'left', width: 200, templet: function (d) {
				return `<div id="require${d.id}" cus-id="${d.id}"></div>`;
			}},
			{ field: 'dataFrom', title: '数据源', align: 'left', width: 200, templet: function (d) {
				var disabledClass = d.showType == showType[2].id ? '' : 'layui-btn-disabled';
				var btnName = '选择数据源';
				if (!isNull(d.dataType)) {
					btnName = '更换数据源';
				}
				return `<button id="dataFrom${d.id}" type="button" cus-id="${d.id}" class="chooseDataFrom ${disabledClass}">${btnName}</button>`;
			}},
			{ field: 'value', title: '默认值', align: 'left', width: 120, templet: function (d) {
				return `<input type="text" id="value${d.id}" placeholder="默认值" cus-id="${d.id}" class="layui-input tableInput" ` +
					`value="` + (isNull(d.value) ? "" : d.value) + `"/>`;
			}},
			{ field: 'className', title: 'class属性', align: 'left', width: 120, templet: function (d) {
				return `<input type="text" id="className${d.id}" placeholder="class属性" cus-id="${d.id}" class="layui-input tableInput" ` +
					`value="` + (isNull(d.className) ? "" : d.className) + `"/>`;
			}},
			{ field: 'iconClassName', title: 'ICON的class属性', align: 'left', width: 150, templet: function (d) {
				return `<input type="text" id="iconClassName${d.id}" placeholder="ICON的class属性" cus-id="${d.id}" class="layui-input tableInput" ` +
					`value="` + (isNull(d.iconClassName) ? "" : d.iconClassName) + `"/>`;
			}},
		]];
	}

	table.render({
		id: 'messageTable',
		elem: '#messageTable',
		method: 'get',
		data: tableDataList,
		even: true,
		page: false,
		limit: 100,
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

			$.each(tableDataList, function (i, item) {
				// 限制条件
				var require = isNull(item.require) ? '' : ($.isArray(item.require) ? item.require.toString() : JSON.parse(item.require).toString());
				skyeyeClassEnumUtil.showEnumDataListByClassName("verificationParams", 'verificationSelect', "require" + item.id, require, form, null, 'formerRequirement');

				// 数据源
				var params = {};
				if (!isNull(item.dataType)) {
					params = {
						dataType: item.dataType,
						defaultData: item.defaultData,
						objectId: item.objectId,
						businessApi: item.businessApi,
					};
				}
				var data = JSON.stringify(params);
				$(`#dataFrom${item.id}`).attr("data", data);
			});

			soulTable.render(this);
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
				if (item.showType == showType[2].id) {
					// 下拉框时，选择的数据源信息
					var dataFrom = JSON.parse($(`#dataFrom${item.id}`).attr('data'));
					if (!isNull(dataFrom.dataType)) {
						item.dataType = dataFrom.dataType;
						item.defaultData = dataFrom.defaultData;
						item.objectId = dataFrom.objectId;
						item.businessApi = dataFrom.businessApi;
					}
				}
				item.require = dataShowType.getData(`require${item.id}`);
				delete item["LAY_TABLE_INDEX"];
				delete item["id"];
			});

			parent.temData = JSON.stringify(tableDataList);
			parent.layer.close(index);
			parent.refreshCode = '0';
		}
		return false;
	});

	// 选择数据来源
	$("body").on("click", ".chooseDataFrom", function() {
		if ($(this).hasClass('layui-btn-disabled')) {
			return false;
		}
		var id = $(this).attr('id');
		_openNewWindows({
			url: "../../tpl/dsFormPage/dataFrom.html?id=" + id,
			title: '数据来源',
			pageId: "dataFrom",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
			}});
	});

	form.on('select(tableSelect)', function(data) {
		var id = data.elem.id;
		var _this = $(`#${id}`);
		buildData(_this);
		// 显示方式
		if (id.startsWith('showType')) {
			var showTypeVal = _this.val();
			var cusId = _this.attr('cus-id');
			if (showTypeVal == showType[2].id) {
				// 下拉框
				$(`#dataFrom${cusId}`).removeClass('layui-btn-disabled');
				$(`#dataFrom${cusId}`).attr('data', '{}');
				$(`#dataFrom${cusId}`).html('选择数据源');
			} else {
				$(`#dataFrom${cusId}`).addClass('layui-btn-disabled');
			}
		}
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