
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
        
    authBtn('1593442212544');// 新增
        
    table.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        url: flowableBasePath + 'erpmachin001',
        where: getTablePatams(),
        even: true,
        page: true,
        overflow: {
            type: 'tips',
            hoverTime: 300, // 悬停时间，单位ms, 悬停 hoverTime 后才会显示，默认为 0
            minWidth: 150, // 最小宽度
            maxWidth: 500 // 最大宽度
        },
        limits: getLimits(),
        limit: getLimit(),
        cols: [[
            { title: systemLanguage["com.skyeye.serialNumber"][languageType], rowspan: '2', type: 'numbers'},
            { field: 'orderNum', rowspan: '2', title: '单据编号', align: 'center', width: 180, templet: function(d){
		        return '<a lay-event="details" class="notice-title-click">' + d.orderNum + '</a>';
		    }},
            { field: 'productionNumber', rowspan: '2', title: '生产计划单', align: 'center', width: 200},
            { colspan: '3', title: '加工成品信息', align: 'center'},
            { field: 'state', rowspan: '2', title: '状态', align: 'left', width: 80, templet: function(d){
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
        if (layEvent === 'delete') { // 删除
            deletemember(data);
        }else if (layEvent === 'details') { // 详情
        	details(data);
        }else if (layEvent === 'edit') { // 编辑
        	edit(data);
        }else if (layEvent === 'subExamine') { // 提交审核
        	subExamine(data);
        }else if (layEvent === 'examine') { // 审核
        	examine(data);
        }
    });

    form.render();
    form.on('submit(formSearch)', function (data) {
        if (winui.verifyForm(data.elem)) {
            loadTable();
        }
        return false;
    });
    
    // 编辑
    function edit(data){
        rowId = data.id;
        _openNewWindows({
            url: "../../tpl/erpMachin/erpMachinEdit.html",
            title: systemLanguage["com.skyeye.editPageTitle"][languageType],
            pageId: "erpMachinEdit",
            area: ['90vw', '90vh'],
            callBack: function(refreshCode){
                if (refreshCode == '0') {
                    winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                    loadTable();
                } else if (refreshCode == '-9999') {
                    winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2, time: 2000});
                }
            }});
    }

    // 删除
    function deletemember(data){
        layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
            AjaxPostUtil.request({url:flowableBasePath + "erpmachin005", params: {rowId: data.id}, type: 'json', callback: function(json){
                if (json.returnCode == 0) {
                    winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
                    loadTable();
                } else {
                    winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
                }
            }});
        });
    }
    
    // 详情
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
	
	// 提交审批
	function subExamine(data){
        layer.confirm('确认要提交审核吗？', { icon: 3, title: '提交审核操作' }, function (index) {
            AjaxPostUtil.request({url:flowableBasePath + "erpmachin007", params: {rowId: data.id}, type: 'json', callback: function(json){
                if (json.returnCode == 0) {
                    winui.window.msg("提交成功。", {icon: 1, time: 2000});
                    loadTable();
                } else {
                    winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
                }
            }});
        });
    }
    
    // 审核
	function examine(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/erpMachin/erpMachinExamine.html", 
			title: "审核",
			pageId: "erpMachinExamine",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                	loadTable();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2, time: 2000});
                }
			}});
    }
	
    //添加
    $("body").on("click", "#addBean", function() {
        _openNewWindows({
            url: "../../tpl/erpMachin/erpMachinAdd.html",
            title: systemLanguage["com.skyeye.addPageTitle"][languageType],
            pageId: "erpMachinAdd",
            area: ['90vw', '90vh'],
            callBack: function(refreshCode){
                if (refreshCode == '0') {
                    winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                    loadTable();
                } else if (refreshCode == '-9999') {
                    winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2, time: 2000});
                }
            }});
    });

    $("body").on("click", "#reloadTable", function() {
        loadTable();
    });

    //刷新
    function loadTable(){
        table.reload("messageTable", {where: getTablePatams()});
    }

    //搜索
    function refreshTable(){
        table.reload("messageTable", {page: {curr: 1}, where: getTablePatams()})
    }
    
    function getTablePatams(){
    	return {
    		defaultNumber: $("#defaultNumber").val(), 
    		materialName: $("#materialName").val()
    	};
    }

    exports('erpMachinList', {});
});
