
var rowId = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'laydate', 'form'], function (exports) {
	
	winui.renderColor();
	
	var $ = layui.$,
		table = layui.table,
		form = layui.form,
		laydate = layui.laydate;
	
	// 发日志
	authBtn('1555748167744');
	
	// '我发出的'页面的选取时间段表格
	laydate.render({
		elem: '#createTime',
		range: '~'
	});
	
	form.render();
	
    initMysendTable();
	// 发出的日志列表
	function initMysendTable(){
		table.render({
		    id: 'mysendTable',
		    elem: '#mysendTable',
		    method: 'post',
		    url: reqBasePath + 'diary005',
		    where: getTableParams(),
		    even: true,
		    page: true,
		    limits: getLimits(),
	    	limit: getLimit(),
		    cols: [[
		        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
		        { field: 'jobTitle', title: '主题', width: 250, templet: function(d){
		        	return '<a lay-event="reading" class="notice-title-click">' + d.jobTitle + '</a>';
	       		}},
		        { field: 'typeName', title: '日志类型', align: 'center', width: 110},
		        { field: 'createTime', title: '发件时间', align: 'center', width: 130 },
		        { title: '星期几', align: 'center', width: 100, templet: function(d){
		        	return getMyDay(new Date(d.createTime));
		        }},
		        { field: 'state', title: '发件状态', align: 'center', width: 130, templet: function(d){
		        	if(d.state == 1){
		        		return "已发送";
		        	}else if(d.state == 2){
		        		return "未发送";
		        	}else{
		        		return "参数错误";
		        	}
		        }},
		        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 200, toolbar: '#tableBar1'}
		    ]],
		    done: function(){
		    	matchingLanguage();
		    }
		});
		
		table.on('tool(mysendTable)', function (obj) {
	        var data = obj.data;
	        var layEvent = obj.event;
	        if (layEvent === 'repeal') { //撤回
	        	repeal(data);
	        }else if(layEvent === 'reading'){ //阅读日志详情
	        	reading(data);
	        }else if(layEvent === 'mydel'){ //删除我发送的日志
	        	mydel(data);
	        }else if(layEvent === 'send'){ //发送我撤回的日志
	        	send(data);
	        }
	    });
	}
	
	// 发日志
	$("body").on("click", "#publish", function(){
    	_openNewWindows({
			url: "../../tpl/jobdiaryMySend/jobdiaryMySendPublish.html", 
			title: "发表日志",
			pageId: "jobdiaryMySendPublish",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1,time: 2000});
                	loadMySendTable();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
                }
			}});
    });
	
	// 撤回日志
	function repeal(data){
		layer.confirm('确认撤回选中数据吗？', { icon: 3, title: '撤回操作' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url:reqBasePath + "diary006", params: {rowId: data.id}, type: 'json', callback: function(json){
    			if(json.returnCode == 0){
    				winui.window.msg("撤回成功", {icon: 1,time: 2000});
    				loadMySendTable();
    			}else{
    				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
    			}
    		}});
		});
	}
	
	// 删除我发出的日志
	function mydel(data){
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
			layer.close(index);
            AjaxPostUtil.request({url:reqBasePath + "diary015", params: {rowId: data.id}, type: 'json', callback: function(json){
    			if(json.returnCode == 0){
    				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1,time: 2000});
    				loadMySendTable();
    			}else{
    				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
    			}
    		}});
		});
	}
	
	// 我发出的日志详情
	function reading(data){
		rowId = data.id;
		if(data.diaryType == 1){
			// 日报
			_openNewWindows({
				url: "../../tpl/jobdiaryMySend/jobdiaryMySendDayDetails.html", 
				title: "日志内容",
				pageId: "jobdiaryMySendDayDetails",
				area: ['90vw', '90vh'],
				callBack: function(refreshCode){
				}});
		}else if(data.diaryType == 2){
			// 周报
			_openNewWindows({
				url: "../../tpl/jobdiaryMySend/jobdiaryMySendWeekDetails.html", 
				title: "日志内容",
				pageId: "jobdiaryMySendWeekDetails",
				area: ['90vw', '90vh'],
				callBack: function(refreshCode){
				}});
		}else if(data.diaryType == 3){
			// 周报
			_openNewWindows({
				url: "../../tpl/jobdiaryMySend/jobdiaryMySendMonthDetails.html", 
				title: "日志内容",
				pageId: "jobdiaryMySendMonthDetails",
				area: ['90vw', '90vh'],
				callBack: function(refreshCode){
				}});
		}
	}
	
	// 发送撤回的日志
	function send(data){
		rowId = data.id;
		if(data.diaryType == 1){
			// 日报
			_openNewWindows({
				url: "../../tpl/jobdiaryMySend/jobdiaryMySendDayEdit.html", 
				title: "编辑日报",
				pageId: "jobdiaryMySendDayEdit",
				area: ['90vw', '90vh'],
				callBack: function(refreshCode){
					if (refreshCode == '0') {
	                	winui.window.msg("发送成功", {icon: 1,time: 2000});
	                	loadMySendTable();
	                } else if (refreshCode == '-9999') {
	                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
	                }
				}});
		}else if(data.diaryType == 2){
			// 周报
			_openNewWindows({
				url: "../../tpl/jobdiaryMySend/jobdiaryMySendWeekEdit.html", 
				title: "编辑周报",
				pageId: "jobdiaryMySendWeekEdit",
				area: ['90vw', '90vh'],
				callBack: function(refreshCode){
					if (refreshCode == '0') {
	                	winui.window.msg("发送成功", {icon: 1,time: 2000});
	                	loadMySendTable();
	                } else if (refreshCode == '-9999') {
	                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
	                }
				}});
		}else if(data.diaryType == 3){
			// 月报
			_openNewWindows({
				url: "../../tpl/jobdiaryMySend/jobdiaryMySendMonthEdit.html", 
				title: "编辑月报",
				pageId: "jobdiaryMySendMonthEdit",
				area: ['90vw', '90vh'],
				callBack: function(refreshCode){
					if (refreshCode == '0') {
	                	winui.window.msg("发送成功", {icon: 1,time: 2000});
	                	loadMySendTable();
	                } else if (refreshCode == '-9999') {
	                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
	                }
				}});
		}
	}
	
    // 我发出的日志刷新
    $("body").on("click", "#reloadTable2", function(){
        loadMySendTable();
    });
	
	// '我发出的'页面的点击搜索
	$("body").on("click", "#searchMySend", function(){
    	table.reload("mysendTable", {page: {curr: 1}, where: getTableParams()});
    });
    
    // 加载'我发出的'列表
    function loadMySendTable(){
    	table.reload("mysendTable", {where: getTableParams()});
    }
    
    function getTableParams(){
    	var startTime = "", endTime = "";
    	if(!isNull($("#createTime").val())){
    		startTime = $("#createTime").val().split('~')[0].trim() + ' 00:00:00';
    		endTime = $("#createTime").val().split('~')[1].trim() + ' 23:59:59';
    	}
    	return {
    		firstTime: startTime,
    		lastTime: endTime,
    		jobTitle: $("#jobTitle").val(),
    		diaryType: $("#myDiaryType").val()
    	};
    }
    	
    exports('jobdiaryMySendList', {});
});
