
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

    $("#showInfo").html("账套选择规则：双击指定行即可选中。");

    table.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        url: sysMainMation.ifsBasePath + 'ifssetofbooks001',
        where: getTableParams(),
        even: true,
        page: true,
        limits: getLimits(),
        limit: getLimit(),
        cols: [[
            { type: 'radio'},
            { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
            { field: 'name', title: '名称', align: 'left', width: 200 },
            { field: 'startTime', title: '开始日期', align: 'center', width: 100 },
            { field: 'endTime', title: '截至日期', align: 'center', width: 100 },
            { field: 'haveAccess', title: '状态', align: 'center', width: 80, templet: function (d) {
                if(d.haveAccess){
                    return "<span class='state-up'>可用</span>";
                }else {
                    return "<span class='state-down'>不可用</span>";
                }
            }},
            { field: 'remark', title: '备注', align: 'left', width: 200 }
        ]],
	    done: function(res){
	    	matchingLanguage();
            initTableSearchUtil.initAdvancedSearch(this, res.searchFilter, form, "请输入名称", function () {
                table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
            });

            for (var i = 0; i < res.rows.length; i++) {
                // 不允许删除的设置为不可选中
                if(!res.rows[i].haveAccess){
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
                    parent.sysIfsUtil.ifsSetOfBooksMation = obj;

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

    form.render();
    $("body").on("click", "#reloadTable", function() {
        table.reloadData("messageTable", {where: getTableParams()});
    });

    function getTableParams() {
        return $.extend(true, {}, initTableSearchUtil.getSearchValue("messageTable"));
    }

    exports('ifsSetOfBooksListChoose', {});
});
