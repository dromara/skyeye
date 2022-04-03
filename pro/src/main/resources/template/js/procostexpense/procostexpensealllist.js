
var rowId = "";

layui.config({
	base: basePath,
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'form', 'table'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		form = layui.form,
		table = layui.table;

	authBtn('1574906688311');
	
	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: reqBasePath + 'procostexpense001',
	    where: getTableParams(),
	    even: true,
	    page: true,
		limits: getLimits(),
		limit: getLimit(),
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
	        { field: 'title', title: '主题', align: 'left', width: 250, templet: function(d){
	        	return '<a lay-event="details" class="notice-title-click">' + d.title + '</a>';
	        }},
	        { field: 'projectName', title: '所属项目', align: 'left', width: 120},
	        { field: 'userName', title: '报销人', align: 'left', width: 80},
	        { field: 'stateName', title: '状态', width: 90, templet: function(d){
	        	if(d.state == 0){
	        		return "<span>草稿</span>";
	        	}else if(d.state == 1){
	        		return "<span class='state-new'>审核中</span>";
	        	}else if(d.state == 11){
	        		return "<span class='state-up'>审核通过</span>";
	        	}else if(d.state == 12){
	        		return "<span class='state-down'>审核失败</span>";
	        	}else if(d.state == 2){
	        		return "<span class='state-down'>作废</span>";
	        	}else if(d.state == 3){
	        		return "<span class='state-error'>撤销</span>";
	        	}
	        }},
	        { field: 'allPrice', title: '报销费用', align: 'left', width: 100},
	        { field: 'reimbursementTime', title: '填报时间', align: 'left', width: 100},
	        { field: 'processInstanceId', title: '流程ID', align: 'center', width: 100, templet: function(d){
	        	if(!isNull(d.processInstanceId)){
	        		return '<a lay-event="processDetails" class="notice-title-click">' + d.processInstanceId + '</a>';
	        	}else{
	        		return "";
	        	}
	        }},
	        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 250, toolbar: '#tableBar'}
	    ]],
	    done: function(){
	    	matchingLanguage();
	    }
	});
	
	table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'details'){ //详情
        	details(data);
        }else if (layEvent === 'processDetails') { //流程详情
			activitiUtil.activitiDetails(data);
        }else if (layEvent === 'edit') { //编辑
        	edit(data);
        }else if (layEvent === 'subApproval') { //提交审批
        	subApproval(data, obj);
        }else if (layEvent === 'cancellation') { //作废
        	cancellation(data, obj);
        }else if (layEvent === 'del') { //删除
        	del(data, obj);
        }else if (layEvent === 'revoke') { //撤销
        	revoke(data);
        }
    });

	// 撤销审批申请
	function revoke(data){
		var msg = '确认从工作流中撤销选中数据吗？';
		layer.confirm(msg, { icon: 3, title: '撤销操作' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url:reqBasePath + "procostexpense007", params:{processInstanceId: data.processInstanceId}, type: 'json', callback: function(json){
    			if(json.returnCode == 0){
    				winui.window.msg("提交成功", {icon: 1, time: 2000});
    				loadTable();
    			}else{
    				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
    			}
    		}});
		});
	}

	// 添加
	$("body").on("click", "#addBean", function(){
    	_openNewWindows({
			url: "../../tpl/procostexpense/procostexpenseadd.html",
			title: systemLanguage["com.skyeye.addPageTitle"][languageType],
			pageId: "procostexpenseadd",
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
	
	// 提交审批
	function subApproval(data, obj){
		layer.confirm(systemLanguage["com.skyeye.approvalOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.approvalOperation"][languageType]}, function (index) {
			layer.close(index);
			activitiUtil.startProcess(sysActivitiModel["proCostExpense"]["key"], function (approvalId) {
				var params = {
					rowId: data.id,
					approvalId: approvalId
				};
				AjaxPostUtil.request({url: reqBasePath + "procostexpense006", params: params, type: 'json', callback: function(json){
					if(json.returnCode == 0){
						winui.window.msg("提交成功", {icon: 1, time: 2000});
						loadTable();
					}else{
						winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
					}
				}});
			});
		});
	}
	
	// 作废
	function cancellation(data, obj){
		var msg = obj ? '确认作废【' + obj.data.title + '】吗？' : '确认作废该任务信息吗？';
		layer.confirm(msg, { icon: 3, title: '作废操作' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url:reqBasePath + "procostexpense010", params:{rowId: data.id}, type: 'json', callback: function(json){
    			if(json.returnCode == 0){
    				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1,time: 2000});
    				loadTable();
    			}else{
    				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
    			}
    		}});
		});
	}
	
	// 详情
	function details(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/procostexpense/procostexpensedetails.html", 
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "procostexpensedetails",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
			}});
	}
	
	// 编辑
	function edit(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/procostexpense/procostexpenseedit.html", 
			title: systemLanguage["com.skyeye.editPageTitle"][languageType],
			pageId: "procostexpenseedit",
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
	
	// 删除
	function del(data, obj){
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
			layer.close(index);
            AjaxPostUtil.request({url: reqBasePath + "procostexpense009", params: {rowId: data.id}, type: 'json', callback: function(json){
    			if(json.returnCode == 0){
    				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1,time: 2000});
    				loadTable();
    			}else{
    				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
    			}
    		}});
		});
	}

	// 搜索表单
	form.render();
	form.on('submit(formSearch)', function (data) {
		if (winui.verifyForm(data.elem)) {
			table.reload("messageTable", {page: {curr: 1}, where: getTableParams()});
		}
		return false;
	});

	//刷新数据
    $("body").on("click", "#reloadTable", function(){
    	loadTable();
    });

    function loadTable(){
    	table.reload("messageTable", {where: getTableParams()});
    }

    function getTableParams(){
    	return {
    		title: $("#title").val(),
			projectName: $("#projectName").val(),
			state: $("#state").val()
    	};
	}

    exports('procostexpensealllist', {});
});
