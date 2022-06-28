
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window',
    echarts: '../echarts/echarts',
    echartsTheme: '../echarts/echartsTheme'
}).define(['window', 'table', 'jquery', 'winui', 'form', 'echarts'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		form = layui.form,
		table = layui.table;
	
	initFourNumList();
	function initFourNumList(){
		AjaxPostUtil.request({url: reqBasePath + "erppage001", params: {}, type: 'json', callback: function (json) {
   			if (json.returnCode == 0) {
 	   			$("#salesMoney").html(json.bean.salesMoney);
 	   			$("#retailMoney").html(json.bean.retailMoney);
 	   			$("#purchaseMoney").html(json.bean.purchaseMoney);
 	   			$("#profitMoney").html(json.bean.profitMoney);
 	   			initSixMonthPurchase();
   			} else {
   				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
   			}
   		}});
	}
	
	// 近六个月审核通过的采购订单
	function initSixMonthPurchase(){
		AjaxPostUtil.request({url: reqBasePath + "erppage002", params: {}, type: 'json', callback: function (json) {
   			if (json.returnCode == 0) {
 	   			renderLayer04Left(json.rows);
 	   			initSixMonthSales();
   			} else {
   				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
   			}
   		}});
	}
	
	// 近六个月审核通过的销售订单
	function initSixMonthSales(){
		AjaxPostUtil.request({url: reqBasePath + "erppage003", params: {}, type: 'json', callback: function (json) {
   			if (json.returnCode == 0) {
 	   			renderLayer04Left2(json.rows);
 	   			initSixMonthProfit();
   			} else {
   				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
   			}
   		}});
	}
	
	// 近12个月审核通过的利润订单
	function initSixMonthProfit(){
		AjaxPostUtil.request({url: reqBasePath + "erppage004", params: {}, type: 'json', callback: function (json) {
   			if (json.returnCode == 0) {
 	   			renderLayer04Left3(json.rows);
 	   			matchingLanguage();
   			} else {
   				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
   			}
   		}});
	}
	
	var myChart1, myChart2, myChart2;
	function renderLayer04Left(rows) {
		myChart1 = echarts.init(document.getElementById("layer04_chart1"));
		var nameStr = new Array();
		var numStr = new Array();
		$.each(rows, function(i, item){
			nameStr.push(item.yearMonth);
			numStr.push(item.purchaseMoney);
		});
		myChart1.setOption({
			title: {
				text: '近六个月审核通过的采购订单',
				x: 'center'
			},
			tooltip: {
				trigger: 'axis'
			},
			xAxis: {
				type: 'category',
				data: nameStr
			},
			yAxis: {
				type: 'value'
			},
			series: [{
				name: '金额',
				type: 'line',
				smooth: true,
				data: numStr
			}]
		});
	}
	
	function renderLayer04Left2(rows) {
		myChart2 = echarts.init(document.getElementById("layer04_chart2"));
		var nameStr = new Array();
		var numStr = new Array();
		$.each(rows, function(i, item){
			nameStr.push(item.yearMonth);
			numStr.push(item.salesMoney);
		});
		myChart2.setOption({
			title: {
				text: '近六个月审核通过的销售订单',
				x: 'center'
			},
			tooltip: {
				trigger: 'axis'
			},
			xAxis: {
				type: 'category',
				data: nameStr
			},
			yAxis: {
				type: 'value'
			},
			series: [{
				name: '金额',
				type: 'line',
				smooth: true,
				data: numStr
			}]
		});
	}
	
	function renderLayer04Left3(rows) {
		myChart3 = echarts.init(document.getElementById("layer04_chart3"));
		var nameStr = new Array();
		var numStr = new Array();
		$.each(rows, function(i, item){
			nameStr.push(item.yearMonth);
			numStr.push(item.profitMoney);
		});
		myChart3.setOption({
			title: {
				text: '近十二个月的利润（已审核通过）',
				x: 'center',
				subtext: '零售订单金额 + 销售订单金额 - 采购订单金额'
			},
			tooltip: {
				trigger: 'axis'
			},
			xAxis: {
				type: 'category',
				data: nameStr
			},
			yAxis: {
				type: 'value'
			},
			series: [{
				name: '利润',
				type: 'line',
				smooth: true,
				data: numStr
			}]
		});
	}
	
	window.onresize = function(){
	    myChart1.resize();
	    myChart2.resize();
	    myChart3.resize();
	}
	
    exports('erpPage', {});
});
