layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'laydate', 'form'], function (exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
	var $ = layui.$,
		form = layui.form,
		laydate = layui.laydate;

	var startTime = laydate.render({
		elem: '#startTime', //指定元素
		format: 'yyyy-MM-dd',
		min: minDate(),
		theme: 'grid',
		done:function(value, date){
			endTime.config.min = {
				year: date.year,
				month: date.month - 1,//关键
				date: date.date,
				hours: date.hours,
				minutes: date.minutes,
				seconds: date.seconds
			};
		}
	});

	var endTime = laydate.render({
		elem: '#endTime', //指定元素
		format: 'yyyy-MM-dd',
		min: minDate(),
		theme: 'grid',
		done:function(value, date){
			startTime.config.max = {
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
	if (!isNull(parent.childParams)) {
		let allDay = 1;
		if (!parent.childParams.allDay) {//不是全天
			allDay = 0;
			$(".allDayIsTrue").hide();
			$(".allDayIsFalse").show();
			$("#scheduleDayTime").val(parent.childParams.start.format("yyyy-MM-dd"));
			$("#scheduleDayStartTime").val(parent.childParams.start.format("hh:mm"));
			$("#scheduleDayEndTime").val(parent.childParams.end.format("hh:mm"));
		} else {//是全天
			$("#startTime").val(parent.childParams.start.format("yyyy-MM-dd"));
			$("#endTime").val(parent.childParams.end.format("yyyy-MM-dd"));
		}
		skyeyeClassEnumUtil.showEnumDataListByClassName("whetherEnum", 'radio', "allDay", allDay, form);
	} else {
		skyeyeClassEnumUtil.showEnumDataListByClassName("whetherEnum", 'radio', "allDay", '', form);
	}

	skyeyeClassEnumUtil.showEnumDataListByClassName("scheduleImported", 'radio', "imported", '', form);
	skyeyeClassEnumUtil.showEnumDataListByClassName("scheduleRemindType", 'select', "remindType", '', form);
	skyeyeClassEnumUtil.showEnumDataListByClassName("checkDayType", 'select', "type", '', form);

	matchingLanguage();
	form.render();

	// 日程是否全天
	form.on('radio(allDayFilter)', function (data) {
		if (data.value == 0) {//不是全天
			$(".allDayIsTrue").hide();
			$(".allDayIsFalse").show();
		} else {//是全天
			$(".allDayIsTrue").show();
			$(".allDayIsFalse").hide();
		}
	});

	form.on('submit(formAddBean)', function (data) {
		if (winui.verifyForm(data.elem)) {
			var params = {
				name: $("#name").val(),
				remark: $("#remark").val(),
				allDay: dataShowType.getData('allDay'),
				remindType: dataShowType.getData('remindType'),
				type: dataShowType.getData('type'),
				imported: dataShowType.getData('imported'),
			};
			if (params.allDay == 1) {//全天
				if (isNull($("#startTime").val())) {
					layer.msg("请选择开始时间", {icon: 5, shift: 6});
					return false;
				}
				if (isNull($("#endTime").val())) {
					layer.msg('请选择结束时间', {icon: 5, shift: 6});
					return false;
				}
				params.startTime = $("#startTime").val() + " 00:00:00";
				params.endTime = $("#endTime").val() + " 23:59:59";
			} else {
				if (isNull($("#scheduleDayTime").val())) {
					layer.msg('请填写日程日期', {icon: 5, shift: 6});
					return false;
				}
				if (isNull($("#scheduleDayStartTime").val())) {
					layer.msg('请填写日程开始时间', {icon: 5, shift: 6});
					return false;
				}
				if (isNull($("#scheduleDayEndTime").val())) {
					layer.msg('请填写日程结束时间', {icon: 5, shift: 6});
					return false;
				}
				params.startTime = $("#scheduleDayTime").val() + " " + $("#scheduleDayStartTime").val() + ":00";
				params.endTime = $("#scheduleDayTime").val() + " " + $("#scheduleDayEndTime").val() + ":00";
			}
			AjaxPostUtil.request({url: sysMainMation.scheduleBasePath + "insertScheduleDay", params: params, type: 'json', method: "POST", callback: function (json) {
				AjaxPostUtil.request({url: sysMainMation.scheduleBasePath + "syseveschedule006", params: {id: json.bean.id}, type: 'json', method: "GET", callback: function (result) {
					parent.childParams = result.bean;
					parent.layer.close(index);
					parent.refreshCode = '0';
				}});
			}});
		}
		return false;
	});

	// 取消
	$("body").on("click", "#cancle", function() {
		parent.layer.close(index);
	});
});