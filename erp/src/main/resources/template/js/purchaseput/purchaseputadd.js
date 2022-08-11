
// 根据那一列的值进行变化,默认根据数量
var showTdByEdit = 'rkNum';

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

	var inoutitemHtml = "";//支出项目

	var otherTemplate = $("#otherTemplate").html();
	var selOption = getFileContent('tpl/template/select-option.tpl');
	//已经选择的商品集合key：表格的行trId，value：商品信息
	var allChooseProduct = {};

	// 获取单据提交类型
	var submitType = erpOrderUtil.getSubmitTypeByOrderType(systemOrderType["putIsPurchase"]["orderType"]);

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

	initTableChooseUtil.initTable({
		id: "productList",
		cols: [
			{id: 'materialId', title: '商品(型号)', formType: 'chooseInput', width: '150', iconClassName: 'chooseProductBtn', verify: 'required'},
			{id: 'mUnitId', title: '单位', formType: 'select', width: '50', verify: 'required', layFilter: 'selectUnitProperty'},
			{id: 'currentTock', title: '库存', formType: 'detail', width: '80'},
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

	// 加载动态表单
	dsFormUtil.loadPageByCode("dsFormShow", sysDsFormWithCodeType["putIsPurchase"]["code"], null);

	matchingLanguage();

	// 商品规格加载变化事件
	mUnitChangeEvent(form);

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
			activitiUtil.startProcess(sysActivitiModel["putIsPurchase"]["key"], function (approvalId) {
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

		//获取其他费用
		var rowPriceTr = $("#otherPriceTable tr");
		var tablePriceData = new Array();
		var otherMoney = 0;
		$.each(rowPriceTr, function(i, item) {
			//获取行编号
			var rowNum = $(item).attr("trcusid").replace("tr", "");
			var row = {
				inoutitemId: $("#inoutitemId" + rowNum).val(),
				otherPrice: $("#otherPrice" + rowNum).val()
			};
			otherMoney += parseFloat(isNull($("#otherPrice" + rowNum).val()) ? 0 : $("#otherPrice" + rowNum).val());
			tablePriceData.push(row);
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
			otherMoneyList: JSON.stringify(tablePriceData),
			submitType: submitType,
			subType: subType,
			approvalId: approvalId
		};
		AjaxPostUtil.request({url: flowableBasePath + "purchaseput002", params: params, type: 'json', method: "POST", callback: function(json) {
			dsFormUtil.savePageData("dsFormShow", json.bean.id);
			parent.layer.close(index);
			parent.refreshCode = '0';
		}});
	}

/*********************** 商品表格操作 start ****************************/
	// 供应商选择
	$("body").on("click", "#supplierNameSel", function (e) {
		sysSupplierUtil.openSysSupplierChoosePage(function (supplierMation){
			$("#supplierName").val(supplierMation.supplierName);
		});
	});

	initChooseProductBtnEnent(form, function(trId, chooseProductMation) {
		// 商品赋值
		allChooseProduct[trId] = chooseProductMation;
	});
/*********************** 商品表格操作 end ****************************/

/*********************** 其他费用表格操作 start ****************************/

	//其他费用变化
	$("body").on("input", ".otherPrice", function() {
		//计算价格
		calculationPrice();
	});
	$("body").on("change", ".otherPrice", function() {
		//计算价格
		calculationPrice();
	});

	//计算其他费用总价格
	function calculationPrice(){
		var rowTr = $("#otherPriceTable tr");
		var allPrice = 0;
		$.each(rowTr, function(i, item) {
			//获取行坐标
			var rowNum = $(item).attr("trcusid").replace("tr", "");
			//获取
			var otherPrice = parseFloat(isNull($("#otherPrice" + rowNum).val()) ? 0 : $("#otherPrice" + rowNum).val());
			allPrice += otherPrice;
		});
		$("#otherPriceTotal").html("费用合计：" + allPrice.toFixed(2));
	}

	var priceNum = 1;
	//新增行
	$("body").on("click", "#addPriceRow", function() {
		addPriceRow();
	});

	//删除行
	$("body").on("click", "#deletePriceRow", function() {
		deletePriceRow();
	});

	//新增行
	function addPriceRow() {
		var par = {
			id: "row" + priceNum.toString(), //checkbox的id
			trId: "tr" + priceNum.toString(), //行的id
			inoutitemId: "inoutitemId" + priceNum.toString(), //支出项目id
			otherPrice: "otherPrice" + priceNum.toString() //金额id
		};
		$("#otherPriceTable").append(getDataUseHandlebars(otherTemplate, par));
		//赋值给支出项目
		$("#" + "inoutitemId" + priceNum.toString()).html(inoutitemHtml);
		form.render('select');
		form.render('checkbox');
		priceNum++;
	}

	//删除行
	function deletePriceRow() {
		var checkRow = $("#otherPriceTable input[type='checkbox'][name='tableCheckRow']:checked");
		if(checkRow.length > 0) {
			$.each(checkRow, function(i, item) {
				$(item).parent().parent().remove();
			});
		} else {
			winui.window.msg('请选择要删除的行', {icon: 2, time: 2000});
		}
		//计算价格
		calculationPrice();
	}
/*********************** 其他费用表格操作 end ****************************/

	$("body").on("click", "#cancle", function() {
		parent.layer.close(index);
	});
});