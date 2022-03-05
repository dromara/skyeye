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
    authBtn('1643952856976');

    // 加载区域
    shopUtil.getShopAreaMation(function (json){
        $("#areaId").html(getDataUseHandlebars($("#selectTemplate").html(), json));
    });

    table.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        url: shopBasePath + 'store001',
        where: getTableParams(),
        even: true,
        page: true,
        limits: getLimits(),
        limit: getLimit(),
        cols: [[
            { title: systemLanguage["com.skyeye.serialNumber"][languageType], fixed: 'left', type: 'numbers'},
            { field: 'name', title: '门店', align: 'left', width: 270, fixed: 'left', templet: function(d){
                return '<a lay-event="select" class="notice-title-click">' + d.name + '</a>';
            }},
            { field: 'areaName', title: '所属区域', width: 120 },
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
        }else if(layEvent == 'select'){ // 详情
            select(data)
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
            url: "../../tpl/store/storeEdit.html",
            title: systemLanguage["com.skyeye.editPageTitle"][languageType],
            pageId: "storeEdit",
            area: ['90vw', '90vh'],
            callBack: function(refreshCode){
                if (refreshCode == '0') {
                    winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1,time: 2000});
                    loadTable();
                } else if (refreshCode == '-9999') {
                    winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
                }
            }});
    }

    // 删除
    function delet(data){
        layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
            layer.close(index);
            AjaxPostUtil.request({url: shopBasePath + "store004", params: {rowId: data.id}, type: 'json', method: "POST", callback: function(json){
                if(json.returnCode == 0){
                    winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
                    loadTable();
                }else{
                    winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
                }
            }});
        });
    }

    // 设置启用状态
    function editEnabled(data){
        layer.confirm('确认要更改为启用状态吗？', { icon: 3, title: '状态变更' }, function (index) {
            AjaxPostUtil.request({url: shopBasePath + "editStoreEnabledState", params: {rowId: data.id, enabled: shopUtil.enableState["enable"]["type"]}, type: 'json', method: "PUT", callback: function(json){
                if(json.returnCode == 0){
                    winui.window.msg("设置成功。", {icon: 1, time: 2000});
                    loadTable();
                }else{
                    winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
                }
            }});
        });
    }

    // 设置禁用状态
    function editNotEnabled(data){
        layer.confirm('确认要更改为禁用状态吗？', { icon: 3, title: '状态变更' }, function (index) {
            AjaxPostUtil.request({url: shopBasePath + "editStoreEnabledState", params: {rowId: data.id, enabled: shopUtil.enableState["disable"]["type"]}, type: 'json', method: "PUT", callback: function(json){
                if(json.returnCode == 0){
                    winui.window.msg("设置成功。", {icon: 1, time: 2000});
                    loadTable();
                }else{
                    winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
                }
            }});
        });
    }

    // 详情
    function select(data){
        rowId = data.id;
        _openNewWindows({
            url: "../../tpl/store/storeInfo.html",
            title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
            pageId: "storeInfo",
            area: ['90vw', '90vh'],
            callBack: function(refreshCode){
            }
        });
    }

    // 添加
    $("body").on("click", "#addBean", function(){
        _openNewWindows({
            url: "../../tpl/store/storeAdd.html",
            title: systemLanguage["com.skyeye.addPageTitle"][languageType],
            pageId: "storeAdd",
            area: ['90vw', '90vh'],
            callBack: function(refreshCode){
                if (refreshCode == '0') {
                    winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1,time: 2000});
                    loadTable();
                } else if (refreshCode == '-9999') {
                    winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
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
            areaId: $("#areaId").val()
        };
    }

    exports('storeList', {});
});
