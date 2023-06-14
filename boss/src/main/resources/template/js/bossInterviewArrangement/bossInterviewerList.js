
// 获取我录入的人员需求关联的面试者信息列表
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
        url: sysMainMation.bossBasePath + 'queryMyEntryBossPersonRequireAboutArrangementList',
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
            { field: 'interviewMation', title: '面试者', width: 100, templet: function (d) {
                return d.interviewMation.name;
            }},
            { field: 'recruitDepartmentMation', title: '面试部门', width: 140, templet: function (d) {
                return isNull(d.personRequireMation) ? '' : d.personRequireMation.recruitDepartmentMation.name;
            }},
            { field: 'recruitJobMation', title: '面试岗位', width: 150, templet: function (d) {
                return isNull(d.personRequireMation) ? '' : d.personRequireMation.recruitJobMation.name;
            }},
            { field: 'interviewTime', title: '面试时间', width: 140, align: 'center' },
            { field: 'interviewer', title: '面试官', width: 120, templet: function (d) {
                return isNull(d.interviewerMation) ? '' : d.interviewerMation.name;
            }},
            { field: 'state', title: '面试状态', width: 160, templet: function (d) {
                return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("bossInterviewArrangementState", 'id', d.state, 'name');
            }},
            { field: 'createTime', title: systemLanguage["com.skyeye.entryTime"][languageType], align: 'center', width: 150 },
            { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 120, toolbar: '#messageTableBar'}
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
        } else if (layEvent === 'arrangeInterviewer') { // 安排面试官
            arrangeInterviewer(data);
        }
    });

    // 安排面试官
    function arrangeInterviewer(data) {
        _openNewWindows({
            url: systemCommonUtil.getUrl('FP2023061300001&id=' + data.id, null),
            title: '安排面试官',
            pageId: "arrangeInterviewer",
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
            url: systemCommonUtil.getUrl('FP2023060400004&id=' + data.id, null),
            title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
            pageId: "bossInterviewArrangementDetails",
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

    exports('bossInterviewerList', {});
});
