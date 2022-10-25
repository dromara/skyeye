var rowId = "";

layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form'], function (exports) {
    winui.renderColor();
    authBtn('1568523956068');
    var $ = layui.$,
        form = layui.form,
        table = layui.table;
    
    table.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        url: flowableBasePath + 'storehouse001',
        where: getTableParams(),
        even: true,
        page: true,
        limits: getLimits(),
        limit: getLimit(),
        cols: [[
            { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
            { field: 'houseName', title: '仓库名称', align: 'left',width: 200, templet: function(d) {
                return '<a lay-event="select" class="notice-title-click">' + d.houseName + '</a>';
            }},
            { field: 'address', title: '仓库地址', align: 'left',width: 300 },
            { field: 'warehousing', title: '仓储费', align: 'left',width: 100 },
            { field: 'truckage', title: '搬运费', align: 'left',width: 100 },
            { field: 'isDefault', title: '默认仓库', align: 'center',width: 80, templet: function(d) {
                if (d.isDefault == '1') {
                    return "<span class='state-up'>是</span>";
                } else if (d.isDefault == '2') {
                    return "<span class='state-down'>否</span>";
                } else {
                    return "<span class='state-error'>参数错误</span>";
                }
            }},
            { field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], width: 120 },
            { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 },
            { field: 'lastUpdateName', title: systemLanguage["com.skyeye.lastUpdateName"][languageType], align: 'left', width: 120 },
            { field: 'lastUpdateTime', title: systemLanguage["com.skyeye.lastUpdateTime"][languageType], align: 'center', width: 150 },
            { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 200, toolbar: '#tableBar'}
        ]],
	    done: function(json) {
	    	matchingLanguage();
            initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "请输入仓库名称", function () {
                table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
            });
	    }
    });

    table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'edit') { // 编辑
            edit(data);
        } else if (layEvent === 'delete') { // 删除
            deleteHouse(data);
        } else if (layEvent === 'select') {
            selectHouse(data);
        }
    });

    // 编辑
    function edit(data) {
        rowId = data.id;
        _openNewWindows({
            url: "../../tpl/storeHouse/storeHouseEdit.html",
            title: systemLanguage["com.skyeye.editPageTitle"][languageType],
            pageId: "storeHouseEdit",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
    }

    // 删除仓库
    function deleteHouse(data) {
        layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index) {
            layer.close(index);
            var params = {
                id: data.id
            };
            AjaxPostUtil.request({url: flowableBasePath + "storehouse004", params: params, type: 'json', method: "DELETE", callback: function(json) {
                winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
        });
    }

    function selectHouse(data) {
        rowId = data.id;
        _openNewWindows({
            url: "../../tpl/storeHouse/storeHouseInfo.html",
            title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
            pageId: "storeHouseInfo",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }
        });
    }
    // 添加仓库
    $("body").on("click", "#addBean", function() {
        _openNewWindows({
            url: "../../tpl/storeHouse/storeHouseAdd.html",
            title: systemLanguage["com.skyeye.addPageTitle"][languageType],
            pageId: "storeHouseAdd",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
    });

    form.render();

    $("body").on("click", "#reloadTable", function() {
        loadTable();
    });

    function loadTable() {
        table.reloadData("messageTable", {where: getTableParams()});
    }

    function getTableParams() {
        return $.extend(true, {}, initTableSearchUtil.getSearchValue("messageTable"));
    }

    exports('storeHouseList', {});
});
