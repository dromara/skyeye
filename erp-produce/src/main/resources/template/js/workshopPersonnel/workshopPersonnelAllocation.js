var rowId = "";

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
    authBtn('1720880910884');

    loadWorkshop();

    form.render();
    var chooseWorkshopId = "";
    function loadWorkshop() {
        table.render({
            id: 'workshopTable',
            elem: '#workshopTable',
            method: 'get',
            url: sysMainMation.erpBasePath + 'queryMyChargeFarmList',
            even: false,
            page: false,
            limits: getLimits(),
            limit: getLimit(),
            cols: [[
                { title: systemLanguage["com.skyeye.serialNumber"][languageType], fixed: 'left', type: 'numbers' },
                { field: 'name', title: '车间', align: 'left', width: 150, templet: function (d) {
                        return '<a lay-event="select" class="notice-title-click">' + d.name + '</a>';
                    }}
            ]],
            done: function(json) {
                matchingLanguage();
                chooseWorkshopId = "";
                loadStaff("");
            }
        });
        table.on('tool(workshopTable)', function (obj) {
            var data = obj.data;
            var layEvent = obj.event;
            if (layEvent == 'select') {
                chooseWorkshopId = data.id;
                loadStaff(data.id);
            }
        });
    }

    function loadStaff(workshopId) {
        table.render({
            id: 'messageTable',
            elem: '#messageTable',
            method: 'post',
            url: sysMainMation.erpBasePath + 'queryFarmStaffList',
            where: {objectId: workshopId},
            even: true,
            page: true,
            limits: getLimits(),
            limit: getLimit(),
            cols: [[
                { title: systemLanguage["com.skyeye.serialNumber"][languageType], fixed: 'left', type: 'numbers' },
                { field: 'jobNumber', title: '工号', align: 'left', width: 140, templet: function (d) {
                        return getNotUndefinedVal(d.staffMation?.jobNumber);
                    }},
                { field: 'userName', title: '姓名', width: 120, templet: function (d) {
                        return getNotUndefinedVal(d.staffMation?.userName);
                    }},
                { field: 'companyName', title: '企业', width: 150, templet: function (d) {
                        return getNotUndefinedVal(d.staffMation?.companyName);
                    }},
                { field: 'departmentName', title: '部门', width: 140, templet: function (d) {
                        return getNotUndefinedVal(d.staffMation?.departmentName);
                    }},
                { field: 'jobName', title: '职位', width: 140, templet: function (d) {
                        return getNotUndefinedVal(d.staffMation?.jobName);
                    }},
                { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 150, toolbar: '#tableBar'}
            ]],
            done: function(json) {
                matchingLanguage();
            }
        });

        table.on('tool(messageTable)', function (obj) {
            var data = obj.data;
            var layEvent = obj.event;
            if (layEvent === 'delete') { // 删除
                delet(data);
            }
        });
    }

    // 删除
    function delet(data) {
        layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function (index) {
            layer.close(index);
            AjaxPostUtil.request({url: sysMainMation.erpBasePath + "deleteFarmStaffById", params: {id: data.id}, type: 'json', method: "DELETE", callback: function (json) {
                    winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
                    table.reloadData("messageTable", {page: {curr: 1}, where: {objectId: chooseWorkshopId}})
                }});
        });
    }

    // 添加
    $("body").on("click", "#addBean", function() {
        if (isNull(chooseWorkshopId)) {
            winui.window.msg('请先选择车间信息.', {icon: 2, time: 2000});
            return false;
        }
        systemCommonUtil.userStaffCheckType = true; // // 人员选择类型，1.多选；其他。单选
        systemCommonUtil.checkStaffMation = []; // 选择时返回的对象
        systemCommonUtil.openSysAllUserStaffChoosePage(function (checkStaffMation) {
            var list = new Array();
            $.each(checkStaffMation, function (i, item) {
                list.push(item.id);
            });
            var params = {
                farmId: chooseWorkshopId,
                staffId: JSON.stringify(list)
            };
            AjaxPostUtil.request({url: sysMainMation.erpBasePath + "insertFarmStaff", params: params, type: 'json', method: "POST", callback: function (json) {
                    loadStaff(chooseWorkshopId);
                }});
        });

    });

    exports('workshopPersonnelAllocation', {});
});
