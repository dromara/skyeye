
var rowId = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form'], function (exports) {
	
	winui.renderColor();
	
	var $ = layui.$,
		form = layui.form,
		table = layui.table;
	
	authBtn('1590913527206');
	
	table.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        url: flowableBasePath + 'erpproduction001',
        where: getTableParams(),
        even: true,
        page: true,
        limits: getLimits(),
        limit: getLimit(),
        cols: [[
        	{ title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
            { field: 'defaultNumber', title: '生产单号', align: 'center', width: 200, templet: function (d) {
		        return '<a lay-event="details" class="notice-title-click">' + d.defaultNumber + '</a>';
		    }},
            { field: 'salesOrderNum', width: 200, title: '关联销售单', align: 'center'},
            { field: 'materialName', width: 150, title: '商品名称'},
            { field: 'materialModel', width: 150, title: '商品型号'},
            { field: 'number', width: 80, title: '计划数量'},
	        { field: 'stateName', width: 80, align: 'center', title: '状态', templet: function (d) {
		        if(d.state == 1){
	        		return "<span class='state-new'>" + d.stateName + "</span>";
	        	}else if(d.state == 2){
	        		return "<span>" + d.stateName + "</span>";
	        	}else if(d.state == 3){
	        		return "<span class='state-up'>" + d.stateName + "</span>";
	        	}else if(d.state == 4){
	        		return "<span class='state-down'>" + d.stateName + "</span>";
	        	}else if(d.state == 5){
	        		return "<span class='state-up'>" + d.stateName + "</span>";
	        	} else {
	        		return "参数错误";
	        	}
		    }},
	        { field: 'planStartDate', width: 140, align: 'center', title: '计划开始时间'},
	        { field: 'planComplateDate', width: 140, align: 'center', title: '计划结束时间'},
            { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 240, toolbar: '#tableBar'}
        ]],
	    done: function(){
	    	matchingLanguage();
	    }
    });
	
	table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'del') { //删除
        	del(data, obj);
        }else if (layEvent === 'edit') { //编辑
        	edit(data);
        }else if (layEvent === 'details') { //详情
        	details(data);
        }else if (layEvent === 'subExamine') { //提交审核
        	subExamine(data);
        }else if (layEvent === 'examine') { //审核
        	examine(data);
        }
    });
	
	form.render();
	form.on('submit(formSearch)', function (data) {
        if (winui.verifyForm(data.elem)) {
        	refreshTable();
        }
        return false;
	});
	
	//删除
	function del(data, obj){
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
			layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "erpproduction005", params:{orderId: data.id}, type: 'json', callback: function (json) {
    			if (json.returnCode == 0) {
    				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
    				loadTable();
    			} else {
    				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
    			}
    		}});
		});
	}
	
	//编辑
	function edit(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/erpProduction/erpProductionEdit.html", 
			title: systemLanguage["com.skyeye.editPageTitle"][languageType],
			pageId: "erpProductionEdit",
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
	
	//详情
	function details(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/erpProduction/erpProductionDetail.html", 
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "erpProductionDetail",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
			}});
	}
	
	//提交审批
	function subExamine(data){
        layer.confirm('确认要提交审核吗？', { icon: 3, title: '提交审核操作' }, function (index) {
            AjaxPostUtil.request({url: flowableBasePath + "erpproduction007", params: {orderId: data.id}, type: 'json', callback: function (json) {
                if (json.returnCode == 0) {
                    winui.window.msg("提交成功。", {icon: 1, time: 2000});
                    loadTable();
                } else {
                    winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
                }
            }});
        });
    }
    
    //审核
	function examine(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/erpProduction/erpProductionExamine.html", 
			title: "审核",
			pageId: "erpProductionExamine",
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
	
	//刷新数据
    $("body").on("click", "#reloadTable", function() {
    	loadTable();
    });
    
    //新增
    $("body").on("click", "#addBean", function() {
    	_openNewWindows({
			url: "../../tpl/erpProduction/erpProductionAdd.html", 
			title: systemLanguage["com.skyeye.addPageTitle"][languageType],
			pageId: "erpProductionAdd",
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
    
    function loadTable(){
    	table.reload("messageTable", {where: getTableParams()});
    }
    
    //搜索
    function refreshTable(){
        table.reload("messageTable", {page: {curr: 1}, where: getTableParams()})
    }
    
    function getTableParams(){
    	return {
    		materialName: $("#materialName").val(),
    		materialModel: $("#materialModel").val(),
    		state: $("#state").val(),
    		defaultNumber: $("#defaultNumber").val(),
    		salesOrderNum: $("#salesOrderNum").val()
    	};
    }
    
    exports('erpProductionList', {});
});
