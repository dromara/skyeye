
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

    authBtn('1641693529159');

    dsFormUtil.loadDsFormPageTypeByPId("firstTypeId", "0");
    form.on('select(firstTypeId)', function(data) {
        var thisRowValue = data.value;
        dsFormUtil.loadDsFormPageTypeByPId("secondTypeId", isNull(thisRowValue) ? "-" : thisRowValue);
        form.render('select');
    });

    table.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        url: flowableBasePath + 'dsFormObjectRelation001',
        where: getTableParams(),
        even: true,
        page: true,
        limits: getLimits(),
        limit: getLimit(),
        cols: [[
            { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
            { field: 'title', title: '中文名称', align: 'left', width: 120, templet: function(d){
                return '<a lay-event="details" class="notice-title-click">' + d.title + '</a>';
            }},
            { field: 'titleEn', title: '英文名称', align: 'left', width: 120 },
            { field: 'code', title: '编码', align: 'left', width: 100 },
            { field: 'firstTypeName', title: '一级分类', align: 'left', width: 120 },
            { field: 'secondTypeName', title: '二级分类', align: 'left', width: 120 },
            { field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], width: 120 },
            { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 },
            { field: 'lastUpdateName', title: systemLanguage["com.skyeye.lastUpdateName"][languageType], align: 'left', width: 100 },
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
        if(layEvent === 'delet'){ // 删除
            delet(data);
        }else if(layEvent === 'edit'){ // 编辑
            edit(data);
        }else if (layEvent === 'details') { // 详情
            details(data);
        }
    });

    // 添加
    $("body").on("click", "#addBean", function() {
        _openNewWindows({
            url: "../../tpl/dsFormObjectRelation/dsFormObjectRelationAdd.html",
            title: systemLanguage["com.skyeye.addPageTitle"][languageType],
            pageId: "dsFormObjectRelationAdd",
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
            AjaxPostUtil.request({url: flowableBasePath + "dsFormObjectRelation003", params: {id: data.id}, type: 'json', method: "DELETE", callback: function (json) {
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
            url: "../../tpl/dsFormObjectRelation/dsFormObjectRelationEdit.html",
            title: systemLanguage["com.skyeye.editPageTitle"][languageType],
            pageId: "dsFormObjectRelationEdit",
            area: ['90vw', '90vh'],
            callBack: function(refreshCode){
                if (refreshCode == '0') {
                    winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                    loadTable();
                } else if (refreshCode == '-9999') {
                    winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2, time: 2000});
                }
            }});
    }

    // 详情
    function details(data){
        rowId = data.id;
        var url = erpOrderUtil.getErpDetailUrl(data);
        _openNewWindows({
            url: "../../tpl/dsFormObjectRelation/dsFormObjectRelationDetails.html",
            title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
            pageId: "dsFormObjectRelationDetails",
            area: ['90vw', '90vh'],
            callBack: function(refreshCode){
            }});
    }

    // 刷新数据
    $("body").on("click", "#reloadTable", function() {
        loadTable();
    });

    form.render();
    form.on('submit(formSearch)', function (data) {
        if (winui.verifyForm(data.elem)) {
            table.reload("messageTable", {page: {curr: 1}, where: getTableParams()});
        }
        return false;
    });

    function loadTable(){
        table.reload("messageTable", {where: getTableParams()});
    }

    function getTableParams(){
        return {
            title: $("#title").val(),
            encoded: $("#encoded").val(),
            firstTypeId: $("#firstTypeId").val(),
            secondTypeId: $("#secondTypeId").val()
        };
    }

    exports('dsFormObjectRelationList', {});
});
