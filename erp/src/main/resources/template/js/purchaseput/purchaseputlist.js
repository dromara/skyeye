
var rowId = "";

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
    authBtn('1571813042536');//新增
    authBtn('1571988895735');//导出
        
    laydate.render({
		elem: '#operTime',
		range: '~'
	});
        
    table.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        url: flowableBasePath + 'purchaseput001',
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
            { field: 'defaultNumber', title: '单据编号', align: 'left', rowspan: '2', width: 220, templet: function(d){
		        var str = '<a lay-event="details" class="notice-title-click">' + d.defaultNumber + '</a>';
		        if(!isNull(d.linkNumber)){
		        	str += '<span class="state-new">[转]</span>';
		        }
		        return str;
		    }},
            { field: 'supplierName', title: '供应商', align: 'left', rowspan: '2', width: 150},
            { title: '审批模式', align: 'center', colspan: '2'},
            { field: 'state', title: '状态', align: 'left', rowspan: '2', width: 80, templet: function(d){
                return activitiUtil.showStateName(d.state, d.submitType);
		    }},
            { field: 'totalPrice', title: '合计金额', align: 'left', rowspan: '2', width: 120},
            { field: 'taxMoney', title: '含税合计', align: 'left', rowspan: '2', width: 120 },
            { field: 'discountLastMoney', title: '优惠后金额', align: 'left', rowspan: '2', width: 120 },
            { field: 'changeAmount', title: '付款', align: 'left', rowspan: '2', width: 120 },
            { field: 'operPersonName', title: '操作人', align: 'left', rowspan: '2', width: 100},
            { field: 'operTime', title: '单据日期', align: 'center', rowspan: '2', width: 140 },
            { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', rowspan: '2', width: 200, toolbar: '#tableBar'}
        ],[
            { field: 'submitType', title: '提交模式', align: 'left', width: 120, templet: function(d){
                return erpOrderUtil.getSubmitTypeName(d);
            }},
            { field: 'processInstanceId', title: '流程实例id', align: 'left', width: 120, templet: function(d){
                return erpOrderUtil.getProcessInstanceIdBySubmitType(d);
            }}
        ]],
        done: function(){
        	matchingLanguage();
	    	soulTable.render(this);
        }
    });

    table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'delete') { //删除
            deleteOrder(data);
        }else if (layEvent === 'details') { //详情
        	details(data);
        }else if (layEvent === 'edit') { //编辑
        	edit(data);
        }else if (layEvent === 'submitToSave') { //提交
            subExamine(data);
        }else if (layEvent === 'subExamine') { //提交审核
            subExamine(data);
        }else if (layEvent === 'activitiProcessDetails') { // 工作流流程详情查看
            activitiUtil.activitiDetails(data);
        }else if (layEvent === 'revoke') { //撤销
            erpOrderUtil.revokeOrderMation(data.processInstanceId, systemOrderType["putIsPurchase"]["orderType"], function(){
                loadTable();
            });
        }
    });

    form.render();
    form.on('submit(formSearch)', function (data) {
        if (winui.verifyForm(data.elem)) {
            refreshTable();
        }
        return false;
    });

    // 删除
    function deleteOrder(data){
        erpOrderUtil.deleteOrderMation(data.id, systemOrderType["putIsPurchase"]["orderType"], function(){
            loadTable();
        });
    }
    
    // 提交数据
	function subExamine(data){
        erpOrderUtil.submitOrderMation(data.id, systemOrderType["putIsPurchase"]["orderType"], data.submitType, sysActivitiModel["putIsPurchase"]["key"], function(){
            loadTable();
        });
    }
    
    // 编辑
	function edit(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/purchaseput/purchaseputedit.html", 
			title: systemLanguage["com.skyeye.editPageTitle"][languageType],
			pageId: "purchaseputedit",
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
    
    // 详情
	function details(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/purchaseput/purchaseputdetails.html", 
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "purchaseputdetails",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
			}});
	}

    // 添加
    $("body").on("click", "#addBean", function(){
        _openNewWindows({
            url: "../../tpl/purchaseput/purchaseputadd.html",
            title: systemLanguage["com.skyeye.addPageTitle"][languageType],
            pageId: "purchaseputadd",
            area: ['90vw', '90vh'],
            callBack: function(refreshCode){
                if (refreshCode == '0') {
                    winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1,time: 2000});
                    loadTable();
                } else if (refreshCode == '-9999') {
                    winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
                }
            }});
    });

    $("body").on("click", "#reloadTable", function() {
        loadTable();
    });

    // 刷新
    function loadTable(){
        table.reload("messageTable", {where: getTableParams()});
    }

    // 搜索
    function refreshTable(){
        table.reload("messageTable", {page: {curr: 1}, where: getTableParams()})
    }
    
    // 导出excel
    $("body").on("click", "#downloadExcel", function () {
    	postDownLoadFile({
			url : flowableBasePath + 'purchaseput005?userToken=' + getCookie('userToken') + '&loginPCIp=' + returnCitySN["cip"],
			params: getTableParams(),
			method : 'post'
		});
    });
    
    function getTableParams(){
        var startTime = "";
        var endTime = "";
    	if(!isNull($("#operTime").val())){
            startTime = $("#operTime").val().split('~')[0].trim() + ' 00:00:00';
            endTime = $("#operTime").val().split('~')[1].trim() + ' 23:59:59';
    	}
    	return {
    		defaultNumber: $("#defaultNumber").val(),
    		startTime: startTime,
    		endTime: endTime
    	};
    }

    exports('purchaseputlist', {});
});
