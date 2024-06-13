
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

	var holderId = GetUrlParam("holderId");
	if (isNull(holderId)) {
		winui.window.msg("请传入适用对象信息", {icon: 2, time: 2000});
		return false;
	}

	table.render({
		id: 'messageTable',
		elem: '#messageTable',
		method: 'post',
        url: sysMainMation.erpBasePath + 'queryPurchaseDeliveryList',
		where: getTableParams(),
		even: true,
		page: true,
		limits: getLimits(),
		limit: getLimit(),
		cols: [[
			{ type: 'radio', fixed: 'left', rowspan: '2' },
			{ title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers', rowspan: '2' },
			{ field: 'oddNumber', title: '单号', rowspan: '2', width: 200, align: 'center', templet: function (d) {
				return '<a lay-event="details" class="notice-title-click">' + d.oddNumber + '</a>';
				if (!isNull(d.fromId)) {
					str += '<span class="state-new">[转]</span>';
				}
				return str;
			}},
			{ field: 'holderMation', title: '供应商', rowspan: '2', align: 'left', width: 150, templet: function (d) {
				return getNotUndefinedVal(d.holderMation?.name);
			}},
			{ field: 'totalPrice', title: '合计金额', rowspan: '2', align: 'left', width: 120 },
			{ field: 'operTime', title: '单据日期', rowspan: '2', align: 'center', width: 140 },
			{ colspan: '2', title: '来源单据信息', align: 'center' },
			{ field: 'state', title: '状态', rowspan: '2', width: 90, templet: function (d) {
				return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("erpOrderStateEnum", 'id', d.state, 'name');
			}},
			{ field: 'qualityInspection', title: '质检状态', rowspan: '2', width: 90, templet: function (d) {
				return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("orderQualityInspectionType", 'id', d.qualityInspection   , 'name');
			}},
			{ field: 'otherState', title: '免检商品入库状态', rowspan: '2', width: 150, templet: function (d) {
				return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("deliveryPutState", 'id', d.otherState, 'name');
			}},
			{ field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], rowspan: '2', width: 120 },
			{ field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], rowspan: '2', align: 'center', width: 150 },
			{ field: 'lastUpdateName', title: systemLanguage["com.skyeye.lastUpdateName"][languageType], rowspan: '2', align: 'left', width: 120 },
			{ field: 'lastUpdateTime', title: systemLanguage["com.skyeye.lastUpdateTime"][languageType], rowspan: '2', align: 'center', width: 150 },
			{ title: systemLanguage["com.skyeye.operation"][languageType], rowspan: '2', fixed: 'right', align: 'center', width: 200, toolbar: '#tableBar'}
		], [
			{ field: 'fromTypeId', title: '来源类型', width: 150, templet: function (d) {
				return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("purchaseOrderFromType", 'id', d.fromTypeId, 'name');
			}},
			{ field: 'fromId', title: '单据编号', width: 200, templet: function (d) {
				return getNotUndefinedVal(d.fromMation?.oddNumber);
			}}
		]],
		done: function(json) {
			matchingLanguage();
			initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "请输入单号", function () {
				table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
			});

			systemCommonUtil.disabledAllRow('radio');
			for (var i = 0; i < json.rows.length; i++) {
				// 只有状态为：【审核通过】并且【待质检/部分质检】的到货单才可以进行选中
				let state = json.rows[i].state;
				let qualityInspection = json.rows[i].qualityInspection;
				if (state == 'pass' &&
					(qualityInspection == 2 || qualityInspection == 3)) {
					systemCommonUtil.enabledRow(json.rows[i].LAY_TABLE_INDEX, 'radio');
				}
			}
			$('#messageTable').next().find('.layui-table-body').find("table" ).find("tbody").children("tr").on('dblclick',function(){
				var dubClick = $('#messageTable').next().find('.layui-table-body').find("table").find("tbody").find(".layui-table-hover");
				if (!dubClick.find("input[type='radio']").prop("disabled")) {
					dubClick.find("input[type='radio']").prop("checked", true);
					form.render();
					var chooseIndex = JSON.stringify(dubClick.data('index'));
					var obj = json.rows[chooseIndex];
					parent.purchaseDeliveryMation = obj;

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
		return $.extend(true, {holderId: holderId}, initTableSearchUtil.getSearchValue("messageTable"));
	}

    exports('purchaseDeliveryNoteChooseList', {});
});