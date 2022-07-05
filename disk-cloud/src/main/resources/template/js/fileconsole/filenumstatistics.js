
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
		laydate = layui.laydate;
		
	var jsonData = new Array();
		
	AjaxPostUtil.request({url: reqBasePath + "fileconsole037", params: {}, type: 'json', callback: function (json) {
		jsonData = json.bean;
		$("#layer02_01 .layer02-data").find('span').eq(0).html(jsonData.allNum.fileNum);
		drawLayer02Label($("#layer02_01 canvas").get(0), "文件总数量", 80, 200);
		$("#layer02_02 .layer02-data").find('span').eq(0).html(jsonData.allNumToday.fileNum);
		drawLayer02Label($("#layer02_02 canvas").get(0), "今日新增文件数量", 80, 200);
		$("#layer02_03 .layer02-data").find('span').eq(0).html(jsonData.allNumThisWeek.fileNum);
		drawLayer02Label($("#layer02_03 canvas").get(0), "本周新增文件数量", 80, 200);
		$("#layer02_04 .layer02-data").find('span').eq(0).html(jsonData.allNum.fileSizeZh);
		drawLayer02Label($("#layer02_04 canvas").get(0), "文件总存储", 80, 200);
		$("#layer02_05 .layer02-data").find('span').eq(0).html(jsonData.allNumToday.fileSizeZh);
		drawLayer02Label($("#layer02_05 canvas").get(0), "今日新增文件存储", 80, 200);
		$("#layer02_06 .layer02-data").find('span').eq(0).html(jsonData.allNumThisWeek.fileSizeZh);
		drawLayer02Label($("#layer02_06 canvas").get(0), "本周新增文件存储", 80, 200);

		//文件类型占比饼状图
		renderChartBar01();

		//文件存储
		renderLayer03Right();

		//本年度月均线流量趋势
		renderLayer04Left();

		//新增文件数（近七天）
		renderLayer04Right();
		matchingLanguage();
	}});
	
	function drawLayer02Label(canvasObj, text, textBeginX, lineEndX) {
		var colorValue = '#FFFFFF';
		var ctx = canvasObj.getContext("2d");
		ctx.beginPath();
		ctx.arc(35, 55, 2, 0, 2 * Math.PI);
		ctx.closePath();
		ctx.fillStyle = colorValue;
		ctx.fill();
		ctx.moveTo(35, 55);
		ctx.lineTo(60, 80);
		ctx.lineTo(lineEndX, 80);
		ctx.lineWidth = 1;
		ctx.strokeStyle = colorValue;
		ctx.stroke();
		ctx.font = '12px Georgia';
		ctx.fillStyle = colorValue;
		ctx.fillText(text, textBeginX, 92);
	}
	
	//文件存储
	function renderLayer03Right() {
		var fileStorageBean = $("#fileStorageBean").html();
		var str = "";
		var colorSize = ["#027825", "#006DD6", "#238681"];
		$.each(jsonData.fileStorageNum, function(i, item){
			item.id = "test" + i;
			jsonStr = {
				bean: item
			};
			str = getDataUseHandlebars(fileStorageBean, jsonStr);
			$("#fileStorageListShow").append(str);
			drawLayer03Right($("#" + item.id + " canvas").get(0), colorSize[i], (item.fileSize / jsonData.allNum.fileSize).toFixed(2));
		});
	}
	
	function drawLayer03Right(canvasObj, colorValue, rate) {
		var ctx = canvasObj.getContext("2d");
		var circle = {
			x: 65, //圆心的x轴坐标值
			y: 80, //圆心的y轴坐标值
			r: 60 //圆的半径
		};
	
		ctx.beginPath();
		ctx.arc(circle.x, circle.y, circle.r, 0, Math.PI * 2)
		ctx.lineWidth = 10;
		ctx.strokeStyle = '#052639';
		ctx.stroke();
		ctx.closePath();
		ctx.beginPath();
		ctx.arc(circle.x, circle.y, circle.r, 1.5 * Math.PI, (1.5 + rate * 2) * Math.PI)
		ctx.lineWidth = 10;
		ctx.lineCap = 'round';
		ctx.strokeStyle = colorValue;
		ctx.stroke();
		ctx.closePath();
		ctx.fillStyle = 'white';
		ctx.font = '20px Calibri';
		ctx.fillText(rate * 100 + '%', circle.x - 15, circle.y + 10);
	}
	
	function renderChartBar01() {
		var myChart = echarts.init(document.getElementById("layer03_left_02"));
		myChart.setOption({
			tooltip: {
				trigger: 'item',
				formatter: "{b} : {c} ({d}%)"
			},
			legend: {
				type: 'scroll',
		        orient: 'vertical',
		        left: 20,
		        top: 20,
		        bottom: 20,
				data: jsonData.fileTypeNumEntity.fileTypeNumStr.replace(/(.*)[,，]$/, '$1').split(',')
			},
			toolbox: {},
			label: {
				normal: {
					show: true,
					formatter: "{b} \n{d}%"
				}
			},
			calculable: true,
			series: [{
				name: '',
				type: 'pie',
				radius: [40, 80],
				center: ['50%', '50%'],
				data: jsonData.fileTypeNumEntity.fileTypeNum
			}]
		});
	}
	
	function renderLayer04Left() {
		var myChart = echarts.init(document.getElementById("layer04_left_chart"));
		var nameStr = "", numStr = "";
		$.each(jsonData.newFileNum, function(i, item){
			nameStr += item.monthName + ',';
			numStr += item.fileNum + ',';
		});
		myChart.setOption({
				tooltip: {
					trigger: 'axis'
				},
				legend: {
					data: nameStr.replace(/(.*)[,，]$/, '$1').split(',')
				},
				grid: {
					left: '3%',
					right: '4%',
					bottom: '5%',
					top: '4%',
					containLabel: true
				},
				xAxis: {
					type: 'category',
					boundaryGap: false,
					data: nameStr.replace(/(.*)[,，]$/, '$1').split(','),
					axisLabel: {
						textStyle: {
							color: "white", //刻度颜色
							fontSize: 8 //刻度大小
						},
						rotate: 45,
						interval: 2
					},
					axisTick: {
						show: false
					},
					axisLine: {
						show: true,
						lineStyle: {
							color: '#0B3148',
							width: 1,
							type: 'solid'
						}
					}
				},
				yAxis: {
					type: 'value',
					axisLabel: {
						textStyle: {
							color: "white", //刻度颜色
							fontSize: 8 //刻度大小
						}
					},
					axisLine: {
						show: true,
						lineStyle: {
							color: '#0B3148',
							width: 1,
							type: 'solid'
						}
					},
					splitLine: {
						show: false
					}
				},
				series: [{
					name: '',
					type: 'line',
					smooth: true,
					areaStyle: {
						normal: {
							color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
								offset: 0,
								color: '#026B6F'
							}, {
								offset: 1,
								color: '#012138'
							}], false),	
							opacity: 0.2
						}
					},
					itemStyle: {
						normal: {
							color: '#009991'
						},
						lineStyle: {
							normal: {
								color: '#009895',
								opacity: 1
							}
						}
					},
					symbol: 'none',
					data: numStr.replace(/(.*)[,，]$/, '$1').split(',')
				}]
			}
		);
	}
	
	//获取前7天数组   cdate 格式 2018-07-12
	function get7days(cdate) {
		var arr = [];
		for(var i = 6; i >= 0; i--) {
			arr.push(getTheday(i, cdate))
		}
		return arr;
	}
	
	function convertDateFromString(dateString) {
		if(dateString) {
			var date = new Date(dateString.replace(/-/, "/"))
			return date;
		}
	}
	
	function getTheday(n, cdate) { //生成后几天的信息
		var d = convertDateFromString(cdate);
		d.setDate(d.getDate() - n); //天数-n
		var mon = d.getMonth() + 1;
		var day = d.getDate();
	
		var d2 = d.getFullYear() + "-" + (mon < 10 ? ('0' + mon) : mon) + "-" + (day < 10 ? ('0' + day) : day); //新日期
		return d2;
	}
	
	function renderLayer04Right() {
		var myChart = echarts.init(document.getElementById("layer04_right_chart"));
		var dataStr = new Array(), nameList = new Array(), clickDate = new Array();
		$.each(jsonData.fileTypeNumSevenDay, function(i, item){
			clickDate.push(item.clickDate);
			
			//加载数据
			var inDataStr = -1;
			$.each(dataStr, function(j, bean){
				if(bean.name == item.typeName){
					inDataStr = j;
					return false;
				}
			});
			if(inDataStr >= 0){
				dataStr[inDataStr]["customData"].push({num: item.fileNum, date: item.clickDate});
			} else {
				var customData = new Array();
				customData.push({num: item.fileNum, date: item.clickDate});
				dataStr.push({
					name: item.typeName,
					type: 'line',
					customData: customData,
					data: ""
				});
			}
			
			//加载名称
			var inNameList = false;
			$.each(nameList, function(j, bean){
				if(bean.name == item.typeName){
					inNameList = true;
					return false;
				}
			});
			if(!inNameList){
				nameList.push({name: item.typeName});
			}
		});
		
		//获取近七天日期
		var sevenDay = get7days(getYMDFormatDate());
		$.each(dataStr, function(k, entity){
			var s = "";
			$.each(sevenDay, function(i, item){
				var dayInStr = -1;
				$.each(entity.customData, function(j, bean){
					if(item == bean.date){
						dayInStr = j;
						return false;
					}
				});
				if(dayInStr >= 0){
					s += entity.customData[dayInStr].num + ',';
				} else {
					s += '0,';
				}
			});
			dataStr[k]["data"] = s.replace(/(.*)[,，]$/, '$1').split(',');
		});
		
		myChart.setOption({
			tooltip: {
				trigger: 'axis'
			},
			legend: {
				top: 20,
				right: 5,
				textStyle: {
					color: 'white'
				},
				orient: 'vertical',
				data: nameList
			},
			grid: {
				left: '3%',
				right: '16%',
				bottom: '3%',
				top: '3%',
				containLabel: true
			},
			xAxis: {
				type: 'category',
				boundaryGap: false,
				axisTick: {
					show: false
				},
				axisLabel: {
					textStyle: {
						color: "white", //刻度颜色
						fontSize: 8 //刻度大小
					}
				},
				axisLine: {
					show: true,
					lineStyle: {
						color: '#0B3148',
						width: 1,
						type: 'solid'
					}
				},
				data: clickDate
			},
			yAxis: {
				type: 'value',
				axisTick: {
					show: false
				},
				axisLabel: {
					textStyle: {
						color: "white", //刻度颜色
						fontSize: 8 //刻度大小
					}
				},
				axisLine: {
					show: true,
					lineStyle: {
						color: '#0B3148',
						width: 1,
						type: 'solid'
					}
				},
				splitLine: {
					show: false
				}
			},
			series: dataStr
		});
	}
	
    exports('filenumstatistics', {});
});
