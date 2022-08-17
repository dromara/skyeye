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
	
	authBtn('1574672416396');//新增

	// 项目分类
	sysDictDataUtil.showDictDataListByDictTypeCode(sysDictData["pmProjectType"]["key"], 'select', "typeId", '', form);

	initTable();
	function initTable(){
		table.render({
		    id: 'messageTable',
		    elem: '#messageTable',
		    method: 'post',
		    url: flowableBasePath + 'proproject002',
		    where: getTableParams(),
		    even: true,
		    page: true,
		    limits: getLimits(),
			limit: getLimit(),
		    cols: [[
		        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers', rowspan: 2},
		        { field: 'projectName', title: '项目名称', align: 'left', width: 200, rowspan: 2, templet: function (d) {
		        	return '<a lay-event="details" class="notice-title-click">' + d.projectName + '</a>';
		        }},
		        { field: 'typeName', title: '项目分类', align: 'left', rowspan: '2', width: 100},
		        { title: '完成时间', align: 'center', colspan: '2'},
		        { field: 'stateName', title: '状态', rowspan: 2, width: 90, templet: function (d) {
		        	if(d.state == '0'){
		        		return "<span>" + d.stateName + "</span>";
		        	}else if(d.state == '1'){
		        		return "<span class='state-new'>" + d.stateName + "</span>";
		        	}else if(d.state == '2'){
		        		return "<span class='state-up'>" + d.stateName + "</span>";
		        	}else if(d.state == '11'){
		        		return "<span class='state-up'>" + d.stateName + "</span>";
		        	}else if(d.state == '12'){
		        		return "<span class='state-down'>" + d.stateName + "</span>";
		        	}else if(d.state == '3'){
		        		return "<span class='state-error'>" + d.stateName + "</span>";
		        	}else if(d.state == '4'){
		        		return "<span class='state-error'>" + d.stateName + "</span>";
		        	}else if(d.state == '5'){
		        		return "<span class='state-error'>" + d.stateName + "</span>";
		        	}
		        }},
		        { field: 'createName', title: '发起人', align: 'left', rowspan: '2', width: 100},
		        { field: 'projectManagerName', title: '负责人', align: 'left', rowspan: '2', width: 100},
		        { field: 'projectMembersName', title: '参与人', align: 'left', rowspan: '2', width: 100},
		        { field: 'projectSponsorName', title: '赞助人', align: 'left', rowspan: '2', width: 100},
		        { field: 'taskNum', title: '任务', align: 'left', rowspan: 2, width: 80 },
		        { field: 'estimatedWorkload', title: '工作量', align: 'left', rowspan: 2, width: 80 },
		        { field: 'estimatedCost', title: '成本费用', align: 'left', rowspan: 2, width: 100 },
		        { field: 'fileNum', title: '文档', align: 'left', rowspan: 2, width: 80, templet: function (d) {
	        		return '<a lay-event="fileNumList" class="notice-title-click">' + d.fileNum + '</a>';
		        }},
		        { field: 'discussNum', title: '讨论板', align: 'left', rowspan: 2, width: 80, templet: function (d) {
	        		return '<a lay-event="discussList" class="notice-title-click">' + d.discussNum + '</a>';
		        }},
		        { field: 'processInstanceId', title: '流程ID', align: 'center', rowspan: 2, width: 100, templet: function (d) {
		        	if (!isNull(d.processInstanceId)){
		        		return '<a lay-event="processDetails" class="notice-title-click">' + d.processInstanceId + '</a>';
		        	} else {
		        		return "";
		        	}
		        }},
		        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', rowspan: 2, width: 250, toolbar: '#tableBar'}
		    ], [
		    	{ field: 'endTime', title: '计划', align: 'center', width: 85},
		        { field: 'actualEndTime', title: '实际', align: 'center', width: 85}
		       ]
		    ],
		    done: function(){
		    	matchingLanguage();
		    }
		});
		
		table.on('tool(messageTable)', function (obj) {
	        var data = obj.data;
	        var layEvent = obj.event;
	        if (layEvent === 'edit') { //编辑
	        	edit(data);
	        } else if (layEvent === 'details'){ //详情
	        	details(data);
	        } else if (layEvent === 'processDetails') { //流程详情
				activitiUtil.activitiDetails(data);
	        } else if (layEvent === 'subApproval') { //提交审批
	        	subApproval(data);
	        } else if (layEvent === 'del') { //删除
	        	del(data);
	        } else if (layEvent === 'revoke') { //撤销
	        	revoke(data);
	        } else if (layEvent === 'nullify') { //作废
	        	nullify(data);
	        } else if (layEvent === 'execute') { //开始执行
	        	if (!isNull(data.projectManager) && !isNull(data.projectMembers) && !isNull(data.projectContent)){
		        	execute(data);
	        	} else {
	        		layer.confirm("该项目还未进行项目任命，是否立即任命？", {btn: ['立即任命', '取消'], icon: 3, title: '操作提醒'}, function (index) {
						layer.close(index);
			            proappoint(data);
					});
	        	}
	        } else if (layEvent === 'proappoint') { //项目任命
	        	proappoint(data);
	        } else if (layEvent === 'end') { //成果和总结
	        	end(data);
	        } else if (layEvent === 'discussList') { //讨论板
	        	discussList(data);
	        } else if (layEvent === 'fileNumList') { //文档
	        	fileNumList(data);
	        }
	    });
	}
	
    // 删除
	function del(data) {
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
			layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "proproject009", params: {rowId: data.id}, type: 'json', callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}
	
	// 撤销
	function revoke(data) {
		layer.confirm("确定撤销申请吗？", {icon: 3, title: '撤销操作'}, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "proproject010", params: {processInstanceId: data.processInstanceId}, type: 'json', method: "PUT", callback: function (json) {
				winui.window.msg("撤销成功", {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}
	
	// 作废
	function nullify(data) {
		layer.confirm("确定作废该项目吗？", {icon: 3, title: '作废操作'}, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "proproject011", params: {rowId: data.id}, type: 'json', callback: function (json) {
				winui.window.msg("作废成功", {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}
	
	// 开始执行
	function execute(data) {
		layer.confirm("确定开始执行该项目吗？", {icon: 3, title: '执行操作'}, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "proproject012", params: {rowId: data.id}, type: 'json', callback: function (json) {
				winui.window.msg("执行成功", {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}
	
	// 项目任命
	function proappoint(data) {
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/proproject/proappoint.html", 
			title: "项目任命",
			pageId: "proappoint",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	}
	
	// 成果和总结
	function end(data) {
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/proproject/properfectinformation.html", 
			title: "成果和总结",
			pageId: "properfectinformation",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	}
    
	// 新增
	$("body").on("click", "#addBean", function() {
    	_openNewWindows({
			url: "../../tpl/proproject/proprojectadd.html", 
			title: "新增项目",
			pageId: "proprojectadd",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
    });
	
	// 编辑
	function edit(data) {
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/proproject/proprojectedit.html", 
			title: "编辑项目",
			pageId: "proprojectedit",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	}
	
	// 详情
	function details(data) {
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/proproject/proprojectdetails.html", 
			title: "项目详情",
			pageId: "proprojectdetails",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	}

	// 提交审批
	function subApproval(data) {
		layer.confirm(systemLanguage["com.skyeye.approvalOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.approvalOperation"][languageType]}, function (index) {
			layer.close(index);
			activitiUtil.startProcess(sysActivitiModel["proProject"]["key"], function (approvalId) {
				var params = {
					rowId: data.id,
					approvalId: approvalId
				};
				AjaxPostUtil.request({url: flowableBasePath + "proproject008", params: params, type: 'json', callback: function (json) {
					winui.window.msg("提交成功", {icon: 1, time: 2000});
					loadTable();
				}});
			});
		});
	}
	
	// 讨论板
	function discussList(data) {
		rowId = data.id;
		_openNewWindows({
			url : "../../tpl/proprojectdiscuss/proprojectdiscusslist.html",
			title : "讨论板",
			pageId : "proprojectdiscusslist",
			area : [ '100vw', '100vh' ],
			callBack : function (refreshCode) {
			}
		});
	}
	
	// 文档
	function fileNumList(data) {
		rowId = data.id;
		_openNewWindows({
			url : "../../tpl/profile/profilelistbyproid.html",
			title : "文档",
			pageId : "profilelistbyproid",
			area : [ '100vw', '100vh' ],
			callBack : function (refreshCode) {
			}
		});
	}

	// 搜索表单
	form.render();
	form.on('submit(formSearch)', function (data) {
		if (winui.verifyForm(data.elem)) {
			table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
		}
		return false;
	});

	$("body").on("click", "#reloadTable", function() {
		loadTable();
	});

	function loadTable() {
		table.reloadData("messageTable", {where: getTableParams()});
	}

	function getTableParams() {
		return {
			typeId: $("#typeId").val(),
			state: $("#state").val(),
			proName: $("#proName").val(),
			proBelongId: $("#proBelongId").val()
		};
	}

    exports('myproprojectlist', {});
});