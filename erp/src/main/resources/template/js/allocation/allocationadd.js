//商品信息
var productMation = {};

// 调拨单
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
		var rowNum = 1; //表格的序号

		var usetableTemplate = $("#usetableTemplate").html();
		var selOption = getFileContent('tpl/template/select-option.tpl');
		//已经选择的商品集合key：表格的行trId，value：商品信息
		var allChooseProduct = {};
		
		//单据时间
 		laydate.render({ 
 			elem: '#operTime',
 			type: 'datetime',
 			value: getFormatDate(),
 	 		trigger: 'click'
 		});
 		
 		textool.init({
	    	eleId: 'remark',
	    	maxlength: 200,
	    	tools: ['count', 'copy', 'reset']
	    });
		
		// 初始化仓库
		erpOrderUtil.getDepotList(function (json){
			// 加载仓库数据
			$("#inDepotId").html(getDataUseHandlebars(selOption, json));
			$("#outDepotId").html(getDataUseHandlebars(selOption, json));
			// 初始化一行数据
			addRow();

			matchingLanguage();
		});

		//产品规格加载变化事件
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
						$("#unitPrice" + thisRowNum).val(bean.estimatePurchasePrice.toFixed(2));//单价
						$("#amountOfMoney" + thisRowNum).val((rkNum * parseFloat(bean.estimatePurchasePrice)).toFixed(2));//金额
						return false;
					}
				});
			} else {
				$("#unitPrice" + thisRowNum).val("0.00");//重置单价为空
				$("#amountOfMoney" + thisRowNum).val("0.00");//重置金额为空
			}
			
			//加载库存
			loadTockByDepotAndMUnit(thisRowNum, $("#outDepotId").val());
			//计算价格
			calculatedTotalPrice();
		});

		// 调出仓库变化事件
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
			$("#allPrice").html(allPrice.toFixed(2));
		}

		form.on('submit(formAddBean)', function(data) {
			
			if(winui.verifyForm(data.elem)) {
				//获取已选商品数据
				var rowTr = $("#useTable tr");
				if(rowTr.length == 0) {
					winui.window.msg('请选择商品.', {icon: 2, time: 2000});
					return false;
				}
				if($("#inDepotId").val() === $("#outDepotId").val()) {
					winui.window.msg('调入仓库不能和调出仓库一致.', {icon: 2, time: 2000});
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
						depotId: $("#outDepotId").val(),
						materialId: $("#materialId" + rowNum).val(),
						mUnitId: $("#unitId" + rowNum).val(),
						rkNum: rkNum.val(),
						unitPrice: $("#unitPrice" + rowNum).val(),
						anotherDepotId: $("#inDepotId").val(),
						remark: $("#remark" + rowNum).val()
					};
					tableData.push(row);
				});
				if(noError) {
					return false;
				}

				var params = {
					operTime: $("#operTime").val(),
					remark: $("#remark").val(),
					depotheadStr: JSON.stringify(tableData)
				};
				AjaxPostUtil.request({url: reqBasePath + "allocation002", params: params, type: 'json', callback: function(json) {
					if(json.returnCode == 0) {
						parent.layer.close(index);
						parent.refreshCode = '0';
					} else {
						winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
					}
				}});
			}
			return false;
		});
		
		//判断选中的产品是否也在数组中
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
				materialId: "materialId" + rowNum.toString(), //产品id
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
		
		//商品选择
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
		
		$("body").on("click", "#cancle", function() {
			parent.layer.close(index);
		});
	});
});