
layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form', 'soulTable'], function (exports) {
    winui.renderColor();
    var $ = layui.$,
        form = layui.form,
        table = layui.table;
    soulTable = layui.soulTable;
    var selTemplate = getFileContent('tpl/template/select-option.tpl');
    //门店物料库存

    // 加载当前用户所属门店
    let storeHtml = '';
    AjaxPostUtil.request({url: sysMainMation.shopBasePath + "storeStaff005", params: {}, type: 'json', method: "GET", callback: function(json) {
            storeHtml = getDataUseHandlebars(selTemplate, json);
            initTable(storeHtml);
        }, async: false});

    var storeId = "";
    form.on('select(storeId)', function(data) {
        var thisRowValue = data.value;
        storeId = isNull(thisRowValue) ? "" : thisRowValue;
        loadTable();
    });

    function initTable() {
        table.render({
            id: 'messageTable',
            elem: '#messageTable',
            method: 'post',
            url: sysMainMation.erpBasePath + 'queryShopStockList',
            where: getTableParams(),
            even: true,
            page: true,
            limits: getLimits(),
            limit: getLimit(),
            cols: [[
                {title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
                {field: 'materialMation', title: '产品', align: 'left', width: 120, templet: function (d) {return isNull(d.materialMation) ? '' : d.materialMation.name}},
                {field: 'normsMation', title: '规格', align: 'left', width: 200, templet: function (d) {return isNull(d.normsMation) ? '' : d.normsMation.name}},
                {field: 'storeMation', title: '门店', align: 'left', width: 200, templet: function (d) {return isNull(d.storeMation) ? '' : d.storeMation.name}},
                {field: 'stock', title: '门店库存', align: 'center', width: 80,templet: function (d) {
                        var str = '<a lay-event="inventory" class="notice-title-click">' + d.stock + '</a>';
                        return str;}},]],
            done: function (json) {
                matchingLanguage();
                soulTable.render(this);
                initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "请输入产品名称", function () {
                    table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
                }, `<label class="layui-form-label">门店</label><div class="layui-input-inline">
						<select id="storeId" name="storeId" lay-filter="storeId" lay-search="">
						${storeHtml}
					</select></div>`);
            }
        });
    }

    table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'inventory') { //详情
            inventory(data);
        }
    });

    // 门店商品库存详情
    function inventory(data) {
        _openNewWindows({
            url: "../../tpl/storeInventory/storeInventoryDetails.html?materialId=" + data.materialId
                + "&normsId="+data.normsId + "&storeId=" + data.storeId,
            title: "门店库存详情",
            pageId: "storeInventoryDetails",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
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
            type:'store',
            holderId: storeId,
        };
        return $.extend(true, params, initTableSearchUtil.getSearchValue("messageTable"));
    }

    exports('storeInventoryList', {});
});
