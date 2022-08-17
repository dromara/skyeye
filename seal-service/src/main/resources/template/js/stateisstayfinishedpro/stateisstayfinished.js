
// 工单完工
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'laydate', 'fileUpload'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
	    	laydate = layui.laydate;
	    
    	var rowNum = 1; //表格的序号
	    var usetableTemplate = $("#usetableTemplate").html();
	    var selOption = getFileContent('tpl/template/select-option.tpl');
	    
	    // 已经选择的商品集合key：表格的行trId，value：商品信息
		var allChooseProduct = {};
		// 故障关键组件信息
		var faultKeyParts = {};
	    
	    //获取完工信息
	    showGrid({
		 	id: "showForm",
		 	url: flowableBasePath + "sealseservice033",
		 	params: {rowId: parent.rowId},
		 	pagination: false,
		 	template: $("#beanTemplate").html(),
		 	ajaxSendLoadBefore: function(hdb){},
		 	ajaxSendAfter: function (json) {
		 		// 实际开工时间
		 		laydate.render({elem: '#comStarTime', type: 'datetime', trigger: 'click'});
		 		
		 		// 实际完工时间
		 		laydate.render({elem: '#comTime', type: 'datetime', trigger: 'click'});

				// 故障类型
				var defaultFaultTypeId = isNull(json.bean.faultTypeId) ? "" : json.bean.faultTypeId;
				sysDictDataUtil.showDictDataListByDictTypeCode(sysDictData["amsServiceFaultType"]["key"], 'select', "faultTypeId", defaultFaultTypeId, form);
		 		
				// 故障关键组件
				if (!isNull(json.bean.faultKeyPartsId)){
					faultKeyParts = {
						productId: json.bean.faultKeyPartsId,
						productName: json.bean.faultKeyPartsName
					};
					$("#faultKeyPartsId").val(faultKeyParts.productName);
				}
				
				// 加载配件使用明细
				if(json.bean.partsList.length > 0){
					initDataToShow(json);
				}
				
				// 配件规格加载变化事件
				form.on('select(selectUnitProperty)', function(data) {
					var thisRowValue = data.value;
					var thisRowNum = data.elem.id.replace("unitId", "");//获取当前行
					if (!isNull(thisRowValue) && thisRowValue != '请选择') {
						// 当前当前行选中的商品信息
						var product = allChooseProduct["tr" + thisRowNum.toString()];
						$.each(product.unitList, function(j, bean) {
							if(thisRowValue == bean.id){// 获取规格
								// 获取当前行数量
								var rkNum = parseInt($("#rkNum" + thisRowNum).val());
								$("#unitPrice" + thisRowNum).html(bean.retailPrice.toFixed(2));// 单价
								$("#amountOfMoney" + thisRowNum).html((rkNum * parseFloat(bean.retailPrice)).toFixed(2));// 金额
								return false;
							}
						});
					} else {
						$("#unitPrice" + thisRowNum).html("0.00");//重置单价为空
						$("#amountOfMoney" + thisRowNum).html("0.00");//重置金额为空
					}
					//加载库存
					loadTockByDepotAndMUnit(thisRowNum);
					//计算价格
					calculatedTotalPrice();
				});

				// 初始化上传
				$("#comPic").upload(systemCommonUtil.uploadCommon003Config('comPic', 14, isNull(json.bean.comPic) ? "" : json.bean.comPic, 10));

				// 附件回显
				skyeyeEnclosure.initTypeISData({'enclosureUpload': json.bean.enclosureInfo});

				matchingLanguage();
		 		form.render();
		 		// 暂存
		 		form.on('submit(temSub)', function (data) {
					saveData(data, "1");
		 	        return false;
		 	    });
		 		
		 	    // 完工
		 	    form.on('submit(finishedSub)', function (data) {
					saveData(data, "2");
		 	        return false;
		 	    });
		 	}
		});

	    function saveData(data, subType){
			if (winui.verifyForm(data.elem)) {
				// 获取已选配件数据
				var rowTr = $("#useTable tr");
				var tableData = new Array();
				var noError = false; // 循环遍历表格数据时，是否有其他错误信息
				$.each(rowTr, function(i, item) {
					// 获取行编号
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
					if(parseInt(rkNum.val()) > parseInt($("#currentTock" + rowNum).html())){
						rkNum.addClass("layui-form-danger");
						rkNum.focus();
						winui.window.msg('超过库存数量.', {icon: 2, time: 2000});
						noError = true;
						return false;
					}
					// 商品对象
					var product = allChooseProduct["tr" + rowNum.toString()];
					if(inTableDataArrayByAssetarId(product.productId, $("#unitId" + rowNum).val(), tableData)) {
						winui.window.msg('一张单中不允许出现相同单位的配件信息.', {icon: 2, time: 2000});
						noError = true;
						return false;
					}
					var row = {
						materialId: product.productId,
						mUnitId: $("#unitId" + rowNum).val(),
						rkNum: rkNum.val(),
						remark: $("#remark" + rowNum).val()
					};
					tableData.push(row);
				});
				if(noError) {
					return false;
				}
				var picUrl = $("#comPic").find("input[type='hidden'][name='upload']").attr("oldurl");
				var params = {
					serviceId: parent.rowId,
					faultTypeId: $("#faultTypeId").val(),
					comTime: $("#comTime").val(),
					comWorkTime: $("#comWorkTime").val(),
					comExecution: $("#comExecution").val(),
					comPic: isNull(picUrl) ? "" : picUrl, //完工拍照，可为空
					enclosureInfo: skyeyeEnclosure.getEnclosureIdsByBoxId('enclosureUpload'), //完工附件，可为空
					comRemark: $("#comRemark").val(),
					coverCost: $("#coverCost").val(),
					otherCost: $("#otherCost").val(),
					comStarTime: $("#comStarTime").val(),
					faultKeyPartsId: isNull(faultKeyParts.productId) ? "" : faultKeyParts.productId,
					actualFailure: $("#actualFailure").val(),
					solution: $("#solution").val(),
					useStr: JSON.stringify(tableData),
					subType: subType
				};
				AjaxPostUtil.request({url: flowableBasePath + "sealseservice035", params: params, type: 'json', callback: function (json) {
					parent.layer.close(index);
					parent.refreshCode = '0';
				}});
			}
		}
		
		//判断选中的备件是否也在数组中
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
		
	    //渲染数据到页面
		function initDataToShow(_object){
			//渲染列表项
			$.each(_object.bean.partsList, function(i, item){
				addRow();
				// 将规格所属的商品信息加入到对象中存储
				allChooseProduct["tr" + (rowNum - 1)] = item;
				$("#unitId" + (rowNum - 1)).html(getDataUseHandlebars(selOption, {rows: item.unitList}));
				$("#unitId" + (rowNum - 1)).val(item.normsId);
				
				// 商品回显
				$("#materialId" + (rowNum - 1)).val(item.productName + "(" + item.productModel + ")");
				$("#currentTock" + (rowNum - 1)).html(item.currentTock);//库存回显
				$("#rkNum" + (rowNum - 1)).val(item.operNumber);//数量回显
				$("#unitPrice" + (rowNum - 1)).html(item.unitPrice.toFixed(2));//单价回显
				$("#amountOfMoney" + (rowNum - 1)).html(item.allPrice.toFixed(2));//金额回显
				$("#remark" + (rowNum - 1)).val(item.remark);//备注回显
			});
			//渲染
 			form.render();
		}
		
		//数量变化
		$("body").on("input", ".rkNum, #coverCost, #otherCost", function(e) {
			calculatedTotalPrice();
		});
		
		/**
		 * 根据规格加载库存
		 * @param rowNum 表格行坐标
		 */
		function loadTockByDepotAndMUnit(rowNum){
			//获取当前选中的规格
			var chooseUnitId = $("#unitId" + rowNum).val();
			//当两个都不为空时
			if (!isNull(chooseUnitId)){
				//获取库存
				AjaxPostUtil.request({url: flowableBasePath + "sealseservice034", params: {mUnitId: chooseUnitId}, type: 'json', callback: function(json) {
					var currentTock = 0;
					if (!isNull(json.bean)){
						currentTock = json.bean.stockNum;
					}
					$("#currentTock" + rowNum).html(currentTock);
				}});
			} else {
				//否则重置库存为空
				$("#currentTock" + rowNum).html("");
			}
		}
		
		// 计算总价
		function calculatedTotalPrice(){
			var rowTr = $("#useTable tr");
			var allPrice = 0;
			$.each(rowTr, function(i, item) {
				// 获取行坐标
				var rowNum = $(item).attr("trcusid").replace("tr", "");
				// 获取数量
				var rkNum = parseInt(isNull($("#rkNum" + rowNum).val()) ? "0" : $("#rkNum" + rowNum).val());
				// 获取单价
				var unitPrice = parseFloat(isNull($("#unitPrice" + rowNum).html()) ? "0" : $("#unitPrice" + rowNum).html());
				// 输出金额
				$("#amountOfMoney" + rowNum).html((rkNum * unitPrice).toFixed(2));
				allPrice += parseFloat($("#amountOfMoney" + rowNum).html());
			});
			// 材料费
			$("#materialCost").html(allPrice.toFixed(2));
			// 总费用 += 服务费 + 其他费用
			allPrice += parseFloat(isNull($("#coverCost").val()) ? "0" : $("#coverCost").val());
			allPrice += parseFloat(isNull($("#otherCost").val()) ? "0" : $("#otherCost").val());
			$("#allPrice").html(allPrice.toFixed(2));
		}
	    
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
				materialId: "materialId" + rowNum.toString(), // 配件id
				unitId: "unitId" + rowNum.toString(), // 规格id
				currentTock: "currentTock" + rowNum.toString(), // 库存id
				rkNum: "rkNum" + rowNum.toString(), // 数量id
				unitPrice: "unitPrice"  + rowNum.toString(), // 单价id
				amountOfMoney: "amountOfMoney"  + rowNum.toString(), // 金额id
				remark: "remark" + rowNum.toString() // 备注id
			};
			$("#useTable").append(getDataUseHandlebars(usetableTemplate, par));
			form.render('checkbox');
			rowNum++;
		}

		// 删除行
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
		
		// 故障组件选择
 	    $("body").on("click", "#faultKeyPartsIdSel", function (e) {
			erpOrderUtil.openMaterialChooseChoosePage(function (chooseProductMation) {
				faultKeyParts = chooseProductMation;
				// 重置故障组件信息
				$("#faultKeyPartsId").val(faultKeyParts.productName);

				form.render();
			});
 	    });
		
		// 商品选择
 	    $("body").on("click", ".chooseProductBtn", function (e) {
 	    	var trId = $(this).parent().parent().attr("trcusid");
			erpOrderUtil.openMaterialChooseChoosePage(function (chooseProductMation) {
				// 获取表格行号
				var thisRowNum = trId.replace("tr", "");
				// 商品赋值
				allChooseProduct[trId] = chooseProductMation;
				// 表格商品名称赋值
				$("#materialId" + thisRowNum.toString()).val(allChooseProduct[trId].productName + "(" + allChooseProduct[trId].productModel + ")");
				// 表格单位赋值
				$("#unitId" + thisRowNum.toString()).html(getDataUseHandlebars(selOption, {rows: allChooseProduct[trId].unitList}));
				form.render('select');
				// 计算价格
				calculatedTotalPrice();
			});
 	    });

	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	});
});