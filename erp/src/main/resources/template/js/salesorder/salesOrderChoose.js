var rowId = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form', 'fsTree'], function (exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
	var $ = layui.$,
		form = layui.form,
		table = layui.table,
		fsTree = layui.fsTree;
	/********* tree 处理   start *************/
	//初始销售单分类类型
    var materialCategoryType;
    fsTree.render({
		id: "materialCategoryType",
		url: flowableBasePath + "salesorder011?loginPCIp=",
		checkEnable: false,
		loadEnable: false,//异步加载
		showLine: false,
		showIcon: true,
		expandSpeed: 'fast',
		clickCallback: zTreeOnClick
	}, function(id){
		materialCategoryType = $.fn.zTree.getZTreeObj(id);
		//加载销售单列表
		initTable();
	});
	
	//节点点击事件
	function zTreeOnClick(event, treeId, treeNode) {
		salesOrderId = treeNode.id == 0 ? '' : treeNode.id;
		if(!isNull(salesOrderId)){
			refreshTable();
		}
	}
	
	$("body").on("input", "#name", function(){
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
	//树节点选中的销售单类型id
	var salesOrderId = "";
	function initTable(){
		table.render({
		    id: 'messageTable',
		    elem: '#messageTable',
		    method: 'post',
		    url: flowableBasePath + 'salesorder012',
		    where: getTableParams(),
			even: true,
		    page: false,
		    cols: [[
		    	{ type: 'radio'},
		        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
		        { field: 'materialName', title: '商品', align: 'left', width: 150},
		        { field: 'materialModel', title: '型号', align: 'left', width: 150},
		        { field: 'materialUnitName', title: '单位', align: 'left', width: 70 },
		        { field: 'operNum', title: '数量', align: 'left', width: 80 },
		        { field: 'unitPrice', title: '单价', align: 'left', width: 80 },
		        { field: 'allPrice', title: '金额', align: 'left', width: 80 },
		        { field: 'taxRate', title: '税率(%)', align: 'left', width: 80 },
		        { field: 'taxMoney', title: '税额', align: 'left', width: 80 },
		        { field: 'taxUnitPrice', title: '含税单价', align: 'left', width: 80 },
		        { field: 'taxLastMoney', title: '合计价税', align: 'left', width: 80 },
		        { field: 'remark', title: '备注', align: 'left', width: 100 }
		    ]],
		    done: function(res, curr, count){
		    	matchingLanguage();
		    	$('#messageTable').next().find('.layui-table-body').find("table" ).find("tbody").children("tr").on('dblclick',function(){
					var dubClick = $('#messageTable').next().find('.layui-table-body').find("table").find("tbody").find(".layui-table-hover");
					dubClick.find("input[type='radio']").prop("checked", true);
					form.render();
					var chooseIndex = JSON.stringify(dubClick.data('index'));
					var obj = res.rows[chooseIndex];
					parent.salesOrder = obj;
					
					parent.refreshCode = '0';
					parent.layer.close(index);
				});
				
				$('#messageTable').next().find('.layui-table-body').find("table" ).find("tbody").children("tr").on('click',function(){
					var click = $('#messageTable').next().find('.layui-table-body').find("table").find("tbody").find(".layui-table-hover");
					click.find("input[type='radio']").prop("checked", true);
					form.render();
				})
		    }
		});
		form.render();
	}
	
	$("body").on("click", "#reloadTable", function(){
    	loadTable();
    });
    
    function loadTable(){
    	table.reload("messageTable", {where: getTableParams()});
    }
    
    function refreshTable(){
    	table.reload("messageTable", {page: {curr: 1}, where: getTableParams()});
    }

	function getTableParams(){
		return {orderId: salesOrderId};
	}
	
    exports('salesOrderChoose', {});
});