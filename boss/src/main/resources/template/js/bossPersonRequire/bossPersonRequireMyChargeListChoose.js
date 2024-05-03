
// 我负责的人员需求申请
layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form'], function (exports) {
    winui.renderColor();
    var index = parent.layer.getFrameIndex(window.name);
    var $ = layui.$,
        form = layui.form,
        table = layui.table;

    table.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        url: sysMainMation.bossBasePath + 'queryMyChargePersonRequireList',
        where: getTableParams(),
        even: true,
        page: true,
        limits: getLimits(),
        limit: getLimit(),
        cols: [[
            { type: 'radio'},
            { field: 'oddNumber', title: '单据编号', align: 'left', width: 200, templet: function (d) {
                return '<a lay-event="details" class="notice-title-click">' + d.oddNumber + '</a>';
            }},
            { field: 'recruitDepartmentId', title: '需求部门', align: 'left', width: 140, templet: function (d) {
                return getNotUndefinedVal(d.recruitDepartmentMation?.name);
            }},
            { field: 'recruitJobId', title: '需求岗位', align: 'left', width: 150, templet: function (d) {
                return getNotUndefinedVal(d.recruitJobMation?.name);
            }},
            { field: 'state', title: '状态', align: 'left', width: 80, templet: function (d) {
                return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("bossPersonRequireState", 'id', d.state, 'name');
            }},
            { field: 'wages', title: '薪资范围', width: 120 },
            { field: 'recruitNum', title: '需求人数', width: 100 },
            { field: 'recruitedNum', title: '已招聘人数', width: 100 },
            { field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], width: 120 },
            { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 },
            { field: 'lastUpdateName', title: systemLanguage["com.skyeye.lastUpdateName"][languageType], align: 'left', width: 120 },
            { field: 'lastUpdateTime', title: systemLanguage["com.skyeye.lastUpdateTime"][languageType], align: 'center', width: 150 }
        ]],
        done: function(res){
            matchingLanguage();
            for (var i = 0; i < res.rows.length; i++) {
                // 招聘结束的设置为不可选中
                if (res.rows[i].state == 'endRecruitment') {
                    systemCommonUtil.disabledRow(res.rows[i].LAY_TABLE_INDEX, 'radio');
                }
            }
            $('#messageTable').next().find('.layui-table-body').find("table" ).find("tbody").children("tr").on('dblclick',function(){
                var dubClick = $('#messageTable').next().find('.layui-table-body').find("table").find("tbody").find(".layui-table-hover");
                if(!dubClick.find("input[type='radio']").prop("disabled")) {
                    dubClick.find("input[type='radio']").prop("checked", true);
                    form.render();
                    var chooseIndex = JSON.stringify(dubClick.data('index'));
                    var obj = res.rows[chooseIndex];
                    parent.bossUtil.bossPersonRequireChooseMation = obj;

                    parent.refreshCode = '0';
                    parent.layer.close(index);
                }
            });

            $('#messageTable').next().find('.layui-table-body').find("table" ).find("tbody").children("tr").on('click',function(){
                var click = $('#messageTable').next().find('.layui-table-body').find("table").find("tbody").find(".layui-table-hover");
                if(!click.find("input[type='radio']").prop("disabled")) {
                    click.find("input[type='radio']").prop("checked", true);
                    form.render();
                }
            })
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

    exports('bossPersonRequireMyChargeListChoose', {});
});
