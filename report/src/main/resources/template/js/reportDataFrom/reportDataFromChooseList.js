
layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form'], function (exports) {
    var index = parent.layer.getFrameIndex(window.name);
    winui.renderColor();
    var $ = layui.$,
        form = layui.form,
        table = layui.table;

    // 数据源选择列表
    table.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        url: sysMainMation.reportBasePath + 'queryReportDataFromList',
        where: getTableParams(),
        even: true,
        page: true,
        limits: getLimits(),
        limit: getLimit(),
        cols: [[
            { type: 'radio'},
            { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
            { field: 'name', title: '名称', align: 'left', width: 150 },
            { field: 'typeName', title: '来源', align: 'left', width: 100 },
            { field: 'remark', title: '备注', align: 'left', width: 150 },
            { field: 'createName', title: '创建人', align: 'left', width: 120 },
            { field: 'createTime', title: '创建时间', align: 'center', width: 140 },
            { field: 'lastUpdateName', title: '最后修改人', align: 'left', width: 120 },
            { field: 'lastUpdateTime', title: '最后修改时间', align: 'center', width: 140}
        ]],
        done: function(res){
            matchingLanguage();
            initTableSearchUtil.initAdvancedSearch(this, res.searchFilter, form, "请输入名称", function () {
                table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
            });
            $('#messageTable').next().find('.layui-table-body').find("table" ).find("tbody").children("tr").on('dblclick',function(){
                var dubClick = $('#messageTable').next().find('.layui-table-body').find("table").find("tbody").find(".layui-table-hover");
                dubClick.find("input[type='radio']").prop("checked", true);
                form.render();
                var id = JSON.stringify(dubClick.data('index'));
                var obj = res.rows[id];
                parent.dataBaseMation = obj;
                parent.layer.close(index);
                parent.refreshCode = '0';
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
    });

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

    exports('reportDataFromChooseList', {});
});
