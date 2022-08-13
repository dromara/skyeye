
// 兼容动态表单
var layedit, form;

layui.config({
	base: basePath,
	version: skyeyeVersion
}).extend({
	window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'laydate', 'textool'].concat(dsFormUtil.mastHaveImport), function(exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
	var $ = layui.$,
		laydate = layui.laydate,
		textool = layui.textool;

	layedit = layui.layedit,
	form = layui.form;

	var showTdByEdit = 'rkNum';//根据那一列的值进行变化,默认根据数量
	var beanTemplate = $("#beanTemplate").html();
	var selOption = getFileContent('tpl/template/select-option.tpl');
	// 已经选择的商品集合key：表格的行trId，value：商品信息
	var allChooseProduct = {};

	// 获取单据提交类型
	var submitType = "";

	// 加载单据数据
	var orderObject = [];
	showGrid({
		id: "showForm",
		url: flowableBasePath + "retailreturns003",
		params: {rowId: parent.rowId},
		pagination: false,
		template: beanTemplate,
		method: "GET",
		ajaxSendAfter:function (json) {
			// 单据时间
			laydate.render({elem: '#operTime', type: 'datetime', trigger: 'click'});
			orderObject = json;
			initOtherMation();
		}
	});

	function initOtherMation() {
		// 初始化账户
		systemCommonUtil.getSysAccountListByType(function (json) {
			$("#accountId").html(getDataUseHandlebars(selOption, json));
		});

		// 初始化仓库
		erpOrderUtil.getDepotList(function (json){
			$("#depotId").html(getDataUseHandlebars(selOption, json));
		});

		// 渲染数据到页面
		initDataToShow();
	}

	//渲染数据到页面
	function initDataToShow(){
		submitType = orderObject.bean.submitType;
		sysMemberUtil.memberMation = {
			id: orderObject.bean.organId,
			contacts: orderObject.bean.supplierName
		};

		$("#memberId").val(sysMemberUtil.memberMation.contacts);//会员
		$("#accountId").val(orderObject.bean.accountId);//账户
		$("#payType").val(orderObject.bean.payType);//付款类型

		initTable();

		// 回显商品列表
		initTableChooseUtil.deleteAllRow('productList');
		$.each(orderObject.bean.norms, function(i, item) {
			if (i == 0) {
				$("#depotId").val(item.depotId);
			}
			var params = {
				"materialId": item.product.productName + "(" + item.product.productModel + ")",
				"mUnitId": {
					"html": getDataUseHandlebars(selOption, {rows: item.product.unitList}),
					"value": item.mUnitId
				},
				"currentTock": item.currentTock,
				"rkNum": item.operNum,
				"unitPrice": item.unitPrice.toFixed(2),
				"amountOfMoney": item.allPrice.toFixed(2),
				"taxRate": item.taxRate.toFixed(2),
				"taxMoney": item.taxMoney.toFixed(2),
				"taxUnitPrice": item.taxUnitPrice.toFixed(2),
				"taxLastMoney": item.taxLastMoney.toFixed(2),
				"remark": item.remark
			};
			var trcusid = initTableChooseUtil.resetData('productList', params);
			// 将规格所属的商品信息加入到对象中存储
			allChooseProduct[trcusid] = item.product;
		});

		erpOrderUtil.orderEditPageSetBtnBySubmitType(submitType, orderObject.bean.state);

		textool.init({eleId: 'remark', maxlength: 200});

		// 加载动态表单
		dsFormUtil.loadPageToEditByObjectId("dsFormShow", orderObject.bean.id);

		matchingLanguage();
		form.render();
	}

	function initTable() {
		// 商品
		initTableChooseUtil.initTable({
			id: "productList",
			cols: [
				{id: 'materialId', title: '商品(型号)', formType: 'chooseInput', width: '150', iconClassName: 'chooseProductBtn', verify: 'required'},
				{id: 'mUnitId', title: '单位', formType: 'select', width: '50', verify: 'required', layFilter: 'selectUnitProperty'},
				{id: 'currentTock', title: '库存', formType: 'detail', width: '80'},
				{id: 'rkNum', title: '数量', formType: 'input', width: '80', className: 'change-input rkNum', verify: 'required|number', value: '1'},
				{id: 'unitPrice', title: '单价', formType: 'input', width: '80', className: 'change-input unitPrice', verify: 'required|money'},
				{id: 'amountOfMoney', title: '金额', formType: 'input', width: '80', className: 'change-input amountOfMoney', verify: 'required|money'},
				{id: 'remark', title: '备注', formType: 'input', width: '100'}
			],
			deleteRowCallback: function (trcusid) {
				delete allChooseProduct[trcusid];
				// 计算价格
				calculatedTotalPrice();
			},
			addRowCallback: function (trcusid) {
				// 设置根据某列变化的颜色
				$("." + showTdByEdit).parent().css({'background-color': '#e6e6e6'});
				calculatedTotalPrice();
			},
			form: form,
			minData: 1
		});
	}

	// 商品规格加载变化事件
	mUnitChangeEvent(form, allChooseProduct, "retailPrice", calculatedTotalPrice);

	// 仓库变化事件
	form.on('select(depotId)', function(data) {
		loadMaterialDepotStockByDepotId(data.value);
	});

	// 数量变化
	$("body").on("input", ".rkNum, .unitPrice, .amountOfMoney", function() {
		if($(this).attr("class").replace("layui-input change-input ", "") != showTdByEdit){
			showTdByEdit = $(this).attr("class").replace("layui-input change-input ", "");
			$(".change-input").parent().removeAttr("style");
			$("." + showTdByEdit).parent().css({'background-color': '#e6e6e6'});
		}
		calculatedTotalPrice();
	});
	$("body").on("change", ".rkNum, .unitPrice, .amountOfMoney", function() {
		if($(this).attr("class").replace("layui-input change-input ", "") != showTdByEdit){
			showTdByEdit = $(this).attr("class").replace("layui-input change-input ", "");
			$(".change-input").parent().removeAttr("style");
			$("." + showTdByEdit).parent().css({'background-color': '#e6e6e6'});
		}
		calculatedTotalPrice();
	});

	//收款金额变化
	$("body").on("input", "#changeAmount", function() {
		calculatedTotalPrice();
	});
	$("body").on("change", "#changeAmount", function() {
		calculatedTotalPrice();
	});

	// 计算总价
	function calculatedTotalPrice(){
		var allPrice = 0;
		$.each(initTableChooseUtil.getDataRowIndex('productList'), function(i, item) {
			// 获取行坐标
			var thisRowKey = item;
			// 获取数量
			var rkNum = parseInt(isNull($("#rkNum" + thisRowKey).val()) ? "0" : $("#rkNum" + thisRowKey).val());
			// 获取单价
			var unitPrice = parseFloat(isNull($("#unitPrice" + thisRowKey).val()) ? "0" : $("#unitPrice" + thisRowKey).val());
			// 获取金额
			var amountOfMoney = parseFloat(isNull($("#amountOfMoney" + thisRowKey).val()) ? "0" : $("#amountOfMoney" + thisRowKey).val());
			if("rkNum" === showTdByEdit){//数量
				// 输出金额
				$("#amountOfMoney" + thisRowKey).val((rkNum * unitPrice).toFixed(2));
			}else if("unitPrice" === showTdByEdit){//单价
				// 输出金额
				$("#amountOfMoney" + thisRowKey).val((rkNum * unitPrice).toFixed(2));
			}else if("amountOfMoney" === showTdByEdit){//金额
				// 输出单价
				$("#unitPrice" + thisRowKey).val((amountOfMoney / rkNum).toFixed(2));
			}
			allPrice += parseFloat($("#amountOfMoney" + thisRowKey).val());
		});
		// 获取收款金额
		var changeAmount = parseFloat(isNull($("#changeAmount").val()) ? "0" : $("#changeAmount").val());
		$("#allPrice").html(allPrice.toFixed(2));
		$("#giveChange").html((changeAmount - allPrice).toFixed(2));
	}

	// 保存为草稿
	form.on('submit(formEditBean)', function(data) {
		if(winui.verifyForm(data.elem)) {
			saveData('1', "");
		}
		return false;
	});

	// 走工作流的提交审批
	form.on('submit(formSubOneBean)', function(data) {
		if(winui.verifyForm(data.elem)) {
			activitiUtil.startProcess(sysActivitiModel["putIsRetailReturns"]["key"], function (approvalId) {
				saveData("2", approvalId);
			});
		}
		return false;
	});

	// 不走工作流的提交
	form.on('submit(formSubTwoBean)', function(data) {
		if(winui.verifyForm(data.elem)) {
			saveData('2', "");
		}
		return false;
	});

	// 工作流中保存
	form.on('submit(save)', function(data) {
		if(winui.verifyForm(data.elem)) {
			saveData('3', "");
		}
		return false;
	});

	function saveData(subType, approvalId) {
		var result = initTableChooseUtil.getDataList('productList');
		if (!result.checkResult) {
			return false;
		}
		var noError = false;
		var tableData = [];
		$.each(result.dataList, function(i, item) {
			//获取行编号
			var thisRowKey = item["trcusid"].replace("tr", "");
			if (parseInt(item.rkNum) == 0) {
				$("#rkNum" + thisRowKey).addClass("layui-form-danger");
				$("#rkNum" + thisRowKey).focus();
				winui.window.msg('数量不能为0', {icon: 2, time: 2000});
				noError = true;
				return false;
			}
			//商品对象
			var product = allChooseProduct["tr" + thisRowKey];
			if (inTableDataArrayByAssetarId(product.productId, item.mUnitId, tableData)) {
				winui.window.msg('一张单中不允许出现相同单位的商品信息.', {icon: 2, time: 2000});
				noError = true;
				return false;
			}
			item["materialId"] = product.productId;
			item["depotId"] = $("#depotId").val();
			tableData.push(item);
		});
		if (noError) {
			return false;
		}

		var params = {
			supplierId: sysMemberUtil.memberMation.id,
			operTime: $("#operTime").val(),
			accountId: $("#accountId").val(),
			payType: $("#payType").val(),
			remark: $("#remark").val(),
			depotheadStr: JSON.stringify(tableData),
			changeAmount: $("#changeAmount").val(),
			giveChange: $("#giveChange").html(),
			subType: subType,
			submitType: submitType,
			approvalId: approvalId,
			rowId: parent.rowId
		};
		AjaxPostUtil.request({url: flowableBasePath + "retailreturns004", params: params, type: 'json', method: "PUT", callback: function(json) {
			dsFormUtil.savePageData("dsFormShow", json.bean.id);
			parent.layer.close(index);
			parent.refreshCode = '0';
		}, async: true});
	}

	// 加载商品选择事件
	initChooseProductBtnEnent(form, function(trId, chooseProductMation) {
		// 商品赋值
		allChooseProduct[trId] = chooseProductMation;
	}, calculatedTotalPrice);

	// 会员选择
	$("body").on("click", ".chooseMemberBtn", function (e) {
		sysMemberUtil.openSysMemberChoosePage(function (memberMation){
			$("#memberId").val(memberMation.contacts);
		});
	});

	$("body").on("click", "#cancle", function() {
		parent.layer.close(index);
	});
});