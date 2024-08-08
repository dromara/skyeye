
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
    var selTemplate = getFileContent('tpl/template/select-option.tpl');

    // 加载当前用户所属车间
    AjaxPostUtil.request({url: sysMainMation.erpBasePath + "queryStaffBelongFarmList", params: {}, type: 'json', method: "GET", callback: function(json) {
        $("#workshopId").html(getDataUseHandlebars(selTemplate, json));
        form.render('select');
        initTable();
    }, async: false});

    var workshopId = "";
    form.on('select(workshopId)', function(data) {
        var thisRowValue = data.value;
        workshopId = isNull(thisRowValue) ? "" : thisRowValue;
        loadTable();
    });

    // 车间任务
    function initTable() {
        table.render({
            id: 'messageTable',
            elem: '#messageTable',
            method: 'post',
            url: sysMainMation.erpBasePath + 'queryMachinProcedureFarmList',
            where: getTableParams(),
            even: true,
            page: true,
            limits: getLimits(),
            limit: getLimit(),
            cols: [[
                { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
                { field: 'id', title: '车间任务ID',width: 280, templet: function (d) {
                        return '<a lay-event="details" class="notice-title-click">' + getNotUndefinedVal(d.id) + '</a>';
                    }},
                { field: 'state', title: '状态',  width: 90, templet: function (d) {
                        return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("machinProcedureFarmState", 'id', d.state, 'name');
                    }},
                { field: 'name', title: '部门', align: 'center', width: 90, templet: function (d) {
                        return getNotUndefinedVal(d.farmMation?.departmentMation?.name);
                    }},
                // { field: 'oddNumber', title: '加工单单号', rowspan: '2', align: 'left', width: 200, templet: function (d) {
                //         var str = '<a lay-event="details" class="notice-title-click">' + d.machinMation?.oddNumber + '</a>';
                //         if (!isNull(d.fromId)) {
                //             str += '<span class="state-new">[转]</span>';
                //         }
                //         return str;
                //     }},
                { field: 'targetNum', title: '任务安排数量', align: 'center', width: 140 },
                { field: 'planStartTime', title: '计划开始时间', align: 'center', width: 140, templet: function (d) {
                        return getNotUndefinedVal(d.machinProcedureMation?.planStartTime);
                    }},
                { field: 'planEndTime', title: '计划结束时间', align: 'center', width: 140, templet: function (d) {
                        return getNotUndefinedVal(d.machinProcedureMation?.planEndTime);
                    }},
                { field: 'actualStartTime', title: '实际开始时间', align: 'center', width: 140, templet: function (d) {
                        return getNotUndefinedVal(d.machinProcedureMation?.actualStartTime);
                    }},
                { field: 'actualEndTime', title: '实际结束时间', align: 'center', width: 140, templet: function (d) {
                        return getNotUndefinedVal(d.machinProcedureMation?.actualEndTime);
                    }},
                { field: 'createTime', title: '创建时间', align: 'center', width: 140, templet: function (d) {
                        return getNotUndefinedVal(d.machinMation?.createTime);
                    }},

                {title: systemLanguage["com.skyeye.operation"][languageType], rowspan: '2', fixed: 'right', align: 'center', width: 200, toolbar: '#tableBar' }
            ]],
            done: function (json) {
                matchingLanguage();
                initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "暂不支持搜索", function () {
                    table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
                });
            }
        });
    }

    table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'receive') { //接收
            receive(data);
        } else if (layEvent === 'antiReception') { //反接收
            antiReception(data);
        }else if (layEvent === 'details') { //详情
            details(data);
        }else if (layEvent === 'processCheck') { //转工序验收
            processCheck(data);
        }else if (layEvent === 'transferDepotPut') { //转加工入库单
            transferDepotPut(data);
        }
    });

    // 转加工入库
    function transferDepotPut(data) {
        _openNewWindows({
            url: "../../tpl/machiningWarehouse/transferDepotPut.html?id=" + data.id,
            title: "转加工入库",
            pageId: "transferDepotPut",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
    }

    // 接收
    function receive(data) {
        layer.confirm('确认要接收该车间任务吗？', { icon: 3, title: '接收任务操作' }, function (index) {
                var params = {
                    id: data.id,
                };
                AjaxPostUtil.request({url: sysMainMation.erpBasePath + "receiveMachinProcedureFarm", params: params, type: 'json', method: 'POST', callback: function (json) {
                    winui.window.msg("接收成功", {icon: 1, time: 2000});
                    loadTable();
                }});
        });
    }

    function antiReception(data) {
        layer.confirm('确认要反接收该车间任务吗？', { icon: 3, title: '反接收任务操作' }, function (index) {
                var params = {
                    id: data.id,
                };
                AjaxPostUtil.request({url: sysMainMation.erpBasePath + "receptionReceiveMachinProcedureFarm", params: params, type: 'json', method: 'POST', callback: function (json) {
                    winui.window.msg("反接收成功", {icon: 1, time: 2000});
                    loadTable();
                }});
        });
    }

    // 车间任务 详情
    function details(data) {
        _openNewWindows({
            url:  systemCommonUtil.getUrl('FP2024072600001&id=' + data.id, null),
            title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
            pageId: "workTaskDetail",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
            }});
    }

    //工序验收
    function processCheck(data){
        _openNewWindows({
            url: "../../tpl/processAcceptance/machiningProcessCheck.html?id=" + data.id,
            title: '工序验收',
            pageId: "machiningProcessCheck",
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

    function getTableParams(){
        var params = {
            type: 'farm',
            objectId: workshopId
        }
        return $.extend(true, params, initTableSearchUtil.getSearchValue("messageTable"));
    }

    exports('workshopTasksList', {});
});
