
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
	// 组件使用
	var chooseMation = {};
	
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
			$('#messageTable').next().find('.layui-table-body').find("table" ).find("tbody").children("tr").on('click',function(){
				var dubClick = $('#messageTable').next().find('.layui-table-body').find("table").find("tbody").find(".layui-table-hover");
				dubClick.find("input[type='radio']").prop("checked", true);
				form.render();
				var chooseIndex = JSON.stringify(dubClick.data('index'));
				var obj = res.rows[chooseIndex];
				chooseMation = obj;
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
	
	var $step = $("#step");
	$step.step({
		index: 0,
		time: 500,
		title: ["选择计划单", "选择工序"]
	});

	// 下一步
	$("body").on("click", "#nextTab", function() {
		if (isNull(chooseMation.id)) {
			winui.window.msg("请选择生产计划单.", {icon: 2, time: 2000});
			return false;
		}
		AjaxPostUtil.request({url: sysMainMation.erpBasePath + "queryProductionById", params: {id: chooseMation.id}, type: 'json', method: 'GET', callback: function(json) {
			chooseMation = json.bean;
			var procedureList = [].concat(json.bean.productionProcedureList);
			if (!isNull(procedureList)) {
				// 加载工序信息
				$.each(procedureList, function(i, item) {
					if (!isNull(item.procedureMation)) {
						if (item.state == '1') {
							item.checkBoxHtml = '<input type="radio" name="procedureListName" value="' + item.procedureMation.id + '">';
							item.stateName = "<span class='state-down'>待下达</span>";
						} else if (item.state == '2') {
							item.checkBoxHtml = '<input type="radio" name="procedureListName" value="' + item.procedureMation.id + '" disabled="disabled">';
							item.stateName = "<span class='state-up'>已下达</span>"
						}
					}
				});
				$("#tBody").html(getDataUseHandlebars($("#tableBody").html(), {procedureList: procedureList}));
				form.render();
				$step.nextStep();
				$("#firstTab").hide();
				$("#secondTab").show();
			}
		}});
	});
	
	// 上一步
	$("body").on("click", "#prevTab", function() {
		$step.prevStep();
		$("#firstTab").show();
		$("#secondTab").hide();
	});
	
	// 保存
	$("body").on("click", "#saveChoose", function() {
		var chooseProcedure = $("input[name='procedureListName']:checked").val();
		if (isNull(chooseProcedure)) {
			winui.window.msg("请选择工序信息.", {icon: 2, time: 2000});
			return false;
		}

		chooseMation.chooseProcedure = getInPoingArr(chooseMation.productionProcedureList, "procedureId", chooseProcedure, "procedureMation");
		parent.productionMation = chooseMation;
		parent.refreshCode = '0';
		parent.layer.close(index);
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
	
    exports('erpProductionNoComplateProcedureList', {});
});