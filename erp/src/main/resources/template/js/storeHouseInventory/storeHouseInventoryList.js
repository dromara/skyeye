
var rowId = "";

var normsId = "";

var depotId = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form', 'soulTable', 'fsTree'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		form = layui.form,
		table = layui.table,
		fsTree = layui.fsTree,
		soulTable = layui.soulTable;
	
	/********* tree 处理   start *************/
	var ztree;
	fsTree.render({
		id: "treeDemo",
		url: sysMainMation.erpBasePath + "storehouse009",
		checkEnable: false,
		showLine: false,
		showIcon: true,
		addDiyDom: ztreeUtil.addDiyDom,
		clickCallback: onClickTree,
		onDblClick: onClickTree
	}, function(id) {
		ztree = $.fn.zTree.getZTreeObj(id);
		var zTreeChecked = ztree.getCheckedNodes(false);
		if (zTreeChecked.length == 0) {
			return false;
		}
		ztree.selectNode(zTreeChecked[0], true, true);
		fuzzySearch(id, '#name', null, true);
		depotId = zTreeChecked[0].id;
		initTable();
		ztreeUtil.initEventListener(id);
	});

	//异步加载的方法
	function onClickTree(event, treeId, treeNode) {
		if(treeNode == undefined) {
			depotId = "";
		} else {
			depotId = treeNode.id;
		}
		loadTable();
	}

	function initTable(){
		table.render({
		    id: 'messageTable',
	        elem: '#messageTable',
	        method: 'post',
	        url: sysMainMation.erpBasePath + 'erpstockinventory002',
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
		        { field: 'name', title: '产品名称', rowspan: '2', align: 'left', width: 150 },
		        { field: 'model', title: '型号', rowspan: '2', align: 'left', width: 150 },
				{ field: 'type', title: '产品类型', rowspan: '2', align: 'left', width: 100, templet: function (d) {
					return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("materialType", 'id', d.type, 'name');
				}},
				{ field: 'fromType', title: '产品来源', rowspan: '2', align: 'center', width: 100, templet: function (d) {
					return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("materialFromType", 'id', d.fromType, 'name');
				}},
				{ title: '库存', colspan: '4', align: 'center', width: 80},
		        { field: 'id', title: '盘点历史', rowspan: '2', align: 'center', width: 80, templet: function (d) {
					return '<a lay-event="historyDetails" class="notice-title-click">盘点历史</a>';
			    }},
				{ field: 'enabled', title: '规格状态', rowspan: '2', align: 'center', width: 80, templet: function (d) {
					return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("commonEnable", 'id', d.materialNorms.enabled, 'name');
				}}
		    ], [
				{ field: 'materialNorms', title: '规格', align: 'left', width: 140, templet: function (d) {
					return d.materialNorms.name;
				}},
		        { field: 'allStock', title: '总库存', align: 'center', width: 80, templet: function (d) {
					if (isNull(d.materialNorms.depotTock)) {
						return '0';
					}
					return d.materialNorms.depotTock.allStock;
				}},
		    	{ field: 'initialTock', title: '初始库存', align: 'center', width: 80, templet: function (d) {
					if (isNull(d.materialNorms.depotTock)) {
						return '0';
					}
					return d.materialNorms.depotTock.initialTock;
				}},
		        { field: 'stockNum', title: '可盘点库存', align: 'center', width: 120, edit: 'text', templet: function (d) {
					if (isNull(d.materialNorms.depotTock)) {
						return '0';
					}
					return d.materialNorms.depotTock.inventoryTock;
				}}
	        ]],
		    done: function(json) {
		    	matchingLanguage();
		    	soulTable.render(this);
				initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "请输入名称", function () {
					table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
				});
		    }
		});
		
		table.on('tool(messageTable)', function (obj) {
	        var data = obj.data;
	        var layEvent = obj.event;
	        if (layEvent === 'historyDetails') { // 盘点历史
	        	historyDetails(data);
	        }
	    });
	    
	    table.on('edit(messageTable)', function(obj){
	    	var data = obj.data;
	    	var value = obj.value;
	    	if (isNull(value)) {
	    		winui.window.msg("请填写盘点数量.", {icon: 2, time: 2000});
	    	} else {
	    		if ((/^(\+|-)?\d+$/.test(value)) && value >= 0) {
	    			var params = {
	    				materialId: data.id,
	    				normsId: data.normsId,
	    				number: value,
	    				depotId: depotId
	    			};
	    			AjaxPostUtil.request({url: sysMainMation.erpBasePath + "erpstockinventory001", params: params, type: 'json', method: 'POST', callback: function(json) {
						winui.window.msg("盘点成功", {icon: 1, time: 2000});
						loadTable();
					}});
	    		} else {
	    			winui.window.msg("请填写盘点数量.", {icon: 2, time: 2000});
	    		}
	    	}
	    });
	}
    
	// 盘点历史详情
	function historyDetails(data) {
		normsId = data.normsId;
		_openNewWindows({
			url: "../../tpl/storeHouseInventory/storeHouseInventoryHistory.html", 
			title: "盘点历史",
			pageId: "storeHouseInventoryHistory",
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
		return $.extend(true, {depotId: depotId}, initTableSearchUtil.getSearchValue("messageTable"));
	}
    
    exports('storeHouseInventoryList', {});
});
