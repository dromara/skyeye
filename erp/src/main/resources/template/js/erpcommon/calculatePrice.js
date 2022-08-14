
// 计算总价
function calculatedTotalPrice() {
	var allPrice = 0, taxLastMoneyPrice = 0;
	$.each(initTableChooseUtil.getDataRowIndex('productList'), function (i, item) {
		// 获取行坐标
		var thisRowKey = item;
		// 获取数量
		var rkNum = parseInt(isNull($("#rkNum" + thisRowKey).val()) ? 0 : $("#rkNum" + thisRowKey).val());
		// 获取单价
		var unitPrice = parseFloat(isNull($("#unitPrice" + thisRowKey).val()) ? 0 : $("#unitPrice" + thisRowKey).val());
		// 获取税率
		var taxRate = parseFloat(isNull($("#taxRate" + thisRowKey).val()) ? 0 : $("#taxRate" + thisRowKey).val()) / 100;
		if ('rkNum' === showTdByEdit) {//数量
			//输出金额
			$("#amountOfMoney" + thisRowKey).val((rkNum * unitPrice).toFixed(2));
			//输出税额=数量*税率*单价
			$("#taxMoney" + thisRowKey).val((rkNum * taxRate * unitPrice).toFixed(2));
			//输出含税单价
			$("#taxUnitPrice" + thisRowKey).val((taxRate * unitPrice + unitPrice).toFixed(2));
			//输出合计价税
			$("#taxLastMoney" + thisRowKey).val((rkNum * taxRate * unitPrice + rkNum * unitPrice).toFixed(2));
		} else if ('unitPrice' === showTdByEdit) {//单价
			//输出金额
			$("#amountOfMoney" + thisRowKey).val((rkNum * unitPrice).toFixed(2));
			//输出税额=数量*税率*单价
			$("#taxMoney" + thisRowKey).val((rkNum * taxRate * unitPrice).toFixed(2));
			//输出含税单价
			$("#taxUnitPrice" + thisRowKey).val((taxRate * unitPrice + unitPrice).toFixed(2));
			//输出合计价税
			$("#taxLastMoney" + thisRowKey).val((rkNum * taxRate * unitPrice + rkNum * unitPrice).toFixed(2));
		} else if ('amountOfMoney' === showTdByEdit) {//金额
			//获取金额
			var amountOfMoney = parseFloat(isNull($("#amountOfMoney" + thisRowKey).val()) ? 0 : $("#amountOfMoney" + thisRowKey).val());
			//输出税额=金额*税率
			$("#taxMoney" + thisRowKey).val((amountOfMoney * taxRate).toFixed(2));
			//输出单价,含税单价,合计价税
			if (rkNum != 0) {
				$("#unitPrice" + thisRowKey).val((amountOfMoney / rkNum).toFixed(2));
				$("#taxUnitPrice" + thisRowKey).val((amountOfMoney / rkNum * taxRate + amountOfMoney / rkNum).toFixed(2));
				$("#taxLastMoney" + thisRowKey).val((amountOfMoney * taxRate + amountOfMoney).toFixed(2));
			} else {
				$("#unitPrice" + thisRowKey).val('0.00');
				$("#taxUnitPrice" + thisRowKey).val('0.00');
				$("#taxLastMoney" + thisRowKey).val('0.00');
			}
		} else if ('taxRate' === showTdByEdit) {//税率
			//输出金额
			$("#amountOfMoney" + thisRowKey).val((rkNum * unitPrice).toFixed(2));
			//输出税额=数量*税率*单价
			$("#taxMoney" + thisRowKey).val((rkNum * taxRate * unitPrice).toFixed(2));
			//输出含税单价
			$("#taxUnitPrice" + thisRowKey).val((taxRate * unitPrice + unitPrice).toFixed(2));
			//输出合计价税
			$("#taxLastMoney" + thisRowKey).val((rkNum * taxRate * unitPrice + rkNum * unitPrice).toFixed(2));
		} else if ('taxMoney' === showTdByEdit) {//税额
			//获取税额
			var taxMoney = parseFloat(isNull($("#taxMoney" + thisRowKey).val()) ? 0 : $("#taxMoney" + thisRowKey).val());
			//输出金额
			$("#amountOfMoney" + thisRowKey).val((rkNum * unitPrice).toFixed(2));
			//获取金额
			var amountOfMoney = parseFloat(isNull($("#amountOfMoney" + thisRowKey).val()) ? 0 : $("#amountOfMoney" + thisRowKey).val());
			//输出含税单价,合计价税,税率
			if (rkNum != 0) {
				if (unitPrice != 0) {
					$("#taxUnitPrice" + thisRowKey).val((taxMoney / rkNum + unitPrice).toFixed(2));
					$("#taxRate" + thisRowKey).val((taxMoney / unitPrice / rkNum * 100).toFixed(2));
				} else {
					$("#taxUnitPrice" + thisRowKey).val('0.00');
					$("#taxRate" + thisRowKey).val('0.00');
					$("#unitPrice" + thisRowKey).val('0.00');
					$("#amountOfMoney" + thisRowKey).val('0.00');
				}
				if (amountOfMoney != 0) {
					$("#taxLastMoney" + thisRowKey).val((amountOfMoney + taxMoney).toFixed(2));
				} else {
					$("#taxLastMoney" + thisRowKey).val('0.00');
				}
			} else {
				$("#taxUnitPrice" + thisRowKey).val('0.00');
				$("#taxLastMoney" + thisRowKey).val('0.00');
			}
		} else if ('taxUnitPrice' === showTdByEdit) {//含税单价
			//获取含税单价
			var taxUnitPrice = parseFloat(isNull($("#taxUnitPrice" + thisRowKey).val()) ? 0 : $("#taxUnitPrice" + thisRowKey).val());
			if (taxUnitPrice == 0) {
				$("#taxLastMoney" + thisRowKey).val('0.00');
				$("#unitPrice" + thisRowKey).val('0.00');
				$("#amountOfMoney" + thisRowKey).val('0.00');
				$("#taxMoney" + thisRowKey).val('0.00');
				$("#taxRate" + thisRowKey).val('0.00');
				return;
			}
			//输出合计价税,税额,税率
			if (unitPrice != 0) {
				if (rkNum != 0) {
					$("#taxLastMoney" + thisRowKey).val((taxUnitPrice * rkNum).toFixed(2));
					$("#amountOfMoney" + thisRowKey).val((unitPrice * thisRowKey).toFixed(2));
				} else {
					$("#taxLastMoney" + thisRowKey).val('0.00');
					$("#amountOfMoney" + thisRowKey).val('0.00');
				}
				$("#taxMoney" + thisRowKey).val((taxUnitPrice - unitPrice).toFixed(2));
				$("#taxRate" + thisRowKey).val(((taxUnitPrice / unitPrice - 1) * 100).toFixed(2));

			} else {
				$("#taxLastMoney" + thisRowKey).val('0.00');
				$("#unitPrice" + thisRowKey).val('0.00');
				$("#amountOfMoney" + thisRowKey).val('0.00');
				$("#taxMoney" + thisRowKey).val('0.00');
				$("#taxRate" + thisRowKey).val('0.00');
			}
		} else if ('taxLastMoney' === showTdByEdit) {//合计价税
			//获取合计价税
			var taxLastMoney = parseFloat(isNull($("#taxLastMoney" + thisRowKey).val()) ? 0 : $("#taxLastMoney" + thisRowKey).val());
			if (taxLastMoney == 0) {
				$("#taxUnitPrice" + thisRowKey).val('0.00');
				$("#unitPrice" + thisRowKey).val('0.00');
				$("#amountOfMoney" + thisRowKey).val('0.00');
				$("#taxMoney" + thisRowKey).val('0.00');
				$("#taxRate" + thisRowKey).val('0.00');
				return;
			}
			//输出含税单价,税额,税率
			if (rkNum != 0) {
				if (unitPrice != 0) {
					$("#taxUnitPrice" + thisRowKey).val((taxLastMoney / rkNum).toFixed(2));
					$("#taxMoney" + thisRowKey).val((taxLastMoney / rkNum - unitPrice).toFixed(2));
					$("#taxRate" + thisRowKey).val(((taxLastMoney / rkNum / unitPrice - 1) * 100).toFixed(2));
					$("#amountOfMoney" + thisRowKey).val((unitPrice * rkNum).toFixed(2));
				} else {
					$("#amountOfMoney" + thisRowKey).val('0.00');
					$("#taxMoney" + thisRowKey).val('0.00');
					$("#taxUnitPrice" + thisRowKey).val('0.00');
					$("#unitPrice" + thisRowKey).val('0.00');
				}
			} else {
				$("#taxUnitPrice" + thisRowKey).val('0.00');
				$("#unitPrice" + thisRowKey).val('0.00');
				$("#amountOfMoney" + thisRowKey).val('0.00');
				$("#taxMoney" + thisRowKey).val('0.00');
				$("#taxRate" + thisRowKey).val('0.00');
			}
		}
		allPrice += parseFloat($("#amountOfMoney" + thisRowKey).val());
		taxLastMoneyPrice += parseFloat($("#taxLastMoney" + thisRowKey).val());
	});
	$("#allPrice").html(allPrice.toFixed(2));
	$("#taxLastMoneyPrice").html(taxLastMoneyPrice.toFixed(2));

	// 优惠率计算
	var discount = parseFloat(isNull($("#discount").val()) ? 0 : $("#discount").val());
	// 输出优惠金额
	var discountMoney = (taxLastMoneyPrice * discount / 100).toFixed(2);
	$("#discountMoney").val(discountMoney);
	// 输出优惠后的金额
	$("#discountLastMoney").html((taxLastMoneyPrice - discountMoney).toFixed(2));
	// 输出本次付款
	$("#changeAmount").val((taxLastMoneyPrice - discountMoney).toFixed(2));
	// 输出欠款金额
	$("#arrears").html('0.00');
}

