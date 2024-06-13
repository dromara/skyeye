
layui.config({
	base: basePath,
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form'], function (exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
	var $ = layui.$,
		form = layui.form,
		table = layui.table;

	var objectId = GetUrlParam("objectId");
	if (isNull(objectId)) {
		winui.window.msg("请传入适用对象信息", {icon: 2, time: 2000});
		return false;
	}

	table.render({
		id: 'messageTable',
		elem: '#messageTable',
		method: 'post',
        url: sysMainMation.erpBasePath + 'querySupplierContractList',
		where: getTableParams(),
		even: true,
		page: true,
		limits: getLimits(),
		limit: getLimit(),
		cols: [[
			{ type: 'radio', fixed: 'left'},
			{ title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers', rowspan: '2' },
			{ field: 'title', title: '合同名称', rowspan: '2', align: 'left', width: 200, templet: function (d) {
				return '<a lay-event="details" class="notice-title-click">' + d.title + '</a>';
			}},
			{ field: 'oddNumber', title: '合同编号', rowspan: '2', align: 'left', width: 150 },
			{ field: 'price', title: '合同金额（元）', rowspan: '2', align: 'left', width: 120 },
			{ field: 'signingTime', title: '签约日期', rowspan: '2', align: 'center', width: 100 },
			{ colspan: '2', title: '来源单据信息', align: 'center' },
			{ field: 'state', title: '状态', rowspan: '2', width: 90, templet: function (d) {
				return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("supplierContractStateEnum", 'id', d.state, 'name');
			}},
			{ field: 'childState', title: '合同产品状态', rowspan: '2', width: 150, templet: function (d) {
				return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("supplierContractChildStateEnum", 'id',d.childState, 'name');
			}},
			{ field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], rowspan: '2', align: 'left', width: 120 },
			{ field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], rowspan: '2', align: 'center', width: 150 },
			{ field: 'lastUpdateName', title: systemLanguage["com.skyeye.lastUpdateName"][languageType], rowspan: '2', align: 'left', width: 120 },
			{ field: 'lastUpdateTime', title: systemLanguage["com.skyeye.lastUpdateTime"][languageType], rowspan: '2', align: 'center', width: 150 },
		], [
			{ field: 'fromTypeId', title: '来源类型', width: 150, templet: function (d) {
				return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("supplierContractFromType", 'id', d.fromTypeId, 'name');
			}},
			{ field: 'fromId', title: '单据编号', width: 200, templet: function (d) {
				return getNotUndefinedVal(d.fromMation?.oddNumber);
			}}
		]],
		done: function(json) {
			matchingLanguage();
			initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "请输入合同名称", function () {
				table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
			});

			for (var i = 0; i < json.rows.length; i++) {
				// 只有【审核通过、执行中】状态的合同才可以进行选中
				if (json.rows[i].state != 'executing' && json.rows[i].state != 'pass') {
					systemCommonUtil.disabledRow(json.rows[i].LAY_TABLE_INDEX, 'radio');
				}
			}
			$('#messageTable').next().find('.layui-table-body').find("table" ).find("tbody").children("tr").on('dblclick',function(){
				var dubClick = $('#messageTable').next().find('.layui-table-body').find("table").find("tbody").find(".layui-table-hover");
				if (!dubClick.find("input[type='radio']").prop("disabled")) {
					dubClick.find("input[type='radio']").prop("checked", true);
					form.render();
					var chooseIndex = JSON.stringify(dubClick.data('index'));
					var obj = json.rows[chooseIndex];
					parent.supplierContractMation = obj;

					parent.refreshCode = '0';
					parent.layer.close(index);
				}
			});

			$('#messageTable').next().find('.layui-table-body').find("table" ).find("tbody").children("tr").on('click',function(){
				var click = $('#messageTable').next().find('.layui-table-body').find("table").find("tbody").find(".layui-table-hover");
				if (!click.find("input[type='radio']").prop("disabled")) {
					click.find("input[type='radio']").prop("checked", true);
					form.render();
				}
			})
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
		return $.extend(true, {objectId: objectId}, initTableSearchUtil.getSearchValue("messageTable"));
	}

    exports('supplierContractChooseList', {});
});