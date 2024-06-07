
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

	table.render({
		id: 'messageTable',
		elem: '#messageTable',
		method: 'post',
        url: sysMainMation.erpBasePath + 'queryPurchaseRequestList',
		where: getTableParams(),
		even: true,
		page: true,
		limits: getLimits(),
		limit: getLimit(),
		cols: [[
			{ type: 'radio', fixed: 'left'},
			{ title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers', rowspan: 2},
			{ field: 'oddNumber', title: '单号', width: 200, align: 'center', templet: function (d) {
				return '<a lay-event="details" class="notice-title-click">' + d.oddNumber + '</a>';
			}},
			{ field: 'processInstanceId', title: '流程ID', width: 100, templet: function (d) {
				return '<a lay-event="processDetails" class="notice-title-click">' + getNotUndefinedVal(d.processInstanceId) + '</a>';
			}},
			{ field: 'title', title: '单据主题', align: 'left', width: 120 },
			{ field: 'fromTypeId', title: '单据来源', width: 90, templet: function (d) {
				return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("purchaseRequestFromType", 'id', d.fromTypeId, 'name');
			}},
			{ field: 'projectMation', title: '项目', align: 'left', width: 150, templet: function (d) {
				return getNotUndefinedVal(d.projectMation?.name);
			}},
			{ field: 'totalPrice', title: '合计金额', align: 'left', width: 120 },
			{ field: 'operTime', title: '单据日期', align: 'center', width: 140 },
			{ field: 'inquiryState', title: '询价状态', width: 90, templet: function (d) {
				return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("purchaseRequestInquiryState", 'id', d.inquiryState, 'name');
			}},
			{ field: 'state', title: '状态', width: 90, templet: function (d) {
				return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("purchaseRequestStateEnum", 'id', d.state, 'name');
			}},
			{ field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], width: 120 },
			{ field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 },
			{ field: 'lastUpdateName', title: systemLanguage["com.skyeye.lastUpdateName"][languageType], align: 'left', width: 120 },
			{ field: 'lastUpdateTime', title: systemLanguage["com.skyeye.lastUpdateTime"][languageType], align: 'center', width: 150 },
			{ title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 200, toolbar: '#tableBar'}
		]],
		done: function(json) {
			matchingLanguage();
			initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "请输入单号", function () {
				table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
			});

			for (var i = 0; i < json.rows.length; i++) {
				// 只有审批通过、执行中的项目才可以进行选中
				let state = json.rows[i].state;
				let inquiryState = json.rows[i].inquiryState;
				if ((state == 'pass' && (inquiryState == 1 || inquiryState == 4)) ||
					(state == 'partialProcurement' && (inquiryState == 1 || inquiryState == 4))) {
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
					parent.purchaseRequestMation = obj;

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
		return $.extend(true, {}, initTableSearchUtil.getSearchValue("messageTable"));
	}

    exports('purchaseRequestChooseList', {});
});