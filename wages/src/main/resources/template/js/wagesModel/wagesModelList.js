
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
    var selTemplate = getFileContent('tpl/template/select-option.tpl');

    authBtn('1610795467751');

    showGrid({
        id: "typeId",
        url: reqBasePath + "wagesmodeltype008",
        params: {},
        pagination: false,
        template: selTemplate,
        method: 'GET',
        ajaxSendLoadBefore: function(hdb){},
        ajaxSendAfter:function(json){
            form.render('select');
        }
    });

    // 薪资模板列表
    table.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        url: reqBasePath + 'wagesmodel001',
        where: getTableParams(),
        even: true,
        page: true,
        limits: getLimits(),
        limit: getLimit(),
        cols: [[
            { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
            { field: 'title', title: '名称', align: 'left', width: 150, templet: function(d){
                return '<a lay-event="details" class="notice-title-click">' + d.title + '</a>';
            }},
            { field: 'nameCn', title: '类型', align: 'left', width: 100},
            { field: 'state', title: '状态', align: 'center', width: 60, templet: function(d){
                if(d.state == '2'){
                    return "<span class='state-down'>禁用</span>";
                }else if(d.state == '1'){
                    return "<span class='state-up'>启用</span>";
                }
            }},
            { field: 'startMonth', title: '开始月份', align: 'center', width: 80},
            { field: 'endMonth', title: '截止月份', align: 'center', width: 80},
            { field: 'sortNo', title: '序号', align: 'left', width: 80},
            { field: 'createName', title: '创建人', align: 'left', width: 80 },
            { field: 'createTime', title: '创建时间', align: 'center', width: 100 },
            { field: 'lastUpdateName', title: '最后修改人', align: 'left', width: 100 },
            { field: 'lastUpdateTime', title: '最后修改时间', align: 'center', width: 100},
            { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 200, toolbar: '#tableBar'}
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
        }else if (layEvent === 'up') { // 启用
            up(data);
        }else if (layEvent === 'down') { //禁用
            down(data);
        }else if (layEvent === 'details') { //详情
            details(data);
        }
    });

    form.render();
    form.on('submit(formSearch)', function (data) {
        if (winui.verifyForm(data.elem)) {
            refreshloadTable();
        }
        return false;
    });

    // 添加
    $("body").on("click", "#addBean", function(){
        _openNewWindows({
            url: "../../tpl/wagesModel/wagesModelAdd.html",
            title: systemLanguage["com.skyeye.addPageTitle"][languageType],
            pageId: "wagesModelAdd",
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

    // 删除
    function delet(data){
        layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
            layer.close(index);
            AjaxPostUtil.request({url:reqBasePath + "wagesmodel005", params:{rowId: data.id}, type:'json', method: "DELETE", callback:function(json){
                if(json.returnCode == 0){
                    winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1,time: 2000});
                    loadTable();
                }else{
                    winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
                }
            }});
        });
    }

    // 禁用
    function down(data){
        layer.confirm(systemLanguage["com.skyeye.disableOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.disableOperation"][languageType]}, function(index) {
            layer.close(index);
            AjaxPostUtil.request({url:reqBasePath + "wagesmodel007", params:{rowId: data.id}, type:'json', method: "GET", callback:function(json){
                if(json.returnCode == 0){
                    winui.window.msg(systemLanguage["com.skyeye.disableOperationSuccessMsg"][languageType], {icon: 1,time: 2000});
                    loadTable();
                }else{
                    winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
                }
            }});
        });
    }

    // 启用
    function up(data){
        layer.confirm(systemLanguage["com.skyeye.enableOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.enableOperation"][languageType]}, function(index) {
            layer.close(index);
            AjaxPostUtil.request({url:reqBasePath + "wagesmodel006", params:{rowId: data.id}, type:'json', method: "GET", callback:function(json){
                if(json.returnCode == 0){
                    winui.window.msg(systemLanguage["com.skyeye.enableOperationSuccessMsg"][languageType], {icon: 1,time: 2000});
                    loadTable();
                }else{
                    winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
                }
            }});
        });
    }

    // 编辑
    function edit(data){
        rowId = data.id;
        _openNewWindows({
            url: "../../tpl/wagesModel/wagesModelEdit.html",
            title: systemLanguage["com.skyeye.editPageTitle"][languageType],
            pageId: "wagesModelEdit",
            area: ['90vw', '90vh'],
            callBack: function(refreshCode){
                if (refreshCode == '0') {
                    winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1,time: 2000});
                    loadTable();
                } else if (refreshCode == '-9999') {
                    winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
                }
            }
        });
    }

    // 详情
    function details(data){
        rowId = data.id;
        _openNewWindows({
            url: "../../tpl/wagesModel/wagesModelDetail.html",
            title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
            pageId: "wagesModelDetail",
            area: ['90vw', '90vh'],
            callBack: function(refreshCode){
            }
        });
    }

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
            title: $("#title").val(),
            typeId: $("#typeId").val(),
            state: $("#state").val()
        };
    }

    exports('wagesModelList', {});
});
