
var rowId = "";

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
	
	var selOption = getFileContent('tpl/template/select-option.tpl');

	erpOrderUtil.getDepotList(function (json){
		// 加载仓库数据
		$("#depotId").html(getDataUseHandlebars(selOption, json));
		// 初始化表格
		initTable();
	});

	function initTable(){
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
		        { title: systemLanguage["com.skyeye.serialNumber"][languageType], rowspan: '2', type: 'numbers'},
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
		        { field: 'allTock', title: '当前库存', align: 'center', width: 120}
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
	        }
	    });
	    
	    form.render();
	}
    
    var loadFirstType = false;
	//初始化商品类型
	function initFirstType(){
		loadFirstType = true;
		var el5;
		el5 = eleTree.render({
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
	
	
	form.on('submit(formSearch)', function (data) {
        
        if (winui.verifyForm(data.elem)) {
        	refreshloadTable();
        }
        return false;
    });
    
    //详情
	function details(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/material/materialdetails.html", 
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "materialdetails",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
			}});
	}
	
	//刷新数据
    $("body").on("click", "#reloadTable", function() {
    	loadTable();
    });
    
    function loadTable(){
    	table.reloadData("messageTable", {where: getTableParams()});
    }
    
    function refreshloadTable(){
    	table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
    }
    
    function getTableParams(){
    	return {
    		materialName: $("#materialName").val(), 
    		model: $("#model").val(), 
    		categoryId: isNull($("#categoryId").val()) ? "" : $("#categoryId").attr("categoryId"), 
    		typeNum: $("#typeNum").val()
    	};
    }
    
    exports('materialInventoryWarningList', {});
});
