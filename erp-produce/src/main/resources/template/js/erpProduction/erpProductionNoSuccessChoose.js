
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
		url: sysMainMation.erpBasePath + 'erpproduction009---已删除',
		where: getTableParams(),
		even: true,
		page: false,
		cols: [[
			{ type: 'radio'},
			{ title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
			{ field: 'oddNumber', title: '生产单号', align: 'center', width: 200, templet: function (d) {
				return '<a lay-event="details" class="notice-title-click">' + d.oddNumber + '</a>';
			}},
			{ field: 'salesOrderNum', width: 200, title: '关联销售单', align: 'center', templet: function (d) {return isNull(d.sealOrderMation) ? '' : d.sealOrderMation.oddNumber}},
			{ field: 'name', title: '产品名称', align: 'left',width: 150, templet: function (d) {return isNull(d.materialMation) ? '' : d.materialMation.name}},
			{ field: 'model', title: '产品型号', align: 'left',width: 150, templet: function (d) {return isNull(d.materialMation) ? '' : d.materialMation.model}},
			{ field: 'norms', title: '产品规格', align: 'left',width: 150, templet: function (d) {return isNull(d.normsMation) ? '' : d.normsMation.name}},
			{ field: 'number', width: 100, title: '计划生产数量'},
			{ field: 'planStartDate', width: 150, align: 'center', title: '计划开始时间'},
			{ field: 'planComplateDate', width: 150, align: 'center', title: '计划结束时间'}
		]],
		done: function(res, curr, count){
			matchingLanguage();
			initTableSearchUtil.initAdvancedSearch(this, res.searchFilter, form, "请输入生产单号", function () {
				table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
			});
			$('#messageTable').next().find('.layui-table-body').find("table" ).find("tbody").children("tr").on('dblclick',function(){
				var dubClick = $('#messageTable').next().find('.layui-table-body').find("table").find("tbody").find(".layui-table-hover");
				dubClick.find("input[type='radio']").prop("checked", true);
				form.render();
				var chooseIndex = JSON.stringify(dubClick.data('index'));
				var obj = res.rows[chooseIndex];
				parent.productionMation = obj;
				parent.refreshCode = '0';
				parent.layer.close(index);
			});

			$('#messageTable').next().find('.layui-table-body').find("table" ).find("tbody").children("tr").on('click',function(){
				var click = $('#messageTable').next().find('.layui-table-body').find("table").find("tbody").find(".layui-table-hover");
				click.find("input[type='radio']").prop("checked", true);
				form.render();
			})
		}
	});

	table.on('tool(messageTable)', function (obj) {
		var data = obj.data;
		var layEvent = obj.event;
		if (layEvent === 'details') { //详情
			details(data);
		}
	});

	// 详情
	function details(data) {
		_openNewWindows({
			url:  systemCommonUtil.getUrl('FP2023092200003&id=' + data.id, null),
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "erpProductionDetail",
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
		return $.extend(true, {}, initTableSearchUtil.getSearchValue("messageTable"));
	}
	
    exports('erpProductionNoSuccessChoose', {});
});