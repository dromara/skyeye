
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

    table.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        url: sysMainMation.ehrBasePath + 'queryContractList',
        where: getTableParams(),
        even: true,
        page: true,
        limits: getLimits(),
        limit: getLimit(),
        cols: [[
            { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
            { field: 'contractNumber', title: '合同编号', align: 'left', width: 160, templet: function (d) {
                return '<a lay-event="details" class="notice-title-click">' + d.contractNumber + '</a>';
            }},
            { field: 'typeId', title: '合同类别', width: 150, templet: function (d) {
                return sysDictDataUtil.getDictDataNameByCodeAndKey("EMPLOYEE_CONTRACT_CATEGORY", d.typeId);
            }},
            { field: 'moldId', title: '合同类型', width: 150, templet: function (d) {
                return sysDictDataUtil.getDictDataNameByCodeAndKey("EMPLOYEE_CONTRACT_TYPE", d.moldId);
            }},
            { field: 'startTime', title: '开始时间', align: 'center', width: 100 },
            { field: 'endTime', title: '结束时间', align: 'center', width: 100 },
            { field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], align: 'left', width: 120 },
            { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 },
            { field: 'lastUpdateName', title: systemLanguage["com.skyeye.lastUpdateName"][languageType], align: 'left', width: 120 },
            { field: 'lastUpdateTime', title: systemLanguage["com.skyeye.lastUpdateTime"][languageType], align: 'center', width: 150 },
            { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 100, toolbar: '#tableBar'}
        ]],
        done: function(json) {
            matchingLanguage();
            initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "请输入合同编号", function () {
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
        }
    });

    // 新增
    $("body").on("click", "#addBean", function() {
        parent._openNewWindows({
            url: systemCommonUtil.getUrl('FP2023120300007&objectId=' + objectId + '&objectKey=' + objectKey, null),
            title: systemLanguage["com.skyeye.addPageTitle"][languageType],
            pageId: "sysStaffArchivesAdd",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
    });

    // 编辑
    function edit(data) {
        parent._openNewWindows({
            url: systemCommonUtil.getUrl('FP2023120300008&objectId=' + objectId + '&objectKey=' + objectKey + '&id=' + data.id, null),
            title: systemLanguage["com.skyeye.editPageTitle"][languageType],
            pageId: "sysStaffArchivesEdit",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }
        });
    }

    // 详情
    function details(data) {
        parent._openNewWindows({
            url: systemCommonUtil.getUrl('FP2023120300009&objectId=' + objectId + '&objectKey=' + objectKey + '&id=' + data.id, null),
            title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
            pageId: "sysStaffArchivesDetails",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
            }});
    }

    // 删除
    function del(data, obj) {
        layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function (index) {
            layer.close(index);
            AjaxPostUtil.request({url: sysMainMation.ehrBasePath + "deleteContractById", params: {id: data.id}, type: 'json', method: 'DELETE', callback: function (json) {
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

    exports('sysStaffContractList', {});
});
