
var rowId = "";

//单据的时间
var operTime = "";

layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form', 'laydate'], function (exports) {
    winui.renderColor();
    var $ = layui.$,
        form = layui.form,
        laydate = layui.laydate,
        table = layui.table;
    
    //初始化统计时间
	operTime = getOneYMFormatDate();
	
	//获取本月日期
	function getOneYMFormatDate(){
		 var date = new Date;
		 var year = date.getFullYear(); 
		 var month = date.getMonth() + 1;
		 month = (month < 10 ? "0" + month : month); 
		 return year.toString() + "-" + month.toString();
	}
	
    laydate.render({
		elem: '#operTime', //指定元素
		type: 'month',
		value: operTime
	});
	
	//初始化数据
	initTable();
	function initTable(){
	    table.render({
	        id: 'messageTable',
	        elem: '#messageTable',
	        method: 'post',
	        url: flowableBasePath + 'statistics006',
	        where: {operTime: operTime, organName: $("#organName").val()},
	        even: true,
	        page: true,
	        limits: [8, 16, 24, 32, 40, 48, 56],
	        limit: 8,
	        cols: [[
	            { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
	            { field: 'defaultNumber', title: '单据编号', align: 'left', width: 250, templet: function(d){
			        var str = '<a lay-event="details" class="notice-title-click">' + d.defaultNumber + '</a>';
			        if(!isNull(d.linkNumber)){
			        	str += '<span class="state-new">[转]</span>';
				        if(d.status == 2){
				        	str += '<span class="state-up"> [正常]</span>';
				        }else{
				        	str += '<span class="state-down"> [预警]</span>';
				        }
			        }
			        return str;
			    }},
			    { field: 'subTypeName', title: '单据类型', align: 'left', width: 100},
	            { field: 'supplierName', title: '供应商名称', align: 'left', width: 120},
	            { field: 'totalPrice', title: '合计金额', align: 'left', width: 100},
	            { field: 'changeAmount', title: '实际支付', align: 'left', width: 120},
	            { field: 'operTime', title: '退货数量', align: 'center', width: 150}
	        ]],
		    done: function(){
		    	matchingLanguage();
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
	
	// 详情
	function details(data){
		rowId = data.id;
		var url = erpOrderUtil.getErpDetailUrl({subType: data.subType});
		_openNewWindows({
			url: url, 
			title: "单据详情",
			pageId: "otherwarehousdetails",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
			}});
	}
	
    
    form.on('submit(formSearch)', function (data) {
        
        if (winui.verifyForm(data.elem)) {
            loadTable();
        }
        return false;
    });

    $("body").on("click", "#reloadTable", function() {
        loadTable();
    });

    $("body").on("click", "#formSearch", function () {
        refreshTable();
    })
    
    //刷新
    function loadTable(){
    	if(isNull($("#operTime").val())){//一定要记得，当createTime为空时
    		winui.window.msg("请选择日期.", {icon: 2, time: 2000});
    	}else {
    		operTime = $("#operTime").val();
	        table.reload("messageTable", {where:{operTime: operTime, organName: $("#organName").val()}});
    	}
    }

    //搜索
    function refreshTable(){
    	if(isNull($("#operTime").val())){//一定要记得，当createTime为空时
    		winui.window.msg("请选择日期.", {icon: 2, time: 2000});
    	}else {
    		operTime = $("#operTime").val();
	        table.reload("messageTable", {page: {curr: 1}, where:{operTime: operTime, organName: $("#organName").val()}})
    	}
    }

    exports('customerreconciliation', {});
});
