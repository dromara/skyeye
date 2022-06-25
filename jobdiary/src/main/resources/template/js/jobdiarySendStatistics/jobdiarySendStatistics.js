
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window',
    echarts: '../echarts/echarts',
    echartsTheme: '../echarts/echartsTheme'
}).define(['window', 'jquery', 'winui', 'laydate', 'form', 'echarts'], function (exports) {
	
	winui.renderColor();
	
	var $ = layui.$,
		form = layui.form,
		laydate = layui.laydate;
	
	// 初始化统计数据
	var thefirstTime = getThirdDayToDate() + ' 00:00:00';
	var thesecondTime = getYMDFormatDate() + ' 23:59:59';
	
	// '统计表'页面的选取时间段表格
	laydate.render({
		elem: '#sendTime',
		range: '~',
		value: getThirdDayToDate() + " ~ " + getYMDFormatDate(),
		done: function(value, date, endDate){
			if(isNull(value)){
				thefirstTime = "";
				thesecondTime = "";
			}else{
				thefirstTime = value.split("~")[0].trim() + ' 00:00:00';
				thesecondTime = value.split("~")[1].trim() + ' 23:59:59';
			}
			initMySendEcharts();
		}
	});
	
	form.render();
	
    // 统计日志饼图
    var echartsPie, ydata = [];
    initloadEcharts();
    function initloadEcharts(){
    	var option = {
			title : {
				text: '日志类型分布',
				subtext: '个人日志',
				x:'center'
			},
			tooltip : {
				trigger: 'item',
				formatter: "{a} <br/>{b} : {c} 条"
			},
			legend: {
				orient : 'vertical',
				x : 'left',
				data:['日报','月报','周报']
			},
			toolbox: {
				show : true,
				feature : {
					mark : {show: true},
					dataView : {show: true, readOnly: false},
					magicType : {
						show: true, 
						type: ['pie', 'funnel'],
						option: {
							funnel: {
								x: '25%',
								width: '50%',
								funnelAlign: 'left',
								max: 1548
							}
						}
					},
					restore : {show: true},
					saveAsImage : {show: true}
				}
			},
			calculable : true,
			series : [{
				name:'日报类型',
				type:'pie',
				radius : '55%',//饼图的半径大小
				center: ['50%', '60%']//饼图的位置
			}]
    	};
    	echartsPie = echarts.init($("#echartsPie")[0], layui.echartsTheme);
    	echartsPie.setOption(option);
    	initMySendEcharts();
    }
    
    function initMySendEcharts(){
		var params = {
			firstTime: thefirstTime, 
			lastTime: thesecondTime
    	};
    	AjaxPostUtil.request({url:reqBasePath + "diary022", params: params, type: 'json', callback: function(json){
    		if (json.returnCode == 0) {
    			ydata = json.rows;
    			echartsPie.setOption({ 
    				series: {
    					data:ydata
    				}
   		        });
   		        matchingLanguage();
    		}else{
    			winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
    		}
    	}});
    }
    	
    exports('jobdiarySendStatistics', {});
});
