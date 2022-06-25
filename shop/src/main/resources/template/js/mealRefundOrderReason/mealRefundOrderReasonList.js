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
    authBtn('1646836960329');

    table.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        url: shopBasePath + 'queryMealRefundOrderReasonList',
        where: getTableParams(),
        even: true,
        page: true,
        limits: getLimits(),
        limit: getLimit(),
        cols: [[
            { title: systemLanguage["com.skyeye.serialNumber"][languageType], fixed: 'left', type: 'numbers'},
            { field: 'titleCn', title: '中文名称', width: 160 },
            { field: 'titleEn', title: '英文名称', width: 160 },
            { field: 'enabled', title: '状态', align: 'center', width: 80, templet: function(d){
                return shopUtil.getEnableStateName(d.enabled);
            }},
            { field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], width: 120 },
            { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 },
            { field: 'lastUpdateName', title: systemLanguage["com.skyeye.lastUpdateName"][languageType], align: 'left', width: 120 },
            { field: 'lastUpdateTime', title: systemLanguage["com.skyeye.lastUpdateTime"][languageType], align: 'center', width: 150},
            { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 150, toolbar: '#tableBar'}
        ]],
	    done: function(){
	    	matchingLanguage();
	    }
    });

    table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'edit') { // 编辑
            edit(data);
        }else if (layEvent === 'delete') { // 删除
            delet(data);
        }else if (layEvent === 'enabled') { // 启用
            editEnabled(data);
        }else if(layEvent == 'unenabled'){ // 禁用
            editNotEnabled(data)
        }
    });

    // 编辑
    function edit(data){
        rowId = data.id;
        _openNewWindows({
            url: "../../tpl/mealRefundOrderReason/mealRefundOrderReasonEdit.html",
            title: systemLanguage["com.skyeye.editPageTitle"][languageType],
            pageId: "mealRefundOrderReasonEdit",
            area: ['90vw', '90vh'],
            callBack: function(refreshCode){
                if (refreshCode == '0') {
                    winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                    loadTable();
                } else if (refreshCode == '-9999') {
                    winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2, time: 2000});
                }
            }});
    }

    // 删除
    function delet(data){
        layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
            layer.close(index);
            AjaxPostUtil.request({url: shopBasePath + "deleteMealRefundOrderReasonById", params: {id: data.id}, type: 'json', method: "POST", callback: function(json){
                if (json.returnCode == 0) {
                    winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
                    loadTable();
                } else {
                    winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
                }
            }});
        });
    }

    // 设置启用状态
    function editEnabled(data){
        layer.confirm('确认要更改为启用状态吗？', { icon: 3, title: '状态变更' }, function (index) {
            AjaxPostUtil.request({url: shopBasePath + "editMealRefundOrderReasonEnabledState", params: {id: data.id, enabled: shopUtil.enableState["enable"]["type"]}, type: 'json', method: "PUT", callback: function(json){
                if (json.returnCode == 0) {
                    winui.window.msg("设置成功。", {icon: 1, time: 2000});
                    loadTable();
                } else {
                    winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
                }
            }});
        });
    }

    // 设置禁用状态
    function editNotEnabled(data){
        layer.confirm('确认要更改为禁用状态吗？', { icon: 3, title: '状态变更' }, function (index) {
            AjaxPostUtil.request({url: shopBasePath + "editMealRefundOrderReasonEnabledState", params: {id: data.id, enabled: shopUtil.enableState["disable"]["type"]}, type: 'json', method: "PUT", callback: function(json){
                if (json.returnCode == 0) {
                    winui.window.msg("设置成功。", {icon: 1, time: 2000});
                    loadTable();
                } else {
                    winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
                }
            }});
        });
    }

    // 添加
    $("body").on("click", "#addBean", function() {
        _openNewWindows({
            url: "../../tpl/mealRefundOrderReason/mealRefundOrderReasonAdd.html",
            title: systemLanguage["com.skyeye.addPageTitle"][languageType],
            pageId: "mealRefundOrderReasonAdd",
            area: ['90vw', '90vh'],
            callBack: function(refreshCode){
                if (refreshCode == '0') {
                    winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                    loadTable();
                } else if (refreshCode == '-9999') {
                    winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2, time: 2000});
                }
            }});
    });

    form.render();
    form.on('submit(formSearch)', function (data) {
        if (winui.verifyForm(data.elem)) {
            table.reload("messageTable", {page: {curr: 1}, where: getTableParams()})
        }
        return false;
    });

    $("body").on("click", "#reloadTable", function() {
        loadTable();
    });

    // 刷新
    function loadTable(){
        table.reload("messageTable", {where: getTableParams()});
    }

    function getTableParams(){
        return {
            name: $("#name").val(),
            enabled: $("#enabled").val()
        };
    }

    exports('mealRefundOrderReasonList', {});
});
