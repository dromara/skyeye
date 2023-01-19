
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form', 'tableCheckBoxUtil'], function (exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
	var $ = layui.$,
		form = layui.form,
		table = layui.table,
		tableCheckBoxUtil = layui.tableCheckBoxUtil;

	if(parent.dsFormUtil.chooseType) {
		// 多选
		var ids = [];
		$.each(parent.dsFormUtil.dsFormChooseList, function (i, item) {
			ids.push(item.id);
		});
		tableCheckBoxUtil.setIds({
			gridId: 'messageTable',
			fieldName: 'farmId',
			ids: ids
		});

		tableCheckBoxUtil.init({
			gridId: 'messageTable',
			filterId: 'messageTable',
			fieldName: 'id'
		});
	} else {
		// 单选
		$("#saveCheckBox").hide();
	}

	var tableList = [];
	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: reqBasePath + 'dsformpage001',
	    where: getTableParams(),
		even: true,
	    page: true,
	    limits: getLimits(),
	    limit: getLimit(),
	    cols: [[
	    	{ type: parent.dsFormUtil.chooseType ? 'checkbox' : 'radio', fixed: 'left'},
			{ field: 'name', title: '名称', align: 'left', width: 120 },
			{ field: 'remark', title: '简介', align: 'left', width: 350 },
			{ field: 'numCode', title: '页面编号', align: 'center', width: 150 },
	    ]],
	    done: function(res, curr, count){
			matchingLanguage();
			$.each(res.rows, function (i, item) {
				if(isNull(getInPoingArr(tableList, "id", item.id, null))){
					tableList.push(item);
				}
			});

			if(parent.dsFormUtil.chooseType) {
				// 多选
				tableCheckBoxUtil.checkedDefault({
					gridId: 'messageTable',
					fieldName: 'id'
				});
			} else {
				// 单选
				$('#messageTable').next().find('.layui-table-body').find("table" ).find("tbody").children("tr").on('dblclick',function(){
					var dubClick = $('#messageTable').next().find('.layui-table-body').find("table").find("tbody").find(".layui-table-hover");
					dubClick.find("input[type='radio']").prop("checked", true);
					form.render();
					var chooseIndex = JSON.stringify(dubClick.data('index'));
					var obj = res.rows[chooseIndex];
					parent.dsFormUtil.dsFormChooseMation = obj;

					parent.refreshCode = '0';
					parent.layer.close(index);
				});

				$('#messageTable').next().find('.layui-table-body').find("table" ).find("tbody").children("tr").on('click',function(){
					var click = $('#messageTable').next().find('.layui-table-body').find("table").find("tbody").find(".layui-table-hover");
					click.find("input[type='radio']").prop("checked", true);
					form.render();
				});
			}

			initTableSearchUtil.initAdvancedSearch(this, res.searchFilter, form, "请输入名称", function () {
				table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
			});
	    }
	});
	
	// 保存
	$("body").on("click", "#saveCheckBox", function() {
		var selectedData = tableCheckBoxUtil.getValue({
			gridId: 'messageTable'
		});
		if(selectedData.length == 0){
			winui.window.msg("请选择表单", {icon: 2, time: 2000});
			return false;
		}
		var result = [];
		$.each(selectedData, function(i, item) {
			result.push(getInPoingArr(tableList, "id", item, ""));
		});

		parent.dsFormUtil.dsFormChooseList = [].concat(result);
		parent.layer.close(index);
		parent.refreshCode = '0';
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
	
    exports('dsFormPageListChoose', {});
});