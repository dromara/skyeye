
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

    authBtn('1622876929121');

    // 数据源类型列表
    showGrid({
        id: "dataFromType",
        url: reportBasePath + "reportcommon008",
        params: {},
        pagination: false,
        template: getFileContent('tpl/template/select-option.tpl'),
        method: "GET",
        ajaxSendLoadBefore: function(hdb){
        },
        ajaxSendAfter:function(json){
            initTable();
        }
    });

    // 数据源列表
    function initTable(){
        table.render({
            id: 'messageTable',
            elem: '#messageTable',
            method: 'post',
            url: reportBasePath + 'reportdatafrom001',
            where: getTableParams(),
            even: true,
            page: true,
            limits: getLimits(),
            limit: getLimit(),
            cols: [[
                { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
                { field: 'name', title: '名称', align: 'left', width: 150},
                { field: 'typeName', title: '来源', align: 'left', width: 150 },
                { field: 'remark', title: '备注', align: 'left', width: 150 },
                { field: 'createName', title: '创建人', align: 'left', width: 100 },
                { field: 'createTime', title: '创建时间', align: 'center', width: 140 },
                { field: 'lastUpdateName', title: '最后修改人', align: 'left', width: 100 },
                { field: 'lastUpdateTime', title: '最后修改时间', align: 'center', width: 140},
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
    }

    //添加
    $("body").on("click", "#addBean", function(){
        _openNewWindows({
            url: "../../tpl/reportDataFrom/reportDataFromAdd.html",
            title: systemLanguage["com.skyeye.addPageTitle"][languageType],
            pageId: "reportDataFromAdd",
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

    // 删除
    function delet(data){
        layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
            layer.close(index);
            AjaxPostUtil.request({url:reportBasePath + "reportdatafrom003", params:{id: data.id}, type:'json', method: "DELETE", callback:function(json){
                if (json.returnCode == 0) {
                    winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
                    loadTable();
                }else{
                    winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
                }
            }});
        });
    }

    // 编辑
    function edit(data){
        rowId = data.id;
        _openNewWindows({
            url: "../../tpl/reportDataFrom/reportDataFromEdit.html",
            title: systemLanguage["com.skyeye.editPageTitle"][languageType],
            pageId: "reportDataFromEdit",
            area: ['90vw', '90vh'],
            callBack: function(refreshCode){
                if (refreshCode == '0') {
                    winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                    loadTable();
                } else if (refreshCode == '-9999') {
                    winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2, time: 2000});
                }
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
    $("body").on("click", "#reloadTable", function(){
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
            name: $("#name").val(),
            type: $("#dataFromType").val()
        };
    }

    exports('reportDataFromList', {});
});
