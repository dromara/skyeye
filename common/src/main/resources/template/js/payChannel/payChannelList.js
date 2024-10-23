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
        table = layui.table,
        soulTable = layui.soulTable;

    var selTemplate = getFileContent('tpl/template/select-option-must.tpl');

    var appId = "";
    appId = GetUrlParam("appId");
    //加载
    let appHtml = '';
    AjaxPostUtil.request({url: sysMainMation.reqBasePath + "queryAllEnabledPayAppList", params: {}, type: 'json', method: "Post", callback: function (json) {
        appId = json.rows.length > 0 ? json.rows[0].id : '-';
        appHtml = getDataUseHandlebars(selTemplate, json);
        initTable();
    }});

    form.on('select(appId)', function (data) {
        var thisRowValue = data.value;
        appId = isNull(thisRowValue) ? "" : thisRowValue;
        loadTable();
    });
    function initTable() {
        table.render({
            id: 'messageTable',
            elem: '#messageTable',
            method: 'post',
            url: reqBasePath + 'queryPayChannelList',
            where: getTableParams(),
            even: true,
            page: true,
            limits: getLimits(),
            limit: getLimit(),
            cols: [[
                { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
                { field: 'codeNum', title: '渠道编码', align: 'center', width: 80, templet: function (d) {
                        return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("payType", 'id', d.codeNum, 'name');
                    }},
                { field: 'enabled', title: '状态', align: 'center', width: 100, templet: function (d) {
                        return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("commonEnable", 'id', d.enabled, 'name');
                    }},
                { field: 'feeRate', title: '渠道费率', align: 'center', width: 120 },
                { field: 'remark', title: '备注', align: 'center', width: 80, templet: function (d) {
                        return  d.remark;
                    }},
                { field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], width: 120 },
                { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 },
                { field: 'lastUpdateName', title: systemLanguage["com.skyeye.lastUpdateName"][languageType], align: 'left', width: 120 },
                { field: 'lastUpdateTime', title: systemLanguage["com.skyeye.lastUpdateTime"][languageType], align: 'center', width: 150 },
                { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 200, toolbar: '#tableBar' }
            ]],
            done: function(json) {
                matchingLanguage();
                initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "暂不支持搜索功能", function () {
                    table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
                }, `<label class="layui-form-label">应用</label><div class="layui-input-inline">
                        <select id="appId" name="appId" lay-filter="appId" lay-search="">
                        ${appHtml}
                    </select></div>`);
            }
        });
        table.on('tool(messageTable)', function (obj) {
            var data = obj.data;
            var layEvent = obj.event;
            if (layEvent === 'delet') { // 删除
                delet(data);
            } else if (layEvent === 'edit') { // 编辑
                edit(data);
            }
        });
    }

    // 添加
    $("body").on("click", "#addBean", function() {
        _openNewWindows({
            url: "../../tpl/payChannel/writePayChannel.html?appId=" + appId,
            title: systemLanguage["com.skyeye.recordPageTitle"][languageType],
            pageId: "payChannelAdd",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
    });

    // 编辑
    function edit(data) {
        rowId = data.id;
        _openNewWindows({
            url: "../../tpl/payChannel/writePayChannel.html?id=" + data.id,
            title: systemLanguage["com.skyeye.recordPageTitle"][languageType],
            pageId: "payChannelEdit",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
    }

    // 删除
    function delet(data) {
        layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function (index) {
            parent.layer.close(index);
            AjaxPostUtil.request({url: reqBasePath + "deletePayChannelById", params: {id: data.id}, type: 'json', method: "DELETE", callback: function (json) {
                    winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
                    loadTable();
                }});
        });
    }

    form.render();
    $("body").on("click", "#reloadTable", function() {
        loadTable();
    });

    function loadTable() {
        table.reloadData("messageTable", {where: getTableParams()});
    }

    function getTableParams() {
        let params = {
            appId: appId,
        };
        return $.extend(true, params, initTableSearchUtil.getSearchValue("messageTable"));
    }

    exports('payChannelList', {});
});
