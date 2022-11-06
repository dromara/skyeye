
var mUnitId = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'skuTable'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
			skuTable = layui.skuTable;

	    showGrid({
		 	id: "showForm",
		 	url: flowableBasePath + "material007",
		 	params: {id: parent.rowId},
		 	pagination: false,
			method: 'GET',
		 	template: $("#beanTemplate").html(),
			ajaxSendLoadBefore: function(hdb, json) {
				json.bean.enabled = skyeyeClassEnumUtil.getEnumDataNameByClassName('commonEnable', 'id', json.bean.enabled, 'name');
				json.bean.fromType = skyeyeClassEnumUtil.getEnumDataNameByClassName('materialFromType', 'id', json.bean.fromType, 'name');
				json.bean.type = skyeyeClassEnumUtil.getEnumDataNameByClassName('materialType', 'id', json.bean.type, 'name');
			},
		 	ajaxSendAfter:function (json) {
				var skuData = {};
				$.each(json.bean.norms, function (index, item) {
					skuData[item.tableNum] = item;
				});
				var enableData = skyeyeClassEnumUtil.getEnumDataListByClassName("commonEnable");
				skuTable.render({
					boxId: 'skuTableBox',
					specTableElemId: 'fairy-spec-table',
					skuTableElemId: 'fairy-sku-table',
					// 是否开启sku表行合并
					rowspan: true,
					edit: false,
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
					specData: JSON.parse(json.bean.normsSpec),
					skuData: skuData,
					otherMationData: json.bean
				});

				// 附件回显
				skyeyeEnclosure.showDetails({"enclosureUploadBtn": json.bean.enclosureInfo});
		 		matchingLanguage();
		 		form.render();
		 	}
		});
		
		$("body").on("click", ".notice-title-click", function (e) {
			mUnitId = $(this).attr("rowid");
			_openNewWindows({
				url: "../../tpl/material/materialStockList.html",
				title: "库存明细",
				pageId: "materialStockList",
				area: ['100vw', '100vh'],
				callBack: function (refreshCode) {
				}});
		});
		
	});
});