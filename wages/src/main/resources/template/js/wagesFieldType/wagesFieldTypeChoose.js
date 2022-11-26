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
		
	// 设置提示信息
	var s = "薪资字段选择规则：当前只筛选已启用薪资字段数据，双击即可选中";
	$("#showInfo").html(s);

	table.render({
		id: 'messageTable',
		elem: '#messageTable',
		method: 'post',
		url: sysMainMation.wagesBasePath + 'wages008',
		where: getTableParams(),
		even: true,
		page: true,
		limits: [8, 16, 24, 32, 40, 48, 56],
		limit: 8,
		cols: [[
			{ type: 'radio'},
			{ title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
			{ field: 'name', title: '名称', align: 'left', width: 150 },
			{ field: 'key', title: '字段key', align: 'left', width: 120 },
			{ field: 'monthlyClearing', title: '月度清零', align: 'center', width: 80, templet: function (d) {
				if (d.monthlyClearing == '1') {
					return "是";
				} else if (d.monthlyClearing == '2') {
					return "否";
				} else {
					return "-";
				}
			}},
			{ field: 'wagesType', title: '字段类型', align: 'center', width: 80, templet: function (d) {
				if (d.wagesType == '1') {
					return "<span class='state-up'>薪资增加</span>";
				} else if (d.wagesType == '2') {
					return "<span class='state-down'>薪资减少</span>";
				} else {
					return "-";
				}
			}}
		]],
		done: function(res, curr, count) {
			matchingLanguage();
			$('#messageTable').next().find('.layui-table-body').find("table" ).find("tbody").children("tr").on('dblclick',function(){
				var dubClick = $('#messageTable').next().find('.layui-table-body').find("table").find("tbody").find(".layui-table-hover");
				dubClick.find("input[type='radio']").prop("checked", true);
				form.render();
				var chooseIndex = JSON.stringify(dubClick.data('index'));
				var obj = res.rows[chooseIndex];
				parent.fieldMation = obj;

				parent.refreshCode = '0';
				parent.layer.close(index);
			});

			$('#messageTable').next().find('.layui-table-body').find("table" ).find("tbody").children("tr").on('click',function(){
				var click = $('#messageTable').next().find('.layui-table-body').find("table").find("tbody").find(".layui-table-hover");
				click.find("input[type='radio']").prop("checked", true);
				form.render();
			});
			initTableSearchUtil.initAdvancedSearch(this, res.searchFilter, form, "请输入名称，字段key", function () {
				table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
			});
		}
	});
	form.render();
	form.on('select(fieldType)', function(data) {
		var fieldType = data.value;
		if (fieldType == 1) {
			table.reloadData("messageTable", {
				url: sysMainMation.wagesBasePath + 'wages008',
				page: {curr: 1},
				where: getTableParams()
			});
		} else if (fieldType == 2) {
			table.reloadData("messageTable", {
				url: sysMainMation.wagesBasePath + 'wages009',
				page: false
			});
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
	
    exports('wagesFieldTypeChoose', {});
});