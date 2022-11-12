
var rowId = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form', 'soulTable'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		form = layui.form,
		table = layui.table,
		soulTable = layui.soulTable;
	var selOption = getFileContent('tpl/template/select-option.tpl');

	// 加载仓库数据
	erpOrderUtil.getDepotList(function (json){
		$("#depotId").html(getDataUseHandlebars(selOption, json));
	});

	table.render({
		id: 'messageTable',
		elem: '#messageTable',
		method: 'post',
		url: flowableBasePath + 'material017',
		where: getTableParams(),
		even: true,
		page: true,
		limits: getLimits(),
		limit: getLimit(),
		overflow: {
			type: 'tips',
			hoverTime: 300, // 悬停时间，单位ms, 悬停 hoverTime 后才会显示，默认为 0
			minWidth: 150, // 最小宽度
			maxWidth: 500 // 最大宽度
		},
		cols: [[
			{ title: systemLanguage["com.skyeye.serialNumber"][languageType], rowspan: '2', type: 'numbers' },
			{ field: 'name', title: '商品名称', rowspan: '2', align: 'left', width: 150, templet: function (d) {
					return '<a lay-event="details" class="notice-title-click">' + d.name + '</a>';
			}},
			{ field: 'model', title: '型号', rowspan: '2', align: 'left', width: 150 },
			{ field: 'categoryName', title: '所属类型', rowspan: '2', align: 'center', width: 100 },
			{ field: 'typeName', title: '商品来源', rowspan: '2', align: 'left', width: 100 },
			{ field: 'unitName', title: '单位', rowspan: '2', align: 'center', width: 80},
			{ title: '库存', colspan: '3', align: 'center', width: 100},
			{ field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], rowspan: '2', align: 'center', width: 150 }
		],[
			{ field: 'safetyTock', title: '安全存量', align: 'center', width: 80},
			{ field: 'allStock', title: '当前库存', align: 'center', width: 120}
		]],
		done: function(json) {
			matchingLanguage();
			soulTable.render(this);
		}
	});

	table.on('tool(messageTable)', function (obj) {
		var data = obj.data;
		var layEvent = obj.event;
		if (layEvent === 'details') { //详情
			details(data);
		}
	});

	form.render();
	form.on('submit(formSearch)', function (data) {
        if (winui.verifyForm(data.elem)) {
        	refreshloadTable();
        }
        return false;
    });
    
    //详情
	function details(data) {
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/material/materialDetails.html",
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "materialDetails",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
			}});
	}
	
	//刷新数据
    $("body").on("click", "#reloadTable", function() {
    	loadTable();
    });
    
    function loadTable() {
    	table.reloadData("messageTable", {where: getTableParams()});
    }
    
    function refreshloadTable() {
    	table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
    }
    
    function getTableParams() {
    	return {
    		materialName: $("#materialName").val(), 
    		model: $("#model").val(), 
    		typeNum: $("#typeNum").val()
    	};
    }
    
    exports('materialInventoryWarningList', {});
});
