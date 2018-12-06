
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    echarts: '../echarts/echarts',
    echartsTheme: '../echarts/echartsTheme',
}).define(['table', 'jquery', 'winui', 'form', 'echarts'], function (exports) {
	
	winui.renderColor();
	var $ = layui.$,
	form = layui.form,
	table = layui.table;
	
	var redisInfo = false,//redis服务器配置信息是否加载
		redisLogsInfo = false,//redis服务器日志信息是否加载
		redisKeysMation = false;//redis服务器里面的key数量变化
	
	var myChart = null, xdata = [], ydata = [], legendData = [];
	
	
	initRedieInfoMation();
	
	//redis服务器配置信息
	function initRedieInfoMation(){
		showGrid({
		 	id: "bar",
		 	url: reqBasePath + "redis001",
		 	params: {},
		 	pagination: false,
		 	template: getFileContent('tpl/sysredis/redisTemplate.tpl'),
		 	ajaxSendLoadBefore: function(hdb){},
		 	ajaxSendAfter:function(json){
		 		redisInfo = true;
	 			$("#bar tbody").scroll(function(){
	 		        $("#bar tbody").scrollTop($(this).scrollTop());
	 		    }); 
		 	}
	    });
	}
	
	//redis服务器日志信息
	function initRedieLogsMation(){
		showGrid({
		 	id: "line",
		 	url: reqBasePath + "redis002",
		 	params: {},
		 	pagination: false,
		 	template: getFileContent('tpl/sysredis/redisLogsTemplate.tpl'),
		 	ajaxSendLoadBefore: function(hdb){},
		 	ajaxSendAfter:function(json){
		 		redisLogsInfo = true;
		 	}
	    });
	}
	
	//redis服务器里面的key数量变化
	function initRedieKeysChangeMation(){
		var option = {
	        title: {
	            text: 'Redis服务器Key数量监测(每五秒刷新一次)', //标题
	            textStyle: {
	                color: '#ff0000'//设置主标题颜色
	            }
	        },
	        tooltip: {trigger: 'axis'},//当trigger为’item’时只会显示该点的数据,为’axis’时显示该列下所有坐标轴所对应的数据
	        legend: {
	        	data: legendData,
	        	type: 'scroll',
	            bottom: 10,
	        },//图例
	        toolbox: {//工具栏，内置有导出图片、数据视图、动态类型切换、数据区域缩放、重置五个工具。
	            show: true,
	            feature: {//toolbox的配置项
	                mark: {show: true }, //辅助线的开关
	                dataView: {//数据视图
	                    show: true,
	                    readOnly: false
	                },
	                magicType: {//动态类型切换
	                    show: true,
	                    //line为折线图，bar为柱状图
	                    type: ['line', 'bar']
	                },
	                restore: {show: true },//配置项还原
	                saveAsImage: {show: true }//将图标保存为图片
	            }
	        },
	        calculable: true,//可计算特性
	        xAxis: [{//x轴样式
                type: 'category',//设置类别
                boundaryGap: false,//数值起始和结束两端空白策略
                axisLine: {
                    lineStyle: {
                        type: 'solid',
                        width: '4'//坐标线的宽度
                    }
                },
                data: xdata
	        }],
	        yAxis: [{//y轴样式
                type: 'value',//设置类别
                axisLabel: {//y轴刻度
                    formatter: '{value}',//设置y轴数值为%
                },
                axisLine: {
                    lineStyle: {
                        type: 'solid',
                        width: '4'//坐标轴宽度
                    }
                }
            }],
	        series: ydata//数据系列，一个图表可能包含多个系列，每一个系列可能包含多个数据
	    };
		AjaxPostUtil.request({url:reqBasePath + "redis003", params:{}, type:'json', callback:function(json){
   			if(json.returnCode == 0){
   				redisKeysMation = true;
   				myChart = echarts.init($("#redisKeys")[0], layui.echartsTheme);
   				myChart.setOption(option);
   				xdata.push(json.rows[0].keys.createTime);
   				for(var i in json.rows){
   					legendData.push(json.rows[i].ip);
   					ydata.push({
   			            name: json.rows[i].ip,
   			            type: 'line',
   			            data: [json.rows[i].keys.dbSize],
   			            markPoint: {
   			                data: [
   			                    { type: 'max', name: '最大值' },
   			                    { type: 'min', name: '最小值' }
   			                ]
   			            },
   			            markLine: {
   			                data: [
   			                    { type: 'average', name: '平均值' }
   			                ]
   			            }
   			        });
   				}
   				myChart.setOption({
   		            xAxis: [{
	                    data: xdata
	                }],
	                legend: {data: legendData},
   		            series: ydata
   		        });
   				setInterval(timeSleRedisKey, 5000);
   			}else{
   				top.winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
   			}
   		}});
	}
	
	//定时获取redis的keys的数量
	function timeSleRedisKey(){
		AjaxPostUtil.request({url:reqBasePath + "redis003", params:{}, type:'json', callback:function(json){
   			if(json.returnCode == 0){
   				if(xdata.length >= 7){
   					xdata = xdata.slice(1);
   				}
   				xdata.push(json.rows[0].keys.createTime);
   				for(var i in json.rows){
   					if(ydata[i].name == json.rows[i].ip){
   						ydata[i].data.push(json.rows[i].keys.dbSize);
   					}
   				}
   				myChart.setOption({
   		            xAxis: [{
	                    data: xdata
	                }],
   		            series: ydata
   		        });
   			}else{
   				top.winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
   			}
   		}});
	}
	
	//监听Winui的左右Tab切换
	winui.tab.on('tabchange(winuitab)', function (data) {
        if(data.index == '0'){
        	if(!redisInfo)
        		initRedieInfoMation();
        }else if(data.index == '1'){
        	if(!redisLogsInfo)
        		initRedieLogsMation();
        }else if(data.index == '2'){
        	if(!redisKeysMation)
        		initRedieKeysChangeMation();
        }
    });
	
    exports('redismonitorlist', {});
});
