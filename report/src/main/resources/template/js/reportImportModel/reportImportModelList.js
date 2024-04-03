
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
        url: sysMainMation.reportBasePath + 'queryReportImportModelList',
        where: getTableParams(),
        toolbar: true,
        even: true,
        page: true,
        limits: getLimits(),
        limit: getLimit(),
        cols: [[
            { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
            { field: 'name', title: 'Echarts名称', align: 'left', width: 250},
            { field: 'modelCode', title: '模型编码', align: 'left', width: 250 },
            { field: 'typeName', title: '分类', align: 'left', width: 120 },
            { field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], width: 120 },
            { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 },
            { field: 'lastUpdateName', title: systemLanguage["com.skyeye.lastUpdateName"][languageType], align: 'left', width: 120 },
            { field: 'lastUpdateTime', title: systemLanguage["com.skyeye.lastUpdateTime"][languageType], align: 'center', width: 150 },
            { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 240, toolbar: '#tableBar'}
        ]],
        done: function(json) {
            matchingLanguage();
            initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "请输入名称，模型编码", function () {
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
        } else if (layEvent === 'uploadHistory') { //上传历史
            uploadHistory(data);
        } else if (layEvent === 'upload') { //上传
            upload(data);
        }
    });

    // 添加
    $("body").on("click", "#addBean", function() {
        _openNewWindows({
            url: systemCommonUtil.getUrl('FP2024040300001', null),
            title: systemLanguage["com.skyeye.addPageTitle"][languageType],
            pageId: "reportImportModelAdd",
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
            AjaxPostUtil.request({url: sysMainMation.reportBasePath + "delReportImportModelById", params: {id: data.id}, type: 'json', method: "DELETE", callback: function(json) {
                winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
        });
    }

    // 编辑
    function edit(data) {
        _openNewWindows({
            url: systemCommonUtil.getUrl('FP2024040300002&id=' + data.id, null),
            title: systemLanguage["com.skyeye.editPageTitle"][languageType],
            pageId: "reportImportModelEdit",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }
        });
    }

    // 上传历史
    function uploadHistory(data) {
        _openNewWindows({
            url: "../../tpl/reportImportHistory/reportImportHistoryList.html?objectId=" + data.modelCode,
            title: '上传历史',
            pageId: "reportImportModelHistoryList",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
            }
        });
    }

    // 上传
    function upload(data) {
        modelId = data.id
        $("#upfile").val("");
        $("#upfile").click();
    }

    var modelId = '';
    $("body").on("change", "#upfile", function() {
        var formData = new FormData();
        var name = $("#upfile").val();
        formData.append("file", $("#upfile")[0].files[0]);
        formData.append("name", name);
        formData.append("modelId", modelId);
        $.ajax({
            url: sysMainMation.reportBasePath + 'importReportImportModel',
            type: 'POST',
            async: false,
            data: formData,
            headers: getRequestHeaders(),
            // 告诉jQuery不要去处理发送的数据
            processData: false,
            // 告诉jQuery不要去设置Content-Type请求头
            contentType: false,
            dataType: "json",
            beforeSend: function(){
                winui.window.msg("正在进行，请稍候", {shift: 1});
            },
            success: function(json) {
                if (json.returnCode == "0") {
                    winui.window.msg("成功导入", {shift: 1});
                    loadTable();
                } else {
                    winui.window.msg("导入失败", {icon: 2, time: 2000});
                }
            }
        });
    });

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

    exports('reportImportModelList', {});
});
