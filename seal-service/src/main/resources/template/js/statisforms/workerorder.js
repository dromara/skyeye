
var startTime = "";
var endTime = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window',
    echarts: '../echarts/echarts',
    echartsTheme: '../echarts/echartsTheme'
}).define(['window', 'table', 'jquery', 'winui', 'form', 'laydate', 'echarts'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		form = layui.form,
		table = layui.table,
		laydate = layui.laydate;
	
	//图表对象
	var myChart;
	
	var now = new Date(); //当前日期 
	var nowDayOfWeek = now.getDay(); //今天本周的第几天 
	var nowDay = now.getDate(); //当前日 
	var nowMonth = now.getMonth(); //当前月 
	var nowYear = now.getYear(); //当前年 
	nowYear += (nowYear < 2000) ? 1900 : 0; //
	
	var lastMonthDate = new Date(); //上月日期
	lastMonthDate.setDate(1);
	lastMonthDate.setMonth(lastMonthDate.getMonth() - 1);
	var lastYear = lastMonthDate.getYear();
	var lastMonth = lastMonthDate.getMonth();
		
	laydate.render({elem: '#timeRange', range: '~'});
	
	initTable();
	function initTable(){
		table.render({
		    id: 'messageTable',
		    elem: '#messageTable',
		    method: 'post',
		    url: flowableBasePath + 'sealseservicestatisforms002',
		    where: {startTime: startTime, endTime: endTime},
		    even: true,
		    page: true,
		    limits: [8, 16, 24, 32, 40, 48, 56],
		    limit: 8,
		    cols: [[
		        { field: 'userName', title: '工人名称', align: 'left', width: 240 },
		        { field: 'allNum', title: '派工数量', align: 'center', width: 120},
		        { field: 'complateNum', title: '完工数量', align: 'center', width: 120},
		        { field: 'complateNum', title: '完工率', align: 'center', width: 120, templet: function (d) {
		        	return (parseFloat(d.complateNum) / parseFloat(d.allNum) * 100).toFixed(2) + '%';
		        }}
		    ]],
		    done: function(res, curr, count){
		    	matchingLanguage();
		    	//工人名称，派工数量，完工数量，完工率
		    	var cusName = [], orderAllNum = [], orderComplateNum = [], percentage = [];
		    	$.each(res.rows, function(i, item) {
		    		cusName.push(item.userName);
		    		orderAllNum.push(item.allNum);
		    		orderComplateNum.push(item.complateNum);
		    		percentage.push((parseFloat(item.complateNum) / parseFloat(item.allNum) * 100).toFixed(2));
		    	});
		    	if(isNull(myChart)){
		    		renderChartBar(cusName, orderAllNum, orderComplateNum, percentage);
		    	} else {
		    		myChart.setOption({
	   		            xAxis: [{
		                    data: cusName
		                }],
	   		            series: [{data: orderAllNum}, {data: orderComplateNum}, {data: percentage}]
	   		        });
		    	}
		    }
		});
	}
	
	form.render();
	$("body").on("click", "#formSearch", function() {
		refreshTable();
	});
	
    function refreshTable(){
    	if(isNull($("#timeRange").val())) {
    		startTime = "";
    		endTime = "";
    	} else {
    		startTime = $("#timeRange").val().split('~')[0].trim();
    		endTime = $("#timeRange").val().split('~')[1].trim();
    	}
    	table.reloadData("messageTable", {page: {curr: 1}, where: {startTime: startTime, endTime: endTime}});
    }
    
    $("body").on("click", ".plan-type-btn", function (e) {
    	$(".plan-type-btn").removeClass("plan-select");
		$(this).addClass("plan-select");
		loadCheckType($(this).data("type"));
	});
	
	//根据不同的类型加载展示值
	function loadCheckType(nowCheckType){
		if(nowCheckType === 'day'){//日报
			var myDate = new Date();
			var year = myDate.getFullYear();//获取当前年
			var yue = myDate.getMonth() + 1;//获取当前月
			var date = myDate.getDate();//获取当前日
			$("#timeRange").val(year + "-" + yue + "-" + date + " ~ " + year + "-" + yue + "-" + date);
		} else if (nowCheckType === 'week'){//周报
			$("#timeRange").val(getWeekStartDate() + " ~ " + getWeekEndDate());
		} else if (nowCheckType === 'month'){//月报
			$("#timeRange").val(getMonthStartDate() + " ~ " + getMonthEndDate());
		} else if (nowCheckType === 'quarter'){//季报
			$("#timeRange").val(getQuarterStartDate() + " ~ " + getQuarterEndDate());
		} else if (nowCheckType === 'year'){//年报
			var myDate = new Date();
			var year = now.getYear(); //当前年
			year += (year < 2000) ? 1900 : 0;
			$("#timeRange").val(year + "-01-01" + " ~ " + year + "-12-31");
		}
		refreshTable();
	}
	
	/**
	 * 获取本周、本季度、上月的开始日期、结束日期
	 */
	//格式化日期：yyyy-MM-dd 
	function formatDate(date) {
		var myyear = date.getFullYear();
		var mymonth = date.getMonth() + 1;
		var myweekday = date.getDate();
		if(mymonth < 10) {
			mymonth = "0" + mymonth;
		}
		if(myweekday < 10) {
			myweekday = "0" + myweekday;
		}
		return(myyear + "-" + mymonth + "-" + myweekday);
	}
	
	//获得某月的天数 
	function getMonthDays(myMonth) {
		var monthStartDate = new Date(nowYear, myMonth, 1);
		var monthEndDate = new Date(nowYear, myMonth + 1, 1);
		var days = (monthEndDate - monthStartDate) / (1000 * 60 * 60 * 24);
		return days;
	}
	
	//获得本季度的开始月份 
	function getQuarterStartMonth() {
		var quarterStartMonth = 0;
		if(nowMonth < 3) {
			quarterStartMonth = 0;
		}
		if(2 < nowMonth && nowMonth < 6) {
			quarterStartMonth = 3;
		}
		if(5 < nowMonth && nowMonth < 9) {
			quarterStartMonth = 6;
		}
		if(nowMonth > 8) {
			quarterStartMonth = 9;
		}
		return quarterStartMonth;
	}
	
	//获得本周的开始日期 
	function getWeekStartDate() {
		var weekStartDate = new Date(nowYear, nowMonth, nowDay - nowDayOfWeek + 1);
		return formatDate(weekStartDate);
	}
	
	//获得本周的结束日期 
	function getWeekEndDate() {
		var weekEndDate = new Date(nowYear, nowMonth, nowDay + (7 - nowDayOfWeek));
		return formatDate(weekEndDate);
	}
	
	//获得本月的开始日期 
	function getMonthStartDate() {
		var monthStartDate = new Date(nowYear, nowMonth, 1);
		return formatDate(monthStartDate);
	}
	
	//获得本月的结束日期 
	function getMonthEndDate() {
		var monthEndDate = new Date(nowYear, nowMonth, getMonthDays(nowMonth));
		return formatDate(monthEndDate);
	}
	
	//获得本季度的开始日期 
	function getQuarterStartDate() {
		var quarterStartDate = new Date(nowYear, getQuarterStartMonth(), 1);
		return formatDate(quarterStartDate);
	}
	
	//获得本季度的结束日期 
	function getQuarterEndDate() {
		var quarterEndMonth = getQuarterStartMonth() + 2;
		var quarterStartDate = new Date(nowYear, quarterEndMonth, getMonthDays(quarterEndMonth));
		return formatDate(quarterStartDate);
	}
	
	//统计图
	function renderChartBar(customName, orderAllNum, orderComplateNum, percentage) {
		var colors = ['#5793f3', '#d14a61', '#675bba'];
		myChart = echarts.init(document.getElementById("orderStatis"));
		myChart.setOption({
			color: colors,
		    tooltip: {
		        trigger: 'axis',
		        axisPointer: {
		            type: 'cross',
		            crossStyle: {
		                color: '#999'
		            }
		        }
		    },
		    grid: {
		        right: '20%'
		    },
		    toolbox: {
		        feature: {
		            dataView: {show: true, readOnly: false},
		            restore: {show: true},
		            saveAsImage: {show: true}
		        }
		    },
		    legend: {
		        data: ['派工数', '完工数', '完工率']
		    },
		    xAxis: [{
	            type: 'category',
	            axisTick: {
	                alignWithLabel: true
	            },
	            axisPointer: {
	                type: 'shadow'
	            },
	            data: customName
	        }],
		    yAxis: [{
	            type: 'value',
	            name: '派工数',
	            position: 'right',
	            max: 50,
	            splitNumber: 5,
	            axisLine: {
	                lineStyle: {
	                    color: colors[0]
	                }
	            },
	            axisLabel: {
	                formatter: '{value} 单'
	            }
	        }, {
	            type: 'value',
	            name: '完工数',
	            position: 'right',
	            max: 50,
	            splitNumber: 5,
	            offset: 80,
	            axisLine: {
	                lineStyle: {
	                    color: colors[1]
	                }
	            },
	            axisLabel: {
	                formatter: '{value} 单'
	            }
	        }, {
	            type: 'value',
	            name: '完工率',
	            position: 'left',
	            min: 0,
	            max: 100,
	            axisLine: {
	                lineStyle: {
	                    color: colors[2]
	                }
	            },
	            axisLabel: {
	                formatter: '{value} %'
	            }
	        }],
		    series: [{
	            name: '派工数',
	            type: 'bar',
	            data: orderAllNum
	        }, {
	            name: '完工数',
	            type: 'bar',
	            yAxisIndex: 1,
	            data: orderComplateNum
	        }, {
	            name: '完工率',
	            type: 'line',
	            yAxisIndex: 2,
	            data: percentage
	        }]
		});
	}
	
	window.onresize = function () {
		myChart.resize();
    }
	
    exports('workerorder', {});
});