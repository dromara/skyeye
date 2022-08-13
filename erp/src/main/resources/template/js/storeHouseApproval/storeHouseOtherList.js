
var rowId = "";
var subType = "";

//单据的开始时间、结束时间
var startTime = "", endTime = "";

layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form', 'laydate', 'soulTable'], function (exports) {
    winui.renderColor();
    var $ = layui.$,
        form = layui.form,
        laydate = layui.laydate,
        soulTable = layui.soulTable,
        table = layui.table;
        
    var selOption = getFileContent('tpl/template/select-option-must.tpl');
        
    laydate.render({
		elem: '#operTime', //指定元素
		range: '~'
	});
	
	initDepotHtml();
	//初始化仓库
	function initDepotHtml() {
		AjaxPostUtil.request({url: flowableBasePath + "storehouse009", params: {}, type: 'json', callback: function(json) {
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
	        url: flowableBasePath + 'storehouseapproval005',
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
	            { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
	            { field: 'defaultNumber', title: '单据编号', align: 'left', width: 220, templet: function (d) {
			        var str = '<a lay-event="details" class="notice-title-click">' + d.defaultNumber + '</a>';
			        return str;
			    }},
			    { field: 'subTypeName', title: '单据类型', align: 'left', width: 100},
	            { field: 'materialNames', title: '关联产品', align: 'left', width: 300},
	            { field: 'status', title: '状态', align: 'left', width: 80, templet: function (d) {
			        if(d.status == '0'){
		        		return "<span class='state-down'>未审核</span>";
		        	}else if(d.status == '1'){
		        		return "<span class='state-up'>审核中</span>";
		        	}else if(d.status == '2'){
		        		return "<span class='state-new'>审核通过</span>";
		        	}else if(d.status == '3'){
		        		return "<span class='state-down'>拒绝通过</span>";
		        	}else if(d.status == '4'){
		        		return "<span class='state-new'>已转采购</span>";
		        	} else {
		        		return "参数错误";
		        	}
			    }},
	            { field: 'totalPrice', title: '合计金额', align: 'left', width: 120},
	            { field: 'taxMoney', title: '含税合计', align: 'left', width: 120 },
	            { field: 'discountLastMoney', title: '优惠后金额', align: 'left', width: 120 },
	            { field: 'changeAmount', title: '付款', align: 'left', width: 120 },
	            { field: 'operPersonName', title: '操作人', align: 'left', width: 100},
	            { field: 'operTime', title: '单据日期', align: 'center', width: 140 },
	            { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 200, toolbar: '#tableBar'}
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
	        }else if (layEvent === 'examine') { //审核
	        	examine(data);
	        }
	    });
    }

    
    form.on('submit(formSearch)', function (data) {
        
        if (winui.verifyForm(data.elem)) {
            loadTable();
        }
        return false;
    });
    
    //审核
	function examine(data){
		rowId = data.id;
		subType = data.subType;
		_openNewWindows({
			url: "../../tpl/storeHouseApproval/storeHouseOtherExamine.html", 
			title: "审核",
			pageId: "storeHouseOtherExamine",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
    }

    // 详情
	function details(data){
		rowId = data.id;
		var url = erpOrderUtil.getErpDetailUrl(data);
		_openNewWindows({
			url: url, 
			title: "单据详情",
			pageId: "storeHouseApprovalOrderDetail",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode) {
			}});
	}

    $("body").on("click", "#reloadTable", function() {
        loadTable();
    });

    $("body").on("click", "#formSearch", function () {
        refreshTable();
    });
    
    //刷新
    function loadTable(){
        table.reloadData("messageTable", {where: getTableParams()});
    }

    //搜索
    function refreshTable(){
        table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()})
    }
    
    function getTableParams(){
    	if(isNull($("#operTime").val())){//一定要记得，当createTime为空时
    		startTime = "";
    		endTime = "";
    	}else {
    		startTime = $("#operTime").val().split('~')[0].trim() + ' 00:00:00';
    		endTime = $("#operTime").val().split('~')[1].trim() + ' 23:59:59';
    	}
    	return {
    		defaultNumber: $("#defaultNumber").val(), 
    		startTime: startTime, 
    		endTime: endTime,
    		depotId: $("#depotId").val()
    	};
    }

    exports('storeHouseOtherList', {});
});
