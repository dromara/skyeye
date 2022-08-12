
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
        soulTable = layui.soulTable,
        table = layui.table;
        
    table.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        url: flowableBasePath + 'erpmachin009',
        where: getTablePatams(),
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
            { title: systemLanguage["com.skyeye.serialNumber"][languageType], rowspan: '2', type: 'numbers'},
            { field: 'orderNum', rowspan: '2', title: '单据编号', align: 'center', width: 180, templet: function (d) {
		        return '<a lay-event="details" class="notice-title-click">' + d.orderNum + '</a>';
		    }},
            { field: 'productionNumber', rowspan: '2', title: '生产计划单', align: 'center', width: 200},
            { colspan: '3', title: '加工成品信息', align: 'center'},
            { field: 'state', rowspan: '2', title: '状态', align: 'left', width: 80, templet: function (d) {
		        if(d.state == '1'){
	        		return "<span class='state-down'>未审核</span>";
	        	}else if(d.state == '2'){
	        		return "<span class='state-up'>审核中</span>";
	        	}else if(d.state == '3'){
	        		return "<span class='state-new'>审核通过</span>";
	        	}else if(d.state == '4'){
	        		return "<span class='state-down'>拒绝通过</span>";
	        	}else if(d.state == '5'){
	        		return "<span class='state-up'>已完成</span>";
	        	} else {
	        		return "参数错误";
	        	}
		    }},
		    { field: 'pickState', rowspan: '2', title: '领料状态', align: 'left', width: 80, templet: function (d) {
		        if(d.pickState == '1'){
	        		return "<span class='state-down'>未领料</span>";
	        	}else if(d.pickState == '2'){
	        		return "<span class='state-up'>已领料</span>";
	        	} else {
	        		return "参数错误";
	        	}
		    }},
            { colspan: '3', title: '加工信息', align: 'center'},
            { field: 'createName', rowspan: '2', title: '录入人', align: 'left', width: 100},
            { field: 'createTime', rowspan: '2', title: '录入日期', align: 'center', width: 150 },
            { title: systemLanguage["com.skyeye.operation"][languageType], rowspan: '2', fixed: 'right', align: 'center', width: 200, toolbar: '#tableBar'}
        ],[
	        { field: 'materialName', title: '名称', align: 'left', width: 120},
	    	{ field: 'unitName', title: '规格', align: 'center', width: 80},
	        { field: 'needNum', title: '加工数量', align: 'center', width: 80},
	        { field: 'departmentName', title: '部门', align: 'left', width: 100},
	    	{ field: 'startTime', title: '开始时间', align: 'center', width: 150},
	        { field: 'endTime', title: '结束时间', align: 'center', width: 150}
        ]],
        done: function(){
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
            loadTable();
        }
        return false;
    });
    
    //详情
	function details(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/erpMachin/erpMachinDetails.html", 
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "erpMachinDetails",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
			}});
	}
	
    $("body").on("click", "#reloadTable", function() {
        loadTable();
    });

    //刷新
    function loadTable(){
        table.reloadData("messageTable", {where: getTablePatams()});
    }

    //搜索
    function refreshTable(){
        table.reloadData("messageTable", {page: {curr: 1}, where: getTablePatams()})
    }
    
    function getTablePatams(){
    	return {
    		defaultNumber: $("#defaultNumber").val(), 
    		materialName: $("#materialName").val()
    	};
    }

    exports('erpDepartMachinList', {});
});
