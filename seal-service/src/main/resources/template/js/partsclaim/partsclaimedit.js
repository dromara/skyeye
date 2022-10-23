// 选取的未完成售后服务工单号
var chooseOrderNum = "";
// 选取的未完成售后服务工单id
var chooseOrderId = "";

// 已经选择的客户信息
var customerMation = {};

// 配件申领单
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
		// 已经选择的商品集合key：表格的行trId，value：商品信息
		var allChooseProduct = {};
		
 		// 加载单据数据
 		var orderObject = [];
		showGrid({
		 	id: "showForm",
		 	url: flowableBasePath + "sealseservice026",
		 	params: {id: parent.rowId},
			method: 'GET',
		 	pagination: false,
		 	template: $("#beanTemplate").html(),
		 	ajaxSendAfter:function (json) {
		 		// 单据时间
		 		laydate.render({elem: '#operTime', type: 'datetime', value: json.bean.applyTime, trigger: 'click'});
		 		
		 		textool.init({eleId: 'remark', maxlength: 200});
		 		
		 		orderObject = json;
		 		// 初始化仓库
				initDepotHtml();
		 	}
		});
		
		// 初始化仓库
		function initDepotHtml() {
			erpOrderUtil.getDepotList(function (json){
				// 加载仓库数据
				$("#depotId").html(getDataUseHandlebars(selOption, json));
				matchingLanguage();
				form.render();
				// 渲染数据
				initDataToShow();
			});
		}
		
		//渲染数据到页面
		function initDataToShow(){
			chooseOrderNum = orderObject.bean.orderNum;
			chooseOrderId = orderObject.bean.serviceId;
			// 客户信息赋值
	 		customerMation = {
	 			id: orderObject.bean.customerId,
	 			customName: orderObject.bean.customerName
	 		}
			$("#customName").val(customerMation.customName);//客户
			$("#serviceId").val(orderObject.bean.orderNum);//工单单号
			$("#remark").val(orderObject.bean.remark);//备注
			$("#allPrice").html(orderObject.bean.allPrice.toFixed(2));//总金额
			// 渲染列表项
			$.each(orderObject.bean.materialList, function(i, item) {
				addRow();
				if(i == 0){
					$("#depotId").val(item.depotId);
				}
				//将规格所属的商品信息加入到对象中存储
				allChooseProduct["tr" + (rowNum - 1)] = item;
				// 单位回显
				$("#mUnitId" + (rowNum - 1)).html(getDataUseHandlebars(selOption, {rows: item.unitList}));
				$("#mUnitId" + (rowNum - 1)).val(item.normsId);
				// 商品回显
				$("#materialId" + (rowNum - 1)).val(item.materialName + "(" + item.materialModel + ")");
				$("#allStock" + (rowNum - 1)).html(item.allStock);//库存回显
				$("#rkNum" + (rowNum - 1)).val(item.operNum);//数量回显
				$("#unitPrice" + (rowNum - 1)).html(item.unitPrice.toFixed(2));//单价回显
				$("#amountOfMoney" + (rowNum - 1)).html(item.allPrice.toFixed(2));//金额回显
				$("#remark" + (rowNum - 1)).val(item.remark);//备注回显
				//设置标识
				$("tr[trcusid='tr" + (rowNum - 1) + "']").attr("thisid", item.id);
			});
			// 附件回显
			skyeyeEnclosure.initTypeISData({'enclosureUpload': orderObject.bean.enclosureInfo});
			//渲染
 			form.render();
		}
		
		// 备件规格加载变化事件
		form.on('select(selectUnitProperty)', function(data) {
			var thisRowValue = data.value;
			var thisRowNum = data.elem.id.replace("mUnitId", "");
			// 当前当前行选中的商品信息
			if (!isNull(thisRowValue) && thisRowValue != '请选择') {
				var material = allChooseProduct["tr" + thisRowNum.toString()];
				$.each(material.unitList, function(j, bean) {
					if(thisRowValue == bean.id){// 获取规格
						// 获取当前行数量
						var rkNum = parseInt($("#rkNum" + thisRowNum).val());
						$("#unitPrice" + thisRowNum).html(bean.retailPrice.toFixed(2));// 单价
						$("#amountOfMoney" + thisRowNum).html((rkNum * parseFloat(bean.retailPrice)).toFixed(2));// 金额
						return false;
					}
				});
			} else {
				$("#unitPrice" + thisRowNum).html("0.00");// 重置单价为空
				$("#amountOfMoney" + thisRowNum).html("0.00");// 重置金额为空
			}
			
			// 加载库存
			loadTockByDepotAndMUnit(thisRowNum, $("#depotId").val());
			// 计算价格
			calculatedTotalPrice();
		});

		// 仓库变化事件
		form.on('select(depotId)', function(data) {
			loadMaterialDepotStockByDepotId(data.value);
		});
		
		//数量变化
		$("body").on("input", ".rkNum", function() {
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
				var unitPrice = parseFloat(isNull($("#unitPrice" + rowNum).html()) ? "0" : $("#unitPrice" + rowNum).html());
				//输出金额
				$("#amountOfMoney" + rowNum).html((rkNum * unitPrice).toFixed(2));
				allPrice += parseFloat($("#amountOfMoney" + rowNum).html());
			});
			$("#allPrice").html(allPrice.toFixed(2));
		}

		form.on('submit(formEditBean)', function(data) {
			if(winui.verifyForm(data.elem)) {
				if(isNull(customerMation.id)){
					winui.window.msg('请选择客户.', {icon: 2, time: 2000});
					return false;
				}
				//获取已选备件数据
				var rowTr = $("#useTable tr");
				if(rowTr.length == 0) {
					winui.window.msg('请选择备件.', {icon: 2, time: 2000});
					return false;
				}
				var tableData = new Array();
				var noError = false; //循环遍历表格数据时，是否有其他错误信息
				$.each(rowTr, function(i, item) {
					//获取行编号
					var rowNum = $(item).attr("trcusid").replace("tr", "");
					// 表格数量对象
					var rkNum = $("#rkNum" + rowNum);
					if(parseInt(rkNum.val()) == 0) {
						rkNum.addClass("layui-form-danger");
						rkNum.focus();
						winui.window.msg('数量不能为0', {icon: 2, time: 2000});
						noError = true;
						return false;
					}
					if(parseInt(rkNum.val()) > parseInt($("#allStock" + rowNum).html())){
						rkNum.addClass("layui-form-danger");
						rkNum.focus();
						winui.window.msg('超过库存数量.', {icon: 2, time: 2000});
						noError = true;
						return false;
					}
					// 商品对象
					var material = allChooseProduct["tr" + rowNum.toString()];
					if(inTableDataArrayByAssetarId(material.materialId, $("#mUnitId" + rowNum).val(), tableData)) {
						winui.window.msg('一张单中不允许出现相同单位的配件信息.', {icon: 2, time: 2000});
						noError = true;
						return false;
					}
					var row = {
						depotId: $("#depotId").val(),
						materialId: material.materialId,
						mUnitId: $("#mUnitId" + rowNum).val(),
						rkNum: rkNum.val(),
						remark: $("#remark" + rowNum).val()
					};
					tableData.push(row);
				});
				if(noError) {
					return false;
				}

				var params = {
					customerId: customerMation.id,//客户
					applyTime: $("#operTime").val(),
					remark: $("#remark").val(),
					applyMaterialStr: JSON.stringify(tableData),
					serviceId: chooseOrderId,
					rowId: parent.rowId,
					enclosureInfo: skyeyeEnclosure.getEnclosureIdsByBoxId('enclosureUpload')
				};
				AjaxPostUtil.request({url: flowableBasePath + "sealseservice027", params: params, type: 'json', callback: function(json) {
					parent.layer.close(index);
					parent.refreshCode = '0';
				}});
			}
			return false;
		});
		
		// 判断选中的备件是否也在数组中
		function inTableDataArrayByAssetarId(materialId, mUnitId, array) {
			var isIn = false;
			$.each(array, function(i, item) {
				if(item.mUnitId === mUnitId && item.materialId === materialId) {
					isIn = true;
					return false;
				}
			});
			return isIn;
		}
		
		// 选取工单
		$("body").on("focus", "#serviceId", function (e) {
 	    	_openNewWindows({
 				url: "../../tpl/partsclaim/mytocompletedlist.html", 
 				title: "选取工单",
 				pageId: "mytocompletedlist",
 				area: ['90vw', '90vh'],
 				callBack: function (refreshCode) {
					$("#serviceId").val(chooseOrderNum);
 				}});
 	    });

		// 新增行
		$("body").on("click", "#addRow", function() {
			addRow();
		});

		// 删除行
		$("body").on("click", "#deleteRow", function() {
			deleteRow();
			// 计算价格
			calculatedTotalPrice();
		});

		// 新增行
		function addRow() {
			var par = {
				id: "row" + rowNum.toString(), // checkbox的id
				trId: "tr" + rowNum.toString(), // 行的id
				materialId: "materialId" + rowNum.toString(), //商品id
				mUnitId: "mUnitId" + rowNum.toString(), // 规格id
				allStock: "allStock" + rowNum.toString(), // 库存id
				rkNum: "rkNum" + rowNum.toString(), // 数量id
				unitPrice: "unitPrice"  + rowNum.toString(), // 单价id
				amountOfMoney: "amountOfMoney"  + rowNum.toString(), // 金额id
				remark: "remark" + rowNum.toString() // 备注id
			};
			$("#useTable").append(getDataUseHandlebars(usetableTemplate, par));
			form.render();
			rowNum++;
		}

		//删除行
		function deleteRow() {
			var checkRow = $("#useTable input[type='checkbox'][name='tableCheckRow']:checked");
			if(checkRow.length > 0) {
				$.each(checkRow, function(i, item) {
					// 删除allChooseProduct已选择的商品信息
					var trId = $(item).parent().parent().attr("trcusid");
					allChooseProduct[trId] = undefined;
					// 移除界面上的信息
					$(item).parent().parent().remove();
				});
			} else {
				winui.window.msg('请选择要删除的行', {icon: 2, time: 2000});
			}
		}

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

		// 商品选择
 	    $("body").on("click", ".chooseProductBtn", function (e) {
 	    	var trId = $(this).parent().parent().attr("trcusid");
			erpOrderUtil.openMaterialChooseChoosePage(function (chooseProductMation) {
				//获取表格行号
				var thisRowNum = trId.replace("tr", "");
				//商品赋值
				allChooseProduct[trId] = chooseProductMation;
				//表格商品名称赋值
				$("#materialId" + thisRowNum.toString()).val(allChooseProduct[trId].materialName + "(" + allChooseProduct[trId].materialModel + ")");
				//表格单位赋值
				$("#mUnitId" + thisRowNum.toString()).html(getDataUseHandlebars(selOption, {rows: allChooseProduct[trId].unitList}));
				form.render('select');
				//计算价格
				calculatedTotalPrice();
			});
 	    });
		
		$("body").on("click", "#cancle", function() {
			parent.layer.close(index);
		});
	});
});