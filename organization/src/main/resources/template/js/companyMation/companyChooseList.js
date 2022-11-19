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
	
	// 选择的公司信息
	var companyList = parent.companyList;
	
	// 设置提示信息
	var s = "企业选择规则：1.多选；如没有查到要选择的企业，请检查企业信息是否满足当前规则。";
	$("#showInfo").html(s);

	initTable();
	function initTable(){
		var ids = [];
		$.each(companyList, function(i, item) {
			ids.push(item.id);
		});
		tableCheckBoxUtil.init({
			gridId: 'messageTable',
			filterId: 'messageTable',
			fieldName: 'id',
			ids: ids
		});

		table.render({
		    id: 'messageTable',
		    elem: '#messageTable',
		    method: 'post',
		    url: reqBasePath + 'companymation010',
		    where: getTableParams(),
			even: true,
		    page: true,
		    limits: [8, 16, 24, 32, 40, 48, 56],
		    limit: 8,
		    cols: [[
		    	{ type: 'checkbox'},
		        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
				{ field: 'name', width: 300, title: '公司名称'},
				{ field: 'departmentNum', title: '部门数', width: 100 },
				{ field: 'userNum', title: '员工数', width: 100 },
		    ]],
		    done: function(res, curr, count){
		    	matchingLanguage();
	    		tableCheckBoxUtil.checkedDefault({
					gridId: 'messageTable',
					fieldName: 'id'
				});

				initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "请输入名称", function () {
					table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
				});
		    }
		});
		
		form.render();
	}

	// 保存
	$("body").on("click", "#saveCheckBox", function() {
		var selectedData = tableCheckBoxUtil.getValueList({
			gridId: 'messageTable'
		});
		if (selectedData.length == 0) {
			winui.window.msg("请选择企业", {icon: 2, time: 2000});
			return false;
		}
		parent.companyList = [].concat(selectedData);
		parent.layer.close(index);
		parent.refreshCode = '0';
	});

	$("body").on("click", "#reloadTable", function() {
		loadTable();
	});

	function loadTable() {
		table.reloadData("messageTable", {where: getTableParams()});
	}

	function getTableParams() {
		return $.extend(true, {}, initTableSearchUtil.getSearchValue("messageTable"));
	}
	
    exports('companyChooseList', {});
});