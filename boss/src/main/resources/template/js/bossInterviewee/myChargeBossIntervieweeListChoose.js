
var rowId = "";
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
        url: flowableBasePath + 'bossInterviewee001',
        where: getTableParams(),
        even: true,
        page: true,
        limits: getLimits(),
        limit: getLimit(),
        cols: [[
            { type: 'radio'},
            { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
            { field: 'name', title: '姓名', align: 'left', width: 100, templet: function (d) {
                return '<a lay-event="details" class="notice-title-click">' + d.name + '</a>';
            }},
            { field: 'phone', title: '联系方式', align: 'left', width: 120 },
            { field: 'fromName', title: '来源', align: 'left', width: 120 },
            { field: 'workYears', title: '工作年限', align: 'left', width: 100 },
            { field: 'favoriteJob', title: '心仪岗位', align: 'left', width: 130 },
            { field: 'stateName', title: '状态', align: 'left', width: 80 },
            { field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], width: 120 },
            { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 },
            { field: 'lastUpdateName', title: systemLanguage["com.skyeye.lastUpdateName"][languageType], align: 'left', width: 120 },
            { field: 'lastUpdateTime', title: systemLanguage["com.skyeye.lastUpdateTime"][languageType], align: 'center', width: 150}
        ]],
        done: function(res){
            matchingLanguage();
            $('#messageTable').next().find('.layui-table-body').find("table" ).find("tbody").children("tr").on('dblclick',function(){
                var dubClick = $('#messageTable').next().find('.layui-table-body').find("table").find("tbody").find(".layui-table-hover");
                dubClick.find("input[type='radio']").prop("checked", true);
                form.render();
                var chooseIndex = JSON.stringify(dubClick.data('index'));
                var obj = res.rows[chooseIndex];
                parent.bossUtil.bossIntervieweeChooseMation = obj;

                parent.refreshCode = '0';
                parent.layer.close(index);
            });

            $('#messageTable').next().find('.layui-table-body').find("table" ).find("tbody").children("tr").on('click',function(){
                var click = $('#messageTable').next().find('.layui-table-body').find("table").find("tbody").find(".layui-table-hover");
                click.find("input[type='radio']").prop("checked", true);
                form.render();
            })
        }
    });

    table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'edit') { //编辑
            edit(data);
        }
    });

    // 详情
    function details(data) {
        rowId = data.id;
        _openNewWindows({
            url: "../../tpl/bossInterviewee/bossIntervieweeDetails.html",
            title: systemLanguage["com.skyeye.editPageTitle"][languageType],
            pageId: "bossIntervieweeDetails",
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

    // 刷新数据
    $("body").on("click", "#reloadTable", function() {
        loadTable();
    });

    function loadTable() {
        table.reloadData("messageTable", {where: getTableParams()});
    }

    function getTableParams() {
        return {
            name: $("#name").val(),
            phone: $("#phone").val(),
            type: 2,
            state: '0,1'
        };
    }

    exports('myChargeBossIntervieweeListChoose', {});
});
