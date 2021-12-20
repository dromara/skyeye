
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui','dragula', 'dropdown', 'fullcalendar', 'jqueryUI', 'laydate', 'form'], function (exports) {
	var $ = layui.jquery,
		form = layui.form,
		laydate = layui.laydate;
	winui.renderColor();

	showGrid({
		id: "checkTime",
		url: reqBasePath + "checkworktime007",
		params: {},
		pagination: false,
		template: $("#workTimeTemplate").html(),
		ajaxSendLoadBefore: function(hdb){
		},
		ajaxSendAfter:function(json){
			initUserSchedule();
			form.on('select(checkTime)', function (data) {
				var value = data.value;
				calendar.fullCalendar('refetchEvents');
			});
		}
	});
	/**
	 * 初始化日程
	 */
	var calendar;
	function initUserSchedule(){
		layui.link(basePath + '../../lib/layui/lay/modules/jqueryui/jquery-ui.min.css');
		var scheduleStartTime = laydate.render({
 			elem: '#scheduleStartTime',
 			format: 'yyyy-MM-dd',
 			theme: 'grid',
			value: getYMDFormatDate(),
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
		var day30 = DateAdd("d", 30, new Date());
		var scheduleEndTime = laydate.render({
 			elem: '#scheduleEndTime',
 			format: 'yyyy-MM-dd',
 			theme: 'grid',
			value: day30.format("yyyy-MM-dd"),
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
		
		form.render();

		calendar = $('#scheduleCalendar').fullCalendar({
			theme: true,
			header: {
				left: 'prev,next today',
				center: 'title',
				right: 'month,agendaDay'
			},
			buttonText: {    											//各按钮的显示文本信息
				month: '月',
				agendaDay: '日',
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
			select: function(start, end, mation, jsEvent) {//选中事件
				var allDay = false;
				if(jsEvent.type == "month"){
					allDay = true;
				}
				if(allDay){
					if(new Date(start._d.format("yyyy-MM-dd")) < new Date(getYMDFormatDate().replace("-", "/").replace("-", "/"))){
						winui.window.msg('不能创建当前时间之前的日程。', { shift: 6, skin: 'msg-skin-message'});
						return false;
					}
					childParams = {
						start: start._d,
						end: DateAdd("d", -1, end._d),
						allDay: allDay
					};
				}else{
					if(new Date(start.format()) < new Date(getFormatDate().replace("-", "/").replace("-", "/"))){
						winui.window.msg('不能创建当前时间之前的日程。', { shift: 6, skin: 'msg-skin-message'});
						return false;
					}
					childParams = {
						start: new Date(start.format()),
						end: new Date(end.format()),
						allDay: allDay
					};
				}
				_openNewWindows({
					url: "../../tpl/index/addSchedule.html",
					title: "新增日程",
					pageId: "addScheduleMation",
					area: ['70vw', '80vh'],
					skin: 'add-schedule-mation',
					callBack: function(refreshCode){
						if(refreshCode == '0') {
							winui.window.msg('日程创建成功', { shift: 6, skin: 'msg-skin-message'});
							joinCalendar(childParams);
							joinTodaySchedule(childParams);
						}
					}});
			},
			eventDrop: function(event, dayDelta, minuteDelta, allDay, revertFunc) {//拖动事件
				if(allDay){
					if(new Date(event.start.format("yyyy-MM-dd")) < new Date(getYMDFormatDate().replace("-", "/").replace("-", "/"))){
						winui.window.msg('即将重置的日期不能早于当前日期。', { shift: 6, skin: 'msg-skin-message'});
						revertFunc();
						return false;
					}
				}else{
					if(event.start < new Date(getFormatDate().replace("-", "/").replace("-", "/"))){
						winui.window.msg('即将重置的日期不能早于当前日期。', { shift: 6, skin: 'msg-skin-message'});
						revertFunc();
						return false;
					}
				}
				$('div[rowid="' + event.id + '"]').parent().remove();
				var params = {
					scheduleTitle: event.title,
					scheduleStartTime: event.start.format("yyyy-MM-dd hh:mm:ss"),
					scheduleEndTime: event.end.format("yyyy-MM-dd hh:mm:ss"),
					id: event.id,
					rowId: event.id
				};
				AjaxPostUtil.request({url:reqBasePath + "syseveschedule005", params:params, type:'json', callback:function(json){
					if(json.returnCode == 0){
						joinTodaySchedule(params);
					}else{
						revertFunc();
						winui.window.msg(json.returnMessage, { shift: 6, skin: 'msg-skin-message'});
					}
				}});
			},
			dayClick: function(date, jsEvent, view) {//点击日历某天时候触发
			},
			eventClick: function(calEvent, jsEvent, view) {//点击日历中某个事情时候触发     event: 包含了日程的信息（例如：日期，标题）；jsEvent: 是原生的javascript事件，包含“点击坐标”之类的信息；view： 是当前的view object；在 eventClick 回调函数内部，this 是当前点击那个日程的
				loadScheduleDetails(calEvent.id);
			},
			eventMouseover: function(event, jsEvent, view){//鼠标移入事件
			},
			events: function(start, end, timezone, callback){
				AjaxPostUtil.request({url:reqBasePath + "syseveschedule002", params:{yearMonth: start._d.format("yyyy-MM"), checkWorkId: $("#checkTime").val()}, type:'json', callback:function(json){
					if(json.returnCode == 0){
						callback(json.rows);
					}else{
						winui.window.msg(json.returnMessage, { shift: 6, skin: 'msg-skin-message'});
					}
				}});
			}
			});

		//获取今日日程
		showGrid({
			id: "schedule-list",
			url: reqBasePath + "syseveschedule003",
			params: {},
			pagination: false,
			method: "GET",
			template: getFileContent('tpl/index/todaySchedule.tpl'),
			ajaxSendLoadBefore: function(hdb){
				hdb.registerHelper('compare1', function(v1, options) {
					if(v1 != '3'){
						return options.inverse(this);
					}else{
						return options.fn(this);
					}
				});
			},
			options: {},
			ajaxSendAfter:function(json){
			}
		});
		matchingLanguage();
	}
	
	//新增日程
	$('#addSchedule').on('click', function () {
		childParams = "";
		_openNewWindows({
			url: "../../tpl/index/addSchedule.html", 
			title: "新增日程",
			pageId: "addScheduleMation",
			area: ['70vw', '80vh'],
			skin: 'add-schedule-mation',
			callBack: function(refreshCode){
                if(refreshCode == '0') {
                	winui.window.msg('日程创建成功', { shift: 6, skin: 'msg-skin-message'});
                	joinFolderList(childParams);
                	joinCalendar(childParams);
                	joinTodaySchedule(childParams);
                }
			}});
	});
	
	// 日程点击事件查看详情
	$("#schedule-list").on("click", ".layui-timeline-content", function(e){
		loadScheduleDetails($(this).attr("rowid"));
	});

	// 加载日程详情
	function loadScheduleDetails(id){
		AjaxPostUtil.request({url:reqBasePath + "syseveschedule006", params:{rowId: id}, type:'json', callback:function(json){
			if(json.returnCode == 0){
				json.bean.scheduleRemarks = stringManipulation.textAreaShow(json.bean.scheduleRemarks);
				showDataUseHandlebars("schedule-detail", getFileContent('tpl/index/scheduleDetail.tpl'), json);
			}else{
				winui.window.msg(json.returnMessage, {shift: 6, skin: 'msg-skin-message'});
			}
		}});
	}

	//删除日程
	$("#schedule-list").on("click", ".schrdule-del", function(e){
		var id = $(this).parent().parent().attr("rowid");
		layer.confirm('确认删除该日程吗？', {id: 'schrdule-del-confim', icon: 3, title: '删除日程', skin: 'msg-skin-message', success: function(layero, index){
			var times = $("#schrdule-del-confim").parent().attr("times");
			var zIndex = $("#schrdule-del-confim").parent().css("z-index");
			$("#layui-layer-shade" + times).css({'z-index': zIndex});
		}}, function (index) {
			layer.close(index);
			AjaxPostUtil.request({url:reqBasePath + "syseveschedule007", params:{rowId: id}, type:'json', callback:function(json){
	   			if(json.returnCode == 0){
	   				$('div[rowid="' + id + '"]').parent().remove();
	   				calendar.fullCalendar('removeEvents', [id]);
		   		}else{
	   				winui.window.msg(json.returnMessage, {shift: 6, skin: 'msg-skin-message'});
	   			}
	   		}});
		});
	});

	//加入日程日历
	function joinCalendar(bean){
		var eventObj = new Object();
		//构造每一个日历记录
		eventObj.id = bean.id;
		eventObj.title = bean.scheduleTitle;
		eventObj.start = new Date(bean.scheduleStartTime);
		eventObj.end = new Date(bean.scheduleEndTime);
        eventObj.showBg = '1';
		if(bean.type == '1'){//个人
			eventObj.backgroundColor = '#63B8FF';
		}else if(bean.type == '2'){//工作
			eventObj.backgroundColor = '#CD69C9';
		}else if(bean.type == '3'){//节假日
			eventObj.backgroundColor = '#54FF9F';
		}else if(bean.type == '4'){//生日
			eventObj.backgroundColor = '#FF0000';
		}else if(bean.type == '5'){//自定义
			eventObj.backgroundColor = '#ADADAD';
		}
		var arr = [];
		eventObj.className = arr;
		if(bean.allDay == '1'){
			eventObj.allDay = true;
		}else{
			eventObj.allDay = false;
		}
		calendar.fullCalendar('renderEvent', eventObj, true);
	}
	
	//加入今日日程
	function joinTodaySchedule(bean){
		var day = bean.scheduleStartTime.split(' ')[0];
		var nowDay = getYMDFormatDate();
		if(day == nowDay){//新增的日程输入今天的日程
			if($("#schedule-list").find(".noMation").length > 0){//如果今天没有日程，移除‘暂无日程’四个字
				$("#schedule-list").find(".noMation").remove();
			}
			var li = $(".schedule-list ul").find("li");
			var liIndex;
			$.each(li, function(i, item){
				var _thisTime = $(item).find('h3[class="layui-timeline-title"]').html().split('~')[0];
				if(!compare_hms(bean.scheduleStartTime, day + " " + _thisTime + ':00') && isNull(liIndex)){
					liIndex = i - 1;
					return false;
				}
			});
			if(isNull(liIndex) && li.length > 0 && liIndex != 0)
				liIndex = li.length - 1;
			var itemHtml = '<li class="layui-timeline-item">'
							+ '<i class="layui-icon layui-timeline-axis">&#xe63f;</i>'
							+ '<div class="layui-timeline-content layui-text" rowid="' + bean.id + '">'
								+ '<h3 class="layui-timeline-title">' + hms2hm(bean.scheduleStartTime) + '~' + hms2hm(bean.scheduleEndTime) + '</h3>'
								+ '<p>' + bean.scheduleTitle + '<i class="fa fa-trash schrdule-del" title="删除日程"></i></p>'
							+ '</div>'
						+ '</li>';
			if(!isNull(liIndex) || liIndex == 0)
				if(liIndex < 0)
					$(".schedule-list ul").find("li").eq(0).before(itemHtml);
				else
					$(".schedule-list ul").find("li").eq(liIndex).after(itemHtml);
			else
				$(".schedule-list").html('<ul class="layui-timeline">' + itemHtml + '</ul>');
		}
	}

    exports('mySchedule', {});
});
