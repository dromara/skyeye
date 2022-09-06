
//多单位数组列表
var unitGroupList = new Array();

//库存页面需要用到的库存信息
var normsStock = new Array();

//工序选择必备参数
var procedureCheckType = 2;//工序选择类型：1.单选procedureMation；2.多选procedureMationList
var procedureMationList = new Array();

// 商品信息
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'fsCommon', 'fsTree', 'element', 'textool'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
	    	fsTree = layui.fsTree,
			fsCommon = layui.fsCommon,
			element = layui.element,
			textool = layui.textool;
	    
	    //多单位计数器
	    var unitIndex = 1;
	    //多单位模板
	    var beanTemplate = $("#beanTemplate").html();
	    //下拉框模板
	    var selTemplate = getFileContent('tpl/template/select-option.tpl');
	    
	    //多个单位时的库存集合
	    //格式{trRow: 1, list: [{depotId: "ea843ebe9b5846f088525cc7a2975057",depotName: "广州第二仓库",initialTock: "123"}]}
	    var normsStockList = new Array();
	    //单个单位时的库存集合
	    //格式[{depotId: "ea843ebe9b5846f088525cc7a2975057",depotName: "广州第二仓库",initialTock: "123"}]
	    var normsStockItem = new Array();
	    
	    //默认隐藏多单位选项内容
	    $(".many-term").hide();
	    
	    textool.init({eleId: 'remark', maxlength: 200});
	    
		//初始商品分类类型
	    var materialCategoryType;
	    fsTree.render({
			id: "materialCategoryType",
			url: flowableBasePath + "materialcategory008",
			checkEnable: true,
			loadEnable: false,//异步加载
			chkStyle: "radio",
			showLine: false,
			showIcon: true,
			expandSpeed: 'fast'
		}, function(id){
			materialCategoryType = $.fn.zTree.getZTreeObj(id);
			fuzzySearch(id, '#name', null, true); //初始化模糊搜索方法
			//加载多单位下拉框
			initManyUnitSelect();
		});

		//加载多单位下拉框
		function initManyUnitSelect(){
			showGrid({
			 	id: "unitGroupId",
			 	url: flowableBasePath + "materialunit006",
			 	params: {},
			 	pagination: false,
			 	template: selTemplate,
			 	ajaxSendLoadBefore: function(hdb) {},
			 	ajaxSendAfter:function (json) {
			 		form.render('select');
			 		unitGroupList = json.rows;
			 	}
		    });
		    
		    form.on('select(unitGroupId)', function(data) {
		    	//初始化仓库库存集合列表
		    	normsStockList = new Array();
				if(isNull(data.value)){
		    		$("#firstInUnit").html("");
		    		$("#firstOutUnit").html("");
		    		form.render('select');
		    	} else {
			    	$.each(unitGroupList, function(i, item) {
			    		if(item.id == data.value){
			    			var str = getDataUseHandlebars(selTemplate, {rows: item.unitList});
			    			$("#firstInUnit").html(str);
		    				$("#firstOutUnit").html(str);
		    				form.render('select');
		    				$("#useTable").html("");
		    				$.each(item.unitList, function(j, bean){
		    					addRow(bean);
		    				});
			    			return false;
			    		}
			    	});
		    	}
			});
		}

		skyeyeEnclosure.init('enclosureUpload');
		matchingLanguage();
 		form.render();
 	    form.on('submit(formAddBean)', function (data) {
 	    	//提交前进行制空操作，防止多余的校验
 	    	if($("#unit").val() === 'true'){//多单位
 	        	$(".single-term").find("input").val("");
        	} else {//单单位
        		$(".many-term").find("select").val("");
        		form.render('select');
        		$("#useTable").html("");
        	}
 	    	
 	        if (winui.verifyForm(data.elem)) {
 	        	var checkNodes = materialCategoryType.getCheckedNodes(true);
 	        	if(checkNodes.length == 0){
 	        		winui.window.msg('请选择商品所属类型', {icon: 2, time: 2000});
 	        		return false;
 	        	}
				var tableData = new Array();
 	        	if($("#unit").val() === 'true'){//多单位
 	        		if(!subVerifyForm("unitGroupId"))return false;//单位非空校验
 	        		if(!subVerifyForm("firstInUnit"))return false;//首选入库单位校验
 	        		if(!subVerifyForm("firstOutUnit"))return false;//首选出库单位非空校验
 	        		//价格表校验
 	        		var rowTr = $("#useTable tr");
					if(rowTr.length == 0) {
						winui.window.msg('请填写价格表~', {icon: 2, time: 2000});
						return false;
					}
					$.each(rowTr, function(i, item) {
						var rowNum = $(item).attr("trcusid").replace("tr", "");
						var unitId = $(item).attr("unitid");//数据库存储的id
						if(!subVerifyForm("safetyTock" + rowNum))return false;//安全存量非空校验
	 	        		if(!subVerifyForm("retailPrice" + rowNum))return false;//零售价非空校验
	 	        		if(!subVerifyForm("lowPrice" + rowNum))return false;//最低售价非空校验
	 	        		if(!subVerifyForm("estimatePurchasePrice" + rowNum))return false;//预计采购价非空校验
	 	        		if(!subVerifyForm("salePrice" + rowNum))return false;//销售价非空校验
						var row = {
							unitId: unitId,
							safetyTock: $("#safetyTock" + rowNum).val(),
							retailPrice: $("#retailPrice" + rowNum).val(),
							lowPrice: $("#lowPrice" + rowNum).val(),
							estimatePurchasePrice: $("#estimatePurchasePrice" + rowNum).val(),
							salePrice: $("#salePrice" + rowNum).val(),
							normsStock: []
						};
						$.each(normsStockList, function(j, bean){
				    		if(bean.trRow === rowNum){
				    			row.normsStock = [].concat(bean.list);
				    			return false;
				    		}
				    	});
						tableData.push(row);
					});
					if(tableData.length < rowTr.length){
						return false;
					}
 	        	} else {//单单位
 	        		if(!subVerifyForm("safetyTock"))return false;//安全存量非空校验
 	        		if(!subVerifyForm("unitName"))return false;//单位非空校验
 	        		if(!subVerifyForm("retailPrice"))return false;//零售价非空校验
 	        		if(!subVerifyForm("lowPrice"))return false;//最低售价非空校验
 	        		if(!subVerifyForm("estimatePurchasePrice"))return false;//预计采购价非空校验
 	        		if(!subVerifyForm("salePrice"))return false;//销售价非空校验
 	        		var row = {
						safetyTock: $("#safetyTock").val(),
						retailPrice: $("#retailPrice").val(),
						lowPrice: $("#lowPrice").val(),
						estimatePurchasePrice: $("#estimatePurchasePrice").val(),
						salePrice: $("#salePrice").val(),
						normsStock: normsStockItem
					};
					tableData.push(row);
 	        	}
				var params = {
        			materialName: $("#materialName").val(),
 	        		model: $("#model").val(),
					unitName: $("#unitName").val(),
 	        		categoryId: checkNodes[0].id,
 	        		remark: $("#remark").val(),
 	        		unit: $("#unit").val() === 'true' ? 2 : 1,
 	        		unitGroupId: $("#unitGroupId").val(),
 	        		firstInUnit: $("#firstInUnit").val(),
 	        		firstOutUnit: $("#firstOutUnit").val(),
	 	        	materialNorms: JSON.stringify(tableData),
	 	        	type: $("#fromType").val() === 'true' ? 1 : 2,
					enclosureInfo: skyeyeEnclosure.getEnclosureIdsByBoxId('enclosureUpload')
 	        	};

 	        	var extendData = new Array();
 	        	$.each($("#extendMationBox .extendMation"), function(i, item) {
 	        		extendData.push({
 	        			labelName: $(item).children(".layui-form-label").children("font").html(),
 	        			content: $(item).children(".layui-input-block").children("input").val(),
 	        			orderBy: (i + 1)
 	        		});
 	        	});
 	        	params.extendData = JSON.stringify(extendData);
 	        	//工序信息
 	        	params.materialProcedure = JSON.stringify(procedureMationList);
 	        	AjaxPostUtil.request({url: flowableBasePath + "material003", params: params, type: 'json', callback: function (json) {
					parent.layer.close(index);
					parent.refreshCode = '0';
 	        	}});
 	        }
 	        return false;
 	    });
 	    
 	    //单个单位新增库存
	    $("body").on("click", "#initialTock", function() {
	    	normsStock = [].concat(normsStockItem);
	    	_openNewWindows({
				url: "../../tpl/materialnormstock/materialnormstock.html", 
				title: "库存信息",
				pageId: "materialnormstock",
				area: ['90vw', '90vh'],
				callBack: function (refreshCode) {
					var str = "";
					normsStockItem = [].concat(normsStock);
					$.each(normsStockItem, function(i, item) {
						str += '<br><span class="layui-badge layui-bg-blue" style="height: 25px !important; line-height: 25px !important; margin: 5px 0px;">' + item.depotName + '<span class="layui-badge layui-bg-gray">' + item.initialTock + '</span></span>';
					});
					$("#initialTock").parent().html('<button type="button" class="layui-btn layui-btn-primary layui-btn-xs" id="initialTock">新增库存</button>' + str);
				}});
	    });
	    //多单位
	    $("body").on("click", ".initialTockMore", function() {
	    	var _this = $(this);
	    	//获取行号
	    	var trRow = _this.attr("id").replace("initialTock", "");
	    	//判断当前行是否有库存集合信息在列表中
	    	var thisRowHasList = -1;
	    	$.each(normsStockList, function(i, item) {
	    		if(item.trRow === trRow){
	    			thisRowHasList = i;
	    			return false;
	    		}
	    	});
	    	if(thisRowHasList >= 0){
	    		normsStock = [].concat(normsStockList[thisRowHasList]["list"]);
	    	} else {
	    		normsStock = new Array();
	    	}
	    	_openNewWindows({
				url: "../../tpl/materialnormstock/materialnormstock.html",
				title: "库存信息",
				pageId: "materialnormstock",
				area: ['70vw', '70vh'],
				callBack: function (refreshCode) {
					var str = "";
					if(thisRowHasList >= 0){
						normsStockList[thisRowHasList]["list"] = [].concat(normsStock);
					} else {
						normsStockList.push({
							trRow: trRow,
							list: [].concat(normsStock)
						});
					}
					$.each(normsStock, function(i, item) {
						str += '<br><span class="layui-badge layui-bg-blue" style="height: 25px !important; line-height: 25px !important; margin: 5px 0px;">' + item.depotName + '<span class="layui-badge layui-bg-gray">' + item.initialTock + '</span></span>';
					});
					_this.parent().html(_this.prop("outerHTML") + str);
				}});
	    });
 	    
 	    //自定义校验是否必填
 	    function subVerifyForm(id){
 	    	if(isNull($("#" + id).val())) {
 	    		$("#" + id).addClass("layui-form-danger");
				$("#" + id).focus();
				winui.window.msg('必填项不能为空', {icon: 5, shift: 6});
 	    	} else {
 	    		return true;
 	    	}
 	    }
 	    
 	    //多单位开关
 		form.on('switch(unit)', function (data) {
 			//同步开关值
 			$(data.elem).val(data.elem.checked);
 			if (data.elem.checked){//多单位
				$(".many-term").show();
				$(".single-term").hide();
 			} else {//单单位
 				$(".many-term").hide();
				$(".single-term").show();
 			}
 		});
 		
 		//商品来源开关
 		form.on('switch(fromType)', function (data) {
 			//同步开关值
 			$(data.elem).val(data.elem.checked);
 		});
 	    
		//添加副单位模板
 	    function addRow(bean){
 	    	var j = {
 	    		trId: "tr" + unitIndex.toString(), //行的id
 	    		unitId: bean.id, //数据库单位id
 	    		unitNameType: bean.baseUnit == 1 ? "基础单位" : "副单位", //单位类型
 	    		unitName: bean.name, //单位
 	    		safetyTock: "safetyTock" + unitIndex.toString(), //安全存量
 	    		initialTock: "initialTock" + unitIndex.toString(), //初始化库存
 	    		retailPrice: "retailPrice" + unitIndex.toString(), //零售价
 	    		lowPrice: "lowPrice" + unitIndex.toString(), //最低售价
 	    		estimatePurchasePrice: "estimatePurchasePrice" + unitIndex.toString(), //预计采购价
 	    		salePrice: "salePrice" + unitIndex.toString() //销售价
 	    	};
 	    	$("#useTable").append(getDataUseHandlebars(beanTemplate, j));
 	    	unitIndex++;
 	    }
 	    
 	    //扩展信息
 	    var extendTemplate = $("#extendTemplate").html();
 	    //新增
 	    $("body").on("click", "#addExtendRow", function() {
 	    	$("#extendMationBox").append(extendTemplate);
 	    });
 	    //删除
 	    $("body").on("click", "#extendMationBox .close-btn", function() {
 	    	var _this = this;
			layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function (index) {
				layer.close(index);
	            $(_this).parent().remove();
			});
 	    });
 	    //双击重命名
 	    $("body").on("dblclick", "#extendMationBox .layui-form-label font", function() {
 	    	var labelName = $(this).html();
 	    	$(this).hide();
 	    	$(this).parent().children("input").val(labelName);
 	    	$(this).parent().children("input").show();
 	    });
 	    $(document).click(function (e) {
 	    	var _con = $('.label-edit');// 设置目标区域
 	    	if(!_con.is(e.target) && _con.has(e.target).length === 0){
 	    		$.each($('.label-edit'), function(i, item) {
 	    			//判断是否是隐藏状态
 	    			if(!$(item).is(':hidden')){
 	    				//显示状态
 	    				$(item).parent().children("font").html($(item).val());
 	    				$(item).parent().children("font").show();
 	    				$(item).hide();
 	    			}
 	    		});
 	    	}
 	    });

	    //工序选择
	    $("body").on("click", "#procedureChoose", function() {
	    	_openNewWindows({
				url: "../../tpl/erpWorkProcedure/erpWorkProcedureChoose.html", 
				title: "工序选择",
				pageId: "erpWorkProcedureChoose",
				area: ['90vw', '90vh'],
				callBack: function (refreshCode) {
					var str = "";
					$.each(procedureMationList, function(i, item) {
						str += '<br><span class="layui-badge layui-bg-blue" style="height: 25px !important; line-height: 25px !important; margin: 5px 0px;">' + item.procedureName + '<span class="layui-badge layui-bg-gray">' + item.number + '</span></span>';
					});
					$("#procedureChoose").parent().html('<button type="button" class="layui-btn layui-btn-primary layui-btn-xs" id="procedureChoose">工序选择</button>' + str);
				}});
	    });
 	    
	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	});
});