
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).define(['table', 'jquery', 'winui', 'form'], function (exports) {
	
	winui.renderColor();
	var $ = layui.$,
	form = layui.form,
	table = layui.table;
	
	var redisInfo = false,//redis服务器配置信息是否加载
		redisLogsInfo = false;//redis服务器日志信息是否加载
	
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
	
	//监听Winui的左右Tab切换
	winui.tab.on('tabchange(winuitab)', function (data) {
        if(data.index == '0'){
        	if(!redisInfo)
        		initRedieInfoMation();
        }else if(data.index == '1'){
        	if(!redisLogsInfo)
        		initRedieLogsMation();
        }
    });
	
    exports('redismonitorlist', {});
});
