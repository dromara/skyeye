
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

    // 加载列表数据权限
    loadAuthBtnGroup('messageTable', '1722244335751');

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

    //门店待确认物料
    function initTable() {
        table.render({
            id: 'messageTable',
            elem: '#messageTable',
            method: 'post',
            url: sysMainMation.erpBasePath + 'queryNeedStoreConfirmDepotOutList',
            where: getTableParams(),
            even: true,
            page: true,
            limits: getLimits(),
            limit: getLimit(),
            cols: [[
                { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers', rowspan: '2' },
                { field: 'oddNumber', title: '单号', rowspan: '2', width: 200, align: 'center', templet: function (d) {
                        var str = '<a lay-event="details" class="notice-title-click">' + d.oddNumber + '</a>';
                        if (!isNull(d.fromId)) {
                            str += '<span class="state-new">[转]</span>';
                        }
                        return str;
                    }},
                { field: 'operTime', title: '单据日期', rowspan: '2', align: 'center', width: 140 },
                { colspan: '2', title: '来源单据信息', align: 'center' },
                { field: 'salesmanMation', title: '业务员', rowspan: '2', align: 'left', width: 200, templet: function (d) {
                        return getNotUndefinedVal(d.salesmanMation?.name);
                    }},
                { field: 'totalPrice', title: '总金额', rowspan: '2', align: 'center', width: 140 },
                { field: 'processInstanceId', title: '流程ID', rowspan: '2', width: 100, templet: function (d) {
                        return '<a lay-event="processDetails" class="notice-title-click">' + getNotUndefinedVal(d.processInstanceId) + '</a>';
                    }},
                { field: 'state', title: '状态', rowspan: '2', width: 90, templet: function (d) {
                        return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("flowableStateEnum", 'id', d.state, 'name');
                    }},
                { field: 'otherState', title: '确认状态', rowspan: '2', width: 90, templet: function (d) {
                        return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("depotOutOtherState", 'id', d.otherState, 'name');
                    }},
                { field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], rowspan: '2', width: 120 },
                { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], rowspan: '2', align: 'center', width: 150 },
                { field: 'lastUpdateName', title: systemLanguage["com.skyeye.lastUpdateName"][languageType], rowspan: '2', align: 'left', width: 120 },
                { field: 'lastUpdateTime', title: systemLanguage["com.skyeye.lastUpdateTime"][languageType], rowspan: '2', align: 'center', width: 150 },
                { title: systemLanguage["com.skyeye.operation"][languageType], rowspan: '2', fixed: 'right', align: 'center', width: 200, toolbar: '#tableBar'}
            ], [
                { field: 'fromTypeId', title: '来源类型', width: 150, templet: function (d) {
                        return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("depotOutFromType", 'id', d.fromTypeId, 'name');
                    }},
                { field: 'fromId', title: '单据编号', width: 200, templet: function (d) {
                        return getNotUndefinedVal(d.fromMation?.oddNumber);
                    }}
            ]],
            done: function(json) {
                matchingLanguage();
                soulTable.render(this);
                initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "请输入单据编号", function () {
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
        if (layEvent === 'materialReceipt') { // 门店物料接收
            materialReceipt(data);
        } else if (layEvent === 'materialReturn'){ // 门店物料退货
            materialReturn(data);
        }else if (layEvent === 'details') { //详情
            details(data);
        } else if (layEvent === 'processDetails') { // 工作流流程详情查看
            activitiUtil.activitiDetails(data);
        }
    });

    // 门店物料接收
    function materialReceipt(data) {
        _openNewWindows({
            url: "../../tpl/storeMaterialsReceipt/storeMaterialsToReceipt.html?id=" + data.id,
            title: "门店物料接收",
            pageId: "storeMaterialsToReceipt",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
    }


    // 门店物料退货
    function materialReturn(data) {
        _openNewWindows({
            url: "../../tpl/storeMaterialsReturn/storeMaterialsToReturn.html?id=" + data.id,
            title: "门店物料退货",
            pageId: "storeMaterialsToReturn",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
    }

    // 详情
    function details(data) {
        _openNewWindows({
            url:  systemCommonUtil.getUrl('FP2024070100007&id=' + data.id, null),
            title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
            pageId: "materialsAwaitingConfirmationDetails",
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
        let params = {
            objectId: storeId,
        };
        return $.extend(true, params, initTableSearchUtil.getSearchValue("messageTable"));
    }

    exports('storeMaterialsAwaitConfirmationList', {});
});