layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'laydate'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
	    	form = layui.form,
	    	laydate = layui.laydate;
	    
	    var scheduleStartTime = laydate.render({
 			elem: '#scheduleStartTime', //指定元素
 			format: 'yyyy-MM-dd',
 			min: minDate(),
 			theme: 'grid',
 			done:function(value, date){
				scheduleEndTime.config.min = {    	    		
					year: date.year,
					month: date.month - 1,//关键
					date: date.date,
					hours: date.hours,
					minutes: date.minutes,
					seconds: date.seconds
				};
 	    	}
 		});
	    
	    var scheduleEndTime = laydate.render({
 			elem: '#scheduleEndTime', //指定元素
 			format: 'yyyy-MM-dd',
 			min: minDate(),
 			theme: 'grid',
 			done:function(value, date){
 				scheduleStartTime.config.max = {
    	    		year: date.year,
        	    	month: date.month - 1,//关键
                    date: date.date,
                    hours: date.hours,
                    minutes: date.minutes,
                    seconds: date.seconds
 	    	    }
 	    	}
 		});
	    
	    var scheduleDayTime = laydate.render({
 			elem: '#scheduleDayTime', //指定元素
 			format: 'yyyy-MM-dd',
 			min: minDate(),
 			theme: 'grid',
	    });
	    
	    var scheduleDayStartTime = laydate.render({
 			elem: '#scheduleDayStartTime', //指定元素
 			format: 'HH:mm',
 			min: minHourDate(),
 			type: 'timeminuteCompare',
 			minutesinterval: 30,
 			done:function(value, date){
 				scheduleDayEndTime.config.min = {
    	    		year: date.year,
        	    	month: date.month - 1,//关键
                    date: date.date,
                    hours: date.hours,
                    minutes: date.minutes,
                    seconds: date.seconds
 	    	    };
 	    	}
 		});
	    
	    var scheduleDayEndTime = laydate.render({
 			elem: '#scheduleDayEndTime', //指定元素
 			format: 'HH:mm',
 			min: minHourDate(),
 			type: 'timeminuteCompare',
 			minutesinterval: 30,
 			done:function(value, date){
 				scheduleDayStartTime.config.max = {
    	    		year: date.year,
        	    	month: date.month - 1,//关键
                    date: date.date,
                    hours: date.hours,
                    minutes: date.minutes,
                    seconds: date.seconds
 	    	    };
 	    	}
 		});
	    
	    //选中日程日历进行添加日程
	    if(!isNull(parent.childParams)){
	    	if(!parent.childParams.allDay){//不是全天
	    		$("#allDay").attr("checked", false);
	    		$("#allDay").val(false);
	    		$(".allDayIsTrue").hide();
 				$(".allDayIsFalse").show();
 				$("#scheduleDayTime").val(parent.childParams.start.format("yyyy-MM-dd"));
 				$("#scheduleDayStartTime").val(parent.childParams.start.format("hh:mm"));
 				$("#scheduleDayEndTime").val(parent.childParams.end.format("hh:mm"));
	    	} else {//是全天
	    		$("#scheduleStartTime").val(parent.childParams.start.format("yyyy-MM-dd"));
	    		$("#scheduleEndTime").val(parent.childParams.end.format("yyyy-MM-dd"));
	    	}
	    }
	    
	    matchingLanguage();
		form.render();
		
		//日程重要性
		form.on('radio(menuLevel)', function (data) {
        });
		
		//日程是否全天
 		form.on('switch(allDay)', function (data) {
 			//同步开关值
 			$(data.elem).val(data.elem.checked);
 			if(!data.elem.checked){//不是全天
 				$(".allDayIsTrue").hide();
 				$(".allDayIsFalse").show();
 			} else {//是全天
 				$(".allDayIsTrue").show();
 				$(".allDayIsFalse").hide();
 			}
 		});
 		
 		//日程类型
 		form.on('select(scheduleType)', function (data) {
 			if(data.value == '5'){
 				$("#scheduleTypeName").parent().parent().show();//自定义类型名称显示
 			} else {
 				$("#scheduleTypeName").parent().parent().hide();//自定义类型名称隐藏
 			}
        });
		
	    form.on('submit(formAddBean)', function (data) {
	        if (winui.verifyForm(data.elem)) {
	        	var params = {
        			scheduleTitle: $("#scheduleTitle").val(),
        			remarks: $("#remarks").val(),
        			allDay: '0',
        			scheduleStartTime: '',
        			scheduleEndTime: '',
        			remindType: $("#remindType").val(),
        			type: $("#scheduleType").val(),
        			typeName: $("#scheduleType option:selected").text(),
        			import: data.field.scheduleImport,
 	        	};
	        	if(data.field.allDay || data.field.allDay == 'true'){//全天
	        		if(isNull($("#scheduleStartTime").val())){
	        			layer.msg("请选择开始时间", {icon: 5, shift: 6});
	        			return false;
	        		}
	        		if(isNull($("#scheduleEndTime").val())){
	        			layer.msg('请选择结束时间', {icon: 5, shift: 6});
	        			return false;
	        		}
	        		params.allDay = '1';
	        		params.scheduleStartTime = $("#scheduleStartTime").val() + " 00:00:00";
	        		params.scheduleEndTime = $("#scheduleEndTime").val() + " 23:59:59";
	        	} else {
	        		if(isNull($("#scheduleDayTime").val())){
	        			layer.msg('请填写日程日期', {icon: 5, shift: 6});
	        			return false;
	        		}
	        		if(isNull($("#scheduleDayStartTime").val())){
	        			layer.msg('请填写日程开始时间', {icon: 5, shift: 6});
	        			return false;
	        		}
	        		if(isNull($("#scheduleDayEndTime").val())){
	        			layer.msg('请填写日程结束时间', {icon: 5, shift: 6});
	        			return false;
	        		}
	        		params.allDay = '0';
	        		params.scheduleStartTime = $("#scheduleDayTime").val() + " " + $("#scheduleDayStartTime").val() + ":00";
	        		params.scheduleEndTime = $("#scheduleDayTime").val() + " " + $("#scheduleDayEndTime").val() + ":00";
	        	}
	        	if(params.type == '5'){
	        		if(isNull($("#scheduleTypeName").val())){
	        			layer.msg('请填写日程类型', {icon: 5, shift: 6});
	        			return false;
	        		}
        			params.typeName = $("#scheduleTypeName").val();
	        	}
 	        	AjaxPostUtil.request({url:reqBasePath + "syseveschedule001", params:params, type: 'json', callback: function(json){
	 	   			if (json.returnCode == 0) {
	 	   				parent.childParams = json.bean;
		        		parent.layer.close(index);
		        		parent.refreshCode = '0';
	 	   			} else {
	 	   				layer.msg(json.returnMessage, {icon: 5, shift: 6});
	 	   			}
	 	   		}});
	        }
	        return false;
	    });
	    
	    // 设置最小可选的日期
	    function minDate(){
	        var now = new Date();
	        return now.getFullYear()+"-" + (now.getMonth()+1) + "-" + now.getDate();
	    }
	    
	    // 设置最小可选的小时日期
	    function minHourDate(){
	        var now = new Date();
	        return now.getHours() + ':' + now.getMinutes() + ':00';
	    }
	    
	    //取消
	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	});
	    
});