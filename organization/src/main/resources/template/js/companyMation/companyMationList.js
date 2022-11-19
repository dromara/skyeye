
var rowId = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'tableTreeDj', 'jquery', 'winui', 'form'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		form = layui.form,
		tableTree = layui.tableTreeDj;
	
	authBtn('1552959308337');
	tableTree.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        url: reqBasePath + 'companymation001',
        where: getTableParams(),
        cols: [[
            { field: 'name', width: 300, title: '公司名称' },
            { field: 'remark', width: 80, title: '公司简介', align: 'center', templet: function (d) {
	        	return '<i class="fa fa-fw fa-html5 cursor" lay-event="remark"></i>';
	        }},
	        { field: 'departmentNum', title: '部门数', width: 100 },
	        { field: 'userNum', title: '员工数', width: 100 },
            { field:'id', width: 400, title: '公司地址', templet: function (d) {
            	var str = d.provinceName + " ";
            	if (!isNull(d.cityName)) {
            		str += d.cityName + " ";
            	}
            	if (!isNull(d.areaName)) {
            		str += d.areaName + " ";
            	}
            	if (!isNull(d.townshipName)) {
            		str += d.townshipName + " ";
            	}
            	if (!isNull(d.absoluteAddress)) {
            		str += d.absoluteAddress;
            	}
	        	return str;
	        }},
	        { field:'createTime', width: 150, align: 'center', title: systemLanguage["com.skyeye.entryTime"][languageType] },
            { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 240, toolbar: '#tableBar' }
        ]],
        isPage: false,
	    done: function(json) {
	    	matchingLanguage();
			initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "请输入名称", function () {
				table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
			});
	    }
    }, {
		keyId: 'id',
		keyPid: 'pId',
		title: 'name',
	});

	tableTree.getTable().on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'del') { //删除
        	del(data, obj);
        } else if (layEvent === 'edit') { //编辑
        	edit(data);
        } else if (layEvent === 'remark') { //公司简介
        	layer.open({
	            id: '公司简介',
	            type: 1,
	            title: '公司简介',
	            shade: 0.3,
	            area: ['90vw', '90vh'],
	            content: data.remark
	        });
        }
    });
	
	// 删除
	function del(data, obj) {
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: reqBasePath + "deleteCompanyMationById", params: {id: data.id}, type: 'json', method: 'DELETE', callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}
	
	// 编辑
	function edit(data) {
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/companyMation/companyMationEdit.html",
			title: systemLanguage["com.skyeye.editPageTitle"][languageType],
			pageId: "companyMationEdit",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	}
	
    // 新增
    $("body").on("click", "#addBean", function() {
    	_openNewWindows({
			url: "../../tpl/companyMation/companyMationAdd.html",
			title: systemLanguage["com.skyeye.addPageTitle"][languageType],
			pageId: "companyMationAdd",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
    });

	form.render();
	$("body").on("click", "#reloadTable", function() {
		loadTable();
	});

	function loadTable() {
		tableTree.reload("messageTable", {where: getTableParams()});
	}

	function getTableParams() {
		return $.extend(true, {}, initTableSearchUtil.getSearchValue("messageTable"));
	}
    
    exports('companyMationList', {});
});
