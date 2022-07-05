
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

    authBtn('1624759338639');
    // 报表页面列表
    table.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        url: reportBasePath + 'reportpage001',
        where: getTableParams(),
        even: true,
        page: true,
        limits: getLimits(),
        limit: getLimit(),
        cols: [[
            { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
            { field: 'nameCh', title: '页面中文名称', align: 'left', width: 150},
            { field: 'nameEn', title: '页面英文名称', align: 'left', width: 150 },
            { field: 'createName', title: '创建人', align: 'left', width: 100 },
            { field: 'createTime', title: '创建时间', align: 'center', width: 140 },
            { field: 'lastUpdateName', title: '最后修改人', align: 'left', width: 100 },
            { field: 'lastUpdateTime', title: '最后修改时间', align: 'center', width: 140},
            { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 250, toolbar: '#tableBar'}
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
        }else if (layEvent === 'design') { //设计
            design(data);
        }else if (layEvent === 'showPage') { //预览
            showPage(data);
        }
    });

    form.render();
    form.on('submit(formSearch)', function (data) {
        if (winui.verifyForm(data.elem)) {
            refreshloadTable();
        }
        return false;
    });

    //添加
    $("body").on("click", "#addBean", function() {
        _openNewWindows({
            url: "../../tpl/reportPage/reportPageAdd.html",
            title: systemLanguage["com.skyeye.addPageTitle"][languageType],
            pageId: "reportPageAdd",
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
            AjaxPostUtil.request({url: reportBasePath + "reportpage005", params:{rowId: data.id}, type: 'json', method: "DELETE", callback: function(json) {
                winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
        });
    }

    // 编辑
    function edit(data){
        rowId = data.id;
        _openNewWindows({
            url: "../../tpl/reportPage/reportPageEdit.html",
            title: systemLanguage["com.skyeye.editPageTitle"][languageType],
            pageId: "reportPageEdit",
            area: ['90vw', '90vh'],
            callBack: function(refreshCode){
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }
        });
    }

    // 设计
    function design(data){
        parent._openNewWindows({
            url: "../../tpl/pageReportDesign/pageReportDesign.html?rowId=" + data.id,
            title: '报表设计',
            pageId: "pageReportDesign",
            area: ['100vw', '100vh'],
            callBack: function(refreshCode){
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }
        });
    }

    // 预览
    function showPage(data){
        window.open("../../tpl/reportPage/reportPageShow.html?rowId=" + data.id, "_blank");
    }

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
            name: $("#name").val()
        };
    }

    exports('reportPageList', {});
});
