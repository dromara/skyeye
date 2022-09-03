
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
	
	authBtn('1637494526858');

	systemModelUtil.loadSysEveModelTypeByPId("firstTypeId", "0");

	form.on('select(firstTypeId)', function(data) {
		var thisRowValue = data.value;
		systemModelUtil.loadSysEveModelTypeByPId("secondTypeId", isNull(thisRowValue) ? "-" : thisRowValue);
		form.render('select');
	});
	
	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: reqBasePath + 'sysevemodel001',
	    where: getTableParams(),
	    even: true,
	    page: true,
		limits: getLimits(),
		limit: getLimit(),
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
	        { field: 'title', title: '标题', width: 300, templet: function (d) {
				return '<a lay-event="details" class="notice-title-click">' + d.title + '</a>';
			}},
	        { field: 'logo', title: 'LOGO', align: 'center', width: 100, templet: function (d) {
				return '<img src="' + fileBasePath + d.logo + '" class="photo-img" lay-event="sysPic">';
	        }},
	        { field: 'firstTypeName', title: '一级分类', width: 120 },
	        { field: 'secondTypeName', title: '二级分类', width: 120 },
			{ field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], width: 120 },
			{ field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 },
			{ field: 'lastUpdateName', title: systemLanguage["com.skyeye.lastUpdateName"][languageType], align: 'left', width: 120 },
			{ field: 'lastUpdateTime', title: systemLanguage["com.skyeye.lastUpdateTime"][languageType], align: 'center', width: 150 },
	        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 180, toolbar: '#tableBar'}
	    ]],
	    done: function(json) {
	    	matchingLanguage();
	    }
	});
	
	table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
		if (layEvent === 'del') { // 删除
			del(data, obj);
		} else if (layEvent === 'edit') { // 编辑
			edit(data);
		} else if (layEvent === 'details') { // 详情
			details(data);
		} else if (layEvent === 'sysPic') { // 图片
			systemCommonUtil.showPicImg(fileBasePath + data.logo);
		}
    });
	
	// 删除
	function del(data, obj) {
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
			layer.close(index);
            AjaxPostUtil.request({url: reqBasePath + "sysevemodel003", params: {id: data.id}, type: 'json', method: "DELETE", callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}
	
	// 编辑
	function edit(data) {
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/sysEveModel/sysEveModelEdit.html",
			title: systemLanguage["com.skyeye.editPageTitle"][languageType],
			pageId: "sysEveModelEdit",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	}
	
	// 新增
	$("body").on("click", "#addBean", function() {
		_openNewWindows({
			url: "../../tpl/sysEveModel/sysEveModelAdd.html?type=1",
			title: systemLanguage["com.skyeye.addPageTitle"][languageType],
			pageId: "sysEveModelAdd",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
    });

	// 详情
	function details(data) {
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/sysEveModel/sysEveModelDetails.html",
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "sysEveModelDetails",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
			}});
	}

	form.render();
	form.on('submit(formSearch)', function (data) {
		if (winui.verifyForm(data.elem)) {
			refreshTable();
		}
		return false;
	});

	// 刷新数据
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
    	return {
    		title: $("#title").val(),
			firstTypeId: $("#firstTypeId").val(),
			secondTypeId: $("#secondTypeId").val(),
			type: 1
    	};
	}
    
    exports('sysEveModelList', {});
});
