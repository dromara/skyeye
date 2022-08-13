
var rowId = "";

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
    
    // 默认加载当月日期
    $("#operTime").val(getOneYMFormatDate());
    laydate.render({elem: '#operTime', type: 'month'});
	
	initTable();
	// 报表查询->客户对账
	function initTable(){
	    table.render({
	        id: 'messageTable',
	        elem: '#messageTable',
	        method: 'post',
	        url: flowableBasePath + 'statistics005',
	        where: getTableParams(),
	        even: true,
	        page: true,
	        limits: getLimits(),
	    	limit: getLimit(),
	        cols: [[
	            { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
	            { field: 'defaultNumber', title: '单据编号', align: 'left', width: 250, templet: function (d) {
			        var str = '<a lay-event="details" class="notice-title-click">' + d.defaultNumber + '</a>';
			        if(!isNull(d.linkNumber)){
			        	str += '<span class="state-new">[转]</span>';
				        if(d.status == 2){
				        	str += '<span class="state-up"> [正常]</span>';
				        } else {
				        	str += '<span class="state-down"> [预警]</span>';
				        }
			        }
			        return str;
			    }},
			    { field: 'subTypeName', title: '单据类型', align: 'left', width: 100},
	            { field: 'supplierName', title: '客户名称', align: 'left', width: 120},
	            { field: 'totalPrice', title: '合计金额', align: 'left', width: 100},
	            { field: 'changeAmount', title: '实际支付', align: 'left', width: 120},
	            { field: 'operTime', title: '单据日期', align: 'center', width: 150}
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
			callBack: function(refreshCode) {
			}});
	}
	
    form.render();
    form.on('submit(formSearch)', function (data) {
        if (winui.verifyForm(data.elem)) {
            table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()})
        }
        return false;
    });

    $("body").on("click", "#reloadTable", function() {
        loadTable();
    });

    function loadTable(){
        table.reloadData("messageTable", {where: getTableParams()});
    }
    
    function getTableParams(){
    	return {
    		operTime: $("#operTime").val(),
    		customerName: $("#customerName").val()
    	};
    }

    exports('customerreconciliation', {});
});
