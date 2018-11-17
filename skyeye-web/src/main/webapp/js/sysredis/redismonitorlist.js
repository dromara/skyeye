
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).define(['table', 'jquery', 'winui', 'form'], function (exports) {
	
	winui.renderColor();
	var $ = layui.$,
	form = layui.form,
	table = layui.table;
	
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
	 			$("#bar tbody").scroll(function(){
	 		        $("#bar tbody").scrollTop($(this).scrollTop());
	 		    }); 
		 	}
	    });
	}
	
	
    exports('redismonitorlist', {});
});
