var rowId = "";

// 我负责的人员需求申请
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
        url: flowableBasePath + 'queryMyChargeBossPersonRequireList',
        where: getTableParams(),
        even: true,
        page: true,
        limits: getLimits(),
        limit: getLimit(),
        cols: [[
            { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
            { field: 'createName', title: '申请人', width: 140},
            { field: 'applyDepartmentName', title: '申请人部门', width: 140},
            { field: 'recruitJobName', title: '需求岗位', width: 150 },
            { field: 'recruitDepartmentName', title: '需求部门', width: 140 },
            { field: 'wages', title: '薪资', width: 120 },
            { field: 'recruitNum', title: '需求人数', width: 100 },
            { field: 'stateName', title: '状态', width: 90, templet: function (d) {
                if(d.state == 6){
                    return '<span class="state-new">招聘中</span>';
                } else if(d.state == 7){
                    return '<span class="state-new">招聘结束</span>';
                }
            }},
            { field: 'createTime', title: systemLanguage["com.skyeye.entryTime"][languageType], width: 150}
        ]],
        done: function(){
            matchingLanguage();
        }
    });

    table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'details') { // 详情
            details(data);
        }
    });

    // 详情
    function details(data) {
        rowId = data.id;
        _openNewWindows({
            url: "../../tpl/bossPersonRequire/bossPersonRequireDetails.html",
            title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
            pageId: "bossPersonRequireDetails",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
            }
        });
    }

    form.render();
    form.on('submit(formSearch)', function (data) {
        if (winui.verifyForm(data.elem)) {
            table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
        }
        return false;
    });

    // 刷新
    $("body").on("click", "#reloadTable", function() {
        loadTable();
    });

    function loadTable() {
        table.reloadData("messageTable", {where: getTableParams()});
    }

    function getTableParams() {
        return {
            state: $("#state").val(),
        };
    }

    exports('bossPersonRequireMyChargeList', {});
});
