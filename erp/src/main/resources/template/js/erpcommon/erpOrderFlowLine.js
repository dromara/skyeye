
var rowId = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'orgChart', 'form'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		form = layui.form;

	var orderId = GetUrlParam("rowId");
	var type = GetUrlParam("type");
	// 加载订单流水线信息
	AjaxPostUtil.request({url: flowableBasePath + "erpcommon002", params: {rowId: orderId, type: type}, type: 'json', method: 'GET', callback: function(data) {
		var newData = dealWithData(data.bean);
		$('#chart-container').orgchart({
			'data': newData,
			'nodeTitle': 'title',
			'nodeContent': 'name',
			'pan': true,
			'zoom': true,
			'parentNodeSymbol': 'fa-receipt',
			'interactive': false
		});
		form.render();

		$("#chart-container").on("click", ".node", function() {
			rowId = $(this).attr("id");
			if(isNull(rowId)){
				parent.$("#orderMationLi").click();
				return false;
			}
			var type = $(this).find(".customName").attr("type");
			var url = erpOrderUtil.getErpDetailUrl({subType: type});
			if(isNull(url)){
				return false;
			}
			_openNewWindows({
				url: url,
				title: "单据详情",
				pageId: "orderDetails",
				area: ['100vw', '100vh'],
				callBack: function (refreshCode) {
				}});
		});
	}});

	function dealWithData(bean){
		bean.title = '<a type="' + bean.subType + '" class="customName">' + bean.title + '</a>';
		bean.name = '<div>' + bean.name + '</div>' +
			'<div class="orgIcon"><font class="left-font">状态：</font>' + getStateIcon(bean.subType, bean.state) + '</div>' +
			'<div class="fontMation"><font class="left-font">计划完成时间：</font><font class="right-font">' + getDataMation(bean.planComplateDate) + '</font></div>' +
			'<div class="fontMation"><font class="left-font">实际完成时间：</font><font class="right-font">' + getDataMation(bean.realComplateDate) + '</font></div>';
		if (!isNull(bean.children) && bean.children.length > 0) {
			$.each(bean.children, function (i, item) {
				bean.children[i] = dealWithData(item);
			});
		}
		return bean;
	}

	function getStateIcon(subType, state){
		var icon = "";
		if(subType == 10 || subType == 11){
			// 采购订单，销售订单
			icon = getErpOrderHeaderState(state);
		} else if (subType == 1 || subType == 5 || subType == 15){
			// 采购入库，销售出库，验收入库单
			icon = getErpHeaderState(state);
		} else if (subType == 17){//加工单子单据（工序验收单）
			icon = getErpMachinChildState(state);
		} else if (subType == 16){//加工单
			icon = getErpMachinHeaderState(state);
		} else if (subType == 18){//生产计划单
			icon = getErpProductionHeaderState(state);
		} else if (subType == 19 || subType == 20 || subType == 21){
			// 领料单/补料单/退料单
			icon = getErpPickHeaderState(state);
		}
		var stateName = getStateName(icon);
		icon = getStateCustom(icon);
		icon = '<i class="fa fa-fw ' + icon + '" title="' + stateName + '"></i>';
		return icon;
	}

	function getDataMation(data) {
		if (isNull(data)){
			return '';
		}
		return data;
	}

	/**
	 * 获取中文对应汉字
	 * @param icon
	 * @returns {string}
	 */
	function getStateName(icon){
		if(icon == 'fa-spinner fa-spin'){
			return '执行中';
		} else if (icon == 'fa-times-circle'){
			return '执行异常';
		} else if (icon == 'fa-check-circle'){
			return '完成';
		}
	}

	/**
	 * 拼接状态颜色属性
	 * @param icon
	 * @returns {string}
	 */
	function getStateCustom(icon){
		if(icon == 'fa-spinner fa-spin'){
			return icon + ' running';
		} else if (icon == 'fa-times-circle'){
			return icon + ' error';
		} else if (icon == 'fa-check-circle'){
			return icon + ' success';
		}
	}

	/**
	 * erp订单状态
	 *
	 * @param state
	 * @returns {string}
	 */
	function getErpOrderHeaderState(state){
		var icon = "";
		// 0未审核、1.审核中、2.审核通过、3.审核拒绝、4.已转入库|出库，已完成
		if(state == 0 || state == 1 || state == 2){
			icon = "fa-spinner fa-spin";
		} else if (state == 3){
			icon = "fa-times-circle";
		} else if (state == 4){
			icon = "fa-check-circle";
		}
		return icon;
	}

	/**
	 * erp出入库订单状态
	 *
	 * @param state
	 * @returns {string}
	 */
	function getErpHeaderState(state){
		var icon = "";
		// 0未审核、1.审核中、2.审核通过、3.审核拒绝、4.已转入库|出库，已完成
		if(state == 0 || state == 1 ){
			icon = "fa-spinner fa-spin";
		} else if (state == 3){
			icon = "fa-times-circle";
		} else if (state == 2 || state == 4){
			icon = "fa-check-circle";
		}
		return icon;
	}

	/**
	 * 加工单状态
	 * @param state
	 * @returns {string}
	 */
	function getErpMachinHeaderState(state){
		var icon = "";
		// 状态  1.新建  2.审核中  3.审核通过  4.审核拒绝  5.已完成
		if(state == 1 || state == 2 || state == 3){
			icon = "fa-spinner fa-spin";
		} else if (state == 4){
			icon = "fa-times-circle";
		} else if (state == 5){
			icon = "fa-check-circle";
		}
		return icon;
	}

	/**
	 * 加工单子单据状态
	 * @param state
	 * @returns {string}
	 */
	function getErpMachinChildState(state){
		var icon = "";
		// 状态  1.待验收  2.验收完成
		if(state == 1){
			icon = "fa-spinner fa-spin";
		} else if (state == 2){
			icon = "fa-check-circle";
		}
		return icon;
	}

	/**
	 * 生产计划单状态
	 * @param state
	 * @returns {string}
	 */
	function getErpProductionHeaderState(state){
		var icon = "";
		// 状态  0.删除  1.新建  2.审核中  3.审核通过  4.审核不通过  5.加工完成
		if(state == 1 || state == 2 || state == 3){
			icon = "fa-spinner fa-spin";
		} else if (state == 4){
			icon = "fa-times-circle";
		} else if (state == 5){
			icon = "fa-check-circle";
		}
		return icon;
	}

	/**
	 * 领料单/退料单/补料单状态
	 * @param state
	 * @returns {string}
	 */
	function getErpPickHeaderState(state){
		var icon = "";
		// 状态  0未审核  1.审核中  2.审核通过-进行领料和退料  3.审核拒绝
		if(state == 0 || state == 1){
			icon = "fa-spinner fa-spin";
		} else if (state == 3){
			icon = "fa-times-circle";
		} else if (state == 2){
			icon = "fa-check-circle";
		}
		return icon;
	}

});