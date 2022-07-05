
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

    authBtn('1611416947230');
    // 薪资字段列表
    table.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        url: reqBasePath + 'wages001',
        where: getTableParams(),
        even: true,
        page: true,
        limits: getLimits(),
        limit: getLimit(),
        cols: [[
            { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
            { field: 'nameCn', title: '名称（中文）', align: 'left', width: 150},
            { field: 'nameEn', title: '名称（英文）', align: 'left', width: 150 },
            { field: 'key', title: '字段key', align: 'left', width: 120 },
            { field: 'state', title: '状态', align: 'center', width: 60, templet: function (d) {
                if(d.state == '2'){
                    return "<span class='state-down'>禁用</span>";
                }else if(d.state == '1'){
                    return "<span class='state-up'>启用</span>";
                }
            }},
            { field: 'monthlyClearing', title: '月度清零', align: 'center', width: 80, templet: function (d) {
                if(d.monthlyClearing == '1'){
                    return "是";
                }else if(d.monthlyClearing == '2'){
                    return "否";
                }
            }},
            { field: 'wagesType', title: '字段类型', align: 'center', width: 80, templet: function (d) {
                if(d.wagesType == '1'){
                    return "<span class='state-up'>薪资增加</span>";
                }else if(d.wagesType == '2'){
                    return "<span class='state-down'>薪资减少</span>";
                }
            }},
            { field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], align: 'left', width: 120 },
            { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 100 },
            { field: 'lastUpdateName', title: systemLanguage["com.skyeye.lastUpdateName"][languageType], align: 'left', width: 100 },
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
        if (layEvent === 'edit') { //编辑
            edit(data);
        }else if (layEvent === 'delet') { //删除
            delet(data);
        }else if (layEvent === 'up') { //启用
            up(data);
        }else if (layEvent === 'down') { //禁用
            down(data);
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
            url: "../../tpl/wagesFieldType/wagesFieldTypeAdd.html",
            title: systemLanguage["com.skyeye.addPageTitle"][languageType],
            pageId: "wagesFieldTypeAdd",
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
            AjaxPostUtil.request({url: reqBasePath + "wages005", params:{rowId: data.id}, type: 'json', method: "DELETE", callback: function (json) {
                winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
        });
    }

    // 禁用
    function down(data){
        layer.confirm(systemLanguage["com.skyeye.disableOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.disableOperation"][languageType]}, function(index) {
            layer.close(index);
            AjaxPostUtil.request({url: reqBasePath + "wages007", params:{rowId: data.id}, type: 'json', method: "GET", callback: function (json) {
                winui.window.msg(systemLanguage["com.skyeye.disableOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
        });
    }

    // 启用
    function up(data){
        layer.confirm(systemLanguage["com.skyeye.enableOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.enableOperation"][languageType]}, function(index) {
            layer.close(index);
            AjaxPostUtil.request({url: reqBasePath + "wages006", params:{rowId: data.id}, type: 'json', method: "GET", callback: function (json) {
                winui.window.msg(systemLanguage["com.skyeye.enableOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
        });
    }

    // 编辑
    function edit(data){
        rowId = data.id;
        _openNewWindows({
            url: "../../tpl/wagesFieldType/wagesFieldTypeEdit.html",
            title: systemLanguage["com.skyeye.editPageTitle"][languageType],
            pageId: "wagesFieldTypeEdit",
            area: ['90vw', '90vh'],
            callBack: function(refreshCode){
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }
        });
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
            name: $("#name").val(),
            key: $("#key").val(),
            state: $("#state").val()
        };
    }

    exports('wagesFieldTypeList', {});
});
