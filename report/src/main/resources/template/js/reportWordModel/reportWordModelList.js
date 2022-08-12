
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

    authBtn('1632727690803');
    // 文字模型列表
    table.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        url: reportBasePath + 'reportwordmodel001',
        where: getTableParams(),
        even: true,
        page: true,
        limits: getLimits(),
        limit: getLimit(),
        cols: [[
            { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
            { field: 'title', title: '标题', align: 'left', width: 150, templet: function (d) {
                return '<a lay-event="details" class="notice-title-click">' + d.title + '</a>';
            }},
            { field: 'logo', title: 'LOGO', align: 'center', width: 180, height: 60, templet: function (d) {
                return '<img src="' + fileBasePath + d.logo + '" style="height:60px" class="cursor" lay-event="printsPicUrl">';
            }},
            { field: 'defaultWidth', title: '默认宽度', align: 'center', width: 120 },
            { field: 'defaultHeight', title: '默认高度', align: 'center', width: 120 },
            { field: 'state', title: '状态', align: 'center', width: 120, templet: function (d) {
                if(d.state == 1){
                    return '未发布';
                } else {
                    return '已发布';
                }
            }},
            { field: 'firstTypeName', title: '一级分类', align: 'left', width: 120 },
            { field: 'secondTypeName', title: '二级分类', align: 'left', width: 120 },
            { field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], width: 120 },
            { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 },
            { field: 'lastUpdateName', title: systemLanguage["com.skyeye.lastUpdateName"][languageType], align: 'left', width: 120 },
            { field: 'lastUpdateTime', title: systemLanguage["com.skyeye.lastUpdateTime"][languageType], align: 'center', width: 150},
            { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 250, toolbar: '#tableBar'}
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
        }else if (layEvent === 'delet') { // 删除
            delet(data);
        }else if (layEvent === 'details') { // 详情
            details(data);
        }else if (layEvent === 'publish') { // 发布
            publish(data);
        }else if (layEvent === 'unPublish') { // 取消发布
            unPublish(data);
        }else if (layEvent === 'printsPicUrl') { //图片预览
            systemCommonUtil.showPicImg(fileBasePath + data.logo);
        }
    });

    form.render();

    // 添加
    $("body").on("click", "#addBean", function() {
        _openNewWindows({
            url: "../../tpl/reportWordModel/reportWordModelAdd.html",
            title: systemLanguage["com.skyeye.addPageTitle"][languageType],
            pageId: "reportWordModelAdd",
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
            AjaxPostUtil.request({url: reportBasePath + "reportwordmodel003", params:{id: data.id}, type: 'json', method: "DELETE", callback: function(json) {
                winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
        });
    }

    // 发布
    function publish(data){
        layer.confirm('确定发布该模型吗？', {icon: 3, title: '发布操作'}, function(index){
            layer.close(index);
            AjaxPostUtil.request({url: reportBasePath + "reportwordmodel008", params:{id: data.id}, type: 'json', method: "PUT", callback: function(json) {
                winui.window.msg('操作成功', {icon: 1, time: 2000});
                loadTable();
            }});
        });
    }

    // 取消发布
    function unPublish(data){
        layer.confirm('确定取消发布该模型吗？', {icon: 3, title: '取消发布操作'}, function(index){
            layer.close(index);
            AjaxPostUtil.request({url: reportBasePath + "reportwordmodel009", params:{id: data.id}, type: 'json', method: "PUT", callback: function(json) {
                winui.window.msg('操作成功', {icon: 1, time: 2000});
                loadTable();
            }});
        });
    }

    // 编辑
    function edit(data){
        rowId = data.id;
        _openNewWindows({
            url: "../../tpl/reportWordModel/reportWordModelEdit.html",
            title: systemLanguage["com.skyeye.editPageTitle"][languageType],
            pageId: "reportWordModelEdit",
            area: ['90vw', '90vh'],
            callBack: function(refreshCode){
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }
        });
    }

    // 详情
    function details(data){
        rowId = data.id;
        _openNewWindows({
            url: "../../tpl/reportWordModel/reportWordModelDetails.html",
            title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
            pageId: "reportWordModelDetails",
            area: ['90vw', '90vh'],
            callBack: function(refreshCode){
            }
        });
    }

    form.on('submit(formSearch)', function (data) {
        if (winui.verifyForm(data.elem)) {
            table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
        }
        return false;
    });

    // 刷新数据
    $("body").on("click", "#reloadTable", function() {
        loadTable();
    });

    function loadTable(){
        table.reloadData("messageTable", {where: getTableParams()});
    }

    function getTableParams(){
        return {
            title: $("#title").val()
        };
    }

    exports('reportWordModelList', {});
});
