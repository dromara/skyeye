
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
    subjectClassesId = GetUrlParam("subjectClassesId");
    if (isNull(objectKey) || isNull(objectId)) {
        winui.window.msg("请传入适用对象信息", {icon: 2, time: 2000});
        return false;
    }

    table.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        url: sysMainMation.schoolBasePath + 'queryAnnouncementListBySubjectClassesId',
        where: getTableParams(),
        even: false,
        page: false,
        cols: [[
            { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
            { field: 'title', title: '标题', align: 'left', width: 300, templet: function (d) {
                return '<a lay-event="details" class="notice-title-click">' + d.title + '</a>';
            }},
            { field: 'content', title: '内容', align: 'left', width: 300},
            { field: 'isConfirm', title: '状态', align: 'left',width: 150, templet: function(d) {
                var str = '';
                if (d.isConfirm == 1) {
                    str +='需要确认';
                }else{
                    str +='无需确认';
                }
                return str;
            }},
            { field: 'confirmNum', title: '已确认人数', align: 'left', width: 300},
            { field: 'unConfirmNum', title: '未确认人数', align: 'left', width: 300},
            { field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], align: 'left', width: 120 , templet: function(d) {
                return getNotUndefinedVal(d.createMation?.name);
            }},
            { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 },
            { field: 'lastUpdateName', title: systemLanguage["com.skyeye.lastUpdateName"][languageType], align: 'left', width: 120 , templet: function(d) {
                return getNotUndefinedVal(d.lastUpdateMation?.name);
            }},
            { field: 'lastUpdateTime', title: systemLanguage["com.skyeye.lastUpdateTime"][languageType], align: 'center', width: 150 },
            { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 257, toolbar: '#tableBar' }
        ]],
        done: function(json) {
            matchingLanguage();
            initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "暂不支持搜索", function () {
                table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
            });
        }
    });

    table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'edit') { //编辑
            edit(data);
        } else if (layEvent === 'details') { //详情
            details(data);
        } else if (layEvent === 'del') { //删除
            del(data);
        }else if (layEvent === 'submit') { //提交情况
            submit(data);
        }
    });

    // 新增
    $("body").on("click", "#addBean", function() {
        parent._openNewWindows({
            url: '../../tpl/announcement/write.html?objectId=' + objectId + '&objectKey=' + objectKey + '&subjectClassesId=' + subjectClassesId,
            title: systemLanguage["com.skyeye.addPageTitle"][languageType],
            pageId: "announcementAdd",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
    });

    // 编辑
    function edit(data) {
        parent._openNewWindows({
            url: '../../tpl/announcement/write.html?objectId=' + objectId + '&objectKey=' + objectKey + '&subjectClassesId=' + subjectClassesId+ '&id=' + data.id,
            title: systemLanguage["com.skyeye.editPageTitle"][languageType],
            pageId: "announcementEdit",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
    }

    // 详情
    function details(data) {
        parent._openNewWindows({
            url: systemCommonUtil.getUrl('FP2024072000006&objectId=' + objectId + '&objectKey=' + objectKey + '&id=' + data.id, null),
            title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
            pageId: "announcementDetails",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
            }});
    }

    // 删除
    function del(data, obj) {
        layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function (index) {
            layer.close(index);
            AjaxPostUtil.request({url: sysMainMation.schoolBasePath + "deleteAnnouncementById", params: {id: data.id}, type: 'json', method: 'DELETE', callback: function (json) {
                    winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
                    loadTable();
                }});
        });
    }

    //确认情况
    function submit(data) {
        parent.parent._openNewWindows({
            url: '../../tpl/announcement/confirm.html?id=' + data.id,
            title: '确认情况',
            pageId: "announcementConfirm",
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
        return $.extend(true, {objectKey: objectKey, objectId: subjectClassesId, subjectClassesId: subjectClassesId}, initTableSearchUtil.getSearchValue("messageTable"));
    }

    exports('announcementList', {});
});