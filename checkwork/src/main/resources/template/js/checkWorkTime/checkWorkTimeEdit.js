
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
	    	laydate = layui.laydate;
	    var type = 1;
	    var restStartTime, restEndTime;
	    
	    showGrid({
		 	id: "showForm",
		 	url: flowableBasePath + "checkworktime003",
		 	params: {rowId: parent.rowId},
		 	pagination: false,
		 	method: "GET",
		 	template: $("#beanTemplate").html(),
		 	ajaxSendAfter:function (json) {
		 		type = json.bean.type;
		 		if(type == 1){
					checkWorkUtil.resetSingleBreak();
		    	} else if (type == 2){
					checkWorkUtil.resetWeekend();
		    	} else if (type == 3){
					checkWorkUtil.resetSingleAndDoubleBreak();
		    	} else if (type == 4){
					checkWorkUtil.resetCustomizeDay(json.bean.days);
		    	}
		 		$("input:radio[name=type][value=" + type + "]").attr("checked", true);
		 		$("input:radio[name=state][value=" + json.bean.state + "]").attr("checked", true);
		 		// 工作时间
		 		var startTime = laydate.render({
		 			elem: '#startTime',
		 			format: 'HH:mm',
		 			type: 'timeminute',
		 			minutesinterval: 30,
					max: getYMDFormatDate() + ' ' + json.bean.endTime + ':00',
					btns: ['confirm'],
		 			done:function(value, date){
		 				endTime.config.min = {
		    	    		year: date.year,
		        	    	month: date.month - 1,//关键
		                    date: date.date,
		                    hours: date.hours,
		                    minutes: date.minutes,
		                    seconds: date.seconds
		 	    	    };
		 				// 设置作息时间
						setRestTime(date, 'min');
		 	    	}
		 		});
			    var endTime = laydate.render({
		 			elem: '#endTime',
		 			format: 'HH:mm',
		 			type: 'timeminute',
		 			minutesinterval: 30,
					min: getYMDFormatDate() + ' ' + json.bean.startTime + ':00',
					btns: ['confirm'],
		 			done:function(value, date){
		 				startTime.config.max = {
		    	    		year: date.year,
		        	    	month: date.month - 1,//关键
		                    date: date.date,
		                    hours: date.hours,
		                    minutes: date.minutes,
		                    seconds: date.seconds
		 	    	    };
		 				// 设置作息时间
						setRestTime(date, 'max');
		 	    	}
		 		});

			    // 作息时间
				restStartTime = laydate.render({
					elem: '#restStartTime',
					format: 'HH:mm',
					type: 'timeminute',
					minutesinterval: 5,
					min: getYMDFormatDate() + ' ' + json.bean.startTime + ':00',
					max: getYMDFormatDate() + ' ' + json.bean.endTime + ':00',
					btns: ['confirm'],
					done:function(value, date){
						restEndTime.config.min = {
							year: date.year,
							month: date.month - 1,//关键
							date: date.date,
							hours: date.hours,
							minutes: date.minutes,
							seconds: date.seconds
						};
					}
				});
				restEndTime = laydate.render({
					elem: '#restEndTime',
					format: 'HH:mm',
					type: 'timeminute',
					minutesinterval: 5,
					min: getYMDFormatDate() + ' ' + json.bean.startTime + ':00',
					max: getYMDFormatDate() + ' ' + json.bean.endTime + ':00',
					btns: ['confirm'],
					done:function(value, date){
						restStartTime.config.max = {
							year: date.year,
							month: date.month - 1,//关键
							date: date.date,
							hours: date.hours,
							minutes: date.minutes,
							seconds: date.seconds
						};
					}
				});
		 		
		 		form.on('radio(type)', function (data) {
		 			type = data.value;
			    	if(type == 1){
						checkWorkUtil.resetSingleBreak();
			    	} else if (type == 2){
						checkWorkUtil.resetWeekend();
			    	} else if (type == 3){
						checkWorkUtil.resetSingleAndDoubleBreak();
			    	} else if (type == 4){
			    		resetCustomize();
			    	}
		        });
		 		
		        matchingLanguage();
		 		form.render();
		 	    form.on('submit(formEditBean)', function (data) {
		 	        if (winui.verifyForm(data.elem)) {
		 	        	var weekDay = getWeekDay();
		 	        	if(isNull(weekDay) || weekDay.length == 0){
		 	        		winui.window.msg('请选择工作日', {icon: 2, time: 2000});
		 	        		return false;
		 	        	}
		 	        	var params = {
		 	        		rowId: parent.rowId,
		 	        		title: $("#title").val(),
		 	        		startTime: $("#startTime").val(),
		 	        		endTime: $("#endTime").val(),
							restStartTime: $("#restStartTime").val(),
							restEndTime: $("#restEndTime").val(),
		 	        		type: $("input[name='type']:checked").val(),
		 	        		state: $("input[name='state']:checked").val(),
		 	        		weekDay: JSON.stringify(weekDay)
		 	        	};
		 	        	AjaxPostUtil.request({url: flowableBasePath + "checkworktime004", params: params, type: 'json', method: "PUT", callback: function (json) {
							parent.layer.close(index);
							parent.refreshCode = '0';
		 	        	}});
		 	        }
		 	        return false;
		 	    });
		 	}
		});

		/**
		 * 设置作息时间范围
		 * @param date 日期
		 * @param type 日期范围类型，min:最小值，max:最大值
		 */
	    function setRestTime(date, type){
			$("#restStartTime").val("");
			$("#restEndTime").val("");
			restStartTime.config[type] = {
				year: date.year,
				month: date.month - 1,//关键
				date: date.date,
				hours: date.hours,
				minutes: date.minutes,
				seconds: date.seconds
			};
			restEndTime.config[type] = {
				year: date.year,
				month: date.month - 1,//关键
				date: date.date,
				hours: date.hours,
				minutes: date.minutes,
				seconds: date.seconds
			};
		}
		
		// 自定义类型可以设置
 	    $("body").on("click", ".weekDay", function() {
 	    	if(type == 4){
		    	var clas = getArrIndexOfPointStr(checkWorkTimeColor, $(this).attr("class"));
		    	$(this).removeClass(clas);
		    	$(this).addClass(getColor(clas));
 	    	}
	    });
 	    
 	    function getColor(nowColor){
 	    	var index = checkWorkTimeColor.indexOf(nowColor);
 	    	if(index == (checkWorkTimeColor.length - 1)){
 	    		return checkWorkTimeColor[0];
 	    	} else {
 	    		return checkWorkTimeColor[index + 1];
 	    	}
 	    }
 	    
 	    function getWeekDay(){
 	    	if(type == 1){
 	    		return getSingleBreak();
 	    	} else if (type == 2){
 	    		return getWeekend();
 	    	} else if (type == 3){
 	    		return getSingleAndDoubleBreak();
 	    	} else if (type == 4){
 	    		return getCustomize();
 	    	}
 	    	return null;
 	    }
 	    
 	    // 获取单休的每周上班日
 	    function getSingleBreak(){
 	    	var result = new Array();
 	    	for(var i = 0; i < 6; i++){
 	    		result.push({
 	    			day: (i + 1),
 	    			type: 1
 	    		});
 	    	}
 	    	return result;
 	    }
 	    
 	    // 获取双休的每周上班日
 	    function getWeekend(){
 	    	var result = new Array();
 	    	for(var i = 0; i < 5; i++){
 	    		result.push({
 	    			day: (i + 1),
 	    			type: 1
 	    		});
 	    	}
 	    	return result;
 	    }
 	    
 	    // 获取单双休的每周上班日
 	    function getSingleAndDoubleBreak(){
 	    	var result = new Array();
 	    	for(var i = 0; i < 5; i++){
 	    		result.push({
 	    			day: (i + 1),
 	    			type: 1
 	    		});
 	    	}
 	    	result.push({
    			day: 6,
    			type: 2
    		});
 	    	return result;
 	    }
 	    
 	    // 获取自定义的每周的上班日
 	    function getCustomize(){
 	    	var result = new Array();
 	    	$.each($(".weekDay"), function(i, item) {
 	    		var clas = getArrIndexOfPointStr(checkWorkTimeColor, $(item).attr("class"));
    			if('layui-bg-blue' == clas){
    				result.push({
	 	    			day: $(item).attr("value"),
	 	    			type: 1
	 	    		});
    			} else if ('layui-bg-orange' == clas){
    				result.push({
	 	    			day: $(item).attr("value"),
	 	    			type: 2
	 	    		});
    			}
 	    	});
 	    	return result;
 	    }
 	    
	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	});
});