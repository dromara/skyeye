
var rowId = "";

layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form', 'laydate'], function (exports) {
    winui.renderColor();
    var index = parent.layer.getFrameIndex(window.name);
    var $ = layui.$,
        form = layui.form,
        laydate = layui.laydate,
        table = layui.table;

    $("#showInfo").html("账套选择规则：双击指定行即可选中。");

    laydate.render({elem: '#startTime', range: '~'});

    laydate.render({elem: '#endTime', range: '~'});

    table.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        url: flowableBasePath + 'ifssetofbooks001',
        where: getTableParams(),
        even: true,
        page: true,
        limits: getLimits(),
        limit: getLimit(),
        cols: [[
            { type: 'radio'},
            { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
            { field: 'name', title: '名称', align: 'left', width: 200, templet: function (d) {
                return '<a lay-event="select" class="notice-title-click">' + d.name + '</a>';
            }},
            { field: 'startTime', title: '开始日期', align: 'center', width: 100},
            { field: 'endTime', title: '截至日期', align: 'center', width: 100},
            { field: 'haveAccess', title: '状态', align: 'center', width: 80, templet: function (d) {
                if(d.haveAccess){
                    return "<span class='state-up'>可用</span>";
                }else {
                    return "<span class='state-down'>不可用</span>";
                }
            }},
            { field: 'remark', title: '备注', align: 'left', width: 200}
        ]],
	    done: function(res){
	    	matchingLanguage();
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

    table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'select'){//查看详情
            selectDetails(data);
        }
    });

    function selectDetails(data) {
        rowId = data.id;
        _openNewWindows({
            url: "../../tpl/ifsSetOfBooks/ifsSetOfBooksDetails.html",
            title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
            pageId: "ifsSetOfBooksDetails",
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

    $("body").on("click", "#reloadTable", function() {
        loadTable();
    });

    // 刷新
    function loadTable() {
        table.reloadData("messageTable", {where: getTableParams()});
    }

    function getTableParams() {
        var startTime1 = "", startTime2 = "";
        if (!isNull($("#startTime").val())) {
            startTime1 = $("#startTime").val().split('~')[0].trim();
            startTime2 = $("#startTime").val().split('~')[1].trim();
        }
        var endTime1 = "", endTime2 = "";
        if (!isNull($("#endTime").val())) {
            endTime1 = $("#endTime").val().split('~')[0].trim();
            endTime2 = $("#endTime").val().split('~')[1].trim();
        }
        return {
            name: $("#name").val(),
            state: 1,
            startTime1: startTime1,
            startTime2: startTime2,
            endTime1: endTime1,
            endTime2: endTime2
        };
    }

    exports('ifsSetOfBooksListChoose', {});
});
