
var processInstanceId = "";//流程id

var sequenceId = "";//动态表单类型的流程

var rowId = "";//用户提交的表单数据的id

var taskId = "";//任务id

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form', 'laydate'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		table = layui.table,
		laydate = layui.laydate,
		form = layui.form;
	
	// '申请时间'页面的选取时间段表格
	laydate.render({elem: '#createTime', range: '~'});
	
	//申请时间
	var startTime = "", endTime = "";
	
	// 我启动的流程
	table.render({
	    id: 'messageMyStartTable',
	    elem: '#messageMyStartTable',
	    method: 'post',
	    url: flowableBasePath + 'activitimode013',
	    where:{startTime: startTime, endTime: endTime, processInstanceId: $("#processInstanceId").val()},
	    even:true,
	    page: true,
		limits: getLimits(),
		limit: getLimit(),
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
	        { field: 'processInstanceId', title: '流程ID', width: 100 },
	        { field: 'taskType', title: '类型', width: 100 },
	        { field: 'createName', title: '申请人', width: 100},
	        { field: 'createTime', title: '申请时间', align: 'center', width: 150 },
	        { field: 'name', title: '当前节点', width: 130, templet: function (d) {
	        	return '[' + d.name + ']';
	        }},
	        { field: 'agencyName', title: '审批人', width: 120},
	        { field: 'suspended', title: '状态<i id="stateDesc" class="fa fa-question-circle" style="margin-left: 5px"></i>', align: 'center', width: 130, templet: function (d) {
	        	if(d.suspended){
	        		return "<span class='state-down'>挂起</span>";
	        	} else {
	        		return "<span class='state-up'>正常</span>";
	        	}
	        }},
	        { field: 'weatherEnd', title: '审批进度', align: 'left', width: 80, templet: function (d) {
	        	if(d.weatherEnd == 0){
	        		return "<span class='state-down'>进行中</span>";
	        	} else {
	        		return "<span class='state-up'>已完成</span>";
	        	}
	        }},
	        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 240, toolbar: '#myStartTableBar'}
	    ]],
	    done: function(){
	    	matchingLanguage();
	    }
	});
	
	table.on('tool(messageMyStartTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'edit') { //编辑
        	edit(data);
        } else if (layEvent === 'details') { //详情
			activitiUtil.activitiDetails(data);
        } else if (layEvent === 'revoke') { //撤销
        	revoke(data);
        } else if (layEvent === 'refreshPic') { //刷新流程图
        	refreshPic(data);
        }
    });
	
	//编辑
	function edit(data) {
		sequenceId = data.sequenceId;
		taskId = data.id;
		processInstanceId = data.processInstanceId;
		rowId = data.dataId;
		_openNewWindows({
			url: data.pageUrl, 
			title: systemLanguage["com.skyeye.editPageTitle"][languageType],
			pageId: "myactivitiedit",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
			}
		});
	}

	//撤销
	function revoke(data) {
		if(isNull(data.revokeMapping)){//撤销接口为空
			winui.window.msg('撤销接口调用失败', {icon: 2, time: 2000});
			return false;
		}
		layer.confirm('确定撤销该流程吗？', { icon: 3, title: '撤销操作' }, function (index) {
			layer.close(index);
	        AjaxPostUtil.request({url: flowableBasePath + data.revokeMapping, params: {processInstanceId: data.processInstanceId}, type: 'json', method: "PUT", callback: function (json) {
				winui.window.msg("撤销成功", {icon: 1, time: 2000});
				reloadMyStartTable();
			}});
		});
	}
	
	//刷新流程图
	function refreshPic(data) {
		layer.confirm('确认重新生成流程图吗？', { icon: 3, title: '刷新流程图操作' }, function (i) {
			layer.close(i);
            AjaxPostUtil.request({url:flowableBasePath + "activitimode027", params: {processInstanceId: data.processInstanceId}, type: 'json', callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
 	   		}});
		});
	}
	
    //刷新我启用的流程
	$("body").on("click", "#reloadMyStartTable", function() {
		reloadMyStartTable();
	});
	
	//搜索
	$("body").on("click", "#formSearch", function() {
		searchMyStartTable();
	});
	
    function reloadMyStartTable(){
    	if (!isNull($("#createTime").val())) {//一定要记得，当createTime为空时
    		startTime = $("#createTime").val().split('~')[0].trim() + ' 00:00:00';
    		endTime = $("#createTime").val().split('~')[1].trim() + ' 23:59:59';
    	} else {
    		startTime = "";
    		endTime = "";
    	}
    	table.reloadData("messageMyStartTable", {where:{startTime: startTime, endTime: endTime, processInstanceId: $("#processInstanceId").val()}});
    }
    
    function searchMyStartTable(){
    	if (!isNull($("#createTime").val())) {//一定要记得，当createTime为空时
    		startTime = $("#createTime").val().split('~')[0].trim() + ' 00:00:00';
    		endTime = $("#createTime").val().split('~')[1].trim() + ' 23:59:59';
    	} else {
    		startTime = "";
    		endTime = "";
    	}
    	table.reloadData("messageMyStartTable", {page: {curr: 1}, where:{startTime: startTime, endTime: endTime, processInstanceId: $("#processInstanceId").val()}});
    }
    
    $("body").on("click", "#stateDesc", function() {
		layer.tips('该状态分为挂机和正常，被挂机待办无法进行审批操作', $("#stateDesc"), {
			tips: [1, '#3595CC'],
			time: 4000
		});
	});
    
    exports('initiatedProcess', {});
});
