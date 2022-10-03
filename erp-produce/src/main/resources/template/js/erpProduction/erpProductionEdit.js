
//工序选择必备参数
var procedureCheckType = 2;//工序选择类型：1.单选procedureMation；2.多选procedureMationList
var procedureMationList = new Array();

// 工艺信息---选择工序时，会覆盖工艺信息
var wayProcedureMation = {};

//销售单信息
var salesOrder = {};

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'laydate', 'textool'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
		    form = layui.form,
		    laydate = layui.laydate,
		    textool = layui.textool;
		
		//子件清单列表集合
		var childProList = new Array();
	    
		// 下拉框模板
	    var selTemplate = getFileContent('tpl/template/select-option-must.tpl');
	    
	    // 单据时间
 		laydate.render({elem: '#operTime', type: 'datetime', value: getFormatDate(), trigger: 'click'});
 		
 		// 计划开始时间
 		laydate.render({elem: '#planStartDate', type: 'datetime', value: getFormatDate(), trigger: 'click'});
 		
 		// 计划结束时间
 		laydate.render({elem: '#planComplateDate', type: 'datetime', value: getFormatDate(), trigger: 'click'});
 		
 		textool.init({eleId: 'remark', maxlength: 200});
 		
 		AjaxPostUtil.request({url: flowableBasePath + "erpproduction003", params: {id: parent.rowId}, type: 'json', method: "GET", callback: function (json) {
			// 商品信息
			erpOrderUtil.chooseProductMation = {
				materialId: json.bean.materialId,
				materialName: json.bean.materialName,
				materialModel: json.bean.materialModel
			};
			$("#materialName").val(json.bean.materialName);
			$("#materialModel").val(json.bean.materialModel);
			// 订单信息
			if (!isNull(json.bean.orderId)){
				salesOrder = {
					orderHeaderId: json.bean.orderId
				};
				$("#salesOrder").val(json.bean.defaultNumber);
			}
			// 规格
			$("#unitList").html(getDataUseHandlebars(selTemplate, {rows: json.bean.unitList}));
			$("#unitList").val(json.bean.normsId);
			$("#number").val(json.bean.number);
			$("#planStartDate").val(json.bean.planStartDate);
			$("#planComplateDate").val(json.bean.planComplateDate);
			$("#operTime").val(json.bean.operTime);
			$("#remark").val(json.bean.remark);

			// 生产方案
			$("#bomList").html(getDataUseHandlebars(selTemplate, {rows: json.bean.bomList}));
			$("#bomList").val(json.bean.bomId);

			//初始化工序
			procedureMationList = [].concat(json.bean.procedureMationList);
			var str = "";
			$.each(procedureMationList, function(i, item) {
				str += '<tr><td>' + item.number + '</td><td>' + item.procedureName + '</td><td>' + item.departmentName + '</td></tr>';
			});
			$("#procedureBody").html(str);
			// 子件清单
			childProList = [].concat(json.bean.childList);
			$("#tBody").html(getDataUseHandlebars($("#tableBody").html(), {rows: childProList}));
			$.each(childProList, function(i, item) {
				$("#proposal" + item.materialId).val(item.number);
				//需求数量=生产数量*单位所需数量
				$("#needNum" + item.materialId).html(parseInt(json.bean.number) * parseInt(item.needNum));
			});
			// 工艺路线赋值
			wayProcedureMation.id = json.bean.wayProcedureId;

			matchingLanguage();
			form.render();
   		}});
		
	    //规格变化事件
	    form.on('select(unitList)', function(data) {
			var thisRowValue = data.value;
			//加载bom方案列表
			loadBomList(thisRowValue);
	    });
	    
	    //加载bom方案列表
	    function loadBomList(normsId){
	    	AjaxPostUtil.request({url: flowableBasePath + "erpbom007", params: {normsId: normsId}, type: 'json', callback: function (json) {
				$("#bomList").html(getDataUseHandlebars(selTemplate, json));
				form.render("select");
				//加载bom方案下的子件列表
				if(json.rows.length > 0){
					loadBomChildProList(json.rows[0].id);
				}
 	   		}});
	    }
	    
	    //方案变化事件
	    form.on('select(bomList)', function(data) {
			var thisRowValue = data.value;
			//加载bom方案下的子件列表
			loadBomChildProList(thisRowValue);
	    });
	    
	    //加载bom方案下的子件列表
	    function loadBomChildProList(bomId){
	    	AjaxPostUtil.request({url: flowableBasePath + "erpbom008", params: {id: bomId}, type: 'json', method: 'GET', callback: function (json) {
				childProList = [].concat(json.rows);
				$("#tBody").html(getDataUseHandlebars($("#tableBody").html(), {rows: childProList}));
				//加载建议采购数量
				loadChildProPosal();
 	   		}});
	    }
	    
	    //计划数量变化事件
	    $("body").on("input", "#number", function() {
			//加载建议采购数量
			loadChildProPosal();
		});
		$("body").on("change", "#number", function() {
			//加载建议采购数量
			loadChildProPosal();
		});
	    
	    //加载建议采购数量
	    function loadChildProPosal(){
	    	//计划生产数量
	    	var number = parseInt(isNull($("#number").val()) ? '0' : $("#number").val());
	    	if(isNaN(number)){
	    		number = 0;
	    	}
	    	$.each(childProList, function(i, item) {
	    		//单位所需数量*生产数量-库存抵扣数量
	    		var proposal = number * parseInt(item.needNum) - parseInt(item.currentTock);
				$("#proposal" + item.materialId).val(proposal < 0 ? 0 : proposal);
				//需求数量=单位所需数量*生产数量
				$("#needNum" + item.materialId).html(number * parseInt(item.needNum));
			});
	    }
	    
	    form.on('submit(formEditBean)', function (data) {
	        if (winui.verifyForm(data.elem)) {
	        	if(procedureMationList.length == 0){
	        		winui.window.msg('请选择工序', {icon: 2, time: 2000});
	        		return false;
	        	}
	        	if(childProList.length == 0){
	        		winui.window.msg('请选择子件清单', {icon: 2, time: 2000});
	        		return false;
	        	}
			    var params = {
			    	orderId: isNull(salesOrder.orderHeaderId) ? '' : salesOrder.orderHeaderId,
			    	materialId: erpOrderUtil.chooseProductMation.materialId,
			    	normsId: $("#unitList").val(),
			    	number: $("#number").val(),
			    	planStartDate: $("#planStartDate").val(),
			    	planComplateDate: $("#planComplateDate").val(),
			    	operTime: $("#operTime").val(),
			    	remark: $("#remark").val(),
			    	bomId: $("#bomList").val(),
			    	rowId: parent.rowId,
					wayProcedureId: isNull(wayProcedureMation.id) ? "" : wayProcedureMation.id
			    };
	        	//工序信息
 	        	params.procedureJsonStr = JSON.stringify(procedureMationList);
 	        	//子件清单
 	        	var childList = [];
 	        	$.each(childProList, function(i, item) {
 	        		childList.push({
 	        			materialId: item.materialId,
 	        			normsId: item.normsId,
 	        			number: $("#proposal" + item.materialId).val(),
 	        			unitNumber: item.needNum,
 	        			unitPrice: item.normsPurchasePrice
 	        		});
 	        	});
 	        	//子件清单信息
 	        	params.childProStr = JSON.stringify(childList);
 	        	
	        	AjaxPostUtil.request({url: flowableBasePath + "erpproduction004", params: params, type: 'json', method: "PUT", callback: function (json) {
					parent.layer.close(index);
					parent.refreshCode = '0';
	 	   		}});
	        }
	        return false;
	    });

		// 工序选择
		$("body").on("click", "#procedureChoose", function() {
			_openNewWindows({
				url: "../../tpl/erpWorkProcedure/erpWorkProcedureChoose.html",
				title: "工序选择",
				pageId: "erpWorkProcedureChoose",
				area: ['90vw', '90vh'],
				callBack: function (refreshCode) {
					wayProcedureMation = {};
					loadProcedureMation();
				}});
		});

		// 工艺选择
		$("body").on("click", "#wayProcedureChoose", function() {
			_openNewWindows({
				url: "../../tpl/erpWayProcedure/erpWayProcedureChoose.html",
				title: "工艺选择",
				pageId: "erpWayProcedureChoose",
				area: ['90vw', '90vh'],
				callBack: function (refreshCode) {
					AjaxPostUtil.request({url: flowableBasePath + "erpwayprocedure008", params: {rowId: wayProcedureMation.id}, type: 'json', method: "GET", callback: function (json) {
						procedureMationList = [].concat(json.bean.procedureList);
						loadProcedureMation();
					}});
				}});
		});

		function loadProcedureMation(){
			var str = "";
			$.each(procedureMationList, function(i, item) {
				str += '<tr><td>' + item.number + '</td><td>' + item.procedureName + '</td><td>' + item.departmentName + '</td></tr>';
			});
			$("#procedureBody").html(str);
			form.render();
		}
	    
	    //商品选择
 	    $("body").on("click", "#productNameSel", function (e) {
			erpOrderUtil.openMaterialChooseChoosePage(function (chooseProductMation) {
				$("#materialName").val(chooseProductMation.materialName);
				$("#materialModel").val(chooseProductMation.materialModel);
				$("#unitList").html(getDataUseHandlebars(selTemplate, {rows: chooseProductMation.unitList}));
				//重置单据信息
				salesOrder = {};
				$("#salesOrder").val("");
				//加载bom方案列表
				loadBomList(chooseProductMation.unitList[0].id);
				form.render("select");
			});
 	    });
 	    
 	    //销售单选择
 	    $("body").on("click", "#salesOrderSel", function (e) {
 	    	_openNewWindows({
 				url: "../../tpl/salesorder/salesOrderChoose.html", 
 				title: "选择销售单据",
 				pageId: "salesOrderChoose",
 				area: ['90vw', '90vh'],
 				callBack: function (refreshCode) {
					erpOrderUtil.chooseProductMation = {
						materialName: salesOrder.materialName,
						materialModel: salesOrder.materialModel,
						materialId: salesOrder.materialId
					};
					$("#materialName").val(erpOrderUtil.chooseProductMation.materialName);
					$("#materialModel").val(erpOrderUtil.chooseProductMation.materialModel);
					//加载数量
					$("#number").val(salesOrder.operNum);
					//单号
					$("#salesOrder").val(salesOrder.orderNumber);
					//单位
					var unitList = [{
						id: salesOrder.mUnitId,
						name: salesOrder.materialUnitName
					}];
					$("#unitList").html(getDataUseHandlebars(selTemplate, {rows: unitList}));
					//加载bom方案列表
					loadBomList(unitList[0].id);
					form.render("select");
 				}});
 	    });
	    
	    // 取消
	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	});
	    
});