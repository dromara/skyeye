
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

    authBtn('1624176433377');
    // 数据源列表
    table.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        url: reportBasePath + 'reportimporthistory001',
        where: getTableParams(),
        even: true,
        page: true,
        limits: getLimits(),
        limit: getLimit(),
        cols: [[
            { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
            { field: 'fileName', title: '文件名称', align: 'left', width: 200},
            { field: 'fileSize', title: '文件大小', align: 'left', width: 100 },
            { field: 'createName', title: '上传人', align: 'left', width: 100 },
            { field: 'createTime', title: '上传时间', align: 'center', width: 140 }
        ]],
        done: function(){
            matchingLanguage();
        }
    });

    form.render();
    form.on('submit(formSearch)', function (data) {
        if (winui.verifyForm(data.elem)) {
            refreshloadTable();
        }
        return false;
    });

    // 导入数据
    $("body").on("click", "#addBean", function() {
        $("#upfile").val("");
        $("#upfile").click();
    });

    // 上传
    $("body").on("change", "#upfile", function() {
        var formData = new FormData();
        var name = $("#upfile").val();
        formData.append("file", $("#upfile")[0].files[0]);
        formData.append("name", name);
        $.ajax({
            url : reportBasePath + 'reportimporthistory002',
            type : 'POST',
            async : false,
            data : formData,
            headers: getRequestHeaders(),
            // 告诉jQuery不要去处理发送的数据
            processData : false,
            // 告诉jQuery不要去设置Content-Type请求头
            contentType : false,
            dataType:"json",
            beforeSend:function(){
                winui.window.msg("正在进行，请稍候", { shift: 1 });
            },
            success : function(json) {
                if(json.returnCode == "0"){
                    winui.window.msg("成功导入", { shift: 1 });
                    loadTable();
                } else {
                    winui.window.msg("导入失败", {icon: 2, time: 2000});
                }
            }
        });
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
            fileName: $("#fileName").val()
        };
    }

    exports('reportImportHistoryList', {});
});
