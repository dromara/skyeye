
var rowId = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'laydate', 'form', 'tableSelect'], function (exports) {
	winui.renderColor();
	
	var $ = layui.$,
		table = layui.table,
		form = layui.form,
		laydate = layui.laydate,
		tableSelect = layui.tableSelect;
	
	tableSelect.render({
    	elem: '#userName',	//定义输入框input对象
    	checkedKey: 'id', //表格的唯一键值，非常重要，影响到选中状态 必填
    	searchKey: 'userName',	//搜索输入框的name值 默认keyword
    	searchPlaceholder: '发件人搜索',	//搜索输入框的提示文字 默认关键词搜索
    	table: {	//定义表格参数，与LAYUI的TABLE模块一致，只是无需再定义表格elem
    		url: sysMainMation.jobdiaayBasePath + 'diary003',
    		where: {userName: $("#userName").val()},
    		method: 'post',
    		page: true,
    	    limits: getLimits(),
	    	limit: getLimit(),
    		cols: [[
    		    { type: 'radio'},
				{ title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
				{ field: 'userName', title: '发件人', width: 100 },
				{ field: 'userSex', title: '性别', width: 60, templet: function (d) {
		        	if(d.userSex == '0'){
		        		return "保密";
		        	}else if(d.userSex == '1'){
		        		return "男";
		        	}else if(d.userSex == '2'){
		        		return "女";
		        	} else {
		        		return "参数错误";
		        	}
		        }},
		        { field: 'departmentName', title: '所在部门', width: 100 }
			]]
    	},
    	done: function (elem, data) {
    		var newJson = data.data[0].userName;
    		var ids = data.data[0].id;
    		elem.val(newJson);
    		elem.attr('ts-selected', ids);
    	}
    })
	
	// '看日报'页面的选取时间段表格
	laydate.render({elem: '#receivedTime', range: '~'});
	
	form.render();
	
	// 收到的日志列表
	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: sysMainMation.jobdiaayBasePath + 'diary001',
	    where: getTableParams(),
	    even: true,
	    page: true,
	    limits: [8, 16, 24, 32, 40, 48, 56],
	    limit: 8,
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
	        { field: 'jobTitle', title: '主题', width: 250, templet: function (d) {
		        	return '<a lay-event="read" class="notice-title-click">' + d.jobTitle + '</a>';
	        }},
	        { field: 'typeName', title: '日志类型', align: 'center', width: 110 },
	        { field: 'state', title: '阅读状态', align: 'center', width: 110, templet: function (d) {
	        	if(d.state == '1'){
	        		return "<span class='state-down'>未读</span>";
	        	}else if(d.state == '2'){
	        		return "<span class='state-up'>已读</span>";
	        	} else {
	        		return "参数错误";
	        	}
	        }},
	        { field: 'createTime', title: '发件时间', align: 'center', width: 130 },
	        { title: '星期几', align: 'center', width: 100, templet: function (d) {
	        	return getMyDay(new Date(d.createTime));
	        }},
	        { field: 'userName', title: '发件人', align: 'center', width: 110 },
	        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 150, toolbar: '#tableBar'}
	    ]],
	    done: function(){
	    	matchingLanguage();
	    }
	});
	
	table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'read') { //阅读
        	read(data);
        } else if (layEven === 'del'){  //删除
        	del(data);
        }
    });

	// 阅读收到的日志
	function read(data) {
		rowId = data.id;
		if(data.diaryType == 1){
			// 日报
			_openNewWindows({
				url: "../../tpl/jobdiaryMyReceive/jobdiaryMyReceiveDayDetails.html", 
				title: "日志内容",
				pageId: "jobdiaryMyReceiveDayDetails",
				area: ['90vw', '90vh'],
				callBack: function (refreshCode) {
					if(data.state == '1')
						loadTable();
				}});
		}else if(data.diaryType == 2){
			// 周报
			_openNewWindows({
				url: "../../tpl/jobdiaryMyReceive/jobdiaryMyReceiveWeekDetails.html", 
				title: "日志内容",
				pageId: "jobdiaryMyReceiveWeekDetails",
				area: ['90vw', '90vh'],
				callBack: function (refreshCode) {
					if(data.state == '1')
						loadTable();
				}});
		}else if(data.diaryType == 3){
			// 月报
			_openNewWindows({
				url: "../../tpl/jobdiaryMyReceive/jobdiaryMyReceiveMonthDetails.html", 
				title: "日志内容",
				pageId: "jobdiaryMyReceiveMonthDetails",
				area: ['90vw', '90vh'],
				callBack: function (refreshCode) {
					if(data.state == '1')
						loadTable();
				}});
		}
	}
	
	// 删除日志
	function del(data) {
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
			layer.close(index);
            AjaxPostUtil.request({url: sysMainMation.jobdiaayBasePath + "diary008", params: {rowId: data.id}, type: 'json', callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}

    // 我收到的日志全部设置为已读
    $("body").on("click", "#alreadyRead", function() {
        alreadyRead();
    });
	
    // 时间线查看自己收到的日志
    $("body").on("click", "#timeLineSel", function() {
        _openNewWindows({
            url: "../../tpl/jobdiaryMyReceive/jobdiaryMyReceiveTimeLine.html", 
            title: "日志时间线",
            pageId: "jobdiaryMyReceiveTimeLine",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
            }});
    });
	
    // 我收到的日志全部设置为已读
    function alreadyRead(){
        AjaxPostUtil.request({url: sysMainMation.jobdiaayBasePath + "diary024", type: 'json', callback: function (json) {
			winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
			loadTable();
        }});
    };
    
    // 我收到的日志刷新
    $("body").on("click", "#reloadTable", function() {
        loadTable();
    });
    
	// 我收到的日志点击搜索
	$("body").on("click", "#searchMyReceived", function() {
    	table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
	});
	
	//加载'看日志'列表
    function loadTable() {
    	table.reloadData("messageTable", {where: getTableParams()});
    }
    
	function getTableParams() {
		var startTime = "", endTime = "";
		if (!isNull($("#receivedTime").val())) {
    		startTime = $("#receivedTime").val().split('~')[0].trim() + ' 00:00:00';
    		endTime = $("#receivedTime").val().split('~')[1].trim() + ' 23:59:59';
    	}
    	return {
    		firstTime: startTime,
    		lastTime: endTime,
    		userName: $("#userName").val(),
    		diaryType: $("#diaryType").val()
    	};
	}
    	
    exports('jobdiaryMyReceiveList', {});
});
