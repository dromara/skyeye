
// 获取所有审批通过状态之后的人员需求申请列表
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

    table.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        url: sysMainMation.bossBasePath + 'queryAllBossPersonRequireList',
        where: getTableParams(),
        even: true,
        page: true,
        limits: getLimits(),
        limit: getLimit(),
        cols: [[
            { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
            { field: 'oddNumber', title: '单据编号', align: 'left', width: 200, templet: function (d) {
                return '<a lay-event="details" class="notice-title-click">' + d.oddNumber + '</a>';
            }},
            { field: 'createName', title: '申请人', width: 120},
            { field: 'recruitDepartmentMation', title: '需求部门', width: 140, templet: function (d) {
                return d.recruitDepartmentMation?.name;
            }},
            { field: 'recruitJobMation', title: '需求岗位', width: 150, templet: function (d) {
                return d.recruitJobMation?.name;
            }},
            { field: 'wages', title: '薪资范围', width: 120 },
            { field: 'recruitNum', title: '需求人数', width: 100 },
            { field: 'processInstanceId', title: '流程ID', width: 100, templet: function (d) {
                return '<a lay-event="processDetails" class="notice-title-click">' + d.processInstanceId + '</a>';
            }},
            { field: 'state', title: '状态', align: 'left', width: 80, templet: function (d) {
                return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("bossPersonRequireState", 'id', d.state, 'name');
            }},
            { field: 'createTime', title: systemLanguage["com.skyeye.entryTime"][languageType], width: 150 },
            { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 257, toolbar: '#messageTableBar'}
        ]],
        done: function(json) {
            matchingLanguage();
            initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "请输入单据编号", function () {
                table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
            });
        }
    });

    table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'details') { // 详情
            details(data);
        } else if (layEvent === 'processDetails') { // 流程详情
            activitiUtil.activitiDetails(data);
        } else if (layEvent === 'setPersonLiable') { // 设置责任人
            setPersonLiable(data);
        }
    });

    // 设置责任人
    function setPersonLiable(data) {
        _openNewWindows({
            url: systemCommonUtil.getUrl('FP2023052800001&id=' + data.id, null),
            title: '设置责任人',
            pageId: "bossPersonRequirePersonLiable",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }
        });
    }

    // 详情
    function details(data) {
        _openNewWindows({
            url: systemCommonUtil.getUrl('FP2023052100003&id=' + data.id, null),
            title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
            pageId: "bossPersonRequireDetails",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
            }
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
        return $.extend(true, {}, initTableSearchUtil.getSearchValue("messageTable"));
    }

    exports('bossPersonRequireAllList', {});
});
