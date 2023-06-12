var rowId = "";

// 面试安排
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

    // 新增
    authBtn('1649929352279');

    table.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        url: sysMainMation.bossBasePath + 'queryMyEntryBossInterviewArrangementList',
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
            { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 140, toolbar: '#messageTableBar'}
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
        } else if (layEvent === 'edit') { // 编辑
            edit(data);
        } else if (layEvent === 'cancellation') { // 作废
            cancellation(data);
        } else if (layEvent === 'inductionResult') { // 入职
            inductionResult(data);
        }
    });

    // 添加
    $("body").on("click", "#addBean", function() {
        _openNewWindows({
            url: systemCommonUtil.getUrl('FP2023060400002', null),
            title: systemLanguage["com.skyeye.addPageTitle"][languageType],
            pageId: "bossInterviewArrangementAdd",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
    });

    // 编辑
    function edit(data) {
        _openNewWindows({
            url: systemCommonUtil.getUrl('FP2023060400003&id=' + data.id, null),
            title: systemLanguage["com.skyeye.editPageTitle"][languageType],
            pageId: "bossInterviewArrangementEdit",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }
        });
    }

    // 入职
    function inductionResult(data) {
        rowId = data.id;
        _openNewWindows({
            url: "../../tpl/bossInterviewArrangement/inductionResult.html",
            title: '入职',
            pageId: "inductionResult",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }
        });
    }

    // 作废
    function cancellation(data) {
        layer.confirm('确认作废该申请吗？', { icon: 3, title: '作废操作' }, function (index) {
            layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "nullifyBossInterviewArrangement", params: {id: data.id}, type: 'json', method: "PUT", callback: function (json) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
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

    exports('bossInterviewArrangementList', {});
});
