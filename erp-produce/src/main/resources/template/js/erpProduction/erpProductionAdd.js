
//商品信息
var productMation = {};

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
	    
		//下拉框模板
	    var selTemplate = getFileContent('tpl/template/select-option-must.tpl');
	    
	    //单据时间
 		laydate.render({ 
			elem: '#operTime',
			type: 'datetime',
			value: getFormatDate(),
			trigger: 'click'
 		});
 		
 		//计划开始时间
 		laydate.render({ 
			elem: '#planStartDate',
			type: 'datetime',
			value: getFormatDate(),
			trigger: 'click'
 		});
 		
 		//计划结束时间
 		laydate.render({ 
			elem: '#planComplateDate',
			type: 'datetime',
			value: getFormatDate(),
			trigger: 'click'
 		});
 		
 		textool.init({
	    	eleId: 'remark',
	    	maxlength: 200,
	    	tools: ['count', 'copy', 'reset']
	    });
		
	    //规格变化事件
	    form.on('select(unitList)', function(data) {
			var thisRowValue = data.value;
			//加载bom方案列表
			loadBomList(thisRowValue);
	    });
	    
	    //加载bom方案列表
	    function loadBomList(normsId){
	    	AjaxPostUtil.request({url:reqBasePath + "erpbom007", params: {normsId: normsId}, type:'json', callback:function(json){
 	   			if(json.returnCode == 0){
	 	   			$("#bomList").html(getDataUseHandlebars(selTemplate, json));
	 	   			form.render("select");
	 	   			//加载bom方案下的子件列表
	 	   			if(json.rows.length > 0){
	 	   				loadBomChildProList(json.rows[0].id);
	 	   			}
 	   			}else{
 	   				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
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
	    	AjaxPostUtil.request({url:reqBasePath + "erpbom008", params: {bomId: bomId}, type:'json', callback:function(json){
 	   			if(json.returnCode == 0){
	 	   			childProList = [].concat(json.rows);
	 	   			$("#tBody").html(getDataUseHandlebars($("#tableBody").html(), {rows: childProList}));
 	   				//加载建议采购数量
 	   				loadChildProPosal();
 	   			}else{
 	   				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
 	   			}
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
	    	$.each(childProList, function(i, item){
	    		//单位所需数量*生产数量-库存抵扣数量
	    		var proposal = number * parseInt(item.needNum) - parseInt(item.deportAllTock);
				$("#proposal" + item.productId).val(proposal < 0 ? 0 : proposal);
				//需求数量=单位所需数量*生产数量
				$("#needNum" + item.productId).html(number * parseInt(item.needNum));
			});
	    }
	    
	    matchingLanguage();
	    form.render();
	    form.on('submit(formAddBean)', function (data) {
	        if (winui.verifyForm(data.elem)) {
	        	if(procedureMationList.length == 0){
	        		winui.window.msg('请选择工序', {icon: 2,time: 2000});
	        		return false;
	        	}
	        	if(childProList.length == 0){
	        		winui.window.msg('请选择子件清单', {icon: 2,time: 2000});
	        		return false;
	        	}
			    var params = {
			    	orderId: isNull(salesOrder.orderHeaderId) ? '' : salesOrder.orderHeaderId,
			    	materialId: productMation.productId,
			    	normsId: $("#unitList").val(),
			    	number: $("#number").val(),
			    	planStartDate: $("#planStartDate").val(),
			    	planComplateDate: $("#planComplateDate").val(),
			    	operTime: $("#operTime").val(),
			    	remark: $("#remark").val(),
			    	bomId: $("#bomList").val(),
					wayProcedureId: isNull(wayProcedureMation.id) ? "" : wayProcedureMation.id
			    };
	        	//工序信息
 	        	params.procedureJsonStr = JSON.stringify(procedureMationList);
 	        	//子件清单
 	        	var childList = [];
 	        	$.each(childProList, function(i, item){
 	        		childList.push({
 	        			materialId: item.productId,
 	        			normsId: item.normsId,
 	        			number: $("#proposal" + item.productId).val(),
 	        			unitNumber: item.needNum,
 	        			unitPrice: item.unitPrice
 	        		});
 	        	});
 	        	//子件清单信息
 	        	params.childProStr = JSON.stringify(childList);
 	        	
	        	AjaxPostUtil.request({url:reqBasePath + "erpproduction002", params: params, type:'json', method: "POST", callback:function(json){
	 	   			if(json.returnCode == 0){
		 	   			parent.layer.close(index);
		 	        	parent.refreshCode = '0';
	 	   			}else{
	 	   				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
	 	   			}
	 	   		}});
	        }
	        return false;
	    });
	    
	    // 工序选择
	    $("body").on("click", "#procedureChoose", function(){
	    	_openNewWindows({
				url: "../../tpl/erpWorkProcedure/erpWorkProcedureChoose.html", 
				title: "工序选择",
				pageId: "erpWorkProcedureChoose",
				area: ['90vw', '90vh'],
				callBack: function(refreshCode){
					if (refreshCode == '0') {
						wayProcedureMation = {};
						loadProcedureMation();
	                } else if (refreshCode == '-9999') {
	                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
	                }
				}});
	    });

		// 工艺选择
		$("body").on("click", "#wayProcedureChoose", function(){
			_openNewWindows({
				url: "../../tpl/erpWayProcedure/erpWayProcedureChoose.html",
				title: "工艺选择",
				pageId: "erpWayProcedureChoose",
				area: ['90vw', '90vh'],
				callBack: function(refreshCode){
					if (refreshCode == '0') {
						AjaxPostUtil.request({url:reqBasePath + "erpwayprocedure008", params: {rowId: wayProcedureMation.id}, type:'json', method: "GET", callback:function(json){
							if(json.returnCode == 0){
								procedureMationList = [].concat(json.bean.procedureList);
								loadProcedureMation();
							}else{
								winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
							}
						}});
					} else if (refreshCode == '-9999') {
						winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
					}
				}});
		});

		function loadProcedureMation(){
			var str = "";
			$.each(procedureMationList, function(i, item){
				str += '<tr><td>' + item.number + '</td><td>' + item.procedureName + '</td><td>' + item.departmentName + '</td></tr>';
			});
			$("#procedureBody").html(str);
			form.render();
		}
	    
	    // 商品选择
 	    $("body").on("click", "#productNameSel", function(e){
 	    	_openNewWindows({
 				url: "../../tpl/material/materialChoose.html", 
 				title: "选择商品",
 				pageId: "productlist",
 				area: ['90vw', '90vh'],
 				callBack: function(refreshCode){
 	                if (refreshCode == '0') {
 	                	$("#productName").val(productMation.productName);
 	                	$("#productModel").val(productMation.productModel);
 	                	$("#unitList").html(getDataUseHandlebars(selTemplate, {rows: productMation.unitList}));
 	                	//重置单据信息
 	                	salesOrder = {};
 	                	$("#salesOrder").val("");
 	                	//加载bom方案列表
 	                	loadBomList(productMation.unitList[0].id);
 	                	form.render("select");
 	                } else if (refreshCode == '-9999') {
 	                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
 	                }
 				}});
 	    });
 	    
 	    //销售单选择
 	    $("body").on("click", "#salesOrderSel", function(e){
 	    	_openNewWindows({
 				url: "../../tpl/salesorder/salesOrderChoose.html", 
 				title: "选择销售单据",
 				pageId: "salesOrderChoose",
 				area: ['90vw', '90vh'],
 				callBack: function(refreshCode){
 	                if (refreshCode == '0') {
 	                	productMation = {
 	                		productName: salesOrder.materialName,
 	                		productModel: salesOrder.materialModel,
 	                		productId: salesOrder.materialId
 	                	};
 	                	$("#productName").val(productMation.productName);
 	                	$("#productModel").val(productMation.productModel);
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
 	                } else if (refreshCode == '-9999') {
 	                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
 	                }
 				}});
 	    });
	    
	    //取消
	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	});
	    
});