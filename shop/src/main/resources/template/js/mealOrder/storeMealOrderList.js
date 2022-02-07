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
    authBtn('1644054570044');

    // 加载我所在的门店
    shopUtil.queryStaffBelongStoreList(function (json){
        $("#storeId").html(getDataUseHandlebars($("#selectTemplate").html(), json));
    });

    table.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        url: shopBasePath + 'meal001',
        where: getTableParams(),
        even: true,
        page: true,
        limits: getLimits(),
        limit: getLimit(),
        cols: [[
            { title: systemLanguage["com.skyeye.serialNumber"][languageType], fixed: 'left', type: 'numbers'},
            { field: 'title', title: '套餐名称', align: 'left', width: 200, fixed: 'left', templet: function(d){
                    return '<a lay-event="select" class="notice-title-click">' + d.title + '</a>';
                }},
            { field: 'logo', title: 'LOGO', align: 'center', width: 60, templet: function(d){
                    return '<img src="' + fileBasePath + d.logo + '" class="photo-img" lay-event="logo">';
                }},
            { field: 'mealNum', title: '可使用次数', width: 120 },
            { field: 'type', title: '套餐分类', width: 100, align: "center", templet: function(d){
                    if(d.type == 1){
                        return "保养套餐";
                    }
                    return "-";
                }},
            { field: 'state', title: '状态', width: 80, align: "center", templet: function(d){
                    if(d.state == 1){
                        return "下线";
                    }else{
                        return "上线";
                    }
                }},
            { field: 'price', title: '价格', width: 100 },
            { field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], width: 120 },
            { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 },
            { field: 'lastUpdateName', title: systemLanguage["com.skyeye.lastUpdateName"][languageType], align: 'left', width: 120 },
            { field: 'lastUpdateTime', title: systemLanguage["com.skyeye.lastUpdateTime"][languageType], align: 'center', width: 150},
            { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 150, toolbar: '#tableBar'}
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
        }else if (layEvent === 'delete') { // 删除
            delet(data);
        }else if(layEvent == 'select'){ // 详情
            select(data)
        }else if (layEvent === 'logo') { // logo预览
            systemCommonUtil.showPicImg(fileBasePath + data.logo);
        }
    });

    // 编辑
    function edit(data){
        rowId = data.id;
        _openNewWindows({
            url: "../../tpl/meal/mealEdit.html",
            title: systemLanguage["com.skyeye.editPageTitle"][languageType],
            pageId: "mealEdit",
            area: ['90vw', '90vh'],
            callBack: function(refreshCode){
                if (refreshCode == '0') {
                    winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1,time: 2000});
                    loadTable();
                } else if (refreshCode == '-9999') {
                    winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
                }
            }});
    }

    // 删除
    function delet(data){
        layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
            layer.close(index);
            AjaxPostUtil.request({url: shopBasePath + "meal004", params: {rowId: data.id}, type: 'json', method: "DELETE", callback: function(json){
                    if(json.returnCode == 0){
                        winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
                        loadTable();
                    }else{
                        winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
                    }
                }});
        });
    }

    // 详情
    function select(data){
        rowId = data.id;
        _openNewWindows({
            url: "../../tpl/meal/mealInfo.html",
            title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
            pageId: "mealInfo",
            area: ['90vw', '90vh'],
            callBack: function(refreshCode){
            }
        });
    }

    // 添加
    $("body").on("click", "#addBean", function(){
        _openNewWindows({
            url: "../../tpl/meal/mealAdd.html",
            title: systemLanguage["com.skyeye.addPageTitle"][languageType],
            pageId: "mealAdd",
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

    form.render();
    form.on('submit(formSearch)', function (data) {
        if (winui.verifyForm(data.elem)) {
            table.reload("messageTable", {page: {curr: 1}, where: getTableParams()})
        }
        return false;
    });

    $("body").on("click", "#reloadTable", function() {
        loadTable();
    });

    // 刷新
    function loadTable(){
        table.reload("messageTable", {where: getTableParams()});
    }

    function getTableParams(){
        return {
            orderNum: $("#orderNum").val(),
            memberName: $("#memberName").val(),
            memberPhone: $("#memberPhone").val(),
            memberPlate: $("#memberPlate").val()
        };
    }

    exports('storeMealOrderList', {});
});
