
// 根据那一列的值进行变化,默认根据数量
var showTdByEdit = 'rkNum';

// 兼容动态表单
var layedit, form;

// 采购退货
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
	var serviceClassName = sysServiceMation["outIsPurchaseReturns"]["key"];

	var inoutitemHtml = "";//支出项目

	var selOption = getFileContent('tpl/template/select-option.tpl');
	//已经选择的商品集合key：表格的行trId，value：商品信息
	var allChooseProduct = {};

	// 获取单据提交类型
	var submitType = erpOrderUtil.getSubmitTypeByKey(serviceClassName);

	// 单据时间
	laydate.render({elem: '#operTime', type: 'datetime', value: getFormatDate(), trigger: 'click'});

	textool.init({eleId: 'remark', maxlength: 200});

	// 初始化账户
	systemCommonUtil.getSysAccountListByType(function (json) {
		// 加载账户数据
		$("#accountId").html(getDataUseHandlebars(selOption, json));
	});

	// 初始化支出项目
	systemCommonUtil.getSysInoutitemListByType(2, function (json) {
		// 加载支出项目
		inoutitemHtml = getDataUseHandlebars(selOption, json);
	});

	// 初始化仓库
	erpOrderUtil.getDepotList(function (json){
		// 加载仓库数据
		$("#depotId").html(getDataUseHandlebars(selOption, json));
	});

	// 商品
	initTableChooseUtil.initTable({
		id: "productList",
		cols: [
			{id: 'materialId', title: '商品(型号)', formType: 'chooseInput', width: '150', iconClassName: 'chooseProductBtn', verify: 'required'},
			{id: 'mUnitId', title: '单位', formType: 'select', width: '50', verify: 'required', layFilter: 'selectUnitProperty'},
			{id: 'allStock', title: '库存', formType: 'detail', width: '80'},
			{id: 'rkNum', title: '数量', formType: 'input', width: '80', className: 'change-input rkNum', verify: 'required|number', value: '1'},
			{id: 'unitPrice', title: '单价', formType: 'input', width: '80', className: 'change-input unitPrice', verify: 'required|money'},
			{id: 'amountOfMoney', title: '金额', formType: 'input', width: '80', className: 'change-input amountOfMoney', verify: 'required|money'},
			{id: 'taxRate', title: '税率(%)', formType: 'input', width: '80', className: 'change-input taxRate', verify: 'required|double', value: '0.00'},
			{id: 'taxMoney', title: '税额', formType: 'input', width: '80', className: 'change-input taxMoney', verify: 'required|money'},
			{id: 'taxUnitPrice', title: '含税单价', formType: 'input', width: '80', className: 'change-input taxUnitPrice', verify: 'required|money'},
			{id: 'taxLastMoney', title: '合计价税', formType: 'input', width: '80', className: 'change-input taxLastMoney', verify: 'required|money'},
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
		},
		form: form,
		minData: 1
	});

	// 其他费用
	initTableChooseUtil.initTable({
		id: "otherPriceTableList",
		cols: [
			{id: 'inoutitemId', title: '支出项目', formType: 'select', width: '120', verify: 'required', modelHtml: inoutitemHtml},
			{id: 'otherPrice', title: '费用合计：0.00', formType: 'input', width: '120', className: 'otherPrice', verify: 'required|money', colHeaderId: 'otherPriceTotal'}
		],
		deleteRowCallback: function (trcusid) {
			calculationPrice();
		},
		addRowCallback: function (trcusid) {
			calculationPrice();
		},
		form: form
	});

	// 加载动态表单
	dsFormUtil.loadPageByCode("dsFormShow", serviceClassName, null);

	matchingLanguage();

	// 商品规格加载变化事件
	mUnitChangeEvent(form, allChooseProduct, "normsEstimatePurchasePrice");

	// 仓库变化事件
	form.on('select(depotId)', function(data) {
		loadMaterialDepotStockByDepotId(data.value);
	});

	// 保存为草稿
	form.on('submit(formAddBean)', function(data) {
		if(winui.verifyForm(data.elem)) {
			saveData("1", "");
		}
		return false;
	});

	// 走工作流的提交审批
	form.on('submit(formSubOneBean)', function(data) {
		if(winui.verifyForm(data.elem)) {
			activitiUtil.startProcess(serviceClassName, null, function (approvalId) {
				saveData("2", approvalId);
			});
		}
		return false;
	});

	// 不走工作流的提交
	form.on('submit(formSubTwoBean)', function(data) {
		if(winui.verifyForm(data.elem)) {
			saveData("2", "");
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
			var material = allChooseProduct["tr" + thisRowKey];
			if (inTableDataArrayByAssetarId(material.materialId, item.mUnitId, tableData)) {
				winui.window.msg('一张单中不允许出现相同单位的商品信息.', {icon: 2, time: 2000});
				noError = true;
				return false;
			}
			item["materialId"] = material.materialId;
			item["depotId"] = $("#depotId").val();
			tableData.push(item);
		});
		if (noError) {
			return false;
		}

		// 其他费用
		var otherPriceResult = initTableChooseUtil.getDataList('otherPriceTableList');
		var otherMoney = 0;
		$.each(otherPriceResult.dataList, function(i, item) {
			otherMoney += parseFloat(isNull(item.otherPrice) ? 0 : item.otherPrice);
		});

		var params = {
			supplierId: sysSupplierUtil.supplierMation.id,
			operTime: $("#operTime").val(),
			accountId: $("#accountId").val(),
			payType: $("#payType").val(),
			remark: $("#remark").val(),
			discount: isNull($("#discount").val()) ? "0.00" : $("#discount").val(),
			discountMoney: isNull($("#discountMoney").val()) ? "0.00" : $("#discountMoney").val(),
			changeAmount: isNull($("#changeAmount").val()) ? "0.00" : $("#changeAmount").val(),
			depotheadStr: JSON.stringify(tableData),
			otherMoney: otherMoney.toFixed(2),
			otherMoneyList: JSON.stringify(otherPriceResult.dataList),
			submitType: submitType,
			subType: subType,
			approvalId: approvalId
		};
		AjaxPostUtil.request({url: flowableBasePath + "purchasereturns002", params: params, type: 'json', method: "POST", callback: function(json) {
			dsFormUtil.savePageData("dsFormShow", json.bean.id);
			parent.layer.close(index);
			parent.refreshCode = '0';
		}});
	}

	// 供应商选择
	$("body").on("click", "#supplierNameSel", function (e) {
		sysSupplierUtil.openSysSupplierChoosePage(function (supplierMation){
			$("#supplierName").val(supplierMation.supplierName);
		});
	});

	// 加载商品选择事件
	initChooseProductBtnEnent(form, function(trId, chooseProductMation) {
		// 商品赋值
		allChooseProduct[trId] = chooseProductMation;
	});

	$("body").on("click", "#cancle", function() {
		parent.layer.close(index);
	});
});