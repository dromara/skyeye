
var rowId = "";

layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window',
}).define(['window', 'tableTreeDj', 'jquery', 'winui', 'form'], function (exports) {
    winui.renderColor();
    var $ = layui.$,
        form = layui.form,
        tableTree = layui.tableTreeDj;

    authBtn('1635086167509');

    tableTree.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        url: reportBasePath + 'reportmodeltype001',
        where: getTableParams(),
        cols: [[
            { field: 'name', width: 300, title: '名称'},
            { field: 'createName', title: '创建人', align: 'left', width: 120 },
            { field: 'createTime', title: '创建时间', align: 'center', width: 140 },
            { field: 'lastUpdateName', title: '最后修改人', align: 'left', width: 120 },
            { field: 'lastUpdateTime', title: '最后修改时间', align: 'center', width: 140},
            { title: '操作', fixed: 'right', align: 'center', width: 120, toolbar: '#tableBar'}
        ]],
        done: function(json) {
            matchingLanguage();
        }
    }, {
        keyId: 'id',
        keyPid: 'parentId',
        title: 'name',
    });

    tableTree.getTable().on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'del') { //删除
            del(data);
        } else if (layEvent === 'edit') { //编辑
            edit(data);
        }
    });

    // 删除
    function del(data) {
        layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
            layer.close(index);
            AjaxPostUtil.request({url: reportBasePath + "reportmodeltype003", params: {id: data.id}, type: 'json', method: "DELETE", callback: function(json) {
                winui.window.msg("删除成功", {icon: 1, time: 2000});
                loadTable();
            }});
        });
    }

    // 编辑
    function edit(data) {
        rowId = data.id;
        _openNewWindows({
            url: "../../tpl/reportModelType/reportModelTypeEdit.html",
            title: systemLanguage["com.skyeye.editPageTitle"][languageType],
            pageId: "reportModelTypeEdit",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg("操作成功", {icon: 1, time: 2000});
                loadTable();
            }});
    }

    // 新增
    $("body").on("click", "#addBean", function() {
        _openNewWindows({
            url: "../../tpl/reportModelType/reportModelTypeAdd.html",
            title: systemLanguage["com.skyeye.addPageTitle"][languageType],
            pageId: "reportModelTypeAdd",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg("操作成功", {icon: 1, time: 2000});
                loadTable();
            }});
    });

    form.render();
    form.on('submit(formSearch)', function (data) {
        if (winui.verifyForm(data.elem)) {
            loadTable();
        }
        return false;
    });

    // 刷新数据
    $("body").on("click", "#reloadTable", function() {
        loadTable();
    });

    function loadTable() {
        tableTree.reload("messageTable", {where: getTableParams()});
    }

    function getTableParams() {
        return {
            name: $("#name").val()
        };
    }

    exports('reportModelTypeList', {});
});
