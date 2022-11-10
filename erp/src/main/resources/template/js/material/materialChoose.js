var rowId = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form', 'tableCheckBoxUtil'], function (exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
	var $ = layui.$,
		form = layui.form,
		table = layui.table,
		tableCheckBoxUtil = layui.tableCheckBoxUtil;

	var checkType = '1';//商品选择类型：1.单选；2.多选
	
	if (!isNull(parent.erpOrderUtil.productCheckType)){
		checkType = parent.erpOrderUtil.productCheckType;
	}

	var s = "商品选择规则：";
	if(checkType == "1"){
		s += '1.单选，双击指定行数据即可选中；';
	} else {
		s += '1.多选；';
		//显示保存按钮
		$("#saveCheckBox").show();
	}
	s += '如没有查到要选择的商品，请检查商品信息是否满足当前规则。';
	$("#showInfo").html(s);

	var categoryId = "";
	sysDictDataUtil.showDictDataListByDictTypeCode(sysDictData["erpMaterialCategory"]["key"], 'selectTree', "materialCategoryType", '', form, function () {
		initTable();
	}, function (chooseId) {
		categoryId = chooseId;
		refreshTable();
	});
	
	function initTable(){
		if(checkType == '2'){
			//初始化值
			var ids = [];
			$.each(parent.erpOrderUtil.chooseProductMation, function(i, item) {
				ids.push(item.materialId);
			});
			tableCheckBoxUtil.setIds({
				gridId: 'messageTable',
				fieldName: 'materialId',
				ids: ids
			});
			tableCheckBoxUtil.init({
				gridId: 'messageTable',
				filterId: 'messageTable',
				fieldName: 'materialId'
			});
		}
			
		table.render({
		    id: 'messageTable',
		    elem: '#messageTable',
		    method: 'post',
		    url: flowableBasePath + 'material010',
		    where: getTableParams(),
			even: true,
		    page: true,
		    limits: [8, 16, 24, 32, 40, 48, 56],
		    limit: 8,
		    cols: [[
		    	{ type: checkType == '1' ? 'radio' : 'checkbox'},
		        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
		        { field: 'materialName', title: '商品名称', align: 'left', width: 150, templet: function (d) {
			        	return '<a lay-event="details" class="notice-title-click">' + d.materialName + '</a>';
			    }},
		        { field: 'materialModel', title: '型号', align: 'left', width: 150 },
		        { field: 'categoryName', title: '所属类型', align: 'left', width: 100 },
		        { field: 'typeName', title: '商品来源', align: 'left', width: 100 },
		        { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 }
		    ]],
		    done: function(json, curr, count) {
		    	matchingLanguage();
		    	if(checkType == '1'){
			    	$('#messageTable').next().find('.layui-table-body').find("table" ).find("tbody").children("tr").on('dblclick',function(){
						var dubClick = $('#messageTable').next().find('.layui-table-body').find("table").find("tbody").find(".layui-table-hover");
						dubClick.find("input[type='radio']").prop("checked", true);
						form.render();
						var chooseIndex = JSON.stringify(dubClick.data('index'));
						var obj = json.rows[chooseIndex];
						parent.erpOrderUtil.chooseProductMation = obj;
						
						parent.refreshCode = '0';
						parent.layer.close(index);
					});
					
					$('#messageTable').next().find('.layui-table-body').find("table" ).find("tbody").children("tr").on('click',function(){
						var click = $('#messageTable').next().find('.layui-table-body').find("table").find("tbody").find(".layui-table-hover");
						click.find("input[type='radio']").prop("checked", true);
						form.render();
					})
		    	} else {
		    		// 多选,设置选中
		    		tableCheckBoxUtil.checkedDefault({
						gridId: 'messageTable',
						fieldName: 'materialId'
					});
		    	}

				initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "请输入商品名称，型号", function () {
					table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
				});

		    }
		});
		
		table.on('tool(messageTable)', function (obj) {
	        var data = obj.data;
	        var layEvent = obj.event;
	        if (layEvent === 'details') { //详情
	        	details(data);
	        }
	    });
		
		form.render();
	}
	
	// 详情
	function details(data) {
		rowId = data.materialId;
		_openNewWindows({
			url: "../../tpl/material/materialDetails.html",
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "materialDetails",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
			}});
	}
	
	// 保存按钮-多选才有
	$("body").on("click", "#saveCheckBox", function() {
		var selectedData = tableCheckBoxUtil.getValue({
			gridId: 'messageTable'
		});
		AjaxPostUtil.request({url: flowableBasePath + "material013", params: {ids: selectedData.toString()}, type: 'json', callback: function (json) {
			parent.erpOrderUtil.chooseProductMation = [].concat(json.rows);
			parent.layer.close(index);
			parent.refreshCode = '0';
   		}});
	});

	$("body").on("click", "#reloadTable", function() {
    	loadTable();
    });
    
    function loadTable() {
    	table.reloadData("messageTable", {where: getTableParams()});
    }
    
    function refreshTable(){
    	table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
    }

	function getTableParams() {
		return $.extend(true, {categoryId: categoryId}, initTableSearchUtil.getSearchValue("messageTable"));
	}
	
    exports('materialChoose', {});
});