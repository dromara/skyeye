
// 生产订单信息
var productionMation = {};

// 根据那一列的值进行变化,默认根据数量
var showTdByEdit = 'rkNum';
// 表格的序号
var rowNum = 1;

// 兼容动态表单
var layedit, form;

// 采购订单
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

	var selOption = getFileContent('tpl/template/select-option.tpl');
	// 已经选择的商品集合key：表格的行trId，value：商品信息
	var allChooseProduct = {};

	// 获取单据提交类型
	var submitType = erpOrderUtil.getSubmitTypeByOrderType(systemOrderType["purchaseOrder"]["orderType"]);

	// 单据时间
	laydate.render({elem: '#operTime', type: 'datetime', value: getFormatDate(), trigger: 'click'});

	// 计划完成日期
	laydate.render({elem: '#planComplateTime', type: 'datetime', trigger: 'click'});

	textool.init({eleId: 'remark', maxlength: 200});

	// 初始化账户
	systemCommonUtil.getSysAccountListByType(function (json) {
		// 加载账户数据
		$("#accountId").html(getDataUseHandlebars(selOption, json));
	});

	initTableChooseUtil.initTable({
		id: "productList",
		cols: [
			{id: 'materialId', title: '商品(型号)', formType: 'chooseInput', width: '150', iconClassName: 'chooseProductBtn', verify: 'required'},
			{id: 'unitId', title: '单位', formType: 'select', width: '50', verify: 'required', layFilter: 'selectUnitProperty', saveKey: 'mUnitId'},
			{id: 'currentTock', title: '库存', formType: 'detail', width: '80'},
			{id: 'rkNum', title: '数量', formType: 'input', width: '80', className: 'change-input rkNum', verify: 'required|number', value: '1', saveKey: 'rkNum'},
			{id: 'unitPrice', title: '单价', formType: 'input', width: '80', className: 'change-input unitPrice', verify: 'required|money', saveKey: 'unitPrice'},
			{id: 'amountOfMoney', title: '金额', formType: 'input', width: '80', className: 'change-input amountOfMoney', verify: 'required|money'},
			{id: 'taxRate', title: '税率(%)', formType: 'input', width: '80', className: 'change-input taxRate', verify: 'required|double', value: '0.00', saveKey: 'taxRate'},
			{id: 'taxMoney', title: '税额', formType: 'input', width: '80', className: 'change-input taxMoney', verify: 'required|money', saveKey: 'taxMoney'},
			{id: 'taxUnitPrice', title: '含税单价', formType: 'input', width: '80', className: 'change-input taxUnitPrice', verify: 'required|money', saveKey: 'taxUnitPrice'},
			{id: 'taxLastMoney', title: '合计价税', formType: 'input', width: '80', className: 'change-input taxLastMoney', verify: 'required|money', saveKey: 'taxLastMoney'},
			{id: 'remark', title: '备注', formType: 'input', width: '100', saveKey: 'remark'}
		],
		deleteRowCallback: function (trId) {
			allChooseProduct[trId] = undefined;
			// 计算价格
			calculatedTotalPrice();
		},
		addRowCallback: function (rowIndexStr) {
			// 设置根据某列变化的颜色
			$("." + showTdByEdit).parent().css({'background-color': '#e6e6e6'});
		},
		form: form
	});

	// 加载动态表单
	dsFormUtil.loadPageByCode("dsFormShow", sysDsFormWithCodeType["purchaseOrder"]["code"], null);

	matchingLanguage();
	// 商品规格加载变化事件
	form.on('select(selectUnitProperty)', function(data) {
		var thisRowValue = data.value;
		var thisRowNum = data.elem.id.replace("unitId", "");//获取当前行
		// 当前当前行选中的商品信息
		if(!isNull(thisRowValue) && thisRowValue != '请选择') {
			var product = allChooseProduct["tr" + thisRowNum.toString()];
			$.each(product.unitList, function(j, bean) {
				if(thisRowValue == bean.id){//获取规格
					//获取当前行数量
					var rkNum = parseInt($("#rkNum" + thisRowNum).val());
					$("#unitPrice" + thisRowNum).val(bean.estimatePurchasePrice.toFixed(2));//单价
					$("#amountOfMoney" + thisRowNum).val((rkNum * parseFloat(bean.estimatePurchasePrice)).toFixed(2));//金额
					return false;
				}
			});
		} else {
			$("#unitPrice" + thisRowNum).val("0.00");//重置单价为空
			$("#amountOfMoney" + thisRowNum).val("0.00");//重置金额为空
		}

		// 加载库存
		loadTockByDepotAndMUnit(thisRowNum, "");
		// 计算价格
		calculatedTotalPrice();
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
			activitiUtil.startProcess(sysActivitiModel["purchaseOrder"]["key"], function (approvalId) {
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

	function saveData(subType, approvalId){
		//获取已选商品数据
		var rowTr = $("#useTable tr");
		if(rowTr.length == 0) {
			winui.window.msg('请选择商品.', {icon: 2, time: 2000});
			return false;
		}
		var tableData = new Array();
		var noError = false; //循环遍历表格数据时，是否有其他错误信息
		$.each(rowTr, function(i, item) {
			//获取行编号
			var rowNum = $(item).attr("trcusid").replace("tr", "");
			//表格数量对象
			var rkNum = $("#rkNum" + rowNum);
			if(parseInt(rkNum.val()) == 0) {
				rkNum.addClass("layui-form-danger");
				rkNum.focus();
				winui.window.msg('数量不能为0', {icon: 2, time: 2000});
				noError = true;
				return false;
			}
			//商品对象
			var product = allChooseProduct["tr" + rowNum.toString()];
			if(inTableDataArrayByAssetarId(product.productId, $("#unitId" + rowNum).val(), tableData)) {
				winui.window.msg('一张单中不允许出现相同单位的商品信息.', {icon: 2, time: 2000});
				noError = true;
				return false;
			}
			var row = {
				materialId: product.productId,
				mUnitId: $("#unitId" + rowNum).val(),
				rkNum: rkNum.val(),
				unitPrice: $("#unitPrice" + rowNum).val(),
				taxRate: $("#taxRate" + rowNum).val(),
				taxMoney: $("#taxMoney" + rowNum).val(),
				taxUnitPrice: $("#taxUnitPrice" + rowNum).val(),
				taxLastMoney: $("#taxLastMoney" + rowNum).val(),
				remark: $("#remark" + rowNum).val()
			};
			tableData.push(row);
		});
		if(noError) {
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
			planComplateTime: $("#planComplateTime").val(),
			productionId: productionMation.id,
			submitType: submitType,
			subType: subType,
			approvalId: approvalId
		};
		AjaxPostUtil.request({url: flowableBasePath + "purchaseorder002", params: params, type: 'json', method: "POST", callback: function(json) {
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

	//商品选择
	$("body").on("click", ".chooseProductBtn", function (e) {
		var trId = $(this).parent().parent().attr("trcusid");
		erpOrderUtil.openMaterialChooseChoosePage(function (chooseProductMation) {
			//获取表格行号
			var thisRowNum = trId.replace("tr", "");
			//商品赋值
			allChooseProduct[trId] = chooseProductMation;
			//表格商品名称赋值
			$("#materialId" + thisRowNum.toString()).val(allChooseProduct[trId].productName + "(" + allChooseProduct[trId].productModel + ")");
			//表格单位赋值
			$("#unitId" + thisRowNum.toString()).html(getDataUseHandlebars(selOption, {rows: allChooseProduct[trId].unitList}));
			form.render('select');
			//计算价格
			calculatedTotalPrice();
		});
	});

	//生产计划单选择
	$("body").on("click", "#productionOrderSel", function (e) {
		_openNewWindows({
			url: "../../tpl/erpProduction/erpProductionNoSuccessChoose.html",
			title: "选择生产计划单",
			pageId: "erpProductionNoSuccessChoose",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
				$("#productionOrder").val(productionMation.defaultNumber);
				//移除之前填写的所有行
				var checkRow = $("#useTable input[type='checkbox'][name='tableCheckRow']");
				$.each(checkRow, function(i, item) {
					//删除allChooseProduct已选择的商品信息
					var trId = $(item).parent().parent().attr("trcusid");
					allChooseProduct[trId] = undefined;
					//移除界面上的信息
					$(item).parent().parent().remove();
				});
				$.each(productionMation.norms, function(i, item){
					addRow();
					//将规格所属的商品信息加入到对象中存储
					allChooseProduct["tr" + (rowNum - 1)] = item.product;
					//单位回显
					$("#unitId" + (rowNum - 1)).html(getDataUseHandlebars(selOption, {rows: item.product.unitList}));
					$("#unitId" + (rowNum - 1)).val(item.normsId);
					//商品回显
					$("#materialId" + (rowNum - 1)).val(item.product.productName + "(" + item.product.productModel + ")");
					$("#currentTock" + (rowNum - 1)).html(item.currentTock);//库存回显
					$("#rkNum" + (rowNum - 1)).val(item.needNum);
					$("#unitPrice" + (rowNum - 1)).val(item.unitPrice.toFixed(2));
					$("#amountOfMoney" + (rowNum - 1)).val(item.allPrice.toFixed(2));
					$("#taxRate" + (rowNum - 1)).val(item.taxRate.toFixed(2));
					$("#taxMoney" + (rowNum - 1)).val(item.taxMoney.toFixed(2));
					$("#taxUnitPrice" + (rowNum - 1)).val(item.taxUnitPrice.toFixed(2));
					$("#taxLastMoney" + (rowNum - 1)).val(item.taxLastMoney.toFixed(2));
				});
				//渲染
				form.render();

				//计算价格
				calculatedTotalPrice();
			}});
	});

	$("body").on("click", "#cancle", function() {
		parent.layer.close(index);
	});
});