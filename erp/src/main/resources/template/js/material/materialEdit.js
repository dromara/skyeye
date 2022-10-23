
// 工序选择必备参数
var procedureCheckType = 2;//工序选择类型：1.单选procedureMation；2.多选procedureMationList
var procedureMationList = new Array();

// 商品信息
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'fsTree', 'textool', 'skuTable'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
	    	fsTree = layui.fsTree,
			textool = layui.textool,
			skuTable = layui.skuTable;
	    
	    // 商品分类类型树对象
	    var materialCategoryType;
	    showGrid({
		 	id: "showForm",
		 	url: flowableBasePath + "material007",
		 	params: {id: parent.rowId},
		 	pagination: false,
			method: 'GET',
		 	template: $("#beanTemplate").html(),
		 	ajaxSendLoadBefore: function(hdb) {
		 	},
		 	ajaxSendAfter:function(j) {
		 		textool.init({eleId: 'remark', maxlength: 200});
		 		
		 		fsTree.render({
					id: "materialCategoryType",
					url: flowableBasePath + "materialcategory008",
					checkEnable: true,
					loadEnable: false,
					chkStyle: "radio",
					showLine: false,
					showIcon: true,
					expandSpeed: 'fast'
				}, function(id) {
					materialCategoryType = $.fn.zTree.getZTreeObj(id);
					fuzzySearch(id, '#name', null, true); //初始化模糊搜索方法
				});
				var zTree = materialCategoryType.getCheckedNodes(false);
				for (var i = 0; i < zTree.length; i++) {
					if(zTree[i].id == j.bean.categoryId){
						materialCategoryType.checkNode(zTree[i], true, true);
					}
				}

				var skuData = {};
				$.each(j.bean.norms, function (index, item) {
					skuData[item.tableNum] = item;
				});
				var enableData = systemCommonUtil.getEnumDataListByClassName("commonEnable");
				var skuTableObj = skuTable.render({
					boxId: 'skuTableBox',
					specTableElemId: 'fairy-spec-table',
					skuTableElemId: 'fairy-sku-table',
					// 是否开启sku表行合并
					rowspan: true,
					// 多规格SKU表配置
					multipleSkuTableConfig: {
						thead: [
							{title: '图片', icon: ''},
							{title: '安全库存', icon: 'layui-icon-cols'},
							{title: '初始库存', width: '150px'},
							{title: '零售价(元)', icon: 'layui-icon-cols'},
							{title: '最低售价(元)', icon: 'layui-icon-cols'},
							{title: '采购价/成本价(元)', icon: 'layui-icon-cols'},
							{title: '销售价(元)', icon: 'layui-icon-cols'},
							{title: '状态', icon: ''},
						],
						tbody: [
							{type: 'image', field: 'logo', value: '', verify: 'required', reqtext: ''},
							{type: 'input', field: 'safetyTock', value: '0', verify: 'required|number'},
							{type: 'btn', field: 'normsStock'},
							{type: 'input', field: 'retailPrice', value: '0', verify: 'required|money'},
							{type: 'input', field: 'lowPrice', value: '0', verify: 'required|money'},
							{type: 'input', field: 'estimatePurchasePrice', value: '0', verify: 'required|money'},
							{type: 'input', field: 'salePrice', value: '0', verify: 'required|money'},
							{type: 'select', field: 'enable', option: enableData.rows},
						]
					},
					specData: JSON.parse(j.bean.normsSpec),
					skuData: skuData,
					otherMationData: j.bean
				});

				systemCommonUtil.showEnumDataListByClassName("commonEnable", 'radio', "enabled", j.bean.enabled, form);
				systemCommonUtil.showEnumDataListByClassName("materialFromType", 'radio', "fromType", j.bean.fromType, form);
				systemCommonUtil.showEnumDataListByClassName("materialType", 'radio', "materialType", j.bean.materialType, form);

				// 初始化工序
				procedureMationList = [].concat(j.bean.procedureMationList);

				skyeyeEnclosure.initTypeISData({'enclosureUpload': j.bean.enclosureInfo});
				matchingLanguage();
				form.render();
		 	    form.on('submit(formEditBean)', function (data) {
		 	        if (winui.verifyForm(data.elem)) {
		 	        	var checkNodes = materialCategoryType.getCheckedNodes(true);
		 	        	if(checkNodes.length == 0){
		 	        		winui.window.msg('请选择商品所属类型', {icon: 2, time: 2000});
		 	        		return false;
		 	        	}
						var params = {
		        			materialName: $("#materialName").val(),
		 	        		model: $("#model").val(),
							unitName: isNull($("#unitName").val()) ? "" : $("#unitName").val(),
		 	        		categoryId: checkNodes[0].id,
		 	        		remark: $("#remark").val(),
							unit: $("input[name='unit']:checked").val(),
							unitGroupId: isNull($("#unitGroupId").val()) ? "" : $("#unitGroupId").val(),
							firstInUnit: isNull($("#firstInUnit").val()) ? "" : $("#firstInUnit").val(),
							firstOutUnit: isNull($("#firstOutUnit").val()) ? "" : $("#firstOutUnit").val(),
							materialNorms: JSON.stringify(skuTableObj.getFormSkuDataList()),
							normsSpec: JSON.stringify(skuTableObj.getFormSpecData()),
							fromType: $("#fromType input:radio:checked").val(),
							type: $("#materialType input:radio:checked").val(),
							enabled: $("#enabled input:radio:checked").val(),
							enclosureInfo: skyeyeEnclosure.getEnclosureIdsByBoxId('enclosureUpload'),
							materialProcedure: JSON.stringify(procedureMationList),
							id: parent.rowId
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
		 	        	AjaxPostUtil.request({url: flowableBasePath + "writeMaterialMation", params: params, type: 'json', method: 'POST', callback: function (json) {
							parent.layer.close(index);
							parent.refreshCode = '0';
		 	        	}});
		 	        }
		 	        return false;
		 	    });
		 	}
		});
	    
 	    // 扩展信息
 	    var extendTemplate = $("#extendTemplate").html();
 	    // 新增
 	    $("body").on("click", "#addExtendRow", function() {
 	    	$("#extendMationBox").append(extendTemplate);
 	    });
 	    // 删除
 	    $("body").on("click", "#extendMationBox .close-btn", function() {
 	    	var _this = this;
			layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function (index) {
				layer.close(index);
	            $(_this).parent().remove();
			});
 	    });
 	    // 双击重命名
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
 	    
 	    // 工序选择
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