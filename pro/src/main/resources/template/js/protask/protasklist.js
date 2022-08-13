
var rowId = "";

var isSplitTask = false;//是否是拆分的新增
var restWorkload = "";//主任务拆分剩下的工作量

layui.config({
	base: basePath,
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'form', 'laydate', 'tableTreeDj'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		form = layui.form,
		laydate = layui.laydate,
		tableTree = layui.tableTreeDj;

	authBtn('1574644930825');
	
	// 任务开始时间
	laydate.render({elem: '#startTime', range: '~'});

	tableTree.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: flowableBasePath + 'protask011',
	    where: getTableParams(),
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers', rowspan: 2},
	        { field: 'taskName', title: '名称', rowspan: 2, width: 200, templet: function (d) {
	        	return '<a lay-event="details" class="notice-title-click">' + d.taskName + '</a>';
	        }},
	        { field: 'processInstanceId', title: '流程ID', rowspan: 2, width: 70 , templet: function (d) {
	        	return '<a lay-event="processDetails" class="notice-title-click">' + d.processInstanceId + '</a>';
	        }},
	        { field: 'state', title: '审批状态', rowspan: 2, width: 80, templet: function (d) {
	        	if (d.state == '0'){
	        		return "草稿";
	        	} else if (d.state == '1'){
	        		return "<span class='state-new'>审核中</span>";
	        	} else if (d.state == '2'){
	        		return "<span class='state-new'>执行中</span>";
	        	} else if (d.state == '3'){
	        		return "<span class='state-new'>执行完成</span>";
	        	} else if (d.state == '4'){
	        		return "<span class='state-error'>关闭</span>";
	        	} else if (d.state == '5'){
	        		return "<span class='state-error'>撤销</span>";
	        	} else if (d.state == '6'){
	        		return "<span class='state-down'>作废</span>";
	        	} else if (d.state == '11'){
	        		return "<span class='state-up'>审核通过</span>";
	        	} else if (d.state == '12'){
	        		return "<span class='state-down'>审核不通过</span>";
	        	} 
	        }},
	        { field: 'projectName', title: '所属项目', rowspan: 2, width: 120 },
	        { field: 'performId', title: '执行人', rowspan: 2, width: 200 },
	        { field: 'createId', title: systemLanguage["com.skyeye.createName"][languageType], rowspan: 2, width: 120 },
	        { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], rowspan: 2, width: 115 },
	        { field: 'startTime', title: '开始时间', rowspan: 2, width: 80 },
	        { title: '工作量(天)', align: 'center', colspan: 2 },
	        { title: systemLanguage["com.skyeye.operation"][languageType], align: 'center', width: 300, rowspan: 2, toolbar: '#tableBar'}
	    ],
    		[
    		 	{ field: 'estimatedWorkload', title: '预估', align: 'center', width: 50},
    		 	{ field: 'actualWorkload', title: '实际', align: 'center', width: 50}
			]	    
	    ],
	    done: function(){
	    	matchingLanguage();
	    }
	}, {
		keyId: 'id',
		keyPid: 'pId',
		title: 'taskName',
	});

	tableTree.getTable().on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'details'){ //详情
        	details(data);
        } else if (layEvent === 'edit') { //编辑
        	edit(data);
        } else if (layEvent === 'del'){ //删除
        	del(data, obj);
        } else if (layEvent === 'subApproval') { //提交审批
        	subApproval(data, obj);
        } else if (layEvent === 'processDetails') {//流程详情
			activitiUtil.activitiDetails(data);
        } else if (layEvent === 'executionBegin') {//开始执行
        	executionBegin(data, obj);
        }else if(layEvent === 'revoke') {//撤销任务审批申请
        	revoke(data);
        }else if(layEvent === 'taskSplit') {//拆分任务
        	taskSplit(data);
        }else if(layEvent === 'cancellation') {//作废
        	cancellation(data, obj);
        }else if(layEvent === 'executionOver') {//执行完成
        	executionOver(data);
        }else if(layEvent === 'executionClose') {//任务关闭
        	executionClose(data, obj);
        }
    });

	// 开始执行
	function executionBegin(data, obj){
		var msg = obj ? '确认开始执行【' + obj.data.taskName + '】吗？' : '确认开始执行该任务吗？';
		layer.confirm(msg, { icon: 3, title: '任务开始执行' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "protask012", params:{rowId: data.id}, type: 'json', callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}
	
	// 任务关闭
	function executionClose(data, obj){
		var msg = obj ? '确认关闭【' + obj.data.taskName + '】吗？' : '确认关闭该任务吗？';
		layer.confirm(msg, { icon: 3, title: '关闭任务' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "protask014", params:{rowId: data.id}, type: 'json', callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}
	
	// 撤销任务审批申请
	function revoke(data){
		var msg = '确认从工作流中撤销选中数据吗？';
		layer.confirm(msg, { icon: 3, title: '撤销任务审批申请' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "protask007", params:{processInstanceId: data.processInstanceId}, type: 'json', callback: function (json) {
				winui.window.msg("提交成功", {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}

	// 添加
	$("body").on("click", "#addBean", function() {
		isSplitTask = false;
    	_openNewWindows({
			url: "../../tpl/protask/protaskadd.html",
			title: "新增任务",
			pageId: "protaskadd",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
    });
	
	// 拆分任务
	function taskSplit(data){
		isSplitTask = true;
		rowId = data.id;
		restWorkload = data.restWorkload;
    	_openNewWindows({
			url: "../../tpl/protask/protaskadd.html",
			title: '<span style="color: blue; font-size:21px">' + data.taskName + '</span><span style="font-size:12px">[拆分子任务]</span>',
			pageId: "protaskadd",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	}
	
	// 执行完成
	function executionOver(data){
		rowId = data.id;
    	_openNewWindows({
			url: "../../tpl/protask/protaskexecution.html",
			title: '执行信息',
			pageId: "protaskexecution",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	}
	
	// 任务提交审批
	function subApproval(data, obj){
		layer.confirm(systemLanguage["com.skyeye.approvalOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.approvalOperation"][languageType]}, function (index) {
			layer.close(index);
			activitiUtil.startProcess(sysActivitiModel["proTask"]["key"], function (approvalId) {
				var params = {
					rowId: data.id,
					approvalId: approvalId
				};
				AjaxPostUtil.request({url: flowableBasePath + "protask008", params: params, type: 'json', callback: function (json) {
					winui.window.msg("提交成功", {icon: 1, time: 2000});
					loadTable();
				}});
			});
		});
	}
	
	// 作废
	function cancellation(data, obj){
		var msg = obj ? '确认作废【' + obj.data.taskName + '】吗？' : '确认作废该任务信息吗？';
		layer.confirm(msg, { icon: 3, title: '任务作废' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "protask009", params:{rowId: data.id}, type: 'json', callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}
	
	// 详情
	function details(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/protask/protaskdetails.html", 
			title: "任务详情",
			pageId: "protaskdetails",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
			}});
	}

	// 编辑
	function edit(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/protask/protaskedit.html",
			title: "编辑任务",
			pageId: "protaskedit",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}
		});
	}
	
	// 删除
	function del(data, obj){
		var msg = obj ? '确认删除【' + obj.data.taskName + '】吗？' : '确认删除选中数据吗？';
		layer.confirm(msg, {icon: 3, title: '删除任务'}, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "protask006", params: {rowId: data.id}, type: 'json', callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}

	// 搜索表单
	form.render();
	form.on('submit(formSearch)', function (data) {
		if (winui.verifyForm(data.elem)) {
			loadTable();
		}
		return false;
	});

	// 刷新数据
    $("body").on("click", "#reloadTable", function() {
    	loadTable();
    });

    function loadTable(){
		tableTree.reload("messageTable", {where: getTableParams()});
    }

    function getTableParams(){
		var theStartTime = "", theEndTime = "";
		if (!isNull($("#startTime").val())) {
			theStartTime = $("#startTime").val().split('~')[0].trim() + ' 00:00:00';
			theEndTime = $("#startTime").val().split('~')[1].trim() + ' 23:59:59';
		}
    	return {
    		taskName: $("#taskName").val(),
			myRole: $("#myRole").val(),
			state: $("#state").val(),
			firstTime: theStartTime,
			lastTime: theEndTime
    	};
	}

    exports('protasklist', {});
});
