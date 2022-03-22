var rowId = "";

// 已选择的员工信息
var checkStaffList = [];
// 多选
var userStaffCheckType = true;

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
    authBtn('1643984717466');

    // 加载区域
    shopUtil.getShopAreaMation(function (json){
        $("#areaId").html(getDataUseHandlebars($("#selectTemplate").html(), json));
        loadStore("-");
    });

    form.on('select(areaId)', function(data) {
        var thisRowValue = data.value;
        thisRowValue = isNull(thisRowValue) ? "-" : thisRowValue;
        loadStore(thisRowValue);
    });

    matchingLanguage();
    form.render();
    var chooseStoreId = "";
    function loadStore(areaId){
        table.render({
            id: 'storeTable',
            elem: '#storeTable',
            method: 'get',
            url: shopBasePath + 'queryStoreList',
            where: {areaId: areaId, enabled: 1},
            even: false,
            page: false,
            limits: getLimits(),
            limit: getLimit(),
            cols: [[
                { title: systemLanguage["com.skyeye.serialNumber"][languageType], fixed: 'left', type: 'numbers'},
                { field: 'name', title: '门店', align: 'left', width: 150, templet: function(d){
                    return '<a lay-event="select" class="notice-title-click">' + d.name + '</a>';
                }}
            ]],
            done: function(){
                matchingLanguage();
            }
        });
        table.on('tool(storeTable)', function (obj) {
            var data = obj.data;
            var layEvent = obj.event;
            if(layEvent == 'select'){
                chooseStoreId = data.id;
                loadStaff(data.id);
            }
        });

        chooseStoreId = "";
        loadStaff("-");
    }

    function loadStaff(storeId){
        table.render({
            id: 'messageTable',
            elem: '#messageTable',
            method: 'post',
            url: shopBasePath + 'storeStaff001',
            where: {storeId: storeId},
            even: true,
            page: true,
            limits: getLimits(),
            limit: getLimit(),
            cols: [[
                { title: systemLanguage["com.skyeye.serialNumber"][languageType], fixed: 'left', type: 'numbers'},
                { field: 'jobNumber', title: '工号', align: 'left', width: 140 },
                { field: 'userName', title: '姓名', width: 120 },
                { field: 'companyName', title: '企业', width: 150 },
                { field: 'departmentName', title: '部门', width: 140 },
                { field: 'jobName', title: '职位', width: 140 },
                { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 150, toolbar: '#tableBar'}
            ]],
            done: function(){
                matchingLanguage();
            }
        });

        table.on('tool(messageTable)', function (obj) {
            var data = obj.data;
            var layEvent = obj.event;
            if (layEvent === 'delete') { // 删除
                delet(data);
            }
        });
    }

    // 删除
    function delet(data){
        layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
            layer.close(index);
            AjaxPostUtil.request({url: shopBasePath + "storeStaff002", params: {id: data.id}, type: 'json', method: "POST", callback: function(json){
                if(json.returnCode == 0){
                    winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
                    table.reload("messageTable", {page: {curr: 1}, where: {storeId: chooseStoreId}})
                }else{
                    winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
                }
            }});
        });
    }

    // 添加
    $("body").on("click", "#addBean", function(){
        if(isNull(chooseStoreId)){
            winui.window.msg('请先选择门店信息.', {icon: 2, time: 2000});
            return false;
        }
        checkStaffList = [];
        _openNewWindows({
            url: "../../tpl/syseveuserstaff/sysEveUserStaffChoose.html",
            title: "选择员工",
            pageId: "sysEveUserStaffChoose",
            area: ['90vw', '90vh'],
            callBack: function(refreshCode){
                if (refreshCode == '0') {
                    var list = new Array();
                    $.each(checkStaffList, function (i, item){
                        list.push(item.id);
                    });
                    var params = {
                        storeId: chooseStoreId,
                        staffId: JSON.stringify(list)
                    };
                    AjaxPostUtil.request({url: shopBasePath + "storeStaff003", params: params, type: 'json', method: "POST", callback: function(json){
                        if(json.returnCode == 0){
                            loadStaff(chooseStoreId);
                        }else{
                            winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
                        }
                    }});
                }
            }});
    });

    exports('storeStaffDistribute', {});
});
