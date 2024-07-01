
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

    // 单据类型枚举类
    let idKeyMap = {};
    let idKeyToIdMap = {};
    let enumResult = skyeyeClassEnumUtil.getEnumDataListByClassName("depotPutFromType");
    $.each(enumResult.rows, function (i, item) {
        idKeyMap[item.idKey] = item.name
        idKeyToIdMap[item.idKey] = item.id
    });

    // 加载列表数据权限
    loadAuthBtnGroup('messageTable', '1719831228984');
    initTable();
    function initTable() {
        table.render({
            id: 'messageTable',
            elem: '#messageTable',
            method: 'post',
            url: sysMainMation.erpBasePath + 'queryDepotPutList',
            where: getTableParams(),
            even: true,
            page: true,
            limits: getLimits(),
            limit: getLimit(),
            cols: [[
                { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
                { field: 'oddNumber', title: '单据单号', align: 'left', width: 220, templet: function (d) {
                    var str = '<a lay-event="details" class="notice-title-click">' + d.oddNumber + '</a>';
                    if (!isNull(d.fromId)) {
                        str += '<span class="state-new">[转]</span>';
                    }
                    return str;
                }},
                { field: 'idKey', title: '单据类型', width: 120, templet: function (d) {
                    return idKeyMap[d.idKey];
                }},
                { field: 'createName', title: '申请人', width: 120 },
                { field: 'createTime', title: '申请时间', align: 'center', width: 150 },
                { field: 'totalPrice', title: '合计金额', align: 'left', width: 120 },
                { field: 'operTime', title: '单据日期', align: 'center', width: 140 },
                { field: 'otherState', title: '入库状态', width: 90, templet: function (d) {
                    return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("depotPutState", 'id', d.otherState, 'name');
                }},
                { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 140, toolbar: '#tableBar'}
            ]],
            done: function(json) {
                matchingLanguage();
                initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "请输入单号", function () {
                    table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
                });
            }
        });

        table.on('tool(messageTable)', function (obj) {
            var data = obj.data;
            var layEvent = obj.event;
            if (layEvent === 'details') { //详情
                details(data);
            } else if (layEvent === 'depotPut') { //入库
                depotPut(data);
            }
        });
    }

    // 详情
    function details(data) {
        let pageUrl = '';
        let type = idKeyToIdMap[data.idKey];
        if (type == 1) {
            // 采购入库单
            pageUrl = 'FP2023042300003';
        } else if (type == 2) {
            // 销售退货单
            pageUrl = 'FP2023042700007';
        } else if (type == 3) {
            // 零售退货单
            pageUrl = 'FP2023042400011';
        } else if (type == 4) {
            // 其他入库单
            pageUrl = 'FP2023042700015';
        } else if (type == 5) {
            // 退料入库单
            pageUrl = 'FP2024070100003';
        } else if (type == 6) {
            // 物料退货单
            pageUrl = 'FP2024070100004';
        }
        _openNewWindows({
            url:  systemCommonUtil.getUrl(pageUrl + '&id=' + data.id, null),
            title: idKeyMap[data.idKey] + systemLanguage["com.skyeye.detailsPageTitle"][languageType],
            pageId: "putOrderDetails",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
            }});
    }

    // 入库
    function depotPut(data) {
        let pageUrl = '';
        let type = idKeyToIdMap[data.idKey];
        if (type == 1) {
            // 采购入库单
            pageUrl = '';
        } else if (type == 2) {
            // 销售退货单
            pageUrl = '';
        } else if (type == 3) {
            // 零售退货单
            pageUrl = '';
        } else if (type == 4) {
            // 其他入库单
            pageUrl = '';
        } else if (type == 5) {
            // 退料入库单
            pageUrl = '';
        } else if (type == 6) {
            // 物料退货单
            pageUrl = '';
        }
        _openNewWindows({
            url:  pageUrl + '?id=' + data.id,
            title: '转仓库入库单',
            pageId: "transPutOrder",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
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
        return $.extend(true, {}, initTableSearchUtil.getSearchValue("messageTable"));
    }

    exports('depotPutTransList', {});
});
