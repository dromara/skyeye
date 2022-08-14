
// 拆分单
layui.config({
	base: basePath,
	version: skyeyeVersion
}).extend({
	window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'laydate', 'textool'], function(exports) {
	winui.renderColor();
	layui.use(['form'], function(form) {
		var index = parent.layer.getFrameIndex(window.name);
		var $ = layui.$,
			laydate = layui.laydate,
			textool = layui.textool;

		var beanTemplate = $("#beanTemplate").html();
		var selOption = getFileContent('tpl/template/select-option.tpl');
		// 已经选择的商品集合key：表格的行trId，value：商品信息
		var allChooseProduct = {};
		
 		// 加载单据数据
 		var orderObject = [];
		showGrid({
		 	id: "showForm",
		 	url: flowableBasePath + "splitlist003",
		 	params: {rowId: parent.rowId},
		 	pagination: false,
		 	template: beanTemplate,
		 	ajaxSendAfter:function (json) {
		 		// 单据时间
		 		laydate.render({elem: '#operTime', type: 'datetime', trigger: 'click'});
		 		orderObject = json;
		 		initDepotHtml();
		 	}
		});
 		
		// 初始化仓库
		function initDepotHtml() {
			erpOrderUtil.getDepotList(function (json) {
				$("#depotId").html(getDataUseHandlebars(selOption, json));
			});

			// 渲染数据到页面
			initDataToShow();
		}
		
		// 渲染数据到页面
		function initDataToShow() {
			initTable();

			// 回显商品列表
			initTableChooseUtil.deleteAllRow('productList');
			$.each(orderObject.bean.items, function(i, item) {
				if (i == 0) {
					$("#depotId").val(item.depotId);
				}
				var params = {
					"materialType": {
						"value": item.mType
					},
					"materialId": item.product.productName + "(" + item.product.productModel + ")",
					"mUnitId": {
						"html": getDataUseHandlebars(selOption, {rows: item.product.unitList}),
						"value": item.mUnitId
					},
					"currentTock": item.currentTock,
					"rkNum": item.operNum,
					"unitPrice": item.unitPrice.toFixed(2),
					"amountOfMoney": item.allPrice.toFixed(2),
					"remark": item.remark
				};
				var trcusid = initTableChooseUtil.resetData('productList', params);
				// 将规格所属的商品信息加入到对象中存储
				allChooseProduct[trcusid] = item.product;
			});

			textool.init({eleId: 'remark', maxlength: 200});
			
			matchingLanguage();
 			form.render();
		}

		function initTable() {
			// 商品
			initTableChooseUtil.initTable({
				id: "productList",
				cols: [
					{id: 'materialType', title: '商品类型', formType: 'select', width: '120', verify: 'required', modelHtml: '<option value="1">组合件</option><option value="2">普通子件</option>'},
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
		mUnitChangeEvent(form, allChooseProduct, "estimatePurchasePrice", calculatedTotalPrice);

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

		// 计算总价
		function calculatedTotalPrice() {
			var allPrice = 0;
			$.each(initTableChooseUtil.getDataRowIndex('productList'), function (i, item) {
				// 获取行坐标
				var thisRowKey = item;
				// 获取数量
				var rkNum = parseInt(isNull($("#rkNum" + thisRowKey).val()) ? "0" : $("#rkNum" + thisRowKey).val());
				// 获取单价
				var unitPrice = parseFloat(isNull($("#unitPrice" + thisRowKey).val()) ? "0" : $("#unitPrice" + thisRowKey).val());
				// 获取金额
				var amountOfMoney = parseFloat(isNull($("#amountOfMoney" + thisRowKey).val()) ? "0" : $("#amountOfMoney" + thisRowKey).val());
				if ("rkNum" === showTdByEdit) {//数量
					// 输出金额
					$("#amountOfMoney" + thisRowKey).val((rkNum * unitPrice).toFixed(2));
				} else if ("unitPrice" === showTdByEdit) {//单价
					// 输出金额
					$("#amountOfMoney" + thisRowKey).val((rkNum * unitPrice).toFixed(2));
				} else if ("amountOfMoney" === showTdByEdit) {//金额
					// 输出单价
					$("#unitPrice" + thisRowKey).val((amountOfMoney / rkNum).toFixed(2));
				}
				allPrice += parseFloat($("#amountOfMoney" + thisRowKey).val());
			});
			$("#allPrice").html(allPrice.toFixed(2));
		}
		
		form.on('submit(formEditBean)', function(data) {
			if(winui.verifyForm(data.elem)) {
				var result = initTableChooseUtil.getDataList('productList');
				if (!result.checkResult) {
					return false;
				}
				var noError = false;
				var hasAssembly = false;//判断是否有组合件
				var hasSplit = false;//判断是否有普通子件
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
					// 商品类型
					var materialType = item.materialType;
					if (materialType === "1") {
						if (!hasAssembly) {
							// 当前没有组合件
							hasAssembly = true;
						} else {
							$("#materialType" + thisRowKey).addClass("layui-form-danger");
							$("#materialType" + thisRowKey).focus();
							winui.window.msg('拆分单中只能存在一个组合件.', {icon: 2, time: 2000});
							noError = true;
							return false;
						}
					} else if (materialType === "2") {
						hasSplit = true;
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

				if (!hasAssembly) {
					winui.window.msg('请选择组合件.', {icon: 2, time: 2000});
					return false;
				}
				if (!hasSplit) {
					winui.window.msg('请选择普通子件.', {icon: 2, time: 2000});
					return false;
				}

				var params = {
					operTime: $("#operTime").val(),
					remark: $("#remark").val(),
					depotheadStr: JSON.stringify(tableData),
					rowId: parent.rowId
				};
				AjaxPostUtil.request({url: flowableBasePath + "splitlist004", params: params, type: 'json', callback: function(json) {
					parent.layer.close(index);
					parent.refreshCode = '0';
				}});
			}
			return false;
		});

		// 加载商品选择事件
		initChooseProductBtnEnent(form, function(trId, chooseProductMation) {
			// 商品赋值
			allChooseProduct[trId] = chooseProductMation;
		}, calculatedTotalPrice);

		$("body").on("click", "#cancle", function() {
			parent.layer.close(index);
		});
	});
});