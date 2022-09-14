var rowId = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form', 'tableCheckBoxUtil', 'fsCommon', 'fsTree'], function (exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
	var $ = layui.$,
		form = layui.form,
		table = layui.table,
		fsTree = layui.fsTree,
		fsCommon = layui.fsCommon,
		tableCheckBoxUtil = layui.tableCheckBoxUtil;
	
	//选择的多商品列表
	productMationList = parent.productMationList;
	
	//设置提示信息
	var s = "商品选择规则：1.多选；如没有查到要选择的商品，请检查商品信息是否满足当前规则。";
	$("#showInfo").html(s);
	
	/********* tree 处理   start *************/
	//初始商品分类类型
    var materialCategoryType;
    fsTree.render({
		id: "materialCategoryType",
		url: flowableBasePath + "materialcategory008",
		checkEnable: false,
		loadEnable: false,//异步加载
		showLine: false,
		showIcon: true,
		expandSpeed: 'fast',
		clickCallback: zTreeOnClick
	}, function(id){
		materialCategoryType = $.fn.zTree.getZTreeObj(id);
		//加载商品列表
		initTable();
	});
	
	//节点点击事件
	function zTreeOnClick(event, treeId, treeNode) {
		categoryId = treeNode.id == 0 ? '' : treeNode.id;
		refreshTable();
	}
	
	$("body").on("input", "#name", function() {
		searchZtree(materialCategoryType, $("#name").val());
	});
	//ztree查询
	var hiddenNodes = [];
	function searchZtree(ztreeObj, ztreeInput) {
		//显示上次搜索后隐藏的结点
		ztreeObj.showNodes(hiddenNodes);
		function filterFunc(node) {
			var keyword = ztreeInput;
			//如果当前结点，或者其父结点可以找到，或者当前结点的子结点可以找到，则该结点不隐藏
			if(searchParent(keyword, node) || searchChildren(keyword, node.children)) {
				return false;
			}
			return true;
		};
		//获取不符合条件的叶子结点
		hiddenNodes = ztreeObj.getNodesByFilter(filterFunc);
		//隐藏不符合条件的叶子结点
		ztreeObj.hideNodes(hiddenNodes);
	}
	/********* tree 处理   end *************/
	//树节点选中的商品类型id
	var categoryId = "";
	function initTable(){
		//初始化值
		var ids = [];
		$.each(productMationList, function(i, item) {
			ids.push(item.productId);
		});
		tableCheckBoxUtil.setIds({
			gridId: 'messageTable',
			fieldName: 'productId',
			ids: ids
		});
		tableCheckBoxUtil.init({
			gridId: 'messageTable',
			filterId: 'messageTable',
			fieldName: 'productId'
		});

		table.render({
		    id: 'messageTable',
		    elem: '#messageTable',
		    method: 'post',
		    url: flowableBasePath + 'material010',
		    where: getTableParams(),
			even: true,
		    page: true,
		    limits: [8, 16, 24, 32, 40, 48, 56],
		    limit: 8,
		    cols: [[
		    	{ type: 'checkbox'},
		        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
		        { field: 'productName', title: '商品名称', align: 'left', width: 150, templet: function (d) {
			        	return '<a lay-event="details" class="notice-title-click">' + d.productName + '</a>';
			    }},
		        { field: 'productModel', title: '型号', align: 'left', width: 150 },
		        { field: 'categoryName', title: '所属类型', align: 'left', width: 100 },
		        { field: 'typeName', title: '商品来源', align: 'left', width: 100 },
		        { field: 'procedureMationList', title: '工序', align: 'left', width: 100, templet: function (d) {
		        	var str = ""
		        	$.each(d.procedureMationList, function(i, item) {
		        		str += '<span class="layui-badge layui-bg-gray">' + item.number + '</span>' + item.procedureName + '<br>';
		        	});
		        	return str;
			    }},
		        { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 }
		    ]],
		    done: function(json, curr, count){
		    	matchingLanguage();
	    		// 设置选中
	    		tableCheckBoxUtil.checkedDefault({
					gridId: 'messageTable',
					fieldName: 'productId'
				});

				initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "请输入商品名称，型号", function () {
					table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
				});
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
	}
	
	//详情
	function details(data) {
		rowId = data.productId;
		_openNewWindows({
			url: "../../tpl/material/materialdetails.html", 
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "materialdetails",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
			}});
	}
	
	var $step = $("#step");
	$step.step({
		index: 0,
		time: 500,
		title: ["选择商品", "选择规格"]
	});
	
	//下一步
	$("body").on("click", "#nextTab", function() {
		var selectedData = tableCheckBoxUtil.getValue({
			gridId: 'messageTable'
		});
		if(selectedData.length == 0){
			winui.window.msg("请选择商品", {icon: 2, time: 2000});
			return false;
		}
		AjaxPostUtil.request({url: flowableBasePath + "material014", params: {ids: selectedData.toString()}, type: 'json', callback: function (json) {
			productMationList = json.rows;
			$step.nextStep();
			$("#firstTab").hide();
			$("#secondTab").show();
			$("#tBody").html(getDataUseHandlebars($("#tableBody").html(), {rows: productMationList}));
			//设置商品来源选中
			$.each(productMationList, function(i, item) {
				$("#type" + item.productId).val(item.typeId);
			});
			form.render();
   		}});
	});
	
	//保存
	$("body").on("click", "#saveChoose", function() {
		var rows = $("#tBody tr");
		if(rows.length == 0){
			winui.window.msg("请选择商品", {icon: 2, time: 2000});
			return false;
		}
		var proList = new Array();
		$.each(rows, function(i, item) {
			var _object = $(item);
			var productId = _object.attr("rowid");
			proList.push({
				productId: productId,
				productName: $("#name" + productId).html(),
				productModel: $("#model" + productId).html(),
				bomId: $("#bom" + productId).val(),
				normsId: $("#norms" + productId).val(),
				unitName: $("#norms" + productId).find("option:selected").text(),
				procedureMationList: getProcedureMationList(productId),
				pId: '0',
				needNum: '1',
				unitPrice: getNormsPrice(productId, $("#norms" + productId).val()),
				wastagePrice: '0.00',
				type: $("#type" + productId).val(),
				remark: '',
				open: 'true',
				isParent: 1
			});
		});
		var params = {
			proList: JSON.stringify(proList)
		};
		AjaxPostUtil.request({url: flowableBasePath + "material015", params: params, type: 'json', callback: function(json) {
			parent.productMationList = [].concat(json.rows);
			parent.layer.close(index);
			parent.refreshCode = '0';
		}});
	});
	
	/**
	 * 获取商品工序
	 */
	function getProcedureMationList(productId){
		var proIndex = -1;
		$.each(productMationList, function(i, item) {
			if(productId == item.productId){
				proIndex = i;
				return false;
			}
		});
		if(proIndex >= 0){
			return productMationList[proIndex].procedureMationList;
		} else {
			return new Array();
		}
	}
	
	/**
	 * 获取指定规格的采购价/成本价
	 */
	function getNormsPrice(productId, normsId){
		//项目索引
		var proIndex = -1;
		//规格索引
		var normsIndex = -1;
		$.each(productMationList, function(i, item) {
			if(productId == item.productId){
				proIndex = i;
				$.each(item.unitList, function(j, bean){
					normsIndex = j;
					return false;
				});
				return false;
			}
		});
		if(proIndex >= 0 && normsIndex >= 0){
			return productMationList[proIndex].unitList[normsIndex].estimatePurchasePrice;
		} else {
			return new Array();
		}
	}
	
	//上一步
	$("body").on("click", "#prevTab", function() {
		$step.prevStep();
		$("#firstTab").show();
		$("#secondTab").hide();
	});
	
	
    form.render();
	$("body").on("click", "#reloadTable", function() {
    	loadTable();
    });
    
    function loadTable() {
    	table.reloadData("messageTable", {where: getTableParams()});
    }
    
    function refreshTable(){
    	table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
    }

	function getTableParams() {
		return $.extend(true, {categoryId: categoryId}, initTableSearchUtil.getSearchValue("messageTable"));
	}
	
    exports('materialChooseToProduce', {});
});