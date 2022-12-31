
layui.config({
	base: basePath,
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'form', 'soulTable', 'table'], function (exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
	var $ = layui.$,
		form = layui.form,
		table = layui.table,
		soulTable = layui.soulTable;
	var childServiceClassName = '';
	var tableDataList = new Array();
	var alignmentData;
	var rowNum = 1;
	AjaxPostUtil.request({url: reqBasePath + "queryAttrTransformById", params: {id: parent.rowId}, type: 'json', method: "GET", callback: function (json) {
		$("#showForm").html(getDataUseHandlebars($("#beanTemplate").html(), json));

		AjaxPostUtil.request({url: reqBasePath + "queryAttrDefinitionList", params: {className: parent.objectId}, type: 'json', method: "POST", callback: function (data) {
			$("#attrKey").html(getDataUseHandlebars(`<option value="">全部</option>{{#each rows}}<option value="{{attrKey}}">{{name}}</option>{{/each}}`, data));
			$("#attrKey").val(json.bean.attrKey);
		}, async: false});

		skyeyeClassEnumUtil.showEnumDataListByClassName("dsFormShowType", 'select', "showType", json.bean.showType, form);
		skyeyeClassEnumUtil.showEnumDataListByClassName("widthScale", 'select', "proportion", json.bean.proportion, form);
		alignmentData = skyeyeClassEnumUtil.getEnumDataListByClassName("alignment");

		if (json.bean.showType == 5) {
			$("#tableBox").show();
			$.each(json.bean.attrTransformTableList, function(j, item) {
				item.id = rowNum;
				tableDataList.push(item);
				rowNum++;
			});
			buildTable();
		} else {
			$("#tableBox").hide();
		}

		matchingLanguage();
		form.render();
		form.on('submit(formEditBean)', function (data) {
			if (winui.verifyForm(data.elem)) {
				if ($("#showType").val() == 5) {
					if (table.cache.messageTable.length == 0) {
						winui.window.msg('请选择表格属性.', {icon: 2, time: 2000});
						return false;
					}
					$.each(table.cache.messageTable, function (i, item) {
						item.id = null;
						item.className = childServiceClassName;
						item.parentAttrKey = $("#attrKey").val();
						item.orderBy = i + 1;
						item.actFlowId = parent.$("#actFlowId").val();
					});
					tableDataList = [].concat(table.cache.messageTable);
				} else {
					tableDataList = new Array();
				}

				var params = {
					className: parent.objectId,
					attrKey: $("#attrKey").val(),
					orderBy: $("#orderBy").val(),
					showType: $("#showType").val(),
					attrTransformTableList: JSON.stringify(tableDataList),
					proportion: $("#proportion").val(),
					actFlowId: parent.$("#actFlowId").val(),
					id: parent.rowId
				};
				AjaxPostUtil.request({url: reqBasePath + "writeAttrTransform", params: params, type: 'json', method: "POST", callback: function (json) {
					parent.layer.close(index);
					parent.refreshCode = '0';
				}});
			}
			return false;
		});
	}, async: false});

	form.on('select(attrKey)', function(data) {
		tableDataList = new Array();
		buildTable();
	});
	form.on('select(showType)', function(data) {
		var value = data.value;
		if (value == 5) {
			$("#tableBox").show();
		} else {
			$("#tableBox").hide();
		}
		tableDataList = new Array();
		buildTable();
	});

	function buildTable() {
		if ($("#showType").val() == 5 && !isNull($("#attrKey").val())) {
			var params = {
				className: parent.objectId,
				attrKey: $("#attrKey").val()
			};
			var childAttr = [];
			AjaxPostUtil.request({url: reqBasePath + "queryChildAttrDefinitionList", params: params, type: 'json', method: "POST", callback: function (json) {
				childAttr = [].concat(json.rows);
				if (childAttr.length > 0) {
					childServiceClassName = childAttr[0].className;
				}
			}, async: false});
			table.render({
				id: 'messageTable',
				elem: '#messageTable',
				method: 'get',
				data: tableDataList,
				even: true,
				page: false,
				rowDrag: {
					trigger: 'row',
					done: function(obj) {}
				},
				cols: [[
					{ type: 'checkbox', align: 'center' },
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
					{ field: 'name', title: '名称', align: 'left', width: 120, templet: function (d) {
						return `<input type="text" id="name${d.id}" placeholder="请填写名称" cus-id="${d.id}" class="layui-input tableInput" ` +
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
					{ field: 'templet', title: '脚本', align: 'left', width: 300, templet: function (d) {
						return `<input type="text" id="templet${d.id}" placeholder="请填写脚本" cus-id="${d.id}" class="layui-input tableInput" ` +
							`value='` + (isNull(d.templet) ? "" : d.templet) + `'/>`;
					}},
				]],
				done: function(json) {
					matchingLanguage();
					if ($(`div[lay-id='messageTable']`).find('.place-holder').length == 0) {
						$(`div[lay-id='messageTable']`).find('.layui-table-body').append('<div class="place-holder"></div>');
					}
					soulTable.render(this);
				}
			});
		} else {
			table.reloadData("messageTable", {data: tableDataList});
		}
	}

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