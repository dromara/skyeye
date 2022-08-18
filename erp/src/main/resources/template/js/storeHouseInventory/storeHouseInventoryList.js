
var rowId = "";

var normsId = "";

var depotId = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form', 'eleTree', 'soulTable'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		form = layui.form,
		table = layui.table,
		soulTable = layui.soulTable,
		eleTree = layui.eleTree;
	
	var selOption = getFileContent('tpl/template/select-option-must.tpl');
	
	initDepotHtml();
	//初始化仓库
	function initDepotHtml() {
		AjaxPostUtil.request({url: flowableBasePath + "storehouse009", params: {}, type: 'json', method: "GET", callback: function(json) {
			//加载仓库数据
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
	        limits: [8, 16, 24, 32, 40, 48, 56],
	        overflow: {
	            type: 'tips',
	            hoverTime: 300, // 悬停时间，单位ms, 悬停 hoverTime 后才会显示，默认为 0
	            minWidth: 150, // 最小宽度
	            maxWidth: 500 // 最大宽度
	        },
	        limit: 8,
		    cols: [[
		        { title: systemLanguage["com.skyeye.serialNumber"][languageType], rowspan: '2', type: 'numbers' },
		        { field: 'name', title: '商品名称', rowspan: '2', align: 'left', width: 150, templet: function (d) {
			        	return '<a lay-event="details" class="notice-title-click">' + d.name + '</a>';
			    }},
		        { field: 'model', title: '型号', rowspan: '2', align: 'left', width: 150 },
		        { field: 'categoryName', title: '所属类型', rowspan: '2', align: 'center', width: 100 },
		        { field: 'typeName', title: '商品来源', rowspan: '2', align: 'left', width: 100 },
		        { title: '库存', colspan: '3', align: 'center', width: 80},
		        { field: 'id', title: '盘点历史', rowspan: '2', align: 'center', width: 80, templet: function (d) {
			        	return '<a lay-event="historyDetails" class="notice-title-click">盘点历史</a>';
			    }},
		        { field: 'unitName', title: '单位', rowspan: '2', align: 'center', width: 80},
		        { field: 'enabled', title: '状态', rowspan: '2', align: 'center', width: 60, templet: function (d) {
		        	if(d.enabled == '0'){
		        		return "<span class='state-down'>禁用</span>";
		        	}else if(d.enabled == '1'){
		        		return "<span class='state-up'>启用</span>";
		        	}
		        }},
		        { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], rowspan: '2', align: 'center', width: 150 }
		    ],[
		        { field: 'allTock', title: '总库存', align: 'center', width: 80},
		    	{ field: 'initialTock', title: '初始库存', align: 'center', width: 80},
		        { field: 'stockNum', title: '可盘点库存', align: 'center', width: 120, edit: 'text'}
	        ]],
		    done: function(){
		    	matchingLanguage();
		    	soulTable.render(this);
		    	if(!loadFirstType){
		    		initFirstType();
		    	}
		    }
		});
		
		table.on('tool(messageTable)', function (obj) {
	        var data = obj.data;
	        var layEvent = obj.event;
	        if (layEvent === 'details') { //详情
	        	details(data);
	        } else if (layEven === 'historyDetails'){ // 盘点历史
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
    
    var loadFirstType = false;
	//初始化商品类型
	function initFirstType(){
		loadFirstType = true;
		var el5 = eleTree.render({
            elem: '.ele5',
            url: flowableBasePath + "materialcategory009",
            defaultExpandAll: true,
            expandOnClickNode: false,
            highlightCurrent: true
        });
        $(".ele5").hide();
		$("#categoryId").on("click",function (e) {
		    e.stopPropagation();
		    $(".ele5").toggle();
		});
		eleTree.on("nodeClick(data5)",function(d) {
		    $("#categoryId").val(d.data.currentData.name);
		    $("#categoryId").attr("categoryId", d.data.currentData.id);
		    $(".ele5").hide();
		}) 
		$(document).on("click",function() {
		    $(".ele5").hide();
		})
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
			url: "../../tpl/material/materialdetails.html", 
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "materialdetails",
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
    		materialName: $("#materialName").val(), 
    		model: $("#model").val(), 
    		categoryId: isNull($("#categoryId").val()) ? "" : $("#categoryId").attr("categoryId"), 
    		typeNum: $("#typeNum").val(), 
    		enabled: $("#enabled").val(),
    		depotId: $("#depotId").val()
    	};
    }
    
    exports('storeHouseInventoryList', {});
});
