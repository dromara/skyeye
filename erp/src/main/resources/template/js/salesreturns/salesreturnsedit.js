
//已经选择的客户信息
var customerMation = {};

//根据那一列的值进行变化,默认根据数量
var showTdByEdit = 'rkNum';
//表格的序号
var rowNum = 1;

// 兼容动态表单
var layedit, form;

// 销售退货单
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

	var inoutitemHtml = "";//支出项目
	var salesManList = new Array();//销售人员
	var priceNum = 1;

	var usetableTemplate = $("#usetableTemplate").html(),
		otherTemplate = $("#otherTemplate").html();
	var selOption = getFileContent('tpl/template/select-option.tpl');
	//已经选择的商品集合key：表格的行trId，value：商品信息
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
		AjaxPostUtil.request({url: flowableBasePath + "salesreturns003", params: {rowId: parent.rowId}, type: 'json', method: "GET", callback: function(json) {
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

			//加载列表项
			$.each(json.bean.norms, function(i, item){
				addRow();
				if(i == 0){
					$("#depotId").val(item.depotId);
				}
				//将规格所属的商品信息加入到对象中存储
				allChooseProduct["tr" + (rowNum - 1)] = item.product;
				//单位回显
				$("#unitId" + (rowNum - 1)).html(getDataUseHandlebars(selOption, {rows: item.product.unitList}));
				$("#unitId" + (rowNum - 1)).val(item.mUnitId);
				//商品回显
				$("#materialId" + (rowNum - 1)).val(item.product.productName + "(" + item.product.productModel + ")");
				$("#currentTock" + (rowNum - 1)).html(item.currentTock);//库存回显
				$("#rkNum" + (rowNum - 1)).val(item.operNum);
				$("#unitPrice" + (rowNum - 1)).val(item.unitPrice.toFixed(2));
				$("#amountOfMoney" + (rowNum - 1)).val(item.allPrice.toFixed(2));
				$("#taxRate" + (rowNum - 1)).val(item.taxRate.toFixed(2));
				$("#taxMoney" + (rowNum - 1)).val(item.taxMoney.toFixed(2));
				$("#taxUnitPrice" + (rowNum - 1)).val(item.taxUnitPrice.toFixed(2));
				$("#taxLastMoney" + (rowNum - 1)).val(item.taxLastMoney.toFixed(2));
				$("#remark" + (rowNum - 1)).val(item.remark);
			});

			//加载其他费用
			$.each(json.bean.otherMoneyList, function(i, item){
				addPriceRow();
				$("#inoutitemId" + (priceNum - 1)).val(item.inoutitemId);
				$("#otherPrice" + (priceNum - 1)).val(parseFloat(item.otherPrice).toFixed(2));
			});

			var userNames = "";
			salesManList = json.bean.userInfo;
			$.each(salesManList, function (i, item) {
				userNames += item.name + ',';
			});
			//人员选择
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

	//商品规格加载变化事件
	form.on('select(selectUnitProperty)', function(data) {
		var thisRowValue = data.value;
		var thisRowNum = data.elem.id.replace("unitId", "");//获取当前行
		//当前当前行选中的商品信息
		if(!isNull(thisRowValue) && thisRowValue != '请选择') {
			var product = allChooseProduct["tr" + thisRowNum.toString()];
			$.each(product.unitList, function(j, bean) {
				if(thisRowValue == bean.id){//获取规格
					//获取当前行数量
					var rkNum = parseInt($("#rkNum" + thisRowNum).val());
					$("#unitPrice" + thisRowNum).val(bean.salePrice.toFixed(2));//单价
					return false;
				}
			});
		} else {
			$("#unitPrice" + thisRowNum).val("0.00");//重置单价为空
		}

		//加载库存
		loadTockByDepotAndMUnit(thisRowNum, $("#depotId").val());
		//计算价格
		calculatedTotalPrice();
	});

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
			activitiUtil.startProcess(sysActivitiModel["putIsSalesReturns"]["key"], function (approvalId) {
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
		if(isNull(customerMation.id)){
			winui.window.msg('请选择客户.', {icon: 2, time: 2000});
			return false;
		}
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
				depotId: $("#depotId").val(),
				materialId: $("#materialId" + rowNum).val(),
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
			otherMoneyList: JSON.stringify(tablePriceData),
			salesMan: salesMan,
			subType: subType,
			submitType: submitType,
			approvalId: approvalId,
			rowId: parent.rowId
		};
		AjaxPostUtil.request({url: flowableBasePath + "salesreturns004", params: params, type: 'json', method: "PUT", callback: function(json) {
			dsFormUtil.savePageData("dsFormShow", json.bean.id);
			parent.layer.close(index);
			parent.refreshCode = '0';
		}});
	}

	// 人员选择
	$("body").on("click", "#toSalesManSelPeople", function(e) {
		systemCommonUtil.userReturnList = [].concat(salesManList);
		systemCommonUtil.chooseOrNotMy = "1"; // 人员列表中是否包含自己--1.包含；其他参数不包含
		systemCommonUtil.chooseOrNotEmail = "2"; // 人员列表中是否必须绑定邮箱--1.必须；其他参数没必要
		systemCommonUtil.checkType = "1"; // 人员选择类型，1.多选；其他。单选
		systemCommonUtil.openSysUserStaffChoosePage(function (userReturnList) {
			// 重置数据
			salesManList = [].concat(systemCommonUtil.tagEditorResetData('salesMan', userReturnList));
		});
	});

/*********************** 商品表格操作 start ****************************/
	//新增行
	$("body").on("click", "#addRow", function() {
		addRow();
	});

	//删除行
	$("body").on("click", "#deleteRow", function() {
		deleteRow();
		//计算价格
		calculatedTotalPrice();
	});

	//新增行
	function addRow() {
		var par = {
			id: "row" + rowNum.toString(), //checkbox的id
			trId: "tr" + rowNum.toString(), //行的id
			materialId: "materialId" + rowNum.toString(), //商品id
			unitId: "unitId" + rowNum.toString(), //规格id
			currentTock: "currentTock" + rowNum.toString(), //库存id
			rkNum: "rkNum" + rowNum.toString(), //数量id
			unitPrice: "unitPrice"  + rowNum.toString(), //单价id
			amountOfMoney: "amountOfMoney"  + rowNum.toString(), //金额id
			taxRate: "taxRate"  + rowNum.toString(), //税率id
			taxMoney: "taxMoney"  + rowNum.toString(), //税额id
			taxUnitPrice: "taxUnitPrice"  + rowNum.toString(), //含税单价id
			taxLastMoney: "taxLastMoney"  + rowNum.toString(), //含税合计id
			remark: "remark" + rowNum.toString() //备注id
		};
		$("#useTable").append(getDataUseHandlebars(usetableTemplate, par));
		form.render();
		rowNum++;
		//设置根据某列变化的颜色
		$("." + showTdByEdit).parent().css({'background-color': '#e6e6e6'});
	}

	//删除行
	function deleteRow() {
		var checkRow = $("#useTable input[type='checkbox'][name='tableCheckRow']:checked");
		if(checkRow.length > 0) {
			$.each(checkRow, function(i, item) {
				//删除allChooseProduct已选择的商品信息
				var trId = $(item).parent().parent().attr("trcusid");
				allChooseProduct[trId] = undefined;
				//移除界面上的信息
				$(item).parent().parent().remove();
			});
		} else {
			winui.window.msg('请选择要删除的行', {icon: 2, time: 2000});
		}
	}

	//客户选择
	$("body").on("click", "#customMationSel", function (e) {
		_openNewWindows({
			url: "../../tpl/customermanage/customerChoose.html",
			title: "选择客户",
			pageId: "customerchooselist",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
				$("#customName").val(customerMation.customName);
			}});
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