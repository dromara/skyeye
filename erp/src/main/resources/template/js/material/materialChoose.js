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

	var checkType = '1';//商品选择类型：1.单选；2.多选
	
	if(!isNull(parent.erpOrderUtil.productCheckType)){
		checkType = parent.erpOrderUtil.productCheckType;
	}
	
	//设置提示信息
	var s = "商品选择规则：";
	if(checkType == "1"){
		s += '1.单选，双击指定行数据即可选中；';
	} else {
		s += '1.多选；';
		//显示保存按钮
		$("#saveCheckBox").show();
	}
	s += '如没有查到要选择的商品，请检查商品信息是否满足当前规则。';
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
		if(checkType == '2'){
			//初始化值
			var ids = [];
			$.each(parent.erpOrderUtil.chooseProductMation, function(i, item){
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
		}
			
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
		    	{ type: checkType == '1' ? 'radio' : 'checkbox'},
		        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
		        { field: 'productName', title: '商品名称', align: 'left', width: 150, templet: function (d) {
			        	return '<a lay-event="details" class="notice-title-click">' + d.productName + '</a>';
			    }},
		        { field: 'productModel', title: '型号', align: 'left', width: 150 },
		        { field: 'categoryName', title: '所属类型', align: 'left', width: 100 },
		        { field: 'typeName', title: '商品来源', align: 'left', width: 100 },
		        { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 }
		    ]],
		    done: function(res, curr, count){
		    	matchingLanguage();
		    	if(checkType == '1'){
			    	$('#messageTable').next().find('.layui-table-body').find("table" ).find("tbody").children("tr").on('dblclick',function(){
						var dubClick = $('#messageTable').next().find('.layui-table-body').find("table").find("tbody").find(".layui-table-hover");
						dubClick.find("input[type='radio']").prop("checked", true);
						form.render();
						var chooseIndex = JSON.stringify(dubClick.data('index'));
						var obj = res.rows[chooseIndex];
						parent.erpOrderUtil.chooseProductMation = obj;
						
						parent.refreshCode = '0';
						parent.layer.close(index);
					});
					
					$('#messageTable').next().find('.layui-table-body').find("table" ).find("tbody").children("tr").on('click',function(){
						var click = $('#messageTable').next().find('.layui-table-body').find("table").find("tbody").find(".layui-table-hover");
						click.find("input[type='radio']").prop("checked", true);
						form.render();
					})
		    	} else {
		    		// 多选,设置选中
		    		tableCheckBoxUtil.checkedDefault({
						gridId: 'messageTable',
						fieldName: 'productId'
					});
		    	}
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
	function details(data){
		rowId = data.productId;
		_openNewWindows({
			url: "../../tpl/material/materialdetails.html", 
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "materialdetails",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
			}});
	}
	
	//保存按钮-多选才有
	$("body").on("click", "#saveCheckBox", function() {
		var selectedData = tableCheckBoxUtil.getValue({
			gridId: 'messageTable'
		});
		AjaxPostUtil.request({url: flowableBasePath + "material013", params: {ids: selectedData.toString()}, type: 'json', callback: function (json) {
   			if (json.returnCode == 0) {
   				parent.erpOrderUtil.chooseProductMation = [].concat(json.rows);
 	   			parent.layer.close(index);
 	        	parent.refreshCode = '0';
   			} else {
   				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
   			}
   		}});
	});
	
	
	$("body").on("click", "#formSearch", function() {
		refreshTable();
	});
	
	 $("body").on("click", "#reloadTable", function() {
    	loadTable();
    });
    
    function loadTable(){
    	table.reload("messageTable", {where: getTableParams()});
    }
    
    function refreshTable(){
    	table.reload("messageTable", {page: {curr: 1}, where: getTableParams()});
    }

	function getTableParams(){
		return {
			materialName: $("#materialName").val(), 
    		model: $("#model").val(), 
    		categoryId: categoryId, 
    		typeNum: $("#typeNum").val()
		};
	}
	
    exports('materialChoose', {});
});