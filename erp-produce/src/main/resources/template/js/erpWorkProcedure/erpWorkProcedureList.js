
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
    
    authBtn('1589098965322');
    
    table.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        url: flowableBasePath + 'erpworkprocedure001',
        where: getTableParams(),
        even: true,
        page: true,
        limits: getLimits(),
        limit: getLimit(),
        cols: [[
            { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
            { field: 'number', title: '工序编号', align: 'left',width: 100, templet: function (d) {
                return '<a lay-event="details" class="notice-title-click">' + d.number + '</a>';
            }},
            { field: 'name', title: '工序名称', align: 'left',width: 120},
            { field: 'typeName', title: '类别', align: 'left',width: 100},
            { field: 'operatorUserNum', title: '操作员', align: 'left',width: 100},
            { field: 'unitPrice', title: '参考单价', align: 'left',width: 100},
            { field: 'departmentName', title: '部门', align: 'left',width: 80},
            { field: 'content', title: '工序内容', align: 'left',width: 200},
            { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 100, toolbar: '#tableBar'}
        ]],
	    done: function(json) {
	    	matchingLanguage();
	    }
    });

    table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'edit') { //编辑
            edit(data);
        } else if (layEvent === 'delete') { //删除
            deleteAccount(data);
        } else if (layEvent === 'details') { //详情
            details(data);
        }
    });

    // 搜索表单
    form.render();
    form.on('submit(formSearch)', function (data) {
        
        if (winui.verifyForm(data.elem)) {
            table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()})
        }
        return false;
    });

    // 编辑
    function edit(data) {
        rowId = data.id;
        _openNewWindows({
            url: "../../tpl/erpWorkProcedure/erpWorkProcedureEdit.html",
            title: systemLanguage["com.skyeye.editPageTitle"][languageType],
            pageId: "erpWorkProcedureEdit",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }
        });
    }

    // 删除
    function deleteAccount(data) {
        layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function (index) {
            layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "erpworkprocedure004", params: {rowId: data.id}, type: 'json', callback: function (json) {
                winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
        });
    }

    // 添加
    $("body").on("click", "#addBean", function() {
        _openNewWindows({
            url: "../../tpl/erpWorkProcedure/erpWorkProcedureAdd.html",
            title: systemLanguage["com.skyeye.addPageTitle"][languageType],
            pageId: "erpWorkProcedureAdd",
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
            url: "../../tpl/erpWorkProcedure/erpWorkProcedureDetail.html",
            title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
            pageId: "erpWorkProcedureDetail",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
            }
        });
    }

    $("body").on("click", "#reloadTable", function() {
        loadTable();
    });

    // 刷新
    function loadTable() {
        table.reloadData("messageTable", {where: getTableParams()});
    }

    function getTableParams() {
    	return {
    		name: $("#name").val(), 
    		number: $("#number").val()
    	};
    }

    exports('erpWorkProcedureList', {});
});
