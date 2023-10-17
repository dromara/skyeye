
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

	// 对左侧菜单项的点击事件
	var clickId = "noCheckList";
	$("body").on("click", "#setting a", function (e) {
		$(".setting a").removeClass("selected");
		$(this).addClass("selected");
		clickId = $(this).attr("rowid");
		table.reload('messageTable', {
			where: getTableParams(),
			cols: [getTableCols()]
		});
	});

	showTableList();
	function showTableList(){
		table.render({
		    id: 'messageTable',
		    elem: '#messageTable',
		    method: 'post',
		    url: sysMainMation.knowlgBasePath + 'knowledgecontent010',
		    where: getTableParams(),
		    even: true,
		    page: true,
			limits: getLimits(),
			limit: getLimit(),
		    cols: [getTableCols()],
		    done: function(json) {
		    	matchingLanguage();
				initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "请输入标题", function () {
					table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
				});
		    }
		});
	}

	function getTableCols() {
		var cols = [
			{ title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
			{ field: 'name', title: '标题', width: 250, templet: function (d) {
				return '<a lay-event="detail" class="notice-title-click">' + d.name + '</a>';
			}},
			{ field: 'typeName', title: '所属分类', width: 160 },
			{ field: 'createName', title: '提交人', width: 120 },
			{ field: 'lastUpdateTime', title: '最后提交时间', align: 'center', width: 150 }
		];
		if (clickId == "checkedList") {
			// 已审核
			cols.push({ field: 'examineId', title: '审核人', width: 120, templet: function (d) {
				return isNull(d.examineMation) ? "" : d.examineMation.name;
			}});
			cols.push({ field: 'examineTime', title: '审核时间', align: 'center', width: 150 });
			cols.push({ field: 'state', title: '审核结果', align: 'center', width: 100, templet: function (d) {
				return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("knowlgContentState", 'id', d.state, 'name');
			}});
		} else {
			// 未审核
			cols.push({ title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 100, toolbar: '#tableBar' });
		}
		return cols;
	}
	
	table.on('tool(messageTable)', function (obj) {
		var data = obj.data;
		var layEvent = obj.event;
		if (layEvent === 'check') { //审核
			check(data);
		} else if (layEvent === 'detail') { //详情
			detail(data);
		}
	});

	// 审核
	function check(data) {
		_openNewWindows({
			url:  systemCommonUtil.getUrl('FP2023101500005&id=' + data.id, null),
			title: "审核",
			pageId: "knowlgExamine",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	}

	// 详情
	function detail(data) {
		_openNewWindows({
			url:  systemCommonUtil.getUrl('FP2023101500007&id=' + data.id, null),
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "knowlgContentExamineDetails",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
			}});
	}
	
	form.render();
	$("body").on("click", "#reloadTable", function() {
		loadTable();
	});
	function loadTable() {
		table.reloadData("messageTable", {where: getTableParams()});
	}

	function getTableParams() {
		var params = {};
		if (clickId == "checkedList") {
			// 已审核
			params.stateList = JSON.stringify(['2', '3']);
		} else {
			// 未审核
			params.state = '1';
		}
		return $.extend(true, params, initTableSearchUtil.getSearchValue("messageTable"));
	}
	
    exports('knowlgExamineList', {});
});
