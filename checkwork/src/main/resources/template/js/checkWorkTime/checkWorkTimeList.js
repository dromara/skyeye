
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
	
	authBtn('1603026174350');
	
	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: sysMainMation.checkworkBasePath + 'checkworktime001',
	    where: getTableParams(),
	    even: true,
	    page: true,
	    limits: getLimits(),
    	limit: getLimit(),
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
	        { field: 'name', title: '标题', align: 'left', width: 150, templet: function (d) {
	        	return '<a lay-event="details" class="notice-title-click">' + d.name + '</a>';
	        }},
	        { field: 'startTime', title: '工作时间段', align: 'center', width: 120, templet: function (d) {
        		return d.startTime + ' ~ ' + d.endTime;
	        }},
			{ field: 'restStartTime', title: '作息时间段', align: 'center', width: 120, templet: function (d) {
				return d.restStartTime + ' ~ ' + d.restEndTime;
			}},
	        { field: 'type', title: '类型', width: 80, align: 'center', templet: function (d) {
				return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("checkWorkTimeType", 'id', d.type, 'name');
			}},
	        { field: 'enabled', title: '状态', width: 80, align: 'center', templet: function (d) {
				return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("commonEnable", 'id', d.enabled, 'name');
	        }},
	        { field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], align: 'left', width: 120 },
            { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 },
            { field: 'lastUpdateName', title: systemLanguage["com.skyeye.lastUpdateName"][languageType], align: 'left', width: 120 },
            { field: 'lastUpdateTime', title: '最后修改时间', align: 'center', width: 150},
	        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 120, toolbar: '#tableBar'}
	    ]],
	    done: function(json) {
	    	matchingLanguage();
			initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "请输入标题", function () {
				table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
			});
	    }
	});
	
	table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'edit') { //编辑
        	edit(data);
        } else if (layEvent === 'delet') { //删除
        	delet(data);
        } else if (layEvent === 'details') { //详情
        	details(data);
        }
    });
	
	// 添加
	$("body").on("click", "#addBean", function() {
    	_openNewWindows({
			url: "../../tpl/checkWorkTime/checkWorkTimeAdd.html",
			title: systemLanguage["com.skyeye.addPageTitle"][languageType],
			pageId: "checkWorkTimeAdd",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
    });
	
	// 删除
	function delet(data) {
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: sysMainMation.checkworkBasePath + "deleteCheckWorkTimeById", params: {id: data.id}, type: 'json', method: "DELETE", callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}
	
	// 编辑
	function edit(data) {
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/checkWorkTime/checkWorkTimeEdit.html", 
			title: systemLanguage["com.skyeye.editPageTitle"][languageType],
			pageId: "checkWorkTimeEdit",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}
		});
	}
	
	// 详情
	function details(data) {
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/checkWorkTime/checkWorkTimeDetails.html", 
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "checkWorkTimeDetails",
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
    
    exports('checkWorkTimeList', {});
});
