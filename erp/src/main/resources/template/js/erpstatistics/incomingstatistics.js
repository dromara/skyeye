
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
        
	// 获取本月日期
	function getOneYMFormatDate(){
		 var date = new Date;
		 var year = date.getFullYear(); 
		 var month = date.getMonth() + 1;
		 month = (month < 10 ? "0" + month : month); 
		 return year.toString() + "-" + month.toString();
	}
	
	table.render({
		id: 'messageTable',
		elem: '#messageTable',
		method: 'post',
		url: flowableBasePath + 'statistics003',
		where: getTableParams(),
		even: true,
		page: true,
		limits: getLimits(),
		limit: getLimit(),
		cols: [[
			{ title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
			{ field: 'materialName', title: '产品名称', align: 'left', width: 250},
			{ field: 'materialModel', title: '型号', align: 'left', width: 150},
			{ field: 'unitName', title: '单位', align: 'left', width: 80},
			{ field: 'currentTock', title: '进货数量', align: 'left', width: 100},
			{ field: 'currentTockMoney', title: '进货金额', align: 'left', width: 120},
			{ field: 'returnCurrentTock', title: '退货数量', align: 'left', width: 100},
			{ field: 'returnCurrentTockMoney', title: '退货金额', align: 'left', width: 120 }
		]],
		done: function(json) {
			matchingLanguage();
			initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "请选择日期", function () {
				table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
			});
		}
	});

	form.render();
    $("body").on("click", "#reloadTable", function() {
        loadTable();
    });

    // 刷新
    function loadTable() {
		table.reloadData("messageTable", {where: getTableParams()});
    }

	function getTableParams() {
		return $.extend(true, {}, initTableSearchUtil.getSearchValue("messageTable"));
	}

    exports('incomingstatistics', {});
});
