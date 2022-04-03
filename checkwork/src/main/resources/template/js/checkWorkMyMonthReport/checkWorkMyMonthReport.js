
// 节假日，工作日，加班，请假，出差
var topLeftDayType = ["3", "6", "7", "8", "9"];

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'laydate', 'form', 'fullcalendar', 'jqueryUI', 'clock'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		table = layui.table,
		form = layui.form;
	
	// 法定下班时间
	var clockOut;
	// 今天的日期类型  1.按班次考勤的日期；2.按加班日考勤的日期
	var dayType;
	// 是否已经加载今日打卡状态信息
	var checkWorkDescShow = false;
	var calendar;
	layui.link(basePath + '../../lib/layui/lay/modules/jqueryui/jquery-ui.min.css');
	
	// 上班打卡
	authBtn('1597534851847');
	// 下班打卡
	authBtn('1597534860411');
	
	var checkTimeList = [];

	// 加载我的考勤班次
	showGrid({
     	id: "checkTime",
     	url: flowableBasePath + "checkworktime007",
     	params: {},
     	pagination: false,
     	template: $("#workTimeTemplate").html(),
     	ajaxSendLoadBefore: function(hdb){
     	},
     	ajaxSendAfter:function(json){
     		checkTimeList = json.rows;
     		matchingLanguage();
     		form.render('select');
     		// 加载工作日
     		loadWorkDays();
     		// 加载考勤打卡日历
     		loadCheckWorkRl();
     	}
    });
    
    form.on('select(checkTime)', function (data) {
 		// 重新加载今日打卡状态信息
 		initIsCheck(function(){
	 		// 重新加载考勤打卡历史
			calendar.fullCalendar("refetchEvents");
			// 加载工作日
			loadWorkDays();
 		});
    });
    
    // 加载工作日
    function loadWorkDays(){
		var workDayation = getInPoingArr(checkTimeList, "timeId", $("#checkTime").val());
		if(workDayation != null){
			var type = workDayation.type;
			if(type == 1){
				resetSingleBreak();
			}else if(type == 2){
				resetWeekend();
			}else if(type == 3){
				resetSingleAndDoubleBreak();
			}else if(type == 4){
				resetCustomizeDay(workDayation.days);
			}
		}
    }

	// 加载考勤打卡日历
	function loadCheckWorkRl(){
		calendar = $('#scheduleCalendar').fullCalendar({
            theme: true,
            header: {
                left: 'prev,next today',
                center: 'title',
                right: 'month'
            },
			buttonText: {    											//各按钮的显示文本信息
				month: '月',
			},
			slotLabelFormat: "H(:mm)a",    							//日期视图左边那一列显示的每一格日期时间格式
			firstDay: 1, //设置一周中显示的第一天是哪天，周日是0，周一是1，类推
			allDayText: "全天",            //自定义全天视图的名称
			monthNames: ["一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"], //月份自定义命名
			monthNamesShort: ["一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"], //月份缩略命名（英语比较实用：全称January可设置缩略为Jan）
			dayNames: ["周日", "周一", "周二", "周三", "周四", "周五", "周六"],       //同理monthNames
			dayNamesShort: ["周日", "周一", "周二", "周三", "周四", "周五", "周六"],  //同理monthNamesShort
			weekNumberTitle: "周",         //周的国际化,默认为"W"
			eventLimitText: "更多",       //当一块区域内容太多以"+2 more"格式显示时，这个more的名称自定义（应该与eventLimit: true一并用）
			dayPopoverFormat: "YYYY年M月d日", //点开"+2 more"弹出的小窗口标题，与eventLimitClick可以结合用
			editable: true,
			weekNumbers: true,     //是否在视图左边显示这是第多少周，默认false
			selectable: true,//设置日程表的天和时间槽是否可以点击选中和拖拽
			selectHelper: true,//设置是否在用户拖拽事件的时候绘制占位符
            // 初始化日程表
            events: function(start, end, timezone, callback){
               loadThisMonthHis(callback, start);
            }
        });
    }
    
    function loadThisMonthHis(callback, start){
        AjaxPostUtil.request({url: flowableBasePath + "checkwork014", params:{monthMation: start._d.format("yyyy-MM"), timeId: $("#checkTime").val()}, type: 'json', callback: function(json){
            if(json.returnCode == 0){
                var event = [];
                if(!isNull(json.rows)){
                    $.each(json.rows, function(i, item) {
						event.push({
							title: ($.inArray(item.type.toString(), topLeftDayType) > -1) ? item.title : item.title + ":" + item.clockIn,
							start: item.start,
							end: item.end,
							backgroundColor: item.backgroundColor,
							allday: item.allday,
							showBg: item.showBg,
							editable: item.editable,
							id: item.id,
							className: item.className
						});
                   });
                }
				callback(event);
                if(!checkWorkDescShow)
            	    initIsCheck();
            }else{
                winui.window.msg(json.returnMessage, { shift: 6, skin: 'msg-skin-message'});
            }
        }});
    }
    
    form.render();
	
	// 判断显示打上班卡或者下班卡
    function initIsCheck(callBack){
    	checkWorkDescShow = true;
        AjaxPostUtil.request({url: flowableBasePath + "checkwork013", params: {timeId: $("#checkTime").val()}, type: 'json', callback: function(json){
            if(json.returnCode == 0){
            	clockOut = json.bean.clockOut;
				dayType = json.bean.type;
            	var s = "";
            	if(json.bean.type == 1){
            		s = getTipStrByTimeId(json);
				}else if(json.bean.type == 2){
					s = getTipStrByOverTime(json);
				}

                $("#checkWorkDescShow").html(s);
                // 控制打卡按钮的显示和隐藏
				showOrHideBtn(json);
                if(typeof(callBack) == "function") {
					callBack();
				}
            }else{
                winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
            }
        }});
    }

	/**
	 * 控制打卡按钮的显示和隐藏
	 * @param json
	 */
	function showOrHideBtn(json){
		// 判断今天是否在该考勤班次里（并且不是加班日），如果不在，则隐藏打卡按钮
		var checkWorkTimeDays = getInPoingArr(checkTimeList, 'timeId', $("#checkTime").val(), 'days');
		if(!judgeInPoingArr(checkWorkTimeDays, 'day', getThisWeekDay()) && json.bean.type == 1){
			$("#clockInBtn").hide();
			$("#clockOutBtn").hide();
		}else{
			if(json.bean.isCheck == "1"){
				// 显示早卡按钮
				$("#clockInBtn").show();
				$("#clockOutBtn").hide();
			}else if(json.bean.isCheck == '2'){
				// 显示晚卡按钮
				$("#clockInBtn").hide();
				$("#clockOutBtn").show();
			}else if(json.bean.isCheck == '3'){
				// 不显示按钮
				$("#clockInBtn").hide();
				$("#clockOutBtn").hide();
			}else if(json.bean.isCheck == '4'){
				// 不显示按钮
				$("#clockInBtn").hide();
				$("#clockOutBtn").hide();
			}
		}
	}

	/**
	 * 根据加班日计算打卡按钮是否隐藏时的提示语
	 *
	 * @param json
	 * @returns {string}
	 */
	function getTipStrByOverTime(json){
		var s = '1.（加班日上班时间为' + json.bean.clockIn + '，晚于该时间即为迟到。法定下班时间为' + json.bean.clockOut + '，早于该时间即为早退）;</br>';
		s += '2.下班后还未进行上班打卡的员工，则视为旷工，当天则不允许打卡，如果当天忘记打卡，系统不会结算该天的工作。';
		if(json.bean.isCheck == "1"){
			// 显示早卡按钮
			s = '您还未进行上班打卡</br></br>' + s;
		}else if(json.bean.isCheck == '2'){
			// 显示晚卡按钮
			s = '您还未进行下班打卡</br></br>' + s;
		}else if(json.bean.isCheck == '3'){
			// 不显示按钮
			s = '已下班(无法打卡)，您今日矿工一天哦</br></br>' + s;
		}else if(json.bean.isCheck == '4'){
			// 不显示按钮
			s = '您已完成今日的打卡任务</br></br>' + s;
		}
		return s;
	}

	/**
	 * 根据考勤班次计算打卡按钮是否隐藏时的提示语
	 *
	 * @param json
	 * @returns {string}
	 */
	function getTipStrByTimeId(json){
		var s = '1.（法定上班时间为' + json.bean.clockIn + '，晚于该时间即为迟到。法定下班时间为' + json.bean.clockOut + '，早于该时间即为早退）;</br>';
		s += '2.下班后还未进行上班打卡的员工，则视为旷工，当天则不允许打卡，如果当天忘记打卡，需第二天向领导进行申诉申请，并说明原因。';
		if(json.bean.isCheck == "1"){
			// 显示早卡按钮
			s = '您还未进行上班打卡</br></br>' + s;
		}else if(json.bean.isCheck == '2'){
			// 显示晚卡按钮
			s = '您还未进行下班打卡</br></br>' + s;
		}else if(json.bean.isCheck == '3'){
			// 不显示按钮
			s = '已下班(无法打卡)，您今日矿工一天哦</br></br>' + s;
		}else if(json.bean.isCheck == '4'){
			// 不显示按钮
			s = '您已完成今日的打卡任务</br></br>' + s;
		}
		return s;
	}
	
	// 上班打卡
	$("body").on("click", "#clockInBtn", function(){
		var timeId = $("#checkTime").val();
		if(dayType == 2){
			timeId = "-";
		}
		AjaxPostUtil.request({url: flowableBasePath + "checkwork001", params:{timeId: timeId}, type: 'json', callback: function(json){
			if(json.returnCode == 0){
				$("#clockInBtn").hide();
				calendar.fullCalendar('refetchEvents');
				winui.window.msg("上班打卡成功", {icon: 1,time: 2000});
			}else{
				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
			}
		}});
	});
	
	// 下班打卡
	$("body").on("click", "#clockOutBtn", function(){
		if(compare_HHmmss(clockOut, getHMSFormatDate())){
			var msg = '当前时间为<span class="state-down">' + getHMSFormatDate() +'</span>，确定现在打下班卡吗？';
			layer.confirm(msg, { icon: 3, title: '下班打卡' }, function (index) {
				checkOutRequest();
			});
		}else{
			checkOutRequest();
		}
	});
	
	// 下班打卡
	function checkOutRequest(){
		var timeId = $("#checkTime").val();
		if(dayType == 2){
			timeId = "-";
		}
		AjaxPostUtil.request({url: flowableBasePath + "checkwork002", params:{timeId: timeId}, type: 'json', callback: function(json){
			if(json.returnCode == 0){
				$("#clockOutBtn").hide();
				calendar.fullCalendar('refetchEvents');
				winui.window.msg("下班打卡成功", {icon: 1,time: 2000});
			}else{
				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
			}
		}});
	}

    exports('checkWorkMyMonthReport', {});
});
