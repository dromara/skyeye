
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

    authBtn('1621139557781');
    // 文件模型配置列表
    table.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        url: reportBasePath + 'reportimportmodel001',
        where: getTableParams(),
        toolbar: true,
        even: true,
        page: true,
        limits: getLimits(),
        limit: getLimit(),
        cols: [[
            { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
            { field: 'fileName', title: 'Echarts名称', align: 'left', width: 250},
            { field: 'modelId', title: '模型ID', align: 'left', width: 250 },
            { field: 'firstTypeName', title: '一级分类', align: 'left', width: 120 },
            { field: 'secondTypeName', title: '二级分类', align: 'left', width: 120 },
            { field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], width: 120 },
            { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 },
            { field: 'lastUpdateName', title: systemLanguage["com.skyeye.lastUpdateName"][languageType], align: 'left', width: 120 },
            { field: 'lastUpdateTime', title: systemLanguage["com.skyeye.lastUpdateTime"][languageType], align: 'center', width: 150},
            { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 200, toolbar: '#tableBar'}
        ]],
        done: function(){
            matchingLanguage();
        }
    });

    table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'edit') { //编辑
            edit(data);
        }else if (layEvent === 'delet') { //删除
            delet(data);
        }
    });

    // 添加
    $("body").on("click", "#addBean", function() {
        _openNewWindows({
            url: "../../tpl/reportImportModel/reportImportModelAdd.html",
            title: systemLanguage["com.skyeye.addPageTitle"][languageType],
            pageId: "reportImportModelAdd",
            area: ['90vw', '90vh'],
            callBack: function(refreshCode){
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
    });

    // 删除
    function delet(data){
        layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
            layer.close(index);
            AjaxPostUtil.request({url: reportBasePath + "reportimportmodel003", params:{rowId: data.id}, type: 'json', method: "DELETE", callback: function(json) {
                    if (json.returnCode == 0) {
                        winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
                        loadTable();
                    } else {
                        winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
                    }
                }});
        });
    }

    // 编辑
    function edit(data){
        rowId = data.id;
        _openNewWindows({
            url: "../../tpl/reportImportModel/reportImportModelEdit.html",
            title: systemLanguage["com.skyeye.editPageTitle"][languageType],
            pageId: "reportImportModelEdit",
            area: ['90vw', '90vh'],
            callBack: function(refreshCode){
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }
        });
    }

    form.render();
    form.on('submit(formSearch)', function (data) {
        if (winui.verifyForm(data.elem)) {
            refreshloadTable();
        }
        return false;
    });

    // 刷新数据
    $("body").on("click", "#reloadTable", function() {
        loadTable();
    });

    function loadTable(){
        table.reload("messageTable", {where: getTableParams()});
    }

    function refreshloadTable(){
        table.reload("messageTable", {page: {curr: 1}, where: getTableParams()});
    }

    function getTableParams(){
        return {
            fileName: $("#fileName").val(),
            modelId: $("#modelId").val()
        };
    }

    exports('reportImportModelList', {});
});
