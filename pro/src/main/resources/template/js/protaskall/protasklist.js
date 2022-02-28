
var rowId = "";

layui.config({
	base: basePath,
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'form', 'laydate', 'treeGrid'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		form = layui.form,
		laydate = layui.laydate,
		treeGrid = layui.treeGrid;

	//任务开始时间
	laydate.render({
		elem: '#startTime', //指定元素
		range: '~'
	});

	treeGrid.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    idField: 'id',
	    url: reqBasePath + 'protask001',
	    cellMinWidth: 100,
	    where: getTableParams(),
	    treeId: 'id',//树形id字段名称
        treeUpId: 'pId',//树形父id字段名称
        treeShowName: 'taskName',//以树形式显示的字段
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers', rowspan: 2},
	        { field: 'taskName', title: '名称', rowspan: 2, width: 200, templet: function(d){
	        	return '<a lay-event="details" class="notice-title-click">' + d.taskName + '</a>';
	        }},
	        { field: 'processInstanceId', title: '流程ID', rowspan: 2, width: 70 , templet: function(d){
	        	return '<a lay-event="processDetails" class="notice-title-click">' + d.processInstanceId + '</a>';
	        }},
	        { field: 'state', title: '审批状态', rowspan: 2, width: 80, templet: function(d){
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
	        { field: 'startTime', title: '结束时间', rowspan: 2, width: 80 },
	        { title: '工作量(天)', width: 150, align: 'center', colspan: '2' }
	    ],
	    	[
				{ field: 'estimatedWorkload', title: '预估', align: 'center', width: 60},
				{ field: 'actualWorkload', title: '实际', align: 'center', width: 60}
	    	 ]
	    ],
	    done: function(){
	    	matchingLanguage();
	    }
	});

	treeGrid.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'details'){ //详情
        	details(data);
        } else if (layEvent === 'processDetails') {//流程详情
			activitiUtil.activitiDetails(data);
        } 
    });

	// 详情
	function details(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/protask/protaskdetails.html", 
			title: "任务详情",
			pageId: "protaskdetails",
			area: ['70vw', '70vh'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1,time: 2000});
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
                }
			}});
	}

	// 搜索表单
	form.render();
	form.on('submit(formSearch)', function (data) {
		if (winui.verifyForm(data.elem)) {
			treeGrid.query("messageTable", {page: {curr: 1}, where: getTableParams()});
		}
		return false;
	});

	// 刷新数据
    $("body").on("click", "#reloadTable", function(){
    	loadTable();
    });

    function loadTable(){
    	treeGrid.query("messageTable", {where: getTableParams()});
    }

    function getTableParams(){
		var theStartTime = "", theEndTime = "";
		if(!isNull($("#startTime").val())){
			theStartTime = $("#startTime").val().split('~')[0].trim() + ' 00:00:00';
			theEndTime = $("#startTime").val().split('~')[1].trim() + ' 23:59:59';
		}
    	return {
    		taskName: $("#taskName").val(),
			createId: $("#createId").val(),
			performId: $("#performId").val(),
			firstTime: theStartTime,
			lastTime: theEndTime
    	};
	}

    exports('protasklist', {});
});
