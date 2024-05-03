
var objectKey = "";
var objectId = "";

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
    objectKey = GetUrlParam("objectKey");
    objectId = GetUrlParam("objectId");
    if (isNull(objectKey) || isNull(objectId)) {
        winui.window.msg("请传入适用对象信息", {icon: 2, time: 2000});
        return false;
    }

    var authPermission = teamObjectPermissionUtil.checkTeamBusinessAuthPermission(objectId, 'crmFollowUpAuthEnum');

    table.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        url: sysMainMation.crmBasePath + 'queryFollowUpList',
        where: getTableParams(),
        even: true,
        page: true,
        limits: getLimits(),
        limit: getLimit(),
        cols: [[
            { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
            { field: 'oddNumber', title: '回访编号', align: 'left', width: 150, templet: function (d) {
                return '<a lay-event="details" class="notice-title-click">' + d.oddNumber + '</a>';
            }},
            { field: 'contractId', title: '合同', width: 200, templet: function (d) {
                return getNotUndefinedVal(d.contractMation?.title);
            }},
            { field: 'typeId', title: '类型', align: 'center', width: 120, templet: function (d) {
                return sysDictDataUtil.getDictDataNameByCodeAndKey('FOLLOW_UP_TYPE', d.typeId);
            }},
            { field: 'contacts', title: '联系人', width: 120, templet: function (d) {
                return getNotUndefinedVal(d.contactsMation?.name);
            }},
            { field: 'satisfaction', title: '客户满意度', align: 'center', width: 120, templet: function (d) {
                return sysDictDataUtil.getDictDataNameByCodeAndKey('FOLLOW_UP_SATISFACTION', d.satisfaction);
            }},
            { field: 'followUserId', title: '回访人', width: 120, templet: function (d) {
                return getNotUndefinedVal(d.followUserMation?.name);
            }},
            { field: 'followTime', title: '回访时间', align: 'center', width: 100 },
            { field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], align: 'left', width: 120 },
            { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 },
            { field: 'lastUpdateName', title: systemLanguage["com.skyeye.lastUpdateName"][languageType], align: 'left', width: 120 },
            { field: 'lastUpdateTime', title: systemLanguage["com.skyeye.lastUpdateTime"][languageType], align: 'center', width: 150 },
            { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 150, templet: function (d) {
                var str = '';
                if (authPermission['edit']) {
                    str += '<a class="layui-btn layui-btn-xs" lay-event="edit"><language showName="com.skyeye.editBtn"></language></a>'
                }
                if (authPermission['delete']) {
                    str += '<a class="layui-btn layui-btn-xs layui-btn-danger" lay-event="delete"><language showName="com.skyeye.deleteBtn"></language></a>'
                }
                return str;
            }}
        ]],
        done: function(json) {
            matchingLanguage();
            initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "请输入回访编号", function () {
                table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
            });
        }
    });

    table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'edit') {
            edit(data);
        } else if (layEvent === 'delete') {
            del(data);
        } else if (layEvent === 'details') {
            details(data);
        }
    });


    // 新增
    $("body").on("click", "#addBean", function() {
        parent._openNewWindows({
            url: systemCommonUtil.getUrl('FP2024050200001&objectId=' + objectId + '&objectKey=' + objectKey, null),
            title: systemLanguage["com.skyeye.addPageTitle"][languageType],
            pageId: "crmDocumentaryAdd",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
    });

    // 编辑
    function edit(data) {
        parent._openNewWindows({
            url: systemCommonUtil.getUrl('FP2024050200002&objectId=' + objectId + '&objectKey=' + objectKey + '&id=' + data.id, null),
            title: systemLanguage["com.skyeye.editPageTitle"][languageType],
            pageId: "crmDocumentaryEdit",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
    }

    // 详情
    function details(data) {
        parent._openNewWindows({
            url: systemCommonUtil.getUrl('FP2024050200003&objectId=' + objectId + '&objectKey=' + objectKey + '&id=' + data.id, null),
            title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
            pageId: "crmDocumentaryDetails",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
    }

    // 删除
    function del(data, obj) {
        layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function (index) {
            layer.close(index);
            AjaxPostUtil.request({url: sysMainMation.crmBasePath + "deleteFollowUpById", params: {id: data.id}, type: 'json', method: 'DELETE', callback: function (json) {
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
        return $.extend(true, {objectKey: objectKey, objectId: objectId}, initTableSearchUtil.getSearchValue("messageTable"));
    }

    exports('crmFollowList', {});
});