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
    var materialId = GetUrlParam("materialId");
    var normsId = GetUrlParam("normsId");
    var farmId = GetUrlParam("farmId");
    //车间选择商品当前库存信息
    initTable()

    function initTable() {
        table.render({
            id: 'messageTable',
            elem: '#messageTable',
            method: 'post',
            url: sysMainMation.erpBasePath + 'queryNormsStockDetailList',
            where: getTableParams(),
            even: true,
            page: true,
            limits: getLimits(),
            limit: getLimit(),
            cols: [[
                {title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
                {
                    field: 'materialMation', title: '产品', align: 'left', width: 120, templet: function (d) {
                        return isNull(d.materialMation) ? '' : d.materialMation.name
                    }
                },
                {
                    field: 'normsMation', title: '规格', align: 'left', width: 200, templet: function (d) {
                        return isNull(d.normsMation) ? '' : d.normsMation.name
                    }
                },
                {field: 'codeNum', title: '条形码', align: 'center', width: 250},
                {
                    field: 'departmentMation', title: '来源部门', align: 'left', width: 200, templet: function (d) {
                        return isNull(d.departmentMation) ? '' : d.departmentMation.name
                    }
                },
                {
                    field: 'depotMation', title: '来源仓库', align: 'left', width: 200, templet: function (d) {
                        return isNull(d.depotMation) ? '' : d.depotMation.name
                    }
                }
            ]],
            done: function (json) {
                matchingLanguage();
                initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "暂不支持搜索", function () {
                    table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
                });
            }
        });
    }

    form.render();
    $("body").on("click", "#reloadTable", function () {
        loadTable();
    });

    function loadTable() {
        table.reloadData("messageTable", {where: getTableParams()});
    }

    function getTableParams() {
        var params = {
            materialId: materialId,
            normsId: normsId,
            farmId: farmId
        }
        return $.extend(true, params, initTableSearchUtil.getSearchValue("messageTable"));
    }

    exports('productInventoryDetails', {});
});
