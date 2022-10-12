
// 生产订单信息
var productionMation = {};

// 根据那一列的值进行变化,默认根据数量
var showTdByEdit = 'rkNum';

// 兼容动态表单
var layedit, form;

// 采购订单
layui.config({
	base: basePath,
	version: skyeyeVersion
}).extend({
	window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'textool'].concat(dsFormUtil.mastHaveImport), function(exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
	var $ = layui.$,
		laydate = layui.laydate,
		textool = layui.textool;
	layedit = layui.layedit,
	form = layui.form;
	var serviceClassName = sysServiceMation["purchaseOrder"]["key"];

	var selOption = getFileContent('tpl/template/select-option.tpl');
	//已经选择的商品集合key：表格的行trId，value：商品信息
	var allChooseProduct = {};

	// 获取单据提交类型
	var submitType = "";

	// 单据时间
	laydate.render({elem: '#operTime', type: 'datetime', trigger: 'click'});

	// 计划完成日期
	laydate.render({elem: '#planComplateTime', type: 'datetime', trigger: 'click'});

	// 初始化账户
	systemCommonUtil.getSysAccountListByType(function (json) {
		// 加载账户数据
		$("#accountId").html(getDataUseHandlebars(selOption, json));
		// 渲染数据
		initDataToShow();
	});

	//渲染数据到页面
	function initDataToShow(){
		AjaxPostUtil.request({url: flowableBasePath + "purchaseorder004", params: {rowId: parent.rowId}, type: 'json', method: "GET", callback: function(json) {
			submitType = json.bean.submitType;
			// 供应商信息赋值
			sysSupplierUtil.supplierMation = {
				id: json.bean.organId,
				supplierName: json.bean.supplierName
			}

			//生产计划单信息
			productionMation = {
				id: isNull(json.bean.productionId) ? "" : json.bean.productionId,
				defaultNumber: isNull(json.bean.producitonOrderNum) ? "" : json.bean.producitonOrderNum
			}
			$("#productionOrder").val(productionMation.defaultNumber);

			$("#supplierName").val(json.bean.supplierName);//供应商
			$("#operTime").val(json.bean.operTime);
			$("#accountId").val(json.bean.accountId);
			$("#payType").val(json.bean.payType);
			$("#allPrice").html(json.bean.totalPrice.toFixed(2));
			$("#taxLastMoneyPrice").html(json.bean.taxLastMoneyPrice.toFixed(2));
			$("#remark").val(json.bean.remark);
			$("#discount").val(json.bean.discount.toFixed(2));
			$("#discountMoney").val(json.bean.discountMoney.toFixed(2));
			$("#discountLastMoney").html(json.bean.discountLastMoney.toFixed(2));
			$("#changeAmount").val(json.bean.changeAmount.toFixed(2));
			$("#arrears").html(json.bean.arrears.toFixed(2));
			$("#planComplateTime").val(json.bean.planComplateTime);

			initTable();
			initTableChooseUtil.deleteAllRow('productList');
			$.each(json.bean.norms, function(i, item) {
				var params = {
					"materialId": item.product.materialName + "(" + item.product.materialModel + ")",
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

			textool.init({eleId: 'remark', maxlength: 200});
			erpOrderUtil.orderEditPageSetBtnBySubmitType(submitType, json.bean.state);

			// 加载动态表单
			dsFormUtil.loadPageToEditByObjectId("dsFormShow", json.bean.id);

			matchingLanguage();
			form.render();
		}});
	}

	function initTable() {
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
				calculatedTotalPrice();
			},
			form: form,
			minData: 1
		});
	}

	// 商品规格加载变化事件
	mUnitChangeEvent(form, allChooseProduct, "estimatePurchasePrice");

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
			activitiUtil.startProcess(serviceClassName, null, function (approvalId) {
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
			var material = allChooseProduct["tr" + thisRowKey];
			if (inTableDataArrayByAssetarId(material.materialId, item.mUnitId, tableData)) {
				winui.window.msg('一张单中不允许出现相同单位的商品信息.', {icon: 2, time: 2000});
				noError = true;
				return false;
			}
			item["materialId"] = material.materialId;
			tableData.push(item);
		});
		if (noError) {
			return false;
		}

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
			productionId: productionMation.id,
			planComplateTime: $("#planComplateTime").val(),
			subType: subType,
			submitType: submitType,
			approvalId: approvalId,
			rowId: parent.rowId
		};
		AjaxPostUtil.request({url: flowableBasePath + "purchaseorder005", params: params, type: 'json', method: "PUT", callback: function(json) {
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

	initChooseProductBtnEnent(form, function(trId, chooseProductMation) {
		// 商品赋值
		allChooseProduct[trId] = chooseProductMation;
	});

	// 生产计划单选择
	$("body").on("click", "#productionOrderSel", function (e) {
		_openNewWindows({
			url: "../../tpl/erpProduction/erpProductionNoSuccessChoose.html",
			title: "选择生产计划单",
			pageId: "erpProductionNoSuccessChoose",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				$("#productionOrder").val(productionMation.defaultNumber);
				initTableChooseUtil.deleteAllRow('productList');
				$.each(productionMation.norms, function(i, item) {
					var params = {
						"materialId": item.product.materialName + "(" + item.product.materialModel + ")",
						"mUnitId": {
							"html": getDataUseHandlebars(selOption, {rows: item.product.unitList}),
							"value": item.normsId
						},
						"currentTock": item.currentTock,
						"rkNum": item.needNum,
						"unitPrice": item.unitPrice.toFixed(2),
						"amountOfMoney": item.allPrice.toFixed(2),
						"taxRate": item.taxRate.toFixed(2),
						"taxMoney": item.taxMoney.toFixed(2),
						"taxUnitPrice": item.taxUnitPrice.toFixed(2),
						"taxLastMoney": item.taxLastMoney.toFixed(2)
					};
					var trcusid = initTableChooseUtil.resetData('productList', params);
					// 将规格所属的商品信息加入到对象中存储
					allChooseProduct[trcusid] = item.product;
				});
				// 渲染
				form.render();

				// 计算价格
				calculatedTotalPrice();
			}});
	});

	$("body").on("click", "#cancle", function() {
		parent.layer.close(index);
	});
});