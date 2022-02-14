//商品信息
var productMation = {};

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

	var rowNum = 1; //表格的序号

	var usetableTemplate = $("#usetableTemplate").html();
	var beanTemplate = $("#beanTemplate").html();
	var selOption = getFileContent('tpl/template/select-option.tpl');
	//已经选择的商品集合key：表格的行trId，value：商品信息
	var allChooseProduct = {};

	// 获取单据提交类型
	var submitType = "";

	//加载单据数据
	var orderObject = [];
	showGrid({
		id: "showForm",
		url: reqBasePath + "retailreturns003",
		params: {rowId: parent.rowId},
		pagination: false,
		template: beanTemplate,
		method: "GET",
		ajaxSendAfter:function(json){
			//单据时间
			laydate.render({
				elem: '#operTime',
				type: 'datetime',
				trigger: 'click'
			});
			orderObject = json;
			initOtherMation();
		}
	});

	function initOtherMation() {
		// 初始化账户
		systemCommonUtil.getSysAccountListByType(function(json){
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
		//渲染列表项
		$.each(orderObject.bean.norms, function(i, item){
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
			$("#rkNum" + (rowNum - 1)).val(item.operNum);//数量回显
			$("#unitPrice" + (rowNum - 1)).val(item.unitPrice.toFixed(2));//单价回显
			$("#amountOfMoney" + (rowNum - 1)).val(item.allPrice.toFixed(2));//金额回显
			$("#remark" + (rowNum - 1)).val(item.remark);//备注回显
			//设置标识
			$("tr[trcusid='tr" + (rowNum - 1) + "']").attr("thisid", item.id);
		});

		erpOrderUtil.orderEditPageSetBtnBySubmitType(submitType, orderObject.bean.state);

		textool.init({
			eleId: 'remark',
			maxlength: 200,
			tools: ['count', 'copy', 'reset', 'clear']
		});

		// 加载动态表单
		dsFormUtil.loadPageToEditByObjectId("dsFormShow", orderObject.bean.id);

		matchingLanguage();
		form.render();
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
					$("#unitPrice" + thisRowNum).val(bean.retailPrice.toFixed(2));//单价
					$("#amountOfMoney" + thisRowNum).val((rkNum * parseFloat(bean.retailPrice)).toFixed(2));//金额
					return false;
				}
			});
		} else {
			$("#unitPrice" + thisRowNum).val("0.00");//重置单价为空
			$("#amountOfMoney" + thisRowNum).val("0.00");//重置金额为空
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

	var showTdByEdit = 'rkNum';//根据那一列的值进行变化,默认根据数量
	//数量变化
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

	//计算总价
	function calculatedTotalPrice(){
		var rowTr = $("#useTable tr");
		var allPrice = 0;
		$.each(rowTr, function(i, item) {
			//获取行坐标
			var rowNum = $(item).attr("trcusid").replace("tr", "");
			//获取数量
			var rkNum = parseInt(isNull($("#rkNum" + rowNum).val()) ? "0" : $("#rkNum" + rowNum).val());
			//获取单价
			var unitPrice = parseFloat(isNull($("#unitPrice" + rowNum).val()) ? "0" : $("#unitPrice" + rowNum).val());
			//获取单价
			var amountOfMoney = parseFloat(isNull($("#amountOfMoney" + rowNum).val()) ? "0" : $("#amountOfMoney" + rowNum).val());
			if("rkNum" === showTdByEdit){//数量
				//输出金额
				$("#amountOfMoney" + rowNum).val((rkNum * unitPrice).toFixed(2));
			}else if("unitPrice" === showTdByEdit){//单价
				//输出金额
				$("#amountOfMoney" + rowNum).val((rkNum * unitPrice).toFixed(2));
			}else if("amountOfMoney" === showTdByEdit){//金额
				//输出单价
				$("#unitPrice" + rowNum).val((amountOfMoney / rkNum).toFixed(2));
			}
			allPrice += parseFloat($("#amountOfMoney" + rowNum).val());
		});
		//获取收款金额
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
				depotId: $("#depotId").val(),
				materialId: $("#materialId" + rowNum).val(),
				mUnitId: $("#unitId" + rowNum).val(),
				rkNum: rkNum.val(),
				unitPrice: $("#unitPrice" + rowNum).val(),
				thisId: isNull($(item).attr("thisid")) ? "" : $(item).attr("thisid"),
				remark: $("#remark" + rowNum).val()
			};
			tableData.push(row);
		});
		if(noError) {
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
		AjaxPostUtil.request({url: reqBasePath + "retailreturns004", params: params, type: 'json', method: "PUT", callback: function(json) {
			if(json.returnCode == 0) {
				dsFormUtil.savePageData("dsFormShow", json.bean.id);
				parent.layer.close(index);
				parent.refreshCode = '0';
			} else {
				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
			}
		}, async: true});
	}

	//判断选中的商品是否也在数组中
	function inTableDataArrayByAssetarId(materialId, unitId, array) {
		var isIn = false;
		$.each(array, function(i, item) {
			if(item.mUnitId === unitId && item.materialId === materialId) {
				isIn = true;
				return false;
			}
		});
		return isIn;
	}

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

	// 商品选择
	$("body").on("click", ".chooseProductBtn", function(e){
		var trId = $(this).parent().parent().attr("trcusid");
		_openNewWindows({
			url: "../../tpl/material/materialChoose.html",
			title: "选择商品",
			pageId: "productlist",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
				if (refreshCode == '0') {
					//获取表格行号
					var thisRowNum = trId.replace("tr", "");
					//商品赋值
					allChooseProduct[trId] = productMation;
					//表格商品名称赋值
					$("#materialId" + thisRowNum.toString()).val(allChooseProduct[trId].productName + "(" + allChooseProduct[trId].productModel + ")");
					//表格单位赋值
					$("#unitId" + thisRowNum.toString()).html(getDataUseHandlebars(selOption, {rows: allChooseProduct[trId].unitList}));
					form.render('select');
					//计算价格
					calculatedTotalPrice();
				} else if (refreshCode == '-9999') {
					winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
				}
			}});
	});

	// 会员选择
	$("body").on("click", ".chooseMemberBtn", function(e){
		sysMemberUtil.openSysMemberChoosePage(function (memberMation){
			$("#memberId").val(memberMation.contacts);
		});
	});

	$("body").on("click", "#cancle", function() {
		parent.layer.close(index);
	});
});