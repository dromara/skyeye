
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

	// 考勤日期选取时间段表格
	laydate.render({
		elem: '#checkDate',
		range: '~'
	});

	// 获取当前登陆人的考勤班次
	checkWorkUtil.getCurrentUserCheckWorkTimeList(function (json) {
		$("#checkTime").html(getDataUseHandlebars($("#workTimeTemplate").html(), json));
		form.render('select');
		loadMyCheckAttend();
	});

	// 我的考勤统计列表
	function loadMyCheckAttend(){
		table.render({
		    id: 'messageTable',
		    elem: '#messageTable',
		    method: 'post',
		    url: flowableBasePath + 'checkwork003',
		    where: getTableParams(),
		    even: true,
		    page: true,
		    limits: getLimits(),
	    	limit: getLimit(),
		    cols: [[
		        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
		        { field: 'userName', title: '姓名', align: 'left', width: 100},
		        { field: 'checkDate', title: '考勤日期', align: 'center', width: 100},
		        { field: 'title', title: '班次', align: 'left', width: 270, templet: function (d) {
		        	if(isNull(d.timeId)){
		        		return "";
		        	}
		        	if(!isNull(d.startTime) && !isNull(d.endTime)){
						return '<a lay-event="details" class="notice-title-click">' + d.title + '[' + d.startTime + '~' + d.endTime + ']' + '</a>';
					} else {
						return '<a lay-event="details" class="notice-title-click">' + d.title + '</a>';
					}
		        }},
		        { title: '星期几', align: 'center', width: 80, templet: function (d) {
		        	return getMyDay(new Date(d.checkDate));
		        }},
		        { field: 'clockIn', title: '上班打卡时间', align: 'center', width: 120, templet: function (d) {
		        	var time = d.clockIn;
		        	if(d.clockInState == '0'){
		        		time += "  ( <span class='state-down'>系统填充</span> )";
		        	}else if(d.clockInState == '1'){
		        		time += "  ( <span class='state-up'>正常</span> )";
		        	}else if(d.clockInState == '2'){
		        		if(d.appealInState == '1' && d.appealInType == '2'){
		        			time += "  ( <span class='state-down'>迟到</span> )" + "<span class='state-up'> ( 申诉成功 ) </span>";
		        		} else {
		        			time += "  ( <span class='state-down'>迟到</span> )";
		        		}
		        	}else if(d.clockInState == '3'){
		        		time += "  ( <span class='state-down'>未打卡</span> )";
		        	} else {
		        		return "";
		        	}
		        	return time;
		        }},
		        { field: 'clockOut', title: '下班打卡时间', align: 'center', width: 120, templet: function (d) {
		        	var time = d.clockOut;
		        	if(d.clockOutState == '0'){
		        		time += "  ( <span class='state-down'>系统填充</span> )";
		        	}else if(d.clockOutState == '1'){
		        		time += "  ( <span class='state-up'>正常</span> )";
		        	}else if(d.clockOutState == '2'){
		        		if(d.appealOutState == '1' && d.appealOutType == '3'){
		        			time += "  ( <span class='state-down'>早退</span> )" + "<span class='state-up'> ( 申诉成功 ) </span>";
		        		} else {
		        			time += "  ( <span class='state-down'>早退</span> )";
		        		}
		        	}else if(d.clockOutState == '3'){
		        		time += "  ( <span class='state-down'>未打卡</span> )";
		        	} else {
		        		return "";
		        	}
		        	return time;
		        }},
		        { field: 'workHours', title: '工时', align: 'left', width: 80},
		        { field: 'state', title: '考勤状态', align: 'left', width: 100, templet: function (d) {
		        	if(d.state == '0'){
		        		return "<span class='state-up'>早卡</span>";
		        	}else if(d.state == '1'){
		        		return "<span class='state-up'>全勤</span>";
		        	}else if(d.state == '2'){
		        		if(d.appealAllState == '1' && d.appealAllType == '1'){
		        			return "<span class='state-down'>缺勤</span>" + "<span class='state-up'> ( 申诉成功 ) </span>";
		        		} else {
		        			return "<span class='state-down'>缺勤</span>";
		        		}
		        	}else if(d.state == '3'){
		        		return "<span class='state-down'>工时不足</span>";
		        	}else if(d.state == '4'){
		        		if(d.appealAllState == '1' && d.appealAllType == '1'){
		        			return "<span class='state-down'>缺早卡</span>" + "<span class='state-up'> ( 申诉成功 ) </span>";
		        		} else {
		        			return "<span class='state-down'>缺早卡</span>";
		        		}
		        	}else if(d.state == '5'){
		        		if(d.appealAllState == '1' && d.appealAllType == '1'){
		        			return "<span class='state-down'>缺晚卡</span>" + "<span class='state-up'> ( 申诉成功 ) </span>";
		        		} else {
		        			return "<span class='state-down'>缺晚卡</span>";
		        		}
		        	} else {
		        		return "参数错误";
		        	}
		        }}
		    ]],
		    done: function(){
		    	matchingLanguage();
		    }
		});
		
		table.on('tool(messageTable)', function (obj) {
	        var data = obj.data;
	        var layEvent = obj.event;
	        if (layEvent === 'details') { //详情
	        	details(data);
	        }
	    });
	}
	
	// 详情
	function details(data){
		rowId = data.timeId;
		_openNewWindows({
			url: "../../tpl/checkWorkTime/checkWorkTimeDetails.html", 
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "checkWorkTimeDetails",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode) {
			}});
	}
	
	form.render();
	
	// 个人考勤情况导出
    $("body").on("click", "#download", function() {
        postDownLoadFile({
            url : flowableBasePath + 'checkwork017',
            params: getTableParams(),
            method : 'post'
        });
    });
	
	// 搜索我的考勤统计
	$("body").on("click", "#searchForm", function() {
    	table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
	});
	
	// 刷新我的考勤信息
    $("body").on("click", "#reloadMyDkTable", function() {
    	loadTable();
    });
    
    function loadTable(){
    	table.reloadData("messageTable", {where: getTableParams()});
    }
    
    function getTableParams(){
    	var startTime = "", endTime = "";
    	if(!isNull($("#checkDate").val())){
    		startTime = $("#checkDate").val().split('~')[0].trim();
    		endTime = $("#checkDate").val().split('~')[1].trim();
    	}
    	return {
    		firstTime: startTime,
    		lastTime: endTime,
    		state: $("#state").val(),
    		clockInState: $("#clockInState").val(),
    		clockOutState: $("#clockOutState").val(),
    		timeId: $("#checkTime").val()
    	};
    }
    
    exports('checkWorkMyAttendanceStatistics', {});
});
