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

	// 项目分类
	sysDictDataUtil.showDictDataListByDictTypeCode(sysDictData["pmProjectType"]["key"], 'select', "typeId", '', form);

	initTable();
	function initTable(){
		table.render({
		    id: 'messageTable',
		    elem: '#messageTable',
		    method: 'post',
		    url: flowableBasePath + 'proproject001',
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
		        { field: 'estimatedCost', title: '成本费用', align: 'left', rowspan: 2, width: 80 },
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
		        }}
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
	        if (layEvent === 'details'){ //详情
	        	details(data);
	        } else if (layEvent === 'processDetails') { //流程详情
				activitiUtil.activitiDetails(data);
	        } else if (layEvent === 'discussList') { //讨论板
	        	discussList(data);
	        } else if (layEvent === 'fileNumList') { //文档
	        	fileNumList(data);
	        }
	    });
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
			proName: $("#proName").val()
    	};
	}
	
    exports('proprojectlist', {});
});