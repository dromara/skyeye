
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    echarts: '../echarts/echarts',
    echartsTheme: '../echarts/echartsTheme',
}).define(['jquery', 'winui', 'echarts'], function (exports) {
	
	winui.renderColor();
	var $ = layui.$;
	
	var ws = null;
	
	var option;
	
	var totalMemoryChart = null, freeMemoryChart = null, freePhysicalMemorySizeChart = null, usedMemoryChart = null, totalThreadChart = null, cpuRatioChart = null,
		xdata = [];
		totalMemoryYdata = [], freeMemoryYdata = [], freePhysicalMemorySizeYdata = [], usedMemoryYdata = [], totalThreadYdata = [], cpuRatioYdata = [];
	
	initMation();
	//服务器信息变化
	function initMation(){
		option = {
		    title: {
		        text: '可使用内存'
		    },
		    tooltip: {
		        trigger: 'axis',
		        formatter: function (params) {
		            params = params[0];
		            return params.name + ' : ' + params.value[1];
		        },
		        axisPointer: {
		            animation: false
		        }
		    },
		    xAxis: {
		        type: 'category',
		        data: xdata
		    },
		    yAxis: {
		        type: 'value',
		        splitLine: {
		            show: false
		        }
		    },
		    series: [{
		        name: '模拟数据',
		        type: 'line',
		        showSymbol: false,
		        hoverAnimation: false,
		        data: totalMemoryYdata
		    }]
		};
		AjaxPostUtil.request({url:reqBasePath + "sysmonitor001", params:{}, type:'json', callback:function(json){
   			if(json.returnCode == 0){
   				totalMemoryChart = echarts.init($("#totalMemoryLine")[0], layui.echartsTheme);
   				freeMemoryChart = echarts.init($("#freeMemoryYLine")[0], layui.echartsTheme);
   				freePhysicalMemorySizeChart = echarts.init($("#freePhysicalMemorySizeLine")[0], layui.echartsTheme);
   				usedMemoryChart = echarts.init($("#usedMemoryLine")[0], layui.echartsTheme);
   				totalThreadChart = echarts.init($("#totalThreadLine")[0], layui.echartsTheme);
   				cpuRatioChart = echarts.init($("#cpuRatioLine")[0], layui.echartsTheme);
   				if (option && typeof option === "object") {
   					totalMemoryChart.setOption(option);
   					freeMemoryChart.setOption(option);
   					freePhysicalMemorySizeChart.setOption(option);
   					usedMemoryChart.setOption(option);
   					totalThreadChart.setOption(option);
   					cpuRatioChart.setOption(option);
   				}
   				$("#osName").html(json.rows[0].osName);
   				$("#totalMemorySize").html(json.rows[0].totalMemorySize);
   				$("#maxMemory").html(json.rows[0].maxMemory);
   				for(var i in json.rows){
   					xdata.push(json.rows[i].createTime);
   					totalMemoryYdata.push({
   			            name: json.rows[i].createTime,
   						value: [json.rows[i].createTime, json.rows[i].totalMemory],
   			        });
   					freeMemoryYdata.push({
   			            name: json.rows[i].createTime,
   						value: [json.rows[i].createTime, json.rows[i].freeMemory],
   			        });
   					freePhysicalMemorySizeYdata.push({
   			            name: json.rows[i].createTime,
   						value: [json.rows[i].createTime, json.rows[i].freePhysicalMemorySize],
   			        });
   					usedMemoryYdata.push({
   			            name: json.rows[i].createTime,
   						value: [json.rows[i].createTime, json.rows[i].usedMemory],
   			        });
   					totalThreadYdata.push({
   			            name: json.rows[i].createTime,
   						value: [json.rows[i].createTime, json.rows[i].totalThread],
   			        });
   					cpuRatioYdata.push({
   			            name: json.rows[i].createTime,
   						value: [json.rows[i].createTime, json.rows[i].cpuRatio],
   			        });
   				}
   				totalMemoryChart.setOption({
   					xAxis: {data: xdata},
   					title: {
   				        text: '当前JVM占用的内存总数(M)'
   				    },
   			        series: [{
   			            data: totalMemoryYdata
   			        }]
   			    });
   				
   				freeMemoryChart.setOption({
   					xAxis: {data: xdata},
   					title: {
   				        text: 'JVM空闲内存(M)'
   				    },
   			        series: [{
   			            data: freeMemoryYdata
   			        }]
   			    });
   				
   				freePhysicalMemorySizeChart.setOption({
   					xAxis: {data: xdata},
   					title: {
   				        text: '剩余的物理内存'
   				    },
   			        series: [{
   			            data: freePhysicalMemorySizeYdata
   			        }]
   			    });
   				
   				usedMemoryChart.setOption({
   					xAxis: {data: xdata},
   					title: {
   				        text: '已使用的物理内存'
   				    },
   			        series: [{
   			            data: usedMemoryYdata
   			        }]
   			    });
   				
   				totalThreadChart.setOption({
   					xAxis: {data: xdata},
   					title: {
   				        text: '线程总数'
   				    },
   			        series: [{
   			            data: totalThreadYdata
   			        }]
   			    });
   				
   				cpuRatioChart.setOption({
   					xAxis: {data: xdata},
   					title: {
   				        text: 'cpu使用率'
   				    },
   			        series: [{
   			            data: cpuRatioYdata
   			        }]
   			    });
   				
   				if ('WebSocket' in window) {
   					ws = new WebSocket("ws://localhost:8081/websocket001" + "?userToken=" + getCookie('userToken') + "&loginPCIp=" + returnCitySN["cip"]);
   					
   					//连接成功建立的回调方法
   	                ws.onopen = function(){
   	                   // Web Socket 已连接上，使用 send() 方法发送数据
   	                };
   	                //接收到消息的回调方法
   	                ws.onmessage = function (evt) { 
   	                   var received_msg = evt.data;
   	                   try {
   	                	   if (typeof JSON.parse(received_msg) == "object") {
   	                		   var jsonData = JSON.parse(received_msg);
   	                		   
   	                		   xdata.push(jsonData.createTime);
   	                		   totalMemoryYdata.push({
   	                			   name: jsonData.createTime,
   	                			   value: [jsonData.createTime, jsonData.totalMemory],
   	                		   });
   	                		   freeMemoryYdata.push({
								   name: jsonData.createTime,
								   value: [jsonData.createTime, jsonData.freeMemory],
   	                		   });
   	                		   freePhysicalMemorySizeYdata.push({
   	                			   name: jsonData.createTime,
   	                			   value: [jsonData.createTime, jsonData.freePhysicalMemorySize],
   	                		   });
   	                		   usedMemoryYdata.push({
   	                			   name: jsonData.createTime,
   	                			   value: [jsonData.createTime, jsonData.usedMemory],
   	                		   });
   	                		   totalThreadYdata.push({
   	                			   name: jsonData.createTime,
   	                			   value: [jsonData.createTime, jsonData.totalThread],
   	                		   });
   	                		   cpuRatioYdata.push({
   	                			   name: jsonData.createTime,
   	                			   value: [jsonData.createTime, jsonData.cpuRatio],
   	                		   });
   	                		   
   	                		   totalMemoryChart.setOption({
   	                			   xAxis: {data: xdata},
   	                			   series: [{
   	                				   data: totalMemoryYdata
   	                			   }]
   	                		   });
   	                		   freeMemoryChart.setOption({
   	                			   xAxis: {data: xdata},
   	                			   series: [{
   	                				   data: freeMemoryYdata
   	                			   }]
   	                		   });
   	                		   freePhysicalMemorySizeChart.setOption({
   	                			   xAxis: {data: xdata},
   	                			   series: [{
   	                				   data: freePhysicalMemorySizeYdata
   	                			   }]
   	                		   });
   	                		   usedMemoryChart.setOption({
   	                			   xAxis: {data: xdata},
   	                			   series: [{
   	                				   data: usedMemoryYdata
   	                			   }]
   	                		   });
   	                		   totalThreadChart.setOption({
   	                			   xAxis: {data: xdata},
   	                			   series: [{
   	                				   data: totalThreadYdata
   	                			   }]
   	                		   });
   	                		   cpuRatioYdata.setOption({
   	                			   xAxis: {data: xdata},
   	                			   series: [{
   	                				   data: cpuRatioYdata
   	                			   }]
   	                		   });
   	                	   	}
   	                   } catch(e) {
   	                   }
   	                };
   	                //连接关闭的回调方法
   	                ws.onclose = function () {
	   	            	 
   	                };
   	                //连接发生错误的回调方法
   	                ws.onerror = function () {
   	                	
   	                };
   	                setInterval(function(){
   	                	ws.send("你好");
   	                }, 5000);
   				} else {
   					alert('当前浏览器 Not support websocket')
   				}
                 
   			}else{
   				top.winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
   			}
   		}});
		
		//页面刷新或者关闭时，关闭socket
		window.onbeforeunload = function(){
			if (ws != null) {
				ws.close();
			}
		}
	}
	
    exports('sysmonitorlist', {});
});
