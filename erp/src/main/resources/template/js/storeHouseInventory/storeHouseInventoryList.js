
var rowId = "";

var normsId = "";

var depotId = "";

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
	
	var selOption = getFileContent('tpl/template/select-option-must.tpl');
	
	initDepotHtml();
	// 初始化仓库
	function initDepotHtml() {
		AjaxPostUtil.request({url: sysMainMation.erpBasePath + "storehouse009", params: {}, type: 'json', method: "GET", callback: function(json) {
			$("#depotId").html(getDataUseHandlebars(selOption, json));
			form.render();
			if(json.rows.length > 0){
				initTable();
			} else {
				winui.window.msg("您还未分配仓库，请联系管理员分配.", {icon: 2, time: 2000});
			}
		}});
	}
	
	function initTable(){
		table.render({
		    id: 'messageTable',
	        elem: '#messageTable',
	        method: 'post',
	        url: flowableBasePath + 'erpstockinventory002',
	        where: getTableParams(),
	        even: true,
	        page: true,
	        overflow: {
	            type: 'tips',
	            hoverTime: 300, // 悬停时间，单位ms, 悬停 hoverTime 后才会显示，默认为 0
	            minWidth: 150, // 最小宽度
	            maxWidth: 500 // 最大宽度
	        },
			limits: getLimits(),
			limit: getLimit(),
		    cols: [[
		        { title: systemLanguage["com.skyeye.serialNumber"][languageType], rowspan: '2', type: 'numbers' },
		        { field: 'materialName', title: '商品名称', rowspan: '2', align: 'left', width: 150, templet: function (d) {
					return '<a lay-event="details" class="notice-title-click">' + d.materialName + '</a>';
			    }},
		        { field: 'materialModel', title: '型号', rowspan: '2', align: 'left', width: 150 },
		        { field: 'materialCategoryName', title: '所属类型', rowspan: '2', align: 'center', width: 100 },
		        { field: 'materialTypeName', title: '商品来源', rowspan: '2', align: 'left', width: 100 },
		        { title: '库存', colspan: '4', align: 'center', width: 80},
		        { field: 'id', title: '盘点历史', rowspan: '2', align: 'center', width: 80, templet: function (d) {
			        	return '<a lay-event="historyDetails" class="notice-title-click">盘点历史</a>';
			    }},
		        { field: 'enabled', title: '状态', rowspan: '2', align: 'center', width: 60, templet: function (d) {
		        	if(d.enabled == '0'){
		        		return "<span class='state-down'>禁用</span>";
		        	} else if (d.enabled == '1'){
		        		return "<span class='state-up'>启用</span>";
		        	}
		        }}
		    ],[
				{ field: 'unitName', title: '规格', align: 'center', width: 80 },
		        { field: 'allStock', title: '总库存', align: 'center', width: 80 },
		    	{ field: 'initialTock', title: '初始库存', align: 'center', width: 80 },
		        { field: 'stockNum', title: '可盘点库存', align: 'center', width: 120, edit: 'text' }
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
	        } else if (layEvent === 'historyDetails'){ // 盘点历史
	        	historyDetails(data);
	        }
	    });
	    
	    table.on('edit(messageTable)', function(obj){
	    	var data = obj.data; //得到所在行所有键值
	    	var value = obj.value //得到修改后的值
	    	if(isNull(value)){
	    		winui.window.msg("请填写盘点数量.", {icon: 2, time: 2000});
	    	} else {
	    		if((/^(\+|-)?\d+$/.test(value)) && value >= 0){
	    			var params = {
	    				materialId: data.id,
	    				normsId: data.normsId,
	    				number: value,
	    				depotId: data.depotId
	    			};
	    			AjaxPostUtil.request({url: flowableBasePath + "erpstockinventory001", params: params, type: 'json', callback: function(json) {
						winui.window.msg("盘点成功", {icon: 1, time: 2000});
					}});
	    		} else {
	    			winui.window.msg("请填写盘点数量.", {icon: 2, time: 2000});
	    		}
	    	}
	    });
	}
    
	form.render();
	form.on('submit(formSearch)', function (data) {
        if (winui.verifyForm(data.elem)) {
        	refreshloadTable();
        }
        return false;
    });
    
    // 详情
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
	
	// 盘点历史详情
	function historyDetails(data) {
		normsId = data.normsId;
		depotId = data.depotId;
		_openNewWindows({
			url: "../../tpl/storeHouseInventory/storeHouseInventoryHistory.html", 
			title: "盘点历史",
			pageId: "storeHouseInventoryHistory",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
			}});
	}
	
	// 刷新数据
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
			typeFrom: $("#typeFrom").val(),
    		enabled: $("#enabled").val(),
    		depotId: $("#depotId").val()
    	};
    }
    
    exports('storeHouseInventoryList', {});
});
