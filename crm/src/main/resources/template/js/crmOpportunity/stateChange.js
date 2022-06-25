
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
    	laydate = layui.laydate;
	    var stateChangeData = parent.stateChangeData;
	    
		authBtn('1573892971460');//初期沟通
		authBtn('1573893005457');//方案与报价
		authBtn('1573893015682');//竞争与投标
		authBtn('1573893026003');//商务谈判
		authBtn('1573893035164');//成交
		authBtn('1573893044277');//丢单
		authBtn('1573893055234');//搁置
	    
	    if(parent.stateChangeData.state == '11'){//审核通过
			$(".layui-form-item").removeClass("layui-hide");
		}else if(parent.stateChangeData.state == '2'){//初期沟通
			$(".typeQuotedPrice").removeClass("layui-hide");
			$(".typeTender").removeClass("layui-hide");
			$(".typeNegotiate").removeClass("layui-hide");
			$(".typeTurnover").removeClass("layui-hide");
			$(".typeLosingTable").removeClass("layui-hide");
			$(".typeLayAside").removeClass("layui-hide");
		}else if(parent.stateChangeData.state == '3'){//方案与报价
			$(".typeConmunicate").removeClass("layui-hide");
			$(".typeTender").removeClass("layui-hide");
			$(".typeNegotiate").removeClass("layui-hide");
			$(".typeTurnover").removeClass("layui-hide");
			$(".typeLosingTable").removeClass("layui-hide");
			$(".typeLayAside").removeClass("layui-hide");
		}else if(parent.stateChangeData.state == '4'){//竞争与投标
			$(".typeConmunicate").removeClass("layui-hide");
			$(".typeQuotedPrice").removeClass("layui-hide");
			$(".typeNegotiate").removeClass("layui-hide");
			$(".typeTurnover").removeClass("layui-hide");
			$(".typeLosingTable").removeClass("layui-hide");
			$(".typeLayAside").removeClass("layui-hide");
		}else if(parent.stateChangeData.state == '5'){//商务谈判
			$(".typeConmunicate").removeClass("layui-hide");
			$(".typeTender").removeClass("layui-hide");
			$(".typeQuotedPrice").removeClass("layui-hide");
			$(".typeTurnover").removeClass("layui-hide");
			$(".typeLosingTable").removeClass("layui-hide");
			$(".typeLayAside").removeClass("layui-hide");
		}else if(parent.stateChangeData.state == '8'){//商务谈判
			$(".typeConmunicate").removeClass("layui-hide");
			$(".typeLosingTable").removeClass("layui-hide");
		}
		//初期沟通
		$("body").on("click", "#conmunicate", function(){
			var msg = '确认【' + stateChangeData.title + '】进入初期沟通状态吗？';
			layer.confirm(msg, { icon: 3, title: '初期沟通' }, function (i) {
				layer.close(i);
	            AjaxPostUtil.request({url: flowableBasePath + "opportunity019", params:{rowId: stateChangeData.id}, type: 'json', callback: function(json){
	    			if (json.returnCode == 0) {
	    				parent.layer.close(index);
		 	        	parent.refreshCode = '0';
	    			} else {
	    				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
	    			}
	    		}});
			});
	    });
		
		//方案与报价
		$("body").on("click", "#quotedPrice", function(){
			var msg = '确认【' + stateChangeData.title + '】进入方案与报价状态吗？';
			layer.confirm(msg, { icon: 3, title: '方案与报价' }, function (i) {
				layer.close(i);
	            AjaxPostUtil.request({url: flowableBasePath + "opportunity020", params:{rowId: stateChangeData.id}, type: 'json', callback: function(json){
	    			if (json.returnCode == 0) {
	    				parent.layer.close(index);
		 	        	parent.refreshCode = '0';
	    			} else {
	    				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
	    			}
	    		}});
			});
	    });
		
		//竞争与投标
		$("body").on("click", "#tender", function(){
			var msg = '确认【' + stateChangeData.title + '】进入竞争与投标状态吗？';
			layer.confirm(msg, { icon: 3, title: '竞争与投标' }, function (i) {
				layer.close(i);
	            AjaxPostUtil.request({url: flowableBasePath + "opportunity021", params:{rowId: stateChangeData.id}, type: 'json', callback: function(json){
	    			if (json.returnCode == 0) {
	    				parent.layer.close(index);
		 	        	parent.refreshCode = '0';
	    			} else {
	    				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
	    			}
	    		}});
			});
	    });
		
		//商务谈判
		$("body").on("click", "#negotiate", function(){
			var msg = '确认【' + stateChangeData.title + '】进入商务谈判状态吗？';
			layer.confirm(msg, { icon: 3, title: '商务谈判' }, function (i) {
				layer.close(i);
	            AjaxPostUtil.request({url: flowableBasePath + "opportunity022", params:{rowId: stateChangeData.id}, type: 'json', callback: function(json){
	    			if (json.returnCode == 0) {
	    				parent.layer.close(index);
		 	        	parent.refreshCode = '0';
	    			} else {
	    				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
	    			}
	    		}});
			});
	    });
		
		//成交
		$("body").on("click", "#turnover", function(){
			var msg = '确认【' + stateChangeData.title + '】进入成交状态吗？';
			layer.confirm(msg, { icon: 3, title: '成交' }, function (i) {
				layer.close(i);
	            AjaxPostUtil.request({url: flowableBasePath + "opportunity023", params:{rowId: stateChangeData.id}, type: 'json', callback: function(json){
	    			if (json.returnCode == 0) {
	    				parent.layer.close(index);
		 	        	parent.refreshCode = '0';
	    			} else {
	    				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
	    			}
	    		}});
			});
	    });
		
		//丢单
		$("body").on("click", "#losingTable", function(){
			var msg = '确认【' + stateChangeData.title + '】进入丢单状态吗？';
			layer.confirm(msg, { icon: 3, title: '丢单' }, function (i) {
				layer.close(i);
	            AjaxPostUtil.request({url: flowableBasePath + "opportunity024", params:{rowId: stateChangeData.id}, type: 'json', callback: function(json){
	    			if (json.returnCode == 0) {
	    				parent.layer.close(index);
		 	        	parent.refreshCode = '0';
	    			} else {
	    				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
	    			}
	    		}});
			});
	    });
		
		//搁置
		$("body").on("click", "#layAside", function(){
			var msg = '确认【' + stateChangeData.title + '】进入搁置状态吗？';
			layer.confirm(msg, { icon: 3, title: '搁置' }, function (i) {
				layer.close(i);
	            AjaxPostUtil.request({url: flowableBasePath + "opportunity025", params:{rowId: stateChangeData.id}, type: 'json', callback: function(json){
	    			if (json.returnCode == 0) {
	    				parent.layer.close(index);
		 	        	parent.refreshCode = '0';
	    			} else {
	    				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
	    			}
	    		}});
			});
	    });
	    
	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	});
});