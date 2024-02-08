
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
	var $ = layui.$;
	var stateChangeData = parent.stateChangeData;

	if(parent.stateChangeData.state == 'pass'){//审核通过
		$(".layui-form-item").removeClass("layui-hide");
	} else if (parent.stateChangeData.state == 'initialCommunication'){//初期沟通
		$(".typeQuotedPrice").removeClass("layui-hide");
		$(".typeTender").removeClass("layui-hide");
		$(".typeNegotiate").removeClass("layui-hide");
		$(".typeTurnover").removeClass("layui-hide");
		$(".typeLayAside").removeClass("layui-hide");
	} else if (parent.stateChangeData.state == 'schemeAndQuotation'){//方案与报价
		$(".typeConmunicate").removeClass("layui-hide");
		$(".typeTender").removeClass("layui-hide");
		$(".typeNegotiate").removeClass("layui-hide");
		$(".typeTurnover").removeClass("layui-hide");
		$(".typeLayAside").removeClass("layui-hide");
	} else if (parent.stateChangeData.state == 'competitionAndBidding'){//竞争与投标
		$(".typeConmunicate").removeClass("layui-hide");
		$(".typeQuotedPrice").removeClass("layui-hide");
		$(".typeNegotiate").removeClass("layui-hide");
		$(".typeTurnover").removeClass("layui-hide");
		$(".typeLayAside").removeClass("layui-hide");
	} else if (parent.stateChangeData.state == 'businessNegotiation'){//商务谈判
		$(".typeConmunicate").removeClass("layui-hide");
		$(".typeTender").removeClass("layui-hide");
		$(".typeQuotedPrice").removeClass("layui-hide");
		$(".typeTurnover").removeClass("layui-hide");
		$(".typeLayAside").removeClass("layui-hide");
	} else if (parent.stateChangeData.state == 'layAside'){//搁置
		$(".typeConmunicate").removeClass("layui-hide");
	}
	$(".typeLosingTable").removeClass("layui-hide");
	//初期沟通
	$("body").on("click", "#conmunicate", function() {
		var msg = '确认【' + stateChangeData.title + '】进入初期沟通状态吗？';
		layer.confirm(msg, { icon: 3, title: '初期沟通' }, function (i) {
			layer.close(i);
			AjaxPostUtil.request({url: sysMainMation.crmBasePath + "opportunity019", params: {id: stateChangeData.id}, type: 'json', method: 'POST', callback: function (json) {
				parent.layer.close(index);
				parent.refreshCode = '0';
			}});
		});
	});

	//方案与报价
	$("body").on("click", "#quotedPrice", function() {
		var msg = '确认【' + stateChangeData.title + '】进入方案与报价状态吗？';
		layer.confirm(msg, { icon: 3, title: '方案与报价' }, function (i) {
			layer.close(i);
			AjaxPostUtil.request({url: sysMainMation.crmBasePath + "opportunity020", params: {id: stateChangeData.id}, type: 'json', method: 'POST', callback: function (json) {
				parent.layer.close(index);
				parent.refreshCode = '0';
			}});
		});
	});

	//竞争与投标
	$("body").on("click", "#tender", function() {
		var msg = '确认【' + stateChangeData.title + '】进入竞争与投标状态吗？';
		layer.confirm(msg, { icon: 3, title: '竞争与投标' }, function (i) {
			layer.close(i);
			AjaxPostUtil.request({url: sysMainMation.crmBasePath + "opportunity021", params: {id: stateChangeData.id}, type: 'json', method: 'POST', callback: function (json) {
				parent.layer.close(index);
				parent.refreshCode = '0';
			}});
		});
	});

	//商务谈判
	$("body").on("click", "#negotiate", function() {
		var msg = '确认【' + stateChangeData.title + '】进入商务谈判状态吗？';
		layer.confirm(msg, { icon: 3, title: '商务谈判' }, function (i) {
			layer.close(i);
			AjaxPostUtil.request({url: sysMainMation.crmBasePath + "opportunity022", params: {id: stateChangeData.id}, type: 'json', method: 'POST', callback: function (json) {
				parent.layer.close(index);
				parent.refreshCode = '0';
			}});
		});
	});

	//成交
	$("body").on("click", "#turnover", function() {
		var msg = '确认【' + stateChangeData.title + '】进入成交状态吗？';
		layer.confirm(msg, { icon: 3, title: '成交' }, function (i) {
			layer.close(i);
			AjaxPostUtil.request({url: sysMainMation.crmBasePath + "opportunity023", params: {id: stateChangeData.id}, type: 'json', method: 'POST', callback: function (json) {
				parent.layer.close(index);
				parent.refreshCode = '0';
			}});
		});
	});

	//丢单
	$("body").on("click", "#losingTable", function() {
		var msg = '确认【' + stateChangeData.title + '】进入丢单状态吗？';
		layer.confirm(msg, { icon: 3, title: '丢单' }, function (i) {
			layer.close(i);
			AjaxPostUtil.request({url: sysMainMation.crmBasePath + "opportunity024", params: {id: stateChangeData.id}, type: 'json', method: 'POST', callback: function (json) {
				parent.layer.close(index);
				parent.refreshCode = '0';
			}});
		});
	});

	//搁置
	$("body").on("click", "#layAside", function() {
		var msg = '确认【' + stateChangeData.title + '】进入搁置状态吗？';
		layer.confirm(msg, { icon: 3, title: '搁置' }, function (i) {
			layer.close(i);
			AjaxPostUtil.request({url: sysMainMation.crmBasePath + "opportunity025", params: {id: stateChangeData.id}, type: 'json', method: 'POST', callback: function (json) {
				parent.layer.close(index);
				parent.refreshCode = '0';
			}});
		});
	});

	$("body").on("click", "#cancle", function() {
		parent.layer.close(index);
	});
});