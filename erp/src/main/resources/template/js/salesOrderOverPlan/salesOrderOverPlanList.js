
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

    laydate.render({
		elem: '#operTime',
		range: '~'
	});
    
    table.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        url: flowableBasePath + 'erpordersaleoverplan001',
        where: getTableParams(),
        even: true,
        page: true,
        limits: getLimits(),
	    limit: getLimit(),
        cols: [[
            { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
            { field: 'defaultNumber', title: '单据编号', align: 'left', width: 200, templet: function(d){
		        return '<a lay-event="details" class="notice-title-click">' + d.defaultNumber + '</a>';
		    }},
            { field: 'totalPrice', title: '合计金额', align: 'left', width: 120},
            { field: 'operTime', title: '单据日期', align: 'center', width: 140 },
            { field: 'createTime', title: '单据录入日期', align: 'center', width: 140 },
            { field: 'status', title: '统筹状态', align: 'center', width: 80, templet: function(d){
		        if(d.state == '1'){
	        		return "<span class='state-down'>未统筹</span>";
	        	}else if(d.state == '2'){
	        		return "<span class='state-up'>已统筹</span>";
	        	}else{
	        		return "参数错误";
	        	}
		    }},

            { field: 'overPlanName', title: '统筹人', align: 'left', width: 130},
            { field: 'overPlanTime', title: '统筹日期', align: 'center', width: 140 },
            { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 200, toolbar: '#tableBar'}
        ]],
	    done: function(){
	    	matchingLanguage();
	    }
    });

    table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'overPlan') { //统筹
            overPlan(data);
        }else if (layEvent === 'details') { //详情
        	details(data);
        }
    });

    // 详情
	function details(data){
		rowId = data.orderId;
		_openNewWindows({
			url: "../../tpl/salesorder/salesorderdetails.html", 
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "salesorderdetails",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
			}});
	}

    // 统筹
    function overPlan(data){
        rowId = data.orderId;
        _openNewWindows({
            url: "../../tpl/salesOrderOverPlan/salesOrderOverPlan.html",
            title: '统筹',
            pageId: "salesOrderOverPlan",
            area: ['90vw', '90vh'],
            callBack: function(refreshCode){
                if (refreshCode == '0') {
                    winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1,time: 2000});
                    loadTable();
                } else if (refreshCode == '-9999') {
                    winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
                }
            }});
    }

    $("body").on("click", "#reloadTable", function() {
        loadTable();
    });

    // 搜索表单
    form.render();
    form.on('submit(formSearch)', function (data) {
        if (winui.verifyForm(data.elem)) {
            refreshTable();
        }
        return false;
    });

    // 刷新
    function loadTable(){
        table.reload("messageTable", {where: getTableParams()});
    }

    // 搜索
    function refreshTable(){
        table.reload("messageTable", {page: {curr: 1}, where: getTableParams()})
    }

    function getTableParams(){
        // 单据的开始时间、结束时间
        var startTime = "", endTime = "";
        if(isNull($("#operTime").val())){//一定要记得，当createTime为空时
            startTime = "";
            endTime = "";
        }else {
            startTime = $("#operTime").val().split('~')[0].trim() + ' 00:00:00';
            endTime = $("#operTime").val().split('~')[1].trim() + ' 23:59:59';
        }
        return {
            defaultNumber: $("#defaultNumber").val(),
            state: $("#state").val(),
            startTime: startTime,
            endTime: endTime
        };
    }
    
    exports('salesOrderOverPlanList', {});
});
