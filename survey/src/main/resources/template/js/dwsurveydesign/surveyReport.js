
var quIndex = 0;//问题序号

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window',
    echarts: '../echarts/echarts',
    echartsTheme: '../echarts/echartsTheme'
}).define(['window', 'jquery', 'winui', 'echarts'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
	    	form = layui.form;
	    
		showGrid({
		 	id: "dwBodyUser",
		 	url: reqBasePath + "dwsurveydirectory026",
		 	params: {rowId: parent.rowId},
		 	pagination: false,
		 	template: getFileContent('tpl/dwsurveydesign/surveyReport.tpl'),
		 	ajaxSendLoadBefore: function(hdb){
		 		
		 		hdb.registerHelper("showIndex", function(v1, options) {
		 			quIndex++;
					return quIndex;
				});
		 		
		 		hdb.registerHelper("showXhIndex", function(v1, options) {
					return parseInt(v1) + 1;
				});
		 		
		 		hdb.registerHelper('compare1', function(v1, v2, options) {
		 			if(v1 == v2){
		 				return options.fn(this);
		 			}else{
		 				return options.inverse(this);
		 			}
		 		});
		 		
		 		hdb.registerHelper('compare7', function(v1, options) {
		 			if(v1 != '16' && v1 != '17'){
		 				return options.fn(this);
		 			}else{
		 				return options.inverse(this);
		 			}
		 		});
		 		
		 	},
		 	ajaxSendAfter:function(json){
		 		
		 		if(json.bean.surveyState == '2'){
		 			$("#surveyState").html("结束");
		 		}
		 		
		 		/**
				 * 统计图切换
				 */
				$(".linechart_pic,.piechart_pic,.barchart_pic,.columnchart_pic").click(function() {
					var th = $(this);
					var thClass = th.attr("class");
					var quId = th.parents(".surveyResultQu").find("input[name='quId']").val();
					var amchartdivId = null;
					if(thClass.indexOf("linechart_pic") > 0) {
						amchartdivId = "line_chart_" + quId;
					} else if(thClass.indexOf("piechart_pic") > 0) {
						amchartdivId = "pie_chart_" + quId;
					} else if(thClass.indexOf("barchart_pic") > 0) {
						amchartdivId = "bar_chart_" + quId;
					} else if(thClass.indexOf("columnchart_pic") > 0) {
						amchartdivId = "column_chart_" + quId;
					}
					th.parent().find(".dw_btn026.active").removeClass("active");
					th.addClass("active");
					if(amchartdivId != null) {
						var amchartdivObj = $("#" + amchartdivId);
						if(!amchartdivObj[0]) {
		 	   				var msg = {};
			 	   			$("#amchart_" + quId).find(".higChartSvg").hide();
							if(thClass.indexOf("linechart_pic") > 0) {
								higLineChart(msg, quId);
							} else if(thClass.indexOf("piechart_pic") > 0) {
								higPieChart(msg, quId);
							} else if(thClass.indexOf("barchart_pic") > 0) {
								higBarChart(msg, quId);
							} else if(thClass.indexOf("columnchart_pic") > 0) {
								higColumnChart(msg, quId);
							}
						} else {
							$("#amchart_" + quId).find(".higChartSvg").hide();
							amchartdivObj.show();
						}
					}
					return false;
				});
				setTimeout(function(){
					$(".columnchart_pic").click();
				}, 2000);
				resetQuNum();
				
				matchingLanguage();
		 	}
	    });
		
		function resetQuNum(){
			var quCoNums = $(".quCoNum");
			$.each(quCoNums, function(i, item){
				$(this).html((i + 1) + "、");
			});
		}
		
		function getHighchartsData(quItemBody, charType) {
			var quType = quItemBody.find("input[name='quType']").val();
			var categories = [];
			var series = new Array();
			var seriesData = new Array();
			var tagText = "次数";
			var legendData = new Array();
			var seriesType = 'bar';
			if(charType === "Line") {
				seriesType = 'line';
			}
			if(quType === "11" || quType === "13" || quType === "18") {
				if(charType === "PIE") {
					seriesData = new Array();
					var rowItemTrs = quItemBody.find(".rowItemTr");
					$.each(rowItemTrs, function() {
						var rowItemOptionName = $(this).find(".rowItemOptionName").text();
						var thColumnItemTrs = $(this).next().find(".columnItemTr");
						$.each(thColumnItemTrs, function() {
							var columnItemOptionName = $(this).find(".columnItemOptionName").text();
							var anCount = $(this).find("input[name='columnItemAnCount']").val();
							var data = {};
							data["value"] = parseInt(anCount);
							data["name"] = rowItemOptionName + "|" + columnItemOptionName;
							series.push(data);

						});
					});
				} else {
					var columnItemTrs = quItemBody.find(".anColumnTable:eq(0) .columnItemTr");
					$.each(columnItemTrs, function() {
						var columnItemOptionName = $(this).find(".columnItemOptionName").text();
						categories.push(columnItemOptionName);
					});
					var rowItemTrs = quItemBody.find(".rowItemTr");
					$.each(rowItemTrs, function() {
						var rowItemOptionName = $(this).find(".rowItemOptionName").text();
						var thColumnItemTrs = $(this).next().find(".columnItemTr");
						seriesData = new Array();
						$.each(thColumnItemTrs, function() {
							var anCount = $(this).find("input[name='columnItemAnCount']").val();
							seriesData.push(parseInt(anCount));
						});
						series.push({ //指定数据列
							name: rowItemOptionName, //数据列名
							type: seriesType,
							data: seriesData //数据
						});
						legendData.push(rowItemOptionName);
					});
				}
			} else {
				var seriesDataTemp = "[";
				var quRadioOptions = quItemBody.find(".quTrOptions");
				$.each(quRadioOptions, function(i, item) {
					var quOptionName = $(this).find(".optionName").text();
					var anCount = $(this).find("input[name='quItemAnCount']").val();
					if(anCount == "") {
						anCount = 0;
					}
					categories.push(quOptionName);
					if(quType === "8") {
						var avgScore = $(this).find("input[name='quItemAvgScore']").val();
						//平均分 setAvgScore  
						avgScore = parseFloat(avgScore).toFixed(2);
						if(avgScore === "NaN") {
							avgScore = "0.00";
						}
						if(charType === "PIE") {
							var data = {};
							data["value"] = parseFloat(avgScore);
							data["name"] = quOptionName;
							seriesData.push(data);
						} else {
							seriesData.push(parseFloat(avgScore));
						}
						tagText = "分数";
					} else if(quType === "9") {
						if(charType === "PIE") {
							var data = {};
							data["value"] = parseInt(anCount);
							data["name"] = quOptionName;
							seriesData.push(data);
						} else {
							seriesData.push(parseInt(anCount));
						}
						tagText = "排名";
					} else {
						if(charType === "PIE") {
							var data = {};
							data["value"] = parseInt(anCount);
							data["name"] = quOptionName;
							seriesData.push(data);
						} else {
							seriesData.push(parseInt(anCount));
						}
					}
				});
				if(charType === "PIE") {
					series = seriesData;
				} else if(charType === "BAR") {
					series = [{
						name: tagText,
						type: 'bar',
						data: seriesData
					}];
				} else {
					series = [{
						name: tagText,
						type: seriesType,
						data: seriesData
					}]
				}
			}
			return [categories, series, tagText, legendData];
		}

		function higColumnChart(resultJson, quId) {
			var chartdivId = "column_chart_" + quId;
			$("#amchart_" + quId).prepend("<div id='" + chartdivId + "' class=\"higChartSvg\"></div>");
			$("#" + chartdivId).css({
				"height": "300px"
			});
			var quItemBody = $("#quTr_" + quId);
			var quTitle = quItemBody.find(".quCoTitleText").text();
			var quTypeName = quItemBody.find("input[name='quTypeCnName']").val();
			var datas = getHighchartsData(quItemBody, "column");
			var categories = datas[0];
			var series = datas[1];
			var tagText = datas[2];
			var legendData = datas[3];
			var myChart = echarts.init($('#' + chartdivId)[0], "shine");
			// 指定图表的配置项和数据
			var option = {
				title: {
					text: quTitle,
					top: 8,
					textStyle: {
						fontSize: 16
					},
					x: 'center'
				},
				toolbox: {
					show: true,
					feature: {
						saveAsImage: {
							show: true
						}
					}
				},
				color: ['#7cb5ec'],
				backgroundColor: '#fff',
				tooltip: {
					trigger: 'axis',
					axisPointer: { // 坐标轴指示器，坐标轴触发有效
						type: 'shadow' // 默认为直线，可选为：'line' | 'shadow'
					},
					top: 20
				},
				grid: {
					left: '3%',
					right: '3%',
					bottom: '5%',
					containLabel: true
				},
				xAxis: {
					type: 'category',
					data: categories,
					axisTick: {
						alignWithLabel: true
					},
					nameGap: 20,
					axisLabel: {
						interval: 0,
						margin: 15
					},
					axisLine: {
						show: false,
						lineStyle: {
							width: 1
						}
					},
					axisTick: {
						show: false
					}
				},
				yAxis: {
					splitNumber: 5,
					type: 'value',
					axisLine: {
						show: false
					},
					axisTick: {
						show: false
					},
					name: tagText
				},
				legend: {
					bottom: 0,
					data: legendData
				},
				series: series
			};
			// 使用刚指定的配置项和数据显示图表。
			myChart.setOption(option);
		}

		function higPieChart(resultJson, quId) {
			var chartdivId = "pie_chart_" + quId;
			$("#amchart_" + quId).prepend("<div id='" + chartdivId + "' class=\"higChartSvg\"></div>");
			$("#" + chartdivId).css({
				"height": "300px"
			});
			var quItemBody = $("#quTr_" + quId);
			var quTitle = quItemBody.find(".quCoTitleText").text();
			var quTypeName = quItemBody.find("input[name='quTypeCnName']").val();
			var datas = getHighchartsData(quItemBody, "PIE");
			var series = datas[1];
			var tagText = datas[2];
			var categories = datas[0]
			var legendData = datas[3];
			var myChart = echarts.init($('#' + chartdivId)[0], "shine");
			var option = {
				title: {
					text: quTitle,
					top: 8,
					textStyle: {
						fontSize: 16
					},
					x: 'center'
				},
				tooltip: {
					trigger: 'item',
					formatter: "{a} <br/>{b} : {c} ({d}%)"
				},
				backgroundColor: '#fff',
				toolbox: {
					show: true,
					feature: {
						saveAsImage: {
							show: true
						}
					}
				},
				legend: {
					x: 'center',
					y: 'bottom',
					data: categories //['直接访问','邮件营销','联盟广告','视频广告','搜索引擎']
				},
				series: // series 
					[{
						name: tagText,
						type: 'pie',
						radius: '55%',
						center: ['50%', '60%'],
						data: series,
						itemStyle: {
							emphasis: {
								shadowBlur: 10,
								shadowOffsetX: 0,
								shadowColor: 'rgba(0, 0, 0, 0.5)'
							}
						}
					}]
			};
			myChart.setOption(option);
		}

		function higBarChart(resultJson, quId) {
			var chartdivId = "bar_chart_" + quId;
			$("#amchart_" + quId).prepend("<div id='" + chartdivId + "' class=\"higChartSvg\"></div>");
			$("#" + chartdivId).css({
				"height": "300px"
			});
			var quItemBody = $("#quTr_" + quId);
			var quTitle = quItemBody.find(".quCoTitleText").text();
			var quTypeName = quItemBody.find("input[name='quTypeCnName']").val();
			var datas = getHighchartsData(quItemBody, "BAR");
			var categories = datas[0];
			var series = datas[1];
			var tagText = datas[2];
			var legendData = datas[3];
			var myChart = echarts.init($('#' + chartdivId)[0], "shine");
			var option = {
				title: {
					text: quTitle,
					top: 8,
					textStyle: {
						fontSize: 16
					},
					x: 'center'
				},
				tooltip: {
					trigger: 'axis',
					axisPointer: {
						type: 'shadow'
					}
				},
				backgroundColor: '#fff',
				color: ['#7cb5ec'],
				legend: {
					data: ['2011年', '2012年']
				},
				grid: {
					left: '3%',
					right: '4%',
					bottom: '10%',
					containLabel: true
				},
				toolbox: {
					show: true,
					feature: {
						saveAsImage: {
							show: true
						}
					}
				},
				xAxis: {
					type: 'value',
					axisTick: {
						alignWithLabel: true
					},
					nameGap: 12,
					axisLabel: {
						interval: 0,
						margin: 15
					},
					axisLine: {
						show: false,
						lineStyle: {
							width: 1
						}
					},
					axisTick: {
						show: false
					},
					splitLine: {
						show: true
					}
				},
				yAxis: {
					type: 'category',
					data: categories
				},
				legend: {
					bottom: 2,
					data: legendData
				},
				series: series
			};
			myChart.setOption(option);
		}

		function higLineChart(resultJson, quId) {
			//根据quId得到数据对象，并且解析
			var chartdivId = "line_chart_" + quId;
			$("#amchart_" + quId).prepend("<div id='" + chartdivId + "' class=\"higChartSvg\"></div>");
			$("#" + chartdivId).css({
				"height": "300px"
			});
			var quItemBody = $("#quTr_" + quId);
			var quTitle = quItemBody.find(".quCoTitleText").text();
			var quTypeName = quItemBody.find("input[name='quTypeCnName']").val();
			var datas = getHighchartsData(quItemBody, "Line");
			var categories = datas[0];
			var series = datas[1];
			var tagText = datas[2];
			var legendData = datas[3];
			var myChart = echarts.init($('#' + chartdivId)[0], "shine");
			// 指定图表的配置项和数据
			var option = {
				title: {
					text: quTitle,
					top: 8,
					textStyle: {
						fontSize: 16
					},
					x: 'center'
				},
				toolbox: {
					show: true,
					feature: {
						saveAsImage: {
							show: true
						}
					}
				},
				backgroundColor: '#fff',
				color: ['#3398DB'],
				tooltip: {
					trigger: 'axis',
					axisPointer: { // 坐标轴指示器，坐标轴触发有效
						type: 'shadow' // 默认为直线，可选为：'line' | 'shadow'
					},
					top: 20
				},
				grid: {
					left: '3%',
					right: '4%',
					bottom: '3%',
					containLabel: true
				},
				xAxis: {
					type: 'category',
					data: categories,
					axisTick: {
						alignWithLabel: true
					},
					nameGap: 20,
					axisLabel: {
						interval: 0,
						margin: 15
					},
					axisLine: {
						show: false,
						lineStyle: {
							width: 1
						}
					},
					axisTick: {
						show: false
					},
					splitLine: {
						show: true
					}
				},
				yAxis: {
					minInterval: 1,
					name: '次数'
				},
				legend: {
					bottom: 0,
					data: legendData
				},
				series: series
			};
			// 使用刚指定的配置项和数据显示图表。
			myChart.setOption(option);
		}

		function substring(json) {
			var bufLen = json.length;
			var lastIndex = json.lastIndexOf(",");
			if(bufLen == (lastIndex + 1)) {
				json = json.substring(0, lastIndex);
			}
			return json;
		}
		
	});
	    
});