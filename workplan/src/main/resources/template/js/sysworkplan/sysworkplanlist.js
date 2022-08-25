
//计划类型--传递后台
var nowCheckType = "day";

//当前选择的计划分类
var checkPlan = '1';//默认个人计划

//计划新增时的时间段计算
var timeSolt = "";

//计划id
var rowId = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form', 'laydate'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		form = layui.form,
		table = layui.table,
		laydate = layui.laydate;
	
	authBtn('1567561566241');//新增个人计划
	authBtn('1567561577490');//新增部门计划
	authBtn('1567561587761');//新增公司计划

	var planCycle = {
		"day": 1,
		"week": 2,
		"month": 3,
		"quarter": 4,
		"halfyear": 5,
		"year": 6,
	};
	
	//获取当前日期信息
	var initDate = new Date();
	//初始化日期
	var checkDateTypeMonth$y = initDate.getFullYear(),//月选择器初始化年值
		checkDateTypeYear$y = initDate.getFullYear();//年选择器初始化年值
	var checkDateTypeMonth$m = initDate.getMonth() + 1;//月选择器初始化月值
	//格式化数据
	checkDateTypeMonth$m = formatDig(checkDateTypeMonth$m);
	//初始化加载当前月的天
	getDaysInMonth(checkDateTypeMonth$y, checkDateTypeMonth$m);
	//初始化月日期选择器
	laydate.render({
		elem: '#checkDateTypeMonth',
		type: 'month',
		value: checkDateTypeMonth$y + '-' + checkDateTypeMonth$m,
		btns: ['now', 'confirm'],
		done: function(value, date, endDate){
			checkDateTypeMonth$y = date.year;
			checkDateTypeMonth$m = formatDig(date.month);
			getDaysInMonth(checkDateTypeMonth$y, checkDateTypeMonth$m);
		}
	});
	
	//初始化年日期选择器
	laydate.render({
		elem: '#checkDateTypeYear',
		type: 'year',
		value: checkDateTypeYear$y,
		btns: ['now', 'confirm'],
		done: function(value, date, endDate){
			checkDateTypeYear$y = date.year;
			loadCheckType(checkDateTypeYear$y);
		}
	});
	
	//对左侧菜单项的点击事件
	$("body").on("click", "#setting a", function (e) {
		$(".setting a").removeClass("selected");
		$(this).addClass("selected");
		checkPlan = $(this).data("check");
		refreshTable();
	});
	
	//计划类型按钮点击
	$("body").on("click", ".plan-type-btn", function (e) {
		var dataType = $(this).data("type");
		if(dataType != nowCheckType){
			$(".plan-type-btn").removeClass("plan-select");
			$(this).addClass("plan-select");
			nowCheckType = dataType;
			if(nowCheckType === 'day'){//日计划
				$("#checkDateTypeYearBox").hide();
				$("#checkDateTypeMonthBox").show();
				getDaysInMonth(checkDateTypeMonth$y, checkDateTypeMonth$m);
			}else if(nowCheckType === 'week' || nowCheckType === 'month' || nowCheckType === 'quarter'
					|| nowCheckType === 'halfyear' || nowCheckType === 'year'){//周计划-月计划-季度计划-半年计划-年计划
				$("#checkDateTypeMonthBox").hide();
				$("#checkDateTypeYearBox").show();
				loadCheckType(checkDateTypeYear$y);
			}
		}
	});
	
	//日期选择
	$("body").on("click", ".plan-choose li", function (e) {
		if(!$(this).hasClass('active')){
			$(".plan-choose li").removeClass("active");
			$(this).addClass("active");
			//加载表格
			refreshTable();
		}
	});
	
	//是否加载表格
	var wetherLoadTable = false;
	function initList(){
		wetherLoadTable = true;
		table.render({
		    id: 'messageTable',
		    elem: '#messageTable',
		    method: 'post',
		    url: sysMainMation.workplanBasePath + 'sysworkplan001',
		    where: getTableParams(),
		    even: true,
		    page: true,
		    limits: [8, 16, 24, 32, 40, 48, 56],
		    limit: 8,
		    cols: [[
		        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
		        { field: 'title', title: '计划主题', width: 300, templet: function (d) {
		        	return '<a lay-event="details" class="notice-title-click">' + d.title + '</a>';
	        	}},
		        { field: 'whetherMail', title: '邮件通知', align: 'center', width: 100 },
		        { field: 'whetherNotice', title: '消息通知', align: 'center', width: 100 },
		        { field: 'whetherTime', title: '定时通知', width: 150 },
		        { field: 'whetherNotify', title: '状态', align: 'center', width: 100 },
		        { field: 'userName', title: systemLanguage["com.skyeye.createName"][languageType], width: 120 },
		        { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 120 },
		        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 240, toolbar: '#tableBar'}
		    ]],
		    done: function(json) {
		    	matchingLanguage();
		    }
		});
		table.on('tool(messageTable)', function (obj) {
	        var data = obj.data;
	        var layEvent = obj.event;
	        if (layEvent === 'edit') { //编辑
	        	edit(data);
	        } else if (layEvent === 'del') { //删除
	        	del(data, obj);
	        } else if (layEvent === 'timingSend') { //定时发送
	        	timingSend(data);
	        } else if (layEvent === 'cancleTiming') { //取消定时发送
	        	cancleTiming(data);
	        } else if (layEvent === 'details') { //详情
	        	details(data);
	        }
	    });
	}
	
	//定时发送
	function timingSend(data) {
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/sysworkplan/sysworkplantiming.html", 
			title: "定时发送",
			pageId: "sysworkplantiming",
			area: ['40vw', '60vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	}
	
	//详情
	function details(data) {
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/sysworkplan/sysworkplandetails.html", 
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "sysworkplantiming",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
			}});
	}
	
	//编辑
	function edit(data) {
		rowId = data.id;
		if(data.planType === '1' || data.planType == 1){//个人计划
			_openNewWindows({
				url: "../../tpl/sysworkplan/sysworkplanedit.html", 
				title: "编辑个人计划",
				pageId: "sysworkplanedit",
				area: ['90vw', '90vh'],
				callBack: function (refreshCode) {
					winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
					loadTable();
				}});
		}else if(data.planType === '2' || data.planType == 2){//部门计划
			_openNewWindows({
				url: "../../tpl/sysworkplan/sysworkplandepedit.html", 
				title: "编辑部门计划",
				pageId: "sysworkplandepedit",
				area: ['90vw', '90vh'],
				callBack: function (refreshCode) {
					winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
					loadTable();
				}});
		}else if(data.planType === '3' || data.planType == 3){//公司计划
			_openNewWindows({
				url: "../../tpl/sysworkplan/sysworkplancomedit.html", 
				title: "编辑公司计划",
				pageId: "sysworkplancomedit",
				area: ['90vw', '90vh'],
				callBack: function (refreshCode) {
					winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
					loadTable();
				}});
		} else {
			winui.window.msg("参数错误", {icon: 2, time: 2000});
		}
	}
	
	//取消定时发送
	function cancleTiming(data) {
		var msg = "确定取消定时发送吗？";
		layer.confirm(msg, { icon: 3, title: '取消定时发送' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: sysMainMation.workplanBasePath + "sysworkplan005", params: {planId: data.id}, type: 'json', callback: function (json) {
				winui.window.msg("已取消定时发送", {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}
	
	//删除
	function del(data, obj){
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
			layer.close(index);
            AjaxPostUtil.request({url: sysMainMation.workplanBasePath + "sysworkplan006", params: {planId: data.id}, type: 'json', callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}
	
	//根据不同的类型加载展示值
	function loadCheckType(year){
		var str = ''
		if(nowCheckType === 'week'){//周计划
			var arr = createWeeks(year);
			$.each(arr, function(i, item){
				let start = item.startTime,
					end = item.endTime;
				var nowWeek = formatDig(i + 1);
				str += '<li value="' + (i + 1) + '">第'+ nowWeek + '周  <span>' + formatDate(start) + '日~' + formatDate(end) + '日</span></li>';
			});
		}else if(nowCheckType === 'month'){//月计划
			var arr = createMonths(year);
			$.each(arr, function(i, item){
				let start = item.startTime,
					end = item.endTime;
				var nowMonth = formatDig(i + 1);
				str += '<li value="' + (i + 1) + '">第'+ nowMonth + '月  <span>' + start + '~' + end + '</span></li>';
			});
		}else if(nowCheckType === 'quarter'){//季度计划
			var arr = createQuarter(year);
			$.each(arr, function(i, item){
				let start = item.startTime,
					end = item.endTime;
				var nowQuarter = formatDig(i + 1);
				str += '<li value="' + (i + 1) + '">第'+ nowQuarter + '季度  <span>' + start + '~' + end + '</span></li>';
			});
		}else if(nowCheckType === 'halfyear'){//半年计划
			var arr = createHalfYear(year);
			$.each(arr, function(i, item){
				let start = item.startTime,
					end = item.endTime;
				var nowHalfYear = formatDig(i + 1);
				str += '<li value="' + (i + 1) + '">'+ (i == 0 ? '上半年' : '下半年') + '  <span>' + start + '~' + end + '</span></li>';
			});
		}else if(nowCheckType === 'year'){//年计划
			str += '<li class="active" value="' + year + '">' + year + '</li>';
		}
		resetDate(str);
	}
	
	//获取指定年月中的所有天
	function getDaysInMonth(year, month){ 
		//parseInt(number, type)这个函数后面如果不跟第2个参数来表示进制的话，默认是10进制
		month = parseInt(month, 10);
		var temp = new Date(year, month, 0);
		var str = '';
		for(var i = 1; i <= temp.getDate(); i++){
			var sDay = formatDig(i);
			str += '<li value="' + i + '">' + sDay + '</li>';
		}
		resetDate(str);
	}
	
	//重置日期
	function resetDate(str){
		$(".plan-choose").html(str);
		//设置选中项
		checkDateItem();
		//加载表格
		if(!wetherLoadTable)
			initList();
		else
			refreshTable();
	}
	
	//选中时间
	function checkDateItem(){
		//判断当前选择的年是否等于今年&&当前选择的月是否等于当月
		if(initDate.getFullYear() === checkDateTypeMonth$y && formatDig(initDate.getMonth() + 1) === checkDateTypeMonth$m){
			if(nowCheckType === 'day'){//日计划
				var today = new Date();
				//获取当前日
				var sDay = today.getDate();
				$(".plan-choose li").removeClass('active');
				var _this = $(".plan-choose").find("li[value='" + sDay + "']");
				_this.addClass('active');
				scoolrTo(_this);
			}
		} else {
			var _this = $(".plan-choose").find("li").eq(0);
			_this.addClass('active');
			scoolrTo(_this);
		}
		
		//判断当前选择的年是否等于今年的年
		if(initDate.getFullYear() === checkDateTypeYear$y){
			if(nowCheckType === 'week'){//周计划
				var i = getWeekOfYear();
				$(".plan-choose li").removeClass('active');
				var _this = $(".plan-choose").find("li[value='" + i + "']");
				_this.addClass('active');
				scoolrTo(_this);
			}else if(nowCheckType === 'month'){//月计划
				var today = new Date();
				//获取当前月
				var sMonth = today.getMonth() + 1;
				$(".plan-choose li").removeClass('active');
				var _this = $(".plan-choose").find("li[value='" + sMonth + "']");
				_this.addClass('active');
				scoolrTo(_this);
			}else if(nowCheckType === 'quarter'){//季度计划
				var today = new Date();
				//获取当前月
				var sMonth = today.getMonth() + 1;
				var i = Math.floor(sMonth / 3) + (sMonth % 3 == 0 ? 0 : 1);
				$(".plan-choose li").removeClass('active');
				var _this = $(".plan-choose").find("li[value='" + i + "']");
				_this.addClass('active');
				scoolrTo(_this);
			}else if(nowCheckType === 'halfyear'){//半年计划
				var today = new Date();
				//获取当前月
				var sMonth = today.getMonth() + 1;
				var i = Math.floor(sMonth / 6) + (sMonth % 6 == 0 ? 0 : 1);
				$(".plan-choose li").removeClass('active');
				var _this = $(".plan-choose").find("li[value='" + i + "']");
				_this.addClass('active');
				scoolrTo(_this);
			}else if(nowCheckType === 'year'){//年计划
				//无需处理
			}
		} else {
			var _this = $(".plan-choose").find("li").eq(0);
			_this.addClass('active');
			scoolrTo(_this);
		}
	}
	
	//滚动到指定对象
	function scoolrTo(item){
		var mainContainer = $('.plan-choose');
		mainContainer.animate({
			scrollTop: item.offset().top - mainContainer.offset().top + mainContainer.scrollTop()
		}, 500);
	}
	
	//获取当前时间属于第几周
	function getWeekOfYear() {
		var today = new Date();
		var firstDay = new Date(today.getFullYear(), 0, 1);
		var dayOfWeek = firstDay.getDay();
		var spendDay = 1;
		if(dayOfWeek != 0) {
			spendDay = 7 - dayOfWeek + 1;
		}
		firstDay = new Date(today.getFullYear(), 0, 1 + spendDay);
		var d = Math.ceil((today.valueOf() - firstDay.valueOf()) / 86400000);
		var result = Math.ceil(d / 7);
		return result + 1;
	}
	
	//获取指定年的所有周
	function createWeeks(year) {
		var arr = new Array();
		const ONE_DAY = 24 * 3600 * 1000;
		var start = new Date(year, 0, 1),
			end = new Date(year, 11, 31);
		var firstDay = start.getDay() || 7,
			lastDay = end.getDay() || 7;
		var startTime = +start,
			endTime = startTime + (7 - firstDay) * ONE_DAY,
			_endTime = end - (7 - lastDay) * ONE_DAY;
		arr.push({
			startTime: startTime,
			endTime: endTime
		});
		startTime = endTime + ONE_DAY;
		endTime = endTime + 7 * ONE_DAY;
		while(endTime < _endTime) {
			arr.push({
				startTime: startTime,
				endTime: endTime
			});
			startTime = endTime + ONE_DAY;
			endTime = endTime + 7 * ONE_DAY;
		}
		arr.push({
			startTime: startTime,
			endTime: endTime
		});
		return arr;
	}
	
	//获取指定年的所有月
	function createMonths(year) {
		var arr = new Array();
		var isrun = year % 400 == 0 || (year % 4 == 0 & year % 100 != 0);//是否闰年
		var ddate = 30;//月末日期,默认30号
		for(var i = 1; i <= 12; i++){
			ddate = 30;
			if(i == 1 || i == 3 || i == 5 || i == 7 || i == 8 || i == 10 || i == 12){
				ddate = 31;
			}else if(i == 2){ //二月
				ddate = isrun ? 29 : 28; //闰年29号,平年28号
			}
			arr.push({
				startTime: formatDig(i) + '月' + '01日',
				endTime: formatDig(i) + '月' + ddate + '日'
			});
		}
		return arr;
	}
	
	//获取指定年的所有季度
	function createQuarter(year) {
		var arr = new Array();
		var ddate = 30;//月末日期,默认30号
		for(var i = 1; i <= 4; i++){
			ddate = 30;
			var sMonth = i * 3;
			if(sMonth == 3 || sMonth == 12){
				ddate = 31;
			}
			arr.push({
				startTime: formatDig(sMonth - 2) + '月' + '01日',
				endTime: formatDig(sMonth) + '月' + ddate + '日'
			});
		}
		return arr;
	}
	
	//获取指定年的所有半年
	function createHalfYear(year) {
		var arr = new Array();
		var ddate = 30;//月末日期,默认30号
		for(var i = 1; i <= 2; i++){
			ddate = 30;
			var sMonth = i * 6;
			if(sMonth == 12){
				ddate = 31;
			}
			arr.push({
				startTime: formatDig(sMonth - 5) + '月' + '01日',
				endTime: formatDig(sMonth) + '月' + ddate + '日'
			});
		}
		return arr;
	}
	
	//格式化日期，结果为：01月11
	function formatDate(mill) {
		var y = new Date(mill);
		let raws = [
			formatDig(y.getMonth() + 1),
			formatDig(y.getDate())
		];
		let format = ['月'];
		return String.raw({
			raw: raws
		}, ...format);
	}
	
	//格式化日期，结果为：01-11
	function formatDateDay(mill) {
		var y = new Date(mill);
		let raws = [
			formatDig(y.getMonth() + 1),
			formatDig(y.getDate())
		];
		let format = ['-'];
		return String.raw({
			raw: raws
		}, ...format);
	}
	
	//格式化数字
	function formatDig(num) {
		return num > 9 ? '' + num : '0' + num;
	}
	
	// 新增个人计划
	$("body").on("click", "#addPeoplePlan", function (e) {
		timeSolt = getTimeSoltStr();
		_openNewWindows({
			url: "../../tpl/sysworkplan/sysworkplanadd.html", 
			title: "新增个人计划",
			pageId: "sysworkplanaddpage",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				refreshTable();
			}});
	});
	
	// 新增部门计划
	$("body").on("click", "#addDepartmentPlan", function (e) {
		timeSolt = getTimeSoltStr();
		_openNewWindows({
			url: "../../tpl/sysworkplan/sysworkplandepadd.html", 
			title: "新增部门计划",
			pageId: "sysworkplandepaddpage",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				refreshTable();
			}});
	});
	
	// 新增公司计划
	$("body").on("click", "#addCompanyPlan", function (e) {
		timeSolt = getTimeSoltStr();
		if(isNull(timeSolt)){
			winui.window.msg("时间维度错误。", {icon: 2, time: 2000});
			return;
		}
		_openNewWindows({
			url: "../../tpl/sysworkplan/sysworkplancomadd.html", 
			title: "新增公司计划",
			pageId: "sysworkplancomaddpage",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				refreshTable();
			}});
	});
	
	/**
	 * 打开新页面时计算事件范围
	 * @returns {String}
	 */
	function getTimeSoltStr(){
		var timeSoltStr = "";
		if(nowCheckType === 'day'){
			var _this = $(".plan-choose").find("li[class='active']").attr("value");
			timeSoltStr = checkDateTypeMonth$y + '-' + checkDateTypeMonth$m + '-' + formatDig(_this);
			timeSoltStr = timeSoltStr + '~' + timeSoltStr;
		}else if(nowCheckType === 'week'){
			var _this = $(".plan-choose").find("li[class='active']").attr("value");
			var arr = createWeeks(checkDateTypeYear$y);
			var timeItem = arr[_this - 1];
			timeSoltStr = checkDateTypeYear$y + '-' + formatDateDay(timeItem.startTime) + '~' + checkDateTypeYear$y + '-' + formatDateDay(timeItem.endTime);
		}else if(nowCheckType === 'month'){
			var _this = $(".plan-choose").find("li[class='active']").attr("value");
			timeSoltStr = checkDateTypeYear$y + '-' + formatDig(_this) + '-01~' + checkDateTypeYear$y + '-' + formatDig(_this) + '-' + getDaysByMonth(checkDateTypeYear$y, _this);
		}else if(nowCheckType === 'quarter'){
			var _this = $(".plan-choose").find("li[class='active']").attr("value");
			var sMonth = _this * 3;
			var ddate = 30;
			if(sMonth == 3 || sMonth == 12){
				ddate = 31;
			}
			timeSoltStr = checkDateTypeYear$y + '-' + formatDig(sMonth - 2) + '-01~' +checkDateTypeYear$y + '-' + formatDig(sMonth) + '-' + ddate;
		}else if(nowCheckType === 'halfyear'){
			var _this = $(".plan-choose").find("li[class='active']").attr("value");
			var sMonth = _this * 6;
			var ddate = 30;
			if(sMonth == 12){
				ddate = 31;
			}
			timeSoltStr = checkDateTypeYear$y + '-' + formatDig(sMonth - 5) + '-01~' +checkDateTypeYear$y + '-' + formatDig(sMonth) + '-' + ddate;
		}else if(nowCheckType === 'year'){
			timeSoltStr = checkDateTypeYear$y + '-01-01~' +checkDateTypeYear$y + '-12-31';
		} else {
			timeSoltStr = "";
		}
		return timeSoltStr;
	}
	
	//获取指定年月中的所有天
	function getDaysByMonth(year, month){ 
		//parseInt(number, type)这个函数后面如果不跟第2个参数来表示进制的话，默认是10进制
		month = parseInt(month, 10);
		var temp = new Date(year, month, 0);
		return temp.getDate();
	}

	form.render();
	form.on('submit(formSearch)', function (data) {
        if (winui.verifyForm(data.elem)) {
        	refreshTable();
        }
        return false;
	});
	
	// 刷新数据
	$("body").on("click", "#reloadTable", function (e) {
		loadTable();
	});

	// 刷新表格
	function refreshTable(){
		table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
	}

	function loadTable() {
		table.reloadData("messageTable", {where: getTableParams()});
	}

	function getTableParams() {
		var timeStr = getTimeSoltStr();
		var startTime = timeStr.split('~')[0] + " 00:00:00";
		var endTime = timeStr.split('~')[1] + " 23:59:59";
		return {
			planType: checkPlan,
			planCycle: planCycle[nowCheckType],
			title: $("#title").val(),
			startTime: startTime,
			endTime: endTime
		};
	}
	
    exports('sysworkplanlist', {});
});
