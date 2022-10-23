
// 已经选择的客户信息
var customerMation = {};

// 根据那一列的值进行变化,默认根据数量
var showTdByEdit = 'rkNum';

// 兼容动态表单
var layedit, form;

// 销售出库单
layui.config({
	base: basePath,
	version: skyeyeVersion
}).extend({
	window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'laydate', 'textool', 'tagEditor'].concat(dsFormUtil.mastHaveImport), function(exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
	var $ = layui.$,
		laydate = layui.laydate,
		textool = layui.textool;
	layedit = layui.layedit,
	form = layui.form;
	var serviceClassName = sysServiceMation["outIsSalesOutlet"]["key"];

	var inoutitemHtml = "";//支出项目
	var salesManList = new Array();//销售人员

	var selOption = getFileContent('tpl/template/select-option.tpl');
	// 已经选择的商品集合key：表格的行trId，value：商品信息
	var allChooseProduct = {};

	// 获取单据提交类型
	var submitType = "";

	// 单据时间
	laydate.render({elem: '#operTime', type: 'datetime', trigger: 'click'});

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
		// 初始化回显数据
		initDataShow();
	});

	// 初始化回显数据
	function initDataShow(){
		AjaxPostUtil.request({url: flowableBasePath + "salesoutlet003", params: {rowId: parent.rowId}, type: 'json', method: "GET", callback: function(json) {
			submitType = json.bean.submitType;
			// 客户信息赋值
			customerMation = {
				id: json.bean.organId,
				customName: json.bean.customName
			}
			$("#customName").val(json.bean.customName);//客户
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
			$("#otherPriceTotal").html("费用合计：" + json.bean.otherMoney.toFixed(2));

			initTable();

			// 回显商品列表
			initTableChooseUtil.deleteAllRow('productList');
			$.each(json.bean.norms, function(i, item) {
				if (i == 0) {
					$("#depotId").val(item.depotId);
				}
				var params = {
					"materialId": item.product.materialName + "(" + item.product.materialModel + ")",
					"mUnitId": {
						"html": getDataUseHandlebars(selOption, {rows: item.product.unitList}),
						"value": item.mUnitId
					},
					"allStock": item.allStock,
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

			// 回显其他费用
			initTableChooseUtil.deleteAllRow('otherPriceTableList');
			$.each(json.bean.otherMoneyList, function(i, item) {
				var params = {
					"inoutitemId": {
						"value": item.inoutitemId
					},
					"otherPrice": parseFloat(item.otherPrice).toFixed(2)
				};
				initTableChooseUtil.resetData('otherPriceTableList', params);
			});

			var userNames = "";
			salesManList = json.bean.userInfo;
			$.each(salesManList, function (i, item) {
				userNames += item.name + ',';
			});
			// 人员选择
			$('#salesMan').tagEditor({
				initialTags: userNames.split(','),
				placeholder: '请选择销售人员',
				editorTag: false,
				beforeTagDelete: function(field, editor, tags, val) {
					salesManList = [].concat(arrayUtil.removeArrayPointName(salesManList, val));
				}
			});
			erpOrderUtil.orderEditPageSetBtnBySubmitType(submitType, json.bean.state);

			textool.init({eleId: 'remark', maxlength: 200});

			// 加载动态表单
			dsFormUtil.loadPageToEditByObjectId("dsFormShow", json.bean.id);

			matchingLanguage();
			form.render();
		}});
	}

	function initTable() {
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
				calculatedTotalPrice();
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
	}

	// 商品规格加载变化事件
	mUnitChangeEvent(form, allChooseProduct, "salePrice");

	// 仓库变化事件
	form.on('select(depotId)', function(data) {
		loadMaterialDepotStockByDepotId(data.value);
	});

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

		//销售人员
		var salesMan = "";
		$.each(salesManList, function (i, item) {
			salesMan += item.id + ',';
		});

		var params = {
			supplierId: customerMation.id,//客户
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
			salesMan: salesMan,
			subType: subType,
			submitType: submitType,
			approvalId: approvalId,
			rowId: parent.rowId
		};
		AjaxPostUtil.request({url: flowableBasePath + "salesoutlet004", params: params, type: 'json', method: "PUT", callback: function(json) {
			dsFormUtil.savePageData("dsFormShow", json.bean.id);
			parent.layer.close(index);
			parent.refreshCode = '0';
		}});
	}

	//人员选择
	$("body").on("click", "#toSalesManSelPeople", function (e) {
		systemCommonUtil.userReturnList = [].concat(salesManList);
		systemCommonUtil.chooseOrNotMy = "1"; // 人员列表中是否包含自己--1.包含；其他参数不包含
		systemCommonUtil.chooseOrNotEmail = "2"; // 人员列表中是否必须绑定邮箱--1.必须；其他参数没必要
		systemCommonUtil.checkType = "1"; // 人员选择类型，1.多选；其他。单选
		systemCommonUtil.openSysUserStaffChoosePage(function (userReturnList) {
			// 重置数据
			salesManList = [].concat(systemCommonUtil.tagEditorResetData('salesMan', userReturnList));
		});
	});

	// 客户选择
	$("body").on("click", "#customMationSel", function (e) {
		_openNewWindows({
			url: "../../tpl/customermanage/customerChoose.html",
			title: "选择客户",
			pageId: "customerchooselist",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				$("#customName").val(customerMation.customName);
			}});
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