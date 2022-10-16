
var rowId = "";

layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form', 'soulTable'], function (exports) {
    winui.renderColor();
    var $ = layui.$,
        form = layui.form,
        soulTable = layui.soulTable,
        table = layui.table;

    authBtn('1598779841062');// 新增

    table.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        url: flowableBasePath + 'erpfarm001',
        where: getTableParams(),
        even: true,
        page: true,
        overflow: {
            type: 'tips',
            hoverTime: 300, // 悬停时间，单位ms, 悬停 hoverTime 后才会显示，默认为 0
            minWidth: 150, // 最小宽度
            maxWidth: 500 // 最大宽度
        },
        limits: getLimits(),
        limit: getLimit(),
        cols: [[
            { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
            { field: 'farmNumber', title: '车间编号', align: 'left', width: 100, templet: function (d) {
                    return '<a lay-event="details" class="notice-title-click">' + d.farmNumber + '</a>';
                }},
            { field: 'farmName', title: '车间名称', align: 'left', width: 250},
            { field: 'state', title: '状态', align: 'left', width: 80, templet: function (d) {
                if(d.state == '1'){
                    return "<span class='state-up'>正常</span>";
                } else if (d.state == '2'){
                    return "<span class='state-down'>维修整改</span>";
                } else {
                    return "参数错误";
                }
            }},
            { field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], align: 'left', width: 120 },
            { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 100 },
            { field: 'lastUpdateName', title: systemLanguage["com.skyeye.lastUpdateName"][languageType], align: 'left', width: 120 },
            { field: 'lastUpdateTime', title: '最后修改时间', align: 'center', width: 100},
            { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 257, toolbar: '#tableBar'}
        ]],
        done: function(json) {
        	matchingLanguage();
            soulTable.render(this);
        }
    });

    table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'delete') { // 删除
            deletemember(data);
        } else if (layEvent === 'details') { // 详情
            details(data);
        } else if (layEvent === 'edit') { // 编辑
            edit(data);
        } else if (layEvent === 'norms') { // 正常
            norms(data);
        } else if (layEvent === 'rectification') { // 维修整改
            rectification(data);
        }
    });

    // 搜索表单
    form.render();
    form.on('submit(formSearch)', function (data) {
        if (winui.verifyForm(data.elem)) {
            table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()})
        }
        return false;
    });

    // 编辑
    function edit(data) {
        rowId = data.id;
        _openNewWindows({
            url: "../../tpl/erpFarm/erpFarmEdit.html",
            title: systemLanguage["com.skyeye.editPageTitle"][languageType],
            pageId: "erpFarmEdit",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
    }

    // 删除
    function deletemember(data) {
        layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function (index) {
            AjaxPostUtil.request({url: flowableBasePath + "erpfarm005", params: {rowId: data.id}, type: 'json', callback: function (json) {
                winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
        });
    }

    // 详情
    function details(data) {
        rowId = data.id;
        _openNewWindows({
            url: "../../tpl/erpFarm/erpFarmDetail.html",
            title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
            pageId: "erpFarmDetail",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
            }});
    }

    // 恢复正常
    function norms(data) {
        layer.confirm('确认要恢复正常吗？', { icon: 3, title: '恢复操作' }, function (index) {
            AjaxPostUtil.request({url: flowableBasePath + "erpfarm008", params: {rowId: data.id}, type: 'json', callback: function (json) {
                winui.window.msg("提交成功。", {icon: 1, time: 2000});
                loadTable();
            }});
        });
    }

    // 维修整改
    function rectification(data) {
        layer.confirm('确认要维修整改吗？', { icon: 3, title: '维修整改操作' }, function (index) {
            AjaxPostUtil.request({url: flowableBasePath + "erpfarm009", params: {rowId: data.id}, type: 'json', callback: function (json) {
                winui.window.msg("提交成功。", {icon: 1, time: 2000});
                loadTable();
            }});
        });
    }

    //添加
    $("body").on("click", "#addBean", function() {
        _openNewWindows({
            url: "../../tpl/erpFarm/erpFarmAdd.html",
            title: systemLanguage["com.skyeye.addPageTitle"][languageType],
            pageId: "erpFarmAdd",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
    });

    $("body").on("click", "#reloadTable", function() {
        loadTable();
    });

    //刷新
    function loadTable() {
        table.reloadData("messageTable", {where: getTableParams()});
    }

    function getTableParams(){
        return {
            farmNumber: $("#farmNumber").val(),
            farmName: $("#farmName").val(),
            state: $("#state").val()
        };
    }

    exports('erpFarmList', {});
});
