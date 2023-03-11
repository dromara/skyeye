var rowId = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		form = layui.form,
		table = layui.table;

	// 公海客户群
	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: sysMainMation.crmBasePath + 'customer012',
	    where: getTableParams(),
	    even: true,
	    page: true,
	    limits: getLimits(),
	    limit: getLimit(),
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
	        { field: 'name', title: '客户名称', align: 'left', width: 300, templet: function (d) {
	        	return '<a lay-event="details" class="notice-title-click">' + d.name + '</a>';
	        }},
	        { field: 'typeId', title: '客户分类', align: 'left', width: 120, templet: function (d) {
				return sysDictDataUtil.getDictDataNameByCodeAndKey("CRM_CUSTOMER_TYPE", d.typeId);
			}},
	        { field: 'fromId', title: '客户来源', align: 'left', width: 120, templet: function (d) {
				return sysDictDataUtil.getDictDataNameByCodeAndKey("CRM_CUSTOMER_FROM", d.fromId);
			}},
	        { field: 'industryId', title: '所属行业', align: 'left', width: 180, templet: function (d) {
				return sysDictDataUtil.getDictDataNameByCodeAndKey("CRM_CUSTOMER_INDUSTRY", d.industryId);
			}},
	        { field: 'noDocumentaryDayNum', title: '未跟单天数', align: 'left', width: 100 },
	        { field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], align: 'left', width: 120 },
	        { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 100 },
	        { field: 'lastUpdateName', title: systemLanguage["com.skyeye.lastUpdateName"][languageType], align: 'left', width: 120 },
	        { field: 'lastUpdateTime', title: '最后修改时间', align: 'center', width: 100}
	    ]],
	    done: function (json) {
	    	matchingLanguage();
	    	var str = "超过" + json.bean.noDocumentaryDayNum + "天未跟单的客户。";
	    	$("#showInfo").html(str);

			initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "请输入客户名称", function () {
				table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
			});
	    }
	});
	
	table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'details') { // 详情
        	details(data);
        }
    });
	
	// 详情
	function details(data) {
		_openNewWindows({
			url: "../../tpl/customerManage/customerManage.html?objectId=" + data.id + "&objectKey=" + data.serviceClassName,
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "customerDetails",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
			}});
	}

	form.render();
	$("body").on("click", "#reloadTable", function() {
		loadTable();
	});

	function loadTable() {
		table.reloadData("messageTable", {where: getTableParams()});
	}

	function getTableParams() {
		return $.extend(true, {}, initTableSearchUtil.getSearchValue("messageTable"));
	}
	
    exports('customerInternationalList', {});
});