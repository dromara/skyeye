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
	
	var params = parent.reportparams;
	
	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: flowableBasePath + 'checkwork018',
	    where: params,
	    even: true,
	    page: true,
	    limits: [8, 16, 24, 32, 40, 48, 56],
	    limit: 8,
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
	        { field: 'checkDate', title: '考勤日期', align: 'center', width: 120},
	        { title: '星期几', align: 'center', width: 100, templet: function (d) {
	        	return getMyDay(new Date(d.checkDate));
	        }},
	        { field: 'clockIn', title: '上班打卡时间', align: 'center', width: 220, templet: function (d) {
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
	        { field: 'clockOut', title: '下班打卡时间', align: 'center', width: 220, templet: function (d) {
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
	        { field: 'workHours', title: '工时', align: 'center', width: 110},
	        { field: 'state', title: '考勤状态', align: 'center', width: 160, templet: function (d) {
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
	
	form.render();
	
    exports('reportdetail', {});
});
