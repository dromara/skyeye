
// 计算总价
function calculatedTotalPrice() {
	var taxLastMoneyPrice = 0;
	$.each(initTableChooseUtil.getDataRowIndex(tableId), function (i, item) {
		// 获取行坐标
		var thisRowKey = item;
		// 获取数量
		var operNumber = parseInt(isNull($("#operNumber" + thisRowKey).val()) ? 0 : $("#operNumber" + thisRowKey).val());
		// 获取单价
		var unitPrice = parseFloat(isNull($("#unitPrice" + thisRowKey).val()) ? 0 : $("#unitPrice" + thisRowKey).val());
		// 获取税率
		var taxRate = parseFloat(isNull($("#taxRate" + thisRowKey).val()) ? 0 : $("#taxRate" + thisRowKey).val()) / 100;
		if ('rkNum' === showTdByEdit) {//数量
			//输出金额
			$("#allPrice" + thisRowKey).val((operNumber * unitPrice).toFixed(2));
			//输出税额=数量*税率*单价
			$("#taxMoney" + thisRowKey).val((operNumber * taxRate * unitPrice).toFixed(2));
			//输出含税单价
			$("#taxUnitPrice" + thisRowKey).val((taxRate * unitPrice + unitPrice).toFixed(2));
			//输出合计价税
			$("#taxLastMoney" + thisRowKey).val((operNumber * taxRate * unitPrice + operNumber * unitPrice).toFixed(2));
		} else if ('unitPrice' === showTdByEdit) {//单价
			//输出金额
			$("#allPrice" + thisRowKey).val((operNumber * unitPrice).toFixed(2));
			//输出税额=数量*税率*单价
			$("#taxMoney" + thisRowKey).val((operNumber * taxRate * unitPrice).toFixed(2));
			//输出含税单价
			$("#taxUnitPrice" + thisRowKey).val((taxRate * unitPrice + unitPrice).toFixed(2));
			//输出合计价税
			$("#taxLastMoney" + thisRowKey).val((operNumber * taxRate * unitPrice + operNumber * unitPrice).toFixed(2));
		} else if ('amountOfMoney' === showTdByEdit) {//金额
			//获取金额
			var allPrice = parseFloat(isNull($("#allPrice" + thisRowKey).val()) ? 0 : $("#allPrice" + thisRowKey).val());
			//输出税额=金额*税率
			$("#taxMoney" + thisRowKey).val((allPrice * taxRate).toFixed(2));
			//输出单价,含税单价,合计价税
			if (operNumber != 0) {
				$("#unitPrice" + thisRowKey).val((allPrice / operNumber).toFixed(2));
				$("#taxUnitPrice" + thisRowKey).val((allPrice / operNumber * taxRate + allPrice / operNumber).toFixed(2));
				$("#taxLastMoney" + thisRowKey).val((allPrice * taxRate + allPrice).toFixed(2));
			} else {
				$("#unitPrice" + thisRowKey).val('0.00');
				$("#taxUnitPrice" + thisRowKey).val('0.00');
				$("#taxLastMoney" + thisRowKey).val('0.00');
			}
		} else if ('taxRate' === showTdByEdit) {//税率
			//输出金额
			$("#allPrice" + thisRowKey).val((operNumber * unitPrice).toFixed(2));
			//输出税额=数量*税率*单价
			$("#taxMoney" + thisRowKey).val((operNumber * taxRate * unitPrice).toFixed(2));
			//输出含税单价
			$("#taxUnitPrice" + thisRowKey).val((taxRate * unitPrice + unitPrice).toFixed(2));
			//输出合计价税
			$("#taxLastMoney" + thisRowKey).val((operNumber * taxRate * unitPrice + operNumber * unitPrice).toFixed(2));
		} else if ('taxMoney' === showTdByEdit) {//税额
			//获取税额
			var taxMoney = parseFloat(isNull($("#taxMoney" + thisRowKey).val()) ? 0 : $("#taxMoney" + thisRowKey).val());
			//输出金额
			$("#allPrice" + thisRowKey).val((operNumber * unitPrice).toFixed(2));
			//获取金额
			var allPrice = parseFloat(isNull($("#allPrice" + thisRowKey).val()) ? 0 : $("#allPrice" + thisRowKey).val());
			//输出含税单价,合计价税,税率
			if (operNumber != 0) {
				if (unitPrice != 0) {
					$("#taxUnitPrice" + thisRowKey).val((taxMoney / operNumber + unitPrice).toFixed(2));
					$("#taxRate" + thisRowKey).val((taxMoney / unitPrice / operNumber * 100).toFixed(2));
				} else {
					$("#taxUnitPrice" + thisRowKey).val('0.00');
					$("#taxRate" + thisRowKey).val('0.00');
					$("#unitPrice" + thisRowKey).val('0.00');
					$("#allPrice" + thisRowKey).val('0.00');
				}
				if (allPrice != 0) {
					$("#taxLastMoney" + thisRowKey).val((allPrice + taxMoney).toFixed(2));
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
				$("#allPrice" + thisRowKey).val('0.00');
				$("#taxMoney" + thisRowKey).val('0.00');
				$("#taxRate" + thisRowKey).val('0.00');
				return;
			}
			//输出合计价税,税额,税率
			if (unitPrice != 0) {
				if (operNumber != 0) {
					$("#taxLastMoney" + thisRowKey).val((taxUnitPrice * operNumber).toFixed(2));
					$("#allPrice" + thisRowKey).val((unitPrice * operNumber).toFixed(2));
				} else {
					$("#taxLastMoney" + thisRowKey).val('0.00');
					$("#allPrice" + thisRowKey).val('0.00');
				}
				$("#taxMoney" + thisRowKey).val((taxUnitPrice - unitPrice).toFixed(2));
				$("#taxRate" + thisRowKey).val(((taxUnitPrice / unitPrice - 1) * 100).toFixed(2));
			} else {
				$("#taxLastMoney" + thisRowKey).val('0.00');
				$("#unitPrice" + thisRowKey).val('0.00');
				$("#allPrice" + thisRowKey).val('0.00');
				$("#taxMoney" + thisRowKey).val('0.00');
				$("#taxRate" + thisRowKey).val('0.00');
			}
		} else if ('taxLastMoney' === showTdByEdit) {//合计价税
			//获取合计价税
			var taxLastMoney = parseFloat(isNull($("#taxLastMoney" + thisRowKey).val()) ? 0 : $("#taxLastMoney" + thisRowKey).val());
			if (taxLastMoney == 0) {
				$("#taxUnitPrice" + thisRowKey).val('0.00');
				$("#unitPrice" + thisRowKey).val('0.00');
				$("#allPrice" + thisRowKey).val('0.00');
				$("#taxMoney" + thisRowKey).val('0.00');
				$("#taxRate" + thisRowKey).val('0.00');
				return;
			}
			//输出含税单价,税额,税率
			if (operNumber != 0) {
				if (unitPrice != 0) {
					$("#taxUnitPrice" + thisRowKey).val((taxLastMoney / operNumber).toFixed(2));
					$("#taxMoney" + thisRowKey).val((taxLastMoney / operNumber - unitPrice).toFixed(2));
					$("#taxRate" + thisRowKey).val(((taxLastMoney / operNumber / unitPrice - 1) * 100).toFixed(2));
					$("#allPrice" + thisRowKey).val((unitPrice * operNumber).toFixed(2));
				} else {
					$("#allPrice" + thisRowKey).val('0.00');
					$("#taxMoney" + thisRowKey).val('0.00');
					$("#taxUnitPrice" + thisRowKey).val('0.00');
					$("#unitPrice" + thisRowKey).val('0.00');
				}
			} else {
				$("#taxUnitPrice" + thisRowKey).val('0.00');
				$("#unitPrice" + thisRowKey).val('0.00');
				$("#allPrice" + thisRowKey).val('0.00');
				$("#taxMoney" + thisRowKey).val('0.00');
				$("#taxRate" + thisRowKey).val('0.00');
			}
		}
		taxLastMoneyPrice += parseFloat($("#taxLastMoney" + thisRowKey).val());
	});

	var discountMoney;
	if ($(".discount").length > 0) {
		// 优惠率计算
		var discount = parseFloat(isNull($(".discount").val()) ? 0 : $(".discount").val());
		// 输出优惠金额
		discountMoney = (taxLastMoneyPrice * discount / 100).toFixed(2);
	} else {
		discountMoney = $(".discountMoney").val();
	}
	if (isNaN(discountMoney)) {
		discountMoney = 0;
	}
	$(".discountMoney").html(discountMoney);
	taxLastMoneyPrice.toFixed(2);
	// 返回最终金额
	$(".totalPrice").html((taxLastMoneyPrice - discountMoney).toFixed(2));
	return (taxLastMoneyPrice - discountMoney).toFixed(2);
}

layui.define(["jquery"], function(exports) {
	var jQuery = layui.jquery;
	(function($) {
		// 数量变化,税率变化
		$("body").on("input", ".rkNum, .unitPrice, .amountOfMoney, .taxRate, .taxMoney, .taxUnitPrice, .taxLastMoney", function () {
			var clazz = $(this).attr("class").replace("layui-input", "").replace("change-input", "").replace("layui-form-danger", "").replace(/\s+/g, "");
			tableId = $(this).parents("table").parent().parent().attr("id");
			if (clazz != showTdByEdit) {
				showTdByEdit = clazz;
				$(".change-input").parent().removeAttr("style");
				$("." + showTdByEdit).parent().css({'background-color': '#e6e6e6'});
			}
			calculatedTotalPrice();
		});
		$("body").on("change", ".rkNum, .unitPrice, .amountOfMoney, .taxRate, .taxMoney, .taxUnitPrice, .taxLastMoney", function () {
			var clazz = $(this).attr("class").replace("layui-input", "").replace("change-input", "").replace("layui-form-danger", "").replace(/\s+/g, "");
			tableId = $(this).parents("table").parent().parent().attr("id");
			if (clazz != showTdByEdit) {
				showTdByEdit = clazz;
				$(".change-input").parent().removeAttr("style");
				$("." + showTdByEdit).parent().css({'background-color': '#e6e6e6'});
			}
			calculatedTotalPrice();
		});
		
		// 优惠率变化/优惠金额变化
		$("body").on("input", ".discount, .discountMoney", function() {
			calculatedTotalPrice();
		});
		$("body").on("change", ".discount, .discountMoney", function() {
			calculatedTotalPrice();
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