layui.define(["jquery"], function(exports) {
	var jQuery = layui.jquery;
	(function($) {
		// 数量变化,税率变化
		$("body").on("input", ".rkNum, .unitPrice, .amountOfMoney, .taxRate, .taxMoney, .taxUnitPrice, .taxLastMoney", function () {
			var clazz = $(this).attr("class").replace("layui-input", "").replace("change-input", "").replace("layui-form-danger", "").replace(/\s+/g, "");
			if (clazz != showTdByEdit) {
				showTdByEdit = clazz;
				$(".change-input").parent().removeAttr("style");
				$("." + showTdByEdit).parent().css({'background-color': '#e6e6e6'});
			}
			calculatedTotalPrice();
		});
		$("body").on("change", ".rkNum, .unitPrice, .amountOfMoney, .taxRate, .taxMoney, .taxUnitPrice, .taxLastMoney", function () {
			var clazz = $(this).attr("class").replace("layui-input", "").replace("change-input", "").replace("layui-form-danger", "").replace(/\s+/g, "");
			if (clazz != showTdByEdit) {
				showTdByEdit = clazz;
				$(".change-input").parent().removeAttr("style");
				$("." + showTdByEdit).parent().css({'background-color': '#e6e6e6'});
			}
			calculatedTotalPrice();
		});
		
		// 优惠率变化
		$("body").on("input", "#discount", function() {
			//获取价格合计
			var taxLastMoneyPrice = parseFloat(isNull($("#taxLastMoneyPrice").html()) ? 0 : $("#taxLastMoneyPrice").html());
			var discount = parseFloat(isNull($(this).val()) ? 0 : $(this).val());
			//输出优惠金额
			$("#discountMoney").val((taxLastMoneyPrice * discount / 100).toFixed(2));
			//输出优惠后的金额
			$("#discountLastMoney").html((taxLastMoneyPrice - (taxLastMoneyPrice * discount / 100)).toFixed(2));
			//输出本次付款
			$("#changeAmount").val((taxLastMoneyPrice - (taxLastMoneyPrice * discount / 100)).toFixed(2));
		});
		$("body").on("change", "#discount", function() {
			//获取价格合计
			var taxLastMoneyPrice = parseFloat(isNull($("#taxLastMoneyPrice").html()) ? 0 : $("#taxLastMoneyPrice").html());
			var discount = parseFloat(isNull($(this).val()) ? 0 : $(this).val());
			//输出优惠金额
			$("#discountMoney").val((taxLastMoneyPrice * discount / 100).toFixed(2));
			//输出优惠后的金额
			$("#discountLastMoney").html((taxLastMoneyPrice - (taxLastMoneyPrice * discount / 100)).toFixed(2));
			//输出本次付款
			$("#changeAmount").val((taxLastMoneyPrice - (taxLastMoneyPrice * discount / 100)).toFixed(2));
		});
		
		// 优惠金额变化
		$("body").on("input", "#discountMoney", function() {
			//获取价格合计
			var taxLastMoneyPrice = parseFloat(isNull($("#taxLastMoneyPrice").html()) ? 0 : $("#taxLastMoneyPrice").html());
			var discountMoney = parseFloat(isNull($(this).val()) ? 0 : $(this).val());
			//输出优惠率
			$("#discount").val((discountMoney / taxLastMoneyPrice * 100).toFixed(2));
			//输出优惠后的金额
			$("#discountLastMoney").html((taxLastMoneyPrice - discountMoney).toFixed(2));
			//输出本次付款
			$("#changeAmount").val((taxLastMoneyPrice - discountMoney).toFixed(2));
		});
		$("body").on("change", "#discountMoney", function() {
			//获取价格合计
			var taxLastMoneyPrice = parseFloat(isNull($("#taxLastMoneyPrice").html()) ? 0 : $("#taxLastMoneyPrice").html());
			var discountMoney = parseFloat(isNull($(this).val()) ? 0 : $(this).val());
			//输出优惠率
			$("#discount").val((discountMoney / taxLastMoneyPrice * 100).toFixed(2));
			//输出优惠后的金额
			$("#discountLastMoney").html((taxLastMoneyPrice - discountMoney).toFixed(2));
			//输出本次付款
			$("#changeAmount").val((taxLastMoneyPrice - discountMoney).toFixed(2));
		});
		
		// 本次付款变化
		$("body").on("input", "#changeAmount", function() {
			//获取优惠后的金额
			var discountLastMoney = parseFloat(isNull($("#discountLastMoney").html()) ? 0 : $("#discountLastMoney").html());
			var changeAmount = parseFloat(isNull($("#changeAmount").val()) ? 0 : $("#changeAmount").val());
			//输出欠款金额
			$("#arrears").html((discountLastMoney - changeAmount).toFixed(2));
		});
		$("body").on("change", "#changeAmount", function() {
			//获取优惠后的金额
			var discountLastMoney = parseFloat(isNull($("#discountLastMoney").html()) ? 0 : $("#discountLastMoney").html());
			var changeAmount = parseFloat(isNull($("#changeAmount").val()) ? 0 : $("#changeAmount").val());
			//输出欠款金额
			$("#arrears").html((discountLastMoney - changeAmount).toFixed(2));
		});

		// 其他费用变化
		$("body").on("input", ".otherPrice", function() {
			calculationPrice();
		});
		$("body").on("change", ".otherPrice", function() {
			calculationPrice();
		});

	})(jQuery);
});

// 计算其他费用总价格
function calculationPrice(){
	var allPrice = 0;
	$.each(initTableChooseUtil.getDataRowIndex('otherPriceTableList'), function(i, item) {
		// 获取行坐标
		var thisRowKey = item;
		var otherPrice = $("#otherPrice" + thisRowKey).val();
		allPrice += parseFloat(isNull(otherPrice) ? 0 : otherPrice);
	});
	$("#otherPriceTotal").html("费用合计：" + allPrice.toFixed(2));
}
