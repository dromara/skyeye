
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

	// 加载列表数据权限
	loadAuthBtnGroup('messageTable', '1574672406390');

	table.render({
		id: 'messageTable',
		elem: '#messageTable',
		method: 'post',
		url: sysMainMation.projectBasePath + 'queryProProjectList',
		where: getTableParams(),
		even: true,
		page: true,
		limits: getLimits(),
		limit: getLimit(),
		cols: [[
			{ type: 'radio', fixed: 'left'},
			{ title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers', rowspan: 2},
			{ field: 'oddNumber', title: '单据编号', width: 200, align: 'center', templet: function (d) {
				return '<a lay-event="details" class="notice-title-click">' + d.oddNumber + '</a>';
			}},
			{ field: 'numberCode', title: '项目单号', width: 200 },
			{ field: 'name', title: '项目名称', width: 300 },
			{ field: 'state', title: '状态', width: 90, align: 'center', templet: function (d) {
				return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("projectStateEnum", 'id', d.state, 'name');
			}},
			{ field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], width: 120 },
			{ field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 },
			{ field: 'lastUpdateName', title: systemLanguage["com.skyeye.lastUpdateName"][languageType], align: 'left', width: 120 },
			{ field: 'lastUpdateTime', title: systemLanguage["com.skyeye.lastUpdateTime"][languageType], align: 'center', width: 150 }
		]],
		done: function(json) {
			matchingLanguage();
			initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "请输入单据编号", function () {
				table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
			});

			for (var i = 0; i < json.rows.length; i++) {
				// 只有执行中的项目才可以进行选中
				if (json.rows[i].state != 'executing') {
					systemCommonUtil.disabledRow(json.rows[i].LAY_TABLE_INDEX, 'radio');
				}
			}
			$('#messageTable').next().find('.layui-table-body').find("table" ).find("tbody").children("tr").on('dblclick',function(){
				var dubClick = $('#messageTable').next().find('.layui-table-body').find("table").find("tbody").find(".layui-table-hover");
				if(!dubClick.find("input[type='radio']").prop("disabled")) {
					dubClick.find("input[type='radio']").prop("checked", true);
					form.render();
					var chooseIndex = JSON.stringify(dubClick.data('index'));
					var obj = json.rows[chooseIndex];
					parent.projectMation = obj;

					parent.refreshCode = '0';
					parent.layer.close(index);
				}
			});

			$('#messageTable').next().find('.layui-table-body').find("table" ).find("tbody").children("tr").on('click',function(){
				var click = $('#messageTable').next().find('.layui-table-body').find("table").find("tbody").find(".layui-table-hover");
				if(!click.find("input[type='radio']").prop("disabled")) {
					click.find("input[type='radio']").prop("checked", true);
					form.render();
				}
			})
		}
	});
		
	table.on('tool(messageTable)', function (obj) {
		var data = obj.data;
		var layEvent = obj.event;
		if (layEvent === 'details'){ // 详情
			details(data);
		}
	});

	// 详情
	function details(data) {
		_openNewWindows({
			url: systemCommonUtil.getUrl('FP2023080300003&id=' + data.id, null),
			title: "项目详情",
			pageId: "projectDetails",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
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
		return $.extend(true, {}, initTableSearchUtil.getSearchValue("messageTable"));
	}

    exports('projectChooseList', {});
});