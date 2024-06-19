
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

	// 选择类型，默认单选，true:多选，false:单选
	var assetReportCheckType = isNull(parent.adminAssistantUtil.assetReportCheckType) ? false : parent.adminAssistantUtil.assetReportCheckType;

	// 设置提示信息
	var s = '资产明细选择规则：';
	if(assetReportCheckType){
		s += '1.多选；如没有查到要选择的资产信息，请检查资产信息是否满足当前规则。';
		var ids = [];
		$.each(parent.adminAssistantUtil.checkAssetReportMation, function(i, item) {
			ids.push(item.id);
		});
		tableCheckBoxUtil.init({
			gridId: 'messageTable',
			filterId: 'messageTable',
			fieldName: 'id',
			ids: ids
		});
	} else {
		s += '双击要选择的数据即可选中';
		$("#saveCheckBox").hide();
	}
	$("#showInfo").html(s);
	
	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: sysMainMation.admBasePath + 'queryAssetReportList',
	    where: getTableParams(),
		even: true,
	    page: true,
	    limits: getLimits(),
	    limit: getLimit(),
	    cols: [[
	    	{ type: assetReportCheckType ? 'checkbox' : 'radio', rowspan: '3', fixed: 'left' },
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], rowspan: '3', fixed: 'left', type: 'numbers' },
			{ field: 'name', title: '资产名称', width: 120 },
			{ field: 'specifications', title: '资产规格', width: 140 },
			{ field: 'assetImg', title: '图片', align: 'center', width: 60, templet: function (d) {
				return '<img src="' + systemCommonUtil.getFilePath(d.assetImg) + '" class="photo-img" lay-event="assetImg">';
			}},
			{ field: 'typeId', title: '资产类型', width: 120, templet: function(d) {
				return sysDictDataUtil.getDictDataNameByCodeAndKey("ADM_ASSET_TYPE", d.typeId);
			}},
			{ field: 'assetNum', title: '资产编号', width: 160 },
			{ field: 'storageArea', title: '存放区域', width: 200 },
			{ field: 'assetAdminMation', title: '管理员', width: 120, templet: function(d) {
				return getNotUndefinedVal(d.assetAdminMation?.name);
			}},
		]],
	    done: function(res) {
	    	matchingLanguage();

			initTableSearchUtil.initAdvancedSearch(this, res.searchFilter, form, "请输入资产名称，资产编号", function () {
				table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
			});
			if (assetReportCheckType) {
				// 设置选中
				tableCheckBoxUtil.checkedDefault({
					gridId: 'messageTable',
					fieldName: 'id'
				});
			} else {
				$('#messageTable').next().find('.layui-table-body').find("table" ).find("tbody").children("tr").on('dblclick',function() {
					var dubClick = $('#messageTable').next().find('.layui-table-body').find("table").find("tbody").find(".layui-table-hover");
					dubClick.find("input[type='radio']").prop("checked", true);
					form.render();
					var chooseIndex = JSON.stringify(dubClick.data('index'));
					var obj = res.rows[chooseIndex];
					parent.adminAssistantUtil.checkAssetReportMation = obj;

					parent.refreshCode = '0';
					parent.layer.close(index);
				});

				$('#messageTable').next().find('.layui-table-body').find("table" ).find("tbody").children("tr").on('click',function() {
					var click = $('#messageTable').next().find('.layui-table-body').find("table").find("tbody").find(".layui-table-hover");
					click.find("input[type='radio']").prop("checked", true);
					form.render();
				});
			}
	    }
	});

	table.on('tool(messageTable)', function (obj) {
		var data = obj.data;
		var layEvent = obj.event;
		if (layEvent === 'assetImg') { // 图片预览
			systemCommonUtil.showPicImg(systemCommonUtil.getFilePath(data.assetImg));
		}
	});

	// 保存
	$("body").on("click", "#saveCheckBox", function() {
		var selectedData = tableCheckBoxUtil.getValue({
			gridId: 'messageTable'
		});
		if (selectedData.length == 0) {
			winui.window.msg("请选择资产", {icon: 2, time: 2000});
			return false;
		}
		parent.adminAssistantUtil.checkAssetReportMation = [].concat(selectedData);
		parent.layer.close(index);
		parent.refreshCode = '0';
	});

	form.render();
	$("body").on("click", "#reloadTable", function() {
    	loadTable();
    });
    
    function loadTable() {
    	table.reloadData("messageTable", {where: getTableParams()});
    }
    
	function getTableParams() {
		return $.extend(true, {state: "unUse"}, initTableSearchUtil.getSearchValue("messageTable"));
	}
	
    exports('assetReportUnUseChoose', {});
});