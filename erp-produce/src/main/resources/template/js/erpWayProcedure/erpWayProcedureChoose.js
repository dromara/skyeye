
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
	    url: sysMainMation.erpBasePath + 'erpwayprocedure001',
	    where: getTableParams(),
	    even: true,
	    page: true,
		limits: getLimits(),
		limit: getLimit(),
	    cols: [[
	    	{ type: 'radio'},
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
			{ field: 'number', title: '工艺编号', align: 'left', width: 100 },
			{ field: 'name', title: '工艺名称', align: 'left', width: 200 },
			{ field: 'enabled', title: '状态', align: 'center', width: 80, templet: function (d) {
				return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("commonEnable", 'id', d.enabled, 'name');
			}}
	    ]],
	    done: function(res, curr, count){
	    	matchingLanguage();
			initTableSearchUtil.initAdvancedSearch(this, res.searchFilter, form, "请输入名称、编号", function () {
				table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
			});

			for (var i = 0; i < res.rows.length; i++) {
				// 未启用的设置为不可选中
				if (res.rows[i].enabled != 1) {
					systemCommonUtil.disabledRow(res.rows[i].LAY_TABLE_INDEX, 'radio');
				}
			}
	    	$('#messageTable').next().find('.layui-table-body').find("table" ).find("tbody").children("tr").on('dblclick',function(){
				var dubClick = $('#messageTable').next().find('.layui-table-body').find("table").find("tbody").find(".layui-table-hover");
				if (!dubClick.find("input[type='radio']").prop("disabled")) {
					dubClick.find("input[type='radio']").prop("checked", true);
					form.render();
					var chooseIndex = JSON.stringify(dubClick.data('index'));
					var obj = res.rows[chooseIndex];

					parent.wayProcedureMation = obj;
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

    exports('erpWayProcedureChoose', {});
});