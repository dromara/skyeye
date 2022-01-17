
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

    $("#showInfo").html("面试者来源选择规则：双击指定行即可选中。");

    table.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        url: reqBasePath + 'bossIntervieweeFrom001',
        where: getTableParams(),
        even: true,
        page: true,
        limits: getLimits(),
        limit: getLimit(),
        cols: [[
            { type: 'radio'},
            { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
            { field: 'title', title: '来源', align: 'left', width: 150 },
            { field: 'fromUrl', title: '来源地址', align: 'left', width: 300 }
        ]],
	    done: function(res){
	    	matchingLanguage();
            $('#messageTable').next().find('.layui-table-body').find("table" ).find("tbody").children("tr").on('dblclick',function(){
                var dubClick = $('#messageTable').next().find('.layui-table-body').find("table").find("tbody").find(".layui-table-hover");
                dubClick.find("input[type='radio']").prop("checked", true);
                form.render();
                var chooseIndex = JSON.stringify(dubClick.data('index'));
                var obj = res.rows[chooseIndex];
                parent.bossUtil.bossIntervieweeFromChooseMation = obj;

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

    form.render();
    form.on('submit(formSearch)', function (data) {
        if (winui.verifyForm(data.elem)) {
            table.reload("messageTable", {page: {curr: 1}, where: getTableParams()});
        }
        return false;
    });

    $("body").on("click", "#reloadTable", function() {
        loadTable();
    });

    // 刷新
    function loadTable(){
        table.reload("messageTable", {where: getTableParams()});
    }

    function getTableParams(){
        return {
            title: $("#title").val()
        };
    }

    exports('bossIntervieweeFromListChoose', {});
});
